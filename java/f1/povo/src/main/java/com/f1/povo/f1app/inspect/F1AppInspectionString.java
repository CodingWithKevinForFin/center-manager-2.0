package com.f1.povo.f1app.inspect;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.IN.IS")
public interface F1AppInspectionString extends F1AppInspectionEntity {

	@PID(2)
	public String getString();
	public void setString(String string);

}
