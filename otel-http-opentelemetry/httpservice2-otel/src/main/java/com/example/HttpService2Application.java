package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

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

    private final RestTemplate restTemplate;
    private final Tracer tracer;

    FetchCommitService(RestTemplate restTemplate, OpenTelemetry openTelemetry) {
        this.restTemplate = restTemplate;
        this.tracer = openTelemetry.getTracer(FetchCommitService.class.getName());
    }

    String fetchCommit() {
        Span span = tracer.spanBuilder("fetch-commit").startSpan();
        try (Scope ignored = span.makeCurrent()) {
            String commitMsg = this.restTemplate.getForObject("https://whatthecommit.com/index.txt", String.class);
            log.info(commitMsg);
            //noinspection DataFlowIssue
            span.setAttribute("commit.message", commitMsg);
            span.addEvent("commit-fetched");
            return commitMsg;
        } finally {
            span.end();
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
