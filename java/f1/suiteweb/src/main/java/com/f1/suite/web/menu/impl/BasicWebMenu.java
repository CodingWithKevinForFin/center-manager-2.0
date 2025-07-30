/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.menu.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class BasicWebMenu extends AbstractWebMenuItem implements WebMenu {

	public static final Comparator<? super WebMenuItem> TEXT_COMPARATOR = new TextComparator();

	private List<WebMenuItem> children;

	public BasicWebMenu(WebMenuItem... children) {
		super("", true);
		for (WebMenuItem i : children)
			i.setParent(this);
		this.children = CH.l(children);
	}
	public BasicWebMenu(String text, boolean enabled, WebMenuItem... children) {
		super(text, enabled);
		for (WebMenuItem i : children)
			i.setParent(this);
		this.children = CH.l(children);
	}
	public BasicWebMenu(String text, boolean enabled, List<? extends WebMenuItem> children) {
		super(text, enabled);
		for (WebMenuItem i : children)
			i.setParent(this);
		this.children = (List<WebMenuItem>) children;
	}

	@Override
	public List<WebMenuItem> getChildren() {
		return this.children;
	}

	public void addChild(WebMenuItem item) {
		this.add(item);
	}

	@Override
	public void add(WebMenuItem link) {
		link.setParent(this);
		children.add(link);
	}

	public WebMenuItem removeChildByAction(String action) {
		for (WebMenuItem c : getChildren()) {
			if (c instanceof BasicWebMenuLink && OH.eq(action, (((BasicWebMenuLink) c).getAction()))) {
				c.setParent(null);
				getChildren().remove(c);
				return c;
			}
		}
		return null;
	}

	public void sort() {
		sort(TEXT_COMPARATOR);
	}

	public void sort(Comparator<? super WebMenuItem> c) {
		//sort children first so that they can figure out what their priority is
		for (WebMenuItem m : children) {
			if (m instanceof WebMenu)
				((WebMenu) m).sort(c);

			if (m.getPriority() >= 0)
				setPriority(Math.min(this.getPriority() < 0 ? Integer.MAX_VALUE : this.getPriority(), m.getPriority()));
		}

		Collections.sort(this.children, c);
	}

	private final static class TextComparator implements Comparator<WebMenuItem> {
		@Override
		public int compare(WebMenuItem o1, WebMenuItem o2) {
			if (o1.getPriority() >= 0 && o2.getPriority() >= 0)
				return OH.compare(o1.getPriority(), o2.getPriority());

			if (o1.getPriority() >= 0 && o2.getPriority() < 0)
				return -1;

			if (o2.getPriority() >= 0 && o1.getPriority() < 0)
				return 1;

			//if priority is not set compare by text
			return SH.COMPARATOR_CASEINSENSITIVE_STRING.compare(o1.getText(), o2.getText());
		}
	}

	@Override
	public int getChildrenCount() {
		return this.children.size();
	}
	@Override
	public void add(int position, WebMenuItem menuItem) {
		menuItem.setParent(this);
		this.children.add(position, menuItem);
	}
	@Override
	public String toString() {
		return "BasicWebMenu [children=" + children + "]";
	}
	@Override
	public BasicWebMenu setBackgroundImage(String backgroundImage) {
		super.setBackgroundImage(backgroundImage);
		return this;
	}

}
