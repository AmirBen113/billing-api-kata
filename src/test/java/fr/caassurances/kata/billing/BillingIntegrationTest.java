package fr.caassurances.kata.billing;

import fr.caassurances.kata.billing.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class BillingIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private List<Product> catalogMock;

    @BeforeEach
    void setup() {
        when(catalogMock.iterator()).thenReturn(List.of(
                new Product(1, "Rice", ProductType.FOOD, new BigDecimal("10.00"), false),
                new Product(4, "Java Book", ProductType.BOOK, new BigDecimal("30.00"), false)
        ).iterator());
    }

    @Test
    void should_calculate_correct_taxes_and_totals() {
        // Prepare a Cart based on business rules for essential goods and books
        Product rice = new Product(1, "Rice", ProductType.FOOD, new BigDecimal("10.00"), false);
        Product book = new Product(4, "Java Book", ProductType.BOOK, new BigDecimal("30.00"), false);

        Cart cart = new Cart(List.of(
                new CartItem(rice, 2), // 20.00 HT, 0% Tax
                new CartItem(book, 1)  // 30.00 HT, 10% Tax
        ));

        webTestClient.post()
                .uri("/api/billing/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cart)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalTax").isEqualTo(3)
                .jsonPath("$.totalInclTax").isEqualTo(53.0)
                .jsonPath("$.lines[1].unitPriceInclTax").isEqualTo(33.00);
    }
}