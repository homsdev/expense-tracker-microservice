package com.homsdev.account_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountRequest {

    @NotBlank(message = "Account name is required and cannot be blank")
    @Size(min = 3, max = 50, message = "Account name must be between 3 and 50 characters")
    private String name;

    @NotBlank(message = "Currency code is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO code (e.g., USD, MXN)")
    private String currency;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    @DecimalMax(value = "999999999.99", message = "Balance exceeds maximum allowed value")
    private BigDecimal balance;
}
