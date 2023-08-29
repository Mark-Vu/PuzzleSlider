package org.example.model;

public class User {
    private String name;
    private String country;
    private int ranking;
    
    public User(String name, String country, int ranking) {
        this.name = name;
        this.country = country;
        this.ranking = ranking;
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

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
}