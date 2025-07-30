/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal.impl;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletSchema;

public class BasicPortletSchema<P extends Portlet> implements PortletSchema<P> {

	final private String jsProtoype;

	public BasicPortletSchema(String name, String jsProtoype, Class<P> clazz, boolean isContainer, boolean isAbsolutePositioning) {
		this.jsProtoype = jsProtoype;
	}
        public BasicPortletSchema(String jsProtoype) {
                this.jsProtoype = jsProtoype;
        }



	@Override
	public String getJsPrototype() {
		return jsProtoype;
	}


}
