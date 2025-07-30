package com.f1.suite.web.portal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.utils.OH;
import com.f1.utils.WebRectangle;

public class PortletHelper {

	public static <T> void findPortletsByType(Portlet container, Class<T> type, Collection<T> sink) {
		if (type.isInstance(container))
			sink.add((T) container);
		if (container instanceof PortletContainer)
			for (Portlet child : ((PortletContainer) container).getChildren().values())
				findPortletsByType(child, type, sink);
	}
	public static Portlet findPortletWithParentByType(Portlet portlet, Class<? extends Portlet> type) {
		while (portlet != null) {
			PortletContainer parent = portlet.getParent();
			if (type.isInstance(parent))
				return portlet;
			portlet = parent;
		}
		return null;
	}

	public static <T> Collection<T> findPortletsByType(Portlet container, Class<T> type) {
		Collection<T> sink = new ArrayList<T>();
		findPortletsByType(container, type, sink);
		return sink;
	}

	public static <T> T findParentByType(Portlet portlet, Class<T> type) {
		while (portlet != null)
			if (type.isInstance(portlet))
				return type.cast(portlet);
			else
				portlet = portlet.getParent();
		return null;
	}

	public static boolean isParentOfChild(Portlet candidateParent, Portlet child) {
		if (candidateParent instanceof PortletContainer)
			while (child != null)
				if (child == candidateParent)
					return true;
				else
					child = child.getParent();
		else
			return candidateParent == child;
		return false;
	}

	public static void ensureVisible(Portlet p) {
		if (p instanceof RootPortlet && !((RootPortlet) p).isPopupWindow())
			return;
		PortletContainer parent = p.getParent();
		if (parent == null)
			throw new RuntimeException("missing parent: " + p);
		ensureVisible(parent);
		parent.bringToFront(p.getPortletId());
	}

	public static Portlet findCommonParent(Portlet p1, Portlet p2) {
		Set<String> ids = new HashSet<String>();
		while (p1 != null) {
			ids.add(p1.getPortletId());
			p1 = p1.getParent();
		}
		while (p2 != null && !ids.contains(p2.getPortletId()))
			p2 = p2.getParent();
		return p2;
	}

	public static PortletContainer findFirstVisibleParent(Portlet portlet) {
		for (PortletContainer parent = portlet.getParent(); parent != null; parent = parent.getParent())
			if (parent.getVisible())
				return parent;
		return null;
	}
	public static int getAbsoluteLeft(Portlet portlet) {
		int r = 0;
		for (;;) {
			PortletContainer parent = portlet.getParent();
			if (parent == null || parent instanceof RootPortlet)
				return r;
			r += parent.getChildOffsetX(portlet.getPortletId());
			portlet = parent;
		}
	}
	public static int getAbsoluteTop(Portlet portlet) {
		int r = 0;
		for (;;) {
			PortletContainer parent = portlet.getParent();
			if (parent == null || parent instanceof RootPortlet)
				return r;
			r += parent.getChildOffsetY(portlet.getPortletId());
			portlet = parent;
		}
	}
	public static Set<String> getNestedPortletIds(Portlet portlet) {
		return getNestedPortletIds(portlet, new HashSet<String>());
	}

	public static Set<String> getNestedPortletIds(Portlet portlet, Set<String> sink) {
		sink.add(portlet.getPortletId());
		if (portlet instanceof PortletContainer)
			for (Portlet child : ((PortletContainer) portlet).getChildren().values())
				getNestedPortletIds(child, sink);
		return sink;
	}
	public static String createJsCopyToClipboard(CharSequence text) {
		StringBuilder sb = new StringBuilder();
		new JsFunction(sb, null, "copyToClipboard").addParamQuoted(text).end();
		return sb.toString();

	}
	public static void getColors(String text, Set<String> sink) {
		if (text == null)
			return;
		int length = text.length();
		for (int i = text.indexOf('#'); i != -1 && i + 6 < length; i = text.indexOf('#', i + 1)) {
			for (int j = 1; j < 8; j++) {
				char c = i + j == length ? 0 : text.charAt(i + j);
				boolean isChar = OH.isBetween(c, '0', '9') || OH.isBetween(c, 'a', 'f') || OH.isBetween(c, 'A', 'F');
				if (j == 7) {
					if (!isChar)
						sink.add(text.substring(i, i + 7).toLowerCase());
				} else if (!isChar)
					break;
			}
		}
	}
	public static long getWindowId(Portlet p) {
		for (;;)
			if (p instanceof RootPortlet)
				return ((RootPortlet) p).getWindowId();
			else
				p = p.getParent();
	}
	public static void swap(Portlet a, Portlet b) {
		PortletContainer aParent = OH.assertNotNull(a.getParent());
		PortletContainer bParent = OH.assertNotNull(b.getParent());
		Portlet tmp = ((BasicPortletManager) a.getManager()).getTmpPortlet();
		aParent.replaceChild(a.getPortletId(), tmp);
		bParent.replaceChild(b.getPortletId(), a);
		aParent.replaceChild(tmp.getPortletId(), b);
	}
	public static Map<String, Object> menuToJson(PortletManager manager, WebMenuItem menu) {
		return manager.getMenuManager().setActiveMenuAndGenerateJson(menu);
	}
	public static WebRectangle getAbsoluteLocation(Portlet p) {
		int left = getAbsoluteLeft(p);
		int top = getAbsoluteTop(p);
		return new WebRectangle(left, top, p.getWidth(), p.getHeight());
	}
}
