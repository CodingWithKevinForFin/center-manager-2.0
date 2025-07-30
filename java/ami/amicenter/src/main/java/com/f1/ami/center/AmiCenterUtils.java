package com.f1.ami.center;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterResponse;
import com.f1.ami.amicommon.msg.AmiRelayRequest;
import com.f1.ami.amicommon.msg.AmiRelayResponse;
import com.f1.ami.amicommon.msg.AmiRelayRunDbRequest;
import com.f1.ami.amiscript.AmiCalcFrameStack;
import com.f1.ami.center.AmiCenterDatasourceThreadSafeLookup.DatasourceRow;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.container.ResultMessage;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiCenterUtils {

	public static final String DB_SERVICE = "DB_SERVICE";
	public static final String FOUNTAIN_ID = "CENTER";
	public static final Map<String, String> NO_NULL = Collections.unmodifiableMap((Map) CH.m("NoNull", "true"));

	public static boolean verifyOk(Logger log, ResultMessage<? extends AmiRelayResponse> result) {
		AmiRelayResponse response = result.getAction();
		if (response == null) {
			LH.warning(log, "response is empty from agent: ",
					getProcessUidFromF1AppResponse(result) + ", request type: " + result.getRequestMessage().getAction().getClass().getName());
			return false;
		}
		if (!response.getOk()) {
			LH.warning(log, "response error from app: ", response.getMessage(), " agent processUid: " + getProcessUidFromF1AppResponse(result));
			return false;
		}
		return true;
	}
	static public String getProcessUidFromF1AppResponse(ResultMessage<? extends AmiRelayResponse> result) {
		AmiRelayRequest request = (AmiRelayRequest) result.getRequestMessage().getAction();
		return request.getTargetAgentProcessUid();
	}

	public static DerivedCellTimeoutController toTimeoutControl(AmiCenterState state, int timeout) {
		if (timeout == AmiCenterQueryDsRequest.NO_TIMEOUT)
			timeout = AmiCenterQueryDsRequest.MAX_TIMEOUT;
		else if (timeout == AmiCenterQueryDsRequest.USE_DEFAULT_TIMEOUT)
			timeout = state.getDefaultDatasourceTimeout();
		return new DerivedCellTimeoutController(timeout);
	}
	public static AmiRelayRunDbRequest toRunDbRequest_ThreadSafe(AmiCenterState state, AmiCenterQueryDsRequest request, long dsid, String dsname, AmiCenterResponse r,
			int timeout) {
		OH.assertNull(r.getMessage());
		final AmiCenterDatasourceThreadSafeLookup dsTable = state.getAmiImdb().getSystemSchema().__DATASOURCE.fastLookup;
		final DatasourceRow row;
		if (timeout == AmiCenterQueryDsRequest.NO_TIMEOUT)
			timeout = AmiCenterQueryDsRequest.MAX_TIMEOUT;
		else if (timeout == AmiCenterQueryDsRequest.USE_DEFAULT_TIMEOUT)
			timeout = state.getDefaultDatasourceTimeout();
		final String passOverride;
		if (request.getDatasourceOverridePasswordEnc() != null) {
			try {
				passOverride = state.decrypt(request.getDatasourceOverridePasswordEnc());
			} catch (Exception e) {
				r.setMessage("error decrypting DS_PASSWORD_ENC value");
				r.setOk(false);
				return null;
			}
		} else if (request.getDatasourceOverridePassword() != null)
			passOverride = request.getDatasourceOverridePassword().getPasswordString();
		else
			passOverride = null;
		if (SH.is(dsname)) {
			row = dsTable.getByName(dsname);
			if (row == null) {
				r.setMessage("Datasource not found: " + dsname);
				r.setOk(false);
				return null;
			}
		} else if (dsid > 0) {
			row = dsTable.getByAmiId(dsid);
			if (row == null) {
				r.setMessage("Datasource id not found: " + dsid);
				r.setOk(false);
				return null;
			}
		} else if (SH.is(request.getDatasourceOverrideAdapter() != null)) {
			if (!state.isAnonymousDatasourcesPermitted()) {
				r.setMessage("Permission Deinied, Anonymous datasources disabled (see " + AmiCenterProperties.PROPERTY_AMI_DB_ANONYMOUS_DATASOURCES_ENABLED + " property)");
				r.setOk(false);
				return null;
			}
			final AmiRelayRunDbRequest agentRequest = state.getTools().nw(AmiRelayRunDbRequest.class);
			agentRequest.setDsAdapter(request.getDatasourceOverrideAdapter());
			agentRequest.setDsRelayId(request.getDatasourceOverrideRelay());
			agentRequest.setDsAmiId(-1);
			agentRequest.setDsName(null);
			agentRequest.setDsPassword(passOverride);
			agentRequest.setDsUrl(request.getDatasourceOverrideUrl());
			agentRequest.setDsOptions(request.getDatasourceOverrideOptions());
			agentRequest.setDsUsername(request.getDatasourceOverrideUsername());
			agentRequest.setClientRequest(request);
			agentRequest.setInvokedBy(request.getInvokedBy());
			agentRequest.setTimeoutMs(timeout);
			return agentRequest;
		} else {
			r.setMessage("Datasource/Adapter not specified");
			r.setOk(false);
			return null;
		}
		final String name = row.getName();
		final String adap = row.getAdapter();
		final String url = getOverride(r, "URL", row, request.getDatasourceOverrideUrl(), row.getUrl());
		final String username = getOverride(r, "USERNAME", row, request.getDatasourceOverrideUsername(), row.getUsername());
		final String pass = getOverride(r, "PASSWORD", row, passOverride, row.getPassword());
		final String options = getOverride(r, "OPTIONS", row, request.getDatasourceOverrideOptions(), row.getOptions());
		final String relayId = getOverride(r, "RELAYID", row, request.getDatasourceOverrideRelay(), row.getRelayId());
		if (r.getMessage() != null)
			return null;
		final AmiRelayRunDbRequest agentRequest = state.getTools().nw(AmiRelayRunDbRequest.class);
		agentRequest.setDsAdapter(adap);
		agentRequest.setDsRelayId(relayId);
		agentRequest.setDsAmiId(row.getAmiId());
		agentRequest.setDsName(name);
		agentRequest.setDsPassword(pass);
		agentRequest.setDsUrl(url);
		agentRequest.setDsOptions(options);
		agentRequest.setDsUsername(username);
		agentRequest.setClientRequest(request);
		agentRequest.setInvokedBy(request.getInvokedBy());
		agentRequest.setTimeoutMs(timeout);
		return agentRequest;
	}
	private static String getOverride(AmiCenterResponse r, String key, DatasourceRow row, String override, String origValue) {
		if (r.getMessage() != null)
			return null;
		if (override == null)
			return origValue;
		if (row.getPermittedOverrides().contains(key))
			return override;
		r.setOk(false);
		r.setMessage("Permission Denied, datasource " + row.getName() + " does not allow overriding " + key);
		return null;
	}

	static public String getRelayStateProcessUid(AmiCenterState state, AmiRelayRunDbRequest req, AmiCenterResponse r) {
		String relayId = req.getDsRelayId();
		if (relayId == null)
			return null;
		Set<String> sink = state.getAmiImdb().getSystemSchema().__RELAY.relayId2ProcessUid_Threadsafe.get(relayId);
		if (sink == null || sink.size() == 0) {
			r.setMessage("Datasource '" + req.getDsName() + "' is attached to disconnected relay: " + relayId);
			r.setOk(false);
			return null;
		} else if (sink.size() > 1) {
			r.setMessage("Multiple Relays found with id: " + relayId);
			r.setOk(false);
			return null;
		}
		return CH.first(sink);
	}

	public static SqlPlanListener parseLoggingLevelOption(String description, String option) {
		if (SH.isnt(option) || "off".equalsIgnoreCase(option) || "quiet".equalsIgnoreCase(option))
			return null;
		else if ("on".equalsIgnoreCase(option))
			return new AmiCenterSqlPlanListener(description, false);
		else if ("verbose".equalsIgnoreCase(option))
			return new AmiCenterSqlPlanListener(description, true);
		else
			throw new RuntimeException("Logging option Expecting QUIET, OFF, ON, or VERBOSE");
	}
	public static byte getPermissions(PropertyController pc, AmiAuthUser user) {
		if (user != null) {
			String value = OH.toString(user.getAuthAttributes().get("AMIDB_PERMISSIONS"));
			if (value != null)
				return parsePermissions(value);
		}
		return parsePermissions(pc.getOptional(AmiCenterProperties.PROPERTY_AMI_DB_DEFAULT_PERMISSIONS, "READ,WRITE,ALTER,EXECUTE"));
	}

	private static byte parsePermissions(String optional) {
		return AmiUtils.parseDbPermissions(optional);
	}
	public static CharSequence toStringForPermissions(byte permissions) {
		StringBuilder sb = new StringBuilder();
		if (MH.anyBits(permissions, AmiCenterQueryDsRequest.PERMISSIONS_READ))
			SH.appendWithDelim(',', "READ", sb);
		if (MH.anyBits(permissions, AmiCenterQueryDsRequest.PERMISSIONS_WRITE))
			SH.appendWithDelim(',', "WRITE", sb);
		if (MH.anyBits(permissions, AmiCenterQueryDsRequest.PERMISSIONS_ALTER))
			SH.appendWithDelim(',', "ALTER", sb);
		if (MH.anyBits(permissions, AmiCenterQueryDsRequest.PERMISSIONS_EXECUTE))
			SH.appendWithDelim(',', "EXECUTE", sb);
		if (sb.length() == 0)
			return "NONE";
		return sb.toString();
	}
	/*
	 * GetService and GetSession returns the same
	 */
	static public AmiImdbSession getService(CalcFrameStack sf) {
		AmiCalcFrameStack ei = AmiUtils.getExecuteInstance2(sf);
		AmiImdbSession service = (AmiImdbSession) ei.getService();
		// Alternative
		//		AmiCenterTopCalcFrameStack top = (AmiCenterTopCalcFrameStack) sf.getTop();
		//		service = (AmiImdbSession) top.getService();
		return service;
	}
	static public AmiImdbSession getSession(CalcFrameStack sf) {
		return (AmiImdbSession) sf.getTop().getTableset();
	}
	static public AmiImdbSession getGlobalSession(CalcFrameStack sf) {
		AmiImdbSession service = AmiCenterUtils.getService(sf);
		return service.getImdb().getState().getGlobalSession();
	}
	static public AmiImdbSession getRtFeedSession(CalcFrameStack sf) {
		AmiImdbSession service = AmiCenterUtils.getService(sf);
		return service.getImdb().getState().getRtFeedSession();
	}
	static public AmiImdbSession getHdbSession(CalcFrameStack sf) {
		/*
		 * ???
		 */
		AmiImdbSession service = AmiCenterUtils.getService(sf);
		return service.getImdb().getGlobalSession();
	}
}
