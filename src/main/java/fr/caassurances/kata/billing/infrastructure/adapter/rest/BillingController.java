package fr.caassurances.kata.billing.infrastructure.adapter.rest;

import fr.caassurances.kata.billing.domain.model.Cart;
import fr.caassurances.kata.billing.domain.model.Invoice;
import fr.caassurances.kata.billing.domain.usecase.GenerateInvoiceUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/billing")
@Tag(name = "Billing", description = "Main API for invoice generation and tax calculation")
public class BillingController {

    private static final Logger log = LoggerFactory.getLogger(BillingController.class);
    private final GenerateInvoiceUseCase generateInvoiceUseCase;

    public BillingController(GenerateInvoiceUseCase generateInvoiceUseCase) {
        this.generateInvoiceUseCase = generateInvoiceUseCase;
    }

    @PostMapping("/invoice")
    @Operation(summary = "Generate a detailed invoice",
            description = "Takes a shopping cart, fetches official prices/types, and returns an invoice with calculated taxes and rounding.")
    @ApiResponse(responseCode = "200", description = "Invoice successfully generated")
    @ApiResponse(responseCode = "400", description = "Invalid cart data provided")
    public Invoice generateInvoice(@Valid @RequestBody Cart cart) {
        long startTime = System.nanoTime(); // Précision à la nanoseconde

        log.info("Incoming request: Generating invoice for cart with {} items", cart.items().size());
        Invoice invoice = generateInvoiceUseCase.execute(cart);

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        log.info(">>> Invoice generated in {}ms [Virtual Threads: {}]",
                durationMs,
                Thread.currentThread().isVirtual());

        return invoice;
    }
}