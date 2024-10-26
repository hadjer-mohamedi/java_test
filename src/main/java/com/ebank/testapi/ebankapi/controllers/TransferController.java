package com.ebank.testapi.ebankapi.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ebank.testapi.ebankapi.models.Account;
import com.ebank.testapi.ebankapi.models.Transfer;
import com.ebank.testapi.ebankapi.repositories.AccountRepository;
import com.ebank.testapi.ebankapi.repositories.TransferRepository;
import com.ebank.testapi.ebankapi.services.AccountService;
import com.ebank.testapi.ebankapi.services.TransferService;

@Controller
public class TransferController {

    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService; 
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    
    @GetMapping("/transfers")
    public String listTransfers(
            @RequestParam(required = false) String fromAccount,
            @RequestParam(required = false) String toAccount,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            Model model) {

        // Fetch all accounts to populate the dropdowns
        List<Account> accounts = accountService.findAllAccounts(); 

        // Get filtered transfers
        List<Transfer> transfers = transferService.findTransfers(fromAccount, toAccount, minAmount, maxAmount, startDate, endDate);

        // Add data to the model
        model.addAttribute("transfers", transfers);
        model.addAttribute("accounts", accounts);  // Pass account list to view for the dropdowns

        return "transfers"; // Return the Thymeleaf view
    }
    
    @GetMapping("/transfers/add")
    public String showAddTransferForm(Model model) {
        List<Account> accounts = accountRepository.findAll();
        model.addAttribute("accounts", accounts); 
        System.out.println("accounts  "+accounts);
      return "addTransfer"; 
}
    @PostMapping("/transfers/add")
    public String addTransfer(@RequestParam Long fromAccountId, @RequestParam Long toAccountId, @RequestParam BigDecimal amount) {
        Account fromAccount = accountRepository.findById(fromAccountId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid from account ID"));
        Account toAccount = accountRepository.findById(toAccountId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid to account ID"));
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in the from account");
        }
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        Transfer transfer = new Transfer();
        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(toAccount);
        transfer.setAmount(amount);
        transferRepository.save(transfer);
    
        return "redirect:/transfers";
    }
    
    @GetMapping("/transfers/delete/{id}")
    public String deleteTransfer(@PathVariable Long id) {
        Transfer transfer = transferRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid transfer ID"));
        Account fromAccount = transfer.getFromAccount();
        Account toAccount = transfer.getToAccount();
        BigDecimal amount = transfer.getAmount();
        toAccount.setBalance(toAccount.getBalance().subtract(amount));
        fromAccount.setBalance(fromAccount.getBalance().add(amount));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        transferRepository.deleteById(id);
        return "redirect:/transfers";
    }
    
}

