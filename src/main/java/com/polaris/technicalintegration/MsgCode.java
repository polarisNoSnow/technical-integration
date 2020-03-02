package com.polaris.technicalintegration;

public enum MsgCode {
	SYSTEM_SUCCESS(100, "处理成功"),
	SYSTEM_ERROR(300, "处理出错"),
	TOKEN_IS_ERROR(405, "用户验证失败"),
	SYSTEM_BUSY(10001, "访问频次达到限制");


	private int msgCode;
	private String message;

	MsgCode(int msgCode, String message) {
		this.msgCode = msgCode;
		this.message = message;
	}

	public int getMsgCode() {
		return msgCode;
	}

	public String getMessage() {
		return message;
	}
}