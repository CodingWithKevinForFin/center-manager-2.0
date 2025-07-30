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

public abstract class AmiWebLoginRequest0 implements com.f1.ami.amicommon.msg.AmiWebLoginRequest ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private java.lang.String _clientAgent;

    private java.lang.String _clientLocation;

    private com.f1.base.Password _password;

    private java.lang.String _userName;

    private static final String NAMES[]={ "clientAgent" ,"clientLocation","password","userName"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 7;
        try{
        switch(h){

                case 1:

                    if(name == "clientAgent" || name.equals("clientAgent")) {this._clientAgent=(java.lang.String)value;return;}
break;
                case 2:

                    if(name == "clientLocation" || name.equals("clientLocation")) {this._clientLocation=(java.lang.String)value;return;}
break;
                case 5:

                    if(name == "userName" || name.equals("userName")) {this._userName=(java.lang.String)value;return;}
break;
                case 6:

                    if(name == "password" || name.equals("password")) {this._password=(com.f1.base.Password)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "clientAgent" || name.equals("clientAgent")) {this._clientAgent=(java.lang.String)value;return true;}
break;
                case 2:

                    if(name == "clientLocation" || name.equals("clientLocation")) {this._clientLocation=(java.lang.String)value;return true;}
break;
                case 5:

                    if(name == "userName" || name.equals("userName")) {this._userName=(java.lang.String)value;return true;}
break;
                case 6:

                    if(name == "password" || name.equals("password")) {this._password=(com.f1.base.Password)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

		    
                    if(name == "clientAgent" || name.equals("clientAgent")) {return this._clientAgent;}
            
break;
                case 2:

		    
                    if(name == "clientLocation" || name.equals("clientLocation")) {return this._clientLocation;}
            
break;
                case 5:

		    
                    if(name == "userName" || name.equals("userName")) {return this._userName;}
            
break;
                case 6:

		    
                    if(name == "password" || name.equals("password")) {return this._password;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "clientAgent" || name.equals("clientAgent")) {return java.lang.String.class;}
break;
                case 2:

                    if(name == "clientLocation" || name.equals("clientLocation")) {return java.lang.String.class;}
break;
                case 5:

                    if(name == "userName" || name.equals("userName")) {return java.lang.String.class;}
break;
                case 6:

                    if(name == "password" || name.equals("password")) {return com.f1.base.Password.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "clientAgent" || name.equals("clientAgent")) {return VALUED_PARAM_clientAgent;}
break;
                case 2:

                    if(name == "clientLocation" || name.equals("clientLocation")) {return VALUED_PARAM_clientLocation;}
break;
                case 5:

                    if(name == "userName" || name.equals("userName")) {return VALUED_PARAM_userName;}
break;
                case 6:

                    if(name == "password" || name.equals("password")) {return VALUED_PARAM_password;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "clientAgent" || name.equals("clientAgent")) {return 0;}
break;
                case 2:

                    if(name == "clientLocation" || name.equals("clientLocation")) {return 1;}
break;
                case 5:

                    if(name == "userName" || name.equals("userName")) {return 3;}
break;
                case 6:

                    if(name == "password" || name.equals("password")) {return 2;}
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
        return 4;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiWebLoginRequest0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "clientAgent" || name.equals("clientAgent")) {return true;}
break;
                case 2:

                    if(name == "clientLocation" || name.equals("clientLocation")) {return true;}
break;
                case 5:

                    if(name == "userName" || name.equals("userName")) {return true;}
break;
                case 6:

                    if(name == "password" || name.equals("password")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "clientAgent" || name.equals("clientAgent")) {return 20;}
break;
                case 2:

                    if(name == "clientLocation" || name.equals("clientLocation")) {return 20;}
break;
                case 5:

                    if(name == "userName" || name.equals("userName")) {return 20;}
break;
                case 6:

                    if(name == "password" || name.equals("password")) {return 24;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _clientAgent;

        case 1:return _clientLocation;

        case 2:return _password;

        case 3:return _userName;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 4);
    }

    public java.lang.String getClientAgent(){
        return this._clientAgent;
    }
    public void setClientAgent(java.lang.String _clientAgent){
    
        this._clientAgent=_clientAgent;
    }

    public java.lang.String getClientLocation(){
        return this._clientLocation;
    }
    public void setClientLocation(java.lang.String _clientLocation){
    
        this._clientLocation=_clientLocation;
    }

    public com.f1.base.Password getPassword(){
        return this._password;
    }
    public void setPassword(com.f1.base.Password _password){
    
        this._password=_password;
    }

    public java.lang.String getUserName(){
        return this._userName;
    }
    public void setUserName(java.lang.String _userName){
    
        this._userName=_userName;
    }





  
    private static final class VALUED_PARAM_CLASS_clientAgent extends AbstractValuedParam<AmiWebLoginRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebLoginRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebLoginRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "clientAgent";
	    }
    
	    @Override
	    public Object getValue(AmiWebLoginRequest0 valued) {
		    return (java.lang.String)((AmiWebLoginRequest0)valued).getClientAgent();
	    }
    
	    @Override
	    public void setValue(AmiWebLoginRequest0 valued, Object value) {
		    valued.setClientAgent((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebLoginRequest0 source, AmiWebLoginRequest0 dest) {
		    dest.setClientAgent(source.getClientAgent());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebLoginRequest0 source, AmiWebLoginRequest0 dest) {
	        return OH.eq(dest.getClientAgent(),source.getClientAgent());
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
	    public void append(AmiWebLoginRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getClientAgent(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebLoginRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getClientAgent(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String clientAgent";
	    }
	    @Override
	    public void clear(AmiWebLoginRequest0 valued){
	       valued.setClientAgent(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_clientAgent=new VALUED_PARAM_CLASS_clientAgent();
  

  
    private static final class VALUED_PARAM_CLASS_clientLocation extends AbstractValuedParam<AmiWebLoginRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebLoginRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebLoginRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 8;
	    }
    
	    @Override
	    public String getName() {
            return "clientLocation";
	    }
    
	    @Override
	    public Object getValue(AmiWebLoginRequest0 valued) {
		    return (java.lang.String)((AmiWebLoginRequest0)valued).getClientLocation();
	    }
    
	    @Override
	    public void setValue(AmiWebLoginRequest0 valued, Object value) {
		    valued.setClientLocation((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebLoginRequest0 source, AmiWebLoginRequest0 dest) {
		    dest.setClientLocation(source.getClientLocation());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebLoginRequest0 source, AmiWebLoginRequest0 dest) {
	        return OH.eq(dest.getClientLocation(),source.getClientLocation());
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
	    public void append(AmiWebLoginRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getClientLocation(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebLoginRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getClientLocation(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String clientLocation";
	    }
	    @Override
	    public void clear(AmiWebLoginRequest0 valued){
	       valued.setClientLocation(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_clientLocation=new VALUED_PARAM_CLASS_clientLocation();
  

  
    private static final class VALUED_PARAM_CLASS_password extends AbstractValuedParam<AmiWebLoginRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 24;
	    }
	    
	    @Override
	    public void write(AmiWebLoginRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.base.Password}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebLoginRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 2;
	    }
    
	    @Override
	    public byte getPid() {
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "password";
	    }
    
	    @Override
	    public Object getValue(AmiWebLoginRequest0 valued) {
		    return (com.f1.base.Password)((AmiWebLoginRequest0)valued).getPassword();
	    }
    
	    @Override
	    public void setValue(AmiWebLoginRequest0 valued, Object value) {
		    valued.setPassword((com.f1.base.Password)value);
	    }
    
	    @Override
	    public void copy(AmiWebLoginRequest0 source, AmiWebLoginRequest0 dest) {
		    dest.setPassword(source.getPassword());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebLoginRequest0 source, AmiWebLoginRequest0 dest) {
	        return OH.eq(dest.getPassword(),source.getPassword());
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
	    public void append(AmiWebLoginRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPassword());
	        
	    }
	    @Override
	    public void append(AmiWebLoginRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPassword());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.base.Password password";
	    }
	    @Override
	    public void clear(AmiWebLoginRequest0 valued){
	       valued.setPassword(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_password=new VALUED_PARAM_CLASS_password();
  

  
    private static final class VALUED_PARAM_CLASS_userName extends AbstractValuedParam<AmiWebLoginRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebLoginRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebLoginRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "userName";
	    }
    
	    @Override
	    public Object getValue(AmiWebLoginRequest0 valued) {
		    return (java.lang.String)((AmiWebLoginRequest0)valued).getUserName();
	    }
    
	    @Override
	    public void setValue(AmiWebLoginRequest0 valued, Object value) {
		    valued.setUserName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebLoginRequest0 source, AmiWebLoginRequest0 dest) {
		    dest.setUserName(source.getUserName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebLoginRequest0 source, AmiWebLoginRequest0 dest) {
	        return OH.eq(dest.getUserName(),source.getUserName());
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
	    public void append(AmiWebLoginRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getUserName(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebLoginRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getUserName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String userName";
	    }
	    @Override
	    public void clear(AmiWebLoginRequest0 valued){
	       valued.setUserName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_userName=new VALUED_PARAM_CLASS_userName();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_clientAgent, VALUED_PARAM_clientLocation, VALUED_PARAM_password, VALUED_PARAM_userName, };



    private static final byte PIDS[]={ 7 ,8,3,1};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 7: return VALUED_PARAM_clientAgent;
             case 8: return VALUED_PARAM_clientLocation;
             case 3: return VALUED_PARAM_password;
             case 1: return VALUED_PARAM_userName;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 7: return true;
             case 8: return true;
             case 3: return true;
             case 1: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 7: return "clientAgent";
             case 8: return "clientLocation";
             case 3: return "password";
             case 1: return "userName";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 7: return 0;
             case 8: return 1;
             case 3: return 2;
             case 1: return 3;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="clientAgent") return 7;
             if(name=="clientLocation") return 8;
             if(name=="password") return 3;
             if(name=="userName") return 1;
            
             if("clientAgent".equals(name)) return 7;
             if("clientLocation".equals(name)) return 8;
             if("password".equals(name)) return 3;
             if("userName".equals(name)) return 1;
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
             case 7: return  this._clientAgent; 
             case 8: return  this._clientLocation; 
             case 3: return  this._password; 
             case 1: return  this._userName; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 7: return java.lang.String.class;
             case 8: return java.lang.String.class;
             case 3: return com.f1.base.Password.class;
             case 1: return java.lang.String.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 7: return 20;
             case 8: return 20;
             case 3: return 24;
             case 1: return 20;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 7: this._clientAgent=(java.lang.String)value;return;
             case 8: this._clientLocation=(java.lang.String)value;return;
             case 3: this._password=(com.f1.base.Password)value;return;
             case 1: this._userName=(java.lang.String)value;return;
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
             case 7: this._clientAgent=(java.lang.String)value;return true;
             case 8: this._clientLocation=(java.lang.String)value;return true;
             case 3: this._password=(com.f1.base.Password)value;return true;
             case 1: this._userName=(java.lang.String)value;return true;
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
        
        case 1:
        
            this._userName=(java.lang.String)converter.read(session);
        
            break;

        case 3:
        
            this._password=(com.f1.base.Password)converter.read(session);
        
            break;

        case 7:
        
            this._clientAgent=(java.lang.String)converter.read(session);
        
            break;

        case 8:
        
            this._clientLocation=(java.lang.String)converter.read(session);
        
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
        
if(this._userName!=null && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this._userName,session);
        
}

if(this._password!=null && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this._password,session);
        
}

if(this._clientAgent!=null && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this._clientAgent,session);
        
}

if(this._clientLocation!=null && (0 & transience)==0){
    out.writeByte(8);
        
    converter.write(this._clientLocation,session);
        
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