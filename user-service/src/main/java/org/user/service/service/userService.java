package org.user.service.service;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.user.service.model.UserDetailsDB;
import org.user.service.repository.UserCacheRepository;
import org.user.service.repository.UserRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class userService implements UserDetailsService {
	
	private static final String User_Create_Topic="user_created";
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
    PasswordEncoder passwordEncoder;
	
	@Autowired
	UserRepository repo;
	
	@Autowired
	UserCacheRepository cache;
	
	@Autowired
	KafkaTemplate<String, String> kafkatemplate;
	
	public void createUser(UserDetailsDB userdetails) throws JsonProcessingException {
		userdetails.setPassword(passwordEncoder.encode(userdetails.getPassword()));
		repo.save(userdetails);
		JSONObject userObject = new JSONObject();
		userObject.put("phone", userdetails.getMobileNumber());
		userObject.put("email",userdetails.getEmail());
		kafkatemplate.send(User_Create_Topic,objectMapper.writeValueAsString(userObject));
	}
	
	public UserDetailsDB getUserById(Integer id) throws Exception {
		UserDetailsDB user; 
		user = cache.get(id);
		if(user != null) {
			return user;
		}
		else {
			user = repo.findById(id).orElseThrow(()->new Exception());
			cache.set(user);
		}
		return user;
	}
	
	public UserDetailsDB getUserByMobile(String number) {
		return repo.findBymobileNumber(number);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return repo.findBymobileNumber(username);
	}
}
