package com.f1.ami.relay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.CalcFrame;
import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.base.Pointer;
import com.f1.utils.CH;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

//This represents the configuration of a line in the relay.transform
public class AmiRelayTransform implements Comparable<AmiRelayTransform>, Lockable {

	final private BasicMultiMap.List<String, String> tableRenamings = new BasicMultiMap.List<String, String>();//source -> target 
	final private List<String> allTableRenamgins = new ArrayList<String>();// target tables to always send
	final private Set<String> tablesToSkip = new HashSet<String>();// source tables to skip
	final private boolean passthroughAllTables;
	final private boolean skipAllTables;
	final private String dictionaryName;
	final private boolean passthroughFields;
	final private byte onTrue;
	final private byte onFalse;
	private AmiRelayDictionary dictionary;
	final private String fileName;
	final private int lineNumber;
	private boolean locked;

	//the cache can change even after locking;
	private Map<String, String[]> tableRenamesCache = new CopyOnWriteHashMap<String, String[]>();
	private String transformName;
	private int priority;
	private String expression;
	private DerivedCellCalculator expressionCalc;
	private Set<Object> expressionDependencies;
	private long statsSentCount = 0;
	private long statsNanos = 0;
	private long statsMismatchCount;
	private long statsMatchCount;

	public AmiRelayTransform(String fileName, int lineNumber, String line) {
		this.fileName = fileName;
		this.lineNumber = lineNumber;
		String parts[] = SH.splitWithEscape(';', '\\', line);
		if (parts.length != 8)
			throw new RuntimeException("Expecting 8 fields (TransformName;Priority;ObjectTypes;Dictionary;Expression;Options;OnTrue;OnFalse) not " + parts.length);
		this.transformName = parts[0];
		if (SH.isnt(transformName))
			throw new RuntimeException("TransformName required: " + parts[0]);
		try {
			this.priority = SH.parseInt(parts[1]);
		} catch (Exception e) {
			throw new RuntimeException("Priority is an invalid number: " + parts[1], e);
		}
		{
			String objectNames[] = SH.splitWithEscape(',', '\\', parts[2]);
			boolean passthrough = false;
			for (String s : objectNames) {
				s = SH.trim(s);
				int i = s.indexOf('=');
				if (i == -1) {
					if ("*".equals(s))
						passthrough = true;
					else
						this.tableRenamings.putMulti(s, s);
				} else {
					String key = SH.trim(s.substring(0, i));
					String val = SH.trim(s.substring(i + 1));
					if ("".equals(key)) {
						this.tablesToSkip.add(val);
					} else if ("*".equals(val)) {
						this.allTableRenamgins.add(key);
					} else
						this.tableRenamings.putMulti(val, key);
				}
			}
			this.passthroughAllTables = passthrough;
			Set<String> dups = CH.comm(tablesToSkip, tableRenamings.keySet(), false, false, true);
			if (!dups.isEmpty())
				throw new RuntimeException("ObjectTypes has conflicts: " + SH.join(',', dups));
			this.skipAllTables = allTableRenamgins.isEmpty() && tableRenamings.isEmpty() && !passthroughAllTables;
		}
		this.dictionaryName = SH.trim(parts[3]);
		this.expression = SH.trim(parts[4]);
		if (SH.is(this.expression)) {
			if (SH.isnt(dictionaryName))
				throw new RuntimeException("When Expression is specified, Dictionary is required");
		}
		boolean passthroughFields = false;
		for (String s : SH.splitWithEscape(',', '\\', parts[5])) {
			if (SH.isnt(s))
				continue;
			s = SH.trim(s);
			if ("PASSTHROUGH".equalsIgnoreCase(s))
				passthroughFields = true;
			else
				throw new RuntimeException("Options has unknown Option: " + s + " (Valid options are: PASSTHROUGH)");
		}
		this.onTrue = AmiRelayRoute.parseAction("OnTrue", parts[6]);
		this.onFalse = AmiRelayRoute.parseAction("OnFalse", parts[7]);
		this.passthroughFields = passthroughFields;
		//			this.dictionariesList = new AmiRelayDictionary[this.dictionaries.length];
	}

	public void bindToDictionaries(Map<String, AmiRelayDictionary> m, AmiRelayScriptManager scriptManager) {
		LockedException.assertNotLocked(this);
		if (SH.is(dictionaryName)) {
			dictionary = m.get(this.dictionaryName);
			if (dictionary == null)
				throw new RuntimeException(
						describe() + ": Dictionaries references unknown dictionary: " + this.dictionaryName + " (known dictionaries: " + SH.join(',', m.keySet()) + ")");
			LockedException.assertLocked(dictionary);
		}
		if (SH.is(this.expression)) {
			try {
				this.expressionCalc = scriptManager.getSqlProcessor().getParser().toCalc(this.expression, new ChildCalcTypesStack(dictionary.getContext()));
				if (this.expressionCalc != null && this.expressionCalc.getReturnType() != Boolean.class)
					throw new RuntimeException("must return boolean");
				this.expressionDependencies = DerivedHelper.getDependencyIds(this.expressionCalc);
			} catch (Exception e) {
				throw new RuntimeException(describe() + " Error with Expression: ", e);
			}
		}

		lock();
	}

	private String describe() {
		return this.fileName + " at line " + this.lineNumber;
	}

	public boolean refersToDictionary() {
		return SH.is(dictionaryName) && !skipAllTables;
	}

	public String[] getMapTableToTargetTables(String name) {
		String[] r = tableRenamesCache.get(name);
		if (r == null) {
			Set<String> t = getTransformsInner(name);
			tableRenamesCache.put(name, r = t.toArray(new String[t.size()]));
		}
		return r;
	}
	private Set<String> getTransformsInner(String name) {
		if (skipAllTables || tablesToSkip.contains(name))
			return Collections.EMPTY_SET;
		Set<String> r = new LinkedHashSet<String>();
		List<String> t = tableRenamings.get(name);
		if (!CH.isEmpty(t))
			r.addAll(t);
		else {
			if (CH.isntEmpty(allTableRenamgins))
				r.addAll(allTableRenamgins);
			if (passthroughAllTables)
				r.add(name);
		}
		return r;
	}

	public boolean isContinueOnMatch() {
		return this.onTrue == AmiRelayRoute.ACTION_CONTINUE;
	}
	public boolean isContinueOnNotMatch() {
		return this.onFalse == AmiRelayRoute.ACTION_CONTINUE;
	}

	@Override
	public void lock() {
		this.locked = true;
	}

	@Override
	public boolean isLocked() {
		return this.locked;
	}

	public boolean isPassthroughFields() {
		return this.passthroughFields;
	}

	public byte[] convertParams(byte[] params, Pointer<String> id, String type, Mutable.Long e) {
		LockedException.assertLocked(this);
		if (!refersToDictionary())
			return params;
		if (this.expressionCalc != null) {
			Map<String, Object> input = this.dictionary.toInputParams(params, id, type, e);
			CalcFrame cf = new BasicCalcFrame(this.dictionary.getContext());
			for (Object i : this.expressionDependencies)
				cf.putValue((String) i, input.get(i));
			TopCalcFrameStack sf = new TopCalcFrameStack(cf);
			if (!Boolean.TRUE.equals(this.expressionCalc.get(sf)))
				return null;
			return this.dictionary.mapParams(input, passthroughFields, id, e);
		} else
			return this.dictionary.mapParams(params, passthroughFields, id, type, e);
	}

	@Override
	public int compareTo(AmiRelayTransform o) {
		int r = OH.compare(this.priority, o.priority);
		return r != 0 ? r : OH.compare(this.transformName, o.transformName);
	}

	public String getName() {
		return this.transformName;
	}

	public long getSentCount() {
		return this.statsSentCount;
	}
	public long getMismatchCount() {
		return this.statsMismatchCount;
	}
	public long getMatchCount() {
		return this.statsMatchCount;
	}
	public long getNanos() {
		return this.statsNanos;
	}

	public void incrementStats(long nanos, int sentCount) {
		this.statsNanos += nanos;
		if (sentCount > 0) {
			this.statsMatchCount++;
			this.statsSentCount += sentCount;
		} else
			this.statsMismatchCount++;
	}

	public String getDictionaryName() {
		return this.dictionaryName;
	}

	public void resetStats() {
		this.statsSentCount = 0;
		this.statsNanos = 0;
		this.statsMismatchCount = 0;
		this.statsMatchCount = 0;
	}
}
