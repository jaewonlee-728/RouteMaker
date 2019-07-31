package com.example.oose.routemaker.Concrete;

import java.io.Serializable;

/**
 * Event class contains information about one particular calendar event.
 */
public class Event implements Serializable {

    /** The id of the day that owns this event. */
    private String dayId;

    /** Unique id of this event. */
    private String eventId;

    /** Start time of this event. */
    private Integer startTime;

    /** End time of this event. */
    private Integer endTime;

    /** The site id of the site this event takes place at. */
    private String siteId;

    /** The name of the site. */
    private String siteName;

    /** Latitude of the site. */
    private double latitude;

    /** Longitude of the site. */
    private double longitude;

    /** Duration of this event. */
    private double duration;

    /**
     * Empty default constructor.
     */
    public Event() { }

    public Event(String eventId, Integer startTime, Integer endTime, String siteId,
                 String siteName, double latitude, double longitude) {
        this.eventId = eventId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.siteId = siteId;
        this.siteName = siteName;
        this.latitude = latitude;
        this.longitude = longitude;

        setDuration();
    }

    /** Getters. */
    public String getDayId() {
        return this.dayId;
    }
    
    public String getEventId() {
        return this.eventId;
    }
    
    public String getSiteName() {
        return this.siteName;
    }
    
    public Integer getStartTime() {
        return this.startTime;
    }
    
    public Integer getEndTime() {
        return this.endTime;
    }
    
    public String getSiteId() {
        return this.siteId;
    }
    
    public double getDuration() {
        return this.duration;
    }
    
    public double getLatitude() {
        return this.latitude;
    }
    
    public double getLongitude() {
        return this.longitude;
    }

    /** Setters. */
    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public void setModifiedDuration(double duration) {
        this.duration = duration;
    }

    public void setDuration() {
        // check if endTime >= startTime, else print error
        int startHour = startTime / 100;
        int startMinute = (startHour * 60) + (startTime % 100);

        int endHour = endTime / 100;
        int endMinute = (endHour * 60) + (endTime % 100);

        this.duration = (endMinute - startMinute) / 60.0;
    }

}
