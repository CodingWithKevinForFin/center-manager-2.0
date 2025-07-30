package com.f1.testing.cmdtester;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Table;
import com.f1.utils.ArgParser.Arguments;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class CmdTester_Sql extends AbstractCmdTester {

	public CmdTester_Sql() {
		super("sql", "run sql");
		getArgumentParser().addSwitchRequired("s", "sql", "*", "sql to execute");
		getArgumentParser().addSwitchOptional("t", "target", "*", "target table to results as");
	}
	@Override
	public void processCommand(CmdTesterMain sim, Arguments arguments) throws Exception {
		String sql = arguments.getRequired("s");
		String target = arguments.getOptional("t");
		Tableset tables = CmdTester_SqlAddTable.getTables(sim);
		Table table = sim.getSqlProcessor().process(sql, new TopCalcFrameStack(tables, EmptyCalcFrame.INSTANCE));
		if (target != null) {
			table.setTitle(target);
			tables.putTable(target, table);
		}
		System.out.println(table);
	}

	public static Map<String, Table> getTables(CmdTesterMain sim) {
		Map<String, Object> obj = sim.getObjects();
		Map<String, Table> tables = (Map<String, Table>) obj.get("TABLES");
		if (tables == null)
			obj.put("TABLES", tables = new HashMap<String, Table>());
		return tables;
	}

}
