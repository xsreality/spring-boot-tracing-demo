## Observability of Kafka services with OpenTelemetry Auto Instrumentation and Spring Native

![image](https://user-images.githubusercontent.com/4991449/140835427-c652c835-c90c-4864-9014-fcf5a45727b7.png)

> [!NOTE]
> Spring Boot 3.2.2 [has a bug](https://github.com/spring-projects/spring-boot/issues/39254) that prevents startup of native application using Spring Data and Jackson. This example uses 3.2.1.

### Overview

Java agents cannot be used with GraalVM native applications. So how do we use OTEL instrumentation with a Spring native application?

This can be done by adding below POM dependencies:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.opentelemetry</groupId>
            <artifactId>opentelemetry-bom</artifactId>
            <version>${opentelemetry-bom.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>io.opentelemetry.instrumentation</groupId>
            <artifactId>opentelemetry-instrumentation-bom-alpha</artifactId>
            <version>${opentelemetry-instrumentation-bom-alpha.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-spring-boot-starter</artifactId>
</dependency>
```

The `opentelemetry-spring-boot-starter` dependency enables auto instrumentation of Spring Web (RestTemplate), Web MVC (Controllers) and WebFlux (WebClient). Unlike the java agent, instrumentation of other libraries (like JDBC, Kafka etc) needs to be enabled explicitly.

Since in this example, we also use Apache Kafka to produce and consume events and a database to store the records, we need to add additional instrumentation libraries to trace these technologies.

```xml
<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-kafka-clients-2.6</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-spring-kafka-2.7</artifactId>
</dependency>
<dependency>
  <groupId>io.opentelemetry.instrumentation</groupId>
  <artifactId>opentelemetry-jdbc</artifactId>
</dependency>
```

For database instrumentation, we must add below entry in `application.properties`:

```properties
spring.datasource.url=jdbc:otel:h2:mem:db
spring.datasource.driver-class-name=io.opentelemetry.instrumentation.jdbc.OpenTelemetryDriver
```

The datasource URL must be prefixed with `jdbc:otel:` and the JDBC driver must be set to `OpenTelemetryDriver`.

For Apache Kafka instrumentation, consumer is instrumented with Spring Kafka:

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(SslBundles sslBundles) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setRecordInterceptor(SpringKafkaTelemetry.create(openTelemetry).createRecordInterceptor());
    factory.setConsumerFactory(consumerFactory(sslBundles));
    return factory;
}
```

The producer is instrumented by creating a custom ProducerFactory that _wraps_ the producer with a tracing enabled producer. See [reference documentation](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/instrumentation/kafka/kafka-clients/kafka-clients-2.6/library#wrapping-clients).

### Building Application and Docker Images

Run below command to compile and build the docker images with Paketo buildpacks. Ensure you have GraalVM Java SDK deployed.

```shell
mvn spring-boot:build-image -Pnative
```

### Run Docker Compose setup

Run below command to start Jaeger and the Kafka services.

```shell
docker-compose up
```

### Trigger a request to `kafkaservice1`.

```shell
curl http://localhost:8080
```

### View Traces in Jaeger

Open Jaeger at `http://localhost:16686` and check the traces.

![image](https://github.com/xsreality/spring-boot-tracing-demo/assets/4991449/15e5abd4-e5a8-450c-b630-f7b84624e969)
