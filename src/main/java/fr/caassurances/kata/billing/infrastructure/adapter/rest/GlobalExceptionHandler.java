package fr.caassurances.kata.billing.infrastructure.adapter.rest;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handling for the Billing API.
 * Ensures professional and structured JSON responses even in case of failure.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles 503 errors from the external Catalog API.
     */
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Map<String, Object>> handleExternalApiError(WebClientResponseException ex) {
        log.error("External API call failed: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());

        HttpStatus status = (HttpStatus) ex.getStatusCode();
        String message = "External Catalog service error";
        String details = ex.getMessage();

        if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            message = "Catalog service is temporarily down (503)";
            details = "The billing process cannot proceed with official prices.";
        }

        return buildResponse(status, message, details);
    }

    /**
     * Handles cases where the Circuit Breaker is OPEN.
     */
    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<Map<String, Object>> handleCircuitBreakerOpen(CallNotPermittedException ex) {
        log.warn("Circuit Breaker is OPEN - Request rejected to protect the system.");
        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service temporarily restricted",
                "The system is protecting itself from a failing dependency. Please try again in a few seconds."
        );
    }

    /**
     * Handles validation or business logic errors (e.g., negative prices, empty carts).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid request data", ex.getMessage());
    }

    /**
     * Global fallback for any unhandled exception (500 Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllErrors(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An internal error occurred",
                "Please contact the technical support team."
        );
    }

    /**
     * Helper method to build a standardized error JSON.
     */
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message, String details) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("details", details);

        return new ResponseEntity<>(body, status);
    }
}