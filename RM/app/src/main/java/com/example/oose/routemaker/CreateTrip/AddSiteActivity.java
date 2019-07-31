package com.example.oose.routemaker.CreateTrip;

import android.app.Activity;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Allows users to add a site while editing a trip schedule.
 * This gets called once the user clicks the red "+" button.
 */
public class AddSiteActivity extends AppCompatActivity implements OnMapReadyCallback, AddCommunicator {

    /** Day Id of the day the usr is currently editing. */
    private String currentlyWorkingDayId = "";

    /** Google Maps. */
    private Marker marker;
    private GoogleMap mMap;

    /** The numerical order of the day that is being considered. */
    private int dayNumbering;

    /** Basic information about the user and this trip. */
    private String userId;
    private Calendar startDate;
    private City selectedCity;
    public ArrayList<String> categoryList;

    private Site selectedSite = null;

    private final int ADD_EVENT_CODE = 4;

    private AddSiteSiteFragment siteFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_site);

        //Get information that the previous activity passed on.
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userId= null;
                currentlyWorkingDayId = null;
                selectedCity = null;
                dayNumbering = 0;
                startDate = null;
            } else {
                dayNumbering = extras.getInt("dayNumbering");
                selectedCity = (City) extras.getSerializable("selectedCity");
                userId= extras.getString("userId");
                currentlyWorkingDayId = extras.getString("currDayId");
                startDate = (Calendar) extras.getSerializable("startDate");
            }
        } else {
            dayNumbering = savedInstanceState.getInt("dayNumbering");
            userId = savedInstanceState.getString("userId");
            currentlyWorkingDayId = savedInstanceState.getString("currDayId");
            startDate = (Calendar) savedInstanceState.getSerializable("startDate");
            selectedCity = (City) savedInstanceState.getSerializable("selectedCity");
        }

        //Set up fragments.
        FragmentManager manager = getFragmentManager();
        siteFragment = (AddSiteSiteFragment) manager.findFragmentById(R.id.add_site_site_fragment);

        //Set up map fragment.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.add_site_map_fragment);
        mapFragment.getView().setClickable(false);
        mapFragment.getMapAsync(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_default);
        setSupportActionBar(mToolbar);

        //Set up the category list.
        categoryList = new ArrayList<>();
        categoryList.add("Food");
        categoryList.add("Landmarks");
        categoryList.add("Museums");
        categoryList.add("Art");
        categoryList.add("Entertainment");
        categoryList.add("Outdoors");
        categoryList.add("Shopping");
        categoryList.add("Night Life");

        //Listener for the add button. Users must choose a site to proceed to the next step.
        final Button selectButton = (Button) findViewById(R.id.button_add_site);
        selectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (selectedSite != null) {
                    Intent intent = new Intent(AddSiteActivity.this, AddEventActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("selectedCity", selectedCity);
                    intent.putExtra("selectedSite", selectedSite);
                    startActivityForResult(intent, 4);
                } else {
                    Toast.makeText(AddSiteActivity.this, "Please select a site!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Receives the result for creating an event, and returns the event to EditScheduleActivity.
     * @param requestCode request code assigned when starting activity for result
     * @param resultCode result of activity on ok / cancel
     * @param intent the intent received from the activity for result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == ADD_EVENT_CODE) {
            if(resultCode == Activity.RESULT_OK){
                Event result = (Event) intent.getSerializableExtra("result");
                Site site = (Site) intent.getSerializableExtra("site");

                Intent resultIntent = new Intent();
                resultIntent.putExtra("result", result);
                resultIntent.putExtra("site", site);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }
    }

    /**
     * On back pressed, if drawer is open, close drawer.
     * If not, close the activity.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    /**
     * Sets a marker of the selected site on the map.
     * @param index index of the selected site in our list of sites.
     * @return true if the site is open this day, false if not.
     */
    public boolean setMarker(int index) {

        //Get the corresponding site.
        Site site = SelectSiteInfo.SITES.get(index);

        //Figure out which day of the week this day is.
        int day = ((startDate.get(Calendar.DAY_OF_WEEK) - 1) + (dayNumbering - 1)) % 7 + 1;

        //If the site is open that day, let the user do things.
        //If not, let the user know that he/she cannot go there on this particular day.
        if (site.openHours[day] != 0) {
            DecimalFormat dfHour = new DecimalFormat("##");
            DecimalFormat dfMinute = new DecimalFormat("00");
            int start = site.openHours[day];
            int end = site.closeHours[day];
            String startString;
            String endString;

            //Interpret the given open time.
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

            //Interpret the given close time.
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

            //Figure out where to shoot the map camera on.
            LatLng center = new LatLng(site.getLatitude(), site.getLongitude());
            double centerLat = center.latitude;
            centerLat += 0.00125;
            LatLng newCenter = new LatLng(centerLat, center.longitude);

            //Shoot the camera.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newCenter, 15));

            //Remove any pre-existing map markers.
            if (marker != null) {
                marker.remove();
            }

            //Set a marker on the map with the site's open and close hours on that day.
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
     * Changes the site fragment when category button is pressed.
     * @param index the category index in our array of categories.
     */
    @Override
    public void respond(int index) {
        alterArray(index - 1);
        android.os.SystemClock.sleep(500);
        setFragment();
    }

    /**
     * On map ready, the camera moves the camera to the city center.
     * @param googleMap Google map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng center = new LatLng(selectedCity.getLatitude(), selectedCity.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 12));
    }

    /**
     * Sets the currently selected site variable.
     * @param position the index position of the site in our arraylist.
     */
    public void setSite(int position) {
        this.selectedSite = SelectSiteInfo.SITES.get(position);
    }

    /**
     * Changes the site array corresponding to the category id.
     * @param category_id the id of the selected category.
     */
    protected void alterArray(int category_id) {
        String category = categoryList.get(category_id);

        //Interpret information to be used for retrofit.
        String temp = selectedCity.getCityCode() + "-" + category;

        //Get the city's sites based on the category.
        SelectSiteInfo.SITES = new ArrayList<>();

        /* Retrofit */
        String baseURL = "https://routemaker.herokuapp.com";
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseURL).build();
        RmAPI siteAPI = restAdapter.create(RmAPI.class);
        siteAPI.getSite(temp, new Callback<ArrayList<Site>>() {
            @Override
            public void success(ArrayList<Site> sites, Response response) {
                SelectSiteInfo.SITES = null;
                SelectSiteInfo.SITES = sites;
                for (int i = 0; i < sites.size(); i++) {
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
     * Replace the site list with a new list. (visualization)
     */
    private void setFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        AddSiteSiteFragment fragment = new AddSiteSiteFragment();
        transaction.replace(R.id.add_site_site_fragment, fragment, "site_list");
        transaction.commit();
    }
}
