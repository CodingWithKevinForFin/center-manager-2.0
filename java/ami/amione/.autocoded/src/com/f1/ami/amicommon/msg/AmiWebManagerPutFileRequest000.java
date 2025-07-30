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


public  class AmiWebManagerPutFileRequest000 extends com.f1.ami.amicommon.msg.AmiWebManagerPutFileRequest00 implements Iterable<ValuedParam>,ByteArraySelfConverter{

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
    public void setAction(short _action){
	    super.setAction(_action);
	    existing.set(0,true);
    }
    public boolean askExistsAction(){
	    return existing.get(0);
    }

	@Override
    public void setComment(java.lang.String _comment){
	    super.setComment(_comment);
	    existing.set(1,true);
    }
    public boolean askExistsComment(){
	    return existing.get(1);
    }

	@Override
    public void setData(byte[] _data){
	    super.setData(_data);
	    existing.set(2,true);
    }
    public boolean askExistsData(){
	    return existing.get(2);
    }

	@Override
    public void setExecutable(java.lang.Boolean _executable){
	    super.setExecutable(_executable);
	    existing.set(3,true);
    }
    public boolean askExistsExecutable(){
	    return existing.get(3);
    }

	@Override
    public void setFileName(java.lang.String _fileName){
	    super.setFileName(_fileName);
	    existing.set(4,true);
    }
    public boolean askExistsFileName(){
	    return existing.get(4);
    }

	@Override
    public void setInvokedBy(java.lang.String _invokedBy){
	    super.setInvokedBy(_invokedBy);
	    existing.set(5,true);
    }
    public boolean askExistsInvokedBy(){
	    return existing.get(5);
    }

	@Override
    public void setOptions(short _options){
	    super.setOptions(_options);
	    existing.set(6,true);
    }
    public boolean askExistsOptions(){
	    return existing.get(6);
    }

	@Override
    public void setPriority(int _priority){
	    super.setPriority(_priority);
	    existing.set(7,true);
    }
    public boolean askExistsPriority(){
	    return existing.get(7);
    }

	@Override
    public void setReadable(java.lang.Boolean _readable){
	    super.setReadable(_readable);
	    existing.set(8,true);
    }
    public boolean askExistsReadable(){
	    return existing.get(8);
    }

	@Override
    public void setRequestTime(long _requestTime){
	    super.setRequestTime(_requestTime);
	    existing.set(9,true);
    }
    public boolean askExistsRequestTime(){
	    return existing.get(9);
    }

	@Override
    public void setSafeFileExtension(java.lang.String _safeFileExtension){
	    super.setSafeFileExtension(_safeFileExtension);
	    existing.set(10,true);
    }
    public boolean askExistsSafeFileExtension(){
	    return existing.get(10);
    }

	@Override
    public void setTargetFileName(java.lang.String _targetFileName){
	    super.setTargetFileName(_targetFileName);
	    existing.set(11,true);
    }
    public boolean askExistsTargetFileName(){
	    return existing.get(11);
    }

	@Override
    public void setWritable(java.lang.Boolean _writable){
	    super.setWritable(_writable);
	    existing.set(12,true);
    }
    public boolean askExistsWritable(){
	    return existing.get(12);
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
	
    
	public AmiWebManagerPutFileRequest000 nw(){
	    return new AmiWebManagerPutFileRequest000();
	}

	public AmiWebManagerPutFileRequest000 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiWebManagerPutFileRequest000 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
	



    public void write(ToByteArrayConverterSession session) throws IOException{
        ObjectToByteArrayConverter converter=session.getConverter();
        byte transience=converter.getSkipTransience();
        DataOutput out=session.getStream();//
        out.writeBoolean(true);//pids supported
        
	    if(existing.get(4) && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this.getFileName(),session);
        
}

	    if(existing.get(0) && (0 & transience)==0){
    out.writeByte(2);
        
    out.writeByte(2);
    out.writeShort(this.getAction());
        
}

	    if(existing.get(2) && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this.getData(),session);
        
}

	    if(existing.get(11) && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this.getTargetFileName(),session);
        
}

	    if(existing.get(12) && (0 & transience)==0){
    out.writeByte(5);
        
    converter.write(this.getWritable(),session);
        
}

	    if(existing.get(8) && (0 & transience)==0){
    out.writeByte(6);
        
    converter.write(this.getReadable(),session);
        
}

	    if(existing.get(3) && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this.getExecutable(),session);
        
}

	    if(existing.get(6) && (0 & transience)==0){
    out.writeByte(8);
        
    out.writeByte(2);
    out.writeShort(this.getOptions());
        
}

	    if(existing.get(10) && (0 & transience)==0){
    out.writeByte(9);
        
    converter.write(this.getSafeFileExtension(),session);
        
}

	    if(existing.get(5) && (0 & transience)==0){
    out.writeByte(51);
        
    converter.write(this.getInvokedBy(),session);
        
}

	    if(existing.get(1) && (0 & transience)==0){
    out.writeByte(52);
        
    converter.write(this.getComment(),session);
        
}

	    if(existing.get(7) && (0 & transience)==0){
    out.writeByte(53);
        
    out.writeByte(4);
    out.writeInt(this.getPriority());
        
}

	    if(existing.get(9) && (0 & transience)==0){
    out.writeByte(54);
        
    out.writeByte(6);
    out.writeLong(this.getRequestTime());
        
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
        
                    this.setFileName((java.lang.String)converter.read(session));
        
                    continue;
  
                case 2:
        
                    if((basicType=in.readByte())!=2)
                        break;
                    this.setAction(in.readShort());
        
                    continue;
  
                case 3:
        
                    this.setData((byte[])converter.read(session));
        
                    continue;
  
                case 4:
        
                    this.setTargetFileName((java.lang.String)converter.read(session));
        
                    continue;
  
                case 5:
        
                    this.setWritable((java.lang.Boolean)converter.read(session));
        
                    continue;
  
                case 6:
        
                    this.setReadable((java.lang.Boolean)converter.read(session));
        
                    continue;
  
                case 7:
        
                    this.setExecutable((java.lang.Boolean)converter.read(session));
        
                    continue;
  
                case 8:
        
                    if((basicType=in.readByte())!=2)
                        break;
                    this.setOptions(in.readShort());
        
                    continue;
  
                case 9:
        
                    this.setSafeFileExtension((java.lang.String)converter.read(session));
        
                    continue;
  
                case 51:
        
                    this.setInvokedBy((java.lang.String)converter.read(session));
        
                    continue;
  
                case 52:
        
                    this.setComment((java.lang.String)converter.read(session));
        
                    continue;
  
                case 53:
        
                    if((basicType=in.readByte())!=4)
                        break;
                    this.setPriority(in.readInt());
        
                    continue;
  
                case 54:
        
                    if((basicType=in.readByte())!=6)
                        break;
                    this.setRequestTime(in.readLong());
        
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