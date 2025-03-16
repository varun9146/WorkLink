package com.worklink.utills;

public class ApiError<M> {
	private M message;

	public ApiError(M message) {
		this.message = message;
	}

	public M getMessage() {
		return message;
	}

	public void setMessage(M message) {
		this.message = message;
	}
}
