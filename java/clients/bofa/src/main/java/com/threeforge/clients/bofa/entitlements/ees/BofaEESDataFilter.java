package com.threeforge.clients.bofa.entitlements.ees;

import java.util.List;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebObject;
import com.f1.ami.web.datafilter.AmiWebDataFilter;
import com.f1.ami.web.datafilter.AmiWebDataFilterQuery;
import com.f1.ami.web.datafilter.AmiWebDataSession;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.structs.table.columnar.ColumnarTable;

public class BofaEESDataFilter implements AmiWebDataFilter {

	private static Logger log = LH.get();
	public static String ENTITLEMENT_TYPE_GROUP = "GROUP";
	public static String ENTITLEMENT_TYPE_ROLE = "ROLE";
	private boolean debug;
	private String username;
	private String host;
	private String port;
	private String database;
	private String dbUser;
	private String dbPassword;
	private String dbTableName;
	private List<String> dbTableColumns;
	private List<String> permittedValues;
	private String entitlementType;
	private EESSession eesSession;
	private int pollingIntervalSeconds;
	private Thread thread;

	public BofaEESDataFilter(AmiWebDataSession session, PropertyController props) {

		this.debug = props.getOptional(BofaEESProperties.PROPERTY_BOFA_EES_DEBUG, Boolean.FALSE);
		this.username = session.getUsername();
		this.host = props.getRequired(BofaEESProperties.PROPERTY_BOFA_EES_HOST);
		this.port = props.getRequired(BofaEESProperties.PROPERTY_BOFA_EES_PORT);
		this.database = props.getRequired(BofaEESProperties.PROPERTY_BOFA_EES_DATASOURCE);
		this.dbUser = props.getRequired(BofaEESProperties.PROPERTY_BOFA_EES_DB_USERNAME);
		this.dbPassword = props.getRequired(BofaEESProperties.PROPERTY_BOFA_EES_DB_PASSWORD);
		this.dbTableName = props.getRequired(BofaEESProperties.PROPERTY_BOFA_EES_DB_TABLE);
		this.entitlementType = props.getRequired(BofaEESProperties.PROPERTY_BOFA_EES_TYPE);
		String permittedValsStr = props.getRequired(BofaEESProperties.PROPERTY_BOFA_EES_PERMITTED_VALUES);
		this.permittedValues = SH.splitToList(",", permittedValsStr);
		this.pollingIntervalSeconds = Caster_Integer.PRIMITIVE.cast(props.getRequired(BofaEESProperties.PROPERTY_BOFA_EES_POLLING_INTERVAL_SECONDS));

		// force kickoff of the thread
		this.eesSession = new EESSession(username, host, port, database, dbUser, dbPassword, dbTableName, dbTableColumns, pollingIntervalSeconds, debug);
		this.eesSession.runInner();
		session.putVariable("__BofaEES", eesSession, EESSession.class);

		// polling
		this.thread = new Thread(eesSession, "EES Polling");
		thread.setDaemon(false);
		thread.start();
		LH.info(log, "Successfully spawned thread for EES session");

		LH.info(log, "Processing entitlements for " + this.username);
		List<String> data = null;
		if (ENTITLEMENT_TYPE_GROUP.equals(this.entitlementType))
			data = this.eesSession.getGroups();
		else if (ENTITLEMENT_TYPE_ROLE.equals(this.entitlementType))
			data = this.eesSession.getRoles();

		boolean valueFound = false;
		if (data == null) {
			LH.warning(log, "Could not fetch any " + this.entitlementType + " for the user " + this.username);
		} else {
			for (String item : data) {
				if (this.permittedValues.contains(item)) {
					valueFound = true;
					break;
				}
			}
		}
		if (!valueFound) {
			session.logout();
			shutdown();
			formatErrorMessage();
		} else {
			LH.info(log, "Sucessfully processed entitlements for " + this.username);
		}

	}
	public String formatErrorMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("<h3>You are not currently entitled to this application.  Please request one of the permitted values below in ARM:</h3>");
		for (String val : this.permittedValues)
			sb.append("<br>" + val);
		return sb.toString();
	}
	private void shutdown() {
		this.eesSession.setRunning(false);
		this.thread.interrupt();
	}
	public void onLogin() {

	}
	public void onLogout() {
		LH.info(log, this.username + " logging out. Closing EESSession.");
		shutdown();
	}
	public byte evaluateNewRow(AmiWebObject realtimeRow) {
		return SHOW_ALWAYS;
	}
	public byte evaluateUpdatedRow(AmiWebObject realtimeRow, byte currentStatus) {
		return SHOW_ALWAYS;
	}
	public void evaluateQueryResponse(AmiWebDataFilterQuery query, ColumnarTable table) {
	}
	public AmiWebDataFilterQuery evaluateQueryRequest(AmiWebDataFilterQuery query) {
		return query;
	}

}
