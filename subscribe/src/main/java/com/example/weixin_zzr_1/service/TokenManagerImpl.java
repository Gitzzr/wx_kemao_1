package com.example.weixin_zzr_1.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.commons.domain.ResponseError;
import com.example.commons.domain.ResponseMessage;
import com.example.commons.domain.ResponseToken;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TokenManagerImpl implements TokenManager {

	private static final Logger LOG = LoggerFactory.getLogger(TokenManagerImpl.class);
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public String getToken(String account) {
		// 此时目前不考虑任何的具体实现，只是简单获取一下令牌，也不缓存，每次都获取。
		// 实际项目绝对不能这样干，因为获取令牌的接口每天最多能够调用2000次（每个appid）。
		// 这里现在暂时为了简化而不考虑缓存，后面会进行重构。

		String appid = "wxc50a6fb63f0ff551";
		String appsecret = "346f51b53d6d9632250d42f590540091";

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
				return ((ResponseToken) rm).getAccessToken();
			}
		} catch (Exception e) {
			throw new RuntimeException("无法获取令牌，因为：" + e.getLocalizedMessage());
		}

		throw new RuntimeException("无法获取令牌，因为：错误代码=" //
				+ ((ResponseError) rm).getErrorCode() //
				+ "，错误描述=" + ((ResponseError) rm).getErrorMessage());
	}
}
