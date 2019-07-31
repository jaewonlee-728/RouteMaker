package com.example.oose.routemaker.Logistics;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.content.Intent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oose.routemaker.API.RmAPI;
import com.example.oose.routemaker.Concrete.User;
import com.example.oose.routemaker.Constants;
import com.example.oose.routemaker.CreateTrip.SelectCityActivity;
import com.example.oose.routemaker.CurrentOrPastTrips.SelectCurrentOrPastTripActivity;
import com.example.oose.routemaker.NewsFeedActivity;
import com.example.oose.routemaker.Pop;
import com.example.oose.routemaker.R;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SettingActivity extends AppCompatActivity {

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    public User user;

    //Retrofit
    String baseURL = "https://routemaker.herokuapp.com";   //BASE URL

    RadioGroup radioGroup;
    RadioButton radioAgeButton;
    CheckBox museumBox;
    CheckBox artBox;
    CheckBox nightLifeBox;
    CheckBox entertainmentBox;
    CheckBox foodBox;
    CheckBox landmarkBox;
    CheckBox outdoorBox;
    CheckBox shoppingBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mDrawerList = (ListView)findViewById(R.id.left_drawer_setting);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout_setting);
        mActivityTitle = getTitle().toString();
        addDrawerItems();
        setupDrawer();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_default);
        setSupportActionBar(mToolbar);
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Bundle extra = getIntent().getExtras();
        user = (User) extra.get("userInfo");
        restoreInformation(user);
        changeInfo();
    }

    /**
     * On click method to change user information (ex. password, ageGroup, preferenceList)
     */
    public void changeInfo() {
        final EditText passwordField = (EditText) findViewById(R.id.password_setting_field);
        final EditText passwordConfirmField = (EditText) findViewById(R.id.password_settingConfirm_field);
        radioGroup = (RadioGroup) findViewById(R.id.ageGroup_setting_radio);

        museumBox = (CheckBox) findViewById(R.id.setting_museum);
        artBox = (CheckBox) findViewById(R.id.setting_art);
        nightLifeBox = (CheckBox) findViewById(R.id.setting_nightlife);
        entertainmentBox = (CheckBox) findViewById(R.id.setting_entertainment);
        foodBox = (CheckBox) findViewById(R.id.setting_food);
        landmarkBox = (CheckBox) findViewById(R.id.setting_landmark);
        outdoorBox = (CheckBox) findViewById(R.id.setting_outdoor);
        shoppingBox = (CheckBox) findViewById(R.id.setting_shopping);

        final Button button = (Button) findViewById(R.id.setting_confirm_button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioAgeButton = (RadioButton) findViewById(selectedId);
                final List<String> preferenceList = new ArrayList<>();

                final String password = passwordField.getText().toString();
                final String passwordConfirm = passwordConfirmField.getText().toString();
                final String ageGroup = radioAgeButton.getText().toString();

                boolean isChecked = false;
                if (museumBox.isChecked()) {
                    preferenceList.add(museumBox.getText().toString());
                    isChecked = true;
                }
                if (artBox.isChecked()) {
                    preferenceList.add(artBox.getText().toString());
                    isChecked = true;
                }
                if (nightLifeBox.isChecked()) {
                    preferenceList.add("NightLife");
                    isChecked = true;
                }
                if (entertainmentBox.isChecked()) {
                    preferenceList.add(entertainmentBox.getText().toString());
                    isChecked = true;
                }
                if (foodBox.isChecked()) {
                    preferenceList.add(foodBox.getText().toString());
                    isChecked = true;
                }
                if (landmarkBox.isChecked()) {
                    preferenceList.add(landmarkBox.getText().toString());
                    isChecked = true;
                }
                if (outdoorBox.isChecked()) {
                    preferenceList.add(outdoorBox.getText().toString());
                    isChecked = true;
                }
                if (shoppingBox.isChecked()) {
                    preferenceList.add(shoppingBox.getText().toString());
                    isChecked = true;
                }

                RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseURL).build();
                RmAPI settingAPI = restAdapter.create(RmAPI.class);

                //Check if there is at least one preference
                if (isChecked) {
                    //Check if there is a change in password
                    if (!password.equals("")) {
                        //Check if the confirmation password matches the entered password
                        if (password.equals(passwordConfirm)) {
                            User new_user = new User(user.getEmail(), user.getFirstName(),
                                    user.getLastName(), password,
                                    ageGroup, preferenceList);
                            settingAPI.updateUser(new_user, new Callback<User>() {
                                @Override
                                public void success(User retUser, Response response) {
                                    Toast.makeText(getApplicationContext(), "Successfully Updated!", Toast.LENGTH_LONG).show();
                                    user = retUser;
                                    Intent intent = new Intent(SettingActivity.this, NewsFeedActivity.class);
                                    intent.putExtra("userId", user.getEmail());
                                    startActivity(intent);
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    System.out.println(error);
                                }
                            });

                        } else {
                            Toast.makeText(getApplicationContext(), "Password did not match. Please confirm password!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        User new_user = new User(user.getEmail(), user.getFirstName(),
                                user.getLastName(), password,
                                ageGroup, preferenceList);
                        user = new_user;
                        settingAPI.updateUser(new_user, new Callback<User>() {
                            @Override
                            public void success(User retUser, Response response) {
                                Toast.makeText(getApplicationContext(), "Successfully Updated!", Toast.LENGTH_LONG).show();
                                user = retUser;
                                Intent intent = new Intent(SettingActivity.this, NewsFeedActivity.class);
                                intent.putExtra("userId", user.getEmail());
                                startActivity(intent);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                System.out.println(error);
                            }
                        });
                        Toast.makeText(getApplicationContext(), "Successfully Updated!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SettingActivity.this, NewsFeedActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Choose at least one preference!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void restoreInformation(User user) {
        TextView userFirstName = (TextView) findViewById(R.id.firstName_setting);
        userFirstName.setText(user.getFirstName());
        TextView userLastName = (TextView) findViewById(R.id.lastName_setting);
        userLastName.setText(user.getLastName());
        TextView userEmail = (TextView) findViewById(R.id.email_setting);
        userEmail.setText(user.getEmail());

        //Check user's original age.
        RadioButton ageButton;
        switch (user.getAgeGroup()) {
            case "20s or Below":
                ageButton = (RadioButton) findViewById(R.id.setting_20s);
                break;
            case "30s":
                ageButton = (RadioButton) findViewById(R.id.setting_30s);
                break;
            case "40s":
                ageButton = (RadioButton) findViewById(R.id.setting_40s);
                break;
            case "50s":
                ageButton = (RadioButton) findViewById(R.id.setting_50s);
                break;
            default:
                ageButton = (RadioButton) findViewById(R.id.setting_60s);
                break;
        }
        ageButton.setChecked(true);

        CheckBox preferenceBox = null;
        for (String s : user.getPreferenceList()) {
            switch (s) {
                case "Museums":
                    preferenceBox = (CheckBox) findViewById(R.id.setting_museum);
                    break;
                case "Food":
                    preferenceBox = (CheckBox) findViewById(R.id.setting_food);
                    break;
                case "Art":
                    preferenceBox = (CheckBox) findViewById(R.id.setting_art);
                    break;
                case "Landmarks":
                    preferenceBox = (CheckBox) findViewById(R.id.setting_landmark);
                    break;
                case "NightLife":
                    preferenceBox = (CheckBox) findViewById(R.id.setting_nightlife);
                    break;
                case "Outdoors":
                    preferenceBox = (CheckBox) findViewById(R.id.setting_outdoor);
                    break;
                case "Entertainment":
                    preferenceBox = (CheckBox) findViewById(R.id.setting_entertainment);
                    break;
                case "Shopping":
                    preferenceBox = (CheckBox) findViewById(R.id.setting_shopping);
                    break;
            }
            preferenceBox.setChecked(true);
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
                intent.putExtra("userId", user.getEmail());
                startActivity(intent);
                finish();
                break;
            case 1:
                intent = new Intent(this, SelectCityActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", user.getEmail());
                startActivity(intent);
                finish();
                break;
            case 2:
                intent = new Intent(this, SelectCurrentOrPastTripActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", user.getEmail());
                intent.putExtra("previousState", "currentTrip");
                startActivity(intent);
                finish();
                break;
            case 3:
                intent = new Intent(this, SelectCurrentOrPastTripActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", user.getEmail());
                intent.putExtra("previousState", "pastTrip");
                startActivity(intent);
                finish();
                break;
            case 4:
                intent = new Intent(this, Pop.class);
                intent.putExtra("userId", user.getEmail());
                startActivityForResult(intent, Constants.SETTING_CODE);
                break;
            default :
                intent = new Intent(this, NewsFeedActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", user.getEmail());
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
                Intent resultIntent = new Intent(SettingActivity.this, SettingActivity.class)
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
            Intent intent = new Intent(SettingActivity.this, NewsFeedActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
