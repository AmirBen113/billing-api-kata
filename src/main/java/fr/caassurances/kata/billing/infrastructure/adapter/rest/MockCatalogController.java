package fr.caassurances.kata.billing.infrastructure.adapter.rest;

import fr.caassurances.kata.billing.domain.model.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/external-api")
@Tag(name = "Mock Catalog", description = "External mocked API representing the product repository")
public class MockCatalogController {

    private final List<Product> products;
    private boolean isDown = false;

    public MockCatalogController(List<Product> products) {
        this.products = products;
    }

    @GetMapping("/products")
    @Operation(summary = "Get full product catalog",
            description = "Returns a generated list of 100 products for performance testing.")
    public ResponseEntity<List<Product>> getProducts() {
        if (isDown) {
            return ResponseEntity.status(503).build();
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping("/toggle")
    @Operation(summary = "Simulate API failure", description = "Toggle between 200 OK and 503 Service Unavailable")
    public String toggle() {
        this.isDown = !this.isDown;
        return "API is now " + (isDown ? "DOWN" : "UP");
    }
}