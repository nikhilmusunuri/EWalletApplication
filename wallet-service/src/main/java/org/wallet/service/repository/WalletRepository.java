package org.wallet.service.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.wallet.service.model.WalletDetails;

public interface WalletRepository extends JpaRepository<WalletDetails, Integer> {

	WalletDetails findBywalletId(String walletId);

	@Transactional
	@Modifying
    @Query("update WalletDetails w set w.balance = w.balance + :amount where w.walletId = :walletId")
    void updateWallet(String walletId, Long amount);

}
