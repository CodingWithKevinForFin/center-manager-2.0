package com.f1.ami.center.table.keygen;

import java.security.SecureRandom;
import java.util.List;

import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiIndexImpl;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.OH;

public class AmiKeyGenerator_DoubleRand implements AmiKeyGenerator {

	private AmiColumn col;
	private SecureRandom rand = new SecureRandom();
	private AmiIndexImpl index;

	public AmiKeyGenerator_DoubleRand(AmiColumnImpl col, AmiIndexImpl i) {
		OH.assertEq(col.getAmiType(), AmiTable.TYPE_DOUBLE);
		this.col = col;
		this.index = i;
	}

	@Override
	public boolean hasValue(AmiRow val) {
		return !val.getIsNull(col);
	}

	@Override
	public void onValue(AmiRow val) {
	}

	@Override
	public void getNextValue(AmiRow sink) {
		double v = rand.nextDouble();
		while (this.index.getRootMap().getIndex(v) != null)
			v = rand.nextDouble();
		sink.setDouble(col, v, null);
	}

	@Override
	public void onValues(List<AmiRow> val) {
	}

}
