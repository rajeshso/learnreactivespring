package com.n2.learnreactivespring.fluxAndMonoPlayground;

import java.time.Duration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

public class VirtualTimeTest {
    @Test
    public void testingWithoutVirtualTime() {
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);
        StepVerifier.create(longFlux.log())
                .expectSubscription()
                .expectNext(0l,1l,2l)
                .verifyComplete();
    }
    @Disabled
    public void testingWithVirtualTime() {
        VirtualTimeScheduler.getOrSet();// This will avoid the clock of the machine. So, it reduces the time to run the test
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);
        StepVerifier.withVirtualTime(()->longFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(2))
                .expectNext(0l,1l,2l)
                .verifyComplete();
    }
}
