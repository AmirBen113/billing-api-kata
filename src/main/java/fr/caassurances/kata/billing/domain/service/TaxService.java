package fr.caassurances.kata.billing.domain.service;

import fr.caassurances.kata.billing.domain.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Service responsible for tax calculation and invoice generation.
 * Handles VAT rules, import taxes, and specific financial rounding.
 */
@Service
public class TaxService {

    // Defined rounding step: 0.05 as per requirements
    private static final BigDecimal ROUNDING_STEP = new BigDecimal("0.05");

    /**
     * Orchestrates the conversion of a Cart into a final Invoice.
     * It ensures that totals are the sum of individual rounded lines to maintain financial consistency.
     */
    public Invoice createInvoice(Cart cart) {
        // 1. Map each cart item to an invoice line with its specific tax calculations
        List<InvoiceLine> lines = cart.items().parallelStream()
                .map(this::mapToInvoiceLine)
                .toList();

        // 2. Aggregate totals based on the business rules
        BigDecimal totalExclTax = cart.items().stream()
                .map(item -> item.product().priceExclTax().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTax = lines.stream()
                .map(InvoiceLine::totalTax)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInclTax = lines.stream()
                .map(line -> line.unitPriceInclTax().multiply(BigDecimal.valueOf(line.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Invoice(lines, totalExclTax, totalTax, totalInclTax);
    }

    /**
     * Calculates the unit price including all taxes (TTC)
     * Formula: Pttc = Pht + round(Pht * tva/100) + round(Pht * ti/100)
     */
    public BigDecimal calculatePriceInclTax(Product product) {
        BigDecimal ht = product.priceExclTax();

        // Determine VAT rate based on product category
        BigDecimal vATRate = switch (product.type()) {
            case FOOD, MEDICINE -> BigDecimal.ZERO;      // 0% for essentials
            case BOOK -> new BigDecimal("0.10");        // 10% for books
            default -> new BigDecimal("0.20");          // 20% for others
        };

        // Calculate and round each tax separately as per the formula
        BigDecimal vATAmount = round(ht.multiply(vATRate));

        BigDecimal importTaxAmount = BigDecimal.ZERO;
        if (product.imported()) {
            // Additional 5% tax for all imported products
            importTaxAmount = round(ht.multiply(new BigDecimal("0.05")));
        }

        return ht.add(vATAmount).add(importTaxAmount);
    }

    /**
     * Maps a CartItem to an InvoiceLine.
     * Calculates total taxes for the line based on unit differences.
     */
    private InvoiceLine mapToInvoiceLine(CartItem item) {
        BigDecimal unitPriceInclTax = calculatePriceInclTax(item.product());

        // Unit tax = Rounded Price TTC - Original Price HT
        BigDecimal unitTax = unitPriceInclTax.subtract(item.product().priceExclTax());
        // Total line tax = unit tax * quantity
        BigDecimal totalLineTax = unitTax.multiply(BigDecimal.valueOf(item.quantity()));

        return new InvoiceLine(
                item.product().label(),
                item.quantity(),
                unitPriceInclTax,
                totalLineTax
        );
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

}