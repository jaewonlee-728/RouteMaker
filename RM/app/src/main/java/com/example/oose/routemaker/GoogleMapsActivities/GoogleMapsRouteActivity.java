package com.example.oose.routemaker.GoogleMapsActivities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.oose.routemaker.Concrete.Event;
import com.example.oose.routemaker.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Class used when generating routes between multiple points on the map.
 */
public class GoogleMapsRouteActivity extends AppCompatActivity implements OnMapReadyCallback, Serializable {

    /** The Google Map to be used in this class. */
    private GoogleMap map;

    /** Points on the map we want to show the route for. */
    ArrayList<LatLng> markerPoints;

    /** The travel mode that the user selected: drive, walk, bike. */
    private String travelMode;

    /** The eventlist we want to show the route for. */
    private ArrayList<Event> currEventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_route);
        markerPoints = new ArrayList<>();
        travelMode = "";
        currEventList = null;

        //Get intent extra stuff from previous activity
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                travelMode = null;
                currEventList = null;
            } else {
                currEventList = (ArrayList) extras.getSerializable("currEventList");
                travelMode = extras.getString("travelMode");
            }
        } else {
            currEventList = (ArrayList) savedInstanceState.getSerializable("currEventList");
            travelMode = (String) savedInstanceState.getSerializable("travelMode");
        }

        //This activity is a popup--we set the metrics for the popup window here.
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.78), (int) (height * 0.78));

        //Get the map fragment.
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                                            .findFragmentById(R.id.map_route);
        mapFragment.getMapAsync(this);

    }

    /**
     * On map ready, show the map.
     * @param googleMap the Google Map being used for this activity.
     */
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //Set the default max and min long/lat.
        //These numbers are used so that we can shoot the camera
        //at the appropriate location on the map.
        //(At the mid point of the farthest stretching points)
        double maxLat = -90;
        double minLat = 90;
        double maxLong = -180;
        double minLong = 180;

        //This same code snippet from here to the end of this method will get reused for showing
        //map through out this activity.
        for (int i = 0; i < currEventList.size(); i++) {
            //Set the markers on the map.
            Event currEvent = currEventList.get(i);
            LatLng markerPos = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
            markerPoints.add(markerPos);

            //Update min and max of longitude and latitude.
            if (currEvent.getLatitude() < minLat) {
                minLat = currEvent.getLatitude();
            }
            if (currEvent.getLatitude() > maxLat) {
                maxLat = currEvent.getLatitude();
            }
            if (currEvent.getLongitude() < minLong) {
                minLong = currEvent.getLongitude();
            }
            if (currEvent.getLongitude() > maxLong) {
                maxLong = currEvent.getLongitude();
            }

            //Get the time information for each of the sites.
            DecimalFormat dfHour = new DecimalFormat("##");
            DecimalFormat dfMinute = new DecimalFormat("00");
            int start = currEvent.getStartTime();
            int end = currEvent.getEndTime();
            int startHour = start / 100;
            String startHourString = dfHour.format(startHour);
            int startMinute = start % 100;
            String startMinuteString = dfMinute.format(startMinute);
            int endHour = end / 100;
            String endHourString = dfHour.format(endHour);
            int endMinute = end % 100;
            String endMinuteString = dfMinute.format(endMinute);

            float markerColor = 0;

            //For each travel mode, the color scheme we use to show the route is different.
            switch (travelMode) {
                case "walking":
                    markerColor = BitmapDescriptorFactory.HUE_AZURE;
                    break;
                case "driving":
                    markerColor = BitmapDescriptorFactory.HUE_GREEN;
                    break;
                case "bicycling":
                    markerColor = BitmapDescriptorFactory.HUE_ROSE;
                    break;
            }

            //Add a marker on the map with open and close information.
            Marker marker = map.addMarker(new MarkerOptions().position(markerPos)
                                                             .icon(BitmapDescriptorFactory
                                                                    .defaultMarker(markerColor))
                                                             .alpha(0.8f).title((i + 1)
                            + ". " + currEvent.getSiteName()).snippet(startHourString
                            + ":" + startMinuteString
                            + " - " + endHourString + ":" + endMinuteString));
            marker.showInfoWindow();
        }

        //Move the map camera location to the midpoint of all locations.
        LatLng center = new LatLng((maxLat + minLat) / 2, (maxLong + minLong) / 2);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 12));

        //Make the url to send to the Google Maps API.
        String url = getDirectionsUrl(markerPoints);

        DownloadTask downloadTask = new DownloadTask();

        //Start downloading json data from Google Directions API
        downloadTask.execute(url);
        //Route will be shown.
    }

    /**
     * Returns the required url to send to Google to get the string
     * required to draw lines on the map to show the route.
     * @param markerPoints list of points on the map.
     * @return url to be sent to Google.
     */
    private String getDirectionsUrl(ArrayList<LatLng> markerPoints){
        // Origin of our route.
        String API_KEY = "AIzaSyDMmULaIRet4_QC3ky3p1OIhI70YjAjwCw";
        String str_origin = "origin=" + markerPoints.get(0).latitude + "," + markerPoints.get(0).longitude;

        // Destination of route: same as origin-->daily route goes in a circle
        String str_dest = "destination=" + markerPoints.get(markerPoints.size() - 1).latitude
                            + "," + markerPoints.get(markerPoints.size() - 1).longitude;
        String str_way_points = "";
        int wayPointCount = 0;
        for (int i = 1; i < markerPoints.size(); i++) {
            if (wayPointCount == 0) {
                str_way_points += (markerPoints.get(i).latitude + "," + markerPoints.get(i).longitude);
                wayPointCount++;
            } else {
                str_way_points += ("|" + markerPoints.get(i).latitude + "," + markerPoints.get(i).longitude);
            }
        }

        //The parameters to be attached to our http request to the Google Maps API.
        //optimized route from Google Maps-->we use our own TSP
//        String parameters = str_origin + "&" + str_dest + "&waypoints=optimize:true" + str_way_points
//          + "&mode=" + travelMode + "&key=" + API_KEY;
        String parameters = str_origin + "&" + str_dest + "&waypoints=" + str_way_points + "&mode=" + travelMode + "&key=" + API_KEY;

        //Output format will be json.
        String output = "json";

        //Return the url to the web service.
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

    }

    /**
     * Code adapted from http://wptrafficanalyzer.in/blog/drawing-driving-route
     *                     -directions-between-two-locations-using-google
     *                     -directions-in-google-map-android-api-v2/
     * A method to download json data from url.
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Excpt while dwnldng url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * Code adapted from http://wptrafficanalyzer.in/blog/drawing-driving-route
     *                     -directions-between-two-locations-using-google
     *                     -directions-in-google-map-android-api-v2
     * Fetches data from url passed.
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * Code adapted from http://wptrafficanalyzer.in/blog/drawing-driving-route
     *                     -directions-between-two-locations-using-google
     *                     -directions-in-google-map-android-api-v2
     * A class to parse the Google Places in JSON format.
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            List<List<HashMap<String, String>>> routes = null;
            JSONObject jObject;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }

            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++){
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size();j++){
                    HashMap<String,String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(7);

                //For each travel mode, the color scheme we use is different.
                switch (travelMode) {
                    case "walking":
                        lineOptions.color(Color.rgb(34, 140, 232)); //baby blue

                        break;
                    case "driving":
                        lineOptions.color(Color.rgb(17, 145, 61)); // dark green

                        break;
                    case "bicycling":
                        lineOptions.color(Color.rgb(145, 39, 35)); //crimson

                        break;
                    default:
                        lineOptions.color(Color.RED);
                        break;
                }

            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);

        } //end onPostExceute

    }

}