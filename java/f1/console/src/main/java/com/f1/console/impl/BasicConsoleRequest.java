package com.f1.console.impl;

import com.f1.console.ConsoleRequest;

public class BasicConsoleRequest implements ConsoleRequest {

	private final String text;
	private boolean saveToHistory;

	public BasicConsoleRequest(String text, boolean saveToHistory) {
		this.text = text;
		this.saveToHistory = saveToHistory;
	}

	@Override
	public String getText() {
		return text;
	}

}
