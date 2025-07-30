package com.f1.ami.plugins.bloomberg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.bloomberglp.blpapi.AuthApplication;
import com.bloomberglp.blpapi.AuthOptions;
import com.bloomberglp.blpapi.AuthUser;
import com.bloomberglp.blpapi.CorrelationID;
import com.bloomberglp.blpapi.Element;
import com.bloomberglp.blpapi.Event;
import com.bloomberglp.blpapi.EventQueue;
import com.bloomberglp.blpapi.Identity;
import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.Name;
import com.bloomberglp.blpapi.Request;
import com.bloomberglp.blpapi.Schema.Datatype;
import com.bloomberglp.blpapi.Service;
import com.bloomberglp.blpapi.Session;
import com.bloomberglp.blpapi.SessionOptions;
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
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.CaseInsensitiveHasher;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiBloombergDatasourceAdapter implements AmiDatasourceAdapter {
	private static final String APPLICATION_NAME = "applicationName";
	private static final String REQUEST_TYPE = "requestType";
	private Map<String, String> options = new HasherMap<String, String>(CaseInsensitiveHasher.INSTANCE);

	public static Map<String, String> buildOptions() {
		HashMap<String, String> r = new HashMap<String, String>();
		r.put(APPLICATION_NAME, "Application Name");
		r.put(REQUEST_TYPE, "requestType");
		return r;
	}

	private AmiServiceLocator serviceLocator;
	private ContainerTools tools;
	protected static final Logger log = LH.get();
	protected static final String HISTORICAL_DATA_RESPONSE = "HistoricalDataResponse";
	protected static final String REFERENCE_DATA_RESPONSE = "ReferenceDataResponse";
	protected static final Name FIELD_DATA = new Name("fieldData");
	protected static final Name SECURITY_DATA = new Name("securityData");
	protected static final Name securities = new Name("securities");
	protected static final Name fieldReq = new Name("fields");
	protected static final Name Security = new Name("security");

	@Override
	public void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException {
		this.serviceLocator = serviceLocator;
		this.tools = tools;
		if (this.serviceLocator.getOptions() != null)
			this.options = SH.splitToMap(options, ',', '=', '\\', serviceLocator.getOptions());
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>(1);
		AmiDatasourceTable sample = tools.nw(AmiDatasourceTable.class);
		String request = this.options.get(REQUEST_TYPE);
		if (SH.equals(request.toLowerCase(), "historical")) {
			sample.setName("Historical");
			sample.setCustomUse(
					"_username=\"demo\" _ip=\"192.168.123.132\" _ticker=\"BBHBEAT Index\" _fields=\"PX_LAST\" _startDate=\"20191209\" _endDate=\"20231202\" _period=\"MONTHLY\"");
		} else if (SH.equals(request.toLowerCase(), "reference")) {
			sample.setName("Reference");
			sample.setCustomUse("_username=\"demo\" _ip=\"192.168.123.132\" _ticker=\"BBHBEAT Index\" _fields=\"PX_Last,TIME\"");
		} else
			LH.warning(log, "UNKNOWN request type: " + request + " Make sure it is reference or historical");
		String defaultQueryForTable = "SELECT * FROM " + sample.getName();
		sample.setCustomQuery(defaultQueryForTable);
		r.add(sample);
		return r;
	}

	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		return tables;
	}

	public String getName() {
		return this.serviceLocator.getTargetName();
	}

	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		String ticker = query.getDirectives().getOrDefault("ticker", "").toString();
		String[] tickerList = ticker.split(",");
		String fields = query.getDirectives().getOrDefault("fields", "").toString();
		String[] fieldList = fields.split(",");
		String startDate = query.getDirectives().getOrDefault("startDate", "").toString();
		String endDate = query.getDirectives().getOrDefault("endDate", "").toString();
		String period = query.getDirectives().getOrDefault("period", "").toString();
		String currency = query.getDirectives().getOrDefault("currency", "USD").toString();
		String username = query.getDirectives().getOrDefault("username", "").toString();
		String ipAddr = query.getDirectives().getOrDefault("ip", "").toString();
		String serverAddress = serviceLocator.getUrl();
		String applicationName = this.options.get(APPLICATION_NAME);
		String requestType = this.options.get(REQUEST_TYPE);
		CorrelationID authCorrelationId = new CorrelationID(username);
		Tuple2<String, String> userDetails = new Tuple2<String, String>();
		userDetails.setAB(username, ipAddr);
		Session session = null;
		if (ConnectionPool.getActiveConnections().containsKey(userDetails))
			session = ConnectionPool.getSession(userDetails);
		else
			session = this.createSession(serverAddress, applicationName, userDetails, authCorrelationId);
		try {
			session.start();
		} catch (Throwable e) {
			LH.severe(log, "CANNOT START SESSION", e);
			ConnectionPool.removeConnection(userDetails);
			return;
		}
		try {
			session.openService("//blp/refdata");
		} catch (Throwable e) {
			LH.severe(log, "FAILED TO OPEN SERVICE", e);
			ConnectionPool.removeConnection(userDetails);
			return;
		}
		ColumnarTable responseTable = tools.nw(ColumnarTable.class);
		if (SH.equals(requestType.toLowerCase(), "historical"))
			getHistorical(session, tickerList, fieldList, startDate, endDate, period, currency, userDetails, responseTable);
		if (SH.equals(requestType.toLowerCase(), "reference"))
			getReference(session, tickerList, fieldList, userDetails, responseTable);
		else
			LH.warning(log, "UNKNOWN request type: Make sure it is reference or historical");
		List<Table> response = new ArrayList<Table>();
		LH.info(log, "AmiBloombergHistorical: Running Query:" + query);
		response.add(responseTable);
		resultSink.setTables(response);
	}

	@Override
	public boolean cancelQuery() {
		return false;
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult results, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload to datasource");
	}
	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}

	public Session createSession(String serverAddress, String applicationName, Tuple2<String, String> userDetails, CorrelationID authCorrelationId) {
		SessionOptions sessionOptions = new SessionOptions();
		SessionOptions.ServerAddress[] servers = new SessionOptions.ServerAddress[2];
		String[] l = serverAddress.split(":");
		String host = l[0];
		int port = Integer.valueOf(l[1]);
		servers[0] = new SessionOptions.ServerAddress(host, port);
		servers[1] = new SessionOptions.ServerAddress(host, port);
		sessionOptions.setServerAddresses(servers);
		sessionOptions.setAutoRestartOnDisconnection(true);
		sessionOptions.setNumStartAttempts(30);
		AuthOptions authOptions = null;
		try {
			authOptions = new AuthOptions(AuthUser.createWithManualOptions(userDetails.getA(), userDetails.getB()), new AuthApplication(applicationName));
		} catch (Throwable e) {
			LH.severe(log, "Cannot authenticate user");
		}
		Session session = null;
		try {
			sessionOptions.setSessionIdentityOptions(authOptions, authCorrelationId);
		} catch (Throwable e) {
			LH.severe(log, e);
		} finally {
			session = new Session(sessionOptions);
			if (session != null)
				ConnectionPool.addConnection(userDetails, session);
		}
		return session;
	}

	public Request createRequest(Session session, String request) {
		Service refDataService = session.getService("//blp/refdata");
		try {
			session.start();
		} catch (Throwable e) {
			LH.severe(log, e);
		}
		Request r = refDataService.createRequest(request);
		return r;
	}

	public void getResponse(Session session, Request r, String correlationID, Tuple2<String, String> userDetails, Table responseTable, EventQueue eventQueue) {
		if (!responseTable.getColumns().contains("ticker")) {
			responseTable.addColumn(String.class, "ticker");
		}
		boolean flag = true;
		while (flag) {
			Event event = null;
			try {
				event = eventQueue.nextEvent();
			} catch (InterruptedException e) {
				LH.severe(log, e);
			}
			for (Message msg : event) {
				if (msg.messageType().equals(HISTORICAL_DATA_RESPONSE)) {
					Element securityData = msg.getElement(SECURITY_DATA);
					Element security = securityData.getElement(Security);
					String ticker = security.getValueAsString();
					if (securityData.hasElement(FIELD_DATA)) {
						Element fieldData = securityData.getElement(FIELD_DATA);
						for (int i = 0; i < fieldData.numValues(); i++) {
							Element data = fieldData.getValueAsElement(i);
							this.createSchema(data, responseTable);
							this.addData(data, responseTable, ticker);
						}
					}
				}
				if (msg.messageType().equals(REFERENCE_DATA_RESPONSE)) {
					Element securityData = msg.getElement(SECURITY_DATA);
					for (int i = 0; i < securityData.numValues(); i++) {
						Element security = securityData.getValueAsElement(i).getElement(Security);
						String ticker = security.getValueAsString();
						Element fieldData = securityData.getValueAsElement(i).getElement(FIELD_DATA);
						this.createSchema(fieldData, responseTable);
						this.addData(fieldData, responseTable, ticker);
					}
				}
				if (event.eventType().equals(Event.EventType.RESPONSE)) {
					flag = false;
				}
			}
		}

	}

	public void getHistorical(Session session, String[] tickers, String[] fields, String startDate, String endDate, String period, String currency,
			Tuple2<String, String> userDetails, Table responseTable) {
		Request r = this.createRequest(session, "HistoricalDataRequest");
		Name sD = new Name("startDate");
		Name eD = new Name("endDate");
		Name periodAdjust = new Name("periodicityAdjustment");
		Name periodSelection = new Name("periodicitySelection");
		Name curr = new Name("currency");

		Element securitiesElement = r.getElement(securities);
		Element fieldElement = r.getElement(fieldReq);

		r.set(sD, startDate);
		r.set(eD, endDate);
		r.set(periodAdjust, "ACTUAL");
		r.set(periodSelection, period);
		r.set(curr, currency);

		String correlationID = "HISTORICAL_REQUEST_";

		for (String ticker : tickers) {
			securitiesElement.appendValue(ticker);
			correlationID += ticker;

		}

		for (String field : fields) {
			fieldElement.appendValue(field);
			correlationID += field;
		}

		correlationID += startDate + endDate + period + " currency: " + currency;
		Identity identity = session.getSessionIdentity();

		CorrelationID cid = new CorrelationID(correlationID + "_" + userDetails.getA() + "_" + this.getName());

		EventQueue eventQueue = new EventQueue();
		try {
			session.sendRequest(r, identity, eventQueue, cid);
		} catch (Throwable e) {
			LH.warning(log, "CANNOT SEND HISTORICAL REQUEST FOR REQUEST ID: " + r.getRequestId() + "Error:" + e);
		}
		LH.info(log, "SENT BLOOMBERG HISTORICAL REQUEST WITH REQUEST ID : " + r.getRequestId());
		getResponse(session, r, correlationID, userDetails, responseTable, eventQueue);
	}

	public void getReference(Session session, String[] tickers, String[] fields, Tuple2<String, String> userDetails, Table responseTable) {
		Request r = this.createRequest(session, "ReferenceDataRequest");
		String correlationID = "REFERENCE_REQUEST_";
		Element securitiesElement = r.getElement(securities);
		Element fieldElement = r.getElement(fieldReq);

		for (String ticker : tickers) {
			securitiesElement.appendValue(ticker);
			correlationID += ticker;
		}

		for (String field : fields) {
			fieldElement.appendValue(field);
			correlationID += field;
		}
		CorrelationID cid = new CorrelationID(correlationID + "_" + userDetails.getA() + "_" + this.getName());

		EventQueue eventQueue2 = new EventQueue();
		Identity identity = session.getSessionIdentity();

		try {
			session.sendRequest(r, identity, eventQueue2, cid);
		} catch (Throwable e) {
			LH.warning(log, "CANNOT SEND REFERENCE REQUEST FOR REQUEST ID: " + r.getRequestId() + "Error:" + e);
		}
		LH.info(log, "SENDING BLOOMBERG REFERENCE REQUEST WITH REQUEST ID: " + r.getRequestId());
		getResponse(session, r, correlationID, userDetails, responseTable, eventQueue2);
	}

	public void createSchema(Element e, Table responseTable) {
		for (int i = 0; i < e.numElements(); i++) {
			Element ee = e.getElement(i);
			String fieldName = e.getElement(i).name().toString();
			if (!responseTable.getColumnIds().contains(fieldName)) {
				responseTable.addColumn(processType(ee), fieldName);
			}
		}
	}

	public void addData(Element e, Table responseTable, String ticker) {
		Object[] a = new Object[responseTable.getColumns().size()];
		a[0] = ticker;

		for (int i = 0; i < e.numElements(); i++) {
			Element value = e.getElement(i);
			String columnName = value.elementDefinition().name().toString();
			int pos = responseTable.getColumn(columnName).getLocation();

			if (value.datatype() == Datatype.STRING || value.datatype() == Datatype.ENUMERATION)
				a[pos] = value.getValueAsString();
			else if (value.datatype() == Datatype.FLOAT64)
				a[pos] = value.getValueAsFloat64();
			else if (value.datatype() == Datatype.DATE)
				a[pos] = (Long) value.getValueAsDate().calendar().getTimeInMillis();
			else if (value.datatype() == Datatype.TIME)
				a[pos] = (Long) value.getValueAsTime().calendar().getTimeInMillis();
			else if (value.datatype() == Datatype.DATETIME)
				a[pos] = (Long) value.getValueAsDatetime().calendar().getTimeInMillis();
			else if (value.datatype() == Datatype.FLOAT32)
				a[pos] = value.getValueAsFloat32();
			else {
				a[pos] = value.getValueAsString();
			}
		}
		responseTable.getRows().addRow(a);
	}

	public Class<?> processType(Element e) {
		if (e.datatype() == Datatype.STRING || e.datatype() == Datatype.ENUMERATION)
			return String.class;
		else if (e.datatype() == Datatype.INT32)
			return Integer.class;
		else if (e.datatype() == Datatype.INT64)
			return Long.class;
		else if (e.datatype() == Datatype.FLOAT32)
			return Float.class;
		else if (e.datatype() == Datatype.FLOAT64)
			return Double.class;
		else if (e.datatype() == Datatype.BOOL)
			return Boolean.class;
		else if (e.datatype() == Datatype.CHAR)
			return Character.class;
		else if (e.datatype() == Datatype.DATE || e.datatype() == Datatype.DATETIME || e.datatype() == Datatype.TIME)
			return Long.class;
		else
			return String.class;
	}
}
