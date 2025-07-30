//Coded by MessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class AmiDatasourceColumn00 extends com.f1.ami.amicommon.msg.AmiDatasourceColumn0 {

    public String askIdeableName(){
        return "F1.AMI.DSC";
    }

    public long askVid(){
        return 3590852796988923333L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)AmiDatasourceColumn00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.ami.amicommon.msg.AmiDatasourceColumn.class;
	}

    
	public AmiDatasourceColumn00 nw(){
	    return new AmiDatasourceColumn00();
	}

	public AmiDatasourceColumn00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiDatasourceColumn00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}