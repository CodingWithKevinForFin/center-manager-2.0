package com.f1.ami.web.dm.portlets;

public interface AmiWebDmLinkWhereFieldListener {
	public void onWhereFieldAdded(AmiWebDmWhereFieldsForm wherefield, int position);
	public void onWhereFieldRemoved(AmiWebDmWhereFieldsForm wherefield, String varname);
}
