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

public abstract class AmiRelayOnConnectResponse0 implements com.f1.ami.amicommon.msg.AmiRelayOnConnectResponse ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private int _amiServerPort;

    private byte _centerId;

    private java.lang.Exception _exception;

    private boolean _guaranteedMessagingEnabled;

    private java.lang.String _hostname;

    private java.lang.String _machineUid;

    private java.lang.String _message;

    private boolean _ok;

    private java.lang.String _processUid;

    private java.lang.String _relayId;

    private long _startTime;

    private static final String NAMES[]={ "amiServerPort" ,"centerId","exception","guaranteedMessagingEnabled","hostname","machineUid","message","ok","processUid","relayId","startTime"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 28;
        try{
        switch(h){

                case 3:

                    if(name == "guaranteedMessagingEnabled" || name.equals("guaranteedMessagingEnabled")) {this._guaranteedMessagingEnabled=(java.lang.Boolean)value;return;}
break;
                case 7:

                    if(name == "amiServerPort" || name.equals("amiServerPort")) {this._amiServerPort=(java.lang.Integer)value;return;}
break;
                case 9:

                    if(name == "hostname" || name.equals("hostname")) {this._hostname=(java.lang.String)value;return;}
break;
                case 12:

                    if(name == "relayId" || name.equals("relayId")) {this._relayId=(java.lang.String)value;return;}
break;
                case 13:

                    if(name == "processUid" || name.equals("processUid")) {this._processUid=(java.lang.String)value;return;}
break;
                case 15:

                    if(name == "message" || name.equals("message")) {this._message=(java.lang.String)value;return;}
break;
                case 20:

                    if(name == "ok" || name.equals("ok")) {this._ok=(java.lang.Boolean)value;return;}
break;
                case 21:

                    if(name == "startTime" || name.equals("startTime")) {this._startTime=(java.lang.Long)value;return;}
break;
                case 23:

                    if(name == "exception" || name.equals("exception")) {this._exception=(java.lang.Exception)value;return;}
break;
                case 24:

                    if(name == "centerId" || name.equals("centerId")) {this._centerId=(java.lang.Byte)value;return;}
break;
                case 27:

                    if(name == "machineUid" || name.equals("machineUid")) {this._machineUid=(java.lang.String)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 28;
        switch(h){

                case 3:

                    if(name == "guaranteedMessagingEnabled" || name.equals("guaranteedMessagingEnabled")) {this._guaranteedMessagingEnabled=(java.lang.Boolean)value;return true;}
break;
                case 7:

                    if(name == "amiServerPort" || name.equals("amiServerPort")) {this._amiServerPort=(java.lang.Integer)value;return true;}
break;
                case 9:

                    if(name == "hostname" || name.equals("hostname")) {this._hostname=(java.lang.String)value;return true;}
break;
                case 12:

                    if(name == "relayId" || name.equals("relayId")) {this._relayId=(java.lang.String)value;return true;}
break;
                case 13:

                    if(name == "processUid" || name.equals("processUid")) {this._processUid=(java.lang.String)value;return true;}
break;
                case 15:

                    if(name == "message" || name.equals("message")) {this._message=(java.lang.String)value;return true;}
break;
                case 20:

                    if(name == "ok" || name.equals("ok")) {this._ok=(java.lang.Boolean)value;return true;}
break;
                case 21:

                    if(name == "startTime" || name.equals("startTime")) {this._startTime=(java.lang.Long)value;return true;}
break;
                case 23:

                    if(name == "exception" || name.equals("exception")) {this._exception=(java.lang.Exception)value;return true;}
break;
                case 24:

                    if(name == "centerId" || name.equals("centerId")) {this._centerId=(java.lang.Byte)value;return true;}
break;
                case 27:

                    if(name == "machineUid" || name.equals("machineUid")) {this._machineUid=(java.lang.String)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 28;
        switch(h){

                case 3:

		    
                    if(name == "guaranteedMessagingEnabled" || name.equals("guaranteedMessagingEnabled")) {return OH.valueOf(this._guaranteedMessagingEnabled);}
		    
break;
                case 7:

		    
                    if(name == "amiServerPort" || name.equals("amiServerPort")) {return OH.valueOf(this._amiServerPort);}
		    
break;
                case 9:

		    
                    if(name == "hostname" || name.equals("hostname")) {return this._hostname;}
            
break;
                case 12:

		    
                    if(name == "relayId" || name.equals("relayId")) {return this._relayId;}
            
break;
                case 13:

		    
                    if(name == "processUid" || name.equals("processUid")) {return this._processUid;}
            
break;
                case 15:

		    
                    if(name == "message" || name.equals("message")) {return this._message;}
            
break;
                case 20:

		    
                    if(name == "ok" || name.equals("ok")) {return OH.valueOf(this._ok);}
		    
break;
                case 21:

		    
                    if(name == "startTime" || name.equals("startTime")) {return OH.valueOf(this._startTime);}
		    
break;
                case 23:

		    
                    if(name == "exception" || name.equals("exception")) {return this._exception;}
            
break;
                case 24:

		    
                    if(name == "centerId" || name.equals("centerId")) {return OH.valueOf(this._centerId);}
		    
break;
                case 27:

		    
                    if(name == "machineUid" || name.equals("machineUid")) {return this._machineUid;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 28;
        switch(h){

                case 3:

                    if(name == "guaranteedMessagingEnabled" || name.equals("guaranteedMessagingEnabled")) {return boolean.class;}
break;
                case 7:

                    if(name == "amiServerPort" || name.equals("amiServerPort")) {return int.class;}
break;
                case 9:

                    if(name == "hostname" || name.equals("hostname")) {return java.lang.String.class;}
break;
                case 12:

                    if(name == "relayId" || name.equals("relayId")) {return java.lang.String.class;}
break;
                case 13:

                    if(name == "processUid" || name.equals("processUid")) {return java.lang.String.class;}
break;
                case 15:

                    if(name == "message" || name.equals("message")) {return java.lang.String.class;}
break;
                case 20:

                    if(name == "ok" || name.equals("ok")) {return boolean.class;}
break;
                case 21:

                    if(name == "startTime" || name.equals("startTime")) {return long.class;}
break;
                case 23:

                    if(name == "exception" || name.equals("exception")) {return java.lang.Exception.class;}
break;
                case 24:

                    if(name == "centerId" || name.equals("centerId")) {return byte.class;}
break;
                case 27:

                    if(name == "machineUid" || name.equals("machineUid")) {return java.lang.String.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 28;
        switch(h){

                case 3:

                    if(name == "guaranteedMessagingEnabled" || name.equals("guaranteedMessagingEnabled")) {return VALUED_PARAM_guaranteedMessagingEnabled;}
break;
                case 7:

                    if(name == "amiServerPort" || name.equals("amiServerPort")) {return VALUED_PARAM_amiServerPort;}
break;
                case 9:

                    if(name == "hostname" || name.equals("hostname")) {return VALUED_PARAM_hostname;}
break;
                case 12:

                    if(name == "relayId" || name.equals("relayId")) {return VALUED_PARAM_relayId;}
break;
                case 13:

                    if(name == "processUid" || name.equals("processUid")) {return VALUED_PARAM_processUid;}
break;
                case 15:

                    if(name == "message" || name.equals("message")) {return VALUED_PARAM_message;}
break;
                case 20:

                    if(name == "ok" || name.equals("ok")) {return VALUED_PARAM_ok;}
break;
                case 21:

                    if(name == "startTime" || name.equals("startTime")) {return VALUED_PARAM_startTime;}
break;
                case 23:

                    if(name == "exception" || name.equals("exception")) {return VALUED_PARAM_exception;}
break;
                case 24:

                    if(name == "centerId" || name.equals("centerId")) {return VALUED_PARAM_centerId;}
break;
                case 27:

                    if(name == "machineUid" || name.equals("machineUid")) {return VALUED_PARAM_machineUid;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 28;
        switch(h){

                case 3:

                    if(name == "guaranteedMessagingEnabled" || name.equals("guaranteedMessagingEnabled")) {return 3;}
break;
                case 7:

                    if(name == "amiServerPort" || name.equals("amiServerPort")) {return 0;}
break;
                case 9:

                    if(name == "hostname" || name.equals("hostname")) {return 4;}
break;
                case 12:

                    if(name == "relayId" || name.equals("relayId")) {return 9;}
break;
                case 13:

                    if(name == "processUid" || name.equals("processUid")) {return 8;}
break;
                case 15:

                    if(name == "message" || name.equals("message")) {return 6;}
break;
                case 20:

                    if(name == "ok" || name.equals("ok")) {return 7;}
break;
                case 21:

                    if(name == "startTime" || name.equals("startTime")) {return 10;}
break;
                case 23:

                    if(name == "exception" || name.equals("exception")) {return 2;}
break;
                case 24:

                    if(name == "centerId" || name.equals("centerId")) {return 1;}
break;
                case 27:

                    if(name == "machineUid" || name.equals("machineUid")) {return 5;}
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
        return 11;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiRelayOnConnectResponse0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 28;
        switch(h){

                case 3:

                    if(name == "guaranteedMessagingEnabled" || name.equals("guaranteedMessagingEnabled")) {return true;}
break;
                case 7:

                    if(name == "amiServerPort" || name.equals("amiServerPort")) {return true;}
break;
                case 9:

                    if(name == "hostname" || name.equals("hostname")) {return true;}
break;
                case 12:

                    if(name == "relayId" || name.equals("relayId")) {return true;}
break;
                case 13:

                    if(name == "processUid" || name.equals("processUid")) {return true;}
break;
                case 15:

                    if(name == "message" || name.equals("message")) {return true;}
break;
                case 20:

                    if(name == "ok" || name.equals("ok")) {return true;}
break;
                case 21:

                    if(name == "startTime" || name.equals("startTime")) {return true;}
break;
                case 23:

                    if(name == "exception" || name.equals("exception")) {return true;}
break;
                case 24:

                    if(name == "centerId" || name.equals("centerId")) {return true;}
break;
                case 27:

                    if(name == "machineUid" || name.equals("machineUid")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 28;
        switch(h){

                case 3:

                    if(name == "guaranteedMessagingEnabled" || name.equals("guaranteedMessagingEnabled")) {return 0;}
break;
                case 7:

                    if(name == "amiServerPort" || name.equals("amiServerPort")) {return 4;}
break;
                case 9:

                    if(name == "hostname" || name.equals("hostname")) {return 20;}
break;
                case 12:

                    if(name == "relayId" || name.equals("relayId")) {return 20;}
break;
                case 13:

                    if(name == "processUid" || name.equals("processUid")) {return 20;}
break;
                case 15:

                    if(name == "message" || name.equals("message")) {return 20;}
break;
                case 20:

                    if(name == "ok" || name.equals("ok")) {return 0;}
break;
                case 21:

                    if(name == "startTime" || name.equals("startTime")) {return 6;}
break;
                case 23:

                    if(name == "exception" || name.equals("exception")) {return 53;}
break;
                case 24:

                    if(name == "centerId" || name.equals("centerId")) {return 1;}
break;
                case 27:

                    if(name == "machineUid" || name.equals("machineUid")) {return 20;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _amiServerPort;

        case 1:return _centerId;

        case 2:return _exception;

        case 3:return _guaranteedMessagingEnabled;

        case 4:return _hostname;

        case 5:return _machineUid;

        case 6:return _message;

        case 7:return _ok;

        case 8:return _processUid;

        case 9:return _relayId;

        case 10:return _startTime;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 11);
    }

    public int getAmiServerPort(){
        return this._amiServerPort;
    }
    public void setAmiServerPort(int _amiServerPort){
    
        this._amiServerPort=_amiServerPort;
    }

    public byte getCenterId(){
        return this._centerId;
    }
    public void setCenterId(byte _centerId){
    
        this._centerId=_centerId;
    }

    public java.lang.Exception getException(){
        return this._exception;
    }
    public void setException(java.lang.Exception _exception){
    
        this._exception=_exception;
    }

    public boolean getGuaranteedMessagingEnabled(){
        return this._guaranteedMessagingEnabled;
    }
    public void setGuaranteedMessagingEnabled(boolean _guaranteedMessagingEnabled){
    
        this._guaranteedMessagingEnabled=_guaranteedMessagingEnabled;
    }

    public java.lang.String getHostname(){
        return this._hostname;
    }
    public void setHostname(java.lang.String _hostname){
    
        this._hostname=_hostname;
    }

    public java.lang.String getMachineUid(){
        return this._machineUid;
    }
    public void setMachineUid(java.lang.String _machineUid){
    
        this._machineUid=_machineUid;
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

    public java.lang.String getProcessUid(){
        return this._processUid;
    }
    public void setProcessUid(java.lang.String _processUid){
    
        this._processUid=_processUid;
    }

    public java.lang.String getRelayId(){
        return this._relayId;
    }
    public void setRelayId(java.lang.String _relayId){
    
        this._relayId=_relayId;
    }

    public long getStartTime(){
        return this._startTime;
    }
    public void setStartTime(long _startTime){
    
        this._startTime=_startTime;
    }





  
    private static final class VALUED_PARAM_CLASS_amiServerPort extends AbstractValuedParam<AmiRelayOnConnectResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiRelayOnConnectResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getAmiServerPort());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayOnConnectResponse0 valued, DataInput stream) throws IOException{
		    
		      valued.setAmiServerPort(stream.readInt());
		    
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
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "amiServerPort";
	    }
    
	    @Override
	    public Object getValue(AmiRelayOnConnectResponse0 valued) {
		    return (int)((AmiRelayOnConnectResponse0)valued).getAmiServerPort();
	    }
    
	    @Override
	    public void setValue(AmiRelayOnConnectResponse0 valued, Object value) {
		    valued.setAmiServerPort((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
		    dest.setAmiServerPort(source.getAmiServerPort());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
	        return OH.eq(dest.getAmiServerPort(),source.getAmiServerPort());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiRelayOnConnectResponse0 valued) {
		    return valued.getAmiServerPort();
	    }
    
	    @Override
	    public void setInt(AmiRelayOnConnectResponse0 valued, int value) {
		    valued.setAmiServerPort(value);
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
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAmiServerPort());
	        
	    }
	    @Override
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAmiServerPort());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int amiServerPort";
	    }
	    @Override
	    public void clear(AmiRelayOnConnectResponse0 valued){
	       valued.setAmiServerPort(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_amiServerPort=new VALUED_PARAM_CLASS_amiServerPort();
  

  
    private static final class VALUED_PARAM_CLASS_centerId extends AbstractValuedParam<AmiRelayOnConnectResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiRelayOnConnectResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getCenterId());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayOnConnectResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 1;
	    }
    
	    @Override
	    public byte getPid() {
            return 8;
	    }
    
	    @Override
	    public String getName() {
            return "centerId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayOnConnectResponse0 valued) {
		    return (byte)((AmiRelayOnConnectResponse0)valued).getCenterId();
	    }
    
	    @Override
	    public void setValue(AmiRelayOnConnectResponse0 valued, Object value) {
		    valued.setCenterId((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
		    dest.setCenterId(source.getCenterId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
	        return OH.eq(dest.getCenterId(),source.getCenterId());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiRelayOnConnectResponse0 valued) {
		    return valued.getCenterId();
	    }
    
	    @Override
	    public void setByte(AmiRelayOnConnectResponse0 valued, byte value) {
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
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getCenterId());
	        
	    }
	    @Override
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getCenterId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte centerId";
	    }
	    @Override
	    public void clear(AmiRelayOnConnectResponse0 valued){
	       valued.setCenterId((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_centerId=new VALUED_PARAM_CLASS_centerId();
  

  
    private static final class VALUED_PARAM_CLASS_exception extends AbstractValuedParam<AmiRelayOnConnectResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 53;
	    }
	    
	    @Override
	    public void write(AmiRelayOnConnectResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Exception}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayOnConnectResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 2;
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
	    public Object getValue(AmiRelayOnConnectResponse0 valued) {
		    return (java.lang.Exception)((AmiRelayOnConnectResponse0)valued).getException();
	    }
    
	    @Override
	    public void setValue(AmiRelayOnConnectResponse0 valued, Object value) {
		    valued.setException((java.lang.Exception)value);
	    }
    
	    @Override
	    public void copy(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
		    dest.setException(source.getException());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
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
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getException());
	        
	    }
	    @Override
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getException());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Exception exception";
	    }
	    @Override
	    public void clear(AmiRelayOnConnectResponse0 valued){
	       valued.setException(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_exception=new VALUED_PARAM_CLASS_exception();
  

  
    private static final class VALUED_PARAM_CLASS_guaranteedMessagingEnabled extends AbstractValuedParam<AmiRelayOnConnectResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiRelayOnConnectResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getGuaranteedMessagingEnabled());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayOnConnectResponse0 valued, DataInput stream) throws IOException{
		    
		      valued.setGuaranteedMessagingEnabled(stream.readBoolean());
		    
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
            return 9;
	    }
    
	    @Override
	    public String getName() {
            return "guaranteedMessagingEnabled";
	    }
    
	    @Override
	    public Object getValue(AmiRelayOnConnectResponse0 valued) {
		    return (boolean)((AmiRelayOnConnectResponse0)valued).getGuaranteedMessagingEnabled();
	    }
    
	    @Override
	    public void setValue(AmiRelayOnConnectResponse0 valued, Object value) {
		    valued.setGuaranteedMessagingEnabled((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
		    dest.setGuaranteedMessagingEnabled(source.getGuaranteedMessagingEnabled());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
	        return OH.eq(dest.getGuaranteedMessagingEnabled(),source.getGuaranteedMessagingEnabled());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiRelayOnConnectResponse0 valued) {
		    return valued.getGuaranteedMessagingEnabled();
	    }
    
	    @Override
	    public void setBoolean(AmiRelayOnConnectResponse0 valued, boolean value) {
		    valued.setGuaranteedMessagingEnabled(value);
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
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getGuaranteedMessagingEnabled());
	        
	    }
	    @Override
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getGuaranteedMessagingEnabled());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean guaranteedMessagingEnabled";
	    }
	    @Override
	    public void clear(AmiRelayOnConnectResponse0 valued){
	       valued.setGuaranteedMessagingEnabled(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_guaranteedMessagingEnabled=new VALUED_PARAM_CLASS_guaranteedMessagingEnabled();
  

  
    private static final class VALUED_PARAM_CLASS_hostname extends AbstractValuedParam<AmiRelayOnConnectResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayOnConnectResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayOnConnectResponse0 valued, DataInput stream) throws IOException{
		    
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
            return 6;
	    }
    
	    @Override
	    public String getName() {
            return "hostname";
	    }
    
	    @Override
	    public Object getValue(AmiRelayOnConnectResponse0 valued) {
		    return (java.lang.String)((AmiRelayOnConnectResponse0)valued).getHostname();
	    }
    
	    @Override
	    public void setValue(AmiRelayOnConnectResponse0 valued, Object value) {
		    valued.setHostname((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
		    dest.setHostname(source.getHostname());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
	        return OH.eq(dest.getHostname(),source.getHostname());
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
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getHostname(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getHostname(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String hostname";
	    }
	    @Override
	    public void clear(AmiRelayOnConnectResponse0 valued){
	       valued.setHostname(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_hostname=new VALUED_PARAM_CLASS_hostname();
  

  
    private static final class VALUED_PARAM_CLASS_machineUid extends AbstractValuedParam<AmiRelayOnConnectResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayOnConnectResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayOnConnectResponse0 valued, DataInput stream) throws IOException{
		    
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
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "machineUid";
	    }
    
	    @Override
	    public Object getValue(AmiRelayOnConnectResponse0 valued) {
		    return (java.lang.String)((AmiRelayOnConnectResponse0)valued).getMachineUid();
	    }
    
	    @Override
	    public void setValue(AmiRelayOnConnectResponse0 valued, Object value) {
		    valued.setMachineUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
		    dest.setMachineUid(source.getMachineUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
	        return OH.eq(dest.getMachineUid(),source.getMachineUid());
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
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getMachineUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getMachineUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String machineUid";
	    }
	    @Override
	    public void clear(AmiRelayOnConnectResponse0 valued){
	       valued.setMachineUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_machineUid=new VALUED_PARAM_CLASS_machineUid();
  

  
    private static final class VALUED_PARAM_CLASS_message extends AbstractValuedParam<AmiRelayOnConnectResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayOnConnectResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayOnConnectResponse0 valued, DataInput stream) throws IOException{
		    
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
            return 43;
	    }
    
	    @Override
	    public String getName() {
            return "message";
	    }
    
	    @Override
	    public Object getValue(AmiRelayOnConnectResponse0 valued) {
		    return (java.lang.String)((AmiRelayOnConnectResponse0)valued).getMessage();
	    }
    
	    @Override
	    public void setValue(AmiRelayOnConnectResponse0 valued, Object value) {
		    valued.setMessage((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
		    dest.setMessage(source.getMessage());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
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
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getMessage(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getMessage(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String message";
	    }
	    @Override
	    public void clear(AmiRelayOnConnectResponse0 valued){
	       valued.setMessage(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_message=new VALUED_PARAM_CLASS_message();
  

  
    private static final class VALUED_PARAM_CLASS_ok extends AbstractValuedParam<AmiRelayOnConnectResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiRelayOnConnectResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getOk());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayOnConnectResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 7;
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
	    public Object getValue(AmiRelayOnConnectResponse0 valued) {
		    return (boolean)((AmiRelayOnConnectResponse0)valued).getOk();
	    }
    
	    @Override
	    public void setValue(AmiRelayOnConnectResponse0 valued, Object value) {
		    valued.setOk((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
		    dest.setOk(source.getOk());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
	        return OH.eq(dest.getOk(),source.getOk());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiRelayOnConnectResponse0 valued) {
		    return valued.getOk();
	    }
    
	    @Override
	    public void setBoolean(AmiRelayOnConnectResponse0 valued, boolean value) {
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
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getOk());
	        
	    }
	    @Override
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getOk());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean ok";
	    }
	    @Override
	    public void clear(AmiRelayOnConnectResponse0 valued){
	       valued.setOk(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_ok=new VALUED_PARAM_CLASS_ok();
  

  
    private static final class VALUED_PARAM_CLASS_processUid extends AbstractValuedParam<AmiRelayOnConnectResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayOnConnectResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayOnConnectResponse0 valued, DataInput stream) throws IOException{
		    
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
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "processUid";
	    }
    
	    @Override
	    public Object getValue(AmiRelayOnConnectResponse0 valued) {
		    return (java.lang.String)((AmiRelayOnConnectResponse0)valued).getProcessUid();
	    }
    
	    @Override
	    public void setValue(AmiRelayOnConnectResponse0 valued, Object value) {
		    valued.setProcessUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
		    dest.setProcessUid(source.getProcessUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
	        return OH.eq(dest.getProcessUid(),source.getProcessUid());
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
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getProcessUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getProcessUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String processUid";
	    }
	    @Override
	    public void clear(AmiRelayOnConnectResponse0 valued){
	       valued.setProcessUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_processUid=new VALUED_PARAM_CLASS_processUid();
  

  
    private static final class VALUED_PARAM_CLASS_relayId extends AbstractValuedParam<AmiRelayOnConnectResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayOnConnectResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayOnConnectResponse0 valued, DataInput stream) throws IOException{
		    
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
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "relayId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayOnConnectResponse0 valued) {
		    return (java.lang.String)((AmiRelayOnConnectResponse0)valued).getRelayId();
	    }
    
	    @Override
	    public void setValue(AmiRelayOnConnectResponse0 valued, Object value) {
		    valued.setRelayId((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
		    dest.setRelayId(source.getRelayId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
	        return OH.eq(dest.getRelayId(),source.getRelayId());
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
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getRelayId(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getRelayId(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String relayId";
	    }
	    @Override
	    public void clear(AmiRelayOnConnectResponse0 valued){
	       valued.setRelayId(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_relayId=new VALUED_PARAM_CLASS_relayId();
  

  
    private static final class VALUED_PARAM_CLASS_startTime extends AbstractValuedParam<AmiRelayOnConnectResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiRelayOnConnectResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getStartTime());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayOnConnectResponse0 valued, DataInput stream) throws IOException{
		    
		      valued.setStartTime(stream.readLong());
		    
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
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "startTime";
	    }
    
	    @Override
	    public Object getValue(AmiRelayOnConnectResponse0 valued) {
		    return (long)((AmiRelayOnConnectResponse0)valued).getStartTime();
	    }
    
	    @Override
	    public void setValue(AmiRelayOnConnectResponse0 valued, Object value) {
		    valued.setStartTime((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
		    dest.setStartTime(source.getStartTime());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayOnConnectResponse0 source, AmiRelayOnConnectResponse0 dest) {
	        return OH.eq(dest.getStartTime(),source.getStartTime());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiRelayOnConnectResponse0 valued) {
		    return valued.getStartTime();
	    }
    
	    @Override
	    public void setLong(AmiRelayOnConnectResponse0 valued, long value) {
		    valued.setStartTime(value);
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
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getStartTime());
	        
	    }
	    @Override
	    public void append(AmiRelayOnConnectResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getStartTime());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long startTime";
	    }
	    @Override
	    public void clear(AmiRelayOnConnectResponse0 valued){
	       valued.setStartTime(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_startTime=new VALUED_PARAM_CLASS_startTime();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_amiServerPort, VALUED_PARAM_centerId, VALUED_PARAM_exception, VALUED_PARAM_guaranteedMessagingEnabled, VALUED_PARAM_hostname, VALUED_PARAM_machineUid, VALUED_PARAM_message, VALUED_PARAM_ok, VALUED_PARAM_processUid, VALUED_PARAM_relayId, VALUED_PARAM_startTime, };



    private static final byte PIDS[]={ 4 ,8,44,9,6,2,43,42,7,5,3};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 4: return VALUED_PARAM_amiServerPort;
             case 8: return VALUED_PARAM_centerId;
             case 44: return VALUED_PARAM_exception;
             case 9: return VALUED_PARAM_guaranteedMessagingEnabled;
             case 6: return VALUED_PARAM_hostname;
             case 2: return VALUED_PARAM_machineUid;
             case 43: return VALUED_PARAM_message;
             case 42: return VALUED_PARAM_ok;
             case 7: return VALUED_PARAM_processUid;
             case 5: return VALUED_PARAM_relayId;
             case 3: return VALUED_PARAM_startTime;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 4: return true;
             case 8: return true;
             case 44: return true;
             case 9: return true;
             case 6: return true;
             case 2: return true;
             case 43: return true;
             case 42: return true;
             case 7: return true;
             case 5: return true;
             case 3: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 4: return "amiServerPort";
             case 8: return "centerId";
             case 44: return "exception";
             case 9: return "guaranteedMessagingEnabled";
             case 6: return "hostname";
             case 2: return "machineUid";
             case 43: return "message";
             case 42: return "ok";
             case 7: return "processUid";
             case 5: return "relayId";
             case 3: return "startTime";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 4: return 0;
             case 8: return 1;
             case 44: return 2;
             case 9: return 3;
             case 6: return 4;
             case 2: return 5;
             case 43: return 6;
             case 42: return 7;
             case 7: return 8;
             case 5: return 9;
             case 3: return 10;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="amiServerPort") return 4;
             if(name=="centerId") return 8;
             if(name=="exception") return 44;
             if(name=="guaranteedMessagingEnabled") return 9;
             if(name=="hostname") return 6;
             if(name=="machineUid") return 2;
             if(name=="message") return 43;
             if(name=="ok") return 42;
             if(name=="processUid") return 7;
             if(name=="relayId") return 5;
             if(name=="startTime") return 3;
            
             if("amiServerPort".equals(name)) return 4;
             if("centerId".equals(name)) return 8;
             if("exception".equals(name)) return 44;
             if("guaranteedMessagingEnabled".equals(name)) return 9;
             if("hostname".equals(name)) return 6;
             if("machineUid".equals(name)) return 2;
             if("message".equals(name)) return 43;
             if("ok".equals(name)) return 42;
             if("processUid".equals(name)) return 7;
             if("relayId".equals(name)) return 5;
             if("startTime".equals(name)) return 3;
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
             case 4: return  OH.valueOf(this._amiServerPort); 
             case 8: return  OH.valueOf(this._centerId); 
             case 44: return  this._exception; 
             case 9: return  OH.valueOf(this._guaranteedMessagingEnabled); 
             case 6: return  this._hostname; 
             case 2: return  this._machineUid; 
             case 43: return  this._message; 
             case 42: return  OH.valueOf(this._ok); 
             case 7: return  this._processUid; 
             case 5: return  this._relayId; 
             case 3: return  OH.valueOf(this._startTime); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 4: return int.class;
             case 8: return byte.class;
             case 44: return java.lang.Exception.class;
             case 9: return boolean.class;
             case 6: return java.lang.String.class;
             case 2: return java.lang.String.class;
             case 43: return java.lang.String.class;
             case 42: return boolean.class;
             case 7: return java.lang.String.class;
             case 5: return java.lang.String.class;
             case 3: return long.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 4: return 4;
             case 8: return 1;
             case 44: return 53;
             case 9: return 0;
             case 6: return 20;
             case 2: return 20;
             case 43: return 20;
             case 42: return 0;
             case 7: return 20;
             case 5: return 20;
             case 3: return 6;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 4: this._amiServerPort=(java.lang.Integer)value;return;
             case 8: this._centerId=(java.lang.Byte)value;return;
             case 44: this._exception=(java.lang.Exception)value;return;
             case 9: this._guaranteedMessagingEnabled=(java.lang.Boolean)value;return;
             case 6: this._hostname=(java.lang.String)value;return;
             case 2: this._machineUid=(java.lang.String)value;return;
             case 43: this._message=(java.lang.String)value;return;
             case 42: this._ok=(java.lang.Boolean)value;return;
             case 7: this._processUid=(java.lang.String)value;return;
             case 5: this._relayId=(java.lang.String)value;return;
             case 3: this._startTime=(java.lang.Long)value;return;
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
             case 4: this._amiServerPort=(java.lang.Integer)value;return true;
             case 8: this._centerId=(java.lang.Byte)value;return true;
             case 44: this._exception=(java.lang.Exception)value;return true;
             case 9: this._guaranteedMessagingEnabled=(java.lang.Boolean)value;return true;
             case 6: this._hostname=(java.lang.String)value;return true;
             case 2: this._machineUid=(java.lang.String)value;return true;
             case 43: this._message=(java.lang.String)value;return true;
             case 42: this._ok=(java.lang.Boolean)value;return true;
             case 7: this._processUid=(java.lang.String)value;return true;
             case 5: this._relayId=(java.lang.String)value;return true;
             case 3: this._startTime=(java.lang.Long)value;return true;
            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
             case 9: return this._guaranteedMessagingEnabled;
             case 42: return this._ok;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askByte(byte pid){
        switch(pid){
             case 8: return this._centerId;
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
             case 4: return this._amiServerPort;
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
             case 3: return this._startTime;
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
             case 9: this._guaranteedMessagingEnabled=value;return;
             case 42: this._ok=value;return;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putByte(byte pid,byte value){
    
        switch(pid){
             case 8: this._centerId=value;return;
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
             case 4: this._amiServerPort=value;return;
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
             case 3: this._startTime=value;return;
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
        
            this._machineUid=(java.lang.String)converter.read(session);
        
            break;

        case 3:
        
            if((basicType=in.readByte())!=6)
                break;
            this._startTime=in.readLong();
        
            break;

        case 4:
        
            if((basicType=in.readByte())!=4)
                break;
            this._amiServerPort=in.readInt();
        
            break;

        case 5:
        
            this._relayId=(java.lang.String)converter.read(session);
        
            break;

        case 6:
        
            this._hostname=(java.lang.String)converter.read(session);
        
            break;

        case 7:
        
            this._processUid=(java.lang.String)converter.read(session);
        
            break;

        case 8:
        
            if((basicType=in.readByte())!=1)
                break;
            this._centerId=in.readByte();
        
            break;

        case 9:
        
            if((basicType=in.readByte())!=0)
                break;
            this._guaranteedMessagingEnabled=in.readBoolean();
        
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
        
if(this._machineUid!=null && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this._machineUid,session);
        
}

if(this._startTime!=0L && (0 & transience)==0){
    out.writeByte(3);
        
    out.writeByte(6);
    out.writeLong(this._startTime);
        
}

if(this._amiServerPort!=0 && (0 & transience)==0){
    out.writeByte(4);
        
    out.writeByte(4);
    out.writeInt(this._amiServerPort);
        
}

if(this._relayId!=null && (0 & transience)==0){
    out.writeByte(5);
        
    converter.write(this._relayId,session);
        
}

if(this._hostname!=null && (0 & transience)==0){
    out.writeByte(6);
        
    converter.write(this._hostname,session);
        
}

if(this._processUid!=null && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this._processUid,session);
        
}

if(this._centerId!=(byte)0 && (0 & transience)==0){
    out.writeByte(8);
        
    out.writeByte(1);
    out.writeByte(this._centerId);
        
}

if(this._guaranteedMessagingEnabled!=false && (0 & transience)==0){
    out.writeByte(9);
        
    out.writeByte(0);
    out.writeBoolean(this._guaranteedMessagingEnabled);
        
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