package fr.caassurances.kata.billing.domain.ports;

import fr.caassurances.kata.billing.domain.model.Product;

import java.util.List;

public interface ProductRepository {

    List<Product> fetchAllProducts();

}
