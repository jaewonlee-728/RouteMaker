package com.example.oose.routemaker.Concrete;

import java.io.Serializable;

/**
 * City object that contains information about a city in our app.
 */
public class City implements Serializable {

    /** Information about a city. */
    private String cityCode;
    private String cityName;
    private String state;
    private double latitude; //default place to place the camera on in the Google Maps
    private double longitude; //default place to place the camera on in the Google Maps

    /**
     * Default empty constructor.
     */
    public City() {}

    /** Getters. */
    public String getCityCode() {
        return this.cityCode;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String getCityName() {
        return this.cityName;
    }

    public String getState() {
        return this.state;
    }
}