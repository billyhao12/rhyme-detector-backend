package com.example.rhymedetectorbackend;

import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.ArrayList;

@RestController
public class Controller {

    // Finds the index of a word in a line based on a syllable index
    private int wordIndex(PLine pl, int sylIndex) {
        int ret = -1;
        int sylLoc = -1;
        while (sylLoc < sylIndex) {
            ret++;
            sylLoc += pl.get(ret).numSyls();
        }

        return ret;
    }

    public record StyledWord(String word, ArrayList<String> style) {}

    @PostMapping("/rhymes/multisyllable")
    public ArrayList<StyledWord>[] highlightMultisyllableRhymes(@RequestBody LyricsData userInput) throws Exception {
        String STATS_FILE = "iterationStatsUF.txt";
        Stats st = new Stats(STATS_FILE);
        Scoring sc = new Scoring(st, Stats.SPLIT);
        Detector det = new Detector(sc);
        Transcriptor tr = new Transcriptor();

        String[] plainLines = userInput.getLyrics().split("\n");

        ArrayList<PLine> inLines = new ArrayList<PLine>();
        for (int i = 0; i < plainLines.length; i++) {
            inLines.add(tr.transcribe(plainLines[i]));
        }
        RhymeCollection rc = det.getRhymes(inLines);
        rc.lines = inLines;
        if (inLines.isEmpty()) {
            throw new BadRequestException("No lines in input text.");
        }

        // Initialize data structure to send as a response
        ArrayList<StyledWord>[] styledLyrics;
        styledLyrics = new ArrayList[plainLines.length];
        for (int i = 0; i < styledLyrics.length; i++) {
            styledLyrics[i] = new ArrayList<>();
            String[] curLine = plainLines[i].split(" ");

            for (int j = 0; j < curLine.length; j++) {
                styledLyrics[i].add(new StyledWord(curLine[j], new ArrayList<>()));
            }
        }

        StringBuilder textOutput = new StringBuilder();
        int styleMod = 0;
        String[] styles = { "bold", "italic", "red", "underline", "strikethrough" };

        // Loop through the lines in the rhyme collection
        for (int i = 0; i < rc.lines.size(); i++) {
            String[] curLine = plainLines[i].split(" ");
            StringBuilder curLineOutput = new StringBuilder();

            ArrayList<Rhyme> curLineRhymes = rc.collection[i];

            // Track positions where tags are inserted
            boolean[] openTag = new boolean[curLine.length];

            // Loop through rhyme phrase pairs
            for (int j = 0; j < curLineRhymes.size(); j++) {
                Rhyme r = curLineRhymes.get(j);

                // First and last word of phrase A
                int firstWord = wordIndex(rc.lines.get(i), r.aStart.syllable);
                int lastWord = wordIndex(rc.lines.get(i), r.aEnd().syllable);

                for (int wordIndex = firstWord; wordIndex <= lastWord; wordIndex++) {
                    styledLyrics[i].get(wordIndex).style.add(styles[styleMod]);
                }

                // Check if rhyme occurs on the same line
                if (r.aStart.sameLine(r.bStart)) {
                    firstWord = wordIndex(rc.lines.get(i), r.bStart.syllable);
                    lastWord = wordIndex(rc.lines.get(i), r.bEnd().syllable);

                    for (int wordIndex = firstWord; wordIndex <= lastWord; wordIndex++) {
                        styledLyrics[i].get(wordIndex).style.add(styles[styleMod]);
                    }
                } else {
                    // Handle rhymes across different lines
                    firstWord = wordIndex(rc.lines.get(i + 1), r.bStart.syllable);
                    lastWord = wordIndex(rc.lines.get(i + 1), r.bEnd().syllable);

                    for (int wordIndex = firstWord; wordIndex <= lastWord; wordIndex++) {
                        styledLyrics[i + 1].get(wordIndex).style.add(styles[styleMod]);
                    }
                }

                styleMod =   (styleMod + 1) % styles.length; // Rotate through the styles
            }
        }

        return styledLyrics;
    }
}
