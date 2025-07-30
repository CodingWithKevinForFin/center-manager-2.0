//Coded by MessageCodeTemplate
package com.f1.base;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class Action00 extends com.f1.base.Action0 {

    public String askIdeableName(){
        return "";
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
	    return (Class)Action00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.base.Action.class;
	}

    
	public Action00 nw(){
	    return new Action00();
	}

	public Action00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public Action00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}