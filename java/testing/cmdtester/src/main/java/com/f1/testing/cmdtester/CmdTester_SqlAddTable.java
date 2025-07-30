package com.f1.testing.cmdtester;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.f1.base.Caster;
import com.f1.utils.ArgParser.Arguments;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.table.BasicTable;

public class CmdTester_SqlAddTable extends AbstractCmdTester {

	public CmdTester_SqlAddTable() {
		super("sql_add_table", "add table for sql processingk");
		getArgumentParser().addSwitchRequired("f", "file", "*", "comma delimited list of files");
	}

	@Override
	public void processCommand(CmdTesterMain sim, Arguments arguments) throws Exception {
		String files = arguments.getRequired("f");
		for (String file : SH.split(',', files)) {
			File f = new File(file);
			String text = IOH.readText(f);
			if (text == null) {
				System.err.println("file not found: " + IOH.getFullPath(f));
				continue;
			}
			String[] lines = SH.splitLines(text);
			String[] names = SH.split('|', lines[0]);
			String[] types = SH.split('|', lines[1]);
			List<String> typesList = CH.l(types);
			List<Class> types2 = OH.castAll(typesList, Class.class, true);
			List<Caster<?>> casters = OH.getAllCasters(types2);

			BasicTable table = new BasicTable(types2.toArray(new Class[types2.size()]), names);
			for (int i = 2; i < lines.length; i++) {
				String[] parts = SH.split('|', lines[i]);
				Object[] values = new Object[types2.size()];
				for (int j = 0; j < types2.size(); j++)
					values[j] = casters.get(j).cast(parts[j]);
				table.getRows().addRow(values);
			}
			table.setTitle(SH.beforeFirst(f.getName(), '.', f.getName()));
			System.out.println(table);
			Tableset tables = getTables(sim);
			tables.putTable(table.getTitle(), table);
		}

	}

	public static Tableset getTables(CmdTesterMain sim) {
		Map<String, Object> obj = sim.getObjects();
		Tableset tables = (Tableset) obj.get("TABLES");
		if (tables == null)
			obj.put("TABLES", tables = new TablesetImpl());
		return tables;
	}

}
