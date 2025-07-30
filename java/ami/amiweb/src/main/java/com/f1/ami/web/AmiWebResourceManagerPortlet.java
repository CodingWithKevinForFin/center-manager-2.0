package com.f1.ami.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.web.pages.AmiWebPages;
import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.base.TableListenable;
import com.f1.container.ResultMessage;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpUtils;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.MouseEvent;
import com.f1.suite.web.portal.BasicPortletDownload;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.WebMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletDivField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletFileUploadField;
import com.f1.suite.web.portal.impl.form.FormPortletFileUploadField.FileData;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebResourceManagerPortlet extends GridPortlet implements WebMenuListener, WebContextMenuFactory, WebContextMenuListener, ConfirmDialogListener,
		AmiWebSpecialPortlet, AmiWebResourcesManagerListener, FormPortletListener {
	private final FastTablePortlet table;
	private final AmiWebService service;
	private final TableListenable tableListenable;
	final private FormPortlet form;
	final private FormPortletButton uploadButton;
	final private FormPortletButton closeButton;

	public AmiWebResourceManagerPortlet(PortletConfig config) {
		super(config);
		service = AmiWebUtils.getService(getManager());

		tableListenable = new BasicTable(new String[] { "Type", "Object", "File Size", "Modified On", "Checksum", "Location", "Width", "Height" });
		table = new FastTablePortlet(generateConfig(), tableListenable, "Resources");

		BasicWebCellFormatter formatter = new BasicWebCellFormatter();
		table.getTable().addColumn(true, "Object", "Object", formatter);
		BasicWebCellFormatter integerWebCellFormatter = this.service.getFormatterManager().getIntegerWebCellFormatter();
		table.getTable().addColumn(true, "File Size", "File Size", integerWebCellFormatter);
		table.getTable().addColumn(true, "Modified On", "Modified On", this.service.getFormatterManager().getDateTimeMillisWebCellFormatter());
		table.getTable().addColumn(true, "Location", "Location", formatter);
		table.getTable().addColumn(true, "Checksum", "Checksum", integerWebCellFormatter);
		table.getTable().addColumn(true, "Width (px)", "Width", integerWebCellFormatter);
		table.getTable().addColumn(true, "Height (px)", "Height", integerWebCellFormatter);
		table.getTable().addColumn(true, "Location", "Location", this.service.getFormatterManager().getBasicFormatter());

		AmiWebHeaderPortlet hp = new AmiWebHeaderPortlet(generateConfig());
		hp.setShowBar(false);
		hp.setShowLegend(false);
		hp.setShowSearch(false);
		hp.updateBlurbPortletLayout("Global Resource Manager",
				"These resources are available for use in <u>all</u> dashboards, not just the current dashboard. Note, these resources are accesible to anyone that has a valid login id.");
		this.form = new FormPortlet(generateConfig());
		this.form.addFormPortletListener(this);
		this.addChild(hp, 0, 0);
		this.addChild(table, 0, 1);
		this.addChild(form, 0, 2);
		//		this.setRowSize(0, 100);
		this.setRowSize(2, 35);
		this.uploadButton = this.form.addButton(new FormPortletButton("Upload Resource"));
		this.closeButton = this.form.addButton(new FormPortletButton("Close"));

		table.getTable().setMenuFactory(this);
		table.getTable().addMenuListener(this);
		displayResourcesOnTable();

		service.getResourcesManager().addListener(this);
	}

	private void displayResourcesOnTable() {
		this.tableListenable.clear();
		for (AmiWebResource i : this.service.getResourcesManager().getWebResources())
			tableListenable.getRows().add(tableListenable.newRow(AmiConsts.TYPE_RESOURCE, i.getName(), i.getSize(), i.getModified(), i.getChecksum(),
					i.isCenter() ? "Center" : "Web", i.getImageWidth() == -1 ? null : i.getImageWidth(), i.getImageHeight() == -1 ? null : i.getImageHeight()));
		this.table.autoSizeAllColumns();
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		super.onBackendResponse(result);
	}

	@Override
	public boolean onUserMouseEvent(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseEvent.CONTEXTMENU && table.getTable().getSelectedRows().size() == 0) {
			getManager().showContextMenu(this.createMenu(), this);
		}
		return super.onUserMouseEvent(mouseEvent);
	}

	@Override
	public void onMenuItem(String id) {
		this.onContextMenuItem(id);
	}

	@Override
	public void onMenuDismissed() {

	}

	private WebMenu createMenu() {
		List<WebMenuItem> entries = new ArrayList<WebMenuItem>();

		//Required: Add and Remove (upload delete)
		//Nice to haves: Copy, Rename, Move, Download, Get Full Path

		if (this.table.getTable().getSelectedRows().size() == 1) {
			entries.add(new BasicWebMenuLink("Open in new Tab", true, "newtab"));
			entries.add(new BasicWebMenuLink("Download", true, "download"));
			entries.add(new BasicWebMenuLink("Get Absolute HTTP URL", true, "geturl"));
			entries.add(new BasicWebMenuLink("Get Relative HTTP URL", true, "getrelurl"));
		}
		entries.add(new BasicWebMenuLink("Upload File", true, "upload"));
		entries.add(new BasicWebMenuLink("Upload File To Center (Deprecated)", true, "upload_center"));
		if (this.table.getTable().getSelectedRows().size() != 0) {
			entries.add(new BasicWebMenuLink("Remove File", true, "remove"));
		}

		BasicWebMenu m = new BasicWebMenu("", true, entries);
		return m;
	}
	@Override
	public WebMenu createMenu(WebTable table) {
		return this.createMenu();
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		this.onContextMenuItem(action);
	}
	private void onContextMenuItem(String action) {
		if ("upload_center".equals(action)) {
			actionUploadCenter();
		} else if ("upload".equals(action)) {
			actionUpload();
		} else if ("remove".equals(action)) {
			actionRemove();
		} else if ("geturl".equals(action)) {
			getHTTPURL(false);
		} else if ("getrelurl".equals(action)) {
			getHTTPURL(true);
		} else if ("newtab".equals(action)) {
			Row r = table.getTable().getActiveRow();
			final String resourcesHTTPPath = AmiWebPages.URL_RESOURCES;
			String fileName = r.getAt(1, Caster_String.INSTANCE);
			new JsFunction(service.getPortletManager().getPendingJs(), "window", "open").addParamQuoted(".." + resourcesHTTPPath + "/" + fileName).addParamQuoted("fileName")
					.addParamQuoted("").end();
		} else if ("download".equals(action)) {
			Row r = table.getTable().getActiveRow();
			String fileName = r.getAt(1, Caster_String.INSTANCE);
			AmiWebResource rsc = this.service.getResourcesManager().getWebResource(fileName);
			if (rsc != null && rsc.getBytes() != null)
				service.getPortletManager().pushPendingDownload(new BasicPortletDownload(SH.afterLast(fileName, '/'), rsc.getBytes()));
			else if (rsc.isCenter()) {
				getManager().showAlert("Download not availabe for legacy resources stored in center. We recommend removing this resource reuploading as a web resource.");
			} else
				getManager().showAlert("Resource not available: " + fileName);
		}
	}

	private void getHTTPURL(boolean isRelative) {
		StringBuilder sb = new StringBuilder();
		HttpRequestResponse req = service.getPortletManager().getCurrentRequestAction();
		Row r = table.getTable().getActiveRow();
		final String resourcesHTTPPath = AmiWebPages.URL_RESOURCES;
		String fileName = r.getAt(1, Caster_String.INSTANCE);

		sb.append("<a target=\"_blank\" href=\"");
		sb.append("..").append(resourcesHTTPPath).append("/").append(fileName);
		sb.append("\">");
		if (isRelative)
			sb.append("..").append(resourcesHTTPPath).append("/").append(fileName);
		else
			sb.append(HttpUtils.buildUrl(req.getIsSecure(), req.getHost(), req.getPort(), resourcesHTTPPath + "/" + fileName, null));
		sb.append("</a>");

		String html = sb.toString();
		HtmlPortlet htm = new HtmlPortlet(generateConfig(), html);
		getManager().showDialog("HTTP URL", htm, 600, 40);
	}
	private void actionRemove() {
		ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete the selected file?", ConfirmDialogPortlet.TYPE_YES_NO);
		dialog.setCallback("confirm_delete_");
		dialog.addDialogListener(this);
		dialog.updateButton(ConfirmDialogPortlet.ID_YES, "Delete");
		getManager().showDialog("Confirmation", dialog);
	}
	private void removeResource(String path, boolean isCenter) {
		service.getResourcesManager().removeResource(path, isCenter);
	}

	private void actionUpload() {
		getManager().showDialog("Add Resource", new AmiWebAddResourcePortlet(generateConfig(), false), 400, 150);
	}
	private void actionUploadCenter() {
		getManager().showDialog("Add Resource To Center", new AmiWebAddResourcePortlet(generateConfig(), true), 400, 150, true);
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
	public boolean onButton(ConfirmDialog source, String id) {
		String callBack = source.getCallback();
		if (SH.startsWith(callBack, "confirm_delete_")) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				List<Row> selectedRows = table.getTable().getSelectedRows();
				for (int i = 0; i < selectedRows.size(); i++) {
					Row row = selectedRows.get(i);
					String name = row.getAt(1, Caster_String.INSTANCE);
					String loc = row.getAt(5, Caster_String.INSTANCE);
					removeResource(name, OH.eq("Center", loc));
				}
				return true;
			} else
				return true;
		}
		return false;
	}

	@Override
	public void close() {
		service.getResourcesManager().removeListener(this);
		super.close();
	}

	public class AmiWebAddResourcePortlet extends GridPortlet implements FormPortletListener {
		private final FormPortlet form;
		private final FormPortletDivField helpField;
		private final FormPortletTextField targetPathField;
		private final FormPortletFileUploadField uploadField;
		private final FormPortletButton uploadButton;
		private final FormPortletButton cancelButton;
		private final AmiWebService service;
		private boolean isCenter;

		public AmiWebAddResourcePortlet(PortletConfig config, boolean isCenter) {
			super(config);
			this.isCenter = isCenter;
			service = AmiWebUtils.getService(getManager());
			form = new FormPortlet(generateConfig());
			targetPathField = new FormPortletTextField("Target Path");
			uploadField = new FormPortletFileUploadField("");
			helpField = new FormPortletDivField("");
			helpField.setValue("Example: folder/");

			uploadField.setCssStyle("_cna=ami_resource_upload");
			uploadField.setWidthPx(120);
			helpField.setHeightPx(20);

			form.getFormPortletStyle().setLabelsWidth(120);
			form.addField(targetPathField);
			form.addField(helpField);
			form.addField(uploadField);
			this.addChild(form);

			form.addFormPortletListener(this);
			this.uploadButton = form.addButton(new FormPortletButton(isCenter ? "Upload To Center" : "Upload"));
			this.cancelButton = form.addButton(new FormPortletButton("Cancel"));
		}
		private void uploadResource(String path, FileData data) {
			if (path == null || data == null)
				return;
			this.service.getResourcesManager().uploadFile(path + data.getName(), data.getData(), isCenter);
		}

		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
			if (button == this.cancelButton)
				this.close();
			else if (button == this.uploadButton) {
				FileData value = uploadField.getValue();
				uploadResource(SH.noNull(targetPathField.getValue()), value);
				this.close();
			}

		}

		@Override
		public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		}

		@Override
		public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		}

	}

	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}

	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}

	@Override
	public void onResourcesChanged() {
		displayResourcesOnTable();
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.closeButton)
			close();
		else if (button == this.uploadButton) {
			actionUpload();
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

}
