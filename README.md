# Distributed Tracing with Spring Cloud Sleuth and OpenTelemetry

This project is a quick example to showcase Distributed Tracing in a Spring Boot application using Spring Cloud Sleuth and OpenTelemetry.

There are 2 tracing scenarios - HTTP and Apache Kafka

### Services communicating over HTTP

![image](https://user-images.githubusercontent.com/4991449/140835345-a2af5646-2488-456d-9296-7baa21b06028.png)

> **NOTE:** This example uses Spring Boot 2.5, Spring Cloud 2020.0.4 and Spring Cloud Sleuth OTel 1.0.0-M12 (not yet GA)

1. HTTP Service 1 makes a GET call to HTTP Service 2. 
2. HTTP Service 2 makes a GET call to whatthecommit.com.
3. HTTP Service 2 returns the response from whatthecommit.com back to HTTP Service 1.

### Services communicating over Apache Kafka

![image](https://user-images.githubusercontent.com/4991449/140835427-c652c835-c90c-4864-9014-fcf5a45727b7.png)

> **NOTE:** This example uses Spring Boot 2.6.0-RC1, Spring Cloud 2021.0.0-M2 and Spring Cloud Sleuth OTel 1.1.0-M3 (not yet GA)

1. Kafka Producer makes a GET call to whatthecommit.com.
2. Kafka Producer produces the response from Step 1 to Apache Kafka.
3. Kafka Consumer consumes the message from Apache Kafka.
4. After consumption, Kafka Consumer store the record in H2 in-memory DB.
