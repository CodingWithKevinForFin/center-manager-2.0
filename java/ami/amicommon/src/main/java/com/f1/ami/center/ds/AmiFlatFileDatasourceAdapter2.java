package com.f1.ami.center.ds;

import static com.f1.ami.amicommon.AmiDatasourceException.DIRECTIVE_ERROR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiCenterUploadTable;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Bytes;
import com.f1.base.Column;
import com.f1.base.Generator;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.FastPrintStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.table.BasicColumn;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.online.OnlineTable;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiFlatFileDatasourceAdapter2 implements AmiDatasourceAdapter {
	private static final String OPTION_WRITE_ENABLED = "WRITE_ENABLED";

	public static class FileReaderGenerator implements Generator<Reader> {
		private File file;

		public FileReaderGenerator(File file) {
			this.file = file;
		}
		@Override
		public Reader nw() {
			try {
				return new FileReader(file);
			} catch (Exception e) {
				throw OH.toRuntime(e);
			}
		}
	}

	protected static final Logger log = LH.get();

	public static Map<String, String> buildOptions() {
		Map<String, String> r = new HashMap<String, String>();
		r.put(OPTION_WRITE_ENABLED, "If set to true, then upload functionality is allowed");
		return r;
	}

	private AmiServiceLocator serviceLocator;
	private ContainerTools tools;
	Map<String, String> options = new HashMap<String, String>();
	private Thread thread;
	private String url;
	private File rootFile;

	@Override
	public void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException {
		this.tools = tools;
		this.serviceLocator = serviceLocator;
		if (serviceLocator.getOptions() != null)
			SH.splitToMap(options, ',', '=', '\\', serviceLocator.getOptions());
		this.url = SH.replaceAll(serviceLocator.getUrl(), '\\', '/');
		this.tools = tools;
		this.rootFile = new File(url);
		if (!rootFile.exists()) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Directory not found on " + EH.getLocalHost() + " ==> '" + IOH.getFullPath(rootFile) + "'");
		}
		if (!rootFile.canRead())
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED,
					"Can not access Directory " + EH.getLocalHost() + " ==> '" + IOH.getFullPath(rootFile) + "' as user " + EH.getUserName());
		if (!rootFile.isDirectory())
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED,
					"URL must be a Directory " + EH.getLocalHost() + " ==> '" + IOH.getFullPath(rootFile) + "' as user " + EH.getUserName());
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController timeout) throws AmiDatasourceException {
		List<AmiDatasourceTable> sink = new ArrayList<AmiDatasourceTable>();
		if (this.rootFile.isFile())
			sink.add(toAmiDatasourceTable(this.rootFile, "", rootFile.getName()));
		else {
			sink.add(toAmiDatasourceTable(this.rootFile, "", "."));
			for (File f : rootFile.listFiles())
				findFilez(f, "", sink, 100);
		}
		return sink;
	}
	private void findFilez(File file, String prefix, List<AmiDatasourceTable> sink, int max) {
		String fn = prefix.length() == 0 ? '/' + file.getName() : (prefix + '/' + file.getName());
		sink.add(toAmiDatasourceTable(file, prefix, file.getName()));

		if (file.isDirectory()) {
			int c = 0;
			File[] listFiles = file.listFiles();
			if (listFiles != null) {
				for (File f : listFiles) {
					if (f.isFile()) {
						if (c++ >= 10)
							continue;
					}
					findFilez(f, fn, sink, max);
					if (sink.size() == max)
						return;
				}
			}
		}
	}
	private AmiDatasourceTable toAmiDatasourceTable(File file, String prefix, String fileName) {
		AmiDatasourceTable table = this.tools.nw(AmiDatasourceTable.class);
		table.setName(file.isDirectory() ? fileName + "/" : fileName);
		table.setCollectionName(prefix + '/');
		return table;
	}

	public String getName() {
		return this.serviceLocator.getTargetName();
	}

	@Override
	public void processQuery(AmiCenterQuery i, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		this.thread = Thread.currentThread();
		List<Table> r = new ArrayList<Table>();
		Map<String, Object> d = i.getDirectives();

		//file
		String fileName = AmiDatasourceUtils.getOptional(d, "file");
		if (SH.isnt(fileName))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "The _file directive is required");
		File input = new File(this.rootFile, fileName);
		boolean returnBinary = AmiDatasourceUtils.getOptional(Caster_Boolean.INSTANCE, d, "binary", false);
		try {
			if (input.isDirectory()) {
				Table tableDir = directoryToTable(input);
				r.add(tableDir);
			} else if (returnBinary) {
				Bytes b = Bytes.valueOf(IOH.readData(input));
				ColumnarTable table = new ColumnarTable();
				table.addColumn(Bytes.class, "data");
				ColumnarRow row = table.newEmptyRow();
				row.put("data", b);
				table.getRows().add(row);
				r.add(table);
			} else {
				Generator<Reader> reader = new FileReaderGenerator(input);
				r.add(AmiDatasourceUtils.processOnlineTable(i.getQuery(), i.getLimit(), "file", streamToTable(d, reader), debugSink, tc));
			}
		} catch (IOException e) {
			LH.log(log, Level.WARNING, "Problem reading the file", e);

		} finally {
			this.thread = null;
		}
		resultSink.setTables(r);
	}

	public static OnlineTable streamToTable(Map<String, Object> d, Generator<Reader> stream) throws AmiDatasourceException {
		Map<String, String> mapping = null;
		Map<String, Column> columns = new LinkedHashMap<String, Column>();

		//linenum
		String linenum = AmiDatasourceUtils.getOptional(d, "linenum", "linenum");
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
		//columns
		String columnsString = AmiDatasourceUtils.getOptional(d, "fields");
		if (SH.is(columnsString))
			parseColumns("_fields", columnsString, columns);
		else
			columnsString = null;

		//patterns
		String pattern = AmiDatasourceUtils.getOptional(String.class, d, "pattern");
		String[] patterns = SH.split('\n', pattern);
		if (patterns != null)
			for (String p : patterns)
				parseColumns("_pattern", SH.beforeFirst(p, '='), columns);

		String mappings = AmiDatasourceUtils.getOptional(d, "mappings");
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
		String filterIn = AmiDatasourceUtils.getOptional(d, "filterIn");
		String filterOut = AmiDatasourceUtils.getOptional(d, "filterOut");
		int skip = CH.getOr(Integer.class, d, "skipLines", 0);
		String delim = AmiDatasourceUtils.getOptional(d, "delim");
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
		String conflateDelimsText = AmiDatasourceUtils.getOptional(d, "conflateDelim");
		if (conflateDelimsText == null || "false".equals(conflateDelimsText))
			conflateDelims = false;
		else if ("true".equals(conflateDelimsText))
			conflateDelims = true;
		else
			throw new AmiDatasourceException(DIRECTIVE_ERROR, "The _conflateDelim directive must be true of false");

		OnlineTable table = new OnlineTable(CH.l(columns.values()));
		table.init(new AmiFileBackedRowIterable(table, stream, skip, filterIn, filterOut, delim, conflateDelims, quote, escape, equals, patterns, linenum, mapping));
		return table;
	}
	private Table directoryToTable(File directory) {
		File[] files = directory.listFiles();
		Table table = new ColumnarTable();
		table.setTitle(directory.getName());
		table.addColumn(long.class, "lastmodified");
		table.addColumn(long.class, "size");
		table.addColumn(String.class, "name");
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			table.getRows().addRow(f.lastModified(), f.length(), f.getName());
		}
		return table;
	}
	static private char toChar(Map<String, Object> d, String key) {
		String r = (String) d.get(key);
		if (r == null)
			return AmiFileBackedRowIterable.NO_CHAR;
		if (r.length() != 1)
			throw new RuntimeException("Expecting single char for _" + key);
		return r.charAt(0);
	}
	static private char toChar(Map<String, String> d, String key, char dflt) {
		String r = d.get(key);
		if (r == null)
			return dflt;
		if (r.length() == 0)
			return 0;
		if (r.length() != 1)
			throw new RuntimeException("Expecting single char for _" + key);
		return r.charAt(0);
	}
	static private boolean toBool(Map<String, String> d, String key, boolean dflt) {
		String r = d.get(key);
		if (r == null)
			return dflt;
		return Caster_Boolean.INSTANCE.cast(r);
	}

	static private void parseColumns(String directiveName, String columnsString, Map<String, Column> columns) throws AmiDatasourceException {
		String[] parts = SH.trimArray(SH.split(',', columnsString));
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			String type = SH.beforeFirst(part, ' ', "String").trim();
			String name = SH.afterFirst(part, ' ', part).trim();
			if (!AmiUtils.isValidVariableName(name, false, false))
				throw new AmiDatasourceException(DIRECTIVE_ERROR, directiveName + " directive defines invalid column name: '" + name + "'");
			byte typeClass = AmiUtils.parseTypeName(type);
			Column column = columns.get(name);
			Class<?> classForValueType = AmiUtils.getClassForValueType(typeClass);
			if (column == null) {
				columns.put(name, new BasicColumn(classForValueType, name));
			} else if (column.getType() != classForValueType)
				throw new AmiDatasourceException(DIRECTIVE_ERROR, directiveName + " has inconsistent type '" + type + "' for column: '" + name + "'");
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
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController timeout)
			throws AmiDatasourceException {
		String rootPath = rootFile.getPath();
		File f = null;

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < tables.size(); i++) {
			AmiDatasourceTable table = tables.get(i);
			String relativePath = table.getCollectionName() + table.getName();
			relativePath = SH.replaceAll(relativePath, '\\', '/');
			relativePath = relativePath.equals("/.") ? "" : relativePath;
			if (SH.startsWith(relativePath, '~'))
				if (!IOH.isSecureChildPath(relativePath))
					throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Security risk.. Rejecting attempt to access home directory: " + relativePath);
			String fullPath = IOH.join(rootPath, relativePath);
			f = new File(fullPath);

			//Get columns for each file
			List<AmiDatasourceColumn> cols = new ArrayList<AmiDatasourceColumn>();

			if (f.isDirectory()) {
				AmiDatasourceColumn col = tools.nw(AmiDatasourceColumn.class);
				col.setName("file_name");
				col.setType(AmiDatasourceColumn.TYPE_STRING);
				cols.add(col);
			}

			table.setColumns(cols);

			table.setCustomQuery("SELECT * FROM file WHERE ${WHERE}");
			sb.append("_file=").append(SH.doubleQuote(relativePath)).append(" _delim=\"\\n\"").append(" _fields=\"String line\"").append(" _binary=false");
			table.setCustomUse(sb.toString());
			SH.clear(sb);
			AmiCenterQuery q = tools.nw(AmiCenterQuery.class);
			q.setQuery("SELECT * FROM file");

			q.setLimit(previewCount);
			Map<String, Object> directives = new LinkedHashMap<String, Object>();
			directives.put("file", relativePath);
			directives.put("delim", "\n");
			directives.put("fields", "String line");
			directives.put("binary", "false"); // if true, turns a file to bytes and return that in a table
			if (f.isFile())
				try {
					AmiFlatFileGuesser guesser = AmiFlatFileGuesser.nw(f, previewCount);
					char delim = guesser.getDelim();
					char quote = guesser.getQuote();
					char escape = guesser.getEscape();
					char equals = guesser.getEquals();
					Map<String, String> mappings = guesser.getMappings();
					int skipLines = guesser.getLinesSkip();
					List<String> fields = guesser.getFields();
					for (int fi = 0; fi < fields.size(); fi++) {
						if (fi > 0)
							sb.append(", ");
						String name = fields.get(fi);
						String type = AmiUtils.toTypeName(guesser.getType(name));
						sb.append(type).append(' ').append(name);
					}
					String fieldsStr = fields.size() > 0 ? sb.toString() : "String line";
					SH.clear(sb);

					sb.append('_').append("file").append('=').append(SH.doubleQuote(relativePath)).append(' ');

					if (delim == ';')
						sb.append('_').append("delim").append('=').append("\"\\").append(SH.toStringEncode(delim, '"')).append('"').append(' ');
					else
						sb.append('_').append("delim").append('=').append('"').append(SH.toStringEncode(delim != 0 ? delim : '\n', '"')).append('"').append(' ');

					sb.append('_').append("quote").append('=').append(SH.doubleQuote(quote != 0 ? SH.toString(quote) : "\"")).append(' ');
					sb.append('_').append("escape").append('=').append(SH.doubleQuote(escape != 0 ? SH.toString(escape) : "\\")).append(' ');
					if (equals != 0)
						sb.append('_').append("equals").append('=').append(SH.doubleQuote(SH.toString(equals))).append(' ');
					sb.append('_').append("skipLines").append('=').append(SH.doubleQuote(SH.toString(skipLines))).append(' ');
					if (CH.isntEmpty(mappings))
						sb.append('_').append("mappings").append('=').append(SH.doubleQuote(SH.joinMap(',', '=', '\\', mappings))).append(' ');
					sb.append('_').append("fields").append('=').append(SH.doubleQuote(fieldsStr)).append(' ');
					sb.append('_').append("binary").append('=').append(false).append(' ');

					table.setCustomUse(sb.toString());
					SH.clear(sb);

				} catch (IOException e) {
					LH.log(log, Level.WARNING, "There was an error trying to guess how to read the flatfile", e);
				}
			q.setDirectives(directives);
			List<Table> previewData = AmiUtils.processQuery(tools, this, q, debugSink, timeout);
			table.setPreviewData(previewData.get(i));
		}

		return tables;
	}
	public static void main(String a[]) throws FileNotFoundException {
		OnlineTable table = new OnlineTable(Long.class, "value", Long.class, "qty", String.class, "name");
		table.setTitle("t");
		String delim = "|";
		char quote = '"';
		char escape = (char) -1;
		char associator = '=';
		String extractors = "value,qty,name=the value (.*) is okay for quantity ([0-9]+) of values (.*)";
		table.init(new AmiFileBackedRowIterable(table, new FileReaderGenerator(new File("/tmp/table.txt2")), 0, null, "asdf", delim, false, quote, escape, associator,
				new String[] { extractors }, null, null));
		SqlProcessor sp = new SqlProcessor();
		Tableset tablesMap = new TablesetImpl();
		tablesMap.putTable(table);
		long now = System.currentTimeMillis();
		Table t2 = sp.process("Select sum(value),sum(qty),min(value),max(value),count(*),name  from t where name==\"steve\" group by name",
				new TopCalcFrameStack(tablesMap, AmiUtils.METHOD_FACTORY, EmptyCalcFrame.INSTANCE));
		long now2 = System.currentTimeMillis();
		System.out.println(t2);
		System.out.println(now2 - now);
		table.close();
	}
	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult results, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		if (!Boolean.TRUE.equals(getOption(OPTION_WRITE_ENABLED, Boolean.FALSE)))
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Permission delied, set WRITE_ENABLED=true in Datasource Options");
		TableWriter fw = new TableWriter();
		for (AmiCenterUploadTable i : upload.getData()) {
			File file = new File(this.rootFile, i.getTargetTable());
			FastPrintStream out;
			try {
				IOH.ensureDir(file.getParentFile());
				out = new FastPrintStream(new FastBufferedOutputStream(new FileOutputStream(file)));
			} catch (Exception e) {
				throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, "Could not write to file: " + IOH.getFullPath(file), e);
			}
			try {
				fw.write((Table) i.getData(), i.getTargetColumns(), out);
			} catch (Exception e) {
				throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, "Could not fully write to file: " + IOH.getFullPath(file), e);
			}
		}
	}

	public static class TableWriter {
		char quote = '"';
		char delim = '|';
		String nll = "null";
		String newline = SH.NEWLINE;
		boolean includeHeader = true;
		boolean includeTypes = true;
		private final StringBuilder buf = new StringBuilder();
		boolean includeData = true;

		public void readDirectives(Map<String, String> d) {
			quote = toChar(d, "quote", quote);
			delim = toChar(d, "delim", delim);
			nll = CH.getOr(d, "null", nll);
			newline = CH.getOr(d, "newline", newline);
			includeHeader = toBool(d, "includeHeader", includeHeader);
			includeTypes = toBool(d, "includeTypes", includeTypes);
			includeData = toBool(d, "includeData", includeData);
		}

		public void write(Table t, List<String> cols2, FastPrintStream out) throws AmiDatasourceException, IOException {
			try {
				Column[] cols = AH.toArray(t.getColumns(), Column.class);
				if (includeHeader) {
					for (int n = 0; n < cols.length; n++) {
						if (n > 0)
							out.print(delim);
						if (cols2 != null && cols2.size() > n)
							out.print(cols2.get(n));
						else
							out.print(cols[n].getId());
					}
					out.print(newline);
				}
				if (includeTypes) {
					for (int n = 0; n < cols.length; n++) {
						if (n > 0)
							out.print(delim);
						out.print(cols[n].getType().getSimpleName());//TODO: be smarter
					}
					out.print(newline);
				}
				if (includeData) {
					for (Row row : t.getRows()) {
						for (int n = 0; n < cols.length; n++) {
							if (n > 0)
								out.print(delim);
							Object cell = row.getAt(n);
							if (cell == null)
								out.print(nll);
							else if (cell instanceof CharSequence) {
								CharSequence cs = (CharSequence) cell;
								buf.setLength(0);
								if (quote != 0) {
									SH.quote(quote, cs, buf);
									out.print(buf);
								} else {
									out.print(cs);
								}
							} else if (cell instanceof Bytes) {
								out.write(((Bytes) cell).getBytes());
							} else
								out.print(cell);
						}
						out.print(newline);
					}
				}
			} finally {
				IOH.close(out);
			}
		}
	}

	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}
	public <T> T getOption(String optionname, T deflt) {
		return CH.getOr((Class<T>) deflt.getClass(), this.options, optionname, deflt);
	}
}
