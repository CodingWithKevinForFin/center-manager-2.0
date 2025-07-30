//Coded by ValuedCodeTemplate
package com.f1.container;

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

public abstract class ResultMessage0 extends com.f1.container.ResultMessage implements com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,Cloneable{

    private int hashCode=ValuedHashCodeGenerator.next(this);
    
    @Override
    public int hashCode(){
      return this.hashCode;
    }
    
    @Override
	public com.f1.container.ResultMessage clone(){
       try{
         
	       return (com.f1.container.ResultMessage)super.clone();
	     
       } catch( Exception e){
          throw new RuntimeException("error cloning",e);
       }
    }
    
    
    

    private com.f1.base.Action _actionNoThrowable;

    private java.lang.Throwable _error;

    private boolean _isIntermediateResult;

    private com.f1.container.RequestMessage _requestMessage;

    private static final String NAMES[]={ "actionNoThrowable" ,"error","isIntermediateResult","requestMessage"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 7;
        try{
        switch(h){

                case 1:

                    if(name == "actionNoThrowable" || name.equals("actionNoThrowable")) {this._actionNoThrowable=(com.f1.base.Action)value;return;}
break;
                case 3:

                    if(name == "isIntermediateResult" || name.equals("isIntermediateResult")) {this._isIntermediateResult=(java.lang.Boolean)value;return;}
break;
                case 4:

                    if(name == "requestMessage" || name.equals("requestMessage")) {this._requestMessage=(com.f1.container.RequestMessage)value;return;}
break;
                case 6:

                    if(name == "error" || name.equals("error")) {this._error=(java.lang.Throwable)value;return;}
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

                    if(name == "actionNoThrowable" || name.equals("actionNoThrowable")) {this._actionNoThrowable=(com.f1.base.Action)value;return true;}
break;
                case 3:

                    if(name == "isIntermediateResult" || name.equals("isIntermediateResult")) {this._isIntermediateResult=(java.lang.Boolean)value;return true;}
break;
                case 4:

                    if(name == "requestMessage" || name.equals("requestMessage")) {this._requestMessage=(com.f1.container.RequestMessage)value;return true;}
break;
                case 6:

                    if(name == "error" || name.equals("error")) {this._error=(java.lang.Throwable)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

		    
                    if(name == "actionNoThrowable" || name.equals("actionNoThrowable")) {return this._actionNoThrowable;}
            
break;
                case 3:

		    
                    if(name == "isIntermediateResult" || name.equals("isIntermediateResult")) {return OH.valueOf(this._isIntermediateResult);}
		    
break;
                case 4:

		    
                    if(name == "requestMessage" || name.equals("requestMessage")) {return this._requestMessage;}
            
break;
                case 6:

		    
                    if(name == "error" || name.equals("error")) {return this._error;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "actionNoThrowable" || name.equals("actionNoThrowable")) {return com.f1.base.Action.class;}
break;
                case 3:

                    if(name == "isIntermediateResult" || name.equals("isIntermediateResult")) {return boolean.class;}
break;
                case 4:

                    if(name == "requestMessage" || name.equals("requestMessage")) {return com.f1.container.RequestMessage.class;}
break;
                case 6:

                    if(name == "error" || name.equals("error")) {return java.lang.Throwable.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "actionNoThrowable" || name.equals("actionNoThrowable")) {return VALUED_PARAM_actionNoThrowable;}
break;
                case 3:

                    if(name == "isIntermediateResult" || name.equals("isIntermediateResult")) {return VALUED_PARAM_isIntermediateResult;}
break;
                case 4:

                    if(name == "requestMessage" || name.equals("requestMessage")) {return VALUED_PARAM_requestMessage;}
break;
                case 6:

                    if(name == "error" || name.equals("error")) {return VALUED_PARAM_error;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "actionNoThrowable" || name.equals("actionNoThrowable")) {return 0;}
break;
                case 3:

                    if(name == "isIntermediateResult" || name.equals("isIntermediateResult")) {return 2;}
break;
                case 4:

                    if(name == "requestMessage" || name.equals("requestMessage")) {return 3;}
break;
                case 6:

                    if(name == "error" || name.equals("error")) {return 1;}
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
	    return (Class)ResultMessage0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "actionNoThrowable" || name.equals("actionNoThrowable")) {return true;}
break;
                case 3:

                    if(name == "isIntermediateResult" || name.equals("isIntermediateResult")) {return true;}
break;
                case 4:

                    if(name == "requestMessage" || name.equals("requestMessage")) {return true;}
break;
                case 6:

                    if(name == "error" || name.equals("error")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "actionNoThrowable" || name.equals("actionNoThrowable")) {return 60;}
break;
                case 3:

                    if(name == "isIntermediateResult" || name.equals("isIntermediateResult")) {return 0;}
break;
                case 4:

                    if(name == "requestMessage" || name.equals("requestMessage")) {return 41;}
break;
                case 6:

                    if(name == "error" || name.equals("error")) {return 53;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _actionNoThrowable;

        case 1:return _error;

        case 2:return _isIntermediateResult;

        case 3:return _requestMessage;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 4);
    }

    public com.f1.base.Action getActionNoThrowable(){
        return this._actionNoThrowable;
    }
    public void setActionNoThrowable(com.f1.base.Action _actionNoThrowable){
    
        this._actionNoThrowable=_actionNoThrowable;
    }

    public java.lang.Throwable getError(){
        return this._error;
    }
    public void setError(java.lang.Throwable _error){
    
        this._error=_error;
    }

    public boolean getIsIntermediateResult(){
        return this._isIntermediateResult;
    }
    public void setIsIntermediateResult(boolean _isIntermediateResult){
    
        this._isIntermediateResult=_isIntermediateResult;
    }

    public com.f1.container.RequestMessage getRequestMessage(){
        return this._requestMessage;
    }
    public void setRequestMessage(com.f1.container.RequestMessage _requestMessage){
    
        this._requestMessage=_requestMessage;
    }





  
    private static final class VALUED_PARAM_CLASS_actionNoThrowable extends AbstractValuedParam<ResultMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 60;
	    }
	    
	    @Override
	    public void write(ResultMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.base.Action}");
		    
	    }
	    
	    @Override
	    public void read(ResultMessage0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.base.Action}");
		    
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
		    return 0;
	    }
    
	    @Override
	    public byte getPid() {
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "actionNoThrowable";
	    }
    
	    @Override
	    public Object getValue(ResultMessage0 valued) {
		    return (com.f1.base.Action)((ResultMessage0)valued).getActionNoThrowable();
	    }
    
	    @Override
	    public void setValue(ResultMessage0 valued, Object value) {
		    valued.setActionNoThrowable((com.f1.base.Action)value);
	    }
    
	    @Override
	    public void copy(ResultMessage0 source, ResultMessage0 dest) {
		    dest.setActionNoThrowable(source.getActionNoThrowable());
	    }
	    
	    @Override
	    public boolean areEqual(ResultMessage0 source, ResultMessage0 dest) {
	        return OH.eq(dest.getActionNoThrowable(),source.getActionNoThrowable());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.base.Action.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.base.Action.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(ResultMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getActionNoThrowable());
	        
	    }
	    @Override
	    public void append(ResultMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getActionNoThrowable());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.base.Action actionNoThrowable";
	    }
	    @Override
	    public void clear(ResultMessage0 valued){
	       valued.setActionNoThrowable(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_actionNoThrowable=new VALUED_PARAM_CLASS_actionNoThrowable();
  

  
    private static final class VALUED_PARAM_CLASS_error extends AbstractValuedParam<ResultMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 53;
	    }
	    
	    @Override
	    public void write(ResultMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Throwable}");
		    
	    }
	    
	    @Override
	    public void read(ResultMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 1;
	    }
    
	    @Override
	    public byte getPid() {
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "error";
	    }
    
	    @Override
	    public Object getValue(ResultMessage0 valued) {
		    return (java.lang.Throwable)((ResultMessage0)valued).getError();
	    }
    
	    @Override
	    public void setValue(ResultMessage0 valued, Object value) {
		    valued.setError((java.lang.Throwable)value);
	    }
    
	    @Override
	    public void copy(ResultMessage0 source, ResultMessage0 dest) {
		    dest.setError(source.getError());
	    }
	    
	    @Override
	    public boolean areEqual(ResultMessage0 source, ResultMessage0 dest) {
	        return OH.eq(dest.getError(),source.getError());
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
	    public void append(ResultMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getError());
	        
	    }
	    @Override
	    public void append(ResultMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getError());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Throwable error";
	    }
	    @Override
	    public void clear(ResultMessage0 valued){
	       valued.setError(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_error=new VALUED_PARAM_CLASS_error();
  

  
    private static final class VALUED_PARAM_CLASS_isIntermediateResult extends AbstractValuedParam<ResultMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(ResultMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getIsIntermediateResult());
		    
	    }
	    
	    @Override
	    public void read(ResultMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setIsIntermediateResult(stream.readBoolean());
		    
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "isIntermediateResult";
	    }
    
	    @Override
	    public Object getValue(ResultMessage0 valued) {
		    return (boolean)((ResultMessage0)valued).getIsIntermediateResult();
	    }
    
	    @Override
	    public void setValue(ResultMessage0 valued, Object value) {
		    valued.setIsIntermediateResult((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(ResultMessage0 source, ResultMessage0 dest) {
		    dest.setIsIntermediateResult(source.getIsIntermediateResult());
	    }
	    
	    @Override
	    public boolean areEqual(ResultMessage0 source, ResultMessage0 dest) {
	        return OH.eq(dest.getIsIntermediateResult(),source.getIsIntermediateResult());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(ResultMessage0 valued) {
		    return valued.getIsIntermediateResult();
	    }
    
	    @Override
	    public void setBoolean(ResultMessage0 valued, boolean value) {
		    valued.setIsIntermediateResult(value);
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
	    public void append(ResultMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getIsIntermediateResult());
	        
	    }
	    @Override
	    public void append(ResultMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getIsIntermediateResult());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean isIntermediateResult";
	    }
	    @Override
	    public void clear(ResultMessage0 valued){
	       valued.setIsIntermediateResult(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_isIntermediateResult=new VALUED_PARAM_CLASS_isIntermediateResult();
  

  
    private static final class VALUED_PARAM_CLASS_requestMessage extends AbstractValuedParam<ResultMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 41;
	    }
	    
	    @Override
	    public void write(ResultMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.container.RequestMessage}");
		    
	    }
	    
	    @Override
	    public void read(ResultMessage0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.container.RequestMessage}");
		    
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
		    return 3;
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
            return "requestMessage";
	    }
    
	    @Override
	    public Object getValue(ResultMessage0 valued) {
		    return (com.f1.container.RequestMessage)((ResultMessage0)valued).getRequestMessage();
	    }
    
	    @Override
	    public void setValue(ResultMessage0 valued, Object value) {
		    valued.setRequestMessage((com.f1.container.RequestMessage)value);
	    }
    
	    @Override
	    public void copy(ResultMessage0 source, ResultMessage0 dest) {
		    dest.setRequestMessage(source.getRequestMessage());
	    }
	    
	    @Override
	    public boolean areEqual(ResultMessage0 source, ResultMessage0 dest) {
	        return OH.eq(dest.getRequestMessage(),source.getRequestMessage());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.container.RequestMessage.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.container.RequestMessage.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(ResultMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getRequestMessage());
	        
	    }
	    @Override
	    public void append(ResultMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getRequestMessage());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.container.RequestMessage requestMessage";
	    }
	    @Override
	    public void clear(ResultMessage0 valued){
	       valued.setRequestMessage(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_requestMessage=new VALUED_PARAM_CLASS_requestMessage();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_actionNoThrowable, VALUED_PARAM_error, VALUED_PARAM_isIntermediateResult, VALUED_PARAM_requestMessage, };


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