package com.example.rhymedetectorbackend.http;

import com.example.rhymedetectorbackend.Lyrics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Lyrics>> handleBadRequestException(BadRequestException ex) {
        ApiResponse<Lyrics> response = ApiResponse.fail(ex.getErrorData());
        return ResponseEntity.badRequest().body(response);
    }
}
