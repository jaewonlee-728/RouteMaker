package com.example.oose.routemaker.CreateTrip;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ListView;

import com.example.oose.routemaker.API.RmAPI;
import com.example.oose.routemaker.Concrete.City;
import com.example.oose.routemaker.Concrete.Trip;
import com.example.oose.routemaker.Concrete.User;
import com.example.oose.routemaker.Constants;
import com.example.oose.routemaker.CurrentOrPastTrips.SelectCurrentOrPastTripActivity;
import com.example.oose.routemaker.Logistics.SettingActivity;
import com.example.oose.routemaker.NewsFeedActivity;
import com.example.oose.routemaker.Pop;
import com.example.oose.routemaker.R;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Activity for selecting start/end date and start time.
 */
public class SelectDateTimeActivity extends AppCompatActivity {

    /** Drawer. */
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /** Basic information about the user and the trip. */
    private String userId;
    private String tripId;
    private City selectedCity;
    private String cityCode;
    private long diff;
    private String startTime;

    /** Textview that shows the users information about the trip. */
    TextView dateAndTimeLabel_start;
    TextView dateAndTimeLabel_end;
    TextView dateAndTimeLabel_time;
    Calendar dateAndTime_start = Calendar.getInstance();
    Calendar dateAndTime_end = Calendar.getInstance();
    Calendar dateAndTime_time = Calendar.getInstance();

    //Retrofit
    String baseURL = "https://routemaker.herokuapp.com";   //BASE URL

    /** date picker for start date */
    DatePickerDialog.OnDateSetListener d1 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet (DatePicker view, int year, int month, int day) {
            dateAndTime_start.set(Calendar.YEAR, year);
            dateAndTime_start.set(Calendar.MONTH, month);
            dateAndTime_start.set(Calendar.DAY_OF_MONTH, day);
            Calendar today = Calendar.getInstance();
            if (today.compareTo(dateAndTime_start) > 0) {
                Toast.makeText(getApplicationContext(), "You can't travel back in time!", Toast.LENGTH_LONG).show();
            } else {
                updateLabel_start();
                diff = ((dateAndTime_end.getTimeInMillis()
                        - dateAndTime_start.getTimeInMillis()) / (1000 * 60 * 60 * 24)) + 1;
            }
        }
    };

    /** date picker for end date */
    DatePickerDialog.OnDateSetListener d2 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet (DatePicker view, int year, int month, int day) {
            dateAndTime_end.set(Calendar.YEAR, year);
            dateAndTime_end.set(Calendar.MONTH, month);
            dateAndTime_end.set(Calendar.DAY_OF_MONTH, day);
            updateLabel_end();
            diff = ((dateAndTime_end.getTimeInMillis()
                    - dateAndTime_start.getTimeInMillis()) / (1000 * 60 * 60 * 24)) + 1;
        }
    };

    /** time picker for start time */
    TimePickerDialog.OnTimeSetListener d3 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime_time.set(Calendar.HOUR_OF_DAY,hourOfDay);
            dateAndTime_time.set(Calendar.MINUTE, minute);
            updateLabel_time();
        }
    };

    /** update text representing start date */
    private void updateLabel_start() {
        Date date = dateAndTime_start.getTime();
        String startDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        dateAndTimeLabel_start.setText(startDate);
    }

    /** update text representing end date */
    private void updateLabel_end() {
        Date date = dateAndTime_end.getTime();
        String endDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        dateAndTimeLabel_end.setText(endDate);
    }

    /** update text representing start time */
    private void updateLabel_time() {
        Date date = dateAndTime_time.getTime();
        String startTime = new SimpleDateFormat("HH:mm").format(date);
        dateAndTimeLabel_time.setText(startTime);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date_time);
        diff = 1;

        //Drawer setup.
        mDrawerList = (ListView)findViewById(R.id.left_drawer_select_date_time);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout_select_date_time);
        addDrawerItems();
        setupDrawer();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_default);
        setSupportActionBar(mToolbar);
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        //Get information from the previous activity.
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userId= null;
            } else {
                userId= extras.getString("userId");
                tripId = extras.getString("tripId");
                selectedCity = (City) extras.getSerializable("city");
            }
        } else {
            userId = (String) savedInstanceState.getSerializable("userId");
            tripId = (String) savedInstanceState.getSerializable("tripId");
            selectedCity = (City) savedInstanceState.getSerializable("city");
        }

        //Listener on the start date button.
        Button startBtn = (Button) findViewById(R.id.startDate_button);
        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DatePickerDialog(SelectDateTimeActivity.this, d1, dateAndTime_start.get(Calendar.YEAR),
                        dateAndTime_start.get(Calendar.MONTH), dateAndTime_start.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Listener on the end date button.
        Button endBtn = (Button) findViewById(R.id.endDate_button);
        endBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DatePickerDialog(SelectDateTimeActivity.this, d2, dateAndTime_end.get(Calendar.YEAR),
                        dateAndTime_end.get(Calendar.MONTH), dateAndTime_end.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Listener on the start time button.
        Button timeBtn = (Button) findViewById(R.id.startTime_button);
        timeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new TimePickerDialog(SelectDateTimeActivity.this, d3, dateAndTime_time.get(Calendar.HOUR_OF_DAY),
                        dateAndTime_time.get(Calendar.MINUTE), true).show();
            }
        });

        dateAndTimeLabel_start = (TextView) findViewById(R.id.startDate_text);
        dateAndTimeLabel_end = (TextView) findViewById(R.id.endDate_text);
        dateAndTimeLabel_time = (TextView) findViewById(R.id.startTime_text);

        //Continue button.
        final Button continueButton = (Button) findViewById(R.id.button_continue_date_time);
        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Error checking.
                if (diff >= 1 && diff <= 7) {
                    if (!v.isSelected()) {
                        v.setSelected(true);
                    }
                    Date date = dateAndTime_time.getTime();
                    dateAndTime_start.getTime();

                    startTime = new SimpleDateFormat("HHmm").format(date);
                    RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseURL).build();
                    RmAPI datetimeAPI = restAdapter.create(RmAPI.class);
                    Trip trip = new Trip(tripId, selectedCity.getCityCode(), dateAndTime_start, dateAndTime_end);
                    datetimeAPI.putTripInfo(diff, startTime, trip, new Callback<Boolean>() {
                        @Override
                        public void success(Boolean success, Response response) {
                            if (success) {
                                Intent intent = new Intent(SelectDateTimeActivity.this, SelectSiteActivity.class);
                                intent.putExtra("numDays", diff);
                                intent.putExtra("userId", userId);
                                intent.putExtra("tripId", tripId);
                                intent.putExtra("city", selectedCity);
                                intent.putExtra("startDate", dateAndTime_start);
                                intent.putExtra("startTime", startTime);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            System.out.println(error);
                        }
                    });
                } else if (diff < 1) {
                    Toast.makeText(getApplicationContext(), "You can't travel back in time!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You can only plan up to 7 days with us!", Toast.LENGTH_LONG).show();
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
     * On back pressed, if drawer is open, close drawer.
     * If not, close the activity.
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
                Intent resultIntent = new Intent(SelectDateTimeActivity.this, SettingActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultIntent.putExtra("userInfo", user);
                startActivity(resultIntent);
                finish();
            }
        }
    }
}