package com.f1.ami.web;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.amiscript.AmiDebugMessageListener;
import com.f1.base.Caster;
import com.f1.base.Row;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.text.SimpleFastTextPortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.formatter.BasicTextFormatter;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.FlowControlThrow;

public class AmiWebWarningDialogPortlet extends GridPortlet
		implements FormPortletListener, WebContextMenuListener, WebContextMenuFactory, AmiDebugMessageListener, AmiWebSpecialPortlet {

	private FastTablePortlet errorsTable;
	private AmiWebDebugManagerImpl debugManager;
	private FormPortlet buttonsPortlet;
	private FormPortletButton clearButton;
	private FormPortletButton closeButton;
	private BasicTable debugs;
	private FormPortlet optionsForm;
	private TabPortlet detailsTabPortlet;
	private FormPortletCheckboxField showWarningsOption;
	private FormPortletCheckboxField showInfosOption;
	private String detailsBgColor;
	private int thickness;
	private IdentityHashMap<AmiDebugMessage, Row> messages2rows = new IdentityHashMap<AmiDebugMessage, Row>();
	private BasicWebColumn targetAriCol;
	private BasicWebColumn methodCol;
	private AmiWebService service;
	private FormPortletTextField maxWarningsOption;
	private FormPortletTextField maxInfoOption;

	public AmiWebWarningDialogPortlet(PortletConfig config, AmiWebService service, AmiWebDebugManagerImpl debugManager, boolean includeWarning, boolean includeInfo) {
		super(config);
		this.debugManager = debugManager;
		this.debugManager.addDebugMessageListener(this);
		this.setOptionsForm(new FormPortlet(generateConfig()));
		this.getOptionsForm().addFormPortletListener(this);
		this.showWarningsOption = new FormPortletCheckboxField("Warnings:");
		this.maxWarningsOption = new FormPortletTextField("Max Warnings:");
		this.maxInfoOption = new FormPortletTextField("Max Info:");
		int topPosPx = 4;
		int widthPx = this.showWarningsOption.getDefaultWidth();
		int heightPx = this.showWarningsOption.getDefaultHeight();
		this.showWarningsOption.setLeftPosPx(80).setTopPosPx(topPosPx).setWidthPx(widthPx).setHeightPx(heightPx);
		this.showWarningsOption.setLabelWidthPx(76);
		this.showInfosOption = new FormPortletCheckboxField("Info:");
		showWarningsOption.setValue(includeWarning);
		this.showInfosOption.setLeftPosPx(this.showWarningsOption.getLeftPosPx() + this.showWarningsOption.getWidthPx() + 40).setTopPosPx(topPosPx).setWidthPx(widthPx)
				.setHeightPx(heightPx);
		this.showInfosOption.setLabelWidthPx(36);
		showInfosOption.setValue(includeInfo);

		this.maxWarningsOption.setLeftPosPx(this.showInfosOption.getLeftPosPx() + this.showInfosOption.getWidthPx() + 110).setTopPosPx(topPosPx).setWidth(40).setHeightPx(heightPx);
		this.maxWarningsOption.setLabelWidthPx(200);
		maxWarningsOption.setValue(SH.s(this.debugManager.getMaxMessages(AmiDebugMessage.SEVERITY_WARNING)));

		this.maxInfoOption.setLeftPosPx(this.maxWarningsOption.getLeftPosPx() + this.maxWarningsOption.getWidthPx() + 70).setTopPosPx(topPosPx).setWidth(40).setHeightPx(heightPx);
		this.maxInfoOption.setLabelWidthPx(200);
		maxInfoOption.setValue(SH.s(this.debugManager.getMaxMessages(AmiDebugMessage.SEVERITY_INFO)));

		//		this.debugs = new BasicTable(new String[] { "Seq", "Severity", "Time", "Type", "Panel", "TargetType", "Target", "Message", "Exception", "Data" });
		this.debugs = new BasicTable(new String[] { "Seq", "Severity", "Time", "Type", "TargetType", "TargetAri", "Method", "Message", "Exception", "Data" });

		setErrorsTable(new FastTablePortlet(generateConfig(), new BasicTable(debugs), "Messages"));
		DividerPortlet div = new DividerPortlet(generateConfig(), false);
		div.setThickness(this.getThickness());
		setDetailsTabPortlet(new TabPortlet(generateConfig()));
		getDetailsTabPortlet().setIsCustomizable(false);
		setButtonsPortlet(new FormPortlet(generateConfig()));
		this.addChild(this.getOptionsForm(), 0, 0);
		addChild(div, 0, 1);
		div.addChild(getErrorsTable());
		div.addChild(getDetailsTabPortlet());
		addChild(getButtonsPortlet(), 0, 2);
		setRowSize(0, 26);
		setRowSize(2, 35);
		this.getButtonsPortlet().addFormPortletListener(this);
		MapWebCellFormatter severityFormatter = new MapWebCellFormatter(new BasicTextFormatter());
		severityFormatter.addEntry(AmiDebugMessage.SEVERITY_INFO, "Debug", "_cna=portlet_icon_debug", "&nbsp;&nbsp;&nbsp;&nbsp;Info");
		severityFormatter.addEntry(AmiDebugMessage.SEVERITY_WARNING, "Warning", "_cna=portlet_icon_warning", "&nbsp;&nbsp;&nbsp;&nbsp;Warning");

		getErrorsTable().getTable().setMenuFactory(this);
		this.service = service;
		AmiWebFormatterManager fm = service.getFormatterManager();
		getErrorsTable().getTable().addColumn(true, "Severity", "Severity", severityFormatter).setWidth(70);
		getErrorsTable().getTable().addColumn(true, "Time", "Time", fm.getTimeMillisWebCellFormatter()).setCssColumn("").setWidth(78);
		getErrorsTable().getTable().addColumn(true, "Type", "Type", fm.getBasicFormatter()).setCssColumn("bold").setWidth(150);
		//		getErrorsTable().getTable().addColumn(true, "Panel", "Panel", fm.getDddFormatter()).setWidth(100).setCssColumn("green").setWidth(80);
		//		getErrorsTable().getTable().addColumn(true, "TargetType", "TargetType", fm.getDddFormatter()).setWidth(150);
		getErrorsTable().getTable().addColumn(true, "Target Type", "TargetType", fm.getHtmlWebCellFormatter()).setWidth(120);
		this.targetAriCol = getErrorsTable().getTable().addColumn(true, "DRI (Dashboard Resource Indicator)", "TargetAri", fm.getDddFormatter()).setWidth(200);
		this.methodCol = getErrorsTable().getTable().addColumn(true, "Method", "Method", fm.getDddFormatter()).setWidth(151);
		getErrorsTable().getTable().addColumn(true, "Message", "Message", fm.getDddFormatter()).setWidth(400);
		getErrorsTable().getTable().addColumn(false, "Sequence", "Seq", fm.getDddFormatter()).setWidth(220).setCssColumn("red");
		getErrorsTable().getTable().sortRows("Seq", true, true, false);
		getErrorsTable().getTable().addMenuListener(this);
		setSize(990, 700);
		setSuggestedSize(990, 700);
		this.getErrorsTable().clearRows();
		for (byte i : AmiDebugMessage.SEVERITY_TYPES)
			for (AmiDebugMessage debug : this.debugManager.getMessages(i))
				addRow(debug);
		clearButton = getButtonsPortlet().addButton(new FormPortletButton("Clear All Warnings"));
		closeButton = getButtonsPortlet().addButton(new FormPortletButton("Close Dialog"));
		this.getOptionsForm().addField(showWarningsOption);
		this.getOptionsForm().addField(showInfosOption);
		this.getOptionsForm().addField(maxWarningsOption);
		this.getOptionsForm().addField(maxInfoOption);
		showWarningsOption.setCssStyle("style.display=inline-block|style.marginRight=8px");
		showInfosOption.setCssStyle("style.display=inline-block");
		maxWarningsOption.setCssStyle("style.display=inline-block");
		maxInfoOption.setCssStyle("style.display=inline-block");
		this.optionsForm.setHtmlLayout(showWarningsOption.getHtmlLayoutSignature() + showInfosOption.getHtmlLayoutSignature());
		updateClearButton();
		updateFilter();
	}
	public void showWarnings(boolean warnings) {
		showWarningsOption.setValue(warnings);
		updateFilter();
	}

	public void showInfo(boolean info) {
		showInfosOption.setValue(info);
		updateFilter();
	}

	private void addRow(AmiDebugMessage debug) {
		String type;
		type = AmiDebugMessage.getTypeAsString(debug.getType());
		String exception = debug.getException() == null ? null : debug.getException().getClass().getSimpleName();
		String ari = debug.getTargetAri();
		String ariType = SH.beforeFirst(ari, ':');
		String ariTarget = SH.afterFirst(ari, ':');
		if (ariType != null) {
			String icon = AmiWebAmiObjectsVariablesHelper.getAmiIconForDomObjectType(ariType);
			ariType = "<image src='" + icon + "'/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + ariType;
		}
		Row row = getErrorsTable().addRow(debug.getSeqNum(), debug.getSeverity(), debug.getTime(), "  " + type, ariType, ariTarget, debug.getTargetCallback(),
				SH.noNull(SH.beforeFirst(debug.getMessage(), '\n')), exception, debug);
		this.messages2rows.put(debug, row);
	}

	private void removeRow(AmiDebugMessage debug) {
		Row row = this.messages2rows.remove(debug);
		if (row != null)
			getErrorsTable().removeRow(row);
	}
	private void updateClearButton() {
		boolean showInfos = showInfosOption.getValue();
		boolean showWarnings = showWarningsOption.getValue();
		if (clearButton != null) {
			this.clearButton.setEnabled(true);
			if (showInfos && showWarnings)
				this.clearButton.setName("Clear all Warnings And Info");
			else if (showInfos)
				this.clearButton.setName("Clear all Info");
			else if (showWarnings)
				this.clearButton.setName("Clear all Warnings");
			else
				this.clearButton.setEnabled(false);
		}
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == closeButton) {
			close();
		} else if (button == clearButton) {
			boolean showInfos = showInfosOption.getValue();
			boolean showWarnings = showWarningsOption.getValue();
			if (showWarnings)
				this.debugManager.clearMessages(AmiDebugMessage.SEVERITY_WARNING);
			if (showInfos)
				this.debugManager.clearMessages(AmiDebugMessage.SEVERITY_INFO);
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.showInfosOption || field == this.showWarningsOption)
			updateFilter();
		else if (field == this.maxWarningsOption) {
			Integer nwMax = Caster_Integer.INSTANCE.castOr(field.getValue(), null);
			if (nwMax == null)
				nwMax = this.debugManager.getMaxMessages(AmiDebugMessage.SEVERITY_WARNING);
			this.debugManager.setMaxMessages(AmiDebugMessage.SEVERITY_WARNING, nwMax);

		} else if (field == this.maxInfoOption) {
			Integer nwMax = Caster_Integer.INSTANCE.castOr(field.getValue(), null);
			if (nwMax == null)
				nwMax = this.debugManager.getMaxMessages(AmiDebugMessage.SEVERITY_INFO);
			this.debugManager.setMaxMessages(AmiDebugMessage.SEVERITY_INFO, nwMax);

		}
	}
	private void updateFilter() {
		Set<String> s = new HashSet<String>();
		if (this.showInfosOption.getValue())
			s.add("Debug");
		if (this.showWarningsOption.getValue())
			s.add("Warning");
		this.errorsTable.getTable().setFilteredIn("Severity", s);
		updateClearButton();

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	private static final Caster<AmiDebugMessage> CASTER_AMI_WEB_DEBUG_MESSAGE = OH.getCaster(AmiDebugMessage.class);

	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("clear".equals(action)) {
			List<Row> selected = table.getSelectedRows();
			for (Row row : selected) {
				AmiDebugMessage em = row.get("Data", CASTER_AMI_WEB_DEBUG_MESSAGE);
				debugManager.removeMessage(em);
			}
		}

	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
	}

	@Override
	public void onSelectedChanged(FastWebTable table) {
		List<Row> rows = table.getSelectedRows();
		Tab current = this.getDetailsTabPortlet().getSelectedTab();
		String currentLocation = current != null ? current.getTitle() : null;
		this.detailsTabPortlet.removeAndCloseAllChildren();
		if (CH.isntEmpty(rows)) {
			AmiDebugMessage em = rows.get(0).get("Data", CASTER_AMI_WEB_DEBUG_MESSAGE);
			Map<Object, Object> details = em.getDetails();
			if (em.getException() instanceof FlowControlThrow) {
				HtmlPortlet html = new HtmlPortlet(generateConfig());
				AmiWebUtils.toHtml(this.service, (FlowControlThrow) em.getException(), html);
				html.setCssStyle("style.overflow=scroll");
				addDetailsTab("Stack", html);
			}
			if (!details.containsKey("Message") && SH.is(em.getMessage()) && (em.getMessage().length() > 100 || em.getMessage().indexOf('\n') != -1))
				addDetailsTab("Message", em.getMessage());
			if (!details.containsKey("Exception (Advanced)") && em.getException() != null)
				addDetailsTab("Exception (Advanced)", SH.printStackTrace(em.getException()));
			for (Entry<Object, Object> e : details.entrySet())
				addDetailsTab(AmiUtils.s(e.getKey()), AmiUtils.s(e.getValue()));

			if (currentLocation != null) {
				Tab t = this.detailsTabPortlet.getTabByName(currentLocation);
				if (t != null)
					this.detailsTabPortlet.setActiveTab(t.getPortlet());
			}
		}
	}
	private void addDetailsTab(String title, String message) {
		SimpleFastTextPortlet tab = new SimpleFastTextPortlet(generateConfig());
		tab.setLines(message);
		addDetailsTab(title, tab);
	}
	private void addDetailsTab(String title, Portlet portlet) {
		this.detailsTabPortlet.addChild(title, portlet);
		getManager().onPortletAdded(portlet);
	}
	@Override
	public WebMenu createMenu(WebTable table) {
		BasicWebMenu r = new BasicWebMenu();
		r.addChild(new BasicWebMenuLink("Clear Message(s)", true, "clear"));
		return r;
	}

	public FastTablePortlet getErrorsTable() {
		return errorsTable;
	}

	public void setErrorsTable(FastTablePortlet errorsTable) {
		this.errorsTable = errorsTable;
	}

	public FormPortlet getButtonsPortlet() {
		return buttonsPortlet;
	}

	public void setButtonsPortlet(FormPortlet buttonsPortlet) {
		this.buttonsPortlet = buttonsPortlet;
	}

	public FormPortlet getOptionsForm() {
		return optionsForm;
	}

	public void setOptionsForm(FormPortlet optionsForm) {
		this.optionsForm = optionsForm;
	}

	public TabPortlet getDetailsTabPortlet() {
		return detailsTabPortlet;
	}

	public void setDetailsTabPortlet(TabPortlet detailsTabPortlet) {
		this.detailsTabPortlet = detailsTabPortlet;
	}

	public String getDetailsBgColor() {
		return detailsBgColor;
	}

	public void setDetailsBgColor(String detailsBgColor) {
		this.detailsBgColor = detailsBgColor;
	}

	public int getThickness() {
		return thickness;
	}

	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	@Override
	public void onClosed() {
		this.debugManager.removeDebugMessageListener(this);
		super.onClosed();
	}

	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {

	}

	@Override
	public void onAmiDebugMessage(AmiDebugManager manager, AmiDebugMessage message) {
		if (manager != this.debugManager)
			return;
		addRow(message);

	}

	@Override
	public void onAmiDebugMessagesRemoved(AmiDebugManager manager, AmiDebugMessage message) {
		if (manager != this.debugManager)
			return;
		this.removeRow(message);
	}

	public void removeButtons() {
		this.removeChild(this.buttonsPortlet.getPortletId());
		this.removeChild(this.optionsForm.getPortletId());
		this.errorsTable.getTable().setFilteredIn("Severity", (Set) null);
		this.setRowSize(0, 0);

	}
	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		Row row = this.errorsTable.getTable().getActiveRow();
		if (row != null) {
			AmiDebugMessage em = row.get("Data", CASTER_AMI_WEB_DEBUG_MESSAGE);
			String ari = em.getTargetAri();
			String callback = em.getTargetCallback();
			if (ari == null)
				return;
			this.service.getDomObjectsManager().showCallbackEditor(ari, callback);
		}
	}
	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}
}
