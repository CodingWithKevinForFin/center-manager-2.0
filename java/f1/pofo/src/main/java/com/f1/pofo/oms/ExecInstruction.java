package com.f1.pofo.oms;

import com.f1.base.ValuedEnum;

/**
 * See FIX tag 18
 * 
 */
public enum ExecInstruction implements ValuedEnum<Character> {

	/** M */
	MIDPOINT_PEGGED('M'),

	/** P */
	MARKET_PEG('P'),

	/** R */
	PRIMARY_PEG('R');

	private char value;

	ExecInstruction(char value) {
		this.value = value;
	}

	@Override
	public Character getEnumValue() {
		return value;
	}

	public static String addExecInstruction(String execInstruction, ExecInstruction value) {
		if (execInstruction == null) {
			return "" + value.getEnumValue();
		} else
			return execInstruction + " " + value.getEnumValue();
	}

}
