package org.transaction.service.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.transaction.service.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class UserAuth implements UserDetailsService {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public User loadUserByUsername(String mobilenum) throws UsernameNotFoundException {

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("TransactionService", "transactionservice");

        String URL = "http://localhost:9090/user/mobilenumber/" + mobilenum;
        
        
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, request, String.class);
        JSONObject obj = null;
		try {
			obj = (JSONObject)new JSONParser().parse(response.getBody());
		} 
		catch (ParseException e) {
		}
		JSONArray authoritiesArray = (JSONArray) obj.get("authorities");
		JSONObject authorityObject = (JSONObject) authoritiesArray.get(0);
	    String authorityValue = (String) authorityObject.get("authority");
        return User.builder()
                .username((String)obj.get("mobileNumber"))
                .password((String)obj.get("password"))
                .authorities(authorityValue)
                .build();
    }
}
