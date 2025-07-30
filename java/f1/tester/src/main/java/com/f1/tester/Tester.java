package com.f1.tester;

import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public interface Tester {

	void setMain(String main);

	void addVmOptions(String... vmOption);

	void addArguments(String... argument);

	void setClassPath(String classPath);

	void setStdOut(OutputStream stdout);

	void setStdErr(OutputStream stderr);

	void setStartShouldBlock(boolean should);

	void setMaxRuntime(long maxRuntime, TimeUnit timeUnit);

	void startTest();

	void stopTest();

	boolean isRunning();

	public void addListener(TesterListener listener);

	public void removeListener(TesterListener listener);

}
