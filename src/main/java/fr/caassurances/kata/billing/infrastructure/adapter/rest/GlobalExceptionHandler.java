package fr.caassurances.kata.billing.infrastructure.adapter.rest;

import fr.caassurances.kata.billing.domain.exception.BusinessException;
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
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles specialized Business errors.
     * Automatically switches to 404 NOT FOUND if the exception name contains "NotFound",
     * otherwise defaults to 400 BAD REQUEST.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
        log.warn("Business rule violation: {}", ex.getMessage());

        HttpStatus status = ex.getClass().getSimpleName().contains("NotFound")
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;

        return buildResponse(
                status,
                "Business Rule Error",
                ex.getMessage()
        );
    }

    /**
     * Handles errors from the external Catalog API
     */
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Map<String, Object>> handleExternalApiError(WebClientResponseException ex) {
        log.error("External API call failed: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());

        HttpStatus status = (HttpStatus) ex.getStatusCode();
        String message = "External Catalog service error";

        if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            message = "Catalog service is temporarily down (503)";
        }

        return buildResponse(status, message, "The billing process cannot proceed with official prices.");
    }

    /**
     * Handles cases where the Circuit Breaker is OPEN.
     * Prevents system overload by rejecting requests early.
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
     * Global fallback for any unexpected internal error (500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllErrors(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An internal error occurred",
                "Please contact the technical support team for further investigation."
        );
    }

    /**
     * Helper method to build a standardized and consistent error JSON structure.
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