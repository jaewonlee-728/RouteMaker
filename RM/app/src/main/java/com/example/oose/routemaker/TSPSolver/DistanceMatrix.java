package com.example.oose.routemaker.TSPSolver;

import com.example.oose.routemaker.Concrete.Site;

import java.util.ArrayList;

public class DistanceMatrix {

    private double[][] distanceMatrix;
    private String[] nameOfSites;
    private double[] lat;
    private double[] lng;

    /**
     * Constructor
     * @param sites
     */
    public DistanceMatrix(ArrayList<Site> sites) {

        this.distanceMatrix = new double[sites.size()][sites.size()];

        nameOfSites = new String[sites.size()];
        lat = new double[sites.size()];
        lng = new double[sites.size()];

        int i = 0;
        for(Site s: sites) {
            nameOfSites[i] = s.getSiteName();
            lat[i] = s.getLatitude();
            lng[i] = s.getLongitude();
            i++;
        }

        /**
         * set the entry of the distance matrix
         */
        for(int j = 0; j < nameOfSites.length; j++) {
            for(int k = 0; k < nameOfSites.length; k++) {
                distanceMatrix[j][k] = Math.sqrt(Math.pow(lat[j] - lat[k], 2) + Math.pow(lng[j] - lng[k], 2));
            }
        }
    }

    /**
     * Matrix that contains all pairs shortest path.
     * @return the matrix that contains all the distances between the cities
     */
    public double[][] getDistanceMatrix() {
        return distanceMatrix;
    }


}