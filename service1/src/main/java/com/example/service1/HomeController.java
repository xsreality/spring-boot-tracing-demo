package com.example.service1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final RestTemplate restTemplate;

    public HomeController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping("/")
    String service1() {
        log.info("Hit service 1!");
        String fooResourceUrl = "http://localhost:8081";
        ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
        log.info("Status code {}", response.getStatusCode());
        return response.getBody();
    }
}
