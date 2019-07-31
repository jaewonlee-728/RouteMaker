package com.example.oose.routemaker.Concrete;

import java.io.Serializable;
import java.util.List;

/**
 * Day class that represents a day in a user's trip.
 */
public class Day implements Serializable {

    /** Basic information about a day of a trip. */
    private String dayId;
    private List<String> eventIds;
    private Integer startTime;

    /** Default empty constructor. */
    public Day() {}

    /** Getters. */
    public String getDayId() {
        return this.dayId;
    }

    public List<String> getEventIds() {
        return this.eventIds;
    }

    public Integer getStartTime() {
        return this.startTime;
    }

}
