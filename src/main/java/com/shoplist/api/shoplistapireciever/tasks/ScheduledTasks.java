package com.shoplist.api.shoplistapireciever.tasks;

import com.shoplist.api.shoplistapireciever.repository.ProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private final ProductRepository productRepository;

    public ScheduledTasks(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Scheduled(cron = "0 5 0 * * *", zone = "Europe/Moscow")
    public void reportCurrentTime() {
        productRepository.removeOldProducts();
    }
}
