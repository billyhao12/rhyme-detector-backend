/**
 * This Controller class provides endpoints that can be called to
 * provide highlighting data for different types of rhymes.
 */

package com.example.rhymedetectorbackend;

import com.example.rhymedetectorbackend.lib.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@CrossOrigin(origins = { "http://localhost:3000", "https://rhyme-detector.vercel.app" })
@RestController
public class Controller {

    public static class BadLyricsException extends RuntimeException {
        private final Lyrics lyrics;

        public BadLyricsException(Lyrics lyrics) {
            this.lyrics = lyrics;
        }

        public Lyrics getErrorData() {
            return lyrics;
        }
    }

    @ExceptionHandler(BadLyricsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Lyrics>> handleBadLyricsException(BadLyricsException ex) {
        ApiResponse<Lyrics> response = ApiResponse.fail(ex.getErrorData());
        return ResponseEntity.badRequest().body(response);
    }

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

    @Operation(
            description = "Highlights monosyllable rhymes",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Lyrics input for highlighting monosyllable rhymes",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Lyrics.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Valid Request",
                                            value = """
                                            {
                                                "lyrics": "Hello world\\nThis is a test"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Invalid Request",
                                            description = "Example of an invalid request body with empty lyrics",
                                            value = """
                                            {
                                                "lyrics": ""
                                            }
                                            """
                                    )
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                            {
                                "status": "success",
                                "data": {
                                    "lyrics": [
                                        [
                                            {"word": "Hello", "style": ["highlight"]},
                                            {"word": "world", "style": []}
                                        ],
                                        [
                                            {"word": "This", "style": []},
                                            {"word": "is", "style": ["highlight"]},
                                            {"word": "a", "style": []},
                                            {"word": "test", "style": ["highlight"]}
                                        ]
                                    ]
                                }
                            }
                            """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Empty lyrics",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                            {
                                "status": "fail",
                                "data": {
                                    "lyrics": "No lyrics to highlight"
                                }
                            }
                            """)
                    )
            )
    })
    @PostMapping("/rhymes/monosyllable")
    public ApiResponse<RhymeData> highlightMonosyllableRhymes(@RequestBody Lyrics lyrics) throws Exception {
        Detector detector = new Detector();
        Transcriptor transcriptor = new Transcriptor();

        if (lyrics.getLyrics() == null || lyrics.getLyrics().isEmpty()) {
            Lyrics errorResponse = new Lyrics("No lyrics to highlight");
            throw new BadLyricsException(errorResponse);
        }

        String[] plainLines = lyrics.getLyrics().split("\n");

        ArrayList<PLine> inLines = new ArrayList<PLine>();
        for (int i = 0; i < plainLines.length; i++) {
            // inLines contain phonemes for each line
            inLines.add(transcriptor.transcribe(plainLines[i]));
        }
        RhymeCollection rhymeCollection = detector.getMonosyllableRhymes(inLines);
        rhymeCollection.lines = inLines;

        // I've never encountered a situation where this is true
        if (inLines.isEmpty()) {
            Lyrics errorResponse = new Lyrics("No lines in input text");
            throw new BadLyricsException(errorResponse);
        }

        // Initialize data structure to send as a response
        // Nested arrays represent lines in the lyrics
        // Objects within nested arrays are StyledWord objects
        ArrayList<StyledWord>[] styledLyrics;
        styledLyrics = new ArrayList[plainLines.length];
        for (int i = 0; i < styledLyrics.length; i++) {
            styledLyrics[i] = new ArrayList<>();
            String[] curLine = plainLines[i].split("[\\s-]+");

            for (int j = 0; j < curLine.length; j++) {
                styledLyrics[i].add(new StyledWord(curLine[j], new ArrayList<>()));
            }
        }

        // Loop through the lines in the rhyme collection
        for (int i = 0; i < rhymeCollection.lines.size(); i++) {
            ArrayList<Rhyme> curLineRhymes = rhymeCollection.collection[i];

            // Loop through rhymes in each line
            for (int j = 0; j < curLineRhymes.size(); j++) {
                Rhyme rhyme = curLineRhymes.get(j);

                // Get index of word "A" in pair
                int wordAIndex = wordIndex(rhymeCollection.lines.get(i), rhyme.aStart.syllable);

                // Apply highlight styling
                StyledWord wordA = styledLyrics[i].get(wordAIndex);
                if (wordA.getStyle().isEmpty()) {
                    wordA.style.add("highlight");
                }

                // Check if rhyme occurs on the same line
                if (rhyme.aStart.sameLine(rhyme.bStart)) {
                    int wordBIndex = wordIndex(rhymeCollection.lines.get(i), rhyme.bStart.syllable);
                    StyledWord wordB = styledLyrics[i].get(wordBIndex);
                    if (wordB.getStyle().isEmpty()) {
                        wordB.style.add("highlight");
                    }
                } else {
                    // Handle rhymes across different lines
                    int wordBIndex = wordIndex(rhymeCollection.lines.get(i + 1), rhyme.bStart.syllable);
                    StyledWord wordB = styledLyrics[i + 1].get(wordBIndex);
                    if (wordB.getStyle().isEmpty()) {
                        wordB.style.add("highlight");
                    }
                }
            }
        }

        RhymeData monosyllableRhymeData = new RhymeData(styledLyrics);
        return ApiResponse.success(monosyllableRhymeData);
    }

    @Operation(
            description = "Highlights multisyllable rhymes",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Lyrics input for highlighting multisyllable rhymes",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Lyrics.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Valid Request",
                                            value = """
                                            {
                                                "lyrics": "Hello world\\nThis is a test"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Invalid Request",
                                            description = "Example of an invalid request body with empty lyrics",
                                            value = """
                                            {
                                                "lyrics": ""
                                            }
                                            """
                                    )
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                            {
                                "status": "success",
                                "data": {
                                    "lyrics": [
                                        [
                                            {"word": "Hello", "style": ["bold"]},
                                            {"word": "world", "style": ["bold, italic"]}
                                        ],
                                        [
                                            {"word": "This", "style": ["bold"]},
                                            {"word": "is", "style": ["bold, italic"]},
                                            {"word": "a", "style": []},
                                            {"word": "test", "style": []}
                                        ]
                                    ]
                                }
                            }
                            """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Empty lyrics",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                            {
                                "status": "fail",
                                "data": {
                                    "lyrics": "No lyrics to highlight"
                                }
                            }
                            """)
                    )
            )
    })
    @PostMapping("/rhymes/multisyllable")
    public ApiResponse<RhymeData> highlightMultisyllableRhymes(@RequestBody Lyrics lyrics) throws Exception {
        String STATS_FILE = "iterationStatsUF.txt";
        Stats st = new Stats(STATS_FILE);
        Scoring sc = new Scoring(st, Stats.SPLIT);
        Detector det = new Detector(sc);
        Transcriptor tr = new Transcriptor();

        if (lyrics.getLyrics() == null || lyrics.getLyrics().isEmpty()) {
            Lyrics errorResponse = new Lyrics("No lyrics to highlight");
            throw new BadLyricsException(errorResponse);
        }

        String[] plainLines = lyrics.getLyrics().split("\n");

        ArrayList<PLine> inLines = new ArrayList<PLine>();
        for (int i = 0; i < plainLines.length; i++) {
            // inLines contain phonemes for each line
            inLines.add(tr.transcribe(plainLines[i]));
        }
        RhymeCollection rc = det.getMultisyllableRhymes(inLines);
        rc.lines = inLines;

        // I've never encountered a situation where this is true
        if (inLines.isEmpty()) {
            Lyrics errorResponse = new Lyrics("No lines in input text");
            throw new BadLyricsException(errorResponse);
        }

        // Initialize data structure to send as a response
        // Nested arrays represent lines in the lyrics
        // Objects within nested arrays are StyledWord objects
        ArrayList<StyledWord>[] styledLyrics;
        styledLyrics = new ArrayList[plainLines.length];
        for (int i = 0; i < styledLyrics.length; i++) {
            styledLyrics[i] = new ArrayList<>();
            String[] curLine = plainLines[i].split("[\\s-]+");

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

        RhymeData multisyllableRhymeData = new RhymeData(styledLyrics);
        return ApiResponse.success(multisyllableRhymeData);
    }
}
