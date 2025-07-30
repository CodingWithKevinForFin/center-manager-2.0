package com.vortex.ssoweb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.MapInMap;
import com.sso.messages.QuerySsoGroupResponse;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoGroupMember;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.SsoUser;

public class SsoWebTreeManager {
	private static final Logger log = Logger.getLogger(SsoWebTreeManager.class.getName());
	private List<SsoPortlet> ssoPortlets = new ArrayList<SsoPortlet>();

	private LongKeyMap<SsoWebGroup> groupsByGroupId = new LongKeyMap<SsoWebGroup>();
	private LongKeyMap<Object> peersByGroupId = new LongKeyMap<Object>();
	//private LongKeyMap<Object> nodesByGroupId = new LongKeyMap<Object>();
	private MapInMap<Long, Long, SsoGroupMember> nodeToGroup = new MapInMap<Long, Long, SsoGroupMember>();
	private MapInMap<Long, Long, SsoGroupMember> groupToNode = new MapInMap<Long, Long, SsoGroupMember>();

	public SsoWebGroup addGroup(SsoGroup node) {
		SsoWebGroup existing = groupsByGroupId.get(node.getId());
		if (existing != null) {
			//TODO
			//existing.setObject(node);
			return existing;
		}
		SsoWebGroup webGroup = new SsoWebGroup(node);
		webGroup.setPeer(peersByGroupId.get(node.getId()));
		groupsByGroupId.put(node.getId(), webGroup);
		Map<Long, SsoGroupMember> m = groupToNode.get(webGroup.getGroupId());
		if (m != null) {
			for (SsoGroupMember gm : m.values()) {
				SsoWebGroup child = groupsByGroupId.get(gm.getMemberId());
				if (child != null) {
					webGroup.addChild(child);
					child.addParent(webGroup);
				}
			}
		}
		m = nodeToGroup.get(webGroup.getGroupId());
		if (m != null) {
			for (SsoGroupMember gm : m.values()) {
				SsoWebGroup parent = groupsByGroupId.get(gm.getGroupId());
				if (parent != null) {
					parent.addChild(webGroup);
					webGroup.addParent(parent);
				}
			}
		}
		return webGroup;
	}
	public SsoWebGroup getGroup(long groupId) {
		return groupsByGroupId.get(groupId);
	}

	public void removeParentChildRelation(SsoGroupMember groupMember) {
		long groupId = groupMember.getGroupId();
		long memberId = groupMember.getMemberId();
		nodeToGroup.removeMulti(memberId, groupId);
		groupToNode.removeMulti(groupId, memberId);
		SsoWebGroup parent = getGroup(groupId);
		SsoWebGroup child = getGroup(memberId);
		if (parent != null)
			parent.removeChild(memberId);
		if (child != null)
			child.removeParent(groupId);
	}

	public SsoGroupMember addParentChildRelation(SsoGroupMember groupMember) {
		final long groupId = groupMember.getGroupId();
		final long memberId = groupMember.getMemberId();
		nodeToGroup.putMulti(memberId, groupId, groupMember);
		groupToNode.putMulti(groupId, memberId, groupMember);
		SsoWebGroup node = getGroup(memberId);
		if (node != null) {
			SsoWebGroup group = getGroup(groupId);
			if (group != null) {
				group.addChild(node);
				node.addParent(group);
			}
		}
		return groupMember;
	}

	public SsoWebGroup removeGroup(long groupId) {
		SsoWebGroup r = groupsByGroupId.remove(groupId);
		if (r != null) {
			for (SsoWebGroup i : r.getParents().values())
				i.removeChild(r.getGroupId());
			for (SsoWebGroup i : r.getChildren().values())
				i.removeParent(r.getGroupId());
			r.clearBindings();
		}
		nodeToGroup.remove(groupId);
		groupToNode.remove(groupId);
		return r;
	}

	public List<SsoWebGroup> getRoots() {
		List<SsoWebGroup> r = new ArrayList<SsoWebGroup>();
		for (SsoWebGroup node : groupsByGroupId.values())
			if (node.getParents().isEmpty())
				r.add(node);
		return r;
	}

	public Iterable<SsoWebGroup> getGroups() {
		return groupsByGroupId.values();
	}
	public void addSsoPortlet(SsoPortlet ssoPortlet) {
		this.ssoPortlets.add(ssoPortlet);
	}

	public void removeSsoPortlet(SsoPortlet ssoPortlet) {
		ssoPortlets.remove(ssoPortlet);
	}
	private void fireOnNewGroup(SsoWebGroup group) {
		for (SsoPortlet p : ssoPortlets)
			p.onNewGroup(group);
	}
	private void fireOnRemoveGroup(SsoWebGroup group) {
		if (group != null)
			for (SsoPortlet p : ssoPortlets)
				p.onRemoveGroup(group);
	}
	private void fireOnEvent(SsoUpdateEvent event) {
		for (SsoPortlet p : ssoPortlets)
			p.onEvent(event);
	}

	public void onEvent(SsoUpdateEvent event) {
		if (event.getOk()) {
			for (SsoGroupMember gm : CH.i(event.getGroupMembers())) {
				if (gm.getRevision() == 65535) {
					removeParentChildRelation(gm);
				} else {
					addParentChildRelation(gm);
				}
			}

			for (SsoGroup group : CH.i(event.getGroups())) {
				if (group.getRevision() == 65535) {
					fireOnRemoveGroup((SsoWebGroup) removeGroup(group.getId()));
				} else
					fireOnNewGroup(addGroup(group));
			}

			for (SsoUser user : CH.i(event.getUsers())) {
				if (user.getRevision() != 65535) {
					setPeer(user.getGroupId(), user);
				}
			}
			for (SsoGroupAttribute att : CH.i(event.getGroupAttributes())) {
				addGroupAttribute(att);
			}

			if (CH.isEmpty(event.getUsers()) && CH.isEmpty(event.getGroups())) {
				for (SsoGroupMember gm : CH.i(event.getGroupMembers())) {
					if (gm.getRevision() == 65535) {
						fireOnRemoveGroupMember(gm);
					} else {
						fireOnNewGroupMember(gm);
					}
				}
			}
		}
	}
	public void addGroupAttribute(SsoGroupAttribute att) {
		SsoWebGroup group = getGroup(att.getGroupId());
		if (group == null) {
			LH.warning(log, "attribute for unknown group: ", att);
		} else {
			if (att.getRevision() == 65535) {
				group.getGroupAttributes().remove(att.getKey());
				fireOnRemoveGroupAttribute(group, att);
			} else {
				group.getGroupAttributes().put(att.getKey(), att);
				fireOnNewGroupAttribute(group, att);
			}
		}

	}
	private void fireOnNewGroupAttribute(SsoWebGroup group, SsoGroupAttribute attribute) {
		for (SsoPortlet p : ssoPortlets)
			p.onNewGroupAttribute(group, attribute);
	}
	private void fireOnRemoveGroupAttribute(SsoWebGroup group, SsoGroupAttribute attribute) {
		for (SsoPortlet p : ssoPortlets)
			p.onRemoveGroupAttribute(group, attribute);
	}
	private void fireOnRemoveGroupMember(SsoGroupMember gm) {
		SsoWebGroup group = getGroup(gm.getGroupId());
		SsoWebGroup node = getGroup(gm.getMemberId());
		if (group != null && node != null)
			for (SsoPortlet p : ssoPortlets)
				p.onRemoveGroupMember(gm, group, node);
	}

	private void fireOnNewGroupMember(SsoGroupMember gm) {
		SsoWebGroup group = getGroup(gm.getGroupId());
		SsoWebGroup node = getGroup(gm.getMemberId());
		if (group != null && node != null)
			for (SsoPortlet p : ssoPortlets)
				p.onNewGroupMember(gm, group, node);
	}

	public SsoGroupMember getGroupMember(long groupId, short memberType, long memberId) {
		return nodeToGroup.getMulti(memberId, groupId);
	}
	public Iterable<SsoWebGroup> getGroupsByType(short type) {
		ArrayList<SsoWebGroup> r = new ArrayList<SsoWebGroup>();
		for (SsoWebGroup g : groupsByGroupId.values())
			if (g.getType() == type)
				r.add(g);
		return r;
	}
	public void setPeer(long groupId, Object user) {
		peersByGroupId.put(groupId, user);
		SsoWebGroup group = getGroup(groupId);
		if (group != null)
			group.setPeer(user);
		//else
		//LH.warning( log ,"Peer has no group: " , user);

	}
	public void onSnapshot(QuerySsoGroupResponse response) {
		for (SsoGroup group : CH.i(response.getGroups()))
			fireOnNewGroup(addGroup(group));
		for (SsoUser user : CH.i(response.getUsers()))
			setPeer(user.getGroupId(), user);
		for (SsoGroupMember groupMember : CH.i(response.getGroupMembers()))
			fireOnNewGroupMember(addParentChildRelation(groupMember));
		for (SsoGroupAttribute groupAttribute : CH.i(response.getGroupAttributes()))
			addGroupAttribute(groupAttribute);
	}

}
