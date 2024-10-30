/**
 * This Controller class provides endpoints that can be called to
 * provide highlighting data for different types of rhymes.
 */

package com.example.rhymedetectorbackend;

import com.example.rhymedetectorbackend.http.ApiResponse;
import com.example.rhymedetectorbackend.http.BadRequestException;
import com.example.rhymedetectorbackend.lib.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@CrossOrigin(origins = { "http://localhost:3000", "https://rhyme-detector.vercel.app" })
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
    public ApiResponse<MultisyllableRhymeData> highlightMultisyllableRhymes(@RequestBody Lyrics lyrics) throws Exception {
        String STATS_FILE = "iterationStatsUF.txt";
        Stats st = new Stats(STATS_FILE);
        Scoring sc = new Scoring(st, Stats.SPLIT);
        Detector det = new Detector(sc);
        Transcriptor tr = new Transcriptor();

        if (lyrics.getLyrics() == null || lyrics.getLyrics().isEmpty()) {
            Lyrics errorResponse = new Lyrics("No lyrics to highlight");
            throw new BadRequestException(errorResponse);
        }

        String[] plainLines = lyrics.getLyrics().split("\n");

        ArrayList<PLine> inLines = new ArrayList<PLine>();
        for (int i = 0; i < plainLines.length; i++) {
            inLines.add(tr.transcribe(plainLines[i]));
        }
        RhymeCollection rc = det.getRhymes(inLines);
        rc.lines = inLines;

        // I've never encountered a situation where this is true
        if (inLines.isEmpty()) {
            Lyrics errorResponse = new Lyrics("No lines in input text");
            throw new BadRequestException(errorResponse);
        }

        // Initialize data structure to send as a response
        // Nested arrays represent lines in the lyrics
        // Objects within nested arrays are StyledWord Records
        ArrayList<StyledWord>[] styledLyrics;
        styledLyrics = new ArrayList[plainLines.length];
        for (int i = 0; i < styledLyrics.length; i++) {
            styledLyrics[i] = new ArrayList<>();
            String[] curLine = plainLines[i].split(" ");

            for (int j = 0; j < curLine.length; j++) {
                styledLyrics[i].add(new StyledWord(curLine[j], new ArrayList<>()));
            }
        }

        // Define styles used to highlight rhymes
        int styleMod = 0;
        String[] styles = { "bold", "italic", "red", "underline", "strikethrough" };

        // Loop through the lines in the rhyme collection
        for (int i = 0; i < rc.lines.size(); i++) {
            ArrayList<Rhyme> curLineRhymes = rc.collection[i];

            // Loop through rhyme phrase pairs
            for (int j = 0; j < curLineRhymes.size(); j++) {
                Rhyme r = curLineRhymes.get(j);

                // First and last word of rhyme phrase A
                int firstWord = wordIndex(rc.lines.get(i), r.aStart.syllable);
                int lastWord = wordIndex(rc.lines.get(i), r.aEnd().syllable);

                // Update the styling of each word contained in the rhyme phrase
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

                // Rotate through the styles
                styleMod = (styleMod + 1) % styles.length;
            }
        }

        MultisyllableRhymeData multisyllableRhymeData = new MultisyllableRhymeData(styledLyrics);
        return ApiResponse.success(multisyllableRhymeData);
    }
}
