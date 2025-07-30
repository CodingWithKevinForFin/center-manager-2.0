package com.f1.ami.amiscript;

import com.f1.base.DateNanos;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_UTCN extends AmiScriptBaseMemberMethods<DateNanos> {

	private AmiScriptMemberMethods_UTCN() {
		super();

		addMethod(FORMAT);
		addMethod(FORMAT2);
		addMethod(DATETIME);
		addMethod(DATETIME2);
		addMethod(DATE);
		addMethod(DATE2);
		addMethod(TIME);
		addMethod(TIME2);
		addCustomDebugProperty("timeNanos", Long.class);
	}
	@Override
	protected Object getCustomDebugProperty(String name, DateNanos value) {
		if ("timeNanos".equals(value.getPanelType()))
			return value.getTimeNanos();
		return super.getCustomDebugProperty(name, value);
	}

	private static final AmiAbstractMemberMethod<DateNanos> FORMAT = new AmiAbstractMemberMethod<DateNanos>(DateNanos.class, "format", String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateNanos targetObject, Object[] params, DerivedCellCalculator caller) {
			String format = (String) params[0];
			return getService(sf).getformatDate(format, targetObject, sf);
		}
		protected String[] buildParamNames() {
			return new String[] { "Format" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {
					"G-era BC/AD, y-year, M-month, w-week in year, W-week in month, D-day in year, d-day in month, F-day of week in month,E-day in week, a-am/pm, H-hour 0..23, k-hour 1..24, K-hour 0..11, h-hour 1..12, m-minute, s-second, S-millisecond, z-timezone, Z-RFR 822 timezone" };
		}
		@Override
		protected String getHelp() {
			return "Returns a string which formats the UTC as a specific date time.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<DateNanos> FORMAT2 = new AmiAbstractMemberMethod<DateNanos>(DateNanos.class, "format", String.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateNanos targetObject, Object[] params, DerivedCellCalculator caller) {
			String format = (String) params[0];
			String timezone = (String) params[1];
			return getService(sf).getformatDate(format, targetObject, timezone);
		}
		protected String[] buildParamNames() {
			return new String[] { "Format", "Timezone" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {
					"G-era BC/AD, y-year, M-month, w-week in year, W-week in month, D-day in year, d-day in month, F-day of week in month,E-day in week, a-am/pm, H-hour 0..23, k-hour 1..24, K-hour 0..11, h-hour 1..12, m-minute, s-second, S-millisecond, z-timezone, Z-RFR 822 timezone",
					"timezone, if null uses user default timezone" };
		}
		@Override
		protected String getHelp() {
			return "Retruns a string which formats the UTC as a string based on the supplied format for a supplied timezone.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<DateNanos> DATETIME = new AmiAbstractMemberMethod<DateNanos>(DateNanos.class, "datetime", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateNanos targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiService service = getService(sf);
			return service.getformatDate("yyyy-MM-dd HH:mm:ss", targetObject, sf);
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}
		@Override
		protected String getHelp() {
			return "Returns a datetime string in yyyy-MM-dd HH:mm:ss.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<DateNanos> DATETIME2 = new AmiAbstractMemberMethod<DateNanos>(DateNanos.class, "datetime", String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateNanos targetObject, Object[] params, DerivedCellCalculator caller) {
			String timezone = (String) params[0];
			return getService(sf).getformatDate("yyyy-MM-dd HH:mm:ss", targetObject, timezone);
		}
		protected String[] buildParamNames() {
			return new String[] { "Timezone" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "timezone, if null uses user default timezone" };
		}
		@Override
		protected String getHelp() {
			return "Returns a datetime string in yyyy-MM-dd HH:mm:ss for a supplied timezone.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<DateNanos> TIME = new AmiAbstractMemberMethod<DateNanos>(DateNanos.class, "time", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateNanos targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiService service = getService(sf);
			return service.getformatDate("HH:mm:ss", targetObject, sf);
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}
		@Override
		protected String getHelp() {
			return "Converts to datetime string in format: HH:mm:ss.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<DateNanos> TIME2 = new AmiAbstractMemberMethod<DateNanos>(DateNanos.class, "time", String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateNanos targetObject, Object[] params, DerivedCellCalculator caller) {
			String timezone = (String) params[0];
			return getService(sf).getformatDate("HH:mm:ss", targetObject, timezone);
		}
		protected String[] buildParamNames() {
			return new String[] { "Timezone" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "timezone, if null uses user default timezone" };
		}
		@Override
		protected String getHelp() {
			return "Returns a datetime string in HH:mm:ss for a supplied timezone.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<DateNanos> DATE = new AmiAbstractMemberMethod<DateNanos>(DateNanos.class, "date", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateNanos targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiService service = getService(sf);
			return service.getformatDate("yyyy-MM-dd", targetObject, sf);
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}
		@Override
		protected String getHelp() {
			return "converts to datetime string in format: yyyy-MM-dd";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<DateNanos> DATE2 = new AmiAbstractMemberMethod<DateNanos>(DateNanos.class, "date", String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateNanos targetObject, Object[] params, DerivedCellCalculator caller) {
			String timezone = (String) params[0];
			return getService(sf).getformatDate("yyyy-MM-dd", targetObject, timezone);
		}
		protected String[] buildParamNames() {
			return new String[] { "Timezone" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "timezone, if null uses user default timezone" };
		}
		@Override
		protected String getHelp() {
			return "converts to datetime string in format: yyyy-MM-dd for a supplied timezone";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	@Override
	public String getVarTypeName() {
		return "UTCN";
	}
	@Override
	public String getVarTypeDescription() {
		return "Time since unix epoc in nanoseconds";
	}
	@Override
	public Class<DateNanos> getVarType() {
		return DateNanos.class;
	}
	@Override
	public Class<DateNanos> getVarDefaultImpl() {
		return null;
	}

	public static final AmiScriptMemberMethods_UTCN INSTANCE = new AmiScriptMemberMethods_UTCN();

}
