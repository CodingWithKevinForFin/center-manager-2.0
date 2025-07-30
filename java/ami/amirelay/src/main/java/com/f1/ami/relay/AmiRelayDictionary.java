package com.f1.ami.relay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.relay.fh.AmiRelayBytesToMapConverter;
import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.base.CalcFrame;
import com.f1.base.Caster;
import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.base.Pointer;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Object;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.CharSequenceHasher;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.sql.EmptyTableset;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.derived.BasicDerivedCellParser;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.MutableCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiRelayDictionary implements Lockable {
	private static final Logger log = LH.get();
	final private String fileName;
	final private String fullText;
	final private int position;

	final private String name;
	final private List<String> extendsNames;
	final private Map<String, Class> types;
	final private Map<String, Node> tgt2expr;
	final private List<AmiRelayDictionary> extendsList = new ArrayList<AmiRelayDictionary>();
	final private Map<String, DerivedCellCalculator> tgt2calcs = new HashMap<String, DerivedCellCalculator>();

	private BasicCalcTypes calcTypes;
	private Map<String, Caster<?>> calcCasters = new HashMap<String, Caster<?>>();
	private Tuple3<String, String[], DerivedCellCalculator>[] allTgt2calcs;//target,dependencies,values
	private Map<CharSequence, String[]> noCalcSrc2Tgt;
	private boolean locked;
	private boolean fastTransform;//Only field names are changing, so we can skip parsing the data portion

	public AmiRelayDictionary(String fileName, String fullText, int position, String name, List<String> extendsNames, Map<String, Class> types, Map<String, Node> tgt2expr) {
		this.fileName = fileName;
		this.position = position;
		this.fullText = fullText;
		this.name = name;
		this.extendsNames = extendsNames;
		this.types = types;
		this.tgt2expr = tgt2expr;
	}

	public String getName() {
		return name;
	}

	public List<String> getExtendsNames() {
		return extendsNames;
	}

	public String getFileLocation() {
		int linenum = SH.getLinePosition(this.fullText, this.position).getA();
		return this.fileName + ":" + (linenum + 1);
	}

	public void link(Map<String, AmiRelayDictionary> sink) {
		for (String i : this.extendsNames) {
			AmiRelayDictionary t = sink.get(i);
			if (t == null)
				throw new ExpressionParserException(this.fullText, position, "In " + getFileLocation() + " dictionary '" + name + "' extends missing dictionary: " + i);
			this.extendsList.add(t);
		}
	}

	public void assertNoCyclicDependencis(Set<String> parents) {
		if (!parents.add(name))
			throw new ExpressionParserException(this.fullText, position, "In " + getFileLocation() + " dictionary '" + name + "' has Cyclic dependency");
		for (AmiRelayDictionary i : this.extendsList)
			i.assertNoCyclicDependencis(parents);
		parents.remove(name);
	}

	public void compile(BasicDerivedCellParser dcp, MethodFactoryManager mfm) {
		LockedException.assertNotLocked(this);
		this.calcTypes = new BasicCalcTypes();
		this.calcTypes.putType("T", String.class);
		this.calcTypes.putType("E", Long.class);
		this.calcTypes.putType("I", String.class);
		for (Entry<String, Class> i : this.types.entrySet()) {
			String key = i.getKey();
			if (key.equals("T") && i.getValue() != String.class)
				throw new RuntimeException("Inconsistent type in transform '" + this.name + "' for variable T");
			if (key.equals("E") && i.getValue() != Long.class)
				throw new RuntimeException("Inconsistent type in transform '" + this.name + "' for variable E");
			if (key.equals("I") && i.getValue() != Long.class)
				throw new RuntimeException("Inconsistent type in transform '" + this.name + "' for variable I");
			this.calcTypes.putType(key, i.getValue());
		}
		for (AmiRelayDictionary i : this.extendsList) {
			BasicCalcTypes t = i.getTypes();
			for (Entry<String, Class<?>> es : t.getTypes().entrySet()) {
				Class<?> existing = this.calcTypes.getType(es.getKey());
				if (existing == null)
					this.calcTypes.putType(es.getKey(), es.getValue());
				else if (existing != es.getValue())
					throw new RuntimeException("Inconsistent type hierarchy in transform '" + this.name + "' for variable '" + es.getKey() + "': " + mfm.forType(existing) + " vs "
							+ mfm.forType(es.getValue()));
			}

			this.calcTypes.putAllIfAbsent(i.getTypes());
		}
		final CalcFrame frame = new BasicCalcFrame(getTypes());
		final CalcTypesStack context = new TopCalcFrameStack(EmptyTableset.INSTANCE, mfm, frame);
		this.tgt2calcs.clear();
		for (Entry<String, Node> i : this.tgt2expr.entrySet()) {
			final String k = i.getKey();
			final Node v = i.getValue();
			final DerivedCellCalculator calc;
			try {
				if (v instanceof VariableNode) {
					String varname = ((VariableNode) v).getVarname();
					calc = new DerivedCellCalculatorRef(v.getPosition(), Object.class, varname);
					this.calcTypes.putType(varname, Object.class);
				} else
					calc = dcp.toCalc(v, context);
			} catch (ExpressionParserException e) {
				e.setExpression(this.fullText);
				throw new RuntimeException("Error with file: " + this.getFileLocation(), e);
			}
			this.tgt2calcs.put(k, calc);
		}
		Map<String, DerivedCellCalculator> allTgt2calcs = new HashMap<String, DerivedCellCalculator>();
		visit(allTgt2calcs);
		this.allTgt2calcs = new Tuple3[allTgt2calcs.size()];
		int n = 0;
		boolean fastMode = true;
		if (this.calcTypes.getVarsCount() > 3 || allTgt2calcs.containsKey("I") || allTgt2calcs.containsKey("E"))
			fastMode = false;
		for (Entry<String, DerivedCellCalculator> i : allTgt2calcs.entrySet()) {
			Set<Object> dep = DerivedHelper.getDependencyIds(i.getValue());
			if (fastMode) {
				if (!(i.getValue() instanceof DerivedCellCalculatorRef))
					fastMode = false;
				else if (dep.contains("T") || dep.contains("I") || dep.contains("E"))
					fastMode = false;
			}
			String[] dependencies = dep.toArray(new String[dep.size()]);
			this.allTgt2calcs[n++] = new Tuple3<String, String[], DerivedCellCalculator>(i.getKey(), dependencies, i.getValue());
			for (String j : dependencies)
				this.calcCasters.put(j, Caster_Object.INSTANCE);
		}
		for (

		String s : this.calcTypes.getVarKeys()) {
			this.calcCasters.put(s, OH.getCaster(this.calcTypes.getType(s)));
		}
		this.fastTransform = fastMode;
		if (this.fastTransform) {
			this.noCalcSrc2Tgt = new HasherMap<CharSequence, String[]>(CharSequenceHasher.INSTANCE);
			for (Tuple3<String, String[], DerivedCellCalculator> i : this.allTgt2calcs) {
				String tgt = i.getA();
				String src = i.getB()[0];
				String[] targets = this.noCalcSrc2Tgt.get(src);
				if (targets == null)
					targets = new String[] { tgt };
				else
					targets = AH.append(targets, tgt);
				this.noCalcSrc2Tgt.put(src, targets);
			}
		}

		lock();

	}

	private void visit(Map<String, DerivedCellCalculator> tgt2calcSink) {
		for (Entry<String, DerivedCellCalculator> i : this.tgt2calcs.entrySet())
			if (!tgt2calcSink.containsKey(i.getKey()))
				tgt2calcSink.put(i.getKey(), i.getValue());
		for (AmiRelayDictionary i : this.extendsList)
			i.visit(tgt2calcSink);
	}

	public Class<?> getType(String key) {
		Class<?> r = this.types.get(key);
		if (r != null)
			return r;
		for (AmiRelayDictionary i : this.extendsList) {
			r = i.getType(key);
			if (r != null)
				return r;
		}
		throw new RuntimeException("Var not found: " + key);
	}

	public BasicCalcTypes getTypes() {
		return calcTypes;
	}

	private void mapParams(Map<String, Object> inParams, Map<String, Object> outParams, boolean passthroughFields) {
		MutableCalcFrame cf = new MutableCalcFrame();
		cf.putAllTypes(this.calcTypes);
		for (Entry<String, Object> i : inParams.entrySet()) {
			String key = i.getKey();
			Caster<?> tc = this.calcCasters.get(key);
			if (tc != null)
				cf.putTypeValue(key, tc.getCastToClass(), tc.cast(i.getValue(), false, false));
			else if (passthroughFields)
				outParams.put(key, i.getValue());
		}
		TopCalcFrameStack cfs = new TopCalcFrameStack(EmptyTableset.INSTANCE, AmiUtils.METHOD_FACTORY, cf);
		outer: for (Tuple3<String, String[], DerivedCellCalculator> i : this.allTgt2calcs) {
			for (String o : i.getB())
				if (!inParams.containsKey(o))
					continue outer;
			try {
				Object value = i.getC().get(cfs);
				outParams.put(i.getA(), value);
			} catch (Exception e) {
				LH.warning(log, "Error with Dictionary " + this.getName() + " for " + i.getA() + ": ", e);
			}
		}

	}

	@Override
	public void lock() {
		locked = true;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	public boolean fastTransform() {
		return this.fastTransform;
	}

	final private AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
	final private AmiRelayBytesToMapConverter converter2 = new AmiRelayBytesToMapConverter();

	public byte[] mapParams(byte[] params, boolean passthroughFields, Pointer<String> id, String type, Mutable.Long expires) {
		if (this.fastTransform)
			return converter2.mapKeys(params, this.noCalcSrc2Tgt, passthroughFields);
		Map<String, Object> inParams = toInputParams(params, id, type, expires);
		return mapParams(inParams, passthroughFields, id, expires);
	}

	public byte[] mapParams(Map<String, Object> inParams, boolean passthroughFields, Pointer<String> id, Mutable.Long expires) {
		Map<String, Object> outParams = new HashMap<String, Object>();
		mapParams(inParams, outParams, passthroughFields);
		for (Map.Entry<String, Object> e : outParams.entrySet()) {
			String key = e.getKey();
			if (key.length() == 1) {
				if ("I".equals(key))
					id.put(Caster_String.INSTANCE.cast(e.getValue()));
				else if ("E".equals(key))
					expires.value = e.getValue() == null ? 0L : (Long) e.getValue();
				else
					converter.append(key, e.getValue());
			} else
				converter.append(key, e.getValue());
		}
		return converter.toBytes();
	}

	public Map<String, Object> toInputParams(byte[] params, Pointer<String> id, String type, Mutable.Long expires) {
		converter.clear();
		Map<String, Object> inParams = AmiRelayBytesToMapConverter.read(params);
		if (id != null)
			inParams.put("I", id.get());
		inParams.put("T", type);
		if (expires.value != 0L)
			inParams.put("E", expires.value);
		return inParams;
	}

	public BasicCalcTypes getContext() {
		return this.calcTypes;
	}

	public Tuple3<String, String[], DerivedCellCalculator>[] getFields() {
		return this.allTgt2calcs;
	}

}
