package com.f1.ami.relay;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.BasicCalcTypes;

public class AmiRelayRoute implements Comparable<AmiRelayRoute> {
	public static final byte ACTION_CONTINUE = 0;
	public static final byte ACTION_BREAK = 1;
	final private String routeName;
	final private int priority;
	final private Set<String> objectTypes;
	final private BasicCalcTypes paramTypes;
	final private String[] requiredParams;
	final private String expression;
	final private DerivedCellCalculator expressionCalc;
	final private String[] routeList;
	final private byte onTrue;
	final private byte onFalse;
	final private BitSet routListCenterids;
	final private String[] messageTypes;
	final private char[] messageTypeChars;//null means all, empty means none
	final private boolean isSkip;
	private boolean hasWildCardObjectType;
	private long matchCount = 0;

	public AmiRelayRoute(String text, AmiRelayState state) throws ClassNotFoundException {
		Map<String, AmiRelayCenterDefinition> centerIds = state.getCentersByName();
		String[] parts = SH.splitWithEscape(';', '\\', text);
		if (parts.length != 9)
			throw new RuntimeException("Expecting 9 parts (RouteName;Priority;messageTypes;ObjectTypes;ParamTypes;Expression;RouteList;OnTrue;OnFalse) not " + parts.length);
		for (int i = 0; i < parts.length; i++)
			parts[i] = SH.trim(parts[i]);
		this.routeName = parts[0];
		if (SH.isnt(routeName))
			throw new RuntimeException("RouteName required: " + parts[0]);
		try {
			this.priority = SH.parseInt(parts[1]);
		} catch (Exception e) {
			throw new RuntimeException("Priority is an invalid number: " + parts[1], e);
		}
		this.messageTypes = split("MessageTypes", parts[2]);
		if (AH.isEmpty(this.messageTypes))
			this.messageTypeChars = OH.EMPTY_CHAR_ARRAY;
		else if (OH.eq(parts[2], "*"))
			this.messageTypeChars = null;
		else {
			this.messageTypeChars = new char[this.messageTypes.length];
			for (int n = 0; n < this.messageTypes.length; n++) {
				String t = this.messageTypes[n];
				if (t.length() != 1 || "ODCS".indexOf(t.charAt(0)) == -1)
					throw new RuntimeException("MessageTypes is invalid: " + t + " (must be either O,D,C or S)");
				this.messageTypeChars[n] = t.charAt(0);
			}
		}
		String[] objectTypes = split("ObjectTypes", parts[3]);
		this.objectTypes = CH.s(objectTypes);
		this.hasWildCardObjectType = this.objectTypes.remove("*");

		this.paramTypes = new BasicCalcTypes();
		this.paramTypes.putType("I", String.class);
		this.paramTypes.putType("T", String.class);
		HashSet<String> rp = new HashSet<String>();
		for (String s : split("PARAM_TYPES", parts[4])) {
			boolean required;
			if (SH.endsWithIgnoreCase(s, " nonull")) {
				required = true;
				s = SH.trim(SH.stripSuffixIgnoreCase(s, " nonull", true));
			} else
				required = false;
			String name = SH.trim(SH.beforeLast(s, ' '));
			String type = SH.trim(SH.afterLast(s, ' '));
			if (paramTypes.getType(name) != null)
				throw new RuntimeException("ParamTypes has duplicate param: " + name);
			paramTypes.putType(name, state.getScriptManager().getMethodFactory().forName(type));
			if (required)
				rp.add(name);
		}
		this.requiredParams = AH.toArray(rp, String.class);
		this.expression = parts[5];
		try {
			this.expressionCalc = state.getScriptManager().prepareSql(expression, paramTypes);
			if (this.expressionCalc != null && this.expressionCalc.getReturnType() != Boolean.class)
				throw new RuntimeException("must return boolean");
		} catch (Exception e) {
			throw new RuntimeException("Expression error: " + e.getMessage(), e);
		}
		this.routeList = split("RouteList", parts[6]);
		this.routListCenterids = new BitSet(routeList.length);
		if (SH.equals(parts[6], "*")) {
			for (AmiRelayCenterDefinition i : centerIds.values())
				this.routListCenterids.set(i.getId());

		} else {
			for (String s : routeList) {
				AmiRelayCenterDefinition centerId = centerIds.get(s);
				if (centerId == null)
					throw new RuntimeException("RouteList has unknown center: " + s + " (Available centers include: " + SH.join(',', centerIds.keySet()) + ")");
				this.routListCenterids.set(centerId.getId());
			}
		}
		this.onTrue = parseAction("OnTrue", parts[7]);
		this.onFalse = parseAction("OnFalse", parts[8]);
		this.isSkip = AH.length(this.messageTypeChars) == 0 || (this.objectTypes.size() == 0 && !this.hasWildCardObjectType);
	}

	private String[] split(String description, String data) {
		if (SH.isnt(data))
			return OH.EMPTY_STRING_ARRAY;
		String[] r = SH.splitWithEscape(',', '\\', data);
		for (String s : r)
			if (SH.isnt(s))
				throw new RuntimeException(description + " is invalid, empty token");
		if (r.length > 1) {
			if (CH.s(r).size() != r.length)
				throw new RuntimeException(description + " is invalid, duplicate entry");
		}
		return r;
	}

	static public byte parseAction(String description, String action) {
		action = SH.trim(action);
		if (SH.isnt(action))
			return ACTION_CONTINUE;
		if ("CONTINUE".equalsIgnoreCase(action))
			return ACTION_CONTINUE;
		else if ("BREAK".equalsIgnoreCase(action))
			return ACTION_BREAK;
		else
			throw new RuntimeException(description + " is invalid: " + action + " (must be CONTINUE or BREAK)");
	}

	public String getRouteName() {
		return routeName;
	}

	public int getPriority() {
		return priority;
	}

	//	public String[] getObjectTypes() {
	//		return objectTypes;
	//	}

	public com.f1.base.CalcTypes getParamTypes() {
		return paramTypes;
	}

	public String getExpression() {
		return expression;
	}

	public DerivedCellCalculator getExpressionCalc() {
		return expressionCalc;
	}

	public String[] getRouteList() {
		return routeList;
	}

	public byte getOnTrue() {
		return onTrue;
	}

	public byte getOnFalse() {
		return onFalse;
	}

	@Override
	public int compareTo(AmiRelayRoute o) {
		int r = OH.compare(this.priority, o.priority);
		return r != 0 ? r : OH.compare(this.routeName, o.routeName);
	}

	public BitSet getRoutListCenterids() {
		return routListCenterids;
	}

	public String[] getMessageTypes() {
		return messageTypes;
	}

	public boolean hasMessageType(char messageType) {
		return this.messageTypeChars == null || AH.indexOf(messageType, this.messageTypeChars) != -1;
	}
	public boolean hasObjectType(String objecType) {
		return objecType == null || this.hasWildCardObjectType || this.objectTypes.contains(objecType);
	}

	public boolean isSkip() {
		return this.isSkip;
	}

	public void onMatch() {
		this.matchCount++;

	}
	public long getMatchCount() {
		return this.matchCount;
	}
}
