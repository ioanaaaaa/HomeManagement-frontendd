package com.fmi.relovut.dto.transactions;

import com.fmi.relovut.models.Transaction;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TransactionChartDto {

    private Double amount;
    private int date;
}
