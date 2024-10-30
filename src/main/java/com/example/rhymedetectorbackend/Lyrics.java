/**
 * This is a basic Lyrics class that contains one "lyrics" key
 * that consists of a String.
 */

package com.example.rhymedetectorbackend;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Lyrics {
    private String lyrics;

    @JsonCreator
    public Lyrics(@JsonProperty("lyrics") String lyrics) {
        this.lyrics = lyrics;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
}
