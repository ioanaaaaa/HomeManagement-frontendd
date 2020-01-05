package com.fmi.relovut.services;

import com.fmi.relovut.dto.currency.CurrencyDto;
import com.fmi.relovut.models.Currency;
import com.fmi.relovut.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CurrencyService {
    @Autowired
    private CurrencyRepository currencyRepository;

    public List<CurrencyDto> getCurrencies() {
        List<CurrencyDto> result = new ArrayList<>();
        currencyRepository.findAll().forEach(c -> result.add(new CurrencyDto(c)));
        return result;
    }

    public void seedCurrencies() {
        seedCurrency("RON", "Romanian leu");
        seedCurrency("EUR", "Euro");
        seedCurrency("USD", "US dollar");
        seedCurrency("GBP", "Pound sterling");
        seedCurrency("RUB", "Russian rouble");
    }

    private void seedCurrency(String isoName, String name) {
        Currency currency = currencyRepository.findByIsoName(isoName);
        if (currency == null) {
            currency = new Currency().setName(name).setIsoName(isoName);
            currencyRepository.save(currency);
        }
    }
}
