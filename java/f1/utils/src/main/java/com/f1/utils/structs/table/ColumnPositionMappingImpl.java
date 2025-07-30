package com.f1.utils.structs.table;

import com.f1.utils.AH;

public class ColumnPositionMappingImpl implements ColumnPositionMapping {

	final private int[] targetPositions;
	final private int[] sourcePositions;

	private ColumnPositionMappingImpl(int[] sourcePositions, int[] targetPositions) {
		this.sourcePositions = sourcePositions;
		this.targetPositions = targetPositions;
	}

	@Override
	public int getTargetPosAt(int n) {
		return targetPositions[n];
	}

	@Override
	public int getSourcePosAt(int n) {
		return sourcePositions[n];
	}

	@Override
	public int getPosCount() {
		return sourcePositions.length;
	}

	@Override
	public int getTargetPosForSourcePos(int sourcePos) {
		int n = AH.indexOf(sourcePos, this.sourcePositions);
		return n == -1 ? -1 : this.targetPositions[n];
	}

	@Override
	public int getSourcePosForTargetPos(int targetPos) {
		int n = AH.indexOf(targetPos, this.targetPositions);
		return n == -1 ? -1 : this.sourcePositions[n];
	}

	@Override
	public boolean isStraight() {
		return false;
	}
	public static ColumnPositionMapping GET(int[] sourcePositions, int[] targetPositions) {
		if (AH.eq(sourcePositions, targetPositions))
			ColumnPositionMappingStraight.GET(sourcePositions.length);
		for (int i = 0; i < sourcePositions.length; i++)
			if (sourcePositions[i] != i)
				return new ColumnPositionMappingImpl(sourcePositions, targetPositions);
		return ColumnPositionMappingSourceStraight.GET(targetPositions);
	}

}
