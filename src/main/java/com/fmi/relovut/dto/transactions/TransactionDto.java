package com.fmi.relovut.dto.transactions;

import com.fmi.relovut.dto.user.UserDto;
import com.fmi.relovut.models.AddFundsTransaction;
import com.fmi.relovut.models.Transaction;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionDto {
    public TransactionDto(Transaction transaction) {
        this.fromUser = new UserDto(transaction.getFromAccount().getUser());
        this.toUser = new UserDto(transaction.getToAccount().getUser());
        this.amount = transaction.getAmount();
        this.rate = transaction.getRate();
        this.date = transaction.getDate();
    }

    public TransactionDto(AddFundsTransaction transaction) {
        this.fromUser = null;
        this.toUser = new UserDto(transaction.getAccount().getUser());
        this.amount = transaction.getAmount();
        this.rate = 1.0d;
        this.date = transaction.getDate();
    }

    private final UserDto fromUser;
    private final UserDto toUser;
    private final Double amount;
    private final Double rate;
    private final Date date;
}
