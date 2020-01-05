package com.fmi.relovut.dto.conversionrate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversionRateResponse {
    public JsonNode rates;
    public String base;
    public Date date;
}
