//Coded by MessageCodeTemplate
package com.f1.ami.center.replication;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiCenterReplicationObjectsMessage00 extends com.f1.ami.center.replication.AmiCenterReplicationObjectsMessage0 {

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
	    return (Class)AmiCenterReplicationObjectsMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.center.replication.AmiCenterReplicationObjectsMessage.class;
	}

    
	public AmiCenterReplicationObjectsMessage00 nw(){
	    return new AmiCenterReplicationObjectsMessage00();
	}

	public AmiCenterReplicationObjectsMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiCenterReplicationObjectsMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}