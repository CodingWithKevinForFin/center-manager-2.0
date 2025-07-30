package com.f1.ami.center;

import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiImdbSessionManagerService;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.BlockNode;
import com.f1.utils.string.sqlnode.AdminNode;
import com.f1.utils.string.sqlnode.AsNode;
import com.f1.utils.string.sqlnode.CreateTableNode;
import com.f1.utils.string.sqlnode.InsertNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.structs.table.derived.DerivedHelper;

public class AmiCenterParseQueryProcessor extends BasicRequestProcessor<AmiCenterQueryDsRequest, State, AmiCenterQueryDsResponse> {

	private OutputPort<RequestMessage<AmiCenterQueryDsRequest>> forward = newRequestOutputPort(AmiCenterQueryDsRequest.class, AmiCenterQueryDsResponse.class);
	private boolean enableConcurrentQueries;
	private AmiImdbSessionManagerService sessionManager;

	public AmiCenterParseQueryProcessor(AmiImdbSessionManagerService sm) {
		super(AmiCenterQueryDsRequest.class, State.class, AmiCenterQueryDsResponse.class);
		this.sessionManager = sm;
	}

	@Override
	public void init() {
		this.enableConcurrentQueries = getTools().getOptional(AmiCenterProperties.PROPERTY_AMI_CENTER_ENABLE_CONCURRENT_QUERIES, Boolean.TRUE);
		LH.info(log, "Concurrent queries to imdb ", this.enableConcurrentQueries ? "enabled" : "disabled");
		super.init();
	}

	@Override
	protected AmiCenterQueryDsResponse processRequest(RequestMessage<AmiCenterQueryDsRequest> action, State st, ThreadScope threadScope) throws Exception {
		AmiCenterQueryDsRequest action2 = action.getAction();
		byte permissions = action2.getPermissions();
		if (!MH.areAllBitsSet(permissions, AmiCenterQueryDsRequest.PERMISSIONS_READ)) {
			AmiCenterQueryDsResponse r = nw(AmiCenterQueryDsResponse.class);
			r.setMessage("Permission Denied, READ required");
			r.setOk(false);
			return r;
		}
		try {
			if (action2.getType() == AmiCenterQueryDsRequest.TYPE_QUERY) {
				long qsid = action2.getQuerySessionId();
				AmiImdbSession session = null;
				if (qsid > 0) {
					if (!action2.getQuerySessionKeepAlive())
						this.sessionManager.removeSession(qsid);
					else
						session = this.sessionManager.getSession(qsid);
				} else if (action2.getQuerySessionKeepAlive()) {
					session = this.sessionManager.newSession(AmiTableUtils.DEFTYPE_USER, action2.getOriginType(), action2.getInvokedBy(),
							AmiTableUtils.toStringForOriginType(action2.getOriginType()), permissions, action2.getTimeoutMs(), action2.getLimit(),
							DerivedHelper.toFrame(action2.getSessionVariables(), action2.getSessionVariableTypes()));
					if (session != null)
						action2.setQuerySessionId(session.getSessionId());
				}
				String queryStr = action2.getQuery();
				if (queryStr == null) {
					AmiCenterQueryDsResponse r = nw(AmiCenterQueryDsResponse.class);
					r.setTables(null);
					r.setRowsEffected(0);
					r.setOk(true);
					if (session != null)
						r.setQuerySessionId(session.getSessionId());
					return r;
				}
				if (OH.eq(action2.getDatasourceName(), "AMI") && !SH.startsWith(queryStr, "QUERY ")) {
					//					if (action2.getLimit() != AmiCenterQueryDsRequest.NO_LIMIT)
					//						queryStr = queryStr + " LIMIT " + action2.getLimit();
					final Node node;
					try {
						SqlExpressionParser sep = new SqlExpressionParser();
						sep.setAllowSqlInjection(action2.getAllowSqlInjection());
						node = sep.parse(queryStr);
					} catch (StackOverflowError e) {
						LH.info(log, "Too many expression: ", e);
						throw new ExpressionParserException(queryStr, 0, "Expression has too many phrases");
					}
					action2.setParsedNode(node);
					if (enableConcurrentQueries) {
						if (permissions == AmiCenterQueryDsRequest.PERMISSIONS_READ || isReadonly(node, session))
							action2.setUseConcurrency(true);
					}
				} else if (enableConcurrentQueries)
					action2.setUseConcurrency(true);

			}
			forward.send(action, threadScope);
			return null;
		} catch (Exception e) {
			LH.info(log, e);
			AmiCenterQueryDsResponse r = nw(AmiCenterQueryDsResponse.class);
			if (e instanceof ExpressionParserException) {
				ExpressionParserException epe = (ExpressionParserException) e;
				r.setMessage(epe.toLegibleString());
			} else
				r.setMessage(e.getMessage());
			r.setException(e);
			r.setQuerySessionId(action2.getQuerySessionId());
			return r;
		}
	}
	private boolean isReadonly(Node node, AmiImdbSession session) {
		if (node instanceof BlockNode) {
			BlockNode bn = (BlockNode) node;
			for (int i = 0; i < bn.getNodesCount(); i++)
				if (!isReadonly(bn.getNodeAt(i), session))
					return false;
			for (int i = 0; i < bn.getCatchNodesCount(); i++)
				if (!isReadonly(bn.getCatchNodeAt(i), session))
					return false;
			return true;
		} else if (node instanceof SqlNode) {
			SqlNode sn = (SqlNode) node;
			try {
				switch (sn.getOperation()) {
					case SqlExpressionParser.ID_SELECT:
					case SqlExpressionParser.ID_SHOW:
					case SqlExpressionParser.ID_ANALYZE:
					case SqlExpressionParser.ID_PREPARE:
					case SqlExpressionParser.ID_DESCRIBE:
						return true;
					case SqlExpressionParser.ID_USE:
						return false;
					case SqlExpressionParser.ID_CREATE: {
						if (sn instanceof CreateTableNode) {
							CreateTableNode ctn = (CreateTableNode) sn;
							for (AdminNode an : ctn.getTableDefs())
								if (an.getOptions() != null && an.getOptions().getOperation() == SqlExpressionParser.ID_PUBLIC)
									return false;
							return true;
						} else {
							return false;
						}
					}
					case SqlExpressionParser.ID_INSERT: {
						InsertNode in = (InsertNode) sn;
						if (session == null)
							return false;
						synchronized (session) {
							return session.isTemporaryTable(in.getTablename());
						}
					}
					case SqlExpressionParser.ID_DELETE: {
						SqlColumnsNode fromClause = (SqlColumnsNode) sn;
						if (fromClause.getOperation() == SqlExpressionParser.ID_DELETE) {
							Node tableNameNode = fromClause.getColumnAt(0);
							String tablename;
							if (tableNameNode instanceof AsNode) {
								tablename = ((AsNode) tableNameNode).getValue().toString();
							} else
								tablename = (tableNameNode).toString();
							if (session == null)
								return false;//TODO:is this okay?
							synchronized (session) {
								return session.isTemporaryTable(tablename);
							}
						}
						break;
					}
					case SqlExpressionParser.ID_UPDATE: {
						Node tableNameNode = ((SqlColumnsNode) sn).getColumnAt(0);
						String tablename;
						if (tableNameNode instanceof AsNode) {
							tablename = ((AsNode) tableNameNode).getValue().toString();
						} else
							tablename = (tableNameNode).toString();
						if (session == null)
							return false;
						synchronized (session) {
							return session.isTemporaryTable(tablename);
						}
					}
				}
			} catch (Exception e) {
				LH.info(log, "Unexpected Error determining readonly status so return non-readonly for: ", sn, e);
				return false;
			}
		}
		return false;
	}

	public OutputPort<RequestMessage<AmiCenterQueryDsRequest>> getForward() {
		return this.forward;
	}

}
