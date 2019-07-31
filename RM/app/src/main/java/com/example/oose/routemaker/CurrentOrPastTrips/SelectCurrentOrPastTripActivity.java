package com.example.oose.routemaker.CurrentOrPastTrips;

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
import android.widget.ListView;
import android.widget.TextView;

import com.example.oose.routemaker.API.RmAPI;
import com.example.oose.routemaker.Concrete.City;
import com.example.oose.routemaker.Concrete.Event;
import com.example.oose.routemaker.Concrete.Trip;
import com.example.oose.routemaker.Concrete.User;
import com.example.oose.routemaker.Constants;
import com.example.oose.routemaker.CreateTrip.EditScheduleActivity;
import com.example.oose.routemaker.CreateTrip.SelectCityActivity;
import com.example.oose.routemaker.Logistics.SettingActivity;
import com.example.oose.routemaker.NewsFeedActivity;
import com.example.oose.routemaker.Pop;
import com.example.oose.routemaker.R;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Lets the user view past or currently ongoing trips.
 * Depending on which item of the drawer the user clicked,
 * the user is directed to different actions.
 */
public class SelectCurrentOrPastTripActivity extends AppCompatActivity implements TripCommunicator {

    /** Drawer. */
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /** Basic information about the user and trip. */
    private String userId;
    private String currTripId;
    private long diff;

    /** Map of past or current event list and dayIds. */
    private Map<String, ArrayList<Event>> eventMap;

    private List<String> dayIds;

    /** List of past or current trips. */
    protected List<Trip> tripList;

    /** The selected trip to work with. */
    private Trip trip;

    private TextView tripText;

    /** Indicates which mode the user is in. Either currentTrip or pastTrip. */
    private String previousState;

    //Fragment
    CurrentOrPastTripFragment tripFragment;

    //Retrofit
    String baseURL = "https://routemaker.herokuapp.com";   //BASE URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_trip);

        //Get information from previous activity.
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        previousState = intent.getStringExtra("previousState");

        currTripId = "";

        tripText = (TextView) findViewById(R.id.select_trip_message);
        if (previousState.equals("pastTrip")) {
            String textToAdd = "These are your past trips.";
            tripText.setText(textToAdd);
        }

        //Drawer.
        mDrawerList = (ListView) findViewById(R.id.left_drawer_select_trip);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_select_trip);
        addDrawerItems();
        setupDrawer();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_default);
        setSupportActionBar(mToolbar);
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        //Set the tripList.
        tripList = new ArrayList<>();

        //Get the list of trips.
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseURL).build();
        RmAPI tripAPI = restAdapter.create(RmAPI.class);
        if (previousState.equals("currentTrip")) {
            tripAPI.getCurrTripList(userId, new Callback<ArrayList<Trip>>() {
                @Override
                public void success(ArrayList<Trip> trips, Response response) {
                    tripList = trips;
                    tripFragment = (CurrentOrPastTripFragment) getFragmentManager().findFragmentById(R.id.fragment_current_trip);
                    setFragment();
                }
                @Override
                public void failure(RetrofitError error) {
                    System.out.println(error);
                }
            });
        } else {
            tripAPI.getPastTripList(userId, new Callback<ArrayList<Trip>>() {
                @Override
                public void success(ArrayList<Trip> trips, Response response) {
                    tripList = trips;
                    tripFragment = (CurrentOrPastTripFragment) getFragmentManager().findFragmentById(R.id.fragment_current_trip);
                    setFragment();
                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println(error);
                }
            });
        }
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
            finish();
        }
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
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * Sync drawer state to the drawer layout.
     * @param savedInstanceState the saved instance state.
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /**
     * Finds out whether the drawer was pressed in the action bar.
     * @param item the menu item in the action bar.
     * @return true if drawer is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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
                Intent resultIntent = new Intent(SelectCurrentOrPastTripActivity.this, SettingActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultIntent.putExtra("userInfo", user);
                startActivity(resultIntent);
                finish();
            }
        }
    }

    /**
     * Sets the fragment to show the list of trips.
     */
    public void setFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        CurrentOrPastTripFragment fragment = new CurrentOrPastTripFragment();
        transaction.replace(R.id.fragment_current_trip, fragment, "trip_list");
        transaction.commit();
    }

    /**
     * Responds to which trip was clicked from the list.
     * @param index the index position of the selected trip.
     */
    public void respond(int index) {
        trip = tripList.get(index);
        currTripId = trip.getTripId();

        //Get detailed information about the selected trip.
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseURL).build();
        RmAPI tripAPI = restAdapter.create(RmAPI.class);
        tripAPI.getSelectedTrip(currTripId, new Callback<Map<String, Map<String, ArrayList<Event>>>>() {
            @Override
            public void success(Map<String, Map<String, ArrayList<Event>>> totalMap, Response response) {
                String cityStr = null;

                for (String cityString : totalMap.keySet()) {
                    cityStr = cityString;
                }
                City selectedCity = new Gson().fromJson(cityStr, City.class);

                eventMap = totalMap.get(cityStr);
                dayIds = new ArrayList<>();
                for (String s : eventMap.keySet()) {
                    dayIds.add(s);
                }
                diff = ((trip.getEndDate().getTimeInMillis() - trip.getStartDate().getTimeInMillis()) / (1000 * 60 * 60 * 24)) + 1;

                // All intent activity here!
                //Throw all the information that EditScheduleActivity requires from activities that call it.
                Intent intent = new Intent(SelectCurrentOrPastTripActivity.this, EditScheduleActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("tripId", currTripId);
                intent.putExtra("city", selectedCity);
                intent.putExtra("startDate", trip.getStartDate());
                intent.putExtra("previousState", previousState);
                intent.putExtra("numDays", diff);
                intent.putExtra("dayIds", (Serializable) dayIds);
                intent.putExtra("eventMap", (Serializable) eventMap);
                intent.putExtra("city", selectedCity);
                startActivity(intent);
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println(error);
            }
        });

    }

    /**
     * @return returns the list of trips.
     */
    @Override
    public List<Trip> getTripList() {
        return this.tripList;
    }
}
