package com.example.commons.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlAccessorType(XmlAccessType.FIELD) // JAXB从字段获取配置信息
@XmlRootElement(name = "xml") // JAXB读取XML时根元素名称
public abstract class InMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 由于微信发送给我们的消息字段是首字母大写的，而Java字段首字母大写不符合JavaBean的规范
	// 如果使用JAXB，需要使用@XmlElement注解指定XML里面元素名称和Java字段之间的关系
	// 如果使用Jackson，则需要使用@JsonProperty注解指定Java字段序列化和反序列化的字段名称映射
	@XmlElement(name = "ToUserName")
	@JsonProperty("ToUserName")
	private String toUserName;

	@XmlElement(name = "FromUserName")
	@JsonProperty("FromUserName")
	private String fromUserName;

	@XmlElement(name = "CreateTime")
	@JsonProperty("CreateTime")
	private long createTime;

	@XmlElement(name = "MsgType")
	@JsonProperty("MsgType")
	private String msgType;

	@XmlElement(name = "MsgId")
	@JsonProperty("MsgId")
	private Long msgId;

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public Long getMsgId() {
		return msgId;
	}

	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}
}
