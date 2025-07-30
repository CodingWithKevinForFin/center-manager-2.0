//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiRelayLogoutMessage00 extends com.f1.ami.amicommon.msg.AmiRelayLogoutMessage0 {

    public String askIdeableName(){
        return "F1.VA.AMIAX";
    }

    public long askVid(){
        return 3590942779791795653L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiRelayLogoutMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiRelayLogoutMessage.class;
	}

    
	public AmiRelayLogoutMessage00 nw(){
	    return new AmiRelayLogoutMessage00();
	}

	public AmiRelayLogoutMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiRelayLogoutMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}