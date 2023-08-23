package org.wallet.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wallet.service.service.WalletService;

@RestController
public class WalletController {

	@Autowired
	WalletService walletservice;
	
	@GetMapping("/balance/{id}")
	public String getBalance(@PathVariable("id") String id) {
		return walletservice.findByWalletId(id).toString();
	}

}
