package com.f1.ami.plugins.apache.avro;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.DatumReader;

import com.f1.ami.amicommon.AmiDatasourceAbstractAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.center.ds.AmiDatasourceUtils;
import com.f1.base.Bytes;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Getter;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.FileNameFilter_TextMatcher;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.impl.PatternTextMatcher;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.table.BasicColumn;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiAvroDatasourceAdapter extends AmiDatasourceAbstractAdapter {
	protected static final Logger log = LH.get();
	private static final String DIRECTIVE_SCHEMA_FILE = "schema";
	private static final String DIRECTIVE_AVRO_FILE = "file";
	private static final String DIRECTIVE_FIELDS = "fields";
	private static final String OPTION_AVRO_SCHEMA_FILE = "AVRO_SCHEMA_FILE";
	private static final String OPTION_AVRO_DATA_EXT = "AVRO_DATA_EXT";
	private static final String DEFAULT_AVRO_DATA_EXT = "avro";
	private static final ObjectToJsonConverter AVRO_JSON_CONVERTER = new ObjectToJsonConverter();
	static {
		AVRO_JSON_CONVERTER.setCompactMode(ObjectToJsonConverter.MODE_COMPACT);
		AVRO_JSON_CONVERTER.registerConverter(AmiAvroUtf8ToJsonConverter.INSTANCE);
	}

	public static Map<String, String> buildOptions() {
		HashMap<String, String> r = new HashMap<String, String>();
		r.put(OPTION_AVRO_DATA_EXT, "avrol data file name extension, default is avro");
		r.put(OPTION_AVRO_SCHEMA_FILE, "location of the avro schema file");
		return r;
	}

	private Map<String, String> options = new HashMap<String, String>();
	private String url;
	private File avroDataDirectory;
	private File defaultAvroSchemaFile;
	private FileNameFilter_TextMatcher avroDataFileMatcher;
	private Schema defaultSchema;
	private String defaultAvroSchemaFilePath;

	//	private FileNameFilter_TextMatcher avroSchemaFileMatcher;
	public AmiAvroDatasourceAdapter() {
	}

	@Override
	public void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException {
		super.init(tools, serviceLocator);
		this.url = SH.replaceAll(serviceLocator.getUrl(), '\\', '/');
		this.avroDataDirectory = new File(url);
		String avroDataRegex = "\\." + getOption(OPTION_AVRO_DATA_EXT, DEFAULT_AVRO_DATA_EXT) + '$';
		this.defaultAvroSchemaFilePath = getOption(OPTION_AVRO_SCHEMA_FILE, "");
		if (!SH.equals(defaultAvroSchemaFilePath, "")) {
			this.defaultAvroSchemaFile = new File(defaultAvroSchemaFilePath);
			checkFile(this.defaultAvroSchemaFile, AmiDatasourceException.INITIALIZATION_FAILED);
			this.defaultSchema = getSchemaFile(this.defaultAvroSchemaFile, AmiDatasourceException.INITIALIZATION_FAILED);
		}
		this.avroDataFileMatcher = new FileNameFilter_TextMatcher(new PatternTextMatcher(avroDataRegex, PatternTextMatcher.CASE_INSENSITIVE, true));
		//		avroSchemaFileMatcher = new FileNameFilter_TextMatcher(new PatternTextMatcher("\\.avsc", PatternTextMatcher.CASE_INSENSITIVE, true));
		if (!avroDataDirectory.exists()) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED,
					"Directory not found on " + EH.getLocalHost() + " ==> '" + IOH.getFullPath(avroDataDirectory) + "'");
		}
		if (!avroDataDirectory.canRead())
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED,
					"Can not access Directory " + EH.getLocalHost() + " ==> '" + IOH.getFullPath(avroDataDirectory) + "' as user " + EH.getUserName());
		if (!avroDataDirectory.isDirectory())
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED,
					"URL must be a Directory " + EH.getLocalHost() + " ==> '" + IOH.getFullPath(avroDataDirectory) + "' as user " + EH.getUserName());
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		MapInMap<String, String, AmiDatasourceTable> schemas = new MapInMap<String, String, AmiDatasourceTable>();
		for (File f : this.avroDataDirectory.listFiles(this.avroDataFileMatcher)) {
			//				this.defaultSchema.getFullName();
			Schema schema = this.getSchemaFromData(f.getName(), AmiDatasourceException.SCHEMA_ERROR);
			String name = schema.getName();
			String namespace = schema.getNamespace();
			if (!schemas.containsKey(namespace, name)) {
				AmiDatasourceTable table = this.tools.nw(AmiDatasourceTable.class);
				String fileName = f.getName();
				table.setName(fileName);
				table.setCollectionName(namespace);
				schemas.putMulti(namespace, fileName, table);
			}
		}
		return CH.l(schemas.valuesMulti());
	}

	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		StringBuilder sb = new StringBuilder();
		// For each table
		for (int i = 0; i < tables.size(); i++) {
			AmiDatasourceTable table = tables.get(i);

			Schema schema = this.getSchemaFromData(table.getName(), AmiDatasourceException.SCHEMA_ERROR);

			// Get the list of columns available for the schema
			List<AmiDatasourceColumn> amiColumnsSchema = this.getAmiColumnsSchema(schema);
			Map<String, Object> amiDirectives = getAmiDirectives(table.getName(), amiColumnsSchema, sb);
			table.setColumns(amiColumnsSchema);
			table.setCustomQuery(this.getAmiCustomQuery(table, sb).toString());
			SH.clear(sb);
			table.setCustomUse(this.getAmiCustomUse(table, amiDirectives, sb).toString());
			SH.clear(sb);

			// Create the query
			AmiCenterQuery q = tools.nw(AmiCenterQuery.class);
			q.setLimit(previewCount);
			q.setQuery(table.getCustomQuery());
			q.setDirectives(amiDirectives);

			// Run process query
			List<Table> previewData = AmiUtils.processQuery(this.tools, this, q, debugSink, tc);
			Table rs = previewData.get(0);
			tables.get(i).setPreviewData(rs);
		}

		return tables;
	}

	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		final List<Table> results = new ArrayList<Table>();
		String queryString = query.getQuery();
		int limit = query.getLimit();
		// Get directives
		Map<String, Object> d = query.getDirectives();
		String avroFile = AmiDatasourceUtils.getRequired(d, DIRECTIVE_AVRO_FILE);
		String schemaFile = AmiDatasourceUtils.getOptional(d, DIRECTIVE_SCHEMA_FILE);
		String fields = AmiDatasourceUtils.getOptional(d, DIRECTIVE_FIELDS);

		// Get schema based on directive
		Schema schema = null;
		if (SH.is(schemaFile))
			if (SH.equals(schemaFile, defaultAvroSchemaFilePath))
				schema = this.defaultSchema;
			else
				schema = this.getSchemaFile(new File(schemaFile), AmiDatasourceException.DIRECTIVE_ERROR);
		else
			schema = this.getSchemaFromData(avroFile, AmiDatasourceException.DIRECTIVE_ERROR);

		// Get columns
		Map<String, Column> columns = new LinkedHashMap<String, Column>();
		if (SH.is(fields))
			parseColumns(fields, columns);
		else {
			List<AmiDatasourceColumn> amiDatasourceColumns = getAmiColumnsSchema(schema);
			toColumnsFromAmiDatasourceColumns(amiDatasourceColumns, columns);
		}

		DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>();
		DataFileReader<GenericRecord> dataFileReader = null;
		GenericRecord record = null;

		ColumnarTable out = new ColumnarTable(new ArrayList(columns.values()));
		out.setTitle("file");
		try {

			File dataFile = getFileFromDirectory(avroFile);
			dataFileReader = new DataFileReader<GenericRecord>(dataFile, datumReader);

			// Convert to table
			long count = 0;
			while (dataFileReader.hasNext()) {
				if (limit != -1 && (count >= limit))
					break;
				record = dataFileReader.next(record);
				// Add row
				ColumnarRow crow = out.newEmptyRow();
				out.getRows().add(crow);
				for (Entry<String, Column> entry : columns.entrySet()) {
					// Go through columns
					String key = entry.getKey();
					Column col = entry.getValue();

					Object object = record.get(key);
					String id = col.getId();

					Caster<?> typeCaster = col.getTypeCaster();
					Object casted = null;
					if (typeCaster instanceof Caster_String && (object instanceof Collection || object instanceof Map))
						casted = AVRO_JSON_CONVERTER.objectToString(object);
					else if (object instanceof ByteBuffer)
						casted = new Bytes(((ByteBuffer) object).array());
					else if (object instanceof GenericFixed)
						casted = new Bytes(((GenericFixed) object).bytes());
					else if (object instanceof GenericEnumSymbol)
						casted = ((GenericEnumSymbol<?>) object).toString();
					else if (object instanceof IndexedRecord)
						casted = ((IndexedRecord) object).toString();
					else if (object instanceof CharSequence)
						casted = typeCaster.cast(object);
					else
						casted = typeCaster.cast(object);
					crow.put(id, casted);
				}
			}
		} catch (

		IOException e) {
			LH.log(log, Level.WARNING, "There was an error ", e);
			throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, e.getMessage());
		} catch (Exception e) {
			LH.log(log, Level.WARNING, "There was an error ", e);
			throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, e.getMessage());
		} finally {
			IOH.close(dataFileReader);
		}

		// Do query part of the command
		SqlProcessor sp = new SqlProcessor();
		Tableset tablesMap = new TablesetImpl();
		tablesMap.putTable(out);
		Table out2 = sp.process(queryString, new TopCalcFrameStack(tablesMap, limit, tc, AmiUtils.METHOD_FACTORY, EmptyCalcFrame.INSTANCE));
		results.add(out2);
		resultSink.setTables(results);
	}

	private File getFileFromDirectory(String avroFile) {
		String parentPath = SH.replaceAll(IOH.getFullPath(this.avroDataDirectory), '\\', '/');
		String filePath = SH.replaceAll(avroFile, '\\', '/');
		String out = parentPath;
		if (!SH.endsWith(parentPath, '/'))
			out += '/';
		out += filePath;

		return new File(out);

	}

	@Override
	public boolean cancelQuery() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult resultsSink, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload to datasource");
	}

	private static class GenericRecordGetter implements Getter<GenericRecord, Object> {
		private String key;

		public GenericRecordGetter(String key) {
			this.key = key;
		}

		@Override
		public Object get(GenericRecord record) {
			return record.get(key);
		}

	}

	private void checkFile(File f, int errorCode) throws AmiDatasourceException {
		if (!f.exists()) {
			throw new AmiDatasourceException(errorCode, "File not found on " + EH.getLocalHost() + " ==> '" + IOH.getFullPath(f) + "'");
		}
		if (!f.canRead())
			throw new AmiDatasourceException(errorCode, "Can not access file " + EH.getLocalHost() + " ==> '" + IOH.getFullPath(f) + "' as user " + EH.getUserName());
	}

	private Schema getSchemaFromData(String fileName, int errorCode) throws AmiDatasourceException {
		File dataFile = getFileFromDirectory(fileName);
		DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>();
		DataFileReader<GenericRecord> dataFileReader = null;

		Schema schema = null;
		try {
			dataFileReader = new DataFileReader<GenericRecord>(dataFile, datumReader);
			schema = dataFileReader.getSchema();
		} catch (IOException e) {
			throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, e.getMessage());
		} finally {
			IOH.close(dataFileReader);
		}

		return schema;
	}
	private Schema getSchemaFile(File f, int errorCode) throws AmiDatasourceException {
		Schema.Parser parser = new Schema.Parser();
		FileInputStream fis = null;
		Schema schema = null;
		try {
			fis = new FileInputStream(f);
			schema = parser.parse(fis);
		} catch (IOException e) {
			throw new AmiDatasourceException(errorCode, e.getMessage());
		} finally {
			IOH.close(fis);
		}
		return schema;
	}
	private List<AmiDatasourceColumn> getAmiColumnsSchema(Schema schema) {
		List<AmiDatasourceColumn> columns = new ArrayList<AmiDatasourceColumn>();

		List<Field> fieldList = schema.getFields();
		for (int i = 0; i < fieldList.size(); i++) {
			Field field = fieldList.get(i);
			Schema fieldSchema = field.schema();

			byte colType = getAmiDatasourceTypeForAvroType(fieldSchema);
			String colName = field.name();
			AmiDatasourceColumn nwCol = tools.nw(AmiDatasourceColumn.class);
			nwCol.setName(colName);
			nwCol.setType(colType);
			if (SH.equals("array", fieldSchema.getType().getName()) || SH.equals("map", fieldSchema.getType().getName()))
				nwCol.setHint(AmiDatasourceColumn.HINT_JSON);
			columns.add(nwCol);

		}

		return columns;
	}
	/*
	 * This is a Map<String, String>
	 */
	private Map<String, Object> getAmiDirectives(String avroFileName, List<AmiDatasourceColumn> amiColumns, StringBuilder sb) {
		Map<String, Object> directives = new LinkedHashMap<String, Object>();
		directives.put(DIRECTIVE_AVRO_FILE, (avroFileName));
		//		directives.put(DIRECTIVE_SCHEMA_FILE, this.defaultAvroSchemaFilePath == null ? "" : (this.defaultAvroSchemaFilePath));
		if (amiColumns != null) {
			getDirectiveFields(amiColumns, sb);
			directives.put(DIRECTIVE_FIELDS, sb.toString());
			SH.clear(sb);
		}

		return directives;
	}

	private StringBuilder getAmiCustomUse(AmiDatasourceTable table, Map<String, Object> directives, StringBuilder sink) {
		for (Entry<String, Object> e : directives.entrySet()) {
			sink.append('_').append(e.getKey());
			sink.append('=').append(SH.doubleQuote(e.getValue().toString())).append(' ');
		}

		return sink;
	}
	private StringBuilder getAmiCustomQuery(AmiDatasourceTable table, StringBuilder sink) {
		sink.append("SELECT * FROM file");

		return sink;
	}
	private static void getDirectiveFields(List<AmiDatasourceColumn> amiColumns, StringBuilder sb) {
		for (int i = 0; i < amiColumns.size(); i++) {
			AmiDatasourceColumn col = amiColumns.get(i);
			if (i != 0)
				sb.append(',').append(' ');
			sb.append(AmiUtils.toTypeName(col.getType())).append(' ');
			sb.append(col.getName());
		}
	}

	private static void parseColumns(String columnsString, Map<String, Column> columns) throws AmiDatasourceException {
		String[] parts = SH.trimArray(SH.split(',', columnsString));
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			String type = SH.beforeFirst(part, ' ', "String").trim();
			String name = SH.afterFirst(part, ' ', part).trim();
			String validName = AmiUtils.toValidVarName(name);
			//			if (!AmiUtils.isValidVariableName(name, false, false))
			//				throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, directiveName + " directive defines invalid column name: '" + name + "'");
			byte typeClass = AmiUtils.parseTypeName(type);
			Column column = columns.get(name);
			Class<?> classForValueType = AmiUtils.getClassForValueType(typeClass);
			if (column == null) {
				columns.put(name, new BasicColumn(classForValueType, validName));
			} else if (column.getType() != classForValueType)
				throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "fields has inconsistent type '" + type + "' for column: '" + name + "'");
		}
	}
	private static void toColumnsFromAmiDatasourceColumns(List<AmiDatasourceColumn> amiDsColumns, Map<String, Column> columns) {
		for (int i = 0; i < amiDsColumns.size(); i++) {
			AmiDatasourceColumn amiCol = amiDsColumns.get(i);
			String name = amiCol.getName();
			String validName = AmiUtils.toValidVarName(name);
			Class<?> classForValueType = AmiUtils.getClassForValueType(amiCol.getType());
			columns.put(amiCol.getName(), new BasicColumn(classForValueType, validName));
		}

	}
	private byte getAmiDatasourceTypeForAvroType(Schema schema) {
		Type type = schema.getType();
		String typeName = type.getName();
		//	int typeOrd = type.ordinal();
		//		Type.DOUBLE;
		byte amiType = AmiDatasourceColumn.TYPE_UNKNOWN;
		switch (typeName) {
			//Primitive data types;
			case "null":
				amiType = AmiDatasourceColumn.TYPE_UNKNOWN;
				break;
			case "boolean":
				amiType = AmiDatasourceColumn.TYPE_BOOLEAN;
				break;
			case "int":
				amiType = AmiDatasourceColumn.TYPE_INT;
				break;
			case "long":
				amiType = AmiDatasourceColumn.TYPE_LONG;
				break;
			case "float":
				amiType = AmiDatasourceColumn.TYPE_FLOAT;
				break;
			case "double":
				amiType = AmiDatasourceColumn.TYPE_DOUBLE;
				break;
			case "bytes":
				// TODO: Not sure what the right type is:
				//				bytes
				//
				//				A sequence of 8-bit unsigned bytes.
				amiType = AmiDatasourceColumn.TYPE_BINARY;
				break;
			case "string":
				amiType = AmiDatasourceColumn.TYPE_STRING;
				break;
			// complex data types
			case "enum":
				amiType = AmiDatasourceColumn.TYPE_STRING;
				break;
			case "array":
				// TODO: Not sure what the right type is:
				amiType = AmiDatasourceColumn.TYPE_STRING;
				break;
			case "map":
				// TODO: Not sure what the right type is:
				amiType = AmiDatasourceColumn.TYPE_STRING;
				break;
			case "fixed":
				amiType = AmiDatasourceColumn.TYPE_BINARY;
				break;
			case "union":
				List<Schema> types = schema.getTypes();
				Class<?> clazz = null;
				for (int i = 0; i < types.size(); i++) {
					byte itype = this.getAmiDatasourceTypeForAvroType(types.get(i));
					if (itype == AmiDatasourceColumn.TYPE_UNKNOWN)
						continue;
					Class<?> iclass = AmiUtils.getClassForValueType(itype);
					if (clazz == null)
						clazz = iclass;
					else
						clazz = AmiUtils.getWidest(clazz, iclass);
				}
				amiType = AmiUtils.getTypeForClass(clazz, AmiDatasourceColumn.TYPE_STRING);

				break;
			case "record":
				amiType = AmiDatasourceColumn.TYPE_STRING;
				break;
			default:
		}

		return amiType;
	}

}
