package com.example.weixin_zzr_1.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.commons.domain.User;
import com.example.commons.domain.text.TextOutMessage;
import com.example.commons.service.TokenManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WeixinProxy {

	private static final Logger LOG = LoggerFactory.getLogger(WeixinProxy.class);
	@Autowired
	private TokenManager tokenManager;
	@Autowired
	private ObjectMapper objectMapper;

	private HttpClient client = HttpClient.newBuilder()//
			.version(Version.HTTP_1_1)// HTTP 1.1
			.build();

	public User getUser(String account, String openId) {
		String token = this.tokenManager.getToken(account);
		String url = "https://api.weixin.qq.com/cgi-bin/user/info"//
				+ "?access_token=" + token//
				+ "&openid=" + openId//
				+ "&lang=zh_CN";

		HttpRequest request = HttpRequest.newBuilder(URI.create(url))//
				.GET()// 以GET方式请求
				.build();
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString(Charset.forName("UTF-8")));

			String body = response.body();

			LOG.trace("调用远程接口返回的内容 : \n{}", body);

			if (body.contains("errcode")) {
				LOG.error("调用远程接口出现问题：" + body);
			} else {
				User user = objectMapper.readValue(body, User.class);
				return user;
			}
		} catch (Exception e) {
			LOG.error("调用远程接口出现问题：" + e.getLocalizedMessage(), e);
		}
		return null;
	}

	public void sendText(String account, String openId, String content) {
		TextOutMessage msg = new TextOutMessage(openId, content);
		// 获取令牌
		String token = this.tokenManager.getToken(account);
		try {
			// 转换消息对象为JSON
			String json = this.objectMapper.writeValueAsString(msg);
			// 发送消息
			String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + token;
			HttpRequest request = HttpRequest.newBuilder(URI.create(url))//
					.POST(BodyPublishers.ofString(json, Charset.forName("UTF-8")))// POST方式发送
					.build();

			// 异步方式发送请求
			CompletableFuture<HttpResponse<String>> future//
					= client.sendAsync(request, BodyHandlers.ofString(Charset.forName("UTF-8")));
			future.thenAccept(response -> {
				String body = response.body();
				LOG.trace("发送客服消息返回的内容 : \n{}", body);
			});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}
