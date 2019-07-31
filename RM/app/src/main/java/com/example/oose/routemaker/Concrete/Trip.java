package com.example.oose.routemaker.Concrete;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * Trip class contains information about one trip.
 */
public class Trip implements Serializable {

    /** Unique trip id for each trip. */
    private String tripId;

    /** City code of the city for this trip. */
    private String cityCode;

    /** Start date of this trip. */
    private Calendar startDate;

    /** End date of this trip. */
    private Calendar endDate;

    /** List of day ids for this trip. */
    private List<String> dayIds;

    /** Empty default constructor. */
    public Trip() { }

    public Trip(String tripId, String cityCode, Calendar startDate, Calendar endDate) {
        this.tripId = tripId;
        //this.dayIds = dayIds;
        this.cityCode = cityCode;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** Getters. */
    public String getTripId() {
        return this.tripId;
    }
    
    public String getCityCode() {
        return this.cityCode;
    }
    
    public Calendar getStartDate() {
        return this.startDate;
    }
    
    public Calendar getEndDate() {
        return this.endDate;
    }
    
    public List<String> getDayIds() {
        return this.dayIds;
    }

}