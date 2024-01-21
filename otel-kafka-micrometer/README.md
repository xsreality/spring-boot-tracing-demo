## Observability of Kafka services with Micrometer Tracing

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

![image](https://github.com/xsreality/spring-boot-tracing-demo/assets/4991449/f94a0057-e5d3-4cb7-8205-325168c0212a)
