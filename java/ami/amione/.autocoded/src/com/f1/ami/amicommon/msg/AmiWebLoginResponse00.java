//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiWebLoginResponse00 extends com.f1.ami.amicommon.msg.AmiWebLoginResponse0 {

    public String askIdeableName(){
        return "F1.WM.LIR";
    }

    public long askVid(){
        return 3590948501434704545L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiWebLoginResponse00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiWebLoginResponse.class;
	}

    
	public AmiWebLoginResponse00 nw(){
	    return new AmiWebLoginResponse00();
	}

	public AmiWebLoginResponse00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiWebLoginResponse00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}