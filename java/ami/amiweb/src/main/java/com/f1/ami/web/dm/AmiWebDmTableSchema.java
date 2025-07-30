package com.f1.ami.web.dm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebAmiScriptCallback;
import com.f1.ami.web.AmiWebScriptManagerForLayout;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.base.CalcTypes;
import com.f1.base.Column;
import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.base.ToStringable;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.DerivedHelper;

public class AmiWebDmTableSchema implements ToStringable, Lockable {

	final public static byte ON_CHANGE_ASK = 0;
	final public static byte ON_CHANGE_IGNORE = 1;
	final public static byte ON_CHANGE_APPLY = 2;

	private static final Logger log = LH.get();
	final private BasicIndexedList<String, String> types = new BasicIndexedList<String, String>();
	final private com.f1.utils.structs.table.stack.BasicCalcTypes classTypes = new com.f1.utils.structs.table.stack.BasicCalcTypes();

	private byte onChangeMode = ON_CHANGE_ASK;

	final private AmiWebDmTablesetSchema parent;
	final private String name;
	final private AmiWebService service;

	//copy constructor
	public AmiWebDmTableSchema(AmiWebDmTableSchema other, AmiWebDmTablesetSchema parent) {
		this.service = other.service;
		OH.assertNotNull(parent);
		this.parent = parent;
		this.name = other.name;
		this.onChangeMode = other.onChangeMode;
		this.types.addAll(other.types);
		this.classTypes.putAll(other.classTypes);
	}
	public AmiWebDmTableSchema(AmiWebService service, String name, AmiWebDmTablesetSchema parent) {
		this.service = service;
		OH.assertNotNull(parent);
		this.parent = parent;
		this.name = name;
	}
	public AmiWebDmTableSchema(AmiWebService service, Table name, AmiWebDmTablesetSchema parent) {
		this.service = service;
		OH.assertNotNull(parent);
		this.parent = parent;
		this.name = OH.assertNotNull(name.getTitle());
		AmiWebScriptManagerForLayout scriptManager = parent.getService().getScriptManager(parent.getCallback().getAmiLayoutAlias());
		for (Column i : name.getColumns()) {
			String type = scriptManager.forType(i.getType());
			addType((String) i.getId(), type);
		}
		lock();
	}
	public AmiWebDmTableSchema(AmiWebDmTableSchema schema, AmiWebDmTablesetSchema s, byte onChangeMode) {
		this.service = schema.service;
		this.parent = s;
		this.name = schema.name;
		this.classTypes.putAll(schema.getClassTypes());
		this.onChangeMode = onChangeMode;
		this.types.addAll(schema.types);
		lock();
	}

	@Deprecated
	public AmiWebDm getDm() {
		return this.parent.getDatamodel();
	}
	public Set<String> getColumnNames() {
		return types.keySet();
	}
	public String getType(String name) {
		return types.get(name);
	}
	public Class<?> getClassType(String name) {
		return classTypes.getType(name);
	}

	public void addType(String name, String type) {
		AmiWebScriptManagerForLayout scriptManager = service.getScriptManager(this.parent.getCallback().getAmiLayoutAlias());
		LockedException.assertNotLocked(this);
		//		if (type == AmiDatasourceColumn.TYPE_NONE)
		//			throw new IllegalArgumentException("column '" + name + "' is missing type");
		types.add(name, type);
		classTypes.putType(name, scriptManager.forName(type));
		names = null;
		classes = null;
	}

	public Table newEmptyTable() {
		lock();
		ensureNamesClassesReady();
		return new ColumnarTable(classes, names);
	}
	public Table mapToSchema(Table source, boolean forceNew) {
		lock();
		if (source == null) {
			return newEmptyTable();
		}
		TableList sourceRows = source.getRows();
		// why not columnar table?
		if (matches(source)) {
			return forceNew ? new ColumnarTable(source) : source;
		}
		ensureNamesClassesReady();
		Table target = new ColumnarTable(classes, names);
		if (source.getSize() == 0)
			return target;
		// deep copy, no need to wrap in COW
		Map<String, Column> tCols = target.getColumnsMap();

		TableList tRows = target.getRows();
		for (int i = 0, l = sourceRows.size(); i < l; i++)
			tRows.addRow(new Object[classes.length]);
		Column tCol;
		for (Column sCol : source.getColumns()) {
			tCol = tCols.get(sCol.getId());
			if (tCol == null)
				continue;
			Iterator<Row> sIt = sourceRows.iterator();
			Iterator<Row> tIt = target.getRows().iterator();
			int sLoc = sCol.getLocation();
			int tLoc = tCol.getLocation();
			Class<?> cast = tCol.getType();
			if (sCol.getType() == cast)
				while (sIt.hasNext())
					tIt.next().putAt(tLoc, sIt.next().getAt(sLoc));
			else
				while (sIt.hasNext())
					tIt.next().putAt(tLoc, tCol.getTypeCaster().cast(sIt.next().getAt(sLoc), false, false));
		}
		return target;
	}
	private boolean matches(Table t) {
		lock();
		if (t.getColumnsCount() != types.getSize())
			return false;
		for (Column c : t.getColumns()) {
			Class<?> type = classTypes.getType((String) c.getId());
			if (OH.ne(c.getTypeCaster().getCastToClass(), type))
				return false;
		}
		return true;
	}

	private String names[];
	private Class classes[];
	private boolean locked;

	private void ensureNamesClassesReady() {
		lock();
		if (names == null) {
			names = new String[types.getSize()];
			classes = new Class[types.getSize()];
			for (int i = 0; i < names.length; i++) {
				String name = types.getKeyAt(i);
				classes[i] = classTypes.getType(name);
				names[i] = name;
			}
		}
	}

	public CalcTypes getClassTypes() {
		return classTypes;
	}
	public String getName() {
		return name;
	}
	public AmiWebDmTablesetSchema getTablesetSchema() {
		return parent;
	}

	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = new HashMap<String, Object>();
		r.put("nm", name);
		r.put("oc", onChangeToString(onChangeMode));
		List<Map> typesList = new ArrayList<Map>(this.types.getSize());
		for (Entry<String, String> i : this.types)
			typesList.add(CH.m("nm", i.getKey(), "tp", i.getValue()));
		r.put("cols", typesList);
		return r;
	}
	public void init(Map<String, Object> val) {
		this.onChangeMode = parseOnChange(CH.getOr(Caster_String.INSTANCE, val, "oc", "ask"));
		List<Map> typesList = (List<Map>) CH.getOrThrow(Caster_Simple.OBJECT, val, "cols");
		for (Map m : typesList) {
			String tp = CH.getOrThrow(Caster_String.INSTANCE, m, "tp");
			String nm = CH.getOrThrow(Caster_String.INSTANCE, m, "nm");
			if (tp.length() == 1)
				addType(nm, parent.getService().getMethodFactory().forType(AmiWebUtils.saveCodeToType(parent.getService(), tp)));//backwards compatibility
			else
				addType(nm, tp);

		}
	}
	public byte getOnChangeMode() {
		return onChangeMode;
	}
	public void setOnChangeMode(byte isOptional) {
		LockedException.assertNotLocked(this);
		this.onChangeMode = isOptional;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		boolean first = true;
		sink.append(getName());
		sink.append("(");
		for (Entry<String, String> e : this.types) {
			if (first)
				first = false;
			else
				sink.append(", ");
			sink.append(e.getValue()).append(" ").append(e.getKey());
		}
		sink.append(")");
		return sink;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	public boolean isSame(AmiWebDmTableSchema nuw) {
		return DerivedHelper.areSame(this.classTypes, nuw.classTypes);
	}
	@Override
	public void lock() {
		this.locked = true;
	}
	@Override
	public boolean isLocked() {
		return locked;
	}

	public static String onChangeToString(byte type) {
		switch (type) {
			case ON_CHANGE_APPLY:
				return "apply";
			case ON_CHANGE_IGNORE:
				return "ignore";
			case ON_CHANGE_ASK:
				return "ask";
		}
		return "ask";
	}
	public static byte parseOnChange(String type) {
		if ("apply".equals(type))
			return ON_CHANGE_APPLY;
		if ("ignore".equals(type))
			return ON_CHANGE_IGNORE;
		if ("ask".equals(type))
			return ON_CHANGE_ASK;
		return ON_CHANGE_ASK;
	}
	public AmiWebAmiScriptCallback getCallback() {
		return this.parent.getCallback();
	}

}
