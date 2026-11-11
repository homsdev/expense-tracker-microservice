package com.homsdev.transactions_service.service.client;


import com.homsdev.transactions_service.dto.UpdateBalanceRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PatchExchange;

import java.math.BigDecimal;

public interface AccountClient {
    @PatchExchange("/api/v1/accounts/{accountId}/balance")
    BigDecimal updateBalance(@PathVariable String accountId,@RequestBody UpdateBalanceRequest request);

}
