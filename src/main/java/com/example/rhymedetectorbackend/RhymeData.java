/**
 * RhymeData is the response returned from both API endpoints.
 * The "lyrics" property consists of an array of ArrayLists that defines style information for each word.
 * The "rhymePairs" property specifies the literal rhyme pairs, their styling, and the line(s) they're located at.
 */

package com.example.rhymedetectorbackend;

import java.util.ArrayList;

public class RhymeData {
    ArrayList<StyledWord>[] lyrics;
    ArrayList<RhymePair> rhymePairs;

    public RhymeData(ArrayList<StyledWord>[] lyrics) {
        this.lyrics = lyrics;
    }

    public RhymeData(ArrayList<StyledWord>[] lyrics, ArrayList<RhymePair> rhymePairs) {
        this.lyrics = lyrics;
        this.rhymePairs = rhymePairs;
    }

    public void setLyrics(ArrayList<StyledWord>[] lyrics) {
        this.lyrics = lyrics;
    }

    public ArrayList<StyledWord>[] getLyrics() {
        return lyrics;
    }

    public ArrayList<RhymePair> getRhymePairs() {
        return rhymePairs;
    }
}
