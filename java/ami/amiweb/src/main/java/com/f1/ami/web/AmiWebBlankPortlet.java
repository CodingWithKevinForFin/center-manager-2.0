package com.f1.ami.web;

import java.util.Map;
import java.util.Set;

import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.base.Table;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.HtmlPortletListener;
import com.f1.suite.web.portal.impl.WebHtmlContextMenuFactory;

public class AmiWebBlankPortlet extends AmiWebAbstractPortlet {

	protected HtmlPortlet inner;

	public AmiWebBlankPortlet(PortletConfig manager) {
		super(manager);
		this.inner = new HtmlPortlet(getManager().generateConfig(), "", "ami_blank");
		this.setChild(inner);
	}

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebBlankPortlet> {

		public static final String ID = "amiblank";

		public Builder() {
			super(AmiWebBlankPortlet.class);
			setIcon("portlet_icon_blank");
		}

		@Override
		public AmiWebBlankPortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebBlankPortlet r = new AmiWebBlankPortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Blank Panel";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
	}

	@Override
	public Map<String, Object> getConfiguration() {
		return super.getConfiguration();
	}

	@Override
	public void populateConfigMenu(WebMenu headMenu) {
		headMenu.add(new BasicWebMenuLink("Create Table / Visualization / Form", true, "choose_data_st").setBackgroundImage(AmiWebConsts.ICON_VIZ_ORANGE));
		headMenu.add(new BasicWebMenuLink("Create Realtime Table / Visualization", true, "choose_data_rt").setBackgroundImage(AmiWebConsts.ICON_VIZ_ORANGE));
		headMenu.add(new BasicWebMenuLink("Create HTML Panel", true, "add_form").setBackgroundImage(AmiWebConsts.ICON_FORM_ORANGE));
		headMenu.add(new BasicWebMenuLink("Create Filter", true, "add_filter").setBackgroundImage(AmiWebConsts.ICON_FILTER_ORANGE));
		headMenu.add(new BasicWebMenuLink("Split Vertically", true, "split_v").setBackgroundImage(AmiWebConsts.ICON_SPLIT_VERT));
		headMenu.add(new BasicWebMenuLink("Split Horizontally", true, "split_h").setBackgroundImage(AmiWebConsts.ICON_SPLIT_HORIZ));
		BasicWebMenu linksMenu = new BasicWebMenu("Link To Panel", true);
		for (AmiWebLayoutFile child : getService().getLayoutFilesManager().getLayout().getChildrenRecursive(true))
			for (AmiWebPortletDef c : child.getRootHiddenPanels().values())
				if (AmiWebInnerDesktopPortlet.Builder.ID.equals(c.getBuilderId()))
					populate(linksMenu, child, c.getAmiPanelId(), -1);//-1, skips desktop
				else
					populate(linksMenu, child, c.getAmiPanelId(), 0);
		// if there is nothing to link, then disable the menu
		if (linksMenu.getChildren().isEmpty())
			linksMenu.setEnabled(false);
		headMenu.add(linksMenu);
		headMenu.add(new BasicWebMenuLink("Import", true, "import").setBackgroundImage(AmiWebConsts.ICON_IMPORT));

	}

	@Override
	public String getConfigMenuTitle() {
		return "Blank Panel";
	}

	@Override
	public boolean onAmiContextMenu(String id) {
		AmiWebDesktopPortlet d = getService().getDesktop();
		if ("split_v".equals(id)) {
			final AmiWebAliasPortlet removed = this;
			final String alias = removed.getAmiParent().getAmiLayoutFullAlias();
			final PortletContainer parent = removed.getParent();
			final AmiWebDividerPortlet newChild = d.newDividerPortlet(generateConfig(), true, d.newAmiWebAmiBlankPortlet(alias), removed, alias, false);
			parent.replaceChild(removed.getPortletId(), newChild);
			getManager().onPortletAdded(newChild);
			newChild.setDefaultOffsetPctToCurrent();
			d.showDividerEditor(newChild);
			return true;
		} else if ("split_h".equals(id)) {
			final AmiWebAliasPortlet removed = this;
			final String alias = removed.getAmiParent().getAmiLayoutFullAlias();
			final PortletContainer parent = removed.getParent();
			final AmiWebDividerPortlet newChild = d.newDividerPortlet(generateConfig(), false, d.newAmiWebAmiBlankPortlet(alias), removed, alias, false);
			parent.replaceChild(removed.getPortletId(), newChild);
			getManager().onPortletAdded(newChild);
			newChild.setDefaultOffsetPctToCurrent();
			d.showDividerEditor(newChild);
			return true;
		} else if ("choose_data_rt".equals(id)) {
			d.showAddRealtimePanelPortlet(this.getPortletId(), this.getAmiLayoutFullAlias());
			return true;
		} else if ("choose_data_st".equals(id)) {
			d.showAddPanelPortlet(this.getPortletId());
			return true;
		} else if ("add_form".equals(id)) {
			AmiWebQueryFormPortlet newPortlet = (AmiWebQueryFormPortlet) d.newPortlet(AmiWebQueryFormPortlet.Builder.ID, d.getAmiLayoutFullAliasForPortletId(this.getPortletId()));
			newPortlet.setSnapSize(20);
			d.replacePortlet(this.getPortletId(), newPortlet);
			return true;
		} else if ("add_filter".equals(id)) {
			d.createFilterPortlet(this.getPortletId());
			return true;
		} else if ("import".equals(id)) {
			AmiWebLayoutManager lm = getService().getLayoutManager();
			AmiWebLayoutHelper.ImportPortlet fp = new AmiWebLayoutHelper.ImportPortlet(lm, getManager().generateConfig(), null, this);
			getManager().showDialog("Import configuration to blank Portlet", fp, lm.getDialogWidth(), lm.getDialogHeight()).setStyle(getService().getUserDialogStyleManager());
			return true;
		} else
			return super.onAmiContextMenu(id);
	}
	@Override
	public boolean getIsFreeFloatingPortlet() {
		return true;
	}

	@Override
	public void getUsedColors(Set<String> sink) {
	}

	@Override
	public String getStyleType() {
		return "panel";
	}

	@Override
	public void clearAmiData() {
	}

	@Override
	public String getPanelType() {
		return "blank";
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

	public void addMenuContextListener(HtmlPortletListener listener) {
		inner.addListener(listener);
	}

	public void setContextMenuFactory(WebHtmlContextMenuFactory mf) {
		inner.setContextMenuFactory(mf);
	}

	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebBlankSettingsPortlet(generateConfig(), this);
	}

}
