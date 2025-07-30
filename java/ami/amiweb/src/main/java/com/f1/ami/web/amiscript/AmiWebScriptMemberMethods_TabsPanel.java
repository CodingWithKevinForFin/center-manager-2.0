package com.f1.ami.web.amiscript;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebAliasPortlet;
import com.f1.ami.web.AmiWebBlankPortlet;
import com.f1.ami.web.AmiWebLayoutHelper;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebTabEntry;
import com.f1.ami.web.AmiWebTabPortlet;
import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_TabsPanel extends AmiWebScriptBaseMemberMethods<AmiWebTabPortlet> {

	private AmiWebScriptMemberMethods_TabsPanel() {
		super();
		addMethod(GET_TABS_COUNT);
		addMethod(GET_TAB_AT);
		addMethod(GET_TAB_FOR);
		addMethod(ADD_TAB);
		addMethod(GET_SELECTED_TAB, "selectedTab");
		addMethod(SET_SELECTED_TAB);
		addCustomDebugProperty("tabs", List.class);
		registerCallbackDefinition(AmiWebTabPortlet.CALLBACK_DEF_ONCLICK);
	}

	@Override
	protected Object getCustomDebugProperty(String name, AmiWebTabPortlet value) {
		if ("tabs".equals(value.getPanelType())) {
			int c = value.getTabsCount();
			ArrayList<AmiWebTabEntry> tabs = new ArrayList<AmiWebTabEntry>(c);
			for (int i = 0; i < c; i++)
				tabs.add(value.getTabAt(i));
			return tabs;
		}
		return super.getCustomDebugProperty(name, value);
	}

	private static final AmiAbstractMemberMethod<AmiWebTabPortlet> GET_TABS_COUNT = new AmiAbstractMemberMethod<AmiWebTabPortlet>(AmiWebTabPortlet.class, "getTabsCount",
			Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getInnerContainer().getTabsCount();
		}

		@Override
		protected String getHelp() {
			return "Returns the number of tabs.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	private static final AmiAbstractMemberMethod<AmiWebTabPortlet> GET_TAB_AT = new AmiAbstractMemberMethod<AmiWebTabPortlet>(AmiWebTabPortlet.class, "getTabAt",
			AmiWebTabEntry.class, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			final Integer index = (Integer) params[0];
			return index != null && OH.isBetweenExcluding(index, 0, targetObject.getTabsCount()) ? targetObject.getTabAt(index) : null;
		}

		protected String[] buildParamNames() {
			return new String[] { "position" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Zero indexed position of Tab to get" };
		}
		@Override
		protected String getHelp() {
			return "Returns the Tab at a zero-indexed location, null if out of bounds.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTabPortlet> GET_TAB_FOR = new AmiAbstractMemberMethod<AmiWebTabPortlet>(AmiWebTabPortlet.class, "getTabFor",
			AmiWebTabEntry.class, AmiWebPortlet.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			final AmiWebPortlet portlet = (AmiWebPortlet) params[0];
			return targetObject.getTabFor(portlet);
		}

		protected String[] buildParamNames() {
			return new String[] { "panelId" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "" };
		}
		@Override
		protected String getHelp() {
			return "Returns the Tab in the specified panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTabPortlet> ADD_TAB = new AmiAbstractMemberMethod<AmiWebTabPortlet>(AmiWebTabPortlet.class, "addTab", AmiWebTabEntry.class,
			Integer.class, String.class, Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer position = (Integer) params[0];
			if (position == null)
				position = 0;
			if (position >= targetObject.getTabsCount())
				position = targetObject.getTabsCount();
			final String title = (String) params[1];
			final Map configuration = (Map) params[2];
			AmiWebService service = targetObject.getService();
			AmiWebBlankPortlet bp = service.getDesktop().newAmiWebAmiBlankPortlet(targetObject.getAmiLayoutFullAlias(), true);
			bp.setTransient(true);
			Tab r = targetObject.getInnerContainer().addChild(position, title, bp, false);
			if (configuration != null) {
				AmiWebAliasPortlet imported = (AmiWebAliasPortlet) AmiWebLayoutHelper.importConfiguration(targetObject.getService(), AmiWebUtils.deepCloneConfig(configuration),
						bp.getPortletId(), true);
				service.getDesktop().replacePortlet(bp.getPortletId(), imported);
				bp.close();
			}
			return targetObject.getTabFor(r);

		}

		protected String[] buildParamNames() {
			return new String[] { "position", "title", "configuration" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Zero indexed position of where to add Tab", "name of Tab", "configuration of the new child Tab" };
		}
		@Override
		protected String getHelp() {
			return "Adds a Tab at the specified position and with the specified configurations.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTabPortlet> GET_SELECTED_TAB = new AmiAbstractMemberMethod<AmiWebTabPortlet>(AmiWebTabPortlet.class, "getSelectedTab",
			AmiWebTabEntry.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSelectedTab();
		}

		@Override
		protected String getHelp() {
			return "Returns the Tab that is currently visible, or null if there are no tabs.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTabPortlet> SET_SELECTED_TAB = new AmiAbstractMemberMethod<AmiWebTabPortlet>(AmiWebTabPortlet.class, "setSelectedTab",
			Boolean.class, AmiWebTabEntry.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebTabEntry entry = (AmiWebTabEntry) params[0];
			if (entry != null && entry.getOwner() != targetObject)
				return false;
			targetObject.getInnerContainer().selectTab(entry == null ? -1 : entry.getTab().getLocation());
			Tab t = targetObject.getInnerContainer().getSelectedTab();
			return true;
		}
		protected String[] buildParamNames() {
			return new String[] { "Tab" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Tab, which must belong to this TabsPanel" };
		}

		@Override
		protected String getHelp() {
			return "Sets the Tab that is currently visible, or null if no Tab is selected.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	@Override
	public String getVarTypeName() {
		return "TabsPanel";
	}

	@Override
	public String getVarTypeDescription() {
		return "A Panel of Tabs for containing sub panels)";
	}
	@Override
	public Class<AmiWebTabPortlet> getVarType() {
		return AmiWebTabPortlet.class;
	}
	@Override
	public Class<AmiWebTabPortlet> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_TabsPanel INSTANCE = new AmiWebScriptMemberMethods_TabsPanel();
}
