package org.example.model;

import org.example.ranking.ScoreRecord;

import java.util.HashMap;

public class User {
    private String name;
    private String country;

    private HashMap<Integer, ScoreRecord> scores;
    
    public User(String name, String country, HashMap<Integer, ScoreRecord> scores) {
        this.name = name;
        this.country = country;
        this.scores = scores;
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

    public HashMap<Integer, ScoreRecord> getScores() {
        return scores;
    }

    public void setScores(HashMap<Integer, ScoreRecord> scores) {
        this.scores = scores;
    }
}