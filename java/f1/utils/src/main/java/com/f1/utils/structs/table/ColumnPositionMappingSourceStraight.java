package com.f1.utils.structs.table;

import com.f1.utils.AH;

public class ColumnPositionMappingSourceStraight implements ColumnPositionMapping {

	final private int[] targetPositions;

	private ColumnPositionMappingSourceStraight(int[] targetPositions) {
		this.targetPositions = targetPositions;
	}

	@Override
	public int getTargetPosAt(int n) {
		return targetPositions[n];
	}

	@Override
	public int getSourcePosAt(int n) {
		return n;
	}

	@Override
	public int getPosCount() {
		return targetPositions.length;
	}

	@Override
	public int getTargetPosForSourcePos(int sourcePos) {
		return sourcePos == -1 ? -1 : this.targetPositions[sourcePos];
	}

	@Override
	public int getSourcePosForTargetPos(int targetPos) {
		return AH.indexOf(targetPos, this.targetPositions);
	}

	@Override
	public boolean isStraight() {
		return false;
	}

	public static ColumnPositionMapping GET(int[] targetPositions) {
		for (int i = 0; i < targetPositions.length; i++)
			if (targetPositions[i] != i)
				return new ColumnPositionMappingSourceStraight(targetPositions);
		return ColumnPositionMappingStraight.GET(targetPositions.length);
	}

}
