package com.f1.ami.web;

import java.util.List;
import java.util.Map;

import com.f1.ami.web.AmiWebAutosaveManager.AutoSaveFile;
import com.f1.base.Row;
import com.f1.base.TableListenable;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebAutosaveManagerPortlet extends GridPortlet implements FormPortletListener, WebContextMenuListener {

	private final AmiWebDesktopPortlet desktop = AmiWebUtils.getService(getManager()).getDesktop();
	private final TableListenable basic = new BasicTable(new String[] { COL_NAME_REVISION, COL_NAME_LAYOUT_NAME, COL_NAME_TIMESTAMP, COL_NAME_REASON });
	private final FastTablePortlet fastTablePortlet = new FastTablePortlet(generateConfig(), this.basic, "Autosaved Layouts");
	private final FormPortlet buttonsForm = new FormPortlet(generateConfig());
	private final FormPortletButton openButton = this.buttonsForm.addButton(new FormPortletButton("Open"));
	private final FormPortletButton diffButton = this.buttonsForm.addButton(new FormPortletButton("View Changes"));
	private final FormPortletButton diffTwoButton = this.buttonsForm.addButton(new FormPortletButton("Diff Selected"));

	private final static int COL_NUM_REVISION = 0;
	private final static int COL_NUM_LAYOUT_NAME = 1;
	private final static int COL_NUM_TIMESTAMP = 2;
	private final static int COL_NUM_REASON = 3;

	private final static String COL_NAME_REVISION = "Revision";
	private final static String COL_NAME_LAYOUT_NAME = "Name";
	private final static String COL_NAME_TIMESTAMP = "Timestamp";
	private final static String COL_NAME_REASON = "Reason";

	private final AmiWebService service;

	public AmiWebAutosaveManagerPortlet(PortletConfig config) {
		super(config);
		this.service = AmiWebUtils.getService(getManager());

		// Populate basic table
		Row row;
		for (AutoSaveFile asf : this.service.getAutosaveManager().getHistory()) {
			row = this.basic.newEmptyRow();
			row.putAt(COL_NUM_REVISION, asf.getNumber());
			row.putAt(COL_NUM_LAYOUT_NAME, asf.getLayoutName());
			row.putAt(COL_NUM_TIMESTAMP, asf.getTimestamp());
			row.putAt(COL_NUM_REASON, asf.getReason());
			this.basic.getRows().add(row);
		}

		// Set up fast table portlet
		addChild(this.fastTablePortlet, 0, 0);
		BasicWebCellFormatter formatter = new BasicWebCellFormatter();
		BasicWebCellFormatter dateFormatter = this.desktop.getService().getFormatterManager().getDateTimeSecsWebCellFormatter();
		this.fastTablePortlet.getTable().addColumn(true, COL_NAME_REVISION, COL_NAME_REVISION, formatter);
		this.fastTablePortlet.getTable().addColumn(true, COL_NAME_LAYOUT_NAME, COL_NAME_LAYOUT_NAME, formatter);
		this.fastTablePortlet.getTable().addColumn(true, COL_NAME_TIMESTAMP, COL_NAME_TIMESTAMP, dateFormatter);
		this.fastTablePortlet.getTable().addColumn(true, COL_NAME_REASON, COL_NAME_REASON, formatter).setWidth(110);
		this.fastTablePortlet.getTable().sortRows(COL_NAME_TIMESTAMP, false, true, false);
		this.fastTablePortlet.getTable().addMenuListener(this);
		AmiWebUtils.applyEndUserTableStyle(this.fastTablePortlet);

		addChild(this.buttonsForm, 0, 1);
		this.buttonsForm.addFormPortletListener(this);
		setRowSize(1, 40);
		setSuggestedSize(445, 300);
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		openSelectedLayout();
	}

	private void openSelectedLayout() {
		List<Row> selectedRows = this.fastTablePortlet.getTable().getSelectedRows();
		if (selectedRows.size() == 1) {
			Row row = selectedRows.get(0);
			String layoutname = (String) row.getAt(COL_NUM_LAYOUT_NAME);
			int rev = row.getAt(COL_NUM_REVISION, Caster_Integer.INSTANCE);
			AutoSaveFile layout = this.service.getAutosaveManager().getLayout(rev);
			if (layout == null) {
				getManager().showAlert("Error loading revision " + rev + ", your history file may be corrupted");
				return;
			}
			service.getLayoutFilesManager().loadLayoutDialog(layoutname, layout.getLayout(), null);
		}
	}
	private void diffSelectedLayout() {
		try {
			List<Row> selectedRows = this.fastTablePortlet.getTable().getSelectedRows();
			if (selectedRows.size() == 1) {

				// Get selected Autosave
				Row row = selectedRows.get(0);
				int rev = row.getAt(COL_NUM_REVISION, Caster_Integer.INSTANCE);
				AutoSaveFile autosave = this.service.getAutosaveManager().getLayout(rev);
				AmiWebLayoutFile autoSaveLayout = null;
				autoSaveLayout = this.service.getLayoutFilesManager().getLayoutFileFromAutosave(autosave);

				AmiWebDiffersPortlet dp = new AmiWebDiffersPortlet(this.service, generateConfig());
				dp.addTabCompareToCurrent(autoSaveLayout, "Current", "AutoSaved");
				this.service.getDesktop().getManager().showDialog("Diff Current Against AutoSave", dp);
			}
		} catch (Exception e) {
			getManager().showAlert("Failed to diff against autosave: ", e);
			return;
		}
	}
	private void diffTwoSelectedLayouts() {
		try {
			List<Row> selectedRows = this.fastTablePortlet.getTable().getSelectedRows();
			if (selectedRows.size() == 2) {
				// Get selected Autosave
				Row row = selectedRows.get(0);
				Row row2 = selectedRows.get(1);
				int rev = row.getAt(COL_NUM_REVISION, Caster_Integer.INSTANCE);
				int rev2 = row2.getAt(COL_NUM_REVISION, Caster_Integer.INSTANCE);
				AutoSaveFile autosave = this.service.getAutosaveManager().getLayout(rev);
				AutoSaveFile autosave2 = this.service.getAutosaveManager().getLayout(rev2);
				AmiWebLayoutFile autoSaveLayout = null;
				AmiWebLayoutFile autoSaveLayout2 = null;
				autoSaveLayout = this.service.getLayoutFilesManager().getLayoutFileFromAutosave(autosave);
				autoSaveLayout2 = this.service.getLayoutFilesManager().getLayoutFileFromAutosave(autosave2);

				AmiWebDiffersPortlet dp = new AmiWebDiffersPortlet(this.service, generateConfig());
				dp.addTab(autoSaveLayout, autoSaveLayout2, "Autosave " + autosave.getNumber(), "Autosave " + autosave2.getNumber());
				this.service.getDesktop().getManager().showDialog("Diff Two AutoSaves", dp);
			}
		} catch (Exception e) {
			getManager().showAlert("Failed to diff against autosave: ", e);
			return;
		}

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.openButton) {
			openSelectedLayout();
		} else if (button == this.diffButton) {
			diffSelectedLayout();
		} else if (button == this.diffTwoButton) {
			diffTwoSelectedLayouts();
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
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
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}

}
