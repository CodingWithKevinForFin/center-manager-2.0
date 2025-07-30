package com.f1.ami.plugins.chroniclequeue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.center.ds.AmiDatasourceUtils;
import com.f1.base.Column;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.table.BasicColumn;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.online.MapBackedRowIterable;
import com.f1.utils.structs.table.online.OnlineTable;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

import net.openhft.chronicle.queue.ChronicleQueue;

public class AmiChronicleQueueDatasourceAdapter implements AmiDatasourceAdapter {
	protected static final Logger log = LH.get(AmiChronicleQueueDatasourceAdapter.class);
	private AmiServiceLocator serviceLocator;
	private ContainerTools tools;

	private Map<String, String> options = new HashMap<String, String>();
	private String url;
	private File rootFile;
	private Thread thread;
	private ChronicleQueue currentQueue;

	@Override
	public void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException {
		this.tools = tools;
		this.serviceLocator = serviceLocator;
		if (serviceLocator.getOptions() != null)
			SH.splitToMap(options, ',', '=', '\\', serviceLocator.getOptions());
		this.url = serviceLocator.getUrl();
		this.rootFile = new File(url);
		if (!rootFile.exists()) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "rootFile not found on " + EH.getLocalHost() + " ==> '" + IOH.getFullPath(rootFile) + "'");
		}
		if (!rootFile.canRead())
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED,
					"Can not access rootFile " + EH.getLocalHost() + " ==> '" + IOH.getFullPath(rootFile) + "' as user " + EH.getUserName());

	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		List<AmiDatasourceTable> sink = new ArrayList<AmiDatasourceTable>();
		AmiChronicleQueueHelper.findTables(this.rootFile, sink, AmiChronicleQueueHelper.DEFAULT_TABLE_PREFIX, this.tools);
		return sink;
	}

	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		for (int i = 0; i < tables.size(); i++) {
			AmiDatasourceTable table = tables.get(i);
			String fileName;
			if (table.getCollectionName() == AmiChronicleQueueHelper.DEFAULT_TABLE_PREFIX)
				fileName = SH.join(File.separator, this.rootFile.getParent(), table.getName());
			else
				fileName = SH.join(File.separator, this.rootFile.getParent(), table.getCollectionName(), table.getName());

			File file = new File(fileName);

			AmiChronicleQueueHelper.getTableSchema(file, table, previewCount, this, tools, debugSink);
			AmiCenterQuery q = AmiChronicleQueueHelper.getPreviewQuery(this.rootFile, file, table, previewCount, this.tools);
			List<Table> previewData = AmiUtils.processQuery(tools, this, q, debugSink, tc);
			previewData.get(0).setTitle(table.getCollectionName() + File.separator + table.getName());
			table.setPreviewData(previewData.get(0));
		}

		return tables;
	}

	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}

	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		this.thread = Thread.currentThread();

		List<Table> r = new ArrayList<Table>();
		Map<String, Object> d = query.getDirectives();
		String fileName = AmiDatasourceUtils.getOptional(d, "file");

		if (SH.isnt(fileName))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "The _file directive is required");

		File input = new File(this.rootFile.getParent() + File.separator + fileName);
		r.add(processTable(query.getQuery(), query.getLimit(), "file", streamToTable(d, input), debugSink, tc));

		this.thread = null;

		resultSink.setTables(r);

	}
	public static Table processTable(String query, int limit, String sourceTableName, OnlineTable table, AmiDatasourceTracker debugSink, TimeoutController tc) {
		try {
			SqlProcessor processor = new SqlProcessor();
			Tableset tablesMap = new TablesetImpl();
			tablesMap.putTable(sourceTableName, table);
			if (limit != -1)
				query += " LIMIT " + limit;
			Table rTable = (Table) processor.process(query, new TopCalcFrameStack(tablesMap, limit, tc, debugSink, AmiUtils.METHOD_FACTORY, EmptyCalcFrame.INSTANCE));
			return rTable;
		} finally {
			IOH.close(table);
		}
	}
	public static OnlineTable streamToTable(Map<String, Object> d, File file) throws AmiDatasourceException {
		Map<String, String> mapping = null;
		Map<String, Column> columns = new LinkedHashMap<String, Column>();

		//linenum
		/*
		String linenum = d.get("linenum");
		if (linenum == null)
			linenum = "linenum";
		if (SH.isnt(linenum))
			linenum = null;
		else {
			linenum = linenum.trim();
			if (!AmiUtils.isValidVariableName(linenum, false, false))
				throw new AmiDatasourceException(DIRECTIVE_ERROR, "_linenum directive defines invalid column name: '" + linenum + "'");
			if (!columns.containsKey(linenum)) {
				columns.put(linenum, new BasicColumn(Integer.class, linenum));
			}
		}
		*/
		//columns
		String columnsString = AmiDatasourceUtils.getOptional(d, "fields");
		if (SH.is(columnsString))
			parseColumns("_fields", columnsString, columns);
		else
			columnsString = null;

		//patterns
		/*
		String pattern = d.get("pattern");
		String[] patterns = SH.split('\n', pattern);
		if (patterns != null)
			for (String p : patterns)
				parseColumns("_pattern", SH.beforeFirst(p, '='), columns);
		
		String mappings = d.get("mappings");
		if (mappings != null) {
			try {
				mapping = new HashMap<String, String>();
				Map<String, String> mapping2 = SH.splitToMap(',', '=', '\\', mappings);
				for (Entry<String, String> e : mapping2.entrySet()) {
					String k = SH.trim(e.getKey());
					String v = SH.trim(e.getValue());
					if (columns.containsKey(k))
						throw new AmiDatasourceException(DIRECTIVE_ERROR, "error in _mappings, can not re-map predefined column: '" + k + "'");
					if (!columns.containsKey(v)) {
						if (!AmiUtils.isValidVariableName(v, false, false))
							throw new AmiDatasourceException(DIRECTIVE_ERROR, "error in _mappings, can not map '" + k + "' to invalid column: '" + v + "'");
						columns.put(v, new BasicColumn(String.class, v));
					}
					mapping.put(k, v);
				}
			} catch (Exception e) {
				throw new AmiDatasourceException(DIRECTIVE_ERROR, "syntax error in _mappings directive", e);
			}
		} else if (patterns.length == 0 && columnsString == null) {
			columns.put("line", new BasicColumn(String.class, "line"));
			patterns = new String[] { "line=(.*)" };
		}
		
		//others
		String filterIn = d.get("filterIn");
		String filterOut = d.get("filterOut");
		int skip = CH.getOr(Integer.class, d, "skipLines", 0);
		String delim = d.get("delim");
		if ("".equals(delim))
			delim = null;
		char quote = toChar(d, "quote");
		char escape = toChar(d, "escape");
		char equals = toChar(d, "equals");
		if (delim != null) {
			if (columnsString == null && CH.isEmpty(mapping))
				throw new AmiDatasourceException(DIRECTIVE_ERROR, "When _delim directive is supplied the _fields directive is also required.");
		} else {
			if (quote != AmiFileBackedRowIterable.NO_CHAR)
				throw new AmiDatasourceException(DIRECTIVE_ERROR, "The _quote directive is only supported when the _delim directive is supplied.");
			if (escape != AmiFileBackedRowIterable.NO_CHAR)
				throw new AmiDatasourceException(DIRECTIVE_ERROR, "The _escape directive is only supported when the _delim directive is supplied.");
			if (equals != AmiFileBackedRowIterable.NO_CHAR)
				throw new AmiDatasourceException(DIRECTIVE_ERROR, "The _equals directive is only supported when the _delim directive is supplied.");
		}
		boolean conflateDelims;
		String conflateDelimsText = d.get("conflateDelim");
		if (conflateDelimsText == null || "false".equals(conflateDelimsText))
			conflateDelims = false;
		else if ("true".equals(conflateDelimsText))
			conflateDelims = true;
		else
			throw new AmiDatasourceException(DIRECTIVE_ERROR, "The _conflateDelim directive must be true of false");
		 */
		String delim = AmiDatasourceUtils.getOptional(d, "delim");
		String index = AmiDatasourceUtils.getOptional(d, "index");
		OnlineTable table = new OnlineTable(CH.l(columns.values()));
		table.init(new MapBackedRowIterable(table, new AmiChronicleQueueIterator(file, table, delim, index)));
		return table;

	}

	static private void parseColumns(String directiveName, String columnsString, Map<String, Column> columns) throws AmiDatasourceException {
		String[] parts = SH.trimArray(SH.split(',', columnsString));
		Column[] r = new Column[parts.length];
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			String type = SH.beforeFirst(part, ' ', "String").trim();
			String name = SH.afterFirst(part, ' ', part).trim();
			if (!AmiUtils.isValidVariableName(name, false, false))
				throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, directiveName + " directive defines invalid column name: '" + name + "'");
			byte typeClass = AmiUtils.parseTypeName(type);
			Column column = columns.get(name);
			Class<?> classForValueType = AmiUtils.getClassForValueType(typeClass);
			if (column == null) {
				columns.put(name, new BasicColumn(classForValueType, name));
			} else if (column.getType() != classForValueType)
				throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, directiveName + " has inconsistent type '" + type + "' for column: '" + name + "'");
		}
	}

	@Override
	public boolean cancelQuery() {
		Thread t = this.thread;
		if (t != null) {
			t.interrupt();
			return true;
		}
		return false;
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult resultsSink, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload to datasource");
	}

}
