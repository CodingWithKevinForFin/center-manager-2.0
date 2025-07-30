//Coded by MessageCodeTemplate
package com.f1.povo.standard;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class UnsubscribeMessage00 extends com.f1.povo.standard.UnsubscribeMessage0 {

    public String askIdeableName(){
        return "F1.ST.US";
    }

    public long askVid(){
        return 3590931911909745297L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)UnsubscribeMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.povo.standard.UnsubscribeMessage.class;
	}

    
	public UnsubscribeMessage00 nw(){
	    return new UnsubscribeMessage00();
	}

	public UnsubscribeMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public UnsubscribeMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}