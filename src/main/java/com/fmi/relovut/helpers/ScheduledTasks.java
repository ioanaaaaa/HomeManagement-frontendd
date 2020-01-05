package com.fmi.relovut.helpers;

import com.fmi.relovut.services.ConversionRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    @Autowired
    private ConversionRateService conversionRateService;

    @Scheduled(initialDelay = 5_000, fixedRate = 3600_000)
    public void UpdateOrCreateConversionRates() {
        conversionRateService.UpdateConversionRates();
    }
}
