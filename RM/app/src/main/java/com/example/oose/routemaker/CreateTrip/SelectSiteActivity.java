package com.example.oose.routemaker.CreateTrip;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.oose.routemaker.API.RmAPI;
import com.example.oose.routemaker.Concrete.City;
import com.example.oose.routemaker.Concrete.Event;
import com.example.oose.routemaker.Concrete.Site;
import com.example.oose.routemaker.Concrete.User;
import com.example.oose.routemaker.Constants;
import com.example.oose.routemaker.CurrentOrPastTrips.SelectCurrentOrPastTripActivity;
import com.example.oose.routemaker.Logistics.SettingActivity;
import com.example.oose.routemaker.NewsFeedActivity;
import com.example.oose.routemaker.Pop;
import com.example.oose.routemaker.R;
import com.example.oose.routemaker.TSPSolver.TSP;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * User can select multiple sites for each day.
 * We show them the sites according to category.
 * After the user selects a bunch of sites here,
 * we direct them to the editschedule activity
 * and use the initial list of sites to run TSP.
 */
public class SelectSiteActivity extends AppCompatActivity implements OnMapReadyCallback, Communicator {

    /** Drawer. */
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /** Marker to be placed on the Google Map for each site. */
    private Marker marker;

    /** Google Map to be used for this activity. */
    private GoogleMap mMap;

    /** Basic information about the user and the trip. */
    private String userId;
    private long diff;

    /** Map of lists of sites for each day.
     * This gets sorted using TSP and turns into the eventMap.
     * Only the eventMap gets passed onto the next activity. */
    private Map<String, ArrayList<Site>> tempMap;

    /** List of dayIds. */
    private ArrayList<String> dayIds;

    /** Day the user is currently working on. */
    private String currentlySelectedDayId = ""; //gets changed every time the user clicks a "Day X" button

    /** List of sites the user picked for a particular day. */
    protected ArrayList<Site> selectedSites;

    /** The numerical numbering of the day. */
    private int day_numbering;

    /** Selected category index. */
    private int category_id;

    private String cityCode;

    /** Trip id of this trip. */
    private String tripId;

    /** City selected for this trip. */
    private City selectedCity;
    private String startTimeString;
    private Calendar startTime;
    private Calendar startDate;
    private int day = -1;

    private SelectSiteSiteFragment siteFragment;
    private SelectSiteDayFragment dayFragment;
    private SelectSiteCategoryFragment categoryFragment;

    //Retrofit
    private String baseURL = "https://routemaker.herokuapp.com";   //BASE URL

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //Initialize things
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sites);
        marker = null;
        tempMap = new HashMap<>();
        selectedSites = new ArrayList<>();
        day_numbering = 1;
        category_id = 1;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.select_site_map_fragment);
        mapFragment.getView().setClickable(false);
        mapFragment.getMapAsync(this);

        //Drawer Initialize
        mDrawerList = (ListView)findViewById(R.id.left_drawer_select_sites);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout_select_sites);
        addDrawerItems();
        setupDrawer();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_default);
        setSupportActionBar(mToolbar);
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        //Get intent from previous activity
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userId= null;
                diff = 1;
                tripId = null;
                cityCode = null;
                startDate = null;
                startTimeString = null;
            } else {
                userId = extras.getString("userId");
                diff = extras.getLong("numDays");
                tripId = extras.getString("tripId");
                selectedCity = (City) extras.getSerializable("city");
                startDate = (Calendar) extras.getSerializable("startDate");
                startTimeString = extras.getString("startTime");
            }
        } else {
            tripId = savedInstanceState.getString("tripId");
            selectedCity = (City) savedInstanceState.getSerializable("city");
            userId = savedInstanceState.getString("userId");
            startDate = (Calendar) savedInstanceState.getSerializable("startDate");
            startTimeString = savedInstanceState.getString("startTime");
        }

        // create startTime calendar
        if (startTimeString != null) {
            DateFormat df = new SimpleDateFormat("HHmm");
            Date date = null;
            try {
                date = df.parse(startTimeString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            startTime = Calendar.getInstance();
            startTime.setTime(date);
        }

        //Add newly created dayId, List pairs to the map
        //get day ids here
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseURL).build();
        RmAPI selectSiteAPI = restAdapter.create(RmAPI.class);
        selectSiteAPI.getDayIds(tripId, new Callback<ArrayList<String>>() {
            @Override
            public void success(final ArrayList<String> dayIdList, Response response) {
                dayIds = dayIdList;
                for (int i = 0; i < diff; i++) {
                    ArrayList<Site> siteList = new ArrayList<>();
                    tempMap.put(dayIds.get(i), siteList);
                }

                currentlySelectedDayId = dayIds.get(0);

                day = startDate.get(Calendar.DAY_OF_WEEK);
                initializeList();

                FragmentManager manager = getFragmentManager();
                siteFragment = (SelectSiteSiteFragment) manager.findFragmentById(R.id.select_site_site_fragment);

                //'Continue button' job
                final Button continueButton = (Button) findViewById(R.id.button_continue_select_site);
                continueButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        tempMap.put(currentlySelectedDayId, selectedSites);
                        boolean allDaysChosen = true;
                        for (int i = 0; i < tempMap.size(); i++) {
                            if (tempMap.get(dayIdList.get(i)).size() == 0) {
                                Toast.makeText(getApplicationContext(), "You need at least one event per day!", Toast.LENGTH_LONG).show();
                                allDaysChosen = false;
                                break;
                            }
                        }
                        if (allDaysChosen) {
                            sortTempList();
                            Map<String, ArrayList<Event>> eventMap = createEventMap();
                            Intent intent = new Intent(SelectSiteActivity.this, EditScheduleActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("numDays", diff);
                            intent.putExtra("eventMap", (Serializable) eventMap);
                            intent.putExtra("tripId", tripId);
                            intent.putExtra("city", selectedCity);
                            intent.putExtra("startDate", startDate);
                            intent.putExtra("previousState", "createTrip");
                            intent.putExtra("dayIds", dayIds);
                            startActivity(intent);
                        }
                    }
                });
            }
            @Override
            public void failure(RetrofitError error) {
                System.out.println(error);
            }
        });
    }

    /**
     * Sort each of the site list using the TSP algorithm.
     */
    public void sortTempList() {
        for (int i = 0; i < tempMap.size(); i++) {
            String currDayId = dayIds.get(i);
            ArrayList<Site> currList = tempMap.get(currDayId);
            TSP t = new TSP();
            ArrayList<Site> sortedList = t.runTSP(currList);
            tempMap.remove(currDayId);
            tempMap.put(currDayId, sortedList);
        }
    }

    /**
     * Sync drawer state to the drawer layout.
     * @param savedInstanceState the saved instance state.
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    /**
     * Finds out whether the drawer was pressed in the action bar.
     * @param item the menu item in the action bar.
     * @return true if drawer is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Activate the navigation drawer toggle
        if (!mDrawerToggle.onOptionsItemSelected(item)) {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Creates a drawer.
     */
    private void addDrawerItems() {
        String[] drawerArray = getResources().getStringArray(R.array.drawer_array);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, drawerArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                selectItem(position);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
/*
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
            }
*/
        });
    }

    /**
     * Set up configuration for drawer open/close.
     */
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * Directs the user to the right activity once clicking the buttons.
     * @param position which item in the drawer list was clicked.
     */
    public void selectItem(int position) {
        Intent intent;
        switch(position) {
            case 0:
                intent = new Intent(this, NewsFeedActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
                break;
            case 1:
                intent = new Intent(this, SelectCityActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
                break;
            case 2:
                intent = new Intent(this, SelectCurrentOrPastTripActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", userId);
                intent.putExtra("previousState", "currentTrip");
                startActivity(intent);
                finish();
                break;
            case 3:
                intent = new Intent(this, SelectCurrentOrPastTripActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", userId);
                intent.putExtra("previousState", "pastTrip");
                startActivity(intent);
                finish();
                break;
            case 4:
                intent = new Intent(this, Pop.class);
                intent.putExtra("userId", userId);
                startActivityForResult(intent, Constants.SETTING_CODE);
                break;
            default :
                intent = new Intent(this, NewsFeedActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
                break;
        }
    }


    /**
     * Responds to activity with result when checking password for setting,
     * and send result to setting activity if valid.
     * @param requestCode request code assigned when starting activity for result
     * @param resultCode result of activity on ok / cancel
     * @param intent the intent received from the activity for result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Constants.SETTING_CODE) {
            if (resultCode == RESULT_OK) {
                User user = (User) intent.getSerializableExtra("userInfo");
                Intent resultIntent = new Intent(SelectSiteActivity.this, SettingActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultIntent.putExtra("userInfo", user);
                startActivity(resultIntent);
                finish();
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("fragment_added", true);
    }

    /**
     * On back pressed, if drawer is open, close drawer.
     * If not, close the activity.
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            dayFragment = (SelectSiteDayFragment) manager.findFragmentById(R.id.select_site_day_fragment);
            categoryFragment = (SelectSiteCategoryFragment) manager.findFragmentById(R.id.select_site_category_fragment);
            transaction.remove(dayFragment);
            transaction.remove(categoryFragment);
            transaction.remove(siteFragment);
            SelectSiteInfo.SITES = null;
            finish();        }
    }

    /**
     * On map ready, move the camera to the center of the selected city.
     * @param googleMap the Google Map we are using.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng center = new LatLng(selectedCity.getLatitude(), selectedCity.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 11));
    }

    /**
     * Set the marker on the currenlty selected site's position on the map.
     * @param index index position of the currently selected site in our arraylist of sites.
     * @return true if the site is open that day, false if else.
     */
    public boolean setMarker(int index) {

        Site site = SelectSiteInfo.SITES.get(index);

        //Let users know if the site is not open that day.
        if (site.openHours[day] != 0) {
            //Parsing job for open and close hour so that we
            //can display that information on the map marker snippet.
            DecimalFormat dfHour = new DecimalFormat("##");
            DecimalFormat dfMinute = new DecimalFormat("00");
            int start = site.openHours[day];
            int end = site.closeHours[day];
            String startString;
            String endString;

            int startHour = start / 100;
            if (startHour == 12) {
                startString = "PM";
            } else if (startHour == 24) {
                startHour -= 24;
                startString = "AM";
            } else if (startHour < 12) {
                startString = "AM";
            } else {
                startHour -= 12;
                startString = "PM";
            }

            String startHourString = dfHour.format(startHour);
            int startMinute = start % 100;
            String startMinuteString = dfMinute.format(startMinute);

            int endHour = end / 100;
            if (endHour == 12) {
                endString = "PM";
            } else if (endHour == 24) {
                endHour -= 24;
                endString = "AM";
            }else if (endHour < 12) {
                endString = "AM";
            } else {
                endHour -= 12;
                endString = "PM";
            }

            String endHourString = dfHour.format(endHour);
            int endMinute = end % 100;
            String endMinuteString = dfMinute.format(endMinute);

            //Get the position of the site.
            LatLng center = new LatLng(site.getLatitude(), site.getLongitude());
            double centerLat = center.latitude;
            centerLat += 0.00125;
            LatLng newCenter = new LatLng(centerLat, center.longitude);

            //Move the Google Maps camera to that location.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newCenter, 15));

            //Remove the marker
            if (marker != null) {
                marker.remove();
            }

            //Add a marker there.
            marker = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .alpha(0.8f).title(site.getSiteName())
                    .snippet(startHourString + ":" + startMinuteString + startString
                            + " - " + endHourString + ":" + endMinuteString + endString));
            marker.showInfoWindow();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Reacts when the day button is clicked.
     * @param day_id the id of the day that was clicked. (numerical order)
     */
    @Override
    public void respond(int day_id) {
        respond(day_id, 0);
    }

    /**
     * Reacts whenever either a day or a category is clicked.
     * @param day_id the day id of the day clicked.
     * @param category_id the category id of the category clicked.
     */
    @Override
    public void respond(int day_id, int category_id) {

        if (category_id == 0) {
            tempMap.put(currentlySelectedDayId, selectedSites);
            this.day_numbering = day_id;
            currentlySelectedDayId = dayIds.get(day_id - 1);
            day = ((startDate.get(Calendar.DAY_OF_WEEK) - 1) + (day_numbering - 1)) % 7 + 1;
            selectedSites = new ArrayList<>();
            selectedSites.addAll(tempMap.get(currentlySelectedDayId));
        } else {
            this.category_id = category_id;
        }
        alterArray(this.category_id - 1);

        android.os.SystemClock.sleep(500);

        setFragment();
    }

    /**
     * Adds a site to the list of sites for a given day.
     * @param index the position of the site in the arraylist of sites.
     */
    public void addSite(int index) {
        Site site = SelectSiteInfo.SITES.get(index);
        if (selectedSites.contains(site)) {
            selectedSites.remove(site);
        } else {
            selectedSites.add(site);
        }
    }

    /**
     * Changes the site array corresponding to the category id.
     * @param category_id the id of the selected category.
     */
    protected void alterArray(int category_id) {
        String category = SelectSiteInfo.CATEGORIES[category_id];

        String temp = selectedCity.getCityCode() + "-" + category;

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseURL).build();
        RmAPI siteAPI = restAdapter.create(RmAPI.class);
        siteAPI.getSite(temp, new Callback<ArrayList<Site>>() {
            @Override
            public void success(ArrayList<Site> sites, Response response) {
                SelectSiteInfo.SITES = null;
                SelectSiteInfo.SITES = sites;
                for (int i = 0; i < SelectSiteInfo.SITES.size(); i++) {
                    SelectSiteInfo.SITES.get(i).initializeHours();
                }
            }
            @Override
            public void failure(RetrofitError error) {
                System.out.println(error);
            }
        });

    }

    /**
     * Sets the fragment with the changed data.
     */
    private void setFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        SelectSiteSiteFragment fragment = new SelectSiteSiteFragment();
        transaction.replace(R.id.select_site_site_fragment, fragment, "site_list");
        transaction.commit();
    }

    /**
     * Initializes the tempMap's ArrayList of sites.
     */
    public void initializeList() {
        tempMap = new HashMap<>();
        for (String dayId : dayIds) {
            tempMap.put(dayId, new ArrayList<Site>());
        }
        selectedSites = tempMap.get(currentlySelectedDayId);
    }

    /**
     * @return returns the number of days of the trip.
     */
    public long getDiff() {
        return this.diff;
    }

    /**
     * Using the TSP sorted map of sites, this method
     * creates an eventMap to be thrown to the EditScheduleActivity.
     * @return map of <K, V> such that K = dayId and V = ArrayList of Events.
     */
    private Map<String, ArrayList<Event>> createEventMap() {
        //create a new map.
        Map<String, ArrayList<Event>> eventMap = new HashMap<>();
        for (int i = 0; i < tempMap.size(); i++) {
            //Get the start time.
            Calendar currTime = Calendar.getInstance();
            currTime.setTime(startTime.getTime());
            String dayId = dayIds.get(i);
            ArrayList<Site> sites = tempMap.get(dayId);
            ArrayList<Event> events = new ArrayList<>();
            for (int j = 0; j < sites.size(); j++) {
                //Fill in the event list.
                Site site = sites.get(j);
                int startTime = currTime.get(Calendar.HOUR_OF_DAY) * 100 + currTime.get(Calendar.MINUTE);
                double duration = site.getDuration();
                int addHour = (int) duration;
                int addMinute = (int) ((duration - addHour) * 60);
                currTime.add(Calendar.HOUR_OF_DAY, addHour);
                currTime.add(Calendar.MINUTE, addMinute);
                int endTime = currTime.get(Calendar.HOUR_OF_DAY) * 100 + currTime.get(Calendar.MINUTE);

                //Create a new event with the obtained information.
                Event event = new Event(i + "" + j, startTime, endTime, site.getSiteId(),
                        site.getSiteName(), site.getLatitude(), site.getLongitude());

                //Add this newly created event to the list of events.
                events.add(event);
            }
            eventMap.put(dayId, events);
        }
        return eventMap;
    }
}