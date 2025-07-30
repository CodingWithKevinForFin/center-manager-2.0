package com.f1.ami.amicommon;

import com.f1.base.Caster;
import com.f1.utils.OH;

public class AmiFactoryOption {

	final private String name;
	final private Class<?> type;
	final private Caster<?> caster;
	final private boolean isRequired;
	final private String help;

	public AmiFactoryOption(String name, Class<?> type) {
		this(name, type, false, name);
	}
	public AmiFactoryOption(String name, Class<?> type, boolean isRequired) {
		this(name, type, isRequired, name);
	}
	public AmiFactoryOption(String name, Class<?> type, boolean isRequired, String help) {
		this.name = name;
		this.type = type;
		this.caster = OH.getCaster(type);
		this.isRequired = isRequired;
		this.help = help;
	}

	/**
	 * @return name of the option
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * @return help a short but helpful hint on using the option
	 */
	public String getHelp() {
		return this.help;
	}

	/**
	 * @return true if the option must be supplied
	 */
	public boolean getRequired() {
		return this.isRequired;
	}

	/**
	 * 
	 * @return the type of value that this option can take, for example, String.class or Integer.class
	 */
	public Class<?> getType() {
		return this.type;
	}

	/**
	 * @return capable of casting the supplied string to the correct type
	 */
	final public Caster<?> getCaster() {
		return this.caster;
	}

	@Override
	public String toString() {
		return "UseOptionDefition [name=" + name + ", type=" + type + ", isRequired=" + isRequired + ", help=" + help + "]";
	}

}
