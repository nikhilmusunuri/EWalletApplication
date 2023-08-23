package org.transaction.service.dto;


import java.util.UUID;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;


import org.transaction.service.model.Transaction;
import org.transaction.service.model.TransactionStatus;
import org.transaction.service.model.User;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransaction {
	
	@NotBlank
	private String receiver;
	
//	@NotBlank
//	private String sender;
	
	@Min(1)
	private Long amount;
	private String reason;
	
	public Transaction to(User sender) {
		return Transaction.builder().reason(this.reason).receiverId(this.receiver).senderId(sender.getUsername()).amount(this.amount).externalId(UUID.randomUUID().toString()).status(TransactionStatus.Pending).build();
	}
}
