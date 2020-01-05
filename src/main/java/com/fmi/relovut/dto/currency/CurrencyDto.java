package com.fmi.relovut.dto.currency;

import com.fmi.relovut.models.Currency;
import lombok.Data;

@Data
public class CurrencyDto {
    public CurrencyDto(Currency currency) {
        this.isoName = currency.getIsoName();
    }

    private final String isoName;
}
