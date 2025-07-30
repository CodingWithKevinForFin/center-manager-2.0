package com.threeforge.clients.bofa.entitlements.ges;

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

public class BofaGESDataFilter implements AmiWebDataFilter {
	public static String ENTITLEMENT_TYPE_GROUP = "GROUP";
	public static String ENTITLEMENT_TYPE_ROLE = "ROLE";
	private static Logger log = LH.get();
	private boolean debug;
	private String clientId;
	private String clientKey;
	private String namespace;
	private List<String> permittedValues;
	private String url;
	private String entitlementType; // group or role
	private String username;

	private GESSession gesSession;
	private int pollingIntervalSeconds;
	private Thread thread;

	public BofaGESDataFilter(AmiWebDataSession session, PropertyController props) {
		this.debug = props.getOptional(BofaGESProperties.PROPERTY_BOFA_GES_DEBUG, Boolean.FALSE);
		this.clientId = props.getRequired(BofaGESProperties.PROPERTY_BOFA_GES_CLIENT_ID);
		this.clientKey = props.getRequired(BofaGESProperties.PROPERTY_BOFA_GES_CLIENT_KEY);
		this.namespace = props.getRequired(BofaGESProperties.PROPERTY_BOFA_GES_NAMESPACE);
		this.url = props.getRequired(BofaGESProperties.PROPERTY_BOFA_GES_URL);
		String permittedValuesStr = props.getRequired(BofaGESProperties.PROPERTY_BOFA_GES_PERMITTED_VALUES);
		this.pollingIntervalSeconds = Caster_Integer.PRIMITIVE.cast(props.getRequired(BofaGESProperties.PROPERTY_BOFA_GES_POLLING_INTERVAL_SECONDS));
		this.permittedValues = SH.splitToList(",", permittedValuesStr);
		this.entitlementType = SH.toUpperCase(props.getRequired(BofaGESProperties.PROPERTY_BOFA_GES_TYPE));

		this.username = session.getUsername();
		// Polling based GES session to use inside the dashboard
		this.gesSession = new GESSession(this.url, this.clientId, this.clientKey, this.username, this.namespace, this.pollingIntervalSeconds, this.entitlementType, this.debug);
		this.gesSession.runInner();
		session.putVariable("__BofaGES", gesSession, GESSession.class);
		this.thread = new Thread(gesSession, "GESSession-" + session.getUsername());
		thread.setDaemon(false);
		thread.start();
		LH.info(log, "Successfully spawned thread for GES session");

		LH.info(log, "Processing entitlements for " + this.username);

		List<String> data = null;
		if (ENTITLEMENT_TYPE_GROUP.equals(this.entitlementType))
			data = this.gesSession.getGroups();
		else if (ENTITLEMENT_TYPE_ROLE.equals(this.entitlementType))
			data = this.gesSession.getRoles();

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
			LH.warning(log, formatErrorMessage());
			shutdown();
			session.logout();
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
	public void onLogout() {
		LH.info(log, this.username + " logging out. Closing GESSession.");
		this.shutdown();
	}
	public void shutdown() {
		this.gesSession.setRunning(false);
		this.thread.interrupt();
	}
	public void onLogin() {
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
