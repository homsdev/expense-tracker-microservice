package com.homsdev.transactions_service.mapper;

import com.homsdev.transactions_service.dto.CreateTransactionRequest;
import com.homsdev.transactions_service.dto.TransactionResponse;
import com.homsdev.transactions_service.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface TransactionMapper {

    @Mapping(target = "category", ignore = true)
    Transaction toTransaction(CreateTransactionRequest createTransactionRequest);

    TransactionResponse toTransactionResponse(Transaction transaction);
}
