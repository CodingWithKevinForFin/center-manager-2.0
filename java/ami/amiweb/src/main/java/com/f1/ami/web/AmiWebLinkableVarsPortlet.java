package com.f1.ami.web;

import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.base.Mapping;
import com.f1.base.Table;

public interface AmiWebLinkableVarsPortlet extends AmiWebPortlet {

	public com.f1.base.CalcTypes getLinkableVars();
	public void processFilter(AmiWebDmLink link, Table table);
}
