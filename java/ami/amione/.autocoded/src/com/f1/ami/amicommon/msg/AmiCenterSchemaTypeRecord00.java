//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiCenterSchemaTypeRecord00 extends com.f1.ami.amicommon.msg.AmiCenterSchemaTypeRecord0 {

    public String askIdeableName(){
        return "F1.VA.AMITR";
    }

    public long askVid(){
        return 3590942779791822861L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiCenterSchemaTypeRecord00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiCenterSchemaTypeRecord.class;
	}

    
	public AmiCenterSchemaTypeRecord00 nw(){
	    return new AmiCenterSchemaTypeRecord00();
	}

	public AmiCenterSchemaTypeRecord00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiCenterSchemaTypeRecord00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}