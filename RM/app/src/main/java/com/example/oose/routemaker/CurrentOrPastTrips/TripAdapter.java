package com.example.oose.routemaker.CurrentOrPastTrips;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oose.routemaker.Concrete.Trip;
import com.example.oose.routemaker.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TripAdapter extends BaseAdapter{

    /** List of trips to connect to Adapter */
    private List<Trip> tripList;

    /** The context in which the adapter is used */
    private final Context context;

    /** Lists for trip info to choose from */
    private List<Integer> imageList;
    private List<String> cityNames;
    private List<String> cityCodes;

    /**
     * Constructor.
     * @param context the context in which the adapter is used
     * @param tripList list of sites to connect to adapter
     */
    public TripAdapter(Context context, List<Trip> tripList) {
        this.tripList = tripList;
        this.context = context;
    }

    /** Get the number of elements in the trip list */
    @Override
    public int getCount() {
        return tripList.size();
    }

    /** Get the item in the position */
    @Override
    public Object getItem(int position) {
        return tripList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    /**
     * Creates view using the adapter
     * @param position the position of item in the list
     * @param convertView the old view to reuse, if not null
     * @param parent the parent that this view will be attached to
     * @return a view of the position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        setUpSites();
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.trip_item, null);
            holder = new ViewHolder();
            holder.cityImage = (ImageView) convertView.findViewById(R.id.select_trip_city_image);
            holder.cityName = (TextView) convertView.findViewById(R.id.select_trip_city_text);
            holder.tripDate = (TextView) convertView.findViewById(R.id.select_trip_date_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String cityCode = tripList.get(position).getCityCode();
        int index = cityCodes.indexOf(cityCode);
        holder.cityImage.setImageResource(imageList.get(index));
        holder.cityName.setText(cityNames.get(index));

        String tripDateString = formatDate(position);
        holder.tripDate.setText(tripDateString);
        return convertView;
    }

    /** Fill lists with trip information for comparison purpose */
    private void setUpSites() {
        imageList = new ArrayList<>();
        cityNames = new ArrayList<>();
        cityCodes = new ArrayList<>();

        imageList.add(R.drawable.newyork);
        imageList.add(R.drawable.dc);
        imageList.add(R.drawable.boston);
        imageList.add(R.drawable.baltimore);
        imageList.add(R.drawable.losangeles);
        imageList.add(R.drawable.chicago);
        imageList.add(R.drawable.seattle);
        imageList.add(R.drawable.sanfrancisco);
        imageList.add(R.drawable.miami);

        cityNames.add("New York");
        cityNames.add("Washington DC");
        cityNames.add("Boston");
        cityNames.add("Baltimore");
        cityNames.add("Los Angeles");
        cityNames.add("Chicago");
        cityNames.add("Seattle");
        cityNames.add("San Francisco");
        cityNames.add("Miami");

        cityCodes.add("NYC");
        cityCodes.add("WAS");
        cityCodes.add("BOS");
        cityCodes.add("BAL");
        cityCodes.add("LAX");
        cityCodes.add("CHI");
        cityCodes.add("SEA");
        cityCodes.add("SFO");
        cityCodes.add("MIA");
    }

    /**
     * Format calendar into string date, for readability.
     * @param position the position of the trip in the trip list
     * @return the String containing start and end date
     */
    private String formatDate(int position) {
        DateFormat tripDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar startDate = tripList.get(position).getStartDate();
        Calendar endDate = tripList.get(position).getEndDate();

        String startDateFormat = tripDateFormat.format(startDate.getTime());
        String endDateFormat = tripDateFormat.format(endDate.getTime());

        return startDateFormat + " ~ " + endDateFormat;
    }

    /** Inner class view object */
    static class ViewHolder {
        ImageView cityImage;
        TextView cityName;
        TextView tripDate;
    }
}

