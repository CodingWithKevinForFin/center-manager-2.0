package com.f1.ami.plugins.chroniclequeue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.center.ds.AmiDatasourceUtils;
import com.f1.container.ContainerTools;
import com.f1.utils.OH;
import com.f1.utils.SH;

import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.wire.DocumentContext;
import net.openhft.chronicle.wire.Wire;

public class AmiChronicleQueueHelper {
	public static String DEFAULT_TABLE_PREFIX = "";

	public static AmiDatasourceTable findTable(File rootFile, File f, ContainerTools tools) {
		AmiDatasourceTable table = null;
		if (f.isDirectory()) {
			boolean hasFiles = false;
			for (File file : f.listFiles()) {
				if (file.isFile()) {
					hasFiles = true;
				}
			}
			if (hasFiles == true) {
				table = tools.nw(AmiDatasourceTable.class);
				table.setName(f.getName());
				table.setCollectionName(SH.afterFirst(SH.afterFirst(f.getParent(), rootFile.getParent()), File.separator));
			}
		}
		return table;
	}
	public static void findTables(File f, List<AmiDatasourceTable> tablesSink, String table_prefix, ContainerTools tools) throws AmiDatasourceException {
		if (f.isFile())
			return;
		else if (f.isDirectory()) {
			boolean hasFiles = false;
			for (File file : f.listFiles()) {
				if (file.isFile()) {
					hasFiles = true;
				} else if (file.isDirectory()) {
					String collection = SH.equals(DEFAULT_TABLE_PREFIX, table_prefix) ? f.getName() : SH.join("/", table_prefix, f.getName());
					AmiChronicleQueueHelper.findTables(file, tablesSink, collection, tools);
				}
			}
			if (hasFiles) {
				AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);
				table.setName(f.getName());
				table.setCollectionName(table_prefix);
				tablesSink.add(table);
			}
		}
	}
	public static Map<Object, Object> getChroniclePreview(File file) {
		HashMap<Object, Object> values = null;
		ChronicleQueue queue = null;
		try {
			queue = ChronicleQueue.singleBuilder(file).build();
			final ExcerptTailer tailer = queue.createTailer();
			final DocumentContext dc = tailer.readingDocument();
			if (dc.isPresent()) {
				Wire wire = dc.wire();
				values = new HashMap<Object, Object>();
				wire.readAllAsMap(Object.class, Object.class, values);

			}
		} finally {
			if (queue != null)
				queue.close();
		}
		return values;
	}

	public static void getTableSchema(File file, AmiDatasourceTable table, int previewCount, AmiChronicleQueueDatasourceAdapter adapter, ContainerTools tools,
			AmiDatasourceTracker debugSink) throws AmiDatasourceException {

		List<AmiDatasourceColumn> sink = new ArrayList<AmiDatasourceColumn>();

		//Get columns for each file
		Map<Object, Object> values = AmiChronicleQueueHelper.getChroniclePreview(file);

		if (values != null)
			for (Object j : values.keySet()) {
				AmiDatasourceColumn col = tools.nw(AmiDatasourceColumn.class);
				Class<?> c = OH.getClass(values.get(j));
				byte type = AmiUtils.getTypeForClass(c, AmiDatasourceColumn.TYPE_STRING);
				col.setName(SH.s(j));
				col.setType(type);
				sink.add(col);
			}
		table.setColumns(sink);

	}
	public static AmiCenterQuery getPreviewQuery(File rootFile, File file, AmiDatasourceTable table, int previewCount, ContainerTools tools) throws AmiDatasourceException {
		AmiCenterQuery q = tools.nw(AmiCenterQuery.class);
		List<AmiDatasourceColumn> cols = table.getColumns();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cols.size(); i++) {
			if (i != 0)
				sb.append(", ");
			AmiDatasourceColumn column = cols.get(i);

			sb.append(AmiUtils.toTypeName(column.getType())).append(' ');
			sb.append(column.getName());
		}

		Map<String, Object> directives = new LinkedHashMap<String, Object>();
		directives.put("fields", sb.toString());
		directives.put("file", SH.afterFirst(SH.afterFirst(file.getAbsolutePath(), rootFile.getParent()), File.separator));
		directives.put("delim", "_");
		directives.put("index", "index");

		q.setQuery("SELECT * FROM file");
		q.setLimit(previewCount);
		q.setDirectives(directives);

		table.setCustomQuery("SELECT * FROM file WHERE ${WHERE}");
		sb.setLength(0);
		sb.append("_delim=").append(SH.doubleQuote(AmiDatasourceUtils.getOptional(directives, "delim")));
		sb.append(" _index=").append(SH.doubleQuote(AmiDatasourceUtils.getOptional(directives, "index")));
		sb.append(" _file=").append(SH.doubleQuote(AmiDatasourceUtils.getOptional(directives, "file")));
		sb.append(" _fields=").append(SH.doubleQuote(AmiDatasourceUtils.getOptional(directives, "fields")));
		table.setCustomUse(sb.toString());

		long entryCount = 0;
		ChronicleQueue queue = null;
		try {
			queue = ChronicleQueue.singleBuilder(file).build();
			entryCount = ((SingleChronicleQueue) queue).entryCount();
			queue.close();
		} finally {
			if (queue != null)
				queue.close();
		}
		table.setPreviewTableSize(entryCount);
		return q;

	}
}
