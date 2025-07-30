package com.f1.ami.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.base.Row;
import com.f1.base.TableListenable;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.BasicPortletDownload;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.style.PortletStyleManager;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebManageLayoutsPortlet extends GridPortlet implements WebContextMenuFactory, WebContextMenuListener, ConfirmDialogListener, FormPortletListener {

	private FastTablePortlet fastTable;
	private AmiWebLayoutManager desktop;
	private TableListenable basic;
	private Set<String> layoutNamesNoPrefix;
	private Set<String> layoutNamesNoPrefixNoExt;
	private final String currentLayoutName;
	private final PortletManager desktopManager;
	private AmiWebService service;
	private String currentLayoutSource;
	private FormPortlet formPortlet;
	private FormPortletButton openButton;
	private FormPortletButton closeButton;

	public AmiWebManageLayoutsPortlet(PortletConfig config, Map<String, AmiWebFile> layoutNames) {
		super(config);

		this.service = AmiWebUtils.getService(getManager());
		this.desktop = service.getLayoutManager();
		this.desktopManager = this.service.getPortletManager();
		this.currentLayoutName = this.service.getVarsManager().getSetting(AmiWebConsts.USER_SETTING_AMI_LAYOUT_CURRENT);
		this.currentLayoutSource = this.service.getVarsManager().getSetting(AmiWebConsts.USER_SETTING_AMI_LAYOUT_CURRENT_SOURCE);
		this.layoutNamesNoPrefix = new HashSet<String>(layoutNames.keySet());
		String substring = null;
		this.layoutNamesNoPrefixNoExt = new HashSet<String>();
		for (String str : this.layoutNamesNoPrefix) {
			if (str.endsWith(".ami")) {
				substring = str.substring(0, str.length() - 4);
				this.layoutNamesNoPrefixNoExt.add(substring);
			}
		}

		TableListenable basic = new BasicTable(new String[] { "File Name", "Date Modified", "Author", "Size" });
		List<String> layoutNamesList = new ArrayList<String>(layoutNamesNoPrefix);

		// Add rows to basic
		Row row;
		AmiWebFile file;
		String authorPrefix = "." + AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX;
		for (int i = 0; i < layoutNamesList.size(); i++) { // Iterate through rows
			row = basic.newEmptyRow();
			file = layoutNames.get(layoutNamesList.get(i));

			if (file != null) {
				String filename = SH.afterFirst(file.getName(), AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX);
				String author = SH.beforeFirst(file.getName(), authorPrefix);
				row.putAt(0, filename);
				row.putAt(1, (Long) file.lastModified());
				row.putAt(2, author);
				row.putAt(3, file.length());

				basic.getRows().add(row);
			}
		}

		this.basic = basic;

		// Display output FastTable
		String titleString = null;
		if (layoutNamesNoPrefix.size() == 1)
			titleString = "Layout";
		else
			titleString = "Layouts";
		this.fastTable = new FastTablePortlet(generateConfig(), this.basic, titleString);
		this.fastTable.getTable().setMenuFactory(this);
		this.fastTable.getTable().addMenuListener(this);
		PortletStyleManager styleManager = getManager().getStyleManager();
		this.fastTable.setDialogStyle(styleManager.getDialogStyle());
		this.fastTable.setFormStyle(getManager().getStyleManager().getFormStyle());
		AmiWebUtils.applyEndUserTableStyle(this.fastTable);
		BasicWebCellFormatter formatter = new BasicWebCellFormatter();
		BasicWebCellFormatter dateFormatter = this.service.getFormatterManager().getDateTimeSecsWebCellFormatter();

		this.fastTable.getTable().addColumn(true, "File Name", "File Name", formatter).setWidth(200).addCssClass("bold");
		this.fastTable.getTable().addColumn(true, "Date Modified", "Date Modified", dateFormatter).setWidth(115);
		this.fastTable.getTable().addColumn(true, "Author", "Author", formatter).setWidth(100);
		this.fastTable.getTable().addColumn(true, "Size", "Size", this.service.getFormatterManager().getMemoryFormatter()).setWidth(100);

		AmiWebHeaderPortlet header = new AmiWebHeaderPortlet(generateConfig());
		header.setShowSearch(false);
		header.updateBlurbPortletLayout("Manage Layouts", "");
		header.setShowLegend(false);
		header.setInformationHeaderHeight(60);
		header.setShowBar(false);
		addChild(header);

		addChild(this.fastTable, 0, 1);

		formPortlet = new FormPortlet(generateConfig());
		openButton = new FormPortletButton("Open");
		closeButton = new FormPortletButton("Close");
		formPortlet.addButton(openButton);
		formPortlet.addButton(closeButton);
		formPortlet.addFormPortletListener(this);

		addChild(formPortlet, 0, 2);
		setRowSize(2, formPortlet.getButtonPanelHeight());
		this.fastTable.getTable().sortRows("Date Modified", false, true, false);
	}

	@Override
	public WebMenu createMenu(WebTable table) {

		List<WebMenuItem> entries = new ArrayList<WebMenuItem>();

		if (this.fastTable.getTable().getSelectedRows().size() != 0) {
			entries.add(new BasicWebMenuLink("Copy", true, "copy"));
			entries.add(new BasicWebMenuLink("Delete", true, "delete"));
			if (this.fastTable.getTable().getSelectedRows().size() == 1) {
				entries.add(0, new BasicWebMenuLink("Open", true, "open"));
				entries.add(1, new BasicWebMenuLink("Rename", true, "rename"));
				entries.add(new BasicWebMenuLink("Download", true, "download"));
				entries.add(new BasicWebMenuLink("Get Full Path", true, "get_path"));
				entries.add(new BasicWebMenuLink("Get URL", true, "get_url"));
			}
		}

		BasicWebMenu m = new BasicWebMenu("", true, entries);
		return m;
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if (action.equals("open")) {
			openSelectedLayout();
		} else if (action.equals("rename")) {
			for (Row r : table.getSelectedRows()) {
				if (this.currentLayoutName != null && this.currentLayoutName.equals(r.get("File Name"))) {
					getManager().showDialog("Current Layout Selected",
							new ConfirmDialogPortlet(generateConfig(), "Cannot rename this layout because it is currently opened.", ConfirmDialog.TYPE_MESSAGE));
					return;
				}
			}
			getManager().showDialog("Rename File", new FileRenamePortlet(generateConfig()));
		} else if (action.equals("copy")) {
			copySelectedFiles();
		} else if (action.equals("delete")) {
			Row r;
			List<Row> selectedRows = table.getSelectedRows();
			int numSelected = selectedRows.size();
			for (int i = 0; i < numSelected; i++) {
				r = selectedRows.get(i);
				if (this.currentLayoutName != null && this.currentLayoutName.equals(r.get("File Name"))) {
					getManager().showDialog("Current Layout Selected", new ConfirmDialogPortlet(generateConfig(),
							"Cannot delete current layout. Please change selected files to be deleted.", ConfirmDialogPortlet.TYPE_MESSAGE));
					return;
				}
			}

			ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete the selected file" + (numSelected > 1 ? "s" : "") + "?",
					ConfirmDialog.TYPE_YES_NO);
			dialog.setCallback("confirm_delete");
			dialog.addDialogListener(this);
			dialog.updateButton(ConfirmDialog.ID_YES, "Delete");
			getManager().showDialog("Confirmation", dialog);
		} else if (action.equals("get_path")) {
			Row selectedRow = fastTable.getTable().getSelectedRows().get(0);
			String fileNameNoPrefix = selectedRow.getAt(0).toString();
			AmiWebFile file = service.getUserFilesManager().getFile(AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX + fileNameNoPrefix);
			getManager().showAlert(file.getFullPath());
		} else if (action.equals("get_url")) {
			Row selectedRow = fastTable.getTable().getSelectedRows().get(0);
			String fileNameNoPrefix = selectedRow.getAt(0).toString();
			getManager().showAlert(service.getUrl() + '?' + AmiWebConsts.URL_PARAM_LAYOUT + '=' + AmiWebConsts.LAYOUT_SOURCE_LOCAL + '_' + SH.encodeUrl(fileNameNoPrefix));
		} else if (action.equals("download")) {
			String layoutNameNoPrefix = this.fastTable.getTable().getSelectedRows().get(0).getAt(0).toString();
			this.service.getAutosaveManager().onLoadLocal();
			String configText = this.service.getUserFilesManager().loadFile(AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX + layoutNameNoPrefix);
			getManager().pushPendingDownload(new BasicPortletDownload(layoutNameNoPrefix, configText.getBytes()));
		}
	}

	private void openSelectedLayout() {
		List<Row> selectedRows = this.fastTable.getTable().getSelectedRows();
		if (selectedRows.size() == 1) {
			String fileNameNoPrefixNoExt = selectedRows.get(0).getAt(0).toString();
			this.service.getAutosaveManager().onLoadLocal();
			this.service.getUserFilesManager().loadFile(AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX + fileNameNoPrefixNoExt);
			service.getLayoutFilesManager().loadLayoutDialog(fileNameNoPrefixNoExt, null, AmiWebConsts.LAYOUT_SOURCE_LOCAL);
			close();
		} else if (selectedRows.size() == 0)
			getManager().showAlert("Select a file to open.");
		else
			getManager().showAlert("Select only one file to open.");
	}

	private void copySelectedFiles() {
		List<Row> selectedRows = this.fastTable.getTable().getSelectedRows();
		Row newRow;
		Row oldRow;
		String oldNameNoPrefix;
		String oldNameNoPrefixNoExt;
		String configText;
		String copyNameNoPrefixNoExt;
		String copyNameNoPrefix;
		AmiWebFile file;
		for (int i = 0; i < selectedRows.size(); i++) {
			oldRow = selectedRows.get(i);
			oldNameNoPrefix = oldRow.getAt(0).toString();
			oldNameNoPrefixNoExt = null;
			if (oldNameNoPrefix.endsWith(".ami"))
				oldNameNoPrefixNoExt = oldNameNoPrefix.substring(0, oldNameNoPrefix.length() - 4);
			configText = this.service.getUserFilesManager().loadFile(AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX + oldNameNoPrefix);

			copyNameNoPrefixNoExt = SH.getNextId(oldNameNoPrefixNoExt, this.layoutNamesNoPrefixNoExt);
			this.layoutNamesNoPrefixNoExt.add(copyNameNoPrefixNoExt);
			copyNameNoPrefix = copyNameNoPrefixNoExt + ".ami";

			this.service.getUserFilesManager().saveFile(AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX + copyNameNoPrefix, configText);
			// Update table
			newRow = this.basic.newEmptyRow();
			file = service.getUserFilesManager().getFile(AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX + copyNameNoPrefix);
			newRow.putAt(0, copyNameNoPrefix);
			if (file != null) {
				newRow.putAt(0, SH.stripPrefix(SH.afterFirst(file.getName(), '.'), AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX, true));
				newRow.putAt(1, (Long) file.lastModified());
				newRow.putAt(2, SH.beforeFirst(file.getName(), '.'));
				newRow.putAt(3, file.length());
			}
		}

		this.fastTable.getTable().sortRows("Date Modified", false, true, false);
	}
	private void deleteSelectedFiles() {

		List<Row> selectedRows = this.fastTable.getTable().getSelectedRows();

		Row currentRow;
		String fileNameWithPrefix;
		String fileNameNoPrefix;
		String fileNameNoPrefixNoExt;
		AmiWebFile file;

		for (int i = 0; i < selectedRows.size(); i++) {

			currentRow = selectedRows.get(i);
			fileNameWithPrefix = AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX + currentRow.getAt(0).toString();
			fileNameNoPrefix = currentRow.getAt(0).toString();
			fileNameNoPrefixNoExt = SH.beforeFirst(fileNameNoPrefix, '.');

			AmiWebUserFilesManager userConfigStore = service.getUserFilesManager();
			file = userConfigStore.getFile(fileNameWithPrefix);

			if (file != null) {
				// Delete file
				//				try {
				userConfigStore.removeFile(fileNameWithPrefix);
				//					IOH.delete(file);
				//				} catch (IOException e) {
				//					LH.warning(LH.get(AmiWebManageLayoutsPortlet.class), "Error retrieving file: ", e);
				//				}

				// Remove file name from set of names
				this.layoutNamesNoPrefix.remove(fileNameNoPrefix);
				this.layoutNamesNoPrefixNoExt.remove(fileNameNoPrefixNoExt); // Do this so that the copy function works correctly 

				// Remove row in table 
				this.basic.getRows().remove(currentRow.getLocation());
			}
		}
	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {

	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {

	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (ConfirmDialogPortlet.ID_YES.equals(id)) {
			if ("confirm_delete".equals(source.getCallback())) {
				deleteSelectedFiles();
			}
		}
		return true;
	}

	public class FileRenamePortlet extends GridPortlet implements FormPortletListener, ConfirmDialogListener {

		private FormPortlet form;
		private FormPortletButton cancelButton;
		private FormPortletTextField nameField;
		private String oldFile;
		private String newNameNoPrefix;

		public FileRenamePortlet(PortletConfig config) {
			super(config);

			String oldNameNoPrefix = fastTable.getTable().getSelectedRows().get(0).getAt(0).toString();
			String oldNameWithPrefix = AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX + oldNameNoPrefix;

			if (oldNameWithPrefix != null) {
				this.oldFile = oldNameWithPrefix;
				form = addChild(new FormPortlet(generateConfig()), 0, 0);
				nameField = form.addField(new FormPortletTextField("New file name")).setValue(oldNameNoPrefix);
				nameField.setWidth(FormPortletTextField.WIDTH_STRETCH);
				form.addButton(new FormPortletButton("Rename"));
				cancelButton = form.addButton(new FormPortletButton("Cancel"));
				form.addFormPortletListener(this);
				setSuggestedSize(400, 75);
			}
		}

		@Override
		public boolean onButton(ConfirmDialog source, String id) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				if ("overwrite".equals(source.getCallback())) {
					renameSelectedFile(true);
				}
			}
			return true;
		}

		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
			if (button == cancelButton)
				close();
			else {
				newNameNoPrefix = nameField.getValue();
				newNameNoPrefix = SH.trim(newNameNoPrefix);
				if (SH.isnt(newNameNoPrefix)) { // Check if newFileName is null or consists only of whitespace characters
					getManager().showAlert("File name required");
				} else if (newNameNoPrefix.indexOf(',') != -1 || newNameNoPrefix.length() > 32 || AmiWebLayoutManager.DEFAULT_LAYOUT_NAME.equals(newNameNoPrefix)) {
					getManager().showAlert("Invalid file name");
				} else if (fastTable.getTable().getSelectedRows().get(0).getAt(0).toString().equals(newNameNoPrefix)) {
					getManager().showAlert("Enter a file name that is different from the current name");
				} else {
					if (layoutNamesNoPrefix.contains(newNameNoPrefix)) {
						ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), "Overwrite existing file " + newNameNoPrefix + "?", ConfirmDialogPortlet.TYPE_YES_NO);
						cdp.setCallback("overwrite");
						cdp.addDialogListener(this);
						getManager().showDialog("File exists", cdp);
					} else {
						renameSelectedFile(false);
					}
					close();
				}
			}
		}

		private void renameSelectedFile(boolean overwriteExisting) {
			String oldNameNoPrefix = fastTable.getTable().getSelectedRows().get(0).getAt(0).toString();
			String oldNameNoPrefixNoExt = SH.beforeFirst(oldNameNoPrefix, '.');
			String newNameNoPrefixNoExt = SH.beforeFirst(newNameNoPrefix, '.');
			//			String newFileAuthor = getManager().describeUser().split(":", 2)[0];
			String newFile = AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX + newNameNoPrefix;
			service.getUserFilesManager().moveFile(oldFile, newFile);

			//			IOH.moveForce(oldFile, newFile);
			layoutNamesNoPrefix.remove(oldNameNoPrefix);
			layoutNamesNoPrefixNoExt.remove(oldNameNoPrefixNoExt);
			Row selectedRow = fastTable.getTable().getSelectedRows().get(0);
			String newRowEntry = newNameNoPrefix;
			if (!overwriteExisting) {
				layoutNamesNoPrefix.add(newNameNoPrefix);
				layoutNamesNoPrefixNoExt.add(newNameNoPrefixNoExt);
			} else {
				List<Row> allRows = fastTable.getTable().getRows();
				Row oldRow = null;
				for (int i = 0; i < allRows.size(); i++) {
					Row currentRow = allRows.get(i);
					if (currentRow.getAt(0).toString().equals(newRowEntry)) {
						oldRow = currentRow;
					}
				}
				basic.getRows().remove(oldRow.getLocation());
			}
			selectedRow.putAt(0, newRowEntry);
			fastTable.getTable().sortRows("Date Modified", false, true, false);

		}

		@Override
		public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

		}

		@Override
		public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

		}

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (OH.eq(button, this.closeButton))
			close();
		else if (OH.eq(button, openButton))
			openSelectedLayout();
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {

	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		openSelectedLayout();
	}

	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}
	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}

}
