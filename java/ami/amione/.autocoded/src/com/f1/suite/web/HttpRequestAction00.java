//Coded by MessageCodeTemplate
package com.f1.suite.web;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class HttpRequestAction00 extends com.f1.suite.web.HttpRequestAction0 {

    public String askIdeableName(){
        return "com.f1.suite.web.HttpRequestAction";
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
	    return (Class)HttpRequestAction00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.suite.web.HttpRequestAction.class;
	}

    
	public HttpRequestAction00 nw(){
	    return new HttpRequestAction00();
	}

	public HttpRequestAction00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public HttpRequestAction00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}