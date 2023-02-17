package com.shoplist.api.shoplistapireciever.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

public class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = UUID.fromString(rs.getObject("id").toString());
        UUID owner = UUID.fromString(rs.getObject("owner").toString());
        String title = rs.getString("title");
        String category = rs.getString("category");
        Integer price = rs.getInt("price");
        ZonedDateTime created = rs.getTimestamp("created").toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime modified = rs.getTimestamp("modified").toInstant().atZone(ZoneId.systemDefault());
        return Product.builder()
                .id(id)
                .owner(owner)
                .title(title)
                .category(category)
                .price(price)
                .created(created)
                .modified(modified)
                .build();
    }
}
