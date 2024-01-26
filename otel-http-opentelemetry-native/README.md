## Observability of HTTP services with OpenTelemetry Instrumentation and Spring Boot Native GraalVM

![image](https://user-images.githubusercontent.com/4991449/140835345-a2af5646-2488-456d-9296-7baa21b06028.png)

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

### Building Application and Docker Images

Run below command to compile and build the docker images with Paketo buildpacks. Ensure you have GraalVM Java SDK deployed.

```shell
mvn spring-boot:build-image -Pnative
```

### Run Docker Compose setup

Run below command to start Jaeger and the HTTP services.

```shell
docker-compose up
```

### Trigger a request to `httpservice1`.

```shell
curl http://localhost:8080
```

### View Traces in Jaeger

Open Jaeger at `http://localhost:16686` and check the traces.

![image](https://github.com/xsreality/spring-boot-tracing-demo/assets/4991449/2ebba88e-ace2-44f0-9f7e-4c2131ebee6c)
