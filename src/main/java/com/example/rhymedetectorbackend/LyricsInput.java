package com.example.rhymedetectorbackend;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LyricsInput {
    private String lyrics;

    @JsonCreator
    public LyricsInput(@JsonProperty("lyrics") String lyrics) {
        this.lyrics = lyrics;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
}
