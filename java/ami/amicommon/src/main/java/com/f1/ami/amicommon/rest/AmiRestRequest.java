package com.f1.ami.amicommon.rest;

import com.f1.http.HttpRequestResponse;
import com.f1.utils.ContentType;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AmiRestRequest {

	private HttpRequestResponse req;
	private boolean hasError;

	public AmiRestRequest(HttpRequestResponse req) {
		this.req = req;
	}

	public void println(String string) {
		req.getOutputStream().println(string);
	}
	public void printJson(Object string) {
		println(ObjectToJsonConverter.INSTANCE_COMPACT_SORTING.objectToString(string));
		req.setContentTypeAsBytes(ContentType.JSON.getMimeTypeAsBytes());
	}

	public String getParam(String string) {
		return req.getParams().get(string);
	}

	public void error(String string) {
		println(string);
		req.setResponseType(req.HTTP_400_BAD_REQUEST);
		this.hasError = true;
	}

	public boolean isDisplayText() {
		String mode = req.getParams().get("display");
		return mode == null || "text".equals(mode);
	}

	public int getParamInt(String string, int dflt) {
		String r = getParam(string);
		if (r == null)
			return dflt;
		try {
			return SH.parseInt(r);
		} catch (Exception e) {
			error("Param " + string + " must be an integer");
			return -1;
		}
	}

	public boolean hasError() {
		return this.hasError;
	}

	public void setContentType(ContentType text) {
		req.setContentTypeAsBytes(text.getMimeTypeAsBytes());
	}

	public HttpRequestResponse getInnerRequest() {
		return this.req;
	}

}
