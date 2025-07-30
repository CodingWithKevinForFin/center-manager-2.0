//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiCenterGetResourceResponse00 extends com.f1.ami.amicommon.msg.AmiCenterGetResourceResponse0 {

    public String askIdeableName(){
        return "F1.VE.GRER";
    }

    public long askVid(){
        return 3590943237939767921L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiCenterGetResourceResponse00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiCenterGetResourceResponse.class;
	}

    
	public AmiCenterGetResourceResponse00 nw(){
	    return new AmiCenterGetResourceResponse00();
	}

	public AmiCenterGetResourceResponse00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiCenterGetResourceResponse00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}