package org.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.user.service.model.UserDetailsDB;
import org.user.service.repository.UserRepository;

@SpringBootApplication
public class UserServiceApplication implements CommandLineRunner
{
	@Autowired
	UserRepository userrepository;
	
	
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
	
    @Override
    public void run(String... args) throws Exception {
    	userrepository.save(UserDetailsDB.builder()
                        .mobileNumber("TransactionService")
                        .name("txn")
                        .password(new BCryptPasswordEncoder().encode("transactionservice"))
                        .authorities("service")
                .build());
    }
}
