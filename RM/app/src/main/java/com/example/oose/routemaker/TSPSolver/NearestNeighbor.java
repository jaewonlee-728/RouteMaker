package com.example.oose.routemaker.TSPSolver;

import com.example.oose.routemaker.Concrete.Site;

import java.util.ArrayList;

public class NearestNeighbor {

    private ArrayList<Site> path;

    private Tool tool = new Tool();

    /**
     * Constructor that finds the path and the cost of the nearest neighbor solution
     * @param distanceMatrix
     * @param startSite
     */
    public NearestNeighbor(final double[][] distanceMatrix, final Site startSite, ArrayList<Site> dayList) {

        path = new ArrayList<>();

        path.add(startSite);

        Site currentSite = startSite;
        int index = 0;
        int listSize = dayList.size();

        /**
         * until there are cities that are not yet been visited
         */
        int i = 1;
        while (i < listSize) {
            // find next city
            int nextSiteIndex = findMin(distanceMatrix[index], dayList);
            // if the city is not -1 (meaning if there is a city to be visited
            if(nextSiteIndex != -1) {
                // add the city to the path
                path.add(dayList.get(nextSiteIndex));
                // update currentCity and i
                currentSite =  dayList.get(nextSiteIndex);
                i++;
            }
        }
    }

    /**
     * Find the nearest city that has not yet been visited
     * @param distanceMatrix
     * @return next city to visit
     */
    private int findMin(double[] distanceMatrix, ArrayList<Site> dayList) {

        int nextSiteIndex = -1;
        int i = 0;
        double min = Double.MAX_VALUE;

        while(i < distanceMatrix.length)  {
            if(tool.isSiteInPath(path, dayList.get(i)) == false && distanceMatrix[i] < min) {
                min = distanceMatrix[i];
                nextSiteIndex = i;
            }
            i++;
        }
        return nextSiteIndex;
    }

    /**
     * @return the array that contains the path
     */
    public ArrayList<Site> getPath() {
        return path;
    }
}