package fr.caassurances.kata.billing.domain.exception;


public class InvalidDataException extends BusinessException {
    public InvalidDataException(String message) {
        super(message);
    }
}