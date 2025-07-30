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


public  class AmiRelayRunDbRequest000 extends com.f1.ami.amicommon.msg.AmiRelayRunDbRequest00 implements Iterable<ValuedParam>,ByteArraySelfConverter{

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
    public void setCenterId(byte _centerId){
	    super.setCenterId(_centerId);
	    existing.set(0,true);
    }
    public boolean askExistsCenterId(){
	    return existing.get(0);
    }

	@Override
    public void setClientRequest(com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest _clientRequest){
	    super.setClientRequest(_clientRequest);
	    existing.set(1,true);
    }
    public boolean askExistsClientRequest(){
	    return existing.get(1);
    }

	@Override
    public void setDsAdapter(java.lang.String _dsAdapter){
	    super.setDsAdapter(_dsAdapter);
	    existing.set(2,true);
    }
    public boolean askExistsDsAdapter(){
	    return existing.get(2);
    }

	@Override
    public void setDsAmiId(long _dsAmiId){
	    super.setDsAmiId(_dsAmiId);
	    existing.set(3,true);
    }
    public boolean askExistsDsAmiId(){
	    return existing.get(3);
    }

	@Override
    public void setDsName(java.lang.String _dsName){
	    super.setDsName(_dsName);
	    existing.set(4,true);
    }
    public boolean askExistsDsName(){
	    return existing.get(4);
    }

	@Override
    public void setDsOptions(java.lang.String _dsOptions){
	    super.setDsOptions(_dsOptions);
	    existing.set(5,true);
    }
    public boolean askExistsDsOptions(){
	    return existing.get(5);
    }

	@Override
    public void setDsPassword(java.lang.String _dsPassword){
	    super.setDsPassword(_dsPassword);
	    existing.set(6,true);
    }
    public boolean askExistsDsPassword(){
	    return existing.get(6);
    }

	@Override
    public void setDsRelayId(java.lang.String _dsRelayId){
	    super.setDsRelayId(_dsRelayId);
	    existing.set(7,true);
    }
    public boolean askExistsDsRelayId(){
	    return existing.get(7);
    }

	@Override
    public void setDsUrl(java.lang.String _dsUrl){
	    super.setDsUrl(_dsUrl);
	    existing.set(8,true);
    }
    public boolean askExistsDsUrl(){
	    return existing.get(8);
    }

	@Override
    public void setDsUsername(java.lang.String _dsUsername){
	    super.setDsUsername(_dsUsername);
	    existing.set(9,true);
    }
    public boolean askExistsDsUsername(){
	    return existing.get(9);
    }

	@Override
    public void setInvokedBy(java.lang.String _invokedBy){
	    super.setInvokedBy(_invokedBy);
	    existing.set(10,true);
    }
    public boolean askExistsInvokedBy(){
	    return existing.get(10);
    }

	@Override
    public void setTargetAgentProcessUid(java.lang.String _targetAgentProcessUid){
	    super.setTargetAgentProcessUid(_targetAgentProcessUid);
	    existing.set(11,true);
    }
    public boolean askExistsTargetAgentProcessUid(){
	    return existing.get(11);
    }

	@Override
    public void setTimeoutMs(int _timeoutMs){
	    super.setTimeoutMs(_timeoutMs);
	    existing.set(12,true);
    }
    public boolean askExistsTimeoutMs(){
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
	
    
	public AmiRelayRunDbRequest000 nw(){
	    return new AmiRelayRunDbRequest000();
	}

	public AmiRelayRunDbRequest000 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiRelayRunDbRequest000 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
	



    public void write(ToByteArrayConverterSession session) throws IOException{
        ObjectToByteArrayConverter converter=session.getConverter();
        byte transience=converter.getSkipTransience();
        DataOutput out=session.getStream();//
        out.writeBoolean(true);//pids supported
        
	    if(existing.get(2) && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this.getDsAdapter(),session);
        
}

	    if(existing.get(6) && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this.getDsPassword(),session);
        
}

	    if(existing.get(8) && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this.getDsUrl(),session);
        
}

	    if(existing.get(5) && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this.getDsOptions(),session);
        
}

	    if(existing.get(9) && (0 & transience)==0){
    out.writeByte(5);
        
    converter.write(this.getDsUsername(),session);
        
}

	    if(existing.get(4) && (0 & transience)==0){
    out.writeByte(6);
        
    converter.write(this.getDsName(),session);
        
}

	    if(existing.get(1) && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this.getClientRequest(),session);
        
}

	    if(existing.get(12) && (0 & transience)==0){
    out.writeByte(8);
        
    out.writeByte(4);
    out.writeInt(this.getTimeoutMs());
        
}

	    if(existing.get(7) && (0 & transience)==0){
    out.writeByte(9);
        
    converter.write(this.getDsRelayId(),session);
        
}

	    if(existing.get(3) && (0 & transience)==0){
    out.writeByte(10);
        
    out.writeByte(6);
    out.writeLong(this.getDsAmiId());
        
}

	    if(existing.get(11) && (0 & transience)==0){
    out.writeByte(40);
        
    converter.write(this.getTargetAgentProcessUid(),session);
        
}

	    if(existing.get(10) && (0 & transience)==0){
    out.writeByte(41);
        
    converter.write(this.getInvokedBy(),session);
        
}

	    if(existing.get(0) && (0 & transience)==0){
    out.writeByte(42);
        
    out.writeByte(1);
    out.writeByte(this.getCenterId());
        
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
        
                    this.setDsAdapter((java.lang.String)converter.read(session));
        
                    continue;
  
                case 2:
        
                    this.setDsPassword((java.lang.String)converter.read(session));
        
                    continue;
  
                case 3:
        
                    this.setDsUrl((java.lang.String)converter.read(session));
        
                    continue;
  
                case 4:
        
                    this.setDsOptions((java.lang.String)converter.read(session));
        
                    continue;
  
                case 5:
        
                    this.setDsUsername((java.lang.String)converter.read(session));
        
                    continue;
  
                case 6:
        
                    this.setDsName((java.lang.String)converter.read(session));
        
                    continue;
  
                case 7:
        
                    this.setClientRequest((com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest)converter.read(session));
        
                    continue;
  
                case 8:
        
                    if((basicType=in.readByte())!=4)
                        break;
                    this.setTimeoutMs(in.readInt());
        
                    continue;
  
                case 9:
        
                    this.setDsRelayId((java.lang.String)converter.read(session));
        
                    continue;
  
                case 10:
        
                    if((basicType=in.readByte())!=6)
                        break;
                    this.setDsAmiId(in.readLong());
        
                    continue;
  
                case 40:
        
                    this.setTargetAgentProcessUid((java.lang.String)converter.read(session));
        
                    continue;
  
                case 41:
        
                    this.setInvokedBy((java.lang.String)converter.read(session));
        
                    continue;
  
                case 42:
        
                    if((basicType=in.readByte())!=1)
                        break;
                    this.setCenterId(in.readByte());
        
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