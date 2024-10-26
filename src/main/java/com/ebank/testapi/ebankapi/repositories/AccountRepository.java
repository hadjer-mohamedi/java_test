package com.ebank.testapi.ebankapi.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ebank.testapi.ebankapi.models.Account;

public interface AccountRepository extends JpaRepository<Account, Long> , JpaSpecificationExecutor<Account>{
    Optional<Account> findByAccountNumber(String accountNumber);
    //Optional<Account> findById(Long id);
}