//Coded by MessageCodeTemplate
package com.f1.povo.standard;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class MapMessage00 extends com.f1.povo.standard.MapMessage0 {

    public String askIdeableName(){
        return "F1.ST.MA";
    }

    public long askVid(){
        return 3590931911238331505L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)MapMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.povo.standard.MapMessage.class;
	}

    
	public MapMessage00 nw(){
	    return new MapMessage00();
	}

	public MapMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public MapMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}