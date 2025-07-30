package com.f1.ami.center.table.index;

import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.base.ToStringable;

public interface AmiQueryFinder extends ToStringable {
	int SCORE_EQ = 6;
	int SCORE_BETWEEN = 5;
	int SCORE_IN = 4;
	int SCORE_GTLT = 3;
	int SCORE_GELE = 2;
	int SCORE_NE = 1;
	int SCORE_ALL = 0;

	int getScore();
	AmiColumnImpl getColumn();

	//return false if the limit is hit (and we should stop searching)
	public boolean getRows(AmiIndexMap_Hash map, AmiQueryFinderVisitor finderVisitor);
	public boolean getRows(AmiIndexMap_Tree map, AmiQueryFinderVisitor finderVisitor);
	public boolean getRows(AmiIndexMap_Series map, AmiQueryFinderVisitor finderVisitor);

	public boolean matches(Comparable<?> value);

	public StringBuilder toString(StringBuilder sink);

	public AmiQueryFinder getNext();
	public void setNext(AmiQueryFinder next);

	/**
	 * @return the query that this clause is a member of
	 */
	AmiPreparedQuery getAmiPreparedQuery();
}
