package com.f1.http.handler;

import java.io.IOException;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpUtils;
import com.f1.utils.FastPrintStream;
import com.f1.utils.GuidHelper;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class ErrorHttpHandler extends AbstractHttpHandler {
	private static Logger log = LH.get();

	@Override
	public void handle(HttpRequestResponse httpRequestResponse) throws IOException {
		super.handle(httpRequestResponse);
		Exception exception = (Exception) httpRequestResponse.getAttributes().get("exception");
		FastPrintStream out = httpRequestResponse.getOutputStream();
		out.println("<html><body>");
		out.println("<span style='color:blue'>3Forge Ultrafast webserver V2.0 </span><P>Error code <B><span style='color:red'>" + httpRequestResponse.getResponseType()
				+ "</B></span><P>For request url: <i>" + httpRequestResponse.getRequestUri() + "</i><P><pre>");
		final String ticket = "TT-" + SH.substring(GuidHelper.getGuid(62), 0, 10);
		if (exception != null) {
			LH.warning(log, "Error Ticket - ", ticket, exception);
			out.println(HttpUtils.escapeHtml(OH.noNull(exception.getMessage(), "")));
			out.println("<BR>Reference ticket: ");
			out.println(ticket);
		}
		out.println("</pre>");
		out.println("</html></body>");
	}
}
