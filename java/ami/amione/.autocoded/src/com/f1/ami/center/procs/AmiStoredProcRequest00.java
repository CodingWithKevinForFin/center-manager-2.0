//Coded by MessageCodeTemplate
package com.f1.ami.center.procs;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiStoredProcRequest00 extends com.f1.ami.center.procs.AmiStoredProcRequest0 {

    public String askIdeableName(){
        return "F1.VE.ASPQ";
    }

    public long askVid(){
        return 3590943237467044197L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiStoredProcRequest00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.center.procs.AmiStoredProcRequest.class;
	}

    
	public AmiStoredProcRequest00 nw(){
	    return new AmiStoredProcRequest00();
	}

	public AmiStoredProcRequest00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiStoredProcRequest00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}