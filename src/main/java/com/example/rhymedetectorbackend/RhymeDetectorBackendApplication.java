/**
 * This is the main application. Run this class to start the server.
 * It contains a basic "/hello" endpoint.
 */

package com.example.rhymedetectorbackend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            description = "Basic greeting endpoint",
            parameters = {
                    @Parameter(
                            name = "myName",
                            description = "The name of the person to greet",
                            example = "Alice"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful greeting message",
                            content = @Content(
                                    mediaType = "text/plain",
                                    examples = {
                                            @ExampleObject(
                                                    value = "Hello Alice!"
                                            )
                                    }
                            )
                    )
            }
    )
    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }
}
