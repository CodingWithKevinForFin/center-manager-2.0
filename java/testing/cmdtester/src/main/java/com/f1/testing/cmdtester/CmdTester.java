package com.f1.testing.cmdtester;

import com.f1.utils.ArgParser;
import com.f1.utils.ArgParser.Arguments;

public interface CmdTester {

	abstract String getName();
	abstract String getDescription();
	abstract ArgParser getArgumentParser();
	abstract void processCommand(CmdTesterMain sim, Arguments arguments) throws Exception;

}
