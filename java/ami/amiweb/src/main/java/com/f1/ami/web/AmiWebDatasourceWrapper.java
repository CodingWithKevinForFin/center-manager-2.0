package com.f1.ami.web;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.web.dm.AmiWebDmManager;

public class AmiWebDatasourceWrapper {

	final private AmiWebObject object;
	final private long id;
	private String name;
	private String adapter;
	private String options;
	private String url;
	private String user;
	private String relayId;
	private String permittedOverrides;

	public AmiWebDatasourceWrapper(AmiWebDmManager manager, AmiWebObject amiWebObject) {
		this.object = amiWebObject;
		this.id = object.getId();
		this.name = (String) object.getParam(AmiConsts.PARAM_DATASOURCE_NAME);
		this.adapter = (String) object.getParam(AmiConsts.PARAM_DATASOURCE_ADAPTER);
		this.options = (String) object.getParam(AmiConsts.PARAM_DATASOURCE_OPTIONS);
		this.url = (String) object.getParam(AmiConsts.PARAM_DATASOURCE_URL);
		this.user = (String) object.getParam(AmiConsts.PARAM_DATASOURCE_USER);
		this.relayId = (String) object.getParam(AmiConsts.PARAM_DATASOURCE_RELAY_ID);
		this.permittedOverrides = (String) object.getParam(AmiConsts.PARAM_DATASOURCE_PERMITTED_OVERRIDES);
	}

	public AmiWebDatasourceWrapper(AmiWebDmManager manager, String name, String adapter, long id) {
		this.name = name;
		this.adapter = adapter;
		this.id = id;
		this.object = null;
	}

	public AmiWebObject getObject() {
		return object;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getAdapter() {
		return adapter;
	}

	public String getOptions() {
		return options;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public long getId() {
		return id;
	}

	public void setAdapter(String adapter) {
		this.adapter = adapter;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getRelayId() {
		return this.relayId;
	}
	public void setRelayId(String relayId) {
		this.relayId = relayId;
	}

	public String getPermittedOverrides() {
		return this.permittedOverrides;
	}
	public void setPermittedOverrides(String po) {
		this.permittedOverrides = po;
	}

}
