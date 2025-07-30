package com.f1.ami.center.table.keygen;

import java.util.List;

import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.OH;

public class AmiKeyGenerator_IntInc implements AmiKeyGenerator {

	private AmiColumn col;
	private int maxVal = 0;

	public AmiKeyGenerator_IntInc(AmiColumnImpl col) {
		OH.assertEq(col.getAmiType(), AmiTable.TYPE_INT);
		this.col = col;
	}

	@Override
	public boolean hasValue(AmiRow val) {
		return !val.getIsNull(col);
	}

	@Override
	public void onValue(AmiRow val) {
		int l = (int) val.getLong(col);
		if (l > maxVal)
			maxVal = l + 1;
	}

	@Override
	public void getNextValue(AmiRow sink) {
		if (maxVal == Integer.MAX_VALUE)
			throw new RuntimeException("MAX VALUE REACHED: " + Integer.MAX_VALUE);
		sink.setLong(col, maxVal++, null);
	}

	@Override
	public void onValues(List<AmiRow> val) {
		for (AmiRow row : val)
			onValue(row);
	}

}
