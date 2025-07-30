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

public abstract class AmiCenterClientSnapshot0 implements com.f1.ami.amicommon.centerclient.AmiCenterClientSnapshot ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,Cloneable{

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
    
    
    

    private java.util.List _cached;

    private byte _centerId;

    private java.lang.String _invokedBy;

    private com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest _origRequest;

    private java.lang.String _processUid;

    private long _seqNum;

    private java.lang.String _sessionUid;

    private java.util.Set _types;

    private static final String NAMES[]={ "cached" ,"centerId","invokedBy","origRequest","processUid","seqNum","sessionUid","types"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 22;
        try{
        switch(h){

                case 4:

                    if(name == "centerId" || name.equals("centerId")) {this._centerId=(java.lang.Byte)value;return;}
break;
                case 7:

                    if(name == "processUid" || name.equals("processUid")) {this._processUid=(java.lang.String)value;return;}
break;
                case 8:

                    if(name == "cached" || name.equals("cached")) {this._cached=(java.util.List)value;return;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return;}
break;
                case 10:

                    if(name == "sessionUid" || name.equals("sessionUid")) {this._sessionUid=(java.lang.String)value;return;}
break;
                case 13:

                    if(name == "seqNum" || name.equals("seqNum")) {this._seqNum=(java.lang.Long)value;return;}
break;
                case 17:

                    if(name == "types" || name.equals("types")) {this._types=(java.util.Set)value;return;}
break;
                case 18:

                    if(name == "origRequest" || name.equals("origRequest")) {this._origRequest=(com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 22;
        switch(h){

                case 4:

                    if(name == "centerId" || name.equals("centerId")) {this._centerId=(java.lang.Byte)value;return true;}
break;
                case 7:

                    if(name == "processUid" || name.equals("processUid")) {this._processUid=(java.lang.String)value;return true;}
break;
                case 8:

                    if(name == "cached" || name.equals("cached")) {this._cached=(java.util.List)value;return true;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return true;}
break;
                case 10:

                    if(name == "sessionUid" || name.equals("sessionUid")) {this._sessionUid=(java.lang.String)value;return true;}
break;
                case 13:

                    if(name == "seqNum" || name.equals("seqNum")) {this._seqNum=(java.lang.Long)value;return true;}
break;
                case 17:

                    if(name == "types" || name.equals("types")) {this._types=(java.util.Set)value;return true;}
break;
                case 18:

                    if(name == "origRequest" || name.equals("origRequest")) {this._origRequest=(com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 22;
        switch(h){

                case 4:

		    
                    if(name == "centerId" || name.equals("centerId")) {return OH.valueOf(this._centerId);}
		    
break;
                case 7:

		    
                    if(name == "processUid" || name.equals("processUid")) {return this._processUid;}
            
break;
                case 8:

		    
                    if(name == "cached" || name.equals("cached")) {return this._cached;}
            
break;
                case 9:

		    
                    if(name == "invokedBy" || name.equals("invokedBy")) {return this._invokedBy;}
            
break;
                case 10:

		    
                    if(name == "sessionUid" || name.equals("sessionUid")) {return this._sessionUid;}
            
break;
                case 13:

		    
                    if(name == "seqNum" || name.equals("seqNum")) {return OH.valueOf(this._seqNum);}
		    
break;
                case 17:

		    
                    if(name == "types" || name.equals("types")) {return this._types;}
            
break;
                case 18:

		    
                    if(name == "origRequest" || name.equals("origRequest")) {return this._origRequest;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 22;
        switch(h){

                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return byte.class;}
break;
                case 7:

                    if(name == "processUid" || name.equals("processUid")) {return java.lang.String.class;}
break;
                case 8:

                    if(name == "cached" || name.equals("cached")) {return java.util.List.class;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return java.lang.String.class;}
break;
                case 10:

                    if(name == "sessionUid" || name.equals("sessionUid")) {return java.lang.String.class;}
break;
                case 13:

                    if(name == "seqNum" || name.equals("seqNum")) {return long.class;}
break;
                case 17:

                    if(name == "types" || name.equals("types")) {return java.util.Set.class;}
break;
                case 18:

                    if(name == "origRequest" || name.equals("origRequest")) {return com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 22;
        switch(h){

                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return VALUED_PARAM_centerId;}
break;
                case 7:

                    if(name == "processUid" || name.equals("processUid")) {return VALUED_PARAM_processUid;}
break;
                case 8:

                    if(name == "cached" || name.equals("cached")) {return VALUED_PARAM_cached;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return VALUED_PARAM_invokedBy;}
break;
                case 10:

                    if(name == "sessionUid" || name.equals("sessionUid")) {return VALUED_PARAM_sessionUid;}
break;
                case 13:

                    if(name == "seqNum" || name.equals("seqNum")) {return VALUED_PARAM_seqNum;}
break;
                case 17:

                    if(name == "types" || name.equals("types")) {return VALUED_PARAM_types;}
break;
                case 18:

                    if(name == "origRequest" || name.equals("origRequest")) {return VALUED_PARAM_origRequest;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 22;
        switch(h){

                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return 1;}
break;
                case 7:

                    if(name == "processUid" || name.equals("processUid")) {return 4;}
break;
                case 8:

                    if(name == "cached" || name.equals("cached")) {return 0;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 2;}
break;
                case 10:

                    if(name == "sessionUid" || name.equals("sessionUid")) {return 6;}
break;
                case 13:

                    if(name == "seqNum" || name.equals("seqNum")) {return 5;}
break;
                case 17:

                    if(name == "types" || name.equals("types")) {return 7;}
break;
                case 18:

                    if(name == "origRequest" || name.equals("origRequest")) {return 3;}
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
        return 8;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiCenterClientSnapshot0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 22;
        switch(h){

                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return true;}
break;
                case 7:

                    if(name == "processUid" || name.equals("processUid")) {return true;}
break;
                case 8:

                    if(name == "cached" || name.equals("cached")) {return true;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return true;}
break;
                case 10:

                    if(name == "sessionUid" || name.equals("sessionUid")) {return true;}
break;
                case 13:

                    if(name == "seqNum" || name.equals("seqNum")) {return true;}
break;
                case 17:

                    if(name == "types" || name.equals("types")) {return true;}
break;
                case 18:

                    if(name == "origRequest" || name.equals("origRequest")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 22;
        switch(h){

                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return 1;}
break;
                case 7:

                    if(name == "processUid" || name.equals("processUid")) {return 20;}
break;
                case 8:

                    if(name == "cached" || name.equals("cached")) {return 21;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 20;}
break;
                case 10:

                    if(name == "sessionUid" || name.equals("sessionUid")) {return 20;}
break;
                case 13:

                    if(name == "seqNum" || name.equals("seqNum")) {return 6;}
break;
                case 17:

                    if(name == "types" || name.equals("types")) {return 22;}
break;
                case 18:

                    if(name == "origRequest" || name.equals("origRequest")) {return 41;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _cached;

        case 1:return _centerId;

        case 2:return _invokedBy;

        case 3:return _origRequest;

        case 4:return _processUid;

        case 5:return _seqNum;

        case 6:return _sessionUid;

        case 7:return _types;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 8);
    }

    public java.util.List getCached(){
        return this._cached;
    }
    public void setCached(java.util.List _cached){
    
        this._cached=_cached;
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

    public com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest getOrigRequest(){
        return this._origRequest;
    }
    public void setOrigRequest(com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest _origRequest){
    
        this._origRequest=_origRequest;
    }

    public java.lang.String getProcessUid(){
        return this._processUid;
    }
    public void setProcessUid(java.lang.String _processUid){
    
        this._processUid=_processUid;
    }

    public long getSeqNum(){
        return this._seqNum;
    }
    public void setSeqNum(long _seqNum){
    
        this._seqNum=_seqNum;
    }

    public java.lang.String getSessionUid(){
        return this._sessionUid;
    }
    public void setSessionUid(java.lang.String _sessionUid){
    
        this._sessionUid=_sessionUid;
    }

    public java.util.Set getTypes(){
        return this._types;
    }
    public void setTypes(java.util.Set _types){
    
        this._types=_types;
    }





  
    private static final class VALUED_PARAM_CLASS_cached extends AbstractValuedParam<AmiCenterClientSnapshot0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiCenterClientSnapshot0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientSnapshot0 valued, DataInput stream) throws IOException{
		    
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
		    return 0;
	    }
    
	    @Override
	    public byte getPid() {
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "cached";
	    }
    
	    @Override
	    public Object getValue(AmiCenterClientSnapshot0 valued) {
		    return (java.util.List)((AmiCenterClientSnapshot0)valued).getCached();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientSnapshot0 valued, Object value) {
		    valued.setCached((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
		    dest.setCached(source.getCached());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
	        return OH.eq(dest.getCached(),source.getCached());
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
	    public void append(AmiCenterClientSnapshot0 valued, StringBuilder sb){
	        
	        sb.append(valued.getCached());
	        
	    }
	    @Override
	    public void append(AmiCenterClientSnapshot0 valued, StringBuildable sb){
	        
	        sb.append(valued.getCached());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List cached";
	    }
	    @Override
	    public void clear(AmiCenterClientSnapshot0 valued){
	       valued.setCached(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_cached=new VALUED_PARAM_CLASS_cached();
  

  
    private static final class VALUED_PARAM_CLASS_centerId extends AbstractValuedParam<AmiCenterClientSnapshot0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiCenterClientSnapshot0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getCenterId());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientSnapshot0 valued, DataInput stream) throws IOException{
		    
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "centerId";
	    }
    
	    @Override
	    public Object getValue(AmiCenterClientSnapshot0 valued) {
		    return (byte)((AmiCenterClientSnapshot0)valued).getCenterId();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientSnapshot0 valued, Object value) {
		    valued.setCenterId((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
		    dest.setCenterId(source.getCenterId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
	        return OH.eq(dest.getCenterId(),source.getCenterId());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiCenterClientSnapshot0 valued) {
		    return valued.getCenterId();
	    }
    
	    @Override
	    public void setByte(AmiCenterClientSnapshot0 valued, byte value) {
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
	    public void append(AmiCenterClientSnapshot0 valued, StringBuilder sb){
	        
	        sb.append(valued.getCenterId());
	        
	    }
	    @Override
	    public void append(AmiCenterClientSnapshot0 valued, StringBuildable sb){
	        
	        sb.append(valued.getCenterId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte centerId";
	    }
	    @Override
	    public void clear(AmiCenterClientSnapshot0 valued){
	       valued.setCenterId((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_centerId=new VALUED_PARAM_CLASS_centerId();
  

  
    private static final class VALUED_PARAM_CLASS_invokedBy extends AbstractValuedParam<AmiCenterClientSnapshot0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterClientSnapshot0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientSnapshot0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiCenterClientSnapshot0 valued) {
		    return (java.lang.String)((AmiCenterClientSnapshot0)valued).getInvokedBy();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientSnapshot0 valued, Object value) {
		    valued.setInvokedBy((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
		    dest.setInvokedBy(source.getInvokedBy());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
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
	    public void append(AmiCenterClientSnapshot0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterClientSnapshot0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String invokedBy";
	    }
	    @Override
	    public void clear(AmiCenterClientSnapshot0 valued){
	       valued.setInvokedBy(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_invokedBy=new VALUED_PARAM_CLASS_invokedBy();
  

  
    private static final class VALUED_PARAM_CLASS_origRequest extends AbstractValuedParam<AmiCenterClientSnapshot0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 41;
	    }
	    
	    @Override
	    public void write(AmiCenterClientSnapshot0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientSnapshot0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest}");
		    
	    }
	    
	    @Override
	    public boolean isPrimitive() {
		    return false;
	    }
    
	    @Override
	    public boolean isValued() {
		    return true;
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
		    return 3;
	    }
    
	    @Override
	    public byte getPid() {
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "origRequest";
	    }
    
	    @Override
	    public Object getValue(AmiCenterClientSnapshot0 valued) {
		    return (com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest)((AmiCenterClientSnapshot0)valued).getOrigRequest();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientSnapshot0 valued, Object value) {
		    valued.setOrigRequest((com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
		    dest.setOrigRequest(source.getOrigRequest());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
	        return OH.eq(dest.getOrigRequest(),source.getOrigRequest());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiCenterClientSnapshot0 valued, StringBuilder sb){
	        
	        sb.append(valued.getOrigRequest());
	        
	    }
	    @Override
	    public void append(AmiCenterClientSnapshot0 valued, StringBuildable sb){
	        
	        sb.append(valued.getOrigRequest());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest origRequest";
	    }
	    @Override
	    public void clear(AmiCenterClientSnapshot0 valued){
	       valued.setOrigRequest(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_origRequest=new VALUED_PARAM_CLASS_origRequest();
  

  
    private static final class VALUED_PARAM_CLASS_processUid extends AbstractValuedParam<AmiCenterClientSnapshot0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterClientSnapshot0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientSnapshot0 valued, DataInput stream) throws IOException{
		    
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
            return "processUid";
	    }
    
	    @Override
	    public Object getValue(AmiCenterClientSnapshot0 valued) {
		    return (java.lang.String)((AmiCenterClientSnapshot0)valued).getProcessUid();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientSnapshot0 valued, Object value) {
		    valued.setProcessUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
		    dest.setProcessUid(source.getProcessUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
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
	    public void append(AmiCenterClientSnapshot0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getProcessUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterClientSnapshot0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getProcessUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String processUid";
	    }
	    @Override
	    public void clear(AmiCenterClientSnapshot0 valued){
	       valued.setProcessUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_processUid=new VALUED_PARAM_CLASS_processUid();
  

  
    private static final class VALUED_PARAM_CLASS_seqNum extends AbstractValuedParam<AmiCenterClientSnapshot0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterClientSnapshot0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getSeqNum());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientSnapshot0 valued, DataInput stream) throws IOException{
		    
		      valued.setSeqNum(stream.readLong());
		    
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "seqNum";
	    }
    
	    @Override
	    public Object getValue(AmiCenterClientSnapshot0 valued) {
		    return (long)((AmiCenterClientSnapshot0)valued).getSeqNum();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientSnapshot0 valued, Object value) {
		    valued.setSeqNum((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
		    dest.setSeqNum(source.getSeqNum());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
	        return OH.eq(dest.getSeqNum(),source.getSeqNum());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterClientSnapshot0 valued) {
		    return valued.getSeqNum();
	    }
    
	    @Override
	    public void setLong(AmiCenterClientSnapshot0 valued, long value) {
		    valued.setSeqNum(value);
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
	    public void append(AmiCenterClientSnapshot0 valued, StringBuilder sb){
	        
	        sb.append(valued.getSeqNum());
	        
	    }
	    @Override
	    public void append(AmiCenterClientSnapshot0 valued, StringBuildable sb){
	        
	        sb.append(valued.getSeqNum());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long seqNum";
	    }
	    @Override
	    public void clear(AmiCenterClientSnapshot0 valued){
	       valued.setSeqNum(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_seqNum=new VALUED_PARAM_CLASS_seqNum();
  

  
    private static final class VALUED_PARAM_CLASS_sessionUid extends AbstractValuedParam<AmiCenterClientSnapshot0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterClientSnapshot0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientSnapshot0 valued, DataInput stream) throws IOException{
		    
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "sessionUid";
	    }
    
	    @Override
	    public Object getValue(AmiCenterClientSnapshot0 valued) {
		    return (java.lang.String)((AmiCenterClientSnapshot0)valued).getSessionUid();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientSnapshot0 valued, Object value) {
		    valued.setSessionUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
		    dest.setSessionUid(source.getSessionUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
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
	    public void append(AmiCenterClientSnapshot0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getSessionUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterClientSnapshot0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getSessionUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String sessionUid";
	    }
	    @Override
	    public void clear(AmiCenterClientSnapshot0 valued){
	       valued.setSessionUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_sessionUid=new VALUED_PARAM_CLASS_sessionUid();
  

  
    private static final class VALUED_PARAM_CLASS_types extends AbstractValuedParam<AmiCenterClientSnapshot0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 22;
	    }
	    
	    @Override
	    public void write(AmiCenterClientSnapshot0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Set}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientSnapshot0 valued, DataInput stream) throws IOException{
		    
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
		    return 7;
	    }
    
	    @Override
	    public byte getPid() {
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "types";
	    }
    
	    @Override
	    public Object getValue(AmiCenterClientSnapshot0 valued) {
		    return (java.util.Set)((AmiCenterClientSnapshot0)valued).getTypes();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientSnapshot0 valued, Object value) {
		    valued.setTypes((java.util.Set)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
		    dest.setTypes(source.getTypes());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientSnapshot0 source, AmiCenterClientSnapshot0 dest) {
	        return OH.eq(dest.getTypes(),source.getTypes());
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
	    public void append(AmiCenterClientSnapshot0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTypes());
	        
	    }
	    @Override
	    public void append(AmiCenterClientSnapshot0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTypes());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.Set types";
	    }
	    @Override
	    public void clear(AmiCenterClientSnapshot0 valued){
	       valued.setTypes(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_types=new VALUED_PARAM_CLASS_types();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_cached, VALUED_PARAM_centerId, VALUED_PARAM_invokedBy, VALUED_PARAM_origRequest, VALUED_PARAM_processUid, VALUED_PARAM_seqNum, VALUED_PARAM_sessionUid, VALUED_PARAM_types, };


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