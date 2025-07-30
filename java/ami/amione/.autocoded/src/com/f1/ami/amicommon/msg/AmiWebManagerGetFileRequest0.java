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

public abstract class AmiWebManagerGetFileRequest0 implements com.f1.ami.amicommon.msg.AmiWebManagerGetFileRequest ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private long _asOfModifiedTime;

    private java.lang.String _comment;

    private java.lang.String _fileName;

    private java.lang.String _invokedBy;

    private short _options;

    private java.lang.String _parentFileName;

    private int _priority;

    private long _requestTime;

    private java.lang.String _safeFileExtension;

    private static final String NAMES[]={ "asOfModifiedTime" ,"comment","fileName","invokedBy","options","parentFileName","priority","requestTime","safeFileExtension"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 12;
        try{
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return;}
break;
                case 1:

                    if(name == "fileName" || name.equals("fileName")) {this._fileName=(java.lang.String)value;return;}
break;
                case 2:

                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {this._safeFileExtension=(java.lang.String)value;return;}
break;
                case 3:

                    if(name == "asOfModifiedTime" || name.equals("asOfModifiedTime")) {this._asOfModifiedTime=(java.lang.Long)value;return;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return;}
break;
                case 6:

                    if(name == "options" || name.equals("options")) {this._options=(java.lang.Short)value;return;}
break;
                case 8:

                    if(name == "requestTime" || name.equals("requestTime")) {this._requestTime=(java.lang.Long)value;return;}
break;
                case 9:

                    if(name == "parentFileName" || name.equals("parentFileName")) {this._parentFileName=(java.lang.String)value;return;}
break;
                case 11:

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
        final int h=Math.abs(name.hashCode()) % 12;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return true;}
break;
                case 1:

                    if(name == "fileName" || name.equals("fileName")) {this._fileName=(java.lang.String)value;return true;}
break;
                case 2:

                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {this._safeFileExtension=(java.lang.String)value;return true;}
break;
                case 3:

                    if(name == "asOfModifiedTime" || name.equals("asOfModifiedTime")) {this._asOfModifiedTime=(java.lang.Long)value;return true;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return true;}
break;
                case 6:

                    if(name == "options" || name.equals("options")) {this._options=(java.lang.Short)value;return true;}
break;
                case 8:

                    if(name == "requestTime" || name.equals("requestTime")) {this._requestTime=(java.lang.Long)value;return true;}
break;
                case 9:

                    if(name == "parentFileName" || name.equals("parentFileName")) {this._parentFileName=(java.lang.String)value;return true;}
break;
                case 11:

                    if(name == "comment" || name.equals("comment")) {this._comment=(java.lang.String)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 12;
        switch(h){

                case 0:

		    
                    if(name == "priority" || name.equals("priority")) {return OH.valueOf(this._priority);}
		    
break;
                case 1:

		    
                    if(name == "fileName" || name.equals("fileName")) {return this._fileName;}
            
break;
                case 2:

		    
                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {return this._safeFileExtension;}
            
break;
                case 3:

		    
                    if(name == "asOfModifiedTime" || name.equals("asOfModifiedTime")) {return OH.valueOf(this._asOfModifiedTime);}
		    
break;
                case 5:

		    
                    if(name == "invokedBy" || name.equals("invokedBy")) {return this._invokedBy;}
            
break;
                case 6:

		    
                    if(name == "options" || name.equals("options")) {return OH.valueOf(this._options);}
		    
break;
                case 8:

		    
                    if(name == "requestTime" || name.equals("requestTime")) {return OH.valueOf(this._requestTime);}
		    
break;
                case 9:

		    
                    if(name == "parentFileName" || name.equals("parentFileName")) {return this._parentFileName;}
            
break;
                case 11:

		    
                    if(name == "comment" || name.equals("comment")) {return this._comment;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 12;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return int.class;}
break;
                case 1:

                    if(name == "fileName" || name.equals("fileName")) {return java.lang.String.class;}
break;
                case 2:

                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {return java.lang.String.class;}
break;
                case 3:

                    if(name == "asOfModifiedTime" || name.equals("asOfModifiedTime")) {return long.class;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return java.lang.String.class;}
break;
                case 6:

                    if(name == "options" || name.equals("options")) {return short.class;}
break;
                case 8:

                    if(name == "requestTime" || name.equals("requestTime")) {return long.class;}
break;
                case 9:

                    if(name == "parentFileName" || name.equals("parentFileName")) {return java.lang.String.class;}
break;
                case 11:

                    if(name == "comment" || name.equals("comment")) {return java.lang.String.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 12;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return VALUED_PARAM_priority;}
break;
                case 1:

                    if(name == "fileName" || name.equals("fileName")) {return VALUED_PARAM_fileName;}
break;
                case 2:

                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {return VALUED_PARAM_safeFileExtension;}
break;
                case 3:

                    if(name == "asOfModifiedTime" || name.equals("asOfModifiedTime")) {return VALUED_PARAM_asOfModifiedTime;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return VALUED_PARAM_invokedBy;}
break;
                case 6:

                    if(name == "options" || name.equals("options")) {return VALUED_PARAM_options;}
break;
                case 8:

                    if(name == "requestTime" || name.equals("requestTime")) {return VALUED_PARAM_requestTime;}
break;
                case 9:

                    if(name == "parentFileName" || name.equals("parentFileName")) {return VALUED_PARAM_parentFileName;}
break;
                case 11:

                    if(name == "comment" || name.equals("comment")) {return VALUED_PARAM_comment;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 12;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return 6;}
break;
                case 1:

                    if(name == "fileName" || name.equals("fileName")) {return 2;}
break;
                case 2:

                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {return 8;}
break;
                case 3:

                    if(name == "asOfModifiedTime" || name.equals("asOfModifiedTime")) {return 0;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 3;}
break;
                case 6:

                    if(name == "options" || name.equals("options")) {return 4;}
break;
                case 8:

                    if(name == "requestTime" || name.equals("requestTime")) {return 7;}
break;
                case 9:

                    if(name == "parentFileName" || name.equals("parentFileName")) {return 5;}
break;
                case 11:

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
        return 9;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiWebManagerGetFileRequest0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 12;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return true;}
break;
                case 1:

                    if(name == "fileName" || name.equals("fileName")) {return true;}
break;
                case 2:

                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {return true;}
break;
                case 3:

                    if(name == "asOfModifiedTime" || name.equals("asOfModifiedTime")) {return true;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return true;}
break;
                case 6:

                    if(name == "options" || name.equals("options")) {return true;}
break;
                case 8:

                    if(name == "requestTime" || name.equals("requestTime")) {return true;}
break;
                case 9:

                    if(name == "parentFileName" || name.equals("parentFileName")) {return true;}
break;
                case 11:

                    if(name == "comment" || name.equals("comment")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 12;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return 4;}
break;
                case 1:

                    if(name == "fileName" || name.equals("fileName")) {return 20;}
break;
                case 2:

                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {return 20;}
break;
                case 3:

                    if(name == "asOfModifiedTime" || name.equals("asOfModifiedTime")) {return 6;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 20;}
break;
                case 6:

                    if(name == "options" || name.equals("options")) {return 2;}
break;
                case 8:

                    if(name == "requestTime" || name.equals("requestTime")) {return 6;}
break;
                case 9:

                    if(name == "parentFileName" || name.equals("parentFileName")) {return 20;}
break;
                case 11:

                    if(name == "comment" || name.equals("comment")) {return 20;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _asOfModifiedTime;

        case 1:return _comment;

        case 2:return _fileName;

        case 3:return _invokedBy;

        case 4:return _options;

        case 5:return _parentFileName;

        case 6:return _priority;

        case 7:return _requestTime;

        case 8:return _safeFileExtension;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 9);
    }

    public long getAsOfModifiedTime(){
        return this._asOfModifiedTime;
    }
    public void setAsOfModifiedTime(long _asOfModifiedTime){
    
        this._asOfModifiedTime=_asOfModifiedTime;
    }

    public java.lang.String getComment(){
        return this._comment;
    }
    public void setComment(java.lang.String _comment){
    
        this._comment=_comment;
    }

    public java.lang.String getFileName(){
        return this._fileName;
    }
    public void setFileName(java.lang.String _fileName){
    
        this._fileName=_fileName;
    }

    public java.lang.String getInvokedBy(){
        return this._invokedBy;
    }
    public void setInvokedBy(java.lang.String _invokedBy){
    
        this._invokedBy=_invokedBy;
    }

    public short getOptions(){
        return this._options;
    }
    public void setOptions(short _options){
    
        this._options=_options;
    }

    public java.lang.String getParentFileName(){
        return this._parentFileName;
    }
    public void setParentFileName(java.lang.String _parentFileName){
    
        this._parentFileName=_parentFileName;
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

    public java.lang.String getSafeFileExtension(){
        return this._safeFileExtension;
    }
    public void setSafeFileExtension(java.lang.String _safeFileExtension){
    
        this._safeFileExtension=_safeFileExtension;
    }





  
    private static final class VALUED_PARAM_CLASS_asOfModifiedTime extends AbstractValuedParam<AmiWebManagerGetFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiWebManagerGetFileRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getAsOfModifiedTime());
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerGetFileRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setAsOfModifiedTime(stream.readLong());
		    
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
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "asOfModifiedTime";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerGetFileRequest0 valued) {
		    return (long)((AmiWebManagerGetFileRequest0)valued).getAsOfModifiedTime();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerGetFileRequest0 valued, Object value) {
		    valued.setAsOfModifiedTime((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
		    dest.setAsOfModifiedTime(source.getAsOfModifiedTime());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
	        return OH.eq(dest.getAsOfModifiedTime(),source.getAsOfModifiedTime());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiWebManagerGetFileRequest0 valued) {
		    return valued.getAsOfModifiedTime();
	    }
    
	    @Override
	    public void setLong(AmiWebManagerGetFileRequest0 valued, long value) {
		    valued.setAsOfModifiedTime(value);
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
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAsOfModifiedTime());
	        
	    }
	    @Override
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAsOfModifiedTime());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long asOfModifiedTime";
	    }
	    @Override
	    public void clear(AmiWebManagerGetFileRequest0 valued){
	       valued.setAsOfModifiedTime(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_asOfModifiedTime=new VALUED_PARAM_CLASS_asOfModifiedTime();
  

  
    private static final class VALUED_PARAM_CLASS_comment extends AbstractValuedParam<AmiWebManagerGetFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebManagerGetFileRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerGetFileRequest0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiWebManagerGetFileRequest0 valued) {
		    return (java.lang.String)((AmiWebManagerGetFileRequest0)valued).getComment();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerGetFileRequest0 valued, Object value) {
		    valued.setComment((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
		    dest.setComment(source.getComment());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
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
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getComment(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getComment(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String comment";
	    }
	    @Override
	    public void clear(AmiWebManagerGetFileRequest0 valued){
	       valued.setComment(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_comment=new VALUED_PARAM_CLASS_comment();
  

  
    private static final class VALUED_PARAM_CLASS_fileName extends AbstractValuedParam<AmiWebManagerGetFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebManagerGetFileRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerGetFileRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "fileName";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerGetFileRequest0 valued) {
		    return (java.lang.String)((AmiWebManagerGetFileRequest0)valued).getFileName();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerGetFileRequest0 valued, Object value) {
		    valued.setFileName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
		    dest.setFileName(source.getFileName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
	        return OH.eq(dest.getFileName(),source.getFileName());
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
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getFileName(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getFileName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String fileName";
	    }
	    @Override
	    public void clear(AmiWebManagerGetFileRequest0 valued){
	       valued.setFileName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_fileName=new VALUED_PARAM_CLASS_fileName();
  

  
    private static final class VALUED_PARAM_CLASS_invokedBy extends AbstractValuedParam<AmiWebManagerGetFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebManagerGetFileRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerGetFileRequest0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiWebManagerGetFileRequest0 valued) {
		    return (java.lang.String)((AmiWebManagerGetFileRequest0)valued).getInvokedBy();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerGetFileRequest0 valued, Object value) {
		    valued.setInvokedBy((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
		    dest.setInvokedBy(source.getInvokedBy());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
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
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String invokedBy";
	    }
	    @Override
	    public void clear(AmiWebManagerGetFileRequest0 valued){
	       valued.setInvokedBy(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_invokedBy=new VALUED_PARAM_CLASS_invokedBy();
  

  
    private static final class VALUED_PARAM_CLASS_options extends AbstractValuedParam<AmiWebManagerGetFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 2;
	    }
	    
	    @Override
	    public void write(AmiWebManagerGetFileRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeShort(valued.getOptions());
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerGetFileRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setOptions(stream.readShort());
		    
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
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "options";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerGetFileRequest0 valued) {
		    return (short)((AmiWebManagerGetFileRequest0)valued).getOptions();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerGetFileRequest0 valued, Object value) {
		    valued.setOptions((java.lang.Short)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
		    dest.setOptions(source.getOptions());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
	        return OH.eq(dest.getOptions(),source.getOptions());
	    }
	    
	    
	    
	    
	    
	    
	    @Override
	    public short getShort(AmiWebManagerGetFileRequest0 valued) {
		    return valued.getOptions();
	    }
    
	    @Override
	    public void setShort(AmiWebManagerGetFileRequest0 valued, short value) {
		    valued.setOptions(value);
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
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getOptions());
	        
	    }
	    @Override
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getOptions());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:short options";
	    }
	    @Override
	    public void clear(AmiWebManagerGetFileRequest0 valued){
	       valued.setOptions((short)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_options=new VALUED_PARAM_CLASS_options();
  

  
    private static final class VALUED_PARAM_CLASS_parentFileName extends AbstractValuedParam<AmiWebManagerGetFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebManagerGetFileRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerGetFileRequest0 valued, DataInput stream) throws IOException{
		    
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
            return "parentFileName";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerGetFileRequest0 valued) {
		    return (java.lang.String)((AmiWebManagerGetFileRequest0)valued).getParentFileName();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerGetFileRequest0 valued, Object value) {
		    valued.setParentFileName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
		    dest.setParentFileName(source.getParentFileName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
	        return OH.eq(dest.getParentFileName(),source.getParentFileName());
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
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getParentFileName(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getParentFileName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String parentFileName";
	    }
	    @Override
	    public void clear(AmiWebManagerGetFileRequest0 valued){
	       valued.setParentFileName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_parentFileName=new VALUED_PARAM_CLASS_parentFileName();
  

  
    private static final class VALUED_PARAM_CLASS_priority extends AbstractValuedParam<AmiWebManagerGetFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiWebManagerGetFileRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getPriority());
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerGetFileRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 6;
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
	    public Object getValue(AmiWebManagerGetFileRequest0 valued) {
		    return (int)((AmiWebManagerGetFileRequest0)valued).getPriority();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerGetFileRequest0 valued, Object value) {
		    valued.setPriority((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
		    dest.setPriority(source.getPriority());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
	        return OH.eq(dest.getPriority(),source.getPriority());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiWebManagerGetFileRequest0 valued) {
		    return valued.getPriority();
	    }
    
	    @Override
	    public void setInt(AmiWebManagerGetFileRequest0 valued, int value) {
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
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int priority";
	    }
	    @Override
	    public void clear(AmiWebManagerGetFileRequest0 valued){
	       valued.setPriority(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_priority=new VALUED_PARAM_CLASS_priority();
  

  
    private static final class VALUED_PARAM_CLASS_requestTime extends AbstractValuedParam<AmiWebManagerGetFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiWebManagerGetFileRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getRequestTime());
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerGetFileRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 7;
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
	    public Object getValue(AmiWebManagerGetFileRequest0 valued) {
		    return (long)((AmiWebManagerGetFileRequest0)valued).getRequestTime();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerGetFileRequest0 valued, Object value) {
		    valued.setRequestTime((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
		    dest.setRequestTime(source.getRequestTime());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
	        return OH.eq(dest.getRequestTime(),source.getRequestTime());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiWebManagerGetFileRequest0 valued) {
		    return valued.getRequestTime();
	    }
    
	    @Override
	    public void setLong(AmiWebManagerGetFileRequest0 valued, long value) {
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
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getRequestTime());
	        
	    }
	    @Override
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getRequestTime());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long requestTime";
	    }
	    @Override
	    public void clear(AmiWebManagerGetFileRequest0 valued){
	       valued.setRequestTime(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_requestTime=new VALUED_PARAM_CLASS_requestTime();
  

  
    private static final class VALUED_PARAM_CLASS_safeFileExtension extends AbstractValuedParam<AmiWebManagerGetFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebManagerGetFileRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerGetFileRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "safeFileExtension";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerGetFileRequest0 valued) {
		    return (java.lang.String)((AmiWebManagerGetFileRequest0)valued).getSafeFileExtension();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerGetFileRequest0 valued, Object value) {
		    valued.setSafeFileExtension((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
		    dest.setSafeFileExtension(source.getSafeFileExtension());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerGetFileRequest0 source, AmiWebManagerGetFileRequest0 dest) {
	        return OH.eq(dest.getSafeFileExtension(),source.getSafeFileExtension());
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
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getSafeFileExtension(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebManagerGetFileRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getSafeFileExtension(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String safeFileExtension";
	    }
	    @Override
	    public void clear(AmiWebManagerGetFileRequest0 valued){
	       valued.setSafeFileExtension(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_safeFileExtension=new VALUED_PARAM_CLASS_safeFileExtension();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_asOfModifiedTime, VALUED_PARAM_comment, VALUED_PARAM_fileName, VALUED_PARAM_invokedBy, VALUED_PARAM_options, VALUED_PARAM_parentFileName, VALUED_PARAM_priority, VALUED_PARAM_requestTime, VALUED_PARAM_safeFileExtension, };



    private static final byte PIDS[]={ 5 ,52,1,51,3,2,53,54,4};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 5: return VALUED_PARAM_asOfModifiedTime;
             case 52: return VALUED_PARAM_comment;
             case 1: return VALUED_PARAM_fileName;
             case 51: return VALUED_PARAM_invokedBy;
             case 3: return VALUED_PARAM_options;
             case 2: return VALUED_PARAM_parentFileName;
             case 53: return VALUED_PARAM_priority;
             case 54: return VALUED_PARAM_requestTime;
             case 4: return VALUED_PARAM_safeFileExtension;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 5: return true;
             case 52: return true;
             case 1: return true;
             case 51: return true;
             case 3: return true;
             case 2: return true;
             case 53: return true;
             case 54: return true;
             case 4: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 5: return "asOfModifiedTime";
             case 52: return "comment";
             case 1: return "fileName";
             case 51: return "invokedBy";
             case 3: return "options";
             case 2: return "parentFileName";
             case 53: return "priority";
             case 54: return "requestTime";
             case 4: return "safeFileExtension";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 5: return 0;
             case 52: return 1;
             case 1: return 2;
             case 51: return 3;
             case 3: return 4;
             case 2: return 5;
             case 53: return 6;
             case 54: return 7;
             case 4: return 8;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="asOfModifiedTime") return 5;
             if(name=="comment") return 52;
             if(name=="fileName") return 1;
             if(name=="invokedBy") return 51;
             if(name=="options") return 3;
             if(name=="parentFileName") return 2;
             if(name=="priority") return 53;
             if(name=="requestTime") return 54;
             if(name=="safeFileExtension") return 4;
            
             if("asOfModifiedTime".equals(name)) return 5;
             if("comment".equals(name)) return 52;
             if("fileName".equals(name)) return 1;
             if("invokedBy".equals(name)) return 51;
             if("options".equals(name)) return 3;
             if("parentFileName".equals(name)) return 2;
             if("priority".equals(name)) return 53;
             if("requestTime".equals(name)) return 54;
             if("safeFileExtension".equals(name)) return 4;
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
             case 5: return  OH.valueOf(this._asOfModifiedTime); 
             case 52: return  this._comment; 
             case 1: return  this._fileName; 
             case 51: return  this._invokedBy; 
             case 3: return  OH.valueOf(this._options); 
             case 2: return  this._parentFileName; 
             case 53: return  OH.valueOf(this._priority); 
             case 54: return  OH.valueOf(this._requestTime); 
             case 4: return  this._safeFileExtension; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 5: return long.class;
             case 52: return java.lang.String.class;
             case 1: return java.lang.String.class;
             case 51: return java.lang.String.class;
             case 3: return short.class;
             case 2: return java.lang.String.class;
             case 53: return int.class;
             case 54: return long.class;
             case 4: return java.lang.String.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 5: return 6;
             case 52: return 20;
             case 1: return 20;
             case 51: return 20;
             case 3: return 2;
             case 2: return 20;
             case 53: return 4;
             case 54: return 6;
             case 4: return 20;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 5: this._asOfModifiedTime=(java.lang.Long)value;return;
             case 52: this._comment=(java.lang.String)value;return;
             case 1: this._fileName=(java.lang.String)value;return;
             case 51: this._invokedBy=(java.lang.String)value;return;
             case 3: this._options=(java.lang.Short)value;return;
             case 2: this._parentFileName=(java.lang.String)value;return;
             case 53: this._priority=(java.lang.Integer)value;return;
             case 54: this._requestTime=(java.lang.Long)value;return;
             case 4: this._safeFileExtension=(java.lang.String)value;return;
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
             case 5: this._asOfModifiedTime=(java.lang.Long)value;return true;
             case 52: this._comment=(java.lang.String)value;return true;
             case 1: this._fileName=(java.lang.String)value;return true;
             case 51: this._invokedBy=(java.lang.String)value;return true;
             case 3: this._options=(java.lang.Short)value;return true;
             case 2: this._parentFileName=(java.lang.String)value;return true;
             case 53: this._priority=(java.lang.Integer)value;return true;
             case 54: this._requestTime=(java.lang.Long)value;return true;
             case 4: this._safeFileExtension=(java.lang.String)value;return true;
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
             case 3: return this._options;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for short value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askInt(byte pid){
        switch(pid){
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
             case 5: return this._asOfModifiedTime;
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
             case 3: this._options=value;return;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for short value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putInt(byte pid,int value){
    
        switch(pid){
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
             case 5: this._asOfModifiedTime=value;return;
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
        
            this._fileName=(java.lang.String)converter.read(session);
        
            break;

        case 2:
        
            this._parentFileName=(java.lang.String)converter.read(session);
        
            break;

        case 3:
        
            if((basicType=in.readByte())!=2)
                break;
            this._options=in.readShort();
        
            break;

        case 4:
        
            this._safeFileExtension=(java.lang.String)converter.read(session);
        
            break;

        case 5:
        
            if((basicType=in.readByte())!=6)
                break;
            this._asOfModifiedTime=in.readLong();
        
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
        
if(this._fileName!=null && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this._fileName,session);
        
}

if(this._parentFileName!=null && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this._parentFileName,session);
        
}

if(this._options!=(short)0 && (0 & transience)==0){
    out.writeByte(3);
        
    out.writeByte(2);
    out.writeShort(this._options);
        
}

if(this._safeFileExtension!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._safeFileExtension,session);
        
}

if(this._asOfModifiedTime!=0L && (0 & transience)==0){
    out.writeByte(5);
        
    out.writeByte(6);
    out.writeLong(this._asOfModifiedTime);
        
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