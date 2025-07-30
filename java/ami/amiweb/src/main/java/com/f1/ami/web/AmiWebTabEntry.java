package com.f1.ami.web;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.impl.TabPlaceholderPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;

public class AmiWebTabEntry implements AmiWebDomObject, AmiWebFormulasListener {
	final public static Integer MIN_BLINK_PERIOD = 100; // in milliseconds
	public static final Comparator<AmiWebTabEntry> COMPARATOR_LOCATION = new Comparator<AmiWebTabEntry>() {

		@Override
		public int compare(AmiWebTabEntry o1, AmiWebTabEntry o2) {
			return OH.compare(o1.getLocation(false), o2.getLocation(false));
		}
	};
	public static final Comparator<AmiWebTabEntry> COMPARATOR_LOCATION_OVERRIDE = new Comparator<AmiWebTabEntry>() {

		@Override
		public int compare(AmiWebTabEntry o1, AmiWebTabEntry o2) {
			return OH.compare(o1.getLocation(true), o2.getLocation(true));
		}
	};
	private final Tab tab;
	final private AmiWebFormula nameFormula;
	final private AmiWebFormula selectColorFormula;
	final private AmiWebFormula unselectColorFormula;
	final private AmiWebFormula selectTextColorFormula;
	final private AmiWebFormula unselectTextColorFormula;
	final private AmiWebFormula blinkColorFormula;
	final private AmiWebFormula blinkPeriodFormula;
	private AmiWebOverrideValue<Boolean> hidden = new AmiWebOverrideValue<Boolean>(false);
	private AmiWebOverrideValue<Integer> location = new AmiWebOverrideValue<Integer>(0);
	private int userLocation;
	private String dmAliasDotName;
	private String dmTableName;
	private AmiWebTabPortlet owner;
	private String id;

	public AmiWebTabEntry(AmiWebTabPortlet owner, Tab tab) {
		this(owner, tab, null);
	}

	public AmiWebTabEntry(AmiWebTabPortlet owner, Tab tab, Map<String, Object> i) {
		this.owner = owner;
		this.tab = tab;
		this.formulas = new AmiWebFormulasImpl(this);
		this.formulas.addFormulasListener(this);
		this.nameFormula = this.formulas.addFormula("name", Object.class);
		this.selectColorFormula = this.formulas.addFormula("selectColor", Object.class);
		this.unselectColorFormula = this.formulas.addFormula("unselectColor", Object.class);
		this.selectTextColorFormula = this.formulas.addFormula("selectTextColor", Object.class);
		this.unselectTextColorFormula = this.formulas.addFormula("unselectTextColor", Object.class);
		this.blinkColorFormula = this.formulas.addFormula("blinkColor", Object.class);
		this.blinkPeriodFormula = this.formulas.addFormula("blinkPeriod", Number.class);
		this.location.setValue(tab.getLocation(), false);
		if (i != null)
			init(i);
		else
			this.id = owner.getNextId(this.isTransient() ? "%tab0" : "tab0");
	}

	public AmiWebTabPortlet getOwner() {
		return this.owner;
	}

	public AmiWebFormula getNameFormula() {
		return nameFormula;
	}

	public String getDmAliasDotName() {
		return dmAliasDotName;
	}
	public String getDmTableName() {
		return dmTableName;
	}

	public int getTabId() {
		return tab.getTabId();
	}
	public void setId(String tabId) {
		if (OH.eq(this.id, tabId))
			return;
		String oldId = this.id;
		this.id = tabId;
		this.owner.onIdChanged(this, oldId, tabId);
		this.updateAri();
		this.owner.updateTab(this);
	}
	public String getId() {
		return id;
	}

	public AmiWebFormula getSelectColorFormula() {
		return selectColorFormula;
	}
	public AmiWebFormula getUnselectColorFormula() {
		return unselectColorFormula;
	}
	public AmiWebFormula getSelectTextColorFormula() {
		return selectTextColorFormula;
	}
	public AmiWebFormula getUnselectTextColorFormula() {
		return unselectTextColorFormula;
	}
	public AmiWebFormula getBlinkColorFormula() {
		return blinkColorFormula;
	}
	public AmiWebFormula getBlinkPeriodFormula() {
		return blinkPeriodFormula;
	}
	public Map<String, Object> getConfiguration() {
		HashMap<String, Object> r = new HashMap<String, Object>();
		AmiWebUtils.putSkipEmpty(r, "nf", nameFormula.getFormulaConfig());
		AmiWebUtils.putSkipEmpty(r, "dmadn", AmiWebUtils.getRelativeAlias(owner.getAmiLayoutFullAlias(), dmAliasDotName));
		AmiWebUtils.putSkipEmpty(r, "sc", selectColorFormula.getFormulaConfig());
		AmiWebUtils.putSkipEmpty(r, "uc", unselectColorFormula.getFormulaConfig());
		AmiWebUtils.putSkipEmpty(r, "st", selectTextColorFormula.getFormulaConfig());
		AmiWebUtils.putSkipEmpty(r, "ut", unselectTextColorFormula.getFormulaConfig());
		AmiWebUtils.putSkipEmpty(r, "tb", dmTableName);
		AmiWebUtils.putSkipEmpty(r, "bc", blinkColorFormula.getFormulaConfig());
		AmiWebUtils.putSkipEmpty(r, "bp", blinkPeriodFormula.getFormulaConfig());
		AmiWebUtils.putSkipEmpty(r, "id", id);
		if (getHidden(false))
			r.put("hidden", true);

		return r;
	}
	private void init(Map<String, Object> configuration) {
		this.nameFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "nf", null));
		this.selectColorFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "sc", null));
		this.unselectColorFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "uc", null));
		this.selectTextColorFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "st", null));
		this.unselectTextColorFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "ut", null));
		this.blinkColorFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "bc", null));
		this.blinkPeriodFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "bp", null));
		this.setHidden(CH.getOr(Caster_Boolean.INSTANCE, configuration, "hidden", Boolean.FALSE), false);
		this.dmAliasDotName = AmiWebUtils.getFullAlias(owner.getAmiLayoutFullAlias(), CH.getOr(Caster_String.INSTANCE, configuration, "dmadn", null));
		this.dmTableName = CH.getOr(Caster_String.INSTANCE, configuration, "tb", null);
		String s = CH.getOr(Caster_String.INSTANCE, configuration, "id", null);
		if (s != null)
			this.id = s;
		else
			this.id = owner.getNextId("tab0");//backwards compatible

	}
	public void setDmId(String dmAliasDotName, String tableName) {
		if (OH.eq(this.dmAliasDotName, dmAliasDotName) && OH.eq(this.dmTableName, tableName))
			return;
		this.dmAliasDotName = dmAliasDotName;
		this.dmTableName = tableName;
		if (this.owner.isInitDone())
			this.owner.redoDmReferences();
	}

	public String getAliasDotName() {
		return this.dmAliasDotName;
	}

	public void onDmNameChanged(String aliasDotName) {
		this.dmAliasDotName = aliasDotName;
	}

	public Tab getTab() {
		return this.tab;
	}

	private String ari;
	private String amiLayoutFullAliasDotId;
	private String amiLayoutFullAlias;

	@Override
	public String getAri() {
		return this.ari;
	}
	public void updateAri() {
		String oldAri = this.ari;
		this.amiLayoutFullAlias = this.getOwner().getAmiLayoutFullAlias();
		this.amiLayoutFullAliasDotId = this.getOwner().getAmiLayoutFullAliasDotId() + "?" + getDomLabel();
		this.ari = AmiWebDomObject.ARI_TYPE_TAB_ENTRY + ":" + this.amiLayoutFullAliasDotId;
		this.getOwner().getService().getDomObjectsManager().fireAriChanged(this, oldAri);
		this.tab.setHtmlIdSelector(AmiWebUtils.toHtmlIdSelector(this));
	}

	@Override
	public String getAriType() {
		return AmiWebDomObject.ARI_TYPE_TAB_ENTRY;
	}

	@Override
	public String getDomLabel() {
		return getId();
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public AmiWebDomObject getParentDomObject() {
		return this.getOwner();
	}

	@Override
	public Class<?> getDomClassType() {
		return AmiWebTabEntry.class;
	}

	@Override
	public Object getDomValue() {
		return this;
	}

	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		// TODO Auto-generated method stub
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
	public boolean isTransient() {
		Portlet p = this.getTab().getPortlet();
		if (p instanceof AmiWebAliasPortlet && ((AmiWebAliasPortlet) p).isTransient())
			return true;
		return false;
	}
	@Override
	public void setTransient(boolean isTransient) {
		throw new UnsupportedOperationException("Invalid operation");
	}

	private boolean isManagedByDomManager = false;
	final private AmiWebFormulasImpl formulas;

	@Override
	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			AmiWebService service = this.owner.getService();
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			this.isManagedByDomManager = true;
		}
	}
	@Override
	public void removeFromDomManager() {
		this.owner.getService().getDomObjectsManager().fireRemoved(this);
		if (this.isManagedByDomManager == true) {
			AmiWebService service = this.owner.getService();
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
	}

	public boolean getHidden(boolean current) {
		return this.hidden.getValue(current);
	}
	public void setHidden(boolean hidden, boolean current) {
		if (this.hidden.setValue(hidden, current))
			this.getTab().setHidden(hidden);
	}
	public int getLocation(boolean current) {
		return this.location.getValue(current);
	}
	public void setLocation(int location, boolean current) {
		if (this.location.setValue(location, current))
			this.getTab().getTabPortlet().moveTab(this.tab, location);
	}

	public void updateLocationFromTab(boolean current) {
		this.location.setValue(this.getTab().getLocation(), current);
		this.hidden.setValue(!(this.getTab().getPortlet() instanceof TabPlaceholderPortlet) && this.getTab().isHidden(), current);
	}

	public void clearLocationHiddenOverrides() {
		this.location.clearOverride();
		this.hidden.clearOverride();
	}
	public boolean hasLocationHiddenOverrides() {
		return this.location.isOverride() || this.hidden.isOverride();
	}

	@Override
	public String getAmiLayoutFullAlias() {
		return this.amiLayoutFullAlias;
	}

	@Override
	public String getAmiLayoutFullAliasDotId() {
		return this.amiLayoutFullAliasDotId;
	}

	@Override
	public AmiWebFormulas getFormulas() {
		return formulas;
	}

	@Override
	public AmiWebService getService() {
		return this.owner.getService();
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return this.owner.getFormulaVarTypes(f);
	}

	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		this.owner.updateTab(this);
	}

	public void setIsDefault(boolean b) {
		this.tab.setIsDefault(b);
	}

	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}
