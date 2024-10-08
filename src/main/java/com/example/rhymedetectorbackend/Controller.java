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

        for (int i = 0; i < rc.lines.size(); i++) {
            ArrayList<Rhyme> curLineRhymes = rc.collection[i];

            for (Rhyme r : curLineRhymes) {
                int firstWord = wordIndex(rc.lines.get(i), r.aStart.syllable);
                int lastWord = wordIndex(rc.lines.get(i), r.aEnd().syllable);

                // Append non-rhyming words first
                if (firstWord != 0) {
                    for (int j = 0; j < firstWord; j++) {
                        textOutput.append(rc.lines.get(i).get(j).getPlainWord()).append(" ");
                    }
                }

                // Apply opening tag for the style
                textOutput.append(styles[styleMod * 2]);

                for (int j = firstWord; j <= lastWord; j++) {
                    textOutput.append(rc.lines.get(i).get(j).getPlainWord());
                    if (j != lastWord) {
                        textOutput.append(" ");
                    }
                }

                // Apply closing tag for the style
                textOutput.append(styles[styleMod * 2 + 1]);

                // Check if rhyme occurs on the same line
                if (r.aStart.sameLine(r.bStart)) {
                    firstWord = wordIndex(rc.lines.get(i), r.bStart.syllable);
                    lastWord = wordIndex(rc.lines.get(i), r.bEnd().syllable);

                    // Append non-rhyming words first
                    if (firstWord != 0) {
                        for (int j = 0; j < firstWord; j++) {
                            textOutput.append(rc.lines.get(i).get(j).getPlainWord()).append(" ");
                        }
                    }

                    // Apply opening tag for the style
                    textOutput.append(styles[styleMod * 2]);

                    for (int j = firstWord; j <= lastWord; j++) {
                        textOutput.append(rc.lines.get(i).get(j).getPlainWord());
                        if (j != lastWord) {
                            textOutput.append(" ");
                        }
                    }

                    // Apply closing tag for the style
                    textOutput.append(styles[styleMod * 2 + 1]);
                } else {
                    firstWord = wordIndex(rc.lines.get(i + 1), r.bStart.syllable);
                    lastWord = wordIndex(rc.lines.get(i + 1), r.bEnd().syllable);

                    textOutput.append(styles[styleMod * 2]);

                    for (int k = firstWord; k <= lastWord; k++) {
                        textOutput.append(rc.lines.get(i + 1).get(k).getPlainWord()).append(" ");
                    }

                    textOutput.append(styles[styleMod * 2 + 1]);
                }

                styleMod = (styleMod + 1) % (styles.length / 2);  // Rotate through the styles
            }

            // Add a newline after each line
            textOutput.append("\n");
        }

        return textOutput.toString();
    }
}
