package com.f1.ami.web.dm.portlets.vizwiz;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebAddObjectColumnFormPortlet;
import com.f1.ami.web.AmiWebCustomColumn;
import com.f1.ami.web.AmiWebDatasourceTablePortlet;
import com.f1.ami.web.AmiWebDesktopPortlet;
import com.f1.ami.web.AmiWebEditStylePortlet;
import com.f1.ami.web.AmiWebSearchColumnsPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.base.Row;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.WebTableColumnContextMenuListener;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.tree.impl.ArrangeColumnsPortlet;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.stack.SingletonCalcTypes;

public class AmiWebVizwiz_Table extends AmiWebVizwiz<AmiWebDatasourceTablePortlet> implements WebContextMenuListener, WebTableColumnContextMenuListener {

	private TabPortlet creatorTabsPortlet;
	private GridPortlet columnGrid;
	private HtmlPortlet blankPortlet;
	private AmiWebAddObjectColumnFormPortlet columnEditor;
	private final FormPortletButton arrangeColumnsButton;
	private final FormPortletButton autosizeColumnsButton;
	private final FormPortletButton searchColumnsButton;

	public AmiWebVizwiz_Table(AmiWebService service, String layoutAlias) {
		super(service, "Table");
		this.creatorTabsPortlet = new TabPortlet(generateConfig());
		this.creatorTabsPortlet.getTabPortletStyle().setBackgroundColor("#4c4c4c");
		AmiWebDesktopPortlet desktop = service.getDesktop();
		AmiWebDatasourceTablePortlet tablePortlet = (AmiWebDatasourceTablePortlet) desktop.newPortlet(AmiWebDatasourceTablePortlet.Builder.ID, layoutAlias);
		setPreviewPortlet(tablePortlet);
		this.columnGrid = new GridPortlet(generateConfig());
		this.columnGrid.addChild(blankPortlet = new HtmlPortlet(generateConfig(), "<BR><center>Click a column in the table to edit."), 0, 0);
		this.blankPortlet.setCssStyle("_bg=#e2e2e2|style.fontSize=20px");
		this.creatorTabsPortlet.addChild("Column Editor", columnGrid);
		this.creatorTabsPortlet.addChild("Style", (new AmiWebEditStylePortlet(getPreviewPortlet().getStylePeer(), generateConfig())).hideCloseButtons(true));
		this.creatorTabsPortlet.setIsCustomizable(false);
		addButtons(CH.l(this.arrangeColumnsButton = new FormPortletButton("Arrange Columns"), this.autosizeColumnsButton = new FormPortletButton("Auto-size Columns"),
				this.searchColumnsButton = new FormPortletButton("Search Columns")));
		this.getPreviewPortlet().getTable().addMenuListener(this);
	}
	@Override
	public Portlet getCreatorPortlet() {
		return this.creatorTabsPortlet;
	}

	@Override
	public boolean initDm(AmiWebDm dm, Portlet initForm, String dmName) {
		StringBuilder errorSink = new StringBuilder();
		AmiWebDatasourceTablePortlet previewPortlet = getPreviewPortlet();
		int col = previewPortlet.getTable().getVisibleColumnsCount();
		FormPortlet fp = (FormPortlet) initForm;

		Set<String> names = new HashSet<String>();
		Set<String> ids = new HashSet<String>();
		AmiWebDmTableSchema schema2 = dm.getResponseOutSchema().getTable(dmName);
		previewPortlet.setAmiTitle(dmName, false);
		previewPortlet.setUsedDatamodel(dm.getAmiLayoutFullAliasDotId(), dmName);
		previewPortlet.setUserDefinedVariables(schema2.getClassTypes());
		for (String var : schema2.getColumnNames()) {
			Class<?> type = schema2.getClassTypes().getType(var);
			String name = AmiWebUtils.toPrettyName(var);
			names.add(name = SH.getNextId(name, names));
			byte t = AmiWebUtils.guessColumnType(type, name);
			int precision = AmiWebUtils.guessColumnPrecision(type, name);
			String escapeVarName = AmiUtils.escapeVarName(var);
			String id = AmiUtils.toValidVarName(var);
			ids.add(id = SH.getNextId(id, ids));
			AmiWebCustomColumn cc = new AmiWebCustomColumn(previewPortlet, id, name, t, precision, escapeVarName, null, null, null, null, null, false, null, false);
			if (!previewPortlet.addCustomColumn(cc, errorSink, col++, null, new SingletonCalcTypes(var, type), false)) {
				getService().getPortletManager().showAlert(errorSink.toString());
				return false;
			}
		}
		previewPortlet.onDmDataChanged(dm);
		for (int i = 0; i < previewPortlet.getTable().getVisibleColumnsCount(); i++) {
			previewPortlet.getTablePortlet().autoSizeColumn(previewPortlet.getTable().getVisibleColumn(i));
		}
		return true;
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
	}
	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn column) {
		AmiWebDatasourceTablePortlet previewPortlet = getPreviewPortlet();
		// if there is no change then no need to update
		if (this.columnEditor != null && this.columnEditor.isChanged()) {
			this.columnEditor.onUpdate(true);
			this.columnGrid.removeChildAt(0, 0);
			this.columnGrid.addChild(blankPortlet, 0, 0);
		}
		AmiWebCustomColumn col = previewPortlet.getCustomDisplayColumn(column.getColumnId());
		if (!previewPortlet.checkCanRemoveCustomColumnById(col.getColumnId()))
			return;
		if (previewPortlet.canEditColumn(col)) {
			if (previewPortlet.getTable().getFilteredInColumns().contains(col.getColumnId())) {
				return;
			}

			AmiWebAddObjectColumnFormPortlet t = new AmiWebAddObjectColumnFormPortlet(generateConfig(), previewPortlet, table.getColumnPosition(column.getColumnId()), col);
			//			t.init(null);
			Portlet old = this.columnGrid.removeChildAt(0, 0);
			if (old != this.blankPortlet)
				old.close();
			this.columnGrid.addChild(t, 0, 0);
			this.getService().getPortletManager().onPortletAdded(t);
			this.columnEditor = t;
			this.columnEditor.getForm().addField(new FormPortletTitleField(column.getColumnName()), 0).setCssStyle("");
			PortletHelper.ensureVisible(t);
		}
	}
	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
	}
	@Override
	public void onColumnContextMenu(WebTable table, WebColumn column, String action) {
	}
	@Override
	public AmiWebDatasourceTablePortlet removePreviewPortlet() {
		super.getPreviewPortlet().getTable().removeMenuListener(this);
		return super.removePreviewPortlet();
	}
	@Override
	public String getName() {
		return "Table";
	}

	@Override
	public void onButton(FormPortletButton button) {
		AmiWebDatasourceTablePortlet t = this.getPreviewPortlet();
		if (button == this.arrangeColumnsButton)
			t.getManager().showDialog("Arrange Columns", new ArrangeColumnsPortlet(t.generateConfig(), t.getTable()));
		else if (button == this.autosizeColumnsButton)
			t.getTablePortlet().autoSizeAllColumns();
		else if (button == this.searchColumnsButton)
			t.getManager()
					.showDialog("Search Columns",
							new AmiWebSearchColumnsPortlet(t.generateConfig(), t.getTable(), t.getTable().getVisibleColumn(0))
									.setStyle(t.getManager().getStyleManager().getFormStyle()),
							AmiWebSearchColumnsPortlet.DIALOG_W, AmiWebSearchColumnsPortlet.DIALOG_H)
					.setStyle(t.getService().getUserDialogStyleManager());
		super.onButton(button);
	}
	@Override
	public void onUserDblclick(FastWebColumns table, String action, Map<String, String> properties) {

	}
	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}
	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}
}
