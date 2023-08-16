package com.example.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@RestController
public class ConsumerController {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerController.class);
    private final WebClient webClient;
    private final ReactiveCircuitBreaker reactiveCircuitBreaker;

    public ConsumerController(ReactiveCircuitBreakerFactory circuitBreakerFactory){
        this.webClient = WebClient.builder().baseUrl("http://localhost:8091").build();
        this.reactiveCircuitBreaker = circuitBreakerFactory.create("recommendations");
    }

    @GetMapping("/")
    public Mono<String> consume() {
        return reactiveCircuitBreaker.run(webClient.get().uri("/").retrieve().bodyToMono(String.class),
            throwable -> {
                logger.warn("Error al hacer la solicitud " + throwable.getMessage());
                return Mono.just("Static Value: Cloud Native Java");
            }
        );
    }
}
