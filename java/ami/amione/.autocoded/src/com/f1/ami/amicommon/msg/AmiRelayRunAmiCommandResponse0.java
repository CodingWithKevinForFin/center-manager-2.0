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

public abstract class AmiRelayRunAmiCommandResponse0 implements com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandResponse ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private java.lang.String _amiMessage;

    private java.lang.String _amiScript;

    private java.lang.String _commandUid;

    private int _connectionId;

    private java.lang.Exception _exception;

    private java.lang.String _message;

    private boolean _ok;

    private java.util.Map _params;

    private int _statusCode;

    private static final String NAMES[]={ "amiMessage" ,"amiScript","commandUid","connectionId","exception","message","ok","params","statusCode"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 31;
        try{
        switch(h){

                case 6:

                    if(name == "statusCode" || name.equals("statusCode")) {this._statusCode=(java.lang.Integer)value;return;}
break;
                case 9:

                    if(name == "message" || name.equals("message")) {this._message=(java.lang.String)value;return;}
break;
                case 10:

                    if(name == "amiMessage" || name.equals("amiMessage")) {this._amiMessage=(java.lang.String)value;return;}
break;
                case 13:

                    if(name == "params" || name.equals("params")) {this._params=(java.util.Map)value;return;}
break;
                case 14:

                    if(name == "ok" || name.equals("ok")) {this._ok=(java.lang.Boolean)value;return;}
break;
                case 20:

                    if(name == "commandUid" || name.equals("commandUid")) {this._commandUid=(java.lang.String)value;return;}
break;
                case 21:

                    if(name == "connectionId" || name.equals("connectionId")) {this._connectionId=(java.lang.Integer)value;return;}
break;
                case 22:

                    if(name == "amiScript" || name.equals("amiScript")) {this._amiScript=(java.lang.String)value;return;}
break;
                case 23:

                    if(name == "exception" || name.equals("exception")) {this._exception=(java.lang.Exception)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 31;
        switch(h){

                case 6:

                    if(name == "statusCode" || name.equals("statusCode")) {this._statusCode=(java.lang.Integer)value;return true;}
break;
                case 9:

                    if(name == "message" || name.equals("message")) {this._message=(java.lang.String)value;return true;}
break;
                case 10:

                    if(name == "amiMessage" || name.equals("amiMessage")) {this._amiMessage=(java.lang.String)value;return true;}
break;
                case 13:

                    if(name == "params" || name.equals("params")) {this._params=(java.util.Map)value;return true;}
break;
                case 14:

                    if(name == "ok" || name.equals("ok")) {this._ok=(java.lang.Boolean)value;return true;}
break;
                case 20:

                    if(name == "commandUid" || name.equals("commandUid")) {this._commandUid=(java.lang.String)value;return true;}
break;
                case 21:

                    if(name == "connectionId" || name.equals("connectionId")) {this._connectionId=(java.lang.Integer)value;return true;}
break;
                case 22:

                    if(name == "amiScript" || name.equals("amiScript")) {this._amiScript=(java.lang.String)value;return true;}
break;
                case 23:

                    if(name == "exception" || name.equals("exception")) {this._exception=(java.lang.Exception)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 31;
        switch(h){

                case 6:

		    
                    if(name == "statusCode" || name.equals("statusCode")) {return OH.valueOf(this._statusCode);}
		    
break;
                case 9:

		    
                    if(name == "message" || name.equals("message")) {return this._message;}
            
break;
                case 10:

		    
                    if(name == "amiMessage" || name.equals("amiMessage")) {return this._amiMessage;}
            
break;
                case 13:

		    
                    if(name == "params" || name.equals("params")) {return this._params;}
            
break;
                case 14:

		    
                    if(name == "ok" || name.equals("ok")) {return OH.valueOf(this._ok);}
		    
break;
                case 20:

		    
                    if(name == "commandUid" || name.equals("commandUid")) {return this._commandUid;}
            
break;
                case 21:

		    
                    if(name == "connectionId" || name.equals("connectionId")) {return OH.valueOf(this._connectionId);}
		    
break;
                case 22:

		    
                    if(name == "amiScript" || name.equals("amiScript")) {return this._amiScript;}
            
break;
                case 23:

		    
                    if(name == "exception" || name.equals("exception")) {return this._exception;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 31;
        switch(h){

                case 6:

                    if(name == "statusCode" || name.equals("statusCode")) {return int.class;}
break;
                case 9:

                    if(name == "message" || name.equals("message")) {return java.lang.String.class;}
break;
                case 10:

                    if(name == "amiMessage" || name.equals("amiMessage")) {return java.lang.String.class;}
break;
                case 13:

                    if(name == "params" || name.equals("params")) {return java.util.Map.class;}
break;
                case 14:

                    if(name == "ok" || name.equals("ok")) {return boolean.class;}
break;
                case 20:

                    if(name == "commandUid" || name.equals("commandUid")) {return java.lang.String.class;}
break;
                case 21:

                    if(name == "connectionId" || name.equals("connectionId")) {return int.class;}
break;
                case 22:

                    if(name == "amiScript" || name.equals("amiScript")) {return java.lang.String.class;}
break;
                case 23:

                    if(name == "exception" || name.equals("exception")) {return java.lang.Exception.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 31;
        switch(h){

                case 6:

                    if(name == "statusCode" || name.equals("statusCode")) {return VALUED_PARAM_statusCode;}
break;
                case 9:

                    if(name == "message" || name.equals("message")) {return VALUED_PARAM_message;}
break;
                case 10:

                    if(name == "amiMessage" || name.equals("amiMessage")) {return VALUED_PARAM_amiMessage;}
break;
                case 13:

                    if(name == "params" || name.equals("params")) {return VALUED_PARAM_params;}
break;
                case 14:

                    if(name == "ok" || name.equals("ok")) {return VALUED_PARAM_ok;}
break;
                case 20:

                    if(name == "commandUid" || name.equals("commandUid")) {return VALUED_PARAM_commandUid;}
break;
                case 21:

                    if(name == "connectionId" || name.equals("connectionId")) {return VALUED_PARAM_connectionId;}
break;
                case 22:

                    if(name == "amiScript" || name.equals("amiScript")) {return VALUED_PARAM_amiScript;}
break;
                case 23:

                    if(name == "exception" || name.equals("exception")) {return VALUED_PARAM_exception;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 31;
        switch(h){

                case 6:

                    if(name == "statusCode" || name.equals("statusCode")) {return 8;}
break;
                case 9:

                    if(name == "message" || name.equals("message")) {return 5;}
break;
                case 10:

                    if(name == "amiMessage" || name.equals("amiMessage")) {return 0;}
break;
                case 13:

                    if(name == "params" || name.equals("params")) {return 7;}
break;
                case 14:

                    if(name == "ok" || name.equals("ok")) {return 6;}
break;
                case 20:

                    if(name == "commandUid" || name.equals("commandUid")) {return 2;}
break;
                case 21:

                    if(name == "connectionId" || name.equals("connectionId")) {return 3;}
break;
                case 22:

                    if(name == "amiScript" || name.equals("amiScript")) {return 1;}
break;
                case 23:

                    if(name == "exception" || name.equals("exception")) {return 4;}
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
        return 9;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiRelayRunAmiCommandResponse0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 31;
        switch(h){

                case 6:

                    if(name == "statusCode" || name.equals("statusCode")) {return true;}
break;
                case 9:

                    if(name == "message" || name.equals("message")) {return true;}
break;
                case 10:

                    if(name == "amiMessage" || name.equals("amiMessage")) {return true;}
break;
                case 13:

                    if(name == "params" || name.equals("params")) {return true;}
break;
                case 14:

                    if(name == "ok" || name.equals("ok")) {return true;}
break;
                case 20:

                    if(name == "commandUid" || name.equals("commandUid")) {return true;}
break;
                case 21:

                    if(name == "connectionId" || name.equals("connectionId")) {return true;}
break;
                case 22:

                    if(name == "amiScript" || name.equals("amiScript")) {return true;}
break;
                case 23:

                    if(name == "exception" || name.equals("exception")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 31;
        switch(h){

                case 6:

                    if(name == "statusCode" || name.equals("statusCode")) {return 4;}
break;
                case 9:

                    if(name == "message" || name.equals("message")) {return 20;}
break;
                case 10:

                    if(name == "amiMessage" || name.equals("amiMessage")) {return 20;}
break;
                case 13:

                    if(name == "params" || name.equals("params")) {return 23;}
break;
                case 14:

                    if(name == "ok" || name.equals("ok")) {return 0;}
break;
                case 20:

                    if(name == "commandUid" || name.equals("commandUid")) {return 20;}
break;
                case 21:

                    if(name == "connectionId" || name.equals("connectionId")) {return 4;}
break;
                case 22:

                    if(name == "amiScript" || name.equals("amiScript")) {return 20;}
break;
                case 23:

                    if(name == "exception" || name.equals("exception")) {return 53;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _amiMessage;

        case 1:return _amiScript;

        case 2:return _commandUid;

        case 3:return _connectionId;

        case 4:return _exception;

        case 5:return _message;

        case 6:return _ok;

        case 7:return _params;

        case 8:return _statusCode;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 9);
    }

    public java.lang.String getAmiMessage(){
        return this._amiMessage;
    }
    public void setAmiMessage(java.lang.String _amiMessage){
    
        this._amiMessage=_amiMessage;
    }

    public java.lang.String getAmiScript(){
        return this._amiScript;
    }
    public void setAmiScript(java.lang.String _amiScript){
    
        this._amiScript=_amiScript;
    }

    public java.lang.String getCommandUid(){
        return this._commandUid;
    }
    public void setCommandUid(java.lang.String _commandUid){
    
        this._commandUid=_commandUid;
    }

    public int getConnectionId(){
        return this._connectionId;
    }
    public void setConnectionId(int _connectionId){
    
        this._connectionId=_connectionId;
    }

    public java.lang.Exception getException(){
        return this._exception;
    }
    public void setException(java.lang.Exception _exception){
    
        this._exception=_exception;
    }

    public java.lang.String getMessage(){
        return this._message;
    }
    public void setMessage(java.lang.String _message){
    
        this._message=_message;
    }

    public boolean getOk(){
        return this._ok;
    }
    public void setOk(boolean _ok){
    
        this._ok=_ok;
    }

    public java.util.Map getParams(){
        return this._params;
    }
    public void setParams(java.util.Map _params){
    
        this._params=_params;
    }

    public int getStatusCode(){
        return this._statusCode;
    }
    public void setStatusCode(int _statusCode){
    
        this._statusCode=_statusCode;
    }





  
    private static final class VALUED_PARAM_CLASS_amiMessage extends AbstractValuedParam<AmiRelayRunAmiCommandResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandResponse0 valued, DataInput stream) throws IOException{
		    
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
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "amiMessage";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandResponse0 valued) {
		    return (java.lang.String)((AmiRelayRunAmiCommandResponse0)valued).getAmiMessage();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandResponse0 valued, Object value) {
		    valued.setAmiMessage((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
		    dest.setAmiMessage(source.getAmiMessage());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
	        return OH.eq(dest.getAmiMessage(),source.getAmiMessage());
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
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getAmiMessage(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getAmiMessage(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String amiMessage";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandResponse0 valued){
	       valued.setAmiMessage(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_amiMessage=new VALUED_PARAM_CLASS_amiMessage();
  

  
    private static final class VALUED_PARAM_CLASS_amiScript extends AbstractValuedParam<AmiRelayRunAmiCommandResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandResponse0 valued, DataInput stream) throws IOException{
		    
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
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "amiScript";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandResponse0 valued) {
		    return (java.lang.String)((AmiRelayRunAmiCommandResponse0)valued).getAmiScript();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandResponse0 valued, Object value) {
		    valued.setAmiScript((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
		    dest.setAmiScript(source.getAmiScript());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
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
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getAmiScript(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getAmiScript(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String amiScript";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandResponse0 valued){
	       valued.setAmiScript(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_amiScript=new VALUED_PARAM_CLASS_amiScript();
  

  
    private static final class VALUED_PARAM_CLASS_commandUid extends AbstractValuedParam<AmiRelayRunAmiCommandResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandResponse0 valued, DataInput stream) throws IOException{
		    
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
            return 9;
	    }
    
	    @Override
	    public String getName() {
            return "commandUid";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandResponse0 valued) {
		    return (java.lang.String)((AmiRelayRunAmiCommandResponse0)valued).getCommandUid();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandResponse0 valued, Object value) {
		    valued.setCommandUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
		    dest.setCommandUid(source.getCommandUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
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
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getCommandUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getCommandUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String commandUid";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandResponse0 valued){
	       valued.setCommandUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_commandUid=new VALUED_PARAM_CLASS_commandUid();
  

  
    private static final class VALUED_PARAM_CLASS_connectionId extends AbstractValuedParam<AmiRelayRunAmiCommandResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getConnectionId());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 3;
	    }
    
	    @Override
	    public byte getPid() {
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "connectionId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandResponse0 valued) {
		    return (int)((AmiRelayRunAmiCommandResponse0)valued).getConnectionId();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandResponse0 valued, Object value) {
		    valued.setConnectionId((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
		    dest.setConnectionId(source.getConnectionId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
	        return OH.eq(dest.getConnectionId(),source.getConnectionId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiRelayRunAmiCommandResponse0 valued) {
		    return valued.getConnectionId();
	    }
    
	    @Override
	    public void setInt(AmiRelayRunAmiCommandResponse0 valued, int value) {
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
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getConnectionId());
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getConnectionId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int connectionId";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandResponse0 valued){
	       valued.setConnectionId(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_connectionId=new VALUED_PARAM_CLASS_connectionId();
  

  
    private static final class VALUED_PARAM_CLASS_exception extends AbstractValuedParam<AmiRelayRunAmiCommandResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 53;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Exception}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandResponse0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Exception}");
		    
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
		    return 4;
	    }
    
	    @Override
	    public byte getPid() {
            return 44;
	    }
    
	    @Override
	    public String getName() {
            return "exception";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandResponse0 valued) {
		    return (java.lang.Exception)((AmiRelayRunAmiCommandResponse0)valued).getException();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandResponse0 valued, Object value) {
		    valued.setException((java.lang.Exception)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
		    dest.setException(source.getException());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
	        return OH.eq(dest.getException(),source.getException());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.Exception.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.Exception.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getException());
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getException());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Exception exception";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandResponse0 valued){
	       valued.setException(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_exception=new VALUED_PARAM_CLASS_exception();
  

  
    private static final class VALUED_PARAM_CLASS_message extends AbstractValuedParam<AmiRelayRunAmiCommandResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 5;
	    }
    
	    @Override
	    public byte getPid() {
            return 43;
	    }
    
	    @Override
	    public String getName() {
            return "message";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandResponse0 valued) {
		    return (java.lang.String)((AmiRelayRunAmiCommandResponse0)valued).getMessage();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandResponse0 valued, Object value) {
		    valued.setMessage((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
		    dest.setMessage(source.getMessage());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
	        return OH.eq(dest.getMessage(),source.getMessage());
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
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getMessage(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getMessage(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String message";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandResponse0 valued){
	       valued.setMessage(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_message=new VALUED_PARAM_CLASS_message();
  

  
    private static final class VALUED_PARAM_CLASS_ok extends AbstractValuedParam<AmiRelayRunAmiCommandResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getOk());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandResponse0 valued, DataInput stream) throws IOException{
		    
		      valued.setOk(stream.readBoolean());
		    
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
		    return 6;
	    }
    
	    @Override
	    public byte getPid() {
            return 42;
	    }
    
	    @Override
	    public String getName() {
            return "ok";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandResponse0 valued) {
		    return (boolean)((AmiRelayRunAmiCommandResponse0)valued).getOk();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandResponse0 valued, Object value) {
		    valued.setOk((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
		    dest.setOk(source.getOk());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
	        return OH.eq(dest.getOk(),source.getOk());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiRelayRunAmiCommandResponse0 valued) {
		    return valued.getOk();
	    }
    
	    @Override
	    public void setBoolean(AmiRelayRunAmiCommandResponse0 valued, boolean value) {
		    valued.setOk(value);
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
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getOk());
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getOk());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean ok";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandResponse0 valued){
	       valued.setOk(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_ok=new VALUED_PARAM_CLASS_ok();
  

  
    private static final class VALUED_PARAM_CLASS_params extends AbstractValuedParam<AmiRelayRunAmiCommandResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 23;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Map}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 7;
	    }
    
	    @Override
	    public byte getPid() {
            return 8;
	    }
    
	    @Override
	    public String getName() {
            return "params";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandResponse0 valued) {
		    return (java.util.Map)((AmiRelayRunAmiCommandResponse0)valued).getParams();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandResponse0 valued, Object value) {
		    valued.setParams((java.util.Map)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
		    dest.setParams(source.getParams());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
	        return OH.eq(dest.getParams(),source.getParams());
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
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getParams());
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getParams());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.Map params";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandResponse0 valued){
	       valued.setParams(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_params=new VALUED_PARAM_CLASS_params();
  

  
    private static final class VALUED_PARAM_CLASS_statusCode extends AbstractValuedParam<AmiRelayRunAmiCommandResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiRelayRunAmiCommandResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getStatusCode());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunAmiCommandResponse0 valued, DataInput stream) throws IOException{
		    
		      valued.setStatusCode(stream.readInt());
		    
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
		    return 8;
	    }
    
	    @Override
	    public byte getPid() {
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "statusCode";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunAmiCommandResponse0 valued) {
		    return (int)((AmiRelayRunAmiCommandResponse0)valued).getStatusCode();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunAmiCommandResponse0 valued, Object value) {
		    valued.setStatusCode((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
		    dest.setStatusCode(source.getStatusCode());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunAmiCommandResponse0 source, AmiRelayRunAmiCommandResponse0 dest) {
	        return OH.eq(dest.getStatusCode(),source.getStatusCode());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiRelayRunAmiCommandResponse0 valued) {
		    return valued.getStatusCode();
	    }
    
	    @Override
	    public void setInt(AmiRelayRunAmiCommandResponse0 valued, int value) {
		    valued.setStatusCode(value);
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
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getStatusCode());
	        
	    }
	    @Override
	    public void append(AmiRelayRunAmiCommandResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getStatusCode());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int statusCode";
	    }
	    @Override
	    public void clear(AmiRelayRunAmiCommandResponse0 valued){
	       valued.setStatusCode(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_statusCode=new VALUED_PARAM_CLASS_statusCode();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_amiMessage, VALUED_PARAM_amiScript, VALUED_PARAM_commandUid, VALUED_PARAM_connectionId, VALUED_PARAM_exception, VALUED_PARAM_message, VALUED_PARAM_ok, VALUED_PARAM_params, VALUED_PARAM_statusCode, };



    private static final byte PIDS[]={ 3 ,7,9,5,44,43,42,8,2};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 3: return VALUED_PARAM_amiMessage;
             case 7: return VALUED_PARAM_amiScript;
             case 9: return VALUED_PARAM_commandUid;
             case 5: return VALUED_PARAM_connectionId;
             case 44: return VALUED_PARAM_exception;
             case 43: return VALUED_PARAM_message;
             case 42: return VALUED_PARAM_ok;
             case 8: return VALUED_PARAM_params;
             case 2: return VALUED_PARAM_statusCode;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 3: return true;
             case 7: return true;
             case 9: return true;
             case 5: return true;
             case 44: return true;
             case 43: return true;
             case 42: return true;
             case 8: return true;
             case 2: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 3: return "amiMessage";
             case 7: return "amiScript";
             case 9: return "commandUid";
             case 5: return "connectionId";
             case 44: return "exception";
             case 43: return "message";
             case 42: return "ok";
             case 8: return "params";
             case 2: return "statusCode";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 3: return 0;
             case 7: return 1;
             case 9: return 2;
             case 5: return 3;
             case 44: return 4;
             case 43: return 5;
             case 42: return 6;
             case 8: return 7;
             case 2: return 8;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="amiMessage") return 3;
             if(name=="amiScript") return 7;
             if(name=="commandUid") return 9;
             if(name=="connectionId") return 5;
             if(name=="exception") return 44;
             if(name=="message") return 43;
             if(name=="ok") return 42;
             if(name=="params") return 8;
             if(name=="statusCode") return 2;
            
             if("amiMessage".equals(name)) return 3;
             if("amiScript".equals(name)) return 7;
             if("commandUid".equals(name)) return 9;
             if("connectionId".equals(name)) return 5;
             if("exception".equals(name)) return 44;
             if("message".equals(name)) return 43;
             if("ok".equals(name)) return 42;
             if("params".equals(name)) return 8;
             if("statusCode".equals(name)) return 2;
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
             case 3: return  this._amiMessage; 
             case 7: return  this._amiScript; 
             case 9: return  this._commandUid; 
             case 5: return  OH.valueOf(this._connectionId); 
             case 44: return  this._exception; 
             case 43: return  this._message; 
             case 42: return  OH.valueOf(this._ok); 
             case 8: return  this._params; 
             case 2: return  OH.valueOf(this._statusCode); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 3: return java.lang.String.class;
             case 7: return java.lang.String.class;
             case 9: return java.lang.String.class;
             case 5: return int.class;
             case 44: return java.lang.Exception.class;
             case 43: return java.lang.String.class;
             case 42: return boolean.class;
             case 8: return java.util.Map.class;
             case 2: return int.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 3: return 20;
             case 7: return 20;
             case 9: return 20;
             case 5: return 4;
             case 44: return 53;
             case 43: return 20;
             case 42: return 0;
             case 8: return 23;
             case 2: return 4;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 3: this._amiMessage=(java.lang.String)value;return;
             case 7: this._amiScript=(java.lang.String)value;return;
             case 9: this._commandUid=(java.lang.String)value;return;
             case 5: this._connectionId=(java.lang.Integer)value;return;
             case 44: this._exception=(java.lang.Exception)value;return;
             case 43: this._message=(java.lang.String)value;return;
             case 42: this._ok=(java.lang.Boolean)value;return;
             case 8: this._params=(java.util.Map)value;return;
             case 2: this._statusCode=(java.lang.Integer)value;return;
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
             case 3: this._amiMessage=(java.lang.String)value;return true;
             case 7: this._amiScript=(java.lang.String)value;return true;
             case 9: this._commandUid=(java.lang.String)value;return true;
             case 5: this._connectionId=(java.lang.Integer)value;return true;
             case 44: this._exception=(java.lang.Exception)value;return true;
             case 43: this._message=(java.lang.String)value;return true;
             case 42: this._ok=(java.lang.Boolean)value;return true;
             case 8: this._params=(java.util.Map)value;return true;
             case 2: this._statusCode=(java.lang.Integer)value;return true;
            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
             case 42: return this._ok;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askByte(byte pid){
        switch(pid){
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
             case 5: return this._connectionId;
             case 2: return this._statusCode;
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
             case 42: this._ok=value;return;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putByte(byte pid,byte value){
    
        switch(pid){
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
             case 5: this._connectionId=value;return;
             case 2: this._statusCode=value;return;
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
        
        case 2:
        
            if((basicType=in.readByte())!=4)
                break;
            this._statusCode=in.readInt();
        
            break;

        case 3:
        
            this._amiMessage=(java.lang.String)converter.read(session);
        
            break;

        case 5:
        
            if((basicType=in.readByte())!=4)
                break;
            this._connectionId=in.readInt();
        
            break;

        case 7:
        
            this._amiScript=(java.lang.String)converter.read(session);
        
            break;

        case 8:
        
            this._params=(java.util.Map)converter.read(session);
        
            break;

        case 9:
        
            this._commandUid=(java.lang.String)converter.read(session);
        
            break;

        case 42:
        
            if((basicType=in.readByte())!=0)
                break;
            this._ok=in.readBoolean();
        
            break;

        case 43:
        
            this._message=(java.lang.String)converter.read(session);
        
            break;

        case 44:
        
            this._exception=(java.lang.Exception)converter.read(session);
        
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
        
if(this._statusCode!=0 && (0 & transience)==0){
    out.writeByte(2);
        
    out.writeByte(4);
    out.writeInt(this._statusCode);
        
}

if(this._amiMessage!=null && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this._amiMessage,session);
        
}

if(this._connectionId!=0 && (0 & transience)==0){
    out.writeByte(5);
        
    out.writeByte(4);
    out.writeInt(this._connectionId);
        
}

if(this._amiScript!=null && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this._amiScript,session);
        
}

if(this._params!=null && (0 & transience)==0){
    out.writeByte(8);
        
    converter.write(this._params,session);
        
}

if(this._commandUid!=null && (0 & transience)==0){
    out.writeByte(9);
        
    converter.write(this._commandUid,session);
        
}

if(this._ok!=false && (0 & transience)==0){
    out.writeByte(42);
        
    out.writeByte(0);
    out.writeBoolean(this._ok);
        
}

if(this._message!=null && (0 & transience)==0){
    out.writeByte(43);
        
    converter.write(this._message,session);
        
}

if(this._exception!=null && (0 & transience)==0){
    out.writeByte(44);
        
    converter.write(this._exception,session);
        
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