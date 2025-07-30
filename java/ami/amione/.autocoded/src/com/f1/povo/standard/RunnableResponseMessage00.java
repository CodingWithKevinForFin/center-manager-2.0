//Coded by MessageCodeTemplate
package com.f1.povo.standard;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class RunnableResponseMessage00 extends com.f1.povo.standard.RunnableResponseMessage0 {

    public String askIdeableName(){
        return "F1.ST.RR";
    }

    public long askVid(){
        return 3590931911669954657L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)RunnableResponseMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.povo.standard.RunnableResponseMessage.class;
	}

    
	public RunnableResponseMessage00 nw(){
	    return new RunnableResponseMessage00();
	}

	public RunnableResponseMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public RunnableResponseMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}