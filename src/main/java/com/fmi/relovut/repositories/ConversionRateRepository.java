package com.fmi.relovut.repositories;

import com.fmi.relovut.models.ConversionRate;
import com.fmi.relovut.models.Currency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversionRateRepository extends CrudRepository<ConversionRate, Long> {
    List<ConversionRate> findByFromCurrencyAndToCurrency(Currency fromCurrency, Currency toCurrency);
}
