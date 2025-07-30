//Coded by ValuedCodeTemplate
package com.f1.ami.amicommon.centerclient;

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

public abstract class AmiCenterClientGetSnapshotRequest0 implements com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,Cloneable{

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
    
    
    

    private java.util.Set _amiObjectTypesToSend;

    private java.util.Set _amiObjectTypesToStopSend;

    private java.lang.String _invokedBy;

    private long _requestTime;

    private java.lang.String _sessionUid;

    private static final String NAMES[]={ "amiObjectTypesToSend" ,"amiObjectTypesToStopSend","invokedBy","requestTime","sessionUid"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 11;
        try{
        switch(h){

                case 3:

                    if(name == "amiObjectTypesToStopSend" || name.equals("amiObjectTypesToStopSend")) {this._amiObjectTypesToStopSend=(java.util.Set)value;return;}
break;
                case 4:

                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {this._amiObjectTypesToSend=(java.util.Set)value;return;}
break;
                case 6:

                    if(name == "requestTime" || name.equals("requestTime")) {this._requestTime=(java.lang.Long)value;return;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return;}
break;
                case 10:

                    if(name == "sessionUid" || name.equals("sessionUid")) {this._sessionUid=(java.lang.String)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 3:

                    if(name == "amiObjectTypesToStopSend" || name.equals("amiObjectTypesToStopSend")) {this._amiObjectTypesToStopSend=(java.util.Set)value;return true;}
break;
                case 4:

                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {this._amiObjectTypesToSend=(java.util.Set)value;return true;}
break;
                case 6:

                    if(name == "requestTime" || name.equals("requestTime")) {this._requestTime=(java.lang.Long)value;return true;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return true;}
break;
                case 10:

                    if(name == "sessionUid" || name.equals("sessionUid")) {this._sessionUid=(java.lang.String)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 3:

		    
                    if(name == "amiObjectTypesToStopSend" || name.equals("amiObjectTypesToStopSend")) {return this._amiObjectTypesToStopSend;}
            
break;
                case 4:

		    
                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {return this._amiObjectTypesToSend;}
            
break;
                case 6:

		    
                    if(name == "requestTime" || name.equals("requestTime")) {return OH.valueOf(this._requestTime);}
		    
break;
                case 9:

		    
                    if(name == "invokedBy" || name.equals("invokedBy")) {return this._invokedBy;}
            
break;
                case 10:

		    
                    if(name == "sessionUid" || name.equals("sessionUid")) {return this._sessionUid;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 3:

                    if(name == "amiObjectTypesToStopSend" || name.equals("amiObjectTypesToStopSend")) {return java.util.Set.class;}
break;
                case 4:

                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {return java.util.Set.class;}
break;
                case 6:

                    if(name == "requestTime" || name.equals("requestTime")) {return long.class;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return java.lang.String.class;}
break;
                case 10:

                    if(name == "sessionUid" || name.equals("sessionUid")) {return java.lang.String.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 3:

                    if(name == "amiObjectTypesToStopSend" || name.equals("amiObjectTypesToStopSend")) {return VALUED_PARAM_amiObjectTypesToStopSend;}
break;
                case 4:

                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {return VALUED_PARAM_amiObjectTypesToSend;}
break;
                case 6:

                    if(name == "requestTime" || name.equals("requestTime")) {return VALUED_PARAM_requestTime;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return VALUED_PARAM_invokedBy;}
break;
                case 10:

                    if(name == "sessionUid" || name.equals("sessionUid")) {return VALUED_PARAM_sessionUid;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 3:

                    if(name == "amiObjectTypesToStopSend" || name.equals("amiObjectTypesToStopSend")) {return 1;}
break;
                case 4:

                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {return 0;}
break;
                case 6:

                    if(name == "requestTime" || name.equals("requestTime")) {return 3;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 2;}
break;
                case 10:

                    if(name == "sessionUid" || name.equals("sessionUid")) {return 4;}
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
	    return (Class)AmiCenterClientGetSnapshotRequest0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 3:

                    if(name == "amiObjectTypesToStopSend" || name.equals("amiObjectTypesToStopSend")) {return true;}
break;
                case 4:

                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {return true;}
break;
                case 6:

                    if(name == "requestTime" || name.equals("requestTime")) {return true;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return true;}
break;
                case 10:

                    if(name == "sessionUid" || name.equals("sessionUid")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 3:

                    if(name == "amiObjectTypesToStopSend" || name.equals("amiObjectTypesToStopSend")) {return 22;}
break;
                case 4:

                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {return 22;}
break;
                case 6:

                    if(name == "requestTime" || name.equals("requestTime")) {return 6;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 20;}
break;
                case 10:

                    if(name == "sessionUid" || name.equals("sessionUid")) {return 20;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _amiObjectTypesToSend;

        case 1:return _amiObjectTypesToStopSend;

        case 2:return _invokedBy;

        case 3:return _requestTime;

        case 4:return _sessionUid;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 5);
    }

    public java.util.Set getAmiObjectTypesToSend(){
        return this._amiObjectTypesToSend;
    }
    public void setAmiObjectTypesToSend(java.util.Set _amiObjectTypesToSend){
    
        this._amiObjectTypesToSend=_amiObjectTypesToSend;
    }

    public java.util.Set getAmiObjectTypesToStopSend(){
        return this._amiObjectTypesToStopSend;
    }
    public void setAmiObjectTypesToStopSend(java.util.Set _amiObjectTypesToStopSend){
    
        this._amiObjectTypesToStopSend=_amiObjectTypesToStopSend;
    }

    public java.lang.String getInvokedBy(){
        return this._invokedBy;
    }
    public void setInvokedBy(java.lang.String _invokedBy){
    
        this._invokedBy=_invokedBy;
    }

    public long getRequestTime(){
        return this._requestTime;
    }
    public void setRequestTime(long _requestTime){
    
        this._requestTime=_requestTime;
    }

    public java.lang.String getSessionUid(){
        return this._sessionUid;
    }
    public void setSessionUid(java.lang.String _sessionUid){
    
        this._sessionUid=_sessionUid;
    }





  
    private static final class VALUED_PARAM_CLASS_amiObjectTypesToSend extends AbstractValuedParam<AmiCenterClientGetSnapshotRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 22;
	    }
	    
	    @Override
	    public void write(AmiCenterClientGetSnapshotRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Set}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientGetSnapshotRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Set}");
		    
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "amiObjectTypesToSend";
	    }
    
	    @Override
	    public Object getValue(AmiCenterClientGetSnapshotRequest0 valued) {
		    return (java.util.Set)((AmiCenterClientGetSnapshotRequest0)valued).getAmiObjectTypesToSend();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientGetSnapshotRequest0 valued, Object value) {
		    valued.setAmiObjectTypesToSend((java.util.Set)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientGetSnapshotRequest0 source, AmiCenterClientGetSnapshotRequest0 dest) {
		    dest.setAmiObjectTypesToSend(source.getAmiObjectTypesToSend());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientGetSnapshotRequest0 source, AmiCenterClientGetSnapshotRequest0 dest) {
	        return OH.eq(dest.getAmiObjectTypesToSend(),source.getAmiObjectTypesToSend());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.util.Set.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.util.Set.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiCenterClientGetSnapshotRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAmiObjectTypesToSend());
	        
	    }
	    @Override
	    public void append(AmiCenterClientGetSnapshotRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAmiObjectTypesToSend());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.Set amiObjectTypesToSend";
	    }
	    @Override
	    public void clear(AmiCenterClientGetSnapshotRequest0 valued){
	       valued.setAmiObjectTypesToSend(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_amiObjectTypesToSend=new VALUED_PARAM_CLASS_amiObjectTypesToSend();
  

  
    private static final class VALUED_PARAM_CLASS_amiObjectTypesToStopSend extends AbstractValuedParam<AmiCenterClientGetSnapshotRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 22;
	    }
	    
	    @Override
	    public void write(AmiCenterClientGetSnapshotRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Set}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientGetSnapshotRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Set}");
		    
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
		    return 1;
	    }
    
	    @Override
	    public byte getPid() {
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "amiObjectTypesToStopSend";
	    }
    
	    @Override
	    public Object getValue(AmiCenterClientGetSnapshotRequest0 valued) {
		    return (java.util.Set)((AmiCenterClientGetSnapshotRequest0)valued).getAmiObjectTypesToStopSend();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientGetSnapshotRequest0 valued, Object value) {
		    valued.setAmiObjectTypesToStopSend((java.util.Set)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientGetSnapshotRequest0 source, AmiCenterClientGetSnapshotRequest0 dest) {
		    dest.setAmiObjectTypesToStopSend(source.getAmiObjectTypesToStopSend());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientGetSnapshotRequest0 source, AmiCenterClientGetSnapshotRequest0 dest) {
	        return OH.eq(dest.getAmiObjectTypesToStopSend(),source.getAmiObjectTypesToStopSend());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.util.Set.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.util.Set.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiCenterClientGetSnapshotRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAmiObjectTypesToStopSend());
	        
	    }
	    @Override
	    public void append(AmiCenterClientGetSnapshotRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAmiObjectTypesToStopSend());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.Set amiObjectTypesToStopSend";
	    }
	    @Override
	    public void clear(AmiCenterClientGetSnapshotRequest0 valued){
	       valued.setAmiObjectTypesToStopSend(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_amiObjectTypesToStopSend=new VALUED_PARAM_CLASS_amiObjectTypesToStopSend();
  

  
    private static final class VALUED_PARAM_CLASS_invokedBy extends AbstractValuedParam<AmiCenterClientGetSnapshotRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterClientGetSnapshotRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientGetSnapshotRequest0 valued, DataInput stream) throws IOException{
		    
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "invokedBy";
	    }
    
	    @Override
	    public Object getValue(AmiCenterClientGetSnapshotRequest0 valued) {
		    return (java.lang.String)((AmiCenterClientGetSnapshotRequest0)valued).getInvokedBy();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientGetSnapshotRequest0 valued, Object value) {
		    valued.setInvokedBy((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientGetSnapshotRequest0 source, AmiCenterClientGetSnapshotRequest0 dest) {
		    dest.setInvokedBy(source.getInvokedBy());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientGetSnapshotRequest0 source, AmiCenterClientGetSnapshotRequest0 dest) {
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
	    public void append(AmiCenterClientGetSnapshotRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterClientGetSnapshotRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String invokedBy";
	    }
	    @Override
	    public void clear(AmiCenterClientGetSnapshotRequest0 valued){
	       valued.setInvokedBy(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_invokedBy=new VALUED_PARAM_CLASS_invokedBy();
  

  
    private static final class VALUED_PARAM_CLASS_requestTime extends AbstractValuedParam<AmiCenterClientGetSnapshotRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterClientGetSnapshotRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getRequestTime());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientGetSnapshotRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 3;
	    }
    
	    @Override
	    public byte getPid() {
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "requestTime";
	    }
    
	    @Override
	    public Object getValue(AmiCenterClientGetSnapshotRequest0 valued) {
		    return (long)((AmiCenterClientGetSnapshotRequest0)valued).getRequestTime();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientGetSnapshotRequest0 valued, Object value) {
		    valued.setRequestTime((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientGetSnapshotRequest0 source, AmiCenterClientGetSnapshotRequest0 dest) {
		    dest.setRequestTime(source.getRequestTime());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientGetSnapshotRequest0 source, AmiCenterClientGetSnapshotRequest0 dest) {
	        return OH.eq(dest.getRequestTime(),source.getRequestTime());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterClientGetSnapshotRequest0 valued) {
		    return valued.getRequestTime();
	    }
    
	    @Override
	    public void setLong(AmiCenterClientGetSnapshotRequest0 valued, long value) {
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
	    public void append(AmiCenterClientGetSnapshotRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getRequestTime());
	        
	    }
	    @Override
	    public void append(AmiCenterClientGetSnapshotRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getRequestTime());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long requestTime";
	    }
	    @Override
	    public void clear(AmiCenterClientGetSnapshotRequest0 valued){
	       valued.setRequestTime(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_requestTime=new VALUED_PARAM_CLASS_requestTime();
  

  
    private static final class VALUED_PARAM_CLASS_sessionUid extends AbstractValuedParam<AmiCenterClientGetSnapshotRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterClientGetSnapshotRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientGetSnapshotRequest0 valued, DataInput stream) throws IOException{
		    
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "sessionUid";
	    }
    
	    @Override
	    public Object getValue(AmiCenterClientGetSnapshotRequest0 valued) {
		    return (java.lang.String)((AmiCenterClientGetSnapshotRequest0)valued).getSessionUid();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientGetSnapshotRequest0 valued, Object value) {
		    valued.setSessionUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientGetSnapshotRequest0 source, AmiCenterClientGetSnapshotRequest0 dest) {
		    dest.setSessionUid(source.getSessionUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientGetSnapshotRequest0 source, AmiCenterClientGetSnapshotRequest0 dest) {
	        return OH.eq(dest.getSessionUid(),source.getSessionUid());
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
	    public void append(AmiCenterClientGetSnapshotRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getSessionUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterClientGetSnapshotRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getSessionUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String sessionUid";
	    }
	    @Override
	    public void clear(AmiCenterClientGetSnapshotRequest0 valued){
	       valued.setSessionUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_sessionUid=new VALUED_PARAM_CLASS_sessionUid();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_amiObjectTypesToSend, VALUED_PARAM_amiObjectTypesToStopSend, VALUED_PARAM_invokedBy, VALUED_PARAM_requestTime, VALUED_PARAM_sessionUid, };


    public boolean askSupportsPids(){
        return false;
    }
    
    public byte[] askPids(){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
        
    }

    public String askParam(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public int askPosition(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public byte askPid(String name){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public Object ask(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public Class askClass(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public byte askBasicType(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public void put(byte pid,Object value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public boolean putNoThrow(byte pid,Object value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public boolean askBoolean(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public byte askByte(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public short askShort(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public char askChar(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public int askInt(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public float askFloat(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public long askLong(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public double askDouble(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putBoolean(byte pid, boolean value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putByte(byte pid, byte value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putShort(byte pid, short value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putChar(byte pid, char value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putInt(byte pid, int value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putFloat(byte pid, float value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putLong(byte pid, long value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putDouble(byte pid, double value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
        
    public boolean askPidValid(byte param){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public ValuedParam askValuedParam(byte param){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
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