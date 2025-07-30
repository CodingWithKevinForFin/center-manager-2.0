//Coded by MessageCodeTemplate
package com.f1.povo.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class MsgStatusMessage00 extends com.f1.povo.msg.MsgStatusMessage0 {

    public String askIdeableName(){
        return "F1.ST.MC";
    }

    public long askVid(){
        return 3590931911242501777L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)MsgStatusMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.povo.msg.MsgStatusMessage.class;
	}

    
	public MsgStatusMessage00 nw(){
	    return new MsgStatusMessage00();
	}

	public MsgStatusMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public MsgStatusMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}