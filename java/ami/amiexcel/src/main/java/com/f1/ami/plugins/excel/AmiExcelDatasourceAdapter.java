package com.f1.ami.plugins.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiExcelDatasourceAdapter implements AmiDatasourceAdapter {
	private static final Logger log = LH.get();

	public static Map<String, String> buildOptions() {
		Map<String, String> r = new HashMap<String, String>();
		return r;
	}

	private ContainerTools tools;
	private AmiServiceLocator locator;
	private String url;
	private String name;
	private File rootFile;

	@Override
	public void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException {
		this.tools = tools;
		this.locator = serviceLocator;
		this.url = SH.replaceAll(serviceLocator.getUrl(), '\\', '/');
		this.name = locator.getTargetName();
		this.rootFile = new File(url);

		if (!rootFile.exists()) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "rootFile not found on " + EH.getLocalHost() + " ==> '" + IOH.getFullPath(rootFile) + "'");
		}
		if (!rootFile.canRead())
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED,
					"Can not access rootFile " + EH.getLocalHost() + " ==> '" + IOH.getFullPath(rootFile) + "' as user " + EH.getUserName());
	}

	public String getName() {
		return name;
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController timeout) throws AmiDatasourceException {
		List<AmiDatasourceTable> sink = new ArrayList<AmiDatasourceTable>();
		findExcelSheets(this.rootFile, sink, 100);
		return sink;
	}

	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController timeout)
			throws AmiDatasourceException {
		//Marshall the data so we use less file pointers
		Map<String, List<AmiDatasourceTable>> fileToSheetsMap = new HashMap<String, List<AmiDatasourceTable>>();
		for (int i = 0; i < tables.size(); i++) {
			AmiDatasourceTable table = tables.get(i);
			String relativePath = table.getCollectionName();
			if (!fileToSheetsMap.containsKey(relativePath)) {
				fileToSheetsMap.put(relativePath, new ArrayList<AmiDatasourceTable>());
			}
			fileToSheetsMap.get(relativePath).add(table);
		}

		String basePath = rootFile.getPath();
		for (String relPath : fileToSheetsMap.keySet()) {
			List<AmiDatasourceTable> requestedTables = fileToSheetsMap.get(relPath);
			String path = SH.replaceAll(basePath, '\\', '/') + "/" + relPath;
			File f = SH.equals(relPath, "") ? rootFile : new File(path);

			Workbook workbook = null;
			try {
				workbook = WorkbookFactory.create(f);
				AmiExcelWorkbookAdapter amiWorkbook = new AmiExcelWorkbookAdapter(tools, f, relPath, workbook);
				amiWorkbook.getPreviewDataForSheets(requestedTables, previewCount, debugSink, timeout);
			} catch (NoSuchFieldError e) {
				LH.warning(log, "Error when getting the preview: ", e);
				throw new AmiDatasourceException(AmiDatasourceException.INITIALIZATION_FAILED, e.getMessage() + ", check to make sure no duplicate libaries of apachi poi");
			} catch (EncryptedDocumentException e) {
				LH.warning(log, "Encrypted Document: ", e);
			} catch (Throwable e) {
				LH.warning(log, "Error when getting the preview: ", e);
				throw new AmiDatasourceException(AmiDatasourceException.INITIALIZATION_FAILED, e.getMessage());
			} finally {
				if (workbook != null)
					try {
						workbook.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		return tables;
	}
	@Override
	public void processQuery(AmiCenterQuery amiQuery, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		List<Table> results = new ArrayList<Table>();
		Workbook wb = null;
		try {

			// Get the file name
			String select = amiQuery.getQuery();
			select = SH.afterFirst(select, " FROM ");
			select = SH.beforeFirst(select, " WHERE");
			select = SH.beforeLast(select, ":");
			select = SH.trim(select);

			// Create an AmiExcelWorkBookAdapter

			String dir = this.locator.getUrl() + "/" + select;
			File file = new File(dir);
			wb = WorkbookFactory.create(file, null, true);
			AmiExcelWorkbookAdapter amiWorkbook = new AmiExcelWorkbookAdapter(tools, file, select, wb);

			// Process the query and set the names for the result tables.
			Table table = amiWorkbook.processQuery(amiQuery, debugSink, tc);
			results.add(table);
		} catch (NoSuchFieldError e) {
			LH.warning(log, "Error when trying to process query: ", e);
			throw new AmiDatasourceException(AmiDatasourceException.INITIALIZATION_FAILED, e.getMessage() + ", check to make sure no duplicate libaries of apachi poi");
		} catch (EncryptedDocumentException e) {
			LH.warning(log, "Encrypted Document: ", e);
		} catch (Throwable e) {
			LH.warning(log, "Error when trying to process query: ", e);
			throw new AmiDatasourceException(AmiDatasourceException.INITIALIZATION_FAILED, e.getMessage(), e);
		} finally {
			if (wb != null)
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}

		resultSink.setTables(results);
	}
	@Override
	public boolean cancelQuery() {
		return false;
	}

	/**
	 * Checks if the file has a xls or an xlsx extension
	 * 
	 * @param file
	 * @return
	 */
	private boolean isMSExcelFileExt(File file) {
		String name = file.getName();
		return file.isFile() && (SH.endsWith(name, ".xls") || SH.endsWith(name, ".xlsx"));
	}

	public void findFilez(File file, String prefix, List<String> sink, int max) {
		if (sink.size() == max)
			return;

		String fn;
		if (SH.equals(prefix, "."))
			fn = "";
		else if (SH.equals(prefix, ""))
			fn = file.getName();
		else
			fn = prefix + "/" + file.getName();

		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			if (listFiles != null)
				for (File f : listFiles) {
					findFilez(f, fn, sink, max);
				}
		} else if (isMSExcelFileExt(file)) {
			if (!SH.startsWith(fn, "~"))
				sink.add(fn);
		}
	}
	public void findExcelSheets(File file, List<AmiDatasourceTable> sink, int max) throws AmiDatasourceException {
		List<String> excelPaths = new ArrayList<String>();
		findFilez(this.rootFile, ".", excelPaths, max);

		String basePath = file.getPath();
		File f = null;
		for (int i = 0; i < excelPaths.size(); i++) {
			String relPath = excelPaths.get(i);
			String path = SH.replaceAll(basePath, '\\', '/') + "/" + relPath;
			f = SH.equals(relPath, "") ? rootFile : new File(path);
			Workbook workbook = null;

			boolean hadError = false;
			try {
				workbook = WorkbookFactory.create(f, null, true);
				AmiExcelWorkbookAdapter amiWorkbook = new AmiExcelWorkbookAdapter(tools, f, relPath, workbook);
				List<String> sheetNames = amiWorkbook.getSheets();
				for (int j = 0; j < sheetNames.size(); j++) {
					final AmiDatasourceTable table = this.tools.nw(AmiDatasourceTable.class);
					table.setName(sheetNames.get(j));
					table.setCollectionName(relPath);
					sink.add(table);
				}
			} catch (NoSuchFieldError e) {
				LH.warning(log, "Error initialization failed: ", e);
				hadError = true;
				throw new AmiDatasourceException(AmiDatasourceException.INITIALIZATION_FAILED, e.getMessage() + ", check to make sure no duplicate libaries of apachi poi");
			} catch (EncryptedDocumentException e) {
				LH.warning(log, "Encrypt Issue with work book: ", IOH.getFullPath(file), e);
			} catch (Throwable e) {
				hadError = true;
				LH.warning(log, "Error With File: ", IOH.getFullPath(file), e.getMessage());
				//				throw new AmiDatasourceException(AmiDatasourceException.INITIALIZATION_FAILED, e.getMessage());
			} finally {
				if (workbook != null)
					try {
						workbook.close();
					} catch (Throwable e) {
						if (!hadError)
							LH.warning(log, "Issue closing work book: ", IOH.getFullPath(file), e);
					}

			}
		}

	}

	@Deprecated
	public List<File> findFiles() throws AmiDatasourceException {
		List<File> sink = new ArrayList<File>();
		findFiles(this.rootFile, ".", sink, 100);
		return sink;
	}
	@Deprecated
	private void findFiles(File file, String prefix, List<File> sink, int max) {
		if (sink.size() == max)
			return;
		String fn = prefix.length() == 0 ? file.getName() : (prefix + "/" + file.getName());

		if (file.isDirectory()) {
			int c = 0;
			for (File f : file.listFiles()) {
				if (f.isFile()) {
					if (c++ >= 10)
						continue;
				}
				findFiles(f, fn, sink, max);
			}
		} else if (isMSExcelFileExt(file))
			sink.add(file);
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult results, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload to datasource");
	}

	@Override
	public AmiServiceLocator getServiceLocator() {
		return locator;
	}
}
