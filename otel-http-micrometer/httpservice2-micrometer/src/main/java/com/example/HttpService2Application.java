package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

@SpringBootApplication
public class HttpService2Application {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(HttpService2Application.class, args);
    }
}

@Service
class FetchCommitService {

    private static final Logger log = LoggerFactory.getLogger(FetchCommitService.class);

    @Value("${whatthecommit.url}")
    private String whatTheCommitUrl;

    private final RestTemplate restTemplate;
    private final ObservationRegistry registry;

    FetchCommitService(RestTemplate restTemplate, ObservationRegistry registry) {
        this.restTemplate = restTemplate;
        this.registry = registry;
    }

    String fetchCommit() {
        var observation = Observation.createNotStarted("fetch-commit", registry).start();
        try (var ignored = observation.openScope()) {
            String commitMsg = this.restTemplate.getForObject(whatTheCommitUrl, String.class);
            //noinspection DataFlowIssue
            observation.highCardinalityKeyValue("commit.message", commitMsg);
            observation.event(Observation.Event.of("commit-fetched"));
            log.info(commitMsg);
            return commitMsg;
        } finally {
            observation.stop();
        }
    }
}

@RestController
class HomeController {

    private final FetchCommitService service;

    HomeController(FetchCommitService service) {
        this.service = service;
    }

    @RequestMapping("/")
    String home() {
        return service.fetchCommit();
    }
}
