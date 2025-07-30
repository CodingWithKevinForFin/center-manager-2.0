package com.f1.ami.web.amiscript;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebAbstractContainerPortlet;
import com.f1.ami.web.AmiWebAbstractTablePortlet;
import com.f1.ami.web.AmiWebAliasPortlet;
import com.f1.ami.web.AmiWebCommandWrapper;
import com.f1.ami.web.AmiWebDesktopPortlet;
import com.f1.ami.web.AmiWebDividerPortlet;
import com.f1.ami.web.AmiWebDmPortlet;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebLayoutFile;
import com.f1.ami.web.AmiWebLayoutHelper;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebRealtimePortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.AmiWebWindow;
import com.f1.ami.web.charts.AmiWebManagedPortlet;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.menu.AmiWebCustomContextMenu;
import com.f1.ami.web.style.AmiWebStyle;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.TabPlaceholderPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.WebRectangle;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_Panel extends AmiWebScriptBaseMemberMethods<AmiWebPortlet> {

	private AmiWebScriptMemberMethods_Panel() {
		super();

		addMethod(CLOSE);
		addMethod(GET_TITLE, "title");
		addMethod(GET_ID, "id");
		addMethod(GET_UID, "uid");
		addMethod(GET_TYPE, "type");
		addMethod(BRING_TO_FRONT);
		addMethod(GET_VALUE);
		addMethod(SET_VALUE);
		addMethod(MINIMIZE);
		addMethod(CALL_COMMAND);
		addMethod(CALL_COMMAND2);
		addMethod(CALL_RELATIONSHIPS);
		addMethod(CALL_RELATIONSHIP_ID);
		addMethod(CLEAR_USER_SELECTION);
		addMethod(GET_CURREBNT_RELATIONSHIP);
		addMethod(GET_STYLE_NUMBER);
		addMethod(GET_STYLE);
		addMethod(GET_STYLE2);
		addMethod(GET_WINDOW, "window");
		addMethod(GET_LOCATION, "location");
		addMethod(EXPORT_CONFIG_INCLUDE_EXTERNAL_RELATIONSHIPS);
		addMethod(EXPORT_CONFIG);
		addMethod(EXPORT_CONFIG2);
		addMethod(ADD_PANEL);
		addMethod(DELETE_PANEL);
		addMethod(GET_PARENT, "parent");
		addMethod(IS_VISIBLE, "visible");
		addMethod(IS_POPPED_OUT, "poppedOut");
		addMethod(IS_UNDOCKED, "undocked");
		addMethod(GET_LAYOUT, "layout");
		addMethod(GET_STYLESET, "styleset");
		addMethod(GET_CONTEXT_MENU, "contextMenu");
		addMethod(SET_DOWNSTREAM_MODE);
		addMethod(GET_DATAMODELS, "datamodels");
		addMethod(GET_DOWNSTREAM_MODE, "downstreamMode");
		addMethod(EXPORT_USER_PREFERENCE);
		addMethod(SET_TITLE);
		registerCallbackDefinition(AmiWebAbstractTablePortlet.CALLBACK_DEF_ONKEY);
		registerCallbackDefinition(AmiWebAbstractTablePortlet.CALLBACK_DEF_ONMOUSE);
		registerCallbackDefinition(AmiWebAbstractTablePortlet.CALLBACK_DEF_ONSIZE);
		registerCallbackDefinition(AmiWebAbstractTablePortlet.CALLBACK_DEF_ONVISIBLE);
	}

	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_TITLE = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getTitle", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTitle(true);
		}
		@Override
		protected String getHelp() {
			return "Returns this panel's title";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_CONTEXT_MENU = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getMenu",
			AmiWebCustomContextMenu.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getCustomContextMenu().getRootMenu();
		}
		@Override
		protected String getHelp() {
			return "Returns this panel's root custom context menu.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_ID = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getId", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject instanceof AmiWebPortlet)
				return ((AmiWebPortlet) targetObject).getAmiPanelId();
			return null;
		}
		@Override
		protected String getHelp() {
			return "Returns this panel's ID (as defined in settings).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_UID = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getUid", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPortletId();
		}
		@Override
		protected String getHelp() {
			return "Returns this panel's ami-generated ID.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_TYPE = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getType", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPanelType();
		}

		@Override
		protected String getHelp() {
			return "Returns the type of this panel, either: html, div_v, div_h, tabs, or one of the AMI visualization types.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebPortlet> CLOSE = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "close", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			if (!targetObject.isTransient())
				throw new FlowControlThrow("Can only delete transient panels: " + targetObject.getAri());
			targetObject.getService().getDesktop().deletePanel(targetObject.getPortletId(), true);
			return null;
		}
		@Override
		protected String getHelp() {
			return "Removes this transient panel. Note this will throw an error if the target panel is not transient.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> DELETE_PANEL = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "deletePanel", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			if (!targetObject.isTransient())
				throw new FlowControlThrow("Can only delete transient panels: " + targetObject.getAri());
			targetObject.getService().getDesktop().deletePanel(targetObject.getPortletId(), true);
			return null;
		}
		@Override
		protected String getHelp() {
			return "Removes this transient panel. Note this will throw an error if the target panel is not transient";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_WINDOW = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getWindow", AmiWebWindow.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Portlet p = PortletHelper.findPortletWithParentByType(targetObject, DesktopPortlet.class);
			if (p == null)
				return null;
			DesktopPortlet dp = (DesktopPortlet) p.getParent();
			Window w = dp.getWindow(p.getPortletId());
			if (w instanceof AmiWebWindow)
				return (AmiWebWindow) w;
			else
				return null;
		}
		@Override
		protected String getHelp() {
			return "Returns the window that owns this panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_LOCATION = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getLocation", WebRectangle.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			DesktopPortlet t = targetObject.getService().getDesktop().getDesktop();
			Portlet p = targetObject;
			WebRectangle r = PortletHelper.getAbsoluteLocation(p);
			r.setTop(r.getTop() - PortletHelper.getAbsoluteTop(t));
			r.setLeft(r.getLeft() - PortletHelper.getAbsoluteLeft(t));
			return r;
		}
		@Override
		protected String getHelp() {
			return "Returns a Rectangle object that stores the location of this panel relative to the upper left corner of the browser, not including the 3Forge header.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> EXPORT_CONFIG_INCLUDE_EXTERNAL_RELATIONSHIPS = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class,
			"exportConfigIncludeExternalRelationships", Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return AmiWebLayoutHelper.exportConfiguration(targetObject.getService(), targetObject.getPortletId(), true, false, true);
		}
		@Override
		protected String getHelp() {
			return "Exports this panels configuration with external relationships";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> EXPORT_CONFIG = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "exportConfig", Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return AmiWebLayoutHelper.exportConfiguration(targetObject.getService(), targetObject.getPortletId(), false, false, true);
		}
		@Override
		protected String getHelp() {
			return "Exports this panel's configuration to a map.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> EXPORT_USER_PREFERENCE = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "exportUserPreferences",
			Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return AmiWebLayoutHelper.exportUserPreferences(targetObject.getService(), targetObject.getAmiLayoutFullAlias(), targetObject.getAmiUserPrefId());
		}
		@Override
		protected String getHelp() {
			return "Exports this panel's user preferences to a map.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> EXPORT_CONFIG2 = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "exportConfig", Map.class,
			Boolean.class, Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			boolean includeRelationship = Boolean.TRUE.equals(params[0]);
			boolean includeDatamodels = Boolean.TRUE.equals(params[1]);
			return AmiWebLayoutHelper.exportConfiguration(targetObject.getService(), targetObject.getPortletId(), includeRelationship, includeDatamodels, true);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "includeRelationships", "includeDatamodels" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "include external relationships", "include external datamodels" };
		}
		@Override
		protected String getHelp() {
			return "Exports this panel's configuration to a map, with the options to include external relationships and external datamodels.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> ADD_PANEL = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "addPanelNextToMe",
			AmiWebDividerPortlet.class, String.class, Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String position = (String) params[0];
			//			if (!targetObject.isTransient())
			//				throw new FlowControlThrow("Can only add panels next to transient panels: " + targetObject.getAri());
			byte pos = AmiWebDesktopPortlet.parsePosition(position);
			if (pos == -1 || pos == AmiWebDesktopPortlet.POPOUT)
				throw new RuntimeException("Unknown position: " + pos);
			Map configuration = (Map) params[1];
			AmiWebService service = targetObject.getService();
			AmiWebManagedPortlet r = service.getDesktop().newAmiWebAmiBlankPortlet(targetObject.getAmiLayoutFullAlias(), true);
			AmiWebDividerPortlet t = service.getDesktop().addAdjacentTo(targetObject.getPortletId(), r, pos, true);
			if (configuration != null) {
				AmiWebAliasPortlet imported = (AmiWebAliasPortlet) AmiWebLayoutHelper.importConfiguration(targetObject.getService(), AmiWebUtils.deepCloneConfig(configuration),
						r.getPortletId(), true);
				targetObject.getService().getDesktop().replacePortlet(r.getPortletId(), imported);
				r.close();
				r = (AmiWebManagedPortlet) imported;
			}
			return AmiWebUtils.getParentAmiContainer(r.getParent());
		}
		@Override
		protected String getHelp() {
			return "Adds a new panel adjacent to this panel by creating a divider portlet. The divider portlet is returned.";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "position", "configuration" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Must be either:LEFT,ABOVE,RIGHT,BELOW", "if null a blank portlet is created" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_PARENT = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getParent", AmiWebPortlet.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			PortletContainer parent = targetObject.getParent();
			AmiWebAbstractContainerPortlet parentAmiContainer = AmiWebUtils.getParentAmiContainer(parent);

			// Support for popped out tab windows;
			if (parentAmiContainer == null) {
				DesktopPortlet desktop = DesktopPortlet.class.cast(PortletHelper.findPortletWithParentByType(targetObject, DesktopPortlet.class).getParent());
				if (desktop == null)
					return parentAmiContainer;
				TabPlaceholderPortlet tabPlaceholderPortlet = desktop.getTabPlaceholderPortlet(targetObject);
				if (tabPlaceholderPortlet == null)
					return parentAmiContainer;

				TabPortlet tabPortlet = tabPlaceholderPortlet.getTab().getTabPortlet();
				parentAmiContainer = AmiWebUtils.getParentAmiContainer(tabPortlet);
			}
			return parentAmiContainer;
		}
		@Override
		protected String getHelp() {
			return "Returns the parent panel of this panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> BRING_TO_FRONT = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "bringToFront", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebUtils.ensureVisibleWithDivider(targetObject, .10, false);
			return null;
		}
		@Override
		protected String getHelp() {
			return "Makes this panel visible and bring its containing window to the front.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> MINIMIZE = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "minimize", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Portlet portlet = PortletHelper.findPortletWithParentByType(targetObject, DesktopPortlet.class);
			if (portlet != null) {
				DesktopPortlet desktop = (DesktopPortlet) portlet.getParent();
				Window window = desktop.getWindow(portlet.getPortletId());
				window.minimizeWindowForce();
			}
			return null;
		}
		@Override
		protected String getHelp() {
			return "Minimizes the window, same as calling getWindow().minimize().";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_CURREBNT_RELATIONSHIP = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class,
			"getCurrentRelationship", AmiWebDmLink.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getCurrentLinkFilteringThis();
		}
		@Override
		protected String getHelp() {
			return "Returns the Relationship object currently applied to this panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_STYLE = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getStyle", String.class, false,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			if (SH.isnt(name))
				return null;
			short code = AmiWebStyleConsts.GET(name);
			if (code == AmiWebStyleConsts.MISSING_CODE)
				return null;
			Object r = targetObject.getStylePeer().resolveValue(targetObject.getStyleType(), code);
			if (r instanceof List)
				r = CH.getAtMod((List) r, 0);
			return AmiUtils.s(r);
		}
		@Override
		protected String getHelp() {
			return "Returns the value for a particular style given the style code, which can be obtained by clicking on the style name. Returns Null if key not found.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "styleKey" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the key associated with this portlet's style" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_STYLE2 = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getStyle", String.class, false,
			String.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			Number position = (Number) params[1];
			if (SH.isnt(name))
				return null;
			if (position == null)
				return null;
			short code = AmiWebStyleConsts.GET(name);
			if (code == AmiWebStyleConsts.MISSING_CODE)
				return null;
			Object r = targetObject.getStylePeer().resolveValue(targetObject.getStyleType(), code);
			if (r instanceof List)
				r = CH.getAtMod((List) r, position.intValue());
			return AmiUtils.s(r);
		}
		@Override
		protected String getHelp() {
			return "Returns the value for a particular style given the style code at the specified position. The style code can be obtained by clicking on the style name. Returns Null if key not found, returns the first value in the list if position is out of bound.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "styleKey", "index" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the key associated with this portlet's style", "If the style is a list, which element in the list to return" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_STYLE_NUMBER = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getStyleNumber", Number.class,
			false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			if (SH.isnt(name))
				return null;
			short code = AmiWebStyleConsts.GET(name);
			if (code == AmiWebStyleConsts.MISSING_CODE)
				return null;
			Object r = targetObject.getStylePeer().resolveValue(targetObject.getStyleType(), code);
			return r instanceof Number ? r : null;
		}
		@Override
		protected String getHelp() {
			return "Returns the numeric value associated with the supplied style key in this portlets style. Null if key not found, or value is not a number. Ex: using this method for text align will return null as its values are not numeric.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "styleKey" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the key associated with this portlet's style" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> SET_VALUE = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "setValue", Boolean.class, String.class,
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String name = (String) params[0];
				Object value = params[1];
				Class<?> existingType = targetObject.getPortletVarTypes().getType(name);
				if (existingType == null && value != null)
					existingType = value.getClass();
				if (value != null && existingType != null && !existingType.isInstance(value)) {
					existingType = value.getClass();
				}
				targetObject.putPortletVar(name, value, existingType);
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String getHelp() {
			return "Adds the key value pair to this portlet's attributes and return true if successful.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_VALUE = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getValue", Object.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.getPortletVars().getValue((String) params[0]);
			} catch (Exception e) {
				return null;
			}
		}
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key" };
		}
		@Override
		protected String getHelp() {
			return "Returns the value associated with key from this portlet's attributes. Returns null if key does not exist.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> CALL_COMMAND = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "callCommand", String.class,
			String.class, String.class, Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String appName = (String) params[0];
			String cmdId = (String) params[1];
			Map arguments = (Map) params[2];
			AmiWebService service = targetObject.getService();

			Collection<AmiWebCommandWrapper> cmds = service.getSystemObjectsManager().getCommandsByAppNameCmdId(appName, cmdId);
			if (cmds.size() == 0) {
				return null;
			}
			if (cmds.size() > 1) {
				if (service.getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
					service.getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_METHOD, targetObject.getAri(), null,
							"Warning more than one app or command registered to the app or command", CH.m("app", appName, "cmdId", cmdId), null));
			}

			AmiWebCommandWrapper cmd = CH.first(cmds);
			if (CH.isEmpty(arguments))
				AmiWebUtils.showRunCommandDialog((AmiWebDomObject) targetObject, service, cmd, null, null, null);
			else {
				AmiRelayRunAmiCommandRequest r = service.sendCommandToBackEnd(targetObject.getPortletId(), cmd, arguments, service.getDefaultTimeoutMs(), null, null, null);
				return r == null ? null : r.getCommandUid();
			}
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "applicationId", "cmdId", "arguments" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "applicationId", "cmdId", "arguments" };
		}

		@Override
		protected String getHelp() {
			return "Calls the given command. Returns commandUid (String) on success, otherwise null.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> CALL_COMMAND2 = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "callCommand", String.class,
			String.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String appName = (String) params[0];
			String cmdId = (String) params[1];
			Map arguments = (Map) SH.splitToMap(',', '=', '\\', (String) params[2]);
			AmiWebService service = targetObject.getService();

			Collection<AmiWebCommandWrapper> cmds = service.getSystemObjectsManager().getCommandsByAppNameCmdId(appName, cmdId);
			if (cmds.size() == 0) {
				return null;
			}
			if (cmds.size() > 1) {
				if (service.getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
					service.getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_CALLBACK, targetObject.getAri(), null,
							"Warning more than one app or command registered to the app or command", CH.m("app", appName, "cmdId", cmdId), null));
			}
			AmiWebCommandWrapper cmd = CH.first(cmds);
			if (CH.isEmpty(arguments))
				AmiWebUtils.showRunCommandDialog((AmiWebDomObject) targetObject, service, cmd, null, null, null);
			else {
				AmiRelayRunAmiCommandRequest r = service.sendCommandToBackEnd(targetObject.getPortletId(), cmd, arguments, service.getDefaultTimeoutMs(), null, null, null);
				return r == null ? null : r.getCommandUid();
			}
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "applicationId", "cmdId", "arguments" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "applicationId", "cmdId", "comma delimited list of key=value arguments" };
		}

		@Override
		protected String getHelp() {
			return "Calls the given command. Returns the commandUid (String) for this command call or null on failure.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> CALL_RELATIONSHIPS = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "callRelationship",
			Boolean.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			return targetObject.runAmiLink(name);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "relationshipName" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of Relationship" };
		}

		@Override
		protected String getHelp() {
			return "Reruns the relationship targeting this panel based on name. Returns true on success.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> CALL_RELATIONSHIP_ID = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "callRelationshipId",
			Boolean.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String id = (String) params[0];
			return targetObject.runAmiLinkId(id);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "relationshipId" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Id of Relationship" };
		}

		@Override
		protected String getHelp() {
			return "Reruns the relationship targeting this panel based on id. Returns true on success.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> CLEAR_USER_SELECTION = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "clearUserSelection",
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.clearUserSelection();
			return null;
		}
		@Override
		protected String getHelp() {
			return "Clears the user's selection.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_DATAMODELS = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getDatamodels", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject instanceof AmiWebDmPortlet) {
				Set<String> dms = ((AmiWebDmPortlet) targetObject).getUsedDmAliasDotNames();
				List<AmiWebDm> r = new ArrayList<AmiWebDm>(dms.size());
				for (String s : dms) {
					AmiWebDmsImpl t = targetObject.getService().getDmManager().getDmByAliasDotName(s);
					if (t != null)
						r.add(t);
				}
				return r;
			}
			return new ArrayList();
		}
		@Override
		protected String getHelp() {
			return "Returns a set of datamodels that were used in this panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> IS_VISIBLE = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "isVisible", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getVisible();
		}
		@Override
		protected String getHelp() {
			return "Returns true if this panel is visible.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> IS_POPPED_OUT = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "isPoppedOut", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			RootPortlet r = PortletHelper.findParentByType(targetObject, RootPortlet.class);
			return r != null && r.isPopupWindow();
		}
		@Override
		protected String getHelp() {
			return "Returns true if this panel is popped out.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> IS_UNDOCKED = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "isUndocked", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getService().getDesktop().getDesktop().isInTearout(targetObject);
		}
		@Override
		protected String getHelp() {
			return "Returns true if this panel is undocked.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_LAYOUT = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getLayout", AmiWebLayoutFile.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getService().getLayoutFilesManager().getLayoutByFullAlias(targetObject.getAmiLayoutFullAlias());
		}
		@Override
		protected String getHelp() {
			return "Returns layout that owns this panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_STYLESET = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getStyleSet", AmiWebStyle.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getStylePeer();
		}
		@Override
		protected String getHelp() {
			return "Returns the StyleSet for this panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> GET_DOWNSTREAM_MODE = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "getDownstreamMode",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject instanceof AmiWebRealtimePortlet)
				return AmiWebUtils.formatDownstreamMode(((AmiWebRealtimePortlet) targetObject).getDownstreamRealtimeMode().get());
			else
				return AmiWebUtils.formatDownstreamMode(AmiWebRealtimePortlet.DOWN_STREAM_MODE_OFF);
		}
		@Override
		protected String getHelp() {
			return "Returns status of send data downstream to realtime panels. The statuses are OFF or SELECTED_OR_ALL.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebPortlet> SET_DOWNSTREAM_MODE = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "setDownstreamMode",
			String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String mode = (String) params[0];
			if (targetObject instanceof AmiWebRealtimePortlet) {
				byte t = AmiWebUtils.parseDownstreamMode(mode);
				if (t != -1)
					((AmiWebRealtimePortlet) targetObject).setDownstreamRealtimeModeOverride(t);
				return AmiWebUtils.formatDownstreamMode(((AmiWebRealtimePortlet) targetObject).getDownstreamRealtimeMode().get());
			}
			return false;
		}
		protected String[] buildParamNames() {
			return new String[] { "mode" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "mode" };
		}
		@Override
		protected String getHelp() {
			return "Sets and returns the mode for sending data downstream to realtime panels. The modes are OFF or SELECTED_OR_ALL.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebPortlet> SET_TITLE = new AmiAbstractMemberMethod<AmiWebPortlet>(AmiWebPortlet.class, "setTitle", Boolean.class, false,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String title = (String) params[0];
			if (title == null) {
				return false;
			}

			targetObject.setAmiTitle(title, true);
			return true;

		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "title" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "title" };
		}
		@Override
		protected String getHelp() {
			return "Sets the title of the panel with the given value";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "Panel";
	}
	@Override
	public String getVarTypeDescription() {
		return "A visualization Panel. There are various subtypes including HtmlPanels, FormPanels, TablePanels, etc.";
	}
	@Override
	public Class<AmiWebPortlet> getVarType() {
		return AmiWebPortlet.class;
	}
	@Override
	public Class<AmiWebPortlet> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_Panel INSTANCE = new AmiWebScriptMemberMethods_Panel();
}
