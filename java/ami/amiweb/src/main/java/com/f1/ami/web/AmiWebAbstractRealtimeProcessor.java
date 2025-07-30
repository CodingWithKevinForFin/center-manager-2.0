package com.f1.ami.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.IterableAndSize;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public abstract class AmiWebAbstractRealtimeProcessor implements AmiWebRealtimeProcessor {

	private static final Logger log = LH.get();
	private AmiWebRealtimeObjectListener[] amiListeners = AmiWebRealtimeObjectListener.EMPTY_ARRAY;
	private Set<String> upperRealtimeIds = new HashSet<String>();
	final private AmiWebService service;
	private boolean isTransient;
	private String ari;
	private String alias;
	private String adn;
	private String name;
	private AmiWebFormulasImpl formulas;

	public AmiWebAbstractRealtimeProcessor(AmiWebService service) {
		this.service = service;
		this.formulas = new AmiWebFormulasImpl(this);
		this.stackFrame = service.createStackFrameReusable(this);
	}

	public AmiWebAbstractRealtimeProcessor(AmiWebService service, String alias) {
		this.service = service;
		this.formulas = new AmiWebFormulasImpl(this);
		this.alias = alias;
		this.stackFrame = service.createStackFrameReusable(this);
	}

	@Override
	public boolean removeAmiListener(AmiWebRealtimeObjectListener listener) {
		int i = AH.indexOf(listener, this.amiListeners);
		if (i == -1)
			return false;
		this.amiListeners = AH.remove(this.amiListeners, i);
		this.service.getWebManagers().onListenerRemoved(this, listener);
		return true;
	}

	@Override
	public boolean addAmiListener(AmiWebRealtimeObjectListener listener) {
		int i = AH.indexOf(listener, this.amiListeners);
		if (i != -1)
			return false;
		this.amiListeners = AH.append(this.amiListeners, listener);
		this.service.getWebManagers().onListenerAdded(this, listener);
		return true;
	}
	@Override
	public boolean hasAmiListeners() {
		return this.amiListeners.length > 0;
	}

	protected final void fireAmiEntityAdded(AmiWebObject entity) {
		for (AmiWebRealtimeObjectListener i : this.amiListeners)
			try {
				i.onAmiEntityAdded(this, entity);
			} catch (Exception e) {
				LH.warning(log, "Error adding Ami Entity for " + OH.getSimpleClassName(i) + " for entity: " + SH.ddd(entity.toString(), 128), e);
			}
	}
	protected final void fireAmiEntityRemoved(AmiWebObject entity) {
		for (AmiWebRealtimeObjectListener i : this.amiListeners)
			try {
				i.onAmiEntityRemoved(this, entity);
			} catch (Exception e) {
				LH.warning(log, "Error removing Ami Entity for " + OH.getSimpleClassName(i) + " for entity: " + SH.ddd(entity.toString(), 128), e);
			}
	}
	protected final void fireOnAmiEntitiesCleared() {
		for (AmiWebRealtimeObjectListener i : this.amiListeners)
			try {
				i.onAmiEntitiesReset(this);
			} catch (Exception e) {
				LH.warning(log, "Error clearing Ami Entities for " + OH.getSimpleClassName(i), e);
			}
	}
	protected final void fireAmiEntityUpdated(AmiWebObjectFields fields, AmiWebObject entity) {
		for (AmiWebRealtimeObjectListener i : this.amiListeners)
			try {
				i.onAmiEntityUpdated(this, fields, entity);
			} catch (Exception e) {
				LH.warning(log, "Error updating Ami Entity for " + OH.getSimpleClassName(i) + " for entity: " + SH.ddd(entity.toString(), 128), e);
			}
	}

	//	@Override
	//	public List<AmiWebRealtimeObjectListener> getListeners() {
	//		return this.listeners;
	//	}

	@Override
	public String getAri() {
		return ari;
	}

	@Override
	public void updateAri() {
		String oldAri = this.ari;
		this.ari = AmiWebDomObject.ARI_TYPE_PROCESSOR + ":" + AmiWebUtils.getFullAlias(this.getAlias(), this.getName());
		if (OH.ne(this.ari, oldAri)) {
			for (AmiWebDomObject i : this.getChildDomObjects())
				i.updateAri();
			this.service.getDomObjectsManager().fireAriChanged(this, oldAri);
			if (oldAri != null) {
				this.getService().getWebManagers().onProcessorRenamed(oldAri, this.ari);
				for (AmiWebRealtimeObjectListener i : this.amiListeners) {
					try {
						i.onLowerAriChanged(this, oldAri, this.ari);
					} catch (Exception e) {
						LH.warning(log, "Error updating Ari Entity for ", OH.getSimpleClassName(i), " for : ", oldAri + " ==> ", this.ari);
					}
				}
			}
		}

	}

	@Override
	public String getAriType() {
		return AmiWebDomObject.ARI_TYPE_PROCESSOR;
	}

	@Override
	public String getDomLabel() {
		return getName();
	}

	public String getName() {
		return name;
	}

	public String getAdn() {
		return this.adn;
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public AmiWebDomObject getParentDomObject() {
		return this.adn == null ? null : this.service.getLayoutFilesManager().getLayoutByFullAlias(AmiWebUtils.getAliasFromAdn(this.adn));
	}

	@Override
	public Class<?> getDomClassType() {
		return AmiWebRealtimeProcessor.class;
	}

	@Override
	public Object getDomValue() {
		return this;
	}

	@Override
	public boolean isTransient() {
		return isTransient;
	}

	@Override
	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return null;
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
	public abstract IterableAndSize<AmiWebObject> getAmiObjects();
	@Override
	public abstract com.f1.base.CalcTypes getRealtimeObjectschema();
	@Override
	public abstract com.f1.base.CalcTypes getRealtimeObjectsOutputSchema();
	@Override
	public abstract void onAmiEntitiesReset(AmiWebRealtimeObjectManager manager);
	@Override
	public abstract void onAmiEntityAdded(AmiWebRealtimeObjectManager manager, AmiWebObject entity);
	@Override
	public abstract void onAmiEntityUpdated(AmiWebRealtimeObjectManager manager, AmiWebObjectFields fields, AmiWebObject entity);
	@Override
	public abstract void onAmiEntityRemoved(AmiWebRealtimeObjectManager manager, AmiWebObject entity);

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = new HashMap<String, Object>();
		return r;
	}

	@Override
	public void init(String alias, Map<String, Object> configuration) {
		//		String name = CH.getOrThrow(String.class, configuration, "name");
		//		setAdn(AmiWebUtils.getFullAlias(alias, name));
	}

	public void setAdn(String adn) {
		//		this.adn = adn;
		this.name = AmiWebUtils.getNameFromAdn(adn);
		this.alias = AmiWebUtils.getAliasFromAdn(adn);
		updateAri();
	}

	@Override
	public String getAlias() {
		return this.alias;
	}

	@Override
	public String getRealtimeId() {
		return getAri();
	}

	private boolean isManagedByDomManager = false;
	private ReusableCalcFrameStack stackFrame;

	@Override
	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			for (AmiWebDomObject i : this.getChildDomObjects())
				i.addToDomManager();
			this.isManagedByDomManager = true;
		}
	}
	@Override
	public void removeFromDomManager() {
		service.getDomObjectsManager().fireRemoved(this);
		if (this.isManagedByDomManager == true) {
			service.getDomObjectsManager().removeManagedDomObject(this);
			for (AmiWebDomObject i : this.getChildDomObjects())
				i.removeFromDomManager();
			this.isManagedByDomManager = false;
		}
	}
	@Override
	public void close() {
		if (this.getLowerRealtimeIds() == null && this.getType() == "BPIPE") {
			return;
		}
		for (String s : getLowerRealtimeIds()) {
			AmiWebRealtimeObjectManager mdo = this.service.getWebManagers().getAmiObjectsByType(s);
			mdo.removeAmiListener(this);
		}
	}

	@Override
	public AmiWebService getService() {
		return this.service;
	}

	@Override
	public Set<String> getUpperRealtimeIds() {
		return AmiWebUtils.updateRealtimeIds(this.amiListeners, this.upperRealtimeIds);
	}

	@Override
	public String getAmiLayoutFullAlias() {
		return alias;
	}

	@Override
	public String getAmiLayoutFullAliasDotId() {
		return adn;
	}

	@Override
	public AmiWebFormulas getFormulas() {
		return this.formulas;
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return EmptyCalcTypes.INSTANCE;
	}

	protected AmiWebFormulasImpl getFormulasImpl() {
		return this.formulas;
	}

	protected void onSchemaChanged(byte status, Map<String, Tuple2<Class, Class>> columns) {
		AmiWebUtils.recompileAmiscript(this);
		rebuild();
		for (final AmiWebRealtimeObjectListener i : this.amiListeners)
			try {
				i.onSchemaChanged(this, status, columns);
			} catch (Exception e) {
				LH.warning(log, "Error updating schema for " + OH.getSimpleClassName(i));
			}
	}

	@Override
	public void onSchemaChanged(AmiWebRealtimeObjectManager manager, byte status, Map<String, Tuple2<Class, Class>> columns) {
		if (status != SCHEMA_DROPPED)
			onSchemaChanged(status, columns);
	}

	public ReusableCalcFrameStack getStackFrame() {
		return this.stackFrame;
	}
	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}
