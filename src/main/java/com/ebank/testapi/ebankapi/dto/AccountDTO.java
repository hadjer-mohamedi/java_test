package com.ebank.testapi.ebankapi.dto;

import java.math.BigDecimal;

public class AccountDTO {

    private String accountNumber;
    private BigDecimal balance;

    public AccountDTO() {}

    public AccountDTO(String accountNumber, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
