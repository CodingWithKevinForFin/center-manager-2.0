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

public abstract class AmiRelayCommandDefMessage0 implements com.f1.ami.amicommon.msg.AmiRelayCommandDefMessage ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private java.lang.String _amiScript;

    private short _appIdStringKey;

    private java.lang.String _argumentsJson;

    private int _callbacksMask;

    private java.lang.String _commandId;

    private int _connectionId;

    private java.lang.String _enabledExpression;

    private java.lang.String _fields;

    private java.lang.String _filterClause;

    private java.lang.String _help;

    private int _level;

    private long _origSeqNum;

    private byte[] _params;

    private int _priority;

    private java.lang.String _selectMode;

    private java.lang.String _style;

    private java.lang.String _title;

    private byte _transformState;

    private java.lang.String _whereClause;

    private static final String NAMES[]={ "amiScript" ,"appIdStringKey","argumentsJson","callbacksMask","commandId","connectionId","enabledExpression","fields","filterClause","help","level","origSeqNum","params","priority","selectMode","style","title","transformState","whereClause"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 43;
        try{
        switch(h){

                case 5:

                    if(name == "title" || name.equals("title")) {this._title=(java.lang.String)value;return;}
break;
                case 6:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return;}
break;
                case 7:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {this._appIdStringKey=(java.lang.Short)value;return;}
break;
                case 9:

                    if(name == "amiScript" || name.equals("amiScript")) {this._amiScript=(java.lang.String)value;return;}
break;
                case 14:

                    if(name == "transformState" || name.equals("transformState")) {this._transformState=(java.lang.Byte)value;return;}
break;
                case 15:

                    if(name == "help" || name.equals("help")) {this._help=(java.lang.String)value;return;}
break;
                case 17:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {this._origSeqNum=(java.lang.Long)value;return;}
break;
                case 20:

                    if(name == "whereClause" || name.equals("whereClause")) {this._whereClause=(java.lang.String)value;return;}
break;
                case 21:

                    if(name == "params" || name.equals("params")) {this._params=(byte[])value;return;}
break;
                case 23:

                    if(name == "selectMode" || name.equals("selectMode")) {this._selectMode=(java.lang.String)value;return;}
break;
                case 25:

                    if(name == "style" || name.equals("style")) {this._style=(java.lang.String)value;return;}
break;
                case 26:

                    if(name == "filterClause" || name.equals("filterClause")) {this._filterClause=(java.lang.String)value;return;}
break;
                case 29:

                    if(name == "enabledExpression" || name.equals("enabledExpression")) {this._enabledExpression=(java.lang.String)value;return;}
break;
                case 32:

                    if(name == "callbacksMask" || name.equals("callbacksMask")) {this._callbacksMask=(java.lang.Integer)value;return;}
break;
                case 33:

                    if(name == "commandId" || name.equals("commandId")) {this._commandId=(java.lang.String)value;return;}
break;
                case 35:

                    if(name == "level" || name.equals("level")) {this._level=(java.lang.Integer)value;return;}
break;
                case 36:

                    if(name == "argumentsJson" || name.equals("argumentsJson")) {this._argumentsJson=(java.lang.String)value;return;}
break;
                case 38:

                    if(name == "connectionId" || name.equals("connectionId")) {this._connectionId=(java.lang.Integer)value;return;}
break;
                case 41:

                    if(name == "fields" || name.equals("fields")) {this._fields=(java.lang.String)value;return;}
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

                case 5:

                    if(name == "title" || name.equals("title")) {this._title=(java.lang.String)value;return true;}
break;
                case 6:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return true;}
break;
                case 7:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {this._appIdStringKey=(java.lang.Short)value;return true;}
break;
                case 9:

                    if(name == "amiScript" || name.equals("amiScript")) {this._amiScript=(java.lang.String)value;return true;}
break;
                case 14:

                    if(name == "transformState" || name.equals("transformState")) {this._transformState=(java.lang.Byte)value;return true;}
break;
                case 15:

                    if(name == "help" || name.equals("help")) {this._help=(java.lang.String)value;return true;}
break;
                case 17:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {this._origSeqNum=(java.lang.Long)value;return true;}
break;
                case 20:

                    if(name == "whereClause" || name.equals("whereClause")) {this._whereClause=(java.lang.String)value;return true;}
break;
                case 21:

                    if(name == "params" || name.equals("params")) {this._params=(byte[])value;return true;}
break;
                case 23:

                    if(name == "selectMode" || name.equals("selectMode")) {this._selectMode=(java.lang.String)value;return true;}
break;
                case 25:

                    if(name == "style" || name.equals("style")) {this._style=(java.lang.String)value;return true;}
break;
                case 26:

                    if(name == "filterClause" || name.equals("filterClause")) {this._filterClause=(java.lang.String)value;return true;}
break;
                case 29:

                    if(name == "enabledExpression" || name.equals("enabledExpression")) {this._enabledExpression=(java.lang.String)value;return true;}
break;
                case 32:

                    if(name == "callbacksMask" || name.equals("callbacksMask")) {this._callbacksMask=(java.lang.Integer)value;return true;}
break;
                case 33:

                    if(name == "commandId" || name.equals("commandId")) {this._commandId=(java.lang.String)value;return true;}
break;
                case 35:

                    if(name == "level" || name.equals("level")) {this._level=(java.lang.Integer)value;return true;}
break;
                case 36:

                    if(name == "argumentsJson" || name.equals("argumentsJson")) {this._argumentsJson=(java.lang.String)value;return true;}
break;
                case 38:

                    if(name == "connectionId" || name.equals("connectionId")) {this._connectionId=(java.lang.Integer)value;return true;}
break;
                case 41:

                    if(name == "fields" || name.equals("fields")) {this._fields=(java.lang.String)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 43;
        switch(h){

                case 5:

		    
                    if(name == "title" || name.equals("title")) {return this._title;}
            
break;
                case 6:

		    
                    if(name == "priority" || name.equals("priority")) {return OH.valueOf(this._priority);}
		    
break;
                case 7:

		    
                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return OH.valueOf(this._appIdStringKey);}
		    
break;
                case 9:

		    
                    if(name == "amiScript" || name.equals("amiScript")) {return this._amiScript;}
            
break;
                case 14:

		    
                    if(name == "transformState" || name.equals("transformState")) {return OH.valueOf(this._transformState);}
		    
break;
                case 15:

		    
                    if(name == "help" || name.equals("help")) {return this._help;}
            
break;
                case 17:

		    
                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return OH.valueOf(this._origSeqNum);}
		    
break;
                case 20:

		    
                    if(name == "whereClause" || name.equals("whereClause")) {return this._whereClause;}
            
break;
                case 21:

		    
                    if(name == "params" || name.equals("params")) {return this._params;}
            
break;
                case 23:

		    
                    if(name == "selectMode" || name.equals("selectMode")) {return this._selectMode;}
            
break;
                case 25:

		    
                    if(name == "style" || name.equals("style")) {return this._style;}
            
break;
                case 26:

		    
                    if(name == "filterClause" || name.equals("filterClause")) {return this._filterClause;}
            
break;
                case 29:

		    
                    if(name == "enabledExpression" || name.equals("enabledExpression")) {return this._enabledExpression;}
            
break;
                case 32:

		    
                    if(name == "callbacksMask" || name.equals("callbacksMask")) {return OH.valueOf(this._callbacksMask);}
		    
break;
                case 33:

		    
                    if(name == "commandId" || name.equals("commandId")) {return this._commandId;}
            
break;
                case 35:

		    
                    if(name == "level" || name.equals("level")) {return OH.valueOf(this._level);}
		    
break;
                case 36:

		    
                    if(name == "argumentsJson" || name.equals("argumentsJson")) {return this._argumentsJson;}
            
break;
                case 38:

		    
                    if(name == "connectionId" || name.equals("connectionId")) {return OH.valueOf(this._connectionId);}
		    
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

                case 5:

                    if(name == "title" || name.equals("title")) {return java.lang.String.class;}
break;
                case 6:

                    if(name == "priority" || name.equals("priority")) {return int.class;}
break;
                case 7:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return short.class;}
break;
                case 9:

                    if(name == "amiScript" || name.equals("amiScript")) {return java.lang.String.class;}
break;
                case 14:

                    if(name == "transformState" || name.equals("transformState")) {return byte.class;}
break;
                case 15:

                    if(name == "help" || name.equals("help")) {return java.lang.String.class;}
break;
                case 17:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return long.class;}
break;
                case 20:

                    if(name == "whereClause" || name.equals("whereClause")) {return java.lang.String.class;}
break;
                case 21:

                    if(name == "params" || name.equals("params")) {return byte[].class;}
break;
                case 23:

                    if(name == "selectMode" || name.equals("selectMode")) {return java.lang.String.class;}
break;
                case 25:

                    if(name == "style" || name.equals("style")) {return java.lang.String.class;}
break;
                case 26:

                    if(name == "filterClause" || name.equals("filterClause")) {return java.lang.String.class;}
break;
                case 29:

                    if(name == "enabledExpression" || name.equals("enabledExpression")) {return java.lang.String.class;}
break;
                case 32:

                    if(name == "callbacksMask" || name.equals("callbacksMask")) {return int.class;}
break;
                case 33:

                    if(name == "commandId" || name.equals("commandId")) {return java.lang.String.class;}
break;
                case 35:

                    if(name == "level" || name.equals("level")) {return int.class;}
break;
                case 36:

                    if(name == "argumentsJson" || name.equals("argumentsJson")) {return java.lang.String.class;}
break;
                case 38:

                    if(name == "connectionId" || name.equals("connectionId")) {return int.class;}
break;
                case 41:

                    if(name == "fields" || name.equals("fields")) {return java.lang.String.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 43;
        switch(h){

                case 5:

                    if(name == "title" || name.equals("title")) {return VALUED_PARAM_title;}
break;
                case 6:

                    if(name == "priority" || name.equals("priority")) {return VALUED_PARAM_priority;}
break;
                case 7:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return VALUED_PARAM_appIdStringKey;}
break;
                case 9:

                    if(name == "amiScript" || name.equals("amiScript")) {return VALUED_PARAM_amiScript;}
break;
                case 14:

                    if(name == "transformState" || name.equals("transformState")) {return VALUED_PARAM_transformState;}
break;
                case 15:

                    if(name == "help" || name.equals("help")) {return VALUED_PARAM_help;}
break;
                case 17:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return VALUED_PARAM_origSeqNum;}
break;
                case 20:

                    if(name == "whereClause" || name.equals("whereClause")) {return VALUED_PARAM_whereClause;}
break;
                case 21:

                    if(name == "params" || name.equals("params")) {return VALUED_PARAM_params;}
break;
                case 23:

                    if(name == "selectMode" || name.equals("selectMode")) {return VALUED_PARAM_selectMode;}
break;
                case 25:

                    if(name == "style" || name.equals("style")) {return VALUED_PARAM_style;}
break;
                case 26:

                    if(name == "filterClause" || name.equals("filterClause")) {return VALUED_PARAM_filterClause;}
break;
                case 29:

                    if(name == "enabledExpression" || name.equals("enabledExpression")) {return VALUED_PARAM_enabledExpression;}
break;
                case 32:

                    if(name == "callbacksMask" || name.equals("callbacksMask")) {return VALUED_PARAM_callbacksMask;}
break;
                case 33:

                    if(name == "commandId" || name.equals("commandId")) {return VALUED_PARAM_commandId;}
break;
                case 35:

                    if(name == "level" || name.equals("level")) {return VALUED_PARAM_level;}
break;
                case 36:

                    if(name == "argumentsJson" || name.equals("argumentsJson")) {return VALUED_PARAM_argumentsJson;}
break;
                case 38:

                    if(name == "connectionId" || name.equals("connectionId")) {return VALUED_PARAM_connectionId;}
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

                case 5:

                    if(name == "title" || name.equals("title")) {return 16;}
break;
                case 6:

                    if(name == "priority" || name.equals("priority")) {return 13;}
break;
                case 7:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return 1;}
break;
                case 9:

                    if(name == "amiScript" || name.equals("amiScript")) {return 0;}
break;
                case 14:

                    if(name == "transformState" || name.equals("transformState")) {return 17;}
break;
                case 15:

                    if(name == "help" || name.equals("help")) {return 9;}
break;
                case 17:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return 11;}
break;
                case 20:

                    if(name == "whereClause" || name.equals("whereClause")) {return 18;}
break;
                case 21:

                    if(name == "params" || name.equals("params")) {return 12;}
break;
                case 23:

                    if(name == "selectMode" || name.equals("selectMode")) {return 14;}
break;
                case 25:

                    if(name == "style" || name.equals("style")) {return 15;}
break;
                case 26:

                    if(name == "filterClause" || name.equals("filterClause")) {return 8;}
break;
                case 29:

                    if(name == "enabledExpression" || name.equals("enabledExpression")) {return 6;}
break;
                case 32:

                    if(name == "callbacksMask" || name.equals("callbacksMask")) {return 3;}
break;
                case 33:

                    if(name == "commandId" || name.equals("commandId")) {return 4;}
break;
                case 35:

                    if(name == "level" || name.equals("level")) {return 10;}
break;
                case 36:

                    if(name == "argumentsJson" || name.equals("argumentsJson")) {return 2;}
break;
                case 38:

                    if(name == "connectionId" || name.equals("connectionId")) {return 5;}
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
        return 19;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiRelayCommandDefMessage0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 43;
        switch(h){

                case 5:

                    if(name == "title" || name.equals("title")) {return true;}
break;
                case 6:

                    if(name == "priority" || name.equals("priority")) {return true;}
break;
                case 7:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return true;}
break;
                case 9:

                    if(name == "amiScript" || name.equals("amiScript")) {return true;}
break;
                case 14:

                    if(name == "transformState" || name.equals("transformState")) {return true;}
break;
                case 15:

                    if(name == "help" || name.equals("help")) {return true;}
break;
                case 17:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return true;}
break;
                case 20:

                    if(name == "whereClause" || name.equals("whereClause")) {return true;}
break;
                case 21:

                    if(name == "params" || name.equals("params")) {return true;}
break;
                case 23:

                    if(name == "selectMode" || name.equals("selectMode")) {return true;}
break;
                case 25:

                    if(name == "style" || name.equals("style")) {return true;}
break;
                case 26:

                    if(name == "filterClause" || name.equals("filterClause")) {return true;}
break;
                case 29:

                    if(name == "enabledExpression" || name.equals("enabledExpression")) {return true;}
break;
                case 32:

                    if(name == "callbacksMask" || name.equals("callbacksMask")) {return true;}
break;
                case 33:

                    if(name == "commandId" || name.equals("commandId")) {return true;}
break;
                case 35:

                    if(name == "level" || name.equals("level")) {return true;}
break;
                case 36:

                    if(name == "argumentsJson" || name.equals("argumentsJson")) {return true;}
break;
                case 38:

                    if(name == "connectionId" || name.equals("connectionId")) {return true;}
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

                case 5:

                    if(name == "title" || name.equals("title")) {return 20;}
break;
                case 6:

                    if(name == "priority" || name.equals("priority")) {return 4;}
break;
                case 7:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return 2;}
break;
                case 9:

                    if(name == "amiScript" || name.equals("amiScript")) {return 20;}
break;
                case 14:

                    if(name == "transformState" || name.equals("transformState")) {return 1;}
break;
                case 15:

                    if(name == "help" || name.equals("help")) {return 20;}
break;
                case 17:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return 6;}
break;
                case 20:

                    if(name == "whereClause" || name.equals("whereClause")) {return 20;}
break;
                case 21:

                    if(name == "params" || name.equals("params")) {return 101;}
break;
                case 23:

                    if(name == "selectMode" || name.equals("selectMode")) {return 20;}
break;
                case 25:

                    if(name == "style" || name.equals("style")) {return 20;}
break;
                case 26:

                    if(name == "filterClause" || name.equals("filterClause")) {return 20;}
break;
                case 29:

                    if(name == "enabledExpression" || name.equals("enabledExpression")) {return 20;}
break;
                case 32:

                    if(name == "callbacksMask" || name.equals("callbacksMask")) {return 4;}
break;
                case 33:

                    if(name == "commandId" || name.equals("commandId")) {return 20;}
break;
                case 35:

                    if(name == "level" || name.equals("level")) {return 4;}
break;
                case 36:

                    if(name == "argumentsJson" || name.equals("argumentsJson")) {return 20;}
break;
                case 38:

                    if(name == "connectionId" || name.equals("connectionId")) {return 4;}
break;
                case 41:

                    if(name == "fields" || name.equals("fields")) {return 20;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _amiScript;

        case 1:return _appIdStringKey;

        case 2:return _argumentsJson;

        case 3:return _callbacksMask;

        case 4:return _commandId;

        case 5:return _connectionId;

        case 6:return _enabledExpression;

        case 7:return _fields;

        case 8:return _filterClause;

        case 9:return _help;

        case 10:return _level;

        case 11:return _origSeqNum;

        case 12:return _params;

        case 13:return _priority;

        case 14:return _selectMode;

        case 15:return _style;

        case 16:return _title;

        case 17:return _transformState;

        case 18:return _whereClause;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 19);
    }

    public java.lang.String getAmiScript(){
        return this._amiScript;
    }
    public void setAmiScript(java.lang.String _amiScript){
    
        this._amiScript=_amiScript;
    }

    public short getAppIdStringKey(){
        return this._appIdStringKey;
    }
    public void setAppIdStringKey(short _appIdStringKey){
    
        this._appIdStringKey=_appIdStringKey;
    }

    public java.lang.String getArgumentsJson(){
        return this._argumentsJson;
    }
    public void setArgumentsJson(java.lang.String _argumentsJson){
    
        this._argumentsJson=_argumentsJson;
    }

    public int getCallbacksMask(){
        return this._callbacksMask;
    }
    public void setCallbacksMask(int _callbacksMask){
    
        this._callbacksMask=_callbacksMask;
    }

    public java.lang.String getCommandId(){
        return this._commandId;
    }
    public void setCommandId(java.lang.String _commandId){
    
        this._commandId=_commandId;
    }

    public int getConnectionId(){
        return this._connectionId;
    }
    public void setConnectionId(int _connectionId){
    
        this._connectionId=_connectionId;
    }

    public java.lang.String getEnabledExpression(){
        return this._enabledExpression;
    }
    public void setEnabledExpression(java.lang.String _enabledExpression){
    
        this._enabledExpression=_enabledExpression;
    }

    public java.lang.String getFields(){
        return this._fields;
    }
    public void setFields(java.lang.String _fields){
    
        this._fields=_fields;
    }

    public java.lang.String getFilterClause(){
        return this._filterClause;
    }
    public void setFilterClause(java.lang.String _filterClause){
    
        this._filterClause=_filterClause;
    }

    public java.lang.String getHelp(){
        return this._help;
    }
    public void setHelp(java.lang.String _help){
    
        this._help=_help;
    }

    public int getLevel(){
        return this._level;
    }
    public void setLevel(int _level){
    
        this._level=_level;
    }

    public long getOrigSeqNum(){
        return this._origSeqNum;
    }
    public void setOrigSeqNum(long _origSeqNum){
    
        this._origSeqNum=_origSeqNum;
    }

    public byte[] getParams(){
        return this._params;
    }
    public void setParams(byte[] _params){
    
        this._params=_params;
    }

    public int getPriority(){
        return this._priority;
    }
    public void setPriority(int _priority){
    
        this._priority=_priority;
    }

    public java.lang.String getSelectMode(){
        return this._selectMode;
    }
    public void setSelectMode(java.lang.String _selectMode){
    
        this._selectMode=_selectMode;
    }

    public java.lang.String getStyle(){
        return this._style;
    }
    public void setStyle(java.lang.String _style){
    
        this._style=_style;
    }

    public java.lang.String getTitle(){
        return this._title;
    }
    public void setTitle(java.lang.String _title){
    
        this._title=_title;
    }

    public byte getTransformState(){
        return this._transformState;
    }
    public void setTransformState(byte _transformState){
    
        this._transformState=_transformState;
    }

    public java.lang.String getWhereClause(){
        return this._whereClause;
    }
    public void setWhereClause(java.lang.String _whereClause){
    
        this._whereClause=_whereClause;
    }





  
    private static final class VALUED_PARAM_CLASS_amiScript extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 0;
	    }
    
	    @Override
	    public byte getPid() {
            return 27;
	    }
    
	    @Override
	    public String getName() {
            return "amiScript";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (java.lang.String)((AmiRelayCommandDefMessage0)valued).getAmiScript();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setAmiScript((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setAmiScript(source.getAmiScript());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getAmiScript(),source.getAmiScript());
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getAmiScript(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getAmiScript(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String amiScript";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setAmiScript(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_amiScript=new VALUED_PARAM_CLASS_amiScript();
  

  
    private static final class VALUED_PARAM_CLASS_appIdStringKey extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 2;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeShort(valued.getAppIdStringKey());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setAppIdStringKey(stream.readShort());
		    
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
		    return 1;
	    }
    
	    @Override
	    public byte getPid() {
            return 44;
	    }
    
	    @Override
	    public String getName() {
            return "appIdStringKey";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (short)((AmiRelayCommandDefMessage0)valued).getAppIdStringKey();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setAppIdStringKey((java.lang.Short)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setAppIdStringKey(source.getAppIdStringKey());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getAppIdStringKey(),source.getAppIdStringKey());
	    }
	    
	    
	    
	    
	    
	    
	    @Override
	    public short getShort(AmiRelayCommandDefMessage0 valued) {
		    return valued.getAppIdStringKey();
	    }
    
	    @Override
	    public void setShort(AmiRelayCommandDefMessage0 valued, short value) {
		    valued.setAppIdStringKey(value);
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return short.class;
	    }
	    private static final Caster CASTER=OH.getCaster(short.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAppIdStringKey());
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAppIdStringKey());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:short appIdStringKey";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setAppIdStringKey((short)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_appIdStringKey=new VALUED_PARAM_CLASS_appIdStringKey();
  

  
    private static final class VALUED_PARAM_CLASS_argumentsJson extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 2;
	    }
    
	    @Override
	    public byte getPid() {
            return 16;
	    }
    
	    @Override
	    public String getName() {
            return "argumentsJson";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (java.lang.String)((AmiRelayCommandDefMessage0)valued).getArgumentsJson();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setArgumentsJson((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setArgumentsJson(source.getArgumentsJson());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getArgumentsJson(),source.getArgumentsJson());
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getArgumentsJson(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getArgumentsJson(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String argumentsJson";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setArgumentsJson(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_argumentsJson=new VALUED_PARAM_CLASS_argumentsJson();
  

  
    private static final class VALUED_PARAM_CLASS_callbacksMask extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getCallbacksMask());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setCallbacksMask(stream.readInt());
		    
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
            return 26;
	    }
    
	    @Override
	    public String getName() {
            return "callbacksMask";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (int)((AmiRelayCommandDefMessage0)valued).getCallbacksMask();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setCallbacksMask((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setCallbacksMask(source.getCallbacksMask());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getCallbacksMask(),source.getCallbacksMask());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiRelayCommandDefMessage0 valued) {
		    return valued.getCallbacksMask();
	    }
    
	    @Override
	    public void setInt(AmiRelayCommandDefMessage0 valued, int value) {
		    valued.setCallbacksMask(value);
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getCallbacksMask());
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getCallbacksMask());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int callbacksMask";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setCallbacksMask(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_callbacksMask=new VALUED_PARAM_CLASS_callbacksMask();
  

  
    private static final class VALUED_PARAM_CLASS_commandId extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "commandId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (java.lang.String)((AmiRelayCommandDefMessage0)valued).getCommandId();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setCommandId((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setCommandId(source.getCommandId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getCommandId(),source.getCommandId());
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getCommandId(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getCommandId(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String commandId";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setCommandId(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_commandId=new VALUED_PARAM_CLASS_commandId();
  

  
    private static final class VALUED_PARAM_CLASS_connectionId extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getConnectionId());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setConnectionId(stream.readInt());
		    
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
            return 10;
	    }
    
	    @Override
	    public String getName() {
            return "connectionId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (int)((AmiRelayCommandDefMessage0)valued).getConnectionId();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setConnectionId((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setConnectionId(source.getConnectionId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getConnectionId(),source.getConnectionId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiRelayCommandDefMessage0 valued) {
		    return valued.getConnectionId();
	    }
    
	    @Override
	    public void setInt(AmiRelayCommandDefMessage0 valued, int value) {
		    valued.setConnectionId(value);
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getConnectionId());
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getConnectionId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int connectionId";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setConnectionId(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_connectionId=new VALUED_PARAM_CLASS_connectionId();
  

  
    private static final class VALUED_PARAM_CLASS_enabledExpression extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 21;
	    }
    
	    @Override
	    public String getName() {
            return "enabledExpression";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (java.lang.String)((AmiRelayCommandDefMessage0)valued).getEnabledExpression();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setEnabledExpression((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setEnabledExpression(source.getEnabledExpression());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getEnabledExpression(),source.getEnabledExpression());
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getEnabledExpression(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getEnabledExpression(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String enabledExpression";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setEnabledExpression(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_enabledExpression=new VALUED_PARAM_CLASS_enabledExpression();
  

  
    private static final class VALUED_PARAM_CLASS_fields extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 7;
	    }
    
	    @Override
	    public byte getPid() {
            return 24;
	    }
    
	    @Override
	    public String getName() {
            return "fields";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (java.lang.String)((AmiRelayCommandDefMessage0)valued).getFields();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setFields((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setFields(source.getFields());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getFields(),source.getFields());
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getFields(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getFields(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String fields";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setFields(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_fields=new VALUED_PARAM_CLASS_fields();
  

  
    private static final class VALUED_PARAM_CLASS_filterClause extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 25;
	    }
    
	    @Override
	    public String getName() {
            return "filterClause";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (java.lang.String)((AmiRelayCommandDefMessage0)valued).getFilterClause();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setFilterClause((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setFilterClause(source.getFilterClause());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getFilterClause(),source.getFilterClause());
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getFilterClause(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getFilterClause(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String filterClause";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setFilterClause(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_filterClause=new VALUED_PARAM_CLASS_filterClause();
  

  
    private static final class VALUED_PARAM_CLASS_help extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 15;
	    }
    
	    @Override
	    public String getName() {
            return "help";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (java.lang.String)((AmiRelayCommandDefMessage0)valued).getHelp();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setHelp((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setHelp(source.getHelp());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getHelp(),source.getHelp());
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getHelp(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getHelp(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String help";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setHelp(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_help=new VALUED_PARAM_CLASS_help();
  

  
    private static final class VALUED_PARAM_CLASS_level extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getLevel());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setLevel(stream.readInt());
		    
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
            return 6;
	    }
    
	    @Override
	    public String getName() {
            return "level";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (int)((AmiRelayCommandDefMessage0)valued).getLevel();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setLevel((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setLevel(source.getLevel());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getLevel(),source.getLevel());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiRelayCommandDefMessage0 valued) {
		    return valued.getLevel();
	    }
    
	    @Override
	    public void setInt(AmiRelayCommandDefMessage0 valued, int value) {
		    valued.setLevel(value);
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getLevel());
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getLevel());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int level";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setLevel(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_level=new VALUED_PARAM_CLASS_level();
  

  
    private static final class VALUED_PARAM_CLASS_origSeqNum extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getOrigSeqNum());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setOrigSeqNum(stream.readLong());
		    
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
		    return 11;
	    }
    
	    @Override
	    public byte getPid() {
            return 45;
	    }
    
	    @Override
	    public String getName() {
            return "origSeqNum";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (long)((AmiRelayCommandDefMessage0)valued).getOrigSeqNum();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setOrigSeqNum((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setOrigSeqNum(source.getOrigSeqNum());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getOrigSeqNum(),source.getOrigSeqNum());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiRelayCommandDefMessage0 valued) {
		    return valued.getOrigSeqNum();
	    }
    
	    @Override
	    public void setLong(AmiRelayCommandDefMessage0 valued, long value) {
		    valued.setOrigSeqNum(value);
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getOrigSeqNum());
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getOrigSeqNum());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long origSeqNum";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setOrigSeqNum(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_origSeqNum=new VALUED_PARAM_CLASS_origSeqNum();
  

  
    private static final class VALUED_PARAM_CLASS_params extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 101;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [B}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [B}");
		    
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
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "params";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (byte[])((AmiRelayCommandDefMessage0)valued).getParams();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setParams((byte[])value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setParams(source.getParams());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getParams(),source.getParams());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return byte[].class;
	    }
	    private static final Caster CASTER=OH.getCaster(byte[].class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getParams());
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getParams());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte[] params";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setParams(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_params=new VALUED_PARAM_CLASS_params();
  

  
    private static final class VALUED_PARAM_CLASS_priority extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getPriority());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setPriority(stream.readInt());
		    
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
            return 20;
	    }
    
	    @Override
	    public String getName() {
            return "priority";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (int)((AmiRelayCommandDefMessage0)valued).getPriority();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setPriority((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setPriority(source.getPriority());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getPriority(),source.getPriority());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiRelayCommandDefMessage0 valued) {
		    return valued.getPriority();
	    }
    
	    @Override
	    public void setInt(AmiRelayCommandDefMessage0 valued, int value) {
		    valued.setPriority(value);
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int priority";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setPriority(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_priority=new VALUED_PARAM_CLASS_priority();
  

  
    private static final class VALUED_PARAM_CLASS_selectMode extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 23;
	    }
    
	    @Override
	    public String getName() {
            return "selectMode";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (java.lang.String)((AmiRelayCommandDefMessage0)valued).getSelectMode();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setSelectMode((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setSelectMode(source.getSelectMode());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getSelectMode(),source.getSelectMode());
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getSelectMode(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getSelectMode(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String selectMode";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setSelectMode(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_selectMode=new VALUED_PARAM_CLASS_selectMode();
  

  
    private static final class VALUED_PARAM_CLASS_style extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 22;
	    }
    
	    @Override
	    public String getName() {
            return "style";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (java.lang.String)((AmiRelayCommandDefMessage0)valued).getStyle();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setStyle((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setStyle(source.getStyle());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getStyle(),source.getStyle());
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getStyle(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getStyle(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String style";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setStyle(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_style=new VALUED_PARAM_CLASS_style();
  

  
    private static final class VALUED_PARAM_CLASS_title extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 16;
	    }
    
	    @Override
	    public byte getPid() {
            return 18;
	    }
    
	    @Override
	    public String getName() {
            return "title";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (java.lang.String)((AmiRelayCommandDefMessage0)valued).getTitle();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setTitle((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setTitle(source.getTitle());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getTitle(),source.getTitle());
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getTitle(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getTitle(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String title";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setTitle(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_title=new VALUED_PARAM_CLASS_title();
  

  
    private static final class VALUED_PARAM_CLASS_transformState extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getTransformState());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setTransformState(stream.readByte());
		    
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
		    return 17;
	    }
    
	    @Override
	    public byte getPid() {
            return 46;
	    }
    
	    @Override
	    public String getName() {
            return "transformState";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (byte)((AmiRelayCommandDefMessage0)valued).getTransformState();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setTransformState((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setTransformState(source.getTransformState());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getTransformState(),source.getTransformState());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiRelayCommandDefMessage0 valued) {
		    return valued.getTransformState();
	    }
    
	    @Override
	    public void setByte(AmiRelayCommandDefMessage0 valued, byte value) {
		    valued.setTransformState(value);
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTransformState());
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTransformState());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte transformState";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setTransformState((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_transformState=new VALUED_PARAM_CLASS_transformState();
  

  
    private static final class VALUED_PARAM_CLASS_whereClause extends AbstractValuedParam<AmiRelayCommandDefMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayCommandDefMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayCommandDefMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 18;
	    }
    
	    @Override
	    public byte getPid() {
            return 19;
	    }
    
	    @Override
	    public String getName() {
            return "whereClause";
	    }
    
	    @Override
	    public Object getValue(AmiRelayCommandDefMessage0 valued) {
		    return (java.lang.String)((AmiRelayCommandDefMessage0)valued).getWhereClause();
	    }
    
	    @Override
	    public void setValue(AmiRelayCommandDefMessage0 valued, Object value) {
		    valued.setWhereClause((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
		    dest.setWhereClause(source.getWhereClause());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayCommandDefMessage0 source, AmiRelayCommandDefMessage0 dest) {
	        return OH.eq(dest.getWhereClause(),source.getWhereClause());
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
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getWhereClause(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayCommandDefMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getWhereClause(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String whereClause";
	    }
	    @Override
	    public void clear(AmiRelayCommandDefMessage0 valued){
	       valued.setWhereClause(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_whereClause=new VALUED_PARAM_CLASS_whereClause();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_amiScript, VALUED_PARAM_appIdStringKey, VALUED_PARAM_argumentsJson, VALUED_PARAM_callbacksMask, VALUED_PARAM_commandId, VALUED_PARAM_connectionId, VALUED_PARAM_enabledExpression, VALUED_PARAM_fields, VALUED_PARAM_filterClause, VALUED_PARAM_help, VALUED_PARAM_level, VALUED_PARAM_origSeqNum, VALUED_PARAM_params, VALUED_PARAM_priority, VALUED_PARAM_selectMode, VALUED_PARAM_style, VALUED_PARAM_title, VALUED_PARAM_transformState, VALUED_PARAM_whereClause, };



    private static final byte PIDS[]={ 27 ,44,16,26,3,10,21,24,25,15,6,45,4,20,23,22,18,46,19};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 27: return VALUED_PARAM_amiScript;
             case 44: return VALUED_PARAM_appIdStringKey;
             case 16: return VALUED_PARAM_argumentsJson;
             case 26: return VALUED_PARAM_callbacksMask;
             case 3: return VALUED_PARAM_commandId;
             case 10: return VALUED_PARAM_connectionId;
             case 21: return VALUED_PARAM_enabledExpression;
             case 24: return VALUED_PARAM_fields;
             case 25: return VALUED_PARAM_filterClause;
             case 15: return VALUED_PARAM_help;
             case 6: return VALUED_PARAM_level;
             case 45: return VALUED_PARAM_origSeqNum;
             case 4: return VALUED_PARAM_params;
             case 20: return VALUED_PARAM_priority;
             case 23: return VALUED_PARAM_selectMode;
             case 22: return VALUED_PARAM_style;
             case 18: return VALUED_PARAM_title;
             case 46: return VALUED_PARAM_transformState;
             case 19: return VALUED_PARAM_whereClause;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 27: return true;
             case 44: return true;
             case 16: return true;
             case 26: return true;
             case 3: return true;
             case 10: return true;
             case 21: return true;
             case 24: return true;
             case 25: return true;
             case 15: return true;
             case 6: return true;
             case 45: return true;
             case 4: return true;
             case 20: return true;
             case 23: return true;
             case 22: return true;
             case 18: return true;
             case 46: return true;
             case 19: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 27: return "amiScript";
             case 44: return "appIdStringKey";
             case 16: return "argumentsJson";
             case 26: return "callbacksMask";
             case 3: return "commandId";
             case 10: return "connectionId";
             case 21: return "enabledExpression";
             case 24: return "fields";
             case 25: return "filterClause";
             case 15: return "help";
             case 6: return "level";
             case 45: return "origSeqNum";
             case 4: return "params";
             case 20: return "priority";
             case 23: return "selectMode";
             case 22: return "style";
             case 18: return "title";
             case 46: return "transformState";
             case 19: return "whereClause";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 27: return 0;
             case 44: return 1;
             case 16: return 2;
             case 26: return 3;
             case 3: return 4;
             case 10: return 5;
             case 21: return 6;
             case 24: return 7;
             case 25: return 8;
             case 15: return 9;
             case 6: return 10;
             case 45: return 11;
             case 4: return 12;
             case 20: return 13;
             case 23: return 14;
             case 22: return 15;
             case 18: return 16;
             case 46: return 17;
             case 19: return 18;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="amiScript") return 27;
             if(name=="appIdStringKey") return 44;
             if(name=="argumentsJson") return 16;
             if(name=="callbacksMask") return 26;
             if(name=="commandId") return 3;
             if(name=="connectionId") return 10;
             if(name=="enabledExpression") return 21;
             if(name=="fields") return 24;
             if(name=="filterClause") return 25;
             if(name=="help") return 15;
             if(name=="level") return 6;
             if(name=="origSeqNum") return 45;
             if(name=="params") return 4;
             if(name=="priority") return 20;
             if(name=="selectMode") return 23;
             if(name=="style") return 22;
             if(name=="title") return 18;
             if(name=="transformState") return 46;
             if(name=="whereClause") return 19;
            
             if("amiScript".equals(name)) return 27;
             if("appIdStringKey".equals(name)) return 44;
             if("argumentsJson".equals(name)) return 16;
             if("callbacksMask".equals(name)) return 26;
             if("commandId".equals(name)) return 3;
             if("connectionId".equals(name)) return 10;
             if("enabledExpression".equals(name)) return 21;
             if("fields".equals(name)) return 24;
             if("filterClause".equals(name)) return 25;
             if("help".equals(name)) return 15;
             if("level".equals(name)) return 6;
             if("origSeqNum".equals(name)) return 45;
             if("params".equals(name)) return 4;
             if("priority".equals(name)) return 20;
             if("selectMode".equals(name)) return 23;
             if("style".equals(name)) return 22;
             if("title".equals(name)) return 18;
             if("transformState".equals(name)) return 46;
             if("whereClause".equals(name)) return 19;
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
             case 27: return  this._amiScript; 
             case 44: return  OH.valueOf(this._appIdStringKey); 
             case 16: return  this._argumentsJson; 
             case 26: return  OH.valueOf(this._callbacksMask); 
             case 3: return  this._commandId; 
             case 10: return  OH.valueOf(this._connectionId); 
             case 21: return  this._enabledExpression; 
             case 24: return  this._fields; 
             case 25: return  this._filterClause; 
             case 15: return  this._help; 
             case 6: return  OH.valueOf(this._level); 
             case 45: return  OH.valueOf(this._origSeqNum); 
             case 4: return  this._params; 
             case 20: return  OH.valueOf(this._priority); 
             case 23: return  this._selectMode; 
             case 22: return  this._style; 
             case 18: return  this._title; 
             case 46: return  OH.valueOf(this._transformState); 
             case 19: return  this._whereClause; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 27: return java.lang.String.class;
             case 44: return short.class;
             case 16: return java.lang.String.class;
             case 26: return int.class;
             case 3: return java.lang.String.class;
             case 10: return int.class;
             case 21: return java.lang.String.class;
             case 24: return java.lang.String.class;
             case 25: return java.lang.String.class;
             case 15: return java.lang.String.class;
             case 6: return int.class;
             case 45: return long.class;
             case 4: return byte[].class;
             case 20: return int.class;
             case 23: return java.lang.String.class;
             case 22: return java.lang.String.class;
             case 18: return java.lang.String.class;
             case 46: return byte.class;
             case 19: return java.lang.String.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 27: return 20;
             case 44: return 2;
             case 16: return 20;
             case 26: return 4;
             case 3: return 20;
             case 10: return 4;
             case 21: return 20;
             case 24: return 20;
             case 25: return 20;
             case 15: return 20;
             case 6: return 4;
             case 45: return 6;
             case 4: return 101;
             case 20: return 4;
             case 23: return 20;
             case 22: return 20;
             case 18: return 20;
             case 46: return 1;
             case 19: return 20;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 27: this._amiScript=(java.lang.String)value;return;
             case 44: this._appIdStringKey=(java.lang.Short)value;return;
             case 16: this._argumentsJson=(java.lang.String)value;return;
             case 26: this._callbacksMask=(java.lang.Integer)value;return;
             case 3: this._commandId=(java.lang.String)value;return;
             case 10: this._connectionId=(java.lang.Integer)value;return;
             case 21: this._enabledExpression=(java.lang.String)value;return;
             case 24: this._fields=(java.lang.String)value;return;
             case 25: this._filterClause=(java.lang.String)value;return;
             case 15: this._help=(java.lang.String)value;return;
             case 6: this._level=(java.lang.Integer)value;return;
             case 45: this._origSeqNum=(java.lang.Long)value;return;
             case 4: this._params=(byte[])value;return;
             case 20: this._priority=(java.lang.Integer)value;return;
             case 23: this._selectMode=(java.lang.String)value;return;
             case 22: this._style=(java.lang.String)value;return;
             case 18: this._title=(java.lang.String)value;return;
             case 46: this._transformState=(java.lang.Byte)value;return;
             case 19: this._whereClause=(java.lang.String)value;return;
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
             case 27: this._amiScript=(java.lang.String)value;return true;
             case 44: this._appIdStringKey=(java.lang.Short)value;return true;
             case 16: this._argumentsJson=(java.lang.String)value;return true;
             case 26: this._callbacksMask=(java.lang.Integer)value;return true;
             case 3: this._commandId=(java.lang.String)value;return true;
             case 10: this._connectionId=(java.lang.Integer)value;return true;
             case 21: this._enabledExpression=(java.lang.String)value;return true;
             case 24: this._fields=(java.lang.String)value;return true;
             case 25: this._filterClause=(java.lang.String)value;return true;
             case 15: this._help=(java.lang.String)value;return true;
             case 6: this._level=(java.lang.Integer)value;return true;
             case 45: this._origSeqNum=(java.lang.Long)value;return true;
             case 4: this._params=(byte[])value;return true;
             case 20: this._priority=(java.lang.Integer)value;return true;
             case 23: this._selectMode=(java.lang.String)value;return true;
             case 22: this._style=(java.lang.String)value;return true;
             case 18: this._title=(java.lang.String)value;return true;
             case 46: this._transformState=(java.lang.Byte)value;return true;
             case 19: this._whereClause=(java.lang.String)value;return true;
            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askByte(byte pid){
        switch(pid){
             case 46: return this._transformState;
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
             case 44: return this._appIdStringKey;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for short value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askInt(byte pid){
        switch(pid){
             case 26: return this._callbacksMask;
             case 10: return this._connectionId;
             case 6: return this._level;
             case 20: return this._priority;
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
             case 45: return this._origSeqNum;
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
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putByte(byte pid,byte value){
    
        switch(pid){
             case 46: this._transformState=value;return;
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
             case 44: this._appIdStringKey=value;return;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for short value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putInt(byte pid,int value){
    
        switch(pid){
             case 26: this._callbacksMask=value;return;
             case 10: this._connectionId=value;return;
             case 6: this._level=value;return;
             case 20: this._priority=value;return;
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
             case 45: this._origSeqNum=value;return;
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
        
        case 3:
        
            this._commandId=(java.lang.String)converter.read(session);
        
            break;

        case 4:
        
            this._params=(byte[])converter.read(session);
        
            break;

        case 6:
        
            if((basicType=in.readByte())!=4)
                break;
            this._level=in.readInt();
        
            break;

        case 10:
        
            if((basicType=in.readByte())!=4)
                break;
            this._connectionId=in.readInt();
        
            break;

        case 15:
        
            this._help=(java.lang.String)converter.read(session);
        
            break;

        case 16:
        
            this._argumentsJson=(java.lang.String)converter.read(session);
        
            break;

        case 18:
        
            this._title=(java.lang.String)converter.read(session);
        
            break;

        case 19:
        
            this._whereClause=(java.lang.String)converter.read(session);
        
            break;

        case 20:
        
            if((basicType=in.readByte())!=4)
                break;
            this._priority=in.readInt();
        
            break;

        case 21:
        
            this._enabledExpression=(java.lang.String)converter.read(session);
        
            break;

        case 22:
        
            this._style=(java.lang.String)converter.read(session);
        
            break;

        case 23:
        
            this._selectMode=(java.lang.String)converter.read(session);
        
            break;

        case 24:
        
            this._fields=(java.lang.String)converter.read(session);
        
            break;

        case 25:
        
            this._filterClause=(java.lang.String)converter.read(session);
        
            break;

        case 26:
        
            if((basicType=in.readByte())!=4)
                break;
            this._callbacksMask=in.readInt();
        
            break;

        case 27:
        
            this._amiScript=(java.lang.String)converter.read(session);
        
            break;

        case 44:
        
            if((basicType=in.readByte())!=2)
                break;
            this._appIdStringKey=in.readShort();
        
            break;

        case 45:
        
            if((basicType=in.readByte())!=6)
                break;
            this._origSeqNum=in.readLong();
        
            break;

        case 46:
        
            if((basicType=in.readByte())!=1)
                break;
            this._transformState=in.readByte();
        
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
        
if(this._commandId!=null && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this._commandId,session);
        
}

if(this._params!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._params,session);
        
}

if(this._level!=0 && (0 & transience)==0){
    out.writeByte(6);
        
    out.writeByte(4);
    out.writeInt(this._level);
        
}

if(this._connectionId!=0 && (0 & transience)==0){
    out.writeByte(10);
        
    out.writeByte(4);
    out.writeInt(this._connectionId);
        
}

if(this._help!=null && (0 & transience)==0){
    out.writeByte(15);
        
    converter.write(this._help,session);
        
}

if(this._argumentsJson!=null && (0 & transience)==0){
    out.writeByte(16);
        
    converter.write(this._argumentsJson,session);
        
}

if(this._title!=null && (0 & transience)==0){
    out.writeByte(18);
        
    converter.write(this._title,session);
        
}

if(this._whereClause!=null && (0 & transience)==0){
    out.writeByte(19);
        
    converter.write(this._whereClause,session);
        
}

if(this._priority!=0 && (0 & transience)==0){
    out.writeByte(20);
        
    out.writeByte(4);
    out.writeInt(this._priority);
        
}

if(this._enabledExpression!=null && (0 & transience)==0){
    out.writeByte(21);
        
    converter.write(this._enabledExpression,session);
        
}

if(this._style!=null && (0 & transience)==0){
    out.writeByte(22);
        
    converter.write(this._style,session);
        
}

if(this._selectMode!=null && (0 & transience)==0){
    out.writeByte(23);
        
    converter.write(this._selectMode,session);
        
}

if(this._fields!=null && (0 & transience)==0){
    out.writeByte(24);
        
    converter.write(this._fields,session);
        
}

if(this._filterClause!=null && (0 & transience)==0){
    out.writeByte(25);
        
    converter.write(this._filterClause,session);
        
}

if(this._callbacksMask!=0 && (0 & transience)==0){
    out.writeByte(26);
        
    out.writeByte(4);
    out.writeInt(this._callbacksMask);
        
}

if(this._amiScript!=null && (0 & transience)==0){
    out.writeByte(27);
        
    converter.write(this._amiScript,session);
        
}

if(this._appIdStringKey!=(short)0 && (0 & transience)==0){
    out.writeByte(44);
        
    out.writeByte(2);
    out.writeShort(this._appIdStringKey);
        
}

if(this._origSeqNum!=0L && (0 & transience)==0){
    out.writeByte(45);
        
    out.writeByte(6);
    out.writeLong(this._origSeqNum);
        
}

if(this._transformState!=(byte)0 && (0 & transience)==0){
    out.writeByte(46);
        
    out.writeByte(1);
    out.writeByte(this._transformState);
        
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