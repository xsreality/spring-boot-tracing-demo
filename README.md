# Distributed Tracing in Spring Boot 3 with Micrometer Tracing and OpenTelemetry Auto Instrumentation

This project is a quick example to showcase Distributed Tracing in a Spring Boot application using Micrometer and OpenTelemetry.

There are 2 tracing scenarios - HTTP and Apache Kafka

## Services communicating over HTTP

![image](https://user-images.githubusercontent.com/4991449/140835345-a2af5646-2488-456d-9296-7baa21b06028.png)

1. HTTP Service 1 makes a GET call to HTTP Service 2.
2. HTTP Service 2 makes a GET call to whatthecommit.com.
3. HTTP Service 2 returns the response from whatthecommit.com back to HTTP Service 1.

### Observability with Micrometer Tracing

1. Stay on the `main` branch.

2. Change to `otel-http` folder: 
    ```shell
    cd otel-http
    ```

3. Compile and build the docker image of http services.
    ```shell
    mvn spring-boot:build-image
    ```

4. Run the docker-compose setup. This will start Jaeger and the HTTP services.
    ```shell
    docker-compose up
    ```

5. Call the httpservice1.
    ```shell
    curl http://localhost:8080
    ```

6. Open Jaeger at `http://localhost:16686` and check the traces.

![image](https://github.com/xsreality/spring-boot-tracing-demo/assets/4991449/e72b4f61-f53e-41ee-a436-fe5536a0d3ab)

### Observability with OpenTelemetry Auto Instrumentation

1. Switch to `spring-boot-3-opentelemetry` branch.

2. Change to `otel-http` folder:
    ```shell
    cd otel-http
    ```

3. Compile and build the docker image of http services.
    ```shell
    mvn spring-boot:build-image
    ```

4. Run the docker-compose setup. This will start Jaeger and the HTTP services.
    ```shell
    docker-compose up
    ```

5. Call the httpservice1.
    ```shell
    curl http://localhost:8080
    ```

6. Open Jaeger at `http://localhost:16686` and check the traces.

![image](https://github.com/xsreality/spring-boot-tracing-demo/assets/4991449/2ebba88e-ace2-44f0-9f7e-4c2131ebee6c)


## Services communicating over Apache Kafka

![image](https://user-images.githubusercontent.com/4991449/140835427-c652c835-c90c-4864-9014-fcf5a45727b7.png)

1. Kafka Producer makes a GET call to whatthecommit.com.
2. Kafka Producer produces the response from Step 1 to Apache Kafka.
3. Kafka Consumer consumes the message from Apache Kafka.
4. After consumption, Kafka Consumer store the record in H2 in-memory DB.

### Observability with Micrometer Tracing

1. Stay on the `main` branch.

2. Change to `otel-kafka` folder:
    ```shell
    cd otel-kafka
    ```

3. Compile and build the docker image of kafka services.
    ```shell
    mvn spring-boot:build-image
    ```

4. Run the docker-compose setup. This will start a 1-node Kafka cluster, Jaeger and the Kafka producer/consumer services.
    ```shell
    docker-compose up
    ```

5. Call the kafkaservice1.
    ```shell
    curl http://localhost:8080
    ```

6. Open Jaeger at `http://localhost:16686` and check the traces.

![image](https://github.com/xsreality/spring-boot-tracing-demo/assets/4991449/f94a0057-e5d3-4cb7-8205-325168c0212a)

### Observability with OpenTelemetry Auto Instrumentation

1. Switch to `spring-boot-3-opentelemetry` branch.

2. Change to `otel-kafka` folder:
    ```shell
    cd otel-kafka
    ```

3. Compile and build the docker image of http services.
    ```shell
    mvn spring-boot:build-image
    ```

4. Run the docker-compose setup. This will start a 1-node Kafka cluster, Jaeger and the Kafka producer/consumer services.
    ```shell
    docker-compose up
    ```

5. Call the kafkaservice1.
    ```shell
    curl http://localhost:8080
    ```

6. Open Jaeger at `http://localhost:16686` and check the traces.

![image](https://github.com/xsreality/spring-boot-tracing-demo/assets/4991449/ae44bf63-1fff-4284-990f-865891e5c5d2)
