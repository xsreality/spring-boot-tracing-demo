package com.example.service1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

@RestController
@SpringBootApplication
public class KafkaService1Application {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaService1Application.class, args);
    }
}

@RestController
class HomeController {

    private final FetchCommitService service;

    public HomeController(FetchCommitService service) {
        this.service = service;
    }

    @RequestMapping("/")
    String service1() {
        return service.fetchCommit();
    }
}

@Service
class FetchCommitService {

    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObservationRegistry registry;

    FetchCommitService(RestTemplate restTemplate, KafkaTemplate<String, String> kafkaTemplate, ObservationRegistry registry) {
        this.restTemplate = restTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.registry = registry;
    }

    String fetchCommit() {
        var observation = Observation.createNotStarted("fetch-commit", registry).start();
        try (var ignored = observation.openScope()) {
            String commitMsg = restTemplate.getForObject("https://whatthecommit.com/index.txt", String.class);
            //noinspection DataFlowIssue
            observation.highCardinalityKeyValue("commit.message", commitMsg);
            observation.event(Observation.Event.of("commit-fetched"));
            this.kafkaTemplate.send("tracing-demo", "commit-msg", commitMsg);
            observation.event(Observation.Event.of("event-triggered"));
            return commitMsg;
        } catch (Exception o_O) {
            observation.error(o_O);
            throw o_O;
        } finally {
            observation.stop();
        }
    }
}
