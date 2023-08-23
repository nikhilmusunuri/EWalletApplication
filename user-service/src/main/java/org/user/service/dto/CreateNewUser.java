package org.user.service.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;


import org.user.service.model.UserDetailsDB;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNewUser {
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String mobileNumber;
	
	private String email;
	
	private String password;
	
	@Min(18)
	private int age;
	
	public UserDetailsDB to() {
		return UserDetailsDB.builder().name(this.name).password(this.password).mobileNumber(this.mobileNumber).email(this.email).age(this.age).authorities("user").build();
	}
}
