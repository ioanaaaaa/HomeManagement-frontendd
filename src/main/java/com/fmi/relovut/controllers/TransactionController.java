package com.fmi.relovut.controllers;

import com.fmi.relovut.dto.transactions.AddFundsDto;
import com.fmi.relovut.dto.transactions.CreateTransactionDto;
import com.fmi.relovut.dto.transactions.TransactionChartDto;
import com.fmi.relovut.dto.transactions.TransactionDto;
import com.fmi.relovut.services.AddFundsService;
import com.fmi.relovut.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AddFundsService addFundsService;

    @GetMapping("")
    public List<TransactionDto> getAllTransaction(Principal principal) {
        return transactionService.getAllTransactions(principal.getName());
    }

    @PostMapping("")
    public void createTransaction(Principal principal, @Validated @RequestBody CreateTransactionDto createTransactionDto) {
        transactionService.createTransaction(principal.getName(),
                createTransactionDto.getToAccountId(),
                createTransactionDto.getAmount());
    }

    @PostMapping("/addFunds")
    public void addFunds(Principal principal, @Validated @RequestBody AddFundsDto addFundsDto) {
        addFundsService.addFunds(principal.getName(), addFundsDto.getAmount());
    }

    @GetMapping(value = "/getFromTransactions")
    public ResponseEntity<List<TransactionChartDto>> getFromTransactions(Principal principal) {
        try{
            return new ResponseEntity<List<TransactionChartDto>>(transactionService.fromTransaction(principal.getName()), HttpStatus.OK);
        } catch (Exception e){
            return  new ResponseEntity<List<TransactionChartDto>>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(value = "/getToTransactions")
    public ResponseEntity<List<TransactionChartDto>> getToTransactions(Principal principal){
        try{
            return new ResponseEntity<List<TransactionChartDto>>(transactionService.toTransaction(principal.getName()), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<List<TransactionChartDto>>(HttpStatus.BAD_REQUEST);
        }

      }


    }
