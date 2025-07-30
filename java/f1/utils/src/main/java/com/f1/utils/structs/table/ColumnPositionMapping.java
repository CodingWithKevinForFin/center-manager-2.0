package com.f1.utils.structs.table;

public interface ColumnPositionMapping {

	public int getTargetPosAt(int n);
	public int getSourcePosAt(int n);
	public int getPosCount();
	public int getTargetPosForSourcePos(int sourcePos);
	public int getSourcePosForTargetPos(int targetPos);
	public boolean isStraight();

}
