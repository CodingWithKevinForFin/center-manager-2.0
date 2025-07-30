//Coded by PartialMessageCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ByteIterator;
import com.f1.utils.VH;
import java.util.BitSet;
import java.util.Iterator;
import com.f1.utils.converter.bytes.ByteArraySelfConverter;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;


public  class AmiWebManagerGetFileResponse000 extends com.f1.ami.amicommon.msg.AmiWebManagerGetFileResponse00 implements Iterable<ValuedParam>,ByteArraySelfConverter{

    private final BitSet existing=new BitSet(askParamsCount());


    
	public void clear(){
	  ValuedParam vp[]=askValuedParams();
	  for(int i=existing.nextSetBit(0);i!=-1;i=existing.nextSetBit(i+1))
	    vp[i].clear(this);
	  existing.clear();
	}

	public boolean askExists(String name){
	    return existing.get(askPosition(name));
	}

	public boolean askExists(byte pid){
	    return existing.get(askPosition(pid));
	}

	public ByteIterator askExistingPids(){
	    return new com.f1.codegen.helpers.PartialMessagePidIterator(existing,this);
	}

	public Iterable<ValuedParam> askExistingValuedParams(){
	    return this;
	}
	
	public Iterator<ValuedParam> iterator(){
	    return new com.f1.codegen.helpers.PartialMessageNameIterator(existing,this);
	}

	@Override
    public void setException(java.lang.Exception _exception){
	    super.setException(_exception);
	    existing.set(0,true);
    }
    public boolean askExistsException(){
	    return existing.get(0);
    }

	@Override
    public void setFile(com.f1.ami.amicommon.msg.AmiFileMessage _file){
	    super.setFile(_file);
	    existing.set(1,true);
    }
    public boolean askExistsFile(){
	    return existing.get(1);
    }

	@Override
    public void setMessage(java.lang.String _message){
	    super.setMessage(_message);
	    existing.set(2,true);
    }
    public boolean askExistsMessage(){
	    return existing.get(2);
    }

	@Override
    public void setOk(boolean _ok){
	    super.setOk(_ok);
	    existing.set(3,true);
    }
    public boolean askExistsOk(){
	    return existing.get(3);
    }

	@Override
    public void setPriority(int _priority){
	    super.setPriority(_priority);
	    existing.set(4,true);
    }
    public boolean askExistsPriority(){
	    return existing.get(4);
    }

	@Override
    public void setProgress(double _progress){
	    super.setProgress(_progress);
	    existing.set(5,true);
    }
    public boolean askExistsProgress(){
	    return existing.get(5);
    }

	@Override
    public void setStatus(byte _status){
	    super.setStatus(_status);
	    existing.set(6,true);
    }
    public boolean askExistsStatus(){
	    return existing.get(6);
    }

	@Override
    public void setTicket(java.lang.String _ticket){
	    super.setTicket(_ticket);
	    existing.set(7,true);
    }
    public boolean askExistsTicket(){
	    return existing.get(7);
    }

	@Override
    public void setTrackedEvents(java.util.List _trackedEvents){
	    super.setTrackedEvents(_trackedEvents);
	    existing.set(8,true);
    }
    public boolean askExistsTrackedEvents(){
	    return existing.get(8);
    }


	@Override
	public void put(String name, Object value) {
	    super.put(name,value);
	    existing.set(askPosition(name),true);
	}

	@Override
	public boolean putNoThrow(String name, Object value) {
	    if(!super.putNoThrow(name,value))
	      return false;
	    existing.set(askPosition(name),true);
	    return true;
	}

	@Override
	public void put(byte pid, Object value) {
	    super.put(pid,value);
	    existing.set(askPosition(pid),true);
	}

	@Override
	public boolean putNoThrow(byte pid, Object value) {
	    if(!super.putNoThrow(pid,value))
	      return false;
	    existing.set(askPosition(pid),true);
	    return true;
	}

	@Override
	public void putBoolean(byte pid, boolean value) {
	    super.putBoolean(pid,value);
	    existing.set(askPosition(pid),true);
	}

	@Override
	public void putByte(byte pid, byte value) {
	    super.putByte(pid,value);
	    existing.set(askPosition(pid),true);
	}

	@Override
	public void putChar(byte pid, char value) {
	    super.putChar(pid,value);
	    existing.set(askPosition(pid),true);
	}

	@Override
	public void putShort(byte pid, short value) {
	    super.putShort(pid,value);
	    existing.set(askPosition(pid),true);
	}

	@Override
	public void putInt(byte pid, int value) {
	    super.putInt(pid,value);
	    existing.set(askPosition(pid),true);
	}

	@Override
	public void putLong(byte pid, long value) {
	    super.putLong(pid,value);
	    existing.set(askPosition(pid),true);
	}

	@Override
	public void putFloat(byte pid, float value) {
	    super.putFloat(pid,value);
	    existing.set(askPosition(pid),true);
	}

	@Override
	public void putDouble(byte pid, double value) {
	    super.putDouble(pid,value);
	    existing.set(askPosition(pid),true);
	}
    
	public void removeValue(String name){
	  ValuedParam vp=askValuedParam(name);
	  vp.clear(this);
	  existing.set(vp.askPosition(),false);
	}
	
	public void removeValue(byte pid){
	  ValuedParam vp=askValuedParam(pid);
	  vp.clear(this);
	  existing.set(vp.askPosition(),false);
	}
	
    
	public AmiWebManagerGetFileResponse000 nw(){
	    return new AmiWebManagerGetFileResponse000();
	}

	public AmiWebManagerGetFileResponse000 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiWebManagerGetFileResponse000 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
	



    public void write(ToByteArrayConverterSession session) throws IOException{
        ObjectToByteArrayConverter converter=session.getConverter();
        byte transience=converter.getSkipTransience();
        DataOutput out=session.getStream();//
        out.writeBoolean(true);//pids supported
        
	    if(existing.get(1) && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this.getFile(),session);
        
}

	    if(existing.get(6) && (0 & transience)==0){
    out.writeByte(2);
        
    out.writeByte(1);
    out.writeByte(this.getStatus());
        
}

	    if(existing.get(3) && (0 & transience)==0){
    out.writeByte(42);
        
    out.writeByte(0);
    out.writeBoolean(this.getOk());
        
}

	    if(existing.get(2) && (0 & transience)==0){
    out.writeByte(43);
        
    converter.write(this.getMessage(),session);
        
}

	    if(existing.get(0) && (0 & transience)==0){
    out.writeByte(44);
        
    converter.write(this.getException(),session);
        
}

	    if(existing.get(5) && (0 & transience)==0){
    out.writeByte(53);
        
    out.writeByte(7);
    out.writeDouble(this.getProgress());
        
}

	    if(existing.get(7) && (0 & transience)==0){
    out.writeByte(55);
        
    converter.write(this.getTicket(),session);
        
}

	    if(existing.get(4) && (0 & transience)==0){
    out.writeByte(57);
        
    out.writeByte(4);
    out.writeInt(this.getPriority());
        
}

	    if(existing.get(8) && (0 & transience)==0){
    out.writeByte(58);
        
    converter.write(this.getTrackedEvents(),session);
        
}
;
        out.writeByte(-1);
    }

    public void read(FromByteArrayConverterSession session) throws IOException{
        ObjectToByteArrayConverter converter=session.getConverter();
        DataInput in=session.getStream();
        if(!in.readBoolean()){
            for (;;) {
                final String name = com.f1.utils.converter.bytes.StringToByteArrayConverter.readString(in);
                if (name.length() == 0)
                    break;
                put(name, converter.read(session));
            }
            return;
		}
        byte basicType;
        for(;;){
            final byte pid=in.readByte();
            switch(pid){
        
                case 1:
        
                    this.setFile((com.f1.ami.amicommon.msg.AmiFileMessage)converter.read(session));
        
                    continue;
  
                case 2:
        
                    if((basicType=in.readByte())!=1)
                        break;
                    this.setStatus(in.readByte());
        
                    continue;
  
                case 42:
        
                    if((basicType=in.readByte())!=0)
                        break;
                    this.setOk(in.readBoolean());
        
                    continue;
  
                case 43:
        
                    this.setMessage((java.lang.String)converter.read(session));
        
                    continue;
  
                case 44:
        
                    this.setException((java.lang.Exception)converter.read(session));
        
                    continue;
  
                case 53:
        
                    if((basicType=in.readByte())!=7)
                        break;
                    this.setProgress(in.readDouble());
        
                    continue;
  
                case 55:
        
                    this.setTicket((java.lang.String)converter.read(session));
        
                    continue;
  
                case 57:
        
                    if((basicType=in.readByte())!=4)
                        break;
                    this.setPriority(in.readInt());
        
                    continue;
  
                case 58:
        
                    this.setTrackedEvents((java.util.List)converter.read(session));
        
                    continue;
                case -1:
                    return;
              default:
                  basicType=in.readByte();
                  break;
                }
            com.f1.utils.converter.bytes.ByteArrayConverter bac=converter.getConverter(basicType);
            if(bac==null)
               throw new RuntimeException("Converter not found for pid "+pid+" basic type:" +basicType);
            bac.read(session);//THIS WILL SILENTLY READ AND DROP THE INVALID VALUE
        }
    }

}