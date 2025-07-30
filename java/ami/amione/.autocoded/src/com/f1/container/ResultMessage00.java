//Coded by MessageCodeTemplate
package com.f1.container;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class ResultMessage00 extends com.f1.container.ResultMessage0 {

    public String askIdeableName(){
        return "F1.BA.RS";
    }

    public long askVid(){
        return 3590855825309616113L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)ResultMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.container.ResultMessage.class;
	}

    
	public ResultMessage00 nw(){
	    return new ResultMessage00();
	}

	public ResultMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public ResultMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}