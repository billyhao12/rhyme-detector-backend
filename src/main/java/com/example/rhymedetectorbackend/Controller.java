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

        ArrayList curLine = new ArrayList();

        // size of ArrayList is number of words in first PLine
        ArrayList nextLine = new ArrayList(rc.lines.get(0).size());
        String[] firstPlainLine = plainLines[0].split(" ");
        for (int i = 0; i < firstPlainLine.length; i++) {
            nextLine.add(firstPlainLine[i]);
        }

        String[] styles = {
                "<b>", "</b>",    // Bold
                "<i>", "</i>",    // Italic
                "<span style=\"color: red\">", "</span>",  // Red color
                "<u>", "</u>",    // Underline
                "<s>", "</s>"     // Strikethrough
        };

        int curLineAddedTags = 0;
        int nextLineAddedTags = 0;

        // Loop through the lines in the rhyme collection
        for (int i = 0; i < rc.lines.size(); i++) {
            curLine = nextLine;

            curLineAddedTags = nextLineAddedTags;
            nextLineAddedTags = 0;

            // If we are not on the last line, prepare ArrayList for next line
            if (rc.lines.size() > i + 1) {
                nextLine = new ArrayList(rc.lines.get(i + 1).size());
                String[] plainLine = plainLines[i + 1].split(" ");
                for (int j = 0; j < plainLine.length; j++) {
                    nextLine.add(plainLine[j]);
                }
            }

            ArrayList<Rhyme> curLineRhymes = rc.collection[i];

            // Loop through rhyme phrase pairs
            for (Rhyme r : curLineRhymes) {
                // First and last word of phrase A
                int firstWord = wordIndex(rc.lines.get(i), r.aStart.syllable);
                int lastWord = wordIndex(rc.lines.get(i), r.aEnd().syllable);

                curLine.add(firstWord + curLineAddedTags, styles[styleMod * 2]);
                curLineAddedTags += 1;

                curLine.add(lastWord + 1 + curLineAddedTags, styles[styleMod * 2 + 1]);
                curLineAddedTags += 1;

                System.out.println("curLine size: " + curLine.size() + ", firstWord: " + firstWord + ", lastWord: " + lastWord);

                // Check if rhyme occurs on the same line
                if (r.aStart.sameLine(r.bStart)) {
                    firstWord = wordIndex(rc.lines.get(i), r.bStart.syllable);
                    lastWord = wordIndex(rc.lines.get(i), r.bEnd().syllable);

                    curLine.add(firstWord + curLineAddedTags, styles[styleMod * 2]);
                    curLineAddedTags += 1;

                    curLine.add(lastWord + 1 + curLineAddedTags, styles[styleMod * 2 + 1]);
                    curLineAddedTags += 1;

                    System.out.println("curLine size: " + curLine.size() + ", firstWord: " + firstWord + ", lastWord: " + lastWord);
                } else {
                    firstWord = wordIndex(rc.lines.get(i + 1), r.bStart.syllable);
                    lastWord = wordIndex(rc.lines.get(i + 1), r.bEnd().syllable);

                    nextLine.add(firstWord + nextLineAddedTags, styles[styleMod * 2]);
                    nextLineAddedTags += 1;

                    nextLine.add(lastWord + 1 + nextLineAddedTags, styles[styleMod * 2 + 1]);
                    nextLineAddedTags += 1;

                    System.out.println("nextLine size: " + nextLine.size() + ", firstWord: " + firstWord + ", lastWord: " + lastWord);
                }

                styleMod = (styleMod + 1) % (styles.length / 2);  // Rotate through the styles
            }

            textOutput.append(String.join(" ", curLine));
            textOutput.append("\n");
        }

        return textOutput.toString();
    }
}
