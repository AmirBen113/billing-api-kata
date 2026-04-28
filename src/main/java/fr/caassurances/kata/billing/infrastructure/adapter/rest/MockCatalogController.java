package fr.caassurances.kata.billing.infrastructure.adapter.rest;

import fr.caassurances.kata.billing.domain.model.Product;
import fr.caassurances.kata.billing.domain.model.ProductType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/external-api")
@Tag(name = "Mock Catalog", description = "External mocked API representing the product repository")
public class MockCatalogController {

    private static final List<Product> GENERATED_CATALOG = new ArrayList<>();
    private boolean isDown = false;

    static {
        Random random = new Random();
        ProductType[] types = ProductType.values();
        String[] names = {"Eco", "Premium", "Deluxe", "Standard", "Basic", "Master", "Ultra"};

        for (int i = 1; i <= 100; i++) {
            ProductType type = types[random.nextInt(types.length)];
            String name = names[random.nextInt(names.length)] + " " + type.name().toLowerCase() + " item #" + i;

            BigDecimal price = BigDecimal.valueOf(1 + (500 - 1) * random.nextDouble())
                    .setScale(2, RoundingMode.HALF_UP);

            boolean isImported = random.nextBoolean();

            GENERATED_CATALOG.add(new Product(i, name, type, price, isImported));
        }
    }

    @GetMapping("/products")
    @Operation(summary = "Get full product catalog",
            description = "Returns a generated list of 100 products for performance testing.")
    public ResponseEntity<List<Product>> getProducts() {
        if (isDown) {
            return ResponseEntity.status(503).build();
        }
        return ResponseEntity.ok(GENERATED_CATALOG);
    }

    @PostMapping("/toggle")
    @Operation(summary = "Simulate API failure", description = "Toggle between 200 OK and 503 Service Unavailable")
    public String toggle() {
        this.isDown = !this.isDown;
        return "API is now " + (isDown ? "DOWN" : "UP");
    }
}