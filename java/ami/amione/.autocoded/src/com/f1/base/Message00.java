//Coded by MessageCodeTemplate
package com.f1.base;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class Message00 extends com.f1.base.Message0 {

    public String askIdeableName(){
        return "F1.BA.MS";
    }

    public long askVid(){
        return 3590855824913440273L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)Message00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.base.Message.class;
	}

    
	public Message00 nw(){
	    return new Message00();
	}

	public Message00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public Message00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}