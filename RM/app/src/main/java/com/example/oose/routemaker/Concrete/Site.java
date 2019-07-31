package com.example.oose.routemaker.Concrete;

import java.io.Serializable;

/**
 * Class that represents a single tourist site.
 * Contains all the information pertaining to a site.
 */
public class Site implements Serializable {

    /** Information about a site. */
    private String category;
    private double duration;
    private String phoneNumber;
    private String siteId;
    private String siteName;
    private double latitude;
    private double longitude;
    private int monOpen;
    private int monClose;
    private int tueOpen;
    private int tueClose;
    private int wedOpen;
    private int wedClose;
    private int thuOpen;
    private int thuClose;
    private int friOpen;
    private int friClose;
    private int satOpen;
    private int satClose;
    private int sunOpen;
    private int sunClose;
    private String cityCode;

    /** Open and close hours during the whole week--different for each day of the week. */
    public int[] openHours;
    public int[] closeHours;

    /** Default empty constructor for a site. */
    public Site() {}

    public Site(String siteName) {
        this.siteName = siteName;
    }

    public Site(String siteId, String siteName, double latitude, double longitude, String phoneNumber,
                String category, double duration, int monOpen, int monClose, int tueOpen, int tueClose,
                int wedOpen, int wedClose, int thuOpen, int thuClose, int friOpen, int friClose, int satOpen, int satClose,
                int sunOpen, int sunClose) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
        this.category = category;
        this.duration = duration;
        this.monClose = monClose;
        this.monOpen = monOpen;
        this.tueOpen = tueOpen;
        this.tueClose = tueClose;
        this.wedOpen = wedOpen;
        this.wedClose = wedClose;
        this.thuOpen = thuOpen;
        this.thuClose = thuClose;
        this.friOpen = friOpen;
        this.friClose = friClose;
        this.satOpen = satOpen;
        this.satClose = satClose;
        this.sunOpen = sunOpen;
        this.sunClose = sunClose;
    }

    /** Sets the open and close hours arrays. */
    public void initializeHours() {
        this.openHours = new int[8];
        this.closeHours = new int[8];

        this.openHours[0] = -1;
        this.openHours[1] = this.sunOpen;
        this.openHours[2] = this.monOpen;
        this.openHours[3] = this.tueOpen;
        this.openHours[4] = this.wedOpen;
        this.openHours[5] = this.thuOpen;
        this.openHours[6] = this.friOpen;
        this.openHours[7] = this.satOpen;

        this.closeHours[0] = -1;
        this.closeHours[1] = this.sunClose;
        this.closeHours[2] = this.monClose;
        this.closeHours[3] = this.tueClose;
        this.closeHours[4] = this.wedClose;
        this.closeHours[5] = this.thuClose;
        this.closeHours[6] = this.friClose;
        this.closeHours[7] = this.satClose;
    }

    /** Getters. */
    public double getDuration() {
        return duration;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }


    public String getSiteId() {
        return siteId;
    }


    public String getSiteName() {
        return siteName;
    }


    public double getLatitude() {
        return latitude;
    }


    public double getLongitude() {
        return longitude;
    }

    /**
     * Overridden equals method.
     * @param o object to be compared.
     * @return true if equal, false if else.
     */
    @Override
    public boolean equals(Object o) {
        Site s = (Site) o;
        return this.siteId.equals(s.siteId);
    }

}
