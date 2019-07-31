package com.example.oose.routemaker.CreateTrip;

/**
 * Allows communication between fragments in SelectSiteActivity
 */
public interface Communicator {
    void respond(int data);
    void respond(int data, int source);
    long getDiff();
}
