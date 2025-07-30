package com.threeforge.clients.wafra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import com.f1.bootstrap.ContainerBootstrap;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;

// NOTE Moved class to com.threeforge.clients.wafra from com.threeforge.wafra
public class WafraAmiJdbcClient {
	private static final int DEFAULT_TIMEOUT = 60000;
	private static final int DEFAULT_LIMIT = 10000;
	private static final Logger logger = LH.get();
	private static final String CLASS_NAME = "com.f1.ami.amidb.jdbc.AmiDbJdbcDriver";
	private final PropertyController props;
	private LinkedHashMap<String, String> queries;

	private boolean debug;
	private boolean debug_logRowsReturned;

	private String driverClassname;
	private String prefix;
	private String suffix;

	private String host;
	private String port;
	private String username;
	private boolean urlEncodedPassword;
	private String pwd;
	private int timeout;
	private int limit;

	public static void main(String args[]) throws SQLException, ClassNotFoundException {
		LH.info(logger, ErrorInfoCodes.I005);
		final ContainerBootstrap cam = new ContainerBootstrap(WafraAmiJdbcClient.class, args);
		String directory = "../config/";
		cam.setConfigDirProperty(directory);
		LH.info(logger, "Set config directory to " + directory);

		final PropertyController props = cam.getProperties();

		WafraAmiJdbcClient client = new WafraAmiJdbcClient(props);

		cam.startup();
		client.connect();

	}

	public WafraAmiJdbcClient(final PropertyController props) {
		this.props = props;
		this.initProperties(this.props);
	}

	public void initProperties(PropertyController props) {
		this.debug = props.getOptional("debug", false);
		this.debug_logRowsReturned = props.getOptional("debug.logRowsReturned", false);
		this.driverClassname = props.getOptional("driver_classname", CLASS_NAME);
		this.prefix = props.getOptional("url_prefix", "jdbc:amisql:");
		this.suffix = props.getOptional("url_suffix", "");
		this.host = props.getRequired("host");
		this.port = props.getRequired("port");
		this.username = props.getRequired("username");
		this.timeout = props.getOptional("timeout", DEFAULT_TIMEOUT);
		this.limit = props.getOptional("limit", DEFAULT_LIMIT);

		this.urlEncodedPassword = props.getOptional("urlEncodePassword", true);
		this.pwd = props.getOptional("pwd");
		if (this.pwd == null) {
			LH.warning(logger, ErrorInfoCodes.E003);
			this.pwd = props.getRequired("plaintext_pwd");
		}

		if (this.urlEncodedPassword)
			this.pwd = SH.encodeUrl(this.pwd);
		this.queries = new LinkedHashMap<String, String>();
		List<String> queriesList = SH.splitToList(",", props.getRequired("queries.list"));
		PropertyController subProps = props.getSubPropertyController("query.");
		LH.info(logger, "Queries to be run: ");
		for (String key : queriesList) {
			String query = subProps.getRequired(key, String.class);
			this.queries.put(key, query);
			LH.info(logger, "Query: " + key + " to be run: " + query);
		}
	}

	public void connect() {

		Connection conn = null;
		try {

			Class.forName(this.driverClassname);
			final String url = this.prefix + this.host + ":" + this.port + "?username=" + this.username + "&password=" + this.pwd + "&timeout=" + this.timeout + "&limit=" + limit
					+ suffix;
			LH.info(logger, ErrorInfoCodes.I007, url);
			try {
				conn = DriverManager.getConnection(url);
				if (conn.isClosed()) {
					LH.warning(logger, ErrorInfoCodes.E001);// E001 = "3FW-E001:Authentication Error, could not
															// establish a
															// valid connection to AMIDB";
				} else {
					LH.info(logger, ErrorInfoCodes.I001);
				}
			} catch (Exception e) {
				LH.warning(logger, ErrorInfoCodes.E001, " threw exception: ", e);// E001 = "3FW-E001:Authentication
				// Error, could not establish a
				// valid connection to AMIDB";
				return;
			}

			for (String key : this.queries.keySet()) {
				String query = this.queries.get(key);
				LH.info(logger, ErrorInfoCodes.I002, " : ", key + " : " + query);
				final ResultSet rs = conn.createStatement().executeQuery(query);

				if (this.debug && this.debug_logRowsReturned) {
					int count = 0;
					if (rs != null)
						while (rs.next()) {
							++count;
						}
					LH.info(logger, ErrorInfoCodes.I006, " : " + key + " : " + query + " returned " + count + " rows");
				}
			}

			LH.info(logger, ErrorInfoCodes.I002);
		} catch (SQLException e) {
			LH.warning(logger, ErrorInfoCodes.E004, e);
		} catch (Exception e) {
			LH.warning(logger, ErrorInfoCodes.E006, e);
		} finally {
			if (conn != null)
				try {
					conn.close();
					LH.info(logger, ErrorInfoCodes.I003);
				} catch (Exception e2) {
					LH.warning(logger, ErrorInfoCodes.E005, e2);
				}
		}
	}

}
