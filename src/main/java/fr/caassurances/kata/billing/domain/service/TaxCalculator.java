package fr.caassurances.kata.billing.domain.service;

import fr.caassurances.kata.billing.domain.model.Product;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TaxCalculator {

    // Defined rounding step: 0.05 as per requirements
    private static final BigDecimal ROUNDING_STEP = new BigDecimal("0.05");

    @Cacheable(value = "taxes", key = "#a0.id()", condition = "#a0 != null")
    public BigDecimal calculatePriceInclTax(Product product) {
        //simulateLatency();

        BigDecimal ht = product.priceExclTax();
        BigDecimal vATRate = switch (product.type()) {
            case FOOD, MEDICINE -> BigDecimal.ZERO;
            case BOOK -> new BigDecimal("0.10");
            default -> new BigDecimal("0.20");
        };

        BigDecimal vATAmount = round(ht.multiply(vATRate));
        BigDecimal importTaxAmount = product.imported() ? round(ht.multiply(new BigDecimal("0.05"))) : BigDecimal.ZERO;

        return ht.add(vATAmount).add(importTaxAmount);
    }

    /**
     * Rounds the tax amount to the nearest 0.05 upwards.
     * Rule: ceil(value / 0.05) * 0.05
     */
    public BigDecimal round(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return value.divide(ROUNDING_STEP, 0, RoundingMode.UP)
                .multiply(ROUNDING_STEP)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void simulateLatency() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}