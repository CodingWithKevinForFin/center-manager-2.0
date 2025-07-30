package com.f1.ami.center.triggers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.center.table.AmiCenterAutoExecuteItinerary;
import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbScriptManager;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.timers.AmiTimer_AmiScript;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Row;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.columnar.ColumnarTableList;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorBlock;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorExpression;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorFlowStatementReturn;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiTrigger_AmiScript extends AmiAbstractTrigger {
	private static final Logger log = LH.get();
	public static final String PREFIX_NEW = "new_";
	public static final String PREFIX_OLD = "old_";

	private String onInserting;
	private String onInserted;
	private String onUpdating;
	private String onUpdated;
	private String onDeleting;
	private String onStartup;
	private DerivedCellCalculatorExpression onInsertingCalc;
	private DerivedCellCalculatorExpression onInsertedCalc;
	private DerivedCellCalculatorExpression onUpdatingCalc;
	private DerivedCellCalculatorExpression onUpdatedCalc;
	private DerivedCellCalculatorExpression onDeletingCalc;
	private CalcFrame vars;
	private com.f1.utils.structs.table.stack.BasicCalcTypes varTypes = new com.f1.utils.structs.table.stack.BasicCalcTypes();
	private Map.Entry[] varsEntries;
	private boolean canMutateRow;
	private boolean runOnStartup;
	private String rowAsMapName;
	private final Map<String, VarType> namesToValues = new HashMap<String, VarType>();
	private final Map<String, VarType> updatingNamesToValues = new HashMap<String, VarType>();
	//	private ValuesFacade valuesFacade = new ValuesFacade();
	//	private ValuesFacade updatingValuesFacade = new ValuesFacade();
	private CalcTypes typesFacade;
	private CalcTypes updatingTypesFacade;
	private DerivedCellCalculatorFlowStatementReturn onInsertingCalcReturn;
	private DerivedCellCalculatorFlowStatementReturn onUpdatingCalcReturn;
	private DerivedCellCalculatorFlowStatementReturn onDeletingCalcReturn;
	private AmiTableImpl table;
	private AmiImdbScriptManager scriptManager;

	@Override
	protected void onStartup(CalcFrameStack sf) {
		this.scriptManager = ((AmiImdbImpl) getImdb()).getScriptManager();
		this.rowAsMapName = this.getBinding().getOption(Caster_String.INSTANCE, "rowVar", null);
		this.canMutateRow = this.getBinding().getOption(Caster_Boolean.INSTANCE, "canMutateRow", Boolean.FALSE);
		this.runOnStartup = this.getBinding().getOption(Caster_Boolean.INSTANCE, "runOnStartup", Boolean.FALSE);
		this.onInserting = this.getBinding().getOption(Caster_String.INSTANCE, "onInsertingScript", null);
		this.onInserted = this.getBinding().getOption(Caster_String.INSTANCE, "onInsertedScript", null);
		this.onUpdating = this.getBinding().getOption(Caster_String.INSTANCE, "onUpdatingScript", null);
		this.onUpdated = this.getBinding().getOption(Caster_String.INSTANCE, "onUpdatedScript", null);
		this.onDeleting = this.getBinding().getOption(Caster_String.INSTANCE, "onDeletingScript", null);
		this.onStartup = this.getBinding().getOption(Caster_String.INSTANCE, "onStartupScript", null);
		if (this.onInserting == null && this.onInserted == null && this.onUpdating == null && this.onUpdated == null && this.onDeleting == null)
			throw new RuntimeException("must specify at least one script to run for  trigger");
		if (this.getBinding().getTableNamesCount() != 1)
			throw new RuntimeException("must specify exactly one table");
		this.varTypes.putAll(AmiTimer_AmiScript.parseVarTypes((AmiImdbImpl) getImdb(), this.getBinding().getOption(Caster_String.INSTANCE, "vars", "")));
		BasicCalcFrame varsMap = new BasicCalcFrame(this.varTypes);
		this.varsEntries = new Map.Entry[this.varTypes.getVarsCount()];
		int n = 0;
		for (String name : varTypes.getVarKeys())
			this.varsEntries[n++] = varsMap.getOrCreateEntry(name);
		this.vars = varsMap;
		this.compile(sf);
	}

	@Override
	public void onInitialized(CalcFrameStack sf) {
		if (this.onStartup != null) {
			try {
				this.getImdb().getScriptManager().executeSql(onStartup, vars, null, AmiConsts.DEFAULT, null, sf);
			} catch (ExpressionParserException e) {
				throw new ExpressionParserException(onStartup, e.getPosition(), "Error with onStartupScript: " + e.getMessage(), e);
			}
		}
		if (runOnStartup) {
			AmiTableImpl table = (AmiTableImpl) this.getImdb().getAmiTable(this.getBinding().getTableNameAt(0));
			ColumnarTableList rows = table.getTable().getRows();
			LH.info(log, "Trigger ", this.getBinding().getTriggerName(), " processing ", rows.size(), "row(s) from ", table.getName() + " (runOnStartup=true)");
			int cnt = 0;
			for (Row row : rows) {
				this.onInserted(table, (AmiRow) row, sf);
				if (++cnt % 100000 == 0)
					LH.info(log, "Trigger ", this.getBinding().getTriggerName(), " processed ", cnt, "/", rows.size(), " rows from ", table.getName());
			}
			LH.info(log, "Trigger ", this.getBinding().getTriggerName(), " onstartup Complete");
		}
	};

	@Override
	public boolean onUpdating(AmiTable table, AmiRow row, AmiPreparedRow updatingTo, CalcFrameStack sf) {
		if (this.onUpdatingCalc == null)
			return true;
		Object result;
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		try {
			session.setIgnoreReturnTables(true);
			result = scriptManager.executeSql(onUpdatingCalc, new ValuesFacade(updatingTypesFacade, updatingNamesToValues, row, updatingTo, canMutateRow, sf),
					AmiImdbScriptManager.ON_EXECUTE_AUTO_HANDLE, null, AmiConsts.DEFAULT, null, sf);
		} finally {
			session.setIgnoreReturnTables(false);
		}
		if (result instanceof AmiCenterAutoExecuteItinerary && onUpdatingCalcReturn != null)
			result = onUpdatingCalcReturn.get(new ReusableCalcFrameStack(sf, row));//If amiscript results in a deferred statement and the script ends in a return, we can't wait for the result so jump right to the return value
		return !Boolean.FALSE.equals(result);
	}

	@Override
	public void onInserted(AmiTable table, AmiRow row, CalcFrameStack sf) {
		execute(onInsertedCalc, false, table, row, sf);
	}
	@Override
	public boolean onInserting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (onInsertingCalc == null)
			return true;
		Object result = execute(onInsertingCalc, canMutateRow, table, row, sf);
		if (result instanceof AmiCenterAutoExecuteItinerary && onInsertingCalcReturn != null) {
			result = onInsertingCalcReturn.get(new ReusableCalcFrameStack(sf, row));//If amiscript results in a deferred statement and the script ends in a return, we can't wait for the result so jump right to the return value
		}
		return !Boolean.FALSE.equals(result);
	}
	@Override
	public void onUpdated(AmiTable table, AmiRow row, CalcFrameStack sf) {
		execute(onUpdatedCalc, false, table, row, sf);
	}

	@Override
	public boolean onDeleting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		Object result = execute(onDeletingCalc, canMutateRow, table, row, sf);
		if (result instanceof AmiCenterAutoExecuteItinerary && onDeletingCalcReturn != null)
			result = onDeletingCalcReturn.get(new ReusableCalcFrameStack(sf, row));//If amiscript results in a deferred statement and the script ends in a return, we can't wait for the result so jump right to the return value
		return !Boolean.FALSE.equals(result);
	}

	private Object execute(DerivedCellCalculatorExpression sql, boolean allowMutate, AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (sql == null)
			return null;
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		try {
			session.setIgnoreReturnTables(true);
			return scriptManager.executeSql(sql, new ValuesFacade(typesFacade, namesToValues, null, row, allowMutate, sf), AmiImdbScriptManager.ON_EXECUTE_AUTO_HANDLE, null,
					AmiConsts.DEFAULT, null, sf);
		} finally {
			session.setIgnoreReturnTables(false);
		}
	}
	@Override
	public void onSchemaChanged(AmiImdb imdb, CalcFrameStack sf) {
		compile(sf);
		super.onSchemaChanged(imdb, sf);
	}
	private void compile(CalcFrameStack fs) {
		this.table = (AmiTableImpl) this.getImdb().getAmiTable(this.getBinding().getTableNameAt(0));
		final CalcTypes columnTypes = this.table.getTable().getColumnTypesMapping();
		this.namesToValues.clear();
		this.updatingNamesToValues.clear();

		final com.f1.utils.structs.table.stack.BasicCalcTypes updatingColumnTypes = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		for (String name : columnTypes.getVarKeys()) {
			Class<?> type = columnTypes.getType(name);
			final String oldName = PREFIX_OLD + name;
			final String newName = PREFIX_NEW + name;
			final int pos = this.table.getColumnLocation(name);
			updatingColumnTypes.putType(oldName, type);
			updatingColumnTypes.putType(newName, type);
			this.namesToValues.put(name, new VarType(name, VarType.NEW, pos));
			this.updatingNamesToValues.put(oldName, new VarType(oldName, VarType.OLD, pos));
			this.updatingNamesToValues.put(newName, new VarType(newName, VarType.NEW, pos));
		}
		if (this.rowAsMapName != null) {
			final String oldName = PREFIX_OLD + rowAsMapName;
			final String newName = PREFIX_NEW + rowAsMapName;
			this.typesFacade = new BasicCalcTypes(columnTypes).putIfAbsent(rowAsMapName, Map.class).putAllIfAbsent(varTypes);
			this.updatingTypesFacade = new BasicCalcTypes(updatingColumnTypes).putIfAbsent(oldName, Map.class).putIfAbsent(newName, Map.class).putAllIfAbsent(this.varTypes);
			final VarType vt = new VarType((String) this.rowAsMapName, VarType.ROW, -1);
			final VarType vtOld = new VarType((String) oldName, VarType.ROW_OLD, -1);
			final VarType vtNew = new VarType((String) newName, VarType.ROW_NEW, -1);
			this.namesToValues.put(vt.name, vt);
			this.updatingNamesToValues.put(vtOld.name, vtOld);
			this.updatingNamesToValues.put(vtNew.name, vtNew);
		} else {
			this.typesFacade = new BasicCalcTypes(columnTypes).putAllIfAbsent(this.varTypes);
			this.updatingTypesFacade = new BasicCalcTypes(updatingColumnTypes).putAllIfAbsent(this.varTypes);
		}
		for (int i = 0; i < this.varsEntries.length; i++) {
			final VarType vt = new VarType((String) this.varsEntries[i].getKey(), VarType.VAR, i);
			this.namesToValues.put(vt.name, vt);
			this.updatingNamesToValues.put(vt.name, vt);
		}
		this.onInsertingCalc = toCalc(this.getImdb(), this.onInserting, this.typesFacade, "onInsertingScript", fs);
		this.onInsertedCalc = toCalc(this.getImdb(), this.onInserted, this.typesFacade, "onInsertedScript", fs);
		this.onUpdatingCalc = toCalc(this.getImdb(), this.onUpdating, this.updatingTypesFacade, "onUpdatingScript", fs);
		this.onUpdatedCalc = toCalc(this.getImdb(), this.onUpdated, this.typesFacade, "onUpdatedScript", fs);
		this.onDeletingCalc = toCalc(this.getImdb(), this.onDeleting, this.typesFacade, "onDeletingScript", fs);
		this.onInsertingCalcReturn = getReturn(this.onInsertingCalc);
		this.onUpdatingCalcReturn = getReturn(this.onUpdatingCalc);
		this.onDeletingCalcReturn = getReturn(this.onDeletingCalc);
	}

	static private DerivedCellCalculatorFlowStatementReturn getReturn(DerivedCellCalculatorExpression exp) {
		if (exp != null && exp.getInnerCalcsCount() == 1 && exp.getInnerCalcAt(0) instanceof DerivedCellCalculatorBlock) {
			DerivedCellCalculatorBlock b = (DerivedCellCalculatorBlock) exp.getInnerCalcAt(0);
			if (CH.size(DerivedHelper.find(b, DerivedCellCalculatorFlowStatementReturn.class, null)) != 1)
				return null;//There must only be 1 return clause
			if (b.getBlockParamsCount() > 0) {
				DerivedCellCalculator t = b.getBlockParam(b.getBlockParamsCount() - 1);
				if (t instanceof DerivedCellCalculatorFlowStatementReturn)
					return (DerivedCellCalculatorFlowStatementReturn) t;
			}
		}
		return null;

	}

	static private DerivedCellCalculatorExpression toCalc(AmiImdbImpl db, String script, com.f1.base.CalcTypes variableTypes, String description, CalcFrameStack fs) {
		try {
			return SH.isnt(script) ? null : db.getScriptManager().prepareSql(script, variableTypes, false, true, fs);
		} catch (ExpressionParserException e) {
			throw new ExpressionParserException(script, e.getPosition(), "Error with " + description + ": " + e.getMessage(), e);
		}
	}
	@Override
	public boolean isSupported(byte type) {
		switch (type) {
			case INSERTING:
				return onInserting != null;
			case INSERTED:
				return onInserted != null;
			case UPDATING:
				return onUpdating != null;
			case UPDATED:
				return onUpdated != null;
			case DELETING:
				return onDeleting != null;
			default:
				return super.isSupported(type);
		}
	}

	private static class VarType {
		static final byte ROW = 1;
		static final byte VAR = 2;
		static final byte OLD = 3;
		static final byte NEW = 4;
		static final byte ROW_OLD = 5;
		static final byte ROW_NEW = 6;
		public final String name;
		public final byte type;
		public final int pos;

		public VarType(String name, byte type, int pos) {
			this.name = name;
			this.type = type;
			this.pos = pos;
		}
	}

	public class ValuesFacade implements Map<String, Object>, CalcFrame {
		private AmiRow innerOld, innerNew;
		private boolean allowMutate;
		private CalcFrameStack sf;
		private CalcTypes types;
		private Map<String, VarType> nameToValues;

		private ValuesFacade(CalcTypes types, Map<String, VarType> nameToValues, AmiRow innerOld, AmiRow innerNew, boolean allowMutate, CalcFrameStack sf) {
			this.types = types;
			this.nameToValues = nameToValues;
			this.innerOld = innerOld;
			this.innerNew = innerNew;
			this.allowMutate = allowMutate;
			this.sf = sf;
		}

		@Override
		public Object get(Object k) {
			final VarType vt = CH.getOrThrow(nameToValues, (String) k, "Key not found");
			switch (vt.type) {
				case VarType.ROW:
					return this.innerNew;
				case VarType.VAR:
					return varsEntries[vt.pos].getValue();
				case VarType.ROW_OLD:
					return this.innerOld;
				case VarType.ROW_NEW:
					return this.innerNew;
				case VarType.OLD:
					return innerOld.getComparable(vt.pos);
				case VarType.NEW:
					if (innerNew.isSet(vt.pos))
						return innerNew.getComparable(vt.pos);
					else if (innerOld != null)
						return innerOld.getComparable(vt.pos);
					else
						return null;
				default:
					throw new IllegalStateException();
			}
		}

		@Override
		public Object put(String key, Object value) {
			final VarType vt = CH.getOrThrow(nameToValues, key, "Key not found");
			switch (vt.type) {
				case VarType.ROW:
				case VarType.ROW_OLD:
				case VarType.ROW_NEW:
					return null;
				case VarType.VAR:
					return varsEntries[vt.pos].setValue(value);
				case VarType.OLD:
					return null;
				case VarType.NEW:
					return allowMutate ? innerNew.setComparable(vt.pos, (Comparable) value, this.sf) : null;
				default:
					throw new IllegalStateException();
			}
		}
		@Override
		public boolean containsKey(Object k) {
			return nameToValues.containsKey(k);
		}

		@Override
		public int size() {
			return this.nameToValues.size();
		}

		@Override
		public int getVarsCount() {
			return this.nameToValues.size();
		}

		@Override
		public boolean isEmpty() {
			return this.nameToValues.isEmpty();
		}

		@Override
		public Set<String> keySet() {
			return this.nameToValues.keySet();
		}

		@Override
		public HasherMap<String, Object>.Values<Object> values() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsValue(Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object remove(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(Map<? extends String, ? extends Object> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<Entry<String, Object>> entrySet() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object getValue(String key) {
			return get(key);
		}

		@Override
		public Object putValue(String key, Object value) {
			return put(key, value);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			boolean first = true;
			for (String i : this.nameToValues.keySet()) {
				if (first)
					first = false;
				else
					sb.append(',');
				sb.append(i).append("=").append(get(i));
			}
			sb.append("}");
			return sb.toString();
		}

		@Override
		public Class<?> getType(String key) {
			return this.types.getType(key);
		}

		@Override
		public Iterable<String> getVarKeys() {
			return this.types.getVarKeys();
		}
		@Override
		public boolean isVarsEmpty() {
			return this.nameToValues.isEmpty();
		}

	}

}
