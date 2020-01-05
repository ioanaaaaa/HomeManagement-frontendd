package com.fmi.relovut.helpers;

import com.fmi.relovut.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DbSeed implements CommandLineRunner {
    @Autowired
    private CurrencyService currencyService;

    @Override
    public void run(String... args) throws Exception {
        // Seed the db with currencies
        currencyService.seedCurrencies();
    }
}
