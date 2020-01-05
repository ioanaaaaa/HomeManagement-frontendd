package com.fmi.relovut.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fmi.relovut.dto.conversionrate.ConversionRateResponse;
import com.fmi.relovut.helpers.GeneralHelper;
import com.fmi.relovut.models.ConversionRate;
import com.fmi.relovut.models.Currency;
import com.fmi.relovut.repositories.ConversionRateRepository;
import com.fmi.relovut.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ConversionRateService {
    private ConversionRateRepository conversionRateRepository;
    private CurrencyRepository currencyRepository;
    private String exchangeRateApiUrl;

    @Autowired
    ConversionRateService(ConversionRateRepository conversionRateRepository,
                          CurrencyRepository currencyRepository,
                          @Value("${com.fmi.relovut.exchange-rate-api-url}") String exchangeRateApiUrl) {
        this.conversionRateRepository = conversionRateRepository;
        this.currencyRepository = currencyRepository;
        this.exchangeRateApiUrl = exchangeRateApiUrl;
    }

    public Double getRateBetween(Currency fromCurrency, Currency toCurrency) {
        if (fromCurrency.equals(toCurrency))
            return 1.0d;

        List<ConversionRate> conversionRates = conversionRateRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
        if (conversionRates.size() <= 0)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not load conversion rates!");

        ConversionRate conversionRate = conversionRates.get(0);
        return GeneralHelper.round(conversionRate.getRate(), 4);
    }

    public void UpdateConversionRates() {
        ArrayList<Currency> currencyList = new ArrayList<>();
        currencyRepository.findAll().forEach(currencyList::add);

        ArrayList<ConversionRate> conversionRateList = new ArrayList<>();
        conversionRateRepository.findAll().forEach(conversionRateList::add);

        currencyList.forEach(currency -> {
            String isoName = currency.getIsoName();
            String url = exchangeRateApiUrl + "/latest?base=" + isoName;
            ConversionRateResponse conversionRateResponse = new RestTemplate().getForObject(url, ConversionRateResponse.class);
            if (conversionRateResponse == null)
                return;

            Date date = new Date();
            String base = conversionRateResponse.base;
            Iterator<Map.Entry<String, JsonNode>> fields = conversionRateResponse.rates.fields();
            while(fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String toIsoName = entry.getKey();
                Double rate = entry.getValue().asDouble() * 0.98d; // We're even making money now. Our pockets ;)

                Currency toCurrency = currencyList.stream()
                        .filter(c -> c.getIsoName().equalsIgnoreCase(toIsoName))
                        .findAny()
                        .orElse(null);
                if (toCurrency == null)
                    continue;

                ConversionRate oldConversionRate = conversionRateList.stream()
                        .filter(c -> c.getFromCurrency().getIsoName().equalsIgnoreCase(currency.getIsoName()) &&
                                     c.getToCurrency().getIsoName().equalsIgnoreCase(toIsoName))
                        .findAny()
                        .orElse(null);

                if (oldConversionRate == null) {
                    oldConversionRate = new ConversionRate()
                        .setDate(date)
                        .setFromCurrency(currency)
                        .setRate(rate)
                        .setToCurrency(toCurrency);
                } else {
                    oldConversionRate
                        .setDate(date)
                        .setRate(rate);
                }

                conversionRateRepository.save(oldConversionRate);
            }
        });
    }
}
