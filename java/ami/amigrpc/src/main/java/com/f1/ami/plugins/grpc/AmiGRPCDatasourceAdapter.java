package com.f1.ami.plugins.grpc;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiDatasourceAbstractAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.grpc.GRPCWrapper;
import com.f1.utils.grpc.ProtobufParser;
import com.f1.utils.grpc.ProtobufProperties;
import com.f1.utils.grpc.ProtobufUtils;
import com.f1.utils.grpc.ProtobufUtils.ProtobufObjectTemplate;
import com.f1.utils.grpc.ProtobufUtils.ProtobufStub;
import com.f1.utils.grpc.ProtobufUtils.StubType;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiGRPCDatasourceAdapter extends AmiDatasourceAbstractAdapter {

	//	private static final Logger log = LH.get();

	private GRPCWrapper grpc = new GRPCWrapper();

	private HashMap<String, ProtobufObjectTemplate> templates = new HashMap<String, ProtobufObjectTemplate>();

	@Override
	public void init(ContainerTools tools, AmiServiceLocator locator) throws AmiDatasourceException {
		super.init(tools, locator);
		final String url = locator.getUrl();
		if (SH.isnt(url))
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "A valid GRPC URL is required");
		final String stubClass = getOption(ProtobufProperties.PROPERTY_GRPC_STUB_CLASS, "");
		if (SH.isnt(stubClass))
			throw new AmiDatasourceException(AmiDatasourceException.INITIALIZATION_FAILED, "No stub class defined");
		String metadata = getOption(ProtobufProperties.PROPERTY_GRPC_METADATA, "");
		final String classPrefix = getOption(ProtobufProperties.PROPERTY_GRPC_CLASS_PREFIX, "");
		grpc.init(url, stubClass, metadata, StubType.BLOCKING, classPrefix);
	}

	public static Map<String, String> buildOptions() {
		return CH.m(ProtobufProperties.PROPERTY_GRPC_STUB_CLASS, "Fully qualified class of the protobuf stub class", ProtobufProperties.PROPERTY_GRPC_CLASS_PREFIX,
				"Optional prefix to be used while deducing protobuf classes", ProtobufProperties.PROPERTY_GRPC_METADATA,
				"Optional map of metadata options to be passed with each call through the stub. Example: abc:123,key2:abc");
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		final ProtobufStub stub = this.grpc.getStub();
		if (stub == null)
			return CH.emptyList(AmiDatasourceTable.class);
		List<AmiDatasourceTable> tables = CH.l();
		for (final Method m : stub.getMethods()) {
			AmiDatasourceTable table = this.tools.nw(AmiDatasourceTable.class);
			table.setName(m.getName());
			tables.add(table);
		}
		return tables;
	}

	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		final ProtobufStub stub = this.grpc.getStub();
		if (stub == null)
			return CH.emptyList(AmiDatasourceTable.class);

		List<Method> methods = stub.getMethods();
		for (int i = 0; i < tables.size(); ++i) {
			final AmiDatasourceTable t = tables.get(i);
			String name = t.getName();
			String queryString = "";

			ColumnarTable result = new ColumnarTable(new Class<?>[] { String.class, String.class }, new String[] { "name", "type" });
			for (final Method m : methods) {
				if (name.equals(m.getName())) {
					for (Parameter p : m.getParameters()) {
						ColumnarRow r = result.newEmptyRow();
						r.put("name", p.getName());
						r.put("type", p.getType().getName());
						String type = SH.toLowerCase(p.getType().getName());
						if ("string".equals(type)) {
							queryString += "\"" + p.getName() + "\",";
						} else {
							try {
								List<String> describe = ProtobufUtils.describeClass(p.getType().getName(), this.templates);
								queryString += "new " + p.getType().getName() + "(" + SH.strip(SH.toString(describe), "[", "]", false) + "),";
							} catch (Exception e) {
								queryString += p.getName() + "(" + p.getType().getName() + "),";
							}
						}
						result.getRows().add(r);
					}
					break;
				}
			}

			queryString = SH.beforeLast(queryString, ',');
			t.setPreviewData(result);
			t.setCustomQuery(t.getName() + "(" + queryString + ")");
		}

		return tables;
	}

	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		List<Table> result = new ArrayList<Table>(1);
		String queryStr = query.getQuery();
		byte firstInstruction = grpc.compileCommand(queryStr);

		//Handle describe command
		if (firstInstruction == ProtobufParser.INSTRUCTION_DESCRIBE_OBJECT) {
			String className = SH.toString(this.grpc.getMethodParams()[0]);
			final List<String> returnList = ProtobufUtils.describeClass(className, templates);
			resultSink.setReturnType(List.class);
			resultSink.setReturnValue(returnList);
			return;
		}

		Object stubResult = this.grpc.invokeCompiledMethod();
		ProtobufObjectTemplate template = null;
		Table resultTable = null;
		if (stubResult instanceof Iterator) {
			Iterator<?> it = (Iterator<?>) stubResult;
			while (it.hasNext()) {
				Object o = it.next();
				if (template == null) {
					template = ProtobufUtils.getOrSetTemplate(o, templates);
					resultTable = template.toEmptyTable(templates);
					resultTable.setTitle(this.grpc.getMethodName());
				}
				template.fillRow(resultTable, null, o, templates, ProtobufUtils.NO_PRIMARY_KEY);
			}
			result.add(resultTable == null ? new ColumnarTable() : resultTable);

		} else {
			template = ProtobufUtils.getOrSetTemplate(stubResult, templates);
			resultTable = template.toEmptyTable(templates);
			resultTable.setTitle(this.grpc.getMethodName());
			template.fillRow(resultTable, null, stubResult, templates, ProtobufUtils.NO_PRIMARY_KEY);
			result.add(resultTable);
		}
		resultSink.setTables(result);
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult resultsSink, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload operations are not supported for this adapter");
	}

}
