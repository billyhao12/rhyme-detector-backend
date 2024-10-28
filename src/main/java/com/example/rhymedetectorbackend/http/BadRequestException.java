package com.example.rhymedetectorbackend.http;

import com.example.rhymedetectorbackend.Lyrics;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    private final Lyrics lyrics;

    public BadRequestException(Lyrics lyrics) {
        this.lyrics = lyrics;
    }

    public Lyrics getErrorData() {
        return lyrics;
    }
}
