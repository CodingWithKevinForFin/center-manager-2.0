//Coded by MessageCodeTemplate
package com.f1.ami.center.replication;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiCenterReplicationDisconnectedMessage00 extends com.f1.ami.center.replication.AmiCenterReplicationDisconnectedMessage0 {

    public String askIdeableName(){
        return "";
    }

    public long askVid(){
        return -1L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiCenterReplicationDisconnectedMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.center.replication.AmiCenterReplicationDisconnectedMessage.class;
	}

    
	public AmiCenterReplicationDisconnectedMessage00 nw(){
	    return new AmiCenterReplicationDisconnectedMessage00();
	}

	public AmiCenterReplicationDisconnectedMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiCenterReplicationDisconnectedMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}