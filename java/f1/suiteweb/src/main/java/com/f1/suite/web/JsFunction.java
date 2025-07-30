/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web;

import com.f1.suite.web.util.WebHelper;
import com.f1.utils.SH;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.json.JsonBuilder;

public class JsFunction {

	private StringBuilder js;
	private boolean isFirst = true;
	private String object;
	private JsonBuilder jsonBuilder = null;

	public JsFunction(StringBuilder sb) {
		this.js = sb;
	}
	public JsFunction(StringBuilder sb, String object, String functionName) {
		reset(sb, object, functionName);
	}

	public JsFunction(String object) {
		this.object = object;
	}
	public JsFunction() {
	}

	public JsFunction reset(StringBuilder sb) {
		this.js = sb;
		isFirst = true;
		if (SH.is(object))
			sb.append(object);
		return this;
	}

	public JsFunction reset(StringBuilder sb, String functionName) {
		this.js = sb;
		isFirst = true;
		if (SH.is(object))
			sb.append(object).append('.');
		sb.append(functionName).append('(');
		if (this.jsonBuilder != null)
			this.jsonBuilder.reset(js);
		return this;
	}
	public JsFunction reset(String object, String functionName) {
		isFirst = true;
		if (SH.is(object))
			this.js.append(object).append('.');
		this.js.append(functionName).append('(');
		return this;
	}
	public JsFunction reset(StringBuilder sb, String object, String functionName) {
		this.js = sb;
		isFirst = true;
		if (SH.is(object))
			sb.append(object).append('.');
		sb.append(functionName).append('(');
		return this;
	}

	public JsFunction call(String functionName) {
		js.append('.').append(functionName).append('(');
		return this;
	}

	public JsFunction addParam(Object param) {
		startParam();
		js.append(param);
		return this;
	}
	public JsFunction addParam(double param) {
		startParam();
		js.append(param);
		return this;
	}
	public JsFunction addParam(long param) {
		startParam();
		js.append(param);
		return this;
	}
	public JsFunction addParam(char param) {
		startParam();
		js.append(param);
		return this;
	}
	public JsFunction addParam(boolean param) {
		startParam();
		js.append(param);
		return this;
	}

	public JsFunction addProperty(String prop) {
		js.append('.').append(prop).append('=');
		return this;
	}

	public JsFunction addPropertyValue(Object val) {
		js.append(val).append(';').append(SH.NEWLINE);
		return this;
	}

	public JsFunction addPropertyValueQuoted(Object val) {
		if (val != null)
			WebHelper.quoteHtml(val.toString(), js);
		else
			js.append("null");

		js.append(';').append(SH.NEWLINE);
		return this;
	}

	public JsFunction startParam() {
		if (!isFirst) {
			js.append(',');
		} else
			isFirst = false;
		return this;
	}
	public JsFunction addParamQuoted(Object param) {
		startParam();
		if (param != null)
			WebHelper.quote(SH.toCharSequence(param), js);
		else
			js.append("null");
		return this;
	}
	public JsFunction addParamQuotedHtml(Object param) {
		startParam();
		if (param != null)
			WebHelper.quoteHtml(SH.toCharSequence(param), js);
		else
			js.append("null");
		return this;
	}
	public JsFunction addParamQuoted(CharSequence param) {
		startParam();
		if (param != null)
			WebHelper.quote(param, js);
		else
			js.append("null");
		return this;
	}
	public JsFunction addParamQuotedHtml(CharSequence param) {
		startParam();
		if (param != null)
			WebHelper.quoteHtml(param, js);
		else
			js.append("null");
		return this;
	}
	public JsFunction close() {
		js.append(")");
		return this;
	}

	public JsFunction end() {
		js.append(");").append(SH.NEWLINE);
		return this;
	}

	public JsFunction addParamJson(Object obj) {
		addParam(RootAssister.INSTANCE.toJson(obj));
		return this;
	}

	public JsonBuilder startJson() {
		if (!isFirst) {
			js.append(',');
		} else
			isFirst = false;
		if (jsonBuilder == null)
			jsonBuilder = new JsonBuilder(js);
		else if (jsonBuilder.isClosed())
			jsonBuilder.reset(js);
		else if (!jsonBuilder.isEmpty())
			throw new IllegalStateException("last json wasn't closed");
		return jsonBuilder;
	}

	public StringBuilder getStringBuilder() {
		return this.js;
	}

}
