package com.f1.ami.amiscript;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.xml.XmlElement;
import com.f1.utils.xml.XmlParser;

public class AmiScriptMemberMethods_XmlParser extends AmiScriptBaseMemberMethods<XmlParser> {
	
	public AmiScriptMemberMethods_XmlParser() {
		super();
		addMethod(this.INIT);
		addMethod(this.parseDocument);
	}

	public final AmiAbstractMemberMethod<XmlParser> INIT = new AmiAbstractMemberMethod<XmlParser>(XmlParser.class, null,
			XmlParser.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlParser targetObject, Object[] params, DerivedCellCalculator caller) {
			return new XmlParser();
		}

		@Override
		protected String getHelp() {
			return "Creates a xml parser";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	
	public final AmiAbstractMemberMethod<XmlParser> parseDocument = new AmiAbstractMemberMethod<XmlParser>(XmlParser.class, "parseDocument",
			XmlElement.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlParser targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.parseDocument((String)params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "xml" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Stringified XML" };
		}
		@Override
		protected String getHelp() {
			return "Parses an xml string and returns an XmlElement object";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	
	@Override
	public String getVarTypeName() {
		return "XmlParser";
	}

	@Override
	public String getVarTypeDescription() {
		return "Parser for XML";
	}

	@Override
	public Class<XmlParser> getVarType() {
		return XmlParser.class;
	}

	@Override
	public Class<? extends XmlParser> getVarDefaultImpl() {
		return XmlParser.class;
	}
	
	public static AmiScriptMemberMethods_XmlParser INSTANCE = new AmiScriptMemberMethods_XmlParser();
}
