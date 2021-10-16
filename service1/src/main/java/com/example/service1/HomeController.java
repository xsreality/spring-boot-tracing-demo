package com.example.service1;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public HomeController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @RequestMapping("/")
    String service1() {
        log.info("Hit service 1!");
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
        return "Message produced to Kafka";
    }
}
