package com.example.service2;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.instrument.kafka.TracingKafkaPropagatorGetter;
import org.springframework.cloud.sleuth.propagation.Propagator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TracingConsumer {
    private static final Logger log = LoggerFactory.getLogger(TracingConsumer.class);

    private final Tracer tracer;
    private final Propagator propagator;
    private final CommitService commitService;

    public TracingConsumer(Tracer tracer, Propagator propagator, CommitService commitService) {
        this.tracer = tracer;
        this.propagator = propagator;
        this.commitService = commitService;
    }

    @KafkaListener(topics = "tracing-demo")
    public void listen(@Payload String message, ConsumerRecord<String, String> record) {
        long now = System.currentTimeMillis();
        Span.Builder spanBuilder = this.propagator.extract(record, new TracingKafkaPropagatorGetter());
        final Span span = spanBuilder.name("consumer-continued").tag("topic", record.topic()).start();
        try (Tracer.SpanInScope spanInScope = tracer.withSpan(span)) {
            log.info("Message is '{}'. Received message on topic {} partition {} offset {} header {} key '{}' value '{}'. Time to consume {} ms",
                    record.value(), record.topic(), record.partition(), record.offset(),
                    record.headers(), record.key(), message, now - record.timestamp());
            commitService.save(new Commit(new Random().nextLong(), record.value()));
            log.info("Commit message is '{}'", record.value());
        } finally {
            span.end();
        }
    }
}
