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

public abstract class AmiWebLoginResponse0 implements com.f1.ami.amicommon.msg.AmiWebLoginResponse ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private java.util.Map _attributes;

    private int _failedLoginAttempts;

    private java.lang.String _message;

    private byte _status;

    private java.lang.String _username;

    private static final String NAMES[]={ "attributes" ,"failedLoginAttempts","message","status","username"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 17;
        try{
        switch(h){

                case 0:

                    if(name == "failedLoginAttempts" || name.equals("failedLoginAttempts")) {this._failedLoginAttempts=(java.lang.Integer)value;return;}
break;
                case 2:

                    if(name == "attributes" || name.equals("attributes")) {this._attributes=(java.util.Map)value;return;}
break;
                case 9:

                    if(name == "message" || name.equals("message")) {this._message=(java.lang.String)value;return;}
break;
                case 12:

                    if(name == "status" || name.equals("status")) {this._status=(java.lang.Byte)value;return;}
break;
                case 16:

                    if(name == "username" || name.equals("username")) {this._username=(java.lang.String)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 17;
        switch(h){

                case 0:

                    if(name == "failedLoginAttempts" || name.equals("failedLoginAttempts")) {this._failedLoginAttempts=(java.lang.Integer)value;return true;}
break;
                case 2:

                    if(name == "attributes" || name.equals("attributes")) {this._attributes=(java.util.Map)value;return true;}
break;
                case 9:

                    if(name == "message" || name.equals("message")) {this._message=(java.lang.String)value;return true;}
break;
                case 12:

                    if(name == "status" || name.equals("status")) {this._status=(java.lang.Byte)value;return true;}
break;
                case 16:

                    if(name == "username" || name.equals("username")) {this._username=(java.lang.String)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 17;
        switch(h){

                case 0:

		    
                    if(name == "failedLoginAttempts" || name.equals("failedLoginAttempts")) {return OH.valueOf(this._failedLoginAttempts);}
		    
break;
                case 2:

		    
                    if(name == "attributes" || name.equals("attributes")) {return this._attributes;}
            
break;
                case 9:

		    
                    if(name == "message" || name.equals("message")) {return this._message;}
            
break;
                case 12:

		    
                    if(name == "status" || name.equals("status")) {return OH.valueOf(this._status);}
		    
break;
                case 16:

		    
                    if(name == "username" || name.equals("username")) {return this._username;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 17;
        switch(h){

                case 0:

                    if(name == "failedLoginAttempts" || name.equals("failedLoginAttempts")) {return int.class;}
break;
                case 2:

                    if(name == "attributes" || name.equals("attributes")) {return java.util.Map.class;}
break;
                case 9:

                    if(name == "message" || name.equals("message")) {return java.lang.String.class;}
break;
                case 12:

                    if(name == "status" || name.equals("status")) {return byte.class;}
break;
                case 16:

                    if(name == "username" || name.equals("username")) {return java.lang.String.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 17;
        switch(h){

                case 0:

                    if(name == "failedLoginAttempts" || name.equals("failedLoginAttempts")) {return VALUED_PARAM_failedLoginAttempts;}
break;
                case 2:

                    if(name == "attributes" || name.equals("attributes")) {return VALUED_PARAM_attributes;}
break;
                case 9:

                    if(name == "message" || name.equals("message")) {return VALUED_PARAM_message;}
break;
                case 12:

                    if(name == "status" || name.equals("status")) {return VALUED_PARAM_status;}
break;
                case 16:

                    if(name == "username" || name.equals("username")) {return VALUED_PARAM_username;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 17;
        switch(h){

                case 0:

                    if(name == "failedLoginAttempts" || name.equals("failedLoginAttempts")) {return 1;}
break;
                case 2:

                    if(name == "attributes" || name.equals("attributes")) {return 0;}
break;
                case 9:

                    if(name == "message" || name.equals("message")) {return 2;}
break;
                case 12:

                    if(name == "status" || name.equals("status")) {return 3;}
break;
                case 16:

                    if(name == "username" || name.equals("username")) {return 4;}
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
        return 5;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiWebLoginResponse0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 17;
        switch(h){

                case 0:

                    if(name == "failedLoginAttempts" || name.equals("failedLoginAttempts")) {return true;}
break;
                case 2:

                    if(name == "attributes" || name.equals("attributes")) {return true;}
break;
                case 9:

                    if(name == "message" || name.equals("message")) {return true;}
break;
                case 12:

                    if(name == "status" || name.equals("status")) {return true;}
break;
                case 16:

                    if(name == "username" || name.equals("username")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 17;
        switch(h){

                case 0:

                    if(name == "failedLoginAttempts" || name.equals("failedLoginAttempts")) {return 4;}
break;
                case 2:

                    if(name == "attributes" || name.equals("attributes")) {return 23;}
break;
                case 9:

                    if(name == "message" || name.equals("message")) {return 20;}
break;
                case 12:

                    if(name == "status" || name.equals("status")) {return 1;}
break;
                case 16:

                    if(name == "username" || name.equals("username")) {return 20;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _attributes;

        case 1:return _failedLoginAttempts;

        case 2:return _message;

        case 3:return _status;

        case 4:return _username;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 5);
    }

    public java.util.Map getAttributes(){
        return this._attributes;
    }
    public void setAttributes(java.util.Map _attributes){
    
        this._attributes=_attributes;
    }

    public int getFailedLoginAttempts(){
        return this._failedLoginAttempts;
    }
    public void setFailedLoginAttempts(int _failedLoginAttempts){
    
        this._failedLoginAttempts=_failedLoginAttempts;
    }

    public java.lang.String getMessage(){
        return this._message;
    }
    public void setMessage(java.lang.String _message){
    
        this._message=_message;
    }

    public byte getStatus(){
        return this._status;
    }
    public void setStatus(byte _status){
    
        this._status=_status;
    }

    public java.lang.String getUsername(){
        return this._username;
    }
    public void setUsername(java.lang.String _username){
    
        this._username=_username;
    }





  
    private static final class VALUED_PARAM_CLASS_attributes extends AbstractValuedParam<AmiWebLoginResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 23;
	    }
	    
	    @Override
	    public void write(AmiWebLoginResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Map}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebLoginResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 0;
	    }
    
	    @Override
	    public byte getPid() {
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "attributes";
	    }
    
	    @Override
	    public Object getValue(AmiWebLoginResponse0 valued) {
		    return (java.util.Map)((AmiWebLoginResponse0)valued).getAttributes();
	    }
    
	    @Override
	    public void setValue(AmiWebLoginResponse0 valued, Object value) {
		    valued.setAttributes((java.util.Map)value);
	    }
    
	    @Override
	    public void copy(AmiWebLoginResponse0 source, AmiWebLoginResponse0 dest) {
		    dest.setAttributes(source.getAttributes());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebLoginResponse0 source, AmiWebLoginResponse0 dest) {
	        return OH.eq(dest.getAttributes(),source.getAttributes());
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
	    public void append(AmiWebLoginResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAttributes());
	        
	    }
	    @Override
	    public void append(AmiWebLoginResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAttributes());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.Map attributes";
	    }
	    @Override
	    public void clear(AmiWebLoginResponse0 valued){
	       valued.setAttributes(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_attributes=new VALUED_PARAM_CLASS_attributes();
  

  
    private static final class VALUED_PARAM_CLASS_failedLoginAttempts extends AbstractValuedParam<AmiWebLoginResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiWebLoginResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getFailedLoginAttempts());
		    
	    }
	    
	    @Override
	    public void read(AmiWebLoginResponse0 valued, DataInput stream) throws IOException{
		    
		      valued.setFailedLoginAttempts(stream.readInt());
		    
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
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "failedLoginAttempts";
	    }
    
	    @Override
	    public Object getValue(AmiWebLoginResponse0 valued) {
		    return (int)((AmiWebLoginResponse0)valued).getFailedLoginAttempts();
	    }
    
	    @Override
	    public void setValue(AmiWebLoginResponse0 valued, Object value) {
		    valued.setFailedLoginAttempts((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiWebLoginResponse0 source, AmiWebLoginResponse0 dest) {
		    dest.setFailedLoginAttempts(source.getFailedLoginAttempts());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebLoginResponse0 source, AmiWebLoginResponse0 dest) {
	        return OH.eq(dest.getFailedLoginAttempts(),source.getFailedLoginAttempts());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiWebLoginResponse0 valued) {
		    return valued.getFailedLoginAttempts();
	    }
    
	    @Override
	    public void setInt(AmiWebLoginResponse0 valued, int value) {
		    valued.setFailedLoginAttempts(value);
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
	    public void append(AmiWebLoginResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getFailedLoginAttempts());
	        
	    }
	    @Override
	    public void append(AmiWebLoginResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getFailedLoginAttempts());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int failedLoginAttempts";
	    }
	    @Override
	    public void clear(AmiWebLoginResponse0 valued){
	       valued.setFailedLoginAttempts(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_failedLoginAttempts=new VALUED_PARAM_CLASS_failedLoginAttempts();
  

  
    private static final class VALUED_PARAM_CLASS_message extends AbstractValuedParam<AmiWebLoginResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebLoginResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebLoginResponse0 valued, DataInput stream) throws IOException{
		    
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
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "message";
	    }
    
	    @Override
	    public Object getValue(AmiWebLoginResponse0 valued) {
		    return (java.lang.String)((AmiWebLoginResponse0)valued).getMessage();
	    }
    
	    @Override
	    public void setValue(AmiWebLoginResponse0 valued, Object value) {
		    valued.setMessage((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebLoginResponse0 source, AmiWebLoginResponse0 dest) {
		    dest.setMessage(source.getMessage());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebLoginResponse0 source, AmiWebLoginResponse0 dest) {
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
	    public void append(AmiWebLoginResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getMessage(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebLoginResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getMessage(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String message";
	    }
	    @Override
	    public void clear(AmiWebLoginResponse0 valued){
	       valued.setMessage(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_message=new VALUED_PARAM_CLASS_message();
  

  
    private static final class VALUED_PARAM_CLASS_status extends AbstractValuedParam<AmiWebLoginResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiWebLoginResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getStatus());
		    
	    }
	    
	    @Override
	    public void read(AmiWebLoginResponse0 valued, DataInput stream) throws IOException{
		    
		      valued.setStatus(stream.readByte());
		    
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
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "status";
	    }
    
	    @Override
	    public Object getValue(AmiWebLoginResponse0 valued) {
		    return (byte)((AmiWebLoginResponse0)valued).getStatus();
	    }
    
	    @Override
	    public void setValue(AmiWebLoginResponse0 valued, Object value) {
		    valued.setStatus((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiWebLoginResponse0 source, AmiWebLoginResponse0 dest) {
		    dest.setStatus(source.getStatus());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebLoginResponse0 source, AmiWebLoginResponse0 dest) {
	        return OH.eq(dest.getStatus(),source.getStatus());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiWebLoginResponse0 valued) {
		    return valued.getStatus();
	    }
    
	    @Override
	    public void setByte(AmiWebLoginResponse0 valued, byte value) {
		    valued.setStatus(value);
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
	    public void append(AmiWebLoginResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getStatus());
	        
	    }
	    @Override
	    public void append(AmiWebLoginResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getStatus());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte status";
	    }
	    @Override
	    public void clear(AmiWebLoginResponse0 valued){
	       valued.setStatus((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_status=new VALUED_PARAM_CLASS_status();
  

  
    private static final class VALUED_PARAM_CLASS_username extends AbstractValuedParam<AmiWebLoginResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebLoginResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebLoginResponse0 valued, DataInput stream) throws IOException{
		    
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
            return "username";
	    }
    
	    @Override
	    public Object getValue(AmiWebLoginResponse0 valued) {
		    return (java.lang.String)((AmiWebLoginResponse0)valued).getUsername();
	    }
    
	    @Override
	    public void setValue(AmiWebLoginResponse0 valued, Object value) {
		    valued.setUsername((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebLoginResponse0 source, AmiWebLoginResponse0 dest) {
		    dest.setUsername(source.getUsername());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebLoginResponse0 source, AmiWebLoginResponse0 dest) {
	        return OH.eq(dest.getUsername(),source.getUsername());
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
	    public void append(AmiWebLoginResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getUsername(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebLoginResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getUsername(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String username";
	    }
	    @Override
	    public void clear(AmiWebLoginResponse0 valued){
	       valued.setUsername(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_username=new VALUED_PARAM_CLASS_username();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_attributes, VALUED_PARAM_failedLoginAttempts, VALUED_PARAM_message, VALUED_PARAM_status, VALUED_PARAM_username, };



    private static final byte PIDS[]={ 5 ,3,4,2,6};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 5: return VALUED_PARAM_attributes;
             case 3: return VALUED_PARAM_failedLoginAttempts;
             case 4: return VALUED_PARAM_message;
             case 2: return VALUED_PARAM_status;
             case 6: return VALUED_PARAM_username;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 5: return true;
             case 3: return true;
             case 4: return true;
             case 2: return true;
             case 6: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 5: return "attributes";
             case 3: return "failedLoginAttempts";
             case 4: return "message";
             case 2: return "status";
             case 6: return "username";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 5: return 0;
             case 3: return 1;
             case 4: return 2;
             case 2: return 3;
             case 6: return 4;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="attributes") return 5;
             if(name=="failedLoginAttempts") return 3;
             if(name=="message") return 4;
             if(name=="status") return 2;
             if(name=="username") return 6;
            
             if("attributes".equals(name)) return 5;
             if("failedLoginAttempts".equals(name)) return 3;
             if("message".equals(name)) return 4;
             if("status".equals(name)) return 2;
             if("username".equals(name)) return 6;
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
             case 5: return  this._attributes; 
             case 3: return  OH.valueOf(this._failedLoginAttempts); 
             case 4: return  this._message; 
             case 2: return  OH.valueOf(this._status); 
             case 6: return  this._username; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 5: return java.util.Map.class;
             case 3: return int.class;
             case 4: return java.lang.String.class;
             case 2: return byte.class;
             case 6: return java.lang.String.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 5: return 23;
             case 3: return 4;
             case 4: return 20;
             case 2: return 1;
             case 6: return 20;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 5: this._attributes=(java.util.Map)value;return;
             case 3: this._failedLoginAttempts=(java.lang.Integer)value;return;
             case 4: this._message=(java.lang.String)value;return;
             case 2: this._status=(java.lang.Byte)value;return;
             case 6: this._username=(java.lang.String)value;return;
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
             case 5: this._attributes=(java.util.Map)value;return true;
             case 3: this._failedLoginAttempts=(java.lang.Integer)value;return true;
             case 4: this._message=(java.lang.String)value;return true;
             case 2: this._status=(java.lang.Byte)value;return true;
             case 6: this._username=(java.lang.String)value;return true;
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
             case 2: return this._status;
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
             case 3: return this._failedLoginAttempts;
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
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putByte(byte pid,byte value){
    
        switch(pid){
             case 2: this._status=value;return;
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
             case 3: this._failedLoginAttempts=value;return;
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
        
            if((basicType=in.readByte())!=1)
                break;
            this._status=in.readByte();
        
            break;

        case 3:
        
            if((basicType=in.readByte())!=4)
                break;
            this._failedLoginAttempts=in.readInt();
        
            break;

        case 4:
        
            this._message=(java.lang.String)converter.read(session);
        
            break;

        case 5:
        
            this._attributes=(java.util.Map)converter.read(session);
        
            break;

        case 6:
        
            this._username=(java.lang.String)converter.read(session);
        
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
        
if(this._status!=(byte)0 && (0 & transience)==0){
    out.writeByte(2);
        
    out.writeByte(1);
    out.writeByte(this._status);
        
}

if(this._failedLoginAttempts!=0 && (0 & transience)==0){
    out.writeByte(3);
        
    out.writeByte(4);
    out.writeInt(this._failedLoginAttempts);
        
}

if(this._message!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._message,session);
        
}

if(this._attributes!=null && (0 & transience)==0){
    out.writeByte(5);
        
    converter.write(this._attributes,session);
        
}

if(this._username!=null && (0 & transience)==0){
    out.writeByte(6);
        
    converter.write(this._username,session);
        
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