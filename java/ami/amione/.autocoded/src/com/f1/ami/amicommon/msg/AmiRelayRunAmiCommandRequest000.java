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


public  class AmiRelayRunAmiCommandRequest000 extends com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest00 implements Iterable<ValuedParam>,ByteArraySelfConverter{

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
    public void setAmiObjectIds(long[] _amiObjectIds){
	    super.setAmiObjectIds(_amiObjectIds);
	    existing.set(0,true);
    }
    public boolean askExistsAmiObjectIds(){
	    return existing.get(0);
    }

	@Override
    public void setAppId(java.lang.String _appId){
	    super.setAppId(_appId);
	    existing.set(1,true);
    }
    public boolean askExistsAppId(){
	    return existing.get(1);
    }

	@Override
    public void setArguments(java.util.Map _arguments){
	    super.setArguments(_arguments);
	    existing.set(2,true);
    }
    public boolean askExistsArguments(){
	    return existing.get(2);
    }

	@Override
    public void setCenterId(byte _centerId){
	    super.setCenterId(_centerId);
	    existing.set(3,true);
    }
    public boolean askExistsCenterId(){
	    return existing.get(3);
    }

	@Override
    public void setCommandDefinitionId(java.lang.String _commandDefinitionId){
	    super.setCommandDefinitionId(_commandDefinitionId);
	    existing.set(4,true);
    }
    public boolean askExistsCommandDefinitionId(){
	    return existing.get(4);
    }

	@Override
    public void setCommandId(long _commandId){
	    super.setCommandId(_commandId);
	    existing.set(5,true);
    }
    public boolean askExistsCommandId(){
	    return existing.get(5);
    }

	@Override
    public void setCommandUid(java.lang.String _commandUid){
	    super.setCommandUid(_commandUid);
	    existing.set(6,true);
    }
    public boolean askExistsCommandUid(){
	    return existing.get(6);
    }

	@Override
    public void setFields(java.util.List _fields){
	    super.setFields(_fields);
	    existing.set(7,true);
    }
    public boolean askExistsFields(){
	    return existing.get(7);
    }

	@Override
    public void setHostIp(java.lang.String _hostIp){
	    super.setHostIp(_hostIp);
	    existing.set(8,true);
    }
    public boolean askExistsHostIp(){
	    return existing.get(8);
    }

	@Override
    public void setInvokedBy(java.lang.String _invokedBy){
	    super.setInvokedBy(_invokedBy);
	    existing.set(9,true);
    }
    public boolean askExistsInvokedBy(){
	    return existing.get(9);
    }

	@Override
    public void setIsManySelect(boolean _isManySelect){
	    super.setIsManySelect(_isManySelect);
	    existing.set(10,true);
    }
    public boolean askExistsIsManySelect(){
	    return existing.get(10);
    }

	@Override
    public void setObjectIds(java.lang.String[] _objectIds){
	    super.setObjectIds(_objectIds);
	    existing.set(11,true);
    }
    public boolean askExistsObjectIds(){
	    return existing.get(11);
    }

	@Override
    public void setObjectTypes(java.lang.String[] _objectTypes){
	    super.setObjectTypes(_objectTypes);
	    existing.set(12,true);
    }
    public boolean askExistsObjectTypes(){
	    return existing.get(12);
    }

	@Override
    public void setRelayConnectionId(int _relayConnectionId){
	    super.setRelayConnectionId(_relayConnectionId);
	    existing.set(13,true);
    }
    public boolean askExistsRelayConnectionId(){
	    return existing.get(13);
    }

	@Override
    public void setSessionId(java.lang.String _sessionId){
	    super.setSessionId(_sessionId);
	    existing.set(14,true);
    }
    public boolean askExistsSessionId(){
	    return existing.get(14);
    }

	@Override
    public void setTargetAgentProcessUid(java.lang.String _targetAgentProcessUid){
	    super.setTargetAgentProcessUid(_targetAgentProcessUid);
	    existing.set(15,true);
    }
    public boolean askExistsTargetAgentProcessUid(){
	    return existing.get(15);
    }

	@Override
    public void setTimeoutMs(int _timeoutMs){
	    super.setTimeoutMs(_timeoutMs);
	    existing.set(16,true);
    }
    public boolean askExistsTimeoutMs(){
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
	
    
	public AmiRelayRunAmiCommandRequest000 nw(){
	    return new AmiRelayRunAmiCommandRequest000();
	}

	public AmiRelayRunAmiCommandRequest000 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiRelayRunAmiCommandRequest000 nwCast(Class[] types, Object[] args){
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
        
    converter.write(this.getArguments(),session);
        
}

	    if(existing.get(4) && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this.getCommandDefinitionId(),session);
        
}

	    if(existing.get(7) && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this.getFields(),session);
        
}

	    if(existing.get(8) && (0 & transience)==0){
    out.writeByte(5);
        
    converter.write(this.getHostIp(),session);
        
}

	    if(existing.get(16) && (0 & transience)==0){
    out.writeByte(6);
        
    out.writeByte(4);
    out.writeInt(this.getTimeoutMs());
        
}

	    if(existing.get(6) && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this.getCommandUid(),session);
        
}

	    if(existing.get(12) && (0 & transience)==0){
    out.writeByte(8);
        
    converter.write(this.getObjectTypes(),session);
        
}

	    if(existing.get(11) && (0 & transience)==0){
    out.writeByte(9);
        
    converter.write(this.getObjectIds(),session);
        
}

	    if(existing.get(13) && (0 & transience)==0){
    out.writeByte(10);
        
    out.writeByte(4);
    out.writeInt(this.getRelayConnectionId());
        
}

	    if(existing.get(5) && (0 & transience)==0){
    out.writeByte(11);
        
    out.writeByte(6);
    out.writeLong(this.getCommandId());
        
}

	    if(existing.get(0) && (0 & transience)==0){
    out.writeByte(12);
        
    converter.write(this.getAmiObjectIds(),session);
        
}

	    if(existing.get(1) && (0 & transience)==0){
    out.writeByte(13);
        
    converter.write(this.getAppId(),session);
        
}

	    if(existing.get(10) && (0 & transience)==0){
    out.writeByte(14);
        
    out.writeByte(0);
    out.writeBoolean(this.getIsManySelect());
        
}

	    if(existing.get(14) && (0 & transience)==0){
    out.writeByte(15);
        
    converter.write(this.getSessionId(),session);
        
}

	    if(existing.get(15) && (0 & transience)==0){
    out.writeByte(40);
        
    converter.write(this.getTargetAgentProcessUid(),session);
        
}

	    if(existing.get(9) && (0 & transience)==0){
    out.writeByte(41);
        
    converter.write(this.getInvokedBy(),session);
        
}

	    if(existing.get(3) && (0 & transience)==0){
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
        
                    this.setArguments((java.util.Map)converter.read(session));
        
                    continue;
  
                case 2:
        
                    this.setCommandDefinitionId((java.lang.String)converter.read(session));
        
                    continue;
  
                case 4:
        
                    this.setFields((java.util.List)converter.read(session));
        
                    continue;
  
                case 5:
        
                    this.setHostIp((java.lang.String)converter.read(session));
        
                    continue;
  
                case 6:
        
                    if((basicType=in.readByte())!=4)
                        break;
                    this.setTimeoutMs(in.readInt());
        
                    continue;
  
                case 7:
        
                    this.setCommandUid((java.lang.String)converter.read(session));
        
                    continue;
  
                case 8:
        
                    this.setObjectTypes((java.lang.String[])converter.read(session));
        
                    continue;
  
                case 9:
        
                    this.setObjectIds((java.lang.String[])converter.read(session));
        
                    continue;
  
                case 10:
        
                    if((basicType=in.readByte())!=4)
                        break;
                    this.setRelayConnectionId(in.readInt());
        
                    continue;
  
                case 11:
        
                    if((basicType=in.readByte())!=6)
                        break;
                    this.setCommandId(in.readLong());
        
                    continue;
  
                case 12:
        
                    this.setAmiObjectIds((long[])converter.read(session));
        
                    continue;
  
                case 13:
        
                    this.setAppId((java.lang.String)converter.read(session));
        
                    continue;
  
                case 14:
        
                    if((basicType=in.readByte())!=0)
                        break;
                    this.setIsManySelect(in.readBoolean());
        
                    continue;
  
                case 15:
        
                    this.setSessionId((java.lang.String)converter.read(session));
        
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