//Coded by MessageCodeTemplate
package com.f1.povo.standard;

import com.f1.base.Valued;
import com.f1.utils.VH;


public  class ObjectMessage00 extends com.f1.povo.standard.ObjectMessage0 {

    public String askIdeableName(){
        return "F1.ST.OB";
    }

    public long askVid(){
        return 3590931911398886977L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)ObjectMessage00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.povo.standard.ObjectMessage.class;
	}

    
	public ObjectMessage00 nw(){
	    return new ObjectMessage00();
	}

	public ObjectMessage00 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public ObjectMessage00 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
}