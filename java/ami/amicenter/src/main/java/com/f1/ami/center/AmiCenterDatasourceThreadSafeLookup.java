package com.f1.ami.center;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.TableListener;
import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;

public class AmiCenterDatasourceThreadSafeLookup implements TableListener {

	private LongKeyMap<DatasourceRow> byId = new LongKeyMap<DatasourceRow>();
	private Map<String, DatasourceRow> byName = new HashMap<String, DatasourceRow>();
	private AmiCenterState state;

	public AmiCenterDatasourceThreadSafeLookup(AmiCenterState state) {
		this.state = state;
	}

	private void put(long amiId, String adapter, String name, String options, String password, String username, String relayId, String url, Set<String> permittedOverrides) {
		final DatasourceRow row = new DatasourceRow(amiId, adapter, name, options, password, username, relayId, url, permittedOverrides);
		synchronized (this) {
			final LongKeyMap<DatasourceRow> byId2 = new LongKeyMap<DatasourceRow>(byId);
			final HashMap<String, DatasourceRow> byName2 = new HashMap<String, DatasourceRow>(byName);
			DatasourceRow old = byId2.put(row.getAmiId(), row);
			if (old != null)
				byName2.remove(old.getName());
			byName2.put(row.getName(), row);
			this.byId = byId2;
			this.byName = byName2;
		}
	}

	private void remove(long amiId) {
		final DatasourceRow t = byId.get(amiId);
		if (t == null)
			return;
		synchronized (this) {
			final LongKeyMap<DatasourceRow> byId2 = new LongKeyMap<DatasourceRow>(byId);
			final HashMap<String, DatasourceRow> byName2 = new HashMap<String, DatasourceRow>(byName);
			byName2.remove(t.getName());
			byId2.remove(t.getAmiId());
			this.byId = byId2;
			this.byName = byName2;
		}
	}

	public DatasourceRow getByAmiId(long amiId) {
		return this.byId.get(amiId);
	}
	public DatasourceRow getByName(String name) {
		return this.byName.get(name);
	}

	public static class DatasourceRow {

		final private long amiId;
		final private String adapter;
		final private String name;
		final private String options;
		final private String password;
		final private String username;
		final private String relayId;
		final private String url;
		final private Set<String> permittedOverrides;

		public DatasourceRow(long amiId, String adapter, String name, String options, String password, String username, String relayId, String url,
				Set<String> permittedOverrides) {
			this.amiId = amiId;
			this.adapter = adapter;
			this.name = name;
			this.options = options;
			this.password = password;
			this.username = username;
			this.relayId = relayId;
			this.permittedOverrides = permittedOverrides;
			this.url = url;
		}

		public long getAmiId() {
			return amiId;
		}

		public String getAdapter() {
			return adapter;
		}

		public String getName() {
			return name;
		}

		public String getOptions() {
			return options;
		}

		public String getUsername() {
			return username;
		}

		public String getRelayId() {
			return relayId;
		}

		public String getPassword() {
			return password;
		}

		public String getUrl() {
			return url;
		}

		public Set<String> getPermittedOverrides() {
			return permittedOverrides;
		}

	}

	@Override
	public void onCell(Row row, int cell, Object oldValue, Object newValue) {
		this.onRowAdded(row);
	}

	@Override
	public void onColumnAdded(Column nuw) {
	}

	@Override
	public void onColumnRemoved(Column old) {
	}

	@Override
	public void onColumnChanged(Column old, Column nuw) {
	}

	@Override
	public void onRowAdded(Row add) {
		AmiRowImpl row = (AmiRowImpl) add;
		String name = row.getString(AmiConsts.PARAM_DATASOURCE_NAME);
		String adapter = row.getString(AmiConsts.PARAM_DATASOURCE_ADAPTER);
		String url = row.getString(AmiConsts.PARAM_DATASOURCE_URL);
		String username = row.getString(AmiConsts.PARAM_DATASOURCE_USER);
		String pw = row.getString(AmiConsts.PARAM_DATASOURCE_PW);
		final String passwordEnc = row.getString(AmiConsts.PARAM_DATASOURCE_PASSWORD), password;
		try {
			password = state.decrypt(passwordEnc);
		} catch (Exception e) {
			throw new RuntimeException("Could not decrypt 'Password' column for value: '" + passwordEnc + "'", e);
		}
		String options = row.getString(AmiConsts.PARAM_DATASOURCE_OPTIONS);
		String relayId = row.getString(AmiConsts.PARAM_DATASOURCE_RELAY_ID);
		String permittedOverrides = row.getString(AmiConsts.PARAM_DATASOURCE_PERMITTED_OVERRIDES);
		Set<String> permittedOverridesSet;
		if (SH.isnt(permittedOverrides))
			permittedOverridesSet = Collections.EMPTY_SET;
		else {
			permittedOverridesSet = new HashSet<String>();
			for (String s : SH.split(",", permittedOverrides))
				permittedOverridesSet.add(s.trim());
		}
		put(row.getAmiId(), adapter, name, options, password, username, relayId, url, permittedOverridesSet);
	}
	@Override
	public void onRowRemoved(Row removed, int location) {
		AmiRowImpl row = (AmiRowImpl) removed;
		this.remove(row.getAmiId());
	}
}
