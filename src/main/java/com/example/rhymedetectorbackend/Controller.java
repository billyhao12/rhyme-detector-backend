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
    public LyricsData highlightMultisyllableRhymes(@RequestBody LyricsData userInput) throws Exception {
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

        JTextPane txtOutput = new JTextPane();
        txtOutput.setText("");

        int styleMod = 0;
        SimpleAttributeSet[] curLine = new SimpleAttributeSet[0];
        SimpleAttributeSet[] nextLine = new SimpleAttributeSet[rc.lines.get(0).size()];
        for (int i = 0; i < nextLine.length; i++) {
            nextLine[i] = new SimpleAttributeSet();
        }

        for (int i=0; i<rc.lines.size(); i++) {
            curLine = nextLine;
            if (rc.lines.size()>i+1) {
                nextLine = new SimpleAttributeSet[rc.lines.get(i+1).size()];
                for (int j=0; j<nextLine.length; j++) {
                    nextLine[j] = new SimpleAttributeSet();
                }
            }

            ArrayList<Rhyme> curLineRhymes = rc.collection[i];

            SimpleAttributeSet[] attributes = new SimpleAttributeSet[5];
            for (int j = 0; j < attributes.length; j++) {
                attributes[j] = new SimpleAttributeSet();
            }

            StyleConstants.setBold(attributes[0], true);
            StyleConstants.setItalic(attributes[1], true);
            // StyleConstants.setFontFamily(attributes[2], "Serif");
            StyleConstants.setForeground(attributes[2], Color.red);
            StyleConstants.setUnderline(attributes[3], true);
            StyleConstants.setStrikeThrough(attributes[4], true);

            for (int j = 0; j < curLineRhymes.size(); j++) {
                Rhyme r = curLineRhymes.get(j);
                int firstWord = wordIndex(rc.lines.get(i),r.aStart.syllable);
                int lastWord = wordIndex(rc.lines.get(i),r.aEnd().syllable);
                for (int k=firstWord; k<=lastWord; k++) {
                    curLine[k].addAttributes(attributes[styleMod]);
                }
                if (r.aStart.sameLine(r.bStart)) {
                    firstWord = wordIndex(rc.lines.get(i),r.bStart.syllable);
                    lastWord = wordIndex(rc.lines.get(i), r.bEnd().syllable);
                    for (int k=firstWord; k<=lastWord; k++) {
                        curLine[k].addAttributes(attributes[styleMod]);
                    }
                } else {
                    firstWord = wordIndex(rc.lines.get(i+1),r.bStart.syllable);
                    lastWord = wordIndex(rc.lines.get(i+1),r.bEnd().syllable);
                    for (int k=firstWord; k<=lastWord; k++) {
                        nextLine[k].addAttributes(attributes[styleMod]);
                    }
                }
                styleMod = (styleMod + 1) % attributes.length;
            }

            try {
                Document d = txtOutput.getDocument();
                for (int k=0; k<curLine.length; k++) {
                    d.insertString(d.getLength(), rc.lines.get(i).get(k).getPlainWord() + " ", curLine[k]);
                }
                d.insertString(d.getLength(), "\n", new SimpleAttributeSet());
                txtOutput.setDocument(d);
            } catch (Exception ble) {
//                JOptionPane.showMessageDialog(this, "Error writing to document." + ble.getMessage());
            }
        }

        return userInput;
    }
}
