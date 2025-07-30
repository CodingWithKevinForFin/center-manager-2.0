package com.f1.ami.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.amiscript.AmiDebugMessageListener;
import com.f1.ami.web.amiscript.AmiWebAmiScriptDerivedCellParser.AmiWebDeclaredMethodFactory;
import com.f1.ami.web.amiscript.AmiWebScriptRunner;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.base.Row;
import com.f1.http.HttpUtils;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextEditField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DebugPause;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodFactory;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.PauseStack;

public class AmiWebMethodPortlet extends GridPortlet implements FormPortletListener, ConfirmDialogListener, FormPortletContextMenuFactory, FormPortletContextMenuListener,
		AmiDebugMessageListener, AmiWebBreakpointEditor, WebContextMenuListener, WebContextMenuFactory {
	private static final Logger log = LH.get();

	private FormPortlet bodyForm;
	private FastTablePortlet table;
	private AmiWebMethodUsagesTreePortlet usages;
	private AmiWebFormPortletAmiScriptField bodyField;
	private DividerPortlet outerDiv;
	private DividerPortlet leftDiv;
	private AmiWebService service;
	private AmiWebDesktopPortlet desktop;
	private String origAmiScriptMethods;
	private FormPortletTitleField titleField;
	private boolean hasPendingChanges = false;
	private boolean isRunning = false;
	private List<AmiWebDmsImpl> pausedDatamodels;
	private DividerPortlet div;
	private TabPortlet tabsPortlet;
	private HtmlPortlet errorPortlet;
	private AmiWebDebugPortlet debuggerPortlet;
	private double errorDivOffset = .7;
	final private String layoutAlias;
	private Tab tab;

	private int[] lineEnds;

	private AmiWebMethodsPortlet owner;

	private BasicTable methodsTable;

	final private AmiWebScriptManagerForLayout scriptManager;

	private HtmlPortlet advancedPortlet;

	public AmiWebMethodPortlet(PortletConfig config, AmiWebMethodsPortlet parent, AmiWebDesktopPortlet desktop, String layoutAlias) {
		super(config);
		this.methodsTable = new BasicTable();
		methodsTable.addColumn(String.class, "Line");
		methodsTable.addColumn(String.class, "Start");
		methodsTable.addColumn(String.class, "End");
		methodsTable.addColumn(String.class, "Name");
		methodsTable.addColumn(String.class, "Body");
		methodsTable.addColumn(String.class, "Params");
		this.table = new FastTablePortlet(generateConfig(), methodsTable, "Methods");
		this.table.getTable().addMenuListener(this);
		this.table.getTable().setMenuFactory(this);
		this.table.getTable().addColumn(true, "Name", "Name", desktop.getService().getFormatterManager().getBasicFormatter()).setWidth(300);
		this.table.getTable().addColumn(true, "Line", "Line", desktop.getService().getFormatterManager().getBasicFormatter()).setWidth(50);
		this.table.getTable().addColumn(true, "Body", "Body", desktop.getService().getFormatterManager().getBasicFormatter()).setWidth(300);
		this.table.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_HIDDEN, false);
		this.layoutAlias = layoutAlias;
		this.scriptManager = desktop.getService().getScriptManager(this.layoutAlias);
		this.owner = parent;
		this.pausedDatamodels = desktop.getService().getDmManager().getAndPausePlayingDatamodels();
		this.desktop = desktop;
		// this is not empty string if you previously wrote amiscript OR if you imported a layout with amiscript
		this.origAmiScriptMethods = scriptManager.getDeclaredMethodsScript();
		buildMethodsTable();
		this.service = AmiWebUtils.getService(getManager());
		boolean isReadonly = this.service.getLayoutFilesManager().getLayoutByFullAlias(layoutAlias).isReadonly();
		this.bodyForm = new FormPortlet(generateConfig());
		this.errorPortlet = new HtmlPortlet(generateConfig());
		this.advancedPortlet = new HtmlPortlet(generateConfig());
		this.tabsPortlet = new TabPortlet(generateConfig());
		this.tabsPortlet.setIsCustomizable(false);
		this.debuggerPortlet = new AmiWebDebugPortlet(generateConfig());
		this.tabsPortlet.addChild("Errors", this.errorPortlet);
		this.tabsPortlet.addChild("Debugger", this.debuggerPortlet);
		this.tabsPortlet.addChild("Advanced", this.advancedPortlet);
		this.div = new DividerPortlet(generateConfig(), false, this.bodyForm, this.tabsPortlet);
		this.div.setOffset(1);
		this.usages = new AmiWebMethodUsagesTreePortlet(generateConfig(), service, layoutAlias);
		this.leftDiv = new DividerPortlet(generateConfig(), false);
		this.leftDiv.setOffset(.75);
		this.leftDiv.addChild(this.table);
		this.leftDiv.addChild(this.usages);
		this.outerDiv = new DividerPortlet(generateConfig(), true);
		this.outerDiv.addChild(this.leftDiv);
		this.outerDiv.addChild(this.div);
		this.outerDiv.setOffsetPx(350);
		this.addChild(outerDiv, 0, 0);
		this.bodyForm.getFormPortletStyle().setLabelsWidth(10);
		this.titleField = bodyForm.addField(new FormPortletTitleField("Ami Script:" + (isReadonly ? " (readonly layout)" : "")));
		this.bodyField = bodyForm.addField(new AmiWebFormPortletAmiScriptField("", this.service.getPortletManager(), this.layoutAlias));
		this.bodyField.setValue(SH.is(origAmiScriptMethods) ? origAmiScriptMethods : "{\n}");
		this.bodyField.focus();
		//		this.bodyField.addVariable("layout", AmiWebLayoutFile.class);
		//		this.bodyField.addVariable("session", AmiWebService.class);
		//		this.bodyField.addVariable("this", AmiWebService.class);
		this.setSuggestedSize(1000, getManager().getRoot().getHeight() - 50);
		this.bodyField.setHeight(FormPortletField.SIZE_STRETCH);
		this.bodyForm.addFormPortletListener(this);
		this.bodyForm.setMenuFactory(this);
		this.bodyForm.addMenuListener(this);
		this.bodyField.setDisabled(isReadonly);
	}
	private void buildMethodsTable() {
		AmiWebScriptManagerForLayout scriptManager = desktop.getService().getScriptManager(this.layoutAlias);
		String script = scriptManager.getDeclaredMethodsScript();
		MethodFactoryManager mfm = scriptManager.getMethodFactory();
		this.methodsTable.clear();
		for (MethodFactory i : scriptManager.getDeclaredMethodFactories()) {
			AmiWebDeclaredMethodFactory dmf = (AmiWebDeclaredMethodFactory) i;
			ParamsDefinition def = dmf.getDefinition();
			int pos = dmf.getInner().getPosition();
			Tuple2<Integer, Integer> lp = SH.getLinePosition(script, pos);
			methodsTable.getRows().addRow(lp.getA(), dmf.getBodyStart(), dmf.getBodyEnd(), SH.afterFirst(def.toString(mfm), ' '), dmf.getText(mfm), dmf.getDefinition());
		}
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}

	private void setCode(String script, AmiDebugManager debugManager, StringBuilder sink) {
		if (debugManager == null) {
			debugManager = new AmiWebDebugManagerImpl(this.service);
			debugManager.setShouldDebug(AmiDebugMessage.SEVERITY_WARNING, true);
		}
		this.isRunning = true;
		debugManager.addDebugMessageListener(this);
		try {
			this.errorPortlet.setHtml("");
			this.advancedPortlet.setHtml("");
			if (this.div.getOffset() != 1)
				this.errorDivOffset = this.div.getOffset();
			this.div.setOffset(1);
			String oldScript = this.scriptManager.getDeclaredMethodsScript();
			if (!scriptManager.setDeclaredMethods(script, debugManager, sink)) {
				return;
			}
			AmiWebLayoutFile layout = this.desktop.getService().getLayoutFilesManager().getLayoutByFullAlias(this.layoutAlias);
			for (AmiWebLayoutFile l = layout.getParent(); l != null; l = l.getParent()) {
				AmiWebScriptManagerForLayout sm1 = service.getScriptManager(l.getFullAlias());
				String s = sm1.getDeclaredMethodsScript();
				if (!sm1.setDeclaredMethods(s, debugManager, sink)) {
					getManager().showAlert("Custom Methods failed to build for layout: " + AmiWebUtils.formatLayoutAlias(l.getFullAlias()) + "<BR>" + sink);
					scriptManager.setDeclaredMethods(oldScript, debugManager, sink);
					for (AmiWebLayoutFile l2 = layout.getParent(); l2 != null; l2 = l2.getParent()) {
						AmiWebScriptManagerForLayout sm2 = service.getScriptManager(l2.getFullAlias());
						String s2 = sm2.getDeclaredMethodsScript();
						sm2.setDeclaredMethods(s2, debugManager, sink);
					}
					return;
				}
			}
			service.getScriptManager().bindVirtuals();
			service.recompileAmiScript();
		} finally {
			debugManager.removeDebugMessageListener(this);
			this.isRunning = false;
		}
	}
	public String getCode() {
		return bodyField.getValue();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.bodyField) {
			this.bodyField.clearAnnotation();
			updatePendingChanges();
		}
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (keycode == 13 && mask != 0) {
			apply(null, null);
			return;
		}
		if (field == this.bodyField)
			((AmiWebFormPortletAmiScriptField) field).onSpecialKeyPressed(formPortlet, field, keycode, mask, cursorPosition);

	}

	public boolean apply(AmiDebugManager debugManager, StringBuilder errorSink) {
		StringBuilder sink = new StringBuilder();
		this.lineEnds = SH.indexOfAll(getCode() + '\n', '\n');
		setCode(getCode(), debugManager, sink);
		if (sink.length() > 0) {
			if (errorSink != null)
				errorSink.append("Error in Custom Methods: ").append(sink);
			PortletHelper.ensureVisible(this);
			this.bodyField.focus();
			return false;
		} else {
			updatePendingChanges();
			return true;
		}
	}

	public boolean hasPendingChanges() {
		return !this.isRunning && this.hasPendingChanges;
	}

	private void updatePendingChanges() {
		if (hasPendingChanges != OH.ne(this.bodyField.getValue(), this.scriptManager.getDeclaredMethodsScript())) {
			hasPendingChanges = !hasPendingChanges;
			if (hasPendingChanges) {
				this.tab.setTitle(AmiWebUtils.formatLayoutAlias(this.layoutAlias) + " *");
				this.titleField.setValue("Ami Script: <span style='color:#000088'>(ALT + ENTER to apply changed)</span>");
			} else {
				this.tab.setTitle(AmiWebUtils.formatLayoutAlias(this.layoutAlias));
				this.titleField.setValue("Ami Script:");
			}
		}
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("CLOSE".equals(source.getCallback())) {
			if (OH.eq(id, ConfirmDialog.ID_YES)) {
				StringBuilder sink = new StringBuilder();
				setCode(this.origAmiScriptMethods, null, sink);
				if (sink.length() > 0)
					getManager().showAlert("Unexpected error, Could not revert methods: " + sink);
				close();
			}
		}
		return true;
	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		BasicWebMenu r = new BasicWebMenu();
		if (field instanceof AmiWebFormPortletAmiScriptField) {
			((AmiWebFormPortletAmiScriptField) field).resetAutoCompletion();
			return null;
		} else {
			AmiWebMenuUtils.createOperatorsMenu(r, this.service, this.layoutAlias);
			AmiWebMenuUtils.createMemberMethodMenu(r, this.service, this.layoutAlias);
		}
		return r;
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		AmiWebMenuUtils.processContextMenuAction(service, action, (FormPortletTextEditField) node);
	}
	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (KeyEvent.ESCAPE.equals(keyEvent.getKey()))
			return true;
		return super.onUserKeyEvent(keyEvent);
	}
	@Override
	public void onClosed() {
		for (int i = 0; i < pausedDatamodels.size(); i++)
			pausedDatamodels.get(i).setIsPlay(true);
		super.onClosed();
		this.service.getBreakpointManager().onEditorClosed(this);
	}

	@Override
	public void onAmiDebugMessage(AmiDebugManager manager, AmiDebugMessage message) {
		Throwable exc = message.getException();
		if (exc instanceof ExpressionParserException) {
			StringBuilder before = new StringBuilder();
			String after;
			String exception;
			ExpressionParserException epe = (ExpressionParserException) exc;
			this.bodyField.moveCursor(epe.getPosition(), true);
			int line = SH.getLinePosition(this.bodyField.getValue(), epe.getPosition()).getA();
			this.bodyField.flashRows(line, line, "red");
			if (OH.eq(this.bodyField.getValue(), epe.getExpression())) {
				this.bodyField.flashRows(line, line, "red");
				this.bodyField.setAnnotation(line, "error", exc.getMessage());
			}
			if (epe.getExpression() == null) {
				Map<Object, Object> details = message.getDetails();
				String expression = (String) details.get("AmiScript");
				if (expression != null)
					epe.setExpression(expression);
			}
			before.append("Ami Script Error in ").append(message.getTargetCallback()).append("\n\n").append(epe.toLegibleStringBefore());
			exception = epe.toLegibleStringException(true);
			after = epe.toLegibleStringAfter();
			StringBuilder errorMessage = new StringBuilder();

			errorMessage.append("<div class='ami_epe_before'>").append(WebHelper.escapeHtmlNewLineToBr(before.toString())).append("</div>");
			errorMessage.append("<div class='ami_epe_exception'>").append(WebHelper.escapeHtmlNewLineToBr(exception)).append("</div>");
			errorMessage.append("<div class='ami_epe_after'>").append(WebHelper.escapeHtmlNewLineToBr(after)).append("</div>");

			String text = errorMessage.toString();
			this.errorPortlet.setHtml(text);
			this.advancedPortlet.setHtml(HttpUtils.escapeHtmlNewLineToBr(SH.printStackTrace(epe)));
			this.errorPortlet.setCssStyle("_fg=#880000|_bg=#FFFFFF|_fm=left|_fm=monospace" + (SH.is(text) ? "|style.border=1px solid #880000" : "|style.border=4px blue none"));
			this.advancedPortlet.setCssStyle("_fg=#880000|_bg=#FFFFFF|_fm=left|_fm=monospace");
			this.div.setOffset(Math.min(errorDivOffset, .9));
			PortletHelper.ensureVisible(this.errorPortlet);
		}
	}
	@Override
	public void onAmiDebugMessagesRemoved(AmiDebugManager manager, AmiDebugMessage message) {
	}

	public boolean hasChanged() {
		return OH.ne(this.origAmiScriptMethods, getCode());
	}

	public void revertChanges() {
		StringBuilder sink = new StringBuilder();
		this.hasPendingChanges = false;
		this.scriptManager.setDeclaredMethods(this.origAmiScriptMethods, null, sink);
		if (sink.length() > 0)
			getManager().showAlert("Unexpected error, Could not revert methods: " + sink);
		service.getScriptManager().bindVirtuals();
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}
	public boolean isBreakpoint(DerivedCellCalculator statment) {
		if (this.lineEnds == null)
			this.lineEnds = SH.indexOfAll(getCode() + '\n', '\n');

		int line = AH.indexOfSortedGreaterThanEqualTo(statment.getPosition(), this.lineEnds);

		if (this.owner.getDebugState() == AmiWebBreakpointManager.DEBUG_CONTINUE) {
			if (this.bodyField.getBreakpoints().contains(line)) {
				this.bodyField.highlightRow(line);
				PortletHelper.ensureVisible(this);
				//			this.debuggerPortlet.setStack(stack, mf);
				return true;
			} else {
				return false;
			}
		} else if (this.owner.getDebugState() == AmiWebBreakpointManager.DEBUG_STEP_OVER) { //step over
			this.bodyField.highlightRow(line);
			PortletHelper.ensureVisible(this);
			return true;
		} else
			return true;
	}
	public void clearHighlights() {
		this.bodyField.clearHighlight();
		this.debuggerPortlet.clear();
	}
	public void showDebug(AmiWebScriptRunner runner) {
		DebugPause pause = (DebugPause) runner.getStep();
		PauseStack stack = pause.getStack();
		BasicMethodFactory mf = this.scriptManager.getMethodFactory();
		this.debuggerPortlet.setStack(stack, mf, this.getAmiScriptEditor().getValue());
		this.div.setOffset(Math.min(errorDivOffset, .9));
		PortletHelper.ensureVisible(this.debuggerPortlet);
		this.owner.addContinueButton();
		this.owner.addStepoverButton(); //add
	}
	public void setCursorPosition(int position) {
		this.bodyField.setCursorPosition(position);
		int line = SH.getLinePosition(this.bodyField.getValue(), position).getA();
		this.bodyField.scrollToRow(line);
		this.bodyField.flashRows(line, line, "yellow");
	}
	public void recompileAmiScript() {
		this.bodyField.clearAnnotation();
		this.buildMethodsTable();
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("view".equals(action)) {
			List<Row> rows = this.table.getTable().getSelectedRows();
			List<ParamsDefinition> paramDefs = new ArrayList<ParamsDefinition>();
			for (Row row : rows)
				paramDefs.add(row.get("Params", ParamsDefinition.class));
			service.getAmiWebViewMethodsManager().showEditor(this.layoutAlias, paramDefs);
		}
	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
	}
	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {
		if (row != null)
			flashMethod(row);
	}
	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		if (fastWebTable == this.table.getTable()) {
			List<Row> rows = this.table.getTable().getSelectedRows();
			if (rows.size() == 1) {
				Row row = rows.get(0);
				flashMethod(row);
			}
			rebuildUsagesTree();
		}
	}
	public void rebuildUsagesTree() {
		List<Row> rows = this.table.getTable().getSelectedRows();
		List<ParamsDefinition> paramDefs = new ArrayList<ParamsDefinition>();
		for (Row row : rows)
			paramDefs.add(row.get("Params", ParamsDefinition.class));
		usages.buildTree(paramDefs);
	}
	private void flashMethod(Row row) {
		Integer start = row.get("Start", Caster_Integer.INSTANCE);
		Integer end = row.get("End", Caster_Integer.INSTANCE);
		this.bodyField.moveCursor(start, true);
		String s = this.bodyField.getValue();
		int sLine = SH.getLinePosition(s, start).getA();
		int eLine = SH.getLinePosition(s, end).getA();
		this.bodyField.flashRows(sLine, eLine, "yellow");

	}
	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}
	@Override
	public WebMenu createMenu(WebTable table) {
		if (table == this.table.getTable()) {
			BasicWebMenu r = new BasicWebMenu();
			r.add(new BasicWebMenuLink("View in new window (readonly)", true, "view"));
			return r;
		}
		return null;
	}
	public AmiWebFormPortletAmiScriptField getAmiScriptEditor() {
		return this.bodyField;
	}
	public String getOrigAmiScript() {
		return this.origAmiScriptMethods;
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}
	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}
}
