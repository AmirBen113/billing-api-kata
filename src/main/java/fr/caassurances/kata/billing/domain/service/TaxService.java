package fr.caassurances.kata.billing.domain.service;

import fr.caassurances.kata.billing.domain.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service responsible for tax calculation and invoice generation.
 * Handles VAT rules, import taxes, and specific financial rounding.
 */
@Service
public class TaxService {

    private final TaxCalculator taxCalculator;

    public TaxService(TaxCalculator taxCalculator) {
        this.taxCalculator = taxCalculator;
    }

    /**
     * Orchestrates the conversion of a Cart into a final Invoice.
     * It ensures that totals are the sum of individual rounded lines to maintain financial consistency.
     */
    public Invoice createInvoice(Cart cart) {
        // 1. Map each cart item to an invoice line with its specific tax calculations
        List<InvoiceLine> lines = cart.items().stream()
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
     * Maps a CartItem to an InvoiceLine.
     * Calculates total taxes for the line based on unit differences.
     */
    private InvoiceLine mapToInvoiceLine(CartItem item) {
        BigDecimal unitPriceInclTax = taxCalculator.calculatePriceInclTax(item.product());
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

}