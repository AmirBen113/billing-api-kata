package fr.caassurances.kata.billing.domain.model;

/**
 * Represents a single line in the cart with a product and its quantity.
 */
public record CartItem(
        Product product,
        int quantity
) {}
