package com.example.rhymedetectorbackend;

public class UserInput {
    private String lyrics;

    public UserInput(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
}
