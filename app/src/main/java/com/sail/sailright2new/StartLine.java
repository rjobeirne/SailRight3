package com.sail.sailright2new;

import android.graphics.Point;
import android.location.Location;
import android.util.Log;

public class StartLine {

    public StartLine (Location a, Location h, Location first) {
        // constructor with 'A' Mark, and 'H' mark location details, and first currentLocation
        markA = a;
        markH = h;
        firstMark = first;
        latA = markA.getLatitude();
        lonA = markA.getLongitude();
        latFirstMark = firstMark.getLatitude();
    }


    // Initialise global object variables
    String startTarget = null;
    Location markA = null;
    Location markH = null;
    Location firstMark;
    double latA = 0;
    double lonA = 0;
    double latFirstMark;
    float lineBearing = 0;
    double slopeLine = 0;
    double constLine = 0;

    // StartLine Location details
    Location mCurrentLocation = null;
    double latBoat = 0;
    double lonBoat = 0;
    float boatHeading, bearingToA, bearingToH;
    float displayBoatHeading, displayBearingToA, displayBearingToH;
    double slopeBoat = 0;
    float slopeBoatAngle, slopeLineAngle;
    double constBoat = 0;
    int directionFactor;


    // This will set the current boat Location details
    private void setBoatDetails(Location currentLocation) {
        // Define boat heading as linear equation lat = slope * lon + constant
        mCurrentLocation = currentLocation;
        latBoat = mCurrentLocation.getLatitude();
        lonBoat = mCurrentLocation.getLongitude();

        boatHeading = mCurrentLocation.getBearing();

        // Convert boat heading to angle from latitude (not north)
        if ( boatHeading > 0) {
            slopeBoatAngle = 90 - boatHeading;
        } else {
            slopeBoatAngle = 90 + boatHeading;
        }

        slopeBoat = Math.tan(Math.toRadians(slopeBoatAngle));
        constBoat = latBoat - (slopeBoat * lonBoat);

        // Convert bearings to compass bearings
                if (boatHeading < 0) {
                    displayBoatHeading = boatHeading + 360;
                } else {
                    displayBoatHeading = boatHeading;
                }
        bearingToA = mCurrentLocation.bearingTo(markA);
                if ( bearingToA < 0) {
                    displayBearingToA = bearingToA + 360;
                } else {
                    displayBearingToA = bearingToA;
                }
        bearingToH = mCurrentLocation.bearingTo(markH);
                if ( bearingToH < 0) {
                    displayBearingToH = bearingToH + 360;
                } else {
                    displayBearingToH = bearingToH;
                }


        // Define finish line as linear equation lat = slope * lon + constant
        lineBearing = markA.bearingTo(markH);

        // Convert line bearing to angle from latitude (not north)
        if ( lineBearing > 0) {
            slopeLineAngle = 90 - lineBearing;
        } else {
            slopeLineAngle = 90 + lineBearing;
        }

        slopeLine = Math.tan(Math.toRadians(slopeLineAngle));
        constLine = latA - (slopeLine * lonA);
    }

    public int getStartDirection() {
        if (latFirstMark < latA) {
            // Approaching finish from the north
            directionFactor = 1;
        } else {
            // Approach from the south
            directionFactor = -1;
        }
        return  directionFactor;
    }
    /**
     *
     * @return
     * The name of the finish point
     */
    public String getStartTarget(Location currentLocation) {
        // Update current Location of the boat, passed in from Main
        mCurrentLocation = currentLocation;
        setBoatDetails(mCurrentLocation);  // Update current boat location details

        if (directionFactor == 1) {
            // Approaching from the north
            if (displayBoatHeading > displayBearingToA) {
                startTarget = "A";
            } else if (displayBoatHeading < displayBearingToH) {
                startTarget = "H";
            } else {
                startTarget = "Line";
            }

        } else {
            // Approaching from the south
            // First correct heading >180 to be negative to avoid problem at due north
            if (boatHeading > 180) {
                boatHeading = boatHeading - 360;
            }
            if (bearingToA > 180) {
                bearingToA = bearingToA - 360;
            }
            if (bearingToH > 180) {
                bearingToH = bearingToH - 360;
            }

            if (boatHeading < bearingToA) {
                startTarget = "A";
            } else if (boatHeading > bearingToH) {
                startTarget = "H";
            } else {
                startTarget = "Line";
            }

        }
        return  startTarget;
    }

    /**
     *
     * @return
     * The finish point on the line
     */
    public Location getStartPoint(Location currentLocation) {

        Location startPoint = new Location("");
        mCurrentLocation = currentLocation;
        setBoatDetails(mCurrentLocation);  // Update the current Location of the boat

        double finLon = (constLine - constBoat) / (slopeBoat - slopeLine);
        double finLat = slopeLine * finLon +constLine;
        startPoint.setLongitude(finLon);
        startPoint.setLatitude(finLat);

        return startPoint;
    }

    public double getApproachAngle() {

        double approachAngle = slopeLineAngle - slopeBoatAngle;
        return approachAngle;
    }

}