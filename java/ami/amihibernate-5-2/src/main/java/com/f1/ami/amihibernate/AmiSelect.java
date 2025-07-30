package com.f1.ami.amihibernate;
/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.sql.SelectFragment;

/**
 * A simple SQL <tt>SELECT</tt> statement
 * 
 * @author Gavin King, Modified by George @3forge
 */
public class AmiSelect {

	//	private Select sel;
	private String selectClause;
	private String fromClause;
	private String outerJoinsAfterFrom;
	private String whereClause;
	private String outerJoinsAfterWhere;
	private String orderByClause;
	private String groupByClause;
	private String comment;
	private LockOptions lockOptions = new LockOptions();
	public final Dialect dialect;

	private int guesstimatedBufferSize = 20;

	public AmiSelect(Dialect dialect) {
		this.dialect = dialect;
	}

	/**
	 * Construct an SQL <tt>SELECT</tt> statement from the given clauses
	 */
	public String toStatementString() {
		StringBuilder buf = new StringBuilder(guesstimatedBufferSize);
		if (StringHelper.isNotEmpty(comment)) {
			buf.append("/* ").append(comment).append(" */ ");
		}

		buf.append("select ").append(selectClause).append(" from ").append(fromClause);

		if (StringHelper.isNotEmpty(outerJoinsAfterFrom)) {
			buf.append(outerJoinsAfterFrom);
		}

		if (StringHelper.isNotEmpty(whereClause) || StringHelper.isNotEmpty(outerJoinsAfterWhere)) {
			buf.append(" where ");
			// the outerJoinsAfterWhere needs to come before where clause to properly
			// handle dynamic filters
			if (StringHelper.isNotEmpty(outerJoinsAfterWhere)) {
				buf.append(outerJoinsAfterWhere);
				if (StringHelper.isNotEmpty(whereClause)) {
					buf.append(" and ");
				}
			}
			if (StringHelper.isNotEmpty(whereClause)) {
				buf.append(whereClause);
			}
		}

		if (StringHelper.isNotEmpty(groupByClause)) {
			buf.append(" group by ").append(groupByClause);
		}

		if (StringHelper.isNotEmpty(orderByClause)) {
			buf.append(" order by ").append(orderByClause);
		}

		if (lockOptions.getLockMode() != LockMode.NONE) {
			buf.append(dialect.getForUpdateString(lockOptions));
		}

		return dialect.transformSelectString(buf.toString());
	}

	/**
	 * Sets the fromClause.
	 * 
	 * @param fromClause
	 *            The fromClause to set
	 */
	public AmiSelect setFromClause(String fromClause) {
		//		System.out.println(fromClause);
		this.fromClause = fromClause;
		this.guesstimatedBufferSize += fromClause.length();
		return this;
	}

	public AmiSelect setFromClause(String tableName, String alias) {
		this.fromClause = tableName + " as " + alias;
		this.guesstimatedBufferSize += fromClause.length();
		return this;
	}

	public AmiSelect setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
		this.guesstimatedBufferSize += orderByClause.length();
		return this;
	}

	public AmiSelect setGroupByClause(String groupByClause) {
		this.groupByClause = groupByClause;
		this.guesstimatedBufferSize += groupByClause.length();
		return this;
	}

	public AmiSelect setOuterJoins(String outerJoinsAfterFrom, String outerJoinsAfterWhere) {
		this.outerJoinsAfterFrom = outerJoinsAfterFrom;

		// strip off any leading 'and' token
		String tmpOuterJoinsAfterWhere = outerJoinsAfterWhere.trim();
		if (tmpOuterJoinsAfterWhere.startsWith("and")) {
			tmpOuterJoinsAfterWhere = tmpOuterJoinsAfterWhere.substring(4);
		}
		this.outerJoinsAfterWhere = tmpOuterJoinsAfterWhere;

		this.guesstimatedBufferSize += outerJoinsAfterFrom.length() + outerJoinsAfterWhere.length();
		return this;
	}

	/**
	 * Sets the selectClause.
	 * 
	 * @param selectClause
	 *            The selectClause to set
	 */
	public AmiSelect setSelectClause(String selectClause) {
		this.selectClause = selectClause;
		this.guesstimatedBufferSize += selectClause.length();
		return this;
	}

	public AmiSelect setSelectClause(SelectFragment selectFragment) {
		setSelectClause(selectFragment.toFragmentString().substring(2));
		return this;
	}

	/**
	 * Sets the whereClause.
	 * 
	 * @param whereClause
	 *            The whereClause to set
	 */
	public AmiSelect setWhereClause(String whereClause) {
		this.whereClause = whereClause;
		this.guesstimatedBufferSize += whereClause.length();
		return this;
	}

	public AmiSelect setComment(String comment) {
		this.comment = comment;
		this.guesstimatedBufferSize += comment.length();
		return this;
	}

	/**
	 * Get the current lock mode
	 * 
	 * @return LockMode
	 * @deprecated Instead use getLockOptions
	 */
	@Deprecated
	public LockMode getLockMode() {
		return lockOptions.getLockMode();
	}

	/**
	 * Set the lock mode
	 * 
	 * @param lockMode
	 * @return this object
	 * @deprecated Instead use setLockOptions
	 */
	@Deprecated
	public AmiSelect setLockMode(LockMode lockMode) {
		lockOptions.setLockMode(lockMode);
		return this;
	}

	/**
	 * Get the current lock options
	 * 
	 * @return LockOptions
	 */
	public LockOptions getLockOptions() {
		return lockOptions;
	}

	/**
	 * Set the lock options
	 * 
	 * @param lockOptions
	 * @return this object
	 */
	public AmiSelect setLockOptions(LockOptions lockOptions) {
		LockOptions.copy(lockOptions, this.lockOptions);
		return this;
	}
}
