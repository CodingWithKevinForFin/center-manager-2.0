package com.vortex.ssoweb;

import java.util.HashMap;
import java.util.Map;

import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoGroupAttribute;

public class SsoWebGroup {

	private Object object;
	final private LongKeyMap<SsoWebGroup> childrenByType = new LongKeyMap<SsoWebGroup>();
	final private LongKeyMap<SsoWebGroup> parentGroups = new LongKeyMap<SsoWebGroup>();
	private SsoGroup group;
	private Map<String, SsoGroupAttribute> attributes = new HashMap<String, SsoGroupAttribute>();

	public SsoWebGroup(SsoGroup group) {
		this.group = group;
	}

	//Group info
	public short getType() {
		return group.getType();
	}

	public SsoGroup getGroup() {
		return group;
	}

	public long getGroupId() {
		return group.getId();
	}

	public String getName() {
		return group.getName();
	}

	//Peer
	public void setPeer(Object object) {
		this.object = object;
	}

	public Object getPeer() {
		return object;
	}

	//children
	public void addChild(SsoWebGroup child) {
		childrenByType.put(child.getGroupId(), child);
	}

	public void removeChild(long id) {
		childrenByType.remove(id);
	}

	public LongKeyMap<SsoWebGroup> getChildren() {
		return childrenByType;
	}

	//parents
	public void addParent(SsoWebGroup parent) {
		this.parentGroups.put(parent.getGroupId(), parent);
	}

	public LongKeyMap<SsoWebGroup> getParents() {
		return parentGroups;
	}

	public void removeParent(long parentId) {
		this.parentGroups.remove(parentId);
	}

	//others
	public void clearBindings() {
		childrenByType.clear();
		parentGroups.clear();
	}

	public Map<String, SsoGroupAttribute> getGroupAttributes() {
		return attributes;
	}

	public String getTypeName() {
		short type = getType();
		switch (type) {
			case SsoGroup.GROUP_TYPE_ACCOUNT:
				return "account";
			case SsoGroup.GROUP_TYPE_ENVIRONMENT:
				return "environment";
			case SsoGroup.GROUP_TYPE_GENERIC:
				return "generic";
			case SsoGroup.GROUP_TYPE_HOST:
				return "host";
			case SsoGroup.GROUP_TYPE_PROCESS:
				return "process";
			case SsoGroup.GROUP_TYPE_REGION:
				return "region";
			case SsoGroup.GROUP_TYPE_USER:
				return "user";
			case SsoGroup.GROUP_TYPE_EXPECTATION:
				return "Expectation";
			case SsoGroup.GROUP_TYPE_DEPLOYMENT:
				return "Deployment";
			default:
				return SH.toString(type);
		}
	}

}
