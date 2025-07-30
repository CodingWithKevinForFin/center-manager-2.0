//Coded by MessageCodeTemplate
package com.f1.ami.center;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiCenterStartItineraryMessage00 extends com.f1.ami.center.AmiCenterStartItineraryMessage0 {

    public String askIdeableName(){
        return "com.f1.ami.center.AmiCenterStartItineraryMessage";
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
	    return (Class)AmiCenterStartItineraryMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.center.AmiCenterStartItineraryMessage.class;
	}

    
	public AmiCenterStartItineraryMessage00 nw(){
	    return new AmiCenterStartItineraryMessage00();
	}

	public AmiCenterStartItineraryMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiCenterStartItineraryMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}