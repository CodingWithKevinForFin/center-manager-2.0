package com.f1.http.handler;

import com.f1.http.HttpTag;

public interface JspTagBuilder {

	public void doSimple(JspBuilderSession session, HttpTag tag, int indent);

	public void doStart(JspBuilderSession session, HttpTag tag, int indent);

	public void doEnd(JspBuilderSession session, HttpTag tag, int indent);

}
