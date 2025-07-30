package com.f1.ami.relay;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiRelayMessage;
import com.f1.base.CalcTypes;
import com.f1.base.Caster;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiRelayRouteChain {

	final static private Logger log = LH.get();
	private AmiRelayRoute[] routes;
	private BasicCalcTypes interestedTypes = new BasicCalcTypes();//Object means we need to indivually cast per rule
	private IntKeyMap<Tuple3<String, Caster<?>, Integer>> interestedTypeKeys = new IntKeyMap<Tuple3<String, Caster<?>, Integer>>();
	private AmiRelayState state;
	final private String[] typeKeysArray;

	public AmiRelayRouteChain(AmiRelayState state, List<AmiRelayRoute> routes) {
		this.routes = AH.toArray(CH.sort(routes), AmiRelayRoute.class);
		for (AmiRelayRoute i : this.routes) {
			if (i.isSkip())
				continue;
			CalcTypes paramTypes = i.getParamTypes();
			for (String key : paramTypes.getVarKeys()) {
				Class<?> val = paramTypes.getType(key);
				Class<?> existing = this.interestedTypes.getType(key);
				if (existing == null)
					this.interestedTypes.putType(key, val);
				else if (OH.ne(existing, val))
					this.interestedTypes.putType(key, Object.class);
			}
		}
		this.state = state;
		int varsCount = this.interestedTypes.getVarsCount();
		if (varsCount == 0) {
			this.typeKeysArray = OH.EMPTY_STRING_ARRAY;
		} else {
			this.typeKeysArray = new String[varsCount];
			this.typeKeysArray[0] = "T";
			this.typeKeysArray[1] = "I";
			this.interestedTypeKeys.put(state.getAmiKeyId("T"), new Tuple3<String, Caster<?>, Integer>("T", Caster_String.INSTANCE, 0));
			this.interestedTypeKeys.put(state.getAmiKeyId("I"), new Tuple3<String, Caster<?>, Integer>("I", Caster_String.INSTANCE, 1));
			int n = 2;
			for (String i : this.interestedTypes.getVarKeys()) {
				short amiKeyId = state.getAmiKeyId(i);
				if (this.interestedTypeKeys.containsKey(amiKeyId))
					continue;
				this.interestedTypeKeys.put(amiKeyId, new Tuple3<String, Caster<?>, Integer>(i, OH.getCaster(this.interestedTypes.getType(i)), n));
				this.typeKeysArray[n] = i;
				n++;
			}
		}
	}

	public String[] getTypeKeysArray() {
		return this.typeKeysArray;
	}

	public IntKeyMap<Tuple3<String, Caster<?>, Integer>> getInterestedTypeKeys() {
		return this.interestedTypeKeys;
	}

	public CalcTypes getInterestedTypes() {
		return this.interestedTypes;
	}

	public void getRoutes(CalcFrameStack sf, String objectType, char messageType, BitSet sink, boolean debug, AmiRelayMessage message) {
		if (debug)
			LH.info(log, "START_ROUTE: ", messageType, "|T=" + objectType);
		for (AmiRelayRoute i : this.routes) {
			final byte action;
			if (!i.hasObjectType(objectType) || !i.hasMessageType(messageType) || !Boolean.TRUE.equals((Boolean) i.getExpressionCalc().get(sf))) {
				action = i.getOnFalse();
				if (debug)
					LH.info(log, "  ", i.getRouteName(), ": MISMATCH");
			} else {
				sink.or(i.getRoutListCenterids());
				i.onMatch();
				action = i.getOnTrue();
				if (debug)
					LH.info(log, "  ", i.getRouteName(), ": MATCH, SEND TO: ", SH.join(",", i.getRouteList()));
			}
			if (action == AmiRelayRoute.ACTION_BREAK)
				break;
		}
		if (debug) {
			if (sink.isEmpty())
				LH.info(log, "END_ROUTE: MESSAGE DROPPED");
			else
				LH.info(log, "END_ROUTE: SENDING TO " + SH.join(",", debugCenters(sink)));
		}
	}
	private List<String> debugCenters(BitSet sink) {
		int n = sink.cardinality();
		if (n == 0)
			return Collections.EMPTY_LIST;
		ArrayList<String> r = new ArrayList<String>(n);
		for (AmiRelayCenterDefinition i : state.getCentersByName().values())
			if (sink.get(i.getId()))
				r.add(i.getName() + "(" + i.getHostPort() + ")");
		return r;
	}

	public boolean shouldRoute(CalcFrameStack sf, String type, char messageType, byte centerId, boolean debug) {
		for (AmiRelayRoute i : this.routes) {
			if (!i.hasMessageType(messageType) || !i.hasMessageType(messageType))
				continue;
			final Boolean b = (Boolean) i.getExpressionCalc().get(sf);
			final byte action;
			if (Boolean.TRUE.equals(b)) {
				if (i.getRoutListCenterids().get(centerId))
					return true;
				action = i.getOnTrue();
			} else
				action = i.getOnFalse();
			if (action == AmiRelayRoute.ACTION_BREAK)
				break;
		}
		return false;
	}

	public AmiRelayRoute[] getRoutes() {
		return this.routes;
	}

	public static void logSkippingRoutes() {
		LH.info(log, "NO ROUTES SO FORWARDING ALL MESSAGES TO ALL CENTERS");
	}

}
