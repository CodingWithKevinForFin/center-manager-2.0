package com.f1.ami.center.table.keygen;

import java.security.SecureRandom;
import java.util.List;

import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.base.UUID;
import com.f1.utils.MH;
import com.f1.utils.OH;

public class AmiKeyGenerator_UUIDRand implements AmiKeyGenerator {

	private AmiColumn col;
	private static SecureRandom RAND = MH.RANDOM_SECURE;

	public AmiKeyGenerator_UUIDRand(AmiColumnImpl col) {
		OH.assertEq(col.getAmiType(), AmiTable.TYPE_UUID);
		this.col = col;
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
		sink.setComparable(col, new UUID(RAND), null);
	}

	@Override
	public void onValues(List<AmiRow> val) {
	}

}
