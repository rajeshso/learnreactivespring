package com.n2.learnreactivespring.fluxAndMonoPlayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest
{
    @Test
    public void fluxTest() {
        final Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("After error"))
                .log();
        stringFlux.subscribe(System.out::println, (e)->System.err.println(e), ()-> System.out.println("Completed"));
    }

    @Test
    public void fluxTestElements_WithoutError() {
        final Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring").log();
        StepVerifier.create(stringFlux)
        .expectNext("Spring")
        .expectNext("Spring Boot")
        .expectNext("Reactive Spring")
        .verifyComplete();
    }
    @Test
    public void fluxTestElements_WithError() {
        final Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
               // .expectError(RuntimeException.class)
                .expectErrorMessage("Exception occurred")
                .verify();
    }
    @Test
    public void fluxTestElements_WithError1() {
        final Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                // .expectError(RuntimeException.class)
                .expectErrorMessage("Exception occurred")
                .verify();
    }
    @Test
    public void fluxTestElementsCount() {
        final Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectErrorMessage("Exception occurred")
                .verify();
    }
    //Mono
    @Test
    public void monoTest() {
        final Mono<String> stringMono = Mono.just("Spring");
        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }
    @Test
    public void monoTest_Error() {

        StepVerifier.create(Mono.error(new RuntimeException("Exception occurred")).log())
                .expectError(RuntimeException.class)
                .verify();
    }
}
