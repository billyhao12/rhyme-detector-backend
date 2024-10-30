/**
 * This is the main application. Run this class to start the server.
 * It contains a basic "/hello" endpoint.
 */

package com.example.rhymedetectorbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class RhymeDetectorBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RhymeDetectorBackendApplication.class, args);
    }

    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }

}
