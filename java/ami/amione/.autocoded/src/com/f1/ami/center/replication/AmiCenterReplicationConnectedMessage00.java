//Coded by MessageCodeTemplate
package com.f1.ami.center.replication;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiCenterReplicationConnectedMessage00 extends com.f1.ami.center.replication.AmiCenterReplicationConnectedMessage0 {

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
	    return (Class)AmiCenterReplicationConnectedMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.center.replication.AmiCenterReplicationConnectedMessage.class;
	}

    
	public AmiCenterReplicationConnectedMessage00 nw(){
	    return new AmiCenterReplicationConnectedMessage00();
	}

	public AmiCenterReplicationConnectedMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiCenterReplicationConnectedMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}