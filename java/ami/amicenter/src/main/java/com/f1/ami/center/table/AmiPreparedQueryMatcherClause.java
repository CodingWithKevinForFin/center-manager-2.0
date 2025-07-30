package com.f1.ami.center.table;

import com.f1.ami.center.table.index.AmiQueryFinder;
import com.f1.utils.Matcher;

/**
 * A specialized clause for an in clause, ex: value in (a,b,c,...)
 * <P>
 * Note, it is expected that {@link #setValues(Comparable)} may/will be called before each call to {@link AmiTable#query(AmiPreparedQuery)} for the owning {@link AmiPreparedQuery}.
 * 
 */
public interface AmiPreparedQueryMatcherClause extends AmiQueryFinder {

	/**
	 * Sets the values that will be used for queries in this clause.
	 * 
	 * @param value
	 *            the values to set the query clause to
	 */
	public void setMatcher(Matcher<? extends Comparable<?>> value);
	/**
	 * @return the values that will be used for queries
	 */
	public Matcher<? extends Comparable<?>> getMatcher();

}
