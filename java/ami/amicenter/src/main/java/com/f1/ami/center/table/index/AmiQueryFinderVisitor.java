package com.f1.ami.center.table.index;

import java.util.List;

import com.f1.ami.center.table.AmiPreparedQueryImpl;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiRowImpl;

final public class AmiQueryFinderVisitor {

	final private AmiQueryScanner scanner;
	private int limit;
	private List<AmiRow> sink;
	final private AmiQueryFinder finder;
	private AmiIndexMap rootMap;

	public AmiQueryFinderVisitor(AmiPreparedQueryImpl inner) {
		this.finder = inner.getFinders()[0];
		this.scanner = inner.getScanner();
		this.rootMap = inner.getIndex().getRootMap();
	}

	public void find(List<AmiRow> sink, int limit) {
		this.limit = limit;
		this.sink = sink;
		if (limit > 0)
			rootMap.getRows(finder, this);
	}

	public boolean add(AmiRowImpl row) {
		if (limit <= 0)
			return false;
		if (scanner != null && !scanner.matches((AmiRowImpl) row))
			return true;//there is still more room
		this.sink.add(row);
		return --limit > 0;
	}

}
