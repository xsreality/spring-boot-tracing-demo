package com.example.service2;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Random;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.WithSpan;

@Component
public class TracingConsumer {

    private static final Logger log = LoggerFactory.getLogger(TracingConsumer.class);

    private final CommitRepository repository;

    public TracingConsumer(CommitRepository repository) {
        this.repository = repository;
    }

    @WithSpan("consume-commit-event")
    @KafkaListener(topics = "tracing-demo")
    public void listen(@Payload String message, ConsumerRecord<String, String> record) {
        var span = Span.current();
        long now = System.currentTimeMillis();
        log.info("Message is '{}'. Received message on topic {} partition {} offset {} header {} key '{}' value '{}'. Time to consume {} ms",
                record.value(), record.topic(), record.partition(), record.offset(),
                record.headers(), record.key(), message, now - record.timestamp());
        repository.save(new Commit(new Random().nextLong(), record.value()));
        span.setAttribute("commit.message", record.value());
        span.addEvent("event-consumed");
        log.info("Commit message is '{}'", record.value());
    }
}
