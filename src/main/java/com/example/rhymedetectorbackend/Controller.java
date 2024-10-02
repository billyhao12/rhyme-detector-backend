package com.example.rhymedetectorbackend;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @PostMapping("/rhymes/multisyllable")
    public LyricsData highlightMultisyllableRhymes(@RequestBody LyricsData userInput) {
//        String[] plainLines = userInput.getLyrics().split("\n");
        return userInput;
    }
}
