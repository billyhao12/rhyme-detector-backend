/**
 * MultisyllableRhymeData is the response data returned from the "/rhymes/multisyllable" endpoint.
 * The "lyrics" key here consists of an array of ArrayLists that defines style information for each word.
 */

package com.example.rhymedetectorbackend;

import java.util.ArrayList;

public class MultisyllableRhymeData {
    ArrayList<StyledWord>[] lyrics;

    public MultisyllableRhymeData(ArrayList<StyledWord>[] lyrics) {
        this.lyrics = lyrics;
    }

    public void setLyrics(ArrayList<StyledWord>[] lyrics) {
        this.lyrics = lyrics;
    }

    public ArrayList<StyledWord>[] getLyrics() {
        return lyrics;
    }
}
