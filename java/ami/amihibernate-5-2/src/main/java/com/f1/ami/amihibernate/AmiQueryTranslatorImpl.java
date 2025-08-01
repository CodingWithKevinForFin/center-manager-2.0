package com.f1.ami.amihibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.hql.internal.QueryExecutionRequestException;
import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
import org.hibernate.hql.internal.antlr.HqlTokenTypes;
import org.hibernate.hql.internal.antlr.SqlTokenTypes;
import org.hibernate.hql.internal.ast.HqlParser;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.ParameterTranslationsImpl;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.hql.internal.ast.exec.BasicExecutor;
import org.hibernate.hql.internal.ast.exec.DeleteExecutor;
import org.hibernate.hql.internal.ast.exec.MultiTableDeleteExecutor;
import org.hibernate.hql.internal.ast.exec.MultiTableUpdateExecutor;
import org.hibernate.hql.internal.ast.exec.StatementExecutor;
import org.hibernate.hql.internal.ast.tree.AggregatedSelectExpression;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.InsertStatement;
import org.hibernate.hql.internal.ast.tree.QueryNode;
import org.hibernate.hql.internal.ast.tree.Statement;
import org.hibernate.hql.internal.ast.util.ASTPrinter;
import org.hibernate.hql.internal.ast.util.NodeTraverser;
import org.hibernate.hql.spi.ParameterTranslations;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.IdentitySet;
import org.hibernate.loader.hql.QueryLoader;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

import antlr.ANTLRException;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;

/**
 * A QueryTranslator that uses an Antlr-based parser.
 *
 * @author Joshua Davis (pgmjsd@sourceforge.net) Modified by George Lin for 3forge AMI
 */
public class AmiQueryTranslatorImpl extends QueryTranslatorImpl {
	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, QueryTranslatorImpl.class.getName());

	private SessionFactoryImplementor factory;

	private String hql;
	private boolean shallowQuery;
	private Map tokenReplacements;

	//TODO:this is only needed during compilation .. can we eliminate the instvar?
	private Map enabledFilters;

	private boolean compiled;
	private QueryLoader queryLoader;
	private StatementExecutor statementExecutor;

	private Statement sqlAst;
	private String sql;

	private ParameterTranslations paramTranslations;
	private List<ParameterSpecification> collectedParameterSpecifications;

	public AmiQueryTranslatorImpl(String queryIdentifier, String query, Map enabledFilters, SessionFactoryImplementor factory, EntityGraphQueryHint entityGraphQueryHint) {
		super(queryIdentifier, query, enabledFilters, factory, entityGraphQueryHint);
		//		this.queryIdentifier = queryIdentifier;
		this.hql = query;
		this.compiled = false;
		this.shallowQuery = false;
		this.enabledFilters = enabledFilters;
		this.factory = factory;
	}

	@Override
	public void compile(Map replacements, boolean shallow) throws QueryException, MappingException {
		doCompile(replacements, shallow, null);
	}
	@Override
	public void compile(String collectionRole, Map replacements, boolean shallow) throws QueryException, MappingException {
		doCompile(replacements, shallow, collectionRole);
	}

	private synchronized void doCompile(Map replacements, boolean shallow, String collectionRole) {
		// If the query is already compiled, skip the compilation.
		if (compiled) {
			LOG.debug("compile() : The query is already compiled, skipping...");
			return;
		}

		// Remember the parameters for the compilation.
		this.tokenReplacements = replacements;
		if (tokenReplacements == null) {
			tokenReplacements = new HashMap();
		}
		this.shallowQuery = shallow;

		try {
			// PHASE 1 : Parse the HQL into an AST.
			final HqlParser parser = parse(true);

			// PHASE 2 : Analyze the HQL AST, and produce an SQL AST.
			final HqlSqlWalker w = analyze(parser, collectionRole);

			sqlAst = (Statement) w.getAST();

			// at some point the generate phase needs to be moved out of here,
			// because a single object-level DML might spawn multiple SQL DML
			// command executions.
			//
			// Possible to just move the sql generation for dml stuff, but for
			// consistency-sake probably best to just move responsiblity for
			// the generation phase completely into the delegates
			// (QueryLoader/StatementExecutor) themselves.  Also, not sure why
			// QueryLoader currently even has a dependency on this at all; does
			// it need it?  Ideally like to see the walker itself given to the delegates directly...

			if (sqlAst.needsExecutor()) {
				statementExecutor = buildAppropriateStatementExecutor(w);
			} else {
				// PHASE 3 : Generate the SQL.
				generate((QueryNode) sqlAst);
				queryLoader = new AmiQueryLoader(this, factory, w.getSelectClause());
			}

			compiled = true;
		} catch (QueryException qe) {
			if (qe.getQueryString() == null) {
				throw qe.wrapWithQueryString(hql);
			} else {
				throw qe;
			}
		} catch (RecognitionException e) {
			// we do not actually propagate ANTLRExceptions as a cause, so
			// log it here for diagnostic purposes
			LOG.trace("Converted antlr.RecognitionException", e);
			throw QuerySyntaxException.convert(e, hql);
		} catch (ANTLRException e) {
			// we do not actually propagate ANTLRExceptions as a cause, so
			// log it here for diagnostic purposes
			LOG.trace("Converted antlr.ANTLRException", e);
			throw new QueryException(e.getMessage(), hql);
		} catch (IllegalArgumentException e) {
			// translate this into QueryException
			LOG.trace("Converted IllegalArgumentException", e);
			throw new QueryException(e.getMessage(), hql);
		}

		//only needed during compilation phase...
		this.enabledFilters = null;
	}

	private void generate(AST sqlAst) throws QueryException, RecognitionException {
		if (sql == null) {
			final AmiSqlGenerator gen = new AmiSqlGenerator(factory);
			//TODO:
			//			final SqlGenerator gen = new AmiSqlGenerator(factory);
			gen.statement(sqlAst);
			sql = gen.getSQL();
			if (LOG.isDebugEnabled()) {
				LOG.debugf("HQL: %s", hql);
				LOG.debugf("SQL: %s", sql);
			}
			gen.getParseErrorHandler().throwQueryException();
			collectedParameterSpecifications = gen.getCollectedParameters();
		}
	}

	private static final ASTPrinter SQL_TOKEN_PRINTER = new ASTPrinter(SqlTokenTypes.class);

	private HqlSqlWalker analyze(HqlParser parser, String collectionRole) throws QueryException, RecognitionException {
		final HqlSqlWalker w = new HqlSqlWalker(this, factory, parser, tokenReplacements, collectionRole);
		final AST hqlAst = parser.getAST();

		// Transform the tree.
		w.statement(hqlAst);

		if (LOG.isDebugEnabled()) {
			LOG.debug(SQL_TOKEN_PRINTER.showAsString(w.getAST(), "--- SQL AST ---"));
		}

		w.getParseErrorHandler().throwQueryException();

		return w;
	}

	private HqlParser parse(boolean filter) throws TokenStreamException {
		// Parse the query string into an HQL AST.
		final HqlParser parser = HqlParser.getInstance(hql);
		parser.setFilter(filter);

		LOG.debugf("parse() - HQL: %s", hql);
		try {
			parser.statement();
		} catch (RecognitionException e) {
			throw new HibernateException("Unexpected error parsing HQL", e);
		}

		final AST hqlAst = parser.getAST();
		parser.getParseErrorHandler().throwQueryException();

		final NodeTraverser walker = new NodeTraverser(new JavaConstantConverter(factory));
		walker.traverseDepthFirst(hqlAst);

		showHqlAst(hqlAst);

		return parser;
	}

	private static final ASTPrinter HQL_TOKEN_PRINTER = new ASTPrinter(HqlTokenTypes.class);

	void showHqlAst(AST hqlAst) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(HQL_TOKEN_PRINTER.showAsString(hqlAst, "--- HQL AST ---"));
		}
	}

	private void errorIfDML() throws HibernateException {
		if (this.sqlAst.needsExecutor()) {
			throw new QueryExecutionRequestException("Not supported for DML operations", hql);
		}
	}

	private void errorIfSelect() throws HibernateException {
		if (!this.sqlAst.needsExecutor()) {
			throw new QueryExecutionRequestException("Not supported for select queries", hql);
		}
	}
	@Override
	public Statement getSqlAST() {
		return sqlAst;
	}

	private HqlSqlWalker getWalker() {
		return sqlAst.getWalker();
	}

	/**
	 * Types of the return values of an <tt>iterate()</tt> style query.
	 *
	 * @return an array of <tt>Type</tt>s.
	 */
	@Override
	public Type[] getReturnTypes() {
		errorIfDML();
		return getWalker().getReturnTypes();
	}
	@Override
	public String[] getReturnAliases() {
		errorIfDML();
		return getWalker().getReturnAliases();
	}
	@Override
	public String[][] getColumnNames() {
		errorIfDML();
		return getWalker().getSelectClause().getColumnNames();
	}
	@Override
	public Set<Serializable> getQuerySpaces() {
		return getWalker().getQuerySpaces();
	}

	@Override
	public List list(SharedSessionContractImplementor session, QueryParameters queryParameters) throws HibernateException {
		// Delegate to the QueryLoader...
		errorIfDML();

		final QueryNode query = (QueryNode) sqlAst;
		final boolean hasLimit = queryParameters.getRowSelection() != null && queryParameters.getRowSelection().definesLimits();
		final boolean needsDistincting = (query.getSelectClause().isDistinct() || getEntityGraphQueryHint() != null || hasLimit) && containsCollectionFetches();
		String string = query.getSelectClause().toString();

		QueryParameters queryParametersToUse;
		if (hasLimit && containsCollectionFetches()) {
			boolean fail = session.getFactory().getSessionFactoryOptions().isFailOnPaginationOverCollectionFetchEnabled();
			if (fail) {
				throw new HibernateException("firstResult/maxResults specified with collection fetch. " + "In memory pagination was about to be applied. "
						+ "Failing because 'Fail on pagination over collection fetch' is enabled.");
			} else {
				LOG.firstOrMaxResultsSpecifiedWithCollectionFetch();
			}
			RowSelection selection = new RowSelection();
			selection.setFetchSize(queryParameters.getRowSelection().getFetchSize());
			selection.setTimeout(queryParameters.getRowSelection().getTimeout());
			queryParametersToUse = queryParameters.createCopyUsing(selection);
		} else {
			queryParametersToUse = queryParameters;
		}

		List results = queryLoader.list(session, queryParametersToUse);

		if (needsDistincting) {
			int includedCount = -1;
			// NOTE : firstRow is zero-based
			int first = !hasLimit || queryParameters.getRowSelection().getFirstRow() == null ? 0 : queryParameters.getRowSelection().getFirstRow();
			int max = !hasLimit || queryParameters.getRowSelection().getMaxRows() == null ? -1 : queryParameters.getRowSelection().getMaxRows();
			List tmp = new ArrayList();
			IdentitySet distinction = new IdentitySet();
			for (final Object result : results) {
				if (!distinction.add(result)) {
					continue;
				}
				includedCount++;
				if (includedCount < first) {
					continue;
				}
				tmp.add(result);
				// NOTE : ( max - 1 ) because first is zero-based while max is not...
				if (max >= 0 && (includedCount - first) >= (max - 1)) {
					break;
				}
			}
			results = tmp;
		}

		return results;
	}

	/**
	 * Return the query results as an iterator
	 */
	@Override
	public Iterator iterate(QueryParameters queryParameters, EventSource session) throws HibernateException {
		// Delegate to the QueryLoader...
		errorIfDML();
		return queryLoader.iterate(queryParameters, session);
	}
	/**
	 * Return the query results, as an instance of <tt>ScrollableResults</tt>
	 */
	@Override
	public ScrollableResultsImplementor scroll(QueryParameters queryParameters, SharedSessionContractImplementor session) throws HibernateException {
		// Delegate to the QueryLoader...
		errorIfDML();
		return queryLoader.scroll(queryParameters, session);
	}

	@Override
	public int executeUpdate(QueryParameters queryParameters, SharedSessionContractImplementor session) throws HibernateException {
		errorIfSelect();
		return statementExecutor.execute(queryParameters, session);
	}
	/**
	 * The SQL query string to be called; implemented by all subclasses
	 */
	@Override
	public String getSQLString() {
		return sql;
	}
	@Override
	public List<String> collectSqlStrings() {
		ArrayList<String> list = new ArrayList<String>();
		if (isManipulationStatement()) {
			String[] sqlStatements = statementExecutor.getSqlStatements();
			Collections.addAll(list, sqlStatements);
		} else {
			list.add(sql);
		}
		return list;
	}

	// -- Package local methods for the QueryLoader delegate --

	@Override
	public boolean isShallowQuery() {
		return this.shallowQuery;
	}
	@Override
	public Map getEnabledFilters() {
		return enabledFilters;
	}

	@Override
	public boolean containsCollectionFetches() {
		errorIfDML();
		List collectionFetches = ((QueryNode) this.sqlAst).getFromClause().getCollectionFetches();
		return collectionFetches != null && collectionFetches.size() > 0;
	}

	@Override
	public boolean isManipulationStatement() {
		return this.sqlAst.needsExecutor();
	}
	@Override
	public boolean isUpdateStatement() {
		return SqlTokenTypes.UPDATE == sqlAst.getStatementType();
	}
	@Override
	public void validateScrollability() throws HibernateException {
		// Impl Note: allows multiple collection fetches as long as the
		// entire fecthed graph still "points back" to a single
		// root entity for return

		errorIfDML();

		final QueryNode query = (QueryNode) sqlAst;

		// If there are no collection fetches, then no further checks are needed
		List collectionFetches = query.getFromClause().getCollectionFetches();
		if (collectionFetches.isEmpty()) {
			return;
		}

		// A shallow query is ok (although technically there should be no fetching here...)
		if (isShallowQuery()) {
			return;
		}

		// Otherwise, we have a non-scalar select with defined collection fetch(es).
		// Make sure that there is only a single root entity in the return (no tuples)
		if (getReturnTypes().length > 1) {
			throw new HibernateException("cannot scroll with collection fetches and returned tuples");
		}

		FromElement owner = null;
		for (Object o : query.getSelectClause().getFromElementsForLoad()) {
			// should be the first, but just to be safe...
			final FromElement fromElement = (FromElement) o;
			if (fromElement.getOrigin() == null) {
				owner = fromElement;
				break;
			}
		}

		if (owner == null) {
			throw new HibernateException("unable to locate collection fetch(es) owner for scrollability checks");
		}

		// This is not strictly true.  We actually just need to make sure that
		// it is ordered by root-entity PK and that that order-by comes before
		// any non-root-entity ordering...

		AST primaryOrdering = query.getOrderByClause().getFirstChild();
		if (primaryOrdering != null) {
			// TODO : this is a bit dodgy, come up with a better way to check this (plus see above comment)
			String[] idColNames = owner.getQueryable().getIdentifierColumnNames();
			String expectedPrimaryOrderSeq = StringHelper.join(", ", StringHelper.qualify(owner.getTableAlias(), idColNames));
			if (!primaryOrdering.getText().startsWith(expectedPrimaryOrderSeq)) {
				throw new HibernateException("cannot scroll results with collection fetches which are not ordered primarily by the root entity's PK");
			}
		}
	}

	private StatementExecutor buildAppropriateStatementExecutor(HqlSqlWalker walker) {
		final Statement statement = (Statement) walker.getAST();
		if (walker.getStatementType() == HqlSqlTokenTypes.DELETE) {
			final FromElement fromElement = walker.getFinalFromClause().getFromElement();
			final Queryable persister = fromElement.getQueryable();
			if (persister.isMultiTable()) {
				return new MultiTableDeleteExecutor(walker);
			} else {
				return new DeleteExecutor(walker, persister);
			}
		} else if (walker.getStatementType() == HqlSqlTokenTypes.UPDATE) {
			final FromElement fromElement = walker.getFinalFromClause().getFromElement();
			final Queryable persister = fromElement.getQueryable();
			if (persister.isMultiTable()) {
				// even here, if only properties mapped to the "base table" are referenced
				// in the set and where clauses, this could be handled by the BasicDelegate.
				// TODO : decide if it is better performance-wise to doAfterTransactionCompletion that check, or to simply use the MultiTableUpdateDelegate
				return new MultiTableUpdateExecutor(walker);
			} else {
				return new BasicExecutor(walker, persister);
			}
		} else if (walker.getStatementType() == HqlSqlTokenTypes.INSERT) {
			return new BasicExecutor(walker, ((InsertStatement) statement).getIntoClause().getQueryable());
		} else {
			throw new QueryException("Unexpected statement type");
		}
	}
	@Override
	public ParameterTranslations getParameterTranslations() {
		if (paramTranslations == null) {
			paramTranslations = new ParameterTranslationsImpl(getWalker().getParameters());
		}
		return paramTranslations;
	}
	@Override
	public List<ParameterSpecification> getCollectedParameterSpecifications() {
		return collectedParameterSpecifications;
	}
	@Override
	public Class getDynamicInstantiationResultType() {
		AggregatedSelectExpression aggregation = queryLoader.getAggregatedSelectExpression();
		return aggregation == null ? null : aggregation.getAggregationResultType();
	}
}