package com.ebank.testapi.ebankapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ebank.testapi.ebankapi.models.Customer;
import com.ebank.testapi.ebankapi.repositories.CustomerRepository;

@Controller
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/customers")
    public String listCustomers(Model model) {
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        return "customers";
    }

    @PostMapping("/customers/add")
    public String addCustomer(@RequestParam String name) {
        Customer customer = new Customer();
        customer.setName(name);
        customerRepository.save(customer);
        return "redirect:/customers";
    }
    @PostMapping("/customers/delete")
    public String deleteCustomer(@RequestParam Long id) {
        customerRepository.deleteById(id);
        return "redirect:/customers"; 
    }
}

