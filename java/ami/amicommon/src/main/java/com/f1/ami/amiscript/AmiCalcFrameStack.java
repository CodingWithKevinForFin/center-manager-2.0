package com.f1.ami.amiscript;

import com.f1.utils.structs.table.stack.CalcFrameStack;

public interface AmiCalcFrameStack extends CalcFrameStack {

	public AmiDebugManager getDebugManager();
	public AmiService getService();
	public byte getSourceDebugType();
	public String getUserName();//We should really return an AmiSession which AmiWebService and AmiDbSession implement 

	public String getSourceAri();//TODO: REMOVE
	public String getLayoutAlias();//TODO: REMOVE

	//These can change... Need to walk the chain
	public String getDefaultDatasource();
	public String getCallbackName();
	public Object getThis();
}
