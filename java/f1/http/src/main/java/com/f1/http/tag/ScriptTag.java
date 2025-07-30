package com.f1.http.tag;

import java.security.SecureRandom;
import java.util.Base64;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpTag;
import com.f1.http.handler.JspBuilderSession;
import com.f1.http.handler.JspTagBuilder;
import com.f1.utils.FastPrintStream;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class ScriptTag implements JspTagBuilder {

	@Override
	public void doSimple(JspBuilderSession session, HttpTag tag, int indent) {
		String src = "";
		try {
			src = tag.getRequired("src");
		} catch (Exception e) {
			System.out.println(ObjectToJsonConverter.INSTANCE_CLEAN.objectToString(tag.getAttributes()));
			System.out.println(session.getBody());
		}
		String type = tag.getOptional("type", null);
		String async = tag.getOptional("async", null);
		String defer = tag.getOptional("defer", null);
		String nonce = tag.getOptional("nonce", "true");
		boolean inline = false;
		StringBuilder bodySb = session.getBody();

		//		String text = IfTag.parseExpression(src);

		if ("inline".contentEquals(src)) {
			inline = true;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("<script ");

		if (inline == false)
			sb.append("src='").append(src).append("'");
		if (type != null)
			sb.append(" type='").append(type).append("'");
		if (async != null)
			sb.append(" async");
		if (defer != null)
			sb.append(" defer");

		call_append(bodySb, sb);
		SH.clear(sb);

		if (SH.equals("true", nonce)) //DO NONCE
			call_addNonce(bodySb);

		if (inline == false)
			sb.append("></script>");
		else
			sb.append(">");

		call_append(bodySb, sb);

	}

	private void call_addNonce(StringBuilder bodySb) {
		bodySb.append(getClass().getName());
		bodySb.append(".addNonce(out,request");
		bodySb.append(");").append(SH.NEWLINE);

	}
	private void call_append(StringBuilder bodySb, StringBuilder sb) {
		bodySb.append(getClass().getName());
		bodySb.append(".append(out,\"");
		bodySb.append(sb.toString());
		bodySb.append("\");").append(SH.NEWLINE);

	}
	@Override
	public void doStart(JspBuilderSession session, HttpTag tag, int indent) {
		doSimple(session, tag, indent);

	}

	@Override
	public void doEnd(JspBuilderSession session, HttpTag tag, int indent) {

	}

	/*
	 * Server Handles this
	 */
	public static final void addNonce(FastPrintStream out, HttpRequestResponse req) {
		out.print("nonce=\"");
		out.print(req.genCspNonce());
		out.print("\"");
		out.flush();
		//		String csp = req.getResponseHeader(HttpUtils.CONTENT_SECURITY_POLICY);
		//		System.out.println(csp);
	}
	/*
	 * Server Handles this
	 */
	public static final void append(FastPrintStream out, String o) {
		if (o == null)
			return;
		out.print(o);

	}
	public static void main(String[] args) {
		SecureRandom r = new SecureRandom();
		byte b[] = new byte[16];
		r.nextBytes(b);
		byte[] enc = Base64.getEncoder().encode(b);
		String nonce = new String(enc);
		System.out.println(nonce);

	}
}
