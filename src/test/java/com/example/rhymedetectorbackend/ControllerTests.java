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
        Lyrics lyrics = new Lyrics("His palms are sweaty, knees weak, arms are heavy\nThere's vomit on his sweater already, mom's spaghetti");

        String mockJsonResponse = "{\"status\":\"success\",\"data\":{\"lyrics\":[[{\"word\":\"His\",\"style\":[]},{\"word\":\"palms\",\"style\":[\"bold\"]},{\"word\":\"are\",\"style\":[\"bold\"]},{\"word\":\"sweaty,\",\"style\":[\"bold\",\"red\"]},{\"word\":\"knees\",\"style\":[]},{\"word\":\"weak,\",\"style\":[]},{\"word\":\"arms\",\"style\":[\"bold\",\"italic\",\"highlight\"]},{\"word\":\"are\",\"style\":[\"bold\",\"highlight\"]},{\"word\":\"heavy\",\"style\":[\"bold\",\"underline\",\"highlight\"]}],[{\"word\":\"There's\",\"style\":[]},{\"word\":\"vomit\",\"style\":[\"italic\",\"bold\"]},{\"word\":\"on\",\"style\":[]},{\"word\":\"his\",\"style\":[]},{\"word\":\"sweater\",\"style\":[\"red\"]},{\"word\":\"already,\",\"style\":[\"underline\",\"italic\"]},{\"word\":\"mom's\",\"style\":[\"highlight\",\"bold\"]},{\"word\":\"spaghetti\",\"style\":[\"highlight\",\"italic\"]}]],\"rhymePairs\":[{\"elementA\":\"palms are sweaty\",\"elementB\":\"arms are heavy\",\"style\":\"bold\",\"lines\":[1]},{\"elementA\":\"arms\",\"elementB\":\"vomit\",\"style\":\"italic\",\"lines\":[1,2]},{\"elementA\":\"sweaty\",\"elementB\":\"sweater\",\"style\":\"red\",\"lines\":[1,2]},{\"elementA\":\"heavy\",\"elementB\":\"already\",\"style\":\"underline\",\"lines\":[1,2]},{\"elementA\":\"arms are heavy\",\"elementB\":\"mom's spaghetti\",\"style\":\"highlight\",\"lines\":[1,2]},{\"elementA\":\"vomit\",\"elementB\":\"mom's\",\"style\":\"bold\",\"lines\":[2]},{\"elementA\":\"already\",\"elementB\":\"spaghetti\",\"style\":\"italic\",\"lines\":[2]}]}}";

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
        Lyrics lyrics = new Lyrics("A child is born with no state of mind\nBlind to the ways of mankind");

        String mockJsonResponse = "{\"status\":\"success\",\"data\":{\"lyrics\":[[{\"word\":\"A\",\"style\":[\"highlight\"]},{\"word\":\"child\",\"style\":[]},{\"word\":\"is\",\"style\":[]},{\"word\":\"born\",\"style\":[]},{\"word\":\"with\",\"style\":[]},{\"word\":\"no\",\"style\":[]},{\"word\":\"state\",\"style\":[]},{\"word\":\"of\",\"style\":[\"highlight\"]},{\"word\":\"mind\",\"style\":[\"highlight\"]}],[{\"word\":\"Blind\",\"style\":[\"highlight\"]},{\"word\":\"to\",\"style\":[]},{\"word\":\"the\",\"style\":[\"highlight\"]},{\"word\":\"ways\",\"style\":[]},{\"word\":\"of\",\"style\":[\"highlight\"]},{\"word\":\"mankind\",\"style\":[\"highlight\"]}]],\"rhymePairs\":[{\"elementA\":\"A\",\"elementB\":\"the\",\"style\":\"highlight\",\"lines\":[1,2]},{\"elementA\":\"of\",\"elementB\":\"of\",\"style\":\"highlight\",\"lines\":[1,2]},{\"elementA\":\"mind\",\"elementB\":\"Blind\",\"style\":\"highlight\",\"lines\":[1,2]},{\"elementA\":\"mind\",\"elementB\":\"mankind\",\"style\":\"highlight\",\"lines\":[1,2]},{\"elementA\":\"Blind\",\"elementB\":\"mankind\",\"style\":\"highlight\",\"lines\":[2]}]}}";

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
