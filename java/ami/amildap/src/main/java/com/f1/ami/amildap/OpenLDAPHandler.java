package com.f1.ami.amildap;

import java.io.File;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.f1.container.ContainerTools;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.extensions.StartTLSExtendedRequest;
import com.unboundid.util.NotMutable;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ssl.AggregateTrustManager;
import com.unboundid.util.ssl.HostNameSSLSocketVerifier;
import com.unboundid.util.ssl.JVMDefaultTrustManager;
import com.unboundid.util.ssl.PEMFileTrustManager;
import com.unboundid.util.ssl.SSLSocketVerifier;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import com.unboundid.util.ssl.TrustStoreTrustManager;

public class OpenLDAPHandler {
	private static final String LDAP_ADDRESS_PROP = "ldap.address";
	private static final String LDAP_PORT_PROP = "ldap.port";
	private static final String LDAP_BIND_DN_PROP = "ldap.bindDN";
	private static final String LDAP_BIND_PW_PROP = "ldap.bindPW";
	private static final String LDAP_BASE_DN_PROP = "ldap.baseDN";
	private static final String LDAP_USER_ATTR_NAME_PROP = "ldap.userAttributeName";
	private static final String LDAP_MAX_CONNECTIONS = "ldap.maxConnections";
	private static final String LDAP_INITIAL_CONNECTIONS = "ldap.initialConnections";
	private static final String LDAP_SSL_TRUSTSTORE_PATH = "ldap.sslTrustStorePath";
	private static final String LDAP_SSL_TRUSTSTORE_PIN = "ldap.sslTrustStorePin";
	private static final String LDAP_SSL_TRUSTSTORE_FORMAT = "ldap.sslTrustStoreFormat";
	private static final String LDAP_SSL_VALIDATE_HOSTNAME = "ldap.sslValidateHostname";
	private static final String LDAP_USE_SSL = "ldap.useSSL";
	private static final String LDAP_USE_STARTTLS = "ldap.useStartTLS";
	private static final Logger log = LH.get();
	private String address;
	private Integer port;
	private String bindDN;
	private String pw;
	private String baseDN;
	
	private LDAPConnectionPool connectionPool;
	private LDAPConnectionPool userConnectionPool;
	private String userAttributeName;
	
	//Skips all verification
	@NotMutable()
	@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
	public final static class SkipHostNameSSLSocketVerifier
	       extends SSLSocketVerifier
	       implements HostnameVerifier {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}

		@Override
		public void verifySSLSocket(String host, int port, SSLSocket sslSocket) throws LDAPException {
			//Do nothing
		}
	}

	public void initConnect(ContainerTools props) {
		this.address = props.getRequired(LDAP_ADDRESS_PROP);
		this.port = Caster_Integer.INSTANCE.cast(props.getRequired(LDAP_PORT_PROP));
		this.bindDN = props.getRequired(LDAP_BIND_DN_PROP);
		this.pw = props.getRequired(LDAP_BIND_PW_PROP);
		this.baseDN = props.getRequired(LDAP_BASE_DN_PROP);
		this.userAttributeName = props.getRequired(LDAP_USER_ATTR_NAME_PROP);

		boolean useSSL = props.getOptional(LDAP_USE_SSL, false);
		boolean useStartTLS = props.getOptional(LDAP_USE_STARTTLS, false);
		
		LDAPConnection rootConnection;
		LDAPConnection userConnection;
		
		try {
			if (useSSL) {
				final String trustStorePath = props.getOptional(LDAP_SSL_TRUSTSTORE_PATH);
				String trustStorePIN = props.getOptional(LDAP_SSL_TRUSTSTORE_PIN);
				String trustStoreFormat = SH.toLowerCase(props.getOptional(LDAP_SSL_TRUSTSTORE_FORMAT));
				
				AggregateTrustManager trustManager = null; 
				 
				if (SH.is(trustStorePath)){
					if (SH.equals("all", trustStoreFormat)) {
						trustManager = new AggregateTrustManager(false, 
								JVMDefaultTrustManager.getInstance(),
								new TrustAllTrustManager());
					} else if (SH.equals("pem", trustStoreFormat)) {
						File pemFile = new File(trustStorePath);
						trustManager = new AggregateTrustManager(false, 
								JVMDefaultTrustManager.getInstance(),
								new PEMFileTrustManager(pemFile));
					} else {
						trustManager = new AggregateTrustManager(false, 
							JVMDefaultTrustManager.getInstance(),
							new TrustStoreTrustManager(trustStorePath, 
								SH.is(trustStorePIN) ? trustStorePIN.toCharArray() : null,
								trustStoreFormat, true));
					}
				} else {
					log.info("Defaulting to JVM default trust manager");
					trustManager = new AggregateTrustManager(false, JVMDefaultTrustManager.getInstance());
				}
				        
				SSLUtil sslUtil = new SSLUtil(trustManager);
				final SSLSocketFactory factory = sslUtil.createSSLSocketFactory();

				LDAPConnectionOptions connectionOptions = new LDAPConnectionOptions();
				boolean validateHostname = props.getOptional(LDAP_SSL_VALIDATE_HOSTNAME, true);
				if (!validateHostname)
					connectionOptions.setSSLSocketVerifier(new SkipHostNameSSLSocketVerifier());
//				connectionOptions.setSSLSocketVerifier(new HostNameSSLSocketVerifier(validateHostname));

				rootConnection = new LDAPConnection(factory, connectionOptions,
						address, port, bindDN, pw);
				userConnection = new LDAPConnection(factory, connectionOptions, address, port);
				
				if (useStartTLS) {
					StartTLSExtendedRequest startTLSRequest = new StartTLSExtendedRequest(factory);
					ExtendedResult rootStartTLSResult, userStartTLSResult;
					try {
						rootStartTLSResult = rootConnection.processExtendedOperation(startTLSRequest);
					} catch (LDAPException e) {
						rootStartTLSResult = new ExtendedResult(e);						
					}
					if (rootStartTLSResult.getResultCode() != ResultCode.SUCCESS)
						log.warning("Failed to initialize connection with startTLS: " + rootStartTLSResult.toString());
					
					try {
						userStartTLSResult = userConnection.processExtendedOperation(startTLSRequest);
					} catch (LDAPException e) {
						userStartTLSResult = new ExtendedResult(e);
					}
					if (userStartTLSResult.getResultCode() != ResultCode.SUCCESS)
						log.warning("Failed to initialize connection with startTLS: " + userStartTLSResult.toString());

				}

			} else {				
				rootConnection = new LDAPConnection(address, port, bindDN, pw);
				userConnection = new LDAPConnection(address, port);
			}
			
			final int maxConnection = props.getOptional(LDAP_MAX_CONNECTIONS, 10);
			final int initialConnection = props.getOptional(LDAP_INITIAL_CONNECTIONS, 5);
			this.userConnectionPool = new LDAPConnectionPool(userConnection, initialConnection, maxConnection);
			this.connectionPool = new LDAPConnectionPool(rootConnection, initialConnection, maxConnection);
			LH.info(log, "Connected to: ", rootConnection.getConnectedAddress(), ':', rootConnection.getConnectedPort());
		} catch (LDAPException le) {
			ResultCode resultCode = le.getResultCode();
			LH.warning(log, "Error connecting to the directory server: ", le);
			LH.warning(log, "Result code: ", resultCode.intValue(), " (", resultCode.getName(), ")");
		} catch (GeneralSecurityException e) {
			LH.warning(log, "Failed to create SSL Socket Factory: " + e.getMessage());
		}
	}

	public void rebind(LDAPConnection connection) {
		try {
			connection.bind(bindDN, pw);
		} catch (LDAPException le) {
			ResultCode resultCode = le.getResultCode();
			LH.warning(log, "Error rebinding connection: ", le);
			LH.warning(log, "Result code: ", resultCode.intValue(), " (", resultCode.getName(), ")");
		}
	}

	public String getUserAttr(LDAPConnection connection, String search, String resultField) {
		String result = "";
		Filter filter = Filter.createEqualityFilter(this.userAttributeName, search);
		SearchRequest searchReq = new SearchRequest(this.baseDN, SearchScope.SUB, filter, resultField);
		try {
			SearchResult searchRes = connection.search(searchReq);
			List<SearchResultEntry> searchEntries = searchRes.getSearchEntries();
			LH.info(log, "Search Entries found: " + searchEntries.size() + " for " + search);

			for (SearchResultEntry entry : searchEntries) {
				if ("dn".equals(resultField)) {
					result = entry.getDN().toString();
				} else {
					result = entry.getAttributeValue(resultField);
				}
			}
		} catch (LDAPSearchException e) {
			final ResultCode resultCode = e.getResultCode();
			LH.warning(log, "Error fulfilling search request: ", e);
			LH.warning(log, "Result code: ", resultCode.intValue(), " (", resultCode.getName(), ")");
			LH.warning(log,  "Diagnostic Message: " + e.getDiagnosticMessage());
			LH.warning(log,  "Matched DN: " + e.getMatchedDN());
			LH.warning(log, "Connection information: " + connection.toString());
		}

		return result;
	}
	
	public String getBindDN() {
		return this.bindDN;
	}
	
	public LDAPConnectionPool getAdminPool() {
		return this.connectionPool;
	}
	
	public LDAPConnectionPool getUserPool() {
		return this.userConnectionPool;
	}
}