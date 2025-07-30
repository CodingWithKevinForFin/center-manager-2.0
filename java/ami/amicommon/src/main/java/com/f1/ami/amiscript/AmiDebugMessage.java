package com.f1.ami.amiscript;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.base.Legible;
import com.f1.utils.SH;

public class AmiDebugMessage {
	public static final byte SEVERITY_INFO = 1;
	public static final byte SEVERITY_WARNING = 2;

	public static final byte[] SEVERITY_TYPES = new byte[] { SEVERITY_INFO, SEVERITY_WARNING };

	public static final byte TYPE_LAYOUT = 1;//(serviceAri,null)
	public static final byte TYPE_DATASOURCE_QUERY = 30; //For AmiCenterQueryDsRequest.TYPE_QUERY
	public static final byte TYPE_DATASOURCE_SHOW_TABLES = 31; //For AmiCenterQueryDsRequest.TYPE_SHOW_TABLES
	public static final byte TYPE_DATASOURCE_UPLOAD = 32; //For AmiCenterQueryDsRequest.TYPE_UPLOAD
	public static final byte TYPE_DATASOURCE_PREVIEW = 33; //For AmiCenterQueryDsRequest.TYPE_PREVIEW
	public static final byte TYPE_DATASOURCE_RESPONSE = 23; //For AmiCenterQueryDsResponse
	public static final byte TYPE_AMISQL = 4;
	public static final byte TYPE_LOG = 6;//(ari,?)
	public static final byte TYPE_SCHEMA_MISMATCH = 7;//(ari,?)
	public static final byte TYPE_EVENT_SCHEDULED = 10;//(serviceAri,null)
	public static final byte TYPE_EVENT_EXECUTED = 11;
	public static final byte TYPE_CUSTOM_METHODS = 12;
	//	public static final byte TYPE_HTML_TEMPLATE = 13;
	public static final byte TYPE_CALLBACK = 14;
	public static final byte TYPE_FORMULA = 15;
	public static final byte TYPE_CMD_RESPONSE = 16;
	public static final byte TYPE_TEST = 18;
	public static final byte TYPE_VARIABLE = 19;
	public static final byte TYPE_QUERY_PLAN = 20;
	public static final byte TYPE_METHOD = 21;//([className::]methodName,null)
	public static final byte TYPE_DYNAMIC_AMISCRIPT = 22;
	public static final byte TYPE_ADMIN_CONSOLE = 24;

	//	public static final String TGT_DM_START = "Datamodel-Start";
	//	public static final String TGT_DM_COMPLETE = "Datamodel-Complete";
	//	public static final String TGT_DS_REQ = "Datasource-Request";
	//	public static final String TGT_DS_RES = "Datasource-Response";
	//	public static final String TGT_COLUMN_ACTION = "Column-Action";
	//	public static final String TGT_FIELD = "Field";
	//	public static final String TGT_BUTTON = "Button";
	//	public static final String TGT_RELATIONSHIP = "Relationship";
	//	public static final String TGT_CMD_RES = "Command-Response";
	//	public static final String TGT_DECLARED_METHOD = "Declared-Method";
	//	public static final String TGT_HTML_EMBEDDED = "Html-Embedded";
	//	public static final String TGT_NOTIFICATION = "Notification";
	//	public static final String TGT_MEM_METHOD = "Member-Method";
	//	public static final String TGT_CORRELATION_ID = "Correlation";
	//	public static final String TGT_HTML_TEMPLATE = "Html-Template";
	//	public static final String TGT_QUERY_PLAN = "QueryPlan";
	//	public static final String TGT_FILTER = "Filter";
	//	public static final String TGT_LAYOUT = "Layout";
	//	public static final String TGT_CUSTOM_MENU_ITEM = "Custom-Menu-Item";
	//	public static final String TGT_EVENT = "Timed-Event";
	//	public static final String TGT_TABLE = "Table";
	//	public static final String TGT_TAB = "Tab";
	//	public static final String TGT_SESSION = "Session";

	private long now;
	final private byte type;

	final private String message;
	final private Map<Object, Object> details;
	final public Throwable exception;
	private long seqnum;
	private byte severity;
	private final String targetAri;
	private final String targetCallback;

	public AmiDebugMessage(byte severity, byte type, String ari, String callback, String message, Map<Object, Object> details, Throwable exception) {

		this.severity = severity;
		this.type = type;
		this.targetAri = ari;
		this.targetCallback = callback;

		if (message == null && exception != null) {
			if (exception instanceof Legible) {
				this.message = ((Legible) exception).toLegibleString();
			} else
				this.message = SH.ddd(exception.getMessage(), 100);
		} else
			this.message = message;
		if (details != null)
			for (Entry<Object, Object> e : details.entrySet())
				if (e.getValue() != null)
					e.setValue(SH.ddd(SH.s(e.getValue()), 1024 * 100));
		this.details = details;
		this.exception = exception;
	}

	public void init(long now, long seqnum) {
		this.now = now;
		this.seqnum = seqnum;
	}

	public long getTime() {
		return now;
	}
	public long getSeqNum() {
		return seqnum;
	}
	public String getMessage() {
		return message;
	}

	public Throwable getException() {
		return exception;
	}

	public byte getType() {
		return type;
	}
	public Map<Object, Object> getDetails() {
		return this.details == null ? Collections.EMPTY_MAP : this.details;
	}

	public byte getSeverity() {
		return this.severity;
	}

	public static String getTypeAsString(byte type) {
		switch (type) {
			case AmiDebugMessage.TYPE_EVENT_SCHEDULED:
				return "Event Scheduled";
			case AmiDebugMessage.TYPE_EVENT_EXECUTED:
				return "Event Executed";
			case AmiDebugMessage.TYPE_LAYOUT:
				return "Loading Layout";
			case AmiDebugMessage.TYPE_LOG:
				return "Log";
			case AmiDebugMessage.TYPE_DATASOURCE_QUERY:
				return "Datasource Query";
			case AmiDebugMessage.TYPE_DATASOURCE_PREVIEW:
				return "Datasource Preview";
			case AmiDebugMessage.TYPE_DATASOURCE_SHOW_TABLES:
				return "Datasource Show Tables";
			case AmiDebugMessage.TYPE_DATASOURCE_UPLOAD:
				return "Datasource Upload";
			case AmiDebugMessage.TYPE_DATASOURCE_RESPONSE:
				return "Datasource Response";
			case AmiDebugMessage.TYPE_AMISQL:
				return "AMI Sql";
			case AmiDebugMessage.TYPE_SCHEMA_MISMATCH:
				return "Schema Mismatch";
			case AmiDebugMessage.TYPE_CUSTOM_METHODS:
				return "Custom Methods";
			case AmiDebugMessage.TYPE_CALLBACK:
				return "Callback";
			case AmiDebugMessage.TYPE_FORMULA:
				return "Formula";
			case AmiDebugMessage.TYPE_CMD_RESPONSE:
				return "Command Response";
			case AmiDebugMessage.TYPE_ADMIN_CONSOLE:
				return "Admin Console";
			case AmiDebugMessage.TYPE_TEST:
				return "Test";
			case AmiDebugMessage.TYPE_VARIABLE:
				return "Variable";
			case AmiDebugMessage.TYPE_QUERY_PLAN:
				return "Query Plan";
			case AmiDebugMessage.TYPE_METHOD:
				return "Method";
			default:
				return SH.toString(type);
		}
	}

	public String getTargetAri() {
		return targetAri;
	}

	public String getTargetCallback() {
		return targetCallback;
	}

}
