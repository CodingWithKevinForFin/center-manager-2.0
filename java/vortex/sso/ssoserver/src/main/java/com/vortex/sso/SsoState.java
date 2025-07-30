package com.vortex.sso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.f1.base.ObjectGenerator;
import com.f1.container.impl.BasicState;
import com.f1.utils.OH;
import com.f1.utils.VH;
import com.f1.utils.index.QueryMultiIndex;
import com.f1.utils.index.QueryResult;
import com.f1.utils.index.QueryUniqueIndex;
import com.f1.utils.index.impl.BasicValuedQueryTable;
import com.f1.utils.structs.LongKeyMap;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoGroupMember;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.SsoUser;

public class SsoState extends BasicState {

	private static final long DEFAULT_DURATION = TimeUnit.DAYS.toMillis(365 * 10);
	final private BasicValuedQueryTable<SsoUser> users;
	final private QueryUniqueIndex<SsoUser> usersByName;
	private QueryUniqueIndex<SsoUser> usersByEmail;
	private QueryUniqueIndex<SsoUser> usersById;
	private QueryUniqueIndex<SsoUser> usersByGroupId;

	final private BasicValuedQueryTable<SsoGroupMember> members;
	final private BasicValuedQueryTable<SsoGroup> groups;
	final private BasicValuedQueryTable<SsoGroupAttribute> groupAttributes;

	private QueryUniqueIndex<SsoGroup> groupsByTypeAndName;
	private QueryUniqueIndex<SsoGroup> groupsById;
	private long defaultExpiresDuration = DEFAULT_DURATION;
	//private BasicMultiMap.Set<Long, Long> groupToMembers;
	private List<SsoUpdateEvent> events;
	private QueryUniqueIndex<SsoGroupMember> membersById;
	private QueryMultiIndex<SsoGroupMember> membersByGroupId;
	private QueryMultiIndex<SsoGroupMember> membersByMemberId;
	private QueryMultiIndex<SsoGroupAttribute> groupAttributesByGroupId;
	private QueryUniqueIndex<SsoGroupAttribute> groupAttributesByGroupIdAndKey;

	public SsoState(ObjectGenerator generator) {
		users = new BasicValuedQueryTable<SsoUser>(VH.getSchema(generator.nw(SsoUser.class)), generator);
		usersByName = users.createUniqueIndex("userName");
		usersByEmail = users.createUniqueIndex("email");
		usersById = users.createUniqueIndex("id");
		usersByGroupId = users.createUniqueIndex("groupId");

		groups = new BasicValuedQueryTable<SsoGroup>(VH.getSchema(generator.nw(SsoGroup.class)), generator);
		groupsByTypeAndName = groups.createUniqueIndex("type", "name");
		groupsById = groups.createUniqueIndex("id");

		members = new BasicValuedQueryTable<SsoGroupMember>(VH.getSchema(generator.nw(SsoGroupMember.class)), generator);
		membersById = members.createUniqueIndex("id");
		membersByGroupId = members.createIndex("groupId");
		membersByMemberId = members.createIndex("memberId");

		//groupToMembers = new BasicMultiMap.Set<Long, Long>();
		events = new ArrayList<SsoUpdateEvent>();

		groupAttributes = new BasicValuedQueryTable<SsoGroupAttribute>(VH.getSchema(generator.nw(SsoGroupAttribute.class)), generator);
		groupAttributesByGroupId = groupAttributes.createIndex("groupId");
		groupAttributesByGroupIdAndKey = groupAttributes.createUniqueIndex("groupId", "key");
	}

	public long getDefaultExpiresDurationMs() {
		return defaultExpiresDuration;
	}

	public void setDefaultExpiresDurationMs(long defaultExpiresDuration) {
		this.defaultExpiresDuration = defaultExpiresDuration;
	}

	public Iterable<SsoUser> getUsers() {
		return users.getValues();
	}

	public SsoUser getUserByUserName(String userName) {
		return usersByName.find(userName);
	}

	public SsoUser getUserByEmail(String email) {
		return usersByEmail.find(email);
	}

	public SsoUser getUser(long id) {
		return usersById.find(id);
	}
	public SsoUser getUserByGroupId(long id) {
		return usersByGroupId.find(id);
	}

	public void addUser(SsoUser user) {
		user.lock();
		users.addRow(user);
	}

	public boolean removeUser(SsoUser existingUser) {
		return users.removeRow(existingUser);
	}

	final private Map<Long, Integer> loginAttempts = new HashMap<Long, Integer>();

	// returns unsuccesful attempts. always returns 0 for success=true, 1 for
	// first success=false, 2 for second etc.
	public int onLoginAttempt(long userId, boolean success) {
		if (success) {
			loginAttempts.remove(userId);
			return 0;
		}
		int r = getLoginAttempts(userId) + 1;
		loginAttempts.put(userId, r);
		return r;
	}

	public int getLoginAttempts(long userId) {
		return OH.noNull(loginAttempts.get(userId), 0);
	}

	public Iterable<SsoGroup> getGroups() {
		return groups.getValues();
	}

	public SsoGroup getGroupByTypeAndName(short groupType, String groupName) {
		return groupsByTypeAndName.find(groupType, groupName);
	}

	public SsoGroup getGroup(long id) {
		return groupsById.find(id);
	}

	public void addGroup(SsoGroup group) {
		group.lock();
		groups.addRow(group);
	}

	public boolean removeGroup(SsoGroup existingGroup) {
		return groups.removeRow(existingGroup);
	}

	public List<SsoUpdateEvent> getEvents() {
		return events;
	}

	public void addEvent(SsoUpdateEvent event) {
		event.lock();
		events.add(event);
	}

	public void setEvents(List<SsoUpdateEvent> events) {
		for (SsoUpdateEvent event : events)
			event.lock();
		this.events = events;
	}

	//Returns false only if checkForCirc is true and there is a circular reference
	public void addGroupMember(SsoGroupMember member) {
		member.lock();
		this.members.addRow(member);
	}

	public boolean isGroupMemberCircRef(SsoGroupMember member) {
		final LongKeyMap<Object> childIds = new LongKeyMap<Object>();
		getAllChildren(member, childIds);
		if (hasParent(member, childIds))
			return true;
		return false;
	}

	private boolean hasParent(SsoGroupMember member, LongKeyMap<Object> ids) {
		if (member == null)
			return false;
		else if (ids.containsKey(member.getGroupId()))
			return true;
		else
			for (SsoGroupMember parent : getGroupMembersByMemberId(member.getGroupId()))
				if (hasParent(parent, ids))
					return true;
		return false;
	}

	private void getAllChildren(SsoGroupMember member, LongKeyMap<Object> childIds) {
		if (member == null)
			return;
		childIds.put(member.getMemberId(), null);
		for (SsoGroupMember child : getGroupMembersByGroupId(member.getMemberId()))
			getAllChildren(child, childIds);
	}
	public Iterable<SsoGroupMember> getGroupMembersByGroupId(long groupId) {
		return membersByGroupId.find(groupId);
	}
	public Iterable<SsoGroupMember> getGroupMembersByMemberId(long memberId) {
		return membersByMemberId.find(memberId);
	}

	public SsoGroupMember getGroupMemberByMemberAndGroupId(long memberId, long groupId) {
		for (SsoGroupMember i : getGroupMembersByMemberId(memberId))
			if (i.getGroupId() == groupId)
				return i;
		return null;
	}

	public Iterable<SsoGroupMember> getGroupMembers() {
		return members.getValues();
	}
	public int getGroupMembersCount() {
		return members.getSize();
	}

	public int getGroupsCount() {
		return groups.getSize();
	}

	public int getUsersCount() {
		return users.getSize();
	}
	public SsoGroupMember getGroupMember(long id) {
		return membersById.find(id);
	}

	public void addGroupAttribute(SsoGroupAttribute groupAttribute) {
		groupAttribute.lock();
		groupAttributes.addRow(groupAttribute);
	}

	public QueryResult<SsoGroupAttribute> getGroupAttributes() {
		return groupAttributes.getValues();
	}

	public Iterable<SsoGroupAttribute> getGroupAttributes(long ssogroupid) {
		return groupAttributesByGroupId.find(ssogroupid);
	}
	public SsoGroupAttribute getGroupAttribute(long ssogroupid, String key) {
		return groupAttributesByGroupIdAndKey.find(ssogroupid, key);
	}
	public boolean removeGroupAttribute(SsoGroupAttribute attribute) {
		return groupAttributes.removeRow(attribute);
	}

	public int getGroupAttributesCount() {
		return groupAttributes.getSize();
	}

	public void removeSsoGroup(SsoGroup group) {
		groups.removeRow(group);
	}

	public void removeSsoUser(SsoUser user) {
		users.removeRow(user);
	}

	public void removeGroupMember(SsoGroupMember gm) {
		members.removeRow(gm);
	}

}
