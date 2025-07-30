package com.f1.ami.center.table;

import com.f1.ami.center.table.index.AmiQueryFinder;

/**
 * A specialized clause for a "between" query: greater than [some min value] and less than [some max value]. The inclusive/exclusive nature of the min/max values is determined in
 * {@link AmiPreparedQuery#addBetween(AmiColumn, boolean, boolean)}
 * <P>
 * Note, it is expected that {@link #setMinMax(Comparable,Comparable)} may/will be called before each call to {@link AmiTable#query(AmiPreparedQuery)} for the owning
 * {@link AmiPreparedQuery}
 * 
 */
public interface AmiPreparedQueryBetweenClause extends AmiQueryFinder {

	/**
	 * convenience method for calling both {@link #setMin(Comparable)} and {@link #setMax(Comparable)} in one method call
	 * 
	 * @param min
	 *            lower bound
	 * @param max
	 *            upper bound
	 */
	public void setMinMax(Comparable min, Comparable max);

	/**
	 * 
	 * @return lower bound of between query
	 */
	public Comparable getMin();

	/**
	 * 
	 * @return lower upper of between query
	 */
	public Comparable getMax();
}
