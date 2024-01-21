## Observability of Kafka services with OpenTelemetry Auto Instrumentation

![image](https://user-images.githubusercontent.com/4991449/140835427-c652c835-c90c-4864-9014-fcf5a45727b7.png)

### Building Application and Docker Images

Run below command to compile and build the docker images with Paketo buildpacks.

```shell
mvn spring-boot:build-image
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

![image](https://github.com/xsreality/spring-boot-tracing-demo/assets/4991449/ae44bf63-1fff-4284-990f-865891e5c5d2)
