package com.f1.anvil.utils;

import com.f1.ami.center.table.AmiRow;

public interface AnvilHorizon {

	public long getOldestTime();
	public long getNewestTime();

	public AnvilHorizonEvent addTime(long time, AmiRow type);
}
