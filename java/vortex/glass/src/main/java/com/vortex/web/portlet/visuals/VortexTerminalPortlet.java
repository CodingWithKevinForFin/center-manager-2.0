package com.vortex.web.portlet.visuals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.console.impl.BasicTelnetAutoCompletion;
import com.f1.container.ResultMessage;
import com.f1.suite.web.WebUser;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.BasicPortletDownload;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletDownload;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.text.FastTextPortlet;
import com.f1.suite.web.portal.impl.text.SimpleFastTextPortlet;
import com.f1.suite.web.portal.impl.text.TextPortletListener;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToAgentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToAgentResponse;
import com.vortex.client.VortexClientMachine;
import com.vortex.web.VortexWebEyeService;

public class VortexTerminalPortlet extends GridPortlet implements FormPortletListener, TextPortletListener {

	private static final int DEFAULT_MAX_RUNTIME_MS = 1000 * 10;
	private DividerPortlet dividerPortlet;
	private OutputPortlet outputPortlet;
	private GridPortlet upperGrid;
	private GridPortlet lowerGrid;
	private FormPortlet inputPortlet;
	private FormPortletTextAreaField inputTextField;
	private FormPortlet machinesPortlet;
	private FormPortletSelectField<String> machinesSelectField;
	private VortexWebEyeService service;
	private FormPortlet usernamePortlet;
	private FormPortletTextField usernameField;
	private FormPortlet pwdPortlet;
	private FormPortletTextField pwdField;
	private boolean allowExit;
	private List<String> history = new ArrayList<String>();
	private int historyPosition = 0;
	private int maxRuntimeMs = DEFAULT_MAX_RUNTIME_MS;
	private boolean isAutofill;
	private String autoFillPartialText;
	private String value;
	private String autoFillPriorValue;
	private int autoFillPosition;
	static final String STYLE_STDIN = "style.color=#66FF66|style.background=#113311";
	static final String STYLE_STDOUT = "style.color=#FFFFFF|style.background=#111111";
	static final String STYLE_STDERR = "style.color=#FF4444|style.background=#330000";
	static final String STYLE_ERROR = "style.color=#000000|style.background=#CC4444|style.fontStyle=italic";
	static final String STYLE_COMMENT = "style.color=#000000|style.background=#8888FF|style.fontStyle=italic";
	private static final int MAX_RESPONSE_BYTES = 1024 * 1024 * 10;
	private static final Logger logger = LH.get(VortexWebEyeService.class);
	public VortexTerminalPortlet(PortletConfig config, String user, String hostMuid, String pwd) {
		super(config);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		this.outputPortlet = new OutputPortlet(generateConfig());
		this.outputPortlet.setStyle("style.background=#111111");
		this.outputPortlet.setLabelStyle("style.background=#333333,style.color=#aaaaaa");
		this.outputPortlet.addListener(this);
		this.inputPortlet = new FormPortlet(generateConfig()).setLabelsWidth(0);
		this.inputTextField = inputPortlet.addField(new FormPortletTextAreaField(""));//.setStyle(STYLE_STDIN);
		this.inputTextField.setCallbackKeys(SH.BYTE_UP_KEY, SH.BYTE_DOWN_KEY, SH.BYTE_TAB_KEY, SH.BYTE_ESC_KEY);
		this.inputPortlet.addFormPortletListener(this);

		this.usernamePortlet = new FormPortlet(generateConfig()).setLabelsWidth(0);
		this.usernameField = this.usernamePortlet.addField(new FormPortletTextField("")).setWidth(60).setValue(user);

		this.machinesPortlet = new FormPortlet(generateConfig()).setLabelsWidth(10);
		this.machinesSelectField = this.machinesPortlet.addField(new FormPortletSelectField<String>(String.class, "@&nbsp;"));
		for (VortexClientMachine i : service.getAgentManager().getAgentMachinesSortedByHostname())
			this.machinesSelectField.addOption(i.getData().getMachineUid(), i.getHostName());
		if (hostMuid != null)
			this.machinesSelectField.setValue(hostMuid);
		else {
			this.machinesSelectField.addOption("", "<No Host Selected>");
			this.machinesSelectField.setValue("");
		}

		this.pwdPortlet = new FormPortlet(generateConfig()).setLabelsWidth(10);
		this.pwdField = this.pwdPortlet.addField(new FormPortletTextField(":&nbsp;")).setWidth(FormPortletTextField.WIDTH_STRETCH);

		pwdField.setValue(OH.noNull(pwd, "/"));
		this.dividerPortlet = new DividerPortlet(generateConfig(), false);
		this.addChild(dividerPortlet, 0, 0);
		this.upperGrid = new GridPortlet(generateConfig());
		this.lowerGrid = new GridPortlet(generateConfig());
		this.dividerPortlet.addChild(upperGrid);
		this.dividerPortlet.addChild(lowerGrid);
		this.upperGrid.addChild(outputPortlet, 0, 0);
		this.lowerGrid.addChild(inputPortlet, 0, 1, 3, 1);
		this.lowerGrid.addChild(usernamePortlet, 0, 0, 1, 1);
		this.lowerGrid.addChild(machinesPortlet, 1, 0, 1, 1);
		this.lowerGrid.addChild(pwdPortlet, 2, 0, 1, 1);
		this.dividerPortlet.setExpandBias(1, 0);
		this.dividerPortlet.setOffsetFromBottomPx(90);
		this.lowerGrid.setColSize(0, 70);
		this.lowerGrid.setColSize(1, 250);
		this.lowerGrid.setRowSize(0, 22);
	}

	public static class Builder extends AbstractPortletBuilder<VortexTerminalPortlet> {
		private static final String ID = "Terminal";
		public Builder() {
			super(VortexTerminalPortlet.class);
			setIcon("portlet_icon_window");
		}
		@Override
		public VortexTerminalPortlet buildPortlet(PortletConfig portletConfig) {
			WebUser user = portletConfig.getPortletManager().getState().getWebState().getUser();
			String userName = null;
			if (user != null)
				userName = user.getUserName();
			VortexTerminalPortlet portlet = new VortexTerminalPortlet(portletConfig, userName, null, "/");
			return portlet;
		}
		@Override
		public String getPortletBuilderName() {
			return "Terminal";
		}
		@Override
		public String getPortletBuilderId() {
			return ID;
		}
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (field == this.inputTextField) {
			String value = this.inputTextField.getValue();
			if (keycode == SH.BYTE_ESC_KEY) {
				this.inputTextField.setValue("");
			} else if (keycode == SH.BYTE_TAB_KEY) {
				int end = value.indexOf(' ', cursorPosition);
				if (end == -1)
					end = value.length();
				int start = value.lastIndexOf(' ', cursorPosition);
				String pwd = getPwd();
				String partial = value.substring(start + 1, end);
				String cmd = ("cd \'" + pwd + "\'\n" + "ls -d " + partial + "*\n");
				byte[] stdin = cmd.getBytes();
				//System.out.println("tab " + cmd);

				VortexAgentRunOsCommandRequest req = nw(VortexAgentRunOsCommandRequest.class);
				req.setCommand("sudo /bin/su - " + this.usernameField.getValue());
				req.setStdin(stdin);
				req.setOwner(this.usernameField.getValue());
				req.setPwd(pwd);
				VortexEyePassToAgentRequest req2 = nw(VortexEyePassToAgentRequest.class);
				req2.setAgentMachineUid(this.machinesSelectField.getValue());
				req2.setAgentRequest(req);
				req2.setComment("ls: " + partial + "*");
				req.setMaxCaptureStderr(MAX_RESPONSE_BYTES);
				req.setMaxCaptureStdout(MAX_RESPONSE_BYTES);
				req.setMaxRuntimeMs(maxRuntimeMs);
				service.sendRequestToBackend(getPortletId(), req2);
				this.isAutofill = true;
				this.autoFillPartialText = partial;
				this.autoFillPriorValue = value;
				this.autoFillPosition = end;

				//Tuple3<Process, byte[], byte[]> res = EH.exec(getManager().getState().getWebState().getPartition().getContainer().getThreadPoolController(), "sudo su - "
				//+ usernameField.getValue(), stdin);
			} else if (keycode == SH.BYTE_UP_KEY) {
				int idx = value.indexOf('\n');
				if (cursorPosition <= idx || idx == -1) {
					if (historyPosition > 0) {
						this.inputTextField.setValue(this.history.get(--historyPosition));
						this.inputTextField.moveCursor(0);
					}
				}
			} else if (keycode == SH.BYTE_DOWN_KEY) {
				int length = value.length();
				int idx = value.lastIndexOf('\n');
				if (cursorPosition >= idx || idx == -1) {
					if (historyPosition < this.history.size() - 1) {
						this.inputTextField.setValue(this.history.get(++historyPosition));
						this.inputTextField.moveCursor(0);
					} else {
						this.inputTextField.setValue("");
					}
				}
			} else if (keycode == SH.BYTE_ENTER_KEY) {
				if (mask == 0) {
					String hostmuid = this.machinesSelectField.getValue();
					if ("".equals(hostmuid)) {
						getManager().showAlert("Select a host first");
						return;
					}
					String text = value;
					if (SH.is(text)) {
						this.history.add(text);
						this.historyPosition = this.history.size();
					} else {
						this.outputPortlet.appendLine("", "", STYLE_STDOUT);
						this.inputTextField.setValue("");
						return;
					}
					this.inputTextField.setValue("");
					if ("history".equals(SH.trim(text))) {
						int i = 0;
						for (String h : history)
							this.outputPortlet.appendLine(SH.toString(i++), h, STYLE_COMMENT);
						return;
					}
					if ("clear".equals(SH.trim(text))) {
						this.outputPortlet.clearLines();
						return;
					}
					if ("exit".equals(SH.trim(text))) {
						if (allowExit)
							close();
						else {
							this.outputPortlet.appendLine("", "exit: Not a dialog window. nothing to do. ", STYLE_COMMENT);
						}
						return;
					}
					String[] parts = SH.splitLines(text);
					if (parts.length > 0) {
						String time = getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.TIME)
								.format(getManager().getState().getWebState().getPartition().getContainer().getTools().getNow());
						this.outputPortlet.appendLine(time + " >>>>", parts[0], STYLE_STDIN);
						for (int i = 1; i < parts.length; i++)
							this.outputPortlet.appendLine("", parts[i], STYLE_STDIN);
						String pwd = getPwd();
						byte[] stdin = ("cd \'" + pwd + "\'\n" + text + "\necho F1MARKER $?\npwd\n").getBytes();

						VortexAgentRunOsCommandRequest req = nw(VortexAgentRunOsCommandRequest.class);
						req.setPwd(pwd);
						req.setCommand("sudo /bin/su - " + this.usernameField.getValue());
						req.setStdin(stdin);
						req.setOwner(this.usernameField.getValue());
						VortexEyePassToAgentRequest req2 = nw(VortexEyePassToAgentRequest.class);
						req2.setAgentMachineUid(this.machinesSelectField.getValue());
						req2.setAgentRequest(req);
						req2.setComment("ex: " + text);
						req.setMaxCaptureStderr(MAX_RESPONSE_BYTES);
						req.setMaxCaptureStdout(MAX_RESPONSE_BYTES);
						req.setMaxRuntimeMs(maxRuntimeMs);
						service.sendRequestToBackend(getPortletId(), req2);
						this.isAutofill = false;

						//Tuple3<Process, byte[], byte[]> res = EH.exec(getManager().getState().getWebState().getPartition().getContainer().getThreadPoolController(), "sudo su - "
						//+ usernameField.getValue(), stdin);
						//String time2 = getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.TIME)
						//.format(getManager().getState().getWebState().getPartition().getContainer().getTools().getNow());

					}
				}
			}
		}
	}
	public void processResponseAutofill(long now, byte[] stdout, byte[] stderr, int exitCode) {
		if (exitCode == 0) {
			String[] parts = SH.splitLines(new String(stdout));
			String commonPrefix = BasicTelnetAutoCompletion.getCommonPrefix(CH.l(parts));
			commonPrefix = SH.stripPrefix(commonPrefix, this.autoFillPartialText, true);
			this.value = this.autoFillPriorValue;
			value = SH.splice(value, this.autoFillPosition, 0, commonPrefix);
			this.inputTextField.setValue(value);
			if (parts.length > 1) {
				for (int i = 0; i < parts.length; i++) {
					parts[i] = SH.afterLast(parts[i], '/', parts[i]);
				}
				this.outputPortlet.appendLine("", "", STYLE_STDOUT);
				List<String> lines = toColumns(parts, this.outputPortlet.getColumnsVisible());
				for (String part : lines) {
					this.outputPortlet.appendLine("", part, STYLE_STDOUT);
				}
			}
		}
		this.autoFillPartialText = null;
		this.autoFillPosition = -1;
		this.autoFillPriorValue = null;

	}
	public void processResponse(long now, byte[] stdout, byte[] stderr) {
		String time2 = getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.TIME).format(now);
		String label = "";
		int trimmed = 0;
		boolean eof = false;
		boolean dirChanged = false;
		if (AH.length(stdout) > 0) {
			String[] lines = SH.splitLines(new String(stdout));
			if (lines.length < 2) {
				this.outputPortlet.appendLine("Error", "Vortex Terminal: Bad Response. Not enough lines", STYLE_ERROR);
			} else {
				String exitCode1 = lines[lines.length - 3];
				String exitCode2 = lines[lines.length - 2];
				String retpwd = lines[lines.length - 1];
				int markerPos = exitCode2.indexOf("F1MARKER ");
				int markerPos1 = exitCode1.indexOf("F1MARKER ");
				LH.info(logger, lines.length, " ", lines[lines.length - 2], lines[lines.length - 1]);

				if (markerPos == -1 && markerPos1 == -1) {
					this.outputPortlet.appendLine("Error", "Vortex Terminal: Bad Response. Missing marker: " + exitCode2, STYLE_ERROR);
				} else {
					String exitCode = exitCode2;
					if (markerPos == -1) {
						exitCode = exitCode1;
						markerPos = markerPos1;
					}

					label = time2 + SH.rightAlign(' ', "[" + SH.afterLast(exitCode, "F1MARKER ") + "]", 5, false);
					int trimLines = 2;
					if (markerPos != 0) {
						trimLines = 1;
						lines[lines.length - 2] = lines[lines.length - 2].substring(0, markerPos);
					}
					for (int i = 0; i < lines.length - trimLines; i++) {
						String s = lines[i];
						if (s.length() > 1000) {
							s = s.substring(0, 1000);
							trimmed++;
						}
						this.outputPortlet.appendLine(label, s, STYLE_STDOUT);
						if (i > 100000) {
							eof = true;
							break;
						}
						label = "";
					}
					if (SH.startsWith(retpwd, '/')) {
						if (!OH.eq(getPwd(), retpwd)) {
							setPwd(retpwd);
							dirChanged = true;
						}
					}
				}
			}
		}
		if (AH.length(stderr) > 0) {
			int i = 0;
			for (String s : SH.splitLines(new String(stderr))) {
				if (s.length() > 1000) {
					s = s.substring(0, 1000);
					trimmed++;
				}
				this.outputPortlet.appendLine(label, s, STYLE_STDERR);
				label = "";
				if (i++ > 100000) {
					eof = true;
					break;
				}
			}
		}
		if (dirChanged)
			this.outputPortlet.appendLine("", "Path of working directory changed: " + getPwd(), STYLE_COMMENT);
		if (trimmed > 0)
			this.outputPortlet.appendLine("Error", "Vortex Terminal: " + trimmed + " line(s) exceeded max length. Partial results returned.", STYLE_ERROR);
		if (eof)
			this.outputPortlet.appendLine("Error", "Vortex Terminal: Too much data returned", STYLE_ERROR);
		if (label != "")
			this.outputPortlet.appendLine(label, "", STYLE_STDOUT);
	}

	private List<String> toColumns(String[] parts, int width) {
		if (parts.length == 0)
			return new ArrayList<String>(0);
		int max = parts[0].length();
		for (int i = 1; i < parts.length; i++)
			max = Math.max(parts[i].length(), max);
		int cols = width / (max + 2);//add 1 to account for spaces
		if (cols == 0)
			cols = 1;
		StringBuilder tmp = new StringBuilder();
		List<String> r = new ArrayList<String>((parts.length + cols - 1) / cols);
		for (int i = 0; i < parts.length;) {
			SH.clear(tmp);
			for (int j = 0; j < cols && i < parts.length; j++, i++) {
				if (j != 0)
					tmp.append("  ");
				SH.leftAlign(' ', parts[i], max, false, tmp);
			}
			r.add(tmp.toString());
		}
		return r;
	}
	public void setPwd(String pwd) {
		//pwd = SH.replaceAll(pwd, ' ', "\\ ");
		this.pwdField.setValue(pwd);
	}

	public void setMachineUid(String string) {
		this.machinesSelectField.setValue(string);
	}

	public void logLocation(String prefix) {
		String time = getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.TIME)
				.format(getManager().getState().getWebState().getPartition().getContainer().getTools().getNow());
		this.outputPortlet.appendLine(time + " ****", prefix + getUserName() + "@" + getMachineHostName() + ":" + getPwd(), STYLE_COMMENT);

	}

	public String getPwd() {
		return pwdField.getValue();
	}

	public String getMachineHostName() {
		return machinesSelectField.getOption(machinesSelectField.getValue()).getName();
	}

	public String getUserName() {
		return usernameField.getValue();
	}

	public boolean isAllowExit() {
		return allowExit;
	}

	public void setAllowExit(boolean allowExit) {
		this.allowExit = allowExit;
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		VortexEyePassToAgentResponse res = (VortexEyePassToAgentResponse) result.getAction();
		if (!res.getOk()) {
			this.outputPortlet.appendLine("Error", "Vortex Eye: " + res.getMessage(), STYLE_ERROR);
			return;
		}
		VortexAgentRunOsCommandResponse res2 = (VortexAgentRunOsCommandResponse) res.getAgentResponse();
		if (!res2.getOk()) {
			this.outputPortlet.appendLine("Error", "Vortex Agent: " + res2.getMessage(), STYLE_ERROR);
			return;
		}
		if (isAutofill)
			processResponseAutofill(res2.getEndTime(), res2.getStdout(), res2.getStderr(), res2.getExitcode());
		else {
			if (AH.length(res2.getStdout()) != res2.getStdoutLength())
				this.outputPortlet.appendLine("Error", "Vortex Terminal: Too much stdout data returned: " + res2.getStdout().length + " exceeds " + res2.getStdoutLength()
						+ " bytes", STYLE_ERROR);
			else
				processResponse(res2.getEndTime(), res2.getStdout(), res2.getStderr());
			if (AH.length(res2.getStderr()) != res2.getStderrLength())
				this.outputPortlet.appendLine("Error", "Vortex Terminal: Too much stderr data returned, displayed " + res2.getStderr().length + " of " + res2.getStderrLength()
						+ " bytes", STYLE_ERROR);
		}
	}

	private static class OutputPortlet extends SimpleFastTextPortlet {

		public OutputPortlet(PortletConfig portletConfig) {
			super(portletConfig);
		}

		@Override
		public void formatText(FastTextPortlet portlet, int lineNumber, StringBuilder sink) {
			Row line = getLine(lineNumber);
			String text = line.getText();
			WebHelper.escapeHtml(text, sink);
			//escapeHtml(text, 0, text.length(), sink);
		}

		@Override
		public WebMenu createMenu(FastTextPortlet fastTextPortlet) {
			return new BasicWebMenu(new BasicWebMenuLink("Download contents", true, "download"), new BasicWebMenuLink("Clear contents", true, "clear"));
		}

	}

	@Override
	public void onTextContextMenu(FastTextPortlet portlet, String id) {
		if (portlet == this.outputPortlet) {
			if ("clear".equals(id)) {
				outputPortlet.clearLines();
			} else if ("download".equals(id)) {
				int lines = outputPortlet.getNumberOfLines(outputPortlet);
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < lines; i++)
					sb.append(outputPortlet.getLine(i).getText()).append(SH.CHAR_CR).append(SH.CHAR_NEWLINE);
				PortletDownload download = new BasicPortletDownload("contents.txt", sb.toString().getBytes());
				getManager().pushPendingDownload(download);
			}
		}
	}

	@Override
	public boolean onTextUserKeyEvent(FastTextPortlet portlet, KeyEvent keyEvent) {
		return false;
	}
}
