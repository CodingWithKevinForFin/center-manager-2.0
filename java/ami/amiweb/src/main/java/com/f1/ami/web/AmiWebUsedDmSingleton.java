package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.AmiWebDmTablesetSchema;
import com.f1.base.Mapping;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.ToDoException;

public class AmiWebUsedDmSingleton {

	private String dmAliasDotName;
	private String dmTableName;
	private Set<String> dmAliasDotNames = Collections.EMPTY_SET;
	private Set<String> dmTableNames = Collections.EMPTY_SET;
	final private AmiWebDmManager dmManager;
	final private AmiWebDmPortlet peer;
	private static final Logger log = LH.get();

	public AmiWebUsedDmSingleton(AmiWebDmManager dmManager, AmiWebDmPortlet peer) {
		this.dmManager = dmManager;
		this.peer = peer;
	}
	public void addUsedDatamodel(String dmAliasDotName, String dmTableName) {
		setUsedDm(dmAliasDotName, dmTableName);
	}
	public void removeUsedDatamodel(String dmName, String value) {
		throw new ToDoException();
	}
	public Set<String> getUsedDmTables(String dmAliasDotName) {
		if (OH.eq(this.dmAliasDotName, dmAliasDotName))
			return this.dmTableNames;
		else
			return Collections.EMPTY_SET;

	}

	public String getDmTableName() {
		return this.dmTableName;
	}
	public void setUsedDm(String aliasDotName, String tableName) {
		boolean dmNameChanged = OH.ne(this.dmAliasDotName, aliasDotName);
		boolean dmTableNameChanged = OH.ne(this.dmTableName, tableName);
		if (!dmNameChanged && !dmTableNameChanged)
			return;
		String oldAliasDotName = this.dmAliasDotName;
		String oldTableName = this.dmTableName;
		if (dmNameChanged) {
			AmiWebDm dm = this.dmManager.getDmByAliasDotName(this.dmAliasDotName);
			if (dm != null)
				dm.removeDmListener(peer);
			this.dmAliasDotName = aliasDotName;
			if (this.dmAliasDotName != null) {
				dm = this.dmManager.getDmByAliasDotName(this.dmAliasDotName);
				this.dmAliasDotNames = CH.s(dmAliasDotName);
				if (dm != null)
					dm.addDmListener(peer);
			} else
				this.dmAliasDotNames = Collections.EMPTY_SET;
		}
		if (dmTableNameChanged) {
			this.dmTableName = tableName;
			dmTableNames = this.dmTableName == null ? Collections.EMPTY_SET : Collections.singleton(this.dmTableName);
		}
		this.dmManager.onPanelDmDependencyChanged(peer, oldAliasDotName, oldTableName, false);
		this.dmManager.onPanelDmDependencyChanged(peer, this.dmAliasDotName, this.dmTableName, true);
	}
	public void getConfiguration(String alias, Map<String, Object> r) {
		List<Map<String, Object>> dms = new ArrayList<Map<String, Object>>();
		for (String name : getUsedDmAliasDotNames())
			dms.add((Map) CH.m("dmadn", AmiWebUtils.getRelativeAlias(alias, name), "dmtbid", CH.l(getUsedDmTables(name))));
		AmiWebUtils.putSkipEmpty(r, "dm", dms);

	}
	public void init(String alias, Map<String, Object> configuration) {
		List<Map<String, Object>> dms = (List<Map<String, Object>>) configuration.get("dm");
		if (dms != null) {
			for (Map<String, Object> i : dms) {
				String id = AmiWebUtils.getFullAlias(alias, (String) i.get("dmadn"));
				List<String> ss = (List<String>) i.get("dmtbid");
				for (String s : ss) {
					try {
						setUsedDm(id, s);
					} catch (Exception e) {
						LH.warning(log, "User ", this.dmManager.getService().getPortletManager().describeUser(), " layou error for dm: ", s, e);
					}
				}
			}
		}
	}
	public boolean matches(String aliasDotName, String dmTable) {
		return OH.eq(dmAliasDotName, aliasDotName) && OH.eq(dmTable, getDmTableName());
	}
	public Table getTable() {
		AmiWebDm dm = this.getDm();
		return dm == null ? null : dm.getResponseTableset().getTableNoThrow(this.getDmTableName());
	}
	public com.f1.base.CalcTypes getTableSchema() {
		final AmiWebDm dm = this.getDm();
		if (dm != null) {
			final AmiWebDmTablesetSchema res = dm.getResponseOutSchema();
			if (res != null) {
				final AmiWebDmTableSchema table = res.getTable(this.getDmTableName());
				if (table != null)
					return table.getClassTypes();
			}
		}
		return null;
	}
	public AmiWebDm getDm() {
		return this.dmManager.getDmByAliasDotName(this.dmAliasDotName);
	}
	public boolean matches(AmiWebDm datamodel) {
		return datamodel != null && OH.eq(this.dmAliasDotName, datamodel.getAmiLayoutFullAliasDotId());
	}
	public void addUsedDatamodel(String alias, String dmName, String dmTable) {
	}
	public void removeUsedDatamodel(String alias, String dmName, String dmTable) {
	}

	public AmiWebDmTableSchema getDmTableSchema() {
		final AmiWebDm dm = getDm();
		return dm == null ? null : dm.getResponseOutSchema().getTable(this.dmTableName);
	}
	public Set<String> getUsedDmAliasDotNames() {
		return this.dmAliasDotNames;
	}
	public String getDmAliasDotName() {
		return this.dmAliasDotName;
	}
	public void addUsedDm(String aliasDotName, String dmTable) {
	}
	public void removeUsedDm(String aliasDotName, String dmTable) {
	}
	public static List<String> extractUsedDmAndTables(Map<String, Object> portletConfig) {
		ArrayList<String> r = new ArrayList<String>();
		List<Map<String, Object>> dms = (List<Map<String, Object>>) portletConfig.get("dm");
		if (CH.isntEmpty(dms))
			r.add((String) dms.get(0).get("dmadn"));
		return r;
	}
	public static void replaceUsedDmAndTable(Map<String, Object> portletConfig, int position, String name) {
		List<Map<String, Object>> dms = (List<Map<String, Object>>) portletConfig.get("dm");
		dms.get(position).put("dmadn", name);
	}
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		if (OH.eq(this.dmAliasDotName, oldAliasDotName)) {
			this.dmAliasDotName = dm.getAmiLayoutFullAliasDotId();
			this.dmAliasDotNames = CH.s(this.dmAliasDotName);
		}
	}
}
