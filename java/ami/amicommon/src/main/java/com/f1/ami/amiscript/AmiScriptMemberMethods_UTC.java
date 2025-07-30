package com.f1.ami.amiscript;

import com.f1.base.DateMillis;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_UTC extends AmiScriptBaseMemberMethods<DateMillis> {

	private AmiScriptMemberMethods_UTC() {
		super();

		addMethod(FORMAT);
		addMethod(FORMAT2);
		addMethod(DATETIME);
		addMethod(DATETIME2);
		addMethod(DATE);
		addMethod(DATE2);
		addMethod(TIME);
		addMethod(TIME2);
		addCustomDebugProperty("timeMillis", Long.class);
	}

	@Override
	protected Object getCustomDebugProperty(String name, DateMillis value) {
		if ("timeMillis".equals(name))
			return value.getDate();
		return super.getCustomDebugProperty(name, value);
	}

	private static final AmiAbstractMemberMethod<DateMillis> FORMAT = new AmiAbstractMemberMethod<DateMillis>(DateMillis.class, "format", String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateMillis targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "Formats the UTC as a specific date time";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<DateMillis> FORMAT2 = new AmiAbstractMemberMethod<DateMillis>(DateMillis.class, "format", String.class, String.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateMillis targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "Formats the UTC as a string based on the supplied format for a supplied timezone";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<DateMillis> DATETIME = new AmiAbstractMemberMethod<DateMillis>(DateMillis.class, "datetime", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateMillis targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "converts to datetime string in format: yyyy-MM-dd HH:mm:ss";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<DateMillis> DATETIME2 = new AmiAbstractMemberMethod<DateMillis>(DateMillis.class, "datetime", String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateMillis targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "converts to datetime string in format: yyyy-MM-dd HH:mm:ss for a supplied timezone";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<DateMillis> TIME = new AmiAbstractMemberMethod<DateMillis>(DateMillis.class, "time", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateMillis targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "converts to datetime string in format: HH:mm:ss";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<DateMillis> TIME2 = new AmiAbstractMemberMethod<DateMillis>(DateMillis.class, "time", String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateMillis targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "converts to datetime string in format: HH:mm:ss for a supplied timezone";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<DateMillis> DATE = new AmiAbstractMemberMethod<DateMillis>(DateMillis.class, "date", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateMillis targetObject, Object[] params, DerivedCellCalculator caller) {
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
	private static final AmiAbstractMemberMethod<DateMillis> DATE2 = new AmiAbstractMemberMethod<DateMillis>(DateMillis.class, "date", String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateMillis targetObject, Object[] params, DerivedCellCalculator caller) {
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
		return "UTC";
	}
	@Override
	public String getVarTypeDescription() {
		return "Time since unix epoc in milliseconds";
	}
	@Override
	public Class<DateMillis> getVarType() {
		return DateMillis.class;
	}
	@Override
	public Class<DateMillis> getVarDefaultImpl() {
		return null;
	}

	public static final AmiScriptMemberMethods_UTC INSTANCE = new AmiScriptMemberMethods_UTC();

}
