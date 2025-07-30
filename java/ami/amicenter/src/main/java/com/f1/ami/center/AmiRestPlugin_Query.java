package com.f1.ami.center;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsTrackerEvent;
import com.f1.ami.amicommon.msg.AmiCenterRequest;
import com.f1.ami.amicommon.msg.AmiCenterResponse;
import com.f1.ami.amicommon.rest.AmiRestPlugin;
import com.f1.ami.amicommon.rest.AmiRestRequest;
import com.f1.ami.center.console.AmiCenterConsoleClient;
import com.f1.ami.center.console.AmiCenterConsoleCmd_Sql;
import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultActionFuture;
import com.f1.container.ResultMessage;
import com.f1.utils.CH;
import com.f1.utils.ContentType;
import com.f1.utils.DateFormatNano;
import com.f1.utils.EH;
import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.FastPrintStream;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.Tuple2;

public class AmiRestPlugin_Query implements AmiRestPlugin {

	private ContainerTools tools;
	private RequestOutputPort<AmiCenterRequest, AmiCenterResponse> port;

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		this.tools = tools;
	}

	@Override
	public String getPluginId() {
		return "REST_QUERY";
	}

	@Override
	public String getEndpoint() {
		return "query";
	}

	@Override
	public void handler(AmiRestRequest rr, AmiAuthUser user) {
		AmiCenterQueryDsRequest request = tools.nw(AmiCenterQueryDsRequest.class);
		byte permissions = AmiCenterUtils.getPermissions(this.tools, user);
		request.setPermissions(permissions);
		request.setInvokedBy(user.getUserName());

		request.setType(AmiCenterQueryDsRequest.TYPE_QUERY);
		request.setOriginType(AmiCenterQueryDsRequest.ORIGIN_REST);
		int timeout = rr.getParamInt("timeout", 60000);
		int limit = rr.getParamInt("limit", 10000);
		String ds = rr.getParam("ds");
		String cmd = rr.getParam("cmd");
		if (SH.isnt(cmd))
			rr.error("cmd required");
		String showplan = rr.getParam("show_plan");
		String stringTemplate = rr.getParam("string_template");
		if (rr.hasError())
			return;
		request.setDatasourceName(ds == null ? "AMI" : ds);
		request.setQuery(cmd);
		request.setLimit(limit);
		request.setQuerySessionKeepAlive(true);
		request.setTimeoutMs(timeout);
		request.setIsTest("on".equals(showplan));
		request.setAllowSqlInjection("on".equals(stringTemplate));
		request.setQuerySessionKeepAlive(false);
		//TODO: request.setSessionVariables(client.getSessionVariables());
		//TODO: request.setSessionVariableTypes(client.getSessionVariableTypes());
		ResultActionFuture<AmiCenterResponse> future = port.requestWithFuture(request, null);
		ResultMessage<AmiCenterResponse> result = future.getResult(timeout);
		if (result.getError() != null)
			throw new RuntimeException(result.getError());
		AmiCenterQueryDsResponse action = (AmiCenterQueryDsResponse) result.getActionNoThrowable();
		String mode = rr.getParam("display");
		if ("text".equals(mode) || mode == null) {
			rr.setContentType(ContentType.TEXT);
			FastByteArrayOutputStream buf = new FastByteArrayOutputStream();
			FastPrintStream out = new FastPrintStream(buf);
			if (!action.getOk()) {
				AmiCenterConsoleClient.toErrorString(action, out);
			} else {
				DateFormatNano dateFormatNano = new DateFormatNano("yyyyMMdd hh:mm:ss.SSS zzz");
				dateFormatNano.setTimeZone(EH.getGMT());
				AmiCenterConsoleCmd_Sql.toString(null, Integer.MAX_VALUE, false, out, action, dateFormatNano, null, null);
			}
			out.flush();
			rr.println(buf.toString());
		} else if (mode.startsWith("json")) {
			boolean cols;
			boolean simple;
			if ("jsonMaps".equals(mode)) {
				simple = true;
				cols = false;
			} else if (mode.equals("json")) {
				simple = false;
				cols = true;
			} else if (mode.equals("jsonRows")) {
				simple = false;
				cols = false;
			} else {
				rr.error("<i>display</i> must be either: text, pipe, jsonMaps, json, jsonRows");
				return;
			}
			Map values = new HashMap();
			values.put("status", action.getOk() ? "ok" : "error");
			values.put("durationNanos", action.getDurrationNanos());
			values.put("rowsAffected", action.getRowsEffected());
			values.put("status", action.getOk() ? "ok" : "error");
			if (CH.isntEmpty(action.getGeneratedKeys())) {
				values.put("autogenKeys", action.getGeneratedKeys());
			}
			if (action.getReturnValueTablePos() == -1) {
				values.put("returnValue", action.getReturnValue());
				if (action.getReturnType() != null)
					values.put("returnType", AmiUtils.METHOD_FACTORY.forType(action.getReturnType()));
			}
			if (!action.getOk()) {
				Map<String, Object> error = new HashMap<String, Object>();
				Exception exception = action.getException();
				if (exception == null) {
					error.put("message", "unknown error");
				} else {
					error.put("message", exception.getMessage());
					if (exception instanceof ExpressionParserException) {
						ExpressionParserException ee = (ExpressionParserException) exception;
						Tuple2<Integer, Integer> pos = SH.getLinePosition(ee.getExpression(), ee.getPosition());
						error.put("atLine", pos.getA());
						error.put("atChar", pos.getB());
					}

				}
				values.put("error", error);
			}

			if (action.getTrackedEvents() != null) {
				List<AmiCenterQueryDsTrackerEvent> events = action.getTrackedEvents();
				List<Object> l = new ArrayList<Object>(events.size());
				for (AmiCenterQueryDsTrackerEvent s : events) {
					long time = s.getTimestamp();
					String type = AmiUtils.toStringForTrackedEventType(s.getType());
					l.add(CH.m("time", time, "type", type, "msg", s.getString()));
					s.getString();

				}
				values.put("plan", l);
			}
			if (CH.isntEmpty(action.getTables())) {
				List<Table> tables = action.getTables();
				if (simple) {
					Map l = new HashMap();
					for (Table t : tables) {
						List<Map> rows = new ArrayList<Map>(t.getSize());
						for (Row row : t.getRows())
							rows.add(new HashMap(row));
						l.put(t.getTitle(), rows);
					}
					values.put("tables", l);
				} else {
					List<Object> l = new ArrayList<Object>(tables.size());
					for (Table t : tables) {
						List<Object> columns = new ArrayList<Object>();
						int rowsCount = t.getSize();
						for (Column c : t.getColumns()) {
							String name = c.getId();
							String type = AmiUtils.METHOD_FACTORY.forType(c.getType());
							Map map = CH.m("name", name, "type", type);
							if (cols) {
								Object[] vals = new Object[rowsCount];
								for (int i = 0; i < rowsCount; i++)
									vals[i] = c.getValue(i);
								map.put("values", vals);
							}
							columns.add(map);
						}
						Map<String, Object> m = new HashMap<String, Object>();
						Map map = CH.m("title", t.getTitle(), "columns", columns);
						if (!cols) {
							int colsCount = t.getColumnsCount();
							Object oRows[] = new Object[rowsCount];
							for (int y = 0; y < rowsCount; y++) {
								Row row = t.getRow(y);
								Object oRow[] = new Object[colsCount];
								for (int x = 0; x < colsCount; x++) {
									oRow[x] = row.getAt(x);
								}
								oRows[y] = oRow;
							}
							map.put("rows", oRows);
						}
						l.add(map);
					}
					values.put("tables", l);
				}
			}
			rr.printJson(values);
		} else if ("pipe".equals(mode)) {
			rr.setContentType(ContentType.TEXT);
			FastByteArrayOutputStream buf = new FastByteArrayOutputStream();
			FastPrintStream out = new FastPrintStream(buf);
			if (!action.getOk()) {
				AmiCenterConsoleClient.toErrorString(action, out);
			} else {
				if (CH.isntEmpty(action.getTables())) {
					StringBuilder sb = new StringBuilder();
					for (Table t : action.getTables()) {
						int colsCount = t.getColumnsCount();
						Object o[] = new Object[2 + 2 * t.getColumnsCount()];
						int n = 0;
						o[n++] = t.getTitle();
						o[n++] = t.getSize();
						for (Column s : t.getColumns()) {
							o[n++] = AmiUtils.METHOD_FACTORY.forType(s.getType());
							o[n++] = s.getId();
						}
						sb.setLength(0);
						SH.joinWithEscape('|', '\\', o, sb);
						out.println(sb);
						for (Row row : t.getRows()) {
							Object oRow[] = new Object[colsCount];
							for (int x = 0; x < colsCount; x++) {
								oRow[x] = row.getAt(x);
							}
							sb.setLength(0);
							SH.joinWithEscape('|', '\\', oRow, sb);
							out.println(sb);
						}
					}
				}
			}
			out.flush();
			rr.println(buf.toString());
		} else
			rr.error("<i>display</i> must be either: text, pipe, jsonMaps, json, jsonRows");
	}

	@Override
	public boolean requiresAuth() {
		return true;
	}

	public void setItineraryPort(RequestOutputPort<AmiCenterRequest, AmiCenterResponse> itineraryPort) {
		this.port = itineraryPort;
		this.tools = port.getTools();
	}

}
