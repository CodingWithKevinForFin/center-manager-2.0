package com.f1.sample;

import com.f1.base.Console;

@Console(help = "sample classs accessed by console")
public class MySampleClass {

	@Console(help = "shows a simple string")
	public String showTest1() {
		return "Hello world";
	}

	int i = 0;

	@Console(help = "increments an internl counter, and returns the prior value")
	public int showNext() {
		return i++;
	}
}
