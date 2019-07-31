package com.example.oose.routemaker.CreateTrip;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.oose.routemaker.Concrete.Event;
import com.example.oose.routemaker.R;

import java.text.DecimalFormat;
import java.util.List;

public class EditScheduleInfoFragment extends Fragment {

    /** Text views for event information. */
    TextView siteName;
    TextView eventHours;
    TextView eventDuration;

    /** List of events */
    public List<Event> eventList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_schedule_info, container, false);
        siteName = (TextView) view.findViewById(R.id.edit_schedule_site_name);
        eventHours = (TextView) view.findViewById(R.id.edit_schedule_event_hour);
        eventDuration = (TextView) view.findViewById(R.id.edit_schedule_event_duration);
        eventList = EditScheduleActivity.currEventList;

        return view;
    }

    /**
     * Change event data to the selected / modified event.
     * @param index the index of event in the event list
     */
    public void changeEventData(int index) {
        Event event = eventList.get(index);

        siteName.setText(event.getSiteName());
        DecimalFormat dfHour = new DecimalFormat("##");
        DecimalFormat dfMinute = new DecimalFormat("00");
        int start = event.getStartTime();
        int end = event.getEndTime();
        int startHour = start / 100;
        String startHourString = dfHour.format(startHour);
        int startMinute = start % 100;
        String startMinuteString = dfMinute.format(startMinute);
        int endHour = end / 100;
        String endHourString = dfHour.format(endHour);
        int endMinute = end % 100;
        String endMinuteString = dfMinute.format(endMinute);
        eventHours.setText(startHourString + ":" + startMinuteString + " - " + endHourString + ":" + endMinuteString);
        int time = (int) Math.round(event.getDuration() * 60);
        int hour = time / 60;
        String hourString = dfHour.format(hour);
        int minute = time % 60;
        String minuteString = dfMinute.format(minute);
        eventDuration.setText("duration: " + hourString + " hours " + minuteString + " minutes");
    }
}
