package com.n2.learnreactivespring.fluxAndMonoPlayground;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("adam","anna","jack","jenny");
    @Test
    public void fluxUsingIterable() {
        Flux<String> namesFlux = Flux.fromIterable(names);
        StepVerifier.create(namesFlux.log())
                .expectNext("adam","anna","jack","jenny")
                .verifyComplete();
    }
    @Test
    public void fluxUsingArray() {
        String[] names = new String[]{"adam","anna","jack","jenny"};
        final Flux<String> stringFlux = Flux.fromArray(names);
        StepVerifier.create(stringFlux.log())
                .expectNext("adam","anna","jack","jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStreams() {
        Flux<String> namesFlux = Flux.fromStream(names.stream());
        StepVerifier.create(namesFlux.log())
                .expectNext("adam","anna","jack","jenny")
                .verifyComplete();
    }

    @Test
    public void monuUsingJustOrEmpty() {
        final Mono<Object> mono = Mono.justOrEmpty(null);//Mono.Empty()'
        StepVerifier.create(mono.log())
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {
        Supplier<String>  stringSupplier = () -> "adam";
        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);
        StepVerifier.create(stringMono.log())
                .expectNext("adam")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {
        final Flux<Integer> integerFlux = Flux.range(1, 5).log();
        StepVerifier.create(integerFlux)
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }
}
