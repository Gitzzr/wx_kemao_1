package com.example.commons.config;

import java.util.ArrayList;
import java.util.List;

import com.example.commons.domain.InMessage;
import com.example.commons.domain.ResponseToken;
import com.example.commons.domain.event.EventInMessage;
import com.example.commons.service.JsonRedisSerializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

public interface EventListenerConfig extends//
		// 表示命令行执行的程序，要求实现一个run方法，在run方法里面启动一个线程等待停止通知
		CommandLineRunner, //
		// 当mvn spring-boot:stop命令执行以后，会发送一个停止的命令给Spring容器。
		// Spring容器在收到此命令以后，会执行停止，于是在停止之前会调用DisposableBean里面的方法。
		DisposableBean {

	// 这是一个停止监视器，等待是否停止的通知
	public final Object stopMonitor = new Object();

	@Override
	public default void run(String... args) throws Exception {
		new Thread(() -> {
			synchronized (stopMonitor) {
				try {
					// 等待停止通知
					stopMonitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public default void destroy() throws Exception {
		// 发送停止通知
		synchronized (stopMonitor) {
			stopMonitor.notify();
		}
	}

	// 相当于Spring的XML配置方式中的<bean>元素
	@Bean
	public default RedisTemplate<String, InMessage> inMessageTemplate(//
			@Autowired RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, InMessage> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);

		// 由于不确定是哪个类型，InMessage只是一个父类，它有许多不同的子类。
		// 因此扩展Jackson2JsonRedisSerializer变得极其重要：重写方法、不要构造参数
		template.setValueSerializer(new JsonRedisSerializer());

		return template;
	}

	@Bean
	public default RedisTemplate<String, ResponseToken> tokenRedisTemplate(//
			@Autowired RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, ResponseToken> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setValueSerializer(new JsonRedisSerializer());

		return template;
	}

	@Bean
	public default MessageListenerAdapter messageListener(
			@Autowired RedisTemplate<String, InMessage> inMessageTemplate) {
		MessageListenerAdapter adapter = new MessageListenerAdapter();
		// 共用模板里面的序列化程序
		adapter.setSerializer(inMessageTemplate.getValueSerializer());

		// 设置消息处理程序的代理对象
		adapter.setDelegate(this);
		// 设置代理对象里面哪个方法用于处理消息，设置方法名
		adapter.setDefaultListenerMethod("handle");

		return adapter;
	}

	@Bean
	public default RedisMessageListenerContainer messageListenerContainer(//
			@Autowired RedisConnectionFactory redisConnectionFactory, //
			@Autowired MessageListener l) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		// 可以监听多个通道的消息
		List<Topic> topics = new ArrayList<>();

		// 监听具体某个通道
		topics.add(new ChannelTopic("zzr_1_event"));
		container.addMessageListener(l, topics);

		return container;
	}

	public void handle(EventInMessage msg);
}
