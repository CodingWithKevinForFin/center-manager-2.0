//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiCenterResource00 extends com.f1.ami.amicommon.msg.AmiCenterResource0 {

    public String askIdeableName(){
        return "F1.VE.RES";
    }

    public long askVid(){
        return 3590943238785030649L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiCenterResource00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiCenterResource.class;
	}

    
	public AmiCenterResource00 nw(){
	    return new AmiCenterResource00();
	}

	public AmiCenterResource00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiCenterResource00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}