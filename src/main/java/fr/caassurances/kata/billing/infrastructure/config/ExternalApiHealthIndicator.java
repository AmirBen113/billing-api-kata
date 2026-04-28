package fr.caassurances.kata.billing.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExternalApiHealthIndicator implements HealthIndicator {

    @Value("${app.external-api.catalog-url}")
    private String catalogUrl;

    @Override
    public Health health() {
        try {
            // Simple ping to the base URL
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(catalogUrl + "/products", String.class);
            return Health.up().withDetail("url", catalogUrl).build();
        } catch (Exception e) {
            return Health.down().withDetail("error", "Catalog API unreachable").build();
        }
    }
}
