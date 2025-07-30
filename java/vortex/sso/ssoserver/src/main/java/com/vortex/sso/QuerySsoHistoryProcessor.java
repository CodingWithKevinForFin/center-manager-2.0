package com.vortex.sso;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.LH;
import com.sso.messages.QuerySsoHistoryRequest;
import com.sso.messages.QuerySsoHistoryResponse;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoUser;

public class QuerySsoHistoryProcessor extends BasicRequestProcessor<QuerySsoHistoryRequest, SsoState, QuerySsoHistoryResponse> {

	private DataSource dbsource;

	public QuerySsoHistoryProcessor(DataSource dbsource) {
		super(QuerySsoHistoryRequest.class, SsoState.class, QuerySsoHistoryResponse.class);
		this.dbsource = dbsource;
	}

	@Override
	protected QuerySsoHistoryResponse processRequest(RequestMessage<QuerySsoHistoryRequest> action, SsoState state, ThreadScope threadScope) throws Exception {
		final QuerySsoHistoryRequest request = action.getAction();
		final QuerySsoHistoryResponse response = nw(QuerySsoHistoryResponse.class);
		final List<Long> ids = request.getIds();
		String idString = ids.toString();
		Connection conn = null;
		switch (request.getType()) {
			case QuerySsoHistoryRequest.TYPE_USER:
				response.setUser(getSsoUsers(conn, idString));
				response.setOk(true);
				break;
			case QuerySsoHistoryRequest.TYPE_GROUP_PARENT_GROUPS:
			case QuerySsoHistoryRequest.TYPE_USER_PARENT_GROUPS:
				response.setParentGroups(getSsoParents(conn, idString));
				response.setOk(true);
				break;
			default:
				response.setOk(false);
				response.setMessage("Invalid request");
				break;
		}
		return response;
	}

	private Map<Long, List<SsoGroup>> getSsoParents(Connection conn, String idString) throws SQLException {
		final Map<Long, List<SsoGroup>> users = new HashMap<Long, List<SsoGroup>>();
		try {
			conn = dbsource.getConnection();
			Statement stmt = conn.createStatement();
			String query = "SELECT * FROM sso.SsoGroupMembers where sso.SsoGroupMembers.member_id in (" + idString.substring(1, idString.length() - 1) + ")";
			ResultSet set = stmt.executeQuery(query);
			while (set.next()) {
				SsoGroup user = nw(SsoGroup.class);
				long memberId = set.getLong("member_id");
				user.setId(set.getLong("group_id"));
				user.setNow(set.getLong("now"));
				user.setRevision(set.getInt("revision"));
				if (users.containsKey(memberId))
					users.get(memberId).add(user);
				else {
					List<SsoGroup> listUsers = new ArrayList<SsoGroup>();
					listUsers.add(user);
					users.put(memberId, listUsers);
				}
			}
		} catch (Exception e) {
			LH.warning(log, "Could not query SsoGroupMembers tables");
		} finally {
			conn.close();
		}
		return users;
	}

	public Map<Long, List<SsoUser>> getSsoUsers(Connection conn, String idString) throws SQLException {
		final Map<Long, List<SsoUser>> users = new HashMap<Long, List<SsoUser>>();
		try {
			conn = dbsource.getConnection();
			Statement stmt = conn.createStatement();
			String query = "SELECT * FROM sso.SsoUser where sso.SsoUser.id in (" + idString.substring(1, idString.length() - 1) + ")";
			ResultSet set = stmt.executeQuery(query);
			while (set.next()) {
				SsoUser user = nw(SsoUser.class);
				long id = set.getLong("id");
				user.setId(id);
				user.setNow(set.getLong("now"));
				user.setExpires(set.getLong("expires"));
				user.setRevision(set.getInt("revision"));
				user.setUserName(set.getString("user_name"));
				user.setFirstName(set.getString("first_name"));
				user.setLastName(set.getString("last_name"));
				user.setPhoneNumber(set.getString("phone_number"));
				user.setPassword(set.getString("password"));
				user.setEmail(set.getString("email"));
				user.setCompany(set.getString("company"));
				user.setResetQuestion(set.getString("reset_question"));
				user.setResetAnswer(set.getString("reset_answer"));
				user.setMaxBadAttempts(set.getInt("max_bad_attempts"));
				user.setStatus(set.getByte("status"));
				user.setEncodingAlgorithm(set.getByte("encoding_algorithm"));
				if (users.containsKey(id))
					users.get(id).add(user);
				else {
					List<SsoUser> listUsers = new ArrayList<SsoUser>();
					listUsers.add(user);
					users.put(id, listUsers);
				}
			}
		} catch (Exception e) {
			LH.warning(log, "Could not query SsoUser tables");
		} finally {
			conn.close();
		}
		return users;
	}
}
