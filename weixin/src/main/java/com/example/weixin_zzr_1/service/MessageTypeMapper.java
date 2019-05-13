package com.example.weixin_zzr_1.service;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.commons.domain.InMessage;
import com.example.commons.domain.image.ImageInMessage;
import com.example.commons.domain.text.TextInMessage;

public class MessageTypeMapper {

	private static Map<String, Class<? extends InMessage>> typeMap = new ConcurrentHashMap<>();

	// 通过一个Map记录了消息类型和类的关系
	static {
		typeMap.put("text", TextInMessage.class);
		typeMap.put("image", ImageInMessage.class);

		typeMap.put("vioce", TextInMessage.class);
		typeMap.put("video", TextInMessage.class);
		typeMap.put("location", TextInMessage.class);
		typeMap.put("shortvideo", TextInMessage.class);
		typeMap.put("link", TextInMessage.class);

		typeMap.put("event", TextInMessage.class);
	}

	// 通过消息类型获取对应的类
	@SuppressWarnings("unchecked")
	public static <T extends InMessage> Class<T> getClass(String type) {
		return (Class<T>) typeMap.get(type);
	}
}

