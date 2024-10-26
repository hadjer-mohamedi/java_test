package com.ebank.testapi.ebankapi.services;

import com.ebank.testapi.ebankapi.repositories.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

import com.ebank.testapi.ebankapi.models.Customer;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    public Customer getAccountById(Long customerId) throws Exception {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new Exception("Customer with ID " + customerId + " not found"));
    }
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();  // Fetch all Customers
    }
}
