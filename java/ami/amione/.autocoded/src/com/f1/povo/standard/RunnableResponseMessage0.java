//Coded by ValuedCodeTemplate
package com.f1.povo.standard;

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

public abstract class RunnableResponseMessage0 implements com.f1.povo.standard.RunnableResponseMessage ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private int _priority;

    private byte _resultCode;

    private java.lang.String _text;

    private java.lang.Throwable _throwable;

    private static final String NAMES[]={ "priority" ,"resultCode","text","throwable"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 8;
        try{
        switch(h){

                case 0:

                    if(name == "throwable" || name.equals("throwable")) {this._throwable=(java.lang.Throwable)value;return;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return;}
break;
                case 5:

                    if(name == "text" || name.equals("text")) {this._text=(java.lang.String)value;return;}
break;
                case 6:

                    if(name == "resultCode" || name.equals("resultCode")) {this._resultCode=(java.lang.Byte)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 0:

                    if(name == "throwable" || name.equals("throwable")) {this._throwable=(java.lang.Throwable)value;return true;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return true;}
break;
                case 5:

                    if(name == "text" || name.equals("text")) {this._text=(java.lang.String)value;return true;}
break;
                case 6:

                    if(name == "resultCode" || name.equals("resultCode")) {this._resultCode=(java.lang.Byte)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 0:

		    
                    if(name == "throwable" || name.equals("throwable")) {return this._throwable;}
            
break;
                case 4:

		    
                    if(name == "priority" || name.equals("priority")) {return OH.valueOf(this._priority);}
		    
break;
                case 5:

		    
                    if(name == "text" || name.equals("text")) {return this._text;}
            
break;
                case 6:

		    
                    if(name == "resultCode" || name.equals("resultCode")) {return OH.valueOf(this._resultCode);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 0:

                    if(name == "throwable" || name.equals("throwable")) {return java.lang.Throwable.class;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {return int.class;}
break;
                case 5:

                    if(name == "text" || name.equals("text")) {return java.lang.String.class;}
break;
                case 6:

                    if(name == "resultCode" || name.equals("resultCode")) {return byte.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 0:

                    if(name == "throwable" || name.equals("throwable")) {return VALUED_PARAM_throwable;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {return VALUED_PARAM_priority;}
break;
                case 5:

                    if(name == "text" || name.equals("text")) {return VALUED_PARAM_text;}
break;
                case 6:

                    if(name == "resultCode" || name.equals("resultCode")) {return VALUED_PARAM_resultCode;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 0:

                    if(name == "throwable" || name.equals("throwable")) {return 3;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {return 0;}
break;
                case 5:

                    if(name == "text" || name.equals("text")) {return 2;}
break;
                case 6:

                    if(name == "resultCode" || name.equals("resultCode")) {return 1;}
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
	    return (Class)RunnableResponseMessage0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 0:

                    if(name == "throwable" || name.equals("throwable")) {return true;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {return true;}
break;
                case 5:

                    if(name == "text" || name.equals("text")) {return true;}
break;
                case 6:

                    if(name == "resultCode" || name.equals("resultCode")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 0:

                    if(name == "throwable" || name.equals("throwable")) {return 53;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {return 4;}
break;
                case 5:

                    if(name == "text" || name.equals("text")) {return 20;}
break;
                case 6:

                    if(name == "resultCode" || name.equals("resultCode")) {return 1;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _priority;

        case 1:return _resultCode;

        case 2:return _text;

        case 3:return _throwable;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 4);
    }

    public int getPriority(){
        return this._priority;
    }
    public void setPriority(int _priority){
    
        this._priority=_priority;
    }

    public byte getResultCode(){
        return this._resultCode;
    }
    public void setResultCode(byte _resultCode){
    
        this._resultCode=_resultCode;
    }

    public java.lang.String getText(){
        return this._text;
    }
    public void setText(java.lang.String _text){
    
        this._text=_text;
    }

    public java.lang.Throwable getThrowable(){
        return this._throwable;
    }
    public void setThrowable(java.lang.Throwable _throwable){
    
        this._throwable=_throwable;
    }





  
    private static final class VALUED_PARAM_CLASS_priority extends AbstractValuedParam<RunnableResponseMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(RunnableResponseMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getPriority());
		    
	    }
	    
	    @Override
	    public void read(RunnableResponseMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 0;
	    }
    
	    @Override
	    public byte getPid() {
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "priority";
	    }
    
	    @Override
	    public Object getValue(RunnableResponseMessage0 valued) {
		    return (int)((RunnableResponseMessage0)valued).getPriority();
	    }
    
	    @Override
	    public void setValue(RunnableResponseMessage0 valued, Object value) {
		    valued.setPriority((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(RunnableResponseMessage0 source, RunnableResponseMessage0 dest) {
		    dest.setPriority(source.getPriority());
	    }
	    
	    @Override
	    public boolean areEqual(RunnableResponseMessage0 source, RunnableResponseMessage0 dest) {
	        return OH.eq(dest.getPriority(),source.getPriority());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(RunnableResponseMessage0 valued) {
		    return valued.getPriority();
	    }
    
	    @Override
	    public void setInt(RunnableResponseMessage0 valued, int value) {
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
	    public void append(RunnableResponseMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public void append(RunnableResponseMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int priority";
	    }
	    @Override
	    public void clear(RunnableResponseMessage0 valued){
	       valued.setPriority(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_priority=new VALUED_PARAM_CLASS_priority();
  

  
    private static final class VALUED_PARAM_CLASS_resultCode extends AbstractValuedParam<RunnableResponseMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(RunnableResponseMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getResultCode());
		    
	    }
	    
	    @Override
	    public void read(RunnableResponseMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setResultCode(stream.readByte());
		    
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
            return "resultCode";
	    }
    
	    @Override
	    public Object getValue(RunnableResponseMessage0 valued) {
		    return (byte)((RunnableResponseMessage0)valued).getResultCode();
	    }
    
	    @Override
	    public void setValue(RunnableResponseMessage0 valued, Object value) {
		    valued.setResultCode((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(RunnableResponseMessage0 source, RunnableResponseMessage0 dest) {
		    dest.setResultCode(source.getResultCode());
	    }
	    
	    @Override
	    public boolean areEqual(RunnableResponseMessage0 source, RunnableResponseMessage0 dest) {
	        return OH.eq(dest.getResultCode(),source.getResultCode());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(RunnableResponseMessage0 valued) {
		    return valued.getResultCode();
	    }
    
	    @Override
	    public void setByte(RunnableResponseMessage0 valued, byte value) {
		    valued.setResultCode(value);
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
	    public void append(RunnableResponseMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getResultCode());
	        
	    }
	    @Override
	    public void append(RunnableResponseMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getResultCode());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte resultCode";
	    }
	    @Override
	    public void clear(RunnableResponseMessage0 valued){
	       valued.setResultCode((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_resultCode=new VALUED_PARAM_CLASS_resultCode();
  

  
    private static final class VALUED_PARAM_CLASS_text extends AbstractValuedParam<RunnableResponseMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(RunnableResponseMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(RunnableResponseMessage0 valued, DataInput stream) throws IOException{
		    
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
            return "text";
	    }
    
	    @Override
	    public Object getValue(RunnableResponseMessage0 valued) {
		    return (java.lang.String)((RunnableResponseMessage0)valued).getText();
	    }
    
	    @Override
	    public void setValue(RunnableResponseMessage0 valued, Object value) {
		    valued.setText((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(RunnableResponseMessage0 source, RunnableResponseMessage0 dest) {
		    dest.setText(source.getText());
	    }
	    
	    @Override
	    public boolean areEqual(RunnableResponseMessage0 source, RunnableResponseMessage0 dest) {
	        return OH.eq(dest.getText(),source.getText());
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
	    public void append(RunnableResponseMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getText(),sb);
	        
	    }
	    @Override
	    public void append(RunnableResponseMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getText(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String text";
	    }
	    @Override
	    public void clear(RunnableResponseMessage0 valued){
	       valued.setText(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_text=new VALUED_PARAM_CLASS_text();
  

  
    private static final class VALUED_PARAM_CLASS_throwable extends AbstractValuedParam<RunnableResponseMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 53;
	    }
	    
	    @Override
	    public void write(RunnableResponseMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Throwable}");
		    
	    }
	    
	    @Override
	    public void read(RunnableResponseMessage0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Throwable}");
		    
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
		    return 3;
	    }
    
	    @Override
	    public byte getPid() {
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "throwable";
	    }
    
	    @Override
	    public Object getValue(RunnableResponseMessage0 valued) {
		    return (java.lang.Throwable)((RunnableResponseMessage0)valued).getThrowable();
	    }
    
	    @Override
	    public void setValue(RunnableResponseMessage0 valued, Object value) {
		    valued.setThrowable((java.lang.Throwable)value);
	    }
    
	    @Override
	    public void copy(RunnableResponseMessage0 source, RunnableResponseMessage0 dest) {
		    dest.setThrowable(source.getThrowable());
	    }
	    
	    @Override
	    public boolean areEqual(RunnableResponseMessage0 source, RunnableResponseMessage0 dest) {
	        return OH.eq(dest.getThrowable(),source.getThrowable());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.Throwable.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.Throwable.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(RunnableResponseMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getThrowable());
	        
	    }
	    @Override
	    public void append(RunnableResponseMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getThrowable());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Throwable throwable";
	    }
	    @Override
	    public void clear(RunnableResponseMessage0 valued){
	       valued.setThrowable(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_throwable=new VALUED_PARAM_CLASS_throwable();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_priority, VALUED_PARAM_resultCode, VALUED_PARAM_text, VALUED_PARAM_throwable, };



    private static final byte PIDS[]={ 5 ,3,1,4};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 5: return VALUED_PARAM_priority;
             case 3: return VALUED_PARAM_resultCode;
             case 1: return VALUED_PARAM_text;
             case 4: return VALUED_PARAM_throwable;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 5: return true;
             case 3: return true;
             case 1: return true;
             case 4: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 5: return "priority";
             case 3: return "resultCode";
             case 1: return "text";
             case 4: return "throwable";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 5: return 0;
             case 3: return 1;
             case 1: return 2;
             case 4: return 3;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="priority") return 5;
             if(name=="resultCode") return 3;
             if(name=="text") return 1;
             if(name=="throwable") return 4;
            
             if("priority".equals(name)) return 5;
             if("resultCode".equals(name)) return 3;
             if("text".equals(name)) return 1;
             if("throwable".equals(name)) return 4;
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
             case 5: return  OH.valueOf(this._priority); 
             case 3: return  OH.valueOf(this._resultCode); 
             case 1: return  this._text; 
             case 4: return  this._throwable; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 5: return int.class;
             case 3: return byte.class;
             case 1: return java.lang.String.class;
             case 4: return java.lang.Throwable.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 5: return 4;
             case 3: return 1;
             case 1: return 20;
             case 4: return 53;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 5: this._priority=(java.lang.Integer)value;return;
             case 3: this._resultCode=(java.lang.Byte)value;return;
             case 1: this._text=(java.lang.String)value;return;
             case 4: this._throwable=(java.lang.Throwable)value;return;
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
             case 5: this._priority=(java.lang.Integer)value;return true;
             case 3: this._resultCode=(java.lang.Byte)value;return true;
             case 1: this._text=(java.lang.String)value;return true;
             case 4: this._throwable=(java.lang.Throwable)value;return true;
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
             case 3: return this._resultCode;
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
             case 5: return this._priority;
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
             case 3: this._resultCode=value;return;
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
             case 5: this._priority=value;return;
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
        
            this._text=(java.lang.String)converter.read(session);
        
            break;

        case 3:
        
            if((basicType=in.readByte())!=1)
                break;
            this._resultCode=in.readByte();
        
            break;

        case 4:
        
            this._throwable=(java.lang.Throwable)converter.read(session);
        
            break;

        case 5:
        
            if((basicType=in.readByte())!=4)
                break;
            this._priority=in.readInt();
        
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
        
if(this._text!=null && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this._text,session);
        
}

if(this._resultCode!=(byte)0 && (0 & transience)==0){
    out.writeByte(3);
        
    out.writeByte(1);
    out.writeByte(this._resultCode);
        
}

if(this._throwable!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._throwable,session);
        
}

if(this._priority!=0 && (0 & transience)==0){
    out.writeByte(5);
        
    out.writeByte(4);
    out.writeInt(this._priority);
        
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