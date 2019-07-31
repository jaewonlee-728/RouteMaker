package com.example.oose.routemaker.CreateTrip;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.oose.routemaker.Concrete.City;
import com.example.oose.routemaker.Concrete.User;
import com.example.oose.routemaker.Constants;
import com.example.oose.routemaker.CurrentOrPastTrips.SelectCurrentOrPastTripActivity;
import com.example.oose.routemaker.Logistics.SettingActivity;
import com.example.oose.routemaker.NewsFeedActivity;
import com.example.oose.routemaker.Pop;
import com.example.oose.routemaker.R;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * The page where it says: Your trip has been created!
 * Send to your email?
 * Add to your calendar?
 *
 * Automatic email generation code from Tiemen Schut
 * http://www.tiemenschut.com/how-to-send-e-mail-directly-from-android-application/
 */
public class CreateConfirmActivity extends AppCompatActivity {

    /** Drawer. */
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /** Basic information about the user and the trip. */
    private City selectedCity;
    private Calendar startDate;
    private long diff;
    private String userId;
    private String previousState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_confirm);

        //Get information from previous activity.
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userId= null;
                diff = 1;
                selectedCity = null;
                startDate = null;
                previousState = null;
            } else {
                userId= extras.getString("userId");
                selectedCity = (City) extras.getSerializable("city");
                diff = extras.getLong("diff");
                startDate = (Calendar) extras.getSerializable("startDate");
                previousState = extras.getString("previousState");
            }
        } else {
            userId= (String) savedInstanceState.getSerializable("userId");
            selectedCity = (City) savedInstanceState.getSerializable("city");
            diff = (long) savedInstanceState.getSerializable("diff");
            startDate = (Calendar) savedInstanceState.getSerializable("startDate");
            previousState = savedInstanceState.getString("previousState");
        }

        //Set up drawer.
        mDrawerList = (ListView)findViewById(R.id.left_drawer_create_confirm);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout_create_confirm);
        addDrawerItems();
        setupDrawer();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_default);
        setSupportActionBar(mToolbar);
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        //User can send an email to himself/herself about the creation/modification
        //of this trip.
        final Button emailButton = (Button) findViewById(R.id.button_send_email);
        emailButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String mode;
                if (previousState.equals("createTrip")) {
                    mode = "created";
                } else {
                    mode = "modified";
                }

                //Sends a confirmation email to the user.
                String email = userId;
                String subject = "[RouteMaker admin] Your trip has been " + mode + "!";

                //Parses the date information to Strings.
                Date start_date = startDate.getTime();
                String start_date_string= new SimpleDateFormat("MM/dd").format(start_date);
                startDate.add(Calendar.DATE, (int) diff - 1);
                Date end_date = startDate.getTime();
                String end_date_string = new SimpleDateFormat("MM/dd").format(end_date);

                //Simple message to send to the user that their trip has been created.
                String content = "Your " + start_date_string + " - " + end_date_string
                                 + " trip to " + selectedCity.getCityName() + " " + "has been " +  mode + "!\n"
                                 + "Please go to the RouteMaker app to check the details.\n"
                                 + "Have a fun trip!";
                sendMail(email, subject, content);
            }
        });

        //Finalize button.
        final Button returnButton = (Button) findViewById(R.id.button_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CreateConfirmActivity.this, NewsFeedActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

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
            Intent intent = new Intent(CreateConfirmActivity.this, NewsFeedActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
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
     * Finds out whether the drawer was pressed in the action bar.
     * @param item the menu item in the action bar.
     * @return true if drawer is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Activate the navigation drawer toggle
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
                Intent resultIntent = new Intent(CreateConfirmActivity.this, SettingActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultIntent.putExtra("userInfo", user);
                startActivity(resultIntent);
                finish();
            }
        }
    }

    /**
     * Sends an email to the user.
     * @param email email address of the user.
     * @param subject subject line of the email.
     * @param messageBody email contents.
     */
    private void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the message.
     * We send it with our admin gmail account: routemaker.jhu@gmail.com
     * @param email the user's email.
     * @param subject the email subject ine.
     * @param messageBody email contents.
     * @param session the email session.
     * @return the message.
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("routemaker.jhu@gmail.com", "RouteMaker Admin"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("routemaker.jhu@gmail.com", "oose2015");
            }
        });
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(CreateConfirmActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}


