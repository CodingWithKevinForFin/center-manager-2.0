package com.f1.ami.plugins.snowflake;

import java.io.FileReader;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.Security;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;

import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Row;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiSnowflakeDatasourceAdapter extends JdbcAdapter {
	private static final Logger log = LH.get();
	private static final String JDBC_DRIVER_CLASS = "net.snowflake.client.jdbc.SnowflakeDriver";
	private static final String JDBC_URL_SUBPROTOCOL = "jdbc:snowflake://";

	// Need to put this in your properties
	private static final String PROPERTY_SNOWFLAKE_PRIVATEKEY_FILE = "snowflake.privatekey.file";
	private static final String PROPERTY_SNOWFLAKE_PRIVATEKEY_PASSPHRASE = "snowflake.privatekey.passphrase";

	// This goes in Datasource Advanced Options
	private static final String OPTION_ALLOW_UNDERSCORES_IN_HOST = "allowUnderscoresInHost";
	private static final String OPTION_AUTHENTICATOR = "authenticator";
	private static final String OPTION_PASSCODE = "passcode";
	private static final String OPTION_PASSCODE_IN_PASSWORD = "passcodeInPassword";
	private static final String OPTION_PASSWORD = "password";
	private static final String OPTION_PRIVATE_KEY = "privatekey";
	private static final String OPTION_PRIVATE_KEY_FILE = "private_key_file";
	private static final String OPTION_PRIVATE_KEY_FILE_PWD = "private_key_file_pwd";
	private static final String OPTION_TOKEN = "token";
	private static final String OPTION_DB = "db";
	private static final String OPTION_ROLE = "role";
	private static final String OPTION_SCHEMA = "schema";
	private static final String OPTION_WAREHOUSE = "warehouse";
	private static final String OPTION_JDBC_OPTIONS = "jdbcOptions";

	@Override
	public void init(ContainerTools tools, AmiServiceLocator locator) throws AmiDatasourceException {
		super.init(tools, locator);
	}
	/*
	 * TODO: Note: build options should have suggested values
	 * TODO: Note: build options should be split into categories jdbc, jdbc.auth, ami
	 */
	//This is the help, TODO: update the help
	public static Map<String, String> buildOptions() {
		Map<String, String> r = JdbcAdapter.buildOptions();

		r.put(OPTION_ALLOW_UNDERSCORES_IN_HOST, "Specifies whether to allow underscores in account names, options: true,false, default false");
		r.put(OPTION_AUTHENTICATOR, "Specifies the authenticator");
		r.put(OPTION_PASSCODE, "Specifies the passcode for mfa");
		r.put(OPTION_PASSCODE_IN_PASSWORD, "Specifies the passcode for mfa is appended to password, options: on,off, default off");
		//		r.put(OPTION_PASSWORD, ""); // SKIP password
		r.put(OPTION_PRIVATE_KEY, "Use private key as configured by properties: snowflake.privatekey.file and snowflake.privatekey.passphrase, options:true/false, default false");
		r.put(OPTION_PRIVATE_KEY_FILE, "Specifies the path to the private key file");
		r.put(OPTION_PRIVATE_KEY_FILE_PWD, "Specifies the password to the private key file");
		r.put(OPTION_TOKEN, "Specifies the OAuth token");
		r.put(OPTION_DB, "Specifies the database, ex: SNOWFLAKE_SAMPLE_DATA");
		r.put(OPTION_ROLE, "Specifies the default access role, ex SYSADMIN");
		r.put(OPTION_SCHEMA, "Specifies the default schema, ex: TPCH_SF1");
		r.put(OPTION_WAREHOUSE, "Specifies the default warehouse, ex: COMPUTE_WH");
		r.put(OPTION_JDBC_OPTIONS, "Additional JDBC properties: key=value,key2=value2...");
		return r;
	}

	/*
	 *  Sample JDBC Url: jdbc:snowflake://<account_identifier>.snowflakecomputing.com/?<connection_params>
	 */

	@Override
	protected StringBuilder createShowTablesQuery(StringBuilder sb, int limit) {
		String limitString = limit > 0 ? " LIMIT " + limit : "";
		sb.append("SHOW TABLES").append(limitString);
		return sb;
	}
	@Override
	protected long execCountQuery(StringBuilder sb, Connection conn, String fullname, int limit, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		return -1;
	}
	@Override
	protected AmiDatasourceTable createAmiDatasourceTable(Row row, StringBuilder sb) {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);

		//Get name of Table and the Collection(Schema,Owner) it's in
		String name = SH.trim(row.get("name", Caster_String.INSTANCE));
		String tableOwner = SH.trim(row.get("owner", Caster_String.INSTANCE));
		String tableCatalog = SH.trim(row.get("database_name", Caster_String.INSTANCE)); //db
		String tableSchema = SH.trim(row.get("schema_name", Caster_String.INSTANCE)); //schema
		String tableType = SH.trim(row.get("kind", Caster_String.INSTANCE));
		long rowsCount = row.get("rows", Caster_Long.INSTANCE);

		//					if ("SYSTEM".equals(tablespace))
		//						continue;

		//End
		table.setName(name);
		table.setCollectionName(SH.join('.', tableCatalog, tableSchema));

		//Set Custom Query 
		table.setCustomQuery("SELECT * FROM " + //
				SH.join('.', SH.doubleQuote(tableCatalog), SH.doubleQuote(tableSchema), SH.doubleQuote(name))//
				+ " WHERE ${WHERE}");
		table.setPreviewTableSize(rowsCount);

		String fullname = getSchemaName(SH.clear(sb), table);
		createSelectQuery(sb, fullname);
		table.setCustomQuery(SH.toStringAndClear(sb));
		return table;
	}

	@Override
	protected String getSchemaName(StringBuilder sb, AmiDatasourceTable table) {
		//		return getSchemaName('.', '"', sb, table.getName(), table.getCollectionName());
		String fqtn = getQFQTN(table.getCollectionName(), table.getName());
		return fqtn;
	}
	/*
	 * Get Quoted Fully Qualified Table Name
	 */
	private final static String getQFQTN(String collectionName, String name) {
		String[] split = SH.split('.', collectionName);

		for (int i = 0; i < split.length; i++)
			split[i] = SH.doubleQuote(split[i]);

		return SH.join('.', split) + '.' + SH.doubleQuote(name);
	}
	@Override
	protected StringBuilder createPreviewQuery(StringBuilder sb, String fullname, int limit) {
		String limitString = limit > 0 ? " LIMIT " + limit : "";
		sb.append("SELECT * FROM ").append(fullname);
		sb.append(limitString);

		return sb;
	}

	@Override
	protected String buildJdbcDriverClass() {
		return JDBC_DRIVER_CLASS;
	}

	public static class PrivateKeyReader {

		private static final String getPrivateKeyPassphrase(ContainerTools tools) {
			return tools.getRequired(PROPERTY_SNOWFLAKE_PRIVATEKEY_PASSPHRASE);
		}
		private static final String getPrivateKeyFile(ContainerTools tools) {
			return tools.getRequired(PROPERTY_SNOWFLAKE_PRIVATEKEY_FILE);
		}

		public static PrivateKey get(ContainerTools tools) throws Exception {
			PrivateKeyInfo privateKeyInfo = null;
			Security.addProvider(new BouncyCastleProvider());

			// Read an object from the private key file.
			String filename = getPrivateKeyFile(tools);
			PEMParser pemParser = new PEMParser(new FileReader(Paths.get(filename).toFile()));
			Object pemObject = pemParser.readObject();
			if (pemObject instanceof PKCS8EncryptedPrivateKeyInfo) {
				// Handle the case where the private key is encrypted.
				PKCS8EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = (PKCS8EncryptedPrivateKeyInfo) pemObject;
				String passphrase = getPrivateKeyPassphrase(tools);
				InputDecryptorProvider pkcs8Prov = new JceOpenSSLPKCS8DecryptorProviderBuilder().build(passphrase.toCharArray());
				privateKeyInfo = encryptedPrivateKeyInfo.decryptPrivateKeyInfo(pkcs8Prov);
			} else if (pemObject instanceof PrivateKeyInfo) {
				// Handle the case where the private key is unencrypted.
				privateKeyInfo = (PrivateKeyInfo) pemObject;
			}
			pemParser.close();
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
			return converter.getPrivateKey(privateKeyInfo);
		}
	}

	@Override
	protected Map<String, Object> buildJdbcArguments() {
		// TODO Auto-generated method stub
		Map<String, Object> r = CH.m("user", getUsernameEncoded() //
		);
		if (SH.is(this.getPasswordEncoded()))
			r.put(OPTION_PASSWORD, this.getPasswordEncoded());

		//		r.put(OPTION_PRIVATE_KEY, "");
		//		r.put(OPTION_PRIVATE_KEY_FILE, "");
		//		r.put(OPTION_PRIVATE_KEY_FILE_PWD, "");
		putBuildOption(OPTION_ALLOW_UNDERSCORES_IN_HOST, r);
		putBuildOption(OPTION_AUTHENTICATOR, r);
		putBuildOption(OPTION_PASSCODE, r);
		putBuildOption(OPTION_PASSCODE_IN_PASSWORD, r);
		putBuildOption(OPTION_TOKEN, r);
		putBuildOption(OPTION_DB, r);
		putBuildOption(OPTION_ROLE, r);
		putBuildOption(OPTION_SCHEMA, r);
		putBuildOption(OPTION_WAREHOUSE, r);

		//TODO: private key
		if (SH.equalsIgnoreCase("true", this.getOption(OPTION_PRIVATE_KEY, ""))) {
			try {
				r.put(OPTION_PRIVATE_KEY, PrivateKeyReader.get(this.tools));
			} catch (Exception e) {
				LH.warning(log, "Unable to load the private key while building options: ", e);
			}
		}
		putBuildOption(OPTION_PRIVATE_KEY_FILE, r);
		putBuildOption(OPTION_PRIVATE_KEY_FILE_PWD, r);

		// Put any options that haven't been added
		r.putAll(SH.splitToMap(',', '=', '\\', getOption(OPTION_JDBC_OPTIONS, "")));

		return r;
	}
	private void putBuildOption(String optionName, Map sink) {
		Set<String> options = this.getOptions();
		if (options.contains(optionName)) {
			String option = this.getOption(optionName, "");
			if (SH.is(option))
				sink.put(optionName, option);
		}
	}
	/*
	 * If default value is necessary
	 */
	private void putBuildOption(String optionName, Map sink, String defaultValue) {
		String option = this.getOption(optionName, defaultValue);
		sink.put(optionName, option);
	}

	@Override
	protected String buildJdbcUrlSubprotocol() {
		return JDBC_URL_SUBPROTOCOL;
	}

	/*
	 * Sample jdbc url: <account_identifier>.snowflakecomputing.com/?<connection_params>
	 */
	@Override
	protected String buildJdbcUrl() {
		return getUrl();
	}

	@Override
	protected String buildJdbcUrlPassword() {
		return getPasswordEncoded();
	}

}
