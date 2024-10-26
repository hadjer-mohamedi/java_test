package com.ebank.testapi.ebankapi.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;  

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;  

import com.ebank.testapi.ebankapi.models.Account;
import com.ebank.testapi.ebankapi.models.Customer;
import com.ebank.testapi.ebankapi.models.Transaction;
import com.ebank.testapi.ebankapi.models.Transfer;
import com.ebank.testapi.ebankapi.repositories.AccountRepository;  
import com.ebank.testapi.ebankapi.services.AccountService;
import com.ebank.testapi.ebankapi.services.CustomerService;
import com.ebank.testapi.ebankapi.repositories.CustomerRepository;
import com.ebank.testapi.ebankapi.repositories.TransactionRepository;  

@Controller class AccountController {  
     
 @Autowired private AccountService accountService;
 @Autowired private AccountRepository accountRepository;  
 @Autowired private TransactionRepository transactionRepository;
 @Autowired private CustomerRepository customerRepository;  
 @Autowired private CustomerService customerService;

 @GetMapping("/accounts")  
 public String listAccounts(@RequestParam(required = false) String customerid,
 @RequestParam(required = false) BigDecimal minBalance,
 @RequestParam(required = false) BigDecimal maxBalance,
 @RequestParam(required = false) String status,
 Model model) {
 // Fetch all customers to populate the dropdowns
 List<Customer> customers = customerService.findAllCustomers();    
 // Get filtered accounts
 List<Account> accounts = accountService.findAccounts( customerid,  minBalance,  maxBalance,  status);

 // Add data to the model 
 model.addAttribute("accounts", accounts);  
 model.addAttribute("customers", customers);  // Pass customers list to view for the dropdowns
 return "accounts";  // Return the Thymeleaf view
 }  

 @GetMapping("/accounts/add")
 public String showAddAccountForm(Model model) {
     List<Customer> customers = customerRepository.findAll();
     System.out.println("Number of customers: " + customers.size()); // Debug line
     model.addAttribute("customers", customers);
     return "addAccount";
 } 

 @PostMapping("/accounts/add")  
 public String addAccount(@RequestParam String accountNumber,  
 @RequestParam BigDecimal balance,  
 @RequestParam Long customerId) {  
Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));
 Account account = new Account();  
 account.setAccountNumber(accountNumber);  
 account.setBalance(balance);   
 if (customer != null) {  
 account.setCustomer(customer);  
 }  
 accountRepository.save(account);  
 return "redirect:/accounts";  
 }  

 @PostMapping("/accounts/delete")  
 public String deleteAccount(@RequestParam Long id) {  
 accountRepository.deleteById(id);  
 return "redirect:/accounts";  
 }  

 @GetMapping("/accounts/transactions")
    public String viewTransactions(@PathVariable("id") Long accountId, Model model) throws Exception {
        Account account = accountService.getAccountById(accountId);
        List<Transaction> transactions = transactionRepository.findByAccount(account);
        model.addAttribute("transactions", transactions);
        model.addAttribute("account", account);
        return "transactions";
    }
}