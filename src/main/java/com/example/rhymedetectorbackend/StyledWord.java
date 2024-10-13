package com.example.rhymedetectorbackend;

import java.util.ArrayList;

public class StyledWord {
    String word;
    ArrayList<String> style;

    public StyledWord(String word, ArrayList<String> style) {
        this.word = word;
        this.style = style;
    }

    public String getWord() {
        return word;
    }

    public ArrayList<String> getStyle() {
        return style;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setStyle(ArrayList<String> style) {
        this.style = style;
    }
}
