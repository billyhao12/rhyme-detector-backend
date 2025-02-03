/**
 * This tests that the endpoints in the Controller are behaving as expected.
 */

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

        String mockJsonResponse = "{\"status\": \"success\",\"data\": {\"lyrics\": [[{\"word\": \"His\",\"style\": []},{\"word\": \"palms\",\"style\": [\"bold\"]},{\"word\": \"are\",\"style\": [\"bold\"]},{\"word\": \"sweaty,\",\"style\": [\"bold\",\"red\"]},{\"word\": \"knees\",\"style\": []},{\"word\": \"weak,\",\"style\": []},{\"word\": \"arms\",\"style\": [\"bold\",\"italic\",\"highlight\"]},{\"word\": \"are\",\"style\": [\"bold\",\"highlight\"]},{\"word\": \"heavy\",\"style\": [\"bold\",\"underline\",\"highlight\"]}],[{\"word\": \"There's\",\"style\": []},{\"word\": \"vomit\",\"style\": [\"italic\",\"bold\"]},{\"word\": \"on\",\"style\": []},{\"word\": \"his\",\"style\": []},{\"word\": \"sweater\",\"style\": [\"red\"]},{\"word\": \"already,\",\"style\": [\"underline\",\"italic\"]},{\"word\": \"mom's\",\"style\": [\"highlight\",\"bold\",\"red\"]},{\"word\": \"spaghetti\",\"style\": [\"highlight\",\"italic\",\"red\"]}],[{\"word\": \"He's\",\"style\": []},{\"word\": \"nervous,\",\"style\": [\"underline\"]},{\"word\": \"but\",\"style\": []},{\"word\": \"on\",\"style\": []},{\"word\": \"the\",\"style\": []},{\"word\": \"surface,\",\"style\": [\"underline\"]},{\"word\": \"he\",\"style\": [\"bold\"]},{\"word\": \"looks\",\"style\": []},{\"word\": \"calm\",\"style\": [\"red\",\"highlight\"]},{\"word\": \"and\",\"style\": [\"red\"]},{\"word\": \"ready\",\"style\": [\"red\",\"italic\"]}],[{\"word\": \"To\",\"style\": []},{\"word\": \"drop\",\"style\": []},{\"word\": \"bombs,\",\"style\": [\"highlight\"]},{\"word\": \"but\",\"style\": []},{\"word\": \"he\",\"style\": [\"bold\"]},{\"word\": \"keeps\",\"style\": []},{\"word\": \"on\",\"style\": []},{\"word\": \"forgetting\",\"style\": [\"italic\"]}]]}}";

        // Convert mock JSON response to a Map
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> expectedResponse = objectMapper.readValue(mockJsonResponse, Map.class);

        // Perform POST request and get the response
        Map<String, Object> actualResponse = restTemplate.postForObject("http://localhost:" + port + "/rhymes/multisyllable", lyrics, Map.class);

        // Assert that the responses are equal
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void multisyllableEndpointShouldFailWhenPassedEmptyString() throws Exception {
        Lyrics lyrics = new Lyrics("");

        String mockJsonResponse = "{\"status\": \"fail\",\"data\": {\"lyrics\": \"No lyrics to highlight\"}}";

        // Convert mock JSON response to a Map
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> expectedResponse = objectMapper.readValue(mockJsonResponse, Map.class);

        // Perform POST request and get the response
        Map<String, Object> actualResponse = restTemplate.postForObject("http://localhost:" + port + "/rhymes/multisyllable", lyrics, Map.class);

        // Assert that the responses are equal
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void shouldHighlightMonosyllableRhymes() throws Exception {
        Lyrics lyrics = new Lyrics("His palms are sweaty, knees weak, arms are heavy\nThere's vomit on his sweater already, mom's spaghetti");

        String mockJsonResponse = "{\"status\":\"success\",\"data\":{\"lyrics\":[[{\"word\":\"His\",\"style\":[\"highlight\"]},{\"word\":\"palms\",\"style\":[\"highlight\"]},{\"word\":\"are\",\"style\":[]},{\"word\":\"sweaty,\",\"style\":[\"highlight\"]},{\"word\":\"knees\",\"style\":[]},{\"word\":\"weak,\",\"style\":[]},{\"word\":\"arms\",\"style\":[]},{\"word\":\"are\",\"style\":[]},{\"word\":\"heavy\",\"style\":[\"highlight\"]}],[{\"word\":\"There's\",\"style\":[]},{\"word\":\"vomit\",\"style\":[]},{\"word\":\"on\",\"style\":[]},{\"word\":\"his\",\"style\":[\"highlight\"]},{\"word\":\"sweater\",\"style\":[\"highlight\"]},{\"word\":\"already,\",\"style\":[\"highlight\"]},{\"word\":\"mom's\",\"style\":[\"highlight\"]},{\"word\":\"spaghetti\",\"style\":[\"highlight\"]}]]}}}";

        // Convert mock JSON response to a Map
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> expectedResponse = objectMapper.readValue(mockJsonResponse, Map.class);

        // Perform POST request and get the response
        Map<String, Object> actualResponse = restTemplate.postForObject("http://localhost:" + port + "/rhymes/monosyllable", lyrics, Map.class);

        // Assert that the responses are equal
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void monosyllableEndpointShouldFailWhenPassedEmptyString() throws Exception {
        Lyrics lyrics = new Lyrics("");

        String mockJsonResponse = "{\"status\": \"fail\",\"data\": {\"lyrics\": \"No lyrics to highlight\"}}";

        // Convert mock JSON response to a Map
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> expectedResponse = objectMapper.readValue(mockJsonResponse, Map.class);

        // Perform POST request and get the response
        Map<String, Object> actualResponse = restTemplate.postForObject("http://localhost:" + port + "/rhymes/monosyllable", lyrics, Map.class);

        // Assert that the responses are equal
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}
