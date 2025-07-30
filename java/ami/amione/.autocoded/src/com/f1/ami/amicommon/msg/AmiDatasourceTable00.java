//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiDatasourceTable00 extends com.f1.ami.amicommon.msg.AmiDatasourceTable0 {

    public String askIdeableName(){
        return "F1.AMI.DST";
    }

    public long askVid(){
        return 3590852796988947881L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiDatasourceTable00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiDatasourceTable.class;
	}

    
	public AmiDatasourceTable00 nw(){
	    return new AmiDatasourceTable00();
	}

	public AmiDatasourceTable00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiDatasourceTable00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}