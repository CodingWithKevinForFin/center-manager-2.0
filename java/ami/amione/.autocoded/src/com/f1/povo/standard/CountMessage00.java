//Coded by MessageCodeTemplate
package com.f1.povo.standard;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class CountMessage00 extends com.f1.povo.standard.CountMessage0 {

    public String askIdeableName(){
        return "F1.ST.CNT";
    }

    public long askVid(){
        return 3590931910472647617L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)CountMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.povo.standard.CountMessage.class;
	}

    
	public CountMessage00 nw(){
	    return new CountMessage00();
	}

	public CountMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public CountMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}