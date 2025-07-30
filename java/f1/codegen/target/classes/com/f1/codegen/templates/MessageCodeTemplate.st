package $c.packageName$;

import com.f1.base.Valued;
import com.f1.utils.VH;


public $if(isAbstract)$abstract$endif$ class $c.simpleClassName$ $c.extendsClause$ $c.innerClass.canonicalName$ {

    public String askIdeableName(){
        return "$s.vin$";
    }

    public long askVid(){
        return $s.vid$L;
    }

    public String askCheckSum(){
        return "asdf";
    }
    
    public StringBuilder toString(StringBuilder sb){
      return VH.toString(this,sb);
    }
    
	public Class<Valued> askType(){
	    return (Class)$c.simpleClassName$.class;
	}
	
	public Class<Valued> askOriginalType(){
	    return (Class)$s.innerClass.canonicalName$.class;
	}

    $if(!isAbstract)$
	public $c.simpleClassName$ nw(){
	    return new $c.simpleClassName$();
	}

	public $c.simpleClassName$ nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public $c.simpleClassName$ nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	$endif$
}