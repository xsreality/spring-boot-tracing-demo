package com.example.service2;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.instrument.kafka.TracingKafkaPropagatorGetter;
import org.springframework.cloud.sleuth.propagation.Propagator;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Component
@RestController
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate;
    private final Tracer tracer;
    private final Propagator propagator;

    public HomeController(KafkaTemplate<String, String> kafkaTemplate, RestTemplate restTemplate, Tracer tracer, Propagator propagator) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
        this.tracer = tracer;
        this.propagator = propagator;
    }

    @RequestMapping("/")
    String service2() {
        log.info("Hit service 2!");
        this.kafkaTemplate.setProducerListener(new ProducerListener<>() {
            @Override
            public void onSuccess(ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata) {
                log.info("Successfully updated kafka {}", producerRecord.value());
            }

            @Override
            public void onError(ProducerRecord<String, String> producerRecord, RecordMetadata metadata, Exception exception) {
                log.error("Error producing record {}", producerRecord, exception);
            }
        });
        this.kafkaTemplate.send("tracing-demo", "this is key", "this is value at " + System.currentTimeMillis());
        return "Service 2 responding for duty.";
    }

    @KafkaListener(topics = "tracing-demo")
    public void listen(@Payload String message, ConsumerRecord<String, String> record) {
        long now = System.currentTimeMillis();
        Span.Builder spanBuilder = this.propagator.extract(record, new TracingKafkaPropagatorGetter());
        final Span span = spanBuilder.name("intermediate-connector").tag("topic", record.topic()).start();
        try (Tracer.SpanInScope spanInScope = tracer.withSpan(span)) {
            log.info("Message is '{}'. Received message on topic {} partition {} offset {} header {} key '{}' value '{}'. Time to consume {} ms",
                    record.value(), record.topic(), record.partition(), record.offset(),
                    record.headers(), record.key(), message, now - record.timestamp());
            // fake next service call
            ResponseEntity<String> response = restTemplate.getForEntity("https://jsonplaceholder.typicode.com/todos/1", String.class);
            log.info("Status code {}", response.getStatusCode());
        } finally {
            span.end();
        }
    }

}
