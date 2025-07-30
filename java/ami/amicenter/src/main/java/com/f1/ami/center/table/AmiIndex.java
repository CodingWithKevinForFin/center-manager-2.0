package com.f1.ami.center.table;

import com.f1.ami.center.table.index.AmiIndexMap;

/**
 * 
 * An index that is associated with a table and a list of columns. As expected, Indexes are used to speed up frequent, well-known queries. An index can be composite (uses values
 * across columns). The first (zeroth) column is the first column considered, then the second and so on. Each column within the index can have its own type of index (hash, sort,
 * series). See {@link AmiTable#createAmiPreparedQuery()} for using the index in runtime
 */
public interface AmiIndex {

	/**
	 * Unordered search, good for only when you are looking for exact values (not ranges for example). Faster in all cases than the sort index. Backed by a HashMap-like data
	 * structure
	 */
	byte TYPE_HASH = AmiIndexMap.TYPE_HASH;

	/**
	 * Ordered search, good for when you are looking for ranges. Backed by a TreeMap-like data structure
	 */
	byte TYPE_SORT = AmiIndexMap.TYPE_SORT;

	/**
	 * Ordered data structure for use with columns of type long. Range queries are extremely fast. Very fast (virtually free) for inserts where values are always increasing,
	 * deletes/updates are highly expensive. Backed by an array list like data structure
	 */
	byte TYPE_SERIES = AmiIndexMap.TYPE_SERIES;

	byte CONSTRAINT_TYPE_UNIQUE = 1;
	byte CONSTRAINT_TYPE_NONE = 2;
	byte CONSTRAINT_TYPE_PRIMARY = 3;

	byte AUTOGEN_RAND = 1;
	byte AUTOGEN_INC = 2;
	byte AUTOGEN_NONE = 3;

	/**
	 * @return the number of columns participating in this index.
	 */
	int getColumnsCount();

	/**
	 * @param pos
	 *            used as the zero-based position for this index to return, must be 0 to less than getColumnsCount()
	 * @return column participating in this index at specified location
	 */
	AmiColumn getColumn(int pos);

	/**
	 * @return table that this index is on
	 */
	AmiTable getTable();
	/**
	 * @param pos
	 *            used as the zero-based position for this index to return, must be 0 to less than getColumnsCount()
	 * @return type of index participating at specified location (TYPE_HASH, TYPE_SORT or TYPE_SERIES)
	 */
	byte getIndexTypeAt(int pos);

	/**
	 * @return name of this index, all indexes within a table are unique
	 */
	String getName();

	byte getDefType();

	long getMemorySize();
}
