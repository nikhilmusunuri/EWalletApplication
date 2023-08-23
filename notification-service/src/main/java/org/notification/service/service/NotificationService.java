package org.notification.service.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

	@Autowired
	SimpleMailMessage simplemailmessage;
	
	@Autowired
	JavaMailSender javamailsender;
	
	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
	
	@KafkaListener(topics= {"Transaction_Completed_Topic"}, groupId="ewallet")
	public void notify(String mssg) throws ParseException {
		
		JSONObject obj = (JSONObject)new JSONParser().parse(mssg);
		String txnstatus = (String) obj.get("status");
		String txnid = (String) obj.get("transactionId");
		Long amount = (Long) obj.get("amount");
		String senderEmail = (String) obj.get("senderEmail");
		String receiverEmail = (String) obj.get("receiverEmail");
		
		String sendermssg = getSenderMessage(txnstatus, amount, txnid);
		String receivermssg = getReceiverMessage(txnstatus, amount, senderEmail);
		
		logger.debug("Received Kafka message: {}", senderEmail);
		logger.debug("Received Kafka message: {}", receiverEmail);
		if(sendermssg != null && sendermssg.length()>0) {
			simplemailmessage.setTo(senderEmail);
			simplemailmessage.setFrom("vtu14194@veltech.edu.in");
			simplemailmessage.setSubject("EWallet Transaction");
			simplemailmessage.setText(sendermssg);
			javamailsender.send(simplemailmessage);
		}
		
		if(receivermssg != null && receivermssg.length()>0) {
			simplemailmessage.setTo(receiverEmail);
			simplemailmessage.setFrom("vtu14194@veltech.edu.in");
			simplemailmessage.setSubject("EWallet Transaction");
			simplemailmessage.setText(receivermssg);
			javamailsender.send(simplemailmessage);
		}
		
	}
	
    private String getSenderMessage(String transactionStatus, Long amount, String transactionId){
        String msg = "";
        if(transactionStatus.equals("Failed")){
            msg = "Hi!! Your transaction with transaction id =" + transactionId + "amount = " + amount/100 + "INR has failed. Please try again";
        }else{
            msg = "Hi!! Your account has been debited with amount " + amount/100 + "INR , transaction id = " + transactionId;
        }

        return msg;
    }

    private String getReceiverMessage(String transactionStatus, Long amount, String senderEmail){
        String msg = "";
        if(transactionStatus.equals("Success")){
            msg = "Hi!! Your account has been credit with amount " + amount/100 + "INR for the transaction done by user " + senderEmail;
        }

        return msg;
    }
}
