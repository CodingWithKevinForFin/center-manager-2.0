//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.centerclient;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiCenterClientObjectMessages00 extends com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessages0 {

    public String askIdeableName(){
        return "com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessages";
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
	    return (Class)AmiCenterClientObjectMessages00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessages.class;
	}

    
	public AmiCenterClientObjectMessages00 nw(){
	    return new AmiCenterClientObjectMessages00();
	}

	public AmiCenterClientObjectMessages00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiCenterClientObjectMessages00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}