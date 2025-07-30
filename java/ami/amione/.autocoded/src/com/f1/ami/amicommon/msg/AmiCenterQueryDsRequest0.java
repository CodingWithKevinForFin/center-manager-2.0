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

public abstract class AmiCenterQueryDsRequest0 implements com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private boolean _allowSqlInjection;

    private java.lang.String _comment;

    private java.lang.String _datasourceName;

    private java.lang.String _datasourceOverrideAdapter;

    private java.lang.String _datasourceOverrideOptions;

    private com.f1.base.Password _datasourceOverridePassword;

    private java.lang.String _datasourceOverridePasswordEnc;

    private java.lang.String _datasourceOverrideRelay;

    private java.lang.String _datasourceOverrideUrl;

    private java.lang.String _datasourceOverrideUsername;

    private java.util.Map _directives;

    private boolean _disableLogging;

    private java.lang.String _invokedBy;

    private boolean _isTest;

    private int _limit;

    private byte _originType;

    private long _parentProcessId;

    private com.f1.utils.string.Node _parsedNode;

    private byte _permissions;

    private int _previewCount;

    private int _priority;

    private java.lang.String _query;

    private long _querySessionId;

    private boolean _querySessionKeepAlive;

    private long _requestTime;

    private java.util.Map _sessionVariableTypes;

    private java.util.Map _sessionVariables;

    private java.util.List _tablesForPreview;

    private int _timeoutMs;

    private byte _type;

    private java.util.List _uploadValues;

    private boolean _useConcurrency;

    private static final String NAMES[]={ "allowSqlInjection" ,"comment","datasourceName","datasourceOverrideAdapter","datasourceOverrideOptions","datasourceOverridePassword","datasourceOverridePasswordEnc","datasourceOverrideRelay","datasourceOverrideUrl","datasourceOverrideUsername","directives","disableLogging","invokedBy","isTest","limit","originType","parentProcessId","parsedNode","permissions","previewCount","priority","query","querySessionId","querySessionKeepAlive","requestTime","sessionVariableTypes","sessionVariables","tablesForPreview","timeoutMs","type","uploadValues","useConcurrency"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 101;
        try{
        switch(h){

                case 6:

                    if(name == "sessionVariableTypes" || name.equals("sessionVariableTypes")) {this._sessionVariableTypes=(java.util.Map)value;return;}
break;
                case 8:

                    if(name == "type" || name.equals("type")) {this._type=(java.lang.Byte)value;return;}
break;
                case 12:

                    if(name == "querySessionKeepAlive" || name.equals("querySessionKeepAlive")) {this._querySessionKeepAlive=(java.lang.Boolean)value;return;}
break;
                case 18:

                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {this._allowSqlInjection=(java.lang.Boolean)value;return;}
break;
                case 19:

                    if(name == "querySessionId" || name.equals("querySessionId")) {this._querySessionId=(java.lang.Long)value;return;}
break;
                case 20:

                    if(name == "datasourceOverrideUrl" || name.equals("datasourceOverrideUrl")) {this._datasourceOverrideUrl=(java.lang.String)value;return;}
break;
                case 22:

                    if(name == "useConcurrency" || name.equals("useConcurrency")) {this._useConcurrency=(java.lang.Boolean)value;return;}
break;
                case 23:

                    if(name == "datasourceOverrideUsername" || name.equals("datasourceOverrideUsername")) {this._datasourceOverrideUsername=(java.lang.String)value;return;}
break;
                case 27:

                    if(name == "directives" || name.equals("directives")) {this._directives=(java.util.Map)value;return;}
break;
                case 28:

                    if(name == "parsedNode" || name.equals("parsedNode")) {this._parsedNode=(com.f1.utils.string.Node)value;return;}
break;
                case 29:

                    if(name == "permissions" || name.equals("permissions")) {this._permissions=(java.lang.Byte)value;return;}
break;
                case 34:

                    if(name == "uploadValues" || name.equals("uploadValues")) {this._uploadValues=(java.util.List)value;return;}
break;
                case 38:

                    if(name == "datasourceOverridePassword" || name.equals("datasourceOverridePassword")) {this._datasourceOverridePassword=(com.f1.base.Password)value;return;}
break;
                case 40:

                    if(name == "datasourceOverrideOptions" || name.equals("datasourceOverrideOptions")) {this._datasourceOverrideOptions=(java.lang.String)value;return;}
break;
                case 41:

                    if(name == "tablesForPreview" || name.equals("tablesForPreview")) {this._tablesForPreview=(java.util.List)value;return;}
break;
                case 50:

                    if(name == "sessionVariables" || name.equals("sessionVariables")) {this._sessionVariables=(java.util.Map)value;return;}
break;
                case 54:

                    if(name == "datasourceOverrideRelay" || name.equals("datasourceOverrideRelay")) {this._datasourceOverrideRelay=(java.lang.String)value;return;}
break;
                case 55:

                    if(name == "originType" || name.equals("originType")) {this._originType=(java.lang.Byte)value;return;}
break;
                case 66:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return;}
break;
                case 73:

                    if(name == "comment" || name.equals("comment")) {this._comment=(java.lang.String)value;return;}
break;
                case 75:

                    if(name == "limit" || name.equals("limit")) {this._limit=(java.lang.Integer)value;return;}
break;
                case 76:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {this._timeoutMs=(java.lang.Integer)value;return;}
break;
                case 77:

                    if(name == "datasourceOverrideAdapter" || name.equals("datasourceOverrideAdapter")) {this._datasourceOverrideAdapter=(java.lang.String)value;return;}
break;
                case 80:

                    if(name == "disableLogging" || name.equals("disableLogging")) {this._disableLogging=(java.lang.Boolean)value;return;}
break;
                case 81:

                    if(name == "datasourceName" || name.equals("datasourceName")) {this._datasourceName=(java.lang.String)value;return;}
break;
                case 83:

                    if(name == "query" || name.equals("query")) {this._query=(java.lang.String)value;return;}
break;
                case 85:

                    if(name == "datasourceOverridePasswordEnc" || name.equals("datasourceOverridePasswordEnc")) {this._datasourceOverridePasswordEnc=(java.lang.String)value;return;}
break;
                case 86:

                    if(name == "previewCount" || name.equals("previewCount")) {this._previewCount=(java.lang.Integer)value;return;}
break;
                case 87:

                    if(name == "requestTime" || name.equals("requestTime")) {this._requestTime=(java.lang.Long)value;return;}
break;
                case 93:

                    if(name == "parentProcessId" || name.equals("parentProcessId")) {this._parentProcessId=(java.lang.Long)value;return;}
break;
                case 97:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return;}
break;
                case 99:

                    if(name == "isTest" || name.equals("isTest")) {this._isTest=(java.lang.Boolean)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 101;
        switch(h){

                case 6:

                    if(name == "sessionVariableTypes" || name.equals("sessionVariableTypes")) {this._sessionVariableTypes=(java.util.Map)value;return true;}
break;
                case 8:

                    if(name == "type" || name.equals("type")) {this._type=(java.lang.Byte)value;return true;}
break;
                case 12:

                    if(name == "querySessionKeepAlive" || name.equals("querySessionKeepAlive")) {this._querySessionKeepAlive=(java.lang.Boolean)value;return true;}
break;
                case 18:

                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {this._allowSqlInjection=(java.lang.Boolean)value;return true;}
break;
                case 19:

                    if(name == "querySessionId" || name.equals("querySessionId")) {this._querySessionId=(java.lang.Long)value;return true;}
break;
                case 20:

                    if(name == "datasourceOverrideUrl" || name.equals("datasourceOverrideUrl")) {this._datasourceOverrideUrl=(java.lang.String)value;return true;}
break;
                case 22:

                    if(name == "useConcurrency" || name.equals("useConcurrency")) {this._useConcurrency=(java.lang.Boolean)value;return true;}
break;
                case 23:

                    if(name == "datasourceOverrideUsername" || name.equals("datasourceOverrideUsername")) {this._datasourceOverrideUsername=(java.lang.String)value;return true;}
break;
                case 27:

                    if(name == "directives" || name.equals("directives")) {this._directives=(java.util.Map)value;return true;}
break;
                case 28:

                    if(name == "parsedNode" || name.equals("parsedNode")) {this._parsedNode=(com.f1.utils.string.Node)value;return true;}
break;
                case 29:

                    if(name == "permissions" || name.equals("permissions")) {this._permissions=(java.lang.Byte)value;return true;}
break;
                case 34:

                    if(name == "uploadValues" || name.equals("uploadValues")) {this._uploadValues=(java.util.List)value;return true;}
break;
                case 38:

                    if(name == "datasourceOverridePassword" || name.equals("datasourceOverridePassword")) {this._datasourceOverridePassword=(com.f1.base.Password)value;return true;}
break;
                case 40:

                    if(name == "datasourceOverrideOptions" || name.equals("datasourceOverrideOptions")) {this._datasourceOverrideOptions=(java.lang.String)value;return true;}
break;
                case 41:

                    if(name == "tablesForPreview" || name.equals("tablesForPreview")) {this._tablesForPreview=(java.util.List)value;return true;}
break;
                case 50:

                    if(name == "sessionVariables" || name.equals("sessionVariables")) {this._sessionVariables=(java.util.Map)value;return true;}
break;
                case 54:

                    if(name == "datasourceOverrideRelay" || name.equals("datasourceOverrideRelay")) {this._datasourceOverrideRelay=(java.lang.String)value;return true;}
break;
                case 55:

                    if(name == "originType" || name.equals("originType")) {this._originType=(java.lang.Byte)value;return true;}
break;
                case 66:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return true;}
break;
                case 73:

                    if(name == "comment" || name.equals("comment")) {this._comment=(java.lang.String)value;return true;}
break;
                case 75:

                    if(name == "limit" || name.equals("limit")) {this._limit=(java.lang.Integer)value;return true;}
break;
                case 76:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {this._timeoutMs=(java.lang.Integer)value;return true;}
break;
                case 77:

                    if(name == "datasourceOverrideAdapter" || name.equals("datasourceOverrideAdapter")) {this._datasourceOverrideAdapter=(java.lang.String)value;return true;}
break;
                case 80:

                    if(name == "disableLogging" || name.equals("disableLogging")) {this._disableLogging=(java.lang.Boolean)value;return true;}
break;
                case 81:

                    if(name == "datasourceName" || name.equals("datasourceName")) {this._datasourceName=(java.lang.String)value;return true;}
break;
                case 83:

                    if(name == "query" || name.equals("query")) {this._query=(java.lang.String)value;return true;}
break;
                case 85:

                    if(name == "datasourceOverridePasswordEnc" || name.equals("datasourceOverridePasswordEnc")) {this._datasourceOverridePasswordEnc=(java.lang.String)value;return true;}
break;
                case 86:

                    if(name == "previewCount" || name.equals("previewCount")) {this._previewCount=(java.lang.Integer)value;return true;}
break;
                case 87:

                    if(name == "requestTime" || name.equals("requestTime")) {this._requestTime=(java.lang.Long)value;return true;}
break;
                case 93:

                    if(name == "parentProcessId" || name.equals("parentProcessId")) {this._parentProcessId=(java.lang.Long)value;return true;}
break;
                case 97:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return true;}
break;
                case 99:

                    if(name == "isTest" || name.equals("isTest")) {this._isTest=(java.lang.Boolean)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 101;
        switch(h){

                case 6:

		    
                    if(name == "sessionVariableTypes" || name.equals("sessionVariableTypes")) {return this._sessionVariableTypes;}
            
break;
                case 8:

		    
                    if(name == "type" || name.equals("type")) {return OH.valueOf(this._type);}
		    
break;
                case 12:

		    
                    if(name == "querySessionKeepAlive" || name.equals("querySessionKeepAlive")) {return OH.valueOf(this._querySessionKeepAlive);}
		    
break;
                case 18:

		    
                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {return OH.valueOf(this._allowSqlInjection);}
		    
break;
                case 19:

		    
                    if(name == "querySessionId" || name.equals("querySessionId")) {return OH.valueOf(this._querySessionId);}
		    
break;
                case 20:

		    
                    if(name == "datasourceOverrideUrl" || name.equals("datasourceOverrideUrl")) {return this._datasourceOverrideUrl;}
            
break;
                case 22:

		    
                    if(name == "useConcurrency" || name.equals("useConcurrency")) {return OH.valueOf(this._useConcurrency);}
		    
break;
                case 23:

		    
                    if(name == "datasourceOverrideUsername" || name.equals("datasourceOverrideUsername")) {return this._datasourceOverrideUsername;}
            
break;
                case 27:

		    
                    if(name == "directives" || name.equals("directives")) {return this._directives;}
            
break;
                case 28:

		    
                    if(name == "parsedNode" || name.equals("parsedNode")) {return this._parsedNode;}
            
break;
                case 29:

		    
                    if(name == "permissions" || name.equals("permissions")) {return OH.valueOf(this._permissions);}
		    
break;
                case 34:

		    
                    if(name == "uploadValues" || name.equals("uploadValues")) {return this._uploadValues;}
            
break;
                case 38:

		    
                    if(name == "datasourceOverridePassword" || name.equals("datasourceOverridePassword")) {return this._datasourceOverridePassword;}
            
break;
                case 40:

		    
                    if(name == "datasourceOverrideOptions" || name.equals("datasourceOverrideOptions")) {return this._datasourceOverrideOptions;}
            
break;
                case 41:

		    
                    if(name == "tablesForPreview" || name.equals("tablesForPreview")) {return this._tablesForPreview;}
            
break;
                case 50:

		    
                    if(name == "sessionVariables" || name.equals("sessionVariables")) {return this._sessionVariables;}
            
break;
                case 54:

		    
                    if(name == "datasourceOverrideRelay" || name.equals("datasourceOverrideRelay")) {return this._datasourceOverrideRelay;}
            
break;
                case 55:

		    
                    if(name == "originType" || name.equals("originType")) {return OH.valueOf(this._originType);}
		    
break;
                case 66:

		    
                    if(name == "priority" || name.equals("priority")) {return OH.valueOf(this._priority);}
		    
break;
                case 73:

		    
                    if(name == "comment" || name.equals("comment")) {return this._comment;}
            
break;
                case 75:

		    
                    if(name == "limit" || name.equals("limit")) {return OH.valueOf(this._limit);}
		    
break;
                case 76:

		    
                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return OH.valueOf(this._timeoutMs);}
		    
break;
                case 77:

		    
                    if(name == "datasourceOverrideAdapter" || name.equals("datasourceOverrideAdapter")) {return this._datasourceOverrideAdapter;}
            
break;
                case 80:

		    
                    if(name == "disableLogging" || name.equals("disableLogging")) {return OH.valueOf(this._disableLogging);}
		    
break;
                case 81:

		    
                    if(name == "datasourceName" || name.equals("datasourceName")) {return this._datasourceName;}
            
break;
                case 83:

		    
                    if(name == "query" || name.equals("query")) {return this._query;}
            
break;
                case 85:

		    
                    if(name == "datasourceOverridePasswordEnc" || name.equals("datasourceOverridePasswordEnc")) {return this._datasourceOverridePasswordEnc;}
            
break;
                case 86:

		    
                    if(name == "previewCount" || name.equals("previewCount")) {return OH.valueOf(this._previewCount);}
		    
break;
                case 87:

		    
                    if(name == "requestTime" || name.equals("requestTime")) {return OH.valueOf(this._requestTime);}
		    
break;
                case 93:

		    
                    if(name == "parentProcessId" || name.equals("parentProcessId")) {return OH.valueOf(this._parentProcessId);}
		    
break;
                case 97:

		    
                    if(name == "invokedBy" || name.equals("invokedBy")) {return this._invokedBy;}
            
break;
                case 99:

		    
                    if(name == "isTest" || name.equals("isTest")) {return OH.valueOf(this._isTest);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 101;
        switch(h){

                case 6:

                    if(name == "sessionVariableTypes" || name.equals("sessionVariableTypes")) {return java.util.Map.class;}
break;
                case 8:

                    if(name == "type" || name.equals("type")) {return byte.class;}
break;
                case 12:

                    if(name == "querySessionKeepAlive" || name.equals("querySessionKeepAlive")) {return boolean.class;}
break;
                case 18:

                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {return boolean.class;}
break;
                case 19:

                    if(name == "querySessionId" || name.equals("querySessionId")) {return long.class;}
break;
                case 20:

                    if(name == "datasourceOverrideUrl" || name.equals("datasourceOverrideUrl")) {return java.lang.String.class;}
break;
                case 22:

                    if(name == "useConcurrency" || name.equals("useConcurrency")) {return boolean.class;}
break;
                case 23:

                    if(name == "datasourceOverrideUsername" || name.equals("datasourceOverrideUsername")) {return java.lang.String.class;}
break;
                case 27:

                    if(name == "directives" || name.equals("directives")) {return java.util.Map.class;}
break;
                case 28:

                    if(name == "parsedNode" || name.equals("parsedNode")) {return com.f1.utils.string.Node.class;}
break;
                case 29:

                    if(name == "permissions" || name.equals("permissions")) {return byte.class;}
break;
                case 34:

                    if(name == "uploadValues" || name.equals("uploadValues")) {return java.util.List.class;}
break;
                case 38:

                    if(name == "datasourceOverridePassword" || name.equals("datasourceOverridePassword")) {return com.f1.base.Password.class;}
break;
                case 40:

                    if(name == "datasourceOverrideOptions" || name.equals("datasourceOverrideOptions")) {return java.lang.String.class;}
break;
                case 41:

                    if(name == "tablesForPreview" || name.equals("tablesForPreview")) {return java.util.List.class;}
break;
                case 50:

                    if(name == "sessionVariables" || name.equals("sessionVariables")) {return java.util.Map.class;}
break;
                case 54:

                    if(name == "datasourceOverrideRelay" || name.equals("datasourceOverrideRelay")) {return java.lang.String.class;}
break;
                case 55:

                    if(name == "originType" || name.equals("originType")) {return byte.class;}
break;
                case 66:

                    if(name == "priority" || name.equals("priority")) {return int.class;}
break;
                case 73:

                    if(name == "comment" || name.equals("comment")) {return java.lang.String.class;}
break;
                case 75:

                    if(name == "limit" || name.equals("limit")) {return int.class;}
break;
                case 76:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return int.class;}
break;
                case 77:

                    if(name == "datasourceOverrideAdapter" || name.equals("datasourceOverrideAdapter")) {return java.lang.String.class;}
break;
                case 80:

                    if(name == "disableLogging" || name.equals("disableLogging")) {return boolean.class;}
break;
                case 81:

                    if(name == "datasourceName" || name.equals("datasourceName")) {return java.lang.String.class;}
break;
                case 83:

                    if(name == "query" || name.equals("query")) {return java.lang.String.class;}
break;
                case 85:

                    if(name == "datasourceOverridePasswordEnc" || name.equals("datasourceOverridePasswordEnc")) {return java.lang.String.class;}
break;
                case 86:

                    if(name == "previewCount" || name.equals("previewCount")) {return int.class;}
break;
                case 87:

                    if(name == "requestTime" || name.equals("requestTime")) {return long.class;}
break;
                case 93:

                    if(name == "parentProcessId" || name.equals("parentProcessId")) {return long.class;}
break;
                case 97:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return java.lang.String.class;}
break;
                case 99:

                    if(name == "isTest" || name.equals("isTest")) {return boolean.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 101;
        switch(h){

                case 6:

                    if(name == "sessionVariableTypes" || name.equals("sessionVariableTypes")) {return VALUED_PARAM_sessionVariableTypes;}
break;
                case 8:

                    if(name == "type" || name.equals("type")) {return VALUED_PARAM_type;}
break;
                case 12:

                    if(name == "querySessionKeepAlive" || name.equals("querySessionKeepAlive")) {return VALUED_PARAM_querySessionKeepAlive;}
break;
                case 18:

                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {return VALUED_PARAM_allowSqlInjection;}
break;
                case 19:

                    if(name == "querySessionId" || name.equals("querySessionId")) {return VALUED_PARAM_querySessionId;}
break;
                case 20:

                    if(name == "datasourceOverrideUrl" || name.equals("datasourceOverrideUrl")) {return VALUED_PARAM_datasourceOverrideUrl;}
break;
                case 22:

                    if(name == "useConcurrency" || name.equals("useConcurrency")) {return VALUED_PARAM_useConcurrency;}
break;
                case 23:

                    if(name == "datasourceOverrideUsername" || name.equals("datasourceOverrideUsername")) {return VALUED_PARAM_datasourceOverrideUsername;}
break;
                case 27:

                    if(name == "directives" || name.equals("directives")) {return VALUED_PARAM_directives;}
break;
                case 28:

                    if(name == "parsedNode" || name.equals("parsedNode")) {return VALUED_PARAM_parsedNode;}
break;
                case 29:

                    if(name == "permissions" || name.equals("permissions")) {return VALUED_PARAM_permissions;}
break;
                case 34:

                    if(name == "uploadValues" || name.equals("uploadValues")) {return VALUED_PARAM_uploadValues;}
break;
                case 38:

                    if(name == "datasourceOverridePassword" || name.equals("datasourceOverridePassword")) {return VALUED_PARAM_datasourceOverridePassword;}
break;
                case 40:

                    if(name == "datasourceOverrideOptions" || name.equals("datasourceOverrideOptions")) {return VALUED_PARAM_datasourceOverrideOptions;}
break;
                case 41:

                    if(name == "tablesForPreview" || name.equals("tablesForPreview")) {return VALUED_PARAM_tablesForPreview;}
break;
                case 50:

                    if(name == "sessionVariables" || name.equals("sessionVariables")) {return VALUED_PARAM_sessionVariables;}
break;
                case 54:

                    if(name == "datasourceOverrideRelay" || name.equals("datasourceOverrideRelay")) {return VALUED_PARAM_datasourceOverrideRelay;}
break;
                case 55:

                    if(name == "originType" || name.equals("originType")) {return VALUED_PARAM_originType;}
break;
                case 66:

                    if(name == "priority" || name.equals("priority")) {return VALUED_PARAM_priority;}
break;
                case 73:

                    if(name == "comment" || name.equals("comment")) {return VALUED_PARAM_comment;}
break;
                case 75:

                    if(name == "limit" || name.equals("limit")) {return VALUED_PARAM_limit;}
break;
                case 76:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return VALUED_PARAM_timeoutMs;}
break;
                case 77:

                    if(name == "datasourceOverrideAdapter" || name.equals("datasourceOverrideAdapter")) {return VALUED_PARAM_datasourceOverrideAdapter;}
break;
                case 80:

                    if(name == "disableLogging" || name.equals("disableLogging")) {return VALUED_PARAM_disableLogging;}
break;
                case 81:

                    if(name == "datasourceName" || name.equals("datasourceName")) {return VALUED_PARAM_datasourceName;}
break;
                case 83:

                    if(name == "query" || name.equals("query")) {return VALUED_PARAM_query;}
break;
                case 85:

                    if(name == "datasourceOverridePasswordEnc" || name.equals("datasourceOverridePasswordEnc")) {return VALUED_PARAM_datasourceOverridePasswordEnc;}
break;
                case 86:

                    if(name == "previewCount" || name.equals("previewCount")) {return VALUED_PARAM_previewCount;}
break;
                case 87:

                    if(name == "requestTime" || name.equals("requestTime")) {return VALUED_PARAM_requestTime;}
break;
                case 93:

                    if(name == "parentProcessId" || name.equals("parentProcessId")) {return VALUED_PARAM_parentProcessId;}
break;
                case 97:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return VALUED_PARAM_invokedBy;}
break;
                case 99:

                    if(name == "isTest" || name.equals("isTest")) {return VALUED_PARAM_isTest;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 101;
        switch(h){

                case 6:

                    if(name == "sessionVariableTypes" || name.equals("sessionVariableTypes")) {return 25;}
break;
                case 8:

                    if(name == "type" || name.equals("type")) {return 29;}
break;
                case 12:

                    if(name == "querySessionKeepAlive" || name.equals("querySessionKeepAlive")) {return 23;}
break;
                case 18:

                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {return 0;}
break;
                case 19:

                    if(name == "querySessionId" || name.equals("querySessionId")) {return 22;}
break;
                case 20:

                    if(name == "datasourceOverrideUrl" || name.equals("datasourceOverrideUrl")) {return 8;}
break;
                case 22:

                    if(name == "useConcurrency" || name.equals("useConcurrency")) {return 31;}
break;
                case 23:

                    if(name == "datasourceOverrideUsername" || name.equals("datasourceOverrideUsername")) {return 9;}
break;
                case 27:

                    if(name == "directives" || name.equals("directives")) {return 10;}
break;
                case 28:

                    if(name == "parsedNode" || name.equals("parsedNode")) {return 17;}
break;
                case 29:

                    if(name == "permissions" || name.equals("permissions")) {return 18;}
break;
                case 34:

                    if(name == "uploadValues" || name.equals("uploadValues")) {return 30;}
break;
                case 38:

                    if(name == "datasourceOverridePassword" || name.equals("datasourceOverridePassword")) {return 5;}
break;
                case 40:

                    if(name == "datasourceOverrideOptions" || name.equals("datasourceOverrideOptions")) {return 4;}
break;
                case 41:

                    if(name == "tablesForPreview" || name.equals("tablesForPreview")) {return 27;}
break;
                case 50:

                    if(name == "sessionVariables" || name.equals("sessionVariables")) {return 26;}
break;
                case 54:

                    if(name == "datasourceOverrideRelay" || name.equals("datasourceOverrideRelay")) {return 7;}
break;
                case 55:

                    if(name == "originType" || name.equals("originType")) {return 15;}
break;
                case 66:

                    if(name == "priority" || name.equals("priority")) {return 20;}
break;
                case 73:

                    if(name == "comment" || name.equals("comment")) {return 1;}
break;
                case 75:

                    if(name == "limit" || name.equals("limit")) {return 14;}
break;
                case 76:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return 28;}
break;
                case 77:

                    if(name == "datasourceOverrideAdapter" || name.equals("datasourceOverrideAdapter")) {return 3;}
break;
                case 80:

                    if(name == "disableLogging" || name.equals("disableLogging")) {return 11;}
break;
                case 81:

                    if(name == "datasourceName" || name.equals("datasourceName")) {return 2;}
break;
                case 83:

                    if(name == "query" || name.equals("query")) {return 21;}
break;
                case 85:

                    if(name == "datasourceOverridePasswordEnc" || name.equals("datasourceOverridePasswordEnc")) {return 6;}
break;
                case 86:

                    if(name == "previewCount" || name.equals("previewCount")) {return 19;}
break;
                case 87:

                    if(name == "requestTime" || name.equals("requestTime")) {return 24;}
break;
                case 93:

                    if(name == "parentProcessId" || name.equals("parentProcessId")) {return 16;}
break;
                case 97:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 12;}
break;
                case 99:

                    if(name == "isTest" || name.equals("isTest")) {return 13;}
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
        return 32;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiCenterQueryDsRequest0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 101;
        switch(h){

                case 6:

                    if(name == "sessionVariableTypes" || name.equals("sessionVariableTypes")) {return true;}
break;
                case 8:

                    if(name == "type" || name.equals("type")) {return true;}
break;
                case 12:

                    if(name == "querySessionKeepAlive" || name.equals("querySessionKeepAlive")) {return true;}
break;
                case 18:

                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {return true;}
break;
                case 19:

                    if(name == "querySessionId" || name.equals("querySessionId")) {return true;}
break;
                case 20:

                    if(name == "datasourceOverrideUrl" || name.equals("datasourceOverrideUrl")) {return true;}
break;
                case 22:

                    if(name == "useConcurrency" || name.equals("useConcurrency")) {return true;}
break;
                case 23:

                    if(name == "datasourceOverrideUsername" || name.equals("datasourceOverrideUsername")) {return true;}
break;
                case 27:

                    if(name == "directives" || name.equals("directives")) {return true;}
break;
                case 28:

                    if(name == "parsedNode" || name.equals("parsedNode")) {return true;}
break;
                case 29:

                    if(name == "permissions" || name.equals("permissions")) {return true;}
break;
                case 34:

                    if(name == "uploadValues" || name.equals("uploadValues")) {return true;}
break;
                case 38:

                    if(name == "datasourceOverridePassword" || name.equals("datasourceOverridePassword")) {return true;}
break;
                case 40:

                    if(name == "datasourceOverrideOptions" || name.equals("datasourceOverrideOptions")) {return true;}
break;
                case 41:

                    if(name == "tablesForPreview" || name.equals("tablesForPreview")) {return true;}
break;
                case 50:

                    if(name == "sessionVariables" || name.equals("sessionVariables")) {return true;}
break;
                case 54:

                    if(name == "datasourceOverrideRelay" || name.equals("datasourceOverrideRelay")) {return true;}
break;
                case 55:

                    if(name == "originType" || name.equals("originType")) {return true;}
break;
                case 66:

                    if(name == "priority" || name.equals("priority")) {return true;}
break;
                case 73:

                    if(name == "comment" || name.equals("comment")) {return true;}
break;
                case 75:

                    if(name == "limit" || name.equals("limit")) {return true;}
break;
                case 76:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return true;}
break;
                case 77:

                    if(name == "datasourceOverrideAdapter" || name.equals("datasourceOverrideAdapter")) {return true;}
break;
                case 80:

                    if(name == "disableLogging" || name.equals("disableLogging")) {return true;}
break;
                case 81:

                    if(name == "datasourceName" || name.equals("datasourceName")) {return true;}
break;
                case 83:

                    if(name == "query" || name.equals("query")) {return true;}
break;
                case 85:

                    if(name == "datasourceOverridePasswordEnc" || name.equals("datasourceOverridePasswordEnc")) {return true;}
break;
                case 86:

                    if(name == "previewCount" || name.equals("previewCount")) {return true;}
break;
                case 87:

                    if(name == "requestTime" || name.equals("requestTime")) {return true;}
break;
                case 93:

                    if(name == "parentProcessId" || name.equals("parentProcessId")) {return true;}
break;
                case 97:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return true;}
break;
                case 99:

                    if(name == "isTest" || name.equals("isTest")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 101;
        switch(h){

                case 6:

                    if(name == "sessionVariableTypes" || name.equals("sessionVariableTypes")) {return 23;}
break;
                case 8:

                    if(name == "type" || name.equals("type")) {return 1;}
break;
                case 12:

                    if(name == "querySessionKeepAlive" || name.equals("querySessionKeepAlive")) {return 0;}
break;
                case 18:

                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {return 0;}
break;
                case 19:

                    if(name == "querySessionId" || name.equals("querySessionId")) {return 6;}
break;
                case 20:

                    if(name == "datasourceOverrideUrl" || name.equals("datasourceOverrideUrl")) {return 20;}
break;
                case 22:

                    if(name == "useConcurrency" || name.equals("useConcurrency")) {return 0;}
break;
                case 23:

                    if(name == "datasourceOverrideUsername" || name.equals("datasourceOverrideUsername")) {return 20;}
break;
                case 27:

                    if(name == "directives" || name.equals("directives")) {return 23;}
break;
                case 28:

                    if(name == "parsedNode" || name.equals("parsedNode")) {return 60;}
break;
                case 29:

                    if(name == "permissions" || name.equals("permissions")) {return 1;}
break;
                case 34:

                    if(name == "uploadValues" || name.equals("uploadValues")) {return 21;}
break;
                case 38:

                    if(name == "datasourceOverridePassword" || name.equals("datasourceOverridePassword")) {return 24;}
break;
                case 40:

                    if(name == "datasourceOverrideOptions" || name.equals("datasourceOverrideOptions")) {return 20;}
break;
                case 41:

                    if(name == "tablesForPreview" || name.equals("tablesForPreview")) {return 21;}
break;
                case 50:

                    if(name == "sessionVariables" || name.equals("sessionVariables")) {return 23;}
break;
                case 54:

                    if(name == "datasourceOverrideRelay" || name.equals("datasourceOverrideRelay")) {return 20;}
break;
                case 55:

                    if(name == "originType" || name.equals("originType")) {return 1;}
break;
                case 66:

                    if(name == "priority" || name.equals("priority")) {return 4;}
break;
                case 73:

                    if(name == "comment" || name.equals("comment")) {return 20;}
break;
                case 75:

                    if(name == "limit" || name.equals("limit")) {return 4;}
break;
                case 76:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return 4;}
break;
                case 77:

                    if(name == "datasourceOverrideAdapter" || name.equals("datasourceOverrideAdapter")) {return 20;}
break;
                case 80:

                    if(name == "disableLogging" || name.equals("disableLogging")) {return 0;}
break;
                case 81:

                    if(name == "datasourceName" || name.equals("datasourceName")) {return 20;}
break;
                case 83:

                    if(name == "query" || name.equals("query")) {return 20;}
break;
                case 85:

                    if(name == "datasourceOverridePasswordEnc" || name.equals("datasourceOverridePasswordEnc")) {return 20;}
break;
                case 86:

                    if(name == "previewCount" || name.equals("previewCount")) {return 4;}
break;
                case 87:

                    if(name == "requestTime" || name.equals("requestTime")) {return 6;}
break;
                case 93:

                    if(name == "parentProcessId" || name.equals("parentProcessId")) {return 6;}
break;
                case 97:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 20;}
break;
                case 99:

                    if(name == "isTest" || name.equals("isTest")) {return 0;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _allowSqlInjection;

        case 1:return _comment;

        case 2:return _datasourceName;

        case 3:return _datasourceOverrideAdapter;

        case 4:return _datasourceOverrideOptions;

        case 5:return _datasourceOverridePassword;

        case 6:return _datasourceOverridePasswordEnc;

        case 7:return _datasourceOverrideRelay;

        case 8:return _datasourceOverrideUrl;

        case 9:return _datasourceOverrideUsername;

        case 10:return _directives;

        case 11:return _disableLogging;

        case 12:return _invokedBy;

        case 13:return _isTest;

        case 14:return _limit;

        case 15:return _originType;

        case 16:return _parentProcessId;

        case 17:return _parsedNode;

        case 18:return _permissions;

        case 19:return _previewCount;

        case 20:return _priority;

        case 21:return _query;

        case 22:return _querySessionId;

        case 23:return _querySessionKeepAlive;

        case 24:return _requestTime;

        case 25:return _sessionVariableTypes;

        case 26:return _sessionVariables;

        case 27:return _tablesForPreview;

        case 28:return _timeoutMs;

        case 29:return _type;

        case 30:return _uploadValues;

        case 31:return _useConcurrency;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 32);
    }

    public boolean getAllowSqlInjection(){
        return this._allowSqlInjection;
    }
    public void setAllowSqlInjection(boolean _allowSqlInjection){
    
        this._allowSqlInjection=_allowSqlInjection;
    }

    public java.lang.String getComment(){
        return this._comment;
    }
    public void setComment(java.lang.String _comment){
    
        this._comment=_comment;
    }

    public java.lang.String getDatasourceName(){
        return this._datasourceName;
    }
    public void setDatasourceName(java.lang.String _datasourceName){
    
        this._datasourceName=_datasourceName;
    }

    public java.lang.String getDatasourceOverrideAdapter(){
        return this._datasourceOverrideAdapter;
    }
    public void setDatasourceOverrideAdapter(java.lang.String _datasourceOverrideAdapter){
    
        this._datasourceOverrideAdapter=_datasourceOverrideAdapter;
    }

    public java.lang.String getDatasourceOverrideOptions(){
        return this._datasourceOverrideOptions;
    }
    public void setDatasourceOverrideOptions(java.lang.String _datasourceOverrideOptions){
    
        this._datasourceOverrideOptions=_datasourceOverrideOptions;
    }

    public com.f1.base.Password getDatasourceOverridePassword(){
        return this._datasourceOverridePassword;
    }
    public void setDatasourceOverridePassword(com.f1.base.Password _datasourceOverridePassword){
    
        this._datasourceOverridePassword=_datasourceOverridePassword;
    }

    public java.lang.String getDatasourceOverridePasswordEnc(){
        return this._datasourceOverridePasswordEnc;
    }
    public void setDatasourceOverridePasswordEnc(java.lang.String _datasourceOverridePasswordEnc){
    
        this._datasourceOverridePasswordEnc=_datasourceOverridePasswordEnc;
    }

    public java.lang.String getDatasourceOverrideRelay(){
        return this._datasourceOverrideRelay;
    }
    public void setDatasourceOverrideRelay(java.lang.String _datasourceOverrideRelay){
    
        this._datasourceOverrideRelay=_datasourceOverrideRelay;
    }

    public java.lang.String getDatasourceOverrideUrl(){
        return this._datasourceOverrideUrl;
    }
    public void setDatasourceOverrideUrl(java.lang.String _datasourceOverrideUrl){
    
        this._datasourceOverrideUrl=_datasourceOverrideUrl;
    }

    public java.lang.String getDatasourceOverrideUsername(){
        return this._datasourceOverrideUsername;
    }
    public void setDatasourceOverrideUsername(java.lang.String _datasourceOverrideUsername){
    
        this._datasourceOverrideUsername=_datasourceOverrideUsername;
    }

    public java.util.Map getDirectives(){
        return this._directives;
    }
    public void setDirectives(java.util.Map _directives){
    
        this._directives=_directives;
    }

    public boolean getDisableLogging(){
        return this._disableLogging;
    }
    public void setDisableLogging(boolean _disableLogging){
    
        this._disableLogging=_disableLogging;
    }

    public java.lang.String getInvokedBy(){
        return this._invokedBy;
    }
    public void setInvokedBy(java.lang.String _invokedBy){
    
        this._invokedBy=_invokedBy;
    }

    public boolean getIsTest(){
        return this._isTest;
    }
    public void setIsTest(boolean _isTest){
    
        this._isTest=_isTest;
    }

    public int getLimit(){
        return this._limit;
    }
    public void setLimit(int _limit){
    
        this._limit=_limit;
    }

    public byte getOriginType(){
        return this._originType;
    }
    public void setOriginType(byte _originType){
    
        this._originType=_originType;
    }

    public long getParentProcessId(){
        return this._parentProcessId;
    }
    public void setParentProcessId(long _parentProcessId){
    
        this._parentProcessId=_parentProcessId;
    }

    public com.f1.utils.string.Node getParsedNode(){
        return this._parsedNode;
    }
    public void setParsedNode(com.f1.utils.string.Node _parsedNode){
    
        this._parsedNode=_parsedNode;
    }

    public byte getPermissions(){
        return this._permissions;
    }
    public void setPermissions(byte _permissions){
    
        this._permissions=_permissions;
    }

    public int getPreviewCount(){
        return this._previewCount;
    }
    public void setPreviewCount(int _previewCount){
    
        this._previewCount=_previewCount;
    }

    public int getPriority(){
        return this._priority;
    }
    public void setPriority(int _priority){
    
        this._priority=_priority;
    }

    public java.lang.String getQuery(){
        return this._query;
    }
    public void setQuery(java.lang.String _query){
    
        this._query=_query;
    }

    public long getQuerySessionId(){
        return this._querySessionId;
    }
    public void setQuerySessionId(long _querySessionId){
    
        this._querySessionId=_querySessionId;
    }

    public boolean getQuerySessionKeepAlive(){
        return this._querySessionKeepAlive;
    }
    public void setQuerySessionKeepAlive(boolean _querySessionKeepAlive){
    
        this._querySessionKeepAlive=_querySessionKeepAlive;
    }

    public long getRequestTime(){
        return this._requestTime;
    }
    public void setRequestTime(long _requestTime){
    
        this._requestTime=_requestTime;
    }

    public java.util.Map getSessionVariableTypes(){
        return this._sessionVariableTypes;
    }
    public void setSessionVariableTypes(java.util.Map _sessionVariableTypes){
    
        this._sessionVariableTypes=_sessionVariableTypes;
    }

    public java.util.Map getSessionVariables(){
        return this._sessionVariables;
    }
    public void setSessionVariables(java.util.Map _sessionVariables){
    
        this._sessionVariables=_sessionVariables;
    }

    public java.util.List getTablesForPreview(){
        return this._tablesForPreview;
    }
    public void setTablesForPreview(java.util.List _tablesForPreview){
    
        this._tablesForPreview=_tablesForPreview;
    }

    public int getTimeoutMs(){
        return this._timeoutMs;
    }
    public void setTimeoutMs(int _timeoutMs){
    
        this._timeoutMs=_timeoutMs;
    }

    public byte getType(){
        return this._type;
    }
    public void setType(byte _type){
    
        this._type=_type;
    }

    public java.util.List getUploadValues(){
        return this._uploadValues;
    }
    public void setUploadValues(java.util.List _uploadValues){
    
        this._uploadValues=_uploadValues;
    }

    public boolean getUseConcurrency(){
        return this._useConcurrency;
    }
    public void setUseConcurrency(boolean _useConcurrency){
    
        this._useConcurrency=_useConcurrency;
    }





  
    private static final class VALUED_PARAM_CLASS_allowSqlInjection extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getAllowSqlInjection());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setAllowSqlInjection(stream.readBoolean());
		    
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
		    return 0;
	    }
    
	    @Override
	    public byte getPid() {
            return 29;
	    }
    
	    @Override
	    public String getName() {
            return "allowSqlInjection";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (boolean)((AmiCenterQueryDsRequest0)valued).getAllowSqlInjection();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setAllowSqlInjection((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setAllowSqlInjection(source.getAllowSqlInjection());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getAllowSqlInjection(),source.getAllowSqlInjection());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterQueryDsRequest0 valued) {
		    return valued.getAllowSqlInjection();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterQueryDsRequest0 valued, boolean value) {
		    valued.setAllowSqlInjection(value);
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAllowSqlInjection());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAllowSqlInjection());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean allowSqlInjection";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setAllowSqlInjection(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_allowSqlInjection=new VALUED_PARAM_CLASS_allowSqlInjection();
  

  
    private static final class VALUED_PARAM_CLASS_comment extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 52;
	    }
    
	    @Override
	    public String getName() {
            return "comment";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsRequest0)valued).getComment();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setComment((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setComment(source.getComment());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getComment(),source.getComment());
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getComment(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getComment(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String comment";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setComment(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_comment=new VALUED_PARAM_CLASS_comment();
  

  
    private static final class VALUED_PARAM_CLASS_datasourceName extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 11;
	    }
    
	    @Override
	    public String getName() {
            return "datasourceName";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsRequest0)valued).getDatasourceName();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setDatasourceName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setDatasourceName(source.getDatasourceName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getDatasourceName(),source.getDatasourceName());
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getDatasourceName(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getDatasourceName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String datasourceName";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setDatasourceName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_datasourceName=new VALUED_PARAM_CLASS_datasourceName();
  

  
    private static final class VALUED_PARAM_CLASS_datasourceOverrideAdapter extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 3;
	    }
    
	    @Override
	    public byte getPid() {
            return 27;
	    }
    
	    @Override
	    public String getName() {
            return "datasourceOverrideAdapter";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsRequest0)valued).getDatasourceOverrideAdapter();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setDatasourceOverrideAdapter((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setDatasourceOverrideAdapter(source.getDatasourceOverrideAdapter());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getDatasourceOverrideAdapter(),source.getDatasourceOverrideAdapter());
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getDatasourceOverrideAdapter(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getDatasourceOverrideAdapter(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String datasourceOverrideAdapter";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setDatasourceOverrideAdapter(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_datasourceOverrideAdapter=new VALUED_PARAM_CLASS_datasourceOverrideAdapter();
  

  
    private static final class VALUED_PARAM_CLASS_datasourceOverrideOptions extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 25;
	    }
    
	    @Override
	    public String getName() {
            return "datasourceOverrideOptions";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsRequest0)valued).getDatasourceOverrideOptions();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setDatasourceOverrideOptions((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setDatasourceOverrideOptions(source.getDatasourceOverrideOptions());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getDatasourceOverrideOptions(),source.getDatasourceOverrideOptions());
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getDatasourceOverrideOptions(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getDatasourceOverrideOptions(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String datasourceOverrideOptions";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setDatasourceOverrideOptions(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_datasourceOverrideOptions=new VALUED_PARAM_CLASS_datasourceOverrideOptions();
  

  
    private static final class VALUED_PARAM_CLASS_datasourceOverridePassword extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 24;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.base.Password}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.base.Password}");
		    
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
		    return 5;
	    }
    
	    @Override
	    public byte getPid() {
            return 23;
	    }
    
	    @Override
	    public String getName() {
            return "datasourceOverridePassword";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (com.f1.base.Password)((AmiCenterQueryDsRequest0)valued).getDatasourceOverridePassword();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setDatasourceOverridePassword((com.f1.base.Password)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setDatasourceOverridePassword(source.getDatasourceOverridePassword());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getDatasourceOverridePassword(),source.getDatasourceOverridePassword());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.base.Password.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.base.Password.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getDatasourceOverridePassword());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getDatasourceOverridePassword());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.base.Password datasourceOverridePassword";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setDatasourceOverridePassword(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_datasourceOverridePassword=new VALUED_PARAM_CLASS_datasourceOverridePassword();
  

  
    private static final class VALUED_PARAM_CLASS_datasourceOverridePasswordEnc extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 24;
	    }
    
	    @Override
	    public String getName() {
            return "datasourceOverridePasswordEnc";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsRequest0)valued).getDatasourceOverridePasswordEnc();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setDatasourceOverridePasswordEnc((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setDatasourceOverridePasswordEnc(source.getDatasourceOverridePasswordEnc());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getDatasourceOverridePasswordEnc(),source.getDatasourceOverridePasswordEnc());
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getDatasourceOverridePasswordEnc(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getDatasourceOverridePasswordEnc(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String datasourceOverridePasswordEnc";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setDatasourceOverridePasswordEnc(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_datasourceOverridePasswordEnc=new VALUED_PARAM_CLASS_datasourceOverridePasswordEnc();
  

  
    private static final class VALUED_PARAM_CLASS_datasourceOverrideRelay extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 26;
	    }
    
	    @Override
	    public String getName() {
            return "datasourceOverrideRelay";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsRequest0)valued).getDatasourceOverrideRelay();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setDatasourceOverrideRelay((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setDatasourceOverrideRelay(source.getDatasourceOverrideRelay());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getDatasourceOverrideRelay(),source.getDatasourceOverrideRelay());
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getDatasourceOverrideRelay(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getDatasourceOverrideRelay(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String datasourceOverrideRelay";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setDatasourceOverrideRelay(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_datasourceOverrideRelay=new VALUED_PARAM_CLASS_datasourceOverrideRelay();
  

  
    private static final class VALUED_PARAM_CLASS_datasourceOverrideUrl extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 21;
	    }
    
	    @Override
	    public String getName() {
            return "datasourceOverrideUrl";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsRequest0)valued).getDatasourceOverrideUrl();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setDatasourceOverrideUrl((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setDatasourceOverrideUrl(source.getDatasourceOverrideUrl());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getDatasourceOverrideUrl(),source.getDatasourceOverrideUrl());
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getDatasourceOverrideUrl(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getDatasourceOverrideUrl(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String datasourceOverrideUrl";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setDatasourceOverrideUrl(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_datasourceOverrideUrl=new VALUED_PARAM_CLASS_datasourceOverrideUrl();
  

  
    private static final class VALUED_PARAM_CLASS_datasourceOverrideUsername extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 22;
	    }
    
	    @Override
	    public String getName() {
            return "datasourceOverrideUsername";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsRequest0)valued).getDatasourceOverrideUsername();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setDatasourceOverrideUsername((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setDatasourceOverrideUsername(source.getDatasourceOverrideUsername());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getDatasourceOverrideUsername(),source.getDatasourceOverrideUsername());
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getDatasourceOverrideUsername(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getDatasourceOverrideUsername(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String datasourceOverrideUsername";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setDatasourceOverrideUsername(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_datasourceOverrideUsername=new VALUED_PARAM_CLASS_datasourceOverrideUsername();
  

  
    private static final class VALUED_PARAM_CLASS_directives extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 23;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Map}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 10;
	    }
    
	    @Override
	    public byte getPid() {
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "directives";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (java.util.Map)((AmiCenterQueryDsRequest0)valued).getDirectives();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setDirectives((java.util.Map)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setDirectives(source.getDirectives());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getDirectives(),source.getDirectives());
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getDirectives());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getDirectives());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.Map directives";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setDirectives(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_directives=new VALUED_PARAM_CLASS_directives();
  

  
    private static final class VALUED_PARAM_CLASS_disableLogging extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getDisableLogging());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setDisableLogging(stream.readBoolean());
		    
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
            return 33;
	    }
    
	    @Override
	    public String getName() {
            return "disableLogging";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (boolean)((AmiCenterQueryDsRequest0)valued).getDisableLogging();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setDisableLogging((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setDisableLogging(source.getDisableLogging());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getDisableLogging(),source.getDisableLogging());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterQueryDsRequest0 valued) {
		    return valued.getDisableLogging();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterQueryDsRequest0 valued, boolean value) {
		    valued.setDisableLogging(value);
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getDisableLogging());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getDisableLogging());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean disableLogging";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setDisableLogging(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_disableLogging=new VALUED_PARAM_CLASS_disableLogging();
  

  
    private static final class VALUED_PARAM_CLASS_invokedBy extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 12;
	    }
    
	    @Override
	    public byte getPid() {
            return 51;
	    }
    
	    @Override
	    public String getName() {
            return "invokedBy";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsRequest0)valued).getInvokedBy();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setInvokedBy((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setInvokedBy(source.getInvokedBy());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String invokedBy";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setInvokedBy(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_invokedBy=new VALUED_PARAM_CLASS_invokedBy();
  

  
    private static final class VALUED_PARAM_CLASS_isTest extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getIsTest());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setIsTest(stream.readBoolean());
		    
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
            return 8;
	    }
    
	    @Override
	    public String getName() {
            return "isTest";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (boolean)((AmiCenterQueryDsRequest0)valued).getIsTest();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setIsTest((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setIsTest(source.getIsTest());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getIsTest(),source.getIsTest());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterQueryDsRequest0 valued) {
		    return valued.getIsTest();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterQueryDsRequest0 valued, boolean value) {
		    valued.setIsTest(value);
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getIsTest());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getIsTest());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean isTest";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setIsTest(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_isTest=new VALUED_PARAM_CLASS_isTest();
  

  
    private static final class VALUED_PARAM_CLASS_limit extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getLimit());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setLimit(stream.readInt());
		    
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
		    return 14;
	    }
    
	    @Override
	    public byte getPid() {
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "limit";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (int)((AmiCenterQueryDsRequest0)valued).getLimit();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setLimit((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setLimit(source.getLimit());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getLimit(),source.getLimit());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterQueryDsRequest0 valued) {
		    return valued.getLimit();
	    }
    
	    @Override
	    public void setInt(AmiCenterQueryDsRequest0 valued, int value) {
		    valued.setLimit(value);
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getLimit());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getLimit());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int limit";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setLimit(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_limit=new VALUED_PARAM_CLASS_limit();
  

  
    private static final class VALUED_PARAM_CLASS_originType extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getOriginType());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setOriginType(stream.readByte());
		    
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
		    return 15;
	    }
    
	    @Override
	    public byte getPid() {
            return 20;
	    }
    
	    @Override
	    public String getName() {
            return "originType";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (byte)((AmiCenterQueryDsRequest0)valued).getOriginType();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setOriginType((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setOriginType(source.getOriginType());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getOriginType(),source.getOriginType());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiCenterQueryDsRequest0 valued) {
		    return valued.getOriginType();
	    }
    
	    @Override
	    public void setByte(AmiCenterQueryDsRequest0 valued, byte value) {
		    valued.setOriginType(value);
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getOriginType());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getOriginType());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte originType";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setOriginType((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_originType=new VALUED_PARAM_CLASS_originType();
  

  
    private static final class VALUED_PARAM_CLASS_parentProcessId extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getParentProcessId());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setParentProcessId(stream.readLong());
		    
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
            return 30;
	    }
    
	    @Override
	    public String getName() {
            return "parentProcessId";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (long)((AmiCenterQueryDsRequest0)valued).getParentProcessId();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setParentProcessId((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setParentProcessId(source.getParentProcessId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getParentProcessId(),source.getParentProcessId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterQueryDsRequest0 valued) {
		    return valued.getParentProcessId();
	    }
    
	    @Override
	    public void setLong(AmiCenterQueryDsRequest0 valued, long value) {
		    valued.setParentProcessId(value);
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getParentProcessId());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getParentProcessId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long parentProcessId";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setParentProcessId(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_parentProcessId=new VALUED_PARAM_CLASS_parentProcessId();
  

  
    private static final class VALUED_PARAM_CLASS_parsedNode extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 60;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.utils.string.Node}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.utils.string.Node}");
		    
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
		    return 3;
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
		    return 17;
	    }
    
	    @Override
	    public byte getPid() {
            return 16;
	    }
    
	    @Override
	    public String getName() {
            return "parsedNode";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (com.f1.utils.string.Node)((AmiCenterQueryDsRequest0)valued).getParsedNode();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setParsedNode((com.f1.utils.string.Node)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setParsedNode(source.getParsedNode());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getParsedNode(),source.getParsedNode());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.utils.string.Node.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.utils.string.Node.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getParsedNode());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getParsedNode());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.utils.string.Node parsedNode";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setParsedNode(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_parsedNode=new VALUED_PARAM_CLASS_parsedNode();
  

  
    private static final class VALUED_PARAM_CLASS_permissions extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getPermissions());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setPermissions(stream.readByte());
		    
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
		    return 18;
	    }
    
	    @Override
	    public byte getPid() {
            return 28;
	    }
    
	    @Override
	    public String getName() {
            return "permissions";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (byte)((AmiCenterQueryDsRequest0)valued).getPermissions();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setPermissions((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setPermissions(source.getPermissions());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getPermissions(),source.getPermissions());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiCenterQueryDsRequest0 valued) {
		    return valued.getPermissions();
	    }
    
	    @Override
	    public void setByte(AmiCenterQueryDsRequest0 valued, byte value) {
		    valued.setPermissions(value);
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPermissions());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPermissions());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte permissions";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setPermissions((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_permissions=new VALUED_PARAM_CLASS_permissions();
  

  
    private static final class VALUED_PARAM_CLASS_previewCount extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getPreviewCount());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setPreviewCount(stream.readInt());
		    
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
		    return 19;
	    }
    
	    @Override
	    public byte getPid() {
            return 19;
	    }
    
	    @Override
	    public String getName() {
            return "previewCount";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (int)((AmiCenterQueryDsRequest0)valued).getPreviewCount();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setPreviewCount((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setPreviewCount(source.getPreviewCount());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getPreviewCount(),source.getPreviewCount());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterQueryDsRequest0 valued) {
		    return valued.getPreviewCount();
	    }
    
	    @Override
	    public void setInt(AmiCenterQueryDsRequest0 valued, int value) {
		    valued.setPreviewCount(value);
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPreviewCount());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPreviewCount());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int previewCount";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setPreviewCount(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_previewCount=new VALUED_PARAM_CLASS_previewCount();
  

  
    private static final class VALUED_PARAM_CLASS_priority extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getPriority());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 20;
	    }
    
	    @Override
	    public byte getPid() {
            return 53;
	    }
    
	    @Override
	    public String getName() {
            return "priority";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (int)((AmiCenterQueryDsRequest0)valued).getPriority();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setPriority((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setPriority(source.getPriority());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getPriority(),source.getPriority());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterQueryDsRequest0 valued) {
		    return valued.getPriority();
	    }
    
	    @Override
	    public void setInt(AmiCenterQueryDsRequest0 valued, int value) {
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int priority";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setPriority(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_priority=new VALUED_PARAM_CLASS_priority();
  

  
    private static final class VALUED_PARAM_CLASS_query extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 21;
	    }
    
	    @Override
	    public byte getPid() {
            return 10;
	    }
    
	    @Override
	    public String getName() {
            return "query";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsRequest0)valued).getQuery();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setQuery((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setQuery(source.getQuery());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getQuery(),source.getQuery());
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getQuery(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getQuery(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String query";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setQuery(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_query=new VALUED_PARAM_CLASS_query();
  

  
    private static final class VALUED_PARAM_CLASS_querySessionId extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getQuerySessionId());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setQuerySessionId(stream.readLong());
		    
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
		    return 22;
	    }
    
	    @Override
	    public byte getPid() {
            return 12;
	    }
    
	    @Override
	    public String getName() {
            return "querySessionId";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (long)((AmiCenterQueryDsRequest0)valued).getQuerySessionId();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setQuerySessionId((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setQuerySessionId(source.getQuerySessionId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getQuerySessionId(),source.getQuerySessionId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterQueryDsRequest0 valued) {
		    return valued.getQuerySessionId();
	    }
    
	    @Override
	    public void setLong(AmiCenterQueryDsRequest0 valued, long value) {
		    valued.setQuerySessionId(value);
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getQuerySessionId());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getQuerySessionId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long querySessionId";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setQuerySessionId(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_querySessionId=new VALUED_PARAM_CLASS_querySessionId();
  

  
    private static final class VALUED_PARAM_CLASS_querySessionKeepAlive extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getQuerySessionKeepAlive());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setQuerySessionKeepAlive(stream.readBoolean());
		    
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
		    return 23;
	    }
    
	    @Override
	    public byte getPid() {
            return 13;
	    }
    
	    @Override
	    public String getName() {
            return "querySessionKeepAlive";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (boolean)((AmiCenterQueryDsRequest0)valued).getQuerySessionKeepAlive();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setQuerySessionKeepAlive((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setQuerySessionKeepAlive(source.getQuerySessionKeepAlive());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getQuerySessionKeepAlive(),source.getQuerySessionKeepAlive());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterQueryDsRequest0 valued) {
		    return valued.getQuerySessionKeepAlive();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterQueryDsRequest0 valued, boolean value) {
		    valued.setQuerySessionKeepAlive(value);
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getQuerySessionKeepAlive());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getQuerySessionKeepAlive());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean querySessionKeepAlive";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setQuerySessionKeepAlive(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_querySessionKeepAlive=new VALUED_PARAM_CLASS_querySessionKeepAlive();
  

  
    private static final class VALUED_PARAM_CLASS_requestTime extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getRequestTime());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setRequestTime(stream.readLong());
		    
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
		    return 24;
	    }
    
	    @Override
	    public byte getPid() {
            return 54;
	    }
    
	    @Override
	    public String getName() {
            return "requestTime";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (long)((AmiCenterQueryDsRequest0)valued).getRequestTime();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setRequestTime((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setRequestTime(source.getRequestTime());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getRequestTime(),source.getRequestTime());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterQueryDsRequest0 valued) {
		    return valued.getRequestTime();
	    }
    
	    @Override
	    public void setLong(AmiCenterQueryDsRequest0 valued, long value) {
		    valued.setRequestTime(value);
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getRequestTime());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getRequestTime());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long requestTime";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setRequestTime(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_requestTime=new VALUED_PARAM_CLASS_requestTime();
  

  
    private static final class VALUED_PARAM_CLASS_sessionVariableTypes extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 23;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Map}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 25;
	    }
    
	    @Override
	    public byte getPid() {
            return 32;
	    }
    
	    @Override
	    public String getName() {
            return "sessionVariableTypes";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (java.util.Map)((AmiCenterQueryDsRequest0)valued).getSessionVariableTypes();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setSessionVariableTypes((java.util.Map)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setSessionVariableTypes(source.getSessionVariableTypes());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getSessionVariableTypes(),source.getSessionVariableTypes());
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getSessionVariableTypes());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getSessionVariableTypes());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.Map sessionVariableTypes";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setSessionVariableTypes(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_sessionVariableTypes=new VALUED_PARAM_CLASS_sessionVariableTypes();
  

  
    private static final class VALUED_PARAM_CLASS_sessionVariables extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 23;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Map}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 26;
	    }
    
	    @Override
	    public byte getPid() {
            return 31;
	    }
    
	    @Override
	    public String getName() {
            return "sessionVariables";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (java.util.Map)((AmiCenterQueryDsRequest0)valued).getSessionVariables();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setSessionVariables((java.util.Map)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setSessionVariables(source.getSessionVariables());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getSessionVariables(),source.getSessionVariables());
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getSessionVariables());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getSessionVariables());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.Map sessionVariables";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setSessionVariables(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_sessionVariables=new VALUED_PARAM_CLASS_sessionVariables();
  

  
    private static final class VALUED_PARAM_CLASS_tablesForPreview extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 27;
	    }
    
	    @Override
	    public byte getPid() {
            return 18;
	    }
    
	    @Override
	    public String getName() {
            return "tablesForPreview";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (java.util.List)((AmiCenterQueryDsRequest0)valued).getTablesForPreview();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setTablesForPreview((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setTablesForPreview(source.getTablesForPreview());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getTablesForPreview(),source.getTablesForPreview());
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTablesForPreview());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTablesForPreview());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List tablesForPreview";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setTablesForPreview(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_tablesForPreview=new VALUED_PARAM_CLASS_tablesForPreview();
  

  
    private static final class VALUED_PARAM_CLASS_timeoutMs extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getTimeoutMs());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 28;
	    }
    
	    @Override
	    public byte getPid() {
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "timeoutMs";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (int)((AmiCenterQueryDsRequest0)valued).getTimeoutMs();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setTimeoutMs((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setTimeoutMs(source.getTimeoutMs());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getTimeoutMs(),source.getTimeoutMs());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterQueryDsRequest0 valued) {
		    return valued.getTimeoutMs();
	    }
    
	    @Override
	    public void setInt(AmiCenterQueryDsRequest0 valued, int value) {
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTimeoutMs());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTimeoutMs());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int timeoutMs";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setTimeoutMs(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_timeoutMs=new VALUED_PARAM_CLASS_timeoutMs();
  

  
    private static final class VALUED_PARAM_CLASS_type extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getType());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setType(stream.readByte());
		    
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
		    return 29;
	    }
    
	    @Override
	    public byte getPid() {
            return 14;
	    }
    
	    @Override
	    public String getName() {
            return "type";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (byte)((AmiCenterQueryDsRequest0)valued).getType();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setType((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setType(source.getType());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getType(),source.getType());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiCenterQueryDsRequest0 valued) {
		    return valued.getType();
	    }
    
	    @Override
	    public void setByte(AmiCenterQueryDsRequest0 valued, byte value) {
		    valued.setType(value);
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getType());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getType());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte type";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setType((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_type=new VALUED_PARAM_CLASS_type();
  

  
    private static final class VALUED_PARAM_CLASS_uploadValues extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 30;
	    }
    
	    @Override
	    public byte getPid() {
            return 15;
	    }
    
	    @Override
	    public String getName() {
            return "uploadValues";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (java.util.List)((AmiCenterQueryDsRequest0)valued).getUploadValues();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setUploadValues((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setUploadValues(source.getUploadValues());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getUploadValues(),source.getUploadValues());
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getUploadValues());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getUploadValues());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List uploadValues";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setUploadValues(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_uploadValues=new VALUED_PARAM_CLASS_uploadValues();
  

  
    private static final class VALUED_PARAM_CLASS_useConcurrency extends AbstractValuedParam<AmiCenterQueryDsRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getUseConcurrency());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setUseConcurrency(stream.readBoolean());
		    
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
		    return 3;
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
		    return 31;
	    }
    
	    @Override
	    public byte getPid() {
            return 17;
	    }
    
	    @Override
	    public String getName() {
            return "useConcurrency";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsRequest0 valued) {
		    return (boolean)((AmiCenterQueryDsRequest0)valued).getUseConcurrency();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsRequest0 valued, Object value) {
		    valued.setUseConcurrency((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
		    dest.setUseConcurrency(source.getUseConcurrency());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsRequest0 source, AmiCenterQueryDsRequest0 dest) {
	        return OH.eq(dest.getUseConcurrency(),source.getUseConcurrency());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterQueryDsRequest0 valued) {
		    return valued.getUseConcurrency();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterQueryDsRequest0 valued, boolean value) {
		    valued.setUseConcurrency(value);
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
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getUseConcurrency());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getUseConcurrency());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean useConcurrency";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsRequest0 valued){
	       valued.setUseConcurrency(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_useConcurrency=new VALUED_PARAM_CLASS_useConcurrency();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_allowSqlInjection, VALUED_PARAM_comment, VALUED_PARAM_datasourceName, VALUED_PARAM_datasourceOverrideAdapter, VALUED_PARAM_datasourceOverrideOptions, VALUED_PARAM_datasourceOverridePassword, VALUED_PARAM_datasourceOverridePasswordEnc, VALUED_PARAM_datasourceOverrideRelay, VALUED_PARAM_datasourceOverrideUrl, VALUED_PARAM_datasourceOverrideUsername, VALUED_PARAM_directives, VALUED_PARAM_disableLogging, VALUED_PARAM_invokedBy, VALUED_PARAM_isTest, VALUED_PARAM_limit, VALUED_PARAM_originType, VALUED_PARAM_parentProcessId, VALUED_PARAM_parsedNode, VALUED_PARAM_permissions, VALUED_PARAM_previewCount, VALUED_PARAM_priority, VALUED_PARAM_query, VALUED_PARAM_querySessionId, VALUED_PARAM_querySessionKeepAlive, VALUED_PARAM_requestTime, VALUED_PARAM_sessionVariableTypes, VALUED_PARAM_sessionVariables, VALUED_PARAM_tablesForPreview, VALUED_PARAM_timeoutMs, VALUED_PARAM_type, VALUED_PARAM_uploadValues, VALUED_PARAM_useConcurrency, };



    private static final byte PIDS[]={ 29 ,52,11,27,25,23,24,26,21,22,4,33,51,8,3,20,30,16,28,19,53,10,12,13,54,32,31,18,7,14,15,17};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 29: return VALUED_PARAM_allowSqlInjection;
             case 52: return VALUED_PARAM_comment;
             case 11: return VALUED_PARAM_datasourceName;
             case 27: return VALUED_PARAM_datasourceOverrideAdapter;
             case 25: return VALUED_PARAM_datasourceOverrideOptions;
             case 23: return VALUED_PARAM_datasourceOverridePassword;
             case 24: return VALUED_PARAM_datasourceOverridePasswordEnc;
             case 26: return VALUED_PARAM_datasourceOverrideRelay;
             case 21: return VALUED_PARAM_datasourceOverrideUrl;
             case 22: return VALUED_PARAM_datasourceOverrideUsername;
             case 4: return VALUED_PARAM_directives;
             case 33: return VALUED_PARAM_disableLogging;
             case 51: return VALUED_PARAM_invokedBy;
             case 8: return VALUED_PARAM_isTest;
             case 3: return VALUED_PARAM_limit;
             case 20: return VALUED_PARAM_originType;
             case 30: return VALUED_PARAM_parentProcessId;
             case 16: return VALUED_PARAM_parsedNode;
             case 28: return VALUED_PARAM_permissions;
             case 19: return VALUED_PARAM_previewCount;
             case 53: return VALUED_PARAM_priority;
             case 10: return VALUED_PARAM_query;
             case 12: return VALUED_PARAM_querySessionId;
             case 13: return VALUED_PARAM_querySessionKeepAlive;
             case 54: return VALUED_PARAM_requestTime;
             case 32: return VALUED_PARAM_sessionVariableTypes;
             case 31: return VALUED_PARAM_sessionVariables;
             case 18: return VALUED_PARAM_tablesForPreview;
             case 7: return VALUED_PARAM_timeoutMs;
             case 14: return VALUED_PARAM_type;
             case 15: return VALUED_PARAM_uploadValues;
             case 17: return VALUED_PARAM_useConcurrency;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 29: return true;
             case 52: return true;
             case 11: return true;
             case 27: return true;
             case 25: return true;
             case 23: return true;
             case 24: return true;
             case 26: return true;
             case 21: return true;
             case 22: return true;
             case 4: return true;
             case 33: return true;
             case 51: return true;
             case 8: return true;
             case 3: return true;
             case 20: return true;
             case 30: return true;
             case 16: return true;
             case 28: return true;
             case 19: return true;
             case 53: return true;
             case 10: return true;
             case 12: return true;
             case 13: return true;
             case 54: return true;
             case 32: return true;
             case 31: return true;
             case 18: return true;
             case 7: return true;
             case 14: return true;
             case 15: return true;
             case 17: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 29: return "allowSqlInjection";
             case 52: return "comment";
             case 11: return "datasourceName";
             case 27: return "datasourceOverrideAdapter";
             case 25: return "datasourceOverrideOptions";
             case 23: return "datasourceOverridePassword";
             case 24: return "datasourceOverridePasswordEnc";
             case 26: return "datasourceOverrideRelay";
             case 21: return "datasourceOverrideUrl";
             case 22: return "datasourceOverrideUsername";
             case 4: return "directives";
             case 33: return "disableLogging";
             case 51: return "invokedBy";
             case 8: return "isTest";
             case 3: return "limit";
             case 20: return "originType";
             case 30: return "parentProcessId";
             case 16: return "parsedNode";
             case 28: return "permissions";
             case 19: return "previewCount";
             case 53: return "priority";
             case 10: return "query";
             case 12: return "querySessionId";
             case 13: return "querySessionKeepAlive";
             case 54: return "requestTime";
             case 32: return "sessionVariableTypes";
             case 31: return "sessionVariables";
             case 18: return "tablesForPreview";
             case 7: return "timeoutMs";
             case 14: return "type";
             case 15: return "uploadValues";
             case 17: return "useConcurrency";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 29: return 0;
             case 52: return 1;
             case 11: return 2;
             case 27: return 3;
             case 25: return 4;
             case 23: return 5;
             case 24: return 6;
             case 26: return 7;
             case 21: return 8;
             case 22: return 9;
             case 4: return 10;
             case 33: return 11;
             case 51: return 12;
             case 8: return 13;
             case 3: return 14;
             case 20: return 15;
             case 30: return 16;
             case 16: return 17;
             case 28: return 18;
             case 19: return 19;
             case 53: return 20;
             case 10: return 21;
             case 12: return 22;
             case 13: return 23;
             case 54: return 24;
             case 32: return 25;
             case 31: return 26;
             case 18: return 27;
             case 7: return 28;
             case 14: return 29;
             case 15: return 30;
             case 17: return 31;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="allowSqlInjection") return 29;
             if(name=="comment") return 52;
             if(name=="datasourceName") return 11;
             if(name=="datasourceOverrideAdapter") return 27;
             if(name=="datasourceOverrideOptions") return 25;
             if(name=="datasourceOverridePassword") return 23;
             if(name=="datasourceOverridePasswordEnc") return 24;
             if(name=="datasourceOverrideRelay") return 26;
             if(name=="datasourceOverrideUrl") return 21;
             if(name=="datasourceOverrideUsername") return 22;
             if(name=="directives") return 4;
             if(name=="disableLogging") return 33;
             if(name=="invokedBy") return 51;
             if(name=="isTest") return 8;
             if(name=="limit") return 3;
             if(name=="originType") return 20;
             if(name=="parentProcessId") return 30;
             if(name=="parsedNode") return 16;
             if(name=="permissions") return 28;
             if(name=="previewCount") return 19;
             if(name=="priority") return 53;
             if(name=="query") return 10;
             if(name=="querySessionId") return 12;
             if(name=="querySessionKeepAlive") return 13;
             if(name=="requestTime") return 54;
             if(name=="sessionVariableTypes") return 32;
             if(name=="sessionVariables") return 31;
             if(name=="tablesForPreview") return 18;
             if(name=="timeoutMs") return 7;
             if(name=="type") return 14;
             if(name=="uploadValues") return 15;
             if(name=="useConcurrency") return 17;
            
             if("allowSqlInjection".equals(name)) return 29;
             if("comment".equals(name)) return 52;
             if("datasourceName".equals(name)) return 11;
             if("datasourceOverrideAdapter".equals(name)) return 27;
             if("datasourceOverrideOptions".equals(name)) return 25;
             if("datasourceOverridePassword".equals(name)) return 23;
             if("datasourceOverridePasswordEnc".equals(name)) return 24;
             if("datasourceOverrideRelay".equals(name)) return 26;
             if("datasourceOverrideUrl".equals(name)) return 21;
             if("datasourceOverrideUsername".equals(name)) return 22;
             if("directives".equals(name)) return 4;
             if("disableLogging".equals(name)) return 33;
             if("invokedBy".equals(name)) return 51;
             if("isTest".equals(name)) return 8;
             if("limit".equals(name)) return 3;
             if("originType".equals(name)) return 20;
             if("parentProcessId".equals(name)) return 30;
             if("parsedNode".equals(name)) return 16;
             if("permissions".equals(name)) return 28;
             if("previewCount".equals(name)) return 19;
             if("priority".equals(name)) return 53;
             if("query".equals(name)) return 10;
             if("querySessionId".equals(name)) return 12;
             if("querySessionKeepAlive".equals(name)) return 13;
             if("requestTime".equals(name)) return 54;
             if("sessionVariableTypes".equals(name)) return 32;
             if("sessionVariables".equals(name)) return 31;
             if("tablesForPreview".equals(name)) return 18;
             if("timeoutMs".equals(name)) return 7;
             if("type".equals(name)) return 14;
             if("uploadValues".equals(name)) return 15;
             if("useConcurrency".equals(name)) return 17;
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
             case 29: return  OH.valueOf(this._allowSqlInjection); 
             case 52: return  this._comment; 
             case 11: return  this._datasourceName; 
             case 27: return  this._datasourceOverrideAdapter; 
             case 25: return  this._datasourceOverrideOptions; 
             case 23: return  this._datasourceOverridePassword; 
             case 24: return  this._datasourceOverridePasswordEnc; 
             case 26: return  this._datasourceOverrideRelay; 
             case 21: return  this._datasourceOverrideUrl; 
             case 22: return  this._datasourceOverrideUsername; 
             case 4: return  this._directives; 
             case 33: return  OH.valueOf(this._disableLogging); 
             case 51: return  this._invokedBy; 
             case 8: return  OH.valueOf(this._isTest); 
             case 3: return  OH.valueOf(this._limit); 
             case 20: return  OH.valueOf(this._originType); 
             case 30: return  OH.valueOf(this._parentProcessId); 
             case 16: return  this._parsedNode; 
             case 28: return  OH.valueOf(this._permissions); 
             case 19: return  OH.valueOf(this._previewCount); 
             case 53: return  OH.valueOf(this._priority); 
             case 10: return  this._query; 
             case 12: return  OH.valueOf(this._querySessionId); 
             case 13: return  OH.valueOf(this._querySessionKeepAlive); 
             case 54: return  OH.valueOf(this._requestTime); 
             case 32: return  this._sessionVariableTypes; 
             case 31: return  this._sessionVariables; 
             case 18: return  this._tablesForPreview; 
             case 7: return  OH.valueOf(this._timeoutMs); 
             case 14: return  OH.valueOf(this._type); 
             case 15: return  this._uploadValues; 
             case 17: return  OH.valueOf(this._useConcurrency); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 29: return boolean.class;
             case 52: return java.lang.String.class;
             case 11: return java.lang.String.class;
             case 27: return java.lang.String.class;
             case 25: return java.lang.String.class;
             case 23: return com.f1.base.Password.class;
             case 24: return java.lang.String.class;
             case 26: return java.lang.String.class;
             case 21: return java.lang.String.class;
             case 22: return java.lang.String.class;
             case 4: return java.util.Map.class;
             case 33: return boolean.class;
             case 51: return java.lang.String.class;
             case 8: return boolean.class;
             case 3: return int.class;
             case 20: return byte.class;
             case 30: return long.class;
             case 16: return com.f1.utils.string.Node.class;
             case 28: return byte.class;
             case 19: return int.class;
             case 53: return int.class;
             case 10: return java.lang.String.class;
             case 12: return long.class;
             case 13: return boolean.class;
             case 54: return long.class;
             case 32: return java.util.Map.class;
             case 31: return java.util.Map.class;
             case 18: return java.util.List.class;
             case 7: return int.class;
             case 14: return byte.class;
             case 15: return java.util.List.class;
             case 17: return boolean.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 29: return 0;
             case 52: return 20;
             case 11: return 20;
             case 27: return 20;
             case 25: return 20;
             case 23: return 24;
             case 24: return 20;
             case 26: return 20;
             case 21: return 20;
             case 22: return 20;
             case 4: return 23;
             case 33: return 0;
             case 51: return 20;
             case 8: return 0;
             case 3: return 4;
             case 20: return 1;
             case 30: return 6;
             case 16: return 60;
             case 28: return 1;
             case 19: return 4;
             case 53: return 4;
             case 10: return 20;
             case 12: return 6;
             case 13: return 0;
             case 54: return 6;
             case 32: return 23;
             case 31: return 23;
             case 18: return 21;
             case 7: return 4;
             case 14: return 1;
             case 15: return 21;
             case 17: return 0;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 29: this._allowSqlInjection=(java.lang.Boolean)value;return;
             case 52: this._comment=(java.lang.String)value;return;
             case 11: this._datasourceName=(java.lang.String)value;return;
             case 27: this._datasourceOverrideAdapter=(java.lang.String)value;return;
             case 25: this._datasourceOverrideOptions=(java.lang.String)value;return;
             case 23: this._datasourceOverridePassword=(com.f1.base.Password)value;return;
             case 24: this._datasourceOverridePasswordEnc=(java.lang.String)value;return;
             case 26: this._datasourceOverrideRelay=(java.lang.String)value;return;
             case 21: this._datasourceOverrideUrl=(java.lang.String)value;return;
             case 22: this._datasourceOverrideUsername=(java.lang.String)value;return;
             case 4: this._directives=(java.util.Map)value;return;
             case 33: this._disableLogging=(java.lang.Boolean)value;return;
             case 51: this._invokedBy=(java.lang.String)value;return;
             case 8: this._isTest=(java.lang.Boolean)value;return;
             case 3: this._limit=(java.lang.Integer)value;return;
             case 20: this._originType=(java.lang.Byte)value;return;
             case 30: this._parentProcessId=(java.lang.Long)value;return;
             case 16: this._parsedNode=(com.f1.utils.string.Node)value;return;
             case 28: this._permissions=(java.lang.Byte)value;return;
             case 19: this._previewCount=(java.lang.Integer)value;return;
             case 53: this._priority=(java.lang.Integer)value;return;
             case 10: this._query=(java.lang.String)value;return;
             case 12: this._querySessionId=(java.lang.Long)value;return;
             case 13: this._querySessionKeepAlive=(java.lang.Boolean)value;return;
             case 54: this._requestTime=(java.lang.Long)value;return;
             case 32: this._sessionVariableTypes=(java.util.Map)value;return;
             case 31: this._sessionVariables=(java.util.Map)value;return;
             case 18: this._tablesForPreview=(java.util.List)value;return;
             case 7: this._timeoutMs=(java.lang.Integer)value;return;
             case 14: this._type=(java.lang.Byte)value;return;
             case 15: this._uploadValues=(java.util.List)value;return;
             case 17: this._useConcurrency=(java.lang.Boolean)value;return;
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
             case 29: this._allowSqlInjection=(java.lang.Boolean)value;return true;
             case 52: this._comment=(java.lang.String)value;return true;
             case 11: this._datasourceName=(java.lang.String)value;return true;
             case 27: this._datasourceOverrideAdapter=(java.lang.String)value;return true;
             case 25: this._datasourceOverrideOptions=(java.lang.String)value;return true;
             case 23: this._datasourceOverridePassword=(com.f1.base.Password)value;return true;
             case 24: this._datasourceOverridePasswordEnc=(java.lang.String)value;return true;
             case 26: this._datasourceOverrideRelay=(java.lang.String)value;return true;
             case 21: this._datasourceOverrideUrl=(java.lang.String)value;return true;
             case 22: this._datasourceOverrideUsername=(java.lang.String)value;return true;
             case 4: this._directives=(java.util.Map)value;return true;
             case 33: this._disableLogging=(java.lang.Boolean)value;return true;
             case 51: this._invokedBy=(java.lang.String)value;return true;
             case 8: this._isTest=(java.lang.Boolean)value;return true;
             case 3: this._limit=(java.lang.Integer)value;return true;
             case 20: this._originType=(java.lang.Byte)value;return true;
             case 30: this._parentProcessId=(java.lang.Long)value;return true;
             case 16: this._parsedNode=(com.f1.utils.string.Node)value;return true;
             case 28: this._permissions=(java.lang.Byte)value;return true;
             case 19: this._previewCount=(java.lang.Integer)value;return true;
             case 53: this._priority=(java.lang.Integer)value;return true;
             case 10: this._query=(java.lang.String)value;return true;
             case 12: this._querySessionId=(java.lang.Long)value;return true;
             case 13: this._querySessionKeepAlive=(java.lang.Boolean)value;return true;
             case 54: this._requestTime=(java.lang.Long)value;return true;
             case 32: this._sessionVariableTypes=(java.util.Map)value;return true;
             case 31: this._sessionVariables=(java.util.Map)value;return true;
             case 18: this._tablesForPreview=(java.util.List)value;return true;
             case 7: this._timeoutMs=(java.lang.Integer)value;return true;
             case 14: this._type=(java.lang.Byte)value;return true;
             case 15: this._uploadValues=(java.util.List)value;return true;
             case 17: this._useConcurrency=(java.lang.Boolean)value;return true;
            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
             case 29: return this._allowSqlInjection;
             case 33: return this._disableLogging;
             case 8: return this._isTest;
             case 13: return this._querySessionKeepAlive;
             case 17: return this._useConcurrency;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askByte(byte pid){
        switch(pid){
             case 20: return this._originType;
             case 28: return this._permissions;
             case 14: return this._type;
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
             case 3: return this._limit;
             case 19: return this._previewCount;
             case 53: return this._priority;
             case 7: return this._timeoutMs;
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
             case 30: return this._parentProcessId;
             case 12: return this._querySessionId;
             case 54: return this._requestTime;
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
             case 29: this._allowSqlInjection=value;return;
             case 33: this._disableLogging=value;return;
             case 8: this._isTest=value;return;
             case 13: this._querySessionKeepAlive=value;return;
             case 17: this._useConcurrency=value;return;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putByte(byte pid,byte value){
    
        switch(pid){
             case 20: this._originType=value;return;
             case 28: this._permissions=value;return;
             case 14: this._type=value;return;
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
             case 3: this._limit=value;return;
             case 19: this._previewCount=value;return;
             case 53: this._priority=value;return;
             case 7: this._timeoutMs=value;return;
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
             case 30: this._parentProcessId=value;return;
             case 12: this._querySessionId=value;return;
             case 54: this._requestTime=value;return;
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
        
            if((basicType=in.readByte())!=4)
                break;
            this._limit=in.readInt();
        
            break;

        case 4:
        
            this._directives=(java.util.Map)converter.read(session);
        
            break;

        case 7:
        
            if((basicType=in.readByte())!=4)
                break;
            this._timeoutMs=in.readInt();
        
            break;

        case 8:
        
            if((basicType=in.readByte())!=0)
                break;
            this._isTest=in.readBoolean();
        
            break;

        case 10:
        
            this._query=(java.lang.String)converter.read(session);
        
            break;

        case 11:
        
            this._datasourceName=(java.lang.String)converter.read(session);
        
            break;

        case 12:
        
            if((basicType=in.readByte())!=6)
                break;
            this._querySessionId=in.readLong();
        
            break;

        case 13:
        
            if((basicType=in.readByte())!=0)
                break;
            this._querySessionKeepAlive=in.readBoolean();
        
            break;

        case 14:
        
            if((basicType=in.readByte())!=1)
                break;
            this._type=in.readByte();
        
            break;

        case 15:
        
            this._uploadValues=(java.util.List)converter.read(session);
        
            break;

        case 16:
        
            this._parsedNode=(com.f1.utils.string.Node)converter.read(session);
        
            break;

        case 17:
        
            if((basicType=in.readByte())!=0)
                break;
            this._useConcurrency=in.readBoolean();
        
            break;

        case 18:
        
            this._tablesForPreview=(java.util.List)converter.read(session);
        
            break;

        case 19:
        
            if((basicType=in.readByte())!=4)
                break;
            this._previewCount=in.readInt();
        
            break;

        case 20:
        
            if((basicType=in.readByte())!=1)
                break;
            this._originType=in.readByte();
        
            break;

        case 21:
        
            this._datasourceOverrideUrl=(java.lang.String)converter.read(session);
        
            break;

        case 22:
        
            this._datasourceOverrideUsername=(java.lang.String)converter.read(session);
        
            break;

        case 23:
        
            this._datasourceOverridePassword=(com.f1.base.Password)converter.read(session);
        
            break;

        case 24:
        
            this._datasourceOverridePasswordEnc=(java.lang.String)converter.read(session);
        
            break;

        case 25:
        
            this._datasourceOverrideOptions=(java.lang.String)converter.read(session);
        
            break;

        case 26:
        
            this._datasourceOverrideRelay=(java.lang.String)converter.read(session);
        
            break;

        case 27:
        
            this._datasourceOverrideAdapter=(java.lang.String)converter.read(session);
        
            break;

        case 28:
        
            if((basicType=in.readByte())!=1)
                break;
            this._permissions=in.readByte();
        
            break;

        case 29:
        
            if((basicType=in.readByte())!=0)
                break;
            this._allowSqlInjection=in.readBoolean();
        
            break;

        case 30:
        
            if((basicType=in.readByte())!=6)
                break;
            this._parentProcessId=in.readLong();
        
            break;

        case 31:
        
            this._sessionVariables=(java.util.Map)converter.read(session);
        
            break;

        case 32:
        
            this._sessionVariableTypes=(java.util.Map)converter.read(session);
        
            break;

        case 33:
        
            if((basicType=in.readByte())!=0)
                break;
            this._disableLogging=in.readBoolean();
        
            break;

        case 51:
        
            this._invokedBy=(java.lang.String)converter.read(session);
        
            break;

        case 52:
        
            this._comment=(java.lang.String)converter.read(session);
        
            break;

        case 53:
        
            if((basicType=in.readByte())!=4)
                break;
            this._priority=in.readInt();
        
            break;

        case 54:
        
            if((basicType=in.readByte())!=6)
                break;
            this._requestTime=in.readLong();
        
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
        
if(this._limit!=0 && (0 & transience)==0){
    out.writeByte(3);
        
    out.writeByte(4);
    out.writeInt(this._limit);
        
}

if(this._directives!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._directives,session);
        
}

if(this._timeoutMs!=0 && (0 & transience)==0){
    out.writeByte(7);
        
    out.writeByte(4);
    out.writeInt(this._timeoutMs);
        
}

if(this._isTest!=false && (0 & transience)==0){
    out.writeByte(8);
        
    out.writeByte(0);
    out.writeBoolean(this._isTest);
        
}

if(this._query!=null && (0 & transience)==0){
    out.writeByte(10);
        
    converter.write(this._query,session);
        
}

if(this._datasourceName!=null && (0 & transience)==0){
    out.writeByte(11);
        
    converter.write(this._datasourceName,session);
        
}

if(this._querySessionId!=0L && (0 & transience)==0){
    out.writeByte(12);
        
    out.writeByte(6);
    out.writeLong(this._querySessionId);
        
}

if(this._querySessionKeepAlive!=false && (0 & transience)==0){
    out.writeByte(13);
        
    out.writeByte(0);
    out.writeBoolean(this._querySessionKeepAlive);
        
}

if(this._type!=(byte)0 && (0 & transience)==0){
    out.writeByte(14);
        
    out.writeByte(1);
    out.writeByte(this._type);
        
}

if(this._uploadValues!=null && (0 & transience)==0){
    out.writeByte(15);
        
    converter.write(this._uploadValues,session);
        
}

if(this._parsedNode!=null && (3 & transience)==0){
    out.writeByte(16);
        
    converter.write(this._parsedNode,session);
        
}

if(this._useConcurrency!=false && (3 & transience)==0){
    out.writeByte(17);
        
    out.writeByte(0);
    out.writeBoolean(this._useConcurrency);
        
}

if(this._tablesForPreview!=null && (0 & transience)==0){
    out.writeByte(18);
        
    converter.write(this._tablesForPreview,session);
        
}

if(this._previewCount!=0 && (0 & transience)==0){
    out.writeByte(19);
        
    out.writeByte(4);
    out.writeInt(this._previewCount);
        
}

if(this._originType!=(byte)0 && (0 & transience)==0){
    out.writeByte(20);
        
    out.writeByte(1);
    out.writeByte(this._originType);
        
}

if(this._datasourceOverrideUrl!=null && (0 & transience)==0){
    out.writeByte(21);
        
    converter.write(this._datasourceOverrideUrl,session);
        
}

if(this._datasourceOverrideUsername!=null && (0 & transience)==0){
    out.writeByte(22);
        
    converter.write(this._datasourceOverrideUsername,session);
        
}

if(this._datasourceOverridePassword!=null && (0 & transience)==0){
    out.writeByte(23);
        
    converter.write(this._datasourceOverridePassword,session);
        
}

if(this._datasourceOverridePasswordEnc!=null && (0 & transience)==0){
    out.writeByte(24);
        
    converter.write(this._datasourceOverridePasswordEnc,session);
        
}

if(this._datasourceOverrideOptions!=null && (0 & transience)==0){
    out.writeByte(25);
        
    converter.write(this._datasourceOverrideOptions,session);
        
}

if(this._datasourceOverrideRelay!=null && (0 & transience)==0){
    out.writeByte(26);
        
    converter.write(this._datasourceOverrideRelay,session);
        
}

if(this._datasourceOverrideAdapter!=null && (0 & transience)==0){
    out.writeByte(27);
        
    converter.write(this._datasourceOverrideAdapter,session);
        
}

if(this._permissions!=(byte)0 && (0 & transience)==0){
    out.writeByte(28);
        
    out.writeByte(1);
    out.writeByte(this._permissions);
        
}

if(this._allowSqlInjection!=false && (0 & transience)==0){
    out.writeByte(29);
        
    out.writeByte(0);
    out.writeBoolean(this._allowSqlInjection);
        
}

if(this._parentProcessId!=0L && (0 & transience)==0){
    out.writeByte(30);
        
    out.writeByte(6);
    out.writeLong(this._parentProcessId);
        
}

if(this._sessionVariables!=null && (0 & transience)==0){
    out.writeByte(31);
        
    converter.write(this._sessionVariables,session);
        
}

if(this._sessionVariableTypes!=null && (0 & transience)==0){
    out.writeByte(32);
        
    converter.write(this._sessionVariableTypes,session);
        
}

if(this._disableLogging!=false && (0 & transience)==0){
    out.writeByte(33);
        
    out.writeByte(0);
    out.writeBoolean(this._disableLogging);
        
}

if(this._invokedBy!=null && (0 & transience)==0){
    out.writeByte(51);
        
    converter.write(this._invokedBy,session);
        
}

if(this._comment!=null && (0 & transience)==0){
    out.writeByte(52);
        
    converter.write(this._comment,session);
        
}

if(this._priority!=0 && (0 & transience)==0){
    out.writeByte(53);
        
    out.writeByte(4);
    out.writeInt(this._priority);
        
}

if(this._requestTime!=0L && (0 & transience)==0){
    out.writeByte(54);
        
    out.writeByte(6);
    out.writeLong(this._requestTime);
        
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