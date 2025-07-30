package com.f1.ami.center.replication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessage;
import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessageImpl;
import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbScriptManager;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiPreparedRowImpl;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.triggers.AmiTrigger_Join;
import com.f1.base.Row;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.structs.CompactLongKeyMap;
import com.f1.utils.structs.CompactLongKeyMap.KeyGetter;
import com.f1.utils.structs.RangeArray;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.MutableCalcFrame;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

//Replication of table
public class AmiCenterReplication {
	public static String OPTION_CLEAR = "Clear";
	public static String OPTION_VALUE_CLEAR_ONDISCONNECT = "OnDisconnect";
	public static String OPTION_VALUE_CLEAR_ONCONNECT = "OnConnect";
	public static String OPTION_VALUE_CLEAR_OFF = "Off";
	public static String OPTION_SOURCE_NAME_COLUMN = "SrcNameColumn";
	public static String OPTION_SOURCE_ID_COLUMN = "SrcIdColumn";
	private static final Logger log = LH.get();

	private static class RowCache {
		public RowCache(long sourceId, AmiRowImpl targetId, Object cache[]) {
			this.sourceAmiid = sourceId;
			this.target = targetId;
			this.cache = cache;
		}

		final public long sourceAmiid;
		//		final public long targetAmiid;
		public Object[] cache;//TODO:make an array and use a IndexList for mapping
		public AmiRowImpl target;
	}

	private static final KeyGetter<RowCache> KEY_GETTER = new KeyGetter<AmiCenterReplication.RowCache>() {

		@Override
		public long getKey(RowCache object) {
			return object.sourceAmiid;
		}
	};

	final private String targetTable;
	final private String name;
	final private String sourceTable;
	final private String source;
	final private CompactLongKeyMap<RowCache> amiIdMappings = new CompactLongKeyMap<RowCache>("", KEY_GETTER, 128);
	final private byte sourceId;
	final private AmiImdbImpl db;
	final private Node[] mappingNodes;
	private boolean isInvalid;
	private String targetDef;
	private AmiCenterReplicationCenter replications;
	private boolean hasCaching;
	private boolean hasFormulas;
	private final Map<String, DerivedCellCalculator> tgtColsToCalc = new HashMap<String, DerivedCellCalculator>();
	private final Map<String, SrcColMapping> srcCol2Mapping = new HashMap<String, SrcColMapping>();
	private final RangeArray<SrcColMapping> srcColCode2Mapping = new RangeArray<AmiCenterReplication.SrcColMapping>();
	private AmiPreparedRowImpl preparedRow;
	private AmiTableImpl tgtTable;

	public static final byte DELETE_OFF = 1;
	public static final byte DELETE_ON_CONNECT = 2;
	public static final byte DELETE_ON_DISCONNECT = 3;
	final private byte onDelete;
	private MutableCalcFrame tmpValsForFomulas;
	private Map<String, DerivedCellCalculator> tmpTargetsWithFormulas = new HashMap<String, DerivedCellCalculator>();
	private ReusableCalcFrameStack tmpValuesstackFrame;
	private String sourceNameColumn;
	private String sourceIdColumn;
	private boolean sourceNameColumnValid;
	private boolean sourceIdColumnValid;

	public AmiCenterReplication(AmiImdbImpl db, AmiCenterReplicationCenter owner, String name, String targetTable, String source, byte sourceId, String sourceTable, String mapping,
			String options) {
		this.replications = owner;
		this.db = db;
		this.name = name;
		this.targetTable = targetTable;
		this.source = source;
		this.sourceId = sourceId;
		this.sourceTable = sourceTable;
		final AmiImdbScriptManager sm = db.getScriptManager();
		final SqlExpressionParser ep = sm.getSqlProcessor().getExpressionParser();
		if (mapping != null) {
			SqlColumnsNode node1 = ep.parseSqlColumnsNdoe(SqlExpressionParser.ID_GROUPBY, mapping);
			mappingNodes = node1.getColumnsCount() == 0 ? null : node1.getColumns();
		} else
			mappingNodes = null;
		if (mappingNodes != null) {
			for (int i = 0; i < mappingNodes.length; i++) {
				Node col = mappingNodes[i];
				if (!(col instanceof OperationNode))
					throw new RuntimeException("SELECTS option should be in the form: targetColumn=sourceColumnFormula");
			}
		}
		byte onDelete = DELETE_ON_DISCONNECT;

		Map<String, String> parts = SH.splitToMap(',', '=', '\\', options);
		String snc = null;
		String sic = null;
		for (Entry<String, String> e : parts.entrySet()) {
			if (OPTION_CLEAR.equals(e.getKey())) {
				onDelete = parseDelete(e.getValue());
			} else if (OPTION_SOURCE_NAME_COLUMN.equals(e.getKey())) {
				snc = e.getValue();
			} else if (OPTION_SOURCE_ID_COLUMN.equals(e.getKey())) {
				sic = e.getValue();
			} else
				throw new RuntimeException(
						"Invalid option: " + e.getKey() + ", valid options are: " + OPTION_CLEAR + ", " + OPTION_SOURCE_ID_COLUMN + ", " + OPTION_SOURCE_NAME_COLUMN);
		}
		this.sourceNameColumn = snc;
		this.sourceIdColumn = sic;
		this.onDelete = onDelete;
		this.tmpValuesstackFrame = new ReusableCalcFrameStack(db.getState().getReusableTopStackFrame());

	}

	public static byte parseDelete(String onDelete) {
		if (SH.equalsIgnoreCase(OPTION_VALUE_CLEAR_OFF, onDelete))
			return DELETE_OFF;
		else if (SH.equalsIgnoreCase(OPTION_VALUE_CLEAR_ONCONNECT, onDelete))
			return DELETE_ON_CONNECT;
		else if (SH.equalsIgnoreCase(OPTION_VALUE_CLEAR_ONDISCONNECT, onDelete))
			return DELETE_ON_DISCONNECT;
		throw new RuntimeException("Invalid " + OPTION_CLEAR + " option (" + onDelete + "), valid options are: " + OPTION_VALUE_CLEAR_OFF + ", " + OPTION_VALUE_CLEAR_ONCONNECT
				+ ", " + OPTION_VALUE_CLEAR_ONDISCONNECT);
	}

	public String formatDelete(byte key) {
		switch (key) {
			case DELETE_OFF:
				return OPTION_VALUE_CLEAR_OFF;
			case DELETE_ON_CONNECT:
				return OPTION_VALUE_CLEAR_ONCONNECT;
			case DELETE_ON_DISCONNECT:
				return OPTION_VALUE_CLEAR_ONDISCONNECT;
			default:
				throw new RuntimeException("Invalid type: " + key);
		}

	}
	public byte getOnDeleteMode() {
		return this.onDelete;
	}

	public CompactLongKeyMap<RowCache> getAmiIdMappings() {
		return this.amiIdMappings;
	}

	public String getTargetTable() {
		return this.targetTable;
	}
	public String getSourceTable() {
		return this.sourceTable;
	}
	public String getName() {
		return this.name;
	}

	public byte getSourceId() {
		return sourceId;
	}
	public String getSource() {
		return source;
	}

	public void clear(AmiImdbImpl db, CalcFrameStack sf) {
		AmiTableImpl t = (AmiTableImpl) db.getAmiTable(this.targetTable);
		long skipped = 0;
		if (t != null) {
			LH.info(log, "Clearing " + amiIdMappings.size() + " rows for table " + this.targetTable);
			for (RowCache i : amiIdMappings) {
				final AmiRowImpl row = i.target;
				if (row.getLocation() != -1)
					t.removeAmiRow(row, sf);
				else
					skipped++;
			}
			AmiColumnImpl<?> col = t.getColumnNoThrow(this.sourceNameColumn);
			if (col != null && col.getType() == String.class && t.getRowsCount() > 0) {
				for (int i = t.getRowsCount() - 1; i >= 0; i--) {
					AmiRowImpl row = t.getAmiRowAt(i);
					if (OH.eq(name, row.getValue(sourceNameColumn)))
						t.removeAmiRow(row, sf);
				}
			}
		} else {
			LH.info(log, this.targetTable + " not found, cannot clear rows");
		}
		if (skipped > 0)
			LH.info(log, "Skipped " + skipped + " rows for " + this.targetTable);
		amiIdMappings.clear();
	}

	private static class SrcColMapping {
		public static final byte MODE_PASSTHROUGH = 1;
		public static final byte MODE_FORMULA = 2;
		public static final byte MODE_NEEDS_CACHING = 4;
		public byte mode;
		public Map<String, DerivedCellCalculator> calcs;
		public int targetPos;
		public int cachePosition;
		final public String name;

		public SrcColMapping(String srcColName) {
			this.name = srcColName;
		}

	}

	public void process(AmiCenterClientObjectMessageImpl msg, AmiImdbSession session, CalcFrameStack sf) {
		if (isInvalid)
			return;
		switch (msg.getAction()) {
			case AmiCenterClientObjectMessage.ACTION_DEL: {
				RowCache rowCache = amiIdMappings.remove(msg.getId());
				if (rowCache == null) {
					LH.warning(log, "REPLICATION '" + this.name + "': Delete for missing record: " + msg);
					return;
				}
				AmiRowImpl existing = rowCache.target;
				if (existing.getLocation() == -1) {
					LH.warning(log, "REPLICATION '" + this.name + "': Delete for deleted record: " + msg);
					return;
				}
				tgtTable.removeAmiRow(existing, sf);
				break;
			}
			case AmiCenterClientObjectMessage.ACTION_ADD: {
				Object[] cache = hasCaching ? new Object[position2cacheName.length] : null;
				if (hasFormulas) {
					tmpValsForFomulas.clear();
					tmpTargetsWithFormulas.clear();
				}
				populatePreparedRow(msg, cache);
				if (hasFormulas) {
					for (Entry<String, DerivedCellCalculator> i : this.tgtColsToCalc.entrySet())
						preparedRow.setComparable(i.getKey(), (Comparable) i.getValue().get(this.tmpValuesstackFrame));
				}
				if (this.sourceNameColumnValid)
					this.preparedRow.putValue(sourceNameColumn, this.name);
				if (this.sourceIdColumnValid)
					this.preparedRow.putValue(sourceIdColumn, msg.getId());
				final AmiRowImpl row2 = tgtTable.insertAmiRow(this.preparedRow, true, false, sf);
				if (row2 == null) {
					LH.warning(log, "REPLICATION '" + this.name + "': Insert rejected for record: " + msg);
				} else {
					this.amiIdMappings.put(new RowCache(msg.getId(), row2, cache));
					this.preparedRow.reset();
				}
				break;
			}
			case AmiCenterClientObjectMessage.ACTION_UPD: {
				RowCache rowCache = this.amiIdMappings.get(msg.getId());
				if (rowCache == null) {
					LH.warning(log, "REPLICATION '" + this.name + "': Update for missing record: " + msg);
					return;
				}
				AmiRowImpl existing = rowCache.target;
				if (existing.getLocation() == -1) {
					LH.warning(log, "REPLICATION '" + this.name + "': Update for deleted record: " + msg);
					return;
				}
				if (hasFormulas) {
					tmpValsForFomulas.clear();
					tmpTargetsWithFormulas.clear();
					if (hasCaching) {
						for (int i = 0; i < rowCache.cache.length; i++)
							this.tmpValsForFomulas.putValue(position2cacheName[i], rowCache.cache[i]);
					}
				}
				populatePreparedRow(msg, rowCache.cache);
				if (tmpTargetsWithFormulas.size() > 0) {
					for (Entry<String, DerivedCellCalculator> i : tmpTargetsWithFormulas.entrySet())
						preparedRow.setComparable(i.getKey(), (Comparable) i.getValue().get(this.tmpValuesstackFrame));
				}
				tgtTable.updateAmiRow(existing, this.preparedRow, sf);
				this.preparedRow.reset();
				break;
			}
		}
	}

	private void populatePreparedRow(AmiCenterClientObjectMessageImpl msg, Object[] cache) {
		for (int i = 0, n = msg.getParamsCount(); i < n; i++) {
			short nmcode = msg.getParamCode(i);
			Comparable vl = (Comparable) msg.getParamValue(i);

			SrcColMapping def = this.srcColCode2Mapping.get(nmcode);
			if (def == null) {
				String nm = msg.getParamName(i);
				def = CH.getOrThrow(this.srcCol2Mapping, nm);
				this.srcColCode2Mapping.put(nmcode, def);
			}
			populatePreparedRowValue(cache, def, vl);
		}
		if (MH.anyBits(msg.getMask(), AmiCenterClientObjectMessage.MASK_OBJECT_ID)) {
			SrcColMapping def = CH.getOrThrow(this.srcCol2Mapping, "I");
			populatePreparedRowValue(cache, def, msg.getObjectId());
		}
	}

	private void populatePreparedRowValue(Object[] cache, SrcColMapping def, Comparable vl) {
		switch (def.mode) {
			case 0:
				return;
			case SrcColMapping.MODE_PASSTHROUGH:
				this.preparedRow.setComparable(def.targetPos, vl);//pass through
				break;
			case SrcColMapping.MODE_FORMULA:
				tmpValsForFomulas.putValue(def.name, vl);//formula
				tmpTargetsWithFormulas.putAll(def.calcs);//formula
				break;
			case SrcColMapping.MODE_PASSTHROUGH | SrcColMapping.MODE_FORMULA:
				this.preparedRow.setComparable(def.targetPos, vl);//pass through
				tmpValsForFomulas.putValue(def.name, vl);//formula
				tmpTargetsWithFormulas.putAll(def.calcs);//formula
				break;
			case SrcColMapping.MODE_NEEDS_CACHING:
				LH.warning(log, "Illegal state");
				break;
			case SrcColMapping.MODE_NEEDS_CACHING | SrcColMapping.MODE_PASSTHROUGH:
				this.preparedRow.setComparable(def.targetPos, vl);//pass through
				LH.warning(log, "Illegal state");
				break;
			case SrcColMapping.MODE_FORMULA | SrcColMapping.MODE_NEEDS_CACHING:
				tmpValsForFomulas.putValue(def.name, vl);//formula
				tmpTargetsWithFormulas.putAll(def.calcs);//formula
				cache[def.cachePosition] = vl;//caching
				//				cache.put(nm, vl);//caching
				break;
			case SrcColMapping.MODE_PASSTHROUGH | SrcColMapping.MODE_FORMULA | SrcColMapping.MODE_NEEDS_CACHING:
				this.preparedRow.setComparable(def.targetPos, vl);//pass through
				tmpValsForFomulas.putValue(def.name, vl);//formula
				tmpTargetsWithFormulas.putAll(def.calcs);//formula
				cache[def.cachePosition] = vl;//caching
				//				cache.put(nm, vl);//caching
				break;
		}
	}

	private String position2cacheName[] = OH.EMPTY_STRING_ARRAY;
	private Map<String, Integer> cacheName2position = new HashMap<String, Integer>();

	public void compile(Map<String, Row> columns) {
		this.tgtTable = (AmiTableImpl) this.db.getAmiTable(targetTable);
		this.targetDef = AmiTrigger_Join.getDependenciesDef(db, tgtTable);
		if (tgtTable == null) {
			this.isInvalid = true;
			return;
		}

		this.sourceNameColumnValid = false;
		if (this.sourceNameColumn != null) {
			AmiColumnImpl<?> t = this.tgtTable.getColumnNoThrow(sourceNameColumn);
			if (t == null)
				LH.warning(log, "REPLICATION '" + this.name + "' has mapping Error, Option 'SrcNameColumn' references missing column: " + sourceNameColumn);
			else if (t.getType() != String.class)
				LH.warning(log, "REPLICATION '" + this.name + "' has mapping Error, Option 'SrcNameColumn' references column that is not of type String: " + sourceNameColumn);
			else
				this.sourceNameColumnValid = true;
		}
		this.sourceIdColumnValid = false;
		if (this.sourceIdColumn != null) {
			AmiColumnImpl<?> t = this.tgtTable.getColumnNoThrow(sourceIdColumn);
			if (t == null)
				LH.warning(log, "REPLICATION '" + this.name + "' has mapping Error, Option 'SrcIdColumn' references missing column: " + sourceNameColumn);
			else if (t.getType() != Long.class)
				LH.warning(log, "REPLICATION '" + this.name + "' has mapping Error, Option 'SrcIdColumn' references column that is not of type long: " + sourceNameColumn);
			else
				this.sourceIdColumnValid = true;
		}
		final AmiImdbScriptManager sm = db.getScriptManager();
		tgtColsToCalc.clear();
		srcCol2Mapping.clear();
		srcColCode2Mapping.clear();
		com.f1.utils.structs.table.stack.MutableCalcFrame types = new MutableCalcFrame();
		if (columns != null)
			for (Entry<String, Row> i : columns.entrySet()) {
				String columnName = i.getKey();
				try {
					String dataType = i.getValue().get("DataType", String.class);
					types.putType(columnName, AmiUtils.METHOD_FACTORY.forName(dataType));
				} catch (Exception e) {
					types.putType(columnName, Object.class);
				}
			}
		this.tmpValsForFomulas = types;
		this.tmpValuesstackFrame.reset(this.tmpValsForFomulas);
		//		for (MappingEntry<String, AmiCenterReplicationColumnDef> i : schema.getColumns().entries())
		//			types.put(i.getKey(), i.getValue().getType());
		for (String e : types.getVarKeys())
			srcCol2Mapping.put(e, new SrcColMapping(e));
		if (mappingNodes != null) {
			ChildCalcTypesStack context = new ChildCalcTypesStack(db.getGlobalSession().getReusableTopStackFrame(), types);
			Set<String> tmpSet = new HashSet<String>();
			for (int i = 0; i < mappingNodes.length; i++) {
				Node col = mappingNodes[i];
				String targetName;
				OperationNode op = (OperationNode) col;
				VariableNode vn = (VariableNode) op.getLeft();
				targetName = vn.getVarname();
				if (tgtTable.getColumnNoThrow(targetName) == null) {
					LH.warning(log, "REPLICATION '" + this.name + "' has mapping Error, Target column not found: " + targetName);
					continue;
				}
				DerivedCellCalculator calc;
				try {
					calc = sm.getSqlProcessor().getParser().toCalc(op.getRight(), context);
				} catch (Exception e) {
					LH.warning(log, "REPLICATION '" + this.name + "' has mapping Error for Target column: " + targetName, e);
					continue;
				}
				DerivedHelper.getDependencyIds(calc, (Set) tmpSet);
				if (tmpSet.size() > 0) {
					for (String s : tmpSet) {
						SrcColMapping def = srcCol2Mapping.get(s);
						def.mode |= SrcColMapping.MODE_FORMULA;
						if (tmpSet.size() > 1) {//if a calc references two or more  vars, we need to cache in case one changes
							def.mode |= SrcColMapping.MODE_NEEDS_CACHING;
							if (!this.cacheName2position.containsKey(s)) {
								int pos = this.cacheName2position.size();
								this.cacheName2position.put(s, pos);
								def.cachePosition = pos;
								this.position2cacheName = AH.append(this.position2cacheName, s);
							}
						}
						if (def.calcs == null)
							def.calcs = new HashMap<String, DerivedCellCalculator>();
						def.calcs.put(targetName, calc);
					}
					tmpSet.clear();
				}
				tgtColsToCalc.put(targetName, calc);
			}
		}
		for (String e : types.getVarKeys()) {
			int loc = tgtTable.getColumnLocation(e);
			if (loc != -1 && !tgtColsToCalc.containsKey(e)) {
				SrcColMapping srcColMapping = srcCol2Mapping.get(e);
				srcColMapping.mode |= SrcColMapping.MODE_PASSTHROUGH;
				srcColMapping.targetPos = loc;
			}
		}
		this.hasCaching = this.position2cacheName.length > 0;
		this.hasFormulas = tgtColsToCalc.size() > 0;
		this.isInvalid = false;
		this.preparedRow = tgtTable.createAmiPreparedRow();
		this.amiIdMappings.clear();
	}

	public void onSchemaChanged(boolean resubscribe, CalcFrameStack sf) {
		Map<String, Row> schema = this.replications.getSchema(this.sourceTable);
		if (schema == null)
			return;
		AmiTableImpl tgtTable = (AmiTableImpl) this.db.getAmiTable(targetTable);
		String t = AmiTrigger_Join.getDependenciesDef(db, tgtTable);
		if (OH.eq(this.targetDef, t))
			return;
		this.targetDef = t;
		compile(schema);
		if (resubscribe && !isInvalid) {
			this.tgtTable.clearRows(sf);
			this.db.getReplicator().resubscribe(this.getName());
		}
	}

	public boolean isInvalid() {
		return isInvalid;
	}

	public boolean isSourceTableExists() {
		return this.replications.getSchema(this.sourceTable) != null;
	}

	public void onDisconnect() {
		this.srcColCode2Mapping.clear();
	}

}
