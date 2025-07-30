package com.vortex.web;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.container.Partition;
import com.f1.container.ResultMessage;
import com.f1.http.HttpRequestResponse;
import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.WebUserAttribute;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletManagerListener;
import com.f1.suite.web.portal.PortletService;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.visual.ProgressBarPortlet;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.IdWebCellFormatter;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.suite.web.table.impl.MemoryWebCellFormatter;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.suite.web.table.impl.PercentWebCellFormatter;
import com.f1.suite.web.table.impl.ToggleButtonCellFormatter;
import com.f1.suite.web.table.impl.WebCellStyleWrapperFormatter;
import com.f1.utils.BitMaskDescription;
import com.f1.utils.CH;
import com.f1.utils.FastPrintStream;
import com.f1.utils.Formatter;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.Property;
import com.f1.utils.SH;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.vortexcommon.msg.agent.VortexAgentDbColumn;
import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentEvent;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.VortexAgentMachineEventStats;
import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.f1.vortexcommon.msg.agent.VortexAgentNetLink;
import com.f1.vortexcommon.msg.agent.VortexAgentSnapshot;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentInspectDbResponse;
import com.f1.vortexcommon.msg.eye.VortexEyeChanges;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryHistoryResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeSnapshotRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeSnapshotResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeStatusRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeStatusResponse;
import com.vortex.client.VortexClientEntitlementsManager;
import com.vortex.client.VortexClientExpectation;
import com.vortex.client.VortexClientManager;
import com.vortex.client.VortexClientManagerListener;
import com.vortex.client.VortexClientUtils;
import com.vortex.ssoweb.SsoService;
import com.vortex.web.formatters.ChecksumFormatter;
import com.vortex.web.formatters.ClassNameFormatter;
import com.vortex.web.formatters.FileNameFormatter;
import com.vortex.web.formatters.HostNameFormatter;
import com.vortex.web.portlet.grids.VortexWebEntitlementsDialogPortlet;

public class VortexWebEyeService implements PortletService, VortexClientManagerListener, PortletManagerListener {

	private static final Logger log = LH.get(VortexWebEyeService.class);
	public static final String ID = "AgentSnapshotService";
	private static final Set<Class<? extends Action>> INTERESTED = (Set) CH.s(VortexAgentSnapshot.class, VortexEyeChanges.class, VortexEyeResponse.class,
			VortexEyeQueryHistoryResponse.class, VortexAgentInspectDbResponse.class, MsgStatusMessage.class);

	final private Map<Message, String> requestToPortletId = new IdentityHashMap<Message, String>();
	private final VortexClientManager agentManager = new VortexClientManager();

	final private PortletManager manager;
	final private MapWebCellFormatter<Byte> eventSeverityFormatter;
	final private MapWebCellFormatter<Integer> eventStatusFormatter;
	final private WebCellFormatter percentFormatter;
	final private BasicWebCellFormatter quantityFormatter;
	final private BasicWebCellFormatter warningNumberFormatter;
	final private BasicWebCellFormatter symbolWebCellFormatter;
	final private BasicWebCellFormatter datetimeWebCellFormatter;
	final private BasicWebCellFormatter dateWebCellFormatter;
	final private BasicWebCellFormatter fullDatetimeWebCellFormatter;
	final private BasicWebCellFormatter timeWebCellFormatter;
	final private BasicWebCellFormatter showButtonWebCellFormatter;
	final private BasicWebCellFormatter hostnameFormatter;
	final private BasicWebCellFormatter filenameFormatter;
	final private BasicWebCellFormatter classNameFormatter;
	final private BasicWebCellFormatter userFormatter;
	final private MapWebCellFormatter<Byte> connectionStateFormatter;
	final private MapWebCellFormatter<Byte> dbColumnTypeFormatter;
	final private MapWebCellFormatter<Byte> dbObjectTypeFormatter;
	final private MapWebCellFormatter<Byte> agentMachineEventLevelFormatter;
	final private MapWebCellFormatter<Byte> netAddressTypeFormatter;
	final private MapWebCellFormatter<Byte> netAddressScopeFormatter;
	final private MapWebCellFormatter<Byte> ruleTypeFormatter;
	final private WebCellFormatter netLinkStateFormatter;
	final private MapWebCellFormatter<Integer> logLevelFormatter;
	final private MapWebCellFormatter<Byte> agentTypeFormatter;
	final private MapWebCellFormatter<Byte> expectationsStateFormatter;
	final private BasicWebCellFormatter memoryFormatter;
	final private BasicWebCellFormatter checksumFormatter;
	final private MapWebCellFormatter<Byte> propertyTypeFormatter;
	final private BasicWebCellFormatter basicFormatter;

	public static final byte STATE_REQUEST_NOT_SENT = 1;
	public static final byte STATE_REQUEST_SENT = 2;
	public static final byte STATE_RESPONSE_RECEIVED = 3;
	public static final byte STATE_RESPONSE_AND_DELTA_RECEIVED = 4;
	public static final byte STATE_DISCONNECTED = 5;
	public static final byte STATE_INIT = 6;
	private static final long PING_TIME = 0;
	private static final long PING_TIME_WAIT = 5000;

	private byte state = STATE_INIT;

	final private List<VortexEyeChanges> queue = new ArrayList<VortexEyeChanges>();
	private VortexEyeSnapshotRequest snapshotRequest;
	final private BasicWebCellFormatter appnameFormatter;
	final private BasicWebCellFormatter agentTypeMaskFormatter;
	final private BasicWebCellFormatter fileMaskFormatter;
	final private SsoService ssoservice;
	final private VortexClientEntitlementsManager entitlementsManager;
	private long sendPingTime;

	public VortexWebEyeService(PortletManager manager) {

		this.manager = manager;

		final LocaleFormatter localeFormatter = manager.getState().getWebState().getFormatter();
		final Formatter datetimeFormatter = localeFormatter.getDateFormatter(LocaleFormatter.DATETIME);
		final Formatter dateFormatter = localeFormatter.getDateFormatter(LocaleFormatter.DATE);
		final Formatter fullDatetimeFormatter = localeFormatter.getDateFormatter(LocaleFormatter.DATETIME_FULL);
		final Formatter timeFormatter = localeFormatter.getDateFormatter(LocaleFormatter.TIME);
		final Formatter n = localeFormatter.getNumberFormatter(0);
		final Formatter pn = localeFormatter.getPercentFormatter(2);

		basicFormatter = new BasicWebCellFormatter().setDefaultWidth(100).setComparator(SH.COMPARATOR_CASEINSENSITIVE).lockFormatter();
		memoryFormatter = new MemoryWebCellFormatter().setDefaultWidth(80).lockFormatter();
		checksumFormatter = new ChecksumFormatter().setDefaultWidth(80).setCssClass("fixedfont").lockFormatter();
		percentFormatter = new WebCellStyleWrapperFormatter(new PercentWebCellFormatter(pn).setCssClass("white"), false, "_bg=#77EE77").setDefaultWidth(90);//.lockFormatter();
		quantityFormatter = new NumberWebCellFormatter(n).setDefaultWidth(80).lockFormatter();
		symbolWebCellFormatter = new BasicWebCellFormatter().setCssClass("bold").setDefaultWidth(60).lockFormatter();
		datetimeWebCellFormatter = new NumberWebCellFormatter(datetimeFormatter).setDefaultWidth(120).lockFormatter();
		dateWebCellFormatter = new NumberWebCellFormatter(dateFormatter).setDefaultWidth(80).lockFormatter();
		fullDatetimeWebCellFormatter = new NumberWebCellFormatter(fullDatetimeFormatter).setDefaultWidth(140).lockFormatter();
		timeWebCellFormatter = new NumberWebCellFormatter(timeFormatter).setDefaultWidth(60).lockFormatter();
		hostnameFormatter = new HostNameFormatter().setDefaultWidth(80).setCssClass("col_location").lockFormatter();
		filenameFormatter = new FileNameFormatter().setDefaultWidth(120).setCssClass("col_location").lockFormatter();
		classNameFormatter = new ClassNameFormatter().setDefaultWidth(60).lockFormatter();
		userFormatter = new BasicWebCellFormatter().setDefaultWidth(60).setCssClass("col_location").lockFormatter();
		appnameFormatter = new BasicWebCellFormatter().setDefaultWidth(170).setCssClass("col_location").lockFormatter();
		showButtonWebCellFormatter = new ToggleButtonCellFormatter("image_show_dn", "image_show_up", "shown", "hidden").setDefaultWidth(30).lockFormatter();

		warningNumberFormatter = new NumberWebCellFormatter(n).setDefaultWidth(80).addCssClass("red");
		warningNumberFormatter.addConditionalDefault("style.color=red").addConditionalString("style.color=grey", 0L, 0, null).lockFormatter();

		propertyTypeFormatter = new MapWebCellFormatter<Byte>(manager.getTextFormatter());
		propertyTypeFormatter.addEntry(Property.TYPE_CODE, "Java Hardcoded");
		propertyTypeFormatter.addEntry(Property.TYPE_COLLECTION, "Collection");
		propertyTypeFormatter.addEntry(Property.TYPE_FILE, "Property File");
		propertyTypeFormatter.addEntry(Property.TYPE_PREFERENCE, "User Preference");
		propertyTypeFormatter.addEntry(Property.TYPE_RESOURCE, "Resource File");
		propertyTypeFormatter.addEntry(Property.TYPE_SYSTEM_ENV, "System Environment");
		propertyTypeFormatter.addEntry(Property.TYPE_SYSTEM_PROPERTY, "System Property");
		propertyTypeFormatter.setDefaultWidth(80).lockFormatter();

		eventSeverityFormatter = new MapWebCellFormatter<Byte>(manager.getTextFormatter());
		eventSeverityFormatter.addEntry(VortexAgentEvent.SEVERITY_NORMAL, "NORMAL");
		eventSeverityFormatter.addEntry(VortexAgentEvent.SEVERITY_WARNING, "WARNING");
		eventSeverityFormatter.addEntry(VortexAgentEvent.SEVERITY_SEVERE, "SEVERE");
		eventSeverityFormatter.setDefaultWidth(60).lockFormatter();

		eventStatusFormatter = new MapWebCellFormatter<Integer>(manager.getTextFormatter());
		eventStatusFormatter.addEntry(VortexAgentEvent.STATUS_UNKNOWN, "UNKNOWN");
		eventStatusFormatter.addEntry(VortexAgentEvent.STATUS_OK, "OK");
		eventStatusFormatter.addEntry(VortexAgentEvent.STATUS_NOT_OK, "NOT OK");
		eventStatusFormatter.addEntry(VortexAgentEvent.STATUS_BAD, "BAD");
		eventStatusFormatter.setDefaultWidth(60).lockFormatter();

		connectionStateFormatter = new MapWebCellFormatter<Byte>(manager.getTextFormatter());
		connectionStateFormatter.addEntry(VortexAgentNetConnection.STATE_LISTEN, "listening", "style.color=green");
		connectionStateFormatter.addEntry(VortexAgentNetConnection.STATE_ESTABLISHED, "established", "style.color=blue");
		connectionStateFormatter.addEntry(VortexAgentNetConnection.STATE_CLOSE_WAIT, "close wait", "style.color=red");
		connectionStateFormatter.addEntry(VortexAgentNetConnection.STATE_TIME_WAIT, "time wait", "style.color=grey");

		connectionStateFormatter.addEntry(VortexAgentNetConnection.STATE_SYN_SENT, "syn sent", "style.color=grey");
		connectionStateFormatter.addEntry(VortexAgentNetConnection.STATE_SYN_RECV, "syn recv", "style.color=grey");
		connectionStateFormatter.addEntry(VortexAgentNetConnection.STATE_FIN_WAIT1, "fin wait1", "style.color=grey");
		connectionStateFormatter.addEntry(VortexAgentNetConnection.STATE_FIN_WAIT2, "fin wait2", "style.color=grey");
		connectionStateFormatter.addEntry(VortexAgentNetConnection.STATE_CLOSING, "closing", "style.color=red");
		connectionStateFormatter.addEntry(VortexAgentNetConnection.STATE_LAST_ACK, "last ack", "style.color=grey");
		connectionStateFormatter.addEntry(VortexAgentNetConnection.STATE_CLOSED, "closed", "style.color=red");
		connectionStateFormatter.addEntry(VortexAgentNetConnection.STATE_DELETE_TCB, "dele tcb", "style.color=grey");

		connectionStateFormatter.setDefaultWidth(75).lockFormatter();

		agentTypeFormatter = new MapWebCellFormatter<Byte>(manager.getTextFormatter());
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_DB_COLUMN, "Database Column");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_DB_DATABASE, "Database");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_DB_OBJECT, "Database Object");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_DB_PRIVILEDGE, "Database Priviledge");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_DB_SERVER, "Database Server");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_DB_TABLE, "Database Table");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_FILE_SYSTEM, "File System ");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_MACHINE, "Machine");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_NET_ADDRESS, "Net Address");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_NET_CONNECTION, "Net Connection");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_NET_LINK, "Net Link");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_PROCESS, "Process");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_CRON, "Crontab");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_BACKUP, "Backup");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_BACKUP_DESTINATION, "Backup Destination");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_BUILD_PROCEDURE, "Build Procedure");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_BUILD_RESULT, "Build Result");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_DEPLOYMENT, "Deployment");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_DEPLOYMENT_SET, "Deployment Set");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_DEVICE, "Device");
		agentTypeFormatter.addEntry(VortexAgentEntity.TYPE_EXPECTATION, "Expectation");
		agentTypeFormatter.setDefaultWidth(75).lockFormatter();

		BitMaskDescription agentTypesMaskDescription = new BitMaskDescription("Agent Type Mask");
		for (Entry<Byte, String> e : agentTypeFormatter.getEnumValuesAsText().entrySet())
			agentTypesMaskDescription.define(1L << e.getKey(), e.getValue());
		agentTypeMaskFormatter = new NumberWebCellFormatter(agentTypesMaskDescription).setDefaultWidth(175).lockFormatter();

		BitMaskDescription fileMaskDescription = new BitMaskDescription("File Mask");
		fileMaskDescription.define(VortexAgentFile.DIRECTORY, "Dir");
		fileMaskDescription.define(VortexAgentFile.FILE, "file");
		fileMaskDescription.define(VortexAgentFile.READABLE, "Read");
		fileMaskDescription.define(VortexAgentFile.WRITEABLE, "Write");
		fileMaskDescription.define(VortexAgentFile.EXECUTABLE, "Execute");
		fileMaskDescription.define(VortexAgentFile.HIDDEN, "Hidden");
		fileMaskDescription.define(VortexAgentFile.ASCII, "Ascii");
		fileMaskDescription.define(VortexAgentFile.DATA_DEFPLATED, "Cmp");
		fileMaskFormatter = new NumberWebCellFormatter(fileMaskDescription).setDefaultWidth(160).lockFormatter();

		expectationsStateFormatter = new MapWebCellFormatter<Byte>(manager.getTextFormatter());
		expectationsStateFormatter.addEntry(VortexClientExpectation.STATE_MATCHED, "Okay", "_cna=portlet_icon_okay", "");
		expectationsStateFormatter.addEntry(VortexClientExpectation.STATE_NO_MATCH, "No Mathing Expectation", "_cna=", "");
		expectationsStateFormatter.setDefaultWidth(20).lockFormatter();

		dbColumnTypeFormatter = new MapWebCellFormatter<Byte>(manager.getTextFormatter());
		dbColumnTypeFormatter.addEntry(VortexAgentDbColumn.TYPE_BOOLEAN, "BOOLEAN");
		dbColumnTypeFormatter.addEntry(VortexAgentDbColumn.TYPE_INT, "INT");
		dbColumnTypeFormatter.addEntry(VortexAgentDbColumn.TYPE_CHAR, "CHAR");
		dbColumnTypeFormatter.addEntry(VortexAgentDbColumn.TYPE_VARCHAR, "VARCHAR");
		dbColumnTypeFormatter.addEntry(VortexAgentDbColumn.TYPE_FLOAT, "FLOAT");
		dbColumnTypeFormatter.addEntry(VortexAgentDbColumn.TYPE_FIXEDPOINT, "FIXEDPOINT");
		dbColumnTypeFormatter.addEntry(VortexAgentDbColumn.TYPE_BLOB, "BLOB");
		dbColumnTypeFormatter.addEntry(VortexAgentDbColumn.TYPE_ENUM, "ENUM");
		dbColumnTypeFormatter.addEntry(VortexAgentDbColumn.TYPE_SET, "SET");
		dbColumnTypeFormatter.addEntry(VortexAgentDbColumn.TYPE_DATE, "DATE");
		dbColumnTypeFormatter.addEntry(VortexAgentDbColumn.TYPE_DATETIME, "DATETIME");
		dbColumnTypeFormatter.addEntry(VortexAgentDbColumn.TYPE_TIMESTAMP, "TIMESTAMP");
		dbColumnTypeFormatter.addEntry(VortexAgentDbColumn.TYPE_TIME, "TIME");
		dbColumnTypeFormatter.addEntry(VortexAgentDbColumn.TYPE_YEAR, "YEAR");
		dbColumnTypeFormatter.addEntry(VortexAgentDbColumn.TYPE_OTHER, "OTHER");
		dbColumnTypeFormatter.setDefaultWidth(60).lockFormatter();

		dbObjectTypeFormatter = new MapWebCellFormatter<Byte>(manager.getTextFormatter());
		dbObjectTypeFormatter.addEntry(VortexAgentDbObject.PROCEDURE, "PROCEDURE");
		dbObjectTypeFormatter.addEntry(VortexAgentDbObject.TRIGGER, "TRIGGER");
		dbObjectTypeFormatter.addEntry(VortexAgentDbObject.CONSTRAINT, "CONSTRAINT");
		dbObjectTypeFormatter.setDefaultWidth(60).lockFormatter();

		agentMachineEventLevelFormatter = new MapWebCellFormatter<Byte>(manager.getTextFormatter());
		agentMachineEventLevelFormatter.addEntry(VortexAgentMachineEventStats.LEVEL_ALL, "ALL");
		agentMachineEventLevelFormatter.addEntry(VortexAgentMachineEventStats.LEVEL_ERROR, "ERROR");
		agentMachineEventLevelFormatter.addEntry(VortexAgentMachineEventStats.LEVEL_WARNING, "WARNING");
		agentMachineEventLevelFormatter.addEntry(VortexAgentMachineEventStats.LEVEL_INFORMATION, "INFO");
		agentMachineEventLevelFormatter.addEntry(VortexAgentMachineEventStats.LEVEL_SUCCESS_AUDIT, "SUCCESS");
		agentMachineEventLevelFormatter.addEntry(VortexAgentMachineEventStats.LEVEL_FAILURE_AUDIT, "FAILURE");
		agentMachineEventLevelFormatter.addEntry(VortexAgentMachineEventStats.LEVEL_UNKNOWN, "UNKNOWN");
		agentMachineEventLevelFormatter.setDefaultWidth(60).lockFormatter();

		netAddressTypeFormatter = new MapWebCellFormatter<Byte>(manager.getTextFormatter());
		netAddressTypeFormatter.addEntry(VortexAgentNetAddress.TYPE_INET, "IPv4");
		netAddressTypeFormatter.addEntry(VortexAgentNetAddress.TYPE_INET6, "IPv6");
		netAddressTypeFormatter.setDefaultWidth(40).lock();

		netAddressScopeFormatter = new MapWebCellFormatter<Byte>(manager.getTextFormatter());
		netAddressScopeFormatter.addEntry(VortexAgentNetAddress.SCOPE_GLOBAL, "global");
		netAddressScopeFormatter.addEntry(VortexAgentNetAddress.SCOPE_HOST, "host");
		netAddressScopeFormatter.addEntry(VortexAgentNetAddress.SCOPE_LINK, "link");
		netAddressScopeFormatter.addEntry(VortexAgentNetAddress.SCOPE_SITE, "site");
		netAddressScopeFormatter.setDefaultWidth(40).lock();

		ruleTypeFormatter = new MapWebCellFormatter<Byte>(manager.getTextFormatter());
		ruleTypeFormatter.addEntry(F1AppAuditTrailRule.EVENT_TYPE_F1, "F1 Event", "style.color=brown");
		ruleTypeFormatter.addEntry(F1AppAuditTrailRule.EVENT_TYPE_LOG, "Log Event", "style.color=purple");
		ruleTypeFormatter.addEntry(F1AppAuditTrailRule.EVENT_TYPE_SQL, "Sql", "style.color=green");
		ruleTypeFormatter.addEntry(F1AppAuditTrailRule.EVENT_TYPE_MSG, "Message", "style.color=blue");
		ruleTypeFormatter.setDefaultWidth(65).lock();

		logLevelFormatter = new MapWebCellFormatter<Integer>(manager.getTextFormatter());
		for (Entry<Integer, String> i : SpeedLoggerLevels.LEVELS_2_LABEL.entrySet()) {
			if (i.getKey() >= SpeedLoggerLevels.WARNING) {
				logLevelFormatter.addEntry(i.getKey(), i.getValue(), "style.color=red");
			} else {
				logLevelFormatter.addEntry(i.getKey(), i.getValue());
			}
		}
		logLevelFormatter.setDefaultWidth(40).lock();

		BitMaskDescription netLinkBitMaskDescription = new BitMaskDescription("Net Link State");
		netLinkBitMaskDescription.define(VortexAgentNetLink.STATE_ALLMULTI, "All Multi");
		netLinkBitMaskDescription.define(VortexAgentNetLink.STATE_BROADCAST, "Broadcast");
		netLinkBitMaskDescription.define(VortexAgentNetLink.STATE_DYNAMIC, "Dynamic");
		netLinkBitMaskDescription.define(VortexAgentNetLink.STATE_LOOPBACK, "Loopback");
		netLinkBitMaskDescription.define(VortexAgentNetLink.STATE_LOWER_UP, "Lower up");
		netLinkBitMaskDescription.define(VortexAgentNetLink.STATE_MULTICAST, "Multicast");
		netLinkBitMaskDescription.define(VortexAgentNetLink.STATE_NOARP, "No Arp");
		netLinkBitMaskDescription.define(VortexAgentNetLink.STATE_POINTTOPOINT, "P2P");
		netLinkBitMaskDescription.define(VortexAgentNetLink.STATE_PROMISC, "All Promisc");
		netLinkBitMaskDescription.define(VortexAgentNetLink.STATE_SLAVE, "Slave");
		netLinkBitMaskDescription.define(VortexAgentNetLink.STATE_UP, "Up");
		netLinkStateFormatter = new NumberWebCellFormatter(netLinkBitMaskDescription).setDefaultWidth(175).lockFormatter();

		this.entitlementsManager = new VortexClientEntitlementsManager(this.agentManager, this.manager.getGenerator());
		ssoservice = (SsoService) manager.getServiceNoThrow(SsoService.ID);
		if (ssoservice != null) {
			ssoservice.addDialogForUsers(new VortexWebEntitlementsDialogPortlet.Builder());
		}
		agentManager.addClientConnectedListener(this);
		manager.addPortletManagerListener(this);
		manager.sendRequestToBackend("Vortex", manager.getGenerator().nw(VortexEyeStatusRequest.class));
		this.sendPingTime = manager.getNow();
	}

	public VortexClientEntitlementsManager getEntitlementsManager() {
		return this.entitlementsManager;
	}

	//
	// ######### FORMATTERS ##########
	//
	public WebCellFormatter getNetAddressTypeFormatter() {
		return netAddressTypeFormatter;
	}
	public WebCellFormatter getNetAddressScopeFormatter() {
		return netAddressScopeFormatter;
	}
	public WebCellFormatter getNetLinkStateFormatter() {
		return netLinkStateFormatter;
	}
	public WebCellFormatter getRuleTypeFormatter() {
		return ruleTypeFormatter;
	}
	public WebCellFormatter getLogLevelFormatter() {
		return logLevelFormatter;
	}
	public WebCellFormatter getAgentMachineEventLevelFormatter() {
		return agentMachineEventLevelFormatter;
	}
	public WebCellFormatter getHostnameFormatter() {
		return hostnameFormatter;
	}
	public WebCellFormatter getFilenameFormatter() {
		return filenameFormatter;
	}
	public WebCellFormatter getClassNameFormatter() {
		return classNameFormatter;
	}
	public WebCellFormatter getUserFormatter() {
		return userFormatter;
	}
	public WebCellFormatter getAppnameFormatter() {
		return appnameFormatter;
	}
	public WebCellFormatter getIdFormatter(String prepend) {
		return new IdWebCellFormatter(prepend);
	}
	public BasicWebCellFormatter getBasicFormatter() {
		return basicFormatter;
	}
	public MapWebCellFormatter<Byte> getDbObjectTypeFormatter() {
		return dbObjectTypeFormatter;
	}
	public MapWebCellFormatter<Byte> getDbColumnTypeFormatter() {
		return dbColumnTypeFormatter;
	}
	public BasicWebCellFormatter getMemoryFormatter() {
		return memoryFormatter;
	}
	public BasicWebCellFormatter getChecksumFormatter() {
		return checksumFormatter;
	}
	public MapWebCellFormatter<Byte> getConnectionStateFormatter() {
		return connectionStateFormatter;
	}
	public MapWebCellFormatter<Byte> getEventSeverityFormatter() {
		return eventSeverityFormatter;
	}
	public MapWebCellFormatter<Byte> getPropertyTypeFormatter() {
		return propertyTypeFormatter;
	}
	public MapWebCellFormatter<Integer> getEventStatusFormatter() {
		return eventStatusFormatter;
	}
	public WebCellFormatter getPercentFormatter() {
		return percentFormatter;
	}
	public BasicWebCellFormatter getNumberFormatter() {
		return quantityFormatter;
	}
	public BasicWebCellFormatter getWarningNumberFormatter() {
		return warningNumberFormatter;
	}

	public BasicWebCellFormatter getSymbolWebCellFormatter() {
		return symbolWebCellFormatter;
	}

	public BasicWebCellFormatter getDateTimeWebCellFormatter() {
		return datetimeWebCellFormatter;
	}
	public BasicWebCellFormatter getDateWebCellFormatter() {
		return dateWebCellFormatter;
	}
	public BasicWebCellFormatter getFullDateTimeWebCellFormatter() {
		return fullDatetimeWebCellFormatter;
	}
	public BasicWebCellFormatter getTimeWebCellFormatter() {
		return timeWebCellFormatter;
	}

	public BasicWebCellFormatter getShowButtonWebCellFormatter() {
		return showButtonWebCellFormatter;
	}

	public MapWebCellFormatter<Byte> getAgentTypeFormatter() {
		return this.agentTypeFormatter;
	}
	public BasicWebCellFormatter getAgentTypeMaskFormatter() {
		return this.agentTypeMaskFormatter;
	}

	public MapWebCellFormatter<Byte> getExpectationsStateFormatter() {
		return this.expectationsStateFormatter;
	}

	public BasicWebCellFormatter getBasicNotNullFormatter(String notNull) {
		return new BasicWebCellFormatter().setNullValue(notNull);
	}
	public WebCellFormatter getFileMaskFormatter() {
		return fileMaskFormatter;
	}

	//
	// #### PORTLET INTERACTIONS ####
	//
	public void sendRequestToBackend(String portletId, VortexEyeRequest request) {
		if (!getEntitlementsManager().checkEntitled(request, manager)) {
			manager.showAlert("----Not entitled for this action----");
			return;
		}
		request.setInvokedBy(getUserName());
		manager.sendRequestToBackend("Vortex", portletId, request);
	}
	public VortexClientManager getAgentManager() {
		return this.agentManager;
	}

	//
	// ######### SERVICE IMPLEMENTATION ##########
	//

	@Override
	public String getServiceId() {
		return ID;
	}

	@Override
	public void close() {
		this.agentManager.onDisconnect(false);
	}

	@Override
	public Set<Class<? extends Action>> getInterestedBackendMessages() {
		return INTERESTED;
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		Action action = result.getAction();
		LH.fine(log, action.getClass() + " response from backend");
		if (action instanceof VortexEyeResponse) {
			if (action instanceof VortexEyeStatusResponse) {
				if (state == STATE_INIT || state == STATE_DISCONNECTED) {
					onVortexDisconnect(false);
					setState(STATE_REQUEST_NOT_SENT);
					sendSnapshotRequestIfNecessary();
				}
			} else if (action instanceof VortexEyeSnapshotResponse) {
				onVortexSnapshotResponse((VortexEyeSnapshotResponse) action, (VortexEyeSnapshotRequest) result.getRequestMessage().getAction(), result);
			} else {
				VortexEyeResponse ver = (VortexEyeResponse) action;
				//if (!ver.getOk()) {
				//if (ver.getMessage() == null)
				//manager.showAlert("Response not okay, but missing message: " + ver.getClass().getName());
				//else
				//manager.showAlert(ver.getMessage());
				//} else if (SH.is(ver.getMessage()))
				//manager.showAlert(ver.getMessage());
			}
		}

	}
	@Override
	public void onBackendAction(Action action) {
		if (action instanceof MsgStatusMessage) {
			MsgStatusMessage status = (MsgStatusMessage) action;
			if ("server.to.gui".equals(status.getTopic()) && status.getTopic() != null) {
				if (!status.getIsConnected()) {
					onVortexDisconnect(true);
				} else {
					onVortexConnect();
				}
			}
			return;
		} else if (action instanceof VortexEyeChanges) {
			onVortexAction((VortexEyeChanges) action);
		} else
			throw new RuntimeException("can not process message: " + action);
	}

	//
	//########### CONNECTION STATE MANAGEMENT ############
	//

	private void onVortexDisconnect(boolean showAlert) {
		switch (state) {
			case STATE_DISCONNECTED:
				break;
			case STATE_REQUEST_NOT_SENT:
				break;
			case STATE_REQUEST_SENT:
				queue.clear();
				setState(state);
				break;
			case STATE_RESPONSE_RECEIVED:
			case STATE_RESPONSE_AND_DELTA_RECEIVED:
				agentManager.onDisconnect(true);
				setState(STATE_DISCONNECTED);
				if (showAlert)
					manager.showAlert("Webserver has disconnected from vortex eye");
				break;
		}
	}

	private void onVortexConnect() {
		switch (state) {
			case STATE_DISCONNECTED:
			case STATE_INIT:
			case STATE_REQUEST_NOT_SENT:
				sendVortexSnapshotRequest();
				setState(STATE_REQUEST_SENT);
				break;
			case STATE_REQUEST_SENT://re-send, test track main came up after request was sent.
				queue.clear();
				sendVortexSnapshotRequest();
				break;
			case STATE_RESPONSE_RECEIVED:
			case STATE_RESPONSE_AND_DELTA_RECEIVED:
				LH.warning(log, "connect w/o disconnect(resposne received)!");
				break;
		}
	}

	private class StatusDialog extends GridPortlet {

		private ProgressBarPortlet progressBar;
		private int totalCount;
		private HtmlPortlet status;

		public StatusDialog(PortletConfig config, int totalCount) {
			super(config);
			this.totalCount = totalCount;
			Formatter nf = manager.getLocaleFormatter().getNumberFormatter(0);
			String text = "<div style=\"width:400px;height:400px;color:white\"><div id='PROCESS_SNAPSHOT_DIV" + getPortletId()
					+ "' style=\"width:690px;left:-145px;top:-160px;height:690px;overflow:hidden;background:url('rsc/tunnel.gif')\">Processing snapshot from eye</div>"
					+ "<div style=\"width:100%;top:150px;font-size:20px;color:#005500\"><center>Loading Data from Backend<BR></div></div>";
			addChild(new HtmlPortlet(generateConfig(), text, "scrollpane"), 0, 0).setJavascript("rotateDiv('PROCESS_SNAPSHOT_DIV" + getPortletId() + "');\n");
			this.progressBar = addChild(new ProgressBarPortlet(generateConfig()), 0, 1);
			this.status = addChild(new HtmlPortlet(generateConfig(), ""), 0, 2);
			this.progressBar.setProgress(0);
			setRowSize(1, 35);
			setRowSize(2, 25);
			setSuggestedSize(400, 400);
			setProcessedCount(0);
		}

		@Override
		public void onClosed() {
			super.onClosed();
			getManager().getPendingJs().append("stopRotateDiv('PROCESS_SNAPSHOT_DIV" + getPortletId() + "');");
		}

		public void setProcessedCount(int count) {
			Formatter nf = manager.getLocaleFormatter().getNumberFormatter(0);
			StringBuilder sb = new StringBuilder();
			if (totalCount > 0) {
				sb.append("<center><B>");
				nf.format(count, sb);
				sb.append(" / ");
				nf.format(totalCount, sb);
				sb.append(" </B>");
			}
			this.status.setHtml(sb.toString());
			this.progressBar.setProgress((double) count / totalCount);
		}

		public void setTotalObjectsCount(int totalCount) {
			this.totalCount = totalCount;

		}

	}

	private int snapshotProcessedCount = 0;
	private StatusDialog statusDialog = null;

	private void onVortexSnapshotResponse(VortexEyeSnapshotResponse action, VortexEyeSnapshotRequest request, ResultMessage<Action> result) {
		if (request != snapshotRequest) {
			LH.warning(log, "response for old request being ignored");
			return;
		} else if (!result.getIsIntermediateResult()) {
			this.snapshotRequest = null;
		}
		switch (state) {
			case STATE_DISCONNECTED:
			case STATE_REQUEST_NOT_SENT:
			case STATE_RESPONSE_RECEIVED:
			case STATE_RESPONSE_AND_DELTA_RECEIVED:
				LH.warning(log, "response received at bad state: ", state);
				break;
			case STATE_REQUEST_SENT: {
				if (result.getIsIntermediateResult()) {
					processVortexResponse(action);
					this.statusDialog.setTotalObjectsCount(action.getTotalObjectsCount());
					this.statusDialog.setProcessedCount(snapshotProcessedCount);
					//TODO: lets think about this, this is to let the front end in
					Partition par = manager.getState().getWebState().getPartition();
					par.unlockForWrite();
					OH.sleep(10);
					par.lockForWrite(100, TimeUnit.SECONDS);
				} else {
					try {
						processVortexResponse(action);
						agentManager.fireSnapshotProcessed();
						if (this.statusDialog != null) {
							this.statusDialog.close();
							this.statusDialog = null;
						}
						for (VortexEyeChanges queuedAction : queue)
							if (queuedAction.getSeqNum() > action.getSnapshot().getSeqNum())
								processVortexAction(queuedAction);
						queue.clear();
						applyEntitlements();
					} finally {
						setState(STATE_RESPONSE_RECEIVED);
					}
				}
				break;
			}
		}
	}
	private void applyEntitlements() {
		WebUserAttribute entitlements = manager.getState().getWebState().getUserAttributes().get("vortex_entitlements");
		if (entitlements != null) {
			this.entitlementsManager.setEntitlements((Map) new ObjectToJsonConverter().stringToObject(SH.toString(entitlements.getValue())));
		}

	}

	private void onVortexAction(VortexEyeChanges action) {
		switch (state) {
			case STATE_DISCONNECTED:
			case STATE_REQUEST_NOT_SENT:
				return;
			case STATE_REQUEST_SENT:
				queue.add((VortexEyeChanges) action);
				break;
			case STATE_RESPONSE_RECEIVED:
				if (action.getSeqNum() <= agentManager.getCurrentSeqNum()) {
					LH.info(log, "Dropping old message w/ seqnum before snapshot seqnum ", agentManager.getCurrentSeqNum(), ": ", action.getSeqNum());
					return;
				}
				setState(STATE_RESPONSE_AND_DELTA_RECEIVED);
				processVortexAction(action);
				break;
			case STATE_RESPONSE_AND_DELTA_RECEIVED:
				processVortexAction(action);
				break;
		}
	}

	private void sendVortexSnapshotRequest() {
		snapshotProcessedCount = 0;

		snapshotRequest = manager.getGenerator().nw(VortexEyeSnapshotRequest.class);
		snapshotRequest.setMaxBatchSize(15000);
		//snapshotRequest.setAmiObjectTypesToSend(new HashSet<String>(agentManager.getAmiObjectTypesBeingViewed()));
		snapshotRequest.setInvokedBy(manager.getState().getWebState().getUser().getUserName());
		snapshotRequest.setSupportsIntermediate(true);
		LH.info(log, "Sending request to backend for ", snapshotRequest.getInvokedBy());
		manager.sendRequestToBackend("Vortex", snapshotRequest);
		if (manager.getRoot() != null) {
			if (this.statusDialog == null) {
				this.statusDialog = new StatusDialog(manager.generateConfig(), 0);
				RootPortlet rp = (RootPortlet) manager.getRoot();
				//rp.addDialog(title, p, width, height)
				rp.addDialog("Processing Snapshot", statusDialog).setHasCloseButton(false);
				manager.onPortletAdded(statusDialog);
			}
		}
	}
	private void sendSnapshotRequestIfNecessary() {
		switch (state) {
			case STATE_REQUEST_NOT_SENT:
				sendVortexSnapshotRequest();
				setState(STATE_REQUEST_SENT);
				break;
			case STATE_RESPONSE_RECEIVED:
			case STATE_RESPONSE_AND_DELTA_RECEIVED:
			case STATE_DISCONNECTED:
			case STATE_REQUEST_SENT:
				break;
		}
	}

	public byte getConnectionState() {
		return state;
	}

	//
	//########### PHYSICAL PROCESSING OF MESSAGES ############
	//u
	private void processVortexResponse(VortexEyeSnapshotResponse response) {

		VortexEyeChanges ss = response.getSnapshot();
		LH.info(log, "Received snapshot from eye for client '", manager.describeUser() + "' w/ seqnum ", ss.getSeqNum(), " (agent entities count=",
				CH.size(ss.getAgentEntitiesAdded()), ", f1 entities count=" + CH.size(ss.getF1AppEntitiesAdded()), ")");

		VortexEyeChanges changes = response.getSnapshot();
		this.snapshotProcessedCount += CH.size(ss.getAgentEntitiesAdded()) + CH.size(ss.getF1AppEntitiesAdded()) + CH.size(ss.getF1AppEntitiesAdded());
		VortexClientUtils.processSnapshot(agentManager, changes, manager.getState().getWebState().getPartition().getContainer().getServices().getConverter());

	}
	private void processVortexAction(VortexEyeChanges action) {
		VortexEyeChanges changes = (VortexEyeChanges) action;
		ObjectToByteArrayConverter converter = (ObjectToByteArrayConverter) manager.getState().getWebState().getPartition().getContainer().getServices().getConverter();
		VortexClientUtils.processChanges(agentManager, changes, converter);
	}

	public String getUserName() {
		return manager.getState().getWebState().getUser().getUserName();
	}

	@Override
	public void onVortexEyeDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVortexEyeSnapshotProcessed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVortexClientListenerAdded(Object o) {
		if (o != this) {
			if (!manager.getIsLoadingConfig())
				sendSnapshotRequestIfNecessary();
		}
	}

	@Override
	public void onFrontendCalled(PortletManager manager, Map<String, String> attributes, HttpRequestAction action) {
		if (state == STATE_INIT && manager.getNow() > this.sendPingTime + PING_TIME_WAIT)
			setState(STATE_DISCONNECTED);
	}

	@Override
	public void onBackendCalled(PortletManager manager, Action action) {
	}

	@Override
	public void onInit(PortletManager manager, Map<String, Object> configuration, String rootId) {
		if (state != STATE_DISCONNECTED && state != STATE_INIT) {
			onVortexDisconnect(false);
			setState(STATE_REQUEST_NOT_SENT);
		}
	}

	private void setState(byte state) {
		if (this.state == state)
			return;
		this.state = state;
		this.agentManager.fireConnectionStateChanged(this);
	}

	//	@Override
	//	public void onInitDone(PortletManager basicPortletManager, Map<String, Object> configuration, String rootId) {
	//		sendSnapshotRequestIfNecessary();
	//	}

	@Override
	public void onVortexConnectionStateChanged(VortexClientManager vortexClientManager, VortexWebEyeService vortexWebEyeService) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPortletManagerClosed() {
		this.agentManager.onDisconnect(false);
	}

	@Override
	public void onPageRefreshed(PortletManager basicPortletManager) {
	}

	@Override
	public void onMetadataChanged(PortletManager basicPortletManager) {
	}

	@Override
	public void onPageLoading(PortletManager basicPortletManager, Map<String, String> attributes, HttpRequestResponse action) {
		FastPrintStream out = action.getOutputStream();
		out.print("<script type=\"text/javascript\" src=\"../custom.js?\"></script>");
	}

	@Override
	public void handleCallback(Map<String, String> attributes, HttpRequestAction action) {
		// TODO Auto-generated method stub

	}
}
