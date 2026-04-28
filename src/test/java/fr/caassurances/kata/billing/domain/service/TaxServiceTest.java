package fr.caassurances.kata.billing.domain.service;

import fr.caassurances.kata.billing.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaxServiceTest {

    private final TaxCalculator taxCalculator = new TaxCalculator();
    private final TaxService taxService = new TaxService(taxCalculator);

    @Test
    @DisplayName("Should apply 10% tax on books and 5% on imports with 0.05 rounding")
    void shouldCalculateTaxesForImportedBook() {
        // Basic tax (10%) = 3.00
        // Import tax (5%) = 1.50
        // Total tax expected = 4.50
        Product book = new Product(4, "Java Book", ProductType.BOOK, new BigDecimal("30.00"), true);
        Cart cart = new Cart(List.of(new CartItem(book, 1)));

        Invoice invoice = taxService.createInvoice(cart);

        assertEquals(new BigDecimal("4.50"), invoice.totalTax(), "Tax should be 4.50");
        assertEquals(new BigDecimal("34.50"), invoice.totalInclTax(), "Total TTC should be 34.50");
    }

    @Test
    @DisplayName("Should apply complex rounding (0.05 up) for imported other products")
    void shouldApplyComplexRounding() {
        // Basic tax (20%) = 9.50
        // Import tax (5%) = 2.375 -> rounded up to 2.40
        // Total tax = 9.50 + 2.40 = 11.90
        Product perfume = new Product(6, "Imported Perfume", ProductType.OTHER, new BigDecimal("47.50"), true);
        Cart cart = new Cart(List.of(new CartItem(perfume, 1)));

        Invoice invoice = taxService.createInvoice(cart);

        assertEquals(new BigDecimal("11.90"), invoice.totalTax(), "Tax should be rounded up to 11.90");
    }
}