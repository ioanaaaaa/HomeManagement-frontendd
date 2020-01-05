package com.fmi.relovut.repositories;

import com.fmi.relovut.models.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends CrudRepository<Account, UUID> {

    @EntityGraph(attributePaths = {"addFundsTransactions", "outgoingTransactions", "incomingTransactions"})
    @Override
    Optional<Account> findById(UUID id);
}
