package com.f1.anvil.utils;

import com.f1.ami.center.table.AmiRow;

public interface AnvilHorizonEvent {

	public AmiRow remove();
	public void updateEvent(long now, AmiRow row);
	public long getTime();
	public AmiRow peek();
}
