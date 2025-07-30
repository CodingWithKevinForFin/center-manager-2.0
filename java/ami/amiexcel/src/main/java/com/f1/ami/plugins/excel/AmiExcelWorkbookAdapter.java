package com.f1.ami.plugins.excel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiExcelWorkbookAdapter {

	private ContainerTools tools;
	private Workbook workbook;
	private File rootFile;
	private String relativeFile;

	public AmiExcelWorkbookAdapter(ContainerTools tools, File rootFile, String relativeFile, Workbook workbook) {
		this.tools = tools;
		this.rootFile = rootFile;
		this.relativeFile = relativeFile;
		this.workbook = workbook;
	}
	public List<String> getSheets() {
		List<String> sheets = new ArrayList<String>();
		int nSheet = workbook.getNumberOfSheets();

		for (int i = 0; i < nSheet; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			sheets.add(sheet.getSheetName());
		}
		return sheets;
	}
	@Deprecated
	public List<AmiDatasourceTable> getSchema() throws AmiDatasourceException {
		int nSheet = workbook.getNumberOfSheets();

		List<AmiDatasourceTable> sink = new ArrayList<AmiDatasourceTable>();
		Set<String> names = new HashSet<String>();
		for (int i = 0; i < nSheet; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			AmiExcelSheetAdapter amiSheet = new AmiExcelSheetAdapter(tools, rootFile, relativeFile, sheet);
			AmiDatasourceTable table = amiSheet.getSchema(names);

			sink.add(table);
		}
		return sink;
	}
	public void getPreviewDataForSheets(List<AmiDatasourceTable> requestedTables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController timeout)
			throws AmiDatasourceException {
		int nSheet = requestedTables.size();
		Set<String> names = new HashSet<String>();

		BasicTable queryTable = new BasicTable();
		queryTable.addColumn(String.class, "select");
		queryTable.addColumn(String.class, "name");

		for (int i = 0; i < nSheet; i++) {
			AmiDatasourceTable table = requestedTables.get(i);
			String sheetName = table.getName();
			Sheet sheet = workbook.getSheet(sheetName);
			AmiExcelSheetAdapter amiSheet = new AmiExcelSheetAdapter(tools, rootFile, relativeFile, sheet);
			amiSheet.getSchema(table, names);
			queryTable.getRows().addRow(table.getCustomQuery(), SH.toString(i));
		}

		// Set up request for preview
		Map<String, Table> request = new HashMap<String, Table>();
		BasicTable wheresTable = new BasicTable();
		wheresTable.addColumn(String.class, "var");
		wheresTable.addColumn(String.class, "wheres");

		request.put("WHERES", new BasicTable());
		request.put("QUERY", queryTable);
		request.put("FIELDS", new BasicTable());

		// Put preview results into their respective tables
		for (int i = 0; i < nSheet; i++) {
			Table previewData = this.processQuery(request, i, previewCount, debugSink, timeout);
			requestedTables.get(i).setPreviewData(previewData);
		}
	}
	public Table processQuery(Map<String, Table> request, int requestIndex, int limit, AmiDatasourceTracker debugSink, TimeoutController timeout) throws AmiDatasourceException {
		Table queryTable = request.get("QUERY");

		// Get the sheet name
		String select = (String) queryTable.get(requestIndex, "select");
		String sheetName = SH.afterFirst(select, " FROM ");
		sheetName = SH.beforeFirst(sheetName, " WHERE ");
		sheetName = SH.afterLast(sheetName, ":");
		sheetName = SH.trim(sheetName);
		StringBuilder sb = new StringBuilder();
		SH.unescape(sheetName, 0, sheetName.length(), '\\', sb);
		sheetName = sb.toString();

		// Create a AmiExcelSheetAdapter 
		Sheet sheet = workbook.getSheet(sheetName);
		AmiExcelSheetAdapter excel = new AmiExcelSheetAdapter(tools, rootFile, relativeFile, sheet);

		Table table = excel.processQuery(request, requestIndex, limit, debugSink, timeout);
		return table;
	}
	public Table processQuery(AmiCenterQuery amiQuery, AmiDatasourceTracker debugSink, TimeoutController timeout) throws AmiDatasourceException {
		// Get the sheet name
		String select = amiQuery.getQuery();
		String sheetName = SH.afterFirst(select, " FROM ");
		sheetName = SH.beforeFirst(sheetName, " WHERE ");
		sheetName = SH.afterLast(sheetName, ":");
		sheetName = SH.trim(sheetName);
		StringBuilder sb = new StringBuilder();
		SH.unescape(sheetName, 0, sheetName.length(), '\\', sb);
		sheetName = sb.toString();

		// Create a AmiExcelSheetAdapter 
		Sheet sheet = workbook.getSheet(sheetName);
		AmiExcelSheetAdapter excel = new AmiExcelSheetAdapter(tools, rootFile, relativeFile, sheet);

		Table table = excel.processQuery(sheetName, amiQuery, debugSink, timeout);
		return table;
	}
}
