//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiRelayObjectDeleteMessage00 extends com.f1.ami.amicommon.msg.AmiRelayObjectDeleteMessage0 {

    public String askIdeableName(){
        return "F1.VA.AMIAD";
    }

    public long askVid(){
        return 3590942779791794893L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiRelayObjectDeleteMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiRelayObjectDeleteMessage.class;
	}

    
	public AmiRelayObjectDeleteMessage00 nw(){
	    return new AmiRelayObjectDeleteMessage00();
	}

	public AmiRelayObjectDeleteMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiRelayObjectDeleteMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}