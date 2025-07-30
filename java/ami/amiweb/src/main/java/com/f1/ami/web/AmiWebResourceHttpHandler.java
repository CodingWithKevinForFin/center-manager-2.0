package com.f1.ami.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiCenterGetResourceRequest;
import com.f1.ami.amicommon.msg.AmiCenterGetResourceResponse;
import com.f1.ami.amicommon.msg.AmiCenterResource;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultActionFuture;
import com.f1.container.ResultMessage;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.suite.web.WebStatesManager;
import com.f1.suite.web.WebUser;
import com.f1.utils.ContentType;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class AmiWebResourceHttpHandler extends AbstractHttpHandler {

	private static final Logger log = LH.get();
	private RequestOutputPort<AmiCenterGetResourceRequest, AmiCenterGetResourceResponse> port;
	private long resourceTimeoutMillis;
	private AmiWebGlobalResourceCache globalResourceCache;

	public AmiWebResourceHttpHandler(RequestOutputPort<AmiCenterGetResourceRequest, AmiCenterGetResourceResponse> port, AmiWebGlobalResourceCache globalResourceCache) {
		this.port = port;
		this.globalResourceCache = globalResourceCache;
	}

	@Override
	public void handle(HttpRequestResponse httpRequest) throws IOException {
		super.handle(httpRequest);
		String path = SH.decodeUrl(httpRequest.getRequestUri());
		if (!path.startsWith("/resources/")) {
			httpRequest.setResponseType(HttpRequestResponse.HTTP_404_NOT_FOUND);
			return;
		}
		path = SH.stripPrefix(path, "/resources/", true);
		final HttpSession session = httpRequest.getSession(false);

		WebStatesManager webState = WebStatesManager.getOrThrow(session);
		WebUser user = webState == null ? null : webState.getUser();
		if (user == null) {
			httpRequest.setResponseType(HttpRequestResponse.HTTP_401_UNAUTHORIZED);
			return;
		}
		ContentType typeByFileExtension = ContentType.getTypeByFileExtension(SH.afterLast(path, '.'), null);
		if (typeByFileExtension == null) {
			httpRequest.getOutputStream().print("Unknown content type for supplied URL");
			httpRequest.setResponseType(HttpRequestResponse.HTTP_400_BAD_REQUEST);
			return;
		}

		AmiWebResource rsc = this.globalResourceCache.get(path);
		if (rsc != null) {
			httpRequest.getOutputStream().write(rsc.getBytes());
			httpRequest.setContentTypeAsBytes(typeByFileExtension.getMimeTypeAsBytes());
			httpRequest.setResponseType(HttpRequestResponse.HTTP_200_OK);
			return;
		}

		final AmiCenterGetResourceRequest beRequest = port.nw(AmiCenterGetResourceRequest.class);
		ArrayList<String> l = new ArrayList<String>(1);
		l.add(path);
		beRequest.setPaths(l);
		ResultActionFuture<AmiCenterGetResourceResponse> future = port.requestWithFuture(beRequest, null);
		ResultMessage<AmiCenterGetResourceResponse> result = future.getResult(this.resourceTimeoutMillis);
		AmiCenterResource resource = null;
		try {
			AmiCenterGetResourceResponse action = result.getAction();
			resource = action.getResources().get(0);
		} catch (Exception e) {
			LH.info(log, "Error getting resource '", path, "'", e);
			httpRequest.setResponseType(HttpRequestResponse.HTTP_503_SERVICE_UNAVAILABLE);
			return;
		}
		if (resource == null || resource.getData() == null) {
			httpRequest.setResponseType(HttpRequestResponse.HTTP_404_NOT_FOUND);
		} else {
			httpRequest.getOutputStream().write(resource.getData());
			httpRequest.setContentTypeAsBytes(typeByFileExtension.getMimeTypeAsBytes());
			httpRequest.setResponseType(HttpRequestResponse.HTTP_200_OK);
		}
	}
}
