package org.transaction.service.service;



import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.transaction.service.model.Transaction;
import org.transaction.service.model.TransactionStatus;
import org.transaction.service.repository.TransactionRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TraansactionService {

	private static final String Transaction_Created = "transaction_created";
	
	private ObjectMapper objmapper = new ObjectMapper();
	
	private RestTemplate resttemplate = new RestTemplate();
	
	@Autowired
	TransactionRepository repo;
	
	@Autowired
	KafkaTemplate<String,String> template;
	
	public String createtranx(Transaction txn) throws JsonProcessingException {
		repo.save(txn);
		
		JSONObject obj = new JSONObject();
		obj.put("senderId", txn.getSenderId());
		obj.put("recieverId", txn.getReceiverId());
		obj.put("amount", txn.getAmount());
		obj.put("transactionId", txn.getExternalId());
		
		template.send(Transaction_Created, objmapper.writeValueAsString(obj));
		
		return txn.getExternalId();
	}
	
	@KafkaListener(topics = {"EWALLETApp_UPDATED_TOPIC"}, groupId = "ewallet")
	public void updateTransaction(String msg) throws org.json.simple.parser.ParseException, JsonProcessingException{
		JSONObject obj = (JSONObject) new JSONParser().parse(msg);
		String externaltxnId = (String)obj.get("transactionId");
		String receiverwalletId = (String) obj.get("receiverWalletId");
		String senderwalletId = (String) obj.get("senderWalletId");
		String walletUpdateStatus = (String) obj.get("status");
		Long amount = (Long)obj.get("amount");
		TransactionStatus txnStatus;
		
		
		
		if(walletUpdateStatus.equals("Failed")) {
			txnStatus=TransactionStatus.Failed;
			repo.updateTransaction(externaltxnId, TransactionStatus.Failed);
		}else {
			txnStatus=TransactionStatus.Success;
			repo.updateTransaction(externaltxnId, TransactionStatus.Success);
		}
		
		
		String senderEmail = this.getEmail(senderwalletId);
		String receiverEmail = this.getEmail(receiverwalletId);
		
		obj=new JSONObject();
		obj.put("transactionId", externaltxnId);
		obj.put("status", txnStatus.toString());
		obj.put("amount", amount);
		obj.put("receiverEmail", receiverEmail);
		obj.put("senderEmail", senderEmail);
		obj.put("receiverPhone", receiverwalletId);
		obj.put("senderPhone", senderwalletId);
		
		
		template.send("Transaction_Completed_Topic",objmapper.writeValueAsString(obj));
	}
	
	public String getEmail(String mobilenumber) {
		HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("TransactionService", "transactionservice");

        String URL = "http://localhost:9090/user/mobilenumber/" + mobilenumber;
        
        
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<String> response = resttemplate.exchange(URL, HttpMethod.GET, request, String.class);
        JSONObject obj1 = null;
        try {
        	obj1 = (JSONObject)new JSONParser().parse(response.getBody());
		} 
		catch (ParseException e) {
		}
        if(obj1!=null) {
        	return (String)obj1.get("email");
        }else {
        	return "failed";
        }
		
	}
}
