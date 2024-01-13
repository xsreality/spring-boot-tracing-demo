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


    MyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    String callService2() {
        return this.restTemplate.getForObject("http://localhost:8081", String.class);
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
