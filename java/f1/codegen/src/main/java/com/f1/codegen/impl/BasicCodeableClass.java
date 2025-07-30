/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Message;
import com.f1.codegen.CodeableClass;
import com.f1.codegen.CodeableParam;
import com.f1.utils.CH;
import com.f1.utils.HashOptimizer;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.VidParser;
import com.f1.utils.agg.BooleanAggregator;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.MultiMap;

public class BasicCodeableClass implements CodeableClass {

	private static final String PIN_PREFIX = "PIN_";
	private static final String PID_PREFIX = "PID_";
	final private Class<?> innerClass;
	final private List<CodeableParam> params;
	final private List<CodeableParam> supportedParams;
	final private List<CodeableParam> unsupportedParams;
	final private Map<String, CodeableParam> paramsByName;
	final private List<CodeableParam> paramsByPid;
	final private Map<String, String> annotations;
	final private boolean pidsSupported;
	final private MultiMap<String, CodeableParam, List<CodeableParam>> paramsByType = new BasicMultiMap.List<String, CodeableParam>();
	final private HashOptimizer<String, CodeableParam> buckets;
	private Class origClass;
	private long vid;
	private String vin;
	private Class<?> cloneReturnType = Message.class;

	public BasicCodeableClass(Class innerClass, Class origClass) throws IllegalArgumentException, IllegalAccessException {
		this.origClass = origClass;
		this.innerClass = innerClass;
		this.params = new ArrayList<CodeableParam>();
		this.paramsByName = new HashMap<String, CodeableParam>();
		vid = VidParser.getVid(innerClass);
		vin = VidParser.getVin(innerClass);
		Map<String, Method> gets = new HashMap<String, Method>();
		Map<String, Method> sets = new HashMap<String, Method>();
		boolean isInterface = Modifier.isInterface(origClass.getModifiers());
		for (Method m : origClass.getMethods()) {
			String name = m.getName();
			if (isInterface || Modifier.isAbstract(m.getModifiers())) {
				if (name.startsWith("get") && m.getParameterTypes().length == 0) {
					gets.put(name.substring(3), m);
					if (m != null && m.getReturnType() == void.class)
						throw new RuntimeException("for " + origClass.getName() + " getter can not return void: " + RH.toLegibleString(m, true, true));
				} else if (name.startsWith("set") && m.getParameterTypes().length == 1) {
					sets.put(name.substring(3), m);
				}
			}
			if (name.equals("clone") && m.getParameterTypes().length == 0) {
				if (cloneReturnType.isAssignableFrom(m.getReturnType())) {//handle covariant return types
					this.cloneReturnType = m.getReturnType();
				}
			}
		}
		for (String name : CH.sort(CH.comm(gets.keySet(), sets.keySet(), false, false, true))) {
			BasicCodeableParam param = (new BasicCodeableParam(gets.get(name), sets.get(name)));
			if (param.isValid())
				params.add(param);
		}
		StringBuilder errors = new StringBuilder();
		for (String name : CH.sort(CH.comm(gets.keySet(), sets.keySet(), true, true, false))) {
			Method getter = gets.get(name);
			Method setter = sets.get(name);
			BasicCodeableParam param = new BasicCodeableParam(getter, setter);
			if (param.isAbstract()) {
				if (getter == null)
					throw new RuntimeException("for " + origClass.getName() + " the declared setter method does not have a corresponding getter: "
							+ RH.toLegibleString(setter, true, true));
			} else {
				if (!param.isValid()) {
					throw new RuntimeException("for " + origClass.getName() + " the declared getter / setter pair is invalid: " + RH.toLegibleString(getter, true, true) + "  /  "
							+ RH.toLegibleString(setter, true, true));
				}
			}
		}
		annotations = BasicCodeableParam.getAnnotations(origClass);
		BooleanAggregator ba = new BooleanAggregator();
		for (CodeableParam p : params) {
			ba.add(p.getAnnotations().containsKey("PID_value"));
		}
		if (ba.hasFalse() && ba.hasTrue())

			throw new RuntimeException("Class must be consistent with providing PIDS: " + origClass.getName());
		boolean pidsSupported = ba.hasTrue();
		if (pidsSupported) {
			Set<String> t = new HashSet<String>();
			String pid;
			for (CodeableParam p : params)
				if (!t.add(pid = p.getAnnotations().get("PID_value")))
					throw new RuntimeException("duplicate pid in " + origClass.getName() + ": " + pid);
			this.pidsSupported = true;
		} else {
			this.pidsSupported = false;
		}
		supportedParams = new ArrayList<CodeableParam>(params.size());
		unsupportedParams = new ArrayList<CodeableParam>(2);
		for (CodeableParam i : params) {
			if (i.getSupported()) {
				supportedParams.add(i);
			} else
				unsupportedParams.add(i);
		}
		for (CodeableParam p : supportedParams)
			if (p.getType().isPrimitive() || OH.isBoxed(p.getType()))
				paramsByType.putMulti(OH.getBoxed(p.getType()).getSimpleName(), p);
		Collections.sort(params, new Comparator<CodeableParam>() {
			@Override
			public int compare(CodeableParam o1, CodeableParam o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		paramsByPid = new ArrayList<CodeableParam>(supportedParams);
		if (pidsSupported)
			Collections.sort(paramsByPid, new Comparator<CodeableParam>() {
				@Override
				public int compare(CodeableParam o1, CodeableParam o2) {
					int l = Integer.parseInt(o1.getAnnotations().get("PID_value"));
					int r = Integer.parseInt(o2.getAnnotations().get("PID_value"));
					return l - r;
				}
			});
		for (CodeableParam p : params)
			CH.putOrThrow(paramsByName, p.getName(), p);

		List<Integer> hashCodes = new ArrayList<Integer>(params.size());
		buckets = new HashOptimizer<String, CodeableParam>();
		int position = 0;
		for (CodeableParam p : supportedParams) {
			buckets.addElement(p.getName(), p);
			p.setPosition(position++);
		}
		for (Field field : innerClass.getFields()) {
			String fName = field.getName();
			if (fName.startsWith(PIN_PREFIX)) {
				String pin = SH.stripPrefix(fName, PIN_PREFIX, true);
				String chName = SH.toCamelHumps("_", pin, false);
				String fieldName = String.valueOf(field.get(null));
				CodeableParam p = paramsByName.get(chName);
				if (p == null)
					throw new RuntimeException("Inconsistent naming for PIN: " + innerClass.getName() + "::" + fName + " (corresponding getter / setter for '" + chName
							+ "' not found)");
				if (!fieldName.toUpperCase().equals(pin.toUpperCase()))
					throw new RuntimeException("Field Declaration must match pattern: PIN_SOMEVALUE=\"someValue\": " + innerClass.getName() + "::" + fName + "=\"" + fieldName
							+ "\"");
			} else if (fName.startsWith(PID_PREFIX)) {
				String pid = SH.stripPrefix(fName, PID_PREFIX, true);
				String chName = SH.toCamelHumps("_", pid, false);
				CodeableParam p = paramsByName.get(chName);
				if (p == null)
					throw new RuntimeException("Inconsistent naming for PID: " + innerClass.getName() + "::" + fName + " (corresponding getter / setter for '" + chName
							+ "' not found)");
			}
		}
	}
	@Override
	public Class getInnerClass() {
		return innerClass;
	}

	@Override
	public List<CodeableParam> getParams() {
		return supportedParams;
	}

	@Override
	public List<CodeableParam> getUnsupportedParams() {
		return unsupportedParams;
	}

	@Override
	public String getExtendsClause() {
		return innerClass.isInterface() ? "implements" : "extends";
	}

	@Override
	public String getPackageName() {
		return innerClass.getPackage().getName();
	}

	@Override
	public String getImplementsClause() {
		return innerClass.isInterface() ? ", " : "implements";
	}

	@Override
	public String getSimpleClassName() {
		return SH.afterLast(getClassName(), '.');
	}

	@Override
	public String getClassName() {
		return SH.replaceAll(innerClass.getName(), '$', '_') + "0";
	}

	@Override
	public boolean getSupportsPids() {
		return pidsSupported;
	}

	@Override
	public MultiMap<String, CodeableParam, List<CodeableParam>> getParamsByType() {
		return paramsByType;
	}

	@Override
	public Map<String, String> getClassAnnotations() {
		return annotations;
	}

	@Override
	public List<CodeableParam> getParamsByPid() {
		return paramsByPid;
	}

	@Override
	public HashOptimizer<String, CodeableParam> getBuckets() {
		return buckets;
	}

	@Override
	public Class getOrigClass() {
		return origClass;
	}

	@Override
	public long getVid() {
		return vid;
	}

	@Override
	public String getVin() {
		return vin;
	}

	public String getCloneReturnType() {
		String r = cloneReturnType.getName();
		return SH.replaceAll(r, '$', '.');
	}

}
