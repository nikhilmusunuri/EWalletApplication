package org.transaction.service.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.transaction.service.dto.CreateTransaction;
import org.transaction.service.model.User;
import org.transaction.service.service.TraansactionService;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class TransactionController {

	@Autowired
	TraansactionService txnservice;
	
	@PostMapping("/transaction")
	public String createTransaction(@RequestBody @Valid CreateTransaction createtransaction) throws JsonProcessingException {
		User sender = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return txnservice.createtranx(createtransaction.to(sender));
	}
	
}
