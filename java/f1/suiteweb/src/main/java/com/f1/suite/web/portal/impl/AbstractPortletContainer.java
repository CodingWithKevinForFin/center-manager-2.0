/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_String;

public abstract class AbstractPortletContainer extends AbstractPortlet implements PortletContainer {

	final private Map<String, Portlet> children = new HashMap<String, Portlet>();
	final private Map<String, Portlet> umChildren = Collections.unmodifiableMap(children);

	public AbstractPortletContainer(PortletConfig config) {
		super(config);
	}

	@Override
	public Portlet getChild(String id) {
		return children.get(id);
	}

	@Override
	public void addChild(Portlet child) {
		CH.putOrThrow(children, child.getPortletId(), child);
		child.setParent(this);
		if (getVisible())
			layoutChildren();
	}
	@Override
	public Portlet removeChild(String childId) {
		Portlet r = CH.removeOrThrow(children, childId);
		if (r.getParent() == this)
			r.setParent(null);
		if (getVisible())
			makeChildVisible(r, false);
		return r;
	}

	protected void makeChildrenVisible(boolean visible) {
		for (Portlet child : children.values())
			makeChildVisible(child, visible);
	}

	protected void makeChildVisible(Portlet child, boolean visible) {
		if (!getVisible() && visible)
			throw new IllegalStateException();
		if (visible == child.getVisible())
			return;
		if (visible) {
			child.setVisible(true);
			callJsAddChild(child);
		} else {
			callJsRemoveChild(child);
			child.setVisible(false);
		}
	}
	protected void callJsAddChild(Portlet p) {
		if (p instanceof RootPortlet)
			return;
		callJsFunction("addChild").addParamQuoted(p.getPortletId()).end();
	}

	protected void callJsRemoveChild(Portlet p) {
		if (p instanceof RootPortlet)
			return;
		callJsFunction("removeChild").addParamQuoted(p.getPortletId()).end();
	}

	abstract protected void layoutChildren();

	@Override
	public void setSize(int width, int height) {
		if (this.getWidth() == width && this.getHeight() == height)
			return;
		super.setSize(width, height);
		if (getVisible())
			layoutChildren();
	}

	@Override
	final public void setVisible(boolean visible) {
		if (visible != getVisible()) {
			if (visible) {
				super.setVisible(visible);
				layoutChildren();
			} else {
				makeChildrenVisible(false);
				super.setVisible(visible);
			}
		}
	}

	@Override
	final public void resetVisibility() {
		super.resetVisibility();
		for (Portlet child : children.values())
			child.resetVisibility();

	}

	@Override
	public void replaceChild(String removed, Portlet replacement) {
		removeChild(removed);
		addChild(replacement);
	}
	@Override
	public void onClosed() {
		super.onClosed();
		if (getChildrenCount() > 0)
			for (Portlet child : new ArrayList<Portlet>(getChildren().values()))
				child.close();
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("addChild".equals(callback)) {
			final String childType = CH.getOrThrow(Caster_String.INSTANCE, attributes, "childType");
			Portlet newChild = getManager().buildPortlet(childType);
			boolean replaced = false;
			for (Portlet child : children.values()) {
				if (child instanceof BlankPortlet) {
					replaceChild(child.getPortletId(), newChild);
					replaced = true;
				}
			}
			if (!replaced)
				addChild(newChild);
		} else if ("showAddPortletDialog".equals(callback)) {
			PortletBuilderPortlet pbp = new PortletBuilderPortlet(generateConfig(), false);
			pbp.setPortletIdOfParentToAddPortletTo(getPortletId());
			getManager().showDialog("Add Portlet", pbp);
		} else if ("deleteChild".equals(callback)) {
			final String childPortletId = CH.getOrThrow(Caster_String.INSTANCE, attributes, "childPortletId");
			onUserDeleteChild(childPortletId);
		} else if ("wrapChild".equals(callback)) {
			final String childType = CH.getOrThrow(Caster_String.INSTANCE, attributes, "childType");
			Portlet newChild = getManager().buildPortlet(childType);
			final String childPortletId = CH.getOrThrow(Caster_String.INSTANCE, attributes, "childPortletId");
			onUserWrapChild(childPortletId, (PortletContainer) newChild);
		} else
			super.handleCallback(callback, attributes);
	}

	private void onUserWrapChild(String childPortletId, PortletContainer newChild) {
		Portlet removed = getChild(childPortletId);
		replaceChild(childPortletId, newChild);
		newChild.addChild(removed);
	}

	protected void onUserDeleteChild(String childPortletId) {
		Portlet removed = getChild(childPortletId);
		replaceChild(childPortletId, getManager().buildPortlet(BlankPortlet.Builder.ID));
		removed.onClosed();
	}

	@Override
	public Map<String, Portlet> getChildren() {
		return umChildren;
	}

	@Override
	public int getChildrenCount() {
		return children.size();
	}
	public void removeAllChildren() {
		if (children.isEmpty())
			return;
		for (String id : CH.l(children.keySet())) {
			removeChild(id);
		}
	}
	public void removeAndCloseAllChildren() {
		if (children.isEmpty())
			return;
		for (String id : CH.l(children.keySet())) {
			Portlet removed = removeChild(id);
			if (removed != null)
				removed.close();
		}

	}

}
