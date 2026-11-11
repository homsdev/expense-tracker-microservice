package com.homsdev.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("account-service", r -> r
                        .path("/api/v1/accounts/**")
                        .uri("http://account-service:8081"))
                .route("order-service", r -> r
                        .path("/api/v1/transaction/**")
                        .uri("http://transaction-service:8082"))
                .route("inventory-service", r -> r
                        .path("/api/v1/category/**")
                        .uri("http://transaction-service:8082"))
                .build();
    }
}
