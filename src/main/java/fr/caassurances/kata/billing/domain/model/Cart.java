package fr.caassurances.kata.billing.domain.model;

import java.util.List;

/**
 * Represents a shopping cart containing a list of items.
 * Immutability ensures the cart state remains consistent during processing.
 */
public record Cart(
        List<CartItem> items
) {
    public Cart {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Cart must contain at least one item");
        }
    }
}
