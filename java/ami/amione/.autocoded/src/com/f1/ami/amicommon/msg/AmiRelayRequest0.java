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

public abstract class AmiRelayRequest0 implements com.f1.ami.amicommon.msg.AmiRelayRequest ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private byte _centerId;

    private java.lang.String _invokedBy;

    private java.lang.String _targetAgentProcessUid;

    private static final String NAMES[]={ "centerId" ,"invokedBy","targetAgentProcessUid"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 6;
        try{
        switch(h){

                case 1:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {this._targetAgentProcessUid=(java.lang.String)value;return;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {this._centerId=(java.lang.Byte)value;return;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 6;
        switch(h){

                case 1:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {this._targetAgentProcessUid=(java.lang.String)value;return true;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {this._centerId=(java.lang.Byte)value;return true;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 6;
        switch(h){

                case 1:

		    
                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return this._targetAgentProcessUid;}
            
break;
                case 4:

		    
                    if(name == "centerId" || name.equals("centerId")) {return OH.valueOf(this._centerId);}
		    
break;
                case 5:

		    
                    if(name == "invokedBy" || name.equals("invokedBy")) {return this._invokedBy;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 6;
        switch(h){

                case 1:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return java.lang.String.class;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return byte.class;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return java.lang.String.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 6;
        switch(h){

                case 1:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return VALUED_PARAM_targetAgentProcessUid;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return VALUED_PARAM_centerId;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return VALUED_PARAM_invokedBy;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 6;
        switch(h){

                case 1:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return 2;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return 0;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 1;}
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
        return 3;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiRelayRequest0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 6;
        switch(h){

                case 1:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return true;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return true;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 6;
        switch(h){

                case 1:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return 20;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return 1;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 20;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _centerId;

        case 1:return _invokedBy;

        case 2:return _targetAgentProcessUid;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 3);
    }

    public byte getCenterId(){
        return this._centerId;
    }
    public void setCenterId(byte _centerId){
    
        this._centerId=_centerId;
    }

    public java.lang.String getInvokedBy(){
        return this._invokedBy;
    }
    public void setInvokedBy(java.lang.String _invokedBy){
    
        this._invokedBy=_invokedBy;
    }

    public java.lang.String getTargetAgentProcessUid(){
        return this._targetAgentProcessUid;
    }
    public void setTargetAgentProcessUid(java.lang.String _targetAgentProcessUid){
    
        this._targetAgentProcessUid=_targetAgentProcessUid;
    }





  
    private static final class VALUED_PARAM_CLASS_centerId extends AbstractValuedParam<AmiRelayRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiRelayRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getCenterId());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 0;
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
	    public Object getValue(AmiRelayRequest0 valued) {
		    return (byte)((AmiRelayRequest0)valued).getCenterId();
	    }
    
	    @Override
	    public void setValue(AmiRelayRequest0 valued, Object value) {
		    valued.setCenterId((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRequest0 source, AmiRelayRequest0 dest) {
		    dest.setCenterId(source.getCenterId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRequest0 source, AmiRelayRequest0 dest) {
	        return OH.eq(dest.getCenterId(),source.getCenterId());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiRelayRequest0 valued) {
		    return valued.getCenterId();
	    }
    
	    @Override
	    public void setByte(AmiRelayRequest0 valued, byte value) {
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
	    public void append(AmiRelayRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getCenterId());
	        
	    }
	    @Override
	    public void append(AmiRelayRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getCenterId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte centerId";
	    }
	    @Override
	    public void clear(AmiRelayRequest0 valued){
	       valued.setCenterId((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_centerId=new VALUED_PARAM_CLASS_centerId();
  

  
    private static final class VALUED_PARAM_CLASS_invokedBy extends AbstractValuedParam<AmiRelayRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 41;
	    }
    
	    @Override
	    public String getName() {
            return "invokedBy";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRequest0 valued) {
		    return (java.lang.String)((AmiRelayRequest0)valued).getInvokedBy();
	    }
    
	    @Override
	    public void setValue(AmiRelayRequest0 valued, Object value) {
		    valued.setInvokedBy((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRequest0 source, AmiRelayRequest0 dest) {
		    dest.setInvokedBy(source.getInvokedBy());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRequest0 source, AmiRelayRequest0 dest) {
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
	    public void append(AmiRelayRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String invokedBy";
	    }
	    @Override
	    public void clear(AmiRelayRequest0 valued){
	       valued.setInvokedBy(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_invokedBy=new VALUED_PARAM_CLASS_invokedBy();
  

  
    private static final class VALUED_PARAM_CLASS_targetAgentProcessUid extends AbstractValuedParam<AmiRelayRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 40;
	    }
    
	    @Override
	    public String getName() {
            return "targetAgentProcessUid";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRequest0 valued) {
		    return (java.lang.String)((AmiRelayRequest0)valued).getTargetAgentProcessUid();
	    }
    
	    @Override
	    public void setValue(AmiRelayRequest0 valued, Object value) {
		    valued.setTargetAgentProcessUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRequest0 source, AmiRelayRequest0 dest) {
		    dest.setTargetAgentProcessUid(source.getTargetAgentProcessUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRequest0 source, AmiRelayRequest0 dest) {
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
	    public void append(AmiRelayRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getTargetAgentProcessUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getTargetAgentProcessUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String targetAgentProcessUid";
	    }
	    @Override
	    public void clear(AmiRelayRequest0 valued){
	       valued.setTargetAgentProcessUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_targetAgentProcessUid=new VALUED_PARAM_CLASS_targetAgentProcessUid();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_centerId, VALUED_PARAM_invokedBy, VALUED_PARAM_targetAgentProcessUid, };



    private static final byte PIDS[]={ 42 ,41,40};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 42: return VALUED_PARAM_centerId;
             case 41: return VALUED_PARAM_invokedBy;
             case 40: return VALUED_PARAM_targetAgentProcessUid;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 42: return true;
             case 41: return true;
             case 40: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 42: return "centerId";
             case 41: return "invokedBy";
             case 40: return "targetAgentProcessUid";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 42: return 0;
             case 41: return 1;
             case 40: return 2;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="centerId") return 42;
             if(name=="invokedBy") return 41;
             if(name=="targetAgentProcessUid") return 40;
            
             if("centerId".equals(name)) return 42;
             if("invokedBy".equals(name)) return 41;
             if("targetAgentProcessUid".equals(name)) return 40;
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
             case 42: return  OH.valueOf(this._centerId); 
             case 41: return  this._invokedBy; 
             case 40: return  this._targetAgentProcessUid; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 42: return byte.class;
             case 41: return java.lang.String.class;
             case 40: return java.lang.String.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 42: return 1;
             case 41: return 20;
             case 40: return 20;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 42: this._centerId=(java.lang.Byte)value;return;
             case 41: this._invokedBy=(java.lang.String)value;return;
             case 40: this._targetAgentProcessUid=(java.lang.String)value;return;
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
             case 42: this._centerId=(java.lang.Byte)value;return true;
             case 41: this._invokedBy=(java.lang.String)value;return true;
             case 40: this._targetAgentProcessUid=(java.lang.String)value;return true;
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