package com.f1.suite.web.portal.impl;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletBuilder;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.visual.GraphPortlet;
import com.f1.suite.web.portal.impl.visual.TreemapPortlet;
import com.f1.utils.SH;

public abstract class AbstractPortletBuilder<T extends Portlet> implements PortletBuilder<T> {

	private String path[];
	private Class<T> type;
	private String icon;
	private boolean isUserCreatable = true;

	public AbstractPortletBuilder(Class<T> type) {
		setPath(SH.afterLast(getClass().getPackage().getName(), '.'));
		this.type = type;
		if (FastTablePortlet.class.isAssignableFrom(type))
			this.icon = "portlet_icon_table";
		else if (FastTreePortlet.class.isAssignableFrom(type))
			this.icon = "portlet_icon_tree";
		else if (FormPortlet.class.isAssignableFrom(type))
			this.icon = "portlet_icon_form";
		else if (TreemapPortlet.class.isAssignableFrom(type))
			this.icon = "portlet_icon_treemap";
		else if (GraphPortlet.class.isAssignableFrom(type))
			this.icon = "portlet_icon_graph";
		else
			this.icon = "portlet_icon_window";
	}
	@Override
	public String[] getPath() {
		return path;
	}

	public AbstractPortletBuilder<T> setPath(String path) {
		this.path = SH.split('.', path);
		return this;
	}
	@Override
	public Class<T> getPortletType() {
		return type;
	}

	@Override
	public String getIcon() {
		return icon;
	}

	public AbstractPortletBuilder<T> setIcon(String icon) {
		this.icon = icon;
		return this;
	}

	@Override
	public boolean getIsUserCreatable() {
		return isUserCreatable;
	}
	public AbstractPortletBuilder<T> setUserCreatable(boolean isUserCreatable) {
		this.isUserCreatable = isUserCreatable;
		return this;
	}
}
