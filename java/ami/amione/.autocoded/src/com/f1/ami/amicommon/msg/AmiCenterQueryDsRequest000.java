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


public  class AmiCenterQueryDsRequest000 extends com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest00 implements Iterable<ValuedParam>,ByteArraySelfConverter{

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
    public void setAllowSqlInjection(boolean _allowSqlInjection){
	    super.setAllowSqlInjection(_allowSqlInjection);
	    existing.set(0,true);
    }
    public boolean askExistsAllowSqlInjection(){
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
    public void setDatasourceName(java.lang.String _datasourceName){
	    super.setDatasourceName(_datasourceName);
	    existing.set(2,true);
    }
    public boolean askExistsDatasourceName(){
	    return existing.get(2);
    }

	@Override
    public void setDatasourceOverrideAdapter(java.lang.String _datasourceOverrideAdapter){
	    super.setDatasourceOverrideAdapter(_datasourceOverrideAdapter);
	    existing.set(3,true);
    }
    public boolean askExistsDatasourceOverrideAdapter(){
	    return existing.get(3);
    }

	@Override
    public void setDatasourceOverrideOptions(java.lang.String _datasourceOverrideOptions){
	    super.setDatasourceOverrideOptions(_datasourceOverrideOptions);
	    existing.set(4,true);
    }
    public boolean askExistsDatasourceOverrideOptions(){
	    return existing.get(4);
    }

	@Override
    public void setDatasourceOverridePassword(com.f1.base.Password _datasourceOverridePassword){
	    super.setDatasourceOverridePassword(_datasourceOverridePassword);
	    existing.set(5,true);
    }
    public boolean askExistsDatasourceOverridePassword(){
	    return existing.get(5);
    }

	@Override
    public void setDatasourceOverridePasswordEnc(java.lang.String _datasourceOverridePasswordEnc){
	    super.setDatasourceOverridePasswordEnc(_datasourceOverridePasswordEnc);
	    existing.set(6,true);
    }
    public boolean askExistsDatasourceOverridePasswordEnc(){
	    return existing.get(6);
    }

	@Override
    public void setDatasourceOverrideRelay(java.lang.String _datasourceOverrideRelay){
	    super.setDatasourceOverrideRelay(_datasourceOverrideRelay);
	    existing.set(7,true);
    }
    public boolean askExistsDatasourceOverrideRelay(){
	    return existing.get(7);
    }

	@Override
    public void setDatasourceOverrideUrl(java.lang.String _datasourceOverrideUrl){
	    super.setDatasourceOverrideUrl(_datasourceOverrideUrl);
	    existing.set(8,true);
    }
    public boolean askExistsDatasourceOverrideUrl(){
	    return existing.get(8);
    }

	@Override
    public void setDatasourceOverrideUsername(java.lang.String _datasourceOverrideUsername){
	    super.setDatasourceOverrideUsername(_datasourceOverrideUsername);
	    existing.set(9,true);
    }
    public boolean askExistsDatasourceOverrideUsername(){
	    return existing.get(9);
    }

	@Override
    public void setDirectives(java.util.Map _directives){
	    super.setDirectives(_directives);
	    existing.set(10,true);
    }
    public boolean askExistsDirectives(){
	    return existing.get(10);
    }

	@Override
    public void setDisableLogging(boolean _disableLogging){
	    super.setDisableLogging(_disableLogging);
	    existing.set(11,true);
    }
    public boolean askExistsDisableLogging(){
	    return existing.get(11);
    }

	@Override
    public void setInvokedBy(java.lang.String _invokedBy){
	    super.setInvokedBy(_invokedBy);
	    existing.set(12,true);
    }
    public boolean askExistsInvokedBy(){
	    return existing.get(12);
    }

	@Override
    public void setIsTest(boolean _isTest){
	    super.setIsTest(_isTest);
	    existing.set(13,true);
    }
    public boolean askExistsIsTest(){
	    return existing.get(13);
    }

	@Override
    public void setLimit(int _limit){
	    super.setLimit(_limit);
	    existing.set(14,true);
    }
    public boolean askExistsLimit(){
	    return existing.get(14);
    }

	@Override
    public void setOriginType(byte _originType){
	    super.setOriginType(_originType);
	    existing.set(15,true);
    }
    public boolean askExistsOriginType(){
	    return existing.get(15);
    }

	@Override
    public void setParentProcessId(long _parentProcessId){
	    super.setParentProcessId(_parentProcessId);
	    existing.set(16,true);
    }
    public boolean askExistsParentProcessId(){
	    return existing.get(16);
    }

	@Override
    public void setParsedNode(com.f1.utils.string.Node _parsedNode){
	    super.setParsedNode(_parsedNode);
	    existing.set(17,true);
    }
    public boolean askExistsParsedNode(){
	    return existing.get(17);
    }

	@Override
    public void setPermissions(byte _permissions){
	    super.setPermissions(_permissions);
	    existing.set(18,true);
    }
    public boolean askExistsPermissions(){
	    return existing.get(18);
    }

	@Override
    public void setPreviewCount(int _previewCount){
	    super.setPreviewCount(_previewCount);
	    existing.set(19,true);
    }
    public boolean askExistsPreviewCount(){
	    return existing.get(19);
    }

	@Override
    public void setPriority(int _priority){
	    super.setPriority(_priority);
	    existing.set(20,true);
    }
    public boolean askExistsPriority(){
	    return existing.get(20);
    }

	@Override
    public void setQuery(java.lang.String _query){
	    super.setQuery(_query);
	    existing.set(21,true);
    }
    public boolean askExistsQuery(){
	    return existing.get(21);
    }

	@Override
    public void setQuerySessionId(long _querySessionId){
	    super.setQuerySessionId(_querySessionId);
	    existing.set(22,true);
    }
    public boolean askExistsQuerySessionId(){
	    return existing.get(22);
    }

	@Override
    public void setQuerySessionKeepAlive(boolean _querySessionKeepAlive){
	    super.setQuerySessionKeepAlive(_querySessionKeepAlive);
	    existing.set(23,true);
    }
    public boolean askExistsQuerySessionKeepAlive(){
	    return existing.get(23);
    }

	@Override
    public void setRequestTime(long _requestTime){
	    super.setRequestTime(_requestTime);
	    existing.set(24,true);
    }
    public boolean askExistsRequestTime(){
	    return existing.get(24);
    }

	@Override
    public void setSessionVariableTypes(java.util.Map _sessionVariableTypes){
	    super.setSessionVariableTypes(_sessionVariableTypes);
	    existing.set(25,true);
    }
    public boolean askExistsSessionVariableTypes(){
	    return existing.get(25);
    }

	@Override
    public void setSessionVariables(java.util.Map _sessionVariables){
	    super.setSessionVariables(_sessionVariables);
	    existing.set(26,true);
    }
    public boolean askExistsSessionVariables(){
	    return existing.get(26);
    }

	@Override
    public void setTablesForPreview(java.util.List _tablesForPreview){
	    super.setTablesForPreview(_tablesForPreview);
	    existing.set(27,true);
    }
    public boolean askExistsTablesForPreview(){
	    return existing.get(27);
    }

	@Override
    public void setTimeoutMs(int _timeoutMs){
	    super.setTimeoutMs(_timeoutMs);
	    existing.set(28,true);
    }
    public boolean askExistsTimeoutMs(){
	    return existing.get(28);
    }

	@Override
    public void setType(byte _type){
	    super.setType(_type);
	    existing.set(29,true);
    }
    public boolean askExistsType(){
	    return existing.get(29);
    }

	@Override
    public void setUploadValues(java.util.List _uploadValues){
	    super.setUploadValues(_uploadValues);
	    existing.set(30,true);
    }
    public boolean askExistsUploadValues(){
	    return existing.get(30);
    }

	@Override
    public void setUseConcurrency(boolean _useConcurrency){
	    super.setUseConcurrency(_useConcurrency);
	    existing.set(31,true);
    }
    public boolean askExistsUseConcurrency(){
	    return existing.get(31);
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
	
    
	public AmiCenterQueryDsRequest000 nw(){
	    return new AmiCenterQueryDsRequest000();
	}

	public AmiCenterQueryDsRequest000 nw(Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}

	public AmiCenterQueryDsRequest000 nwCast(Class[] types, Object[] args){
	    if(args==null || args.length==0)
	        return nw();
	    throw new UnsupportedOperationException("ONLY DEFAULT CONSTRUCTOR SUPPORTED");
	}
	
	



    public void write(ToByteArrayConverterSession session) throws IOException{
        ObjectToByteArrayConverter converter=session.getConverter();
        byte transience=converter.getSkipTransience();
        DataOutput out=session.getStream();//
        out.writeBoolean(true);//pids supported
        
	    if(existing.get(14) && (0 & transience)==0){
    out.writeByte(3);
        
    out.writeByte(4);
    out.writeInt(this.getLimit());
        
}

	    if(existing.get(10) && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this.getDirectives(),session);
        
}

	    if(existing.get(28) && (0 & transience)==0){
    out.writeByte(7);
        
    out.writeByte(4);
    out.writeInt(this.getTimeoutMs());
        
}

	    if(existing.get(13) && (0 & transience)==0){
    out.writeByte(8);
        
    out.writeByte(0);
    out.writeBoolean(this.getIsTest());
        
}

	    if(existing.get(21) && (0 & transience)==0){
    out.writeByte(10);
        
    converter.write(this.getQuery(),session);
        
}

	    if(existing.get(2) && (0 & transience)==0){
    out.writeByte(11);
        
    converter.write(this.getDatasourceName(),session);
        
}

	    if(existing.get(22) && (0 & transience)==0){
    out.writeByte(12);
        
    out.writeByte(6);
    out.writeLong(this.getQuerySessionId());
        
}

	    if(existing.get(23) && (0 & transience)==0){
    out.writeByte(13);
        
    out.writeByte(0);
    out.writeBoolean(this.getQuerySessionKeepAlive());
        
}

	    if(existing.get(29) && (0 & transience)==0){
    out.writeByte(14);
        
    out.writeByte(1);
    out.writeByte(this.getType());
        
}

	    if(existing.get(30) && (0 & transience)==0){
    out.writeByte(15);
        
    converter.write(this.getUploadValues(),session);
        
}

	    if(existing.get(17) && (3 & transience)==0){
    out.writeByte(16);
        
    converter.write(this.getParsedNode(),session);
        
}

	    if(existing.get(31) && (3 & transience)==0){
    out.writeByte(17);
        
    out.writeByte(0);
    out.writeBoolean(this.getUseConcurrency());
        
}

	    if(existing.get(27) && (0 & transience)==0){
    out.writeByte(18);
        
    converter.write(this.getTablesForPreview(),session);
        
}

	    if(existing.get(19) && (0 & transience)==0){
    out.writeByte(19);
        
    out.writeByte(4);
    out.writeInt(this.getPreviewCount());
        
}

	    if(existing.get(15) && (0 & transience)==0){
    out.writeByte(20);
        
    out.writeByte(1);
    out.writeByte(this.getOriginType());
        
}

	    if(existing.get(8) && (0 & transience)==0){
    out.writeByte(21);
        
    converter.write(this.getDatasourceOverrideUrl(),session);
        
}

	    if(existing.get(9) && (0 & transience)==0){
    out.writeByte(22);
        
    converter.write(this.getDatasourceOverrideUsername(),session);
        
}

	    if(existing.get(5) && (0 & transience)==0){
    out.writeByte(23);
        
    converter.write(this.getDatasourceOverridePassword(),session);
        
}

	    if(existing.get(6) && (0 & transience)==0){
    out.writeByte(24);
        
    converter.write(this.getDatasourceOverridePasswordEnc(),session);
        
}

	    if(existing.get(4) && (0 & transience)==0){
    out.writeByte(25);
        
    converter.write(this.getDatasourceOverrideOptions(),session);
        
}

	    if(existing.get(7) && (0 & transience)==0){
    out.writeByte(26);
        
    converter.write(this.getDatasourceOverrideRelay(),session);
        
}

	    if(existing.get(3) && (0 & transience)==0){
    out.writeByte(27);
        
    converter.write(this.getDatasourceOverrideAdapter(),session);
        
}

	    if(existing.get(18) && (0 & transience)==0){
    out.writeByte(28);
        
    out.writeByte(1);
    out.writeByte(this.getPermissions());
        
}

	    if(existing.get(0) && (0 & transience)==0){
    out.writeByte(29);
        
    out.writeByte(0);
    out.writeBoolean(this.getAllowSqlInjection());
        
}

	    if(existing.get(16) && (0 & transience)==0){
    out.writeByte(30);
        
    out.writeByte(6);
    out.writeLong(this.getParentProcessId());
        
}

	    if(existing.get(26) && (0 & transience)==0){
    out.writeByte(31);
        
    converter.write(this.getSessionVariables(),session);
        
}

	    if(existing.get(25) && (0 & transience)==0){
    out.writeByte(32);
        
    converter.write(this.getSessionVariableTypes(),session);
        
}

	    if(existing.get(11) && (0 & transience)==0){
    out.writeByte(33);
        
    out.writeByte(0);
    out.writeBoolean(this.getDisableLogging());
        
}

	    if(existing.get(12) && (0 & transience)==0){
    out.writeByte(51);
        
    converter.write(this.getInvokedBy(),session);
        
}

	    if(existing.get(1) && (0 & transience)==0){
    out.writeByte(52);
        
    converter.write(this.getComment(),session);
        
}

	    if(existing.get(20) && (0 & transience)==0){
    out.writeByte(53);
        
    out.writeByte(4);
    out.writeInt(this.getPriority());
        
}

	    if(existing.get(24) && (0 & transience)==0){
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
        
                case 3:
        
                    if((basicType=in.readByte())!=4)
                        break;
                    this.setLimit(in.readInt());
        
                    continue;
  
                case 4:
        
                    this.setDirectives((java.util.Map)converter.read(session));
        
                    continue;
  
                case 7:
        
                    if((basicType=in.readByte())!=4)
                        break;
                    this.setTimeoutMs(in.readInt());
        
                    continue;
  
                case 8:
        
                    if((basicType=in.readByte())!=0)
                        break;
                    this.setIsTest(in.readBoolean());
        
                    continue;
  
                case 10:
        
                    this.setQuery((java.lang.String)converter.read(session));
        
                    continue;
  
                case 11:
        
                    this.setDatasourceName((java.lang.String)converter.read(session));
        
                    continue;
  
                case 12:
        
                    if((basicType=in.readByte())!=6)
                        break;
                    this.setQuerySessionId(in.readLong());
        
                    continue;
  
                case 13:
        
                    if((basicType=in.readByte())!=0)
                        break;
                    this.setQuerySessionKeepAlive(in.readBoolean());
        
                    continue;
  
                case 14:
        
                    if((basicType=in.readByte())!=1)
                        break;
                    this.setType(in.readByte());
        
                    continue;
  
                case 15:
        
                    this.setUploadValues((java.util.List)converter.read(session));
        
                    continue;
  
                case 16:
        
                    this.setParsedNode((com.f1.utils.string.Node)converter.read(session));
        
                    continue;
  
                case 17:
        
                    if((basicType=in.readByte())!=0)
                        break;
                    this.setUseConcurrency(in.readBoolean());
        
                    continue;
  
                case 18:
        
                    this.setTablesForPreview((java.util.List)converter.read(session));
        
                    continue;
  
                case 19:
        
                    if((basicType=in.readByte())!=4)
                        break;
                    this.setPreviewCount(in.readInt());
        
                    continue;
  
                case 20:
        
                    if((basicType=in.readByte())!=1)
                        break;
                    this.setOriginType(in.readByte());
        
                    continue;
  
                case 21:
        
                    this.setDatasourceOverrideUrl((java.lang.String)converter.read(session));
        
                    continue;
  
                case 22:
        
                    this.setDatasourceOverrideUsername((java.lang.String)converter.read(session));
        
                    continue;
  
                case 23:
        
                    this.setDatasourceOverridePassword((com.f1.base.Password)converter.read(session));
        
                    continue;
  
                case 24:
        
                    this.setDatasourceOverridePasswordEnc((java.lang.String)converter.read(session));
        
                    continue;
  
                case 25:
        
                    this.setDatasourceOverrideOptions((java.lang.String)converter.read(session));
        
                    continue;
  
                case 26:
        
                    this.setDatasourceOverrideRelay((java.lang.String)converter.read(session));
        
                    continue;
  
                case 27:
        
                    this.setDatasourceOverrideAdapter((java.lang.String)converter.read(session));
        
                    continue;
  
                case 28:
        
                    if((basicType=in.readByte())!=1)
                        break;
                    this.setPermissions(in.readByte());
        
                    continue;
  
                case 29:
        
                    if((basicType=in.readByte())!=0)
                        break;
                    this.setAllowSqlInjection(in.readBoolean());
        
                    continue;
  
                case 30:
        
                    if((basicType=in.readByte())!=6)
                        break;
                    this.setParentProcessId(in.readLong());
        
                    continue;
  
                case 31:
        
                    this.setSessionVariables((java.util.Map)converter.read(session));
        
                    continue;
  
                case 32:
        
                    this.setSessionVariableTypes((java.util.Map)converter.read(session));
        
                    continue;
  
                case 33:
        
                    if((basicType=in.readByte())!=0)
                        break;
                    this.setDisableLogging(in.readBoolean());
        
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