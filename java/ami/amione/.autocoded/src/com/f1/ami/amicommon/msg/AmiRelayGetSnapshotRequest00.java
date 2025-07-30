//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public abstract class AmiRelayGetSnapshotRequest00 extends com.f1.ami.amicommon.msg.AmiRelayGetSnapshotRequest0 {

    public String askIdeableName(){
        return "F1.VA.GSQ";
    }

    public long askVid(){
        return 3590942780280195593L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiRelayGetSnapshotRequest00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiRelayGetSnapshotRequest.class;
	}

    
}