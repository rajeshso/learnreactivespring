package com.n2.learnreactivespring.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest
public class FluxAndMonoControllerTest {

    @Autowired WebTestClient webTestClient;

    @Test
    public void flux_approach1() {
        final Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();
        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }

    @Test
    public void fluxApproach2() {
        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Integer.class)
                .hasSize(5);
    }

    @Test
    public void fluxApproach3() {
        final List<Integer> expectedIntegers = Arrays.asList(1, 2, 3, 4, 5);
        final EntityExchangeResult<List<Integer>> listEntityExchangeResult = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();
        assertEquals(expectedIntegers, listEntityExchangeResult.getResponseBody());
    }

    @Test
    public void fluxApproach4() {
        final List<Integer> expectedIntegers = Arrays.asList(1, 2, 3, 4, 5);
        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith((response -> {
                    assertEquals(expectedIntegers, response.getResponseBody());
                }
        ));
    }

    @Test
    public void fluxStream() {
        final Flux<Long> longStreamFlux = webTestClient.get().uri("/fluxstream")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();
        StepVerifier.create(longStreamFlux)
                .expectNext(0l)
                .expectNext(1l)
                .expectNext(2l)
                .thenCancel()
                .verify();
    }

    @Test
    public void mono() {
        Integer expectedValue = Integer.valueOf(1);

        webTestClient.get().uri("/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith((result)->{
                    assertEquals(expectedValue, result.getResponseBody());
                });
    }
}
