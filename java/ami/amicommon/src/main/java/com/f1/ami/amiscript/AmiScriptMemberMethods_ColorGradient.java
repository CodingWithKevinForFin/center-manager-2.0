package com.f1.ami.amiscript;

import java.util.Map;
import java.util.TreeMap;

import com.f1.base.CalcFrame;
import com.f1.utils.ColorGradient;
import com.f1.utils.ColorHelper;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_ColorGradient extends AmiScriptBaseMemberMethods<ColorGradient> {

	private AmiScriptMemberMethods_ColorGradient() {
		super();

		addMethod(INIT);
		addMethod(TO_COLOR);
		addMethod(GET_STOPS_COUNT);
		addMethod(GET_STOP_COLOR);
		addMethod(GET_STOP_VALUE);
		addCustomDebugProperty("stops", Map.class);
	}

	@Override
	protected Object getCustomDebugProperty(String name, ColorGradient value) {
		if ("stops".equals(name)) {
			Map<Double, String> m = new TreeMap<Double, String>();
			for (int i = 0; i < value.getStopsCount(); i++)
				m.put(value.getStopValue(i), ColorHelper.toString(value.getStopColor(i)));
			return m;
		}
		return super.getCustomDebugProperty(name, value);
	}

	private final static AmiAbstractMemberMethod<ColorGradient> INIT = new AmiAbstractMemberMethod<ColorGradient>(ColorGradient.class, null, ColorGradient.class, false,
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, ColorGradient targetObject, Object[] params, DerivedCellCalculator caller) {
			String g = (String) params[0];
			return new ColorGradient(g);
		}
		protected String[] buildParamNames() {
			return new String[] { "gradient" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "num:color,num:color" };
		}
		@Override
		protected String getHelp() {
			return "Construct a new Color Gradient.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<ColorGradient> TO_COLOR = new AmiAbstractMemberMethod<ColorGradient>(ColorGradient.class, "toColor", String.class, false,
			Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, ColorGradient targetObject, Object[] params, DerivedCellCalculator caller) {
			Number g = (Number) params[0];
			return g == null ? null : ColorHelper.toString(targetObject.toColor(g.doubleValue()));
		}
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "value" };
		}
		@Override
		protected String getHelp() {
			return "Returns the color hexcode associated with the supplied value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<ColorGradient> GET_STOPS_COUNT = new AmiAbstractMemberMethod<ColorGradient>(ColorGradient.class, "getStopsCount", String.class,
			false) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, ColorGradient targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getStopsCount();
		}
		@Override
		protected String getHelp() {
			return "Returns the number of color stops, typically there should be at least two.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<ColorGradient> GET_STOP_COLOR = new AmiAbstractMemberMethod<ColorGradient>(ColorGradient.class, "getStopColor", String.class,
			false, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, ColorGradient targetObject, Object[] params, DerivedCellCalculator caller) {
			Number g = (Number) params[0];
			if (g == null)
				return null;
			int n = g.intValue();
			if (n >= targetObject.getStopsCount())
				return null;
			return ColorHelper.toString(targetObject.getStopColor(n));
		}
		protected String[] buildParamNames() {
			return new String[] { "index" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "zero-based index" };
		}
		@Override
		protected String getHelp() {
			return "Returns the color at the specified index.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<ColorGradient> GET_STOP_VALUE = new AmiAbstractMemberMethod<ColorGradient>(ColorGradient.class, "getStopValue", String.class,
			false, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, ColorGradient targetObject, Object[] params, DerivedCellCalculator caller) {
			Number g = (Number) params[0];
			if (g == null)
				return null;
			int n = g.intValue();
			if (n >= targetObject.getStopsCount())
				return null;
			return targetObject.getStopColor(n);
		}
		protected String[] buildParamNames() {
			return new String[] { "index" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "zero-based index" };
		}
		@Override
		protected String getHelp() {
			return "Returns the stop value at the specified index.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	@Override
	public String getVarTypeName() {
		return "ColorGradient";
	}
	@Override
	public String getVarTypeDescription() {
		return "An immutable color gradient, represented by a series of color stops order by the stop value.";
	}
	@Override
	public Class<ColorGradient> getVarType() {
		return ColorGradient.class;
	}
	@Override
	public Class<ColorGradient> getVarDefaultImpl() {
		return ColorGradient.class;
	}

	public static AmiScriptMemberMethods_ColorGradient INSTANCE = new AmiScriptMemberMethods_ColorGradient();
}
