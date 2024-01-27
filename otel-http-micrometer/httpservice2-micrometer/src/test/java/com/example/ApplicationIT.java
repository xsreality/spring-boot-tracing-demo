package com.example;

import com.github.tomakehurst.wiremock.http.Body;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.tck.MeterRegistryAssert;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Span.Kind;
import io.micrometer.tracing.test.SampleTestRunner;
import io.micrometer.tracing.test.simple.SpansAssert;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.micrometer.tracing.test.SampleTestRunner.TracingSetup.IN_MEMORY_OTEL;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringBootApplicationProperties")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "whatthecommit.url=http://localhost:${wiremock.server.port}",
        })
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
class ApplicationIT extends SampleTestRunner {

    private static final String COMMIT_MESSAGE = "A funny commit message";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObservationRegistry observationRegistry;

    @Autowired
    MeterRegistry meterRegistry;

    @Override
    protected ObservationRegistry createObservationRegistry() {
        return this.observationRegistry;
    }

    @Override
    protected MeterRegistry createMeterRegistry() {
        return this.meterRegistry;
    }

    @Override
    public TracingSetup[] getTracingSetup() {
        return new TracingSetup[]{IN_MEMORY_OTEL};
    }

    @Override
    public SampleTestRunnerConsumer yourCode() {

        return (bb, meterRegistry) -> {
            stubFor(get(urlEqualTo("/"))
                    .willReturn(aResponse()
                            .withStatus(SC_OK)
                            .withResponseBody(new Body(COMMIT_MESSAGE)))
            );

            this.mockMvc.perform(MockMvcRequestBuilders.get("/"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString(COMMIT_MESSAGE)));

            SpansAssert.assertThat(bb.getFinishedSpans())
                    .haveSameTraceId()
                    .hasNumberOfSpansEqualTo(3)
                    .thenASpanWithNameEqualTo("http get")
                    .hasKindEqualTo(Kind.CLIENT)
                    .hasTag("method", "GET")
                    .hasTag("status", "200")
                    .backToSpans()
                    .thenASpanWithNameEqualTo("fetch-commit")
                    .hasTag("commit.message", COMMIT_MESSAGE)
                    .hasEventWithNameEqualTo("commit-fetched")
                    .backToSpans()
                    .thenASpanWithNameEqualTo("http get /")
                    .hasKindEqualTo(Kind.SERVER)
                    .hasTag("method", "GET")
                    .hasTag("http.url", "/")
                    .hasTag("status", "200");

            MeterRegistryAssert.assertThat(meterRegistry)
                    .hasTimerWithNameAndTags("fetch-commit", Tags.of(Tag.of("error", "none")))
                    .hasMeterWithName("fetch-commit.commit-fetched");
        };
    }
}
