/**
 * This class defines a format of the data that will be returned
 * when a bad request is sent from the client.
 */

package com.example.rhymedetectorbackend.http;

import com.example.rhymedetectorbackend.Lyrics;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadLyricsException extends RuntimeException {
    private final Lyrics lyrics;

    public BadLyricsException(Lyrics lyrics) {
        this.lyrics = lyrics;
    }

    public Lyrics getErrorData() {
        return lyrics;
    }
}
