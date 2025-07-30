//Coded by MessageCodeTemplate
package com.f1.povo.standard;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class RunnableRequestMessage00 extends com.f1.povo.standard.RunnableRequestMessage0 {

    public String askIdeableName(){
        return "F1.ST.RQ";
    }

    public long askVid(){
        return 3590931911667869521L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)RunnableRequestMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.povo.standard.RunnableRequestMessage.class;
	}

    
	public RunnableRequestMessage00 nw(){
	    return new RunnableRequestMessage00();
	}

	public RunnableRequestMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public RunnableRequestMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}