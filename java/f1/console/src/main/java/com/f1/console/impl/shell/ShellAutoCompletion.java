package com.f1.console.impl.shell;

public interface ShellAutoCompletion {

	/**
	 * @return the text to use for autocompletion (on the command line)
	 */
	public String getAutoCompletion();

	/**
	 * @return the text to display to the user
	 */
	public String getText();

}
