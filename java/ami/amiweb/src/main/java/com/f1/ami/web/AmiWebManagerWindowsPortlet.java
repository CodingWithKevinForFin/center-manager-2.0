package com.f1.ami.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.CH;
import com.f1.utils.SH;

public class AmiWebManagerWindowsPortlet extends GridPortlet implements FormPortletListener {

	public static final String HIDDEN = "HIDDEN";
	public static final String REGULAR = "REGULAR";
	public static final String MAXIMIZED_NO_HEADER = "MAXIMIZED_NO_HEADER";
	public static final char TYPE_MAXIMIZED = 'M';
	public static final char TYPE_REGULAR = 'R';
	public static final char TYPE_HIDDEN = 'H';
	private DesktopPortlet desktop;
	private AmiWebDesktopPortlet amiDesktop;
	private FormPortlet form;
	private FormPortletButton cancelButton;
	private FormPortletButton applyButton;
	private List<FormPortletSelectField<Character>> selectFields = new ArrayList<FormPortletSelectField<Character>>();

	public AmiWebManagerWindowsPortlet(PortletConfig config, AmiWebDesktopPortlet desktop) {
		super(config);
		this.amiDesktop = desktop;
		this.desktop = this.amiDesktop.getInnerDesktop();
		this.form = new FormPortlet(generateConfig());
		addChild(this.form);

		this.form.addField(new FormPortletTitleField("Window Type"));
		Map<String, Window> windows = new HashMap<String, DesktopPortlet.Window>();
		for (Window window : this.desktop.getWindows()) {
			if (window.getPortlet() instanceof AmiWebSpecialPortlet)
				continue;
			if (this.amiDesktop.getService().getAmiQueryFormEditorsManager().isEditor(window.getPortlet()))
				continue;
			windows.put(window.getName(), window);
		}
		for (String name : CH.sort(windows.keySet(), SH.COMPARATOR_CASEINSENSITIVE_STRING)) {
			Window window = windows.get(name);
			FormPortletSelectField<Character> select = new FormPortletSelectField<Character>(Character.class, window.getName());
			select.setWidth(160);
			select.setLabelPaddingPx(5);
			select.addOption(TYPE_MAXIMIZED, "Maximized, No Header");
			select.addOption(TYPE_REGULAR, "Regular");
			select.addOption(TYPE_HIDDEN, "Hidden");
			select.setCorrelationData(window);
			this.selectFields.add(select);
			select.setValue(getType(window, false));
			this.form.addField(select);
			setSuggestedSize(400, 500);
		}

		this.form.addFormPortletListener(this);
		applyButton = this.form.addButton(new FormPortletButton("Apply"));
		cancelButton = this.form.addButton(new FormPortletButton("Cancel"));
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton)
			close();
		else if (button == this.applyButton) {
			for (FormPortletSelectField<Character> i : this.selectFields) {
				Window w = (Window) i.getCorrelationData();
				applyType(i.getValue(), w, true);
			}
			close();
		}
	}
	public static void applyType(char value, Window w, boolean applyToDefault) {
		switch (value) {
			case TYPE_MAXIMIZED:
				w.setIsHidden(false, applyToDefault);
				w.setAllowFloat(false, applyToDefault);
				w.setAllowMax(true, applyToDefault);
				w.setAllowMin(false, applyToDefault);
				w.setCloseable(false, applyToDefault);
				w.setHasHeader(false, applyToDefault);
				if (w.isWindowFloating())
					w.maximizeWindow();
				break;
			case TYPE_REGULAR:
				w.setIsHidden(false, applyToDefault);
				w.setAllowFloat(true, applyToDefault);
				w.setAllowMax(true, applyToDefault);
				w.setAllowMin(true, applyToDefault);
				w.setCloseable(true, applyToDefault);
				w.setHasHeader(true, applyToDefault);
				break;
			case TYPE_HIDDEN:
				w.setIsHidden(true, applyToDefault);
				w.setAllowMax(true, applyToDefault);
				w.setAllowMin(true, applyToDefault);
				w.setCloseable(true, applyToDefault);
				w.setHasHeader(true, applyToDefault);
				w.setAllowFloat(true, applyToDefault);
		}
		if (applyToDefault) {
			w.setDefaultLocationToCurrent();
			w.setDefaultStateToCurrent();
			w.setDefaultZIndexToCurrent();
		}

	}
	public static char getType(Window window, boolean current) {
		if (window.isHidden(current))
			return TYPE_HIDDEN;
		else if (window.getHasHeader(current))
			return TYPE_REGULAR;
		else
			return TYPE_MAXIMIZED;
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}
	public static String formatType(char type) {
		switch (type) {
			case TYPE_MAXIMIZED:
				return MAXIMIZED_NO_HEADER;
			case TYPE_REGULAR:
				return REGULAR;
			case TYPE_HIDDEN:
				return HIDDEN;
			default:
				return "UNKNOWN TYPE:" + type;
		}
	}

	public static char parseType(String type, char dflt) {
		if (MAXIMIZED_NO_HEADER.contentEquals(type))
			return TYPE_MAXIMIZED;
		if (REGULAR.contentEquals(type))
			return TYPE_REGULAR;
		if (HIDDEN.contentEquals(type))
			return TYPE_HIDDEN;
		return dflt;
	}

}
