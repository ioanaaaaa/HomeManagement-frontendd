package com.fmi.relovut.dto.account;

import com.fmi.relovut.dto.currency.CurrencyDto;
import com.fmi.relovut.models.Account;
import lombok.Data;

@Data
public class AccountDetailsDto {
    public AccountDetailsDto(Account account) {
        this.id = account.getId().toString();
        this.currency = new CurrencyDto(account.getCurrency());
        this.amount = account.getAmount();
        this.email = account.getUser().getEmail();
        this.fullname = account.getUser().getFullname();
    }

    private final String id;
    private final Double amount;
    private final CurrencyDto currency;

    private final String email;
    private final String fullname;
}
