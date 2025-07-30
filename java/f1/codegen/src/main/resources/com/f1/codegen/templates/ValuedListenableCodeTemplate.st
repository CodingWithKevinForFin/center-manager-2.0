package $c.packageName$;

import com.f1.base.*;
import java.util.List;
import java.util.ArrayList;
import com.f1.utils.structs.ByteKeyMap;
import com.f1.utils.structs.ByteKeyMap.Node;


public $if(isAbstract)$abstract$endif$ class $c.simpleClassName$ $c.extendsClause$ $c.innerClass.canonicalName$ $c.implementsClause$ com.f1.base.CodeGenerated {

	private ByteKeyMap<List<ValuedListener>> listeners = new ByteKeyMap<List<ValuedListener>>();
	private List<ValuedListener> listeners2 = null;
	
	public $c.simpleClassName$ clone(){
	   final $c.simpleClassName$ r=($c.simpleClassName$)super.clone();
	   r.listeners=new ByteKeyMap<List<ValuedListener>>();
	   r.listeners2=null;
	   return r;
	}
	
$c.params:{ x |

//$c.innerClass$
$if(c.supportsPids)$
	@Override
    public void set$x.upperCasedName$($x.type.canonicalName$ $x.varname$){
		List<ValuedListener> l=listeners.get((byte)$x.annotations.PID_value$);
		if (l != null || listeners2!=null) {
			final $x.type.canonicalName$ old = super.get$x.upperCasedName$();
			super.set$x.upperCasedName$($x.varname$);
			if(l!=null) for (int i=0,c=l.size();i<c;i++)
$if(x.isByte)$
				l.get(i).onValuedByte(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$elseif(x.isBoolean)$
				l.get(i).onValuedBoolean(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$elseif(x.isShort)$
				l.get(i).onValuedShort(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$elseif(x.isInt)$
				l.get(i).onValuedInt(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$elseif(x.isLong)$
				l.get(i).onValuedLong(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$elseif(x.isDouble)$
				l.get(i).onValuedDouble(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$elseif(x.isFloat)$
				l.get(i).onValuedFloat(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$elseif(x.isChar)$
				l.get(i).onValuedChar(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$else$
				l.get(i).onValued(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$endif$
			if(listeners2!=null) for (int i=0,c=listeners2.size();i<c;i++){
              $if(x.isValuedListenable)$
                 //if($x.varname$!=null) $x.varname$.addListener(listeners2.get(i));
              $endif$
$if(x.isByte)$
				listeners2.get(i).onValuedByte(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$elseif(x.isBoolean)$
				listeners2.get(i).onValuedBoolean(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$elseif(x.isShort)$
				listeners2.get(i).onValuedShort(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$elseif(x.isInt)$
				listeners2.get(i).onValuedInt(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$elseif(x.isLong)$
				listeners2.get(i).onValuedLong(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$elseif(x.isDouble)$
				listeners2.get(i).onValuedDouble(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$elseif(x.isFloat)$
				listeners2.get(i).onValuedFloat(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$elseif(x.isChar)$
				listeners2.get(i).onValuedChar(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$else$
				listeners2.get(i).onValued(this,"$x.name$", (byte)$x.annotations.PID_value$,old,$x.varname$);
$endif$
           }
		} else
			super.set$x.upperCasedName$($x.varname$);
    }
$else$
	@Override
    public void set$x.upperCasedName$($x.type.canonicalName$ $x.varname$){
		List<ValuedListener> l=listeners.get((byte)$x.position$);
		if (l != null || listeners2!=null) {
			final $x.type.canonicalName$ old = super.get$x.upperCasedName$();
			super.set$x.upperCasedName$($x.varname$);
			if(l!=null) for (int i=0,c=l.size();i<c;i++)
$if(x.isByte)$
				l.get(i).onValuedByte(this,"$x.name$", NO_PID,old,$x.varname$);
$elseif(x.isBoolean)$
				l.get(i).onValuedBoolean(this,"$x.name$", NO_PID,old,$x.varname$);
$elseif(x.isShort)$
				l.get(i).onValuedShort(this,"$x.name$", NO_PID,old,$x.varname$);
$elseif(x.isInt)$
				l.get(i).onValuedInt(this,"$x.name$", NO_PID,old,$x.varname$);
$elseif(x.isLong)$
				l.get(i).onValuedLong(this,"$x.name$", NO_PID,old,$x.varname$);
$elseif(x.isDouble)$
				l.get(i).onValuedDouble(this,"$x.name$", NO_PID,old,$x.varname$);
$elseif(x.isFloat)$
				l.get(i).onValuedFloat(this,"$x.name$", NO_PID,old,$x.varname$);
$elseif(x.isChar)$
				l.get(i).onValuedChar(this,"$x.name$", NO_PID,old,$x.varname$);
$else$
				l.get(i).onValued(this,"$x.name$", NO_PID,old,$x.varname$);
$endif$
			if(listeners2!=null) for (int i=0,c=listeners2.size();i<c;i++)
$if(x.isByte)$
				listeners2.get(i).onValuedByte(this,"$x.name$", NO_PID,old,$x.varname$);
$elseif(x.isBoolean)$
				listeners2.get(i).onValuedBoolean(this,"$x.name$", NO_PID,old,$x.varname$);
$elseif(x.isShort)$
				listeners2.get(i).onValuedShort(this,"$x.name$", NO_PID,old,$x.varname$);
$elseif(x.isInt)$
				listeners2.get(i).onValuedInt(this,"$x.name$", NO_PID,old,$x.varname$);
$elseif(x.isLong)$
				listeners2.get(i).onValuedLong(this,"$x.name$", NO_PID,old,$x.varname$);
$elseif(x.isDouble)$
				listeners2.get(i).onValuedDouble(this,"$x.name$", NO_PID,old,$x.varname$);
$elseif(x.isFloat)$
				listeners2.get(i).onValuedFloat(this,"$x.name$", NO_PID,old,$x.varname$);
$elseif(x.isChar)$
				listeners2.get(i).onValuedChar(this,"$x.name$", NO_PID,old,$x.varname$);
$else$
				listeners2.get(i).onValued(this,"$x.name$", NO_PID,old,$x.varname$);
$endif$
		} else
			super.set$x.upperCasedName$($x.varname$);
    }
$endif$

}$





	@Override
	public void put(String name, Object value) {
		askValuedParam(name).setValue(this, value);
	}

	@Override
	public boolean putNoThrow(String name, Object value) {
		if (!super.askParamValid(name))
			return false;
		askValuedParam(name).setValue(this, value);
		return true;
	}

	@Override
	public void put(byte pid, Object value) {
		askValuedParam(pid).setValue(this, value);
	}

	@Override
	public boolean putNoThrow(byte pid, Object value) {
		if (!super.askPidValid(pid))
			return false;
		askValuedParam(pid).setValue(this, value);
		return true;
	}

	@Override
	public void putBoolean(byte pid, boolean value) {
		askValuedParam(pid).setBoolean(this, value);
	}

	@Override
	public void putByte(byte pid, byte value) {
		askValuedParam(pid).setByte(this, value);
	}

	@Override
	public void putChar(byte pid, char value) {
		askValuedParam(pid).setChar(this, value);
	}

	@Override
	public void putShort(byte pid, short value) {
		askValuedParam(pid).setShort(this, value);
	}

	@Override
	public void putInt(byte pid, int value) {
		askValuedParam(pid).setInt(this, value);
	}

	@Override
	public void putLong(byte pid, long value) {
		askValuedParam(pid).setLong(this, value);
	}

	@Override
	public void putFloat(byte pid, float value) {
		askValuedParam(pid).setFloat(this, value);
	}

	@Override
	public void putDouble(byte pid, double value) {
		askValuedParam(pid).setDouble(this, value);
	}

	@Override
	public void addListener(byte pid, ValuedListener listener) {
		Node<List<ValuedListener>> n = getListenersNode(pid);
		List<ValuedListener> l = n.getValue();
		if (l == null) {
			n.setValue(l = new ArrayList<ValuedListener>(1));
			l.add(listener);
		} else if (!l.contains(listener))
			l.add(listener);
	}

	@Override
	public boolean addListener(ValuedListener listener) {
	    if(listeners2==null) listeners2=new ArrayList<ValuedListener>();
	    if(listeners2.contains(listener)) return false;
		listeners2.add(listener);
		//listener.onValuedAdded(this);
		return true;
	}

	@Override
	public boolean removeListener(ValuedListener listener) {
	    if(listeners2==null) return false;
		boolean r=listeners2.remove(listener);
		if(listeners2.size()==0)
		  listeners2=null;
		//listener.onValuedRemoved(this);
		return r;
	}

	@Override
	public void removeListener(byte pid, ValuedListener listener) {
		List<ValuedListener> l = getListeners(pid);
		if (l == null)
			return;
		l.remove(listener);
		if (l.size() == 0)
			l.remove(pid);
	}

$if(c.supportsPids)$
	final private List<ValuedListener> getListeners(byte pid) {
		return listeners.get(pid);
	}

	final private Node<List<ValuedListener>> getListenersNode(byte pid) {
		return listeners.getNodeOrCreate(pid);
	}

	final private List<ValuedListener> getListeners(String name) {
		return listeners.get(askPid(name));
	}

	final private Node<List<ValuedListener>> getListenersNode(String name) {
		return listeners.getNodeOrCreate(askPid(name));
	}
$else$
	final private List<ValuedListener> getListeners(byte pid) {
		return listeners.get((byte) askPosition(pid));
	}

	final private Node<List<ValuedListener>> getListenersNode(byte pid) {
		return listeners.getNodeOrCreate((byte) askPosition(pid));
	}

	final private List<ValuedListener> getListeners(String name) {
		return listeners.get((byte) askPosition(name));
	}

	final private Node<List<ValuedListener>> getListenersNode(String name) {
		return listeners.getNodeOrCreate((byte) askPosition(name));
	}
$endif$

	@Override
	public void addListener(String field, ValuedListener listener) {
		Node<List<ValuedListener>> n = getListenersNode(field);
		List<ValuedListener> l = n.getValue();
		if (l == null) {
			n.setValue(l = new ArrayList<ValuedListener>(1));
			l.add(listener);
		} else if (!l.contains(listener))
			l.add(listener);
	}

	@Override
	public void removeListener(String field, ValuedListener listener) {
		List<ValuedListener> l = getListeners(field);
		if (l == null)
			return;
		l.remove(listener);
		if (l.size() == 0)
			l.remove(field);
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
	
	
  @Override
  public Iterable<ValuedListener> getValuedListeners()
  {
    return listeners2 == null ? com.f1.utils.EmptyCollection.INSTANCE : listeners2; // TODO :consider field level listeners
  }
  
  @Override
  public void askChildValuedListenables(List<ValuedListenable> sink){
  $c.params:{ x |
  $if(!x.isImmutable)$
     {
       final Object o=(Object)get$x.upperCasedName$();
       if(o instanceof ValuedListenable) sink.add((ValuedListenable)o);
     }
     $endif$
  }$
  }
  
}
