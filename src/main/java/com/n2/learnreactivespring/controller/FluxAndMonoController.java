package com.n2.learnreactivespring.controller;

import java.time.Duration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> returnFlux() {
        return Flux.just(1,2,3,4,5)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

}