/**
 * This file tests that the app is able to start.
 * It also tests that the "/hello" endpoint is working.
 */

package com.example.rhymedetectorbackend;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RhymeDetectorBackendApplicationTests {

    @Autowired
    private RhymeDetectorBackendApplication rhymeDetectorBackendApplication;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        assertThat(rhymeDetectorBackendApplication).isNotNull();
    }

    @Test
    void shouldSayHello() throws Exception {
        assertThat(restTemplate.getForObject("http://localhost:" + port + "/hello", String.class)).isEqualTo("Hello World!");
    }

    @Test
    void shouldSayHelloWithName() throws Exception {
        assertThat(restTemplate.getForObject("http://localhost:" + port + "/hello?myName=Alice", String.class)).isEqualTo("Hello Alice!");
    }
}
