package com.example.oose.routemaker.TSPSolver;

import com.example.oose.routemaker.Concrete.Site;

import java.util.ArrayList;

public class TSP {

        public ArrayList<Site> runTSP(ArrayList<Site> dayList) {
            ArrayList<Site> path;

        	// build the matrix with the distances
            double[][] distanceMatrix = new DistanceMatrix(dayList).getDistanceMatrix();

            // calculate nearest neighbor
            NearestNeighbor nearestNeighborSolution = new NearestNeighbor(distanceMatrix, dayList.get(0), dayList);
            path = nearestNeighborSolution.getPath();
            for (int i = 0; i < path.size(); i++) {
            	System.out.print(path.get(i).getSiteId() + " ");
            }
            return path;
        }

}