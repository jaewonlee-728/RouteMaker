package com.example.oose.routemaker.CreateTrip;

/**
 * Allows communication between fragments in EditScheduleActivity
 */
public interface EditCommunicator {
    void resetTransport(int index);
    long getDiff();
    void respond(int index);
}
