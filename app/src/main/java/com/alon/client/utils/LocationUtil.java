package com.alon.client.utils;

public class LocationUtil {

    private double lat, lng;

    // Constructors.
    public LocationUtil(){

    }

    public LocationUtil(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    // Getters and setters.
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
