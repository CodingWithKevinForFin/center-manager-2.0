package com.f1.ami.center.console;

import java.util.List;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.center.dialects.AmiDbDialect;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.DateFormatNano;
import com.f1.utils.Formatter;
import com.f1.utils.OH;
import com.f1.utils.Println;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;

public class AmiCenterConsoleCmd_Sql extends AmiCenterConsoleCmd {

	public AmiCenterConsoleCmd_Sql() {
		super(" *", "run sql command");
	}
	@Override
	public void process(AmiCenterConsoleClient client, String cmd, String[] cmdParts) {
		AmiCenterQueryDsRequest request = client.getTools().nw(AmiCenterQueryDsRequest.class);
		request.setDatasourceName((String) client.getLocalSetting("ds"));
		AmiDbDialect dialect = client.getDialect();
		if (dialect != null)
			cmd = dialect.prepareQuery(cmd);
		request.setQuery(cmd);
		request.setPermissions(client.getPermissions());
		request.setType(AmiCenterQueryDsRequest.TYPE_QUERY);
		request.setOriginType(AmiCenterQueryDsRequest.ORIGIN_CMDLINE);
		int timeout = client.getLocalSetting("timeout", Integer.class);
		int limit = client.getLocalSetting("limit", Integer.class);
		int maxPrintChars = client.getLocalSetting("max_print_chars", Integer.class);
		boolean multilineCells = "on".equals(client.getLocalSetting("multiline_cells", String.class));
		request.setLimit(limit);
		request.setQuerySessionKeepAlive(true);
		request.setTimeoutMs(timeout);
		request.setIsTest("on".equals(client.getLocalSetting("show_plan", String.class)));
		request.setAllowSqlInjection("on".equals(client.getLocalSetting("string_template", String.class)));
		request.setSessionVariables(client.getSessionVariables());
		request.setSessionVariableTypes(client.getSessionVariableTypes());
		long querySessionId = client.getQuerySessionId();
		request.setQuerySessionId(querySessionId);
		Println out = client.getOutputStream();
		AmiCenterQueryDsResponse action = client.sendToAmiState(request, AmiCenterQueryDsResponse.class);
		if (action == null) {
			return;
		}
		//		if (action.getOk()) {
		client.setQuerySessionId(action.getQuerySessionId());
		DateFormatNano datetimeFormat = client.getDatetimeFormat();
		Formatter decimalFormat = client.getDecimalFormat();
		Formatter numberFormat = client.getNumberFormat();
		toString(dialect, maxPrintChars, multilineCells, out, action, datetimeFormat, decimalFormat, numberFormat);
	}
	public static void toString(AmiDbDialect dialect, int maxPrintChars, boolean multilineCells, Println out, AmiCenterQueryDsResponse action, DateFormatNano datetimeFormat,
			Formatter decimalFormat, Formatter numberFormat) {
		if (action.getTables() != null)
			for (Table i : action.getTables()) {
				if (dialect != null)
					i = dialect.prepareResult(i);
				StringBuilder sb = new StringBuilder();
				Formatter[] formatters = new Formatter[i.getColumnsCount()];
				for (int n = 0; n < formatters.length; n++) {
					final Class<?> type = i.getColumnAt(n).getType();
					final Formatter f;
					if (type == DateMillis.class || type == DateNanos.class)
						f = datetimeFormat;
					else if (OH.isFloat(type)) {
						f = decimalFormat;
					} else if (Number.class.isAssignableFrom(type)) {
						f = numberFormat;
					} else
						f = null;
					formatters[n] = f == null ? AmiUtils.FORMATTER : f;
				}
				int options = TableHelper.SHOW_ALL;
				if (!multilineCells)
					options |= TableHelper.DISALBE_MULTILINE_CELLS;
				TableHelper.toString(i, "", options, sb, SH.NEWLINE, TableHelper.DEFAULT_VERTICLE, TableHelper.DEFAULT_HORIZONTAL, TableHelper.DEFAULT_CROSS, maxPrintChars,
						AmiUtils.COLUMN_TYPES.getInnerValueKeyMap(), formatters);
				if (sb.length() >= maxPrintChars)
					sb.append(".......... (max_print_chars=").append(maxPrintChars).append(" reached,use SETLOCAL to extend) ..........\n");
				if (sb.length() == 0)
					sb.append("Table contents too large for display (max_print_chars=").append(maxPrintChars).append(" reached,use SETLOCAL to extend)\n");
				out.println(sb);
			}
		Class<?> returnType = action.getReturnType();
		boolean hasReturnValue = returnType != null && returnType != Void.class;
		if (hasReturnValue) {
			Object returnValue = AmiUtils.getReturnValue(action);
			if (returnValue != null)
				returnType = returnValue.getClass();
			String type = AmiUtils.METHOD_FACTORY.forType(returnType);
			out.print('(');
			out.print(type == null ? returnType : type);
			out.print(')');
			if (returnValue == null)
				out.println("null");
			else {
				if ((returnType == DateMillis.class || returnType == DateNanos.class) && datetimeFormat != null)
					out.println(datetimeFormat.format(returnValue));
				else {
					String s = AmiUtils.sJson(returnValue);
					if (s != null && s.indexOf('\n') != -1)
						out.println();
					out.println(s);
				}
			}
		}
		List<Object> ids = action.getGeneratedKeys();
		if (CH.isntEmpty(ids)) {
			StringBuilder sb = new StringBuilder("AUTOGEN-KEYS: [");
			SH.join(", ", ids, sb);
			sb.append("]");
			if (sb.length() >= maxPrintChars) {
				sb.setLength(maxPrintChars);
				sb.append(".......... (max_print_chars=").append(maxPrintChars).append(" reached,use SETLOCAL to extend) ..........\n");
			}
			out.println(sb);
		}
		//		}

		StringBuilder sb = new StringBuilder();
		DateFormatNano f = datetimeFormat;
		AmiUtils.toMessage(action, f, sb);
		if (sb.length() > 0) {
			if (sb.length() >= maxPrintChars) {
				sb.setLength(maxPrintChars);
				sb.append(".......... (max_print_chars=").append(maxPrintChars).append(" reached,use SETLOCAL to extend) ..........\n");
			}
			out.println(sb);
		}
	}

	@Override
	public boolean verifyLocalSetting(String key, Object value, StringBuilder sink) {
		if ("timeout".equals(key)) {
			Integer val = (Integer) value;
			if (val < 1) {
				sink.append("timeout must be positive value");
				return false;
			}

		} else if ("limit".equals(key)) {
			Integer val = (Integer) value;
			if (val != -1 && val < 0) {
				sink.append("limit must be a positive value or -1 for no limit");
				return false;
			}
		} else if ("max_print_lines".equals(key)) {
			Integer val = (Integer) value;
			if (val < 0) {
				sink.append("max_print_chars can't be negative");
				return false;
			}
		} else if ("show_plan".equals(key)) {
			String val = (String) value;
			if (!"on".equals(val) && !"off".equals(val)) {
				sink.append("show_plan must be 'on' or 'off'");
				return false;
			}
		} else if ("string_template".equals(key)) {
			String val = (String) value;
			if (!"on".equals(val) && !"off".equals(val)) {
				sink.append("string_template must be 'on' or 'off'");
				return false;
			}
		} else if ("ds".equals(key)) {
			if (SH.isnt((String) value)) {
				sink.append("ds required");
				return false;
			}
		} else if ("multiline_cells".equals(key)) {
			String val = (String) value;
			if (!"on".equals(val) && !"off".equals(val)) {
				sink.append("multiline_cells must be 'on' or 'off'");
				return false;
			}
		}
		return super.verifyLocalSetting(key, value, sink);
	}

	@Override
	public void init(AmiCenterConsoleClient client) {
		int defaultLimit = AmiUtils.getDefaultLimit(client.getTools());
		client.addLocalSetting("timeout", "20000", Integer.class);
		client.addLocalSetting("limit", SH.s(defaultLimit), Integer.class);
		client.addLocalSetting("max_print_chars", "100000", Integer.class);
		client.addLocalSetting("show_plan", "off", String.class);
		client.addLocalSetting("multiline_cells", "on", String.class);
		client.addLocalSetting("string_template", "off", String.class);
		client.addLocalSetting("ds", "AMI", String.class);
	}

}
