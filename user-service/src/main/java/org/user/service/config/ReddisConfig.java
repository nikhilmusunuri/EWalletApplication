package org.user.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.user.service.model.UserDetailsDB;



@Configuration
public class ReddisConfig {

	@Value("${redis.host.url}")
	String hosturl;
	
	@Value("${redis.port}")
	Integer port;
	
	@Value("${redis.auth.password}")
	String password;
	
	@Bean
	public LettuceConnectionFactory getConnection() {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(this.hosturl, this.port);
		configuration.setPassword(this.password);
		
		LettuceConnectionFactory connectionfactory = new LettuceConnectionFactory(configuration);
		
		return connectionfactory;
	}
	
	@Bean
	public RedisTemplate<String, UserDetailsDB> getTemplate(){
		RedisTemplate<String, UserDetailsDB> redistemplate = new RedisTemplate<>();
		redistemplate.setConnectionFactory(getConnection());
		
		redistemplate.setKeySerializer(new StringRedisSerializer());
		redistemplate.setValueSerializer(new JdkSerializationRedisSerializer());
		
		return redistemplate;
	}
}
