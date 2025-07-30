package com.f1.ami.web.form;

import java.util.Map;

import com.f1.ami.web.AmiWebFormPortletAmiScriptField;
import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.ColorPickerListener;
import com.f1.suite.web.portal.impl.ColorPickerPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextEditField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.CH;
import com.f1.utils.SH;

public class AmiWebQueryFormEditHtmlPortlet extends FormPortlet implements FormPortletListener, FormPortletContextMenuFactory, FormPortletContextMenuListener, ColorPickerListener {

	private AmiWebFormPortletAmiScriptField html;
	private FormPortletButton submitButton;
	private AmiWebQueryFormPortlet portlet;
	private FormPortletButton previewButton;
	private FormPortletButton cancelButton;
	private String originalHtml;
	private boolean previewed;

	public AmiWebQueryFormEditHtmlPortlet(AmiWebQueryFormPortlet portlet, PortletConfig config) {
		super(config);
		this.portlet = portlet;
		getFormPortletStyle().setLabelsWidth(10);
		addField(new FormPortletTitleField("HTML (Press alt-enter for preview)"));
		this.html = addField(new AmiWebFormPortletAmiScriptField("", getManager(), this.portlet.getAmiLayoutFullAlias()));
		this.html.setValue(SH.noNull(portlet.getHtmlTemplate(false)));
		this.html.setHeight(FormPortletField.HEIGHT_STRETCH);
		this.html.setCssStyle("style.fontSize=12px");
		this.originalHtml = html.getValue();
		previewButton = addButton(new FormPortletButton("Preview HTML"));
		submitButton = addButton(new FormPortletButton("Submit"));
		cancelButton = addButton(new FormPortletButton("Cancel"));
		addFormPortletListener(this);
		setMenuFactory(this);
		addMenuListener(this);
	}
	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return 700;
	}
	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 850;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton bttn) {
		if (bttn == submitButton) {
			this.portlet.setDialogHeight(this.getHeight());
			this.portlet.setDialogWidth(this.getWidth());
			int positionLeft = PortletHelper.getAbsoluteLeft(this);
			int positionTop = PortletHelper.getAbsoluteTop(this);
			this.portlet.setDialogLeft(positionLeft);
			this.portlet.setDialogTop(positionTop);
			Exception e = this.portlet.getHtmlFormula().testFormula(this.html.getValue());
			if (e != null) {
				getManager().showAlert(e.getMessage(), e);
				return;
			}
			this.portlet.setHtmlTemplate(this.html.getValue(), false);
			this.portlet.updateFieldStyles();
			close();
		} else if (bttn == previewButton) {
			this.portlet.setHtmlTemplate(this.html.getValue(), true);
			this.portlet.updateFieldStyles();
			previewed = true;
		} else if (bttn == cancelButton) {
			if (previewed) {
				this.portlet.setHtmlTemplate(this.originalHtml, false);
			}
			close();
		}

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (keycode == 13 && mask != 0) {
			this.portlet.setHtmlTemplate(this.html.getValue(), true);
			this.portlet.updateFieldStyles();
			previewed = true;
		}
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		if (field == this.html) {
			BasicWebMenu inputs = new BasicWebMenu("Insert Field", true);
			for (QueryField<?> md : this.portlet.fieldsById.values()) {
				if (md.getField().isVisible())
					continue;
				String text = md.getField().getTitle();
				inputs.add(new BasicWebMenuLink(text, true, "add_input_" + cursorPosition + "_" + md.getName()));
			}
			if (inputs.getChildrenCount() == 0)
				inputs.setEnabled(false);

			WebMenu variables = new BasicWebMenu("Variables", portlet.getUsedDm() != null);
			if (portlet.getUsedDm() != null) {
				boolean hasSpecial = false;
				for (String i : CH.sort(portlet.getUsedDm().getClassTypes().getVarKeys())) {
					if (AmiWebUtils.isReservedVar(i))
						hasSpecial = true;
					else
						variables.add(new BasicWebMenuLink(i, true, "var_" + AmiWebUtils.toValidVarname(cursorPosition + "_" + i)).setAutoclose(true));
				}
				if (hasSpecial) {
					variables.add(new BasicWebMenuDivider());
					for (String i : CH.sort(portlet.getUsedDm().getClassTypes().getVarKeys())) {
						if (AmiWebUtils.isReservedVar(i)) {
							String name = AmiWebUtils.RESERVED_PARAMS.get(i);
							variables.add(new BasicWebMenuLink(name == null ? i : i + " (" + name + ")", true, "var_" + AmiWebUtils.toValidVarname(cursorPosition + "_" + i))
									.setAutoclose(true));
						}
					}
				}
			}

			BasicWebMenuLink customColor = new BasicWebMenuLink("Custom color...", true, "color_" + cursorPosition);

			BasicWebMenu r = new BasicWebMenu();
			r.add(new BasicWebMenuLink("Embed javascript call to onAmiJsCallback(...)", true, "add_amijscallback_" + cursorPosition));

			r.add(inputs);
			r.add(variables);
			r.add(new BasicWebMenuDivider());
			r.add(customColor);
			AmiWebMenuUtils.createOperatorsMenu(r, portlet.getService(), this.portlet.getAmiLayoutFullAlias());
			AmiWebMenuUtils.createMemberMethodMenu(r, portlet.getService(), this.portlet.getAmiLayoutFullAlias());
			return r;
		}
		return null;
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField field) {
		if (field == this.html) {
			if (action.startsWith("add_amijscallback_")) {
				int position = SH.parseInt(SH.afterLast(action, '_'));
				html.insertTextNoThrow(position, "amiJsCallback(this,\"SampleAction\")");
			} else if (action.startsWith("add_input_")) {
				String t = SH.stripPrefix(action, "add_input_", true);
				String text = "${inputs." + SH.afterFirst(t, '_') + "}";
				int position = SH.parseInt(SH.beforeFirst(t, '_'));
				html.insertTextNoThrow(position, text);
			} else if (action.startsWith("add_var_")) {
				String t = SH.stripPrefix(action, "add_var_", true);
				String text = "{" + SH.afterFirst(t, '_') + ";format=\"html\"}";
				int position = SH.parseInt(SH.beforeFirst(t, '_'));
				html.insertTextNoThrow(position, text);
			} else if (action.startsWith("add_")) {
				String fid = SH.stripPrefix(action, "add_", true);
				this.portlet.addFieldMenuAction(fid);
			} else if (action.startsWith("color_")) {
				AmiWebMenuUtils.showCustomColorChooser((FormPortletTextEditField) field);
			} else if (action.startsWith("var_")) {
				String txt = SH.stripPrefix(action, "var_", true);
				txt = SH.trim('`', '`', txt);
				int pos = Integer.parseInt(SH.beforeFirst(txt, "_"));
				String var = SH.afterFirst(txt, "_");
				html.insertTextNoThrow(pos, "${" + var + "}");
			} else {
				AmiWebMenuUtils.processContextMenuAction(this.portlet.getService(), action, html);
			}
		}
	}

	@Override
	public void onColorChanged(ColorPickerPortlet target, String oldColor, String nuwColor) {
	}

	@Override
	public void onOkayPressed(ColorPickerPortlet target) {
		String color = target.getColor();
		if (color == null) {
			target.close();
			return;
		}
		Integer position = (Integer) target.getCorrelationData();
		html.insertTextNoThrow(position, color);
		target.close();
	}

	@Override
	public void onCancelPressed(ColorPickerPortlet target) {
		target.close();
	}
	public AmiWebQueryFormEditHtmlPortlet hideCloseButtons(boolean hide) {
		clearButtons();
		addButton(this.previewButton);
		if (!hide) {
			addButton(this.submitButton);
			addButton(this.cancelButton);
		}
		return this;
	}

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (KeyEvent.ESCAPE.equals(keyEvent.getKey()))
			return true;
		return super.onUserKeyEvent(keyEvent);
	}
}
