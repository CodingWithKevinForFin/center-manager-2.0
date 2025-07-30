package com.f1.ami.center.table;

import com.f1.ami.center.table.index.AmiQueryFinder;

/**
 * A specialized clause for a simple ordinal comparison, ex: less than, equal to, etc. Note the column is on the left side and the value (see {@link #setValue(Comparable)}) is on
 * the right side. So LT evaluates to "column is less than value".
 * <P>
 * Note, it is expected that {@link #setValue(Comparable)} may/will be called before each call to {@link AmiTable#query(AmiPreparedQuery)} for the owning {@link AmiPreparedQuery}.
 * 
 */
public interface AmiPreparedQueryCompareClause extends AmiQueryFinder {

	/**
	 * Is Equal to
	 */
	static final public byte EQ = 0;
	/**
	 * Not Equal to
	 */
	static final public byte NE = 1;
	/**
	 * Greater than or Equal to
	 */
	static final public byte GE = 2;
	/**
	 * Greater than
	 */
	static final public byte GT = 3;
	/**
	 * Less than
	 */
	static final public byte LT = 4;
	/**
	 * Less than or Equal to
	 */
	static final public byte LE = 5;

	/**
	 * Sets the value that will used for queries in this clause.
	 * 
	 * @param value
	 *            the value to set the query clause to
	 */
	public void setValue(Comparable value);

	/**
	 * @return the value that will be used for queries
	 */
	public Comparable getValue();

	/**
	 * @return the type of comparison this is, see EQ, NE, GE, GT, LT, LE for types
	 */
	public byte getCompareType();

}
