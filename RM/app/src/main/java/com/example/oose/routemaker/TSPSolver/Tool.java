package com.example.oose.routemaker.TSPSolver;

import com.example.oose.routemaker.Concrete.Site;

import java.util.ArrayList;

public class Tool {

    /**
     * compute the cost of a given path
     * @param path
     * @param distanceMatrix
     * @return
     */
    public double computeCost(int[] path, double[][] distanceMatrix) {
        double cost = 0;
        for(int i = 1; i < path.length; i++) {
            cost += distanceMatrix[path[i-1]][path[i]];
        }
        cost += distanceMatrix[path[path.length - 1]] [path[0]];
        return cost;
    }

    /**
     * @param path
     * @param srcIndex
     * @return destination city of an edge given the source index
     */
    public int getDestination(int[] path, int srcIndex) {
        if(srcIndex + 1 == path.length) {
            return path[0];
        }
        return path[srcIndex + 1];
    }

    /**
     * @param path
     * @param srcIndex
     * @return destination index of an edge given the source index
     */
    public int getIndexOfDestination(int[] path, int srcIndex) {
        if(srcIndex + 1 == path.length) {
            return 0;
        }
        return srcIndex + 1;
    }


    /**
     * Check if the city is in the path
     * @param path
     * @return true: if the city is already in the path, false otherwise
     */
    public boolean isSiteInPath(ArrayList<Site> path, Site site) {
        if (path.contains(site)) {
            return true;
        } else {
            return false;
        }
    }
}