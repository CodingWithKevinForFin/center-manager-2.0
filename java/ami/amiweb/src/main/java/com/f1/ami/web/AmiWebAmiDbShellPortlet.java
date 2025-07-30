package com.f1.ami.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.web.dm.portlets.AmiWebDmViewDataPortlet;
import com.f1.base.Action;
import com.f1.base.Table;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.BackendResponseListener;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet.Callback;
import com.f1.suite.web.portal.impl.HtmlPortletListener;
import com.f1.suite.web.portal.impl.MultiDividerPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.form.Form;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntSet;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebAmiDbShellPortlet extends GridPortlet implements AmiWebSpecialPortlet, FormPortletListener, HtmlPortletListener {

	private static final String BUTTON_STYLE = "_fm=bold|_fg=#FFFFFF|style.border=0px|_cn=ami_test_query_button";
	final private MultiDividerPortlet div;
	final private FormPortlet form;
	final private FormPortlet inputForm;
	final private AmiWebDmViewDataPortlet tableViewerPortlet;
	final private AmiWebService service;
	final private FormPortletTextAreaField inputField;
	final private FormPortletButtonField runButton;
	final private HtmlPortlet resultForm;
	final private FormPortletButtonField upButton;
	final private FormPortletButtonField dnButton;
	final private FormPortletTextField timeoutField;
	final private FormPortletTextField limitField;
	final private FormPortletCheckboxField stringTemplateField;
	final private FormPortletCheckboxField showQueryPlanField;
	final private FormPortletSelectField<String> datasourceField;
	final private IntSet tablesViewed = new IntSet();
	final private IntKeyMap<Table> tables = new IntKeyMap<Table>();
	final private FormPortletButtonField clButton;
	final private int historyStartPos;
	//	final private BasicTable historyTable;
	//	final private FastTablePortlet historyTablePortlet;
	private Map<String, Class> varTypes;
	private Map<String, Object> varValues;
	private byte permissions = AmiCenterQueryDsRequest.PERMISSIONS_FULL;
	private long sessionId = -1;
	private List<String> history = new ArrayList<String>();
	private int historyPosition;
	private boolean isRunning;
	private int nextTableId = 0;
	private int nextTableToDelete = 0;
	private final Pattern SHOW_HISTORY = Pattern.compile("\\W*SHOW\\W+HISTORY\\b(.*)", Pattern.CASE_INSENSITIVE);
	private final Pattern QUIT = Pattern.compile("\\W*QUIT\\W*", Pattern.CASE_INSENSITIVE);
	private final Pattern SETLOCAL = Pattern.compile("\\W*SETLOCAL\\b.*", Pattern.CASE_INSENSITIVE);
	private final Pattern HELP = Pattern.compile("\\W*HELP\\b.*", Pattern.CASE_INSENSITIVE);
	private FormPortletTextField reverseLookupField;
	private FormPortletButtonField srButton;

	public AmiWebAmiDbShellPortlet(AmiWebService service, PortletConfig config) {
		super(config);
		this.history.addAll(service.getShellHistory());
		this.historyPosition = this.history.size();
		this.historyStartPos = this.historyPosition;
		this.service = service;
		this.reverseLookupField = new FormPortletTextField("reverse-search>");
		this.div = new MultiDividerPortlet(generateConfig(), false);
		this.inputForm = new FormPortlet(generateConfig());
		this.form = new FormPortlet(generateConfig());
		this.resultForm = new HtmlPortlet(generateConfig());
		this.resultForm.addListener(this);
		this.resultForm.setJavascript("scrollToBottom()");
		this.tableViewerPortlet = new AmiWebDmViewDataPortlet(generateConfig());
		this.tableViewerPortlet.hideButtons();
		this.inputField = this.inputForm.addField(new FormPortletTextAreaField(""));
		this.inputField.setCallbackKeys(38, 40, (int) 'R', 27);
		this.reverseLookupField.setCallbackKeys(38, 40, 27);
		this.reverseLookupField.setCssStyle("style.fontFamily=courier|_bg=#444444|_fg=#77FF77");
		this.inputField.setLeftTopRightBottom(0, 0, 0, 0);
		RootPortlet root = (RootPortlet) this.service.getPortletManager().getRoot();
		int width = root.getWidth();
		int height = root.getHeight();

		this.clButton = this.form.addField(new FormPortletButtonField("").setValue("Clear"));
		this.clButton.setCssStyle(BUTTON_STYLE);
		this.clButton.setRightPosPx(10).setBottomPosPx(3).setWidthPx(60).setHeightPx(22);
		this.runButton = this.form.addField(new FormPortletButtonField("").setValue("Connecting"));
		this.runButton.setDisabled(true);
		this.limitField = form.addField(new FormPortletTextField("Limit:"));
		//add helper tooltip for limit field
		this.limitField.setHelp("Override&nbsp;max&nbsp;rows&nbsp;the&nbsp;query&nbsp;can&nbsp;Return<BR> Leave&nbsp;blank&nbsp;or&nbsp;use&nbsp;-1&nbsp;for&nbsp;no&nbsp;limit");
		this.timeoutField = form.addField(new FormPortletTextField("Timeout (sec):"));
		if (width < 1920 && height < 1080) {
			this.dnButton = this.form.addField(new FormPortletButtonField("").setValue("Dn"));
			this.dnButton.setRightPosPx(80).setBottomPosPx(3).setWidthPx(30).setHeightPx(22);
			this.upButton = this.form.addField(new FormPortletButtonField("").setValue("Up"));
			this.upButton.setRightPosPx(120).setBottomPosPx(3).setWidthPx(30).setHeightPx(22);
			this.srButton = this.form.addField(new FormPortletButtonField("").setValue("Find"));
			this.srButton.setRightPosPx(160).setBottomPosPx(3).setWidthPx(45).setHeightPx(22);
			this.runButton.setRightPosPx(215).setBottomPosPx(3).setWidthPx(45).setHeightPx(22);
			this.timeoutField.setLeftPosPx(100).setBottomPosPx(3).setWidthPx(55).setHeightPx(22);
			this.limitField.setLeftPosPx(200).setBottomPosPx(3).setWidthPx(55).setHeightPx(22).setLabelWidthPx(164);
			this.stringTemplateField = form.addField(new FormPortletCheckboxField("Str Template:"));
			this.stringTemplateField.setLeftPosPx(350).setBottomPosPx(3).setWidthPx(55).setHeightPx(22).setLabelCssStyle("style.zIndex=-2"); // ensure limit tooltip is clickable
			this.showQueryPlanField = form.addField(new FormPortletCheckboxField("Show Plan:"));
			this.showQueryPlanField.setLeftPosPx(450).setBottomPosPx(3).setWidthPx(55).setHeightPx(22);
			this.datasourceField = form.addField(new FormPortletSelectField<String>(String.class, "DS:"));
			this.datasourceField.setLeftPosPx(500).setBottomPosPx(3).setWidthPx(125).setHeightPx(22);
		} else {
			this.dnButton = this.form.addField(new FormPortletButtonField("").setValue("Dn (Alt+&darr;)"));
			this.dnButton.setRightPosPx(80).setBottomPosPx(3).setWidthPx(80).setHeightPx(22);
			this.upButton = this.form.addField(new FormPortletButtonField("").setValue("Up (Alt+&uarr;)"));
			this.upButton.setRightPosPx(170).setBottomPosPx(3).setWidthPx(80).setHeightPx(22);
			this.srButton = this.form.addField(new FormPortletButtonField("").setValue("Find (Alt+R)"));
			this.srButton.setRightPosPx(260).setBottomPosPx(3).setWidthPx(90).setHeightPx(22);
			this.runButton.setRightPosPx(360).setBottomPosPx(3).setWidthPx(90).setHeightPx(22);
			this.timeoutField.setLeftPosPx(100).setBottomPosPx(3).setWidthPx(55).setHeightPx(22);
			this.limitField.setLeftPosPx(200).setBottomPosPx(3).setWidthPx(55).setHeightPx(22).setLabelWidthPx(164);
			this.stringTemplateField = form.addField(new FormPortletCheckboxField("String Template:"));
			this.stringTemplateField.setLeftPosPx(370).setBottomPosPx(3).setWidthPx(55).setHeightPx(22).setLabelCssStyle("style.zIndex=-2");//ensure limit tooltip is clickable
			this.showQueryPlanField = form.addField(new FormPortletCheckboxField("Show Query Plan:"));
			this.showQueryPlanField.setLeftPosPx(520).setBottomPosPx(3).setWidthPx(55).setHeightPx(22);
			this.datasourceField = form.addField(new FormPortletSelectField<String>(String.class, "Datasource:"));
			this.datasourceField.setLeftPosPx(630).setBottomPosPx(3).setWidthPx(125).setHeightPx(22);
		}
		this.inputField.setBorderWidth(0);
		this.dnButton.setCssStyle(BUTTON_STYLE);
		this.upButton.setCssStyle(BUTTON_STYLE);
		this.srButton.setCssStyle(BUTTON_STYLE);
		this.timeoutField.setValue(SH.toString(service.getDefaultTimeoutMs() / 1000d));
		this.limitField.setValue(SH.toString(service.getDefaultLimit()));
		this.resultForm.setCssStyle("style.fontFamily=courier|_bg=#000000|_fg=#44FF44|style.overflow=scroll");
		this.inputField.setCssStyle("style.fontFamily=courier|_bg=#000000|_fg=#44FF44");
		this.stringTemplateField.setValue(false);
		this.showQueryPlanField.setValue(false);
		for (AmiWebDatasourceWrapper s2 : this.service.getSystemObjectsManager().getDatasources()) {
			datasourceField.addOption(s2.getName(), s2.getName() + " ( " + s2.getAdapter() + " )");
		}
		this.datasourceField.setValueNoThrow("AMI");

		this.div.addChild(inputForm);
		this.div.addChild(resultForm);
		this.div.addChild(this.tableViewerPortlet);
		this.div.setWeights(new double[] { 1, 1, 2 });
		this.div.setThickness(2);
		this.div.setColor("#00000");
		this.addChild(this.div, 0, 0);
		this.addChild(this.form, 0, 1);
		this.setRowSize(1, 26);
		this.form.addFormPortletListener(this);
		this.inputForm.addFormPortletListener(this);
		setRowSize(1, 40);
		setIsRunning(false);
		sendAuth();
	}

	private void sendAuth() {
		AmiCenterQueryDsRequest request = prepareRequest();
		if (request == null)
			return;
		service.sendRequestToBackend(this, request);
	}

	private AmiCenterQueryDsRequest prepareRequest() {
		AmiCenterQueryDsRequest request = getManager().getTools().nw(AmiCenterQueryDsRequest.class);

		int timeout;
		try {
			timeout = (int) (SH.parseDouble(SH.trim(this.timeoutField.getValue())) * 1000);
		} catch (Exception e) {
			getManager().showAlert("Timeout is not in a valid format");
			return null;
		}
		int limit;
		String val = this.limitField.getValue();
		if (SH.isnt(val)) //if empty str, then return -1, consistent with amiscriptCbPortlet
			limit = -1;
		else {
			try {
				limit = SH.parseInt(SH.trim(val));
			} catch (Exception e) {
				getManager().showAlert("Limit is not in a valid format, must be a postive number or blank");
				return null;
			}
			if (limit < 0 && limit != -1) {//for backward compatibility reasons, limit=-1 also works
				getManager().showAlert("Limit cannot be negative, must be a postive number or blank");
				return null;
			}
		}
		request.setLimit(limit);
		request.setTimeoutMs(timeout);
		request.setQuerySessionKeepAlive(true);
		request.setIsTest(this.showQueryPlanField.getBooleanValue());
		request.setInvokedBy(service.getUserName());
		request.setSessionVariableTypes(this.varTypes);
		request.setSessionVariables(this.varValues);
		request.setAllowSqlInjection(this.stringTemplateField.getBooleanValue());
		request.setPermissions(permissions);
		request.setType(AmiCenterQueryDsRequest.TYPE_QUERY);
		request.setOriginType(AmiCenterQueryDsRequest.ORIGIN_FRONTEND_SHELL);
		request.setDatasourceName(this.datasourceField.getValue());
		return request;
	}

	private void setIsRunning(boolean status) {
		this.isRunning = status;
		this.runButton.setDisabled(false);
		if (status) {
			this.runButton.setCssStyle("_fm=bold|_fg=#FFFFFF|style.border=0px|style.borderRadius=5px|_cn=ami_cancel_query_button");
			this.runButton.setValue("Cancel");
		} else {
			this.service.getBreakpointManager().clearHighlights();
			this.runButton.setCssStyle(BUTTON_STYLE);
			RootPortlet root = (RootPortlet) this.service.getPortletManager().getRoot();
			int width = root.getWidth();
			int height = root.getHeight();
			if (width < 1920 && height < 1080) {
				this.runButton.setValue("Run");
			} else {
				this.runButton.setValue("Run (alt+&#9166;)");
			}
		}

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {

	}

	private String current = null;
	private List<String> reverseHistory = new ArrayList<String>();
	private int reverseHistroyPos;

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.runButton) {
			this.inputForm.removeFieldNoThrow(this.reverseLookupField);
			this.inputField.setTopPosPx(0);
			String query = this.inputField.getValue();
			if (SH.isnt(query))
				return;
			if (QUIT.matcher(query).matches()) {
				close();
				return;
			}
			if (SETLOCAL.matcher(query).matches()) {
				getManager().showAlert("setlocal not available in this tool, instead use the options at the bottom of this panel");
				return;
			}
			if (HELP.matcher(query).matches()) {
				getManager().showAlert(
						"<B>QUIT</B> - close this terminal<P><B>SHOW HISTORY <i>pattern</i></B> - show history<P>For help on SQL visit <a target='_blank' href='https://docs.3forge.com/mediawiki/AMI_Realtime_Database'>3forge.com documentation</a>");
				return;
			}
			Matcher m = SHOW_HISTORY.matcher(query);
			if (m.matches()) {
				String pattern = m.group(1);
				pattern = SH.trim(pattern);
				Table table = new BasicTable(String.class, "History");
				if (SH.is(pattern)) {
					table.setTitle("History - " + pattern);
					TextMatcher matcher = SH.m(pattern);
					for (String i : this.history)
						if (matcher.matches(i))
							table.getRows().addRow(i);
				} else {
					table.setTitle("History");
					TextMatcher matcher = SH.m(pattern);
					for (String i : this.history)
						table.getRows().addRow(i);
				}
				this.tableViewerPortlet.clear();
				this.tableViewerPortlet.addTable(table);
				return;
			}
			this.current = null;
			if (this.isRunning) {
				setIsRunning(false);
				return;
			}
			AmiCenterQueryDsRequest request = prepareRequest();
			if (request == null)
				return;
			request.setQuery(query);
			request.setQuerySessionId(this.sessionId);
			service.sendRequestToBackend(this, request);
			this.tableViewerPortlet.clear();
			this.tablesViewed.clear();
			appendOutput("#44ff44", "\n" + SH.trim(query));
			if (this.history.size() == 0 || OH.ne(CH.last(history), query)) {
				this.history.add(query);
				//				this.historyTable.getRows().addRow(query);
			}
			this.historyPosition = this.history.size();
			this.setIsRunning(true);
		} else if (field == this.upButton) {
			if (history.size() == 0 || this.historyPosition == 0)
				return;
			if (historyPosition == this.history.size())
				current = this.inputField.getValue();
			this.historyPosition--;
			this.inputField.setValue(this.history.get(historyPosition));
		} else if (field == this.dnButton) {
			if (history.size() == 0 || this.historyPosition >= this.history.size())
				return;
			this.historyPosition++;
			if (historyPosition == this.history.size())
				this.inputField.setValue(this.current);
			else
				this.inputField.setValue(this.history.get(historyPosition));
		} else if (field == this.clButton) {
			this.resultForm.setHtml("");
			this.tables.clear();
			this.tableViewerPortlet.clear();
			this.tablesViewed.clear();
		} else if (field == this.reverseLookupField) {
			String value = this.reverseLookupField.getValue();
			if (SH.is(value)) {
				this.inputField.setValue("");
				this.reverseHistory.clear();
				Set<String> t = new HashSet<String>();
				for (String s : this.history) {
					if (SH.indexOfIgnoreCase(s, value, 0) != -1 && t.add(s)) {
						this.reverseHistory.add(s);
					}
				}
				this.reverseHistroyPos = this.reverseHistory.size() - 1;
			}
			if (this.reverseHistory.size() == 0)
				this.inputField.setValue("");
			else
				this.inputField.setValue(this.reverseHistory.get(this.reverseHistroyPos));
		} else if (field == this.srButton) {
			if (this.reverseLookupField.getForm() == null) {
				this.inputForm.addField(this.reverseLookupField);
				this.reverseLookupField.setLeftPosPx(105).setTopPosPx(0).setRightPosPx(0).setHeightPx(21);
				this.inputField.setTopPosPx(22);
				this.inputField.setValue("");
			}
			this.reverseLookupField.setValue("");
			this.reverseLookupField.focus();
		}
	}

	private void appendOutput(String color, String txt) {
		if (SH.isnt(txt))
			return;
		StringBuilder sb = new StringBuilder();
		sb.append("<span style='color:").append(color).append("'>");
		WebHelper.escapeHtmlNewLineToBr(txt, sb);
		sb.append("</span>");
		this.resultForm.appendHtml(sb.toString());
		//		this.outputField.setValue(sb.toString());
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (field == this.inputField) {
			if (keycode == 13 && mask == Form.KEY_ALT) {
				onFieldValueChanged(null, this.runButton, null);
				return;
			} else if (keycode == 38 && mask == Form.KEY_ALT) {
				onFieldValueChanged(null, this.upButton, null);
			} else if (keycode == 40 && mask == Form.KEY_ALT) {
				onFieldValueChanged(null, this.dnButton, null);
			} else if (keycode == 'R' && (mask == Form.KEY_CTRL || mask == Form.KEY_ALT)) {
				onFieldValueChanged(null, this.srButton, null);
			}
			if (keycode == 27) {
				this.inputForm.removeFieldNoThrow(this.reverseLookupField);
				this.inputField.setTopPosPx(0);
			}
		} else if (field == this.reverseLookupField) {
			if (keycode == 13 || keycode == 27) {
				this.inputForm.removeFieldNoThrow(this.reverseLookupField);
				this.inputField.setTopPosPx(0);
				if (keycode == 27)
					this.inputField.setValue("");
				this.inputField.focus();
			} else if (keycode == 38) {
				if (this.reverseHistroyPos > 0) {
					this.reverseHistroyPos--;
					this.inputField.setValue(this.reverseHistory.get(this.reverseHistroyPos));
				}
			} else if (keycode == 40) {
				if (this.reverseHistroyPos < this.reverseHistory.size() - 1) {
					this.reverseHistroyPos++;
					this.inputField.setValue(this.reverseHistory.get(this.reverseHistroyPos));
				}
			}
		}

	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		if (result.getError() != null) {
			getManager().showAlert("Internal Error:" + result.getError().getMessage(), result.getError());
			return;
		}
		AmiCenterQueryDsResponse response = (AmiCenterQueryDsResponse) result.getAction();
		if (sessionId == -1) {
			this.sessionId = response.getQuerySessionId();
			if (this.sessionId >= 0) {
				appendOutput("#ffffff", "Successfully connected to Center, Session ID " + this.sessionId);
			}
		}
		StringBuilder sb = new StringBuilder();

		if (response.getOk()) {
			sb.append("<BR>");
			this.inputField.setValue("");
			List<Table> tables = response.getTables();
			if (CH.isntEmpty(tables)) {
				List<Tuple2<String, String>> ts = new ArrayList<Tuple2<String, String>>(tables.size());
				for (Table i : tables) {
					String size = SH.toString(i.getSize() + " x " + i.getColumnsCount());
					ts.add(new Tuple2<String, String>(OH.noNull(i.getTitle(), ""), size));
				}
				sb.append("<span style='color:#ffffff'>");
				for (int i = 0; i < ts.size(); i++) {
					Tuple2<String, String> t = ts.get(i);
					int width = Math.max(t.getA().length() + 4, t.getB().length());
					SH.repeat('-', width, sb.append(i == 0 ? "+" : " +")).append('+');
				}
				sb.append("<BR>");
				for (int i = 0; i < ts.size(); i++) {
					int tableId = nextTableId++;
					Tuple2<String, String> t = ts.get(i);
					int width = Math.max(t.getA().length() + 4, t.getB().length());
					sb.append(i == 0 ? "|" : " |");
					SH.repeat("&nbsp;", (width - t.getA().length() - 4) / 2, sb);
					Callback cb1 = new HtmlPortlet.Callback("show_table").addAttribute("tid", tableId);
					Callback cb2 = new HtmlPortlet.Callback("add_table").addAttribute("tid", tableId);
					sb.append("<a href='#' style='color:#8888ff' onclick='").append(this.resultForm.generateCallback(cb1)).append("'>");
					sb.append(t.getA());
					sb.append("</A>&nbsp(<a href='#' style='color:#8888ff' onclick='").append(this.resultForm.generateCallback(cb2)).append("'>");
					sb.append("+");
					sb.append("</A>)");
					SH.repeat("&nbsp;", (width - t.getA().length() - 4 + 1) / 2, sb);
					sb.append('|');
					Table table = (Table) tables.get(i);
					this.tables.put(tableId, table);
					this.tableViewerPortlet.addTable(table);
					this.tablesViewed.add(tableId);
				}
				sb.append("<BR>");
				for (int i = 0; i < ts.size(); i++) {
					Tuple2<String, String> t = ts.get(i);
					int width = Math.max(t.getA().length() + 4, t.getB().length());
					SH.repeat('-', width, sb.append(i == 0 ? "+" : " +")).append('+');
				}
				sb.append("<BR>");
				for (int i = 0; i < ts.size(); i++) {
					Tuple2<String, String> t = ts.get(i);
					int width = Math.max(t.getA().length() + 4, t.getB().length());
					sb.append(i == 0 ? "|" : " |").append(t.getB());
					SH.repeat("&nbsp;", width - t.getB().length(), sb);
					sb.append('|');
				}
				sb.append("<BR>");
				for (int i = 0; i < ts.size(); i++) {
					Tuple2<String, String> t = ts.get(i);
					int width = Math.max(t.getA().length() + 4, t.getB().length());
					SH.repeat('-', width, sb.append(i == 0 ? "+" : " +")).append('+');
				}
				sb.append("</span><BR>");
			}
			this.resultForm.appendHtml(sb.toString());
			sb.setLength(0);
			if (SH.is(response.getMessage()))
				sb.append(response.getMessage()).append('\n');
			AmiUtils.toMessage(response, service.getFormatterManager().getTimeMillisFormatter().getInner(), sb);
			appendOutput("#ffffff", sb.toString());

			//NOTE This code should be VERY similar to  AmiCenterConsolCmd_Sql
			Class<?> returnType = response.getReturnType();
			boolean hasReturnValue = returnType != null && returnType != Void.class;
			if (hasReturnValue) {
				sb.setLength(0);
				Object returnValue = AmiUtils.getReturnValue(response);
				if (returnValue != null)
					returnType = returnValue.getClass();
				sb.append("(").append(this.service.getScriptManager("").forType(returnType));
				sb.append(")");
				String s = AmiUtils.sJson(returnValue);
				if (s != null && s.indexOf('\n') != -1)
					sb.append('\n');
				sb.append(s);
				appendOutput("#FFAAFF", sb.toString());
			}
		} else {
			sb.setLength(0);
			appendOutput("#ff4444", "\n" + response.getMessage() + "\n");
			sb.setLength(0);
			AmiUtils.toMessage(response, service.getFormatterManager().getTimeMillisFormatter().getInner(), sb);
			appendOutput("#ffffff", sb.toString());
		}

		setIsRunning(false);
	}

	@Override
	public void onUserClick(HtmlPortlet portlet) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserCallback(HtmlPortlet htmlPortlet, String id, int mouseX, int mouseY, HtmlPortlet.Callback cb) {
		if (OH.eq(id, "show_table")) {
			int tableId = (Integer) cb.getAttribute("tid");
			Table table = this.tables.get(tableId);
			if (table == null)
				getManager().showAlert("Table is no longer available");
			this.tableViewerPortlet.clear();
			this.tablesViewed.clear();
			this.tablesViewed.add(tableId);
			this.tableViewerPortlet.addTable(table);
		} else if (OH.eq(id, "add_table")) {
			int tableId = (Integer) cb.getAttribute("tid");
			Table table = this.tables.get(tableId);
			if (table == null)
				getManager().showAlert("Table is no longer available");
			if (this.tablesViewed.add(tableId))
				this.tableViewerPortlet.addTable(table);
		}

	}

	@Override
	public void onHtmlChanged(String old, String nuw) {

	}

	@Override
	public void onClosed() {
		this.service.getShellHistory().addAll(this.history.subList(this.historyStartPos, this.history.size()));
		super.onClosed();
		if (this.sessionId != -1) {
			AmiCenterQueryDsRequest request = getManager().getTools().nw(AmiCenterQueryDsRequest.class);
			request.setDatasourceName("AMI");
			request.setQuerySessionKeepAlive(false);
			request.setQuery(null);
			request.setPermissions(permissions);
			request.setType(AmiCenterQueryDsRequest.TYPE_QUERY);
			request.setOriginType(AmiCenterQueryDsRequest.ORIGIN_FRONTEND_SHELL);
			request.setQuerySessionId(this.sessionId);
			this.sessionId = -1;
			service.sendRequestToBackend((BackendResponseListener) null, request);
		}
	}
}
