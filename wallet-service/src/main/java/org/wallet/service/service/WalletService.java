package org.wallet.service.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.wallet.service.model.WalletDetails;
import org.wallet.service.repository.WalletRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WalletService {

	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	WalletRepository repo;
	
	@Autowired
	KafkaTemplate<String, String> kafkatemplate;
	
	@KafkaListener(topics= {"user_created"}, groupId= "ewallet")
	public void createWallet(String mssg) throws ParseException {
		JSONObject obj = (JSONObject)new JSONParser().parse(mssg);
		
		String userMobileNum = (String)obj.get("phone");
		
		WalletDetails walletdetails = WalletDetails.builder().balance(10000L).walletId(userMobileNum).build();
		
		repo.save(walletdetails);
	}
	
	@KafkaListener(topics= {"transaction_created"}, groupId= "ewallet")
	public void createTxns(String mssg) throws ParseException, JsonProcessingException {
		JSONObject obj = (JSONObject)new JSONParser().parse(mssg);
		
		String senderWalletId = (String)obj.get("senderId");
		String recieverWalletId = (String)obj.get("recieverId");
		Long amount = (Long)obj.get("amount");
		String transactionId = (String)obj.get("transactionId");
		
		try {
			WalletDetails senderWallet = repo.findBywalletId(senderWalletId);
			WalletDetails receiverWallet = repo.findBywalletId(recieverWalletId);
			if (senderWallet == null || receiverWallet == null || senderWallet.getBalance() < amount) {
				obj = this.init(recieverWalletId, senderWalletId, amount, transactionId, "Failed");
				obj.put("senderWalletBalance", senderWallet==null ? 0 : senderWallet.getBalance());
				
				kafkatemplate.send("EWALLETApp_UPDATED_TOPIC", objectMapper.writeValueAsString(obj));
				return;
			}
			else {
				repo.updateWallet(senderWalletId, -amount);
		        repo.updateWallet(recieverWalletId, amount);
		        obj = this.init(recieverWalletId, senderWalletId, amount, transactionId, "Success");
				
				kafkatemplate.send("EWALLETApp_UPDATED_TOPIC", objectMapper.writeValueAsString(obj));
			}
			
		}catch(Exception e) {
	        obj = this.init(recieverWalletId, senderWalletId, amount, transactionId, "Failed");
			obj.put("errorMessage", e.getMessage());
			kafkatemplate.send("EWALLETApp_UPDATED_TOPIC", objectMapper.writeValueAsString(obj));
		}


	}
	
	private JSONObject init(String receiverId, String senderId, Long amount,String transactionId, String status){
		JSONObject obj = new JSONObject();
		obj.put("transactionId", transactionId);
		obj.put("senderWalletId", senderId);
		obj.put("receiverWalletId", receiverId);
		obj.put("amount", amount);
		obj.put("status", status);
		return obj;

	}
	
	public Long findByWalletId(String id) {
		WalletDetails walletdetails =  repo.findBywalletId(id);
		return walletdetails.getBalance()/100;
	}
}
