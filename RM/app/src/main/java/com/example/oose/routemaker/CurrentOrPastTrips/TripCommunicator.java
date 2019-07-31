package com.example.oose.routemaker.CurrentOrPastTrips;

import com.example.oose.routemaker.Concrete.Trip;

import java.util.List;

/**
 * Allows communication between fragment and activity in SelectCurrentOrPastTripActivity.
 */
public interface TripCommunicator {
    void respond(int index);
    List<Trip> getTripList();
}
