package com.vortex.ssoweb;

import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoGroupMember;
import com.sso.messages.SsoUpdateEvent;

public interface SsoPortlet {

	public void onNewGroup(SsoWebGroup group);
	public void onRemoveGroup(SsoWebGroup group);

	public void onEvent(SsoUpdateEvent event);

	public void onNewGroupMember(SsoGroupMember gm, SsoWebGroup group, SsoWebGroup node);
	public void onRemoveGroupMember(SsoGroupMember gm, SsoWebGroup group, SsoWebGroup node);

	public void onNewGroupAttribute(SsoWebGroup group, SsoGroupAttribute attribute);
	public void onRemoveGroupAttribute(SsoWebGroup group, SsoGroupAttribute attribute);

}
