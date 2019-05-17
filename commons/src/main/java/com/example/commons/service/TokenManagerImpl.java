package com.example.commons.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import com.example.commons.domain.ResponseError;
import com.example.commons.domain.ResponseMessage;
import com.example.commons.domain.ResponseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TokenManagerImpl implements TokenManager {

	private static final Logger LOG = LoggerFactory.getLogger(TokenManagerImpl.class);
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	@Qualifier("tokenRedisTemplate")
	private RedisTemplate<String, ResponseToken> tokenRedisTemplate;

	@Override
	public String getToken(String account) {
		BoundValueOperations<String, ResponseToken> ops = tokenRedisTemplate.boundValueOps("weixin_access_token");
		ResponseToken token = ops.get();
		LOG.trace("获取令牌，结果： {}", token);
		if (token == null) {
			// 增加事务锁
			for (int i = 0; i < 10; i++) {
				Boolean locked = tokenRedisTemplate.opsForValue()
						// 如果key不存在（Absent）就设置一个键值对到数据库里面
						// 如果设置成功返回true，否则返回false
						.setIfAbsent("weixin_access_token_lock", new ResponseToken());
				LOG.trace("没有令牌，增加事务锁，结果：{}", locked);
				if (locked == true) {
					// 增加事务锁成功
					try {
						// 再次检查token是否在数据库里面!
						token = ops.get();
						if (token == null) {
							LOG.trace("再次检查令牌，还是没有，调用远程接口");
							token = getRemoteToken(account);

							// 把token存储到Redis里面
							ops.set(token);
							// 设置令牌的过期时间，减60表示提前一分钟去更新令牌
							ops.expire(token.getExpiresIn() - 60, TimeUnit.SECONDS);
						} else {
							LOG.trace("再次检查令牌，已经有令牌在Redis里面，直接使用");
						}
						// 不需要继续循环
						break;
					} finally {
						LOG.trace("删除令牌事务锁");
						// 删除事务锁
						tokenRedisTemplate.delete("weixin_access_token_lock");
						synchronized (this) {
							// 通知其他的线程继续执行！
							this.notifyAll();
						}
					}

				} else {
					// 增加事务锁不成功，要等待1分钟再重试
					synchronized (this) {
						try {
							LOG.trace("其他线程锁定了令牌，无法获得锁，等待...");
							this.wait(1000 * 60);
						} catch (InterruptedException e) {
							LOG.error("等待获取分布式的事务锁出现问题：" + e.getLocalizedMessage(), e);
							break;
						}
					}
				}
			}
		}
		if (token != null) {
			return token.getAccessToken();
		}
		return null;
	}

	public ResponseToken getRemoteToken(String account) {
		// 此时目前不考虑任何的具体实现，只是简单获取一下令牌，也不缓存，每次都获取。
		// 实际项目绝对不能这样干，因为获取令牌的接口每天最多能够调用2000次（每个appid）。
		// 这里现在暂时为了简化而不考虑缓存，后面会进行重构。

		String appid = "wx6001c904cb6abb1d";
		String appsecret = "9d6dec6f93c00a09c087c8d507244d60";

		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"//
				+ "&appid=" + appid//
				+ "&secret=" + appsecret;

		HttpClient hc = HttpClient.newBuilder()//
				.version(Version.HTTP_1_1)// HTTP的协议版本号
				.build();
		HttpRequest request = HttpRequest.newBuilder(URI.create(url))//
				.GET()// 发送GET请求
				.build();

		ResponseMessage rm;
		try {
			// BodyHandlers是一个工具类，它提供了许多的响应体处理程序
			// ofString表示把响应体转换为String的一个处理程序
			// Charset.forName("UTF-8")使用UTF-8的字符编码
			HttpResponse<String> response = hc.send(request, BodyHandlers.ofString(Charset.forName("UTF-8")));

			String body = response.body();

			LOG.trace("调用远程接口返回的内容 : \n{}", body);

			if (body.contains("errcode")) {
				// 出现了错误
				rm = objectMapper.readValue(body, ResponseError.class);
				rm.setStatus(2);
			} else {
				// 成功
				rm = objectMapper.readValue(body, ResponseToken.class);
				rm.setStatus(1);
			}
			// return rm;
			if (rm.getStatus() == 1) {
				// return ((ResponseToken) rm).getAccessToken();
				return ((ResponseToken) rm);
			}
		} catch (Exception e) {
			throw new RuntimeException("无法获取令牌，因为：" + e.getLocalizedMessage());
		}

		throw new RuntimeException("无法获取令牌，因为：错误代码=" //
				+ ((ResponseError) rm).getErrorCode() //
				+ "，错误描述=" + ((ResponseError) rm).getErrorMessage());
	}
}
