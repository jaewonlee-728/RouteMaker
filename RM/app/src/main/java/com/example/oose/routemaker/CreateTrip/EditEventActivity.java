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
import com.example.oose.routemaker.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * User is directed to this page when he/she wants to edit an existing
 * event from the schedule list.
 */
public class EditEventActivity extends AppCompatActivity {

    /**basic information about the user and this trip. */
    String userId;
    String dayId;

    /** Newly crated event. */
    Event event;

    /** index of the event being considered right now. */
    int index;

    /** Text view that shows users information about the event. */
    TextView siteName;
    TextView startHourText;
    TextView endHourText;
    TextView eventDuration;

    /** Start time and end time buttons. */
    Button startButton;
    Button endButton;

    Calendar startTimeCalendar = Calendar.getInstance();
    Calendar endTimeCalendar = Calendar.getInstance();

    /** Duration of this event. */
    double duration;

    /** string formats for hour and minute */
    DecimalFormat dfHour = new DecimalFormat("##");
    DecimalFormat dfMinute = new DecimalFormat("00");

    /** time picker for start time */
    TimePickerDialog.OnTimeSetListener d1 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startTimeCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
            startTimeCalendar.set(Calendar.MINUTE, minute);
            updateStartTime();
            updateDuration();
        }
    };

    /** time picker for end time */
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
     * Updates the start time.
     */
    private void updateStartTime() {
        Date date = startTimeCalendar.getTime();
        String startTime = new SimpleDateFormat("HH:mm").format(date);
        startHourText.setText(startTime);
    }

    /**
     * Updates the end time.
     */
    private void updateEndTime() {
        Date date = endTimeCalendar.getTime();
        String endTime = new SimpleDateFormat("HH:mm").format(date);
        endHourText.setText(endTime);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        //Get information from the previous activity.
        Intent intent = getIntent();
        index = intent.getIntExtra("index", -1);
        dayId = intent.getStringExtra("currDayId");
        userId = intent.getStringExtra("userId");

        siteName = (TextView) findViewById(R.id.edit_event_site_name);
        startHourText = (TextView) findViewById(R.id.edit_start_time_text);
        endHourText = (TextView) findViewById(R.id.edit_end_time_text);
        eventDuration = (TextView) findViewById(R.id.edit_event_duration);

        //Get the event to be edited.
        event = EditScheduleActivity.currEventList.get(index);

        setTime(event);

        //Listens to the user's interactions with the start time button.
        startButton = (Button) findViewById(R.id.edit_start_time_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new TimePickerDialog(EditEventActivity.this, d1, startTimeCalendar.get(Calendar.HOUR_OF_DAY),
                        startTimeCalendar.get(Calendar.MINUTE), true).show();
                if (!v.isSelected()) {
                    v.setSelected(true);
                }
            }
        });

        //Listens to the user's interactions with the end time button.
        endButton = (Button) findViewById(R.id.edit_end_time_button);
        endButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new TimePickerDialog(EditEventActivity.this, d2, endTimeCalendar.get(Calendar.HOUR_OF_DAY),
                        endTimeCalendar.get(Calendar.MINUTE), true).show();
                if (!v.isSelected()) {
                    v.setSelected(true);
                }
            }
        });
        setEventData();

        //User confirms changes to this event.
        Button okButton = (Button) findViewById(R.id.button_edit_event_confirm);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event.setStartTime(startTimeCalendar.get(Calendar.HOUR_OF_DAY) * 100 + startTimeCalendar.get(Calendar.MINUTE));
                event.setEndTime(endTimeCalendar.get(Calendar.HOUR_OF_DAY) * 100 + endTimeCalendar.get(Calendar.MINUTE));
                event.setModifiedDuration(duration);

                Intent intent = new Intent();
                intent.putExtra("result", event);
                intent.putExtra("index", index);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * On back button pressed, cancel the transaction.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("result", event);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

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
     * Sets the event's site name.
     */
    public void setEventData() {
        siteName.setText(event.getSiteName());
    }

    /**
     * Updates the duration of the event when start/end time is selected.
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
