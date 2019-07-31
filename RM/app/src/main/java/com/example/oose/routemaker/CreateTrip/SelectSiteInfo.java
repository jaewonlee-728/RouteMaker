package com.example.oose.routemaker.CreateTrip;

import com.example.oose.routemaker.Concrete.Site;

import java.util.ArrayList;

/**
 * Information class that keeps getting updated and referenced when
 * the user tries to pick a site to add to his/her schedule.
 */
public class SelectSiteInfo {

    /**
     * Button messages for the days.
     */
    public static final String[] DAYS =
            {
                    "DAY 1",
                    "DAY 2",
                    "DAY 3",
                    "DAY 4",
                    "DAY 5",
                    "DAY 6",
                    "DAY 7"
            };

    /**
     * Button messages for the categories.
     */
    public static final String[] CATEGORIES =
            {
                    "Landmarks",
                    "Museums",
                    "Art",
                    "Entertainment",
                    "Outdoors",
                    "Shopping",
                    "NightLife"
            };

    /**
     * ArrayList of sites for each day-category pair.
     */
    public static ArrayList<Site> SITES;

}
