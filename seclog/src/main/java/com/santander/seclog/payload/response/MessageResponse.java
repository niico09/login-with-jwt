package com.santander.seclog.payload.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageResponse {
	private String message;
	private String dateTime;
	private String data;

	private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	public MessageResponse(String message, String data) {
	    this.message = message;
	    this.data = data;
		this.dateTime = timeFormatter.format(LocalDateTime.now());
	  }

	public MessageResponse(String s) {
		this.message = s;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
