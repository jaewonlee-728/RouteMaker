package com.example.oose.routemaker.CreateTrip;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.example.oose.routemaker.Concrete.User;
import com.example.oose.routemaker.Constants;
import com.example.oose.routemaker.CurrentOrPastTrips.SelectCurrentOrPastTripActivity;
import com.example.oose.routemaker.GoogleMapsActivities.GoogleMapsRouteActivity;
import com.example.oose.routemaker.Logistics.SettingActivity;
import com.example.oose.routemaker.NewsFeedActivity;
import com.example.oose.routemaker.Pop;
import com.example.oose.routemaker.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EditScheduleActivity extends AppCompatActivity implements EditCommunicator {

    /** Map that contains the list of events for each day of the trip. */
    public static Map<String, ArrayList<Event>> eventMap;

    /** The list of events we are looking at right now. */
    public static List<Event> currEventList;

    /** Drawer. */
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /** User id. */
    private String userId;

    /** Number of days of the trip. */
    private long diff;

    /** List of dayIds--dayId is the primary key for Day. */
    private List<String> dayIds;

    /** The day the user is currently working with. */
    private String currentlyWorkingDayId;

    /** Transport button: drive car. Rest of the buttons are declared locally. */
    private Button button_Drive;

    /** List of transport button. */
    private List<Button> transport_buttons;

    /** Indicates the user's choice of transport. Used for map routing. */
    private String travelMode;

    /** Day numbering for each day--starts from 1. */
    private int dayNumbering;

    /** Id of this trip. */
    private String tripId;

    /** The start date of this trip. */
    private Calendar startDate;

    /** The city for this trip. */
    private City selectedCity;

    /**Indicates which activity got directed to this class.
     * Three cases: "createTrip", "currentTrip", "pastTrip".
     * Things we do for those three cases differ in this class. */
    private String previousState;

    /** Request code for editing or deleting an existing event. */
    private final int EDIT_CODE = 1;

    /** Request code for adding a newly created event. */
    private final int ADD_CODE = 3;

    /** Retrofit. */
    private String baseURL = "https://routemaker.herokuapp.com";   //BASE URL

    /** Fragments. */
    EditScheduleEventFragment eventFragment;
    EditScheduleInfoFragment siteInfoFragment;
    FragmentManager manager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);
        travelMode = "driving";
        dayNumbering = 0;
        diff = 1;
        dayIds = new ArrayList<>();
        transport_buttons = new ArrayList<>();

        //Get intent extra stuff from previous activity
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userId= null;
                diff = 1;
                tripId = null;
                selectedCity = null;
                eventMap = null;
                dayIds = null;
                startDate = null;
                previousState = null;
            } else {
                userId = extras.getString("userId");
                tripId = extras.getString("tripId");
                selectedCity = (City) extras.getSerializable("city");
                diff = extras.getLong("numDays");
                eventMap = (HashMap) extras.getSerializable("eventMap");
                dayIds = extras.getStringArrayList("dayIds");
                startDate = (Calendar) extras.getSerializable("startDate");
                previousState = extras.getString("previousState");
            }
        } else {
            userId= savedInstanceState.getString("userId");
            tripId= savedInstanceState.getString("tripId");
            selectedCity = (City) savedInstanceState.getSerializable("city");
            diff = (long) savedInstanceState.getSerializable("numDays");
            eventMap = (HashMap) savedInstanceState.getSerializable("eventMap");
            dayIds = savedInstanceState.getStringArrayList("dayIds");
            startDate = (Calendar) savedInstanceState.getSerializable("startDate");
            previousState = savedInstanceState.getString("previousState");
        }

        //Get the currently working day and event list.
        assert dayIds != null;
        if (dayIds.size() > 0) {
            currentlyWorkingDayId = dayIds.get(0);
        } else {
            currentlyWorkingDayId = "";
        }
        currEventList = new ArrayList<>();
//        currEventList = eventMap.get(dayIds.get(0));

        //Drawer work
        mDrawerList = (ListView)findViewById(R.id.left_drawer_edit_schedule);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout_edit_schedule);
        addDrawerItems();
        setupDrawer();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_default);
        setSupportActionBar(mToolbar);
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        //Fragment managers.
        manager = getFragmentManager();
        eventFragment = (EditScheduleEventFragment) manager.findFragmentById(R.id.fragment_schedule_list);
        eventFragment.setCommunicator(this);

        //Transport buttons
        setTransportButtons();

        //FloatingActionButton for adding site.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_site);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (previousState.equals("createTrip") || previousState.equals("currentTrip")) {
                    if (currEventList != null) {
                        int dayNumbering = dayIds.indexOf(currentlyWorkingDayId) + 1;
                        SelectSiteInfo.SITES = new ArrayList<>();
                        Intent intent = new Intent(EditScheduleActivity.this, AddSiteActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("dayNumbering", dayNumbering);
                        intent.putExtra("currDayId", currentlyWorkingDayId);
                        intent.putExtra("selectedCity", selectedCity);
                        intent.putExtra("startDate", startDate);
                        intent.putExtra("eventMap", (Serializable) eventMap);
                        startActivityForResult(intent, 3);
                    }
                } else { //previousState.equals("pastTrip")
                    Toast.makeText(getApplicationContext(), "You cannot add to past trips!", Toast.LENGTH_LONG).show();
                }
            }
        });

        if (previousState.equals("pastTrip")) {
            fab.setVisibility(View.GONE);
        }

        //View Map button
        final Button viewMapButton = (Button) findViewById(R.id.button_view_map);
        viewMapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (currEventList.size() != 0) {
                    Intent intent = new Intent(EditScheduleActivity.this, GoogleMapsRouteActivity.class);
                    intent.putExtra("travelMode", travelMode);
                    intent.putExtra("city", selectedCity);
                    intent.putExtra("currEventList", (Serializable) currEventList);
                    startActivity(intent);
                } else {
                    if (dayNumbering == 0) {
                        Toast.makeText(getApplicationContext(), "You have to select a day first!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No route to display for Day" + dayNumbering + "!", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        //Finalize button --> Text on the button is "OK"
        //The actions we take for createTrip, currentTrip, and pastTrip are different.
        final Button finalizeButton = (Button) findViewById(R.id.button_finalize);
        finalizeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (previousState) {
                    case "createTrip": {
                        boolean noConflict = true;
                        for (int i = 0; i < dayIds.size(); i++) {
                            ArrayList<Event> events = eventMap.get(dayIds.get(i));
                            for (int j = 0; j < events.size() - 1; j++) {
                                if (events.get(j).getEndTime() > events.get(j + 1).getStartTime()) {
                                    noConflict = false;
                                }
                            }
                        }

                        //No time conflicts.
                        if (noConflict) {
                            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseURL).build();
                            RmAPI eventAPI = restAdapter.create(RmAPI.class);
                            eventAPI.putEvent(userId, eventMap, new Callback<Boolean>() {
                                @Override
                                public void success(Boolean aBoolean, Response response) {
                                    System.out.println(aBoolean);
                                    Intent intent = new Intent(EditScheduleActivity.this, CreateConfirmActivity.class);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("tripId", tripId);
                                    intent.putExtra("startDate", startDate);
                                    intent.putExtra("diff", diff);
                                    intent.putExtra("previousState", previousState);
                                    intent.putExtra("city", selectedCity);
                                    detachFragments();
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    System.out.println(error);
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "You have conflicting schedules!", Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
                    case "currentTrip": {
                        boolean noConflict = true;
                        for (int i = 0; i < dayIds.size(); i++) {
                            ArrayList<Event> events = eventMap.get(dayIds.get(i));
                            for (int j = 0; j < events.size() - 1; j++) {
                                if (events.get(j).getEndTime() > events.get(j + 1).getStartTime()) {
                                    noConflict = false;
                                }
                            }
                        }

                        //No time conflict.
                        if (noConflict) {
                            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseURL).build();
                            RmAPI eventAPI = restAdapter.create(RmAPI.class);
                            eventAPI.updateEvent(eventMap, new Callback<Boolean>() {
                                @Override
                                public void success(Boolean aBoolean, Response response) {
                                    System.out.println(aBoolean);
                                    Intent intent = new Intent(EditScheduleActivity.this, CreateConfirmActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("tripId", tripId);
                                    intent.putExtra("startDate", startDate);
                                    intent.putExtra("diff", diff);
                                    intent.putExtra("previousState", previousState);
                                    intent.putExtra("city", selectedCity);
                                    detachFragments();
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    System.out.println(error);
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "You have conflicting schedules!", Toast.LENGTH_LONG).show();
                        }

                        break;
                    }
                    default:  //previousState.equals("pastTrip")
                        Intent intent = new Intent(EditScheduleActivity.this, NewsFeedActivity.class);
                        intent.putExtra("userId", userId);
                        detachFragments();
                        startActivity(intent);
                        finish();
                        break;
                }

            }

        });

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
                detachFragments();
                startActivity(intent);
                finish();
                break;
            case 1:
                intent = new Intent(this, SelectCityActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", userId);
                detachFragments();
                startActivity(intent);
                finish();
                break;
            case 2:
                intent = new Intent(this, SelectCurrentOrPastTripActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", userId);
                intent.putExtra("previousState", "currentTrip");
                detachFragments();
                startActivity(intent);
                finish();
                break;
            case 3:
                intent = new Intent(this, SelectCurrentOrPastTripActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", userId);
                intent.putExtra("previousState", "pastTrip");
                detachFragments();
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
                detachFragments();
                startActivity(intent);
                finish();
                break;
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
            detachFragments();
            finish();
        }
    }

    /**
     * Sets the transport buttons.
     */
    public void setTransportButtons() {

        //Driving option.
        button_Drive = (Button) findViewById(R.id.button_drive);
        button_Drive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!(v.isSelected())) {
                    setAllTransportFalse();
                    v.setSelected(true);
                    travelMode = "driving";
                }
            }
        });

        //Walking option.
        Button button_Walk = (Button) findViewById(R.id.button_walk);
        button_Walk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!(v.isSelected())) {
                    setAllTransportFalse();
                    v.setSelected(true);
                    travelMode = "walking";
                }
            }
        });

        //Bike option.
        Button button_Bike = (Button) findViewById(R.id.button_bike);
        button_Bike.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!(v.isSelected())) {
                    setAllTransportFalse();
                    v.setSelected(true);
                    travelMode = "bicycling";
                }
            }
        });

        transport_buttons.add(button_Drive);
        transport_buttons.add(button_Walk);
        transport_buttons.add(button_Bike);
    }

    /**
     * Before we set a transport button as selected,
     * set all of them unselected first.
     */
    public void setAllTransportFalse() {
        for (Button b : transport_buttons) {
            b.setSelected(false);
        }
    }

    /**
     * Resets the transport mode we want to show the map routes.
     * also change event list to show in event fragment.
     * @param index the day being considered.
     */
    @Override
    public void resetTransport(int index) {
        setAllTransportFalse();
        button_Drive.setSelected(true);
        travelMode = "driving";

        currentlyWorkingDayId = dayIds.get(index - 1);
        dayNumbering = index;
        currEventList = eventMap.get(currentlyWorkingDayId);

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        EditScheduleEventFragment siteFragment = new EditScheduleEventFragment();
        transaction.replace(R.id.fragment_schedule_list, siteFragment, "site");
        transaction.commit();
    }

    /**
     * Get the number of days in the trip.
     * @return the number of days in this trip.
     */
    public long getDiff() {
        return this.diff;
    }

    /**
     * Responds to clicking on a specific event, and opens up a information page.
     * @param index
     */
    @Override
    public void respond(int index) {
        siteInfoFragment = (EditScheduleInfoFragment) manager.findFragmentById(R.id.fragment_schedule_info);
        Intent intent = new Intent(this, EditScheduleInfoActivity.class);
        intent.putExtra("index", index);
        intent.putExtra("previousState", previousState);
        intent.putExtra("currDayId", currentlyWorkingDayId);
        intent.putExtra("eventMap", (Serializable) eventMap);
        startActivityForResult(intent, EDIT_CODE);
    }

    /**
     * Responds to activities with result,
     * which are editing / deleting / adding events, and sort schedule accordingly.
     * @param requestCode request code assigned when starting activity for result
     * @param resultCode result of activity on ok / cancel
     * @param intent the intent received from the activity for result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // edit / delete
        if (requestCode == EDIT_CODE) {
            if(resultCode == Activity.RESULT_OK){
                // receives the event and index of where the event existed,
                // a null event for deletion
                Event result = (Event) intent.getSerializableExtra("result");
                int index = intent.getIntExtra("index", -1);
                // if successfully received index
                if (index != -1) {
                    // remove existing event at the index
                    eventMap.get(currentlyWorkingDayId).remove(index);
                    // if an event is received, add the event to the list,
                    // in order of event start time.
                    if (result != null) {
                        int insertIndex = 0;
                        int numEl = eventMap.get(currentlyWorkingDayId).size();
                        while (insertIndex < numEl && eventMap.get(currentlyWorkingDayId).get(insertIndex).getStartTime() < result.getStartTime()) {
                            insertIndex++;
                        }
                        eventMap.get(currentlyWorkingDayId).add(insertIndex, result);
                    }
                    // replace fragment to a list containing the edited schedule
                    resetTransport(dayIds.indexOf(currentlyWorkingDayId) + 1);
                }
            }
            // if user canceled editing or pressed back button
            if (resultCode == Activity.RESULT_CANCELED) {
                resetTransport(dayIds.indexOf(currentlyWorkingDayId) + 1);
            }
        }

        // addition
        if (requestCode == ADD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // receives the created event
                Event result = (Event) intent.getSerializableExtra("result");
                // adds event to the list, in order of event start time
                int index = 0;
                int numEl = eventMap.get(currentlyWorkingDayId).size();
                while (index < numEl && eventMap.get(currentlyWorkingDayId).get(index).getStartTime() < result.getStartTime()) {
                    index++;
                }
                eventMap.get(currentlyWorkingDayId).add(index, result);
            }
            // replace fragment to a list containing the edited schedule
            resetTransport(dayIds.indexOf(currentlyWorkingDayId) + 1);
        }

        if (requestCode == Constants.SETTING_CODE) {
            if (resultCode == RESULT_OK) {
                User user = (User) intent.getSerializableExtra("userInfo");
                Intent resultIntent = new Intent(EditScheduleActivity.this, SettingActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultIntent.putExtra("userInfo", user);
                detachFragments();
                startActivity(resultIntent);
                finish();
            }
        }
    }

    /**
     * Detaches fragments attached to activity when moving to a different activity,
     * such as finalizing trip or returning to selecting sites.
     */
    private void detachFragments() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        eventFragment = (EditScheduleEventFragment) manager.findFragmentById(R.id.fragment_schedule_list);
        eventFragment.clearData();
        EditScheduleDayFragment dayFragment = (EditScheduleDayFragment) manager.findFragmentById(R.id.edit_schedule_day_fragment);
        transaction.remove(dayFragment);
        transaction.remove(eventFragment);
        transaction.commit();
    }
}