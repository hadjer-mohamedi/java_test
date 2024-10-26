package com.ebank.testapi.ebankapi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ebank.testapi.ebankapi.models.Account;
import com.ebank.testapi.ebankapi.models.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> , JpaSpecificationExecutor<Transaction>{
    List<Transaction> findByAccount(Account account);
    List<Transaction> findByAccountId(Long accountId);
}
