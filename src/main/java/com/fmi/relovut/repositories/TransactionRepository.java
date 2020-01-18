package com.fmi.relovut.repositories;

import com.fmi.relovut.models.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findByFromAccount_Id(UUID id);
    List<Transaction> findByToAccount_Id(UUID id);

    List<Transaction> findByFromAccount_IdOrToAccount_idAndDateBetween(UUID fromAccount,UUID id,Date fromDate, Date toDate);
    List<Transaction> findByToAccount_idOrFromAccount_IdAndDateBetween(UUID fromAccount,UUID id,Date fromDate, Date toDate);
}
