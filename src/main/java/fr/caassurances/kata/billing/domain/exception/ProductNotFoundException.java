package fr.caassurances.kata.billing.domain.exception;


public class ProductNotFoundException extends BusinessException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}