package com.f1.utils.fix;

/**
 * indicates an exception while parsing a fix message
 * 
 * @see FixParser#parse(String)
 */
public class FixParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FixParseException() {
		super();
	}

	public FixParseException(String message_, Throwable cause_) {
		super(message_, cause_);
	}

	public FixParseException(String message_) {
		super(message_);
	}

	public FixParseException(Throwable cause_) {
		super(cause_);
	}

}
