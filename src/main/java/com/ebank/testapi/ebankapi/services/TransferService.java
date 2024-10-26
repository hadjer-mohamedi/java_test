package com.ebank.testapi.ebankapi.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ebank.testapi.ebankapi.models.Transfer;
import com.ebank.testapi.ebankapi.repositories.AccountRepository;
import com.ebank.testapi.ebankapi.repositories.TransferRepository;

@Service

public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    public TransferService(TransferRepository transferRepository,AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository ;
    }

    // The main method to handle filtering
    public List<Transfer> findTransfers(String fromAccount, String toAccount, BigDecimal minAmount, BigDecimal maxAmount, LocalDateTime startDate, LocalDateTime endDate) {
        return transferRepository.findAll(Specification.where(fromAccountSpec(fromAccount))
                .and(toAccountSpec(toAccount))
                .and(minAmountSpec(minAmount))
                .and(maxAmountSpec(maxAmount))
                .and(startDateSpec(startDate))
                .and(endDateSpec(endDate)));
    }

    // Specification for filtering by fromAccount
    private Specification<Transfer> fromAccountSpec(String fromAccount) {
        return (root, query, criteriaBuilder) -> {
            if (fromAccount != null && !fromAccount.isEmpty()) {
                return criteriaBuilder.equal(root.get("fromAccount").get("accountNumber"), fromAccount);
            }
            return null; // Ignore if fromAccount is null or empty
        };
    }

    // Specification for filtering by toAccount
    private Specification<Transfer> toAccountSpec(String toAccount) {
        return (root, query, criteriaBuilder) -> {
            if (toAccount != null && !toAccount.isEmpty()) {
                return criteriaBuilder.equal(root.get("toAccount").get("accountNumber"), toAccount);
            }
            return null; // Ignore if toAccount is null or empty
        };
    }

    // Specification for filtering by minAmount
    private Specification<Transfer> minAmountSpec(BigDecimal minAmount) {
        return (root, query, criteriaBuilder) -> {
            if (minAmount != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount);
            }
            return null; // Ignore if minAmount is null
        };
    }

    // Specification for filtering by maxAmount
    private Specification<Transfer> maxAmountSpec(BigDecimal maxAmount) {
        return (root, query, criteriaBuilder) -> {
            if (maxAmount != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount);
            }
            return null; // Ignore if maxAmount is null
        };
    }

    // Specification for filtering by startDate
    private Specification<Transfer> startDateSpec(LocalDateTime startDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("transferDate"), startDate);
            }
            return null; // Ignore if startDate is null
        };
    }

    // Specification for filtering by endDate
    private Specification<Transfer> endDateSpec(LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("transferDate"), endDate);
            }
            return null; // Ignore if endDate is null
        };
    }
}
