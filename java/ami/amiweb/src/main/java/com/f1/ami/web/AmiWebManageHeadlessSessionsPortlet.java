package com.f1.ami.web;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.web.cloud.AmiWebCloudLayoutTree;
import com.f1.ami.web.cloud.AmiWebCloudManager;
import com.f1.ami.web.headless.AmiWebHeadlessManager;
import com.f1.ami.web.headless.AmiWebHeadlessSession;
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

public class AmiWebManageHeadlessSessionsPortlet extends GridPortlet implements WebContextMenuFactory, WebContextMenuListener, FormPortletListener, ConfirmDialogListener {

	private static final String ACTION_ADD_SESSION = "add_session";
	private static final String ACTION_DELETE_SESSION = "delete_session";
	private static final String ACTION_COPY_SESSION = "copy_session";
	private static final String ACTION_START_SESSION = "start_session";
	private static final String ACTION_STOP_SESSION = "stop_session";

	private static final Logger log = LH.get();
	private static final String COLUMN_SESSIONNAME = "Name";
	private static final String COLUMN_USERNAME = "User";
	private static final String COLUMN_RESOLUTION = "Resolution";
	private static final String COLUMN_CURRENT_OWNER = "Owner";
	private static final String COLUMN_AUTOSTART = "Autostart";
	private static final String COLUMN_ACTIVE = "Active";
	private static final String COLUMN_ISADMIN = "Is Admin";
	private static final String COLUMN_ISDEV = "Is Dev";
	private static final String COLUMN_DEFAULT_LAYOUT = "Default Layout";
	private static final String COLUMN_LAYOUTS = "Layouts";
	private static final String COLUMN_PERMISSIONS = "Permissions";
	private static final String COLUMN_OTHERS = "Others";

	private FastTablePortlet fastTable;
	private TableListenable basic;
	private FormPortletButton refreshButton;
	private FormPortletButton addSessionButton;
	private FormPortletButton deleteSessionButton;
	private FormPortletButton copySessionButton;
	private FormPortletButton exportButton;
	private ContainerTools properties;
	final private AmiWebCloudManager cloudManager;
	final private AmiWebHeadlessManager headlessManager;

	public AmiWebManageHeadlessSessionsPortlet(PortletConfig config, ContainerTools properties, AmiWebCloudManager amiWebCloudManager) {
		super(config);
		this.properties = properties;
		this.cloudManager = amiWebCloudManager;
		this.headlessManager = getManager().getTools().getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class);

		AmiWebHeaderPortlet header = new AmiWebHeaderPortlet(generateConfig());
		header.setShowSearch(false);
		header.updateBlurbPortletLayout("Manage Headless Sessions", "");
		header.setShowLegend(false);
		header.setInformationHeaderHeight(80);
		header.getBarFormPortlet().addFormPortletListener(this);
		this.refreshButton = header.getBarFormPortlet().addButton(new FormPortletButton("Refresh").setCssStyle("_bg=#86cf8c|_fg=#000000"));
		this.addSessionButton = header.getBarFormPortlet().addButton(new FormPortletButton("Add").setCssStyle("_bg=#b3e5fc|_fg=#000000"));
		this.deleteSessionButton = header.getBarFormPortlet().addButton(new FormPortletButton("Delete").setCssStyle("_bg=#b3e5fc|_fg=#000000"));
		this.copySessionButton = header.getBarFormPortlet().addButton(new FormPortletButton("Copy").setCssStyle("_bg=#b3e5fc|_fg=#000000"));
		this.exportButton = header.getBarFormPortlet().addButton(new FormPortletButton("Export").setCssStyle("_bg=#ffd359|_fg=#000000"));
		header.updateBarPortletLayout(this.addSessionButton.getHtmlLayoutSignature());
		addChild(header, 0, 0);

		TableListenable basicTable = new BasicTable(new String[] { COLUMN_SESSIONNAME, COLUMN_ACTIVE, COLUMN_AUTOSTART, COLUMN_USERNAME, COLUMN_CURRENT_OWNER, COLUMN_RESOLUTION,
				COLUMN_ISADMIN, COLUMN_ISDEV, COLUMN_PERMISSIONS, COLUMN_DEFAULT_LAYOUT, COLUMN_LAYOUTS, COLUMN_OTHERS });
		BasicWebCellFormatter formatter = new BasicWebCellFormatter();
		this.basic = basicTable;
		this.fastTable = new FastTablePortlet(generateConfig(), this.basic, "Headless Sessions");
		BasicWebColumn sessionNameColumn = this.fastTable.getTable().addColumn(true, COLUMN_SESSIONNAME, COLUMN_SESSIONNAME, formatter).setCssColumn("bold").setWidth(100);
		this.fastTable.getTable().sortRows(sessionNameColumn.getColumnId(), true, true, false);
		this.fastTable.getTable().addColumn(true, COLUMN_ACTIVE, COLUMN_ACTIVE, formatter).setWidth(50);
		this.fastTable.getTable().addColumn(true, COLUMN_AUTOSTART, COLUMN_AUTOSTART, formatter).setWidth(60);
		this.fastTable.getTable().addColumn(true, COLUMN_USERNAME, COLUMN_USERNAME, formatter).setWidth(80);
		this.fastTable.getTable().addColumn(true, COLUMN_CURRENT_OWNER, COLUMN_CURRENT_OWNER, formatter).setWidth(80);
		this.fastTable.getTable().addColumn(true, COLUMN_RESOLUTION, COLUMN_RESOLUTION, formatter).setWidth(80);
		this.fastTable.getTable().addColumn(true, COLUMN_ISADMIN, COLUMN_ISADMIN, formatter).setWidth(50);
		this.fastTable.getTable().addColumn(true, COLUMN_ISDEV, COLUMN_ISDEV, formatter).setWidth(50);
		this.fastTable.getTable().addColumn(true, COLUMN_DEFAULT_LAYOUT, COLUMN_DEFAULT_LAYOUT, formatter).setWidth(150);
		this.fastTable.getTable().addColumn(true, COLUMN_LAYOUTS, COLUMN_LAYOUTS, formatter).setWidth(150);
		this.fastTable.getTable().addColumn(true, COLUMN_PERMISSIONS, COLUMN_PERMISSIONS, formatter).setWidth(205);
		this.fastTable.getTable().addColumn(true, COLUMN_OTHERS, COLUMN_OTHERS, formatter).setWidth(80);
		PortletStyleManager styleManager = getManager().getStyleManager();
		this.fastTable.setDialogStyle(styleManager.getDialogStyle());
		this.fastTable.setFormStyle(getManager().getStyleManager().getFormStyle());
		AmiWebUtils.applyEndUserTableStyle(this.fastTable);

		parseHeadlessText();

		addChild(this.fastTable, 0, 1);
		this.fastTable.getTable().setMenuFactory(this);
		this.fastTable.getTable().addMenuListener(this);
	}

	private String getHeadlessText() {
		File path = properties.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_HEADLESS_FILE, File.class);
		String text = "";
		CachedFile file = new CachedFile(path, 1000);
		if (path.isFile())
			text = file.getData().getText();
		return text;
	}

	private void parseHeadlessText() {
		String text = getHeadlessText();

		if (SH.is(text)) {
			String[] lines = SH.splitLines(text);
			for (String line : lines) {
				if (SH.isnt(line) || line.startsWith("#"))
					continue;
				String[] parts = SH.splitWithEscape('|', '\\', line);
				if (parts.length < 3)
					continue;
				String sessionName = parts[0];
				if (SH.isnt(sessionName))
					continue;
				boolean autostart = !sessionName.startsWith("!");
				if (!autostart)
					sessionName = sessionName.substring(1);
				final String sessionUser = parts[1];
				final String resolution = parts[2];
				Object[] row = newEmptyRow();
				putNoFire(row, COLUMN_SESSIONNAME, sessionName);
				putNoFire(row, COLUMN_USERNAME, sessionUser);
				putNoFire(row, COLUMN_RESOLUTION, resolution);
				String isAdmin = "false";
				String isDev = "false";
				String defaultLayout = "";
				String layouts = "";
				String permissions = "";
				String others = "";
				for (int i = 3; i < parts.length; ++i) {
					final String part = parts[i];
					if (SH.isnt(part))
						continue;
					final String key = SH.beforeFirst(part, '=', null);
					final String val = SH.afterFirst(part, '=', null);
					if (key == null)
						others += "|" + part;
					else if (key.equals("ISADMIN"))
						isAdmin = val;
					else if (key.equals("ISDEV"))
						isDev = val;
					else if (key.equals("DEFAULT_LAYOUT"))
						defaultLayout = val;
					else if (key.equals("LAYOUTS"))
						layouts = val;
					else if (key.equals("AMIDB_PERMISSIONS"))
						permissions = val;
					else
						others += "|" + part;
				}
				if (!others.isEmpty())
					others = others.substring(1, others.length());
				AmiWebHeadlessSession session = headlessManager.getSessionByName(sessionName);
				Boolean isAlive = false;
				String currentOwner = null;
				if (session != null) {
					if (session.getWebState() != null) {
						isAlive = session.getWebState().isAlive();
						if (session.getWebState().getWebStatesManager() != null)
							currentOwner = session.getWebState().getWebStatesManager().getUserName();
					}
				}
				putNoFire(row, COLUMN_ACTIVE, isAlive);
				putNoFire(row, COLUMN_AUTOSTART, autostart);
				putNoFire(row, COLUMN_ISADMIN, isAdmin);
				putNoFire(row, COLUMN_ISDEV, isDev);
				putNoFire(row, COLUMN_DEFAULT_LAYOUT, defaultLayout);
				putNoFire(row, COLUMN_LAYOUTS, layouts);
				putNoFire(row, COLUMN_PERMISSIONS, permissions);
				putNoFire(row, COLUMN_OTHERS, others);
				putNoFire(row, COLUMN_CURRENT_OWNER, currentOwner);
				this.fastTable.addRow(row);
			}
		}
	}

	private Object[] newEmptyRow() {
		return new Object[basic.getColumnsCount()];
	}

	private void putNoFire(Object[] r, String id, Object value) {
		r[basic.getColumn(id).getLocation()] = value;
	}

	private void refresh() {
		this.fastTable.clearRows();
		parseHeadlessText();
	}

	private void onUpdate(String addedSession) {
		StringBuilder newHeadless = new StringBuilder();
		newHeadless.append("#Each line represents a headless session. Syntax is: HEADLESSNAME|USERNAME|SCREEN_WIDTH x SCREENHEIGHT|Key1=Value1|Key2=Value2|....\n");
		for (Row r : this.fastTable.getTable().getRows()) {
			if (!(Boolean) r.get(COLUMN_AUTOSTART))
				newHeadless.append('!');
			newHeadless.append(r.get(COLUMN_SESSIONNAME));
			newHeadless.append("|").append(r.get(COLUMN_USERNAME));
			newHeadless.append("|").append(r.get(COLUMN_RESOLUTION));
			newHeadless.append("|").append("ISADMIN=" + SH.toString(r.get(COLUMN_ISADMIN)));
			newHeadless.append("|").append("ISDEV=" + SH.toString(r.get(COLUMN_ISDEV)));
			if (r.get(COLUMN_DEFAULT_LAYOUT) != null && !SH.isEmpty(SH.toString(r.get(COLUMN_DEFAULT_LAYOUT))))
				newHeadless.append("|").append("DEFAULT_LAYOUT=" + r.get(COLUMN_DEFAULT_LAYOUT));
			if (r.get(COLUMN_LAYOUTS) != null && !SH.isEmpty(SH.toString(r.get(COLUMN_LAYOUTS))))
				newHeadless.append("|").append("LAYOUTS=" + r.get(COLUMN_LAYOUTS));
			if (r.get(COLUMN_PERMISSIONS) != null && !SH.isEmpty(SH.toString(r.get(COLUMN_PERMISSIONS))))
				newHeadless.append("|").append("AMIDB_PERMISSIONS=" + r.get(COLUMN_PERMISSIONS));
			if (r.get(COLUMN_OTHERS) != null && !SH.isEmpty(SH.toString(r.get(COLUMN_OTHERS))))
				newHeadless.append("|").append(r.get(COLUMN_OTHERS));
			if (addedSession != null && addedSession.equals(SH.toString(r.get(COLUMN_SESSIONNAME)))) {
				String[] lines = SH.splitLines(newHeadless.toString());
				String toAdd = lines[lines.length - 1];
				headlessManager.addSessionByLine(toAdd);
			}
			newHeadless.append("\n");
		}
		writeToFile(newHeadless.toString());
	}

	private void writeToFile(String text) {
		File path = this.properties.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_HEADLESS_FILE, new File("data/headless.txt"));
		try {
			IOH.ensureDir(path.getParentFile());
			IOH.writeText(path, text);
			LH.info(log, "Updated " + IOH.getFullPath(path));
		} catch (IOException e) {
			throw new RuntimeException("Error writing to access file: " + IOH.getFullPath(path), e);
		}
	}

	private void deletePrompt() {
		List<Row> selected = this.fastTable.getTable().getSelectedRows();
		if (selected.isEmpty())
			return;
		String deletePrompt = "Delete headless session(s): ";
		for (Row r : selected)
			deletePrompt += r.get(COLUMN_SESSIONNAME) + ", ";
		deletePrompt = deletePrompt.substring(0, deletePrompt.lastIndexOf(", "));
		deletePrompt += ". Are you sure?";
		ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), deletePrompt, ConfirmDialogPortlet.TYPE_OK_CANCEL, this);
		getManager().showDialog("Delete Headless Session", cdp);
		cdp.setCallback("DELETE");
		cdp.setCorrelationData(selected);
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
				Boolean hasActive = false;
				String postMessage = "";
				for (Object row : selected) {
					if (row instanceof Row) {
						String sessionName = SH.toString(((Row)row).get(COLUMN_SESSIONNAME));
						AmiWebHeadlessSession session = headlessManager.getSessionByName(sessionName);
						if (session == null) {
							getManager().showAlert("Cannot find session for session name: \"" + sessionName + "\"! Try refreshing session page");
							continue;
						}
						if (session.getWebState() != null && session.getWebState().isAlive()) {
							hasActive = true;
							postMessage += sessionName + ", ";
						}
						else {
							headlessManager.removeSession(session);
							this.fastTable.removeRow((Row)row);
						}
					}
				}
				if (hasActive) {
					postMessage = postMessage.substring(0, postMessage.lastIndexOf(", "));
					getManager().showAlert("Unable to remove session(s): " + postMessage + ". Please stop the session(s) before removing");
				}
				onUpdate(null);
			} else if ("STOP".equals(source.getCallback())) {
				String sessionName = (String)source.getCorrelationData();
				AmiWebHeadlessSession toStop = headlessManager.getSessionByName(sessionName);
				if (toStop == null) {
					getManager().showAlert("Cannot find session for session name: \"" + sessionName + "\"! Try refreshing session page");
					return false;
				}
				toStop.stop();
				refresh();
			}
		}
		return true;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.addSessionButton)
			getManager().showDialog("Add Headless Session", new HeadlessSessionConfigPortlet(generateConfig(), null), 700, 580);
		else if (button == this.deleteSessionButton)
			deletePrompt();
		else if (button == this.copySessionButton) {
			List<Row> selected = this.fastTable.getTable().getSelectedRows();
			if (selected.size() != 1)
				return;
			Row selectedRow = selected.size() > 0 ? selected.get(0) : null;
			getManager().showDialog("Add Headless Session", new HeadlessSessionConfigPortlet(generateConfig(), selectedRow), 700, 580);
		} else if (button == this.exportButton)
			getManager().showDialog("Export Headless Config", new ViewHeadlessPortlet(generateConfig(), getHeadlessText()), 1200, 700);
		else if (button == this.refreshButton)
			refresh();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if (action.equals(ACTION_ADD_SESSION)) {
			getManager().showDialog("Add Headless Session", new HeadlessSessionConfigPortlet(generateConfig(), null), 700, 580);
			return;
		} else if (action.equals(ACTION_DELETE_SESSION)) {
			deletePrompt();
			return;
		}
		List<Row> selected = this.fastTable.getTable().getSelectedRows();
		Row selectedRow = selected.size() > 0 ? selected.get(0) : null;
		if (action.equals(ACTION_COPY_SESSION))
			getManager().showDialog("Add Headless Session", new HeadlessSessionConfigPortlet(generateConfig(), selectedRow), 700, 580);
		else if (action.equals(ACTION_START_SESSION)) {
			String sessionName = SH.toString(selectedRow.get(COLUMN_SESSIONNAME));
			sessionName = SH.stripPrefix(sessionName, "!", false);
			AmiWebHeadlessSession toStart = headlessManager.getSessionByName(sessionName);
			if (toStart == null) {
				getManager().showAlert("Cannot find session for session name: \"" + sessionName + "\"! Try refreshing session page");
				return;
			}
			toStart.start();
			refresh();
		} else if (action.equals(ACTION_STOP_SESSION)) {
			String sessionName = SH.toString(selectedRow.get(COLUMN_SESSIONNAME));
			sessionName = SH.stripPrefix(sessionName, "!", false);
			String stopPrompt = "Stop headless session: " + sessionName + ". Any unsaved changes will be lost. Are you sure?";
			ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), stopPrompt, ConfirmDialogPortlet.TYPE_OK_CANCEL, this);
			getManager().showDialog("Stop Headless Session", cdp);
			cdp.setCallback("STOP");
			cdp.setCorrelationData(sessionName);
		}
	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
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
	public WebMenu createMenu(WebTable table) {
		BasicWebMenu menu = new BasicWebMenu();
		menu.add(new BasicWebMenuLink("Add Headless Session", true, ACTION_ADD_SESSION));
		if (this.fastTable.getTable().getSelectedRows().size() > 0) {
			menu.add(new BasicWebMenuLink("Delete Headless Session(s)", true, ACTION_DELETE_SESSION));
			if (this.fastTable.getTable().getSelectedRows().size() == 1) {
				menu.add(new BasicWebMenuLink("Copy Headless Session", true, ACTION_COPY_SESSION));
				if ((Boolean) this.fastTable.getTable().getSelectedRows().get(0).get(COLUMN_ACTIVE))
					menu.add(new BasicWebMenuLink("Stop Session", true, ACTION_STOP_SESSION));
				else
					menu.add(new BasicWebMenuLink("Start Session", true, ACTION_START_SESSION));
			}
		}
		return menu;
	}

	public class HeadlessSessionConfigPortlet extends GridPortlet implements FormPortletListener {

		final private FormPortlet form;
		final private FormPortletTextField sessionNameField;
		final private FormPortletTextField sessionUserNameField;
		final private FormPortletTextField sessionResolutionField;
		final private FormPortletCheckboxField isAdminField;
		final private FormPortletCheckboxField isDevField;
		final private FormPortletCheckboxField autostartField;
		final private FormPortletMultiCheckboxField<String> permissionsField;
		private FormPortletTextAreaField layoutsField;
		private FormPortletSelectField<String> defaultLayoutField;
		final private FormPortletTextAreaField othersField;
		private FormPortletButton cxlButton;
		private FormPortletButton okButton;

		public HeadlessSessionConfigPortlet(PortletConfig config, Row user) {
			super(config);
			AmiWebHeaderPortlet header = new AmiWebHeaderPortlet(generateConfig());
			header.setShowSearch(false);
			String blurbTitle;
			if (user != null)
				blurbTitle = "Copy ";
			else
				blurbTitle = "Add ";
			header.updateBlurbPortletLayout(blurbTitle + "Headless Session", "");
			header.setShowLegend(false);
			header.setInformationHeaderHeight(60);
			header.setShowBar(false);
			addChild(header, 0, 0);
			addChild(form = new FormPortlet(generateConfig()), 0, 1);
			form.addField(sessionNameField = new FormPortletTextField("Session Name:"));
			form.addField(autostartField = new FormPortletCheckboxField("Autostart:"));
			this.autostartField.setValue(true);
			form.addField(sessionUserNameField = new FormPortletTextField("Session User:"));
			form.addField(sessionResolutionField = new FormPortletTextField("Session Resolution:"));
			form.addField(isAdminField = new FormPortletCheckboxField("Is Admin:"));
			form.addField(isDevField = new FormPortletCheckboxField("Is Dev:"));
			form.addField(permissionsField = new FormPortletMultiCheckboxField<String>(String.class, "Permissions:")).setWidth(250);
			for (String p : AmiWebAdminToolPortlet.validPermissions)
				permissionsField.addOption(p, p);
			form.addField(layoutsField = new FormPortletTextAreaField("Permissible Layouts:")).setHeight(100);
			form.addField(defaultLayoutField = new FormPortletSelectField<String>(String.class, "Default Layout:"));
			AmiWebCloudLayoutTree layouts = AmiWebManageHeadlessSessionsPortlet.this.cloudManager.getCloudLayouts();
			addLayouts("", layouts);
			form.addField(othersField = new FormPortletTextAreaField("Others:")).setHeight(100);

			if (user != null) {
				sessionResolutionField.setValue(SH.toString(user.get(COLUMN_RESOLUTION)));
				isAdminField.setValue((Boolean) user.get(COLUMN_ISADMIN).equals("true"));
				isDevField.setValue((Boolean) user.get(COLUMN_ISDEV).equals("true"));
				autostartField.setValue((Boolean) user.get(COLUMN_AUTOSTART).equals("true"));
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
				if (verifySession()) {
					Object[] row = AmiWebManageHeadlessSessionsPortlet.this.newEmptyRow();
					AmiWebManageHeadlessSessionsPortlet.this.putNoFire(row, COLUMN_SESSIONNAME, sessionNameField.getValue());
					AmiWebManageHeadlessSessionsPortlet.this.putNoFire(row, COLUMN_AUTOSTART, autostartField.getValue());
					AmiWebManageHeadlessSessionsPortlet.this.putNoFire(row, COLUMN_USERNAME, sessionUserNameField.getValue());
					AmiWebManageHeadlessSessionsPortlet.this.putNoFire(row, COLUMN_RESOLUTION, sessionResolutionField.getValue());
					AmiWebManageHeadlessSessionsPortlet.this.putNoFire(row, COLUMN_CURRENT_OWNER, null);
					AmiWebManageHeadlessSessionsPortlet.this.putNoFire(row, COLUMN_ISADMIN, Boolean.toString(isAdminField.getBooleanValue()));
					AmiWebManageHeadlessSessionsPortlet.this.putNoFire(row, COLUMN_ISDEV, Boolean.toString(isDevField.getBooleanValue()));
					AmiWebManageHeadlessSessionsPortlet.this.putNoFire(row, COLUMN_DEFAULT_LAYOUT, defaultLayoutField.getValue());
					AmiWebManageHeadlessSessionsPortlet.this.putNoFire(row, COLUMN_LAYOUTS,
							layoutsField.getValue() != null ? SH.replaceAll(layoutsField.getValue(), '\n', ',') : null);
					AmiWebManageHeadlessSessionsPortlet.this.putNoFire(row, COLUMN_PERMISSIONS, AmiWebAdminToolPortlet.permissionStringBuilder(permissionsField.getValue()));
					AmiWebManageHeadlessSessionsPortlet.this.putNoFire(row, COLUMN_OTHERS,
							othersField.getValue() != null ? SH.replaceAll(othersField.getValue(), '\n', '|') : null);
					AmiWebManageHeadlessSessionsPortlet.this.fastTable.addRow(row);
					AmiWebManageHeadlessSessionsPortlet.this.onUpdate(sessionNameField.getValue());
					AmiWebManageHeadlessSessionsPortlet.this.refresh();
					close();
				}
			}
		}

		private Boolean verifySession() {
			String sessionName = sessionNameField.getValue();
			if (sessionName == null || sessionName.length() == 0 || sessionName.startsWith("!") || !AmiUtils.isValidVariableName(sessionName, false, false)) {
				getManager().showAlert("Please input a valid session name");
				return false;
			}
			TableList t = AmiWebManageHeadlessSessionsPortlet.this.basic.getRows();
			for (Row r : t) {
				if (r.get(COLUMN_SESSIONNAME).equals(sessionName)) {
					getManager().showAlert("Headless session already exists: " + sessionName);
					return false;
				}
			}
			String sessionUser = sessionUserNameField.getValue();
			if (sessionUser == null || sessionUser.length() == 0) {
				getManager().showAlert("Please input a valid session username");
				return false;
			}
			String sessionResolution = sessionResolutionField.getValue();
			if (sessionResolution == null || sessionResolution.length() == 0) {
				getManager().showAlert("Please input a valid session resolution e.g. 1000x800");
				return false;
			}
			final String resWidth = SH.beforeFirst(sessionResolution, 'x', null);
			final String resHeight = SH.afterFirst(sessionResolution, 'x', null);
			if (resWidth == null || resHeight == null || !SH.isWholeNumber(resWidth) || !SH.isWholeNumber(resHeight)) {
				getManager().showAlert("Please input a valid session resolution e.g. 1000x800");
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

	public class ViewHeadlessPortlet extends GridPortlet implements FormPortletListener {
		private FormPortlet form;
		private FormPortletButton closeButton;
		private FormPortletTextAreaField textAreaField;

		public ViewHeadlessPortlet(PortletConfig config, String headlessText) {
			super(config);
			this.form = new FormPortlet(generateConfig());
			FormPortletTitleField titleField = this.form.addField(new FormPortletTitleField(""));
			this.textAreaField = form.addField(new FormPortletTextAreaField(""));
			textAreaField.setLeftPosPx(5);
			textAreaField.setTopPosPx(30);
			textAreaField.setBottomPosPx(50);
			textAreaField.setRightPosPx(5);
			textAreaField.setValue(headlessText);
			titleField.setTopPosPx(5);
			titleField.setHeightPx(25);
			titleField.setLeftPosPx(5);
			titleField.setWidthPx(600);
			titleField.setValue("Select and copy the text below into your clip board using Ctrl+C");
			this.form.addFormPortletListener(this);
			this.closeButton = new FormPortletButton("Close");
			this.form.addButton(this.closeButton);
			this.addChild(form, 0, 0);
			this.setRowSize(1, 40);
		}

		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
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

	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}
}