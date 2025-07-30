package com.f1.utils.ftp.client;

public class FtpException extends RuntimeException {

	final private int errorCode;

	public FtpException() {
		super();
		this.errorCode = FtpConstants.CODE_NONE;
	}

	public FtpException(String message, Throwable cause) {
		super(message, cause);
		this.errorCode = FtpConstants.CODE_NONE;
	}

	public FtpException(String message) {
		super(message);
		this.errorCode = FtpConstants.CODE_NONE;
	}
	public FtpException(int code, String message) {
		super(message);
		this.errorCode = code;
	}

	public FtpException(Throwable cause) {
		super(cause);
		this.errorCode = FtpConstants.CODE_NONE;
	}

	public int getErrorCode() {
		return errorCode;
	}

}
