package com.vortex.web.portlet.grids;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabListener;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.JavaInvoker;
import com.f1.utils.string.JavaInvoker.ObjectScope;
import com.f1.utils.string.Node;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.MapInMapInMap;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoGroupRequest;
import com.sso.messages.UpdateSsoGroupResponse;
import com.vortex.client.VortexClientEntitlementsManager;
import com.vortex.ssoweb.SsoService;
import com.vortex.ssoweb.SsoUserDialog;
import com.vortex.ssoweb.SsoWebGroup;
import com.vortex.web.VortexWebEyeService;

public class VortexWebEntitlementsDialogPortlet extends GridPortlet implements SsoUserDialog, FormPortletListener, TabListener, FormPortletContextMenuListener,
		FormPortletContextMenuFactory {

	final private TabPortlet tabsPortlet;
	final private FormPortlet fieldsPortlet;
	final private FormPortlet textareaPortlet;
	final private FormPortlet buttonsPortlet;
	final private ObjectToJsonConverter converter;
	final private FormPortletButton saveButton;
	final private Map<String, String> values = new TreeMap<String, String>();
	final private FormPortletTextAreaField textAreaField;
	final private Map<String, FormPortletTextField> fields = new HashMap<String, FormPortletTextField>();
	final private VortexWebEyeService service;
	final private VortexClientEntitlementsManager entitlementsManager;
	private Set<String> keys;
	private FormPortletButton helpButton;
	private boolean inTabChange;
	private SsoWebGroup group;
	private SsoUser user;
	private SsoService ssoservice;

	public VortexWebEntitlementsDialogPortlet(PortletConfig config) {
		super(config);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		this.ssoservice = (SsoService) getManager().getService(SsoService.ID);
		this.entitlementsManager = this.service.getEntitlementsManager();
		this.keys = this.entitlementsManager.getRuleKeys();
		tabsPortlet = new TabPortlet(generateConfig());
		fieldsPortlet = new FormPortlet(generateConfig());
		textareaPortlet = new FormPortlet(generateConfig());
		buttonsPortlet = new FormPortlet(generateConfig());
		tabsPortlet.addChild("Standard", fieldsPortlet);
		tabsPortlet.addChild("Free Text", textareaPortlet);
		this.textAreaField = textareaPortlet.addField(new FormPortletTextAreaField("")).setHeight(550);
		textareaPortlet.setLabelsWidth(0);
		for (String k : CH.sort(keys))
			CH.putOrThrow(fields, k, this.fieldsPortlet.addField(new FormPortletTextField(k + "=")).setWidth(FormPortletTextField.WIDTH_STRETCH));

		tabsPortlet.addTabListener(this);
		tabsPortlet.setIsCustomizable(false);
		addChild(tabsPortlet, 0, 0);
		saveButton = buttonsPortlet.addButton(new FormPortletButton("save"));
		helpButton = buttonsPortlet.addButton(new FormPortletButton("show variables"));
		this.fieldsPortlet.addMenuListener(this);
		this.fieldsPortlet.setMenuFactory(this);
		buttonsPortlet.addFormPortletListener(this);
		addChild(buttonsPortlet, 0, 1);
		setRowSize(1, 40);
		this.converter = new ObjectToJsonConverter();
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebEntitlementsDialogPortlet> {

		public static final String ID = "VortexEntitlementsPortlet";

		public Builder() {
			super(VortexWebEntitlementsDialogPortlet.class);

		}

		@Override
		public VortexWebEntitlementsDialogPortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebEntitlementsDialogPortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Edit Vortex Entitlements";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == saveButton) {
			if (this.fieldsPortlet.getVisible()) {
				if (!parseFields())
					return;
			} else {
				if (!parseTextArea())
					return;
			}
			UpdateSsoGroupRequest updateRequest = getManager().getGenerator().nw(UpdateSsoGroupRequest.class);
			updateRequest.setGroupId(this.group.getGroup().getId());
			SsoGroupAttribute attr = getManager().getGenerator().nw(SsoGroupAttribute.class);
			attr.setGroupId(updateRequest.getGroupId());
			SsoWebGroup group = ssoservice.getSsoTree().getGroup(updateRequest.getGroupId());
			if (group == null) {
				getManager().showAlert("Group not found: " + updateRequest.getGroupId());
				return;
			}
			attr.setKey("vortex_entitlements");
			attr.setType(SsoGroupAttribute.TYPE_JSON);
			attr.setValue(converter.objectToString(this.values));
			updateRequest.setGroupAttributes(CH.l(attr));
			ssoservice.sendRequestToBackend(getPortletId(), updateRequest);
		} else if (button == helpButton) {
			FastTreePortlet tree = new FastTreePortlet(generateConfig());
			MapInMapInMap<String, String, String, String> vars = this.entitlementsManager.getVariablesTree();
			for (Entry<String, MapInMap<String, String, String>> i : vars.entrySet()) {
				populateTree(tree, tree.getTreeManager().getRoot(), i.getKey(), i.getValue(), 1);
			}
			getManager().showDialog("Help", tree);
		}

	}

	private void populateTree(FastTreePortlet tree, WebTreeNode parent, String name, Object value, int depth) {
		if (value instanceof String)
			tree.createNode(name + " <B>" + value, parent, false).setIcon("portlet_icon_db_object");
		else if (value instanceof Map) {
			WebTreeNode p = tree.createNode(name, parent, false).setIcon(depth == 1 ? "portlet_icon_folder" : "portlet_icon_list");
			for (Entry<String, ?> i : ((Map<String, ?>) value).entrySet()) {
				populateTree(tree, p, i.getKey(), i.getValue(), depth + 1);
			}
		}

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(TabPortlet tabPortlet, Tab tab) {
		if (inTabChange)
			return;
		this.inTabChange = true;
		try {
			if (tabPortlet == this.tabsPortlet) {
				if (tab.getPortlet() == this.fieldsPortlet) {
					if (!parseTextArea()) {
						tabsPortlet.setActiveTab(this.textareaPortlet);
						return;
					}
				} else if (tab.getPortlet() == this.textareaPortlet) {
					if (!parseFields()) {
						tabsPortlet.setActiveTab(this.fieldsPortlet);
						return;
					}
				}
			}
		} finally {
			inTabChange = false;
		}
	}

	private boolean parseFields() {
		final Map<String, String> r = new HashMap<String, String>();
		for (String i : keys) {
			String value = fields.get(i).getValue();
			r.put(i, value);
		}
		if (!processValues(r)) {
		} else {
			this.values.putAll(r);
			populateFields();
			return true;
		}
		return false;
	}
	private boolean processValues(Map<String, String> rawValues) {
		StringBuilder errorsSink = new StringBuilder();
		for (Entry<String, String> i : rawValues.entrySet()) {
			this.entitlementsManager.parse(i.getKey(), i.getValue(), errorsSink);
			if (errorsSink.length() > 0) {
				getManager().showAlert(errorsSink.toString());
				return false;
			}
		}
		return true;
	}

	private boolean parseTextArea() {
		int lineNum = 0;
		int firstBadLine = -1;
		int errorsCount = 0;
		StringBuilder errors = new StringBuilder();
		Set<String> remainingKeys = new TreeSet<String>(keys);
		Map<String, String> keyValues = new TreeMap<String, String>();
		for (String line : SH.splitLines(this.textAreaField.getValue())) {
			String error = null;
			lineNum++;
			if (SH.isnt(line))
				continue;
			String key = SH.trim(SH.beforeFirst(line, ":=", null));
			if (key == null) {
				error = "Missing ':=' associator";
			} else if (!remainingKeys.remove(key)) {
				if (keys.contains(key))
					error = "Duplicate rule: " + key;
				else
					error = "Unknown rule: " + key;
			} else {
				String val = SH.trim(SH.afterFirst(line, ":=", null));
				keyValues.put(key, val);
			}

			if (error != null) {
				if (firstBadLine == -1)
					firstBadLine = lineNum;
				errorsCount++;
				if (errorsCount < 10)
					errors.append("At Line ").append(lineNum).append(": ").append(error).append(SH.NEWLINE);
			}
		}
		if (errorsCount == 0 && remainingKeys.size() > 0) {
			errors.append("Missing entitlments have been re-added at bottom");
			this.textAreaField.setValue(SH.trim(this.textAreaField.getValue()) + '\n' + SH.join(":=true\n", remainingKeys) + ":=true");
			firstBadLine = lineNum + 1;
			errorsCount++;
		}
		if (errors.length() > 0) {
			if (errorsCount > 10)
				errors.append("<Suppressing last ").append(errorsCount - 10).append(" error(s)>").append(SH.NEWLINE);
			this.textAreaField.moveCursor(0, firstBadLine - 1);
			getManager().showAlert(errors.toString());
		} else if (!processValues(keyValues)) {
		} else {
			this.values.putAll(keyValues);
			populateFields();
			return true;
		}
		return false;
		//for (Entry<String, String> e : keyValues.entrySet()) {
		//this.fields.get(e.getKey()).setValue(e.getValue());
		//}
		//this.textAreaField.setValue(SH.joinMap("\n", ":=", keyValues));
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		UpdateSsoGroupResponse action = (UpdateSsoGroupResponse) result.getAction();
		if (action.getOk())
			close();
		else
			getManager().showAlert(action.getMessage());
	}
	@Override
	public void setUser(SsoWebGroup group, SsoUser user) {
		this.group = group;
		this.user = user;
		Map<String, SsoGroupAttribute> attributes = group.getGroupAttributes();
		SsoGroupAttribute attribute = attributes.get("vortex_entitlements");
		Map<String, String> values = null;
		if (attribute != null) {
			try {
				values = (Map) converter.stringToObject(SH.toString(attribute.getValue()));
			} catch (Exception e) {
				getManager().showAlert("General error loading entitlements");
				values = new HashMap<String, String>();
			}
		} else
			values = new HashMap<String, String>();
		for (String e : keys) {
			String value = CH.getOr(values, e, "true");
			this.values.put(e, value);
		}
		//if (values.size() > 0)
		//getManager().showAlert("Old entitlements dropped: " + SH.join(',', values.keySet()));

		populateFields();
	}

	private void populateFields() {
		StringBuilder sb = new StringBuilder();
		for (String i : keys) {
			String value = this.values.get(i);
			sb.append(i).append(":=").append(value).append(SH.CHAR_NEWLINE);
			fields.get(i).setValue(value);
		}
		textAreaField.setValue(sb.toString());
	}

	public void parseCondition(String text) {
		JavaExpressionParser parser = new JavaExpressionParser();
		Node exp = parser.parse(text);
	}

	public static void main(String a[]) throws IOException {
		JavaExpressionParser parser = new JavaExpressionParser();
		List<String> imports = new ArrayList<String>();
		JavaInvoker inv = new JavaInvoker();
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(System.in));
		String line;

		Map<String, Object> map = CH.m("a", 15, "b", CH.m("c", 5, "d", 7));
		ObjectScope objects = new JavaInvoker.MapBackedObjectScope(map, imports);
		while ((line = lnr.readLine()) != null) {
			Node exp = parser.parse(line);
		}
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (action.startsWith("var:")) {
			node.setValue(node.getValue() + SH.stripPrefix(action, "var:", true));
		}
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		String rule = SH.stripSuffix(field.getTitle(), "=", true);//TODO: bad, use a member field map
		MapInMap<String, String, String> rules = this.entitlementsManager.getVariablesTree().get(rule);
		BasicWebMenu r = new BasicWebMenu();
		for (String cat : CH.sort(rules.keySet())) {
			Map<String, String> vars = rules.get(cat);
			BasicWebMenu sub = new BasicWebMenu(cat, true, new ArrayList<WebMenuItem>());
			r.addChild(sub);
			for (String var : CH.sort(vars.keySet())) {
				sub.addChild(new BasicWebMenuLink(var + "&nbsp;&nbsp;&nbsp;[ " + vars.get(var) + " ]", true, "var:" + cat + "." + var));
			}

		}
		return r;
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onTabRemoved(TabPortlet tabPortlet, Tab tab) {
	}

	@Override
	public void onTabAdded(TabPortlet tabPortlet, Tab tab) {
	}

	@Override
	public void onTabMoved(TabPortlet tabPortlet, int origPosition, Tab tab) {
	}
}
