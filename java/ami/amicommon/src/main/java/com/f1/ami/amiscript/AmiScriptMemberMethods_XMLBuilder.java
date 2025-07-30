package com.f1.ami.amiscript;

import java.util.Map;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.xml.XmlBuilder;
import com.f1.utils.xml.XmlElement;
import com.f1.utils.xml.XmlNode;

public class AmiScriptMemberMethods_XMLBuilder extends AmiScriptBaseMemberMethods<XmlBuilder> {

	public AmiScriptMemberMethods_XMLBuilder() {
		super();

		addMethod(this.NEW);
		addMethod(this.NEW2);
		addMethod(this.INIT);
		addMethod(this.CLEAR);
		addMethod(this.RESET);
		addMethod(this.POP);
		addMethod(this.POP_NODE);
		addMethod(this.ENTER);
		addMethod(this.CURR_SIZE);
		addMethod(this.CURR_TYPE);
		addMethod(this.NODES);
		addMethod(this.ELEMENTS);
		addMethod(this.ELEMENTS2);
		addMethod(this.WALK);
		addMethod(this.WALK2);
		addMethod(this.WALK3);
		addMethod(this.FIRST);
		addMethod(this.ADD_TEXT);
		addMethod(this.ADD_TEXT_ELEMENT);
		addMethod(this.ADD_ELEMENT);
		addMethod(this.ADD_ELEMENT2);
		addMethod(this.ADD_NODE);
		addMethod(this.ADD_ATTRIBUTES);
		addMethod(this.ADD_ATTRIBUTES2);
		addMethod(this.ADD_ATTRIBUTES3);
		addMethod(this.ADD_ATTRIBUTES4);
		addMethod(this.CURRENT);
		addMethod(this.LAST_ADD);
		addMethod(this.BUILD);
		addMethod(this.BUILD_AND_CLEAR);
		addMethod(this.TO_STRING);
		addMethod(this.TO_STRING2);
		//		addMethod(this.parseDocument);
	}

	public final AmiAbstractMemberMethod<XmlBuilder> NEW = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, null, XmlBuilder.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return new XmlBuilder();
		}

		@Override
		protected String getHelp() {
			return "Creates a XmlBuilder";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> NEW2 = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, null, XmlBuilder.class, XmlNode.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return new XmlBuilder((XmlNode) params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "XmlElement" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Stringified XML" };
		}
		@Override
		protected String getHelp() {
			return "Creates a XmlBuilder and initializes with an existing xml";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> INIT = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "init", XmlBuilder.class, XmlNode.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.init((XmlNode) params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "XmlElement" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Stringified XML" };
		}
		@Override
		protected String getHelp() {
			return "Initializes the XmlBuilder with an existing XML node";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> CLEAR = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "clear", XmlBuilder.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.clear();
		}
		@Override
		protected String getHelp() {
			return "Clears the XmlBuilder";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> RESET = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "reset", XmlBuilder.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.reset();
		}
		@Override
		protected String getHelp() {
			return "Resets the XmlBuilder";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> POP = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "pop", XmlBuilder.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.pop();
		}
		@Override
		protected String getHelp() {
			return "Pops a XML element from the builder";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> POP_NODE = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "popNode", XmlBuilder.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.popNode();
		}
		@Override
		protected String getHelp() {
			return "Pops the latest node from the XmlBuilder";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> ENTER = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "enter", XmlBuilder.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.enter();
		}
		@Override
		protected String getHelp() {
			return "Enter into the element that was last added";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> CURR_SIZE = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "csize", Integer.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.csize();
		}
		@Override
		protected String getHelp() {
			return "Returns the current element's size in XmlBuilder. <BR> Returns -1 if XmlBuilder is not a list";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> CURR_TYPE = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "ctype", Object.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.ctype();
		}
		@Override
		protected String getHelp() {
			return "Returns the current element's type in XmlBuilder";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> NODES = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "nodes", XmlBuilder.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.nodes();
		}
		@Override
		protected String getHelp() {
			return "Walk Nodes";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> ELEMENTS = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "elements", XmlBuilder.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.elements();
		}
		@Override
		protected String getHelp() {
			return "Walk Elements";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> ELEMENTS2 = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "elements", XmlBuilder.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.elements((String) params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "name" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "XML element name" };
		}
		@Override
		protected String getHelp() {
			return "Walk Elements with provided name";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> WALK = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "walk", XmlBuilder.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.walk((String) params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "name" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "XML element name" };
		}
		@Override
		protected String getHelp() {
			return "Walk Elements";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> WALK2 = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "walk", XmlBuilder.class, true, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.walk(params);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "stops" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Expects stops to be strings or integers. " };
		}
		@Override
		protected String getHelp() {
			return "Walk Elements";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> WALK3 = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "walk", XmlBuilder.class, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.walk(params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "pos" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Position of the XML element" };
		}
		@Override
		protected String getHelp() {
			return "Walk Elements";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> FIRST = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "first", XmlBuilder.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.walk(params[0]);
		}
		@Override
		protected String getHelp() {
			return "Returns the first elements";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> ADD_TEXT = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "addText", XmlBuilder.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.addText((String) params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "text" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Text to append to the XmlBuilder" };
		}
		@Override
		protected String getHelp() {
			return "Adds text to XmlBuilder";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> ADD_TEXT_ELEMENT = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "addTextElement", XmlBuilder.class, String.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.addTextElement((String) params[0], (String) params[1]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "name", "text" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Alias of element", "Text to add to the XmlBuilder" };
		}
		@Override
		protected String getHelp() {
			return "Adds text to element in XmlBuilder";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> ADD_ELEMENT = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "addElement", XmlBuilder.class, XmlElement.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.addElement((XmlElement) params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "xmlElement" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "XML element to be added to the XmlBuilder" };
		}
		@Override
		protected String getHelp() {
			return "Adds XmlElement in XmlBuilder";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> ADD_ELEMENT2 = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "addElement", XmlBuilder.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.addElement((String) params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "elementName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Name of element to be added to the XmlBuilder" };
		}
		@Override
		protected String getHelp() {
			return "Adds element to element in XmlBuilder using ";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> ADD_NODE = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "addNode", XmlBuilder.class, XmlNode.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.addNode((XmlNode) params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "nodeName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of xml node to be added into the XmlBuilder" };
		}
		@Override
		protected String getHelp() {
			return "Adds a new node with provided name";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> ADD_ATTRIBUTES = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "addAttribute", XmlBuilder.class, Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.addAttributes((Map<String, String>) params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "attributes" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Map of attributes of xml elements" };
		}
		@Override
		protected String getHelp() {
			return "Adds attributes to XmlBuilder based on provided map";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> ADD_ATTRIBUTES2 = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "addAttribute", XmlBuilder.class, String.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.addAttribute((String) params[0], (String) params[1]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key of the xml element", "value of the xml element" };
		}
		@Override
		protected String getHelp() {
			return "Adds attributes to XmlBuilder based on key, value provided";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> ADD_ATTRIBUTES3 = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "addAttribute", XmlBuilder.class, String.class,
			Long.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.addAttribute((String) params[0], (Long) params[1]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key of the xml element", "value of the xml element" };
		}
		@Override
		protected String getHelp() {
			return "Adds attributes to XmlBuilder based on key, value provided";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> ADD_ATTRIBUTES4 = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "addAttribute", XmlBuilder.class, String.class,
			Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.addAttribute((String) params[0], (Double) params[1]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key of the xml element", "value of the xml element" };
		}
		@Override
		protected String getHelp() {
			return "Adds attributes to XmlBuilder based on key, value provided";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> CURRENT = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "current", Object.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.current();
		}
		@Override
		protected String getHelp() {
			return "Returns current element";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> LAST_ADD = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "lastAdd", XmlNode.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.lastAdd();
		}
		@Override
		protected String getHelp() {
			return "Returns last added xml node";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> BUILD = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "build", XmlNode.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.build();
		}
		@Override
		protected String getHelp() {
			return "Builds XML";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> BUILD_AND_CLEAR = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "buildAndClear", XmlNode.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.buildAndClear();
		}
		@Override
		protected String getHelp() {
			return "Builds and clears XML";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> TO_STRING = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "toString", XmlBuilder.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.toString((Object) params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "xmlElement" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "xmlElement to stringify" };
		}
		@Override
		protected String getHelp() {
			return "Stringifies xmlElement";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlBuilder> TO_STRING2 = new AmiAbstractMemberMethod<XmlBuilder>(XmlBuilder.class, "toString", XmlBuilder.class, StringBuilder.class,
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.toString((StringBuilder) params[0], (Object) params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "stringBuilder", "xmlElement" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Stringbuilder to append to", "xmlElement to stringify" };
		}
		@Override
		protected String getHelp() {
			return "Stringifies xmlElement ";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "XmlBuilder";
	}

	@Override
	public String getVarTypeDescription() {
		return "Builder for XML";
	}

	@Override
	public Class<XmlBuilder> getVarType() {
		return XmlBuilder.class;
	}

	@Override
	public Class<? extends XmlBuilder> getVarDefaultImpl() {
		return XmlBuilder.class;
	}

	public static AmiScriptMemberMethods_XMLBuilder INSTANCE = new AmiScriptMemberMethods_XMLBuilder();
}
