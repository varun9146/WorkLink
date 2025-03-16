package com.worklink.model;

public class ValidateOTP {

	private String message;
	private String session;

	public ValidateOTP(String message, String session) {
		super();
		this.message = message;
		this.session = session;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	@Override
	public String toString() {
		return "ValidateOTP [message=" + message + ", session=" + session + "]";
	}

}
