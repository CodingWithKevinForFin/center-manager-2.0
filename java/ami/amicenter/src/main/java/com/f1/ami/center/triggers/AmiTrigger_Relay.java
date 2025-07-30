package com.f1.ami.center.triggers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbFlushable;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbObjectsManager;
import com.f1.ami.center.table.AmiImdbScriptManager;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.client.AmiClient;
import com.f1.base.Bytes;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.sql.SqlDerivedCellParser;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableStackFramePool;

public class AmiTrigger_Relay extends AmiAbstractTrigger implements AmiImdbFlushable {

	private static final Logger log = LH.get();
	private AmiClient client;
	private boolean needsFlush = false;
	private AmiImdbObjectsManager om;
	private Map<String, Assignment> mapping = new TreeMap<String, Assignment>();
	private String dependenciesDef;

	private static class Assignment {
		final int amiColPos;
		final String targetName;
		final DerivedCellCalculator calc;
		final byte amiType;

		public Assignment(String targetName, DerivedCellCalculator calc) {
			this.targetName = targetName;
			this.calc = calc;
			this.amiType = AmiTable.TYPE_NONE;
			this.amiColPos = -1;
		}

		public Assignment(AmiColumnImpl col) {
			this.targetName = col.getName();
			this.calc = null;
			this.amiType = col.getAmiType();
			this.amiColPos = col.getLocation();
		}
	}

	private Assignment[] updates;
	private Assignment[] inserts;
	private Assignment[] deletes;
	private String targetTableName;
	private DerivedCellCalculator where;
	private ReusableStackFramePool stackFramePool;

	@Override
	protected void onStartup(CalcFrameStack sf) {
		this.stackFramePool = getImdb().getState().getStackFramePool();
		AmiImdbImpl t = (AmiImdbImpl) this.getImdb();
		this.om = t.getObjectsManager();
		if (this.getBinding().getTableNamesCount() != 1)
			throw new RuntimeException("RELAY trigger must have one table");
		this.client = new AmiClient();
		String host = this.getBinding().getOption(String.class, "host");
		int port = this.getBinding().getOption(Integer.class, "port");
		String login = this.getBinding().getOption(String.class, "login");
		String file = this.getBinding().getOption(String.class, "keystoreFile", null);
		String pass = this.getBinding().getOption(String.class, "keystorePass", null);
		int options = AmiClient.ENABLE_AUTO_PROCESS_INCOMING | AmiClient.ENABLE_QUIET | AmiClient.ENABLE_SEND_SEQNUM | AmiClient.ENABLE_SEND_TIMESTAMPS;
		if (SH.is(file)) {
			String pass2 = t.getState().decrypt(pass);
			client.start(host, port, login, options, new File(file), pass2);
		} else
			client.start(host, port, login, options);
		build(sf);
	}

	private void build(CalcFrameStack cfs) {
		final AmiImdbImpl db = (AmiImdbImpl) this.getImdb();
		final AmiTriggerBinding binding = this.getBinding();
		final AmiImdbScriptManager sm = db.getScriptManager();
		final SqlProcessor sqlProcessor = sm.getSqlProcessor();
		final SqlExpressionParser ep = sqlProcessor.getExpressionParser();
		final SqlDerivedCellParser p = sqlProcessor.getParser();
		final AmiTableImpl table = db.getAmiTable(binding.getTableNameAt(0));
		this.mapping.clear();
		String selects = binding.getOption(Caster_String.INSTANCE, "derivedValues", null);
		Map<String, Assignment> mapping = new HashMap<String, Assignment>();
		ChildCalcTypesStack context = new ChildCalcTypesStack(cfs, table.getTable().getColumnTypesMapping(), sm.getMethodFactory());
		if (selects != null) {
			Assignment[] assignments;
			SqlColumnsNode node1 = ep.parseSqlColumnsNdoe(SqlExpressionParser.ID_SELECT, selects);
			assignments = new Assignment[node1.getColumnsCount()];
			for (int i = 0; i < node1.getColumnsCount(); i++) {
				Node col = node1.getColumnAt(i);
				String targetName;
				OperationNode op;
				try {
					op = (OperationNode) col;
					VariableNode vn = (VariableNode) op.getLeft();
					targetName = vn.getVarname();
				} catch (Exception e) {
					throw new RuntimeException("selects option should be in the form: target=sourceColumn", e);
				}
				DerivedCellCalculator calc = p.toCalc(op.getRight(), context);
				assignments[i] = new Assignment(targetName, calc);
			}
			for (Assignment a : assignments)
				CH.putOrThrow(mapping, a.targetName, a);
		}
		String where = binding.getOption(Caster_String.INSTANCE, "where", null);
		DerivedCellCalculator whereCalc;
		if (SH.is(where)) {
			whereCalc = p.toCalc(where, context);
			if (!Boolean.class.equals(whereCalc.getReturnType()))
				throw new RuntimeException("where clause must return boolean: " + where);
		} else
			whereCalc = null;
		for (int i = 0; i < table.getColumnsCount(); i++) {
			AmiColumnImpl<?> col = table.getColumnAt(i);
			if (isReserved(col.getName()))
				continue;
			if (!mapping.containsKey(col.getName()))
				mapping.put(col.getName(), new Assignment(col));
		}
		this.mapping.putAll(mapping);
		this.deletes = toAssignments("deletes");
		this.updates = toAssignments("updates");
		this.inserts = toAssignments("inserts");
		this.targetTableName = getBinding().getOption(Caster_String.INSTANCE, "target", table.getName());
		this.dependenciesDef = AmiTrigger_Join.getDependenciesDef(this.getImdb(), table);
		this.where = whereCalc;
	}

	private boolean isReserved(String name) {
		if (name == null || name.length() != 1)
			return false;
		char c = name.charAt(0);
		return Character.isUpperCase(c) && c != 'I' && c != 'E';
	}

	private Assignment[] toAssignments(String option) {
		String keys = getBinding().getOption(Caster_String.INSTANCE, option, null);
		if (keys == null) {
			Assignment[] r = new Assignment[this.mapping.size()];
			int pos = 0;
			for (Assignment a : this.mapping.values())
				r[pos++] = a;
			return r;
		} else {
			String[] keysArray = SH.splitWithEscape(',', '\\', keys);
			Assignment[] r = new Assignment[keysArray.length];
			for (int pos = 0; pos < keysArray.length; pos++) {
				Assignment assignment = this.mapping.get(keysArray[pos]);
				if (assignment == null)
					throw new RuntimeException("Option '" + option + "' references missing column: " + keysArray[pos]);
				r[pos] = assignment;
			}
			return r;
		}
	}

	@Override
	public void onInserted(AmiTable table, AmiRow row, CalcFrameStack sf) {
		writeVals('O', row, inserts, sf);
	}

	@Override
	public boolean onUpdating(AmiTable table, AmiRow row, CalcFrameStack sf) {
		return true;
	}
	@Override
	public void onUpdated(AmiTable table, AmiRow row, CalcFrameStack sf) {
		writeVals('O', row, updates, sf);
	}

	@Override
	public boolean onDeleting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		writeVals('D', row, deletes, sf);
		return true;
	}

	private void writeVals(char o, AmiRow row, Assignment[] cols, CalcFrameStack sf) {
		if (cols.length == 0)
			return;
		if (!client.isConnected()) {
			LH.warning(log, "Trigger '", this.getBinding().getTriggerName(), "' is not connected to relay so dropping event: T=", o, ",", row);
			return;
		}

		ReusableCalcFrameStack rsf = this.stackFramePool.borrow(sf, row);
		if (where != null && !Boolean.TRUE.equals(where.get(rsf))) {
			this.stackFramePool.release(rsf);
			return;
		}
		client.startMessage(o);
		client.addMessageParamString("T", this.targetTableName);
		try {
			for (Assignment col : cols) {
				String key = col.targetName;
				int colPos = col.amiColPos;
				if (colPos == -1) {//its' derived
					Object value = col.calc.get(rsf);
					try {
						client.addMessageParamObject(key, value);
					} catch (IllegalArgumentException e) {
						LH.warning(log, "Trigger '", this.getBinding().getTriggerName(), "' has unsupported type: ", key + "=" + value);
					}
				} else if (row.getIsNull(colPos))
					client.addMessageParamNull(key);
				else
					switch (col.amiType) {
						case AmiTable.TYPE_BIGDEC:
							client.addMessageParamDouble(key, row.getDouble(colPos));
							break;
						case AmiTable.TYPE_BIGINT:
							client.addMessageParamLong(key, row.getLong(colPos));
							break;
						case AmiTable.TYPE_BINARY:
							client.addMessageParamBinary(key, ((Bytes) row.getComparable(colPos)).getBytes());
							break;
						case AmiTable.TYPE_BOOLEAN:
							client.addMessageParamBoolean(key, row.getLong(colPos) != 0);
							break;
						case AmiTable.TYPE_BYTE:
							client.addMessageParamInt(key, (int) row.getLong(colPos));
							break;
						case AmiTable.TYPE_CHAR:
							client.addMessageParamString(key, (char) row.getLong(colPos));
							break;
						case AmiTable.TYPE_COMPLEX:
							client.addMessageParamString(key, row.getString(colPos));
							break;
						case AmiTable.TYPE_DOUBLE:
							client.addMessageParamDouble(key, row.getDouble(colPos));
							break;
						case AmiTable.TYPE_ENUM:
							client.addMessageParamString(key, row.getString(colPos));
							break;
						case AmiTable.TYPE_FLOAT:
							client.addMessageParamFloat(key, (float) row.getDouble(colPos));
							break;
						case AmiTable.TYPE_INT:
							client.addMessageParamInt(key, (int) row.getLong(colPos));
							break;
						case AmiTable.TYPE_LONG:
							client.addMessageParamLong(key, row.getLong(colPos));
							break;
						case AmiTable.TYPE_SHORT:
							client.addMessageParamInt(key, (int) row.getLong(colPos));
							break;
						case AmiTable.TYPE_STRING:
							client.addMessageParamString(key, row.getString(colPos));
							break;
						case AmiTable.TYPE_UTC:
							client.addMessageParamLong(key, row.getLong(colPos));
							break;
						case AmiTable.TYPE_UTCN:
							client.addMessageParamLong(key, row.getLong(colPos));
							break;
						case AmiTable.TYPE_UUID:
							client.addMessageParamString(key, row.getString(colPos));
							break;
					}
			}
		} finally {
			client.sendMessage();
		}
		if (!this.needsFlush) {
			om.registerNeedsflush(this);
			this.needsFlush = true;
		}
		this.stackFramePool.release(rsf);
	}

	@Override
	public boolean isSupported(byte type) {
		switch (type) {
			case INSERTED:
			case UPDATED:
			case DELETING:
				return true;
			default:
				return false;
		}
	}

	@Override
	public void flushPersister(CalcFrameStack sf) {
		if (this.client.isConnected())
			this.client.flush();
		this.needsFlush = false;
	}

	@Override
	public String getFlushableName() {
		return "RELAY_TRIGGER:" + getBinding().getTriggerName();
	}

	@Override
	public void onSchemaChanged(AmiImdb imdb, CalcFrameStack sf) {
		final AmiTableImpl table = (AmiTableImpl) imdb.getAmiTable(getBinding().getTableNameAt(0));
		String t = AmiTrigger_Join.getDependenciesDef(imdb, table);
		if (OH.ne(this.dependenciesDef, t))
			build(sf);
	}

	@Override
	public void onClosed() {
		this.client.close();
	}

}
