package fr.caassurances.kata.billing.domain.model;

import java.math.BigDecimal;

/**
 * Detailed line of an invoice for a specific product.
 * Shows the calculated taxes and final unit price.
 */
public record InvoiceLine(
        String label,
        int quantity,
        BigDecimal unitPriceInclTax, // Unit price after tax (TTC)
        BigDecimal totalTax          // Total taxes for this specific line
) {}
