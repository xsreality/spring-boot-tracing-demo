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

    FetchCommitService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    String fetchCommit() {
        String commitMsg = this.restTemplate.getForObject("https://whatthecommit.com/index.txt", String.class);
        log.info(commitMsg);
        return commitMsg;
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
