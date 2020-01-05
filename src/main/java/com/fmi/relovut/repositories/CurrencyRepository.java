package com.fmi.relovut.repositories;

import com.fmi.relovut.models.Currency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends CrudRepository<Currency, Long> {
    Currency findByIsoName(String isoName);
}
