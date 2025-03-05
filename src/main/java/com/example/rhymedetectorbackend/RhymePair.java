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
    ArrayList<Integer> line;

    public RhymePair() {
        this.elementA = "";
        this.elementB = "";
        this.style = "";
        this.line = new ArrayList<>();
    }

    public RhymePair(String elementA, String style) {
        this.elementA = elementA;
        this.elementB = "";
        this.style = style;
        this.line = new ArrayList<>();
    }

    public RhymePair(String elementA, String elementB, String style, ArrayList<Integer> line) {
        this.elementA = elementA;
        this.elementB = elementB;
        this.style = style;
        this.line = line;
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

    public ArrayList<Integer> getLine() {
        return line;
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
        this.line.add(lineNumber);
    }
}
