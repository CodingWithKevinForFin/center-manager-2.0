package com.f1.ami.amicommon;

import com.f1.utils.SH;

public class AmiDatasourceException extends Exception {

	public static final int INITIALIZATION_FAILED = 1;
	public static final int CONNECTION_FAILED = 2;
	public static final int TIMEOUT_EXCEEDED = 8;
	public static final int SCHEMA_ERROR = 3;
	public static final int UNKNOWN_ERROR = 4;
	public static final int SYNTAX_ERROR = 5;
	public static final int DIRECTIVE_ERROR = 6;
	public static final int UNSUPPORTED_OPERATION_ERROR = 7;

	private final int code;

	public AmiDatasourceException(int code, String message, Throwable cause) {
		super(SH.is(message) ? message : toString(code), cause);
		this.code = code;
	}

	public AmiDatasourceException(int code, String message) {
		super(SH.is(message) ? message : toString(code));
		this.code = code;
	}

	public AmiDatasourceException(int code, Throwable cause) {
		super(toString(code), cause);
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}
	@Override
	public String getMessage() {
		return super.getMessage();
	}
	public static String toString(int code) {
		switch (code) {
			case TIMEOUT_EXCEEDED:
				return "Timeout Exceeeded";
			case INITIALIZATION_FAILED:
				return "Initialization Failed";
			case CONNECTION_FAILED:
				return "Connection Failed";
			case SCHEMA_ERROR:
				return "Schema Error";
			case UNKNOWN_ERROR:
				return "Unknown Error";
			case SYNTAX_ERROR:
				return "Syntax Error";
			case DIRECTIVE_ERROR:
				return "Directive Error";
			case UNSUPPORTED_OPERATION_ERROR:
				return "Unsupported Operation";
			default:
				return "Code " + code;
		}
	}

}
