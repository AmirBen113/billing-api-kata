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
import java.util.List;

@RestController
@RequestMapping("/external-api")
@Tag(name = "Mock Catalog", description = "External mocked API representing the product repository")
public class MockCatalogController {

    private boolean isDown = false;

    @GetMapping("/products")
    @Operation(summary = "Get full product catalog",
            description = "Returns the hardcoded list of products used for the Kata exercise.")
    public ResponseEntity<Object> getProducts() {
        if (isDown) {
            return ResponseEntity.status(503).build();
        }
        return ResponseEntity.ok(List.of(
                new Product(1, "Rice", ProductType.FOOD, new BigDecimal("15.00"), false),
                new Product(2, "Chocolate", ProductType.FOOD, new BigDecimal("5.50"), true),
                new Product(3, "Painkiller", ProductType.MEDICINE, new BigDecimal("12.00"), false),
                new Product(4, "Java Book", ProductType.BOOK, new BigDecimal("30.00"), true),
                new Product(5, "Dictionary", ProductType.BOOK, new BigDecimal("25.00"), false),
                new Product(6, "Perfume", ProductType.OTHER, new BigDecimal("80.00"), true),
                new Product(7, "Laptop", ProductType.OTHER, new BigDecimal("1200.00"), true),
                new Product(8, "Coffee", ProductType.FOOD, new BigDecimal("4.20"), false),
                new Product(9, "Notebook", ProductType.OTHER, new BigDecimal("3.00"), false),
                new Product(10, "Imported Wine", ProductType.OTHER, new BigDecimal("15.00"), true)
        ));
    }

    @PostMapping("/toggle")
    public String toggle() {
        this.isDown = !this.isDown;
        return "API is now " + (isDown ? "DOWN" : "UP");
    }
}