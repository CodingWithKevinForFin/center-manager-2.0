package com.f1.ami.web.rt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebManagers;
import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebRealtimeObjectManager;
import com.f1.ami.web.AmiWebRealtimeProcessor;
import com.f1.ami.web.AmiWebScriptManagerForLayout;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebRealtimeProcessorWizard_Decorate extends GridPortlet implements FormPortletListener, FormPortletContextMenuListener, FormPortletContextMenuFactory {

	private FormPortletSelectField<String> leftField;
	private FormPortletTextField nameField;
	private Map<String, Row> rows = new HashMap<String, Row>();
	private FormPortlet form;
	private FormPortletButton cancelButton;
	private FormPortletButton submitButton;
	private AmiWebService service;
	private FormPortletSelectField<String> aliasField;

	public AmiWebRealtimeProcessorWizard_Decorate(PortletConfig config, Set<String> entries) {
		super(config);
		this.service = AmiWebUtils.getService(this.getManager());
		this.form = new FormPortlet(generateConfig());
		this.leftField = new FormPortletSelectField<String>(String.class, "Primary Feed");
		this.nameField = new FormPortletTextField("Processor Id");
		Set<String> names = new HashSet<String>();
		for (AmiWebRealtimeProcessor i : service.getWebManagers().getRealtimeProcessors())
			names.add(SH.afterFirst(i.getRealtimeId(), ':'));
		String name = SH.getNextId("Decorate", names, 2);
		this.nameField.setValue(name);
		this.aliasField = new FormPortletSelectField<String>(String.class, "Owning Layout");
		Set<String> t = new HashSet<String>();
		for (String s : this.service.getLayoutFilesManager().getAvailableAliasesDown(""))
			this.aliasField.addOption(s, AmiWebUtils.formatLayoutAlias(s));
		this.nameField.setLeftTopWidthHeightPx(110, 20, 200, 20);
		this.aliasField.setLeftTopWidthHeightPx(110, 50, 200, 20);
		this.leftField.setLeftTopWidthHeightPx(110, 80, 200, 20);
		this.form.addField(nameField);
		this.form.addField(aliasField);
		this.form.addField(leftField);
		int top = 110;
		this.form.addField(new FormPortletTitleField("Selects").setRightPosPx(10).setWidthPx(200).setHeightPx(20).setTopPosPx(top));
		this.form.addField(new FormPortletTitleField("Right Key").setRightPosPx(240).setWidthPx(180).setHeightPx(20).setTopPosPx(top));
		this.form.addField(new FormPortletTitleField("Left Key").setRightPosPx(440).setWidthPx(180).setHeightPx(20).setTopPosPx(top));
		this.addChild(form);
		for (String s : entries) {
			top += 25;
			this.leftField.addOption(s, s);
			Row row = new Row(s, top);
			this.rows.put(s, row);
			form.addField(row.leftIndex);
			form.addField(row.rightIndex);
			form.addField(row.selects);
		}
		form.addButton(this.cancelButton = new FormPortletButton("Cancel"));
		form.addButton(this.submitButton = new FormPortletButton("Submit"));
		form.addFormPortletListener(this);
		form.addMenuListener(this);
		form.setMenuFactory(this);
		setSuggestedSize(800, 500);
		onFieldValueChanged(this.form, this.leftField, null);
	}

	private class Row {
		FormPortletTextField leftIndex;
		FormPortletTextField rightIndex;
		FormPortletTextField selects;
		private String name;
		private AmiWebRealtimeObjectManager realtime;
		private com.f1.base.CalcTypes schema;

		Row(String name, int top) {
			this.name = name;
			this.realtime = service.getWebManagers().getAmiObjectsByType(name);
			this.schema = realtime.getRealtimeObjectsOutputSchema();
			this.leftIndex = new FormPortletTextField(name + ":");
			this.rightIndex = new FormPortletTextField("=&nbsp;");
			this.selects = new FormPortletTextField("");
			this.selects.setHasButton(true).setRightPosPx(10).setWidthPx(200).setHeightPx(20).setTopPosPx(top);
			this.rightIndex.setHasButton(true).setRightPosPx(240).setWidthPx(180).setHeightPx(20).setTopPosPx(top);
			this.leftIndex.setHasButton(true).setRightPosPx(440).setWidthPx(180).setHeightPx(20).setTopPosPx(top);
			this.leftIndex.setCorrelationData(this);
			this.rightIndex.setCorrelationData(this);
			this.selects.setCorrelationData(this);
		}

		public void disable(boolean b) {
			if (b) {
				form.removeFieldNoThrow(this.leftIndex);
				form.removeFieldNoThrow(this.rightIndex);
				form.removeFieldNoThrow(this.selects);
			} else {
				form.addFieldNoThrow(this.leftIndex);
				form.addFieldNoThrow(this.rightIndex);
				form.addFieldNoThrow(this.selects);
			}
			this.leftIndex.setLabelCssStyle(b ? "_fg=#888888" : "_fg=#000000");
		}
		public void setTopPx(int px) {
			this.leftIndex.setTopPosPx(px);
			this.rightIndex.setTopPosPx(px);
			this.selects.setTopPosPx(px);
		}
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		Row row = (Row) field.getCorrelationData();
		com.f1.base.CalcTypes object;
		if (row.leftIndex == field) {
			object = this.rows.get(leftField.getValue()).schema;
		} else
			object = row.schema;
		WebMenu r = new BasicWebMenu();
		WebMenu r2 = new BasicWebMenu("variables", true);
		for (String i : object.getVarKeys())
			r2.add(new BasicWebMenuLink(i, true, "var_" + i));
		r.add(r2);
		AmiWebMenuUtils.createOperatorsMenu(r, service, "");
		return r;
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		AmiWebService service = AmiWebUtils.getService(this.getManager());
		AmiWebMenuUtils.processContextMenuAction(service, action, node);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton)
			this.close();
		else if (button == this.submitButton) {
			String alias = aliasField.getValue();
			AmiWebScriptManagerForLayout sm = service.getScriptManager(alias);
			for (String i : this.rows.keySet()) {
				if (!SH.startsWith(i, AmiWebManagers.FEED)) {
					String adn = AmiWebUtils.ari2adn(i);
					if (!AmiWebUtils.isParentAliasOrSame(alias, adn)) {
						getManager().showAlert("Processor can not be in " + adn + " layout, because it would not have visibility to: " + i);
						return;
					}
				}

			}

			String name = SH.trim(this.nameField.getValue());
			if (!AmiUtils.isValidVariableName(name, false, false, false)) {
				getManager().showAlert("Invalid Processor name");
				return;
			}
			String adn = AmiWebUtils.getFullAlias(alias, name);
			if (this.service.getWebManagers().getAllTableTypes("").contains(AmiWebManagers.PROCESSOR + adn)) {
				getManager().showAlert("Processor name already exists");
				return;
			}
			com.f1.base.CalcTypes leftSchema = this.rows.get(leftField.getValue()).schema;
			for (Entry<String, Row> e : this.rows.entrySet()) {
				if (this.leftField.getValue().equals(e.getKey()))
					continue;
				Row row = e.getValue();
				try {
					if (SH.isnt(row.leftIndex.getValue())) {
						getManager().showAlert("Invalid formula for '" + row.name + "' ==> Left Key: required");
						return;
					}
					sm.toCalc(row.leftIndex.getValue(), leftSchema, null, null);
				} catch (Exception ex) {
					getManager().showAlert("Invalid formula for '" + row.name + "' Left Key: " + ex.getMessage(), ex);
					return;
				}

				try {
					sm.toCalc(row.rightIndex.getValue(), row.schema, null, null);
					if (SH.isnt(row.rightIndex.getValue())) {
						getManager().showAlert("Invalid formula for '" + row.name + "' ==> Right Key: required");
						return;
					}
				} catch (Exception ex) {
					getManager().showAlert("Invalid formula for '" + row.name + "' ==> Right Key: " + ex.getMessage(), ex);
					return;
				}
				String[] selects = SH.split(',', row.selects.getValue());
				if (SH.isnt(row.selects.getValue())) {
					getManager().showAlert("Invalid formula for '" + row.name + "' ==> selects clause: required");
					return;
				}

				for (int i = 0; i < selects.length; i++) {
					String key = SH.trim(SH.beforeFirst(selects[i], '='));
					String val = SH.trim(SH.afterFirst(selects[i], '='));
					if (!AmiUtils.isValidVariableName(key, false, false)) {
						getManager().showAlert("Invalid formula for '" + row.name + "' ==> Select clause '" + selects[i] + "': Invalid variable name: " + key);
						return;
					}
					try {
						sm.toCalc(val, row.schema, null, null);
					} catch (Exception ex) {
						getManager().showAlert("Invalid formula for '" + row.name + "' ==> Select clause '" + selects[i] + "': " + ex.getMessage(), ex);
						return;
					}
				}
			}
			AmiWebRealtimeProcessor_Decorate p = new AmiWebRealtimeProcessor_Decorate(service, alias);
			p.setAdn(adn);
			p.setLeft(this.rows.get(leftField.getValue()).realtime);
			for (Entry<String, Row> e : this.rows.entrySet()) {
				Row row = e.getValue();
				if (this.leftField.getValue().equals(e.getKey()))
					continue;
				Map<String, String> selects2 = new LinkedHashMap<String, String>();
				String[] selects = SH.split(',', row.selects.getValue());
				for (int i = 0; i < selects.length; i++) {
					String key = SH.trim(SH.beforeFirst(selects[i], '='));
					String val = SH.trim(SH.afterFirst(selects[i], '='));
					selects2.put(key, val);
				}
				p.addRight(p.getNextJoinId(), row.realtime, leftSchema, row.schema, row.leftIndex.getValue(), row.rightIndex.getValue(), selects2);
			}
			service.getWebManagers().addProcessor(p);
			close();
		}

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		int top = 135;
		if (field == this.leftField)
			for (Row i : this.rows.values()) {
				boolean eq = OH.eq(i.name, field.getValue());
				i.disable(eq);
				i.setTopPx(top);
				if (!eq)
					top += 25;
			}
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}

}
