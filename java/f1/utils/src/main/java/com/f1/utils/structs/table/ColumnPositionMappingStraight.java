package com.f1.utils.structs.table;

public class ColumnPositionMappingStraight implements ColumnPositionMapping {

	private int size;

	private ColumnPositionMappingStraight(int size) {
		this.size = size;
	}

	@Override
	public int getTargetPosAt(int n) {
		return n;
	}

	@Override
	public int getSourcePosAt(int n) {
		return n;
	}

	@Override
	public int getPosCount() {
		return size;
	}

	@Override
	public int getTargetPosForSourcePos(int sourcePos) {
		return sourcePos;
	}

	@Override
	public int getSourcePosForTargetPos(int targetPos) {
		return targetPos;
	}

	@Override
	public boolean isStraight() {
		return true;
	}

	static private ColumnPositionMappingStraight[] CACHE = new ColumnPositionMappingStraight[1000];

	public static ColumnPositionMappingStraight GET(int size) {
		if (size >= CACHE.length)
			return new ColumnPositionMappingStraight(size);
		ColumnPositionMappingStraight r = CACHE[size];
		if (r == null)
			CACHE[size] = r = new ColumnPositionMappingStraight(size);
		return r;
	}

}
