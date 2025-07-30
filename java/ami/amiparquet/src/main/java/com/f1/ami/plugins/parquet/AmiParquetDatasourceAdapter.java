package com.f1.ami.plugins.parquet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetReader.Builder;

import com.f1.ami.amicommon.AmiDatasourceAbstractAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.EH;
import com.f1.utils.FileNameFilter_TextMatcher;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.impl.PatternTextMatcher;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiParquetDatasourceAdapter extends AmiDatasourceAbstractAdapter {

	private static final String OPTION_PARQUET_DATA_EXT = "OPTION_PARQUET_DATA_EXT";
	private static final String DEFAULT_PARQUET_DATA_EXT = "parquet";

	private FileNameFilter_TextMatcher parquetDataFileMatcher;
	private AmiServiceLocator locator;
	private String filepath;
	private File dataDirectory = null;

	public static Map<String, String> buildOptions() {
		HashMap<String, String> r = new HashMap<String, String>();
		r.put(OPTION_PARQUET_DATA_EXT, "Comma delimited list of parquet data file name extensions, defaults to 'parquet'");
		return r;
	}

	@Override
	public void init(ContainerTools tools, AmiServiceLocator locator) throws AmiDatasourceException {
		super.init(tools, locator);
		this.locator = locator;
		this.tools = tools;

		this.filepath = SH.replaceAll(locator.getUrl(), '\\', '/');
		if (filepath.isEmpty())
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "A valid parquet filepath is required");

		this.dataDirectory = new File(filepath);
		if (!dataDirectory.exists())
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED,
					"Directory not found on " + EH.getLocalHost() + " ==> '" + IOH.getFullPath(dataDirectory) + "'");
		if (!dataDirectory.canRead())
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED,
					"Can not access Directory " + EH.getLocalHost() + " ==> '" + IOH.getFullPath(dataDirectory) + "' as user " + EH.getUserName());
		if (!dataDirectory.isDirectory())
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED,
					"URL must be a Directory " + EH.getLocalHost() + " ==> '" + IOH.getFullPath(dataDirectory) + "' as user " + EH.getUserName());

		String regex_options = getOption(OPTION_PARQUET_DATA_EXT, DEFAULT_PARQUET_DATA_EXT);
		List<String> regex_list = SH.splitToList(",", regex_options);
		String parsed_regex = "";
		for (int i = 0; i < regex_list.size(); ++i) {
			if (i != 0) {
				parsed_regex += "|";
			}
			parsed_regex += "^*\\." + SH.trim(regex_list.get(i)) + "$";
		}

		this.parquetDataFileMatcher = new FileNameFilter_TextMatcher(new PatternTextMatcher(parsed_regex, PatternTextMatcher.CASE_INSENSITIVE, true));
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		final File[] files = this.dataDirectory.listFiles(this.parquetDataFileMatcher);
		List<AmiDatasourceTable> tables = new ArrayList<AmiDatasourceTable>(files.length);
		for (final File f : files) {
			AmiDatasourceTable table = this.tools.nw(AmiDatasourceTable.class);
			table.setName(f.getName());
			tables.add(table);
		}
		return tables;
	}

	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		for (int i = 0; i < tables.size(); ++i) {
			final AmiDatasourceTable t = tables.get(i);
			String name = t.getName();
			try {
				Path p = new Path(this.filepath + "/" + name);
				Builder<?> builder = ParquetReader.builder(new AmiParquetReadSupport(), p);
				ParquetReader<?> reader2 = builder.build();
				ColumnarTable result = null;
				int rows_read = 0;
				while (rows_read < previewCount) {
					AmiParquetSimpleGroup group = (AmiParquetSimpleGroup) reader2.read();

					if (group == null)
						break;

					// Get the list of columns available for the schema
					if (result == null)
						result = (ColumnarTable) group.toAmiSchema();

					List<Object> val = group.getDataFlat();
					ColumnarRow r = result.newEmptyRow();
					r.setValues(val.toArray());
					result.getRows().add(r);
					++rows_read;
				}

				t.setPreviewData(result);
				t.setCustomQuery("SELECT * FROM " + name);
				reader2.close();

			} catch (Exception e) {
				throw new AmiDatasourceException(AmiDatasourceException.SCHEMA_ERROR, "Failed to parse table: " + name);
			}
		}
		return tables;
	}

	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		int limit = query.getLimit();

		String queryStr = SH.trim(query.getQuery());
		String[] args = SH.split(' ', queryStr);

		if (args.length < 4) {
			throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, "Expected syntax: SELECT [colnames/*] FROM file [WHERE ...], got: " + queryStr);
		}

		String operation = SH.toUpperCase(args[0]);
		if (!operation.equals("SELECT"))
			throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Adapter only supports SELECT clauses, got: " + args[0]);

		String uppercase_query = SH.toUpperCase(queryStr);
		if (SH.indexOfFirst(uppercase_query, 7, "LIMIT") != -1)
			throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "LIMIT keyword not supported, use ds limit=xx instead");

		int from_idx = SH.indexOfFirst(uppercase_query, 7, "FROM");
		if (from_idx == -1)
			throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, "Missing FROM clause");

		int where_idx = SH.indexOfFirst(uppercase_query, from_idx + 4, "WHERE");
		String whereClause = "";
		String fromFile = "";

		if (where_idx == -1) { //No where clause to handle
			fromFile = SH.trim(SH.substring(queryStr, from_idx + 4, queryStr.length()));
		} else {
			fromFile = SH.trim(SH.substring(queryStr, from_idx + 4, where_idx));
			whereClause = SH.trim(SH.substring(queryStr, where_idx + 5, queryStr.length()));
		}

		if (fromFile.isEmpty())
			throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, "Failed to read FROM clause");

		String columns = SH.trim(SH.substring(queryStr, 7, from_idx));

		try {
			Path p = new Path(this.filepath + "/" + fromFile);
			Builder<?> builder = ParquetReader.builder(new AmiParquetReadSupport(columns), p);

			ParquetReader<?> reader = builder.build();
			ColumnarTable result = null;
			int rows_read = 0;
			while (true) {
				AmiParquetSimpleGroup group = (AmiParquetSimpleGroup) reader.read();

				if (group == null)
					break;

				// Get the list of columns available for the schema
				if (result == null)
					result = (ColumnarTable) group.toAmiSchema();

				if (rows_read >= limit)
					break;

				List<Object> val = group.getDataFlat();
				ColumnarRow r = result.newEmptyRow();
				r.setValues(val.toArray());
				result.getRows().add(r);
				++rows_read;
			}
			result.setTitle(fromFile);
			List<Table> final_result = new ArrayList<Table>(1);

			if (whereClause.isEmpty() && columns.equals("*")) {
				final_result.add(result);
			} else {
				String new_query = "SELECT " + columns + " FROM `" + fromFile + "` WHERE " + whereClause + ";";
				SqlProcessor sp = new SqlProcessor();
				Tableset tablesMap = new TablesetImpl();
				tablesMap.putTable(result);
				Table out2 = sp.process(new_query, new TopCalcFrameStack(tablesMap, limit, tc, AmiUtils.METHOD_FACTORY, EmptyCalcFrame.INSTANCE));
				final_result.add(out2);
			}
			resultSink.setTables(final_result);
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw new AmiDatasourceException(AmiDatasourceException.SCHEMA_ERROR, "Failed to parse table: " + fromFile + ".\n" + e.toString());
		}
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult resultsSink, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload operations not supported");
	}

	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.locator;
	}
}
