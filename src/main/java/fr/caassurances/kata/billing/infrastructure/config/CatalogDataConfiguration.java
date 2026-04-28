package fr.caassurances.kata.billing.infrastructure.config;

import fr.caassurances.kata.billing.domain.model.Product;
import fr.caassurances.kata.billing.domain.model.ProductType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class CatalogDataConfiguration {

    @Bean
    public List<Product> defaultCatalog() {
        List<Product> generatedCatalog = new ArrayList<>();
        Random random = new Random();
        ProductType[] types = ProductType.values();
        String[] names = {"Eco", "Premium", "Deluxe", "Standard", "Basic", "Master", "Ultra"};

        for (int i = 1; i <= 1000; i++) {
            ProductType type = types[random.nextInt(types.length)];
            String name = names[random.nextInt(names.length)] + " " + type.name().toLowerCase() + " item #" + i;

            BigDecimal price = BigDecimal.valueOf(1 + (500 - 1) * random.nextDouble())
                    .setScale(2, RoundingMode.HALF_UP);

            boolean isImported = random.nextBoolean();
            generatedCatalog.add(new Product(i, name, type, price, isImported));
        }
        return generatedCatalog;
    }
}