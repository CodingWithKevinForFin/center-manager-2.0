//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiRelayAckMessage00 extends com.f1.ami.amicommon.msg.AmiRelayAckMessage0 {

    public String askIdeableName(){
        return "F1.VA.CBA";
    }

    public long askVid(){
        return 3590942779926929657L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiRelayAckMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiRelayAckMessage.class;
	}

    
	public AmiRelayAckMessage00 nw(){
	    return new AmiRelayAckMessage00();
	}

	public AmiRelayAckMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiRelayAckMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}