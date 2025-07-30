package com.f1.pofo.fix;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FX.OQ")
public interface FixOrderRequest extends FixRequest {
	// This is on new order requests only and cannot be changed on replaces
	/**
	 * security identifier (typically fix tag 48)
	 * 
	 * @return
	 */
	@PID(30)
	public String getSecurityID();

	public void setSecurityID(String SecurityID);

	/**
	 * @return the id source (typically tag 22)
	 */
	@PID(31)
	public int getIDType();

	public void setIDType(int IDType);

}
