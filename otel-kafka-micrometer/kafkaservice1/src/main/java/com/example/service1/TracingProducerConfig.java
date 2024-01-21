package com.example.service1;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.MicrometerProducerListener;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

import io.micrometer.core.instrument.MeterRegistry;

@EnableKafka
@Configuration
public class TracingProducerConfig {

    private final KafkaProperties kafkaProperties;
    private final MeterRegistry meterRegistry;

    public TracingProducerConfig(KafkaProperties kafkaProperties, MeterRegistry meterRegistry) {
        this.kafkaProperties = kafkaProperties;
        this.meterRegistry = meterRegistry;
    }

    @SuppressWarnings("resource")
    @Bean
    public Map<String, Object> producerConfigs(SslBundles sslBundles) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties(sslBundles));

        props.put(ProducerConfig.CLIENT_ID_CONFIG, "com.example.tracing-demo-producer");
        props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass().getName());
        props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass().getName());

        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory(SslBundles sslBundles) {
        final ProducerFactory<String, String> factory = new DefaultKafkaProducerFactory<>(producerConfigs(sslBundles));
        factory.addListener(new MicrometerProducerListener<>(meterRegistry)); // adds native Kafka producer metrics
        return factory;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(SslBundles sslBundles) {
        var template = new KafkaTemplate<>(producerFactory(sslBundles));
        template.setObservationEnabled(true);
        return template;
    }
}
