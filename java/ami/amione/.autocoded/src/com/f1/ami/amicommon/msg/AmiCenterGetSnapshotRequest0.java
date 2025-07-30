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

public abstract class AmiCenterGetSnapshotRequest0 implements com.f1.ami.amicommon.msg.AmiCenterGetSnapshotRequest ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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

    private java.lang.String _comment;

    private boolean _includeStringPool;

    private java.lang.String _invokedBy;

    private int _maxBatchSize;

    private int _priority;

    private long _requestTime;

    private boolean _supportsIntermediate;

    private static final String NAMES[]={ "amiObjectTypesToSend" ,"comment","includeStringPool","invokedBy","maxBatchSize","priority","requestTime","supportsIntermediate"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 20;
        try{
        switch(h){

                case 3:

                    if(name == "includeStringPool" || name.equals("includeStringPool")) {this._includeStringPool=(java.lang.Boolean)value;return;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return;}
break;
                case 5:

                    if(name == "supportsIntermediate" || name.equals("supportsIntermediate")) {this._supportsIntermediate=(java.lang.Boolean)value;return;}
break;
                case 7:

                    if(name == "maxBatchSize" || name.equals("maxBatchSize")) {this._maxBatchSize=(java.lang.Integer)value;return;}
break;
                case 8:

                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {this._amiObjectTypesToSend=(java.util.Set)value;return;}
break;
                case 12:

                    if(name == "requestTime" || name.equals("requestTime")) {this._requestTime=(java.lang.Long)value;return;}
break;
                case 17:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return;}
break;
                case 19:

                    if(name == "comment" || name.equals("comment")) {this._comment=(java.lang.String)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 20;
        switch(h){

                case 3:

                    if(name == "includeStringPool" || name.equals("includeStringPool")) {this._includeStringPool=(java.lang.Boolean)value;return true;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return true;}
break;
                case 5:

                    if(name == "supportsIntermediate" || name.equals("supportsIntermediate")) {this._supportsIntermediate=(java.lang.Boolean)value;return true;}
break;
                case 7:

                    if(name == "maxBatchSize" || name.equals("maxBatchSize")) {this._maxBatchSize=(java.lang.Integer)value;return true;}
break;
                case 8:

                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {this._amiObjectTypesToSend=(java.util.Set)value;return true;}
break;
                case 12:

                    if(name == "requestTime" || name.equals("requestTime")) {this._requestTime=(java.lang.Long)value;return true;}
break;
                case 17:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return true;}
break;
                case 19:

                    if(name == "comment" || name.equals("comment")) {this._comment=(java.lang.String)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 20;
        switch(h){

                case 3:

		    
                    if(name == "includeStringPool" || name.equals("includeStringPool")) {return OH.valueOf(this._includeStringPool);}
		    
break;
                case 4:

		    
                    if(name == "priority" || name.equals("priority")) {return OH.valueOf(this._priority);}
		    
break;
                case 5:

		    
                    if(name == "supportsIntermediate" || name.equals("supportsIntermediate")) {return OH.valueOf(this._supportsIntermediate);}
		    
break;
                case 7:

		    
                    if(name == "maxBatchSize" || name.equals("maxBatchSize")) {return OH.valueOf(this._maxBatchSize);}
		    
break;
                case 8:

		    
                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {return this._amiObjectTypesToSend;}
            
break;
                case 12:

		    
                    if(name == "requestTime" || name.equals("requestTime")) {return OH.valueOf(this._requestTime);}
		    
break;
                case 17:

		    
                    if(name == "invokedBy" || name.equals("invokedBy")) {return this._invokedBy;}
            
break;
                case 19:

		    
                    if(name == "comment" || name.equals("comment")) {return this._comment;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 20;
        switch(h){

                case 3:

                    if(name == "includeStringPool" || name.equals("includeStringPool")) {return boolean.class;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {return int.class;}
break;
                case 5:

                    if(name == "supportsIntermediate" || name.equals("supportsIntermediate")) {return boolean.class;}
break;
                case 7:

                    if(name == "maxBatchSize" || name.equals("maxBatchSize")) {return int.class;}
break;
                case 8:

                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {return java.util.Set.class;}
break;
                case 12:

                    if(name == "requestTime" || name.equals("requestTime")) {return long.class;}
break;
                case 17:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return java.lang.String.class;}
break;
                case 19:

                    if(name == "comment" || name.equals("comment")) {return java.lang.String.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 20;
        switch(h){

                case 3:

                    if(name == "includeStringPool" || name.equals("includeStringPool")) {return VALUED_PARAM_includeStringPool;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {return VALUED_PARAM_priority;}
break;
                case 5:

                    if(name == "supportsIntermediate" || name.equals("supportsIntermediate")) {return VALUED_PARAM_supportsIntermediate;}
break;
                case 7:

                    if(name == "maxBatchSize" || name.equals("maxBatchSize")) {return VALUED_PARAM_maxBatchSize;}
break;
                case 8:

                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {return VALUED_PARAM_amiObjectTypesToSend;}
break;
                case 12:

                    if(name == "requestTime" || name.equals("requestTime")) {return VALUED_PARAM_requestTime;}
break;
                case 17:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return VALUED_PARAM_invokedBy;}
break;
                case 19:

                    if(name == "comment" || name.equals("comment")) {return VALUED_PARAM_comment;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 20;
        switch(h){

                case 3:

                    if(name == "includeStringPool" || name.equals("includeStringPool")) {return 2;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {return 5;}
break;
                case 5:

                    if(name == "supportsIntermediate" || name.equals("supportsIntermediate")) {return 7;}
break;
                case 7:

                    if(name == "maxBatchSize" || name.equals("maxBatchSize")) {return 4;}
break;
                case 8:

                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {return 0;}
break;
                case 12:

                    if(name == "requestTime" || name.equals("requestTime")) {return 6;}
break;
                case 17:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 3;}
break;
                case 19:

                    if(name == "comment" || name.equals("comment")) {return 1;}
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
	    return (Class)AmiCenterGetSnapshotRequest0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 20;
        switch(h){

                case 3:

                    if(name == "includeStringPool" || name.equals("includeStringPool")) {return true;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {return true;}
break;
                case 5:

                    if(name == "supportsIntermediate" || name.equals("supportsIntermediate")) {return true;}
break;
                case 7:

                    if(name == "maxBatchSize" || name.equals("maxBatchSize")) {return true;}
break;
                case 8:

                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {return true;}
break;
                case 12:

                    if(name == "requestTime" || name.equals("requestTime")) {return true;}
break;
                case 17:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return true;}
break;
                case 19:

                    if(name == "comment" || name.equals("comment")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 20;
        switch(h){

                case 3:

                    if(name == "includeStringPool" || name.equals("includeStringPool")) {return 0;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {return 4;}
break;
                case 5:

                    if(name == "supportsIntermediate" || name.equals("supportsIntermediate")) {return 0;}
break;
                case 7:

                    if(name == "maxBatchSize" || name.equals("maxBatchSize")) {return 4;}
break;
                case 8:

                    if(name == "amiObjectTypesToSend" || name.equals("amiObjectTypesToSend")) {return 22;}
break;
                case 12:

                    if(name == "requestTime" || name.equals("requestTime")) {return 6;}
break;
                case 17:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 20;}
break;
                case 19:

                    if(name == "comment" || name.equals("comment")) {return 20;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _amiObjectTypesToSend;

        case 1:return _comment;

        case 2:return _includeStringPool;

        case 3:return _invokedBy;

        case 4:return _maxBatchSize;

        case 5:return _priority;

        case 6:return _requestTime;

        case 7:return _supportsIntermediate;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 8);
    }

    public java.util.Set getAmiObjectTypesToSend(){
        return this._amiObjectTypesToSend;
    }
    public void setAmiObjectTypesToSend(java.util.Set _amiObjectTypesToSend){
    
        this._amiObjectTypesToSend=_amiObjectTypesToSend;
    }

    public java.lang.String getComment(){
        return this._comment;
    }
    public void setComment(java.lang.String _comment){
    
        this._comment=_comment;
    }

    public boolean getIncludeStringPool(){
        return this._includeStringPool;
    }
    public void setIncludeStringPool(boolean _includeStringPool){
    
        this._includeStringPool=_includeStringPool;
    }

    public java.lang.String getInvokedBy(){
        return this._invokedBy;
    }
    public void setInvokedBy(java.lang.String _invokedBy){
    
        this._invokedBy=_invokedBy;
    }

    public int getMaxBatchSize(){
        return this._maxBatchSize;
    }
    public void setMaxBatchSize(int _maxBatchSize){
    
        this._maxBatchSize=_maxBatchSize;
    }

    public int getPriority(){
        return this._priority;
    }
    public void setPriority(int _priority){
    
        this._priority=_priority;
    }

    public long getRequestTime(){
        return this._requestTime;
    }
    public void setRequestTime(long _requestTime){
    
        this._requestTime=_requestTime;
    }

    public boolean getSupportsIntermediate(){
        return this._supportsIntermediate;
    }
    public void setSupportsIntermediate(boolean _supportsIntermediate){
    
        this._supportsIntermediate=_supportsIntermediate;
    }





  
    private static final class VALUED_PARAM_CLASS_amiObjectTypesToSend extends AbstractValuedParam<AmiCenterGetSnapshotRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 22;
	    }
	    
	    @Override
	    public void write(AmiCenterGetSnapshotRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Set}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterGetSnapshotRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "amiObjectTypesToSend";
	    }
    
	    @Override
	    public Object getValue(AmiCenterGetSnapshotRequest0 valued) {
		    return (java.util.Set)((AmiCenterGetSnapshotRequest0)valued).getAmiObjectTypesToSend();
	    }
    
	    @Override
	    public void setValue(AmiCenterGetSnapshotRequest0 valued, Object value) {
		    valued.setAmiObjectTypesToSend((java.util.Set)value);
	    }
    
	    @Override
	    public void copy(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
		    dest.setAmiObjectTypesToSend(source.getAmiObjectTypesToSend());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
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
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAmiObjectTypesToSend());
	        
	    }
	    @Override
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAmiObjectTypesToSend());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.Set amiObjectTypesToSend";
	    }
	    @Override
	    public void clear(AmiCenterGetSnapshotRequest0 valued){
	       valued.setAmiObjectTypesToSend(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_amiObjectTypesToSend=new VALUED_PARAM_CLASS_amiObjectTypesToSend();
  

  
    private static final class VALUED_PARAM_CLASS_comment extends AbstractValuedParam<AmiCenterGetSnapshotRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterGetSnapshotRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterGetSnapshotRequest0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiCenterGetSnapshotRequest0 valued) {
		    return (java.lang.String)((AmiCenterGetSnapshotRequest0)valued).getComment();
	    }
    
	    @Override
	    public void setValue(AmiCenterGetSnapshotRequest0 valued, Object value) {
		    valued.setComment((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
		    dest.setComment(source.getComment());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
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
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getComment(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getComment(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String comment";
	    }
	    @Override
	    public void clear(AmiCenterGetSnapshotRequest0 valued){
	       valued.setComment(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_comment=new VALUED_PARAM_CLASS_comment();
  

  
    private static final class VALUED_PARAM_CLASS_includeStringPool extends AbstractValuedParam<AmiCenterGetSnapshotRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterGetSnapshotRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getIncludeStringPool());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterGetSnapshotRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setIncludeStringPool(stream.readBoolean());
		    
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
		    return 2;
	    }
    
	    @Override
	    public byte getPid() {
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "includeStringPool";
	    }
    
	    @Override
	    public Object getValue(AmiCenterGetSnapshotRequest0 valued) {
		    return (boolean)((AmiCenterGetSnapshotRequest0)valued).getIncludeStringPool();
	    }
    
	    @Override
	    public void setValue(AmiCenterGetSnapshotRequest0 valued, Object value) {
		    valued.setIncludeStringPool((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
		    dest.setIncludeStringPool(source.getIncludeStringPool());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
	        return OH.eq(dest.getIncludeStringPool(),source.getIncludeStringPool());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterGetSnapshotRequest0 valued) {
		    return valued.getIncludeStringPool();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterGetSnapshotRequest0 valued, boolean value) {
		    valued.setIncludeStringPool(value);
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
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getIncludeStringPool());
	        
	    }
	    @Override
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getIncludeStringPool());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean includeStringPool";
	    }
	    @Override
	    public void clear(AmiCenterGetSnapshotRequest0 valued){
	       valued.setIncludeStringPool(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_includeStringPool=new VALUED_PARAM_CLASS_includeStringPool();
  

  
    private static final class VALUED_PARAM_CLASS_invokedBy extends AbstractValuedParam<AmiCenterGetSnapshotRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterGetSnapshotRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterGetSnapshotRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 51;
	    }
    
	    @Override
	    public String getName() {
            return "invokedBy";
	    }
    
	    @Override
	    public Object getValue(AmiCenterGetSnapshotRequest0 valued) {
		    return (java.lang.String)((AmiCenterGetSnapshotRequest0)valued).getInvokedBy();
	    }
    
	    @Override
	    public void setValue(AmiCenterGetSnapshotRequest0 valued, Object value) {
		    valued.setInvokedBy((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
		    dest.setInvokedBy(source.getInvokedBy());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
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
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String invokedBy";
	    }
	    @Override
	    public void clear(AmiCenterGetSnapshotRequest0 valued){
	       valued.setInvokedBy(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_invokedBy=new VALUED_PARAM_CLASS_invokedBy();
  

  
    private static final class VALUED_PARAM_CLASS_maxBatchSize extends AbstractValuedParam<AmiCenterGetSnapshotRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterGetSnapshotRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getMaxBatchSize());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterGetSnapshotRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setMaxBatchSize(stream.readInt());
		    
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
		    return 4;
	    }
    
	    @Override
	    public byte getPid() {
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "maxBatchSize";
	    }
    
	    @Override
	    public Object getValue(AmiCenterGetSnapshotRequest0 valued) {
		    return (int)((AmiCenterGetSnapshotRequest0)valued).getMaxBatchSize();
	    }
    
	    @Override
	    public void setValue(AmiCenterGetSnapshotRequest0 valued, Object value) {
		    valued.setMaxBatchSize((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
		    dest.setMaxBatchSize(source.getMaxBatchSize());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
	        return OH.eq(dest.getMaxBatchSize(),source.getMaxBatchSize());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterGetSnapshotRequest0 valued) {
		    return valued.getMaxBatchSize();
	    }
    
	    @Override
	    public void setInt(AmiCenterGetSnapshotRequest0 valued, int value) {
		    valued.setMaxBatchSize(value);
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
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getMaxBatchSize());
	        
	    }
	    @Override
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getMaxBatchSize());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int maxBatchSize";
	    }
	    @Override
	    public void clear(AmiCenterGetSnapshotRequest0 valued){
	       valued.setMaxBatchSize(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_maxBatchSize=new VALUED_PARAM_CLASS_maxBatchSize();
  

  
    private static final class VALUED_PARAM_CLASS_priority extends AbstractValuedParam<AmiCenterGetSnapshotRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterGetSnapshotRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getPriority());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterGetSnapshotRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 5;
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
	    public Object getValue(AmiCenterGetSnapshotRequest0 valued) {
		    return (int)((AmiCenterGetSnapshotRequest0)valued).getPriority();
	    }
    
	    @Override
	    public void setValue(AmiCenterGetSnapshotRequest0 valued, Object value) {
		    valued.setPriority((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
		    dest.setPriority(source.getPriority());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
	        return OH.eq(dest.getPriority(),source.getPriority());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterGetSnapshotRequest0 valued) {
		    return valued.getPriority();
	    }
    
	    @Override
	    public void setInt(AmiCenterGetSnapshotRequest0 valued, int value) {
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
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int priority";
	    }
	    @Override
	    public void clear(AmiCenterGetSnapshotRequest0 valued){
	       valued.setPriority(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_priority=new VALUED_PARAM_CLASS_priority();
  

  
    private static final class VALUED_PARAM_CLASS_requestTime extends AbstractValuedParam<AmiCenterGetSnapshotRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterGetSnapshotRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getRequestTime());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterGetSnapshotRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 6;
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
	    public Object getValue(AmiCenterGetSnapshotRequest0 valued) {
		    return (long)((AmiCenterGetSnapshotRequest0)valued).getRequestTime();
	    }
    
	    @Override
	    public void setValue(AmiCenterGetSnapshotRequest0 valued, Object value) {
		    valued.setRequestTime((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
		    dest.setRequestTime(source.getRequestTime());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
	        return OH.eq(dest.getRequestTime(),source.getRequestTime());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterGetSnapshotRequest0 valued) {
		    return valued.getRequestTime();
	    }
    
	    @Override
	    public void setLong(AmiCenterGetSnapshotRequest0 valued, long value) {
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
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getRequestTime());
	        
	    }
	    @Override
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getRequestTime());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long requestTime";
	    }
	    @Override
	    public void clear(AmiCenterGetSnapshotRequest0 valued){
	       valued.setRequestTime(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_requestTime=new VALUED_PARAM_CLASS_requestTime();
  

  
    private static final class VALUED_PARAM_CLASS_supportsIntermediate extends AbstractValuedParam<AmiCenterGetSnapshotRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterGetSnapshotRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getSupportsIntermediate());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterGetSnapshotRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setSupportsIntermediate(stream.readBoolean());
		    
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
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "supportsIntermediate";
	    }
    
	    @Override
	    public Object getValue(AmiCenterGetSnapshotRequest0 valued) {
		    return (boolean)((AmiCenterGetSnapshotRequest0)valued).getSupportsIntermediate();
	    }
    
	    @Override
	    public void setValue(AmiCenterGetSnapshotRequest0 valued, Object value) {
		    valued.setSupportsIntermediate((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
		    dest.setSupportsIntermediate(source.getSupportsIntermediate());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterGetSnapshotRequest0 source, AmiCenterGetSnapshotRequest0 dest) {
	        return OH.eq(dest.getSupportsIntermediate(),source.getSupportsIntermediate());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterGetSnapshotRequest0 valued) {
		    return valued.getSupportsIntermediate();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterGetSnapshotRequest0 valued, boolean value) {
		    valued.setSupportsIntermediate(value);
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
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getSupportsIntermediate());
	        
	    }
	    @Override
	    public void append(AmiCenterGetSnapshotRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getSupportsIntermediate());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean supportsIntermediate";
	    }
	    @Override
	    public void clear(AmiCenterGetSnapshotRequest0 valued){
	       valued.setSupportsIntermediate(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_supportsIntermediate=new VALUED_PARAM_CLASS_supportsIntermediate();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_amiObjectTypesToSend, VALUED_PARAM_comment, VALUED_PARAM_includeStringPool, VALUED_PARAM_invokedBy, VALUED_PARAM_maxBatchSize, VALUED_PARAM_priority, VALUED_PARAM_requestTime, VALUED_PARAM_supportsIntermediate, };



    private static final byte PIDS[]={ 3 ,52,4,51,2,53,54,1};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 3: return VALUED_PARAM_amiObjectTypesToSend;
             case 52: return VALUED_PARAM_comment;
             case 4: return VALUED_PARAM_includeStringPool;
             case 51: return VALUED_PARAM_invokedBy;
             case 2: return VALUED_PARAM_maxBatchSize;
             case 53: return VALUED_PARAM_priority;
             case 54: return VALUED_PARAM_requestTime;
             case 1: return VALUED_PARAM_supportsIntermediate;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 3: return true;
             case 52: return true;
             case 4: return true;
             case 51: return true;
             case 2: return true;
             case 53: return true;
             case 54: return true;
             case 1: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 3: return "amiObjectTypesToSend";
             case 52: return "comment";
             case 4: return "includeStringPool";
             case 51: return "invokedBy";
             case 2: return "maxBatchSize";
             case 53: return "priority";
             case 54: return "requestTime";
             case 1: return "supportsIntermediate";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 3: return 0;
             case 52: return 1;
             case 4: return 2;
             case 51: return 3;
             case 2: return 4;
             case 53: return 5;
             case 54: return 6;
             case 1: return 7;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="amiObjectTypesToSend") return 3;
             if(name=="comment") return 52;
             if(name=="includeStringPool") return 4;
             if(name=="invokedBy") return 51;
             if(name=="maxBatchSize") return 2;
             if(name=="priority") return 53;
             if(name=="requestTime") return 54;
             if(name=="supportsIntermediate") return 1;
            
             if("amiObjectTypesToSend".equals(name)) return 3;
             if("comment".equals(name)) return 52;
             if("includeStringPool".equals(name)) return 4;
             if("invokedBy".equals(name)) return 51;
             if("maxBatchSize".equals(name)) return 2;
             if("priority".equals(name)) return 53;
             if("requestTime".equals(name)) return 54;
             if("supportsIntermediate".equals(name)) return 1;
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
             case 3: return  this._amiObjectTypesToSend; 
             case 52: return  this._comment; 
             case 4: return  OH.valueOf(this._includeStringPool); 
             case 51: return  this._invokedBy; 
             case 2: return  OH.valueOf(this._maxBatchSize); 
             case 53: return  OH.valueOf(this._priority); 
             case 54: return  OH.valueOf(this._requestTime); 
             case 1: return  OH.valueOf(this._supportsIntermediate); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 3: return java.util.Set.class;
             case 52: return java.lang.String.class;
             case 4: return boolean.class;
             case 51: return java.lang.String.class;
             case 2: return int.class;
             case 53: return int.class;
             case 54: return long.class;
             case 1: return boolean.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 3: return 22;
             case 52: return 20;
             case 4: return 0;
             case 51: return 20;
             case 2: return 4;
             case 53: return 4;
             case 54: return 6;
             case 1: return 0;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 3: this._amiObjectTypesToSend=(java.util.Set)value;return;
             case 52: this._comment=(java.lang.String)value;return;
             case 4: this._includeStringPool=(java.lang.Boolean)value;return;
             case 51: this._invokedBy=(java.lang.String)value;return;
             case 2: this._maxBatchSize=(java.lang.Integer)value;return;
             case 53: this._priority=(java.lang.Integer)value;return;
             case 54: this._requestTime=(java.lang.Long)value;return;
             case 1: this._supportsIntermediate=(java.lang.Boolean)value;return;
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
             case 3: this._amiObjectTypesToSend=(java.util.Set)value;return true;
             case 52: this._comment=(java.lang.String)value;return true;
             case 4: this._includeStringPool=(java.lang.Boolean)value;return true;
             case 51: this._invokedBy=(java.lang.String)value;return true;
             case 2: this._maxBatchSize=(java.lang.Integer)value;return true;
             case 53: this._priority=(java.lang.Integer)value;return true;
             case 54: this._requestTime=(java.lang.Long)value;return true;
             case 1: this._supportsIntermediate=(java.lang.Boolean)value;return true;
            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
             case 4: return this._includeStringPool;
             case 1: return this._supportsIntermediate;
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
             case 2: return this._maxBatchSize;
             case 53: return this._priority;
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
             case 4: this._includeStringPool=value;return;
             case 1: this._supportsIntermediate=value;return;
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
             case 2: this._maxBatchSize=value;return;
             case 53: this._priority=value;return;
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
        
        case 1:
        
            if((basicType=in.readByte())!=0)
                break;
            this._supportsIntermediate=in.readBoolean();
        
            break;

        case 2:
        
            if((basicType=in.readByte())!=4)
                break;
            this._maxBatchSize=in.readInt();
        
            break;

        case 3:
        
            this._amiObjectTypesToSend=(java.util.Set)converter.read(session);
        
            break;

        case 4:
        
            if((basicType=in.readByte())!=0)
                break;
            this._includeStringPool=in.readBoolean();
        
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
        
if(this._supportsIntermediate!=false && (0 & transience)==0){
    out.writeByte(1);
        
    out.writeByte(0);
    out.writeBoolean(this._supportsIntermediate);
        
}

if(this._maxBatchSize!=0 && (0 & transience)==0){
    out.writeByte(2);
        
    out.writeByte(4);
    out.writeInt(this._maxBatchSize);
        
}

if(this._amiObjectTypesToSend!=null && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this._amiObjectTypesToSend,session);
        
}

if(this._includeStringPool!=false && (0 & transience)==0){
    out.writeByte(4);
        
    out.writeByte(0);
    out.writeBoolean(this._includeStringPool);
        
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