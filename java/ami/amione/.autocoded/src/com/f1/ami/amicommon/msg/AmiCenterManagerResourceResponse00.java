//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public abstract class AmiCenterManagerResourceResponse00 extends com.f1.ami.amicommon.msg.AmiCenterManagerResourceResponse0 {

    public String askIdeableName(){
        return "F1.VE.MRER";
    }

    public long askVid(){
        return 3590943238415178929L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiCenterManagerResourceResponse00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiCenterManagerResourceResponse.class;
	}

    
}