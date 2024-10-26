package com.ebank.testapi.ebankapi.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ebank.testapi.ebankapi.models.Account;
import com.ebank.testapi.ebankapi.models.Transaction;
import com.ebank.testapi.ebankapi.models.TransactionType;
import com.ebank.testapi.ebankapi.repositories.AccountRepository;
import com.ebank.testapi.ebankapi.repositories.TransactionRepository;
import com.ebank.testapi.ebankapi.services.AccountService;
import com.ebank.testapi.ebankapi.services.TransactionService;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository; 

     @GetMapping("/transactionhistory")
    public String listTransactions(Model model,
                                   @RequestParam(required = false) Long accountId,
                                   @RequestParam(required = false) BigDecimal minAmount,
                                   @RequestParam(required = false) BigDecimal maxAmount,
                                   @RequestParam(required = false) String transactionType) {
        List<Transaction> transactions = transactionService.findTransactions(accountId, minAmount, maxAmount, transactionType);
        List<Account> accounts = accountService.findAllAccounts();
        model.addAttribute("transactions", transactions);
        System.err.println("******* accounts ******** "+accounts);
        model.addAttribute("accounts", accounts); // Account list for dropdown
        return "transactionHistory";
    }
    @GetMapping("/add")
    public String showAddTransactionForm(Model model) throws Exception{
        List<Account> accounts = accountRepository.findAll();
        model.addAttribute("accounts", accounts);
        return "addTransaction";
    }

    @PostMapping("/add")
    public String addTransaction(@RequestParam("transactionType") TransactionType transactionType,
                                 @RequestParam("amount") BigDecimal amount,
                                 @RequestParam("transactionDate") LocalDateTime transactionDate,
                                 @RequestParam("fromAccountId") Long fromAccountId) throws Exception{
        Account fromAccount = accountRepository.findById(fromAccountId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));

        Transaction transaction = new Transaction();
        transaction.setTransactionType(transactionType);
        transaction.setAmount(Double.parseDouble(amount.toString()));
        transaction.setTransactionDate(transactionDate);
        transaction.setAccount(fromAccount);

        // Apply the transaction logic here (e.g., updating account balances if needed)
        if (transactionType == TransactionType.WITHDRAWAL && fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds for withdrawal");
        }

        // Update account balance based on the transaction type
        if (transactionType == TransactionType.DEPOSIT) {
            fromAccount.setBalance(fromAccount.getBalance().add(amount));
        } else if (transactionType == TransactionType.WITHDRAWAL) {
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        }

        // Save updated account and transaction
        accountRepository.save(fromAccount);
        transactionRepository.save(transaction);
        return "redirect:/transactions/transactionhistory";
    }
   

  
}
