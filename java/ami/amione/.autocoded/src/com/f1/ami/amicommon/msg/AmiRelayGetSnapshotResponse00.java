//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public abstract class AmiRelayGetSnapshotResponse00 extends com.f1.ami.amicommon.msg.AmiRelayGetSnapshotResponse0 {

    public String askIdeableName(){
        return "F1.VA.GSR";
    }

    public long askVid(){
        return 3590942780280250465L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiRelayGetSnapshotResponse00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiRelayGetSnapshotResponse.class;
	}

    
}