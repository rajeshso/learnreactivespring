package com.n2.learnreactivespring.fluxAndMonoPlayground;

import static java.lang.Thread.sleep;
import static reactor.core.scheduler.Schedulers.parallel;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoTransformTest {
    List<String> names = Arrays.asList("adam","anna","jack","jenny");

    @Test
    public void transformUsingMap() {
        final Flux<String> stringFlux = Flux.fromIterable(names)
                .log()
                .map(s-> s.toUpperCase())
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("ADAM","ANNA","JACK","JENNY")
                .verifyComplete();
    }
    @Test
    public void transformUsingMap_Length() {
        final Flux<Integer> stringFlux = Flux.fromIterable(names)
                .log()
                .map(s-> s.length())
                .log();
        StepVerifier.create(stringFlux)
                .expectNext(4,4,4,5)
                .verifyComplete();
    }
    @Test
    public void transformUsingMap_Length_repeat() {
        final Flux<Integer> stringFlux = Flux.fromIterable(names)
                .map(s-> s.length())
                .repeat(1)
                .log();
        StepVerifier.create(stringFlux)
                .expectNext(4,4,4,5,4,4,4,5)
                .verifyComplete();
    }
    @Test
    public void transformUsingMap_filter() {
        final Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(s -> s.length()>4)
                .map(s-> s.toUpperCase())
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s));
                }); //db or external call that returns another flux s-> Flux<String>
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    //Simulate a db call
    private List<String> convertToList(String s) {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }
    @Test
    public void transformUsingFlatMap_usingParallel() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))//Flux<String>
                .window(2) //Flux<Flux<String>> -> (A,B), (C,D), (E,F)
                .flatMap(s ->
                    s.map(this::convertToList).subscribeOn(parallel())
                ).flatMap(s-> Flux.fromIterable(s))//Flux<String>
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_usingParallel_maintainOrder() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))//Flux<String>
                .window(2) //Flux<Flux<String>> -> (A,B), (C,D), (E,F)
/*                .concatMap(s ->
                        s.map(this::convertToList).subscribeOn(parallel())
                ).concatMap(s-> Flux.fromIterable(s))//Flux<String>*/
                .flatMapSequential(s ->
                        s.map(this::convertToList).subscribeOn(parallel())
                ).concatMap(s-> Flux.fromIterable(s))//Flux<String>
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }
}
