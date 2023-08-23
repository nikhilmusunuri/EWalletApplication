package org.transaction.service.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.transaction.service.model.Transaction;
import org.transaction.service.model.TransactionStatus;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

	@Transactional
	@Modifying
	@Query("update Transaction t set t.status = ?2 where t.externalId = ?1")
    void updateTransaction(String externalTxnId, TransactionStatus transactionStatus);
}
