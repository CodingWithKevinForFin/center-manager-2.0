//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.centerclient;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiCenterClientMsgStatusMessage00 extends com.f1.ami.amicommon.centerclient.AmiCenterClientMsgStatusMessage0 {

    public String askIdeableName(){
        return "com.f1.ami.amicommon.centerclient.AmiCenterClientMsgStatusMessage";
    }

    public long askVid(){
        return -1L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiCenterClientMsgStatusMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.centerclient.AmiCenterClientMsgStatusMessage.class;
	}

    
	public AmiCenterClientMsgStatusMessage00 nw(){
	    return new AmiCenterClientMsgStatusMessage00();
	}

	public AmiCenterClientMsgStatusMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiCenterClientMsgStatusMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}