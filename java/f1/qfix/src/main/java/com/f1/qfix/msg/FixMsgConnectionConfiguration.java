package com.f1.qfix.msg;

import com.f1.base.Factory;
import com.f1.msg.impl.BasicMsgConnectionConfiguration;
import com.f1.utils.PropertyController;

public class FixMsgConnectionConfiguration extends BasicMsgConnectionConfiguration {

	private PropertyController props;

	public FixMsgConnectionConfiguration(String name, Factory<String, String> logNamer, PropertyController props) {
		super(name, logNamer);
		this.props = props.getSubPropertyController("qfix.");
	}
	public FixMsgConnectionConfiguration(String name, PropertyController props) {
		super(name);
		this.props = props.getSubPropertyController("qfix.");

	}
	public PropertyController getConfig() {
		return props;
	}

}
