package com.ems.models;

public class Speaker {
    private int speakerId;
    private String name;
    private String bio;

    public Speaker() {
    }

    public Speaker(int speakerId, String name, String bio) {
        this.speakerId = speakerId;
        this.name = name;
        this.bio = bio;
    }

    public int getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(int speakerId) {
        this.speakerId = speakerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}