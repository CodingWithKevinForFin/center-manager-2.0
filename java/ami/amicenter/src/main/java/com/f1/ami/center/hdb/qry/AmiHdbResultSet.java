package com.f1.ami.center.hdb.qry;

import java.util.List;
import java.util.Map;

import com.f1.ami.center.hdb.AmiHdbPartition;

public interface AmiHdbResultSet {

	public byte SORT_INDEX_ASC = 1;
	public byte SORT_INDEX_DESC = -1;
	public byte SORT_INDEX_NONE = 0;

	AmiHdbPartition getCurrentPartition();
	String getIndexColumn();
	void init(List<AmiHdbPartition> partitions);
	boolean next(int limit);
	int nextOnlyCount();
	Map<Comparable, int[]> getKeys();

	Comparable getIndexValueAt(int i);
	int getCurrentRowAt(int i);
	int getCurrentRowsCount();

}
