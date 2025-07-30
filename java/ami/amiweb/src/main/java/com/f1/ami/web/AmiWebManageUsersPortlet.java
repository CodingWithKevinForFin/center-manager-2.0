package com.f1.ami.web;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiCommonProperties;
import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiEncrypter;
import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.web.cloud.AmiWebCloudLayoutTree;
import com.f1.ami.web.cloud.AmiWebCloudManager;
import com.f1.ami.web.headless.AmiWebHeadlessManager;
import com.f1.base.Row;
import com.f1.base.TableList;
import com.f1.base.TableListenable;
import com.f1.container.ContainerTools;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletMultiCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.style.PortletStyleManager;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.utils.CachedFile;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebManageUsersPortlet extends GridPortlet implements WebContextMenuFactory, WebContextMenuListener, FormPortletListener, ConfirmDialogListener {

	private static final String OFF = "off";
	private static final String DEFAULT_AUTH = "com.f1.ami.web.auth.AmiAuthenticatorFileBacked";

	private static final String ACTION_ADD_USER = "user_add";
	private static final String ACTION_EDIT_USER = "user_edit";
	private static final String ACTION_CHANGE_PASSWORD = "user_change_password";
	private static final String ACTION_DELETE_USER = "delete_user";
	private static final String ACTION_COPY_USER = "copy_user";
	private static final String ACTION_REFRESH_USER = "refresh_user";

	private static final Logger log = LH.get();
	private static final String COLUMN_USERNAME = "User";
	private static final String COLUMN_ISADMIN = "Is Admin";
	private static final String COLUMN_ISDEV = "Is Dev";
	private static final String COLUMN_DEFAULT_LAYOUT = "Default Layout";
	private static final String COLUMN_LAYOUTS = "Layouts";
	private static final String COLUMN_PERMISSIONS = "Permissions";
	private static final String COLUMN_ISLOGGEDIN = "Active";
	private static final String COLUMN_SESSIONS = "Sessions";
	private static final String COLUMN_OTHERS = "Others";

	private Map<String, String> passwordStore = new HashMap<String, String>();
	private FastTablePortlet fastTable;
	private TableListenable basic;
	private FormPortletButton addUserButton;
	private FormPortletButton editUserButton;
	private FormPortletButton deleteUserButton;
	private FormPortletButton copyUserButton;
	private FormPortletButton importButton;
	private FormPortletButton exportButton;
	private ContainerTools properties;
	final private AmiWebCloudManager cloudManager;
	private BasicWebColumn viewSessionsColumn;
	private AmiWebAdminToolPortlet parent;

	public AmiWebManageUsersPortlet(PortletConfig config, ContainerTools properties, AmiWebCloudManager amiWebCloudManager, AmiWebAdminToolPortlet parent) {
		super(config);
		this.properties = properties;
		this.cloudManager = amiWebCloudManager;
		this.parent = parent;

		AmiWebHeaderPortlet header = new AmiWebHeaderPortlet(generateConfig());
		header.setShowSearch(false);
		String info = checkPlugins();
		header.updateBlurbPortletLayout("Manage AMI Users", info);
		header.setShowLegend(false);
		header.setInformationHeaderHeight(80);
		header.getBarFormPortlet().addFormPortletListener(this);
		this.addUserButton = header.getBarFormPortlet().addButton(new FormPortletButton("Add").setCssStyle("_bg=#b3e5fc|_fg=#000000"));
		this.editUserButton = header.getBarFormPortlet().addButton(new FormPortletButton("Edit").setCssStyle("_bg=#b3e5fc|_fg=#000000"));
		this.deleteUserButton = header.getBarFormPortlet().addButton(new FormPortletButton("Delete").setCssStyle("_bg=#b3e5fc|_fg=#000000"));
		this.copyUserButton = header.getBarFormPortlet().addButton(new FormPortletButton("Copy").setCssStyle("_bg=#b3e5fc|_fg=#000000"));
		this.importButton = header.getBarFormPortlet().addButton(new FormPortletButton("Import").setCssStyle("_bg=#ffd359|_fg=#000000"));
		this.exportButton = header.getBarFormPortlet().addButton(new FormPortletButton("Export").setCssStyle("_bg=#ffd359|_fg=#000000"));
		header.updateBarPortletLayout(this.addUserButton.getHtmlLayoutSignature());
		addChild(header, 0, 0);
		TableListenable basicTable = new BasicTable(new String[] { COLUMN_USERNAME, COLUMN_ISADMIN, COLUMN_ISDEV, COLUMN_DEFAULT_LAYOUT, COLUMN_LAYOUTS, COLUMN_PERMISSIONS,
				COLUMN_OTHERS, COLUMN_ISLOGGEDIN, COLUMN_SESSIONS });
		BasicWebCellFormatter formatter = new BasicWebCellFormatter();
		this.basic = basicTable;
		this.fastTable = new FastTablePortlet(generateConfig(), this.basic, "Users");
		BasicWebColumn userNameColumn = this.fastTable.getTable().addColumn(true, COLUMN_USERNAME, COLUMN_USERNAME, formatter).setCssColumn("bold").setWidth(90);
		this.fastTable.getTable().sortRows(userNameColumn.getColumnId(), true, true, false);
		this.fastTable.getTable().addColumn(true, COLUMN_ISADMIN, COLUMN_ISADMIN, formatter).setWidth(50);
		this.fastTable.getTable().addColumn(true, COLUMN_ISDEV, COLUMN_ISDEV, formatter).setWidth(50);
		this.fastTable.getTable().addColumn(true, COLUMN_DEFAULT_LAYOUT, COLUMN_DEFAULT_LAYOUT, formatter).setWidth(150);
		this.fastTable.getTable().addColumn(true, COLUMN_LAYOUTS, COLUMN_LAYOUTS, formatter).setWidth(200);
		this.fastTable.getTable().addColumn(true, COLUMN_PERMISSIONS, COLUMN_PERMISSIONS, formatter).setWidth(205);
		this.fastTable.getTable().addColumn(true, COLUMN_OTHERS, COLUMN_OTHERS, formatter).setWidth(50);
		this.fastTable.getTable().addColumn(true, COLUMN_ISLOGGEDIN, COLUMN_ISLOGGEDIN, formatter).setWidth(50);
		this.viewSessionsColumn = this.fastTable.getTable().addColumn(true, COLUMN_SESSIONS, COLUMN_SESSIONS, formatter).setWidth(80).setIsClickable(true)
				.setCssColumn("manage_users_view_sessions");

		PortletStyleManager styleManager = getManager().getStyleManager();
		this.fastTable.setDialogStyle(styleManager.getDialogStyle());
		this.fastTable.setFormStyle(getManager().getStyleManager().getFormStyle());
		AmiWebUtils.applyEndUserTableStyle(this.fastTable);

		parseAccessText();

		addChild(this.fastTable, 0, 1);
		this.fastTable.getTable().setMenuFactory(this);
		this.fastTable.getTable().addMenuListener(this);
	}

	private String checkPlugins() {
		String entitlementMode = properties.getOptional(AmiCommonProperties.PROPERTY_USERS_ACCESS_FILE_FOR_ENTITLEMENTS, OFF);
		if (OFF.equalsIgnoreCase(entitlementMode)) {
			String authPlugin = properties.getRequired(AmiWebProperties.PROPERTY_AMI_WEB_AUTH_PLUGIN_CLASS);
			String ssoPlugin = properties.getOptional(AmiWebProperties.PROPERTY_SSO_PLUGIN_CLASS, "");
			if (SH.isnt(ssoPlugin)) //check for SAML Plugin if SSO plugin doesn't exist
				ssoPlugin = properties.getOptional(AmiWebProperties.PROPERTY_SAML_PLUGIN_CLASS, "");
			if (!DEFAULT_AUTH.equals(authPlugin))
				return "<span style=\"color:#FFFF00\">WARN</span>: Custom Auth Plugin: \"" + authPlugin + "\" in use. Set \""
						+ AmiCommonProperties.PROPERTY_USERS_ACCESS_FILE_FOR_ENTITLEMENTS
						+ "=on/required/force/required_force\" in your properties file for changes here to apply to users.";
			else if (SH.is(ssoPlugin))
				return "<span style=\"color:#FFFF00\">WARN</span>: Custom SSO/SAML plugin: \"" + ssoPlugin + "\" in use. Set \""
						+ AmiCommonProperties.PROPERTY_USERS_ACCESS_FILE_FOR_ENTITLEMENTS
						+ "=on/required/force/required_force\" in your properties file for changes here to apply to users.";
		}
		return "";
	}

	private void onUpdate() {
		String encryptMode = properties.getOptionalEnum(AmiCommonProperties.PROPERTY_USERS_ACCESS_FILE_ENCRYPT, "off", "password");
		StringBuilder newAccess = new StringBuilder();
		newAccess.append("#Each line represents a user, Syntax is: USERNAME|PASSWORD|Key1=Value1|Key2=Value2|....\n");
		for (Row r : this.fastTable.getTable().getRows()) {
			newAccess.append(r.get(COLUMN_USERNAME));
			if ("password".equals(encryptMode)) {
				AmiEncrypter encrypter = properties.getServices().getService(AmiConsts.SERVICE_ENCRYPTER, AmiEncrypter.class);
				String plainPass = (String) r.get(COLUMN_USERNAME);
				String encryptedPass = encrypter.encrypt(passwordStore.get(plainPass));
				newAccess.append("|").append(encryptedPass);
			}
			else
				newAccess.append("|").append(passwordStore.get(SH.toString(r.get(COLUMN_USERNAME))));
			newAccess.append("|").append("ISADMIN=" + SH.toString(r.get(COLUMN_ISADMIN)));
			newAccess.append("|").append("ISDEV=" + SH.toString(r.get(COLUMN_ISDEV)));
			if (r.get(COLUMN_DEFAULT_LAYOUT) != null && !SH.isEmpty(SH.toString(r.get(COLUMN_DEFAULT_LAYOUT))))
				newAccess.append("|").append("DEFAULT_LAYOUT=" + r.get(COLUMN_DEFAULT_LAYOUT));
			if (r.get(COLUMN_LAYOUTS) != null && !SH.isEmpty(SH.toString(r.get(COLUMN_LAYOUTS))))
				newAccess.append("|").append("LAYOUTS=" + r.get(COLUMN_LAYOUTS));
			if (r.get(COLUMN_PERMISSIONS) != null && !SH.isEmpty(SH.toString(r.get(COLUMN_PERMISSIONS))))
				newAccess.append("|").append("AMIDB_PERMISSIONS=" + r.get(COLUMN_PERMISSIONS));
			if (r.get(COLUMN_OTHERS) != null && !SH.isEmpty(SH.toString(r.get(COLUMN_OTHERS))))
				newAccess.append("|").append(r.get(COLUMN_OTHERS));
			newAccess.append("\n");
		}
		writeToFile(newAccess.toString());
		getManager().showAlert("User update success");
	}

	private void writeToFile(String text) {
		File path = this.properties.getOptional(AmiCommonProperties.PROPERTY_USERS_ACCESS_FILE, new File("data/access.txt"));
		try {
			IOH.ensureDir(path.getParentFile());
			IOH.writeText(path, text);
			LH.info(log, "Updated " + IOH.getFullPath(path));
		} catch (IOException e) {
			throw new RuntimeException("Error writing to access file: " + IOH.getFullPath(path), e);
		}
	}

	private void setPassword(String username, String password) {
		passwordStore.put(username, password);
	}

	public Row isUser(String username) {
		TableList t = this.basic.getRows();
		for (Row r : t) {
			if (SH.equals((String)r.get(COLUMN_USERNAME), username))
				return r;
		}
		return null;
	}

	private String getAccessText() {
		File path = properties.getOptional(AmiCommonProperties.PROPERTY_USERS_ACCESS_FILE, new File("data/access.txt"));
		String text = "";
		CachedFile file = new CachedFile(path, 1000);
		if (path.isFile())
			text = file.getData().getText();
		return text;
	}

	private void parseAccessText() {

		List<String> activeUsers = getManager().getTools().getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class).getActiveUsers();

		String text = getAccessText();

		for (String line : SH.splitLines(text)) {
			if (SH.isnt(line) || line.startsWith("#"))
				continue;
			String[] parts = SH.splitWithEscape('|', '\\', line);
			if (parts.length < 2)
				continue;
			final String un = parts[0];
			final String pw = parts[1];
			if (SH.isnt(un))
				continue;
			Object[] row = newEmptyRow();
			putNoFire(row, COLUMN_USERNAME, un);
			String _isAdmin = "false";
			String _isDev = "false";
			String _defaultLayout = "";
			String _layouts = "";
			String _permissions = "";
			String _others = "";
			for (int i = 2; i < parts.length; ++i) {
				final String part = parts[i];
				if (SH.isnt(part))
					continue;
				final String key = SH.beforeFirst(part, '=', null);
				final String val = SH.afterFirst(part, '=', null);
				if (key == null)
					_others += "|" + part;
				else if (key.equals("ISADMIN"))
					_isAdmin = val;
				else if (key.equals("ISDEV"))
					_isDev = val;
				else if (key.equals("DEFAULT_LAYOUT"))
					_defaultLayout = val;
				else if (key.equals("LAYOUTS"))
					_layouts = val;
				else if (key.equals("AMIDB_PERMISSIONS"))
					_permissions = val;
				else
					_others += "|" + part;
			}
			if (!_others.isEmpty())
				_others = _others.substring(1, _others.length());
			putNoFire(row, COLUMN_ISADMIN, _isAdmin);
			putNoFire(row, COLUMN_ISDEV, _isDev);
			putNoFire(row, COLUMN_DEFAULT_LAYOUT, _defaultLayout);
			putNoFire(row, COLUMN_LAYOUTS, _layouts);
			putNoFire(row, COLUMN_PERMISSIONS, _permissions);
			putNoFire(row, COLUMN_ISLOGGEDIN, activeUsers.contains(un));
			putNoFire(row, COLUMN_SESSIONS, "View All");
			putNoFire(row, COLUMN_OTHERS, _others);
			setPassword(un, pw);
			this.fastTable.addRow(row);
		}
	}

	private void removeUser(String username) {
		Row r = this.isUser(username);
		if (r != null)
			this.fastTable.removeRow(r);
	}

	private Object[] newEmptyRow() {
		return new Object[basic.getColumnsCount()];
	}

	private void putNoFire(Object[] r, String id, Object value) {
		r[basic.getColumn(id).getLocation()] = value;
	}

	private void refresh() {
		this.fastTable.clearRows();
		parseAccessText();
	}

	private void deletePrompt(List<Row> selected) {
		String deletePrompt = "Delete user(s): ";
		for (Row r : selected)
			deletePrompt += r.get(COLUMN_USERNAME) + ", ";
		deletePrompt = deletePrompt.substring(0, deletePrompt.lastIndexOf(", "));
		deletePrompt += ". Are you sure?";
		ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), deletePrompt, ConfirmDialogPortlet.TYPE_OK_CANCEL, this);
		getManager().showDialog("Delete User", cdp);
		cdp.setCallback("DELETE");
		cdp.setCorrelationData(selected);
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		BasicWebMenu menu = new BasicWebMenu();
		menu.add(new BasicWebMenuLink("Add User", true, ACTION_ADD_USER));
		if (this.fastTable.getTable().getSelectedRows().size() > 0) {
			menu.add(new BasicWebMenuLink("Delete User(s)", true, ACTION_DELETE_USER));
			if (this.fastTable.getTable().getSelectedRows().size() == 1) {
				menu.add(new BasicWebMenuLink("Edit User", true, ACTION_EDIT_USER));
				menu.add(new BasicWebMenuLink("Change Password", true, ACTION_CHANGE_PASSWORD));
				menu.add(new BasicWebMenuLink("Copy User", true, ACTION_COPY_USER));
			}
		}
		menu.add(new BasicWebMenuLink("Refresh", true, ACTION_REFRESH_USER));
		return menu;
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		List<Row> selected = this.fastTable.getTable().getSelectedRows();
		Row selectedRow = selected.size() > 0 ? selected.get(0) : null;

		if (SH.equals(action, ACTION_ADD_USER))
			getManager().showDialog("Add User", new UserConfigPortlet(generateConfig(), selectedRow, false, false, false), 700, 520);
		else if (SH.equals(action, ACTION_EDIT_USER))
			getManager().showDialog("Edit User", new UserConfigPortlet(generateConfig(), selectedRow, true, false, false), 700, 520);
		else if (SH.equals(action, ACTION_CHANGE_PASSWORD))
			getManager().showDialog("Change Password", new UserConfigPortlet(generateConfig(), selectedRow, false, false, true), 600, 300);
		else if (SH.equals(action, ACTION_DELETE_USER))
			deletePrompt(selected);
		else if (SH.equals(action, ACTION_COPY_USER))
			getManager().showDialog("Copy User", new UserConfigPortlet(generateConfig(), selectedRow, false, true, false), 700, 520);
		else if (SH.equals(action, ACTION_REFRESH_USER))
			refresh();
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (ConfirmDialogPortlet.ID_YES.equals(id)) {
			if ("DELETE".equals(source.getCallback())) {
				Object _source = source.getCorrelationData();
				List<?> selected = (List<?>) _source;
				for (Object row : selected) {
					if (row instanceof Row) {
						this.fastTable.removeRow((Row) row);
					}
				}
				onUpdate();
			}
		}
		return true;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		List<Row> selected = this.fastTable.getTable().getSelectedRows();
		Row selectedRow = selected.size() > 0 ? selected.get(0) : null;

		if (button == this.addUserButton)
			getManager().showDialog("Add User", new UserConfigPortlet(generateConfig(), null, false, false, false), 700, 520);
		else if (button == this.importButton) {
			getManager().showDialog("Import User Access", new ViewAccessPortlet(generateConfig(), null, false), 1200, 700);
		} else if (button == this.exportButton) {
			getManager().showDialog("Export User Acess", new ViewAccessPortlet(generateConfig(), getAccessText(), true), 1200, 700);
		} else if (selectedRow != null) {
			if (button == this.editUserButton)
				getManager().showDialog("Edit User", new UserConfigPortlet(generateConfig(), selectedRow, true, false, false), 700, 520);
			else if (button == this.deleteUserButton) {
				deletePrompt(selected);
			} else if (button == this.copyUserButton)
				getManager().showDialog("Copy User", new UserConfigPortlet(generateConfig(), selectedRow, false, true, false), 700, 520);
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		if (col == this.viewSessionsColumn) {
			String username = (String) (row.get(COLUMN_USERNAME));
			this.parent.changeSessionTab(username);
		}
	}

	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {
	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
	}

	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}

	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}

	public class UserConfigPortlet extends GridPortlet implements FormPortletListener {

		final private FormPortlet form;
		final private FormPortletTextField nameField;
		final private FormPortletTextField passwordField;
		final private FormPortletCheckboxField isAdminField;
		final private FormPortletCheckboxField isDevField;
		final private FormPortletMultiCheckboxField<String> permissionsField;
		final private FormPortletTextAreaField layoutsField;
		final private FormPortletSelectField<String> defaultLayoutField;
		final private FormPortletTextAreaField othersField;
		final private FormPortletButton cxlButton;
		final private FormPortletButton okButton;
		final private boolean isEdit, isChangePassword;

		public UserConfigPortlet(PortletConfig config, Row user, boolean isEdit, boolean isCopy, boolean isChangePassword) {
			super(config);
			this.isEdit = isEdit;
			this.isChangePassword = isChangePassword;
			AmiWebHeaderPortlet header = new AmiWebHeaderPortlet(generateConfig());
			header.setShowSearch(false);
			String blurbTitle;
			if (this.isEdit)
				blurbTitle = "Edit ";
			else if (isCopy)
				blurbTitle = "Copy ";
			else if (isChangePassword)
				blurbTitle = "Change Password";
			else
				blurbTitle = "Add ";
			if (isChangePassword)
				header.updateBlurbPortletLayout(blurbTitle, "");
			else
				header.updateBlurbPortletLayout(blurbTitle + "AMI User", "");
			header.setShowLegend(false);
			header.setInformationHeaderHeight(60);
			header.setShowBar(false);
			addChild(header, 0, 0);
			addChild(form = new FormPortlet(generateConfig()), 0, 1);
			form.addField(nameField = new FormPortletTextField("User Name:"));
			if (isChangePassword) {
				nameField.setValue((String) user.get(COLUMN_USERNAME));
				nameField.setDisabled(true);
				form.addField(passwordField = new FormPortletTextField("New Password: ").setPassword(true));
				permissionsField = null;
				isAdminField = null;
				isDevField = null;
				othersField = null;
				layoutsField = null;
				defaultLayoutField = null;
			} else {
				if (this.isEdit) {
					nameField.setDisabled(true);
					passwordField = null;
				} else
					form.addField(passwordField = new FormPortletTextField("Password: ").setPassword(true));
				form.addField(isAdminField = new FormPortletCheckboxField("Is Admin:"));
				form.addField(isDevField = new FormPortletCheckboxField("Is Dev:"));
				form.addField(permissionsField = new FormPortletMultiCheckboxField<String>(String.class, "Permissions:")).setWidth(250);
				for (String p : AmiWebAdminToolPortlet.validPermissions)
					permissionsField.addOption(p, p);
				form.addField(layoutsField = new FormPortletTextAreaField("Permissible Layouts:")).setHeight(100);
				form.addField(defaultLayoutField = new FormPortletSelectField<String>(String.class, "Default Layout:"));
				AmiWebCloudLayoutTree layouts = AmiWebManageUsersPortlet.this.cloudManager.getCloudLayouts();
				addLayouts("", layouts);
				form.addField(othersField = new FormPortletTextAreaField("Others:")).setHeight(100);
				if (this.isEdit || isCopy) {
					if (isEdit)
						nameField.setValue((String) user.get(COLUMN_USERNAME));
					isAdminField.setValue("true".equals(user.get(COLUMN_ISADMIN)));
					isDevField.setValue("true".equals(user.get(COLUMN_ISDEV)));
					layoutsField.setValue(SH.replaceAll(((String) user.get(COLUMN_LAYOUTS)), ',', '\n'));
					othersField.setValue(SH.replaceAll(((String) user.get(COLUMN_OTHERS)), '|', '\n'));
					Set<String> permissions = new HashSet<String>(Arrays.asList(((String) user.get(COLUMN_PERMISSIONS)).split(",")));
					permissions = AmiWebAdminToolPortlet.verifyPermissions(permissions);
					permissionsField.setValue(permissions);
					String userDefaultLayout = (String) user.get(COLUMN_DEFAULT_LAYOUT);
					defaultLayoutField.addOptionNoThrow(userDefaultLayout, userDefaultLayout);
					defaultLayoutField.setValue(userDefaultLayout);
				} else
					defaultLayoutField.setValue("");
			}
			form.addFormPortletListener(this);
			this.okButton = form.addButton(new FormPortletButton("Apply"));
			this.cxlButton = form.addButton(new FormPortletButton("Cancel"));
		}

		private void addLayouts(String string, AmiWebCloudLayoutTree layouts) {
			for (Entry<String, String> i : layouts.getLayoutNamesAndId().entrySet())
				this.defaultLayoutField.addOption(i.getValue(), i.getValue());
			for (Entry<String, AmiWebCloudLayoutTree> i : layouts.getChildren().entrySet())
				addLayouts(layouts.getName(), i.getValue());
			this.defaultLayoutField.addOption("", "");
		}

		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
			if (button == this.cxlButton)
				close();
			else if (button == okButton) {
				String username = nameField.getValue();
				String password = passwordField != null ? passwordField.getValue() : null;
				if (verifyUser(username, password)) {
					if (this.isChangePassword) {
						AmiWebManageUsersPortlet.this.setPassword(username, password);
						AmiWebManageUsersPortlet.this.onUpdate();
					} else {
						if (this.isEdit)
							AmiWebManageUsersPortlet.this.removeUser(username);
						else
							AmiWebManageUsersPortlet.this.setPassword(username, password);
						Object[] row = AmiWebManageUsersPortlet.this.newEmptyRow();
						AmiWebManageUsersPortlet.this.putNoFire(row, COLUMN_USERNAME, username);
						AmiWebManageUsersPortlet.this.putNoFire(row, COLUMN_ISADMIN, SH.toString(isAdminField.getValue() == null ? false : isAdminField.getValue()));
						AmiWebManageUsersPortlet.this.putNoFire(row, COLUMN_ISDEV, SH.toString(isDevField.getValue() == null ? false : isDevField.getValue()));
						AmiWebManageUsersPortlet.this.putNoFire(row, COLUMN_DEFAULT_LAYOUT, defaultLayoutField.getValue());
						AmiWebManageUsersPortlet.this.putNoFire(row, COLUMN_LAYOUTS, layoutsField.getValue() != null ? SH.replaceAll(layoutsField.getValue(), '\n', ',') : null);
						AmiWebManageUsersPortlet.this.putNoFire(row, COLUMN_PERMISSIONS, AmiWebAdminToolPortlet.permissionStringBuilder(permissionsField.getValue()));
						AmiWebManageUsersPortlet.this.putNoFire(row, COLUMN_ISLOGGEDIN, false);
						AmiWebManageUsersPortlet.this.putNoFire(row, COLUMN_SESSIONS, "View All");
						AmiWebManageUsersPortlet.this.putNoFire(row, COLUMN_OTHERS, othersField.getValue() != null ? SH.replaceAll(othersField.getValue(), '\n', '|') : null);
						AmiWebManageUsersPortlet.this.fastTable.addRow(row);
						AmiWebManageUsersPortlet.this.onUpdate();
						AmiWebManageUsersPortlet.this.refresh();
					}
					close();
				}
			}
		}

		private boolean verifyUser(String user, String password) {
			if (user == null || user.length() == 0) {
				getManager().showAlert("Please input a valid username");
				return false;
			}
			if ((!isEdit) && (password == null || password.length() == 0)) {
				getManager().showAlert("Please input a password");
				return false;
			}
			if ((isChangePassword || isEdit) && AmiWebManageUsersPortlet.this.isUser(user) == null) {
				getManager().showAlert("User: \"" + user + "\" not found");
				return false;
			}
			if (!isEdit && !isChangePassword && AmiWebManageUsersPortlet.this.isUser(user) != null) {
				getManager().showAlert("User already exists: " + user);
				return false;
			}
			return true;
		}

		@Override
		public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		}

		@Override
		public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		}
	}

	public class ViewAccessPortlet extends GridPortlet implements FormPortletListener {
		private FormPortlet form;
		private FormPortletButton closeButton;
		private FormPortletButton importButton;
		private FormPortletTextAreaField textAreaField;

		public ViewAccessPortlet(PortletConfig config, String accessText, Boolean isExport) {
			super(config);
			this.form = new FormPortlet(generateConfig());
			FormPortletTitleField titleField = this.form.addField(new FormPortletTitleField(""));
			this.textAreaField = form.addField(new FormPortletTextAreaField(""));
			textAreaField.setLeftPosPx(5);
			textAreaField.setTopPosPx(30);
			textAreaField.setBottomPosPx(50);
			textAreaField.setRightPosPx(5);
			if (isExport)
				textAreaField.setValue(accessText);
			titleField.setTopPosPx(5);
			titleField.setHeightPx(25);
			titleField.setLeftPosPx(5);
			titleField.setWidthPx(600);
			if (isExport)
				titleField.setValue("Select and copy the text below into your clip board using Ctrl+C");
			else
				titleField.setValue("Enter your user configuration in the text area below");
			this.form.addFormPortletListener(this);
			if (!isExport) {
				this.closeButton = new FormPortletButton("Cancel");
				this.importButton = new FormPortletButton("Import");
				this.form.addButton(this.importButton);
			} else
				this.closeButton = new FormPortletButton("Close");
			this.form.addButton(this.closeButton);
			this.addChild(form, 0, 0);
			this.setRowSize(1, 40);
		}

		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
			if (button == this.importButton) {
				String config = this.textAreaField.getValue();
				if (config == null || SH.isnt(config.trim()))
					return;
				AmiWebManageUsersPortlet.this.writeToFile(this.textAreaField.getValue());
				AmiWebManageUsersPortlet.this.refresh();
				close();
			}
			if (button == this.closeButton)
				close();
		}

		@Override
		public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		}

		@Override
		public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		}
	}
}