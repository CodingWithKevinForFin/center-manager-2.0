/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg.impl;

import com.f1.base.Factory;
import com.f1.msg.MsgConnectionConfiguration;
import com.f1.utils.impl.PassThroughFactory;

public class BasicMsgConnectionConfiguration implements MsgConnectionConfiguration {

	final private String name;
	final private Factory<String, String> logNamer;

	public BasicMsgConnectionConfiguration(String name) {
		this.name = name;
		this.logNamer = PassThroughFactory.INSTANCE;
	}

	public BasicMsgConnectionConfiguration(String name, Factory<String, String> logNamer) {
		this.name = name;
		this.logNamer = logNamer;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "name=" + name;
	}

	@Override
	public Factory<String, String> getLogNamer() {
		return logNamer;
	}

}
