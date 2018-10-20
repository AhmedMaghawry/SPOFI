package com.ezzat.spofi.Model;

import java.io.Serializable;

public class Location implements Serializable {

    private String Country;
    private String City;
    private String lang;
    private String lat;

    public Location() {
    }

    public Location(String country, String city, String lang, String lat) {
        Country = country;
        City = city;
        this.lang = lang;
        this.lat = lat;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
