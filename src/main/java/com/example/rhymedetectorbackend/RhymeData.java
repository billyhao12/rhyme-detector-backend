/**
 * RhymeData is the response returned from both API endpoints.
 * The "lyrics" key here consists of an array of ArrayLists that defines style information for each word.
 */

package com.example.rhymedetectorbackend;

import java.util.ArrayList;

public class RhymeData {
    ArrayList<StyledWord>[] lyrics;

    public RhymeData(ArrayList<StyledWord>[] lyrics) {
        this.lyrics = lyrics;
    }

    public void setLyrics(ArrayList<StyledWord>[] lyrics) {
        this.lyrics = lyrics;
    }

    public ArrayList<StyledWord>[] getLyrics() {
        return lyrics;
    }
}
