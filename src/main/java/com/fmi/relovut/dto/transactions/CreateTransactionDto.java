package com.fmi.relovut.dto.transactions;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateTransactionDto {
    public UUID toAccountId;
    public Double amount;
}
