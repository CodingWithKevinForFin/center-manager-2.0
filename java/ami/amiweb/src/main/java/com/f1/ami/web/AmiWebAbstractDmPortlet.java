package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmError;
import com.f1.ami.web.dm.AmiWebDmListener;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.structs.BasicMultiMap;

public abstract class AmiWebAbstractDmPortlet extends AmiWebAbstractPortlet implements AmiWebDmPortlet, AmiWebDmListener, ConfirmDialogListener {

	private boolean clearOnDataStale = true;

	public AmiWebAbstractDmPortlet(PortletConfig config) {
		super(config);
	}

	final public void addUsedDm(String aliasDotName, String tableName) {
		AmiWebDm dm = getService().getDmManager().getDmByAliasDotName(aliasDotName);

		if (dm != null) {
			if (!dmAliasDotNameToTables.containsKey(aliasDotName))
				dm.addDmListener(this);
			if (dm.isCurrentlyRunning())
				onDmRunningQuery(dm, false);
		}
		this.dmAliasDotNameToTables.putMulti(aliasDotName, tableName);
		this.getService().getDmManager().onPanelDmDependencyChanged(this, aliasDotName, tableName, true);
	}
	final public void removeUsedDm(String aliasDotName, String tableName) {
		AmiWebDm dm = getService().getDmManager().getDmByAliasDotName(aliasDotName);
		this.dmAliasDotNameToTables.removeMultiAndKeyIfEmpty(aliasDotName, tableName);
		if (dm != null) {
			if (!dmAliasDotNameToTables.containsKey(aliasDotName))
				dm.removeDmListener(this);
		}
		this.getService().getDmManager().onPanelDmDependencyChanged(this, aliasDotName, tableName, false);
	}

	private BasicMultiMap.Set<String, String> dmAliasDotNameToTables = new BasicMultiMap.Set<String, String>();

	protected boolean isDatamodelHaveDependencies(String dmAliasDotName, String tbName) {
		return true;
	}

	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		String alias = this.getAmiLayoutFullAlias();
		List<Map<String, Object>> dms = new ArrayList<Map<String, Object>>();
		for (String name : getUsedDmAliasDotNames()) {
			dms.add((Map) CH.m("dmadn", AmiWebUtils.getRelativeAlias(alias, name), "dmtbid", CH.l(getUsedDmTables(name))));
		}
		r.put("dm", dms);
		if (this.clearOnDataStale == false) //Only put in configuration if false
			r.put("cods", this.clearOnDataStale);
		return r;
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {

		String alias = this.getAmiLayoutFullAlias();
		List<Map<String, Object>> dms = (List<Map<String, Object>>) configuration.get("dm");
		if (dms != null) {
			for (Map<String, Object> i : dms) {
				String id = AmiWebUtils.getFullAlias(alias, (String) i.get("dmadn"));
				List<String> tables = (List<String>) i.get("dmtbid");
				for (String s : tables) {
					addUsedDm(id, s);
				}
			}
		}
		this.clearOnDataStale = CH.getOrNoThrow(Caster_Boolean.INSTANCE, configuration, "cods", true); // If null or not in configuration true
		super.init(configuration, origToNewIdMapping, sb);
	}

	@Override
	public void onDmDataChanged(AmiWebDm datamodel) {
		showWaitingSplash(false);
	}
	@Override
	public void onDmRunningQuery(AmiWebDm datamodel, boolean isRequery) {
		if (!isRequery) {
			showWaitingSplash(true);
			if (this.clearOnDataStale)
				clearAmiData();
		}
	}

	@Override
	public void onDmError(AmiWebDm datamodel, AmiWebDmError error) {
		clearAmiData();
		showWaitingSplash(false);
	};

	@Override
	public void onDmDataBeforeFilterChanged(AmiWebDm datamodel) {
	}
	@Override
	public Set<String> getUsedDmAliasDotNames() {
		return this.dmAliasDotNameToTables.keySet();
	}

	@Override
	public Set<String> getUsedDmTables(String aliasDotName) {
		Set<String> r = this.dmAliasDotNameToTables.get(aliasDotName);
		return r != null ? r : Collections.EMPTY_SET;
	}

	public boolean isClearOnDataStale() {
		return clearOnDataStale;
	}

	public void setClearOnDataStale(boolean clearOnDataStale) {
		this.clearOnDataStale = clearOnDataStale;
	}
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		Set<String> existing = this.dmAliasDotNameToTables.remove(oldAliasDotName);
		if (existing != null)
			this.dmAliasDotNameToTables.put(dm.getAmiLayoutFullAliasDotId(), existing);
	};
}
