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

public abstract class RunnableRequestMessage0 implements com.f1.povo.standard.RunnableRequestMessage ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private java.lang.Object _partitionId;

    private int _priority;

    private java.lang.Runnable _runnable;

    private int _timeoutMs;

    private static final String NAMES[]={ "partitionId" ,"priority","runnable","timeoutMs"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 6;
        try{
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return;}
break;
                case 1:

                    if(name == "runnable" || name.equals("runnable")) {this._runnable=(java.lang.Runnable)value;return;}
break;
                case 3:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {this._timeoutMs=(java.lang.Integer)value;return;}
break;
                case 5:

                    if(name == "partitionId" || name.equals("partitionId")) {this._partitionId=(java.lang.Object)value;return;}
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

                case 0:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return true;}
break;
                case 1:

                    if(name == "runnable" || name.equals("runnable")) {this._runnable=(java.lang.Runnable)value;return true;}
break;
                case 3:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {this._timeoutMs=(java.lang.Integer)value;return true;}
break;
                case 5:

                    if(name == "partitionId" || name.equals("partitionId")) {this._partitionId=(java.lang.Object)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 6;
        switch(h){

                case 0:

		    
                    if(name == "priority" || name.equals("priority")) {return OH.valueOf(this._priority);}
		    
break;
                case 1:

		    
                    if(name == "runnable" || name.equals("runnable")) {return this._runnable;}
            
break;
                case 3:

		    
                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return OH.valueOf(this._timeoutMs);}
		    
break;
                case 5:

		    
                    if(name == "partitionId" || name.equals("partitionId")) {return this._partitionId;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 6;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return int.class;}
break;
                case 1:

                    if(name == "runnable" || name.equals("runnable")) {return java.lang.Runnable.class;}
break;
                case 3:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return int.class;}
break;
                case 5:

                    if(name == "partitionId" || name.equals("partitionId")) {return java.lang.Object.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 6;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return VALUED_PARAM_priority;}
break;
                case 1:

                    if(name == "runnable" || name.equals("runnable")) {return VALUED_PARAM_runnable;}
break;
                case 3:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return VALUED_PARAM_timeoutMs;}
break;
                case 5:

                    if(name == "partitionId" || name.equals("partitionId")) {return VALUED_PARAM_partitionId;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 6;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return 1;}
break;
                case 1:

                    if(name == "runnable" || name.equals("runnable")) {return 2;}
break;
                case 3:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return 3;}
break;
                case 5:

                    if(name == "partitionId" || name.equals("partitionId")) {return 0;}
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
	    return (Class)RunnableRequestMessage0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 6;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return true;}
break;
                case 1:

                    if(name == "runnable" || name.equals("runnable")) {return true;}
break;
                case 3:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return true;}
break;
                case 5:

                    if(name == "partitionId" || name.equals("partitionId")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 6;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return 4;}
break;
                case 1:

                    if(name == "runnable" || name.equals("runnable")) {return 60;}
break;
                case 3:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return 4;}
break;
                case 5:

                    if(name == "partitionId" || name.equals("partitionId")) {return 18;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _partitionId;

        case 1:return _priority;

        case 2:return _runnable;

        case 3:return _timeoutMs;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 4);
    }

    public java.lang.Object getPartitionId(){
        return this._partitionId;
    }
    public void setPartitionId(java.lang.Object _partitionId){
    
        this._partitionId=_partitionId;
    }

    public int getPriority(){
        return this._priority;
    }
    public void setPriority(int _priority){
    
        this._priority=_priority;
    }

    public java.lang.Runnable getRunnable(){
        return this._runnable;
    }
    public void setRunnable(java.lang.Runnable _runnable){
    
        this._runnable=_runnable;
    }

    public int getTimeoutMs(){
        return this._timeoutMs;
    }
    public void setTimeoutMs(int _timeoutMs){
    
        this._timeoutMs=_timeoutMs;
    }





  
    private static final class VALUED_PARAM_CLASS_partitionId extends AbstractValuedParam<RunnableRequestMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 18;
	    }
	    
	    @Override
	    public void write(RunnableRequestMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Object}");
		    
	    }
	    
	    @Override
	    public void read(RunnableRequestMessage0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Object}");
		    
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
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "partitionId";
	    }
    
	    @Override
	    public Object getValue(RunnableRequestMessage0 valued) {
		    return (java.lang.Object)((RunnableRequestMessage0)valued).getPartitionId();
	    }
    
	    @Override
	    public void setValue(RunnableRequestMessage0 valued, Object value) {
		    valued.setPartitionId((java.lang.Object)value);
	    }
    
	    @Override
	    public void copy(RunnableRequestMessage0 source, RunnableRequestMessage0 dest) {
		    dest.setPartitionId(source.getPartitionId());
	    }
	    
	    @Override
	    public boolean areEqual(RunnableRequestMessage0 source, RunnableRequestMessage0 dest) {
	        return OH.eq(dest.getPartitionId(),source.getPartitionId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.Object.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.Object.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(RunnableRequestMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPartitionId());
	        
	    }
	    @Override
	    public void append(RunnableRequestMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPartitionId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Object partitionId";
	    }
	    @Override
	    public void clear(RunnableRequestMessage0 valued){
	       valued.setPartitionId(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_partitionId=new VALUED_PARAM_CLASS_partitionId();
  

  
    private static final class VALUED_PARAM_CLASS_priority extends AbstractValuedParam<RunnableRequestMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(RunnableRequestMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getPriority());
		    
	    }
	    
	    @Override
	    public void read(RunnableRequestMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 1;
	    }
    
	    @Override
	    public byte getPid() {
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "priority";
	    }
    
	    @Override
	    public Object getValue(RunnableRequestMessage0 valued) {
		    return (int)((RunnableRequestMessage0)valued).getPriority();
	    }
    
	    @Override
	    public void setValue(RunnableRequestMessage0 valued, Object value) {
		    valued.setPriority((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(RunnableRequestMessage0 source, RunnableRequestMessage0 dest) {
		    dest.setPriority(source.getPriority());
	    }
	    
	    @Override
	    public boolean areEqual(RunnableRequestMessage0 source, RunnableRequestMessage0 dest) {
	        return OH.eq(dest.getPriority(),source.getPriority());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(RunnableRequestMessage0 valued) {
		    return valued.getPriority();
	    }
    
	    @Override
	    public void setInt(RunnableRequestMessage0 valued, int value) {
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
	    public void append(RunnableRequestMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public void append(RunnableRequestMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int priority";
	    }
	    @Override
	    public void clear(RunnableRequestMessage0 valued){
	       valued.setPriority(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_priority=new VALUED_PARAM_CLASS_priority();
  

  
    private static final class VALUED_PARAM_CLASS_runnable extends AbstractValuedParam<RunnableRequestMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 60;
	    }
	    
	    @Override
	    public void write(RunnableRequestMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Runnable}");
		    
	    }
	    
	    @Override
	    public void read(RunnableRequestMessage0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Runnable}");
		    
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
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "runnable";
	    }
    
	    @Override
	    public Object getValue(RunnableRequestMessage0 valued) {
		    return (java.lang.Runnable)((RunnableRequestMessage0)valued).getRunnable();
	    }
    
	    @Override
	    public void setValue(RunnableRequestMessage0 valued, Object value) {
		    valued.setRunnable((java.lang.Runnable)value);
	    }
    
	    @Override
	    public void copy(RunnableRequestMessage0 source, RunnableRequestMessage0 dest) {
		    dest.setRunnable(source.getRunnable());
	    }
	    
	    @Override
	    public boolean areEqual(RunnableRequestMessage0 source, RunnableRequestMessage0 dest) {
	        return OH.eq(dest.getRunnable(),source.getRunnable());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.Runnable.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.Runnable.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(RunnableRequestMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getRunnable());
	        
	    }
	    @Override
	    public void append(RunnableRequestMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getRunnable());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Runnable runnable";
	    }
	    @Override
	    public void clear(RunnableRequestMessage0 valued){
	       valued.setRunnable(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_runnable=new VALUED_PARAM_CLASS_runnable();
  

  
    private static final class VALUED_PARAM_CLASS_timeoutMs extends AbstractValuedParam<RunnableRequestMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(RunnableRequestMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getTimeoutMs());
		    
	    }
	    
	    @Override
	    public void read(RunnableRequestMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setTimeoutMs(stream.readInt());
		    
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
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "timeoutMs";
	    }
    
	    @Override
	    public Object getValue(RunnableRequestMessage0 valued) {
		    return (int)((RunnableRequestMessage0)valued).getTimeoutMs();
	    }
    
	    @Override
	    public void setValue(RunnableRequestMessage0 valued, Object value) {
		    valued.setTimeoutMs((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(RunnableRequestMessage0 source, RunnableRequestMessage0 dest) {
		    dest.setTimeoutMs(source.getTimeoutMs());
	    }
	    
	    @Override
	    public boolean areEqual(RunnableRequestMessage0 source, RunnableRequestMessage0 dest) {
	        return OH.eq(dest.getTimeoutMs(),source.getTimeoutMs());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(RunnableRequestMessage0 valued) {
		    return valued.getTimeoutMs();
	    }
    
	    @Override
	    public void setInt(RunnableRequestMessage0 valued, int value) {
		    valued.setTimeoutMs(value);
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
	    public void append(RunnableRequestMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTimeoutMs());
	        
	    }
	    @Override
	    public void append(RunnableRequestMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTimeoutMs());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int timeoutMs";
	    }
	    @Override
	    public void clear(RunnableRequestMessage0 valued){
	       valued.setTimeoutMs(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_timeoutMs=new VALUED_PARAM_CLASS_timeoutMs();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_partitionId, VALUED_PARAM_priority, VALUED_PARAM_runnable, VALUED_PARAM_timeoutMs, };



    private static final byte PIDS[]={ 1 ,4,2,3};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 1: return VALUED_PARAM_partitionId;
             case 4: return VALUED_PARAM_priority;
             case 2: return VALUED_PARAM_runnable;
             case 3: return VALUED_PARAM_timeoutMs;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 1: return true;
             case 4: return true;
             case 2: return true;
             case 3: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 1: return "partitionId";
             case 4: return "priority";
             case 2: return "runnable";
             case 3: return "timeoutMs";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 1: return 0;
             case 4: return 1;
             case 2: return 2;
             case 3: return 3;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="partitionId") return 1;
             if(name=="priority") return 4;
             if(name=="runnable") return 2;
             if(name=="timeoutMs") return 3;
            
             if("partitionId".equals(name)) return 1;
             if("priority".equals(name)) return 4;
             if("runnable".equals(name)) return 2;
             if("timeoutMs".equals(name)) return 3;
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
             case 1: return  this._partitionId; 
             case 4: return  OH.valueOf(this._priority); 
             case 2: return  this._runnable; 
             case 3: return  OH.valueOf(this._timeoutMs); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 1: return java.lang.Object.class;
             case 4: return int.class;
             case 2: return java.lang.Runnable.class;
             case 3: return int.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 1: return 18;
             case 4: return 4;
             case 2: return 60;
             case 3: return 4;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 1: this._partitionId=(java.lang.Object)value;return;
             case 4: this._priority=(java.lang.Integer)value;return;
             case 2: this._runnable=(java.lang.Runnable)value;return;
             case 3: this._timeoutMs=(java.lang.Integer)value;return;
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
             case 1: this._partitionId=(java.lang.Object)value;return true;
             case 4: this._priority=(java.lang.Integer)value;return true;
             case 2: this._runnable=(java.lang.Runnable)value;return true;
             case 3: this._timeoutMs=(java.lang.Integer)value;return true;
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
             case 4: return this._priority;
             case 3: return this._timeoutMs;
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
             case 4: this._priority=value;return;
             case 3: this._timeoutMs=value;return;
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
        
            this._partitionId=(java.lang.Object)converter.read(session);
        
            break;

        case 2:
        
            this._runnable=(java.lang.Runnable)converter.read(session);
        
            break;

        case 3:
        
            if((basicType=in.readByte())!=4)
                break;
            this._timeoutMs=in.readInt();
        
            break;

        case 4:
        
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
        
if(this._partitionId!=null && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this._partitionId,session);
        
}

if(this._runnable!=null && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this._runnable,session);
        
}

if(this._timeoutMs!=0 && (0 & transience)==0){
    out.writeByte(3);
        
    out.writeByte(4);
    out.writeInt(this._timeoutMs);
        
}

if(this._priority!=0 && (0 & transience)==0){
    out.writeByte(4);
        
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