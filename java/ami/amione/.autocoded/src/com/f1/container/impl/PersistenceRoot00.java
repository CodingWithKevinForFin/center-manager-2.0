//Coded by MessageCodeTemplate
package com.f1.container.impl;

import com.f1.base.Valued;
import com.f1.utils.VH;


public abstract class PersistenceRoot00 extends com.f1.container.impl.PersistenceRoot0 {

    public String askIdeableName(){
        return "F1.CN.PR";
    }

    public long askVid(){
        return 3590861660343772833L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)PersistenceRoot00.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)com.f1.container.impl.PersistenceRoot.class;
	}

    
}