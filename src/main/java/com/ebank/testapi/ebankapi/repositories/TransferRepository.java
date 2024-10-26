package com.ebank.testapi.ebankapi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ebank.testapi.ebankapi.models.Transfer;


@Repository
public interface TransferRepository  extends JpaRepository<Transfer, Long> , JpaSpecificationExecutor<Transfer>{
    List<Transfer> findByFromAccount_IdOrToAccount_Id(Long fromAccountId, Long toAccountId);

}
