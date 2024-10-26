package com.ebank.testapi.ebankapi.services;

import com.ebank.testapi.ebankapi.models.Account;
import com.ebank.testapi.ebankapi.models.Customer;
import com.ebank.testapi.ebankapi.models.Transfer;
import com.ebank.testapi.ebankapi.repositories.AccountRepository;
import com.ebank.testapi.ebankapi.repositories.CustomerRepository;
import com.ebank.testapi.ebankapi.repositories.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BankService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransferRepository transferRepository;

    public BankService(AccountRepository accountRepository, CustomerRepository customerRepository, TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transferRepository = transferRepository;
    }

    public Account createAccount(Long customerId, BigDecimal initialDeposit) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        Account account = new Account();
        account.setBalance(initialDeposit);
        account.setCustomer(customer);
        return accountRepository.save(account);
    }

    @Transactional
    public Transfer transferAmount(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Account fromAccount = accountRepository.findById(fromAccountId).orElseThrow();
        Account toAccount = accountRepository.findById(toAccountId).orElseThrow();
        
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        
        Transfer transfer = new Transfer();
        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(toAccount);
        transfer.setAmount(amount);
        
        transferRepository.save(transfer);
        
        return transfer;
    }

    public BigDecimal getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow();
        return account.getBalance();
    }

    public List<Transfer> getTransferHistory(Long accountId) {
        return transferRepository.findByFromAccount_IdOrToAccount_Id(accountId, accountId);
    }
}
