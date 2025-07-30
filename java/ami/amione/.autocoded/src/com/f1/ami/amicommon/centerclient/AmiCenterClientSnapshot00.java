//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.centerclient;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiCenterClientSnapshot00 extends com.f1.ami.amicommon.centerclient.AmiCenterClientSnapshot0 {

    public String askIdeableName(){
        return "com.f1.ami.amicommon.centerclient.AmiCenterClientSnapshot";
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
	    return (Class)AmiCenterClientSnapshot00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.centerclient.AmiCenterClientSnapshot.class;
	}

    
	public AmiCenterClientSnapshot00 nw(){
	    return new AmiCenterClientSnapshot00();
	}

	public AmiCenterClientSnapshot00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiCenterClientSnapshot00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}