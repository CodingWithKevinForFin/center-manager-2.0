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

public abstract class AmiWebManagerPutFileRequest0 implements com.f1.ami.amicommon.msg.AmiWebManagerPutFileRequest ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private short _action;

    private java.lang.String _comment;

    private byte[] _data;

    private java.lang.Boolean _executable;

    private java.lang.String _fileName;

    private java.lang.String _invokedBy;

    private short _options;

    private int _priority;

    private java.lang.Boolean _readable;

    private long _requestTime;

    private java.lang.String _safeFileExtension;

    private java.lang.String _targetFileName;

    private java.lang.Boolean _writable;

    private static final String NAMES[]={ "action" ,"comment","data","executable","fileName","invokedBy","options","priority","readable","requestTime","safeFileExtension","targetFileName","writable"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 46;
        try{
        switch(h){

                case 0:

                    if(name == "writable" || name.equals("writable")) {this._writable=(java.lang.Boolean)value;return;}
break;
                case 2:

                    if(name == "executable" || name.equals("executable")) {this._executable=(java.lang.Boolean)value;return;}
break;
                case 6:

                    if(name == "options" || name.equals("options")) {this._options=(java.lang.Short)value;return;}
break;
                case 10:

                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {this._safeFileExtension=(java.lang.String)value;return;}
break;
                case 11:

                    if(name == "comment" || name.equals("comment")) {this._comment=(java.lang.String)value;return;}
break;
                case 14:

                    if(name == "action" || name.equals("action")) {this._action=(java.lang.Short)value;return;}
break;
                case 15:

                    if(name == "fileName" || name.equals("fileName")) {this._fileName=(java.lang.String)value;return;}
break;
                case 16:

                    if(name == "targetFileName" || name.equals("targetFileName")) {this._targetFileName=(java.lang.String)value;return;}
break;
                case 20:

                    if(name == "requestTime" || name.equals("requestTime")) {this._requestTime=(java.lang.Long)value;return;}
break;
                case 24:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return;}
break;
                case 36:

                    if(name == "data" || name.equals("data")) {this._data=(byte[])value;return;}
break;
                case 38:

                    if(name == "readable" || name.equals("readable")) {this._readable=(java.lang.Boolean)value;return;}
break;
                case 45:

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
        final int h=Math.abs(name.hashCode()) % 46;
        switch(h){

                case 0:

                    if(name == "writable" || name.equals("writable")) {this._writable=(java.lang.Boolean)value;return true;}
break;
                case 2:

                    if(name == "executable" || name.equals("executable")) {this._executable=(java.lang.Boolean)value;return true;}
break;
                case 6:

                    if(name == "options" || name.equals("options")) {this._options=(java.lang.Short)value;return true;}
break;
                case 10:

                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {this._safeFileExtension=(java.lang.String)value;return true;}
break;
                case 11:

                    if(name == "comment" || name.equals("comment")) {this._comment=(java.lang.String)value;return true;}
break;
                case 14:

                    if(name == "action" || name.equals("action")) {this._action=(java.lang.Short)value;return true;}
break;
                case 15:

                    if(name == "fileName" || name.equals("fileName")) {this._fileName=(java.lang.String)value;return true;}
break;
                case 16:

                    if(name == "targetFileName" || name.equals("targetFileName")) {this._targetFileName=(java.lang.String)value;return true;}
break;
                case 20:

                    if(name == "requestTime" || name.equals("requestTime")) {this._requestTime=(java.lang.Long)value;return true;}
break;
                case 24:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return true;}
break;
                case 36:

                    if(name == "data" || name.equals("data")) {this._data=(byte[])value;return true;}
break;
                case 38:

                    if(name == "readable" || name.equals("readable")) {this._readable=(java.lang.Boolean)value;return true;}
break;
                case 45:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 46;
        switch(h){

                case 0:

		    
                    if(name == "writable" || name.equals("writable")) {return this._writable;}
            
break;
                case 2:

		    
                    if(name == "executable" || name.equals("executable")) {return this._executable;}
            
break;
                case 6:

		    
                    if(name == "options" || name.equals("options")) {return OH.valueOf(this._options);}
		    
break;
                case 10:

		    
                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {return this._safeFileExtension;}
            
break;
                case 11:

		    
                    if(name == "comment" || name.equals("comment")) {return this._comment;}
            
break;
                case 14:

		    
                    if(name == "action" || name.equals("action")) {return OH.valueOf(this._action);}
		    
break;
                case 15:

		    
                    if(name == "fileName" || name.equals("fileName")) {return this._fileName;}
            
break;
                case 16:

		    
                    if(name == "targetFileName" || name.equals("targetFileName")) {return this._targetFileName;}
            
break;
                case 20:

		    
                    if(name == "requestTime" || name.equals("requestTime")) {return OH.valueOf(this._requestTime);}
		    
break;
                case 24:

		    
                    if(name == "priority" || name.equals("priority")) {return OH.valueOf(this._priority);}
		    
break;
                case 36:

		    
                    if(name == "data" || name.equals("data")) {return this._data;}
            
break;
                case 38:

		    
                    if(name == "readable" || name.equals("readable")) {return this._readable;}
            
break;
                case 45:

		    
                    if(name == "invokedBy" || name.equals("invokedBy")) {return this._invokedBy;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 46;
        switch(h){

                case 0:

                    if(name == "writable" || name.equals("writable")) {return java.lang.Boolean.class;}
break;
                case 2:

                    if(name == "executable" || name.equals("executable")) {return java.lang.Boolean.class;}
break;
                case 6:

                    if(name == "options" || name.equals("options")) {return short.class;}
break;
                case 10:

                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {return java.lang.String.class;}
break;
                case 11:

                    if(name == "comment" || name.equals("comment")) {return java.lang.String.class;}
break;
                case 14:

                    if(name == "action" || name.equals("action")) {return short.class;}
break;
                case 15:

                    if(name == "fileName" || name.equals("fileName")) {return java.lang.String.class;}
break;
                case 16:

                    if(name == "targetFileName" || name.equals("targetFileName")) {return java.lang.String.class;}
break;
                case 20:

                    if(name == "requestTime" || name.equals("requestTime")) {return long.class;}
break;
                case 24:

                    if(name == "priority" || name.equals("priority")) {return int.class;}
break;
                case 36:

                    if(name == "data" || name.equals("data")) {return byte[].class;}
break;
                case 38:

                    if(name == "readable" || name.equals("readable")) {return java.lang.Boolean.class;}
break;
                case 45:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return java.lang.String.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 46;
        switch(h){

                case 0:

                    if(name == "writable" || name.equals("writable")) {return VALUED_PARAM_writable;}
break;
                case 2:

                    if(name == "executable" || name.equals("executable")) {return VALUED_PARAM_executable;}
break;
                case 6:

                    if(name == "options" || name.equals("options")) {return VALUED_PARAM_options;}
break;
                case 10:

                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {return VALUED_PARAM_safeFileExtension;}
break;
                case 11:

                    if(name == "comment" || name.equals("comment")) {return VALUED_PARAM_comment;}
break;
                case 14:

                    if(name == "action" || name.equals("action")) {return VALUED_PARAM_action;}
break;
                case 15:

                    if(name == "fileName" || name.equals("fileName")) {return VALUED_PARAM_fileName;}
break;
                case 16:

                    if(name == "targetFileName" || name.equals("targetFileName")) {return VALUED_PARAM_targetFileName;}
break;
                case 20:

                    if(name == "requestTime" || name.equals("requestTime")) {return VALUED_PARAM_requestTime;}
break;
                case 24:

                    if(name == "priority" || name.equals("priority")) {return VALUED_PARAM_priority;}
break;
                case 36:

                    if(name == "data" || name.equals("data")) {return VALUED_PARAM_data;}
break;
                case 38:

                    if(name == "readable" || name.equals("readable")) {return VALUED_PARAM_readable;}
break;
                case 45:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return VALUED_PARAM_invokedBy;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 46;
        switch(h){

                case 0:

                    if(name == "writable" || name.equals("writable")) {return 12;}
break;
                case 2:

                    if(name == "executable" || name.equals("executable")) {return 3;}
break;
                case 6:

                    if(name == "options" || name.equals("options")) {return 6;}
break;
                case 10:

                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {return 10;}
break;
                case 11:

                    if(name == "comment" || name.equals("comment")) {return 1;}
break;
                case 14:

                    if(name == "action" || name.equals("action")) {return 0;}
break;
                case 15:

                    if(name == "fileName" || name.equals("fileName")) {return 4;}
break;
                case 16:

                    if(name == "targetFileName" || name.equals("targetFileName")) {return 11;}
break;
                case 20:

                    if(name == "requestTime" || name.equals("requestTime")) {return 9;}
break;
                case 24:

                    if(name == "priority" || name.equals("priority")) {return 7;}
break;
                case 36:

                    if(name == "data" || name.equals("data")) {return 2;}
break;
                case 38:

                    if(name == "readable" || name.equals("readable")) {return 8;}
break;
                case 45:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 5;}
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
        return 13;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiWebManagerPutFileRequest0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 46;
        switch(h){

                case 0:

                    if(name == "writable" || name.equals("writable")) {return true;}
break;
                case 2:

                    if(name == "executable" || name.equals("executable")) {return true;}
break;
                case 6:

                    if(name == "options" || name.equals("options")) {return true;}
break;
                case 10:

                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {return true;}
break;
                case 11:

                    if(name == "comment" || name.equals("comment")) {return true;}
break;
                case 14:

                    if(name == "action" || name.equals("action")) {return true;}
break;
                case 15:

                    if(name == "fileName" || name.equals("fileName")) {return true;}
break;
                case 16:

                    if(name == "targetFileName" || name.equals("targetFileName")) {return true;}
break;
                case 20:

                    if(name == "requestTime" || name.equals("requestTime")) {return true;}
break;
                case 24:

                    if(name == "priority" || name.equals("priority")) {return true;}
break;
                case 36:

                    if(name == "data" || name.equals("data")) {return true;}
break;
                case 38:

                    if(name == "readable" || name.equals("readable")) {return true;}
break;
                case 45:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 46;
        switch(h){

                case 0:

                    if(name == "writable" || name.equals("writable")) {return 10;}
break;
                case 2:

                    if(name == "executable" || name.equals("executable")) {return 10;}
break;
                case 6:

                    if(name == "options" || name.equals("options")) {return 2;}
break;
                case 10:

                    if(name == "safeFileExtension" || name.equals("safeFileExtension")) {return 20;}
break;
                case 11:

                    if(name == "comment" || name.equals("comment")) {return 20;}
break;
                case 14:

                    if(name == "action" || name.equals("action")) {return 2;}
break;
                case 15:

                    if(name == "fileName" || name.equals("fileName")) {return 20;}
break;
                case 16:

                    if(name == "targetFileName" || name.equals("targetFileName")) {return 20;}
break;
                case 20:

                    if(name == "requestTime" || name.equals("requestTime")) {return 6;}
break;
                case 24:

                    if(name == "priority" || name.equals("priority")) {return 4;}
break;
                case 36:

                    if(name == "data" || name.equals("data")) {return 101;}
break;
                case 38:

                    if(name == "readable" || name.equals("readable")) {return 10;}
break;
                case 45:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 20;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _action;

        case 1:return _comment;

        case 2:return _data;

        case 3:return _executable;

        case 4:return _fileName;

        case 5:return _invokedBy;

        case 6:return _options;

        case 7:return _priority;

        case 8:return _readable;

        case 9:return _requestTime;

        case 10:return _safeFileExtension;

        case 11:return _targetFileName;

        case 12:return _writable;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 13);
    }

    public short getAction(){
        return this._action;
    }
    public void setAction(short _action){
    
        this._action=_action;
    }

    public java.lang.String getComment(){
        return this._comment;
    }
    public void setComment(java.lang.String _comment){
    
        this._comment=_comment;
    }

    public byte[] getData(){
        return this._data;
    }
    public void setData(byte[] _data){
    
        this._data=_data;
    }

    public java.lang.Boolean getExecutable(){
        return this._executable;
    }
    public void setExecutable(java.lang.Boolean _executable){
    
        this._executable=_executable;
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

    public int getPriority(){
        return this._priority;
    }
    public void setPriority(int _priority){
    
        this._priority=_priority;
    }

    public java.lang.Boolean getReadable(){
        return this._readable;
    }
    public void setReadable(java.lang.Boolean _readable){
    
        this._readable=_readable;
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

    public java.lang.String getTargetFileName(){
        return this._targetFileName;
    }
    public void setTargetFileName(java.lang.String _targetFileName){
    
        this._targetFileName=_targetFileName;
    }

    public java.lang.Boolean getWritable(){
        return this._writable;
    }
    public void setWritable(java.lang.Boolean _writable){
    
        this._writable=_writable;
    }





  
    private static final class VALUED_PARAM_CLASS_action extends AbstractValuedParam<AmiWebManagerPutFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 2;
	    }
	    
	    @Override
	    public void write(AmiWebManagerPutFileRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeShort(valued.getAction());
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerPutFileRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setAction(stream.readShort());
		    
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
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "action";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerPutFileRequest0 valued) {
		    return (short)((AmiWebManagerPutFileRequest0)valued).getAction();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerPutFileRequest0 valued, Object value) {
		    valued.setAction((java.lang.Short)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
		    dest.setAction(source.getAction());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
	        return OH.eq(dest.getAction(),source.getAction());
	    }
	    
	    
	    
	    
	    
	    
	    @Override
	    public short getShort(AmiWebManagerPutFileRequest0 valued) {
		    return valued.getAction();
	    }
    
	    @Override
	    public void setShort(AmiWebManagerPutFileRequest0 valued, short value) {
		    valued.setAction(value);
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
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAction());
	        
	    }
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAction());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:short action";
	    }
	    @Override
	    public void clear(AmiWebManagerPutFileRequest0 valued){
	       valued.setAction((short)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_action=new VALUED_PARAM_CLASS_action();
  

  
    private static final class VALUED_PARAM_CLASS_comment extends AbstractValuedParam<AmiWebManagerPutFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebManagerPutFileRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerPutFileRequest0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiWebManagerPutFileRequest0 valued) {
		    return (java.lang.String)((AmiWebManagerPutFileRequest0)valued).getComment();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerPutFileRequest0 valued, Object value) {
		    valued.setComment((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
		    dest.setComment(source.getComment());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
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
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getComment(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getComment(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String comment";
	    }
	    @Override
	    public void clear(AmiWebManagerPutFileRequest0 valued){
	       valued.setComment(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_comment=new VALUED_PARAM_CLASS_comment();
  

  
    private static final class VALUED_PARAM_CLASS_data extends AbstractValuedParam<AmiWebManagerPutFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 101;
	    }
	    
	    @Override
	    public void write(AmiWebManagerPutFileRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [B}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerPutFileRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [B}");
		    
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
            return "data";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerPutFileRequest0 valued) {
		    return (byte[])((AmiWebManagerPutFileRequest0)valued).getData();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerPutFileRequest0 valued, Object value) {
		    valued.setData((byte[])value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
		    dest.setData(source.getData());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
	        return OH.eq(dest.getData(),source.getData());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return byte[].class;
	    }
	    private static final Caster CASTER=OH.getCaster(byte[].class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getData());
	        
	    }
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getData());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte[] data";
	    }
	    @Override
	    public void clear(AmiWebManagerPutFileRequest0 valued){
	       valued.setData(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_data=new VALUED_PARAM_CLASS_data();
  

  
    private static final class VALUED_PARAM_CLASS_executable extends AbstractValuedParam<AmiWebManagerPutFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 10;
	    }
	    
	    @Override
	    public void write(AmiWebManagerPutFileRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Boolean}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerPutFileRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Boolean}");
		    
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
		    return true;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return true || false;
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
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "executable";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerPutFileRequest0 valued) {
		    return (java.lang.Boolean)((AmiWebManagerPutFileRequest0)valued).getExecutable();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerPutFileRequest0 valued, Object value) {
		    valued.setExecutable((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
		    dest.setExecutable(source.getExecutable());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
	        return OH.eq(dest.getExecutable(),source.getExecutable());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.Boolean.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.Boolean.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getExecutable());
	        
	    }
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getExecutable());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Boolean executable";
	    }
	    @Override
	    public void clear(AmiWebManagerPutFileRequest0 valued){
	       valued.setExecutable(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_executable=new VALUED_PARAM_CLASS_executable();
  

  
    private static final class VALUED_PARAM_CLASS_fileName extends AbstractValuedParam<AmiWebManagerPutFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebManagerPutFileRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerPutFileRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "fileName";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerPutFileRequest0 valued) {
		    return (java.lang.String)((AmiWebManagerPutFileRequest0)valued).getFileName();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerPutFileRequest0 valued, Object value) {
		    valued.setFileName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
		    dest.setFileName(source.getFileName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
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
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getFileName(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getFileName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String fileName";
	    }
	    @Override
	    public void clear(AmiWebManagerPutFileRequest0 valued){
	       valued.setFileName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_fileName=new VALUED_PARAM_CLASS_fileName();
  

  
    private static final class VALUED_PARAM_CLASS_invokedBy extends AbstractValuedParam<AmiWebManagerPutFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebManagerPutFileRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerPutFileRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 51;
	    }
    
	    @Override
	    public String getName() {
            return "invokedBy";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerPutFileRequest0 valued) {
		    return (java.lang.String)((AmiWebManagerPutFileRequest0)valued).getInvokedBy();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerPutFileRequest0 valued, Object value) {
		    valued.setInvokedBy((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
		    dest.setInvokedBy(source.getInvokedBy());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
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
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String invokedBy";
	    }
	    @Override
	    public void clear(AmiWebManagerPutFileRequest0 valued){
	       valued.setInvokedBy(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_invokedBy=new VALUED_PARAM_CLASS_invokedBy();
  

  
    private static final class VALUED_PARAM_CLASS_options extends AbstractValuedParam<AmiWebManagerPutFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 2;
	    }
	    
	    @Override
	    public void write(AmiWebManagerPutFileRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeShort(valued.getOptions());
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerPutFileRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 6;
	    }
    
	    @Override
	    public byte getPid() {
            return 8;
	    }
    
	    @Override
	    public String getName() {
            return "options";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerPutFileRequest0 valued) {
		    return (short)((AmiWebManagerPutFileRequest0)valued).getOptions();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerPutFileRequest0 valued, Object value) {
		    valued.setOptions((java.lang.Short)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
		    dest.setOptions(source.getOptions());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
	        return OH.eq(dest.getOptions(),source.getOptions());
	    }
	    
	    
	    
	    
	    
	    
	    @Override
	    public short getShort(AmiWebManagerPutFileRequest0 valued) {
		    return valued.getOptions();
	    }
    
	    @Override
	    public void setShort(AmiWebManagerPutFileRequest0 valued, short value) {
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
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getOptions());
	        
	    }
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getOptions());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:short options";
	    }
	    @Override
	    public void clear(AmiWebManagerPutFileRequest0 valued){
	       valued.setOptions((short)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_options=new VALUED_PARAM_CLASS_options();
  

  
    private static final class VALUED_PARAM_CLASS_priority extends AbstractValuedParam<AmiWebManagerPutFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiWebManagerPutFileRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getPriority());
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerPutFileRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 7;
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
	    public Object getValue(AmiWebManagerPutFileRequest0 valued) {
		    return (int)((AmiWebManagerPutFileRequest0)valued).getPriority();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerPutFileRequest0 valued, Object value) {
		    valued.setPriority((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
		    dest.setPriority(source.getPriority());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
	        return OH.eq(dest.getPriority(),source.getPriority());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiWebManagerPutFileRequest0 valued) {
		    return valued.getPriority();
	    }
    
	    @Override
	    public void setInt(AmiWebManagerPutFileRequest0 valued, int value) {
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
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int priority";
	    }
	    @Override
	    public void clear(AmiWebManagerPutFileRequest0 valued){
	       valued.setPriority(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_priority=new VALUED_PARAM_CLASS_priority();
  

  
    private static final class VALUED_PARAM_CLASS_readable extends AbstractValuedParam<AmiWebManagerPutFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 10;
	    }
	    
	    @Override
	    public void write(AmiWebManagerPutFileRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Boolean}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerPutFileRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Boolean}");
		    
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
		    return true;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return true || false;
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
            return 6;
	    }
    
	    @Override
	    public String getName() {
            return "readable";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerPutFileRequest0 valued) {
		    return (java.lang.Boolean)((AmiWebManagerPutFileRequest0)valued).getReadable();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerPutFileRequest0 valued, Object value) {
		    valued.setReadable((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
		    dest.setReadable(source.getReadable());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
	        return OH.eq(dest.getReadable(),source.getReadable());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.Boolean.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.Boolean.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getReadable());
	        
	    }
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getReadable());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Boolean readable";
	    }
	    @Override
	    public void clear(AmiWebManagerPutFileRequest0 valued){
	       valued.setReadable(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_readable=new VALUED_PARAM_CLASS_readable();
  

  
    private static final class VALUED_PARAM_CLASS_requestTime extends AbstractValuedParam<AmiWebManagerPutFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiWebManagerPutFileRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getRequestTime());
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerPutFileRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 9;
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
	    public Object getValue(AmiWebManagerPutFileRequest0 valued) {
		    return (long)((AmiWebManagerPutFileRequest0)valued).getRequestTime();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerPutFileRequest0 valued, Object value) {
		    valued.setRequestTime((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
		    dest.setRequestTime(source.getRequestTime());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
	        return OH.eq(dest.getRequestTime(),source.getRequestTime());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiWebManagerPutFileRequest0 valued) {
		    return valued.getRequestTime();
	    }
    
	    @Override
	    public void setLong(AmiWebManagerPutFileRequest0 valued, long value) {
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
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getRequestTime());
	        
	    }
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getRequestTime());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long requestTime";
	    }
	    @Override
	    public void clear(AmiWebManagerPutFileRequest0 valued){
	       valued.setRequestTime(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_requestTime=new VALUED_PARAM_CLASS_requestTime();
  

  
    private static final class VALUED_PARAM_CLASS_safeFileExtension extends AbstractValuedParam<AmiWebManagerPutFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebManagerPutFileRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerPutFileRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 10;
	    }
    
	    @Override
	    public byte getPid() {
            return 9;
	    }
    
	    @Override
	    public String getName() {
            return "safeFileExtension";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerPutFileRequest0 valued) {
		    return (java.lang.String)((AmiWebManagerPutFileRequest0)valued).getSafeFileExtension();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerPutFileRequest0 valued, Object value) {
		    valued.setSafeFileExtension((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
		    dest.setSafeFileExtension(source.getSafeFileExtension());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
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
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getSafeFileExtension(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getSafeFileExtension(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String safeFileExtension";
	    }
	    @Override
	    public void clear(AmiWebManagerPutFileRequest0 valued){
	       valued.setSafeFileExtension(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_safeFileExtension=new VALUED_PARAM_CLASS_safeFileExtension();
  

  
    private static final class VALUED_PARAM_CLASS_targetFileName extends AbstractValuedParam<AmiWebManagerPutFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebManagerPutFileRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerPutFileRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 11;
	    }
    
	    @Override
	    public byte getPid() {
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "targetFileName";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerPutFileRequest0 valued) {
		    return (java.lang.String)((AmiWebManagerPutFileRequest0)valued).getTargetFileName();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerPutFileRequest0 valued, Object value) {
		    valued.setTargetFileName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
		    dest.setTargetFileName(source.getTargetFileName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
	        return OH.eq(dest.getTargetFileName(),source.getTargetFileName());
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
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getTargetFileName(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getTargetFileName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String targetFileName";
	    }
	    @Override
	    public void clear(AmiWebManagerPutFileRequest0 valued){
	       valued.setTargetFileName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_targetFileName=new VALUED_PARAM_CLASS_targetFileName();
  

  
    private static final class VALUED_PARAM_CLASS_writable extends AbstractValuedParam<AmiWebManagerPutFileRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 10;
	    }
	    
	    @Override
	    public void write(AmiWebManagerPutFileRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Boolean}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerPutFileRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Boolean}");
		    
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
		    return true;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return true || false;
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
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "writable";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerPutFileRequest0 valued) {
		    return (java.lang.Boolean)((AmiWebManagerPutFileRequest0)valued).getWritable();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerPutFileRequest0 valued, Object value) {
		    valued.setWritable((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
		    dest.setWritable(source.getWritable());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerPutFileRequest0 source, AmiWebManagerPutFileRequest0 dest) {
	        return OH.eq(dest.getWritable(),source.getWritable());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.Boolean.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.Boolean.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getWritable());
	        
	    }
	    @Override
	    public void append(AmiWebManagerPutFileRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getWritable());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Boolean writable";
	    }
	    @Override
	    public void clear(AmiWebManagerPutFileRequest0 valued){
	       valued.setWritable(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_writable=new VALUED_PARAM_CLASS_writable();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_action, VALUED_PARAM_comment, VALUED_PARAM_data, VALUED_PARAM_executable, VALUED_PARAM_fileName, VALUED_PARAM_invokedBy, VALUED_PARAM_options, VALUED_PARAM_priority, VALUED_PARAM_readable, VALUED_PARAM_requestTime, VALUED_PARAM_safeFileExtension, VALUED_PARAM_targetFileName, VALUED_PARAM_writable, };



    private static final byte PIDS[]={ 2 ,52,3,7,1,51,8,53,6,54,9,4,5};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 2: return VALUED_PARAM_action;
             case 52: return VALUED_PARAM_comment;
             case 3: return VALUED_PARAM_data;
             case 7: return VALUED_PARAM_executable;
             case 1: return VALUED_PARAM_fileName;
             case 51: return VALUED_PARAM_invokedBy;
             case 8: return VALUED_PARAM_options;
             case 53: return VALUED_PARAM_priority;
             case 6: return VALUED_PARAM_readable;
             case 54: return VALUED_PARAM_requestTime;
             case 9: return VALUED_PARAM_safeFileExtension;
             case 4: return VALUED_PARAM_targetFileName;
             case 5: return VALUED_PARAM_writable;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 2: return true;
             case 52: return true;
             case 3: return true;
             case 7: return true;
             case 1: return true;
             case 51: return true;
             case 8: return true;
             case 53: return true;
             case 6: return true;
             case 54: return true;
             case 9: return true;
             case 4: return true;
             case 5: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 2: return "action";
             case 52: return "comment";
             case 3: return "data";
             case 7: return "executable";
             case 1: return "fileName";
             case 51: return "invokedBy";
             case 8: return "options";
             case 53: return "priority";
             case 6: return "readable";
             case 54: return "requestTime";
             case 9: return "safeFileExtension";
             case 4: return "targetFileName";
             case 5: return "writable";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 2: return 0;
             case 52: return 1;
             case 3: return 2;
             case 7: return 3;
             case 1: return 4;
             case 51: return 5;
             case 8: return 6;
             case 53: return 7;
             case 6: return 8;
             case 54: return 9;
             case 9: return 10;
             case 4: return 11;
             case 5: return 12;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="action") return 2;
             if(name=="comment") return 52;
             if(name=="data") return 3;
             if(name=="executable") return 7;
             if(name=="fileName") return 1;
             if(name=="invokedBy") return 51;
             if(name=="options") return 8;
             if(name=="priority") return 53;
             if(name=="readable") return 6;
             if(name=="requestTime") return 54;
             if(name=="safeFileExtension") return 9;
             if(name=="targetFileName") return 4;
             if(name=="writable") return 5;
            
             if("action".equals(name)) return 2;
             if("comment".equals(name)) return 52;
             if("data".equals(name)) return 3;
             if("executable".equals(name)) return 7;
             if("fileName".equals(name)) return 1;
             if("invokedBy".equals(name)) return 51;
             if("options".equals(name)) return 8;
             if("priority".equals(name)) return 53;
             if("readable".equals(name)) return 6;
             if("requestTime".equals(name)) return 54;
             if("safeFileExtension".equals(name)) return 9;
             if("targetFileName".equals(name)) return 4;
             if("writable".equals(name)) return 5;
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
             case 2: return  OH.valueOf(this._action); 
             case 52: return  this._comment; 
             case 3: return  this._data; 
             case 7: return  this._executable; 
             case 1: return  this._fileName; 
             case 51: return  this._invokedBy; 
             case 8: return  OH.valueOf(this._options); 
             case 53: return  OH.valueOf(this._priority); 
             case 6: return  this._readable; 
             case 54: return  OH.valueOf(this._requestTime); 
             case 9: return  this._safeFileExtension; 
             case 4: return  this._targetFileName; 
             case 5: return  this._writable; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 2: return short.class;
             case 52: return java.lang.String.class;
             case 3: return byte[].class;
             case 7: return java.lang.Boolean.class;
             case 1: return java.lang.String.class;
             case 51: return java.lang.String.class;
             case 8: return short.class;
             case 53: return int.class;
             case 6: return java.lang.Boolean.class;
             case 54: return long.class;
             case 9: return java.lang.String.class;
             case 4: return java.lang.String.class;
             case 5: return java.lang.Boolean.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 2: return 2;
             case 52: return 20;
             case 3: return 101;
             case 7: return 10;
             case 1: return 20;
             case 51: return 20;
             case 8: return 2;
             case 53: return 4;
             case 6: return 10;
             case 54: return 6;
             case 9: return 20;
             case 4: return 20;
             case 5: return 10;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 2: this._action=(java.lang.Short)value;return;
             case 52: this._comment=(java.lang.String)value;return;
             case 3: this._data=(byte[])value;return;
             case 7: this._executable=(java.lang.Boolean)value;return;
             case 1: this._fileName=(java.lang.String)value;return;
             case 51: this._invokedBy=(java.lang.String)value;return;
             case 8: this._options=(java.lang.Short)value;return;
             case 53: this._priority=(java.lang.Integer)value;return;
             case 6: this._readable=(java.lang.Boolean)value;return;
             case 54: this._requestTime=(java.lang.Long)value;return;
             case 9: this._safeFileExtension=(java.lang.String)value;return;
             case 4: this._targetFileName=(java.lang.String)value;return;
             case 5: this._writable=(java.lang.Boolean)value;return;
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
             case 2: this._action=(java.lang.Short)value;return true;
             case 52: this._comment=(java.lang.String)value;return true;
             case 3: this._data=(byte[])value;return true;
             case 7: this._executable=(java.lang.Boolean)value;return true;
             case 1: this._fileName=(java.lang.String)value;return true;
             case 51: this._invokedBy=(java.lang.String)value;return true;
             case 8: this._options=(java.lang.Short)value;return true;
             case 53: this._priority=(java.lang.Integer)value;return true;
             case 6: this._readable=(java.lang.Boolean)value;return true;
             case 54: this._requestTime=(java.lang.Long)value;return true;
             case 9: this._safeFileExtension=(java.lang.String)value;return true;
             case 4: this._targetFileName=(java.lang.String)value;return true;
             case 5: this._writable=(java.lang.Boolean)value;return true;
            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
             case 7: return this._executable;
             case 6: return this._readable;
             case 5: return this._writable;
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
             case 2: return this._action;
             case 8: return this._options;
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
             case 7: this._executable=value;return;
             case 6: this._readable=value;return;
             case 5: this._writable=value;return;
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
             case 2: this._action=value;return;
             case 8: this._options=value;return;
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
        
            if((basicType=in.readByte())!=2)
                break;
            this._action=in.readShort();
        
            break;

        case 3:
        
            this._data=(byte[])converter.read(session);
        
            break;

        case 4:
        
            this._targetFileName=(java.lang.String)converter.read(session);
        
            break;

        case 5:
        
            this._writable=(java.lang.Boolean)converter.read(session);
        
            break;

        case 6:
        
            this._readable=(java.lang.Boolean)converter.read(session);
        
            break;

        case 7:
        
            this._executable=(java.lang.Boolean)converter.read(session);
        
            break;

        case 8:
        
            if((basicType=in.readByte())!=2)
                break;
            this._options=in.readShort();
        
            break;

        case 9:
        
            this._safeFileExtension=(java.lang.String)converter.read(session);
        
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

if(this._action!=(short)0 && (0 & transience)==0){
    out.writeByte(2);
        
    out.writeByte(2);
    out.writeShort(this._action);
        
}

if(this._data!=null && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this._data,session);
        
}

if(this._targetFileName!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._targetFileName,session);
        
}

if(this._writable!=null && (0 & transience)==0){
    out.writeByte(5);
        
    converter.write(this._writable,session);
        
}

if(this._readable!=null && (0 & transience)==0){
    out.writeByte(6);
        
    converter.write(this._readable,session);
        
}

if(this._executable!=null && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this._executable,session);
        
}

if(this._options!=(short)0 && (0 & transience)==0){
    out.writeByte(8);
        
    out.writeByte(2);
    out.writeShort(this._options);
        
}

if(this._safeFileExtension!=null && (0 & transience)==0){
    out.writeByte(9);
        
    converter.write(this._safeFileExtension,session);
        
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