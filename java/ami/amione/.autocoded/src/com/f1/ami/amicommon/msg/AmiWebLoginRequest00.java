//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiWebLoginRequest00 extends com.f1.ami.amicommon.msg.AmiWebLoginRequest0 {

    public String askIdeableName(){
        return "F1.WM.LIQ";
    }

    public long askVid(){
        return 3590948501434649673L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiWebLoginRequest00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiWebLoginRequest.class;
	}

    
	public AmiWebLoginRequest00 nw(){
	    return new AmiWebLoginRequest00();
	}

	public AmiWebLoginRequest00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiWebLoginRequest00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}