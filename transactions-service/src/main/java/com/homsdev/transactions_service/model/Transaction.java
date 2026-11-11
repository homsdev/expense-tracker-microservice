package com.homsdev.transactions_service.model;

import com.homsdev.transactions_service.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracker_transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private TransactionType type;
    private String description;
    private BigDecimal amount;
    private String currency;

    private String accountId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private LocalDateTime creationDate;
    private LocalDateTime lastModifiedDate;
}
