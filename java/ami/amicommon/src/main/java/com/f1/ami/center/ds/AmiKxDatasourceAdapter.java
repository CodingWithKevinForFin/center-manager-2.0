package com.f1.ami.center.ds;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;
import com.f1.utils.structs.table.derived.TimeoutController;

import kx.c;
import kx.c.Flip;
import kx.c.KException;

public class AmiKxDatasourceAdapter implements AmiDatasourceAdapter {
	private static final Logger log = LH.get();

	public static Map<String, String> buildOptions() {
		return Collections.EMPTY_MAP;
	}

	private String name;
	private String url;
	private String password;
	private ContainerTools tools;
	private String options;

	private AmiServiceLocator locator;

	private String username;

	@Override
	public void init(ContainerTools tools, AmiServiceLocator locator) {
		this.tools = tools;
		this.locator = locator;
		this.name = locator.getTargetName();
		this.username = locator.getUsername();
		this.url = locator.getUrl();
		this.password = locator.getPassword() == null ? null : new String(locator.getPassword());
		this.options = locator.getOptions();
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		c c = null;
		try {
			c = getConnection();
			String[] tables = (String[]) this.exec(c, "tables`.", debugSink);
			List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>(tables.length);
			for (String t : tables) {
				try {
					AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);
					table.setName(t);
					r.add(table);
				} catch (Exception e) {
					LH.warning(log, "Error with table: ", t, e);
					continue;
				}
			}
			return r;
		} catch (AmiDatasourceException e) {
			throw e;
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Could not connect to datasource: " + url, e);
		} finally {
			if (c != null)
				try {
					c.close();
				} catch (IOException e) {
				}
		}
	}

	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> requestTables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		c c = null;
		try {
			c = getConnection();

			List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>(requestTables.size());
			for (int i = 0; i < requestTables.size(); i++) {
				AmiDatasourceTable t = requestTables.get(i);
				try {
					AmiDatasourceTable table = this.getSchema(c, t.getName(), t.getName(), debugSink);
					r.add(table);
					// query for the preview; handles hdb splayed by date + in mem ones
					String q = "$[`date in key `.;min[(COUNT,exec x from select count i from TABLE where date=max date)]#0!select from TABLE where date=max date, i<COUNT;min[(COUNT;count TABLE)]#0!TABLE]";
					q = q.replaceAll("COUNT", SH.toString(previewCount));
					q = q.replaceAll("TABLE", t.getName());
					AmiCenterQuery query = tools.nw(AmiCenterQuery.class);
					query.setLimit(previewCount);
					query.setQuery(q);
					List<Table> previewData = AmiUtils.processQuery(this.tools, this, query, debugSink, tc);
					table.setPreviewData(previewData.get(i));
				} catch (Exception e) {
					LH.warning(log, "Error with table: ", t, e);
					continue;
				}
			}
			return r;
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Could not connect to datasource: " + url, e);
		} finally {
			if (c != null)
				try {
					c.close();
				} catch (IOException e) {
				}
		}
	}

	private AmiDatasourceTable getSchema(c c, String tname, String customExpression, AmiDatasourceTracker debugSink) throws Exception {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);
		table.setName(tname);
		c.Flip s = (Flip) exec(c, "0!meta " + customExpression, debugSink);
		table.setColumns(new ArrayList<AmiDatasourceColumn>(s.x.length));
		StringBuilder cq = new StringBuilder("select ");
		for (int i = 0; i < Array.getLength(s.y[0]); i++) {
			String name = (String) Array.get(s.y[0], i);
			char type = Array.getChar(s.y[1], i);
			AmiDatasourceColumn col = tools.nw(AmiDatasourceColumn.class);
			col.setName(name);

			// @todo: what to do about date/time/etc...?
			col.setType(AmiKxHelper.getAmiTypeForKxType(type));
			if (i > 0)
				cq.append(", ");
			cq.append(name);

			table.getColumns().add(col);
		}
		cq.append(" from ").append(table.getName()).append(" where ${WHERE}");
		table.setCustomQuery(cq.toString());

		return table;
	}

	private c getConnection() throws AmiDatasourceException {
		try {
			final int i = this.url.indexOf(':');
			final String host = this.url.substring(0, i);
			final int port = SH.parseInt(url, i + 1, url.length(), 10);
			return new c(host, port, this.password == null ? this.username : this.username + ":" + this.password);
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Could not connect to datasource: " + url, e);
		}
	}

	private Object exec(c c, String sql, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		return exec(c, sql, null, tracker);
	}

	private Object exec(c c, String func, Object arg[], AmiDatasourceTracker debugSink) throws AmiDatasourceException {
		try {
			LH.info(log, "sql- ", func, " arg - ", Arrays.toString(arg));
			if (arg == null || arg.length == 0)
				return c.k(OH.toString(func));
			else {
				Object arg2 = AH.insert(arg, 0, func.toCharArray());
				return c.k(arg2);
			}
		} catch (KException e) {
			throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, OH.toString(func), e);
		} catch (IOException e) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, e);
		}
	}

	private static final String LMT_FUNC = "{[lmt;x] $[.Q.qt[x]|type[x] in `short$til[21];(lmt & count x)#x;x]}";
	private static final String E_FUNC = "{[v] @[value;v;{'\"Failed to execute \", .Q.s[x], \" Error - \", y}[v]]}";

	private Table query(String customQuery, String table, Map<String, Object> directives, int limit, AmiDatasourceTracker debug) throws AmiDatasourceException {
		String query = customQuery;
		if (limit >= 0) {
			query = LMT_FUNC + "[" + limit + "] " + query;
		}

		Object args[] = null;
		if (directives != null) {
			int maxPos = -1;
			IntKeyMap<String> params = null;
			for (Entry<String, Object> s : directives.entrySet())
				if (s.getKey().startsWith("param")) {
					int pos;
					try {
						pos = SH.parseInt(SH.stripPrefix(s.getKey(), "param", true)) - 1;
					} catch (Exception e) {
						throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, "Bad param suffix: '" + s.getKey() + "'");
					}
					if (pos < 0)
						throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, "Bad param suffix: '" + s.getKey() + "'");
					maxPos = Math.max(maxPos, pos);
					if (params == null)
						params = new IntKeyMap<String>();
					params.put(pos, (String) s.getValue());
				}
			if (maxPos != -1) {
				args = new Object[maxPos + 1];
				for (Node<String> i : params)
					args[i.getKey()] = i.getValue().toCharArray();
				int missing = AH.indexOf(null, args);
				if (missing != -1)
					throw new AmiDatasourceException(AmiDatasourceException.SCHEMA_ERROR, "Missing param: param" + (missing + 1));
			}

			if (directives.containsKey("func")) {
				String f = AmiDatasourceUtils.getOptional(directives, "func");

				args = args == null ? new Object[0] : args;
				query = f;
			}
		}

		c c = null;
		LH.info(log, "Running query on ", url, " ==> ", query);
		try {
			c = getConnection();
			Object o = exec(c, query, args, debug);

			LH.info(log, "Query came back from kdb...creating in mem table");

			Table r = AmiKxHelper.toTable(table, o, limit);
			LH.info(log, "Query returned ", r.getSize(), " row(s)");
			return r;
		} catch (AmiDatasourceException e) {
			throw e;
		} finally {
			if (c != null)
				try {
					c.close();
				} catch (IOException e) {
					LH.warning(log, "Failed to close KX connection");
				}
		}
	}
	public String getName() {
		return name;
	}

	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debug, TimeoutController tc) throws AmiDatasourceException {
		List<Table> r = new ArrayList<Table>();
		try {
			String select = query.getQuery();
			Table rs = query(select, name, query.getDirectives(), query.getLimit(), debug);
			if (SH.isnt(rs.getTitle()))
				rs.setTitle(extractTableName(select));
			//check if this is table of tables ... i.e. dict of name-> table
			if (rs.getSize() == 1) {
				boolean all = true;
				for (int i = 0; i < rs.getColumnsCount(); i++)
					if (!Table.class.isAssignableFrom(rs.getAt(0, i).getClass())) {
						all = false;
						break;
					}

				if (all) {
					for (int i = 0; i < rs.getColumnsCount(); i++)
						r.add((Table) rs.getAt(0, i));

					resultSink.setTables(r);
				}
			}

			// check if this was a list of tables...represented by one column table of tables...assign name to each tbl from name split by ','
			String[] na = SH.trimArray(SH.split(',', name));
			if (rs.getColumnsCount() == 1 && na.length == rs.getSize()) {
				//check that all values are Tables

				boolean all = true;
				for (int i = 0; i < rs.getSize(); i++)
					if (!Table.class.isAssignableFrom(rs.getAt(i, 0).getClass())) {
						all = false;
						break;
					}

				if (all) {
					for (int i = 0; i < rs.getSize(); i++)
						r.add((Table) rs.getAt(i, 0));

					resultSink.setTables(r);
					return;
				}
			}

			// if it isn't a table of tables (dict name -> table) or a list of table represented by one column table of tables >>> then it is a table with multiple columns and we must add it to the result
			r.add(rs);
		} catch (AmiDatasourceException e) {
			throw e;
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, e);
		}
		resultSink.setTables(r);
		return;
	}
	private String extractTableName(String select) {
		StringCharReader cr = new StringCharReader(select);
		cr.setCaseInsensitive(true);
		cr.readUntilSequence(" from ", null);
		if (cr.expectSequenceNoThrow(" from ")) {
			StringBuilder sb = new StringBuilder();
			cr.readWhileAny(StringCharReader.ALPHA_NUM_UNDERBAR, sb);
			if (sb.length() > 0)
				return sb.toString();
		}
		return "Table1";
	}

	@Override
	public boolean cancelQuery() {
		return false;
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult results, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload to datasource");
	}

	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.locator;
	}

}
