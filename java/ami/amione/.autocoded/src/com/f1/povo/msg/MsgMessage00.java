//Coded by MessageCodeTemplate
package com.f1.povo.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class MsgMessage00 extends com.f1.povo.msg.MsgMessage0 {

    public String askIdeableName(){
        return "F1.ST.MS";
    }

    public long askVid(){
        return 3590931911275863953L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)MsgMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.povo.msg.MsgMessage.class;
	}

    
	public MsgMessage00 nw(){
	    return new MsgMessage00();
	}

	public MsgMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public MsgMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}