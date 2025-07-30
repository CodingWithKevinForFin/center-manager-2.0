//Coded by MessageCodeTemplate
package com.f1.suite.utils.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class MsgAction00 extends com.f1.suite.utils.msg.MsgAction0 {

    public String askIdeableName(){
        return "com.f1.suite.utils.msg.MsgAction";
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
	    return (Class)MsgAction00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.suite.utils.msg.MsgAction.class;
	}

    
	public MsgAction00 nw(){
	    return new MsgAction00();
	}

	public MsgAction00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public MsgAction00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}