package com.f1.ami.center.table.keygen;

import java.security.SecureRandom;
import java.util.List;

import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiIndexImpl;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.MH;
import com.f1.utils.OH;

public class AmiKeyGenerator_LongRand implements AmiKeyGenerator {

	private AmiColumn col;
	private SecureRandom rand = new SecureRandom();
	private AmiIndexImpl index;

	public AmiKeyGenerator_LongRand(AmiColumnImpl col, AmiIndexImpl i) {
		OH.assertEq(col.getAmiType(), AmiTable.TYPE_LONG);
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
		long v = MH.abs(rand.nextLong());
		while (this.index.getRootMap().getIndex(v) != null)
			v = MH.abs(rand.nextLong());
		sink.setLong(col, v, null);
	}

	@Override
	public void onValues(List<AmiRow> val) {
	}

}
