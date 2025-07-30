package com.f1.ami.amiscript;

import java.util.List;
import java.util.Map;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.xml.XmlElement;
import com.f1.utils.xml.XmlNode;
import com.f1.utils.xml.XmlText;

public class AmiScriptMemberMethods_XmlElement extends AmiScriptBaseMemberMethods<XmlElement> {

	public AmiScriptMemberMethods_XmlElement() {
		super();
		addMethod(INIT);
		addMethod(this.getName);
		addMethod(this.getElements);
		addMethod(this.getElements2);
		addMethod(this.getAttribute);
		addMethod(this.getAttributes);
		addMethod(this.getInnerAsString);
		addMethod(this.addAttribute);
		addMethod(this.addAttribute2);
		addMethod(this.addAttribute3);
		addMethod(this.removeAttribute);
		addMethod(this.getChildren);
		addMethod(this.hasAttribute);
		addMethod(this.toString);
		addMethod(this.toLegibleString);
		//addMethod(this.setFirstElement);
		//addMethod(this.deleteFirstElement);
		addMethod(this.getFirstElement);
		addMethod(this.addNode);
		addMethod(this.addChild);
	}

	private static final AmiAbstractMemberMethod<XmlElement> INIT = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, null, XmlElement.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			String xmlString = (String) params[0];
			return new XmlElement(xmlString);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "xmlString" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "xmlString to create the XmlElement" };
		}
		@Override
		protected String getHelp() {
			return "Creates an XML Element from an XML String";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> getName = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "getName", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getName();
		}
		@Override
		protected String getHelp() {
			return "Returns tag name of current element";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> getElements = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "getElements", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getElements();
		}
		@Override
		protected String getHelp() {
			return "Returns any elements within the current element";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> getElements2 = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "getElements", List.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];

			return targetObject.getElements(name);
		}
		@Override
		protected String getHelp() {
			return "Returns any elements within the current element based on the name specified";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "name" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Name the elements should match" };
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> getFirstElement = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "getFirstElement", XmlElement.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];

			return targetObject.getFirstElement(name);
		}
		@Override
		protected String getHelp() {
			return "Returns the first element with the specified name";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "name" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Name the element should match" };
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> getAttributes = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "getAttributes", Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAttributes();
		}
		@Override
		protected String getHelp() {
			return "Returns any attributes of the current element";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> getAttribute = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "getAttribute", String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			String key = (String) params[0];

			return targetObject.getAttribute(key);
		}
		@Override
		protected String getHelp() {
			return "Gets the attribute specified by a name";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "name" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Name the attribute should match" };
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> hasAttribute = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "hasAttribute", String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			String key = (String) params[0];

			return targetObject.hasAttribute(key);
		}
		@Override
		protected String getHelp() {
			return "Check if the XmlElement contains an attribute based on the entered key";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Key that the attribute should have" };
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> getInnerAsString = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "getInnerAsString", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getInnerAsString();
		}
		@Override
		protected String getHelp() {
			return "Returns inner value of element as string";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> toString = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "toString", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.toString();
		}
		@Override
		protected String getHelp() {
			return "Returns the XmlElement as a string";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> toLegibleString = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "toLegibleString", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.toLegibleString();
		}
		@Override
		protected String getHelp() {
			return "Returns the XmlElement as a legible string";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> addAttribute = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "addAttribute", XmlElement.class, String.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			String key = (String) params[0];

			String value = (String) params[1];

			return targetObject.addAttribute(key, value);
		}
		@Override
		protected String getHelp() {
			return "Add an attribute to the XmlElement corresponding to the key-value pair";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key corresponding to the key", "value the key will have" };
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> addAttribute2 = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "addAttribute", XmlElement.class, String.class,
			Long.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			String key = (String) params[0];

			Long value = (long) params[1];

			return targetObject.addAttribute(key, value);
		}
		@Override
		protected String getHelp() {
			return "Add an attribute to the XmlElement corresponding to the key-value pair";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key corresponding to the key", "value the key will have" };
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> addAttribute3 = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "addAttribute", XmlElement.class, String.class,
			Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			String key = (String) params[0];

			Double value = (Double) params[1];

			return targetObject.addAttribute(key, value);
		}
		@Override
		protected String getHelp() {
			return "Add an attribute to the XmlElement corresponding to the key-value pair";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key corresponding to the key", "value the key will have" };
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> addNode = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "addNode", XmlElement.class, XmlText.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			XmlText node = (XmlText) params[0];

			return targetObject.addTextNode(node);
		}
		@Override
		protected String getHelp() {
			return "Add an existing XmlText node to the XmlElement";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "XmlText" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "XmlText node to be added" };
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> addChild = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "addChild", XmlElement.class, XmlNode.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			XmlNode node = (XmlNode) params[0];

			return targetObject.addChild(node);
		}
		@Override
		protected String getHelp() {
			return "Add an XmlNode - element or text - to an XmlElement";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "XmlNode" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "XmlNode node to be added" };
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> removeAttribute = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "removeAttribute", XmlElement.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];

			return targetObject.removeAttribute(name);
		}
		@Override
		protected String getHelp() {
			return "Remove an attribute based on the attribute name entered";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "name" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Name that should match the attribute to remove" };
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> getChildren = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "getChildren", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getChildren();
		}
		@Override
		protected String getHelp() {
			return "Get a list of the XmlElement's children";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> setFirstElement = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "setFirstElement", XmlElement.class,
			XmlElement.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			XmlElement first = (XmlElement) params[0];

			return targetObject.setFirstElement(first);
		}
		@Override
		protected String getHelp() {
			return "Set the first element to be the entered XmlElement";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "XmlElement" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "XmlElement object that should become the first element" };
		}
	};

	public final AmiAbstractMemberMethod<XmlElement> deleteFirstElement = new AmiAbstractMemberMethod<XmlElement>(XmlElement.class, "deleteFirstElement", XmlElement.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, XmlElement targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];

			return targetObject.deleteFirstElement(name);
		}
		@Override
		protected String getHelp() {
			return "Delete the first XmlElement based on the entered name";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "name" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Name that should match the XmlElement to be deleted" };
		}
	};

	@Override
	public String getVarTypeName() {
		return "XmlElement";
	}

	@Override
	public String getVarTypeDescription() {
		return "Element for XML";
	}

	@Override
	public Class<XmlElement> getVarType() {
		return XmlElement.class;
	}

	@Override
	public Class<? extends XmlElement> getVarDefaultImpl() {
		return XmlElement.class;
	}

	public static AmiScriptMemberMethods_XmlElement INSTANCE = new AmiScriptMemberMethods_XmlElement();

}
