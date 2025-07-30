//Coded by MessageCodeTemplate
package com.f1.suite.web.portal.impl;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class ClosePortletManagerAction00 extends com.f1.suite.web.portal.impl.ClosePortletManagerAction0 {

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
	    return (Class)ClosePortletManagerAction00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.suite.web.portal.impl.ClosePortletManagerAction.class;
	}

    
	public ClosePortletManagerAction00 nw(){
	    return new ClosePortletManagerAction00();
	}

	public ClosePortletManagerAction00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public ClosePortletManagerAction00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}