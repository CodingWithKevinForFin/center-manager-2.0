package com.f1.utils.structs.table.stack;

import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.utils.structs.table.derived.MethodFactoryManager;

public interface CalcTypesStack {
	//false if the parent's variables are not visible. For example: Method block would be false, for-loop block would be true
	public boolean isParentVisible();

	public CalcTypesStack getTop();
	public CalcTypesStack getParent();
	public MethodFactoryManager getFactory();

	public CalcTypes getFrame();
	public CalcFrame getFrameConsts();

	//Visible to all child frames. This should always go straight to the top
	public CalcTypes getGlobal();
	public CalcFrame getGlobalConsts();
}
