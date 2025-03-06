/**
 * RhymePair defines literal string representations of rhyme pairs,
 * their styling,
 * and the line or lines the pair is located at in a set of lyrics.
 */

package com.example.rhymedetectorbackend;

import java.util.ArrayList;

public class RhymePair {
    String elementA;
    String elementB;
    String style;
    ArrayList<Integer> lines;

    public RhymePair() {
        this.elementA = "";
        this.elementB = "";
        this.style = "";
        this.lines = new ArrayList<>();
    }

    public RhymePair(String elementA, String style) {
        this.elementA = elementA;
        this.elementB = "";
        this.style = style;
        this.lines = new ArrayList<>();
    }

    public RhymePair(String elementA, String elementB, String style, ArrayList<Integer> lines) {
        this.elementA = elementA;
        this.elementB = elementB;
        this.style = style;
        this.lines = lines;
    }

    public String getElementA() {
        return elementA;
    }

    public String getElementB() {
        return elementB;
    }

    public String getStyle() {
        return style;
    }

    public ArrayList<Integer> getLines() {
        return lines;
    }

    public void setElementA(String elementA) {
        this.elementA = elementA;
    }

    public void setElementB(String elementB) {
        this.elementB = elementB;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void addALine(int lineNumber) {
        this.lines.add(lineNumber);
    }
}
