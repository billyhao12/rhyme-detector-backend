/**
 * The StyledWord class contains the word and a style array that lists the styles applied for the word.
 * For example, the word "heavy" can have styles "bold", "underline", and "strikethrough".
 */

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
