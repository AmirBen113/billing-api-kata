package fr.caassurances.kata.billing.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    private static final Logger log = LoggerFactory.getLogger(WebClientConfig.class);

    @Bean
    public WebClient catalogWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8080/external-api") // Pointing to our mock controller
                .filter(logRequest())  // Trace outgoing requests
                .filter(logResponse()) // Trace incoming responses
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Outgoing HTTP Request: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Incoming HTTP Response status: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}