//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiCenterSchemaRecord00 extends com.f1.ami.amicommon.msg.AmiCenterSchemaRecord0 {

    public String askIdeableName(){
        return "F1.VA.AMISR";
    }

    public long askVid(){
        return 3590942779791821417L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiCenterSchemaRecord00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiCenterSchemaRecord.class;
	}

    
	public AmiCenterSchemaRecord00 nw(){
	    return new AmiCenterSchemaRecord00();
	}

	public AmiCenterSchemaRecord00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiCenterSchemaRecord00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}