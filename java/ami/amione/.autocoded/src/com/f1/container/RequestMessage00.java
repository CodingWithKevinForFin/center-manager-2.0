//Coded by MessageCodeTemplate
package com.f1.container;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class RequestMessage00 extends com.f1.container.RequestMessage0 {

    public String askIdeableName(){
        return "F1.BA.RQ";
    }

    public long askVid(){
        return 3590855825305445841L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)RequestMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.container.RequestMessage.class;
	}

    
	public RequestMessage00 nw(){
	    return new RequestMessage00();
	}

	public RequestMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public RequestMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}