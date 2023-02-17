package com.shoplist.api.shoplistapireciever;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShoplistApiRecieverApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoplistApiRecieverApplication.class, args);
    }

}
