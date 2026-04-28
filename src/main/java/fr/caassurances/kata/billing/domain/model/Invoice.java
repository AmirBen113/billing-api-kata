package fr.caassurances.kata.billing.domain.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Final invoice containing detailed calculations for each product and totals.
 */
public record Invoice(
        List<InvoiceLine> lines,
        BigDecimal totalExclTax, // Total Price Excluding Tax (HT)
        BigDecimal totalTax,     // Total Amount of taxes
        BigDecimal totalInclTax  // Total Price Including Tax (TTC)
) {}
