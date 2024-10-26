package com.ebank.testapi.ebankapi.services;

import com.ebank.testapi.ebankapi.models.Transaction;
import com.ebank.testapi.ebankapi.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    public void addTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    // Find transactions with filters
    public List<Transaction> findTransactions(Long accountId, BigDecimal minAmount, BigDecimal maxAmount, String transactionType) {
        return transactionRepository.findAll(Specification.where(accountSpec(accountId))
                .and(minAmountSpec(minAmount))
                .and(maxAmountSpec(maxAmount))
                .and(transactionTypeSpec(transactionType)));
    }

    // Filter by Account ID
    private Specification<Transaction> accountSpec(Long accountId) {
        return (root, query, criteriaBuilder) -> {
            if (accountId != null) {
                return criteriaBuilder.equal(root.get("account").get("id"), accountId);
            }
            return null;
        };
    }

    // Filter by minimum amount
    private Specification<Transaction> minAmountSpec(BigDecimal minAmount) {
        return (root, query, criteriaBuilder) -> {
            if (minAmount != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount);
            }
            return null;
        };
    }

    // Filter by maximum amount
    private Specification<Transaction> maxAmountSpec(BigDecimal maxAmount) {
        return (root, query, criteriaBuilder) -> {
            if (maxAmount != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount);
            }
            return null;
        };
    }

    // Filter by Transaction Type
    private Specification<Transaction> transactionTypeSpec(String transactionType) {
        return (root, query, criteriaBuilder) -> {
            if (transactionType != null && !transactionType.isEmpty()) {
                return criteriaBuilder.equal(root.get("transactionType"), transactionType);
            }
            return null;
        };
    }
}
