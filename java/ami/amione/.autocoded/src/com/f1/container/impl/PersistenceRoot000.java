//Coded by ValuedListenableCodeTemplate
package com.f1.container.impl;

import com.f1.base.*;
import java.util.List;
import java.util.ArrayList;
import com.f1.utils.structs.ByteKeyMap;
import com.f1.utils.structs.ByteKeyMap.Node;


public  class PersistenceRoot000 extends com.f1.container.impl.PersistenceRoot00 implements com.f1.base.CodeGenerated {

	private ByteKeyMap<List<ValuedListener>> listeners = new ByteKeyMap<List<ValuedListener>>();
	private List<ValuedListener> listeners2 = null;
	
	public PersistenceRoot000 clone(){
	   final PersistenceRoot000 r=(PersistenceRoot000)super.clone();
	   r.listeners=new ByteKeyMap<List<ValuedListener>>();
	   r.listeners2=null;
	   return r;
	}
	


//class com.f1.container.impl.PersistenceRoot00

	@Override
    public void setPartitionId(java.lang.Object _partitionId){
		List<ValuedListener> l=listeners.get((byte)1);
		if (l != null || listeners2!=null) {
			final java.lang.Object old = super.getPartitionId();
			super.setPartitionId(_partitionId);
			if(l!=null) for (int i=0,c=l.size();i<c;i++)

				l.get(i).onValued(this,"partitionId", (byte)1,old,_partitionId);

			if(listeners2!=null) for (int i=0,c=listeners2.size();i<c;i++){
              

				listeners2.get(i).onValued(this,"partitionId", (byte)1,old,_partitionId);

           }
		} else
			super.setPartitionId(_partitionId);
    }




//class com.f1.container.impl.PersistenceRoot00

	@Override
    public void setPersistedRoot(com.f1.base.ValuedListenable _persistedRoot){
		List<ValuedListener> l=listeners.get((byte)3);
		if (l != null || listeners2!=null) {
			final com.f1.base.ValuedListenable old = super.getPersistedRoot();
			super.setPersistedRoot(_persistedRoot);
			if(l!=null) for (int i=0,c=l.size();i<c;i++)

				l.get(i).onValued(this,"persistedRoot", (byte)3,old,_persistedRoot);

			if(listeners2!=null) for (int i=0,c=listeners2.size();i<c;i++){
              
                 //if(_persistedRoot!=null) _persistedRoot.addListener(listeners2.get(i));
              

				listeners2.get(i).onValued(this,"persistedRoot", (byte)3,old,_persistedRoot);

           }
		} else
			super.setPersistedRoot(_persistedRoot);
    }




//class com.f1.container.impl.PersistenceRoot00

	@Override
    public void setStateType(java.lang.Class _stateType){
		List<ValuedListener> l=listeners.get((byte)4);
		if (l != null || listeners2!=null) {
			final java.lang.Class old = super.getStateType();
			super.setStateType(_stateType);
			if(l!=null) for (int i=0,c=l.size();i<c;i++)

				l.get(i).onValued(this,"stateType", (byte)4,old,_stateType);

			if(listeners2!=null) for (int i=0,c=listeners2.size();i<c;i++){
              

				listeners2.get(i).onValued(this,"stateType", (byte)4,old,_stateType);

           }
		} else
			super.setStateType(_stateType);
    }




//class com.f1.container.impl.PersistenceRoot00

	@Override
    public void setType(java.lang.Class _type){
		List<ValuedListener> l=listeners.get((byte)2);
		if (l != null || listeners2!=null) {
			final java.lang.Class old = super.getType();
			super.setType(_type);
			if(l!=null) for (int i=0,c=l.size();i<c;i++)

				l.get(i).onValued(this,"type", (byte)2,old,_type);

			if(listeners2!=null) for (int i=0,c=listeners2.size();i<c;i++){
              

				listeners2.get(i).onValued(this,"type", (byte)2,old,_type);

           }
		} else
			super.setType(_type);
    }








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

    
	public PersistenceRoot000 nw(){
	    return new PersistenceRoot000();
	}

	public PersistenceRoot000 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public PersistenceRoot000 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
	
	
  @Override
  public Iterable<ValuedListener> getValuedListeners()
  {
    return listeners2 == null ? com.f1.utils.EmptyCollection.INSTANCE : listeners2; // TODO :consider field level listeners
  }
  
  @Override
  public void askChildValuedListenables(List<ValuedListenable> sink){
  
  
  
  
     {
       final Object o=(Object)getPersistedRoot();
       if(o instanceof ValuedListenable) sink.add((ValuedListenable)o);
     }
     
  
  
  
  
  
  }
  
}
