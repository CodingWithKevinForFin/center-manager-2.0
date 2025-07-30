package com.f1.testing.cmdtester;

import com.f1.utils.ArgParser;
import com.f1.utils.agg.LongAggregator;

public abstract class AbstractCmdTester implements CmdTester {

	private String name;
	private ArgParser arguments;
	private String description;

	public static String[] a(String... a) {
		return a;
	}

	public AbstractCmdTester(String name, String description) {
		this.name = name;
		this.arguments = new ArgParser(name);
		this.description = description;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public ArgParser getArgumentParser() {
		return arguments;
	}

	protected static void describe(LongAggregator merchantIdsAdded, String string) {
		if (merchantIdsAdded.getCount() == 0)
			System.out.println("No " + string + " added");
		else
			System.out.println("Added " + merchantIdsAdded.getCount() + " " + string + ", ids:[" + merchantIdsAdded.getMin() + "..." + merchantIdsAdded.getMax() + "]");

	}

}
