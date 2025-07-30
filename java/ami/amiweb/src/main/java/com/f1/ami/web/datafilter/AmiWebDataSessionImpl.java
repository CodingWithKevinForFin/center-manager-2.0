package com.f1.ami.web.datafilter;

import java.util.HashSet;
import java.util.Set;

import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebVarsManager;
import com.f1.http.HttpRequestResponse;
import com.f1.utils.CH;
import com.f1.utils.SH;

public class AmiWebDataSessionImpl implements AmiWebDataSession {

	private AmiWebService inner;
	private AmiWebVarsManager varManager;

	public AmiWebDataSessionImpl(AmiWebService inner) {
		this.inner = inner;
		this.varManager = inner.getVarsManager();
	}

	@Override
	public String getUsername() {
		return this.inner.getUserName();
	}

	@Override
	public Set<String> getVariableNames() {
		return CH.s(new HashSet<String>(), this.varManager.getGlobalVarNames());
	}

	@Override
	public Object getVariableValue(String name) {
		return this.varManager.getGlobalVarValue(name);
	}
	@Override
	public Class<?> getVariableType(String name) {
		return this.varManager.getGlobalVarType(name);
	}

	@Override
	public <T> void putVariable(String key, T value, Class<T> type) {
		if (this.varManager.getGlobalVarType(key) != null)
			throw new RuntimeException("var already exists: " + key);
		type.cast(value);
		this.varManager.addGlobalVar(key, value, type, AmiWebVarsManager.SOURCE_PLUGIN);
	}

	@Override
	public void logout() {
		this.inner.getPortletManager().close();

	}

	public String getBrowserIP() {
		HttpRequestResponse r = this.inner.getPortletManager().getCurrentRequestAction();
		String ipAddr = null;
		if (r != null) {
			ipAddr = r.getHeader().get("X-Forwarded-For");
			if (SH.isEmpty(ipAddr))
				ipAddr = r.getRemoteHost();
		}
		return ipAddr;
	}

}
