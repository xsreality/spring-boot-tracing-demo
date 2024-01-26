# Observability in Spring Boot 3 with Micrometer Tracing and OpenTelemetry Auto Instrumentation

This project is a quick example to showcase Distributed Tracing in a Spring Boot application using Micrometer and OpenTelemetry.

There are 2 tracing scenarios - HTTP and Apache Kafka.

## Services communicating over HTTP

![image](https://user-images.githubusercontent.com/4991449/140835345-a2af5646-2488-456d-9296-7baa21b06028.png)

1. HTTP Service 1 makes a GET call to HTTP Service 2.
2. HTTP Service 2 makes a GET call to whatthecommit.com.
3. HTTP Service 2 returns the response from whatthecommit.com back to HTTP Service 1.

## Services communicating over Apache Kafka

![image](https://user-images.githubusercontent.com/4991449/140835427-c652c835-c90c-4864-9014-fcf5a45727b7.png)

1. Kafka Producer makes a GET call to whatthecommit.com.
2. Kafka Producer produces the response from Step 1 to Apache Kafka.
3. Kafka Consumer consumes the message from Apache Kafka.
4. After consumption, Kafka Consumer store the record in H2 in-memory DB.

## Project Structure

For each scenario, there are two implementations - Micrometer Tracing and OpenTelemetry Auto Instrumentation.

1. The folder [otel-http-micrometer](https://github.com/xsreality/spring-boot-tracing-demo/tree/main/otel-http-micrometer) demonstrates Micrometer tracing of HTTP services communication.
2. The folder [otel-http-opentelemetry](https://github.com/xsreality/spring-boot-tracing-demo/tree/main/otel-http-opentelemetry) demonstrates OTEL Auto Instrumentation tracing of HTTP services communication.
3. The folder [otel-kafka-micrometer](https://github.com/xsreality/spring-boot-tracing-demo/tree/main/otel-kafka-micrometer) demonstrates Micrometer tracing of Kafka services communication.
4. The folder [otel-kafka-opentelemetry](https://github.com/xsreality/spring-boot-tracing-demo/tree/main/otel-kafka-opentelemetry) demonstrates OTEL Auto Instrumentation tracing of Kafka services communication.
5. The folder [otel-http-micrometer-native](https://github.com/xsreality/spring-boot-tracing-demo/tree/main/otel-http-micrometer-native) demonstrates Micromter tracing of HTTP services communication with Spring native GraalVM.
6. The folder [otel-http-opentelemetry-native](https://github.com/xsreality/spring-boot-tracing-demo/tree/main/otel-http-opentelemetry-native) demonstrates OTEL tracing of HTTP services communication with Spring native GraalVM without using the Java agent.
7. The folder [otel-kafka-opentelemetry-native](https://github.com/xsreality/spring-boot-tracing-demo/tree/main/otel-kafka-opentelemetry-native) demonstrates OTEL tracing of Kafka and DB services communication with Spring native GraalVM without using the Java agent.

Visit the folder to get more details about building and running the application.
