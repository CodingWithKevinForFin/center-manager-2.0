//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public abstract class AmiCenterQueryDsRequest00 extends com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest0 {

    public String askIdeableName(){
        return "F1.VE.QDOQ";
    }

    public long askVid(){
        return 3590943238703474973L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiCenterQueryDsRequest00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest.class;
	}

    
}