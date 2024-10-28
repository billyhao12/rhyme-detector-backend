package com.example.rhymedetectorbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTests {

    @Autowired
    private Controller controller;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    void shouldHighlightMultisyllableRhymes() throws Exception {
        Lyrics lyrics = new Lyrics("His palms are sweaty, knees weak, arms are heavy\nThere's vomit on his sweater already, mom's spaghetti\nHe's nervous, but on the surface, he looks calm and ready\nTo drop bombs, but he keeps on forgetting");

        String mockJsonResponse = "{\"status\": \"success\",\"data\": {\"lyrics\": [[{\"word\": \"His\",\"style\": []},{\"word\": \"palms\",\"style\": [\"bold\"]},{\"word\": \"are\",\"style\": [\"bold\"]},{\"word\": \"sweaty,\",\"style\": [\"bold\",\"red\"]},{\"word\": \"knees\",\"style\": []},{\"word\": \"weak,\",\"style\": []},{\"word\": \"arms\",\"style\": [\"bold\",\"italic\",\"strikethrough\"]},{\"word\": \"are\",\"style\": [\"bold\",\"strikethrough\"]},{\"word\": \"heavy\",\"style\": [\"bold\",\"underline\",\"strikethrough\"]}],[{\"word\": \"There's\",\"style\": []},{\"word\": \"vomit\",\"style\": [\"italic\",\"bold\"]},{\"word\": \"on\",\"style\": []},{\"word\": \"his\",\"style\": []},{\"word\": \"sweater\",\"style\": [\"red\"]},{\"word\": \"already,\",\"style\": [\"underline\",\"italic\"]},{\"word\": \"mom's\",\"style\": [\"strikethrough\",\"bold\",\"red\"]},{\"word\": \"spaghetti\",\"style\": [\"strikethrough\",\"italic\",\"red\"]}],[{\"word\": \"He's\",\"style\": []},{\"word\": \"nervous,\",\"style\": [\"underline\"]},{\"word\": \"but\",\"style\": []},{\"word\": \"on\",\"style\": []},{\"word\": \"the\",\"style\": []},{\"word\": \"surface,\",\"style\": [\"underline\"]},{\"word\": \"he\",\"style\": [\"bold\"]},{\"word\": \"looks\",\"style\": []},{\"word\": \"calm\",\"style\": [\"red\",\"strikethrough\"]},{\"word\": \"and\",\"style\": [\"red\"]},{\"word\": \"ready\",\"style\": [\"red\",\"italic\"]}],[{\"word\": \"To\",\"style\": []},{\"word\": \"drop\",\"style\": []},{\"word\": \"bombs,\",\"style\": [\"strikethrough\"]},{\"word\": \"but\",\"style\": []},{\"word\": \"he\",\"style\": [\"bold\"]},{\"word\": \"keeps\",\"style\": []},{\"word\": \"on\",\"style\": []},{\"word\": \"forgetting\",\"style\": [\"italic\"]}]]}}";

        // Convert mock JSON response to a Map
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> expectedResponse = objectMapper.readValue(mockJsonResponse, Map.class);

        // Perform POST request and get the response
        Map<String, Object> actualResponse = restTemplate.postForObject("http://localhost:" + port + "/rhymes/multisyllable", lyrics, Map.class);

        // Assert that the responses are equal
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}
