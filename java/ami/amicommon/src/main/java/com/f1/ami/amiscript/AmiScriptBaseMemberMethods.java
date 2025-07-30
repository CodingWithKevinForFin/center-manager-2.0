package com.f1.ami.amiscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.ClassDebugInspector;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AmiScriptBaseMemberMethods<T> implements ClassDebugInspector<T> {

	private AmiDebugManager debugManager;
	private final List<AmiAbstractMemberMethod<T>> methods = new ArrayList<AmiAbstractMemberMethod<T>>();
	private final LinkedHashMap<String, AmiAbstractMemberMethod<T>> methodsForDebug = new LinkedHashMap<String, AmiAbstractMemberMethod<T>>();
	private Map<String, Class<?>> debugProperties = new HashMap<String, Class<?>>();
	protected static final Logger log = LH.get();

	public AmiScriptBaseMemberMethods() {
		//		this.debugManager = debugManager;
	}

	final protected AmiDebugManager getDebugManager() {
		return this.debugManager;
	}

	final public List<AmiAbstractMemberMethod<T>> getMethods() {
		return this.methods;
	}

	final protected void addMethod(AmiAbstractMemberMethod<T> method, String debugProperty) {
		OH.assertEq(method.getParamNames().length, 0);
		OH.assertEq(method.getVarArgType(), null);
		addCustomDebugProperty(debugProperty, method.getReturnType());
		CH.putOrThrow(this.methodsForDebug, debugProperty, method);
		CH.putOrThrow(debugProperties, debugProperty, method.getReturnType());
		addMethod(method);
	}
	final protected void addMethod(AmiAbstractMemberMethod<T> method) {
		this.methods.add(method);
		method.setMemberMethods(this);
	}

	final protected void addCustomDebugProperty(String debugProperty, Class<?> type) {
		CH.putOrThrow(debugProperties, debugProperty, type);
	}

	@Override
	public abstract String getVarTypeName();
	public abstract String getVarTypeDescription();
	public abstract Class<T> getVarType();
	public abstract Class<? extends T> getVarDefaultImpl();

	@Override
	final public Object getDebugProperty(String name, T value, CalcFrameStack sf) {
		AmiAbstractMemberMethod<T> method = this.methodsForDebug.get(name);
		if (method != null)
			return method.invokeMethod(sf, value, OH.EMPTY_OBJECT_ARRAY, null);
		return getCustomDebugProperty(name, value);
	}

	protected Object getCustomDebugProperty(String name, T value) {
		LH.warning(log, "missing registered property for " + value.getClass(), ": ", name);
		return null;
	}

	//	@Override
	//	public void getDebugProperty(T value, Map<String, Object> sink) {
	//		for (Entry<String, AmiAbstractMemberMethod<T>> e : this.methodsForDebug.entrySet())
	//			sink.put(e.getKey(), e.getValue().invokeMethod(null, value, null));
	//	}
	//

	@Override
	final public Map<String, Class<?>> getDebugProperties() {
		return this.debugProperties;
	}

	protected static AmiService getService(CalcFrameStack sf) {
		return AmiUtils.getExecuteInstance2(sf).getService();
	}

}
