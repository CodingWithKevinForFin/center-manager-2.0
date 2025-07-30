//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiRelayLoginMessage00 extends com.f1.ami.amicommon.msg.AmiRelayLoginMessage0 {

    public String askIdeableName(){
        return "F1.VA.AMIAL";
    }

    public long askVid(){
        return 3590942779791795197L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiRelayLoginMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiRelayLoginMessage.class;
	}

    
	public AmiRelayLoginMessage00 nw(){
	    return new AmiRelayLoginMessage00();
	}

	public AmiRelayLoginMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiRelayLoginMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}