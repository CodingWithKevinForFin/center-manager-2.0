package com.f1.ami.web.style;

import com.f1.utils.structs.table.derived.ToDerivedString;

//A portlet that has supports cascaded styling.
public interface AmiWebStyledPortlet extends ToDerivedString {
	public AmiWebStyledPortletPeer getStylePeer();
	public void onStyleValueChanged(short code, Object old, Object nuw);
	public String getStyleType();
	public void onParentStyleChanged(AmiWebStyledPortletPeer amiWebStyledPortletPeer);
	//	public void onVarConstChanged(String var);
}
