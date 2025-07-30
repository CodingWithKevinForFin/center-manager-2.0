package com.f1.ami.relay.fh;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.AmiRelayServer;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;

public abstract class AmiFHBase implements AmiFH {

	public static String PROP_AMI_ID = "amiId";
	public static String PROP_AMI_TABLE_MAP = "amiTableMap";

	private static final Logger log = LH.get();
	protected static final byte[] EMPTY_PARAMS = OH.EMPTY_BYTE_ARRAY;

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	protected PropertyController sysProps;
	private int status = STATUS_STOPPED;
	protected PropertyController props;
	private int id;
	private AmiRelayIn amiServer;
	private long conTime;
	private int remotePort = 0;
	private String remoteIp = "localhost";
	private boolean loggedin = false;

	private String appId;

	private String statusReason;

	private String name;
	private Map<String, String> amiTableMap;

	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		this.id = id;
		this.sysProps = sysProps;
		this.props = props;
		this.amiServer = amiServer;
		this.name = name;
		String amiTableMapString = this.props.getOptional(PROP_AMI_TABLE_MAP);
		this.amiTableMap = SH.isnt(amiTableMapString) ? null : SH.splitToMap(',', '=', amiTableMapString);

		setAppId(this.props.getOptional(PROP_AMI_ID, this.name));
	}

	@Override
	public void start() {
		setStatus(STATUS_STARTING);
	}

	private void setStatus(int status) {
		setStatus(status, "");
	}

	private void setStatus(int status, String reason) {
		statusReason = reason;
		int ps = this.status;
		this.status = status;

		pcs.firePropertyChange(PCE_STATUS_CHANGED, ps, this.status);
	}

	protected void onStartFinish(boolean success) {
		setStatus(success ? STATUS_STARTED : STATUS_START_FAILED);
	}

	@Override
	public void stop() {
		setStatus(STATUS_STOPPING);
	}

	protected void onStopFinish(boolean success) {
		setStatus(success ? STATUS_STOPPED : STATUS_STOP_FAILED);
	}

	protected void onFailed(String reason) {
		setStatus(STATUS_FAILED, reason);
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public String getStatusReason() {
		return statusReason;
	}

	final public int getId() {
		return id;
	}

	protected AmiRelayIn getManager() {
		return amiServer;
	}

	@Override
	public long getConnectionTime() {
		return conTime;
	}

	protected void setConnectionTime(long conTime) {
		this.conTime = conTime;
	}

	@Override
	public int getRemotePort() {
		return remotePort;
	}

	protected void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	@Override
	public String getRemoteIp() {
		return remoteIp;
	}

	protected void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

	protected AmiRelayIn getAmiRelayIn() {
		return getManager();
	}

	@Override
	public String getAppId() {
		return appId;
	}

	final protected void setAppId(String appId) {
		this.appId = appId;
	}

	final public String getName() {
		return this.name;
	}

	protected void login() {
		if (loggedin)
			return;

		log.info("Connecting/Logging into ami relay");

		getAmiRelayIn().onLogin(null, null, EMPTY_PARAMS);

		log.info("Connected/Logged into ami - successfully");

		loggedin = true;
	}

	protected void logout() {
		if (!loggedin)
			return;

		log.info("Attempting to logout from ami");
		getAmiRelayIn().onLogout(EMPTY_PARAMS, true);

		loggedin = false;
	}

	protected String encodeString(String s) {
		if (s == null)
			return s;
		return "\"" + s;
	}

	protected String encodeEnum(String s) {
		if (s == null)
			return s;
		return "'" + s;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		this.pcs.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		this.pcs.removePropertyChangeListener(l);
	}

	@Override
	public void call(AmiRelayServer server, AmiRelayRunAmiCommandRequest action, StringBuilder errorSink) {
	}

	private ArrayList<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
	private ArrayList<String> stringList = new ArrayList<String>();

	protected List<String> from(String s) {
		ArrayList<String> l = new ArrayList<String>(1);
		l.add(s);
		return l;
	}

	public String getDescription() {
		return this.name;
	}

	@Override
	public boolean onAck(long seqnum) {
		return true;
	}
	@Override
	public void onCenterConnected(String centerId) {
	}

	public String getAmiTableName(String sourceTableName) {
		if (CH.isEmpty(this.amiTableMap))
			return sourceTableName;
		String amiTableName = this.amiTableMap.get(sourceTableName);
		if (SH.is(amiTableName))
			return amiTableName;
		else
			return sourceTableName;
	}

	public void publishObjectToAmi(long seqNum, String I_id, String tableName, long E_expiresOn, byte[] encodedMap) {
		getAmiRelayIn().onObject(seqNum, I_id, this.getAmiTableName(tableName), E_expiresOn, encodedMap);
	}

	public void publishObjectDeleteToAmi(long origSeqnum, String I_ids, String tableName, byte[] encodedMaps) {
		getAmiRelayIn().onObjectDelete(origSeqnum, I_ids, this.getAmiTableName(tableName), encodedMaps);
	}
}
