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


public  class AmiCenterManageDatasourceRequest000 extends com.f1.ami.amicommon.msg.AmiCenterManageDatasourceRequest00 implements Iterable<ValuedParam>,ByteArraySelfConverter{

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
    public void setAdapter(java.lang.String _adapter){
	    super.setAdapter(_adapter);
	    existing.set(0,true);
    }
    public boolean askExistsAdapter(){
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
    public void setDelete(boolean _delete){
	    super.setDelete(_delete);
	    existing.set(2,true);
    }
    public boolean askExistsDelete(){
	    return existing.get(2);
    }

	@Override
    public void setEdit(boolean _edit){
	    super.setEdit(_edit);
	    existing.set(3,true);
    }
    public boolean askExistsEdit(){
	    return existing.get(3);
    }

	@Override
    public void setId(long _id){
	    super.setId(_id);
	    existing.set(4,true);
    }
    public boolean askExistsId(){
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
    public void setName(java.lang.String _name){
	    super.setName(_name);
	    existing.set(6,true);
    }
    public boolean askExistsName(){
	    return existing.get(6);
    }

	@Override
    public void setOptions(java.lang.String _options){
	    super.setOptions(_options);
	    existing.set(7,true);
    }
    public boolean askExistsOptions(){
	    return existing.get(7);
    }

	@Override
    public void setPassword(java.lang.String _password){
	    super.setPassword(_password);
	    existing.set(8,true);
    }
    public boolean askExistsPassword(){
	    return existing.get(8);
    }

	@Override
    public void setPermittedOverrides(java.lang.String _permittedOverrides){
	    super.setPermittedOverrides(_permittedOverrides);
	    existing.set(9,true);
    }
    public boolean askExistsPermittedOverrides(){
	    return existing.get(9);
    }

	@Override
    public void setPriority(int _priority){
	    super.setPriority(_priority);
	    existing.set(10,true);
    }
    public boolean askExistsPriority(){
	    return existing.get(10);
    }

	@Override
    public void setRelayId(java.lang.String _relayId){
	    super.setRelayId(_relayId);
	    existing.set(11,true);
    }
    public boolean askExistsRelayId(){
	    return existing.get(11);
    }

	@Override
    public void setRequestTime(long _requestTime){
	    super.setRequestTime(_requestTime);
	    existing.set(12,true);
    }
    public boolean askExistsRequestTime(){
	    return existing.get(12);
    }

	@Override
    public void setSelectedName(java.lang.String _selectedName){
	    super.setSelectedName(_selectedName);
	    existing.set(13,true);
    }
    public boolean askExistsSelectedName(){
	    return existing.get(13);
    }

	@Override
    public void setSkipTest(boolean _skipTest){
	    super.setSkipTest(_skipTest);
	    existing.set(14,true);
    }
    public boolean askExistsSkipTest(){
	    return existing.get(14);
    }

	@Override
    public void setUrl(java.lang.String _url){
	    super.setUrl(_url);
	    existing.set(15,true);
    }
    public boolean askExistsUrl(){
	    return existing.get(15);
    }

	@Override
    public void setUsername(java.lang.String _username){
	    super.setUsername(_username);
	    existing.set(16,true);
    }
    public boolean askExistsUsername(){
	    return existing.get(16);
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
	
    
	public AmiCenterManageDatasourceRequest000 nw(){
	    return new AmiCenterManageDatasourceRequest000();
	}

	public AmiCenterManageDatasourceRequest000 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiCenterManageDatasourceRequest000 nwCast(Class[] types, Object[] args){
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
        
    out.writeByte(6);
    out.writeLong(this.getId());
        
}

	    if(existing.get(6) && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this.getName(),session);
        
}

	    if(existing.get(0) && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this.getAdapter(),session);
        
}

	    if(existing.get(15) && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this.getUrl(),session);
        
}

	    if(existing.get(16) && (0 & transience)==0){
    out.writeByte(5);
        
    converter.write(this.getUsername(),session);
        
}

	    if(existing.get(8) && (0 & transience)==0){
    out.writeByte(6);
        
    converter.write(this.getPassword(),session);
        
}

	    if(existing.get(7) && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this.getOptions(),session);
        
}

	    if(existing.get(2) && (0 & transience)==0){
    out.writeByte(8);
        
    out.writeByte(0);
    out.writeBoolean(this.getDelete());
        
}

	    if(existing.get(3) && (0 & transience)==0){
    out.writeByte(9);
        
    out.writeByte(0);
    out.writeBoolean(this.getEdit());
        
}

	    if(existing.get(13) && (0 & transience)==0){
    out.writeByte(10);
        
    converter.write(this.getSelectedName(),session);
        
}

	    if(existing.get(11) && (0 & transience)==0){
    out.writeByte(11);
        
    converter.write(this.getRelayId(),session);
        
}

	    if(existing.get(9) && (0 & transience)==0){
    out.writeByte(12);
        
    converter.write(this.getPermittedOverrides(),session);
        
}

	    if(existing.get(14) && (0 & transience)==0){
    out.writeByte(13);
        
    out.writeByte(0);
    out.writeBoolean(this.getSkipTest());
        
}

	    if(existing.get(5) && (0 & transience)==0){
    out.writeByte(51);
        
    converter.write(this.getInvokedBy(),session);
        
}

	    if(existing.get(1) && (0 & transience)==0){
    out.writeByte(52);
        
    converter.write(this.getComment(),session);
        
}

	    if(existing.get(10) && (0 & transience)==0){
    out.writeByte(53);
        
    out.writeByte(4);
    out.writeInt(this.getPriority());
        
}

	    if(existing.get(12) && (0 & transience)==0){
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
        
                    if((basicType=in.readByte())!=6)
                        break;
                    this.setId(in.readLong());
        
                    continue;
  
                case 2:
        
                    this.setName((java.lang.String)converter.read(session));
        
                    continue;
  
                case 3:
        
                    this.setAdapter((java.lang.String)converter.read(session));
        
                    continue;
  
                case 4:
        
                    this.setUrl((java.lang.String)converter.read(session));
        
                    continue;
  
                case 5:
        
                    this.setUsername((java.lang.String)converter.read(session));
        
                    continue;
  
                case 6:
        
                    this.setPassword((java.lang.String)converter.read(session));
        
                    continue;
  
                case 7:
        
                    this.setOptions((java.lang.String)converter.read(session));
        
                    continue;
  
                case 8:
        
                    if((basicType=in.readByte())!=0)
                        break;
                    this.setDelete(in.readBoolean());
        
                    continue;
  
                case 9:
        
                    if((basicType=in.readByte())!=0)
                        break;
                    this.setEdit(in.readBoolean());
        
                    continue;
  
                case 10:
        
                    this.setSelectedName((java.lang.String)converter.read(session));
        
                    continue;
  
                case 11:
        
                    this.setRelayId((java.lang.String)converter.read(session));
        
                    continue;
  
                case 12:
        
                    this.setPermittedOverrides((java.lang.String)converter.read(session));
        
                    continue;
  
                case 13:
        
                    if((basicType=in.readByte())!=0)
                        break;
                    this.setSkipTest(in.readBoolean());
        
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