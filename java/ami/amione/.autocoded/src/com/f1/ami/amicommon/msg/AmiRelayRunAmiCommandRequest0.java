//Coded by ValuedCodeTemplate
package com.f1.ami.amicommon.msg;

import com.f1.base.Valued;
import com.f1.base.Caster;
import com.f1.utils.DetailedException;
import com.f1.base.ValuedSchema;
import com.f1.base.Acker;
import com.f1.base.ValuedHashCodeGenerator;
import com.f1.base.Ackable;
import com.f1.base.ValuedParam;
import com.f1.base.StringBuildable;
import com.f1.utils.AbstractValuedParam;
import com.f1.utils.converter.bytes.ByteArraySelfConverter;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.BasicFixPoint;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import com.f1.utils.VH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public abstract class AmiRelayRunAmiCommandRequest0 implements com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

    private int hashCode=ValuedHashCodeGenerator.next(this);
    
    @Override
    public int hashCode(){
      return this.hashCode;
    }
    
    @Override
	public com.f1.base.Message clone(){
       try{
         
	       return (com.f1.base.Message)super.clone();
	     
       } catch( Exception e){
          throw new RuntimeException("error cloning",e);
       }
    }
    
    
    

    private long[] _amiObjectIds;

    private java.lang.String _appId;

    private java.util.Map _arguments;

    private byte _centerId;

    private java.lang.String _commandDefinitionId;

    private long _commandId;

    private java.lang.String _commandUid;

    private java.util.List _fields;

    private java.lang.String _hostIp;

    private java.lang.String _invokedBy;

    private boolean _isManySelect;

    private java.lang.String[] _objectIds;

    private java.lang.String[] _objectTypes;

    private int _relayConnectionId;

    private java.lang.String _sessionId;

    private java.lang.String _targetAgentProcessUid;

    private int _timeoutMs;

    private static final String NAMES[]={ "amiObjectIds" ,"appId","arguments","centerId","commandDefinitionId","commandId","commandUid","fields","hostIp","invokedBy","isManySelect","objectIds","objectTypes","relayConnectionId","sessionId","targetAgentProcessUid","timeoutMs"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 43;
        try{
        switch(h){

                case 1:

                    if(name == "centerId" || name.equals("centerId")) {this._centerId=(java.lang.Byte)value;return;}
break;
                case 3:

                    if(name == "amiObjectIds" || name.equals("amiObjectIds")) {this._amiObjectIds=(long[])value;return;}
break;
                case 10:

                    if(name == "isManySelect" || name.equals("isManySelect")) {this._isManySelect=(java.lang.Boolean)value;return;}
break;
                case 15:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {this._timeoutMs=(java.lang.Integer)value;return;}
break;
                case 16:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return;}
break;
                case 18:

                    if(name == "commandUid" || name.equals("commandUid")) {this._commandUid=(java.lang.String)value;return;}
break;
                case 21:

                    if(name == "hostIp" || name.equals("hostIp")) {this._hostIp=(java.lang.String)value;return;}
break;
                case 22:

                    if(name == "objectIds" || name.equals("objectIds")) {this._objectIds=(java.lang.String[])value;return;}
break;
                case 23:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {this._targetAgentProcessUid=(java.lang.String)value;return;}
break;
                case 25:

                    if(name == "relayConnectionId" || name.equals("relayConnectionId")) {this._relayConnectionId=(java.lang.Integer)value;return;}
break;
                case 29:

                    if(name == "objectTypes" || name.equals("objectTypes")) {this._objectTypes=(java.lang.String[])value;return;}
break;
                case 30:

                    if(name == "sessionId" || name.equals("sessionId")) {this._sessionId=(java.lang.String)value;return;}
break;
                case 32:

                    if(name == "appId" || name.equals("appId")) {this._appId=(java.lang.String)value;return;}
break;
                case 33:

                    if(name == "commandId" || name.equals("commandId")) {this._commandId=(java.lang.Long)value;return;}
break;
                case 37:

                    if(name == "commandDefinitionId" || name.equals("commandDefinitionId")) {this._commandDefinitionId=(java.lang.String)value;return;}
break;
                case 40:

                    if(name == "arguments" || name.equals("arguments")) {this._arguments=(java.util.Map)value;return;}
break;
                case 41:

                    if(name == "fields" || name.equals("fields")) {this._fields=(java.util.List)value;return;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
        }catch(NullPointerException e){
            throw new NullPointerException("primitive value can not be null: "+name);
        }
    }


	@Override
    public boolean putNoThrow(String name, Object value){
    
           if(name==null)
               return false;
        final int h=Math.abs(name.hashCode()) % 43;
        switch(h){

                case 1:

                    if(name == "centerId" || name.equals("centerId")) {this._centerId=(java.lang.Byte)value;return true;}
break;
                case 3:

                    if(name == "amiObjectIds" || name.equals("amiObjectIds")) {this._amiObjectIds=(long[])value;return true;}
break;
                case 10:

                    if(name == "isManySelect" || name.equals("isManySelect")) {this._isManySelect=(java.lang.Boolean)value;return true;}
break;
                case 15:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {this._timeoutMs=(java.lang.Integer)value;return true;}
break;
                case 16:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return true;}
break;
                case 18:

                    if(name == "commandUid" || name.equals("commandUid")) {this._commandUid=(java.lang.String)value;return true;}
break;
                case 21:

                    if(name == "hostIp" || name.equals("hostIp")) {this._hostIp=(java.lang.String)value;return true;}
break;
                case 22:

                    if(name == "objectIds" || name.equals("objectIds")) {this._objectIds=(java.lang.String[])value;return true;}
break;
                case 23:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {this._targetAgentProcessUid=(java.lang.String)value;return true;}
break;
                case 25:

                    if(name == "relayConnectionId" || name.equals("relayConnectionId")) {this._relayConnectionId=(java.lang.Integer)value;return true;}
break;
                case 29:

                    if(name == "objectTypes" || name.equals("objectTypes")) {this._objectTypes=(java.lang.String[])value;return true;}
break;
                case 30:

                    if(name == "sessionId" || name.equals("sessionId")) {this._sessionId=(java.lang.String)value;return true;}
break;
                case 32:

                    if(name == "appId" || name.equals("appId")) {this._appId=(java.lang.String)value;return true;}
break;
                case 33:

                    if(name == "commandId" || name.equals("commandId")) {this._commandId=(java.lang.Long)value;return true;}
break;
                case 37:

                    if(name == "commandDefinitionId" || name.equals("commandDefinitionId")) {this._commandDefinitionId=(java.lang.String)value;return true;}
break;
                case 40:

                    if(name == "arguments" || name.equals("arguments")) {this._arguments=(java.util.Map)value;return true;}
break;
                case 41:

                    if(name == "fields" || name.equals("fields")) {this._fields=(java.util.List)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 43;
        switch(h){

                case 1:

		    
                    if(name == "centerId" || name.equals("centerId")) {return OH.valueOf(this._centerId);}
		    
break;
                case 3:

		    
                    if(name == "amiObjectIds" || name.equals("amiObjectIds")) {return this._amiObjectIds;}
            
break;
                case 10:

		    
                    if(name == "isManySelect" || name.equals("isManySelect")) {return OH.valueOf(this._isManySelect);}
		    
break;
                case 15:

		    
                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return OH.valueOf(this._timeoutMs);}
		    
break;
                case 16:

		    
                    if(name == "invokedBy" || name.equals("invokedBy")) {return this._invokedBy;}
            
break;
                case 18:

		    
                    if(name == "commandUid" || name.equals("commandUid")) {return this._commandUid;}
            
break;
                case 21:

		    
                    if(name == "hostIp" || name.equals("hostIp")) {return this._hostIp;}
            
break;
                case 22:

		    
                    if(name == "objectIds" || name.equals("objectIds")) {return this._objectIds;}
            
break;
                case 23:

		    
                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return this._targetAgentProcessUid;}
            
break;
                case 25:

		    
                    if(name == "relayConnectionId" || name.equals("relayConnectionId")) {return OH.valueOf(this._relayConnectionId);}
		    
break;
                case 29:

		    
                    if(name == "objectTypes" || name.equals("objectTypes")) {return this._objectTypes;}
            
break;
                case 30:

		    
                    if(name == "sessionId" || name.equals("sessionId")) {return this._sessionId;}
            
break;
                case 32:

		    
                    if(name == "appId" || name.equals("appId")) {return this._appId;}
            
break;
                case 33:

		    
                    if(name == "commandId" || name.equals("commandId")) {return OH.valueOf(this._commandId);}
		    
break;
                case 37:

		    
                    if(name == "commandDefinitionId" || name.equals("commandDefinitionId")) {return this._commandDefinitionId;}
            
break;
                case 40:

		    
                    if(name == "arguments" || name.equals("arguments")) {return this._arguments;}
            
break;
                case 41:

		    
                    if(name == "fields" || name.equals("fields")) {return this._fields;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 43;
        switch(h){

                case 1:

                    if(name == "centerId" || name.equals("centerId")) {return byte.class;}
break;
                case 3:

                    if(name == "amiObjectIds" || name.equals("amiObjectIds")) {return long[].class;}
break;
                case 10:

                    if(name == "isManySelect" || name.equals("isManySelect")) {return boolean.class;}
break;
                case 15:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return int.class;}
break;
                case 16:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return java.lang.String.class;}
break;
                case 18:

                    if(name == "commandUid" || name.equals("commandUid")) {return java.lang.String.class;}
break;
                case 21:

                    if(name == "hostIp" || name.equals("hostIp")) {return java.lang.String.class;}
break;
                case 22:

                    if(name == "objectIds" || name.equals("objectIds")) {return java.lang.String[].class;}
break;
                case 23:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return java.lang.String.class;}
break;
                case 25:

                    if(name == "relayConnectionId" || name.equals("relayConnectionId")) {return int.class;}
break;
                case 29:

                    if(name == "objectTypes" || name.equals("objectTypes")) {return java.lang.String[].class;}
break;
                case 30:

                    if(name == "sessionId" || name.equals("sessionId")) {return java.lang.String.class;}
break;
                case 32:

                    if(name == "appId" || name.equals("appId")) {return java.lang.String.class;}
break;
                case 33:

                    if(name == "commandId" || name.equals("commandId")) {return long.class;}
break;
                case 37:

                    if(name == "commandDefinitionId" || name.equals("commandDefinitionId")) {return java.lang.String.class;}
break;
                case 40:

                    if(name == "arguments" || name.equals("arguments")) {return java.util.Map.class;}
break;
                case 41:

                    if(name == "fields" || name.equals("fields")) {return java.util.List.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 43;
        switch(h){

                case 1:

                    if(name == "centerId" || name.equals("centerId")) {return VALUED_PARAM_centerId;}
break;
                case 3:

                    if(name == "amiObjectIds" || name.equals("amiObjectIds")) {return VALUED_PARAM_amiObjectIds;}
break;
                case 10:

                    if(name == "isManySelect" || name.equals("isManySelect")) {return VALUED_PARAM_isManySelect;}
break;
                case 15:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return VALUED_PARAM_timeoutMs;}
break;
                case 16:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return VALUED_PARAM_invokedBy;}
break;
                case 18:

                    if(name == "commandUid" || name.equals("commandUid")) {return VALUED_PARAM_commandUid;}
break;
                case 21:

                    if(name == "hostIp" || name.equals("hostIp")) {return VALUED_PARAM_hostIp;}
break;
                case 22:

                    if(name == "objectIds" || name.equals("objectIds")) {return VALUED_PARAM_objectIds;}
break;
                case 23:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return VALUED_PARAM_targetAgentProcessUid;}
break;
                case 25:

                    if(name == "relayConnectionId" || name.equals("relayConnectionId")) {return VALUED_PARAM_relayConnectionId;}
break;
                case 29:

                    if(name == "objectTypes" || name.equals("objectTypes")) {return VALUED_PARAM_objectTypes;}
break;
                case 30:

                    if(name == "sessionId" || name.equals("sessionId")) {return VALUED_PARAM_sessionId;}
break;
                case 32:

                    if(name == "appId" || name.equals("appId")) {return VALUED_PARAM_appId;}
break;
                case 33:

                    if(name == "commandId" || name.equals("commandId")) {return VALUED_PARAM_commandId;}
break;
                case 37:

                    if(name == "commandDefinitionId" || name.equals("commandDefinitionId")) {return VALUED_PARAM_commandDefinitionId;}
break;
                case 40:

                    if(name == "arguments" || name.equals("arguments")) {return VALUED_PARAM_arguments;}
break;
                case 41:

                    if(name == "fields" || name.equals("fields")) {return VALUED_PARAM_fields;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 43;
        switch(h){

                case 1:

                    if(name == "centerId" || name.equals("centerId")) {return 3;}
break;
                case 3:

                    if(name == "amiObjectIds" || name.equals("amiObjectIds")) {return 0;}
break;
                case 10:

                    if(name == "isManySelect" || name.equals("isManySelect")) {return 10;}
break;
                case 15:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return 16;}
break;
                case 16:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 9;}
break;
                case 18:

                    if(name == "commandUid" || name.equals("commandUid")) {return 6;}
break;
                case 21:

                    if(name == "hostIp" || name.equals("hostIp")) {return 8;}
break;
                case 22:

                    if(name == "objectIds" || name.equals("objectIds")) {return 11;}
break;
                case 23:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return 15;}
break;
                case 25:

                    if(name == "relayConnectionId" || name.equals("relayConnectionId")) {return 13;}
break;
                case 29:

                    if(name == "objectTypes" || name.equals("objectTypes")) {return 12;}
break;
                case 30:

                    if(name == "sessionId" || name.equals("sessionId")) {return 14;}
break;
                case 32:

                    if(name == "appId" || name.equals("appId")) {return 1;}
break;
                case 33:

                    if(name == "commandId" || name.equals("commandId")) {return 5;}
break;
                case 37:

                    if(name == "commandDefinitionId" || name.equals("commandDefinitionId")) {return 4;}
break;
                case 40:

                    if(name == "arguments" || name.equals("arguments")) {return 2;}
break;
                case 41:

                    if(name == "fields" || name.equals("fields")) {return 7;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public String[] askParams(){
        return NAMES;
    }

	@Override
    public int askParamsCount(){
        return 17;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiRelayRunAmiCommandRequest0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 43;
        switch(h){

                case 1:

                    if(name == "centerId" || name.equals("centerId")) {return true;}
break;
                case 3:

                    if(name == "amiObjectIds" || name.equals("amiObjectIds")) {return true;}
break;
                case 10:

                    if(name == "isManySelect" || name.equals("isManySelect")) {return true;}
break;
                case 15:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return true;}
break;
                case 16:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return true;}
break;
                case 18:

                    if(name == "commandUid" || name.equals("commandUid")) {return true;}
break;
                case 21:

                    if(name == "hostIp" || name.equals("hostIp")) {return true;}
break;
                case 22:

                    if(name == "objectIds" || name.equals("objectIds")) {return true;}
break;
                case 23:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return true;}
break;
                case 25:

                    if(name == "relayConnectionId" || name.equals("relayConnectionId")) {return true;}
break;
                case 29:

                    if(name == "objectTypes" || name.equals("objectTypes")) {return true;}
break;
                case 30:

                    if(name == "sessionId" || name.equals("sessionId")) {return true;}
break;
                case 32:

                    if(name == "appId" || name.equals("appId")) {return true;}
break;
                case 33:

                    if(name == "commandId" || name.equals("commandId")) {return true;}
break;
                case 37:

                    if(name == "commandDefinitionId" || name.equals("commandDefinitionId")) {return true;}
break;
                case 40:

                    if(name == "arguments" || name.equals("arguments")) {return true;}
break;
                case 41:

                    if(name == "fields" || name.equals("fields")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 43;
        switch(h){

                case 1:

                    if(name == "centerId" || name.equals("centerId")) {return 1;}
break;
                case 3:

                    if(name == "amiObjectIds" || name.equals("amiObjectIds")) {return 106;}
break;
                case 10:

                    if(name == "isManySelect" || name.equals("isManySelect")) {return 0;}
break;
                case 15:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return 4;}
break;
                case 16:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 20;}
break;
                case 18:

                    if(name == "commandUid" || name.equals("commandUid")) {return 20;}
break;
                case 21:

                    if(name == "hostIp" || name.equals("hostIp")) {return 20;}
break;
                case 22:

                    if(name == "objectIds" || name.equals("objectIds")) {return 56;}
break;
                case 23:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return 20;}
break;
                case 25:

                    if(name == "relayConnectionId" || name.equals("relayConnectionId")) {return 4;}
break;
                case 29:

                    if(name == "objectTypes" || name.equals("objectTypes")) {return 56;}
break;
                case 30:

                    if(name == "sessionId" || name.equals("sessionId")) {return 20;}
break;
                case 32:

                    if(name == "appId" || name.equals("appId")) {return 20;}
break;
                case 33:

                    if(name == "commandId" || name.equals("commandId")) {return 6;}
break;
                case 37:

                    if(name == "commandDefinitionId" || name.equals("commandDefinitionId")) {return 20;}
break;
                case 40:

                    if(name == "arguments" || name.equals("arguments")) {return 23;}
break;
                case 41:

                    if(name == "fields" || name.equals("fields")) {return 21;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _amiObjectIds;

        case 1:return _appId;

        case 2:return _arguments;

        case 3:return _centerId;

        case 4:return _commandDefinitionId;

        case 5:return _commandId;

        case 6:return _commandUid;

        case 7:return _fields;

        case 8:return _hostIp;

        case 9:return _invokedBy;

        case 10:return _isManySelect;

        case 11:return _objectIds;

        case 12:return _objectTypes;

        case 13:return _relayConnectionId;

        case 14:return _sessionId;

        case 15:return _targetAgentProcessUid;

        case 16:return _timeoutMs;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 17);
    }

    public long[] getAmiObjectIds(){
        return this._amiObjectIds;
    }
    public void setAmiObjectIds(long[] _amiObjectIds){
    
        this._amiObjectIds=_amiObjectIds;
    }

    public java.lang.String getAppId(){
        return this._appId;
    }
    public void setAppId(java.lang.String _appId){
    
        this._appId=_appId;
    }

    public java.util.Map getArguments(){
        return this._arguments;
    }
    public void setArguments(java.util.Map _arguments){
    
        this._arguments=_arguments;
    }

    public byte getCenterId(){
        return this._centerId;
    }
    public void setCenterId(byte _centerId){
    
        this._centerId=_centerId;
    }

    public java.lang.String getCommandDefinitionId(){
        return this._commandDefinitionId;
    }
    public void setCommandDefinitionId(java.lang.String _commandDefinitionId){
    
        this._commandDefinitionId=_commandDefinitionId;
    }

    public long getCommandId(){
        return this._commandId;
    }
    public void setCommandId(long _commandId){
    
        this._commandId=_commandId;
    }

    public java.lang.String getCommandUid(){
        return this._commandUid;
    }
    public void setCommandUid(java.lang.String _commandUid){
    
        this._commandUid=_commandUid;
    }

    public java.util.List getFields(){
        return this._fields;
    }
    public void setFields(java.util.List _fields){
    
        this._fields=_fields;
    }

    public java.lang.String getHostIp(){
        return this._hostIp;
    }
    public void setHostIp(java.lang.String _hostIp){
    
        this._hostIp=_hostIp;
    }

    public java.lang.String getInvokedBy(){
        return this._invokedBy;
    }
    public void setInvokedBy(java.lang.String _invokedBy){
    
        this._invokedBy=_invokedBy;
    }

    public boolean getIsManySelect(){
        return this._isManySelect;
    }
    public void setIsManySelect(boolean _isManySelect){
    
        this._isManySelect=_isManySelect;
    }

    public java.lang.String[] getObjectIds(){
        return this._objectIds;
    }
    public void setObjectIds(java.lang.String[] _objectIds){
    
        this._objectIds=_objectIds;
    }

    public java.lang.String[] getObjectTypes(){
        return this._objectTypes;
    }
    public void setObjectTypes(java.lang.String[] _objectTypes){
    
        this._objectTypes=_objectTypes;
    }

    public int getRelayConnectionId(){
        return this._relayConnectionId;
    }
    public void setRelayConnectionId(int _relayConnectionId){
    
        this._relayConnectionId=_relayConnectionId;
    }

    public java.lang.String getSessionId(){
        return this._sessionId;
    }
    public void setSessionId(java.lang.String _sessionId){
    
        this._sessionId=_sessionId;
    }

    public java.lang.String getTargetAgentProcessUid(){
        return this._targetAgentProcessUid;
    }
    public void setTargetAgentProcessUid(java.lang.String _targetAgentProcessUid){
    
        this._targetAgentProcessUid=_targetAgentProcessUid;
    }

    public int getTimeoutMs(){
        return this._timeoutMs;
    }
    public void setTimeoutMs(int _timeoutMs){
    
        this._timeoutMs=_timeoutMs;
    }





  
    private static final class VALUED_PARAM_CLASS_amiObjectIds extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 106;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [J}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [J}");
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return false;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || false;
	    }
	    @Override
	    public boolean isImmutable() {
		    return false;
	    }
    
	    @Override
	    public int askPosition() {
		    return 0;
	    }
    
	    @Override
	    public byte getPid() {
            return 12;
	    }
    
	    @Override
	    public String getName() {
            return "amiObjectIds";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (long[])((AmiRelayRunAmiCommandRequest0)valued).getAmiObjectIds();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setAmiObjectIds((long[])value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setAmiObjectIds(source.getAmiObjectIds());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getAmiObjectIds(),source.getAmiObjectIds());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return long[].class;
	    }
	    private static final Caster CASTER=OH.getCaster(long[].class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAmiObjectIds());
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAmiObjectIds());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long[] amiObjectIds";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setAmiObjectIds(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_amiObjectIds=new VALUED_PARAM_CLASS_amiObjectIds();
  

  
    private static final class VALUED_PARAM_CLASS_appId extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return false;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || false;
	    }
	    @Override
	    public boolean isImmutable() {
		    return true;
	    }
    
	    @Override
	    public int askPosition() {
		    return 1;
	    }
    
	    @Override
	    public byte getPid() {
            return 13;
	    }
    
	    @Override
	    public String getName() {
            return "appId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunAmiCommandRequest0)valued).getAppId();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setAppId((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setAppId(source.getAppId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getAppId(),source.getAppId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.String.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.String.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getAppId(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getAppId(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String appId";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setAppId(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_appId=new VALUED_PARAM_CLASS_appId();
  

  
    private static final class VALUED_PARAM_CLASS_arguments extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 23;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Map}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Map}");
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return false;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || false;
	    }
	    @Override
	    public boolean isImmutable() {
		    return false;
	    }
    
	    @Override
	    public int askPosition() {
		    return 2;
	    }
    
	    @Override
	    public byte getPid() {
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "arguments";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (java.util.Map)((AmiRelayRunAmiCommandRequest0)valued).getArguments();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setArguments((java.util.Map)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setArguments(source.getArguments());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getArguments(),source.getArguments());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.util.Map.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.util.Map.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getArguments());
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getArguments());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.Map arguments";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setArguments(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_arguments=new VALUED_PARAM_CLASS_arguments();
  

  
    private static final class VALUED_PARAM_CLASS_centerId extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getCenterId());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setCenterId(stream.readByte());
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return true;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || true;
	    }
	    @Override
	    public boolean isImmutable() {
		    return true;
	    }
    
	    @Override
	    public int askPosition() {
		    return 3;
	    }
    
	    @Override
	    public byte getPid() {
            return 42;
	    }
    
	    @Override
	    public String getName() {
            return "centerId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (byte)((AmiRelayRunAmiCommandRequest0)valued).getCenterId();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setCenterId((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setCenterId(source.getCenterId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getCenterId(),source.getCenterId());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiRelayRunAmiCommandRequest0 valued) {
		    return valued.getCenterId();
	    }
    
	    @Override
	    public void setByte(AmiRelayRunAmiCommandRequest0 valued, byte value) {
		    valued.setCenterId(value);
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return byte.class;
	    }
	    private static final Caster CASTER=OH.getCaster(byte.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getCenterId());
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getCenterId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte centerId";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setCenterId((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_centerId=new VALUED_PARAM_CLASS_centerId();
  

  
    private static final class VALUED_PARAM_CLASS_commandDefinitionId extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return false;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || false;
	    }
	    @Override
	    public boolean isImmutable() {
		    return true;
	    }
    
	    @Override
	    public int askPosition() {
		    return 4;
	    }
    
	    @Override
	    public byte getPid() {
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "commandDefinitionId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunAmiCommandRequest0)valued).getCommandDefinitionId();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setCommandDefinitionId((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setCommandDefinitionId(source.getCommandDefinitionId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getCommandDefinitionId(),source.getCommandDefinitionId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.String.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.String.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getCommandDefinitionId(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getCommandDefinitionId(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String commandDefinitionId";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setCommandDefinitionId(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_commandDefinitionId=new VALUED_PARAM_CLASS_commandDefinitionId();
  

  
    private static final class VALUED_PARAM_CLASS_commandId extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getCommandId());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setCommandId(stream.readLong());
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return true;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || true;
	    }
	    @Override
	    public boolean isImmutable() {
		    return true;
	    }
    
	    @Override
	    public int askPosition() {
		    return 5;
	    }
    
	    @Override
	    public byte getPid() {
            return 11;
	    }
    
	    @Override
	    public String getName() {
            return "commandId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (long)((AmiRelayRunAmiCommandRequest0)valued).getCommandId();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setCommandId((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setCommandId(source.getCommandId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getCommandId(),source.getCommandId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiRelayRunAmiCommandRequest0 valued) {
		    return valued.getCommandId();
	    }
    
	    @Override
	    public void setLong(AmiRelayRunAmiCommandRequest0 valued, long value) {
		    valued.setCommandId(value);
	    }
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return long.class;
	    }
	    private static final Caster CASTER=OH.getCaster(long.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getCommandId());
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getCommandId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long commandId";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setCommandId(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_commandId=new VALUED_PARAM_CLASS_commandId();
  

  
    private static final class VALUED_PARAM_CLASS_commandUid extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return false;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || false;
	    }
	    @Override
	    public boolean isImmutable() {
		    return true;
	    }
    
	    @Override
	    public int askPosition() {
		    return 6;
	    }
    
	    @Override
	    public byte getPid() {
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "commandUid";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunAmiCommandRequest0)valued).getCommandUid();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setCommandUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setCommandUid(source.getCommandUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getCommandUid(),source.getCommandUid());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.String.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.String.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getCommandUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getCommandUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String commandUid";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setCommandUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_commandUid=new VALUED_PARAM_CLASS_commandUid();
  

  
    private static final class VALUED_PARAM_CLASS_fields extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return false;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || false;
	    }
	    @Override
	    public boolean isImmutable() {
		    return false;
	    }
    
	    @Override
	    public int askPosition() {
		    return 7;
	    }
    
	    @Override
	    public byte getPid() {
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "fields";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (java.util.List)((AmiRelayRunAmiCommandRequest0)valued).getFields();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setFields((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setFields(source.getFields());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getFields(),source.getFields());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.util.List.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.util.List.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getFields());
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getFields());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List fields";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setFields(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_fields=new VALUED_PARAM_CLASS_fields();
  

  
    private static final class VALUED_PARAM_CLASS_hostIp extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return false;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || false;
	    }
	    @Override
	    public boolean isImmutable() {
		    return true;
	    }
    
	    @Override
	    public int askPosition() {
		    return 8;
	    }
    
	    @Override
	    public byte getPid() {
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "hostIp";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunAmiCommandRequest0)valued).getHostIp();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setHostIp((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setHostIp(source.getHostIp());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getHostIp(),source.getHostIp());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.String.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.String.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getHostIp(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getHostIp(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String hostIp";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setHostIp(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_hostIp=new VALUED_PARAM_CLASS_hostIp();
  

  
    private static final class VALUED_PARAM_CLASS_invokedBy extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return false;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || false;
	    }
	    @Override
	    public boolean isImmutable() {
		    return true;
	    }
    
	    @Override
	    public int askPosition() {
		    return 9;
	    }
    
	    @Override
	    public byte getPid() {
            return 41;
	    }
    
	    @Override
	    public String getName() {
            return "invokedBy";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunAmiCommandRequest0)valued).getInvokedBy();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setInvokedBy((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setInvokedBy(source.getInvokedBy());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getInvokedBy(),source.getInvokedBy());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.String.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.String.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String invokedBy";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setInvokedBy(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_invokedBy=new VALUED_PARAM_CLASS_invokedBy();
  

  
    private static final class VALUED_PARAM_CLASS_isManySelect extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getIsManySelect());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setIsManySelect(stream.readBoolean());
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return true;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || true;
	    }
	    @Override
	    public boolean isImmutable() {
		    return true;
	    }
    
	    @Override
	    public int askPosition() {
		    return 10;
	    }
    
	    @Override
	    public byte getPid() {
            return 14;
	    }
    
	    @Override
	    public String getName() {
            return "isManySelect";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (boolean)((AmiRelayRunAmiCommandRequest0)valued).getIsManySelect();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setIsManySelect((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setIsManySelect(source.getIsManySelect());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getIsManySelect(),source.getIsManySelect());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiRelayRunAmiCommandRequest0 valued) {
		    return valued.getIsManySelect();
	    }
    
	    @Override
	    public void setBoolean(AmiRelayRunAmiCommandRequest0 valued, boolean value) {
		    valued.setIsManySelect(value);
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return boolean.class;
	    }
	    private static final Caster CASTER=OH.getCaster(boolean.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getIsManySelect());
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getIsManySelect());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean isManySelect";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setIsManySelect(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_isManySelect=new VALUED_PARAM_CLASS_isManySelect();
  

  
    private static final class VALUED_PARAM_CLASS_objectIds extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 56;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [Ljava.lang.String;}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [Ljava.lang.String;}");
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return false;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || false;
	    }
	    @Override
	    public boolean isImmutable() {
		    return false;
	    }
    
	    @Override
	    public int askPosition() {
		    return 11;
	    }
    
	    @Override
	    public byte getPid() {
            return 9;
	    }
    
	    @Override
	    public String getName() {
            return "objectIds";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (java.lang.String[])((AmiRelayRunAmiCommandRequest0)valued).getObjectIds();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setObjectIds((java.lang.String[])value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setObjectIds(source.getObjectIds());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getObjectIds(),source.getObjectIds());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.String[].class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.String[].class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getObjectIds());
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getObjectIds());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String[] objectIds";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setObjectIds(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_objectIds=new VALUED_PARAM_CLASS_objectIds();
  

  
    private static final class VALUED_PARAM_CLASS_objectTypes extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 56;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [Ljava.lang.String;}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [Ljava.lang.String;}");
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return false;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || false;
	    }
	    @Override
	    public boolean isImmutable() {
		    return false;
	    }
    
	    @Override
	    public int askPosition() {
		    return 12;
	    }
    
	    @Override
	    public byte getPid() {
            return 8;
	    }
    
	    @Override
	    public String getName() {
            return "objectTypes";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (java.lang.String[])((AmiRelayRunAmiCommandRequest0)valued).getObjectTypes();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setObjectTypes((java.lang.String[])value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setObjectTypes(source.getObjectTypes());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getObjectTypes(),source.getObjectTypes());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.String[].class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.String[].class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getObjectTypes());
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getObjectTypes());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String[] objectTypes";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setObjectTypes(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_objectTypes=new VALUED_PARAM_CLASS_objectTypes();
  

  
    private static final class VALUED_PARAM_CLASS_relayConnectionId extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getRelayConnectionId());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setRelayConnectionId(stream.readInt());
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return true;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || true;
	    }
	    @Override
	    public boolean isImmutable() {
		    return true;
	    }
    
	    @Override
	    public int askPosition() {
		    return 13;
	    }
    
	    @Override
	    public byte getPid() {
            return 10;
	    }
    
	    @Override
	    public String getName() {
            return "relayConnectionId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (int)((AmiRelayRunAmiCommandRequest0)valued).getRelayConnectionId();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setRelayConnectionId((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setRelayConnectionId(source.getRelayConnectionId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getRelayConnectionId(),source.getRelayConnectionId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiRelayRunAmiCommandRequest0 valued) {
		    return valued.getRelayConnectionId();
	    }
    
	    @Override
	    public void setInt(AmiRelayRunAmiCommandRequest0 valued, int value) {
		    valued.setRelayConnectionId(value);
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return int.class;
	    }
	    private static final Caster CASTER=OH.getCaster(int.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getRelayConnectionId());
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getRelayConnectionId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int relayConnectionId";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setRelayConnectionId(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_relayConnectionId=new VALUED_PARAM_CLASS_relayConnectionId();
  

  
    private static final class VALUED_PARAM_CLASS_sessionId extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return false;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || false;
	    }
	    @Override
	    public boolean isImmutable() {
		    return true;
	    }
    
	    @Override
	    public int askPosition() {
		    return 14;
	    }
    
	    @Override
	    public byte getPid() {
            return 15;
	    }
    
	    @Override
	    public String getName() {
            return "sessionId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunAmiCommandRequest0)valued).getSessionId();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setSessionId((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setSessionId(source.getSessionId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getSessionId(),source.getSessionId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.String.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.String.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getSessionId(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getSessionId(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String sessionId";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setSessionId(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_sessionId=new VALUED_PARAM_CLASS_sessionId();
  

  
    private static final class VALUED_PARAM_CLASS_targetAgentProcessUid extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return false;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || false;
	    }
	    @Override
	    public boolean isImmutable() {
		    return true;
	    }
    
	    @Override
	    public int askPosition() {
		    return 15;
	    }
    
	    @Override
	    public byte getPid() {
            return 40;
	    }
    
	    @Override
	    public String getName() {
            return "targetAgentProcessUid";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunAmiCommandRequest0)valued).getTargetAgentProcessUid();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setTargetAgentProcessUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setTargetAgentProcessUid(source.getTargetAgentProcessUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getTargetAgentProcessUid(),source.getTargetAgentProcessUid());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.String.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.String.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getTargetAgentProcessUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getTargetAgentProcessUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String targetAgentProcessUid";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setTargetAgentProcessUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_targetAgentProcessUid=new VALUED_PARAM_CLASS_targetAgentProcessUid();
  

  
    private static final class VALUED_PARAM_CLASS_timeoutMs extends AbstractValuedParam<AmiRelayRunAmiCommandRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getTimeoutMs());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setTimeoutMs(stream.readInt());
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return true;
	    }
    
	    @Override
	    public boolean isValued() {
		    return false;
	    }
    
	    @Override
	    public byte getTransience() {
		    return 0;
	    }
    
	    @Override
	    public boolean isBoxed() {
		    return false;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return false || true;
	    }
	    @Override
	    public boolean isImmutable() {
		    return true;
	    }
    
	    @Override
	    public int askPosition() {
		    return 16;
	    }
    
	    @Override
	    public byte getPid() {
            return 6;
	    }
    
	    @Override
	    public String getName() {
            return "timeoutMs";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandRequest0 valued) {
		    return (int)((AmiRelayRunAmiCommandRequest0)valued).getTimeoutMs();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandRequest0 valued, Object value) {
		    valued.setTimeoutMs((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
		    dest.setTimeoutMs(source.getTimeoutMs());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandRequest0 source, AmiRelayRunAmiCommandRequest0 dest) {
	        return OH.eq(dest.getTimeoutMs(),source.getTimeoutMs());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiRelayRunAmiCommandRequest0 valued) {
		    return valued.getTimeoutMs();
	    }
    
	    @Override
	    public void setInt(AmiRelayRunAmiCommandRequest0 valued, int value) {
		    valued.setTimeoutMs(value);
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return int.class;
	    }
	    private static final Caster CASTER=OH.getCaster(int.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTimeoutMs());
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTimeoutMs());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int timeoutMs";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandRequest0 valued){
	       valued.setTimeoutMs(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_timeoutMs=new VALUED_PARAM_CLASS_timeoutMs();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_amiObjectIds, VALUED_PARAM_appId, VALUED_PARAM_arguments, VALUED_PARAM_centerId, VALUED_PARAM_commandDefinitionId, VALUED_PARAM_commandId, VALUED_PARAM_commandUid, VALUED_PARAM_fields, VALUED_PARAM_hostIp, VALUED_PARAM_invokedBy, VALUED_PARAM_isManySelect, VALUED_PARAM_objectIds, VALUED_PARAM_objectTypes, VALUED_PARAM_relayConnectionId, VALUED_PARAM_sessionId, VALUED_PARAM_targetAgentProcessUid, VALUED_PARAM_timeoutMs, };



    private static final byte PIDS[]={ 12 ,13,1,42,2,11,7,4,5,41,14,9,8,10,15,40,6};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 12: return VALUED_PARAM_amiObjectIds;
             case 13: return VALUED_PARAM_appId;
             case 1: return VALUED_PARAM_arguments;
             case 42: return VALUED_PARAM_centerId;
             case 2: return VALUED_PARAM_commandDefinitionId;
             case 11: return VALUED_PARAM_commandId;
             case 7: return VALUED_PARAM_commandUid;
             case 4: return VALUED_PARAM_fields;
             case 5: return VALUED_PARAM_hostIp;
             case 41: return VALUED_PARAM_invokedBy;
             case 14: return VALUED_PARAM_isManySelect;
             case 9: return VALUED_PARAM_objectIds;
             case 8: return VALUED_PARAM_objectTypes;
             case 10: return VALUED_PARAM_relayConnectionId;
             case 15: return VALUED_PARAM_sessionId;
             case 40: return VALUED_PARAM_targetAgentProcessUid;
             case 6: return VALUED_PARAM_timeoutMs;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 12: return true;
             case 13: return true;
             case 1: return true;
             case 42: return true;
             case 2: return true;
             case 11: return true;
             case 7: return true;
             case 4: return true;
             case 5: return true;
             case 41: return true;
             case 14: return true;
             case 9: return true;
             case 8: return true;
             case 10: return true;
             case 15: return true;
             case 40: return true;
             case 6: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 12: return "amiObjectIds";
             case 13: return "appId";
             case 1: return "arguments";
             case 42: return "centerId";
             case 2: return "commandDefinitionId";
             case 11: return "commandId";
             case 7: return "commandUid";
             case 4: return "fields";
             case 5: return "hostIp";
             case 41: return "invokedBy";
             case 14: return "isManySelect";
             case 9: return "objectIds";
             case 8: return "objectTypes";
             case 10: return "relayConnectionId";
             case 15: return "sessionId";
             case 40: return "targetAgentProcessUid";
             case 6: return "timeoutMs";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 12: return 0;
             case 13: return 1;
             case 1: return 2;
             case 42: return 3;
             case 2: return 4;
             case 11: return 5;
             case 7: return 6;
             case 4: return 7;
             case 5: return 8;
             case 41: return 9;
             case 14: return 10;
             case 9: return 11;
             case 8: return 12;
             case 10: return 13;
             case 15: return 14;
             case 40: return 15;
             case 6: return 16;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="amiObjectIds") return 12;
             if(name=="appId") return 13;
             if(name=="arguments") return 1;
             if(name=="centerId") return 42;
             if(name=="commandDefinitionId") return 2;
             if(name=="commandId") return 11;
             if(name=="commandUid") return 7;
             if(name=="fields") return 4;
             if(name=="hostIp") return 5;
             if(name=="invokedBy") return 41;
             if(name=="isManySelect") return 14;
             if(name=="objectIds") return 9;
             if(name=="objectTypes") return 8;
             if(name=="relayConnectionId") return 10;
             if(name=="sessionId") return 15;
             if(name=="targetAgentProcessUid") return 40;
             if(name=="timeoutMs") return 6;
            
             if("amiObjectIds".equals(name)) return 12;
             if("appId".equals(name)) return 13;
             if("arguments".equals(name)) return 1;
             if("centerId".equals(name)) return 42;
             if("commandDefinitionId".equals(name)) return 2;
             if("commandId".equals(name)) return 11;
             if("commandUid".equals(name)) return 7;
             if("fields".equals(name)) return 4;
             if("hostIp".equals(name)) return 5;
             if("invokedBy".equals(name)) return 41;
             if("isManySelect".equals(name)) return 14;
             if("objectIds".equals(name)) return 9;
             if("objectTypes".equals(name)) return 8;
             if("relayConnectionId".equals(name)) return 10;
             if("sessionId".equals(name)) return 15;
             if("targetAgentProcessUid".equals(name)) return 40;
             if("timeoutMs".equals(name)) return 6;
            throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public boolean askSupportsPids(){
        return true;
    }
    
    public byte[] askPids(){
    return PIDS;
    }

    public Object ask(byte pid){
        switch(pid){
             case 12: return  this._amiObjectIds; 
             case 13: return  this._appId; 
             case 1: return  this._arguments; 
             case 42: return  OH.valueOf(this._centerId); 
             case 2: return  this._commandDefinitionId; 
             case 11: return  OH.valueOf(this._commandId); 
             case 7: return  this._commandUid; 
             case 4: return  this._fields; 
             case 5: return  this._hostIp; 
             case 41: return  this._invokedBy; 
             case 14: return  OH.valueOf(this._isManySelect); 
             case 9: return  this._objectIds; 
             case 8: return  this._objectTypes; 
             case 10: return  OH.valueOf(this._relayConnectionId); 
             case 15: return  this._sessionId; 
             case 40: return  this._targetAgentProcessUid; 
             case 6: return  OH.valueOf(this._timeoutMs); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 12: return long[].class;
             case 13: return java.lang.String.class;
             case 1: return java.util.Map.class;
             case 42: return byte.class;
             case 2: return java.lang.String.class;
             case 11: return long.class;
             case 7: return java.lang.String.class;
             case 4: return java.util.List.class;
             case 5: return java.lang.String.class;
             case 41: return java.lang.String.class;
             case 14: return boolean.class;
             case 9: return java.lang.String[].class;
             case 8: return java.lang.String[].class;
             case 10: return int.class;
             case 15: return java.lang.String.class;
             case 40: return java.lang.String.class;
             case 6: return int.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 12: return 106;
             case 13: return 20;
             case 1: return 23;
             case 42: return 1;
             case 2: return 20;
             case 11: return 6;
             case 7: return 20;
             case 4: return 21;
             case 5: return 20;
             case 41: return 20;
             case 14: return 0;
             case 9: return 56;
             case 8: return 56;
             case 10: return 4;
             case 15: return 20;
             case 40: return 20;
             case 6: return 4;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 12: this._amiObjectIds=(long[])value;return;
             case 13: this._appId=(java.lang.String)value;return;
             case 1: this._arguments=(java.util.Map)value;return;
             case 42: this._centerId=(java.lang.Byte)value;return;
             case 2: this._commandDefinitionId=(java.lang.String)value;return;
             case 11: this._commandId=(java.lang.Long)value;return;
             case 7: this._commandUid=(java.lang.String)value;return;
             case 4: this._fields=(java.util.List)value;return;
             case 5: this._hostIp=(java.lang.String)value;return;
             case 41: this._invokedBy=(java.lang.String)value;return;
             case 14: this._isManySelect=(java.lang.Boolean)value;return;
             case 9: this._objectIds=(java.lang.String[])value;return;
             case 8: this._objectTypes=(java.lang.String[])value;return;
             case 10: this._relayConnectionId=(java.lang.Integer)value;return;
             case 15: this._sessionId=(java.lang.String)value;return;
             case 40: this._targetAgentProcessUid=(java.lang.String)value;return;
             case 6: this._timeoutMs=(java.lang.Integer)value;return;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
        }catch(NullPointerException e){
            throw new NullPointerException("primitive value can not be null: "+askParam(pid));
        }catch(RuntimeException e){
            throw new RuntimeException("error setting value by: "+askParam(pid),e);
        }
    }
    
    public boolean putNoThrow(byte pid,Object value){
        switch(pid){
             case 12: this._amiObjectIds=(long[])value;return true;
             case 13: this._appId=(java.lang.String)value;return true;
             case 1: this._arguments=(java.util.Map)value;return true;
             case 42: this._centerId=(java.lang.Byte)value;return true;
             case 2: this._commandDefinitionId=(java.lang.String)value;return true;
             case 11: this._commandId=(java.lang.Long)value;return true;
             case 7: this._commandUid=(java.lang.String)value;return true;
             case 4: this._fields=(java.util.List)value;return true;
             case 5: this._hostIp=(java.lang.String)value;return true;
             case 41: this._invokedBy=(java.lang.String)value;return true;
             case 14: this._isManySelect=(java.lang.Boolean)value;return true;
             case 9: this._objectIds=(java.lang.String[])value;return true;
             case 8: this._objectTypes=(java.lang.String[])value;return true;
             case 10: this._relayConnectionId=(java.lang.Integer)value;return true;
             case 15: this._sessionId=(java.lang.String)value;return true;
             case 40: this._targetAgentProcessUid=(java.lang.String)value;return true;
             case 6: this._timeoutMs=(java.lang.Integer)value;return true;
            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
             case 14: return this._isManySelect;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askByte(byte pid){
        switch(pid){
             case 42: return this._centerId;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public char askChar(byte pid){
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for char value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public short askShort(byte pid){
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for short value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askInt(byte pid){
        switch(pid){
             case 10: return this._relayConnectionId;
             case 6: return this._timeoutMs;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for int value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public float askFloat(byte pid){
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for float value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public long askLong(byte pid){
        switch(pid){
             case 11: return this._commandId;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for long value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public double askDouble(byte pid){
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for double value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putBoolean(byte pid,boolean value){
    
        switch(pid){
             case 14: this._isManySelect=value;return;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putByte(byte pid,byte value){
    
        switch(pid){
             case 42: this._centerId=value;return;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putChar(byte pid,char value){
    
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for char value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putShort(byte pid,short value){
    
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for short value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putInt(byte pid,int value){
    
        switch(pid){
             case 10: this._relayConnectionId=value;return;
             case 6: this._timeoutMs=value;return;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for int value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putFloat(byte pid,float value){
    
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for float value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putLong(byte pid,long value){
    
        switch(pid){
             case 11: this._commandId=value;return;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for long value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putDouble(byte pid,double value){
    
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for double value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void read(FromByteArrayConverterSession session) throws IOException{
    
        ObjectToByteArrayConverter converter=session.getConverter();
        DataInput in=session.getStream();//
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
        
            this._arguments=(java.util.Map)converter.read(session);
        
            break;

        case 2:
        
            this._commandDefinitionId=(java.lang.String)converter.read(session);
        
            break;

        case 4:
        
            this._fields=(java.util.List)converter.read(session);
        
            break;

        case 5:
        
            this._hostIp=(java.lang.String)converter.read(session);
        
            break;

        case 6:
        
            if((basicType=in.readByte())!=4)
                break;
            this._timeoutMs=in.readInt();
        
            break;

        case 7:
        
            this._commandUid=(java.lang.String)converter.read(session);
        
            break;

        case 8:
        
            this._objectTypes=(java.lang.String[])converter.read(session);
        
            break;

        case 9:
        
            this._objectIds=(java.lang.String[])converter.read(session);
        
            break;

        case 10:
        
            if((basicType=in.readByte())!=4)
                break;
            this._relayConnectionId=in.readInt();
        
            break;

        case 11:
        
            if((basicType=in.readByte())!=6)
                break;
            this._commandId=in.readLong();
        
            break;

        case 12:
        
            this._amiObjectIds=(long[])converter.read(session);
        
            break;

        case 13:
        
            this._appId=(java.lang.String)converter.read(session);
        
            break;

        case 14:
        
            if((basicType=in.readByte())!=0)
                break;
            this._isManySelect=in.readBoolean();
        
            break;

        case 15:
        
            this._sessionId=(java.lang.String)converter.read(session);
        
            break;

        case 40:
        
            this._targetAgentProcessUid=(java.lang.String)converter.read(session);
        
            break;

        case 41:
        
            this._invokedBy=(java.lang.String)converter.read(session);
        
            break;

        case 42:
        
            if((basicType=in.readByte())!=1)
                break;
            this._centerId=in.readByte();
        
            break;
                case -1:
                    return;
                default:
                    basicType=in.readByte();
            putNoThrow(pid,converter.getConverter(basicType).read(session));
            }
        }
    }
    

    public void write(ToByteArrayConverterSession session) throws IOException{
        ObjectToByteArrayConverter converter=session.getConverter();
        byte transience=converter.getSkipTransience();
        DataOutput out=session.getStream();
        out.writeBoolean(true);//pids supported
        
if(this._arguments!=null && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this._arguments,session);
        
}

if(this._commandDefinitionId!=null && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this._commandDefinitionId,session);
        
}

if(this._fields!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._fields,session);
        
}

if(this._hostIp!=null && (0 & transience)==0){
    out.writeByte(5);
        
    converter.write(this._hostIp,session);
        
}

if(this._timeoutMs!=0 && (0 & transience)==0){
    out.writeByte(6);
        
    out.writeByte(4);
    out.writeInt(this._timeoutMs);
        
}

if(this._commandUid!=null && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this._commandUid,session);
        
}

if(this._objectTypes!=null && (0 & transience)==0){
    out.writeByte(8);
        
    converter.write(this._objectTypes,session);
        
}

if(this._objectIds!=null && (0 & transience)==0){
    out.writeByte(9);
        
    converter.write(this._objectIds,session);
        
}

if(this._relayConnectionId!=0 && (0 & transience)==0){
    out.writeByte(10);
        
    out.writeByte(4);
    out.writeInt(this._relayConnectionId);
        
}

if(this._commandId!=0L && (0 & transience)==0){
    out.writeByte(11);
        
    out.writeByte(6);
    out.writeLong(this._commandId);
        
}

if(this._amiObjectIds!=null && (0 & transience)==0){
    out.writeByte(12);
        
    converter.write(this._amiObjectIds,session);
        
}

if(this._appId!=null && (0 & transience)==0){
    out.writeByte(13);
        
    converter.write(this._appId,session);
        
}

if(this._isManySelect!=false && (0 & transience)==0){
    out.writeByte(14);
        
    out.writeByte(0);
    out.writeBoolean(this._isManySelect);
        
}

if(this._sessionId!=null && (0 & transience)==0){
    out.writeByte(15);
        
    converter.write(this._sessionId,session);
        
}

if(this._targetAgentProcessUid!=null && (0 & transience)==0){
    out.writeByte(40);
        
    converter.write(this._targetAgentProcessUid,session);
        
}

if(this._invokedBy!=null && (0 & transience)==0){
    out.writeByte(41);
        
    converter.write(this._invokedBy,session);
        
}

if(this._centerId!=(byte)0 && (0 & transience)==0){
    out.writeByte(42);
        
    out.writeByte(1);
    out.writeByte(this._centerId);
        
}
;
        out.writeByte(-1);
    }
    


    public ValuedParam[] askValuedParams(){
        return VALUED_PARAMS;
    }

    
    
    
    private int ___ackerId;
    private Acker ___acker;
    
    @Override
	public boolean askAckIsPosDup(){
	    return ___ackerId<0;
	}
    @Override
	public int askAckId(){
	    return ___ackerId<0 ? -___ackerId : ___ackerId;
	}

    @Override
	public void putAckId(int ___ackerId,boolean isPosDup){
	    this.___ackerId=isPosDup ? -___ackerId : ___ackerId;
	}

    @Override
	public void ack(Object v){
	    if(___acker!=null)
	        ___acker.ack(this,v);
	}

    @Override
	public void registerAcker(Acker ___acker){
	    if(___acker!=null && this.___acker!=null)
	        throw new RuntimeException("Acker already registered");
	    this.___acker=___acker;
	}

    @Override
	public void transferAckerTo(Ackable ackable){
	    ackable.registerAcker(___acker);
	    this.___acker=null;
	    ackable.putAckId(askAckId(),askAckIsPosDup());
	    this.___ackerId=NO_ACK_ID;
	}
    
	
    @Override
    public String toString(){
        return VH.toString(this);
    }
    
    @Override
    public ValuedSchema<Valued> askSchema(){
        return this;
    }
    
    private static final DetailedException newMissingValueException(Object key,Object existing,String message){
      DetailedException e=new DetailedException(message);
      e.set("key",key);
      e.set("existing",existing);
      return e;
    }
    private final DetailedException newLockedException(Object key,Object value){
      DetailedException e=new DetailedException("Can not modify locked class");
      if(key instanceof String)
        e.set("target param",key);
      else if(key!=null)
        e.set("target pid",key);
      e.set("target value",value);
      e.set("target",this);
      e.initCause(new com.f1.base.LockedException());
      return e;
    }
    

}