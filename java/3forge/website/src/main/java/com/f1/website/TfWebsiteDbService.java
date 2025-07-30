package com.f1.website;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.f1.base.ObjectGenerator;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.db.DbService;
import com.f1.utils.ids.BatchIdGenerator;
import com.f1.utils.ids.DbBackedIdGenerator;

public class TfWebsiteDbService extends DbService {

	private BatchIdGenerator<Long> fountain;

	public TfWebsiteDbService(DataSource datasource, ObjectGenerator generator, File sqlpath) throws IOException {
		super(datasource, generator);
		this.fountain = new BatchIdGenerator.Factory<Long>(new DbBackedIdGenerator.Factory(datasource, "Id_Fountains", "next_id", "namespace", 100000), 1000).get("TFWEBSITE");
		add(sqlpath, ".sql");
	}

	public void addUser(HttpRequestResponse req, TfWebsiteUser user, String audit, Connection conn) throws Exception {
		Map<Object, Object> m = toMap(user);
		m.put("audit", audit);
		long id = addAudit(req, user, null, audit, null, conn);
		m.put("audit_id", id);
		super.execute("insert_user", m, conn);
	}
	public void addApplicant(HttpRequestResponse req, TfWebsiteApplicant applicant, Connection conn) throws Exception {
		Map<Object, Object> m = toMap(applicant);
		System.out.println(m.toString());
		m.remove("active");
		m.remove("createdOn");
		m.remove("modifiedOn");
		m.remove("revision");
		m.remove("id");
		super.execute("insert_applicant", m, conn);
	}
	public long addAudit(HttpRequestResponse req, TfWebsiteUser tfuser, String username, String audit, String description, Connection conn) throws Exception {
		Map<Object, Object> m = new HashMap<Object, Object>();
		HttpSession session = req.getSession(false);
		long id = nextId();
		m.put("id", id);
		m.put("createdOn", System.currentTimeMillis());
		m.put("userId", tfuser == null ? null : tfuser.getId());
		m.put("username", username == null && tfuser != null ? tfuser.getUsername() : username);
		m.put("sessionId", session == null ? null : (String) session.getSessionId());
		m.put("remoteAddr", (String) req.getRemoteHost());
		m.put("audit", audit);
		m.put("description", description);
		super.execute("insert_audit", m, conn);
		return id;
	}
	public void addPressReleaseUser(HttpRequestResponse req, WebsiteUser user, boolean contactByPhone, boolean contactByEmail, Connection conn) throws Exception {
		Map<Object, Object> m = new HashMap<Object, Object>();
		Long id = nextId();
		m.put("id", id);
		m.put("createdOn", System.currentTimeMillis());
		m.put("email", user == null ? null : user.getEmail());
		m.put("firstName", user == null ? null : user.getFirstName());
		m.put("lastName", user == null ? null : user.getLastName());
		m.put("company", user == null ? null : user.getCompany());
		m.put("phone", user == null ? null : user.getPhone());
		m.put("contactPhone", contactByPhone);
		m.put("contactEmail", contactByEmail);
		super.execute("insert_press_release_user", m, conn);
	}
	public TfWebsiteUser queryUser(String username, Connection conn) throws Exception {
		return CH.first(super.executeQuery("query_user", CH.m("field", "username", "value", username), TfWebsiteUser.class, conn));
	}
	public TfWebsiteUser queryUser(long userid, Connection conn) throws Exception {
		return CH.first(super.executeQuery("query_user", CH.m("field", "id", "value", userid), TfWebsiteUser.class, conn));
	}
	public List<TfWebsiteUser> queryUserCompany(String company, Connection conn) throws Exception {
		return super.executeQuery("query_user", CH.m("field", "company", "value", company), TfWebsiteUser.class, conn);
	}
	public TfWebsiteUser queryUserByVerifyGuid(String guid, Connection conn) throws Exception {
		return CH.first(super.executeQuery("query_user", CH.m("field", "verify_guid", "value", guid), TfWebsiteUser.class, conn));
	}
	public TfWebsiteUser queryUserByForgotGuid(String guid, Connection conn) throws Exception {
		return CH.first(super.executeQuery("query_user", CH.m("field", "forgot_guid", "value", guid), TfWebsiteUser.class, conn));
	}

	public Map<Object, Object> toMap(TfWebsiteObject entry) {
		validate(entry);
		final HashMap<Object, Object> sink = new HashMap<Object, Object>();
		for (ValuedParam<Valued> i : entry.askSchema().askValuedParams()) {
			Object o = i.getValue(entry);
			if (o != null) {
				if (i.getReturnType() == float.class && MH.isntNumber((Float) o))
					o = null;
				else if (i.getReturnType() == double.class && MH.isntNumber((Double) o))
					o = null;
			}
			sink.put(i.getName(), o);
		}
		sink.put("active", entry.getRevision() != TfWebsiteObject.REVISION_DELETED);
		return sink;
	}

	public static void validate(TfWebsiteObject entry) {
		if (!isValidTime(entry.getCreatedOn(), false))
			throw new RuntimeException("Bad createdOn: " + entry);
		if (!isValidTime(entry.getModifiedOn(), false))
			throw new RuntimeException("Bad modifiedOn: " + entry);
		if (OH.isntBetween(entry.getRevision(), 0, TfWebsiteObject.REVISION_DELETED))
			throw new RuntimeException("Bad revision: " + entry);
		if (entry.getModifiedOn() < entry.getCreatedOn())
			throw new RuntimeException("Bad modifiedOn (before createdOn): " + entry);
		if (entry.getRevision() == 0 && entry.getModifiedOn() != entry.getCreatedOn())
			throw new RuntimeException("Bad modifiedOn (after createdOn but revision is zero): " + entry);
	}

	public static boolean isValidTime(long timestamp, boolean allowZero) {
		return (allowZero && timestamp == 0) || (OH.isBetween(timestamp, 100000000000L, 10000000000000L));
	}

	public long nextId() {
		return fountain.createNextId();
	}

}
