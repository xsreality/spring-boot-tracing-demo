package com.example.service1;

import org.apache.kafka.clients.producer.Producer;
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
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.kafkaclients.v2_6.KafkaTelemetry;

@EnableKafka
@Configuration
public class TracingProducerConfig {

    private final KafkaProperties kafkaProperties;
    private final MeterRegistry meterRegistry;
    private final OpenTelemetry openTelemetry;

    public TracingProducerConfig(KafkaProperties kafkaProperties, MeterRegistry meterRegistry, OpenTelemetry openTelemetry) {
        this.kafkaProperties = kafkaProperties;
        this.meterRegistry = meterRegistry;
        this.openTelemetry = openTelemetry;
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
        final ProducerFactory<String, String> factory = new OtelKafkaProducerFactory(producerConfigs(sslBundles), openTelemetry);
        factory.addListener(new MicrometerProducerListener<>(meterRegistry)); // adds native Kafka producer metrics
        return factory;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(SslBundles sslBundles) {
        return new KafkaTemplate<>(producerFactory(sslBundles));
    }
}

/**
 * A custom {@link ProducerFactory} to wrap the producer with tracing enabled producer.
 */
@SuppressWarnings("NullableProblems")
class OtelKafkaProducerFactory extends DefaultKafkaProducerFactory<String, String> {

    private final OpenTelemetry openTelemetry;

    public OtelKafkaProducerFactory(Map<String, Object> configs, OpenTelemetry openTelemetry) {
        super(configs);
        this.openTelemetry = openTelemetry;
    }

    @Override
    protected Producer<String, String> createKafkaProducer() {
        final Producer<String, String> producer = super.createKafkaProducer();
        return KafkaTelemetry.create(openTelemetry).wrap(producer);
    }

    @Override
    public Producer<String, String> createProducer() {
        final Producer<String, String> producer = super.createProducer();
        return KafkaTelemetry.create(openTelemetry).wrap(producer);
    }

    @Override
    public Producer<String, String> createNonTransactionalProducer() {
        final Producer<String, String> producer = super.createNonTransactionalProducer();
        return KafkaTelemetry.create(openTelemetry).wrap(producer);
    }
}