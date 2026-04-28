package fr.caassurances.kata.billing.infrastructure.adapter.client;

import fr.caassurances.kata.billing.domain.model.Product;
import fr.caassurances.kata.billing.domain.ports.ProductRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Adapter implementation for fetching product data from an external REST API.
 * This class implements the Outbound Port defined in the domain layer.
 */
@Component
public class CatalogClient implements ProductRepository {

    private static final Logger log = LoggerFactory.getLogger(CatalogClient.class);
    private final WebClient webClient;

    public CatalogClient(WebClient.Builder webClientBuilder,
                          @Value("${app.external-api.catalog-url}") String catalogUrl) {
        this.webClient = webClientBuilder.baseUrl(catalogUrl).build();
    }

    /**
     * Fetches the complete product catalog from the external mocked API.
     * Integrated with a Circuit Breaker to ensure system resilience if the external service fails.
     *
     * @return List of products from the catalog
     */
    @Override
    @CircuitBreaker(name = "catalogService", fallbackMethod = "getFallbackProducts")
    @Cacheable(value = "catalog")
    public List<Product> fetchAllProducts() {
        return webClient.get()
                .uri("/products")
                .retrieve()
                .bodyToFlux(Product.class)
                .collectList()
                .block(); // Blocking call to maintain compatibility with the synchronous domain service
    }

    /**
     * Fallback method triggered when the Circuit Breaker is open or an error occurs.
     * Prevents cascading failures by returning a safe default response.
     *
     * @param e The exception that triggered the fallback
     * @return An empty list or a default set of products
     */
    public List<Product> getFallbackProducts(Exception e) {
        log.error("External Catalog API is unavailable or failing. Circuit Breaker triggered fallback. Error: {}", e.getMessage());
        return List.of();
    }
}