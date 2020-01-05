package com.fmi.relovut.dto.transactions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddFundsDto {
    @NotNull
    public Double amount;
}
