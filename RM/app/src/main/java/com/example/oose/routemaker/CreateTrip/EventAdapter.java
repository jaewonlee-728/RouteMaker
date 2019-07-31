package com.example.oose.routemaker.CreateTrip;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.oose.routemaker.Concrete.Event;
import com.example.oose.routemaker.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Adapter for event list.
 */
public class EventAdapter extends BaseAdapter {

    /** List of events to connect to Adapter */
    private List<Event> eventList;

    /** The context in which the adapter is used */
    private final Context context;

    /**
     * Constructor.
     * @param context the context in which the adapter is used
     * @param eventList list of events to connect to adapter
     */
    public EventAdapter(Context context, List<Event> eventList) {
        this.eventList = eventList;
        this.context = context;
    }

    /** Get the number of elements in the event list */
    @Override
    public int getCount() {
        return eventList.size();
    }

    /** Get the item in the position */
    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    /** Creates view using the adapter */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.event_item, null);
            holder = new ViewHolder();
            holder.siteName = (TextView) convertView.findViewById(R.id.event_name);
            holder.siteHour = (TextView) convertView.findViewById(R.id.event_start_hour);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.siteName.setText(eventList.get(position).getSiteName());
        String siteHourString = formatHour(position);
        holder.siteHour.setText(siteHourString);
        return convertView;
    }

    /**
     * format the integer hour for readability
     * @param position the index of the event to access from the event list
     * @return the String format of the start hour and end hour
     */
    private String formatHour(int position) {
        DecimalFormat dfHour = new DecimalFormat("##");
        DecimalFormat dfMinute = new DecimalFormat("00");

        int start = eventList.get(position).getStartTime();
        int end = eventList.get(position).getEndTime();
        int startHour = start / 100;
        String startHourString = dfHour.format(startHour);
        int startMinute = start % 100;
        String startMinuteString = dfMinute.format(startMinute);
        int endHour = end / 100;
        String endHourString = dfHour.format(endHour);
        int endMinute = end % 100;
        String endMinuteString = dfMinute.format(endMinute);

        return startHourString + ":" + startMinuteString + " - " + endHourString + ":" + endMinuteString;
    }

    /** Inner class view object */
    static class ViewHolder {
        TextView siteName;
        TextView siteHour;
    }
}
