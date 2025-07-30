package com.f1.ami.center.table;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;

/**
 * A Prepared query is a way of VERY quickly finding records by supplying a combination of conditions (the combinations are and'ed together). The trick is to build the the prepared
 * query once at startup and then plug in the conditional parameters at runtime and execute the query. Here are the steps:<BR>
 * <B>A) To Create the query (this should be done once at startup):</B><BR>
 * 1) Create a prepared query using {@link AmiTable#createAmiPreparedQuery()}<BR>
 * 2) Add the various conditionals to the newly created query in step a.1 using the various addXX methods in this class, hold onto the clauses that are returned<BR>
 * <P>
 * <B>B) To execute the query (at runtime):</B><BR>
 * 1) For each of the {@link AmiPreparedQueryClause}s returned in step a.2 set the value you are searching for/against<BR>
 * 2.i) Call {@link AmiTable#query(AmiPreparedQuery, int, java.util.List)} with prepared query on step a.1 and iterate over results from supplied sink (3rd argument) <BR>
 * 2.ii) Or if you need only expect a single value call {@link AmiTable#query(AmiPreparedQuery)}
 * <P>
 * On first execute (after mutating the AMI Prepared query) the framework will find the ideal index to use. If none are available then a forward scan will be used (this obviously
 * runs in N time).
 * <P>
 * For Example:<BR>
 * 
 * <PRE>
 * A PreparedQuery might represent: select from Orders where (price < 100) and (quantity between 100 and 200) and (symbol in ("MSFT","IBM","APPL"))
 * </PRE>
 * <P>
 * In this case, the query has 3 query clauses: Lt, between and in
 * 
 */
public interface AmiPreparedQuery {

	/**
	 * Create an <B>equal to</B> query clause where only rows whose column's value matched supplied value. See {@link AmiPreparedQueryCompareClause#setValue(Comparable)}.
	 * <P>
	 * In other words, let's say you wanted to create a prepared search where px == [some value]. Use this method and pass in the "px" column.
	 * 
	 * @param column
	 *            the column to compare to
	 * @return the prepared clause for this comparison.
	 */
	public AmiPreparedQueryCompareClause addEq(AmiColumn column);
	/**
	 * Create a <B>not equal to</B> query clause where only rows whose column's value matched supplied value for said condition. See
	 * {@link AmiPreparedQueryCompareClause#setValue(Comparable)}.
	 * <P>
	 * In other words, let's say you wanted to create a prepared search where px!= [some value]. Use this method and pass in the "px" column.
	 * 
	 * @param column
	 *            the column to compare to
	 * @return the prepared clause for this comparison.
	 */
	public AmiPreparedQueryCompareClause addNe(AmiColumn column);
	/**
	 * Create a <B>greater than</B> query clause where only rows whose column's value matched supplied value for said condition. See
	 * {@link AmiPreparedQueryCompareClause#setValue(Comparable)}.
	 * <P>
	 * In other words, let's say you wanted to create a prepared search where px > [some value]. Use this method and pass in the "px" column.
	 * 
	 * @param column
	 *            the column to compare to
	 * @return the prepared clause for this comparison.
	 */
	public AmiPreparedQueryCompareClause addGt(AmiColumn column);
	/**
	 * Create an <B>less than</B> query clause where only rows whose column's value matched supplied value for said condition. See
	 * {@link AmiPreparedQueryCompareClause#setValue(Comparable)}.
	 * <P>
	 * In other words, let's say you wanted to create a prepared search where px < [some value]. Use this method and pass in the "px" column.
	 * 
	 * @param column
	 *            the column to compare to
	 * @return the prepared clause for this comparison.
	 */
	public AmiPreparedQueryCompareClause addLt(AmiColumn column);
	/**
	 * Create an <B>greater than or equal to</B> query clause where only rows whose column's value matched supplied value for said condition. See
	 * {@link AmiPreparedQueryCompareClause#setValue(Comparable)}.
	 * <P>
	 * In other words, let's say you wanted to create a prepared search where px >= [some value]. Use this method and pass in the "px" column.
	 * 
	 * @param column
	 *            the column to compare to
	 * @return the prepared clause for this comparison.
	 */
	public AmiPreparedQueryCompareClause addGe(AmiColumn column);
	/**
	 * Create an <B>less than or equal to</B> query clause where only rows whose column's value matched supplied value for said condition. See
	 * {@link AmiPreparedQueryCompareClause#setValue(Comparable)}.
	 * <P>
	 * In other words, let's say you wanted to create a prepared search where px <= [some value]. Use this method and pass in the "px" column
	 * 
	 * @param column
	 *            the column to compare to
	 * @return the prepared clause for this comparison.
	 */
	public AmiPreparedQueryCompareClause addLe(AmiColumn column);

	/**
	 * internal, proprietary
	 */
	public AmiPreparedQueryCompareClause addCompare(AmiColumn column, byte type);

	/**
	 * Create a <B>greater than [or equal to] and less than [or equal to]</B> query clause, where only rows whose column's value matched supplied values for said condition. This is
	 * highly optimized within all 3 indexes, vs using 2 conditionals. See {@link AmiPreparedQueryBetweenClause#setMinMax(Comparable, Comparable)}
	 * <P>
	 * In other words, let's say you wanted to create a prepared search where px >= [some value] and px <= [some other value]. Use this method and pass in the "px" column.
	 * 
	 * @param column
	 *            the column to compare to
	 * @return the prepared clause for this comparison.
	 */
	public AmiPreparedQueryBetweenClause addBetween(AmiColumn column, boolean minInclusive, boolean maxInclusive);

	/**
	 * Create an <B>match</B> (=~) query clause where only rows whose column's value matched supplied value for said condition. See
	 * {@link AmiPreparedQueryCompareClause#setValue(Comparable)}.
	 * <P>
	 * In other words, let's say you wanted to create a prepared search where name =~ [some expression]. Use this method and pass in the "name" column
	 * 
	 * @param column
	 *            the column to compare to
	 * @return the prepared clause for this comparison.
	 */
	public AmiPreparedQueryMatcherClause addMatcher(AmiColumn column);

	/**
	 * create a <B>in [a set of values]</B> query clause, where only rows whose column's value match one of supplied values. This is highly optimized within all 3 indexes, vs using
	 * many equal clauses. See {@link AmiPreparedQueryInClause#setValues(java.util.Set)}
	 * <P>
	 * In other words, let's say you wanted to create a prepared search where px is in [value1,value2 or value3]. Use this method and pass in the "px" column.
	 * 
	 * @param column
	 *            the column to compare to
	 * @return the prepared clause for this comparison.
	 */
	public AmiPreparedQueryInClause addIn(AmiColumn column);
	void addExpression(DerivedCellCalculator dcc);

	/**
	 * @return the table that this prepared query applies to
	 */
	public AmiTable getAmiTable();

}
