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

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.WithSpan;

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

    FetchCommitService(RestTemplate restTemplate, KafkaTemplate<String, String> kafkaTemplate) {
        this.restTemplate = restTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @WithSpan("fetch-commit")
    String fetchCommit() {
        Span span = Span.current();
        String commitMsg = restTemplate.getForObject("https://whatthecommit.com/index.txt", String.class);
        //noinspection DataFlowIssue
        span.setAttribute("commit.message", commitMsg);
        span.addEvent("commit-fetched");
        this.kafkaTemplate.send("tracing-demo", "commit-msg", commitMsg);
        span.addEvent("event-triggered");
        return commitMsg;
    }
}
