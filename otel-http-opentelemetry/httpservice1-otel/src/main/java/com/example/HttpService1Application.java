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

import io.opentelemetry.instrumentation.annotations.WithSpan;

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

    @Value("${httpservice2.endpoint}")
    private String httpService2Endpoint;

    MyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @WithSpan("CallService2")
    String callService2() {
        return this.restTemplate.getForObject(httpService2Endpoint, String.class);
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
