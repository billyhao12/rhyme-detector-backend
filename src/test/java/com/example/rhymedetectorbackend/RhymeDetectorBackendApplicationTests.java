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
}
