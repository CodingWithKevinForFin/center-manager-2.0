//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public abstract class AmiWebManagerListRootsRequest00 extends com.f1.ami.amicommon.msg.AmiWebManagerListRootsRequest0 {

    public String askIdeableName(){
        return "F1.WM.LRQ";
    }

    public long askVid(){
        return 3590948501453415897L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiWebManagerListRootsRequest00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiWebManagerListRootsRequest.class;
	}

    
}