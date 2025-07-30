package com.f1.ami.center.table;

import java.util.Set;

import com.f1.ami.center.table.index.AmiQueryFinder;

/**
 * A specialized clause for an in clause, ex: value in (a,b,c,...)
 * <P>
 * Note, it is expected that {@link #setValues(Comparable)} may/will be called before each call to {@link AmiTable#query(AmiPreparedQuery)} for the owning {@link AmiPreparedQuery}.
 * 
 */
public interface AmiPreparedQueryInClause extends AmiQueryFinder {

	/**
	 * Sets the values that will be used for queries in this clause.
	 * 
	 * @param value
	 *            the values to set the query clause to
	 */
	public void setValues(Set<Comparable> value);
	/**
	 * @return the values that will be used for queries
	 */
	public Set<Comparable> getValues();

}
