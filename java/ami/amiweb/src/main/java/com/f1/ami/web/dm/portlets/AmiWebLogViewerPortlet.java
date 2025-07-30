package com.f1.ami.web.dm.portlets;

import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.amiscript.AmiDebugMessageListener;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.text.SimpleFastTextPortlet;
import com.f1.utils.SH;

public class AmiWebLogViewerPortlet extends GridPortlet implements AmiDebugMessageListener {
	public static final byte TYPE_ADMIN = 0;
	public static final byte TYPE_USER_LOG = 1;
	public static final byte TYPE_USER_WARN = 2;
	public static final byte TYPE_RETURN = 3;
	public static final byte TYPE_EXCEPTION = 4;
	public static final byte TYPE_EXCECUTE = 5;
	public static final byte TYPE_INPUT = 6;
	public static final byte TYPE_UPLOAD = 7;
	public static final byte TYPE_SHOW_TABLES = 8;

	private SimpleFastTextPortlet resultSql;
	private AmiWebService service;
	private StringBuilder buf = new StringBuilder();

	public AmiWebLogViewerPortlet(PortletConfig config) {
		super(config);
		this.service = AmiWebUtils.getService(getManager());
		this.resultSql = new SimpleFastTextPortlet(generateConfig());
		this.addChild(resultSql);
		this.resultSql.setStyle("_bg=#fafafa|_fg=#000000");
		this.resultSql.setLabelStyle("_bg=#dddddd|_fg=#666666|_fm=bold");
	}

	public void clear() {
		this.resultSql.clearLines();
	}

	public int getLogsCount() {
		return this.resultSql.getNumberOfLines(this.resultSql);
	}

	@Override
	public void onAmiDebugMessage(AmiDebugManager manager, AmiDebugMessage message) {
		if (message.getType() == AmiDebugMessage.TYPE_DATASOURCE_QUERY) {
			Object query = message.getDetails().get("Query");
			log(TYPE_EXCECUTE, (String) query);
		} else if (message.getType() == AmiDebugMessage.TYPE_DATASOURCE_UPLOAD) {
			Object query = message.getDetails().get("Query");
			log(TYPE_UPLOAD, (String) query);
		} else if (message.getType() == AmiDebugMessage.TYPE_DATASOURCE_SHOW_TABLES) {
			Object query = message.getDetails().get("Query");
			log(TYPE_SHOW_TABLES, (String) query);
		} else if (message.getType() == AmiDebugMessage.TYPE_LOG) {
			String time = service.getFormatterManager().getTimeMillisWebCellFormatter().formatCellToText(getManager().getNow());
			String msg = message.getMessage();
			log(message.getSeverity() == message.SEVERITY_WARNING ? TYPE_USER_WARN : TYPE_USER_LOG, msg);
		}
	}

	@Override
	public void onAmiDebugMessagesRemoved(AmiDebugManager manager, AmiDebugMessage message) {
	}

	public void log(byte type, String message) {
		buf.setLength(0);
		service.getFormatterManager().getTimeMillisWebCellFormatter().formatCellToText(getManager().getNow(), buf);
		final String style;
		switch (type) {
			case TYPE_ADMIN:
				buf.append("   SYSTEM");
				style = "_fg=#008800";
				break;
			case TYPE_USER_LOG:
				buf.append(" USER LOG");
				style = "_fg=#000000";
				break;
			case TYPE_USER_WARN:
				buf.append(" USER WRN");
				style = "_fg=#880000";
				break;
			case TYPE_RETURN:
				buf.append("   RETURN");
				style = "_fg=#000088";
				break;
			case TYPE_EXCEPTION:
				buf.append(" UNCAUGHT");
				style = "_fg=#FFFFFF|_bg=#FF8C00";
				break;
			case TYPE_EXCECUTE:
				buf.append("  EXECUTE");
				style = "_fg=#006600";
				break;
			case TYPE_UPLOAD:
				buf.append("   UPLOAD");
				style = "_fg=#006600";
				break;
			case TYPE_SHOW_TABLES:
				buf.append("     SHOW");
				style = "_fg=#006600";
				break;
			case TYPE_INPUT:
				buf.append("    INPUT");
				style = "_fg=#666666";
				break;
			default:
				style = "";
		}
		String time = buf.toString();
		if (message == null) {
			this.resultSql.appendLine(time, "<null>", style);
		} else {
			for (String part : SH.splitLines((String) message)) {
				this.resultSql.appendLine(time, (String) part, style);
				time = "";
			}
		}
	}
}
