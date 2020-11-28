package com.n2.learnreactivespring.fluxAndMonoPlayground;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

public class FluxAndMonoErrorTest {
    @Test
    public void fluxErrorHandling() {
        Flux<String> stringFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception Ocurred")))
                .concatWith(Flux.just("D"))
                .onErrorResume((e) ->
                {
                    System.err.println("Exception is :" +e.getMessage());
                    return Flux.just("default1","default2");
                });
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
               // .expectError(RuntimeException.class)
               //  .verify();
                .expectNext("default1","default2")
                .verifyComplete();
    }
    @Test
    public void fluxErrorHandling_onErrorReturn() {
        Flux<String> stringFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception Ocurred")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("default");
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                // .expectError(RuntimeException.class)
                //  .verify();
                .expectNext("default")
                .verifyComplete();
    }
    @Test
    public void fluxErrorHandling_onErrorMap() {
        Flux<String> stringFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception Ocurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap( (e)-> new CustomException(e));
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_onErrorMap_withRetry() {
        Flux<String> stringFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap( (e)-> new CustomException(e))
                .retry(2);
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                .expectError(CustomException.class)
                .verify();
    }
    @Test
    public void fluxErrorHandling_onErrorMap_withRetryBackoff() {
        Flux<String> stringFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap( (e)-> new CustomException(e))
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(5)));
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                .expectError(IllegalStateException.class)
                .verify();
    }
    private class CustomException extends Throwable {
        private String message;
        public CustomException(Throwable e) {
            this.message = e.getMessage();
        }

        @Override
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
