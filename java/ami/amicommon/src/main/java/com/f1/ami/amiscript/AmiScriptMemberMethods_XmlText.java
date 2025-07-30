package com.f1.ami.amiscript;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.xml.XmlText;

public class AmiScriptMemberMethods_XmlText extends AmiScriptBaseMemberMethods<XmlText> {

	public AmiScriptMemberMethods_XmlText() {
		super();
		addMethod(INIT);
		addMethod(toString, this.toString());
		addMethod(toLegibleString);
	}

	private static final AmiAbstractMemberMethod<XmlText> INIT = new AmiAbstractMemberMethod<XmlText>(XmlText.class, null, XmlText.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlText targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String text = (String) params[0];

				XmlText xmlText = new XmlText(text);

				return xmlText;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "String" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "String to create the XmlText node" };
		}
		@Override
		protected String getHelp() {
			return "Creates an XmlText node based on the given string";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlText> toString = new AmiAbstractMemberMethod<XmlText>(XmlText.class, "toString", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlText targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.toString();
		}
		@Override
		protected String getHelp() {
			return "Returns the XmlText node as a string";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlText> toLegibleString = new AmiAbstractMemberMethod<XmlText>(XmlText.class, "toLegibleString", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlText targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.toLegibleString(new StringBuilder(), 0);
		}
		@Override
		protected String getHelp() {
			return "Returns the XmlText node as a legible string";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "XmlText";
	}

	@Override
	public String getVarTypeDescription() {
		return "A text node for an XmlElement";
	}

	@Override
	public Class<XmlText> getVarType() {
		return XmlText.class;
	}

	@Override
	public Class<? extends XmlText> getVarDefaultImpl() {
		return XmlText.class;
	}

	public static AmiScriptMemberMethods_XmlText INSTANCE = new AmiScriptMemberMethods_XmlText();

}
