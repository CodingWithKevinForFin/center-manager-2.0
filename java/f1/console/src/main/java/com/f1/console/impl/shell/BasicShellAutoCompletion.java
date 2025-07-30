package com.f1.console.impl.shell;

public class BasicShellAutoCompletion implements ShellAutoCompletion {

	public static final BasicShellAutoCompletion EMPTY = new BasicShellAutoCompletion("", "");

	final private String text;
	final private String autoCompletion;

	public BasicShellAutoCompletion(String text, String autoCompletion) {
		this.text = text;
		this.autoCompletion = autoCompletion;
	}

	@Override
	public String getAutoCompletion() {
		return autoCompletion;
	}

	@Override
	public String getText() {
		return text;
	}

}
