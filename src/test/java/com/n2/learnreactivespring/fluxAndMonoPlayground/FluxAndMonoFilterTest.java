package com.n2.learnreactivespring.fluxAndMonoPlayground;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoFilterTest {
    List<String> names = Arrays.asList("adam","anna","jack","jenny");
    @Test
    public void filterTest() {
        final Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(s->s.startsWith("a"))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("adam", "anna")
                .verifyComplete();

    }
    @Test
    public void filterTestLength() {
        final Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(s->s.length()>4)
                .log();//jenny
        StepVerifier.create(stringFlux)
                .expectNext("jenny")
                .verifyComplete();

    }
}
