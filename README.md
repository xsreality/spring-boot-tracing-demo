# Distributed Tracing in Spring Boot 3 with Micrometer Tracing and OpenTelemetry Auto Instrumentation

This project is a quick example to showcase Distributed Tracing in a Spring Boot application using Micrometer and OpenTelemetry.

There are 2 tracing scenarios - HTTP and Apache Kafka

### Services communicating over HTTP

![image](https://user-images.githubusercontent.com/4991449/140835345-a2af5646-2488-456d-9296-7baa21b06028.png)

1. HTTP Service 1 makes a GET call to HTTP Service 2.
2. HTTP Service 2 makes a GET call to whatthecommit.com.
3. HTTP Service 2 returns the response from whatthecommit.com back to HTTP Service 1.

### Services communicating over Apache Kafka

![image](https://user-images.githubusercontent.com/4991449/140835427-c652c835-c90c-4864-9014-fcf5a45727b7.png)

1. Kafka Producer makes a GET call to whatthecommit.com.
2. Kafka Producer produces the response from Step 1 to Apache Kafka.
3. Kafka Consumer consumes the message from Apache Kafka.
4. After consumption, Kafka Consumer store the record in H2 in-memory DB.

## Start Zipkin

Run below command to start Zipkin.

```bash
docker run --rm --name jaeger \
  -e COLLECTOR_ZIPKIN_HOST_PORT=:9411 \
  -p 6831:6831/udp \
  -p 6832:6832/udp \
  -p 5778:5778 \
  -p 16686:16686 \
  -p 4317:4317 \
  -p 4318:4318 \
  -p 14250:14250 \
  -p 14268:14268 \
  -p 14269:14269 \
  -p 9411:9411 \
  jaegertracing/all-in-one:1.53

```

## Micrometer Tracing

The `main` branch uses Micrometer Tracing library to instrument the codebase.

Build the modules with `mvn compile package`.

Run `http-service1` and `http-service2` with below command at the root of the repository:

```bash
java -jar otel-http/httpservice1/target/httpservice1-0.0.1-SNAPSHOT.jar
java -jar otel-http/httpservice2/target/httpservice2-0.0.1-SNAPSHOT.jar
```

Make a curl request to Service 1: `curl http://localhost:8080`.

Access Jaeger UI at http://localhost:16686

![image](https://github.com/xsreality/spring-boot-tracing-demo/assets/4991449/d61a2ff6-2869-451c-9f30-1f758e71e745)
