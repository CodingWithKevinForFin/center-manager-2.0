package com.vortex.web.portlet.tables;

import com.f1.base.Row;
import com.f1.suite.web.portal.BasicPortletDownload;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletDownload;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.eye.VortexEyeJournalReport;
import com.vortex.client.VortexClientJournalReportListener;
import com.vortex.web.VortexWebEyeService;

public class VortexEyeJournalReportTablePortlet extends VortexWebTablePortlet implements VortexClientJournalReportListener {

	private VortexWebEyeService service;

	public VortexEyeJournalReportTablePortlet(PortletConfig config) {
		super(config, null);
		String[] ids = { "year", "month", "agents", "users", "text" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Audit Rules");
		SmartTable st = new BasicSmartTable(inner);
		MapWebCellFormatter<Integer> stateFormatter = new MapWebCellFormatter<Integer>(getManager().getTextFormatter());
		stateFormatter.addEntry(1, "January");
		stateFormatter.addEntry(2, "February");
		stateFormatter.addEntry(3, "March");
		stateFormatter.addEntry(4, "April");
		stateFormatter.addEntry(5, "May");
		stateFormatter.addEntry(6, "June");
		stateFormatter.addEntry(7, "July");
		stateFormatter.addEntry(8, "August");
		stateFormatter.addEntry(9, "September");
		stateFormatter.addEntry(10, "October");
		stateFormatter.addEntry(11, "November");
		stateFormatter.addEntry(12, "December");
		stateFormatter.setDefaultWidth(70).lockFormatter();
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Year", "year", service.getBasicFormatter()).setWidth(50);
		table.addColumn(true, "Month", "month", stateFormatter).setWidth(80);
		table.addColumn(true, "Text (click and email contents to info@3forge.com)", "text", service.getBasicFormatter()).setWidth(700)
				.setCssColumn("bold blue fixedfont clickable").setIsClickable(true);
		table.addColumn(true, "Max Agents", "agents", service.getBasicFormatter()).setWidth(75);
		table.addColumn(true, "Max Users", "users", service.getBasicFormatter()).setWidth(75);
		agentManager.addJournalReportListener(this);
		setTable(table);
		for (VortexEyeJournalReport rule : service.getAgentManager().getJournalReports())
			onJournalReport(rule);
		getTable().sortRows("year", true, true, false);
		getTable().sortRows("month", true, true, true);
	}
	@Override
	public void close() {
		super.close();
		agentManager.removeJournalReportListener(this);
	}
	@Override
	public void onJournalReport(VortexEyeJournalReport report) {
		addRow(report.getYear(), report.getMonth(), report.getMaxAgents(), report.getMaxAccounts(), report.getText());
	}

	@Override
	public void onEyeDisconnected() {
		getTable().clear();
	}

	public static class Builder extends AbstractPortletBuilder<VortexEyeJournalReportTablePortlet> {

		public static final String ID = "JournalReport";

		public Builder() {
			super(VortexEyeJournalReportTablePortlet.class);
		}

		@Override
		public VortexEyeJournalReportTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexEyeJournalReportTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "F1 Eye Journal Report Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		String text = row.get("text", String.class);
		int year = row.get("year", Integer.class);
		int month = row.get("month", Integer.class);
		byte[] data = ("# Please copy, paste and email the text below to info@3forge.com without modifying the contents.\r\n\r\n" + text).getBytes();
		PortletDownload download = new BasicPortletDownload("Vortex_Usage_" + (year * 100 + month) + ".txt", data);
		getManager().pushPendingDownload(download);
	}
	@Override
	public void onEyeSnapshotProcessed() {
		// TODO Auto-generated method stub

	}
}
