package com.example.oose.routemaker.CreateTrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
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
import com.example.oose.routemaker.Concrete.User;
import com.example.oose.routemaker.Constants;
import com.example.oose.routemaker.CurrentOrPastTrips.SelectCurrentOrPastTripActivity;
import com.example.oose.routemaker.Logistics.SettingActivity;
import com.example.oose.routemaker.NewsFeedActivity;
import com.example.oose.routemaker.Pop;
import com.example.oose.routemaker.R;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * User chooses a city to travel to.
 */
public class SelectCityActivity extends AppCompatActivity {

    /** Drawer. */
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /** City code of the city the user chose. */
    private static String cityCode;

    /** All the city buttons. */
    private List<Button> buttons;

    /** Basic information about the trip and the user. */
    private String userId;
    private String tripId;
    private City selectedCity;

    private RmAPI cityAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws NetworkOnMainThreadException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        //Drawer setup.
        mDrawerList = (ListView)findViewById(R.id.left_drawer_selectCity);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout_selectCity);
        addDrawerItems();
        setupDrawer();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_default);
        setSupportActionBar(mToolbar);
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        //Initialization.
        String baseURL = "https://routemaker.herokuapp.com";
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseURL).build();
        cityAPI = restAdapter.create(RmAPI.class);
        buttons = new ArrayList<>();

        //Get information about the user from previous activity.
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userId = null;
            } else {
                userId = extras.getString("userId");
            }
        } else {
            userId = (String) savedInstanceState.getSerializable("userId");
        }

        cityAPI.currentTrip(userId, new Callback<String>() {
            @Override
            public void success(String tid, Response response) {
                tripId = tid;
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println(error);
            }
        });

        cityCode = "";

        /** All the city buttons and their listeners from here. */

        final Button newyork = (Button) findViewById(R.id.button_city_newyork);
        newyork.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                    cityCode = "";
                } else {
                    setAllOthersFalse();
                    v.setSelected(true);
                    cityCode = "NYC";
                }
            }
        });

        final Button dc = (Button) findViewById(R.id.button_city_dc);
        dc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                    cityCode = "";
                } else {
                    setAllOthersFalse();
                    v.setSelected(true);
                    cityCode = "WAS";
                }
            }
        });

        final Button boston = (Button) findViewById(R.id.button_city_boston);
        boston.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                    cityCode = "";
                } else {
                    setAllOthersFalse();
                    v.setSelected(true);
                    cityCode = "BOS";
                }
            }
        });

        final Button sanfrancisco = (Button) findViewById(R.id.button_city_sanfrancisco);
        sanfrancisco.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                    cityCode = "";
                } else {
                    setAllOthersFalse();
                    v.setSelected(true);
                    cityCode = "SFO";
                }
            }
        });

        final Button losangeles = (Button) findViewById(R.id.button_city_losangeles);
        losangeles.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                    cityCode = "";
                } else {
                    setAllOthersFalse();
                    v.setSelected(true);
                    cityCode = "LAX";
                }
            }
        });

        final Button chicago = (Button) findViewById(R.id.button_city_chicago);
        chicago.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                    cityCode = "";
                } else {
                    setAllOthersFalse();
                    v.setSelected(true);
                    cityCode = "CHI";
                }
            }
        });

        final Button seattle = (Button) findViewById(R.id.button_city_seattle);
        seattle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                    cityCode = "";
                } else {
                    setAllOthersFalse();
                    v.setSelected(true);
                    cityCode = "SEA";
                }
            }
        });

        final Button baltimore = (Button) findViewById(R.id.button_city_baltimore);
        baltimore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                    cityCode = "";
                } else {
                    setAllOthersFalse();
                    v.setSelected(true);
                    cityCode = "BAL";
                }
            }
        });

        final Button miami = (Button) findViewById(R.id.button_city_miami);
        miami.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                    cityCode = "";
                } else {
                    setAllOthersFalse();
                    v.setSelected(true);
                    cityCode = "MIA";
                }
            }
        });

        //Continue button listener.
        final Button continueButton = (Button) findViewById(R.id.button_continue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (cityCode.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please choose a city!", Toast.LENGTH_LONG).show();
                } else {
                    cityAPI.getCity(cityCode, new Callback<City>() {
                        @Override
                        public void success(City city, Response response) {
                            Intent intent = new Intent(SelectCityActivity.this, SelectDateTimeActivity.class);
                            selectedCity = city;
                            intent.putExtra("userId", userId);
                            intent.putExtra("tripId", tripId);
                            intent.putExtra("city", selectedCity);
                            startActivity(intent);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            System.out.println(error);
                        }
                    });
                }
            }
        });

        //Add all the buttons to our list of buttons.
        buttons.add(newyork);
        buttons.add(boston);
        buttons.add(losangeles);
        buttons.add(miami);
        buttons.add(dc);
        buttons.add(chicago);
        buttons.add(seattle);
        buttons.add(baltimore);
        buttons.add(sanfrancisco);

    }

    /**
     * Before setting one city as selected, set all alse first.
     */
    private void setAllOthersFalse() {
        for (Button b : buttons) {
            b.setSelected(false);
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
                Intent resultIntent = new Intent(SelectCityActivity.this, SettingActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultIntent.putExtra("userInfo", user);
                startActivity(resultIntent);
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
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}