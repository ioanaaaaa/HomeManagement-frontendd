package com.fmi.relovut.dto.transactions;

import com.fmi.relovut.models.AddFundsTransaction;
import lombok.Data;

import java.util.Date;

@Data
public class AddFundsTransactionDto {
    public AddFundsTransactionDto(AddFundsTransaction transaction) {
        this.amount = transaction.getAmount();
        this.date = transaction.getDate();
    }

    private final Double amount;
    private final Date date;
}
