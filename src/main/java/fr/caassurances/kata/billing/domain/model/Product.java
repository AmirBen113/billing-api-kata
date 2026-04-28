package fr.caassurances.kata.billing.domain.model;

import java.math.BigDecimal;

/**
 * Domain model representing a product.
 * Uses Java Record for immutability and concise data handling.
 *
 * @param id Unique identifier of the product
 * @param label Product description
 * @param type Category used for tax calculation
 * @param priceExclTax Net price before taxes
 * @param imported Flag for additional import tax
 */
public record Product (
        int id,
        String label,
        ProductType type,
        BigDecimal priceExclTax,
        boolean imported
){}

