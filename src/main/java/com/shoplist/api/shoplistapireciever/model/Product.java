package com.shoplist.api.shoplistapireciever.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
public class Product {
    private static final String DEFAULT_CATEGORY = "other";

    @Builder.Default
    private UUID id = UUID.randomUUID();
    private UUID owner;
    private String title;
    @Builder.Default
    private String category = "other";
    @Builder.Default
    private int price = 0;
    @Builder.Default
    private ZonedDateTime created = ZonedDateTime.now();
    @Builder.Default
    private ZonedDateTime modified = ZonedDateTime.now();

    public String getCategory() {
        return StringUtils.hasLength(category) ? category : DEFAULT_CATEGORY;
    }
}
