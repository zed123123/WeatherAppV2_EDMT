package com.example.weatherappv2_edmt.Model;

public class CityList {
    private int id;
    private String name;
    private String country;
    private Coord coord;

    public CityList() {
    }

    public CityList(int id, String name, String country, Coord coord) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.coord = coord;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }
}
