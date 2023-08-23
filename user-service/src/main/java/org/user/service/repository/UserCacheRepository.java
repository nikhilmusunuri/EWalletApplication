package org.user.service.repository;


import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.user.service.model.UserDetailsDB;

@Repository
public class UserCacheRepository {
	
	public static final String USER_CACHE_KEY_PREFIX = "user::";
    public static final Integer USER_CACHE_KEY_EXPIRY = 600;
    
	@Autowired
	RedisTemplate<String, UserDetailsDB> redistemplate;
	
	
	public void set(UserDetailsDB user) {
		this.redistemplate.opsForValue().set(getKey(user.getId()),user,USER_CACHE_KEY_EXPIRY,TimeUnit.SECONDS);
	}
	
	public UserDetailsDB get(Integer id) {
		Object result = this.redistemplate.opsForValue().get(getKey(id));
        return (result == null) ? null :  (UserDetailsDB) result;
	}
	
	private String getKey(Integer userId){
        return USER_CACHE_KEY_PREFIX + userId;
    }
}
