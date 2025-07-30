package com.f1.ami.sqltests;

import java.util.HashMap;
import java.util.Map;

public class ArgumentParser {
	private final Map<String, String> optsMap = new HashMap<String, String>();

	/**
	 * We are using option/value pairs here, like "-option1 value1 -option2 - value2"
	 * 
	 * @param args
	 */
	public ArgumentParser(String[] args) {

		for (int i = 0; i < args.length; i++) {
			switch (args[i].charAt(0)) {
				case '-':
					if (args[i].length() < 2)
						throw new IllegalArgumentException("Not a valid argument: " + args[i]);
					if (args.length - 1 == i)
						throw new IllegalArgumentException("Expected arg after: " + args[i]);
					// -opt
					optsMap.put(args[i], args[i + 1]);
					i++;
					break;
				default:
					break;
			}
		}
	}

	public String getRequiredOption(String key) {
		String val = optsMap.get(key);
		if (val == null)
			throw new IllegalArgumentException("Required option " + key + " not provided");
		return val;
	}

	public String getOption(String key) {
		return optsMap.get(key);
	}
}
