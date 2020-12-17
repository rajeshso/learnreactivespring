package com.n2.learnreactivespring.repository;

import com.n2.learnreactivespring.document.Item;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    private List<Item> items = Arrays.asList(
            new Item(null,"Samsung TV",400.00),
            new Item(null,"LG TV", 420.00),
            new Item(null, "Apple Watch",299.99),
            new Item(null, "Beats Headphone",149.99),
            new Item("1","Bose Headphones", 150.12));


    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Inserted item is "+item);
                }).blockLast();// block to ensure all the items are saved before the end of the method
    }

    @Disabled
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();

    }

    @Disabled
    public void getItemById() {
        StepVerifier.create(itemReactiveRepository.findById("1"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Bose Headphones"))
                .verifyComplete();
    }

    @Disabled
    public void findByDescriptionTest() {
        StepVerifier.create(itemReactiveRepository.findByDescription("Samsung TV").log())
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Samsung TV"))
                .verifyComplete();
    }

    @Test
    public void saveItemTest() {
        Item item = new Item(null, "Google home mini", 30.00);
        final Mono<Item> savedItem = itemReactiveRepository.save(item);
        StepVerifier.create(savedItem.log("saved item"))
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getId()!=null && item1.getDescription().equals("Google home mini"))
                .verifyComplete();
    }

    @Test
    public void updateItemTest() {
        double newPrice = 123.00;
        Flux<Item> samsung_tv = itemReactiveRepository.findByDescription("Samsung TV")
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap(item -> {
                    return itemReactiveRepository.save(item);
                });
       StepVerifier.create(samsung_tv)
            .expectSubscription()
            .expectNextMatches(item -> item.getPrice() == 123.00)
            .verifyComplete();
    }
    @Disabled
    public void deleteByIdTest() {
        final Mono<Void> deletedItem = itemReactiveRepository.findById("1")
                .map(Item::getId)
                .flatMap(id -> itemReactiveRepository.deleteById(id));
        //itemReactiveRepository.findById("1").flatMap(item -> itemReactiveRepository.delete(item));
        StepVerifier.create(deletedItem)
                .expectSubscription()
                .verifyComplete();
    }
}
