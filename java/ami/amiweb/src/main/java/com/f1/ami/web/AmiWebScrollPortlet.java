package com.f1.ami.web;

import java.util.Map;
import java.util.Set;

import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Scroll;
import com.f1.base.Table;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.menu.WebMenuLinkListener;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.impl.ScrollPortlet;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;

public class AmiWebScrollPortlet extends AmiWebAbstractContainerPortlet implements AmiWebStyledPortlet {

	public static final String AMISCROLLPANE_ID = "scrollpane";
	private final AmiWebInnerScrollPortlet scrollPortlet;

	public AmiWebScrollPortlet(PortletConfig portletConfig) {
		super(portletConfig);
		this.scrollPortlet = new AmiWebInnerScrollPortlet(generateConfig(), this);
		setChild(this.scrollPortlet);
		this.getStylePeer().initStyle();
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToPortletIdMapping, StringBuilder sb) {
		super.init(configuration, origToPortletIdMapping, sb);
		this.getInnerContainer().init(configuration, origToPortletIdMapping, sb);
	}
	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.putAll(getInnerContainer().getConfiguration());

		return r;
	}
	@Override
	public void onAmiInitDone() {
		super.onAmiInitDone();
	}
	@Override
	public void populateConfigMenu(WebMenu headMenu) {
		AmiWebAbstractPortlet inner = this.getAmiInnerPanel();
		if (inner != null) {
			BasicWebMenu l = new BasicWebMenu("Inner Panel...", true);
			AmiWebDesktopPortlet.populateMenuForPortlet(inner, l);

			headMenu.add(l);

		}
	}

	@Override
	public void clearAmiData() {

	}

	@Override
	public String getPanelType() {
		return AMISCROLLPANE_ID;
	}

	@Override
	public void clearUserSelection() {

	}

	@Override
	public boolean isRealtime() {
		return false;
	}

	@Override
	public Table getSelectableRows(AmiWebDmLink link, byte type) {
		return null;
	}

	@Override
	public boolean hasSelectedRows(AmiWebDmLink link) {
		return false;
	}

	@Override
	public String getConfigMenuTitle() {
		return "Scrollpane";
	}

	@Override
	public void getUsedColors(Set<String> sink) {

	}

	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_Scroll.TYPE;
	}

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebScrollPortlet> implements AmiWebPortletContainerBuilder<AmiWebScrollPortlet> {

		public static final String ID = AMISCROLLPANE_ID;

		public Builder() {
			super(AmiWebScrollPortlet.class);
			setIcon("portlet_icon_blank"); //TODO: change icon
		}

		@Override
		public AmiWebScrollPortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebScrollPortlet r = new AmiWebScrollPortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Ami ScrollPane";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}
		@Override
		public void extractChildPorletIds(Map<String, Object> config, Map<String, Map> sink) {
			CH.putNoKeyNull(sink, CH.getOr(String.class, config, ScrollPortlet.CONFIG_CHILD, null), null);
		}

		@Override
		public boolean removePortletId(Map<String, Object> portletConfig, String amiPanelId) {
			if (OH.eq(amiPanelId, portletConfig.get(ScrollPortlet.CONFIG_CHILD)))
				portletConfig.remove(ScrollPortlet.CONFIG_CHILD);
			else
				return false;
			return true;
		}

		@Override
		public boolean replacePortletId(Map<String, Object> portletConfig, String oldPanelId, String amiPanelId) {
			if (OH.eq(oldPanelId, portletConfig.get(ScrollPortlet.CONFIG_CHILD)))
				portletConfig.put(ScrollPortlet.CONFIG_CHILD, amiPanelId);
			else
				return false;
			return true;
		}

	}

	@Override
	PortletContainer getInnerContainer() {
		return this.scrollPortlet;
	}
	public AmiWebAbstractPortlet getAmiInnerPanel() {
		Portlet p = this.scrollPortlet.getInnerPortlet();
		if (p != null && p instanceof AmiWebAbstractPortlet)
			return (AmiWebAbstractPortlet) p;
		else
			return null;
	}
	@Override
	public void onStyleValueChanged(short value, Object old, Object nuw) {
		if (value == AmiWebStyleConsts.CODE_SCROLL_WD) {
			this.scrollPortlet.setScrollBarWidth(Caster_Integer.INSTANCE.cast(nuw));
			this.scrollPortlet.flagUpdateStyle();
		} else if (value == AmiWebStyleConsts.CODE_BG_CL) {
			this.scrollPortlet.setOption(ScrollPortlet.OPTION_BG_CL, nuw);
		} else if (value == AmiWebStyleConsts.CODE_SCROLL_GRIP_CL) {
			this.scrollPortlet.setOption(ScrollPortlet.OPTION_GRIP_CL, nuw);
		} else if (value == AmiWebStyleConsts.CODE_SCROLL_TRACK_CL) {
			this.scrollPortlet.setOption(ScrollPortlet.OPTION_TRACK_CL, nuw);
		} else if (value == AmiWebStyleConsts.CODE_SCROLL_BTN_CL) {
			this.scrollPortlet.setOption(ScrollPortlet.OPTION_BTN_CL, nuw);
		} else if (value == AmiWebStyleConsts.CODE_SCROLL_ICONS_CL) {
			this.scrollPortlet.setOption(ScrollPortlet.OPTION_ICONS_CL, nuw);
		} else if (value == AmiWebStyleConsts.CODE_SCROLL_BDR_CL) {
			this.scrollPortlet.setOption(ScrollPortlet.OPTION_BDR_CL, nuw);
		} else if (value == AmiWebStyleConsts.CODE_SCROLL_BAR_CORNER_CL) {
			this.scrollPortlet.setOption(ScrollPortlet.OPTION_CORNER_CL, nuw);
		} else if (value == AmiWebStyleConsts.CODE_SCROLL_BAR_HIDE_ARROWS) {
			this.scrollPortlet.setOption(ScrollPortlet.OPTION_HIDE_AW, nuw);
		} else if (value == AmiWebStyleConsts.CODE_SCROLL_BAR_RADIUS) {
			this.scrollPortlet.setOption(ScrollPortlet.OPTION_SCR_BDR_RD, nuw);
		}
		super.onStyleValueChanged(value, old, nuw);
	}

	class AmiWebScrollInnerPanelWebMenuLinkListener implements WebMenuLinkListener {
		private final AmiWebScrollPortlet portlet;

		public AmiWebScrollInnerPanelWebMenuLinkListener(AmiWebScrollPortlet portlet) {
			this.portlet = portlet;
		}

		@Override
		public boolean onMenuItem(WebMenuLink item) {
			AmiWebAbstractPortlet innerPanel = this.portlet.getAmiInnerPanel();
			if (innerPanel != null) {
				innerPanel.onAmiContextMenu(item.getAction());
			}
			return false;
		}

	}

	class AmiWebInnerScrollPortlet extends ScrollPortlet {
		private AmiWebScrollPortlet owner;

		public AmiWebInnerScrollPortlet(PortletConfig portletConfig, AmiWebScrollPortlet owner) {
			super(portletConfig);
			this.owner = owner;
		}
		@Override
		protected Portlet configSaveIdToPortlet(Map<String, String> config, String amiPanelId) {
			return AmiWebUtils.getService(getManager()).getPortletByAliasDotPanelId(AmiWebUtils.getFullAlias(owner.getAmiLayoutFullAlias(), amiPanelId));
		}

		@Override
		protected String portletToConfigSaveId(Portlet portlet) {
			if (portlet instanceof AmiWebAliasPortlet) {
				AmiWebAliasPortlet amiWebAliasPortlet = (AmiWebAliasPortlet) portlet;
				if (!getService().getDesktop().getIsDoingExportTransient())
					amiWebAliasPortlet = amiWebAliasPortlet.getNonTransientPanel();
				return AmiWebUtils.getRelativeAlias(owner.getAmiLayoutFullAlias(), amiWebAliasPortlet.getAmiLayoutFullAliasDotId());
			}
			return null;
		}
		@Override
		public void handleCallback(String callback, Map<String, String> attributes) {
			if (SH.equals(callback, "onCustomMenu")) {
				int x = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "x");
				int y = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "y");
				WebMenu menu = this.owner.createMenu(this);
				if (menu != null) {
					getManager().showContextMenu(menu, this.owner, x, y);
				}
			} else
				super.handleCallback(callback, attributes);
		}
		@Override
		public void replaceChild(String removed, Portlet replacement) {
			// Fix bug where inner portlet doesn't have size
			Portlet toRemovedPortlet = super.getChild(removed);
			replacement.setSize(toRemovedPortlet.getWidth(), toRemovedPortlet.getHeight());
			super.replaceChild(removed, replacement);
		}
	}

	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebScrollSettingsPortlet(generateConfig(), this);
	}

}
