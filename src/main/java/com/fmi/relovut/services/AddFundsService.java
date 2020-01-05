package com.fmi.relovut.services;

import com.fmi.relovut.models.AddFundsTransaction;
import com.fmi.relovut.models.User;
import com.fmi.relovut.repositories.AddFundsTransactionRepository;
import com.fmi.relovut.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
public class AddFundsService {
    @Autowired
    private AddFundsTransactionRepository addFundsTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    public void addFunds(String userEmail, Double amount) {
        if (amount <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount cannot be lower or equal to 0!");

        User user = userRepository.findByEmail(userEmail);
        AddFundsTransaction newTransaction = new AddFundsTransaction()
                .setAccount(user.getAccount())
                .setAmount(amount)
                .setDate(new Date());
        user.getAccount().getAddFundsTransactions().add(newTransaction);
        user.getAccount().setAmount(user.getAccount().getAmount() + amount);

        addFundsTransactionRepository.save(newTransaction);
        userRepository.save(user);
    }

}
