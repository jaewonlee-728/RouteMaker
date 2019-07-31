package com.example.oose.routemaker.CreateTrip;

import android.app.Activity;
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

import com.example.oose.routemaker.Concrete.Event;
import com.example.oose.routemaker.Concrete.User;
import com.example.oose.routemaker.Constants;
import com.example.oose.routemaker.CurrentOrPastTrips.SelectCurrentOrPastTripActivity;
import com.example.oose.routemaker.Logistics.SettingActivity;
import com.example.oose.routemaker.NewsFeedActivity;
import com.example.oose.routemaker.Pop;
import com.example.oose.routemaker.R;

import java.io.Serializable;

/**
 * Shows specific information about an event once the user
 * selects it from the schedule view.
 */
public class EditScheduleInfoActivity extends AppCompatActivity {

    /** Drawer. */
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /** Basic information about the user and the trip. */
    private String userId;
    private String dayId;
    private int index;
    private String previousState;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        Intent intent = getIntent();
        index = intent.getIntExtra("index", 0);
        dayId = intent.getStringExtra("currDayId");
        previousState = intent.getStringExtra("previousState");

        //Drawer setup.
        mDrawerList = (ListView) findViewById(R.id.left_drawer_event_info);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_event_info);
        addDrawerItems();
        setupDrawer();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_default);
        setSupportActionBar(mToolbar);
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        //Get the event the user wants to edit.
        event = EditScheduleActivity.currEventList.get(index);

        //Show information about the event.
        EditScheduleInfoFragment infoFragment = (EditScheduleInfoFragment)
                getFragmentManager().findFragmentById(R.id.fragment_schedule_info);
        infoFragment.changeEventData(index);

        //Listener for the "Edit" button.
        final Button editButton = (Button) findViewById(R.id.button_edit_event);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!previousState.equals("pastTrip")) {
                    Intent intent = new Intent(EditScheduleInfoActivity.this, EditEventActivity.class);
                    intent.putExtra("index", index);
                    intent.putExtra("userId", userId);
                    intent.putExtra("currDayId", dayId);
                    intent.putExtra("event", event);
                    startActivityForResult(intent, 2);
                } else {
                    Toast.makeText(getApplicationContext(), "You cannot edit past trip schedules!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        //Listener for the "Delete" button.
        final Button deleteButton = (Button) findViewById(R.id.button_delete_event);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!previousState.equals("pastTrip")) {
                    // delete current event
                    Intent intent = new Intent();
                    intent.putExtra("index", index);
                    intent.putExtra("result", (Serializable) null);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "You cannot delete past trip schedules!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        if (previousState.equals("pastTrip")) {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
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
            Intent intent = new Intent();
            intent.putExtra("index", index);
            intent.putExtra("result", event);
            setResult(Activity.RESULT_CANCELED, intent);
            finish();        }
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
                Intent resultIntent = new Intent(EditScheduleInfoActivity.this, SettingActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultIntent.putExtra("userInfo", user);
                startActivity(resultIntent);
                finish();
            }
        }

        if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                Event result = (Event) intent.getSerializableExtra("result");
                int index = intent.getIntExtra("index", -1);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result", result);
                resultIntent.putExtra("index", index);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }
    }
}
