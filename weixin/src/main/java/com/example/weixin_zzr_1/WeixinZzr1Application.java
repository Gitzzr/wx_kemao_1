package com.example.weixin_zzr_1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.commons.domain.InMessage;
import com.example.commons.service.JsonRedisSerializer;

@SpringBootApplication
public class WeixinZzr1Application {
	
	// 相当于Spring的XML配置方式中的<bean>元素
	@Bean
	public RedisTemplate<String, InMessage> inMessageTemplate(//
			@Autowired RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, InMessage> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		
		// 设置一个序列化程序，就可以非常方便自动序列化！
		// Redis是键值对方式存储数据的，所以其实KeySerializer是把键序列化成可以传输的数据。
		// 由于泛型的时候已经确定，Key其实是String，所以可以使用系统默认的
//		template.setKeySerializer(new StringRedisSerializer());

		// 由于不确定是哪个类型，InMessage只是一个父类，它有许多不同的子类。
		// 因此扩展Jackson2JsonRedisSerializer变得极其重要：重写方法、不要构造参数
		template.setValueSerializer(new JsonRedisSerializer());
//		template.setDefaultSerializer(new JsonRedisSerializer());
				
		return template;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(WeixinZzr1Application.class, args);
	}

}
