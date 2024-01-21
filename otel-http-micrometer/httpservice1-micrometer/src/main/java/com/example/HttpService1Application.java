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
public class HttpService1Application {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(HttpService1Application.class, args);
    }
}

@Service
class MyService {

    private final RestTemplate restTemplate;
    private final ObservationRegistry registry;

    @Value("${httpservice2.endpoint}")
    private String httpService2Endpoint;

    MyService(RestTemplate restTemplate, ObservationRegistry registry) {
        this.restTemplate = restTemplate;
        this.registry = registry;
    }

    String callService2() {
        var observation = Observation.createNotStarted("call-service2", registry).start();
        try (var ignored = observation.openScope()) {
            String commitMsg = this.restTemplate.getForObject(httpService2Endpoint, String.class);
            //noinspection DataFlowIssue
            observation.highCardinalityKeyValue("commit.message", commitMsg);
            return commitMsg;
        } finally {
            observation.stop();
        }

    }
}

@RestController
class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final MyService service;

    HomeController(MyService service) {
        this.service = service;
    }

    @RequestMapping("/")
    String home() {
        log.info("Hit service 2");
        return service.callService2();
    }
}
