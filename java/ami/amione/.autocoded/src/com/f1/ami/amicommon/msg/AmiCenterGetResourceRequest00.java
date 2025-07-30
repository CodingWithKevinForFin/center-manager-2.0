//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiCenterGetResourceRequest00 extends com.f1.ami.amicommon.msg.AmiCenterGetResourceRequest0 {

    public String askIdeableName(){
        return "F1.VE.GREQ";
    }

    public long askVid(){
        return 3590943237939766477L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiCenterGetResourceRequest00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiCenterGetResourceRequest.class;
	}

    
	public AmiCenterGetResourceRequest00 nw(){
	    return new AmiCenterGetResourceRequest00();
	}

	public AmiCenterGetResourceRequest00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiCenterGetResourceRequest00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}