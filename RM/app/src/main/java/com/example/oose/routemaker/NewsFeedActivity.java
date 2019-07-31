package com.example.oose.routemaker;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.content.Intent;
import android.widget.TextView;

import com.example.oose.routemaker.API.RmAPI;
import com.example.oose.routemaker.Concrete.User;
import com.example.oose.routemaker.CreateTrip.SelectCityActivity;
import com.example.oose.routemaker.CurrentOrPastTrips.SelectCurrentOrPastTripActivity;
import com.example.oose.routemaker.Logistics.MainActivity;
import com.example.oose.routemaker.Logistics.SettingActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Activity that the user first enters once logging in.
 * We show the city of the day to the user.
 */
public class NewsFeedActivity extends AppCompatActivity {

    /** Drawer. */
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /** User's id/email. */
    String userId;

    /** List of cities and their images to be shown in the city of the day view. */
    private List<Integer> imageList;
    private List<String> cityName;

    /** Messages to be shown to the user. */
    TextView welcomeMessage;
    TextView cityMessage;
    ImageView cityImageView;

    //Retrofit
    String baseURL = "https://routemaker.herokuapp.com";   //BASE URL

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        //Drawer setup.
        mDrawerList = (ListView) findViewById(R.id.left_drawer_newsfeed);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_newsfeed);

        addDrawerItems();
        setupDrawer();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_default);
        setSupportActionBar(mToolbar);
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        //Get information from login page.
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userId= null;
            } else {
                userId= extras.getString("userId");
            }
        } else {
            userId = savedInstanceState.getString("userId");
        }

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseURL).build();
        RmAPI nameAPI = restAdapter.create(RmAPI.class);
        nameAPI.getName(userId, new Callback<String>() {
            @Override
            public void success(String name, Response response) {
                String username = name;
                Random randomNumber = new Random();
                int index = randomNumber.nextInt(9);

                welcomeMessage = (TextView) findViewById(R.id.hello_text);
                cityMessage = (TextView) findViewById(R.id.featured_city_message);
                cityImageView = (ImageView) findViewById(R.id.city_image);

                welcomeMessage.setText("Welcome to RouteMaker " + username + "!");
                cityMessage.setText("Hey " + username + ", why not travel to " + cityName.get(index) + "?");
                cityImageView.setImageResource(imageList.get(index));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        setUpSites();
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
     * On back pressed, if drawer is open, close drawer.
     * If not, close the activity.
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(NewsFeedActivity.this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
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
                Intent resultIntent = new Intent(NewsFeedActivity.this, SettingActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultIntent.putExtra("userInfo", user);
                startActivity(resultIntent);
                finish();
            }
        }
    }

    /**
     * Used when showing featured city of the day.
     */
    private void setUpSites() {
        imageList = new ArrayList<>();
        cityName = new ArrayList<>();

        imageList.add(R.drawable.newyork);
        imageList.add(R.drawable.dc);
        imageList.add(R.drawable.boston);
        imageList.add(R.drawable.baltimore);
        imageList.add(R.drawable.losangeles);
        imageList.add(R.drawable.chicago);
        imageList.add(R.drawable.seattle);
        imageList.add(R.drawable.sanfrancisco);
        imageList.add(R.drawable.miami);

        cityName.add("New York");
        cityName.add("Washington DC");
        cityName.add("Boston");
        cityName.add("Baltimore");
        cityName.add("Los Angeles");
        cityName.add("Chicago");
        cityName.add("Seattle");
        cityName.add("San Francisco");
        cityName.add("Miami");
    }
}