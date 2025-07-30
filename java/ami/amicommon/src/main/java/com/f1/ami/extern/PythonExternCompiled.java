package com.f1.ami.extern;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptException;

import org.python.core.PyException;
import org.python.util.PythonInterpreter;

import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.amiscript.AmiCalcFrameStack;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.WriterLineBuffer;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.ExternCompiled;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.MutableCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class PythonExternCompiled implements ExternCompiled {
	protected static final Logger log = LH.get();
	private final CalcTypes variableTypes;
	private final String code;
	private final MethodFactoryManager mfm;

	public PythonExternCompiled(CalcTypesStack context, String code) {
		this.mfm = context.getFactory();
		this.variableTypes = new BasicCalcTypes(context.getFrame());
		String[] lines = SH.splitLines(code);
		if (lines.length > 0) {
			StringBuilder prefix = new StringBuilder();
			for (int ln = 0; ln < lines.length; ln++) {
				SH.clear(prefix);
				String line = lines[ln];
				for (int i = 0; i < line.length(); i++) {
					char c = line.charAt(i);
					if (c == ' ' || c == '\t')
						prefix.append(c);
					else
						break;
				}
				if (line.length() > prefix.length())
					break;
			}
			String pf = prefix.toString();
			if (pf.length() > 0)
				for (int ln = 0; ln < lines.length; ln++)
					lines[ln] = SH.stripPrefix(lines[ln], pf, false);
			this.code = SH.join('\n', lines);
		} else
			this.code = code;
	}

	public Object execute(CalcFrameStack sf) {
		CalcFrame mutableVariables = sf.getFrame();
		//TODO: Load properties
		PythonInterpreter pyinterp = null;
		try {
			Properties properties = new Properties();

			System.out.println(System.getenv("jythonpath"));
			PythonInterpreter.initialize(System.getProperties(), properties, new String[] { "" });
			pyinterp = new PythonInterpreter(new org.python.core.PyStringMap(), new org.python.core.PySystemState());

			CalcFrameStack origMap = sf.getTop();
			if (origMap instanceof AmiCalcFrameStack) {
				AmiCalcFrameStack origValues = (AmiCalcFrameStack) origMap;
				AmiDebugWriter debugWriter = new AmiDebugWriter(origValues);
				pyinterp.setOut(debugWriter);
			}

			pyinterp.exec("import java.lang");
			pyinterp.exec("from com.f1.base import Table");
			pyinterp.exec("from com.f1.utils.structs.table import BasicTable");
			pyinterp.exec("from com.f1.utils import TableHelper");

			// Put variables
			for (String var : mutableVariables.getVarKeys()) {
				//				if ("tableset".equals(var))
				//					continue;
				//				if ("session".equals(var))
				//					continue;
				//				if ("this".equals(var))
				//					continue;
				pyinterp.set(var, AmiPyObject.castToPy(mfm, mutableVariables.getValue(var)));
			}
			
			// Put Global variables, like for dm onProcess(WHERE,wheres)..
			for (String var : origMap.getGlobal().getVarKeys()) {
				//				if ("tableset".equals(var))
				//					continue;
				//				if ("session".equals(var))
				//					continue;
				//				if ("this".equals(var))
				//					continue;
				pyinterp.set(var, AmiPyObject.castToPy(mfm, origMap.getGlobal().getValue(var)));
			}

			// Eval script 
			//			pyinterp.compile(code);
			pyinterp.exec(code);

			// Get variables
			//				dcc.getDeclaredVarsNested(sink);
			for (String var : mutableVariables.getVarKeys()) {
				Class<?> type = variableTypes.getType(var);
				Object x = null;
				x = pyinterp.get(var, type);
				mutableVariables.putValue(var, x);
			}

			//Get tables
			//			LH.log(log, Level.INFO, "Extern Python:\n", debugWriter);
		} catch (PyException e) {
			LH.log(log, Level.WARNING, e, e.value);
			throw new RuntimeException(e.value.toString(), e);
			//		} catch (Exception e) {
			//			LH.log(log, Level.WARNING, e);
			//			throw e;
		} finally {
			if (pyinterp != null) {
				pyinterp.cleanup();
				pyinterp.close();
			}
		}
		return null;
	}
	public void getDependencies(Set<String> ids) {
		for (String s : variableTypes.getVarKeys())
			ids.add(s);
	}
	public Class getReturnType() {
		return Object.class;
	}
	public static void main(String[] args) throws ScriptException, IOException {
		MutableCalcFrame mv = new MutableCalcFrame();
		mv.putType("str", String.class);
		mv.putType("list", List.class);
		mv.putType("map", Map.class);
		mv.putType("arr", int[].class);
		mv.putType("i", int.class);
		mv.putType("I", Integer.class);
		mv.putType("l", long.class);
		mv.putType("L", Long.class);
		mv.putType("s", short.class);
		mv.putType("S", Short.class);
		mv.putType("d", double.class);
		mv.putType("D", Double.class);
		mv.putType("f", float.class);
		mv.putType("F", Float.class);
		mv.putType("b", boolean.class);
		mv.putType("B", Boolean.class);
		mv.putType("byte", byte.class);
		mv.putType("Byte", Byte.class);
		mv.putType("c", char.class);
		mv.putType("C", Character.class);

		mv.putValue("str", "hello");
		mv.putValue("list", Arrays.asList(1, 2, 3, 4));
		mv.putValue("map", new HashMap() {
			{
				put("a", "bird");
				put("b", "cow");
				put("c", "ant");
			}
		});
		mv.putValue("arr", new int[] { 4, 5, 6, 7 });
		mv.putValue("i", 888);
		mv.putValue("I", new Integer(889));
		mv.putValue("l", System.currentTimeMillis());
		mv.putValue("L", new Long(System.currentTimeMillis() / 1000));
		mv.putValue("d", 71.03d);
		mv.putValue("D", new Double(72.0));
		mv.putValue("d", 122.73f);
		mv.putValue("D", new Float(127.5));
		mv.putValue("s", 12);
		mv.putValue("S", new Short((short) 2));
		mv.putValue("b", true);
		mv.putValue("B", new Boolean(false));
		mv.putValue("byte", 1223);
		mv.putValue("Byte", new Byte((byte) 1342));
		mv.putValue("c", 'a');
		mv.putValue("C", new Character('z'));

		Tableset ts = new TablesetImpl();
		Table table = new BasicTable();
		table.setTitle("MyTable");
		table.addColumn(String.class, "name");
		table.addColumn(int.class, "age");
		table.getRows().add(table.newRow("James", 57));
		table.getRows().add(table.newRow("Monia", 27));
		table.getRows().add(table.newRow("Risty", 22));
		ts.putTable("mytable", table);

		Map<String, String> codeTests = new HashMap<String, String>();
		codeTests.put("str", "print str + \" world\"");
		codeTests.put("setVar", "print i\ni = 999");
		codeTests.put("listComp", "print ([x*x for x in list])");
		codeTests.put("listCompArray", "print ([x*x for x in arr])");
		codeTests.put("listSet", "list = [6,7,8,9,10]");
		codeTests.put("listSetArray", "arr = [6,7,8,9,10]");
		codeTests.put("mapPrint", "print[(key,val) for key, val in map.iteritems()]");
		codeTests.put("mapGet", "print map.get(\"a\")");
		codeTests.put("mapGet2", "print map[\"a\"]");
		codeTests.put("mapSet", "map[\"a\"] = \"penguin\"");
		codeTests.put("mapSetAll", "map = {'a': 'bat', 'b': 'tree'}");
		codeTests.put("mapSetAll2", "map = {'a': 'bat', 'b': 'tree', 'c':777}");
		codeTests.put("importJava", "from com.f1.base import Table");
		codeTests.put("importJava2", "from com.f1.utils.sql import TablesetImpl");
		codeTests.put("tablePrint", "print( TableHelper.toString(tableset.getTable('mytable')) )");

		//TablesetTests
		codeTests.put("tableset1", "tableset.clearTables()");
		codeTests.put("tableset2", "print(TableHelper.toString(tableset.getTable('mytable')))");
		codeTests.put("tableset3", "print(tableset.getTableNames())");
		codeTests.put("tableset4", "print(tableset.getTable('mytable').getSize())");
		codeTests.put("tableset5", "tableset.removeTable('mytable')");
		codeTests.put("tableset6",
				"newTable = BasicTable()\nnewTable.setTitle('NewTable')\nnewTable.addColumn(java.lang.Integer, 'count')\nnewTable.getRows().add(newTable.newRow(17))\ntableset.putTable('newtable', newTable)\nprint(TableHelper.toString(tableset.getTable('newtable')))");
		codeTests.put("table1", "mytable = tableset.getTable('mytable')\nprint( mytable.getAt(0,0) )");
		codeTests.put("table2", "mytable = tableset.getTable('mytable')\nmytable.getRows().add(1, mytable.newRow('Terry', 40))");
		codeTests.put("table3", "mytable = tableset.getTable('mytable')\nprint(mytable.getTitle())");
		codeTests.put("table4", "mytable = tableset.getTable('mytable')\nprint(mytable.getRows().get(2))");
		codeTests.put("table5", "mytable = tableset.getTable('mytable')\nmytable.getRows().remove(mytable.getRows().get(1))");
		codeTests.put("table6", "mytable = tableset.getTable('mytable')\nprint(mytable.getColumnAt(0).getId())");
		codeTests.put("table7", "mytable = tableset.getTable('mytable')\nprint(mytable.getColumns().size())");
		codeTests.put("table8", "mytable = tableset.getTable('mytable')\nprint(mytable.getColumn('name').getType())");
		codeTests.put("table9", "mytable = tableset.getTable('mytable')\nprint(mytable.getColumn('age').getLocation())");
		codeTests.put("table10", "mytable = tableset.getTable('mytable')\nmytable.removeColumn('age')");

		String code = codeTests.get("table10");
		PythonExternCompiled e = new PythonExternCompiled(new TopCalcFrameStack(new TablesetImpl(), mv), code);
		e.execute(new TopCalcFrameStack(ts, mv));
		code = codeTests.get("tablePrint");
		e = new PythonExternCompiled(new TopCalcFrameStack(new TablesetImpl(), mv), code);
		e.execute(new TopCalcFrameStack(ts, mv));
	}

	public class AmiDebugWriter extends WriterLineBuffer {
		protected final Logger amiScripLog = Logger.getLogger("AMISCRIPT");
		private AmiDebugManager amiDebugManager;
		private AmiCalcFrameStack ei;

		public AmiDebugWriter(AmiCalcFrameStack ei) {
			this.ei = ei;
			this.amiDebugManager = ei.getDebugManager();
		}

		@Override
		public void onEol(CharSequence data) {
			if (this.amiDebugManager.shouldDebug(AmiDebugMessage.SEVERITY_INFO)) {
				String message = data.toString();
				this.amiDebugManager.addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_INFO, AmiDebugMessage.TYPE_LOG, ei.getSourceAri(), ei.getCallbackName(),
						"log: " + SH.ddd(message, 255), CH.m("message", message), null));
			}
		}

	}

	public class AmiSession<T> {
		protected final Logger amiScripLog = Logger.getLogger("AMISCRIPT");
		private AmiDebugManager amiDebugManager;
		private AmiCalcFrameStack ei;

		public AmiSession(AmiCalcFrameStack ei) {
			this.ei = ei;
			this.amiDebugManager = ei.getDebugManager();
		}

		public void log(T... params) {
			StringBuilder sb = new StringBuilder();
			Map<Object, Object> details = new HashMap<Object, Object>();
			for (int i = 0; i < params.length; i++) {
				sb.append(params[i]);
				details.put("Param " + i, params[i]);
			}
			String message = sb.toString();
			LH.info(amiScripLog, message);
			//			this.amiDebugManager.debug(AmiDebugMessage.SEVERITY_INFO, AmiDebugMessage.TYPE_AMISCRIPT, null, AmiDebugMessage.TGT_MEM_METHOD, "Session::log", message, details, null);
			//			this.amiDebugManager.debug(AmiDebugMessage.SEVERITY_INFO, AmiDebugMessage.TYPE_LOG, null, AmiDebugMessage.TGT_MEM_METHOD, null, message, details, null);
			if (this.amiDebugManager.shouldDebug(AmiDebugMessage.SEVERITY_INFO))
				this.amiDebugManager.addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_INFO, AmiDebugMessage.TYPE_LOG, ei.getSourceAri(), ei.getCallbackName(),
						"log: " + SH.ddd(message, 255), details, null));
		}
	}
}
