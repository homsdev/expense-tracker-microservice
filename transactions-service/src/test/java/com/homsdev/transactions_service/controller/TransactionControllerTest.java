package com.homsdev.transactions_service.controller;

import com.homsdev.transactions_service.dto.TransactionResponse;
import com.homsdev.transactions_service.exception.ResourceNotFoundException;
import com.homsdev.transactions_service.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    // @EnableJpaAuditing on the application class needs this bean; no database is used here
    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    @Test
    void getAllTransactionsReturnsOk() throws Exception {
        TransactionResponse response = new TransactionResponse();
        response.setId("tx-1");
        when(transactionService.getAllTransactions()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/transaction"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("tx-1"));
    }

    @Test
    void getTransactionByIdReturnsOk() throws Exception {
        TransactionResponse response = new TransactionResponse();
        response.setId("tx-1");
        when(transactionService.getTransactionById("tx-1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/transaction/tx-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("tx-1"));
    }

    @Test
    void getTransactionByIdReturnsNotFoundProblemWhenMissing() throws Exception {
        when(transactionService.getTransactionById("missing"))
                .thenThrow(new ResourceNotFoundException("Transaction", "missing"));

        mockMvc.perform(get("/api/v1/transaction/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createTransactionReturnsCreated() throws Exception {
        TransactionResponse response = new TransactionResponse();
        response.setId("tx-1");
        when(transactionService.createTransaction(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/transaction")
                        .contentType("application/json")
                        .content("{\"type\":\"EXPENSE\",\"amount\":25.50,\"accountId\":\"acc-1\",\"categoryId\":\"cat-1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("tx-1"));
    }

    @Test
    void deleteTransactionReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/transaction/tx-1"))
                .andExpect(status().isNoContent());

        verify(transactionService).deleteTransaction("tx-1");
    }
}
