package com.f1.ami.relay.fh.amps;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.crankuptheamps.authentication.kerberos.AMPSKerberosGSSAPIAuthenticator;
import com.crankuptheamps.client.Authenticator;
import com.crankuptheamps.client.Client;
import com.crankuptheamps.client.Command;
import com.crankuptheamps.client.Message;
import com.crankuptheamps.client.MessageStream;
import com.crankuptheamps.client.TCPSTransport;
import com.crankuptheamps.client.exception.AMPSException;
import com.crankuptheamps.client.exception.AuthenticationException;
import com.f1.ami.amicommon.AmiDatasourceAbstractAdapter;
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
import com.f1.ami.center.ds.AmiDatasourceUtils;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.CharReader;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.structs.table.columnar.ColumnarColumn;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.google.protobuf.Parser;

public class AmiAmpsDatasourceAdapter extends AmiDatasourceAbstractAdapter {

	private static final String MODE_RAW = "raw";

	private static final String DEFAULT_OPTION_CLIENT_NAME = "AMI";

	protected static final Logger log = LH.get();

	public static final String OPTION_CLIENT_NAME = "CLIENT_NAME";
	public static final String DIRECTIVE_MODE = "mode";
	public static final String BATCH_SIZE = "BATCH_SIZE";
	public static final String PROTOBUF_CLASS = "PROTOBUF_CLASS";

	public static final String KEYSTORE_FILE = "SSL_KEYSTORE_FILE";
	public static final String KEYSTORE_PASSWORD = "SSL_KEYSTORE_PASSWORD";

	public static final String STORE_TYPE = "SSL_STORE_TYPE";
	public static final String PROTOCOL = "SSL_PROTOCOL";

	//kerberos
	public static final String AMPS_HOSTNAME = "AMPS_HOSTNAME_FOR_KERBEROS";// AMPS hostname for kerberos authentication
	public static final String KERBEROS_USERNAME = "KERBEROS_USERNAME";// username for kerberos authentication
	public static final String KERBEROS_TIMEOUT = "KERBEROS_TIMEOUT";//milliseconds for kerberos authentication timeout
	public static final String KERBEROS_JAAS_MODULE = "KERBEROS_JAAS_MODULE_NAME";//name of the module for the kerberos connection in the jaas config

	private String clientUrl;
	private String adminUrl;
	private byte messageType;
	private Parser<?> protoParser;

	private String protocol;

	private String storeType;

	private String keystorePassword;

	private String keystoreFile;

	private String kerberosAmpsHostname;
	private long kerberosTimeout;
	private boolean useKerberos = false;
	private Authenticator kerberosAuthenticator;

	public static Map<String, String> buildOptions() {
		HashMap<String, String> r = new HashMap<String, String>();
		r.put(OPTION_CLIENT_NAME, "timeout in milliseconds");
		r.put(BATCH_SIZE, "batch size passed to amps configuration, default is 10000");
		r.put(PROTOBUF_CLASS, "If using protobufs, the class implementing com.google.protobuf.Parser");
		// SSL params
		r.put(KEYSTORE_FILE, "path of the keystore file with client certificate");
		r.put(KEYSTORE_PASSWORD, "password for keystore");

		r.put(STORE_TYPE, "type of keystore and trustore. e.g. JKS, PKCS12");
		r.put(PROTOCOL, "connection protocol e.g. TLSv1.1, TLSv1.2");

		//Kerberos params
		r.put(AMPS_HOSTNAME, "hostname of the amps server");
		r.put(KERBEROS_TIMEOUT, "milliseconds for kerberos authentication timeout");
		r.put(KERBEROS_JAAS_MODULE, "name of the module for kerberos authentication in the JAAS config");

		return r;
	}

	@Override
	public void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException {
		super.init(tools, serviceLocator);
		this.tools = tools;
		try {
			String url = serviceLocator.getUrl();
			String pass = new String(OH.noNull(serviceLocator.getPassword(), OH.EMPTY_CHAR_ARRAY));
			pass = SH.encodeUrl(pass);

			this.clientUrl = SH.replaceAll(SH.beforeFirst(url, ',', url), "****", pass);
			String au = SH.afterFirst(url, ',', null);
			this.adminUrl = au == null ? null : SH.replaceAll(au, "****", pass);
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.INITIALIZATION_FAILED, "URL should be in format:  host:port  or host:port,adminPort");
		}
		String protobufClass = getOption(PROTOBUF_CLASS, "");
		if (SH.is(protobufClass)) {
			this.messageType = AmiAmpsHelper.MTYPE_PROTOBUF;
			this.protoParser = AmiAmpsHelper.getProtobufParser(protobufClass);
		} else {
			this.messageType = AmiAmpsHelper.parseType(this.clientUrl);
		}

		//SSL
		this.keystoreFile = getOption(KEYSTORE_FILE, "");
		if (SH.is(keystoreFile)) {
			this.keystorePassword = getOption(KEYSTORE_PASSWORD, "");

			this.storeType = getOption(STORE_TYPE, "JKS");
			this.protocol = getOption(PROTOCOL, "TLSv1.2");

		}

		//kerberos
		this.kerberosAmpsHostname = getOption(AMPS_HOSTNAME, "");
		if (SH.is(kerberosAmpsHostname)) {
			this.setupKerberosAuth();
		}

	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		if (adminUrl == null)
			return Collections.EMPTY_LIST;
		String json;
		Object vals;
		try {
			if (this.useKerberos) {
				json = this.getRequestOverKerberos(adminUrl + "/amps/instance/sow.json");
				if (SH.isnt(json)) {
					throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Kerberos HTTP authentication failed. Please check log for warnings");
				}
				LH.fine(log, "Response: ", json);
			} else {
				try {
					if (SH.is(this.keystoreFile))
						setupSSLContext(keystoreFile, keystorePassword, storeType, protocol);
				} catch (Exception e) {
					throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, e);
				}
				json = new String(IOH.doGet(new URL(adminUrl + "/amps/instance/sow.json"), null, null, true, 60000));
			}
			vals = ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(json);
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.INITIALIZATION_FAILED, "Could not connect to admin port: " + adminUrl, e);
		}
		List<Map<String, Object>> sow = (List) RootAssister.INSTANCE.getNestedValue(vals, "amps.instance.sow", true);
		List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>();
		for (Map<String, Object> m : sow) {
			String name = (String) m.get("topic");
			AmiDatasourceTable t = tools.nw(AmiDatasourceTable.class);
			t.setName(name);
			t.setColumns(new ArrayList<AmiDatasourceColumn>());
			t.setCreateTableClause(AmiUtils.toValidVarName(name));
			t.setCustomQuery("SELECT * FROM " + name + " WHERE ${WHERE}");
			r.add(t);
		}

		return r;
	}

	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		Client client = getConnection();
		try {

			List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>(tables.size());
			for (AmiDatasourceTable i : tables) {
				MessageStream ms = client.sow(i.getName());
				List<Map<String, Object>> jsons = new ArrayList<Map<String, Object>>();
				int remaining = previewCount;
				for (Message row : ms) {
					if (remaining == 0)
						break;
					if (row.getCommand() == Message.Command.SOW) {
						jsons.add(toMap(row));
						remaining--;
					}
				}
				ColumnarTable table = toTable(jsons, i.getName());
				i.setPreviewData(table);
				r.add(i);
			}
			return r;
		} catch (AMPSException e) {
			throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, "Could not get preview data: ", e);
		} finally {
			client.close();
		}
	}

	private Map<String, Object> toMap(Message row) {
		Map<String, Object> r = new HasherMap<String, Object>();
		AmiAmpsHelper.parseMessage(this.messageType, row.getData(), r, null, protoParser);
		return r;
	}

	private Client getConnection() throws AmiDatasourceException {

		try {
			if (SH.is(this.keystoreFile))
				setupSSLContext(keystoreFile, keystorePassword, storeType, protocol);
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, e);
		}

		Client client = new Client(getOption(OPTION_CLIENT_NAME, DEFAULT_OPTION_CLIENT_NAME));

		try {
			client.connect(this.clientUrl);
			if (this.useKerberos) {
				client.logon(this.kerberosTimeout, this.kerberosAuthenticator);
			}
			client.logon(2000);
		} catch (AMPSException e) {
			if (client != null)
				client.close();
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Could not connect to client: " + this.clientUrl, e);
		} catch (Exception e) {
			if (client != null)
				client.close();
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Could not connect to client: " + this.clientUrl, e);
		} finally {
		}
		return client;
	}

	public static void setupSSLContext(String keystore, String keystorePassword, String storeType, String protocol)
			throws KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {

		KeyStore ks = KeyStore.getInstance(storeType);

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(keystore);
			ks.load(fis, keystorePassword.toCharArray());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (fis != null)
				fis.close();
		}

		// Get the key manager factory, using the default
		// algorithm.
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

		// Initialize the factory with the keystore.
		kmf.init(ks, keystorePassword.toCharArray());

		// Get the SSL context
		SSLContext context = SSLContext.getInstance(protocol);

		// Use the key manager just constructed, with defaults
		// for the trust manager and randomness source.

		context.init(kmf.getKeyManagers(), null, null);

		// Set the SSLContext for the TCPS transport
		// to the context just set up with the keystore.
		TCPSTransport.setDefaultSSLContext(context);
	}

	private String getRequestOverKerberos(String url) {

		boolean skipPortAtKerberosDatabaseLookup = true;

		Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider> create()
				.register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory(skipPortAtKerberosDatabaseLookup)).build();

		CloseableHttpClient client = HttpClients.custom().setDefaultAuthSchemeRegistry(authSchemeRegistry).build();
		HttpClientContext context = HttpClientContext.create();
		BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();

		Credentials useJaasCreds = new Credentials() {

			public String getPassword() {
				return null;
			}

			public Principal getUserPrincipal() {
				return null;
			}

		};

		credentialsProvider.setCredentials(new AuthScope(null, -1, null), useJaasCreds);
		context.setCredentialsProvider(credentialsProvider);

		HttpGet httpget = new HttpGet(url);

		try {
			CloseableHttpResponse response = client.execute(httpget, context);
			try {
				LH.fine(log, "Get Request over kerberos returned response: ", response.getStatusLine());
				if (response.getStatusLine().getStatusCode() == 200) {
					// if ok, return json
					return EntityUtils.toString(response.getEntity());
				} else {
					LH.warning(log, "Error in Kerberos GET: ", response.getStatusLine(), EntityUtils.toString(response.getEntity()));
					return "";
				}
			} catch (Exception e) {
				LH.warning(log, "Exception while parsing response: ", e);
				return "";
			} finally {
				response.close();
			}
		} catch (IOException e) {
			LH.warning(log, "Exception while getting response: ", e);
			return "";
		}
	}

	private void setupKerberosAuth() throws AmiDatasourceException {
		this.kerberosTimeout = getOption(KERBEROS_TIMEOUT, 5000);
		String ampsSPN = "AMPS/" + this.kerberosAmpsHostname;
		String jaasConfigName = getOption(KERBEROS_JAAS_MODULE, "");

		if (!SH.is(jaasConfigName)) {
			throw new AmiDatasourceException(AmiDatasourceException.INITIALIZATION_FAILED, "Please enter both JAAS config path and module name");
		}

		this.useKerberos = true;
		try {
			this.kerberosAuthenticator = new AMPSKerberosGSSAPIAuthenticator(ampsSPN, jaasConfigName);
		} catch (AuthenticationException e) {
			LH.warning(log, "Exception while connecting to kerberos:", e);
		}
	}

	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		String q = query.getQuery();
		StringCharReader scr = new StringCharReader(q);
		scr.setCaseInsensitive(true);
		scr.setToStringIncludesLocation(true);
		scr.expectSequence("select");
		scr.skip(StringCharReader.WHITE_SPACE);
		StringBuilder columns = new StringBuilder();
		StringBuilder tables = new StringBuilder();
		StringBuilder where = new StringBuilder();
		scr.readUntilSequenceAndSkip(" FROM ", columns);
		if (scr.readUntilSequenceAndSkip(" WHERE ", tables) == -1) {
			scr.readUntil(CharReader.EOF, tables);
		} else
			scr.readUntil(CharReader.EOF, where);
		Client client = getConnection();
		try {
			int batchSize = getOption(BATCH_SIZE, 10000);
			if (batchSize > query.getLimit())
				batchSize = query.getLimit();
			String filter = SH.trim(where.toString());
			if ("true".equals(filter) || "1=1".equals(filter))
				filter = "";
			String options = AmiDatasourceUtils.getOptional(query.getDirectives(), "options");
			String tableName = columns.toString();
			if (options == null) {
				if (tableName.equals("*"))
					options = "";
				else {
					StringBuilder sb = new StringBuilder();
					sb.append("select=[-/");
					for (String s : SH.split(",", tableName)) {
						sb.append(",+/");
						SH.trim(s, sb);
					}
					sb.append(']');
					options = sb.toString();

				}
			}
			Command cmd = new Command("sow");
			cmd.setTopic(tables.toString());
			cmd.setOptions(options);
			cmd.setBatchSize(batchSize);
			cmd.setTimeout(tc.getTimeoutMillisRemaining());
			cmd.setFilter(filter);
			MessageStream ms = client.execute(cmd);
			List<Map<String, Object>> jsons = new ArrayList<Map<String, Object>>();
			int remaining = query.getLimit();
			for (Message row : ms) {
				row.getMessageType();
				if (remaining == 0)
					break;
				if (row.getCommand() == Message.Command.SOW) {
					jsons.add(toMap(row));
					remaining--;
				}
			}
			ColumnarTable table = toTable(jsons, tableName);
			List<Table> t = new ArrayList<Table>();
			t.add(table);
			resultSink.setTables(t);
		} catch (AMPSException e) {
			throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, "Could not process query: " + query.getQuery(), e);
		} finally {
			client.close();
		}
	}

	@Override
	public boolean cancelQuery() {
		return false;
	}

	@Override
	public void processUpload(AmiCenterUpload request, AmiCenterQueryResult resultsSink, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		//Get Connection
		Client client = getConnection();
		try {
			LinkedHashMap<String, Object> temp = new LinkedHashMap<String, Object>();
			String mode = Caster_String.INSTANCE.cast(CH.getOr(request.getDirectives(), DIRECTIVE_MODE, ""));
			final byte mType = this.messageType; // prevent modification
			StringBuilder sb = new StringBuilder();
			// Go through each table that needs to be uploaded
			for (AmiCenterUploadTable ul : request.getData()) {
				String topic = ul.getTargetTable();
				Table table = ul.getData();
				List<Row> rows = table.getRows();
				String data = null;
				if (MODE_RAW.equalsIgnoreCase(mode)) {
					// use ds="amps" mode="raw" insert into `/messages` values ("a=b;c=d;f=ff");
					for (Row r : rows) {
						// each row contains 1 line of fix, so 1 column only
						// e.g. a=b;c=d;f=ff
						data = (String) (r.getAt(0));
						client.publish(topic, data);
					}
				} else {
					List<String> targetColumns = ul.getTargetColumns();
					int targetColumnsCount = targetColumns.size();
					int columnsCount = table.getColumnsCount();
					if (targetColumnsCount != columnsCount)
						throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR,
								"Source and target columns count mismatch: " + columnsCount + " != " + targetColumnsCount);
					LH.info(log, "Inserting ", table.getRows().size(), " row(s) x ", columnsCount, " columns(s) into ", ul.getTargetTable());
					// For each row, do the upload
					switch (mType) {
						case AmiAmpsHelper.MTYPE_JSON:
							for (Row row : rows) {
								for (int i = 0; i < targetColumnsCount; i++) {
									temp.put(targetColumns.get(i), row.getAt(i));
								}
								data = ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(temp);
								client.publish(topic, data);
								temp.clear();
							}
							break;
						case AmiAmpsHelper.MTYPE_NVFIX:
							for (Row row : rows) {
								for (int i = 0; i < targetColumnsCount; i++) {
									sb.append(targetColumns.get(i)).append("=").append(row.getAt(i)).append(AmiAmpsHelper.NVFIX_DELIMTER);
								}
								data = sb.toString();
								client.publish(topic, data);
								sb.setLength(0);
							}
							break;
						case AmiAmpsHelper.MTYPE_FIX:
							for (Row row : rows) {
								for (int i = 0; i < targetColumnsCount; i++) {
									sb.append(targetColumns.get(i)).append("=").append(row.getAt(i)).append(AmiAmpsHelper.FIX_DELIMITER);
								}
								data = sb.toString();
								client.publish(topic, data);
								sb.setLength(0);
							}
							break;
					}
				}

			}

		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, "Remote INSERT Error", e);
		} finally {
			if (client != null)
				client.close();
		}
	}

	protected ColumnarTable toTable(List<Map<String, Object>> jsons, String tableName) {
		LinkedHashMap<String, Class> types = new LinkedHashMap<String, Class>();
		for (Map<String, Object> i : jsons) {
			for (Entry<String, Object> e : i.entrySet()) {
				if (e.getValue() == null)
					continue;
				Class existingType = types.get(e.getKey());
				Class type = toType(e.getValue());
				if (existingType == null) {
					types.put(e.getKey(), type);
				} else if (existingType != type) {
					types.put(e.getKey(), OH.getWidest(existingType, type));
				}
			}
		}
		ColumnarTable r = new ColumnarTable();
		int n = 0;
		ColumnarColumn[] cols = new ColumnarColumn[types.size()];
		for (Entry<String, Class> e : types.entrySet())
			cols[n++] = r.addColumn(e.getValue(), e.getKey());
		for (Map<String, Object> i : jsons) {
			ColumnarRow row = r.newEmptyRow();
			for (ColumnarColumn c : cols)
				row.putAt(c.getLocation(), c.getTypeCaster().cast(i.get(c.getId())));
			r.getRows().add(row);
		}
		return r;
	}

	private static Class toType(Object value) {
		if (value instanceof Number) {
			if (OH.isFloat(value.getClass()))
				return Double.class;
			return Long.class;
		} else if (value instanceof Map) {
			return Map.class;
		} else
			return String.class;
	}
	//	public static void main(String[] args) {
	//		JsonBuilder jb = new JsonBuilder();
	//		jb.startMap();
	//		jb.addKeyValueAutotype("abc", 123, true);
	//		List l = new ArrayList();
	//		l.add("hello");
	//		l.add("world");
	//		jb.addKeyValueAutotype("k", l, true);
	//		jb.addKeyValueAutotype("j", false, true);
	//		jb.addKeyValueAutotype("m", "testing", true);
	//		jb.endMap();
	//		System.out.println(jb.toString());
	//
	//		Map m = new LinkedHashMap<String, Object>();
	//		m.put("abc", 123);
	//		m.put("k", l);
	//		m.put("j", false);
	//		m.put("m", "testing");
	//
	//		System.out.println(ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(m));
	//	}
}
