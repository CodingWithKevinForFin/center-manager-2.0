package com.f1.ami.web.rt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebAbstractRealtimeProcessor;
import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulasImpl;
import com.f1.ami.web.AmiWebFormulasListener;
import com.f1.ami.web.AmiWebObject;
import com.f1.ami.web.AmiWebObjectFields;
import com.f1.ami.web.AmiWebObjectFieldsImpl;
import com.f1.ami.web.AmiWebRealtimeObjectListener;
import com.f1.ami.web.AmiWebRealtimeObjectManager;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.IterableAndSize;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.impl.IdentityHasher;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebRealtimeProcessor_Decorate extends AmiWebAbstractRealtimeProcessor {
	private static final Logger log = LH.get();
	private static final String ASSIGN = "assign_";

	public AmiWebRealtimeProcessor_Decorate(AmiWebService service) {
		super(service);
	}

	public AmiWebRealtimeProcessor_Decorate(AmiWebService service, String alias) {
		super(service, alias);
	}

	public void setLeft(AmiWebRealtimeObjectManager left) {
		this.left = left;
		lowerRealtimeIds.add(left.getRealtimeId());
		this.left.addAmiListener(this);
		for (AmiWebObject i : this.left.getAmiObjects())
			onAmiEntityAdded(this.left, i);
	}

	public void addRight(String id, AmiWebRealtimeObjectManager right, com.f1.base.CalcTypes leftVars, com.f1.base.CalcTypes rightVars, String leftExpression,
			String rightExpression, Map<String, String> selects) {
		OH.assertNotNull(this.left);
		this.others.add(right);
		lowerRealtimeIds.add(right.getRealtimeId());
		Index index = new Index(id, right, leftVars, rightVars, leftExpression, rightExpression, selects);
		for (Output o : this.leftToObjectIndex.values())
			index.onLeftAdded(o);
		CH.putOrThrow(this.indexesById, id, index);
		indexes = AH.append(this.indexes, index);
	}

	@Override
	public String getType() {
		return AmiWebRealtimeProcessorPlugin_Decorate.PLUGIN_ID;
	}

	private com.f1.utils.structs.table.stack.BasicCalcTypes leftVarTypes = new com.f1.utils.structs.table.stack.BasicCalcTypes();
	private AmiWebRealtimeObjectManager left;
	final private List<AmiWebRealtimeObjectManager> others = new ArrayList<AmiWebRealtimeObjectManager>();
	final private Set<String> lowerRealtimeIds = new HashSet<String>();
	private Index[] indexes = new Index[0];
	private Map<String, Index> indexesById = new HashMap<String, AmiWebRealtimeProcessor_Decorate.Index>();

	private HasherMap<AmiWebObject, Output> leftToObjectIndex = new HasherMap<AmiWebObject, Output>();

	@Override
	public IterableAndSize<AmiWebObject> getAmiObjects() {
		return (IterableAndSize) leftToObjectIndex.values();
	}

	@Override
	public CalcTypes getRealtimeObjectschema() {
		BasicCalcTypes r = new com.f1.utils.structs.table.stack.BasicCalcTypes(this.left.getRealtimeObjectsOutputSchema());
		for (Index i : this.indexes)
			AmiUtils.mergeTo(r, i.asTypes);
		return r;
	}

	@Override
	public CalcTypes getRealtimeObjectsOutputSchema() {
		BasicCalcTypes r = new com.f1.utils.structs.table.stack.BasicCalcTypes(this.left.getRealtimeObjectsOutputSchema());
		for (Index i : this.indexes)
			AmiUtils.mergeTo(r, i.asTypes);
		return r;
	}

	@Override
	public Set<String> getLowerRealtimeIds() {
		return lowerRealtimeIds;
	}

	@Override
	public void onAmiEntitiesReset(AmiWebRealtimeObjectManager manager) {
		rebuild();
	}

	@Override
	public void onAmiEntityAdded(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		Entry<AmiWebObject, Output> node = leftToObjectIndex.getOrCreateEntry(entity);
		if (node.getValue() != null)
			throw new IllegalStateException("Duplicate entity: " + entity);
		Output output = new Output(entity, getService().getNextAmiObjectUId());
		node.setValue(output);
		for (Index i : this.indexes)
			i.onLeftAdded(output);
		fireAmiEntityAdded(output);
	}

	private final AmiWebObjectFieldsImpl tmpFields = new AmiWebObjectFieldsImpl();

	@Override
	public void onAmiEntityUpdated(AmiWebRealtimeObjectManager manager, AmiWebObjectFields fields, AmiWebObject entity) {
		Output output = leftToObjectIndex.get(entity);
		boolean indexChanged = false;
		for (int i = 0; i < fields.getChangesCount(); i++) {
			String key = fields.getChangeField(i);
			output.put(key, entity.get(key));
		}
		tmpFields.clear();
		if (output != null)
			for (Index i : this.indexes)
				if (i.onLeftUpdated(fields, output)) {
					for (String s : i.asKeys)
						tmpFields.addChange(s, null);
					indexChanged = true;
				}
		if (indexChanged) {
			tmpFields.addChanges(fields);
			fireAmiEntityUpdated(tmpFields, output);
		} else {
			if (fields == null)
				entity.fill((CalcFrame) output);
			else {
				for (int i = 0, n = fields.getChangesCount(); i < n; i++) {
					String name = fields.getChangeField(i);
					output.put(name, entity.get(name));
				}
			}
			fireAmiEntityUpdated(fields, output);
		}
	}

	@Override
	public void onAmiEntityRemoved(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		Output output = leftToObjectIndex.remove(entity);
		if (output != null) {
			for (Index i : this.indexes)
				i.onLeftRemoved(output);
			fireAmiEntityRemoved(output);
		}
	}

	private static final Object SKIP = new Object();

	public class Index implements AmiWebRealtimeObjectListener, AmiWebDomObject, AmiWebFormulasListener {

		public BasicCalcTypes asTypes;
		private BasicCalcTypes rightVarTypes = new BasicCalcTypes();
		String[] asKeys;
		String[] asValues;
		DerivedCellCalculator[] asCalcs;
		Object asValuesTmp[];
		HasherMap<Object, Set<Output>> leftIndex = new HasherMap<Object, Set<Output>>();
		HasherMap<Output, Object> leftToKeyIndex = new HasherMap<Output, Object>(IdentityHasher.INSTANCE);
		HasherMap<Object, AmiWebObject> rightIndex = new HasherMap<Object, AmiWebObject>();

		final private AmiWebRealtimeObjectManager right;
		private String ari;
		private String amiLayoutFullAliasDotId;
		final private String id;
		final private AmiWebFormulasImpl formulas;
		private boolean isManagedByDomManager = false;
		final private AmiWebFormula leftFormula;
		final private AmiWebFormula rightFormula;
		private BasicCalcTypes rightVars;
		private BasicCalcTypes leftVars;
		private boolean init;

		public Index(String id, AmiWebRealtimeObjectManager right, CalcTypes leftVars, CalcTypes rightVars, String leftCalc, String rightCalc, Map<String, String> selects) {
			this.rightVars = new BasicCalcTypes(rightVars);
			this.leftVars = new BasicCalcTypes(leftVars);
			formulas = new AmiWebFormulasImpl(this);
			this.formulas.addFormulasListener(this);
			this.id = id;
			this.right = right;
			this.leftFormula = this.formulas.addFormula("left", Object.class);
			this.rightFormula = this.formulas.addFormula("right", Object.class);
			this.leftFormula.setFormula(leftCalc, false);
			this.rightFormula.setFormula(rightCalc, false);
			{
				Set<Object> sink = new HashSet<Object>();
				DerivedHelper.getDependencyIds(this.leftFormula.getFormulaCalc(), sink);
				leftVarTypes.putAll(leftVars);
			}
			{
				Set<Object> sink = new HashSet<Object>();
				DerivedHelper.getDependencyIds(this.rightFormula.getFormulaCalc(), sink);
				asKeys = new String[selects.size()];
				asValues = new String[selects.size()];
				asCalcs = new DerivedCellCalculator[selects.size()];
				asValuesTmp = new Object[selects.size()];
				int n = 0;
				asTypes = new com.f1.utils.structs.table.stack.BasicCalcTypes();
				for (Entry<String, String> entry : selects.entrySet()) {
					AmiWebFormula f = this.formulas.addFormula(ASSIGN + entry.getKey(), Object.class);
					asKeys[n] = entry.getKey();
					asValues[n] = entry.getValue();
					f.setFormula(entry.getValue(), false);
					asCalcs[n] = f.getFormulaCalc();
					//					asCalcs[n] = cp.toCalc(entry.getValue(), rightVars);
					DerivedHelper.getDependencyIds(this.asCalcs[n], sink);
					asTypes.putType(asKeys[n], this.asCalcs[n].getReturnType());
					n++;
				}
				this.rightVarTypes = new BasicCalcTypes();
				for (Object s : sink) {
					Class<?> type = rightVars.getType((String) s);
					if (type != null)
						this.rightVarTypes.putType((String) s, type);
				}
			}
			ReusableCalcFrameStack sf = getStackFrame();
			right.addAmiListener(this);
			for (AmiWebObject o : right.getAmiObjects())
				this.rightIndex.put(getKeyFromRight(o), o);
			updateAri();
			this.init = true;
		}

		private Object getKeyFromLeft(Output i) {
			try {
				DerivedCellCalculator c = this.leftFormula.getFormulaCalc();
				return c == null ? null : c.get(getStackFrame().reset(i.leftEntity));
			} catch (Exception e) {
				LH.w(log, e);
				return null;
			}
		}
		private Object getKeyFromRight(AmiWebObject i) {
			try {
				DerivedCellCalculator c = this.rightFormula.getFormulaCalc();
				return c == null ? null : c.get(getStackFrame().reset(i));
			} catch (Exception e) {
				LH.w(log, e);
				return null;
			}
		}
		@Override
		public void onAmiEntitiesReset(AmiWebRealtimeObjectManager manager) {
			rebuild();
		}

		@Override
		public void onAmiEntityAdded(AmiWebRealtimeObjectManager manager, AmiWebObject rightEntity) {
			Object key = getKeyFromRight(rightEntity);
			this.rightIndex.put(key, rightEntity);
			Set<Output> leftEntities = this.leftIndex.get(key);
			if (CH.isntEmpty(leftEntities)) {
				tmpFields.clear();
				writeRightToLeft(null, rightEntity, leftEntities, true);
			}
		}

		@Override
		public void onAmiEntityUpdated(AmiWebRealtimeObjectManager manager, AmiWebObjectFields fields, AmiWebObject rightEntity) {
			Object key = getKeyFromRight(rightEntity);
			this.rightIndex.put(key, rightEntity);
			Set<Output> leftEntities = this.leftIndex.get(key);
			if (CH.isntEmpty(leftEntities)) {
				tmpFields.clear();
				writeRightToLeft(fields, rightEntity, leftEntities, true);
			}
		}

		private void writeRightToLeft(AmiWebObjectFields fields, AmiWebObject rightEntity, Set<Output> leftEntities, boolean shouldPublish) {
			ReusableCalcFrameStack sf = getStackFrame();
			sf.reset(rightEntity);
			for (int i = 0; i < asKeys.length; i++) {
				try {
					asValuesTmp[i] = asCalcs[i].get(sf);
				} catch (Exception e) {
					LH.w(log, e);
					asValuesTmp[i] = SKIP;
				}
			}
			if (shouldPublish) {
				for (Output row : leftEntities) {
					tmpFields.clear();
					for (int i = 0; i < asKeys.length; i++) {
						Object newVal = asValuesTmp[i];
						if (newVal == SKIP)
							continue;
						Object oldVal = row.put(asKeys[i], newVal);
						if (OH.ne(oldVal, newVal))
							tmpFields.addChange(asKeys[i], oldVal);
					}
					if (tmpFields.getChangesCount() > 0)
						fireAmiEntityUpdated(tmpFields, row);
				}
			} else {
				for (Output row : leftEntities)
					for (int i = 0; i < asKeys.length; i++)
						row.put(asKeys[i], asValuesTmp[i]);
			}
		}

		@Override
		public void onAmiEntityRemoved(AmiWebRealtimeObjectManager anager, AmiWebObject rightEntity) {
			CalcFrameStack sf = getStackFrame();
			Object key = getKeyFromRight(rightEntity);
			this.rightIndex.remove(key);
		}

		public void onLeftAdded(Output leftEntity) {
			CalcFrameStack sf = getStackFrame();
			Object key = getKeyFromLeft(leftEntity);
			this.leftToKeyIndex.put(leftEntity, key);
			Entry<Object, Set<Output>> existing = this.leftIndex.getOrCreateEntry(key);
			Set<Output> s = existing.getValue();
			if (s == null)
				existing.setValue(s = new LinkedHasherSet<Output>());
			s.add(leftEntity);
			AmiWebObject rightEntity = this.rightIndex.get(key);
			if (rightEntity != null)
				writeRightToLeft(null, rightEntity, s.size() == 1 ? s : Collections.singleton(leftEntity), false);
		}
		public void onLeftRemoved(Output leftEntity) {
			Object key = this.leftToKeyIndex.remove(leftEntity);
			Set<Output> s = this.leftIndex.get(key);
			if (s != null)
				s.remove(leftEntity);
		}

		public boolean onLeftUpdated(AmiWebObjectFields fields, Output leftEntity) {
			CalcFrameStack sf = getStackFrame();
			Object origKey = this.leftToKeyIndex.get(leftEntity);
			Object key = getKeyFromLeft(leftEntity);
			if (OH.ne(key, origKey)) {
				this.leftToKeyIndex.put(leftEntity, key);
				Set<Output> s = this.leftIndex.get(origKey);
				if (s != null)
					s.remove(origKey);
				Entry<Object, Set<Output>> existing = this.leftIndex.getOrCreateEntry(key);
				s = existing.getValue();
				if (s == null)
					existing.setValue(s = new LinkedHasherSet<Output>());
				s.add(leftEntity);
				AmiWebObject rightEntity = this.rightIndex.get(key);
				if (rightEntity != null)
					writeRightToLeft(fields, rightEntity, s.size() == 1 ? s : Collections.singleton(leftEntity), false);
				return true;
			}
			return false;
		}

		public AmiWebRealtimeProcessor_Decorate getProcessor() {
			return AmiWebRealtimeProcessor_Decorate.this;
		}

		@Override
		public String toDerivedString() {
			return getAri();
		}

		@Override
		public StringBuilder toDerivedString(StringBuilder sb) {
			return sb.append(getAri());
		}

		@Override
		public String getAriType() {
			return AmiWebDomObject.ARI_TYPE_JOIN;
		}

		@Override
		public String getDomLabel() {
			return id;
		}

		@Override
		public String getAmiLayoutFullAlias() {
			return AmiWebRealtimeProcessor_Decorate.this.getAmiLayoutFullAlias();
		}

		@Override
		public String getAmiLayoutFullAliasDotId() {
			return amiLayoutFullAliasDotId;
		}

		@Override
		public String getAri() {
			return this.ari;
		}

		@Override
		public void updateAri() {
			String oldAri = this.ari;
			this.amiLayoutFullAliasDotId = AmiWebRealtimeProcessor_Decorate.this.getAmiLayoutFullAliasDotId() + "?" + getDomLabel();
			this.ari = AmiWebDomObject.ARI_TYPE_JOIN + ":" + this.amiLayoutFullAliasDotId;
			if (OH.ne(this.ari, oldAri)) {
				this.getService().getDomObjectsManager().fireAriChanged(this, oldAri);
			}
		}

		@Override
		public List<AmiWebDomObject> getChildDomObjects() {
			return Collections.EMPTY_LIST;
		}

		@Override
		public AmiWebDomObject getParentDomObject() {
			return AmiWebRealtimeProcessor_Decorate.this;
		}

		@Override
		public Class<?> getDomClassType() {
			return this.getClass();
		}

		@Override
		public Object getDomValue() {
			return this;
		}

		@Override
		public boolean isTransient() {
			return AmiWebRealtimeProcessor_Decorate.this.isTransient();
		}

		@Override
		public void setTransient(boolean isTransient) {
			throw new UnsupportedOperationException();
		}

		@Override
		public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
			return null;
		}

		@Override
		public void addToDomManager() {
			if (this.isManagedByDomManager == false) {
				AmiWebService service = this.getService();
				service.getDomObjectsManager().addManagedDomObject(this);
				service.getDomObjectsManager().fireAdded(this);
				this.isManagedByDomManager = true;
			}

		}

		@Override
		public void removeFromDomManager() {
			this.getService().getDomObjectsManager().fireRemoved(this);
			if (this.isManagedByDomManager == true) {
				AmiWebService service = this.getService();
				service.getDomObjectsManager().removeManagedDomObject(this);
				this.isManagedByDomManager = false;
			}
		}

		@Override
		public AmiWebFormulasImpl getFormulas() {
			return formulas;
		}

		@Override
		public AmiWebService getService() {
			return AmiWebRealtimeProcessor_Decorate.this.getService();
		}

		@Override
		public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
			if (f == this.leftFormula)
				return new com.f1.utils.structs.table.stack.CalcTypesTuple2(leftVars, left.getRealtimeObjectsOutputSchema());
			return new com.f1.utils.structs.table.stack.CalcTypesTuple2(rightVars, this.right.getRealtimeObjectsOutputSchema());
		}

		@Override
		public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
			if (!init)
				return;
			if (formula == this.leftFormula) {
			} else if (formula == this.rightFormula) {
			} else {
				String formulaId = formula.getFormulaId();
				if (formulaId.startsWith(ASSIGN)) {
					String key = SH.stripPrefix(formulaId, ASSIGN, true);
					for (int i = 0; i < this.asKeys.length; i++) {
						if (OH.eq(this.asKeys[i], key))
							this.asCalcs[i] = formula.getFormulaCalc();
					}
				}
			}
			AmiWebRealtimeProcessor_Decorate.this.rebuild();

		}

		public void clear() {
			this.rightIndex.clear();
			this.leftIndex.clear();
			this.leftToKeyIndex.clear();
			this.rightIndex.clear();
		}

		@Override
		public void onLowerAriChanged(AmiWebRealtimeObjectManager manager, String oldAri, String newAri) {
		}

		@Override
		public void onSchemaChanged(AmiWebRealtimeObjectManager manager, byte status, Map<String, Tuple2<Class, Class>> columns) {
			AmiWebRealtimeProcessor_Decorate.this.onSchemaChanged(status, columns);
		}
		@Override
		public String objectToJson() {
			return DerivedHelper.toString(this);
		}

	}

	private class Output extends HashMap<String, Object> implements AmiWebObject {

		final private long uniqueId;
		public final AmiWebObject leftEntity;

		public Output(AmiWebObject leftEntity, long uniqueId) {
			this.uniqueId = uniqueId;
			this.leftEntity = leftEntity;
			leftEntity.fill((CalcFrame) this);
		}
		@Override
		public StringBuilder toString(StringBuilder sink) {
			return sink.append(super.toString());
		}

		public Object putValue(String s, Object newValue) {
			return super.put(s, newValue);
		}

		@Override
		public Object getParam(String param) {
			return super.get(param);
		}

		@Override
		public long getUniqueId() {
			return uniqueId;
		}

		@Override
		public String getObjectId() {
			return null;
		}

		@Override
		public long getId() {
			return uniqueId;
		}

		@Override
		public String getTypeName() {
			return getName();
		}

		@Override
		public boolean equals(Object o) {
			return o == this;
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(this);
		}
		@Override
		public void fill(Map<String, Object> sink) {
			sink.putAll(this);
		}
		@Override
		public void fill(CalcFrame sink) {
			for (String s : keySet())
				sink.putValue(s, get(s));
		}
		@Override
		public Object getValue(String key) {
			return get(key);
		}
		@Override
		public Class<?> getType(String key) {
			return getRealtimeObjectschema().getType(key);
		}
		@Override
		public Iterable<String> getVarKeys() {
			return getRealtimeObjectschema().getVarKeys();
		}
		@Override
		public int getVarsCount() {
			return getRealtimeObjectschema().getVarsCount();
		}
		@Override
		public boolean isVarsEmpty() {
			return false;
		}

	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("leftId", this.left.getRealtimeId());
		List<Map<String, Object>> t = new ArrayList<Map<String, Object>>();
		for (Index i : this.indexes) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("rightId", i.right.getRealtimeId());
			m.put("leftKey", i.leftFormula.getFormulaConfig());
			m.put("rightKey", i.rightFormula.getFormulaConfig());
			List<Map<String, Object>> selects = new ArrayList<Map<String, Object>>();
			for (int n = 0; n < i.asKeys.length; n++)
				selects.add(CH.m(new HashMap<String, Object>(), "name", i.asKeys[n], "expression", i.asValues[n]));
			m.put("selects", selects);
			m.put("rightVarTypes", AmiWebUtils.toVarTypesConfiguration(getService(), this.getAlias(), i.rightVarTypes, new HashMap<String, String>()));
			t.add(m);
		}
		r.put("leftVarTypes", AmiWebUtils.toVarTypesConfiguration(getService(), this.getAlias(), this.leftVarTypes, new HashMap<String, String>()));
		r.put("rights", t);
		return r;
	}

	@Override
	public void rebuild() {
		for (Output i : leftToObjectIndex.values())
			fireAmiEntityRemoved(i);
		leftToObjectIndex.clear();
		for (Index i : this.indexes) {
			i.clear();
		}
		for (AmiWebObject o : this.left.getAmiObjects())
			onAmiEntityAdded(null, o);
		for (Index i : this.indexes) {
			for (AmiWebObject o : i.right.getAmiObjects())
				i.onAmiEntityAdded(null, o);
		}
	}

	@Override
	public void init(String alias, Map<String, Object> configuration) {
		super.init(alias, configuration);
		String leftId = CH.getOrThrow(Caster_String.INSTANCE, configuration, "leftId");
		setLeft(this.getService().getWebManagers().getAmiObjectsByType(leftId));
		this.leftVarTypes = AmiWebUtils.fromVarTypesConfiguration(getService(), (Map<String, String>) configuration.get("leftVarTypes"));
		List<Map<String, Object>> t = CH.getOrThrow(List.class, configuration, "rights");
		for (Map<String, Object> i : t) {
			com.f1.utils.structs.table.stack.BasicCalcTypes rightVarTypes = AmiWebUtils.fromVarTypesConfiguration(getService(), (Map<String, String>) i.get("rightVarTypes"));
			String id = CH.getOr(Caster_String.INSTANCE, i, "id", null);
			String rightId = CH.getOrThrow(Caster_String.INSTANCE, i, "rightId");
			String leftKey = CH.getOrThrow(Caster_String.INSTANCE, i, "leftKey");
			String rightKey = CH.getOrThrow(Caster_String.INSTANCE, i, "rightKey");
			List<Map<String, Object>> selects = CH.getOrThrow(List.class, i, "selects");
			HasherMap<String, String> selectsMap = new HasherMap<String, String>(selects.size());
			for (Map<String, Object> select : selects) {
				String name = CH.getOrThrow(Caster_String.INSTANCE, select, "name");
				String expression = CH.getOrThrow(Caster_String.INSTANCE, select, "expression");
				selectsMap.put(name, expression);
			}
			if (id == null)
				id = getNextJoinId();
			addRight(id, this.getService().getWebManagers().getAmiObjectsByType(rightId), this.leftVarTypes, rightVarTypes, leftKey, rightKey, selectsMap);
		}
	}

	public String getNextJoinId() {
		return SH.getNextId("join1", this.indexesById.keySet());
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return new ArrayList<AmiWebDomObject>(this.indexesById.values());
	}

	@Override
	public void onLowerAriChanged(AmiWebRealtimeObjectManager manager, String oldAri, String newAri) {
		CH.removeOrThrow(lowerRealtimeIds, oldAri);
		CH.addOrThrow(lowerRealtimeIds, newAri);
	}

	@Override
	protected void onSchemaChanged(byte status, Map<String, Tuple2<Class, Class>> columns) {
		super.onSchemaChanged(status, columns);
	}

}
