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


public  class AmiRelaySendEmailRequest000 extends com.f1.ami.amicommon.msg.AmiRelaySendEmailRequest00 implements Iterable<ValuedParam>,ByteArraySelfConverter{

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
    public void setAttachmentDatas(java.util.List _attachmentDatas){
	    super.setAttachmentDatas(_attachmentDatas);
	    existing.set(0,true);
    }
    public boolean askExistsAttachmentDatas(){
	    return existing.get(0);
    }

	@Override
    public void setAttachmentNames(java.util.List _attachmentNames){
	    super.setAttachmentNames(_attachmentNames);
	    existing.set(1,true);
    }
    public boolean askExistsAttachmentNames(){
	    return existing.get(1);
    }

	@Override
    public void setBody(java.lang.String _body){
	    super.setBody(_body);
	    existing.set(2,true);
    }
    public boolean askExistsBody(){
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
    public void setFrom(java.lang.String _from){
	    super.setFrom(_from);
	    existing.set(4,true);
    }
    public boolean askExistsFrom(){
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
    public void setIsHtml(boolean _isHtml){
	    super.setIsHtml(_isHtml);
	    existing.set(6,true);
    }
    public boolean askExistsIsHtml(){
	    return existing.get(6);
    }

	@Override
    public void setPassword(com.f1.base.Password _password){
	    super.setPassword(_password);
	    existing.set(7,true);
    }
    public boolean askExistsPassword(){
	    return existing.get(7);
    }

	@Override
    public void setSendEmailUid(java.lang.String _sendEmailUid){
	    super.setSendEmailUid(_sendEmailUid);
	    existing.set(8,true);
    }
    public boolean askExistsSendEmailUid(){
	    return existing.get(8);
    }

	@Override
    public void setSubject(java.lang.String _subject){
	    super.setSubject(_subject);
	    existing.set(9,true);
    }
    public boolean askExistsSubject(){
	    return existing.get(9);
    }

	@Override
    public void setTargetAgentProcessUid(java.lang.String _targetAgentProcessUid){
	    super.setTargetAgentProcessUid(_targetAgentProcessUid);
	    existing.set(10,true);
    }
    public boolean askExistsTargetAgentProcessUid(){
	    return existing.get(10);
    }

	@Override
    public void setTimeoutMs(int _timeoutMs){
	    super.setTimeoutMs(_timeoutMs);
	    existing.set(11,true);
    }
    public boolean askExistsTimeoutMs(){
	    return existing.get(11);
    }

	@Override
    public void setToList(java.util.List _toList){
	    super.setToList(_toList);
	    existing.set(12,true);
    }
    public boolean askExistsToList(){
	    return existing.get(12);
    }

	@Override
    public void setUsername(java.lang.String _username){
	    super.setUsername(_username);
	    existing.set(13,true);
    }
    public boolean askExistsUsername(){
	    return existing.get(13);
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
	
    
	public AmiRelaySendEmailRequest000 nw(){
	    return new AmiRelaySendEmailRequest000();
	}

	public AmiRelaySendEmailRequest000 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiRelaySendEmailRequest000 nwCast(Class[] types, Object[] args){
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
        
    converter.write(this.getBody(),session);
        
}

	    if(existing.get(9) && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this.getSubject(),session);
        
}

	    if(existing.get(12) && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this.getToList(),session);
        
}

	    if(existing.get(4) && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this.getFrom(),session);
        
}

	    if(existing.get(6) && (0 & transience)==0){
    out.writeByte(5);
        
    out.writeByte(0);
    out.writeBoolean(this.getIsHtml());
        
}

	    if(existing.get(1) && (0 & transience)==0){
    out.writeByte(6);
        
    converter.write(this.getAttachmentNames(),session);
        
}

	    if(existing.get(0) && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this.getAttachmentDatas(),session);
        
}

	    if(existing.get(8) && (0 & transience)==0){
    out.writeByte(8);
        
    converter.write(this.getSendEmailUid(),session);
        
}

	    if(existing.get(11) && (0 & transience)==0){
    out.writeByte(9);
        
    out.writeByte(4);
    out.writeInt(this.getTimeoutMs());
        
}

	    if(existing.get(13) && (0 & transience)==0){
    out.writeByte(10);
        
    converter.write(this.getUsername(),session);
        
}

	    if(existing.get(7) && (0 & transience)==0){
    out.writeByte(11);
        
    converter.write(this.getPassword(),session);
        
}

	    if(existing.get(10) && (0 & transience)==0){
    out.writeByte(40);
        
    converter.write(this.getTargetAgentProcessUid(),session);
        
}

	    if(existing.get(5) && (0 & transience)==0){
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
        
                    this.setBody((java.lang.String)converter.read(session));
        
                    continue;
  
                case 2:
        
                    this.setSubject((java.lang.String)converter.read(session));
        
                    continue;
  
                case 3:
        
                    this.setToList((java.util.List)converter.read(session));
        
                    continue;
  
                case 4:
        
                    this.setFrom((java.lang.String)converter.read(session));
        
                    continue;
  
                case 5:
        
                    if((basicType=in.readByte())!=0)
                        break;
                    this.setIsHtml(in.readBoolean());
        
                    continue;
  
                case 6:
        
                    this.setAttachmentNames((java.util.List)converter.read(session));
        
                    continue;
  
                case 7:
        
                    this.setAttachmentDatas((java.util.List)converter.read(session));
        
                    continue;
  
                case 8:
        
                    this.setSendEmailUid((java.lang.String)converter.read(session));
        
                    continue;
  
                case 9:
        
                    if((basicType=in.readByte())!=4)
                        break;
                    this.setTimeoutMs(in.readInt());
        
                    continue;
  
                case 10:
        
                    this.setUsername((java.lang.String)converter.read(session));
        
                    continue;
  
                case 11:
        
                    this.setPassword((com.f1.base.Password)converter.read(session));
        
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