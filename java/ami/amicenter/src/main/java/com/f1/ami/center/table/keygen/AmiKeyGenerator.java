package com.f1.ami.center.table.keygen;

import java.util.List;

import com.f1.ami.center.table.AmiRow;

public interface AmiKeyGenerator {

	public boolean hasValue(AmiRow val);
	public void onValue(AmiRow val);
	public void onValues(List<AmiRow> val);
	public void getNextValue(AmiRow sink);
}
