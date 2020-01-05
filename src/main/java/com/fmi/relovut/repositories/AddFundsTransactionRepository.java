package com.fmi.relovut.repositories;

import com.fmi.relovut.models.AddFundsTransaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AddFundsTransactionRepository extends CrudRepository<AddFundsTransaction, Long> {
    List<AddFundsTransaction> findByAccount_Id(UUID id);
}
