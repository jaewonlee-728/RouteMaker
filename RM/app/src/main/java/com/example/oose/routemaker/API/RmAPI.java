package com.example.oose.routemaker.API;

import com.example.oose.routemaker.Concrete.City;
import com.example.oose.routemaker.Concrete.Day;
import com.example.oose.routemaker.Concrete.Event;
import com.example.oose.routemaker.Concrete.Site;
import com.example.oose.routemaker.Concrete.Trip;
import com.example.oose.routemaker.Concrete.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface RmAPI {
    //API for signup; creates a user object and post it to server
    @POST("/users/sign_up")
    void createUser(@Body User user, Callback<Boolean> response);

    //API for login; sends server the email address and retrieves the user information
    @GET("/users/{email}")
    void getUser(@Path("email") String email, Callback<User> userCallback);

    @PUT("/users/setting")
    void updateUser(@Body User user, Callback<User> userCallback);

    @GET("/city/{cityCode}")
    void getCity(@Path("cityCode") String cityCode, Callback<City> cityCallback);

    @GET("/trip/{email}")
    void getTripCount(@Path("email") String email, Callback<Integer> countCallback);

    @PUT("/trip/current/{email}")
    void currentTrip(@Path("email") String email, Callback<String> currentCallback);

    @GET("/site/{temp}")
    void getSite(@Path("temp") String temp, Callback<ArrayList<Site>> siteCallback);

    @POST("/trip/{days}/{startTime}")
    void setDays(@Path("days") long diff,@Path("startTime") String temp, @Body Trip trip, Callback<Boolean> dayCallback);

    @GET("/users/name/{email}")
    void getName(@Path("email") String email, Callback<String> nameCallback);

    @GET("/trip/{tripId}/dayIds")
    void getDayIds(@Path("tripId") String tripId, Callback<ArrayList<String>> dayIdCallback);

    @POST("/trip/{days}/{startTime}")
    void putTripInfo(@Path("days") long days, @Path("startTime") String startTime, @Body Trip trip, Callback<Boolean> someCallBack);

    @POST("/event/map/{email}")
    void putEvent(@Path("email") String email, @Body Map<String, ArrayList<Event>> map, Callback<Boolean> eventCallback);

    @GET("/users/{email}/currentTrips")
    void getCurrTripList(@Path("email") String email, Callback<ArrayList<Trip>> tripListCallBack);

    @GET("/users/{email}/pastTrips")
    void getPastTripList(@Path("email") String email, Callback<ArrayList<Trip>> tripListCallBack);

    @GET("/trip/{tripId}/eventList")
    void getSelectedTrip(@Path("tripId") String tripId, Callback<Map<String, Map<String, ArrayList<Event>>>> currTripCallback);

    @PUT("/event/map")
    void updateEvent(@Body Map<String, ArrayList<Event>> map, Callback<Boolean> eventCallback);
}