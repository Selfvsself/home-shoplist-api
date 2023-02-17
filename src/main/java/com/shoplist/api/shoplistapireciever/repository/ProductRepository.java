package com.shoplist.api.shoplistapireciever.repository;

import com.shoplist.api.shoplistapireciever.model.Product;
import com.shoplist.api.shoplistapireciever.model.ProductRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final String TABLE_NAME = "products";

    public ProductRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Product> getAllProductForUser(UUID ownerId) {
        String SQL = String.format("SELECT * FROM %s WHERE owner = :ownerId", TABLE_NAME);
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("ownerId", ownerId);
        List<Product> products = jdbcTemplate.query(SQL, namedParameters, new ProductRowMapper());
        return products;
    }

    public Product addProduct(Product product, UUID owner) {
        Product result = Product.builder()
                .owner(owner)
                .title(product.getTitle())
                .category(product.getCategory())
                .price(product.getPrice())
                .build();
        String sql = String.format(
                "INSERT INTO %s (id, owner, title, category, price, created, modified) VALUES " +
                        "(:id, :owner, :title, :category, :price, :created, :modified)"
                , TABLE_NAME);
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("id", result.getId());
        namedParameters.addValue("owner", result.getOwner());
        namedParameters.addValue("title", result.getTitle());
        namedParameters.addValue("category", result.getCategory());
        namedParameters.addValue("price", result.getPrice());
        namedParameters.addValue("created", Timestamp.from(result.getCreated().toInstant()));
        namedParameters.addValue("modified", Timestamp.from(result.getModified().toInstant()));

        jdbcTemplate.update(sql, namedParameters);
        return result;
    }

    public Product removeProduct(UUID productId, UUID userId) throws NullPointerException {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("id", productId);
        namedParameters.addValue("ownerId", userId);

        Product result = getProductById(productId, userId).orElseThrow(
                () -> new NullPointerException(String.format("Not message with id %s", productId.toString())));

        String sql = String.format(
                "DELETE FROM %s WHERE id = :id AND owner = :ownerId"
                , TABLE_NAME);

        jdbcTemplate.update(sql, namedParameters);
        return result;
    }

    public Optional<Product> getProductById(UUID productId, UUID userId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("id", productId);
        namedParameters.addValue("ownerId", userId);

        String select = String.format(
                "SELECT * FROM %s WHERE id = :id AND owner = :ownerId"
                , TABLE_NAME);

        Product result = jdbcTemplate.query(select, namedParameters, new ProductRowMapper()).get(0);
        return Optional.of(result);
    }

    public Product updateProduct(Product product, UUID userId) {
        getProductById(product.getId(), userId)
                .orElseThrow(() -> new NullPointerException(String.format("Not message with id %s", product.getId().toString())));

        String update = String.format("UPDATE %s SET " +
                "(title, category, price, modified) = " +
                "(:title, :category, :price, :modified)" +
                " WHERE id = :id AND owner = :ownerId", TABLE_NAME);

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("id", product.getId());
        namedParameters.addValue("ownerId", product.getOwner());
        namedParameters.addValue("title", product.getTitle());
        namedParameters.addValue("category", product.getCategory());
        namedParameters.addValue("price", product.getPrice());
        namedParameters.addValue("modified", Timestamp.from(ZonedDateTime.now().toInstant()));

        jdbcTemplate.update(update, namedParameters);

        Product result = getProductById(product.getId(), userId).orElseThrow(
                () -> new NullPointerException(String.format("Not message with id %s", product.getId().toString())));
        return result;
    }

    public void removeOldProducts() {
        String sql = String.format(
                "DELETE FROM %s WHERE price > 0 AND modified < :date"
                , TABLE_NAME);

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("date", new Date(System.currentTimeMillis()));
        jdbcTemplate.update(sql, namedParameters);
    }
}
