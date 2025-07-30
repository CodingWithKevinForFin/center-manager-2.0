//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public abstract class AmiWebManagerProcessSpecialFileResponse00 extends com.f1.ami.amicommon.msg.AmiWebManagerProcessSpecialFileResponse0 {

    public String askIdeableName(){
        return "F1.VE.PSFR";
    }

    public long askVid(){
        return 3590943238655024441L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiWebManagerProcessSpecialFileResponse00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiWebManagerProcessSpecialFileResponse.class;
	}

    
}