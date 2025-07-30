package com.f1.ami.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.web.scm.AmiWebScmBasePortlet;
import com.f1.base.Row;
import com.f1.base.TableListenable;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.DeclaredMethodFactory;

public class AmiWebViewCustomMethodsPortlet extends AmiWebScmBasePortlet implements WebContextMenuFactory, WebContextMenuListener, ConfirmDialogListener {
	private static final Logger log = LH.get();

	public static final String COLUMN_OWNING_LAYOUT = "Owning Layout";
	public static final String COLUMN_METHOD_NAME = "Method Name";
	public static final String COLUMN_PERMISSION = "Permissions";
	public static final String COLUMN_LINECOUNT = "Lines";
	public static final String COLUMN_PARAMCOUNT = "Param Count";
	public static final String COLUMN_METHOD_SIGNATURE = "Method Signature";
	public static final String STATUS_LAYOUT_READONLY = "Read";
	public static final String STATUS_LAYOUT_WRITABLE = "Write";
	public static final String COLUMN_ROW_ID = "Row Id"; // combination of owning layout and method name separated by dot (.)

	private AmiWebService service;
	private AmiWebViewObjectsPortlet objectsPortlet;
	private FastTablePortlet fastTable;
	private TableListenable basic;
	private int methods_count;
	private MapInMap<String, String, Row> name2Row = new MapInMap<String, String, Row>();
	private MapInMap<String, String, DeclaredMethodFactory> name2MethodFactory = new MapInMap<String, String, DeclaredMethodFactory>();

	public AmiWebViewCustomMethodsPortlet(PortletConfig config, AmiWebService service, AmiWebViewObjectsPortlet otp) {
		super(config);
		this.service = service;
		this.objectsPortlet = otp;
		TableListenable basicTable = new BasicTable(
				new String[] { COLUMN_OWNING_LAYOUT, COLUMN_METHOD_NAME, COLUMN_PARAMCOUNT, COLUMN_METHOD_SIGNATURE, COLUMN_PERMISSION, COLUMN_LINECOUNT, COLUMN_ROW_ID });
		BasicWebCellFormatter formatter = new BasicWebCellFormatter();

		this.basic = basicTable;
		this.fastTable = new FastTablePortlet(generateConfig(), this.basic, AmiWebViewObjectsPortlet.CUSTOM_METHOD_TAB_TITLE);
		otp.addTableOptions(this.fastTable);
		this.fastTable.getTable().addColumn(true, COLUMN_OWNING_LAYOUT, COLUMN_OWNING_LAYOUT, formatter).setWidth(300);
		this.fastTable.getTable().addColumn(true, COLUMN_METHOD_NAME, COLUMN_METHOD_NAME, formatter).setWidth(200);
		this.fastTable.getTable().addColumn(true, COLUMN_PARAMCOUNT, COLUMN_PARAMCOUNT, formatter).setWidth(100);
		this.fastTable.getTable().addColumn(true, COLUMN_METHOD_SIGNATURE, COLUMN_METHOD_SIGNATURE, formatter).setWidth(300);
		this.fastTable.getTable().addColumn(true, COLUMN_LINECOUNT, COLUMN_LINECOUNT, formatter).setWidth(100);
		this.fastTable.getTable().addColumn(true, COLUMN_PERMISSION, COLUMN_PERMISSION, formatter).setWidth(100);
		this.fastTable.getTable().addColumn(false, COLUMN_ROW_ID, COLUMN_ROW_ID, formatter).setWidth(100);

		// get all the methods for each layout and create new rows for each
		for (String layoutName : service.getLayoutFilesManager().getFullAliasesByPriority()) {
			List<DeclaredMethodFactory> amiScriptMethodFactories = service.getScriptManager(layoutName).getDeclaredMethodFactories();
			for (DeclaredMethodFactory dmf : amiScriptMethodFactories) {
				populateRowForCustomMethod(newEmptyRow(), layoutName, dmf);
			}
		}

		this.fastTable.getTable().setMenuFactory(this);
		this.fastTable.getTable().addMenuListener(this);

		addChild(this.fastTable, 0, 0);
		this.setRowSize(1, 40);

	}
	public void refresh() {
		this.fastTable.clearRows();
		resetCustomMethodsCount();
		for (String layoutName : service.getLayoutFilesManager().getFullAliasesByPriority()) {
			List<DeclaredMethodFactory> amiScriptMethodFactories = service.getScriptManager(layoutName).getDeclaredMethodFactories();
			for (DeclaredMethodFactory dmf : amiScriptMethodFactories) {
				populateRowForCustomMethod(newEmptyRow(), layoutName, dmf);
			}
		}
	}
	private void populateRowForCustomMethod(Object[] r, String layoutName, DeclaredMethodFactory dmf) {
		if ("".equals(layoutName))
			putNoFire(r, COLUMN_OWNING_LAYOUT, "<root>");
		else
			putNoFire(r, COLUMN_OWNING_LAYOUT, layoutName);

		if (service.getLayoutFilesManager().getLayoutByFullAlias(layoutName).isReadonly())
			putNoFire(r, COLUMN_PERMISSION, STATUS_LAYOUT_READONLY);
		else
			putNoFire(r, COLUMN_PERMISSION, STATUS_LAYOUT_WRITABLE);

		putNoFire(r, COLUMN_METHOD_NAME, dmf.getDefinition().getMethodName());
		putNoFire(r, COLUMN_PARAMCOUNT, dmf.getDefinition().getParamsCount());
		putNoFire(r, COLUMN_METHOD_SIGNATURE, dmf.getDefinition().toString(service.getMethodFactory()));
		putNoFire(r, COLUMN_LINECOUNT, getLineCount(dmf.getBodyText()));
		putNoFire(r, COLUMN_ROW_ID, layoutName + "." + dmf.getDefinition().getMethodName());

		Row row = fastTable.addRow(r);
		name2MethodFactory.putMulti(layoutName, dmf.getDefinition().getMethodName(), dmf);
		name2Row.putMulti(layoutName, dmf.getDefinition().getMethodName(), row);
		this.methods_count++;
	}
	public int getLineCount(String content) {
		return SH.indexOfAll(content + '\n', '\n').length;
	}
	public FastTablePortlet getTablePortlet() {
		return fastTable;
	}
	public Row getRowByLayoutAndMethodName(String layoutName, String methodName) {
		return this.name2Row.getMulti(layoutName, methodName);
	}
	public DeclaredMethodFactory getMethodFactoryByLayoutAndMethodName(String layoutName, String methodName) {
		return this.name2MethodFactory.getMulti(layoutName, methodName);
	}
	public int getCustomMethodsCount() {
		return methods_count;
	}
	public void setCustomMethodsCount(int count) {
		this.methods_count = count;
	}
	public void resetCustomMethodsCount() {
		setCustomMethodsCount(0);
	}
	private void putNoFire(Object r[], String id, Object value) {
		r[this.basic.getColumn(id).getLocation()] = value;
	}
	private Object[] newEmptyRow() {
		return new Object[basic.getColumnsCount()];
	}
	public String getCustomMethodCodeJsonForMethod(String layoutName, String methodName) {
		Map<String, Object> method = new HashMap<String, Object>();
		AmiWebUtils.putAmiScript(method, "amiScriptMethods", getMethodFactoryByLayoutAndMethodName(layoutName, methodName).getText(this.service.getMethodFactory()));
		return AmiWebLayoutHelper.toJson(method, ObjectToJsonConverter.MODE_CLEAN);
	}
	@Override
	public WebMenu createMenu(WebTable table) {
		BasicWebMenu menu = new BasicWebMenu();
		if (this.fastTable.getTable().getSelectedRows().size() == 1) {
			menu.add(new BasicWebMenuLink("Show Configuration", true, AmiWebViewObjectsPortlet.ACTION_SHOW_COFIGURATION));
			menu.add(new BasicWebMenuLink("Refresh", true, "refresh_all"));
		} else if (this.fastTable.getTable().getSelectedRows().size() == 2) {
			menu.add(new BasicWebMenuLink("Diff Configurations", true, AmiWebViewObjectsPortlet.ACTION_DIFF_CONFIGURATIONS));
		} else if (this.fastTable.getTable().getSelectedRows().size() == 0)
			menu.add(new BasicWebMenuLink("Refresh", true, "refresh_all"));
		return menu;

	}
	public Iterable<Tuple3<String, String, Row>> getOwningLayoutAndCustomMethodNames() {
		return (Iterable<Tuple3<String, String, Row>>) this.name2Row.entrySetMulti();
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
		if (action.equals("refresh_all")) {
			refresh();
		} else if (action.equals(AmiWebViewObjectsPortlet.ACTION_SHOW_COFIGURATION)) {
			Row r = table.getSelectedRows().get(0);
			String layoutName = (String) r.get(COLUMN_OWNING_LAYOUT) == "<root>" ? "" : (String) r.get(COLUMN_OWNING_LAYOUT);
			String methodName = (String) r.get(COLUMN_METHOD_NAME);
			AmiWebUtils.showConfiguration(service, getCustomMethodCodeJsonForMethod(layoutName, methodName), "Custom Methods: " + layoutName,
					objectsPortlet.getEscapedSearchText());
		} else if (action.equals(AmiWebViewObjectsPortlet.ACTION_DIFF_CONFIGURATIONS)) {
			Row r1 = table.getSelectedRows().get(0);
			Row r2 = table.getSelectedRows().get(1);

			String leftLayoutName = (String) r1.get(COLUMN_OWNING_LAYOUT) == "<root>" ? "" : (String) r1.get(COLUMN_OWNING_LAYOUT);
			String leftMethodName = (String) r1.get(COLUMN_METHOD_NAME);
			String rightLayoutName = (String) r2.get(COLUMN_OWNING_LAYOUT) == "<root>" ? "" : (String) r2.get(COLUMN_OWNING_LAYOUT);
			String rightMethodName = (String) r2.get(COLUMN_METHOD_NAME);
			String leftDiffTitle = (String) r1.get(COLUMN_OWNING_LAYOUT) + "." + this.name2MethodFactory.getMulti(leftLayoutName, leftMethodName).getDefinition().getMethodName();
			String rightDiffTitle = (String) r2.get(COLUMN_OWNING_LAYOUT) + "."
					+ this.name2MethodFactory.getMulti(rightLayoutName, rightMethodName).getDefinition().getMethodName();
			AmiWebUtils.diffConfigurations(service, getCustomMethodCodeJsonForMethod(leftLayoutName, leftMethodName),
					getCustomMethodCodeJsonForMethod(rightLayoutName, rightMethodName), WebHelper.escapeHtml(leftDiffTitle), WebHelper.escapeHtml(rightDiffTitle),
					objectsPortlet.getEscapedSearchText());
		}
	}
	public Row getSelectedRow(WebTable table) {
		if (table.getSelectedRows().size() > 0)
			return table.getSelectedRows().get(0);
		else
			return null;
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		Row r = getSelectedRow(this.fastTable.getTable());
		if (r != null) {
			String layoutName = (String) r.get(COLUMN_OWNING_LAYOUT);
			String methodName = (String) r.get(COLUMN_METHOD_NAME);
			String layoutNameResolved = "<root>".equals(layoutName) ? "" : layoutName;
			service.getDesktop().showCustomMethodsPortlet(); // important: need to call this before the next line.
			AmiWebMethodPortlet mp = service.getDesktop().getMethodPortlet().getMethodPortlet(layoutNameResolved);
			if (mp != null) {
				DeclaredMethodFactory dmf = this.name2MethodFactory.getMulti(layoutNameResolved, methodName);
				service.getDesktop().getMethodPortlet().getTabs().bringToFront(mp.getPortletId());
				int charPos = dmf.getInner().getPosition();
				mp.getAmiScriptEditor().moveCursor(charPos, true);
				String s = mp.getAmiScriptEditor().getValue();
				int sLine = SH.getLinePosition(s, charPos).getA();
				int eLine = SH.getLinePosition(s, charPos + dmf.getBodyText().length()).getA();
				mp.getAmiScriptEditor().flashRows(sLine, eLine, "yellow");
			} else {
				LH.warning(log, "Error processing double click on custom methods tab in the object browser portlet, layout name: ", layoutName);
			}
		}
	}
	@Override
	public void onClosed() {
		this.fastTable.getTable().setMenuFactory(null);
		this.fastTable.getTable().removeMenuListener(this);
		super.onClosed();
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
