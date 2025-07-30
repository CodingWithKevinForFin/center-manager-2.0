package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.amiscript.AmiWebScriptRunner;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmFilter;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmManagerImpl;
import com.f1.ami.web.dm.AmiWebDmManagerListener;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.base.CalcFrame;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.SingletonCalcFrame;

public class AmiWebAmiScriptCallbacks implements AmiWebDmManagerListener {
	private static final Logger log = LH.get();
	private AmiWebService service;
	private AmiWebDomObject thiz;

	public AmiWebAmiScriptCallbacks(AmiWebService service, AmiWebDomObject thiz) {
		this.thiz = thiz;
		this.service = service;
		this.service.getDmManager().addDmManagerListener(this);
	}

	private Map<String, AmiWebAmiScriptCallback> callbacks = new HashMap<String, AmiWebAmiScriptCallback>();
	private String alias;

	public void registerCallbackDefinition(ParamsDefinition def) {
		registerCallbackDefinition(def, Collections.EMPTY_MAP, null);
	}
	public AmiWebAmiScriptCallback registerCallbackDefinition(ParamsDefinition def, Map<String, String> dflts, AmiWebDmsImpl dm) {
		AmiWebAmiScriptCallback callback = new AmiWebAmiScriptCallback(this, this.alias, service, dm, def, dflts, def.getMethodName());
		this.service.getDomObjectsManager().addCallback(callback);
		this.callbacks.put(def.getMethodName(), callback);
		callback.setAmiLayoutAlias(this.alias);
		return callback;
	}

	public ParamsDefinition getAmiScriptCallbackDefinition(String key) {
		return this.callbacks.get(key).getParamsDef();
	}

	public String getAmiScriptCallback(String key) {
		return this.callbacks.get(key).getAmiscript(true);
	}
	public AmiWebAmiScriptCallback getCallback(String key) {
		return this.callbacks.get(key);
	}

	public Set<String> getAmiScriptCallbackDefinitions() {
		return this.callbacks.keySet();
	}
	public Map<String, AmiWebAmiScriptCallback> getAmiScriptCallbackDefinitionsMap() {
		return this.callbacks;
	}

	public Map<String, Object> getConfiguration() {
		List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
		for (AmiWebAmiScriptCallback i : this.callbacks.values()) {
			Map<String, Object> t = i.getConfiguration();
			if (!t.isEmpty())
				values.add(t);
		}
		if (values.isEmpty())
			return Collections.EMPTY_MAP;
		else
			return CH.m("entries", values);
	}

	public void initCallbacksLinkedVariables() {
		for (AmiWebAmiScriptCallback cb : this.callbacks.values()) {
			cb.initLinkedVariables();
		}
	}
	private void initV3(AmiWebDmsImpl dms, String alias, Map<String, Object> m, StringBuilder sb) {
		List<Map<String, Object>> cb = (List) m.get("entries");
		if (cb != null) {
			for (Map<String, Object> i : cb) {
				String name = (String) i.get("name");
				AmiWebAmiScriptCallback c = this.callbacks.get(name);
				if (c != null) {
					c.init(dms, alias, i, sb);
				}

			}
		}
	}

	private void initV2(AmiWebDmsImpl dms, String alias, Map<String, Object> m, StringBuilder sb) {
		Map<String, Object> newMap = new HashMap<String, Object>();
		List<Map<String, Object>> cb = new ArrayList<Map<String, Object>>();

		for (Map.Entry<String, Object> callback : m.entrySet()) {
			Map<String, Object> entry = new HashMap<String, Object>();
			entry.put("name", callback.getKey());
			entry.put("amiscript", SH.s(callback.getValue()));
			cb.add(entry);
		}
		newMap.put("entries", cb);
		this.initV3(dms, alias, newMap, sb);

	}

	public void init(AmiWebDmsImpl dms, String alias, Map<String, Object> m, StringBuilder sb) {
		if (m == null)
			return;
		OH.assertNotNull(alias);
		this.setAmiLayoutAlias(alias);
		if (m.size() > 0) {
			Object cb = (List) m.get("entries");
			if (cb != null && cb instanceof List)
				this.initV3(dms, alias, m, sb);
			else
				this.initV2(dms, alias, m, sb);
		}
	}
	public void showEditor() {
		this.service.getAmiWebCallbackEditorsManager().showEditDmPortlet(this.service, this);
	}
	public Object execute(String string) {
		AmiWebAmiScriptCallback cb = this.callbacks.get(string);
		if (cb == null) {
			LH.warning(log, logMe(), "Callback not found: " + string);
			return null;
		}
		AmiWebDebugManagerImpl dm = service.getDebugManager();
		if (cb.hasCode()) {
			ParamsDefinition def = cb.getParamsDef();
			OH.assertEq(def.getParamsCount(), 0);
			return cb.execute(dm, EmptyCalcFrame.INSTANCE);
			//TODO:			return executeInner(string, this.callbacksValues.get(string), t);
		}
		return null;
	}
	public Object execute(String string, Object arg0) {
		AmiWebAmiScriptCallback cb = this.callbacks.get(string);
		if (cb == null) {
			LH.warning(log, logMe(), "Callback not found: " + string);
			return null;
		}
		AmiWebDebugManagerImpl dm = service.getDebugManager();
		if (cb.hasCode()) {
			ParamsDefinition def = cb.getParamsDef();
			OH.assertEq(def.getParamsCount(), 1);
			return cb.execute(dm, new SingletonCalcFrame(def.getParamName(0), def.getParamType(0), arg0));
		}
		return null;
	}

	private String logMe() {
		if (thiz == null)
			return service.getUserName() + ": Callbacks for null ";
		return service.getUserName() + ": Callbacks for '" + thiz.getAri() + "' ";
	}
	public Object execute(String string, Object arg0, Object arg1) {
		AmiWebAmiScriptCallback cb = this.callbacks.get(string);
		if (cb == null) {
			LH.warning(log, logMe(), "Callback not found: " + string);
			return null;
		}
		AmiWebDebugManagerImpl dm = service.getDebugManager();
		if (cb.hasCode()) {
			ParamsDefinition def = cb.getParamsDef();
			OH.assertEq(def.getParamsCount(), 2);
			CalcFrame values = new BasicCalcFrame(def.getParamTypesMapping());
			values.putValue(def.getParamName(0), arg0);
			values.putValue(def.getParamName(1), arg1);
			return cb.execute(dm, values);
		}
		return null;
	}
	public Object execute(String string, Object arg0, Object arg1, Object arg2) {
		AmiWebAmiScriptCallback cb = this.callbacks.get(string);
		if (cb == null) {
			LH.warning(log, logMe(), "Callback not found: " + string);
			return null;
		}
		AmiWebDebugManagerImpl dm = service.getDebugManager();
		if (cb.hasCode()) {
			ParamsDefinition def = cb.getParamsDef();
			OH.assertEq(def.getParamsCount(), 3);
			CalcFrame values = new BasicCalcFrame(def.getParamTypesMapping());
			values.putValue(def.getParamName(0), arg0);
			values.putValue(def.getParamName(1), arg1);
			values.putValue(def.getParamName(2), arg2);
			return cb.execute(dm, values);
		}
		return null;
	}

	public Object executeArgs(String string, Object[] args) {
		AmiWebAmiScriptCallback cb = this.callbacks.get(string);
		if (cb == null) {
			LH.warning(log, logMe(), "Callback not found: " + string);
			return null;
		}
		AmiWebDebugManagerImpl dm = service.getDebugManager();
		if (cb.hasCode()) {
			ParamsDefinition def = cb.getParamsDef();
			OH.assertEq(def.getParamsCount(), args.length);
			CalcFrame values = new BasicCalcFrame(def.getParamTypesMapping());
			for (int i = 0; i < args.length; i++)
				values.putValue(def.getParamName(i), args[i]);
			return cb.execute(dm, values);
		}
		return null;
	}
	public AmiWebScriptRunner executeRunner(String string, Object... args) {
		AmiWebAmiScriptCallback cb = this.callbacks.get(string);
		if (cb == null) {
			LH.warning(log, logMe(), "Callback not found: " + string);
			return null;
		}
		AmiWebDebugManagerImpl dm = service.getDebugManager();
		if (cb.hasCode()) {
			ParamsDefinition def = cb.getParamsDef();
			OH.assertEq(def.getParamsCount(), args.length);
			CalcFrame values = new BasicCalcFrame(def.getParamTypesMapping());
			for (int i = 0; i < args.length; i++)
				values.putValue(def.getParamName(i), args[i]);
			return cb.executeReturnRunner(dm, values);
		}
		return null;
	}
	public boolean isImplemented(String methodName) {
		final AmiWebAmiScriptCallback cb = this.callbacks.get(methodName);
		return cb != null && cb.hasCode();
	}

	public String getAmiLayoutAlias() {
		return this.alias;
	}
	public void setAmiLayoutAlias(String amiLayoutAlias) {
		OH.assertNotNull(amiLayoutAlias);
		this.alias = amiLayoutAlias;
		for (AmiWebAmiScriptCallback callback : this.callbacks.values()) {
			callback.setAmiLayoutAlias(amiLayoutAlias);
		}
	}
	public AmiWebDomObject getThis() {
		return this.thiz;
	}
	public void setThis(AmiWebDomObject thiz) {
		this.thiz = thiz;
	}

	public boolean setAmiScriptCallbackNoCompile(String key, String script) {
		AmiWebAmiScriptCallback t = CH.getOrThrow(this.callbacks, key);
		if (SH.isnt(script)) {
			t.setAmiscript(null, false);
		} else {
			t.setAmiscript(script, false);
		}
		return true;
	}
	@Override
	public void onDmUpdated(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
	}
	@Override
	public void onDmAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
	}
	@Override
	public void onDmRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
		for (AmiWebAmiScriptCallback i : this.callbacks.values())
			i.onDmRemoved(dm.getAmiLayoutFullAliasDotId());
	}
	@Override
	public void onDmNameChanged(AmiWebDmManager amiWebDmManagerImpl, String oldAliasDotName, AmiWebDm dm) {
		for (AmiWebAmiScriptCallback i : this.callbacks.values())
			i.onDmNameChanged(oldAliasDotName, dm.getAmiLayoutFullAliasDotId());
	}
	@Override
	public void onDmDependencyAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm upper, AmiWebDm lower) {
	}
	@Override
	public void onDmDependencyRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm upper, AmiWebDm lower) {
	}
	@Override
	public void onDmLinkAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDmLink link) {
	}
	@Override
	public void onDmLinkRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDmLink link) {
	}
	@Override
	public void onDmManagerInitDone() {
	}
	@Override
	public void onDmDependencyAdded(AmiWebDmManager manager, AmiWebDmPortlet target, String dmName, String tableName) {
	}
	@Override
	public void onDmDependencyRemoved(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmPortlet target, String dmName, String tableName) {
	}
	@Override
	public void onFilterDependencyAdded(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmFilter target, String dmName, String tableName) {
	}
	@Override
	public void onFilterDependencyRemoved(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmFilter target, String dmName, String tableName) {
	};

	private boolean isClosed = false;

	public void close() {
		if (isClosed)
			return;
		isClosed = true;
		this.service.getDmManager().removeDmManagerListener(this);
		for (AmiWebAmiScriptCallback i : this.callbacks.values()) {
			i.close();
		}
	}

	//	public void open() {
	//		if (this.isClosed == false)
	//			return;
	//		this.isClosed = false;
	//		this.service.getDmManager().addDmManagerListener(this);
	//		for (AmiWebAmiScriptCallback i : this.callbacks.values()) {
	//			i.open();
	//		}
	//	}

	public void recompileAmiscript() {
		for (AmiWebAmiScriptCallback i : this.callbacks.values())
			i.recompileAmiscript();

	}
	public void copyFrom(AmiWebAmiScriptCallbacks other) {

		for (Entry<String, AmiWebAmiScriptCallback> i : this.callbacks.entrySet()) {
			AmiWebAmiScriptCallback c = other.getCallback(i.getKey());
			if (c != null)
				i.getValue().copyFrom(c);
		}

	}
	public AmiWebService getService() {
		return this.service;
	}
	public void onVarConstChanged(String var) {
		for (AmiWebAmiScriptCallback i : this.callbacks.values())
			i.onVarConstChanged(var);
	}

}
