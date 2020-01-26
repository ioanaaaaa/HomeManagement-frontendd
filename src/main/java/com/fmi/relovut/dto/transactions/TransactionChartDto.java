package com.fmi.relovut.dto.transactions;

import com.fmi.relovut.models.Transaction;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionChartDto {
    public TransactionChartDto(Transaction transaction) {
        this.amount = transaction.getAmount();
        this.date = transaction.getDate();
    }
    private final Double amount;
    private final Date date;
}
