package com.f1.ami.web.amiscript;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiCenterDefinition;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterPassToRelayRequest;
import com.f1.ami.amicommon.msg.AmiCenterPassToRelayResponse;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandResponse;
import com.f1.ami.amicommon.msg.AmiRelaySendEmailRequest;
import com.f1.ami.amicommon.msg.AmiRelaySendEmailResponse;
import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.amiscript.AmiCalcFrameStack;
import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebCommandWrapper;
import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebDevTools;
import com.f1.ami.web.AmiWebLayoutFile;
import com.f1.ami.web.AmiWebLayoutHelper;
import com.f1.ami.web.AmiWebLayoutManager;
import com.f1.ami.web.AmiWebManager;
import com.f1.ami.web.AmiWebNotification;
import com.f1.ami.web.AmiWebRpcRequest;
import com.f1.ami.web.AmiWebRpcResponse;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebTimedEvent;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.auth.AmiSsoSession;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.style.AmiWebStyle;
import com.f1.base.Bytes;
import com.f1.base.CalcFrame;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Password;
import com.f1.base.Table;
import com.f1.http.HttpRequestResponse;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.WebStatesManager;
import com.f1.suite.web.portal.BasicPortletDownload;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.RootPortletDialog;
import com.f1.suite.web.portal.style.PortletStyleManager_Dialog;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.GuidHelper;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.WebRectangle;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebScriptMemberMethods_Session extends AmiWebScriptBaseMemberMethods<AmiWebService> {
	private static final Logger logger = LH.get();
	private static final int MAX_CLIPBOARD_SIZE = 1024 * 1024;
	private static final Logger amiScriptLog = Logger.getLogger("AMISCRIPT.SESSION.LOG");
	private static final Logger amiScriptWarn = Logger.getLogger("AMISCRIPT.SESSION.WARN");

	private AmiWebScriptMemberMethods_Session() {
		super();

		addMethod(LOG);
		addMethod(WARN);
		addMethod(GET_USERNAME, "username");
		addMethod(GET_TRANSIENT_ID_PREFIX, "transientIdPrefix");
		addMethod(GET_HOST_IP, "hostip");
		addMethod(GET_BROWSER_IP, "browserIp");
		addMethod(GET_SESSION_ID, "sessionId");
		addMethod(GET_LOGIN_ID, "loginId");
		addMethod(GET_PANELS, "panels");
		addMethod(GET_GLOBAL_PROPERTY);
		addMethod(GET_PROPERTY);
		addMethod(GET_FILE_SYSTEM, "fileSystem");
		addMethod(GET_DATAMODELS, "datamodels");
		addMethod(GET_WINDOWS);
		addMethod(GET_URL);
		addMethod(GET_BROWSER_ZOOM);
		addMethod(GET_WINDOWS_MAP);
		addMethod(GET_URL_PARAMS);
		addMethod(SET_URL_PARAMS);

		addMethod(ALERT);
		addMethod(PROMPT);
		addMethod(NOW);
		addMethod(NOW_NANO);
		addMethod(GET_VALUE);
		addMethod(GET_VALUES);
		addMethod(SET_VALUE);
		addMethod(NOTIFY);
		addMethod(GET_DESKTOP_LOCATION);
		addMethod(SET_BROWSER_TITLE);
		addMethod(GET_BROWSER_TITLE, "browserTitle");
		addMethod(GET_TIMEZONE, "timezone");
		addMethod(CALL_COMMAND);
		addMethod(CALL_COMMAND2);
		addMethod(CALL_COMMAND_SYNC);
		addMethod(CALL_COMMAND_SYNC2);
		addMethod(SEND_EMAIL);
		addMethod(SEND_EMAIL_SYNC);
		addMethod(SEND_EMAIL2);
		addMethod(SEND_EMAIL_SYNC2);
		addMethod(CALL_RPC_SYNC);

		addMethod(GET_RELATIONSHIP);
		addMethod(GET_RELATIONSHIPS, "relationships");
		addMethod(EXECUTE_SCRIPT);
		addMethod(EXPORT_LAYOUT);
		addMethod(IMPORT_DATAMODEL);
		addMethod(LOAD_LAYOUT);
		addMethod(GET_ROOT_LAYOUT);
		addMethod(IMPORT_WINDOW);
		addMethod(GET_CUSTOM_PREFERENCES);
		addMethod(PUT_CUSTOM_PREFERENCES);
		addMethod(GET_CUSTOM_PREFERENCE_IDS);
		addMethod(GET_USER_PREFERENCES_NAMESPACE);
		addMethod(SAVE_USER_PREFERENCES);
		addMethod(LOAD_USER_PREFERENCES);
		addMethod(LOAD_USER_PREFERENCES2);
		addMethod(PLAY_AUDIO);
		addMethod(IS_HTTPS_CONNECTION);
		addMethod(DOWNLOAD_TO_BROWSER);
		addMethod(DOWNLOAD_TO_BROWSER2);
		addMethod(IS_FEED_SNAPSHOT_PROCESSED);
		addMethod(OPEN_WINDOW);
		addMethod(GET_DEVTOOLS, "devtools");

		addMethod(GET_STYLESET_IDS);
		addMethod(GET_STYLESETS, "stylesets");
		addMethod(GET_STYLESET);
		addMethod(GET_SSO_SESSION);
		addMethod(LOGOUT);
		addMethod(SET_LABEL);
		addMethod(GET_LABEL, "label");
		addMethod(GET_DASHBOARD_STYLESET, "dashboardStyleset");
		addMethod(COPY_TO_CLIPBOARD);
	}

	@Deprecated
	private static final AmiAbstractMemberMethod<AmiWebService> IS_FEED_SNAPSHOT_PROCESSED = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class,
			"isFeedSnapshotProcessed", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			String type = (String) params[0];
			for (AmiCenterDefinition i : service.getCenterIds()) {
				AmiWebManager wm = service.getWebManagers().getWebManager(i.getId());
				if (!wm.isInterestedIn(type) || !wm.getAmiObjectsByType(type).getSnapshotProcessed())
					return false;
			}
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "type" };
		};
		protected String[] buildParamDescriptions() {
			return new String[] { "type, aka Table name" };
		};
		@Override
		protected String getHelp() {
			return "Returns true if the snapshot has been processed from ALL centers for the supplied type.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> GET_USER_PREFERENCES_NAMESPACE = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class,
			"getUserPreferencesNamespace", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			return service.getVarsManager().getUserPrefNamespace();
		}
		@Override
		protected String getHelp() {
			return "Returns the user preferences namespace set on this dashboard.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_CUSTOM_PREFERENCES = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getCustomPreference",
			Object.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			String id = (String) params[0];
			if (id == null)
				return null;
			return service.getVarsManager().getCustomPreference(id);
		}
		protected String[] buildParamNames() {
			return new String[] { "customPreferenceId" };
		};
		protected String[] buildParamDescriptions() {
			return new String[] { "customPreferenceId" };
		};
		@Override
		protected String getHelp() {
			return "Returns a custom prefernce, or null if no custom preference exists for the id.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> PUT_CUSTOM_PREFERENCES = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "putCustomPreference",
			Object.class, String.class, Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			String id = (String) params[0];
			if (id == null)
				return null;
			Object m = params[1];
			ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(m);
			service.getVarsManager().putCustomPreference(id, m);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "customPreferenceId", "PreferencesOrNull" };
		};
		protected String[] buildParamDescriptions() {
			return new String[] { "customPreferenceId", "PreferencesOrNull" };
		};
		@Override
		protected String getHelp() {
			return "Sets the custom preferences by id, if preferencesOrNull is null then existing preferences are removed.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_CUSTOM_PREFERENCE_IDS = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class,
			"getCustomPreferenceIds", Set.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			return service.getVarsManager().getCustomPreferenceIds();
		}
		@Override
		protected String getHelp() {
			return "Returns a set of the existing custom preferences ids.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> IMPORT_WINDOW = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "importWindow", Boolean.class,
			String.class, Map.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			String windowName = (String) params[0];
			Map<String, Object> configuration = (Map<String, Object>) params[1];
			AmiWebLayoutHelper.importWindowConfig(targetObject, windowName, AmiWebUtils.deepCloneConfig(configuration), true);
			return true;
		}
		protected String[] buildParamNames() {
			return new String[] { "windowName", "panelConfig" };
		};
		protected String[] buildParamDescriptions() {
			return new String[] { "windowName", "panelConfig" };
		};
		@Override
		protected String getHelp() {
			return "Imports a new Window.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> EXPORT_LAYOUT = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "exportLayout", Map.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getLayoutFilesManager().getLayoutConfiguration(false);
		}
		protected String[] buildParamNames() {
			return new String[] {};
		};
		protected String[] buildParamDescriptions() {
			return new String[] {};
		};

		@Override
		protected String getHelp() {
			return "Exports the current layout to a map.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	static class ResetLayoutTimedEvent extends AmiWebTimedEvent {
		private final String layoutName;
		private final Map<String, Object> configuration;

		public ResetLayoutTimedEvent(String layoutName, Map<String, Object> configuration) {
			this.layoutName = layoutName;
			this.configuration = configuration;
		}
		@Override
		public void execute(AmiWebService service) {
			service.getLayoutFilesManager().loadLayoutDialog(this.layoutName, service.getLayoutFilesManager().toJson(this.configuration), null);
		}

		@Override
		public String describe() {
			return "load new layout";
		}

	}

	private static final AmiAbstractMemberMethod<AmiWebService> LOAD_LAYOUT = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "loadLayout", Boolean.class,
			String.class, Map.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			String layoutname = (String) params[0];
			Map<String, Object> configuration = (Map<String, Object>) params[1];
			if (layoutname == null)
				layoutname = AmiWebLayoutManager.DEFAULT_LAYOUT_NAME;
			service.addTimedEvent(0, new ResetLayoutTimedEvent(layoutname, configuration));
			return true;
		}
		protected String[] buildParamNames() {
			return new String[] { "layoutName", "layoutConfig" };
		};
		protected String[] buildParamDescriptions() {
			return new String[] { "layoutName", "layoutConfig" };
		};

		@Override
		protected String getHelp() {
			return "Loads a layout from a json configuration, see exportConfig().";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> IMPORT_DATAMODEL = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "importDatamodel",
			AmiWebDm.class, Map.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			Map<String, Object> configuration = (Map<String, Object>) params[0];
			final AmiWebDmManager dmManager = targetObject.getDmManager();
			String alias = "";//TODO:
			AmiWebDmsImpl dm = dmManager.importDms(alias, configuration, new StringBuilder(), true);
			return dm;
		}
		protected String[] buildParamNames() {
			return new String[] { "dmConfig" };
		};
		protected String[] buildParamDescriptions() {
			return new String[] { "dmConfig" };
		};

		@Override
		protected String getHelp() {
			return "Imports a datamodel.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> CALL_COMMAND = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "callCommand", String.class,
			String.class, String.class, Table.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack stackFrame, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			AmiCalcFrameStack execInstance = AmiUtils.getExecuteInstance2(stackFrame);
			String appName = (String) params[0];
			String cmdId = (String) params[1];
			Table table = (Table) params[2];
			Collection<AmiWebCommandWrapper> cmds = service.getSystemObjectsManager().getCommandsByAppNameCmdId(appName, cmdId);
			if (cmds.size() == 0) {
				return null;
			}
			if (cmds.size() > 1) {
				if (service.getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
					service.getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_METHOD, service.getAri(), null,
							"Warning more than one app or command registered to the app or command", CH.m("app", appName, "cmdId", cmdId), null));
			}
			AmiWebCommandWrapper cmd = CH.first(cmds);

			int timeout = execInstance.getTimeoutController().getTimeoutMillisRemaining();
			AmiCenterPassToRelayRequest r = service.sendCommandToBackEnd(cmd, table, timeout, null);
			AmiRelayRunAmiCommandRequest r2 = (AmiRelayRunAmiCommandRequest) (r == null ? null : r.getAgentRequest());
			return r2 == null ? null : r2.getCommandUid();
		}
		protected String[] buildParamNames() {
			return new String[] { "appId", "commandId", "table" };
		};
		protected String[] buildParamDescriptions() {
			return new String[] { "appId", "commandId", "table" };
		};

		@Override
		protected String getHelp() {
			return "Calls a command (asynchronously) and returns the command uid. If the command has no route, then null is returned. See callback Layout::onAsyncCommandResponse(...) for processing the response once it's available. WARNING: If multiple commands are registered under the same appId and commandId then it will call the first";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> CALL_COMMAND2 = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "callCommand", String.class,
			String.class, String.class, Table.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack stackFrame, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			AmiCalcFrameStack execInstance = AmiUtils.getExecuteInstance2(stackFrame);
			String appName = (String) params[0];
			String cmdId = (String) params[1];
			Table table = (Table) params[2];
			Number timeoutMillis = (Number) params[3];
			if (timeoutMillis == null)
				timeoutMillis = execInstance.getTimeoutController().getTimeoutMillisRemaining();

			Collection<AmiWebCommandWrapper> cmds = service.getSystemObjectsManager().getCommandsByAppNameCmdId(appName, cmdId);
			if (cmds.size() == 0) {
				return null;
			}
			if (cmds.size() > 1) {
				if (service.getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
					service.getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_METHOD, service.getAri(), null,
							"Warning more than one app or command registered to the app or command", CH.m("app", appName, "cmdId", cmdId), null));
			}
			AmiWebCommandWrapper cmd = CH.first(cmds);
			if (cmd == null)
				return null;

			AmiCenterPassToRelayRequest r = service.sendCommandToBackEnd(cmd, table, timeoutMillis.intValue(), null);
			AmiRelayRunAmiCommandRequest r2 = (AmiRelayRunAmiCommandRequest) (r == null ? null : r.getAgentRequest());
			return r2 == null ? null : r2.getCommandUid();
		}
		protected String[] buildParamNames() {
			return new String[] { "appId", "commandId", "table", "timeoutMillis" };
		};
		protected String[] buildParamDescriptions() {
			return new String[] { "appId", "commandId", "table", "timeoutMillis" };
		};

		@Override
		protected String getHelp() {
			return "Calls a command (asynchronously) and returns the command uid. If the command has no route, then null is returned. See callback Layout::onAsyncCommandResponse(...) for processing the response once it's available.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> SEND_EMAIL = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "sendEmail", String.class,
			String.class, List.class, String.class, Boolean.class, String.class, List.class, List.class, String.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			String from = (String) params[0];
			List<String> toList = OH.castAll((List<?>) params[1], String.class, false);
			String subject = (String) params[2];
			boolean isHtml = Boolean.TRUE.equals((Boolean) params[3]);
			String body = (String) params[4];
			List an = (List) params[5];
			List ad = (List) params[6];
			String username = (String) params[7];
			Password password = Password.valueOf((String) params[8]);
			Tuple2<List<String>, List<byte[]>> t = buildAttachemnts(an, ad);
			AmiCenterPassToRelayRequest r = service.sendEmailToBackEnd(body, subject, toList, from, isHtml, t.getA(), t.getB(), username, password, service.getDefaultTimeoutMs(),
					null, null);
			AmiRelaySendEmailRequest r2 = (AmiRelaySendEmailRequest) (r == null ? null : r.getAgentRequest());
			return r2 == null ? null : r2.getSendEmailUid();
		}
		protected String[] buildParamNames() {
			return new String[] { "fromAddress", "toAddresses", "subject", "isHtml", "body", "attachmentNames", "attachmentDatas", "optionalUsername", "optionalPassword" };
		};
		protected String[] buildParamDescriptions() {
			return new String[] { "From Address", "List addresses to send email to", "Email Subject", "true=email,false=text", "Body", "Optional List of Attachment Names",
					"Optional List of Attachment data, either Bytes or Strings", "Optional username to connect to smtp server with",
					"Optional password to connect o smtp server with" };
		};

		@Override
		protected String getHelp() {
			return "Sends an email and returns immediately with the email-send-UID.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> SEND_EMAIL2 = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "sendEmail", String.class,
			String.class, List.class, String.class, Boolean.class, String.class, List.class, List.class, String.class, String.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			String from = (String) params[0];
			List<String> toList = OH.castAll((List<?>) params[1], String.class, false);
			String subject = (String) params[2];
			boolean isHtml = Boolean.TRUE.equals((Boolean) params[3]);
			String body = (String) params[4];
			List an = (List) params[5];
			List ad = (List) params[6];
			String username = (String) params[7];
			Password password = Password.valueOf((String) params[8]);
			String relayIds = (String) params[9];
			Tuple2<List<String>, List<byte[]>> t = buildAttachemnts(an, ad);
			AmiCenterPassToRelayRequest r = service.sendEmailToBackEnd(body, subject, toList, from, isHtml, t.getA(), t.getB(), username, password, service.getDefaultTimeoutMs(),
					null, relayIds);
			AmiRelaySendEmailRequest r2 = (AmiRelaySendEmailRequest) (r == null ? null : r.getAgentRequest());
			return r2 == null ? null : r2.getSendEmailUid();
		}
		protected String[] buildParamNames() {
			return new String[] { "fromAddress", "toAddresses", "subject", "isHtml", "body", "attachmentNames", "attachmentDatas", "optionalUsername", "optionalPassword",
					"optionalRelayId" };
		};
		protected String[] buildParamDescriptions() {
			return new String[] { "From Address", "List addresses to send email to", "Email Subject", "true=email,false=text", "Body", "Optional List of Attachment Names",
					"Optional List of Attachment data, either Bytes or Strings", "Optional username to connect to smtp server with",
					"Optional password to connect o smtp server with", "relayId of relay to use for sending email, use a comma delimited list to denote primary, secondary, etc" };
		};

		@Override
		protected String getHelp() {
			return "Sends an email and returns immediately with the email-send-UID.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> SEND_EMAIL_SYNC = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "sendEmailSync", String.class,
			String.class, List.class, String.class, Boolean.class, String.class, List.class, List.class, String.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			String from = (String) params[0];
			List<String> toList = OH.castAll((List<?>) params[1], String.class, false);
			String subject = (String) params[2];
			boolean isHtml = Boolean.TRUE.equals((Boolean) params[3]);
			String body = (String) params[4];
			List an = (List) params[5];
			List ad = (List) params[6];
			String username = (String) params[7];
			Password password = Password.valueOf((String) params[8]);
			Tuple2<List<String>, List<byte[]>> t = buildAttachemnts(an, ad);
			AmiSendEmailFlowControlPause pause = new AmiSendEmailFlowControlPause(body, subject, toList, from, isHtml, t.getA(), t.getB(), username, password, null,
					sf.getTimeoutController().getTimeoutMillisRemaining(), caller);
			return pause;
		}
		protected String[] buildParamNames() {
			return new String[] { "fromAddress", "toAddresses", "subject", "isHtml", "body", "attachmentNames", "attachmentDatas", "optionalUsername", "optionalPassword" };
		};
		protected String[] buildParamDescriptions() {
			return new String[] { "From Address", "List addresses to send email to", "Email Subject", "true=email,false=text", "Body", "Optional List of Attachment Names",
					"Optional List of Attachment data, either Bytes or Strings", "Optional username to connect to smtp server with",
					"Optional password to connect o smtp server with" };
		};

		@Override
		protected String getHelp() {
			return "Sends an email and returns with the email result.";
		}

		@Override
		public Object resumeMethod(CalcFrameStack sf, AmiWebService target, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
			AmiSendEmailFlowControlPause t = (AmiSendEmailFlowControlPause) fp;
			//			AmiRelaySendEmailRequest q = (AmiRelaySendEmailRequest) t.getCommandRequest().getAgentRequest();
			AmiCenterPassToRelayResponse r = t.getCommandResponse();
			AmiRelaySendEmailResponse r2 = r == null ? null : (AmiRelaySendEmailResponse) r.getAgentResponse();
			if (r2 == null)
				return "ERROR";
			if (r2.getException() != null)
				throw new RuntimeException(r2.getException());
			return r2.getOk() ? "SUCCESS" : r2.getMessage();
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public boolean isPausable() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebService> SEND_EMAIL_SYNC2 = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "sendEmailSync", String.class,
			String.class, List.class, String.class, Boolean.class, String.class, List.class, List.class, String.class, String.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			String from = (String) params[0];
			List<String> toList = OH.castAll((List<?>) params[1], String.class, false);
			String subject = (String) params[2];
			boolean isHtml = Boolean.TRUE.equals((Boolean) params[3]);
			String body = (String) params[4];
			List an = (List) params[5];
			List ad = (List) params[6];
			String username = (String) params[7];
			Password password = Password.valueOf((String) params[8]);
			String relayIds = (String) params[9];
			Tuple2<List<String>, List<byte[]>> t = buildAttachemnts(an, ad);
			AmiSendEmailFlowControlPause pause = new AmiSendEmailFlowControlPause(body, subject, toList, from, isHtml, t.getA(), t.getB(), username, password, relayIds,
					service.getDefaultTimeoutMs(), caller);
			return pause;
		}
		protected String[] buildParamNames() {
			return new String[] { "fromAddress", "toAddresses", "subject", "isHtml", "body", "attachmentNames", "attachmentDatas", "optionalUsername", "optionalPassword",
					"optionalRelayId" };
		};
		protected String[] buildParamDescriptions() {
			return new String[] { "From Address", "List addresses to send email to", "Email Subject", "true=email,false=text", "Body", "Optional List of Attachment Names",
					"Optional List of Attachment data, either Bytes or Strings", "Optional username to connect to smtp server with",
					"Optional password to connect o smtp server with", "relayId of relay to use for sending email, use a comma delimited list to denote primary, secondary, etc" };
		};

		@Override
		protected String getHelp() {
			return "Sends an email and returns with the email result.";
		}

		@Override
		public Object resumeMethod(CalcFrameStack sf, AmiWebService target, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
			AmiSendEmailFlowControlPause t = (AmiSendEmailFlowControlPause) fp;
			//			AmiRelaySendEmailRequest q = (AmiRelaySendEmailRequest) t.getCommandRequest().getAgentRequest();
			AmiCenterPassToRelayResponse r = t.getCommandResponse();
			AmiRelaySendEmailResponse r2 = r == null ? null : (AmiRelaySendEmailResponse) r.getAgentResponse();
			if (r2 == null)
				return "ERROR";
			if (r2.getException() != null)
				throw new RuntimeException(r2.getException());
			return r2.getOk() ? "SUCCESS" : r2.getMessage();
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public boolean isPausable() {
			return true;
		};

	};

	private static Tuple2<List<String>, List<byte[]>> buildAttachemnts(List<?> an, List<?> ad) {
		List<String> attachmentNames;
		List<byte[]> attachmentDatas;
		if (CH.isEmpty(an) || CH.isEmpty(ad)) {
			attachmentNames = null;
			attachmentDatas = null;
		} else {
			int size = Math.max(an.size(), ad.size());
			attachmentNames = new ArrayList<String>(size);
			attachmentDatas = new ArrayList<byte[]>(size);
			for (int i = 0; i < size; i++) {
				String name = Caster_String.INSTANCE.cast(an.get(i));
				if (name == null)
					name = "Attachemnt" + (i + 1);
				Object data = ad.get(i);
				final byte[] bytes;
				if (data instanceof Bytes)
					bytes = ((Bytes) data).getBytes();
				else if (data == null)
					bytes = "<Null Attachment Data>".getBytes();
				else
					bytes = AmiUtils.s(data).getBytes();
				attachmentNames.add(name);
				attachmentDatas.add(bytes);
			}
		}
		return new Tuple2<List<String>, List<byte[]>>(attachmentNames, attachmentDatas);
	}

	private static final AmiAbstractMemberMethod<AmiWebService> CALL_COMMAND_SYNC2 = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "callCommandSync",
			AmiWebCommandResponse.class, String.class, String.class, Table.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack stackFrame, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			AmiCalcFrameStack execInstance = AmiUtils.getExecuteInstance2(stackFrame);
			String appName = (String) params[0];
			String cmdId = (String) params[1];
			Table table = (Table) params[2];
			Number timeoutMillis = (Number) params[3];
			if (timeoutMillis == null)
				timeoutMillis = execInstance.getTimeoutController().getTimeoutMillisRemaining();

			Collection<AmiWebCommandWrapper> cmds = service.getSystemObjectsManager().getCommandsByAppNameCmdId(appName, cmdId);
			if (cmds.size() > 1) {
				if (service.getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
					service.getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_METHOD, service.getAri(), null,
							"Warning more than one app or command registered to the app or command", CH.m("app", appName, "cmdId", cmdId), null));
			}
			AmiWebCommandWrapper cmd = CH.first(cmds);

			if (cmds.size() == 0 || table == null || table.getSize() == 0) {
				AmiRelayRunAmiCommandResponse r2 = service.nw(AmiRelayRunAmiCommandResponse.class);
				if (cmd == null) {
					r2.setAmiMessage("AppName for command id Not registered: " + appName + "::" + cmdId);
					r2.setStatusCode(AmiRelayRunAmiCommandResponse.STATUS_COMMAND_NOT_REGISTERED);
				} else if (table == null || table.getSize() == 0) {
					r2.setAmiMessage("Table is empty");
					r2.setStatusCode(AmiRelayRunAmiCommandResponse.STATUS_GENERAL_ERROR);
				}
				AmiRelayRunAmiCommandRequest req = service.nw(AmiRelayRunAmiCommandRequest.class);
				req.setAppId(appName);
				req.setCommandUid(GuidHelper.getGuid(62));
				req.setCommandDefinitionId(cmdId);
				req.setTimeoutMs(service.getDefaultTimeoutMs());
				return new AmiWebCommandResponse(service, req, r2);
			}

			AmiCallCommandFlowControlPause pause = new AmiCallCommandFlowControlPause(cmd, table, timeoutMillis.intValue(), caller);
			return pause;
		}
		protected String[] buildParamNames() {
			return new String[] { "appId", "commandId", "table", "timeoutMillis" };
		};
		protected String[] buildParamDescriptions() {
			return new String[] { "appId", "commandId", "table", "timeoutMillis" };
		};

		@Override
		protected String getHelp() {
			return "Calls a command synchronously with a specified timeout, meaning do not continue execution of script until the command completes.";
		}
		@Override
		public Object resumeMethod(CalcFrameStack sf, AmiWebService service, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
			AmiCallCommandFlowControlPause t = (AmiCallCommandFlowControlPause) fp;
			AmiRelayRunAmiCommandRequest q = (AmiRelayRunAmiCommandRequest) t.getCommandRequest().getAgentRequest();
			AmiCenterPassToRelayResponse r = t.getCommandResponse();
			AmiRelayRunAmiCommandResponse r2 = r == null ? null : (AmiRelayRunAmiCommandResponse) r.getAgentResponse();
			if (r2 == null) {
				r2 = service.nw(AmiRelayRunAmiCommandResponse.class);
				r2.setAmiMessage("Unknown Error");
				r2.setStatusCode(AmiRelayRunAmiCommandResponse.STATUS_GENERAL_ERROR);
			}
			return new AmiWebCommandResponse(service, q, r2);
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
		@Override
		public boolean isPausable() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebService> CALL_COMMAND_SYNC = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "callCommandSync",
			AmiWebCommandResponse.class, String.class, String.class, Table.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack stackFrame, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			AmiCalcFrameStack ei = AmiUtils.getExecuteInstance2(stackFrame);
			String appName = (String) params[0];
			String cmdId = (String) params[1];
			Table table = (Table) params[2];
			Collection<AmiWebCommandWrapper> cmds = service.getSystemObjectsManager().getCommandsByAppNameCmdId(appName, cmdId);
			if (cmds.size() > 1) {
				if (service.getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
					service.getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_METHOD, service.getAri(), null,
							"Warning more than one app or command registered to the app or command", CH.m("app", appName, "cmdId", cmdId), null));
			}
			AmiWebCommandWrapper cmd = CH.first(cmds);
			if (cmds.size() == 0 || table == null || table.getSize() == 0) {
				AmiRelayRunAmiCommandResponse r2 = service.nw(AmiRelayRunAmiCommandResponse.class);
				if (cmd == null) {
					r2.setAmiMessage("AppName for command id Not registered: " + appName + "::" + cmdId);
					r2.setStatusCode(AmiRelayRunAmiCommandResponse.STATUS_COMMAND_NOT_REGISTERED);
				} else if (table == null || table.getSize() == 0) {
					r2.setAmiMessage("Table is empty");
					r2.setStatusCode(AmiRelayRunAmiCommandResponse.STATUS_GENERAL_ERROR);
				}
				AmiRelayRunAmiCommandRequest req = service.nw(AmiRelayRunAmiCommandRequest.class);
				req.setAppId(appName);
				req.setCommandUid(GuidHelper.getGuid(62));
				req.setCommandDefinitionId(cmdId);
				req.setTimeoutMs(service.getDefaultTimeoutMs());
				return new AmiWebCommandResponse(service, req, r2);
			}

			AmiCallCommandFlowControlPause pause = new AmiCallCommandFlowControlPause(cmd, table, ei.getTimeoutController().getTimeoutMillisRemaining(), caller);
			return pause;
		}
		protected String[] buildParamNames() {
			return new String[] { "appId", "commandId", "table" };
		};
		protected String[] buildParamDescriptions() {
			return new String[] { "appId", "commandId", "table" };
		};

		@Override
		protected String getHelp() {
			return "Calls a command synchronously, meaning do not continue execution of script until the command completes.";
		}
		@Override
		public Object resumeMethod(CalcFrameStack sf, AmiWebService service, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
			AmiCallCommandFlowControlPause t = (AmiCallCommandFlowControlPause) fp;
			AmiRelayRunAmiCommandRequest q = (AmiRelayRunAmiCommandRequest) t.getCommandRequest().getAgentRequest();
			AmiCenterPassToRelayResponse r = t.getCommandResponse();
			AmiRelayRunAmiCommandResponse r2 = r == null ? null : (AmiRelayRunAmiCommandResponse) r.getAgentResponse();
			if (r2 == null) {
				r2 = service.nw(AmiRelayRunAmiCommandResponse.class);
				r2.setAmiMessage("Unknown Error");
				r2.setStatusCode(AmiRelayRunAmiCommandResponse.STATUS_GENERAL_ERROR);
			}
			return new AmiWebCommandResponse(service, q, r2);
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
		@Override
		public boolean isPausable() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebService> CALL_RPC_SYNC = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "callRpcSync",
			AmiWebRpcResponse.class, AmiWebRpcRequest.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			AmiWebRpcRequest request = (AmiWebRpcRequest) params[0];
			AmiCallRestFlowControlPause pause = new AmiCallRestFlowControlPause(request, service.getDefaultTimeoutMs(), caller);
			return pause;
		}
		protected String[] buildParamNames() {
			return new String[] { "rpcRequest" };
		};

		@Override
		protected String getHelp() {
			return "Sends a request to a remote instanceof of AMI using CORS.";
		}
		@Override
		public Object resumeMethod(CalcFrameStack sf, AmiWebService target, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
			AmiCallRestFlowControlPause t = (AmiCallRestFlowControlPause) fp;
			return t.getResponse();
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public boolean isPausable() {
			return true;
		};
	};

	private static final AmiAbstractMemberMethod<AmiWebService> SET_VALUE = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "setValue", Boolean.class, String.class,
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String name = (String) params[0];
				Object value = params[1];
				targetObject.getScriptManager().putAmiScriptValue(name, value);
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String getHelp() {
			return "Sets the key value pair to this session's attributes and returns true if successful.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_VALUE = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getValue", Object.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String name = (String) params[0];
				return targetObject.getScriptManager().getAmiScriptValue(name);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key" };
		}
		@Override
		protected String getHelp() {
			return "Returns the value associated with key from this sessions attributes. Returns null if key does not exist.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_VALUES = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getValues", Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return new LinkedHashMap(targetObject.getScriptManager().getAmiScriptValuesMap());
		}
		@Override
		protected String getHelp() {
			return "Returns a map of the values from this sessions attributes. Note, modifying the returned map will have no effect on the sessions variables.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> LOG = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "log", String.class, true, Object.class,
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack stackFrame, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiCalcFrameStack ei = AmiUtils.getExecuteInstance2(stackFrame);
			String message = toDerivedString(params);
			Map<Object, Object> details = new HashMap<Object, Object>();
			for (int i = 1; i < params.length; i++)
				details.put("Param " + i, params[i]);

			StringBuilder sb = new StringBuilder();
			sb.append(targetObject.getPortletManager().describeUser());
			sb.append(" -- ");
			sb.append(message);
			String s = sb.toString();
			LH.info(amiScriptLog, s);
			details.put("Message", message);
			AmiDebugManager debugManager = ei.getDebugManager();
			if (debugManager != null && ei.getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_INFO))
				debugManager.addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_INFO, AmiDebugMessage.TYPE_LOG, ei == null ? null : ei.getSourceAri(),
						ei == null ? null : ei.getCallbackName(), "log: " + SH.ddd(message, 255), details, null));
			return message;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "message", "additionalMessages" };
		}

		@Override
		protected String getHelp() {
			return "Logs user description followed by dash dash and then a concatenation of supplied arguments to AMISCRIPT logger. Returns message logged.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> WARN = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "warn", String.class, true, Object.class,
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack stackFrame, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiCalcFrameStack ei = AmiUtils.getExecuteInstance2(stackFrame);
			String message = toDerivedString(params);
			Map<Object, Object> details = new HashMap<Object, Object>();
			for (int i = 1; i < params.length; i++)
				details.put("Param " + i, params[i]);

			StringBuilder sb = new StringBuilder();
			sb.append(targetObject.getPortletManager().describeUser());
			sb.append(" -- ");
			sb.append(AmiUtils.s(message));
			String s = sb.toString();
			LH.warning(amiScriptWarn, s);
			details.put("Message", message);
			AmiDebugManager debugManager = ei.getDebugManager();
			if (debugManager != null && ei.getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
				debugManager.addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_LOG, ei == null ? null : ei.getSourceAri(),
						ei == null ? null : ei.getCallbackName(), "log: " + SH.ddd(message, 255), details, null));

			return message;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "message", "additionalMessages" };
		}

		@Override
		protected String getHelp() {
			return "Logs at warning level the user description followed by dash dash and then a concatenation of supplied arguments to AMISCRIPT logger. Returns message logged.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> ALERT = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "alert", String.class, true, Object.class,
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			String text = AmiUtils.snn(params[0], "null");
			PortletStyleManager_Dialog dp = service.getPortletManager().getStyleManager().getDialogStyle();
			int w, h;
			String title = "Message";
			if (params.length == 3) {
				title = AmiUtils.snn(params[2], "Message");
			} else if (params.length == 2) {
				title = AmiUtils.snn(params[1], "Message");
			}

			final PortletManager portletManager = service.getPortletManager();
			text = service.cleanHtml(text);
			ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(portletManager.generateConfig(), text, ConfirmDialogPortlet.TYPE_MESSAGE);
			w = dp.getDialogWidth();
			h = dp.getDialogHeight();
			portletManager.showDialog(title, cdp, w, h);
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "alertText", "closeButtonTextAndTitleText" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "The text content of the alert",
					"the text for the close button. Defaults to \"Close\". the text for the title (top left). Defaults to \"MESSAGE\"." };
		}

		@Override
		protected String getHelp() {
			return "Shows alert dialog with given text. The second and third arguments are optional. Returns null.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> PROMPT = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "prompt", String.class, true, Object.class,
			String.class, List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			String text = AmiUtils.snn(params[0], "null");
			String[] lines = SH.splitLines(text);
			String longest = OH.noNull(SH.getLongest(lines), "");
			String title = OH.noNull((String) params[1], "Prompt");
			List buttons = (List) params[2];
			if (CH.isEmpty(buttons))
				buttons = CH.l("Close");

			final PortletManager portletManager = targetObject.getPortletManager();
			RootPortletDialog dialog;
			AmiAlertPromptControlPause pause = new AmiAlertPromptControlPause(caller);

			text = targetObject.cleanHtml(text);
			ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(portletManager.generateConfig(), text, ConfirmDialogPortlet.TYPE_MESSAGE);
			cdp.clearButtons();
			Set<String> ids = new HashSet<String>();
			for (Object i : buttons) {
				String s = AmiUtils.s(i);
				if (ids.add(s))
					cdp.addButton(s, s);
			}
			int w = 60 + MH.clip(longest.length() * 7, 300, 1000);
			int h = 100 + MH.clip(AH.length(lines) * 20, 70, 600);
			//			PortletStyleManager_Dialog dp = getService().getPortletManager().getStyleManager().getDialogStyle();
			//			w = dp.getDialogWidth();
			//			h = dp.getDialogHeight();
			dialog = portletManager.showDialog(title, cdp, w, h);
			cdp.addDialogListener(pause);
			dialog.addListener(pause);
			return pause;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "alertText", "title", "buttons" };
		}

		@Override
		protected String getHelp() {
			return "Shows a dialog with given text and title, along with a list of buttons to choose from. Return blocks until user chooses a button, returns whichever button was pressed, or null if the user closed the dialog.";
		}
		@Override
		public Object resumeMethod(CalcFrameStack sf, AmiWebService target, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
			AmiAlertPromptControlPause t = (AmiAlertPromptControlPause) fp;
			return t.getResponse();
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
		@Override
		public boolean isPausable() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebService> NOW = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "now", DateMillis.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return new DateMillis(targetObject.getPortletManager().getNow());
		}
		@Override
		protected String getHelp() {
			return "Returns the current time in milliseconds since unix epoch.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> NOW_NANO = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "nowNano", DateNanos.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return new DateNanos(EH.currentTimeNanos());
		}
		@Override
		protected String getHelp() {
			return "Returns the current time in nanoseconds since unix epoch.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> NOTIFY = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "notify", String.class, String.class,
			String.class, String.class, Map.class, Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String title = (String) params[0];
				String body = (String) params[1];
				String imageUrl = (String) params[2];
				Map options = (Map) params[3];
				Map attachmentForCallback = (Map) params[4];
				if (imageUrl == null)
					imageUrl = "rsc/3forge_notify_new.png";
				AmiWebNotification t = targetObject.showNotification(title, body, imageUrl, options, attachmentForCallback);
				return t.getNotification().getId();
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "title", "body", "imageurl", "options", "attachment" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "title of the notification", "html body of the notification", "the url of the image (icon)", "options",
					"an attachment to be included in the callback (passed into 3rd param of Layout::onNotificationHandled)" };
		}
		@Override
		protected String getHelp() {
			return "Displays a notification to the user in the task tray. When the user clicks on the notification the Layout::onNotificationHandled(...) is called. Returns a unique id representing the notification.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_DESKTOP_LOCATION = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getDesktopLocation",
			WebRectangle.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return new WebRectangle(0, 0, targetObject.getDesktop().getWidth(), targetObject.getDesktop().getHeight());
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}
		@Override
		protected String getHelp() {
			return "Returns the location of the desktop, upper left is always zero, the width and height reflect the browser size (minus window border).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_BROWSER_TITLE = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getBrowserTitle",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPortletManager().getRoot().getTitle();
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}
		@Override
		protected String getHelp() {
			return "Returns the title shown in the browser title bar.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> SET_BROWSER_TITLE = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "setBrowserTitle", Object.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			String title = (String) params[0];
			if (SH.isnt(title))
				title = targetObject.getPortletManager().getDefaultBrowserTitle();
			targetObject.getPortletManager().getRoot().setTitle(title);
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "title" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "title" };
		}
		@Override
		protected String getHelp() {
			return "Sets the title shown in the browser title bar. If supplied title is null or blank, sets back to default.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_USERNAME = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getUsername", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getVarsManager().getUsername();
		}
		@Override
		protected String getHelp() {
			return "Returns the current username, which is the name used to login.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_TRANSIENT_ID_PREFIX = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getTransientIdPrefix",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getVarsManager().getTransientIdPrefix();
		}
		@Override
		protected String getHelp() {
			return "Returns the transient id prefix";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_HOST_IP = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getHostIp", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			WebStatesManager webStatesManager = targetObject.getPortletManager().getState().getWebStatesManager();
			return webStatesManager == null ? null : webStatesManager.getRemoteAddress();
		}
		@Override
		protected String getHelp() {
			return "Returns the current host ip of the remote connection known by the ami web server.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_BROWSER_IP = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getBrowserIp", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			String ipAddr = null;
			HttpRequestResponse r = targetObject.getPortletManager().getLastRequestAction();
			if (r != null) {
				ipAddr = r.getHeader().get("X-Forwarded-For");
				if (SH.isEmpty(ipAddr))
					ipAddr = r.getRemoteHost();
			}
			return ipAddr;
		}
		@Override
		protected String getHelp() {
			return "Returns the current browser ip.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_SESSION_ID = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getSessionId", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPortletManager().getState().getPgId();
		}
		@Override
		protected String getHelp() {
			return "Returns the current sessionid.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_LOGIN_ID = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getLoginId", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPortletManager().getState().getSessionId();
		}
		@Override
		protected String getHelp() {
			return "Returns the current sessionid.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_TIMEZONE = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getTimeZone", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getVarsManager().getTimeZoneId();
		}
		@Override
		protected String getHelp() {
			return "Returns the user's timezone Identifier, same as the global reserved variable __TIMEZONE.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_PROPERTY = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getProperty", String.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			String key = (String) params[0];
			CalcFrame constsMap = service.getScriptManager("").getConstsMap(null);
			return constsMap.getValue(key);
		}
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key" };
		}
		@Override
		protected String getHelp() {
			return "Returns value associated with key from this session's global properties. Returns null if key does not exist.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_FILE_SYSTEM = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getFileSystem",
			AmiWebFileSystem.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAmiFileSystem();
		}

		@Override
		protected String getHelp() {
			return "Returns a FileSystem object for accessing the file system that amiweb is running on.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_GLOBAL_PROPERTY = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getGlobalProperty",
			String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			return service.getPortletManager().getTools().getOptional((String) params[0]);
		}
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key" };
		}
		@Override
		protected String getHelp() {
			return "Returns the value associated with key from this servers properties, for example a property set in root.properties file. Returns null if property does not exist.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_PANELS = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getPanels", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAmiPanelManager().getAmiPanels();
		}
		@Override
		protected String getHelp() {
			return "Returns all AMI panels.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_DATAMODELS = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getDatamodels", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return CH.l(targetObject.getDmManager().getDatamodels());
		}
		@Override
		protected String getHelp() {
			return "Returns all datamodels.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> GET_WINDOWS = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getWindows", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return CH.l(AmiWebUtils.getAmiWindowsWithAmiPanels(targetObject.getDesktop()).values());
		}
		@Override
		protected String getHelp() {
			return "Returns all AMI windows as a list.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> GET_WINDOWS_MAP = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getWindowsMap", Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return AmiWebUtils.getAmiWindowsWithAmiPanels(targetObject.getDesktop());
		}
		@Override
		protected String getHelp() {
			return "Returns all AMI windows as a map of ami alias dot panel id to window.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "Session";
	}
	@Override
	public String getVarTypeDescription() {
		return "Represents the session for this user's login. There is a global variable 'session' that implements a Session instance and can be used for accessing session level objects such as properties, panels, datamodels, etc.";
	}
	@Override
	public Class<AmiWebService> getVarType() {
		return AmiWebService.class;
	}
	@Override
	public Class<AmiWebService> getVarDefaultImpl() {
		return null;
	}

	private static final AmiAbstractMemberMethod<AmiWebService> GET_RELATIONSHIP = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getRelationship",
			AmiWebDmLink.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			String id = (String) params[0];
			return targetObject.getDmManager().getDmLinkByAliasDotRelationshipId(id);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "relationshipId" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "id of the relationship" };
		}
		@Override
		protected String getHelp() {
			return "Returns the relationship for the given alias.relationshipId, or null if there is no  relationship with the given id.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_RELATIONSHIPS = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getRelationships",
			List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return CH.l(targetObject.getDmManager().getDmLinks());
		}
		@Override
		protected String getHelp() {
			return "Returns a list of all relationships.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> EXECUTE_SCRIPT = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "exec", Object.class, Number.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			Number when = (Number) params[0];
			String script = (String) params[1];
			StringBuilder error = new StringBuilder();
			String layoutAlias = "";
			DerivedCellCalculator calc = service.getScriptManager(layoutAlias).parseAmiScript(script, EmptyCalcTypes.INSTANCE, error, service.getDebugManager(),
					AmiDebugMessage.TYPE_DYNAMIC_AMISCRIPT, service, "exec", false, null);
			if (calc == null)
				throw new FlowControlThrow("Could not compile '" + script + "' ==> " + error);
			long time = AmiUtils.getMillis(when);
			service.addTimedEvent(time, "", script, calc);
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "when", "amiScript" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "time - negative number is milliseconds in the future from now, zero or positive number is absolute time in millis.", "Script to execute" };
		}
		@Override
		protected String getHelp() {
			return "Runs script deferred. The provided script is compiled in current thread, but executed outside this call. If provided time is in the past, then the script will be executed at the earliest opportunity. This always runs on the root layout, we recommend using layout.exec(...) instead.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> PLAY_AUDIO = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "playAudio", Object.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			String object = (String) params[0];
			if (object != null)
				service.getPortletManager().playAudio(object);
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "audiourl" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the url of the audio clip to play" };
		}
		@Override
		protected String getHelp() {
			return "Plays audio, specified by the url.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> IS_HTTPS_CONNECTION = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "isHttpsConnection",
			Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPortletManager().getIsSecureConnection();
		}
		@Override
		protected String getHelp() {
			return "Returns true if the browser is connected over https (instead of http).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> DOWNLOAD_TO_BROWSER = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "downloadToBrowser",
			Boolean.class, String.class, Bytes.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			String fileName = Caster_String.INSTANCE.cast(params[0]);
			try {
				Bytes fileData = (Bytes) params[1];
				targetObject.getPortletManager().pushPendingDownload(new BasicPortletDownload(fileName, fileData.getBytes()));
				return true;
			} catch (Exception e) {
				LH.warning(logger, "Error for ", fileName, ": ", e);
			}
			return false;
		}
		protected String[] buildParamNames() {
			return new String[] { "fileName", "fileData" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the name of the file", "the content of the file" };
		}
		@Override
		protected String getHelp() {
			return "Returns true if able to download file to browser, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebService> DOWNLOAD_TO_BROWSER2 = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "downloadToBrowser",
			Boolean.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			String fileName = Caster_String.INSTANCE.cast(params[0]);
			String fileData = Caster_String.INSTANCE.cast(params[1]);
			try {
				byte[] bytes = fileData.getBytes();
				targetObject.getPortletManager().pushPendingDownload(new BasicPortletDownload(fileName, bytes));
				return true;
			} catch (Exception e) {
				LH.warning(logger, "Error for ", fileName, ": ", e);
			}
			return false;
		}
		protected String[] buildParamNames() {
			return new String[] { "fileName", "fileData" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the name of the file", "the content of the file" };
		}
		@Override
		protected String getHelp() {
			return "Returns true if able to download file to browser, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> OPEN_WINDOW = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "openWindowURL", Object.class,
			String.class, Double.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			PortletManager portletManager = service.getPortletManager();
			RootPortlet root = (RootPortlet) portletManager.getRoot();
			String url = (String) params[0];
			Double pctSize = (Double) params[1];
			String windowName = OH.noNull((String) params[2], "_blank");
			String features = OH.noNull((String) params[3], "");
			StringBuilder featuresString = new StringBuilder();
			if (pctSize != null) {
				pctSize = MH.clip(pctSize, .1, 1);
				double offset = (1d - pctSize) / 2d;
				int width = (int) (root.getWidth() * pctSize);
				int height = (int) (root.getHeight() * pctSize);
				int left = (int) (root.getWidth() * offset) + root.getScreenX();
				int top = (int) (root.getHeight() * offset) + root.getScreenY();
				featuresString.append("width=").append(width);
				featuresString.append(",height=").append(height);
				featuresString.append(",left=").append(left);
				featuresString.append(",top=").append(top);
			}
			if (SH.is(features)) {
				if (SH.is(featuresString))
					featuresString.append(',');
				featuresString.append(features);
			}
			new JsFunction(portletManager.getPendingJs(), "window", "open").addParamQuoted(url).addParamQuoted(windowName).addParamQuoted(featuresString).end();
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "url", "percentSizeOfMainWndow", "windowName", "customFeatures" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "required url of target window", "optional percentSizeOfWindow - valid values are 0.1 - 1.0, null creates new tab",
					"optional name of window, _blank by default", "optional windowFeatures - see javascript window.open " };
		}
		@Override
		protected String getHelp() {
			return "Opens a new Window with the url, size, name, and custom features specified.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_ROOT_LAYOUT = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getRootLayout",
			AmiWebLayoutFile.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			return service.getLayoutFilesManager().getLayout();
		}
		@Override
		protected String getHelp() {
			return "Returns root layout for this dashboard.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> GET_DEVTOOLS = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getDevTools",
			AmiWebDevTools.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			return service.getDesktop().getIsLocked() ? null : service.getDevTools();
		}
		@Override
		protected String getHelp() {
			return "Returns the dev tools for this session, or null if account is a non-dev user.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> GET_STYLESETS = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getStyleSets", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return CH.l(targetObject.getStyleManager().getAllStyles());
		}
		@Override
		protected String getHelp() {
			return "Returns a list of all stylesets, including predefined ones.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> GET_STYLESET_IDS = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getStyleSetIds", Set.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return CH.s(targetObject.getStyleManager().getStyleIds());
		}
		@Override
		protected String getHelp() {
			return "Return set of ids for all defined stylesets, including ids for predefined styles.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_STYLESET = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getStyleSet", AmiWebStyle.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			String id = (String) params[0];
			return id == null ? null : targetObject.getStyleManager().getStyleById(id);
		}
		protected String[] buildParamNames() {
			return new String[] { "styleSetId" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Id of the styleSet to return" };
		}
		@Override
		protected String getHelp() {
			return "Returns the styleset for a given id.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> GET_URL = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getUrl", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getUrl();
		}
		@Override
		protected String getHelp() {
			return "Returns the url.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_BROWSER_ZOOM = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getBrowserZoom", Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			RootPortlet rp = (RootPortlet) targetObject.getPortletManager().getRoot();
			return rp.getBrowserZoom();
		}
		@Override
		protected String getHelp() {
			return "Returns the Browser Zoom (1 = 100%)";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_SSO_SESSION = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getSsoSession",
			AmiSsoSession.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getVarsManager().getSsoSession();
		}
		@Override
		protected String getHelp() {
			return "Returns the sso session associated with this user session.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> LOGOUT = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "logout", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			if (!service.getDesktop().getIsLocked()) {
				String s = service.getVarsManager().getSetting(AmiWebConsts.USER_SETTING_LOGOUT);
				if ("Ignore".equals(s))
					return null;
				if ("Debug".equals(s) || SH.isnt(s)) {
					service.getPortletManager()
							.showAlert("Would normally log you out, but you are a developer!<BR>(For options, See Account -> My Developer Settings -> On Automated Logout)");
					return null;
				}
			}
			if (service.isHeadlessSession())
				service.getPortletManager().showAlert("Can not end headless session");
			else
				service.getPortletManager().getState().getWebStatesManager().getSession().kill();
			return null;
		}
		@Override
		protected String getHelp() {
			return "Ends this users session and logs the user out of all sessions.  NOTE: If user is a developer, then follows Account -> My Developer Settings -> On Automated Logout settings.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_DASHBOARD_STYLESET = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getDashboardStyleSet",
			AmiWebStyle.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getDesktop().getStylePeer();
		}
		@Override
		protected String getHelp() {
			return "Returns the StyleSet for this panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> GET_URL_PARAMS = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getUrlParams", Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			TreeMap<String, String> r = new TreeMap<String, String>(targetObject.getPortletManager().getUrlParams());
			return r;
		}
		@Override
		protected String getHelp() {
			return "Returns all AMI windows as a map of ami alias dot panel id to window.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> SET_URL_PARAMS = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "setUrlParams", Object.class,
			Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			Map<Object, Object> m = (Map) params[0];
			if (m == null)
				m = Collections.EMPTY_MAP;
			Map<String, String> existing = targetObject.getPortletManager().getUrlParams();
			LinkedHashMap nuw = new LinkedHashMap();
			String currentLayout = existing.get(AmiWebConsts.URL_PARAM_LAYOUT);
			if (currentLayout != null)//this keeps it in the front of the linked map
				nuw.put(AmiWebConsts.URL_PARAM_LAYOUT, currentLayout);
			if (m != null)
				for (Map.Entry e : m.entrySet())
					if (e.getKey() != null && e.getValue() != null) {
						final String s = AmiUtils.s(e.getKey());
						if (s != null && s.length() > 0 && !AmiWebConsts.URL_PARAM_LAYOUT.equals(s)) {
							nuw.put(s, AmiUtils.s(e.getValue()));
						}
					}
			targetObject.setUrlParams(nuw);
			targetObject.getPortletManager().setUrlParams(nuw);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "url_params" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Url Parameters" };
		}
		@Override
		protected String getHelp() {
			return "Rewrite the browser's current URL parameters. Note, the LAYOUT param can NOT be modifed using this method and will be ignored if supplied. Also, this will not cause the onUrlParams callback to be fired";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> GET_LABEL = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "getLabel", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSessionLabel();
		}
		@Override
		protected String getHelp() {
			return "Returns the label associated with this session. The label is visible in the 3forge_session page";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebService> SET_LABEL = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "setLabel", Object.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setSessionLabel((String) params[0]);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "label" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "label for this session" };
		}
		@Override
		protected String getHelp() {
			return "Set the label associated with this session. The label is visible in the 3forge_session page";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> LOAD_USER_PREFERENCES = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "loadUserPreferences",
			String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			String userPrefs = (String) params[0];
			if (userPrefs == null || userPrefs.isEmpty()) {
				return false;
			}
			try {
				List<Map<String, Object>> prefs = (List<Map<String, Object>>) ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(userPrefs);
				targetObject.getVarsManager().applyCustomPrefs(prefs, true);
				targetObject.getDesktop().getCallbacks().execute("onUserPrefsLoading", prefs);
				targetObject.applyUserPrefs(prefs);
				targetObject.getDesktop().getCallbacks().execute("onUserPrefsLoaded", prefs);
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		protected String[] buildParamNames() {
			return new String[] { "userPreferences" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "user preferences. User preferences format follows the exported user preferences." };
		}
		@Override
		protected String getHelp() {
			return "Loads the user preferences.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> SAVE_USER_PREFERENCES = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "saveUserPreferences",
			Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			String hasUserPrefNameSpace = targetObject.getVarsManager().getUserPrefNamespace();
			if (hasUserPrefNameSpace == null || hasUserPrefNameSpace.isEmpty()) {
				return false;
			}
			try {
				List<Map<String, Object>> userPrefs = targetObject.getUserPrefs();
				targetObject.getDesktop().getCallbacks().execute("onUserPrefsSaving", userPrefs);
				String json = targetObject.getLayoutFilesManager().toJson(userPrefs);
				targetObject.getUserFilesManager().saveFile(AmiWebConsts.USER_SETTING_AMI_PREFS_PREFIX + hasUserPrefNameSpace, json);
				targetObject.getDesktop().getCallbacks().execute("onUserPrefsSaved", userPrefs);
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		@Override
		protected String getHelp() {
			return "Saves the user preferences. Same as Account > Save Preferences. Returns false if User Namespace is not set";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> LOAD_USER_PREFERENCES2 = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "loadUserPreferences",
			Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService targetObject, Object[] params, DerivedCellCalculator caller) {
			String hasUserPrefNameSpace = targetObject.getVarsManager().getUserPrefNamespace();
			if (hasUserPrefNameSpace == null || hasUserPrefNameSpace.isEmpty()) {
				return false;
			}
			try {
				String json = targetObject.getUserFilesManager().loadFile(AmiWebConsts.USER_SETTING_AMI_PREFS_PREFIX + hasUserPrefNameSpace);
				if (SH.is(json)) {
					Object obj = ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(json);
					targetObject.getVarsManager().applyCustomPrefs((List<Map<String, Object>>) obj, true);

					targetObject.getDesktop().getCallbacks().execute("onUserPrefsLoading", obj);
					targetObject.applyUserPrefs((List<Map<String, Object>>) obj);
					targetObject.getDesktop().getCallbacks().execute("onUserPrefsLoaded", obj);
				}
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		@Override
		protected String getHelp() {
			return "Loads the user preferences. Same as Account > Load Preferences. Returns false if User Namespace is not set";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebService> COPY_TO_CLIPBOARD = new AmiAbstractMemberMethod<AmiWebService>(AmiWebService.class, "copyToClipboard", String.class,
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebService service, Object[] params, DerivedCellCalculator caller) {
			String toCopy = (String) params[0];
			if (toCopy.length() > MAX_CLIPBOARD_SIZE)
				return "FAILED: Too large for clipboard";
			StringBuilder sb = service.getPortletManager().getPendingJs();
			sb.append(PortletHelper.createJsCopyToClipboard(toCopy));
			return "SUCCESS";
		}
		@Override
		protected String getHelp() {
			return "Copies text to clipboard - returns result of copy";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "text" };
		}
	};

	private static String toDerivedString(Object[] params) {
		String message;
		if (params.length > 1) {
			StringBuilder sb = AmiUtils.s(params[0], new StringBuilder());
			for (int i = 1; i < params.length; i++)
				AmiUtils.s(params[i], sb.append(','));
			message = sb.toString();
		} else
			message = AmiUtils.s(params[0]);
		return message;
	}

	public final static AmiWebScriptMemberMethods_Session INSTANCE = new AmiWebScriptMemberMethods_Session();
}
