package fr.caassurances.kata.billing.domain.usecase;

import fr.caassurances.kata.billing.domain.exception.InvalidDataException;
import fr.caassurances.kata.billing.domain.exception.ProductNotFoundException;
import fr.caassurances.kata.billing.domain.model.Cart;
import fr.caassurances.kata.billing.domain.model.CartItem;
import fr.caassurances.kata.billing.domain.model.Invoice;
import fr.caassurances.kata.billing.domain.model.Product;
import fr.caassurances.kata.billing.domain.ports.ProductRepository;
import fr.caassurances.kata.billing.domain.service.TaxService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GenerateInvoiceUseCase {

    private final ProductRepository productRepository;
    private final TaxService taxService;

    public GenerateInvoiceUseCase(ProductRepository productRepository, TaxService taxService) {
        this.productRepository = productRepository;
        this.taxService = taxService;
    }

    public Invoice execute(Cart cart) {
        // 1. Fetch official products from the external API
        List<Product> officialProducts = productRepository.fetchAllProducts();

        // Map for quick lookup by ID
        Map<Integer, Product> catalogMap = officialProducts.stream()
                .collect(Collectors.toMap(Product::id, p -> p));

        // 2. Re-build the cart items with official data to prevent price fraud
        List<CartItem> validatedItems = cart.items().stream()
                .map(item -> {
                    Integer productId = Optional.ofNullable(item.product())
                            .map(Product::id)
                            .orElseThrow(() -> new InvalidDataException("A cart item does not contain a valid product"));

                    Product officialProduct = catalogMap.get(productId);

                    if (officialProduct == null) {
                        throw new ProductNotFoundException("Product with ID " + productId + " is unknown in the catalog");
                    }
                    return new CartItem(officialProduct, item.quantity());
                })
                .toList();

        // 3. Delegate to the pure domain service for calculation
        return taxService.createInvoice(new Cart(validatedItems));
    }
}