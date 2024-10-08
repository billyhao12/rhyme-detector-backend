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

    @PostMapping("/rhymes/multisyllable")
    public String highlightMultisyllableRhymes(@RequestBody LyricsData userInput) throws Exception {
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

        StringBuilder textOutput = new StringBuilder();
        int styleMod = 0;

        String[] styles = {
                "<b>", "</b>",    // Bold
                "<i>", "</i>",    // Italic
                "<span style=\"color: red\">", "</span>",  // Red color
                "<u>", "</u>",    // Underline
                "<s>", "</s>"     // Strikethrough
        };

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

                // Insert opening tag
                if (!openTag[firstWord]) {
                    curLine[firstWord] = styles[styleMod * 2] + curLine[firstWord];
                    openTag[firstWord] = true;  // Mark that we've inserted an opening tag
                }

                // Insert closing tag
                curLine[lastWord] += styles[styleMod * 2 + 1];

                // Check if rhyme occurs on the same line
                if (r.aStart.sameLine(r.bStart)) {
                    firstWord = wordIndex(rc.lines.get(i), r.bStart.syllable);
                    lastWord = wordIndex(rc.lines.get(i), r.bEnd().syllable);

                    if (!openTag[firstWord]) {
                        curLine[firstWord] = styles[styleMod * 2] + curLine[firstWord];
                        openTag[firstWord] = true;
                    }

                    curLine[lastWord] += styles[styleMod * 2 + 1];
                } else {
                    // Handle rhymes across different lines
                    String[] nextLine = plainLines[i + 1].split(" ");
                    firstWord = wordIndex(rc.lines.get(i + 1), r.bStart.syllable);
                    lastWord = wordIndex(rc.lines.get(i + 1), r.bEnd().syllable);

                    nextLine[firstWord] = styles[styleMod * 2] + nextLine[firstWord];
                    nextLine[lastWord] += styles[styleMod * 2 + 1];

                    // Replace the next line in the plainLines array with updated version
                    plainLines[i + 1] = String.join(" ", nextLine);
                }

                styleMod = (styleMod + 1) % (styles.length / 2);  // Rotate through the styles
            }

            // Append the current line with all the styles applied
            textOutput.append(String.join(" ", curLine));
            textOutput.append("\n");
        }

        return textOutput.toString();
    }
}
