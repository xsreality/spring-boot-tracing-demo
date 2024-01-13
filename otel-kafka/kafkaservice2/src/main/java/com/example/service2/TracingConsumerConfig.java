package com.example.service2;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TracingConsumerConfig {

    private final KafkaProperties kafkaProperties;

    @Autowired
    public TracingConsumerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public Map<String, Object> consumerConfigs(SslBundles sslBundles) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties(sslBundles));

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.String().deserializer().getClass().getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Serdes.String().deserializer().getClass().getName());

        return props;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory(SslBundles sslBundles) {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(sslBundles));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(SslBundles sslBundles) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setObservationEnabled(true);
        factory.setConsumerFactory(consumerFactory(sslBundles));
        // Since Spring Kafka 2.3, container fails to start if topic is missing. This check is done by
        // initializing an Admin Client. We disable this to skip checking topic existence.
        factory.setMissingTopicsFatal(false);
        return factory;
    }
}
