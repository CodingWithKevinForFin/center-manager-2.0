package com.f1.ami.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.ami.web.AmiWebPortletDef.Callback;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;

public abstract class AmiWebAbstractPortletBuilder<T extends AmiWebAliasPortlet> extends AbstractPortletBuilder<T> implements AmiWebPortletBuilder<T> {

	public AmiWebAbstractPortletBuilder(Class<T> type) {
		super(type);
	}

	@Override
	public List<AmiWebPortletDef.Callback> getCallbacks(Map<String, Object> portletConfig) {
		List<Callback> r = new ArrayList<AmiWebPortletDef.Callback>();
		AmiWebPortletDef.getCallbacks(portletConfig, r);
		return r;
	}

}
