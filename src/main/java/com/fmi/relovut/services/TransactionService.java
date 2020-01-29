package com.fmi.relovut.services;

import com.fmi.relovut.dto.account.AccountDetailsDto;
import com.fmi.relovut.dto.transactions.TransactionChartDto;
import com.fmi.relovut.dto.transactions.TransactionDto;
import com.fmi.relovut.helpers.GeneralHelper;
import com.fmi.relovut.models.Account;
import com.fmi.relovut.models.AddFundsTransaction;
import com.fmi.relovut.models.Transaction;
import com.fmi.relovut.models.User;
import com.fmi.relovut.repositories.AccountRepository;
import com.fmi.relovut.repositories.AddFundsTransactionRepository;
import com.fmi.relovut.repositories.TransactionRepository;
import com.fmi.relovut.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.util.Pair.toMap;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AddFundsTransactionRepository addFundsTransactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ConversionRateService conversionRateService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public List<TransactionDto> getAllTransactions(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        List<Transaction> transactionFrom = transactionRepository.findByFromAccount_Id(user.getAccount().getId());
        List<Transaction> transactionsTo = transactionRepository.findByToAccount_Id(user.getAccount().getId());
        List<AddFundsTransaction> addFundsTransactions = addFundsTransactionRepository.findByAccount_Id(user.getAccount().getId());

         return Stream
                .concat(
                    Stream.concat(
                            transactionFrom.stream().map(TransactionDto::new),
                            transactionsTo.stream().map(TransactionDto::new)),
                    addFundsTransactions.stream().map(TransactionDto::new))
                .sorted(Comparator.comparing(TransactionDto::getDate).reversed())
                .collect(Collectors.toList());
    }

    public void createTransaction(String userEmail, UUID toAccount, Double amount) {
        User user = userRepository.findByEmail(userEmail);
        if (amount > user.getAccount().getAmount())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough funds!");

        Optional<Account> opt_account = accountRepository.findById(toAccount);
        if (!opt_account.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find destination account!");
        Account account = opt_account.get();

        if (account.getUser().getEmail().equalsIgnoreCase(user.getEmail()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot transfer money to yourself!");

        Double rate = conversionRateService.getRateBetween(user.getAccount().getCurrency(), account.getCurrency());
        Transaction newTransaction = new Transaction()
                .setAmount(amount)
                .setDate(new Date())
                .setFromAccount(user.getAccount())
                .setRate(rate)
                .setToAccount(account);
        account.setAmount(account.getAmount() + GeneralHelper.round(amount * rate, 4));
        user.getAccount().setAmount(user.getAccount().getAmount() - amount);
        transactionRepository.save(newTransaction);
    }


    public List<TransactionDto> getTransactionsInInterval(Date fromDate, Date toDate, String currentUserEmail){
        if(fromDate.after(toDate)){
            fromDate = toDate;
            toDate = fromDate;
        }

        AccountDetailsDto accountDetailsDto = userService.getAccountDetails(currentUserEmail);

        UUID accountId =  UUID.fromString(accountDetailsDto.getId());
        List<Transaction> transactions =  transactionRepository.findByFromAccount_IdOrToAccount_idAndDateBetween(accountId,accountId, fromDate, toDate);
        transactions.addAll(transactionRepository.findByToAccount_idOrFromAccount_IdAndDateBetween(accountId,accountId, fromDate, toDate));

        List<TransactionDto> transactionDtos = transactions.stream()
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .map(TransactionDto::new)
                .collect(Collectors.toList());

        return transactionDtos;
    }

    public List<TransactionChartDto> toTransaction(String userEmail){
          User user = userRepository.findByEmail(userEmail);
          Date date = new Date();
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(date);
          int month = calendar.get(Calendar.MONTH) + 1;

          List<Transaction> incomingTransactions = transactionRepository.findByToAccount_Id(user.getAccount().getId())
                      .stream().filter(transaction -> transaction.getDate().toInstant().atZone(ZoneId.systemDefault()).getMonthValue() == month)
                      .collect(Collectors.toList());

        Map<Integer, List<TransactionChartDto>> incomingTransactionsDto = incomingTransactions.stream()
                .map(trans ->  TransactionChartDto.builder()
                            .date(trans.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfMonth())
                            .amount(trans.getAmount())
                            .build()
                ).collect(Collectors.groupingBy(TransactionChartDto::getDate));

        List<TransactionChartDto> incomingData =new ArrayList<>();

        for (Integer key : incomingTransactionsDto.keySet()) {
            List<TransactionChartDto> values = incomingTransactionsDto.get(key);
            Double amount = values.stream().collect(Collectors.summingDouble(TransactionChartDto::getAmount));

            incomingData.add(TransactionChartDto.builder()
                    .amount(amount)
                    .date(key)
                    .build());
        }

        return incomingData;
 }

    public  List<TransactionChartDto> fromTransaction(String userEmail){
        User user = userRepository.findByEmail(userEmail);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;

        List<Transaction> outgoingTransactions = transactionRepository.findByFromAccount_Id(user.getAccount().getId())
                .stream().filter(transaction -> transaction.getDate().toInstant().atZone(ZoneId.systemDefault()).getMonthValue() == month)
                .collect(Collectors.toList());

        Map<Integer, List<TransactionChartDto>> outgoingTransactionsDto = outgoingTransactions.stream()
                .map(trans ->  TransactionChartDto.builder()
                .date(trans.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfMonth())
                .amount(trans.getAmount())
                .build()
        ).collect(Collectors.groupingBy(TransactionChartDto::getDate));

        List<TransactionChartDto> outgoingData =new ArrayList<>();

        for (Integer key : outgoingTransactionsDto.keySet()) {
            List<TransactionChartDto> values = outgoingTransactionsDto.get(key);
            Double amount = values.stream().collect(Collectors.summingDouble(TransactionChartDto::getAmount));
            outgoingData.add(TransactionChartDto.builder()
                    .amount(amount)
                    .date(key)
                    .build());
        }
        return outgoingData;
    }

}
