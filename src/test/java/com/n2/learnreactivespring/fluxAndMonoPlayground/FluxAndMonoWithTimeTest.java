package com.n2.learnreactivespring.fluxAndMonoPlayground;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoWithTimeTest {
    @Test
    public void infiniteSequence() throws InterruptedException {
        final Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200)).log();//emits from 0 to infinite
        infiniteFlux.subscribe((element) -> System.out.println("Value is "+element));
        Thread.sleep(3000);
    }
    @Test
    public void infiniteSequenceTest() throws InterruptedException {
        final Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200))
                .take(3)
                .log();//emits from 0 to infinite
        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0l,1l,2l)
                .verifyComplete();
    }
    @Test
    public void infiniteSequenceMap() throws InterruptedException {
        final Flux<Integer> infiniteFlux = Flux.interval(Duration.ofMillis(200))
                .delayElements(Duration.ofSeconds(1))
                .map(l-> l.intValue())
                .take(3)
                .log();//emits from 0 to infinite
        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0,1,2)
                .verifyComplete();
    }
}
