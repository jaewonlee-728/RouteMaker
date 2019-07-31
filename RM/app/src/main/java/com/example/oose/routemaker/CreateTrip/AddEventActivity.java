package com.example.oose.routemaker.CreateTrip;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.oose.routemaker.Concrete.Event;
import com.example.oose.routemaker.Concrete.Site;
import com.example.oose.routemaker.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity that adds an event to the user's current schedule list.
 * It allows the user to select a start time and end time for the event.
 */
public class AddEventActivity extends AppCompatActivity {

    /** User's user id. */
    String userId;

    /** Index of the site for this event in our list. */
    int index;

    /** Event that is being created. */
    Event event;

    /** Corresponding site for this event. */
    Site site;

    /** Text views showing information about this event being created. */
    TextView siteName;
    TextView startHourText;
    TextView endHourText;
    TextView eventDuration;

    /** Start and end time buttons */
    Button startButton;
    Button endButton;

    Calendar startTimeCalendar = Calendar.getInstance();
    Calendar endTimeCalendar = Calendar.getInstance();

    /** Duration of this event. */
    double duration;

    /** Decimal format declarations. */
    DecimalFormat dfHour = new DecimalFormat("##");
    DecimalFormat dfMinute = new DecimalFormat("00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        //Gets the information passed on by the previous activity.
        Intent intent = getIntent();
        index = intent.getIntExtra("index", -1);
        userId = intent.getStringExtra("userId");
        site = (Site) intent.getSerializableExtra("selectedSite");

        //Creates a new event with just the site information and no time information.
        //The time information is for the user to provide very soon.
        event = new Event("new", 0000, 2359, site.getSiteId(), site.getSiteName(), site.getLatitude(), site.getLongitude());
        siteName = (TextView) findViewById(R.id.edit_event_site_name);
        startHourText = (TextView) findViewById(R.id.edit_start_time_text);
        endHourText = (TextView) findViewById(R.id.edit_end_time_text);
        eventDuration = (TextView) findViewById(R.id.edit_event_duration);

        //Allows the user to pick the start and end times of this event.
        setTime(event);

        //Listener for the start time button.
        startButton = (Button) findViewById(R.id.edit_start_time_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new TimePickerDialog(AddEventActivity.this, d1, startTimeCalendar.get(Calendar.HOUR_OF_DAY),
                        startTimeCalendar.get(Calendar.MINUTE), true).show();
                if (!v.isSelected()) {
                    v.setSelected(true);
                }
            }
        });

        //Listener for the end time button.
        endButton = (Button) findViewById(R.id.edit_end_time_button);
        endButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new TimePickerDialog(AddEventActivity.this, d2, endTimeCalendar.get(Calendar.HOUR_OF_DAY),
                        endTimeCalendar.get(Calendar.MINUTE), true).show();
                if (!v.isSelected()) {
                    v.setSelected(true);
                }
            }
        });

        setEventData();

        //Listener for the create event button. A new event gets created/finalized here.
        Button createButton = (Button) findViewById(R.id.button_edit_event_confirm);
        createButton.setText(R.string.create_event);
        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Set the start time, end time, and duration for good. (can edit in the future)
                event.setStartTime(startTimeCalendar.get(Calendar.HOUR_OF_DAY) * 100
                        + startTimeCalendar.get(Calendar.MINUTE));
                event.setEndTime(endTimeCalendar.get(Calendar.HOUR_OF_DAY) * 100
                        + endTimeCalendar.get(Calendar.MINUTE));
                event.setModifiedDuration(duration);

                Intent intent = new Intent();
                intent.putExtra("result", event);
                intent.putExtra("site", site);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }

    /**
     * Time picker that listens to user's input on start time.
     */
    TimePickerDialog.OnTimeSetListener d1 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startTimeCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
            startTimeCalendar.set(Calendar.MINUTE, minute);
            updateStartTime();
            updateDuration();
        }
    };

    /**
     * Time picker that listens to user's input on end time.
     */
    TimePickerDialog.OnTimeSetListener d2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            endTimeCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
            endTimeCalendar.set(Calendar.MINUTE, minute);
            updateEndTime();
            updateDuration();
        }
    };

    /**
     * Sets the time for this event using the user input.
     * @param event the event we are creating right now.
     */
    private void setTime(Event event) {

        startTimeCalendar.set(Calendar.HOUR_OF_DAY, event.getStartTime() / 100);
        startTimeCalendar.set(Calendar.MINUTE, event.getStartTime() % 100);
        updateStartTime();

        endTimeCalendar.set(Calendar.HOUR_OF_DAY, event.getEndTime() / 100);
        endTimeCalendar.set(Calendar.MINUTE, event.getEndTime() % 100);
        updateEndTime();

        int hour = (int) event.getDuration();
        String hourString = dfHour.format(hour);
        int minute = (int) ((event.getDuration() - hour) * 60);
        String minuteString = dfMinute.format(minute);
        String textToPut = "duration: " + hourString + " hours " + minuteString + " minutes";
        eventDuration.setText(textToPut);
    }

    /**
     * Updates the start time of this event.
     */
    private void updateStartTime() {
        Date date = startTimeCalendar.getTime();
        String startTime = new SimpleDateFormat("HH:mm").format(date);
        startHourText.setText(startTime);
    }

    /**
     * Updates the end time of this event.
     */
    private void updateEndTime() {
        Date date = endTimeCalendar.getTime();
        String endTime = new SimpleDateFormat("HH:mm").format(date);
        endHourText.setText(endTime);
    }

    /**
     * On back button pressed, we cancel the transactions.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("result", event);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    /**
     * Sets the site's site name.
     */
    public void setEventData() {
        siteName.setText(site.getSiteName());
    }

    /**
     * Update's the event's duration based on the user's input.
     */
    private void updateDuration() {
        duration = ((double) endTimeCalendar.getTime().getTime() - (startTimeCalendar.getTime().getTime())) / (1000 * 60 * 60.0);
        int hour = (int) duration;
        String hourString = dfHour.format(hour);
        int minute = (int) Math.round((duration - hour) * 60);
        String minuteString = dfMinute.format(minute);
        String textToPut = "duration: " + hourString + " hours " + minuteString + " minutes";
        eventDuration.setText(textToPut);
    }

}

