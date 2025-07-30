package com.f1.testing.cmdtester;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Table;
import com.f1.utils.ArgParser.Arguments;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.BasicTable;

public class CmdTester_SqlShow extends AbstractCmdTester {

	public CmdTester_SqlShow() {
		super("sql_show", "show tables");
		getArgumentParser().addSwitchOptional("q", "quiet", "*", "show data too");
	}
	@Override
	public void processCommand(CmdTesterMain sim, Arguments arguments) throws Exception {
		Boolean quiet = arguments.getOptional("q", Boolean.FALSE);
		Tableset tables = CmdTester_SqlAddTable.getTables(sim);
		for (String name : tables.getTableNames()) {
			Table table = tables.getTable(name);
			if (quiet) {
				table = new BasicTable(table);
				table.getRows().clear();
			}
			System.out.println(table);
		}
	}

	public static Map<String, Table> getTables(CmdTesterMain sim) {
		Map<String, Object> obj = sim.getObjects();
		Map<String, Table> tables = (Map<String, Table>) obj.get("TABLES");
		if (tables == null)
			obj.put("TABLES", tables = new HashMap<String, Table>());
		return tables;
	}

}
