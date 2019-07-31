package com.example.oose.routemaker.Concrete;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * Class that represents a user for our app.
 */
public class User implements Serializable {

    /** Basic information about a user. */
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String ageGroup;

    private String currentlyEditingTripId;
    private List<String> preferenceList;
    private List<String> currentTripIds;
    private List<String> pastTripIds;

    public User(String email, String firstName, String lastName, String password, String ageGroup, List<String> preferenceList) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.ageGroup = ageGroup;
        this.preferenceList = preferenceList;
        this.currentTripIds = new ArrayList<>();
        this.pastTripIds = new ArrayList<>();
    }

    /** Getters and setters. */
    public String getEmail() {
        return this.email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getPassword() {
        return this.password;
    }

    public String getAgeGroup() {
        return this.ageGroup;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getPreferenceList() {
        return this.preferenceList;
    }

    public void addTrip(String tripId) {
        this.currentTripIds.add(tripId);
    }

    public void setCurrentlyEditingTripId(String currentlyEditingTripId) {
        this.currentlyEditingTripId = currentlyEditingTripId;
    }

    public void resetCurrentlyEditingTripId(){
        this.currentlyEditingTripId = "";
    }

}
