package com.f1.ami.amiscript;

import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.CalcFrame;
import com.f1.base.UUID;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_UUID extends AmiScriptBaseMemberMethods<UUID> {

	private AmiScriptMemberMethods_UUID() {
		super();
		addMethod(INIT);
		addMethod(INIT2);
		addMethod(GET_LEAST_SIGNIFICANT_BITS, "leastSignificantBits");
		addMethod(GET_MOST_SIGNIFICANT_BITS, "mostSignificantBits");
		addMethod(GET_VERSION);
		addMethod(GET_VARIANT);
		addMethod(GET_NODE);
		addMethod(GET_CLOCK_SEQUENCE);
		addMethod(GET_TIMESTAMP_RAW);
		addMethod(GET_TIMESTAMP_UTC);
		addMethod(GET_TIMESTAMP_UTCN);
	}

	private static final AmiAbstractMemberMethod<UUID> INIT = new AmiAbstractMemberMethod<UUID>(UUID.class, null, UUID.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, UUID targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String s = (String) params[0];
				return new UUID(s);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "UUID formatted string" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "List, ex: 9a572886-a0ba-49e0-9218-0ac8302cc489" };
		}
		@Override
		protected String getHelp() {
			return "creates a UUID";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<UUID> INIT2 = new AmiAbstractMemberMethod<UUID>(UUID.class, null, UUID.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, UUID targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return new UUID(MH.RANDOM_SECURE);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String getHelp() {
			return "create a new, random secure UUID";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<UUID> GET_MOST_SIGNIFICANT_BITS = new AmiAbstractMemberMethod<UUID>(UUID.class, "getMostSignificantBits", Long.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, UUID target, Object[] params, DerivedCellCalculator caller) {
			return target.getMostSignificantBits();
		}
		@Override
		protected String getHelp() {
			return "returns the most significant 64 bits";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<UUID> GET_LEAST_SIGNIFICANT_BITS = new AmiAbstractMemberMethod<UUID>(UUID.class, "getLeastSignificantBits", Long.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, UUID target, Object[] params, DerivedCellCalculator caller) {
			return target.getLeastSignificantBits();
		}
		@Override
		protected String getHelp() {
			return "returns the  least significant 64 bits";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<UUID> GET_VERSION = new AmiAbstractMemberMethod<UUID>(UUID.class, "getVersion", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, UUID target, Object[] params, DerivedCellCalculator caller) {
			return target.getVersion();
		}
		@Override
		protected String getHelp() {
			return "returns the version of this uid";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<UUID> GET_VARIANT = new AmiAbstractMemberMethod<UUID>(UUID.class, "getVariant", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, UUID target, Object[] params, DerivedCellCalculator caller) {
			return target.getVariant();
		}
		@Override
		protected String getHelp() {
			return "returns the variant of this uid (it is the raw variant)";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<UUID> GET_NODE = new AmiAbstractMemberMethod<UUID>(UUID.class, "getNode", Long.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, UUID target, Object[] params, DerivedCellCalculator caller) {
			return target.getNode();
		}
		@Override
		protected String getHelp() {
			return "returns the node part of this uid";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<UUID> GET_CLOCK_SEQUENCE = new AmiAbstractMemberMethod<UUID>(UUID.class, "getClockSequence", UUID.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, UUID target, Object[] params, DerivedCellCalculator caller) {
			if (target.getVersion() != 1)
				return null;
			return target.getClockSequence();
		}
		@Override
		protected String getHelp() {
			return "Returns an int that is the clock sequence value associated with this UUID.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<UUID> GET_TIMESTAMP_RAW = new AmiAbstractMemberMethod<UUID>(UUID.class, "getTimestampRaw", UUID.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, UUID target, Object[] params, DerivedCellCalculator caller) {
			if (target.getVersion() != 1)
				return null;
			return target.getTimestamp();
		}
		@Override
		protected String getHelp() {
			return "Returns the time stamp value of this UUID.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<UUID> GET_TIMESTAMP_UTC = new AmiAbstractMemberMethod<UUID>(UUID.class, "getTimestampUTC", DateMillis.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, UUID target, Object[] params, DerivedCellCalculator caller) {
			if (target.getVersion() != 1)
				return null;
			return new DateMillis(target.getUnixTimestampMillis());
		}
		@Override
		protected String getHelp() {
			return "Returns the timestamp of this UUID in millisecond.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<UUID> GET_TIMESTAMP_UTCN = new AmiAbstractMemberMethod<UUID>(UUID.class, "getTimestampUTCN", DateNanos.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, UUID target, Object[] params, DerivedCellCalculator caller) {
			if (target.getVersion() != 1)
				return null;
			return new DateNanos(target.getUnixTimestampNanos());
		}
		@Override
		protected String getHelp() {
			return "Returns the timestamp of this UUID in nanoseconds.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "UUID";
	}
	@Override
	public String getVarTypeDescription() {
		return "An immutable universally unique identifier (UUID).";
	}
	@Override
	public Class<UUID> getVarType() {
		return UUID.class;
	}
	@Override
	public Class<UUID> getVarDefaultImpl() {
		return UUID.class;
	}

	public static AmiScriptMemberMethods_UUID INSTANCE = new AmiScriptMemberMethods_UUID();
}
