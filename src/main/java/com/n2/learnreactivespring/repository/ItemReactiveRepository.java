package com.n2.learnreactivespring.repository;

import com.n2.learnreactivespring.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {
    Flux<Item> findByDescription(String description);
}
