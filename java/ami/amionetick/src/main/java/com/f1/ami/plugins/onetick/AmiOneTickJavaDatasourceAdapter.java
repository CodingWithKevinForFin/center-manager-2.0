package com.f1.ami.plugins.onetick;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.center.ds.AmiDatasourceUtils;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.omd.jomd.OmdConstants;
import com.omd.jomd.OneTickLib;
import com.omd.jomd.OtqQuery;
import com.omd.jomd.RequestGroup;
import com.omd.jomd.StringCollection;
import com.omd.jomd.otq_parameters_t;

public class AmiOneTickJavaDatasourceAdapter implements AmiDatasourceAdapter {

	private static final Logger log = LH.get();
	private AmiServiceLocator serviceLocator;
	private ContainerTools tools;

	public static Map<String, String> buildOptions() {
		return new HashMap<String, String>();
	}
	@Override
	public void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException {
		this.tools = tools;
		this.serviceLocator = serviceLocator;
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		return new ArrayList();
	}

	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		return new ArrayList();
	}

	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}

	static private boolean tickLibLoaded = false;

	static void initTickLib(ContainerTools tools) throws AmiDatasourceException {
		if (tickLibLoaded)
			return;
		synchronized (AmiOneTickJavaDatasourceAdapter.class) {
			if (tickLibLoaded)
				return;
			String file;
			try {
				file = tools.getRequired("onetick.config.file");
			} catch (Exception e) {
				throw new AmiDatasourceException(AmiDatasourceException.INITIALIZATION_FAILED,
						"Option missing, which is required for one tick adapter: onetick.config.file=/path/to/one_tick_config.txt (please provide in local.properties and restart AMI)",
						e);
			}
			try {
				new OneTickLib(file);
			} catch (Exception e) {
				throw new AmiDatasourceException(AmiDatasourceException.INITIALIZATION_FAILED, "Failed to load one tick config file: '" + file + "'", e);
			}
			tickLibLoaded = true;
		}
	}
	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		initTickLib(this.tools);
		Map<String, Object> d = new HashMap<String, Object>(query.getDirectives());
		String srt = AmiDatasourceUtils.getOptional(d, "s");//"20180201093000";
		String end = AmiDatasourceUtils.getOptional(d, "e");//"20180201103000";
		String symbol = AmiDatasourceUtils.getOptional(d, "symbol");//"20180201103000";
		String symbolDate = AmiDatasourceUtils.getOptional(d, "symbol_date");//"20180201103000";
		String timezone = AmiDatasourceUtils.getOptional(d, "timezone");//"20180201103000";
		if (timezone == null)
			timezone = EH.getTimeZone();
		String url = getServiceLocator().getUrl();//"https://cldr.onetick.com:443/omdwebapi/rest/";
		String otq = query.getQuery();//"38/151/otq/c4853462-32a1-474c-b6c6-ff3af6527202.otq";
		String db = AmiDatasourceUtils.getOptional(d, "db");//TAQ

		com.omd.jomd.Connection conn = new com.omd.jomd.Connection();
		conn.connect(url);

		OtqQuery otquery = new OtqQuery(otq);
		StringCollection symbols = new StringCollection();
		if (symbol != null) {
			for (String s : SH.split(',', symbol))
				symbols.add(s);
			otquery.set_symbols(symbols);
		}
		if (symbolDate != null) {
			try {
				otquery.set_symbol_date(SH.parseInt(symbolDate));
			} catch (Exception e) {
				throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_symbol_date is not valid", e);
			}
		}
		if (srt != null)
			otquery.set_start_time(YYYMMDDhhmmss2Date(srt, timezone, "_s"));
		if (end != null)
			otquery.set_end_time(YYYMMDDhhmmss2Date(end, timezone, "_e"));
		otq_parameters_t params = new otq_parameters_t();
		for (Map.Entry<String, Object> t : d.entrySet())
			if (t.getKey().startsWith("otq_param_"))
				params.set(SH.stripPrefix(t.getKey(), "otq_param_", true), t.getValue().toString());
		if (db != null)
			params.set("db", db);
		otquery.set_otq_parameters(params);
		resultSink.setTables(new ArrayList<Table>());
		long start = System.currentTimeMillis();
		LH.info(log, " Sent Onetick query: ", otq, " with directives: ", d);
		AmiOneTickInfoCallback cb = new AmiOneTickInfoCallback(timezone, (List) resultSink.getTables());
		try {
			RequestGroup.process_otq_file(otquery, cb, conn);
		} finally {
			cb.delete();
		}
		int rows = 0;
		for (Table t : resultSink.getTables())
			rows += t.getSize();
		LH.info(log, " Onetick query '", otq, "' returned ", rows, " row(s) in ", System.currentTimeMillis() - start, " milli(s)");
	}
	private Date YYYMMDDhhmmss2Date(String srt, String timezone, String paramName) throws AmiDatasourceException {
		try {
			return YYYMMDDhhmmss2Date(srt, timezone);
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, paramName + " is not a valid YYYYMMDDHHMMSS format", e);
		}
	}

	@Override
	public boolean cancelQuery() {
		return false;
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult resultsSink, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload to datasource");
	}
	private static Date YYYMMDDhhmmss2Date(String time, String tz) {
		if (time.equalsIgnoreCase("NOW"))
			return OmdConstants.OMD_NOW;
		SimpleDateFormat sdf;
		if (time.contains(".")) {
			sdf = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
		} else {
			sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		}
		sdf.setTimeZone(TimeZone.getTimeZone(tz));
		ParsePosition pos = new ParsePosition(0);
		Date d = sdf.parse(time, pos);
		if (d == null)
			throw new RuntimeException("Invalid time specification. Time is expected to be presented as YYYYMMDDhhmmss[.SSS]");
		return d;
	}

}
