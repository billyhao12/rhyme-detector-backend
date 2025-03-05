package com.example.rhymedetectorbackend;

import java.util.ArrayList;

public class RhymePair {
    String elementA;
    String elementB;
    ArrayList<Integer> line;

    public RhymePair(String elementA, String elementB, ArrayList<Integer> line) {
        this.elementA = elementA;
        this.elementB = elementB;
        this.line = line;
    }
}
