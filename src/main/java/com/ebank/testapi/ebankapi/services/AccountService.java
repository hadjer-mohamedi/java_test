package com.ebank.testapi.ebankapi.services;


import com.ebank.testapi.ebankapi.dto.AccountDTO;
import com.ebank.testapi.ebankapi.exception.AccountNotFoundException;
import com.ebank.testapi.ebankapi.models.Account;
import com.ebank.testapi.ebankapi.models.Transfer;
import com.ebank.testapi.ebankapi.repositories.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    public Account getAccountById(Long accountId) throws Exception {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new Exception("Account with ID " + accountId + " not found"));
    }
    public List<Account> findAllAccounts() {
        return accountRepository.findAll();  // Fetch all accounts
    }
        // The main method to handle filtering
    public List<Account> findAccounts(String customerid, BigDecimal minBalance, BigDecimal maxBalance, String status) {
        return accountRepository.findAll(Specification.where(customerSpec(customerid))
                .and(minBalanceSpec(minBalance))
                .and(maxBalanceSpec(maxBalance))
                .and(statusSpec(status)));
    }
    // Specification for filtering by customer
    private Specification<Account> customerSpec(String customer) {
        return (root, query, criteriaBuilder) -> {
            if (customer != null && !customer.isEmpty()) {
                return criteriaBuilder.equal(root.get("customer").get("id"), customer);
            }
            return null; // Ignore if fromAccount is null or empty
        };
    }
        // Specification for filtering by minBalance
        private Specification<Account> minBalanceSpec(BigDecimal minBalance) {
            return (root, query, criteriaBuilder) -> {
                if (minBalance != null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("balance"), minBalance);
                }
                return null; // Ignore if minBalance is null
            };
        }
    
        // Specification for filtering by maxBalance
        private Specification<Account> maxBalanceSpec(BigDecimal maxBalance) {
            return (root, query, criteriaBuilder) -> {
                if (maxBalance != null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("balance"), maxBalance);
                }
                return null; // Ignore if maxAmount is null
            };
        }
    // Specification for filtering by status
    private Specification<Account> statusSpec(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status != null && !status.isEmpty()) {
                return criteriaBuilder.equal(root.get("status"), status);
            }
            return null; // Ignore if status is null or empty
        };
    }
    @Transactional
    public AccountDTO createAccount(String accountNumber, BigDecimal initialBalance) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(initialBalance);
        accountRepository.save(account);
        return new AccountDTO(account.getAccountNumber(), account.getBalance());
    }

    public AccountDTO getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account with number " + accountNumber + " not found"));
        return new AccountDTO(account.getAccountNumber(), account.getBalance());
    }

    @Transactional
    public AccountDTO depositToAccount(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account with number " + accountNumber + " not found"));
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        return new AccountDTO(account.getAccountNumber(), account.getBalance());
    }

    @Transactional
    public AccountDTO withdrawFromAccount(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account with number " + accountNumber + " not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds for account " + accountNumber);
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        return new AccountDTO(account.getAccountNumber(), account.getBalance());
    }
}
