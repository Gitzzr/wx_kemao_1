package com.example.commons.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseError extends ResponseMessage {

	@JsonProperty("errcode")
	private int errorCode;
	@JsonProperty("errmsg")
	private String errorMessage;

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
