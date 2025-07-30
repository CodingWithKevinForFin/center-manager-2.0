/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.tags;

import com.f1.http.HttpTag;
import com.f1.http.handler.JspBuilderSession;
import com.f1.http.tag.IfTag;
import com.f1.utils.SH;

public class NumberFormatterTag extends AbstractFormatterTag {

	@Override
	protected void format(JspBuilderSession session, HttpTag tag, int i) {
		String decimals = IfTag.parseExpression(tag.getOptional("decimals", "0"));
		String value = IfTag.parseExpression(tag.getOptional("value", "0"));
		session.getBody().append("localFormatter.getNumberFormatter(").append(decimals).append(").format(").append(value).append(", out);");
		session.getBody().append(SH.NEWLINE);

	}

}
