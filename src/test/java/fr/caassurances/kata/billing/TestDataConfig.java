package fr.caassurances.kata.billing;

import fr.caassurances.kata.billing.domain.model.Product;
import fr.caassurances.kata.billing.domain.model.ProductType;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;
import java.util.List;

@TestConfiguration
public class TestDataConfig {

    @Bean
    @Primary
    public List<Product> testCatalog() {
        return List.of(
                new Product(1, "Rice", ProductType.FOOD, new BigDecimal("10.00"), false),
                new Product(4, "Java Book", ProductType.BOOK, new BigDecimal("30.00"), false)
        );
    }
}