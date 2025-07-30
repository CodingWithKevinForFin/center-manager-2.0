package com.vortex.sso;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.f1.base.ObjectGenerator;
import com.f1.container.ContainerServices;
import com.f1.utils.db.DbService;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoGroupMember;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.SsoUser;

public class SsoDbService extends DbService {

	final private ContainerServices services;

	public SsoDbService(DataSource datasource, ObjectGenerator generator, ContainerServices services) {
		super(datasource, generator);
		this.services = services;
	}

	public void insertGroup(SsoGroup group, Connection con) throws Exception {
		Map<Object, Object> m = new HashMap<Object, Object>();
		if (group.getRevision() == 0) {
			long id = services.getUidGenerator("SsoGroup").createNextId();
			group.setId(id);
		}

		long now = services.getClock().getNow();
		m.put("active", group.getRevision() != 65535);
		m.put("id", group.getId());
		m.put("revision", group.getRevision());
		m.put("name", group.getName());
		m.put("groupType", group.getType());
		m.put("now", now);
		execute("insert_ssogroup", m, con);
	}

	public void insertUser(SsoUser user, Connection con) throws Exception {
		Map<Object, Object> m = new HashMap<Object, Object>();
		if (user.getRevision() == 0) {
			long id = services.getUidGenerator("SsoUser").createNextId();
			user.setId(id);
		}
		long now = services.getClock().getNow();
		m.put("now", now);
		m.put("active", user.getRevision() != 65535);
		m.put("revision", user.getRevision());
		m.put("company", user.getCompany());
		m.put("email", user.getEmail());
		m.put("status", user.getStatus());
		m.put("encoding_algorithm", user.getEncodingAlgorithm());
		m.put("expires", user.getExpires());
		m.put("first_name", user.getFirstName());
		m.put("id", user.getId());
		m.put("last_name", user.getLastName());
		m.put("password", user.getPassword());
		m.put("phone_number", user.getPhoneNumber());
		m.put("reset_answer", user.getResetAnswer());
		m.put("reset_question", user.getResetQuestion());
		m.put("user_name", user.getUserName());
		m.put("group_id", user.getGroupId());
		m.put("max_bad_attempts", user.getMaxBadAttempts());
		execute("insert_ssouser", m, con);

	}
	public void insertGroupMember(SsoGroupMember groupMember, Connection con) throws Exception {
		long now = services.getClock().getNow();
		if (groupMember.getRevision() == 0) {
			long id = services.getUidGenerator("SsoGroupMembers").createNextId();
			groupMember.setId(id);
		}
		final Map<Object, Object> m = new HashMap<Object, Object>();

		m.put("now", now);
		m.put("active", groupMember.getRevision() != 65535);
		m.put("revision", groupMember.getRevision());
		m.put("id", groupMember.getId());
		m.put("group_id", groupMember.getGroupId());
		m.put("member_id", groupMember.getMemberId());
		//m.put("member_type", groupMember.getMemberType());
		execute("insert_ssogroupmember", m, con);
	}
	public void insertGroupAttribute(SsoGroupAttribute groupAttribute, Connection con) throws Exception {
		long now = services.getClock().getNow();
		if (groupAttribute.getRevision() == 0) {
			long id = services.getUidGenerator("SsoGroupAttributes").createNextId();
			groupAttribute.setId(id);
		}
		final Map<Object, Object> m = new HashMap<Object, Object>();
		groupAttribute.setNow(now);

		m.put("now", now);
		m.put("active", groupAttribute.getRevision() != 65535);
		m.put("revision", groupAttribute.getRevision());
		m.put("id", groupAttribute.getId());
		m.put("group_id", groupAttribute.getGroupId());
		m.put("attribute_key", groupAttribute.getKey());
		m.put("attribute_type", groupAttribute.getType());
		m.put("attribute_value", groupAttribute.getValue());
		execute("insert_ssogroup_attribute", m, con);
	}
	public void insertUpdateEvent(SsoUpdateEvent action, DbService dbservice, Connection connection) throws Exception {
		long id = services.getUidGenerator("SsoUpdateEvent").createNextId();
		action.setId(id);
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("id", action.getId());
		map.put("now", action.getNow());
		map.put("event_type", action.getType());
		map.put("session", action.getSession());
		map.put("name", action.getName());
		map.put("message", action.getMessage());
		map.put("ok", action.getOk());
		map.put("namespace", action.getNamespace());
		map.put("member_id", action.getMemberId());
		map.put("client_location", action.getClientLocation());
		dbservice.execute("insert_ssoupdateevent", map, connection);
	}

	public void insertUserEvent(String userName, String email, Long ssouserId, byte type, String namespace, Connection con) throws Exception {
		Map<Object, Object> m = new HashMap<Object, Object>();
		long id = services.getUidGenerator("SsoUser").createNextId();
		m.put("now", services.getTools().getNow());
		m.put("supplied_user_name", userName);
		m.put("supplied_email", email);
		m.put("name_space", namespace);
		m.put("ssouser_id", ssouserId);
		m.put("event_type", type);
		execute("insert_ssouser_event", m, con);
	}

}
