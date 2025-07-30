package com.f1.ami.center.jdbc;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.messaging.SimpleMessagingServerConnectionHandler;
import com.f1.ami.amicommon.messaging.SimpleMessagingServerConnectionHandlerFactory;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiCenterRequest;
import com.f1.ami.amicommon.msg.AmiCenterResponse;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.dialects.AmiDbDialectPlugin;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.container.ContainerTools;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultActionFuture;
import com.f1.container.ResultMessage;
import com.f1.container.exceptions.ContainerTimeoutException;
import com.f1.utils.LH;

public class AmiCenterJdbcServer implements SimpleMessagingServerConnectionHandlerFactory {

	private Logger log = LH.get();
	private final RequestOutputPort<AmiCenterRequest, AmiCenterResponse> itineraryPort;
	private AmiCenterState state;
	private AmiAuthenticatorPlugin authenticator;
	private Map<String, AmiDbDialectPlugin> dialects;
	private int defaultDatasourceTimeout = -1;
	private int jdbcProtocolVersion;

	public AmiCenterJdbcServer(AmiAuthenticatorPlugin authenticator, RequestOutputPort<AmiCenterRequest, AmiCenterResponse> ip, Map<String, AmiDbDialectPlugin> dialects,
			int jdbcProtocolVersion) {
		this.itineraryPort = ip;
		this.authenticator = authenticator;
		this.dialects = dialects;
		this.defaultDatasourceTimeout = AmiUtils.getDefaultTimeout(getTools());
		this.jdbcProtocolVersion = jdbcProtocolVersion;
	}

	@Override
	public SimpleMessagingServerConnectionHandler newHandler(String remoteAddress) {
		return new AmiCenterJdbcServerConnectionHandler(remoteAddress, this);
	}
	public AmiCenterQueryDsResponse sendToAmiState(String invokedBy, AmiCenterQueryDsRequest request) {
		request.setInvokedBy(invokedBy);
		request.setRequestTime(System.currentTimeMillis());
		ResultActionFuture<AmiCenterResponse> future = itineraryPort.requestWithFuture(request, null);
		int timeout = AmiUtils.toTimeout(request.getTimeoutMs(), this.defaultDatasourceTimeout);
		ResultMessage<AmiCenterResponse> response;
		if (timeout == 0) {
			LH.warning(log, "Zero timeout for query ");
			return wrap("AMI_JDBC_TIMEOUT_EXCEEDED(" + timeout + ")");
		}
		try {
			response = future.getResult(timeout);
		} catch (ContainerTimeoutException e) {
			LH.warning(log, "Timeout for query: ", e.getMessage());
			return wrap("AMI_JDBC_TIMEOUT_EXCEEDED(" + timeout + ")");
		}
		AmiCenterQueryDsResponse action = (AmiCenterQueryDsResponse) response.getAction();

		//		if (!action.getOk()) {
		//			Exception exception = action.getException();
		//			if (exception != null) {
		//				return wrap(null,exception);
		//			} else if (SH.is(action.getMessage()))
		//				return wrap(action.getMessage();
		//			else
		//				return "Unknown error";
		//		}
		return action;
	}
	private AmiCenterQueryDsResponse wrap(String string) {
		AmiCenterQueryDsResponse r = getTools().nw(AmiCenterQueryDsResponse.class);
		r.setMessage(string);
		r.setOk(false);
		return r;
	}

	public void sendToAmiStateNoResponse(String invokedBy, AmiCenterRequest request) {
		request.setInvokedBy(invokedBy);
		request.setRequestTime(System.currentTimeMillis());
		ResultActionFuture<AmiCenterResponse> future = itineraryPort.requestWithFuture(request, null);
	}
	public ContainerTools getTools() {
		return itineraryPort.getTools();
	}
	public AmiAuthenticatorPlugin getAuthenticator() {
		return authenticator;
	}

	public Map<String, AmiDbDialectPlugin> getDialects() {
		return this.dialects;
	}

	public int getJdbcProtocolVersion() {
		return jdbcProtocolVersion;
	}

}
