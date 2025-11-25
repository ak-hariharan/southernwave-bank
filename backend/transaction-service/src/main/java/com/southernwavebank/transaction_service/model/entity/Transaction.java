package com.southernwavebank.transaction_service.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.southernwavebank.transaction_service.model.TransactionStatus;
import com.southernwavebank.transaction_service.model.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;  

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private LocalDateTime transactionTime;
    
    @Column(unique = true)
    private String transactionReference;
}
