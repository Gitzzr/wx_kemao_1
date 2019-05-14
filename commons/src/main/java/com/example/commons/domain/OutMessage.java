package com.example.commons.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class OutMessage {

	@JsonProperty("touser")
	private String toUser;
	@JsonProperty("msgtype")
	private String messageType;

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
}
