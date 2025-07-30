//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public abstract class AmiWebManagerPutFileRequest00 extends com.f1.ami.amicommon.msg.AmiWebManagerPutFileRequest0 {

    public String askIdeableName(){
        return "F1.WM.PFQ";
    }

    public long askVid(){
        return 3590948501745334937L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiWebManagerPutFileRequest00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiWebManagerPutFileRequest.class;
	}

    
}