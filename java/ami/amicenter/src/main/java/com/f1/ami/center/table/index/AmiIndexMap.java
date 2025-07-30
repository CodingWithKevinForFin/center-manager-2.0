package com.f1.ami.center.table.index;

public interface AmiIndexMap {

	byte TYPE_SORT = 1;
	byte TYPE_HASH = 2;
	byte TYPE_SERIES = 3;
	byte TYPE_VALUES = 4;

	void removeIndex(Comparable key);
	AmiIndexMap getIndex(Comparable value);
	void putIndex(Comparable key, AmiIndexMap value);

	public Iterable<Comparable> getKeysForDebug();
	public Iterable<AmiIndexMap> getValuesForDebug();
	boolean isIndexEmpty();

	//return false if the limit is hit (and we should stop searching)
	boolean getRows(AmiQueryFinder amiQueryFinder, AmiQueryFinderVisitor visitor);
	long getMemorySize();
	int getKeysCount();

}
