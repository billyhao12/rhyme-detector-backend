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
