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

public abstract class AmiCenterQueryDsTrackerEvent0 implements com.f1.ami.amicommon.msg.AmiCenterQueryDsTrackerEvent ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private long _duration;

    private java.lang.Exception _exception;

    private java.lang.String _message;

    private boolean _ok;

    private int _priority;

    private double _progress;

    private java.lang.String _resultTableName;

    private com.f1.base.Table _resultTableSample;

    private int _resultTableSize;

    private java.lang.String _string;

    private java.lang.String _ticket;

    private long _timestamp;

    private java.util.List _trackedEvents;

    private byte _type;

    private static final String NAMES[]={ "duration" ,"exception","message","ok","priority","progress","resultTableName","resultTableSample","resultTableSize","string","ticket","timestamp","trackedEvents","type"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 35;
        try{
        switch(h){

                case 2:

                    if(name == "progress" || name.equals("progress")) {this._progress=(java.lang.Double)value;return;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return;}
break;
                case 7:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {this._trackedEvents=(java.util.List)value;return;}
break;
                case 8:

                    if(name == "message" || name.equals("message")) {this._message=(java.lang.String)value;return;}
break;
                case 9:

                    if(name == "exception" || name.equals("exception")) {this._exception=(java.lang.Exception)value;return;}
break;
                case 10:

                    if(name == "type" || name.equals("type")) {this._type=(java.lang.Byte)value;return;}
break;
                case 13:

                    if(name == "ok" || name.equals("ok")) {this._ok=(java.lang.Boolean)value;return;}
break;
                case 14:

                    if(name == "resultTableSample" || name.equals("resultTableSample")) {this._resultTableSample=(com.f1.base.Table)value;return;}
break;
                case 17:

                    if(name == "ticket" || name.equals("ticket")) {this._ticket=(java.lang.String)value;return;}
break;
                case 18:

                    if(name == "string" || name.equals("string")) {this._string=(java.lang.String)value;return;}
break;
                case 19:

                    if(name == "resultTableName" || name.equals("resultTableName")) {this._resultTableName=(java.lang.String)value;return;}
break;
                case 20:

                    if(name == "resultTableSize" || name.equals("resultTableSize")) {this._resultTableSize=(java.lang.Integer)value;return;}
break;
                case 31:

                    if(name == "duration" || name.equals("duration")) {this._duration=(java.lang.Long)value;return;}
break;
                case 34:

                    if(name == "timestamp" || name.equals("timestamp")) {this._timestamp=(java.lang.Long)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 35;
        switch(h){

                case 2:

                    if(name == "progress" || name.equals("progress")) {this._progress=(java.lang.Double)value;return true;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return true;}
break;
                case 7:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {this._trackedEvents=(java.util.List)value;return true;}
break;
                case 8:

                    if(name == "message" || name.equals("message")) {this._message=(java.lang.String)value;return true;}
break;
                case 9:

                    if(name == "exception" || name.equals("exception")) {this._exception=(java.lang.Exception)value;return true;}
break;
                case 10:

                    if(name == "type" || name.equals("type")) {this._type=(java.lang.Byte)value;return true;}
break;
                case 13:

                    if(name == "ok" || name.equals("ok")) {this._ok=(java.lang.Boolean)value;return true;}
break;
                case 14:

                    if(name == "resultTableSample" || name.equals("resultTableSample")) {this._resultTableSample=(com.f1.base.Table)value;return true;}
break;
                case 17:

                    if(name == "ticket" || name.equals("ticket")) {this._ticket=(java.lang.String)value;return true;}
break;
                case 18:

                    if(name == "string" || name.equals("string")) {this._string=(java.lang.String)value;return true;}
break;
                case 19:

                    if(name == "resultTableName" || name.equals("resultTableName")) {this._resultTableName=(java.lang.String)value;return true;}
break;
                case 20:

                    if(name == "resultTableSize" || name.equals("resultTableSize")) {this._resultTableSize=(java.lang.Integer)value;return true;}
break;
                case 31:

                    if(name == "duration" || name.equals("duration")) {this._duration=(java.lang.Long)value;return true;}
break;
                case 34:

                    if(name == "timestamp" || name.equals("timestamp")) {this._timestamp=(java.lang.Long)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 35;
        switch(h){

                case 2:

		    
                    if(name == "progress" || name.equals("progress")) {return OH.valueOf(this._progress);}
		    
break;
                case 4:

		    
                    if(name == "priority" || name.equals("priority")) {return OH.valueOf(this._priority);}
		    
break;
                case 7:

		    
                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return this._trackedEvents;}
            
break;
                case 8:

		    
                    if(name == "message" || name.equals("message")) {return this._message;}
            
break;
                case 9:

		    
                    if(name == "exception" || name.equals("exception")) {return this._exception;}
            
break;
                case 10:

		    
                    if(name == "type" || name.equals("type")) {return OH.valueOf(this._type);}
		    
break;
                case 13:

		    
                    if(name == "ok" || name.equals("ok")) {return OH.valueOf(this._ok);}
		    
break;
                case 14:

		    
                    if(name == "resultTableSample" || name.equals("resultTableSample")) {return this._resultTableSample;}
            
break;
                case 17:

		    
                    if(name == "ticket" || name.equals("ticket")) {return this._ticket;}
            
break;
                case 18:

		    
                    if(name == "string" || name.equals("string")) {return this._string;}
            
break;
                case 19:

		    
                    if(name == "resultTableName" || name.equals("resultTableName")) {return this._resultTableName;}
            
break;
                case 20:

		    
                    if(name == "resultTableSize" || name.equals("resultTableSize")) {return OH.valueOf(this._resultTableSize);}
		    
break;
                case 31:

		    
                    if(name == "duration" || name.equals("duration")) {return OH.valueOf(this._duration);}
		    
break;
                case 34:

		    
                    if(name == "timestamp" || name.equals("timestamp")) {return OH.valueOf(this._timestamp);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 35;
        switch(h){

                case 2:

                    if(name == "progress" || name.equals("progress")) {return double.class;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {return int.class;}
break;
                case 7:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return java.util.List.class;}
break;
                case 8:

                    if(name == "message" || name.equals("message")) {return java.lang.String.class;}
break;
                case 9:

                    if(name == "exception" || name.equals("exception")) {return java.lang.Exception.class;}
break;
                case 10:

                    if(name == "type" || name.equals("type")) {return byte.class;}
break;
                case 13:

                    if(name == "ok" || name.equals("ok")) {return boolean.class;}
break;
                case 14:

                    if(name == "resultTableSample" || name.equals("resultTableSample")) {return com.f1.base.Table.class;}
break;
                case 17:

                    if(name == "ticket" || name.equals("ticket")) {return java.lang.String.class;}
break;
                case 18:

                    if(name == "string" || name.equals("string")) {return java.lang.String.class;}
break;
                case 19:

                    if(name == "resultTableName" || name.equals("resultTableName")) {return java.lang.String.class;}
break;
                case 20:

                    if(name == "resultTableSize" || name.equals("resultTableSize")) {return int.class;}
break;
                case 31:

                    if(name == "duration" || name.equals("duration")) {return long.class;}
break;
                case 34:

                    if(name == "timestamp" || name.equals("timestamp")) {return long.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 35;
        switch(h){

                case 2:

                    if(name == "progress" || name.equals("progress")) {return VALUED_PARAM_progress;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {return VALUED_PARAM_priority;}
break;
                case 7:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return VALUED_PARAM_trackedEvents;}
break;
                case 8:

                    if(name == "message" || name.equals("message")) {return VALUED_PARAM_message;}
break;
                case 9:

                    if(name == "exception" || name.equals("exception")) {return VALUED_PARAM_exception;}
break;
                case 10:

                    if(name == "type" || name.equals("type")) {return VALUED_PARAM_type;}
break;
                case 13:

                    if(name == "ok" || name.equals("ok")) {return VALUED_PARAM_ok;}
break;
                case 14:

                    if(name == "resultTableSample" || name.equals("resultTableSample")) {return VALUED_PARAM_resultTableSample;}
break;
                case 17:

                    if(name == "ticket" || name.equals("ticket")) {return VALUED_PARAM_ticket;}
break;
                case 18:

                    if(name == "string" || name.equals("string")) {return VALUED_PARAM_string;}
break;
                case 19:

                    if(name == "resultTableName" || name.equals("resultTableName")) {return VALUED_PARAM_resultTableName;}
break;
                case 20:

                    if(name == "resultTableSize" || name.equals("resultTableSize")) {return VALUED_PARAM_resultTableSize;}
break;
                case 31:

                    if(name == "duration" || name.equals("duration")) {return VALUED_PARAM_duration;}
break;
                case 34:

                    if(name == "timestamp" || name.equals("timestamp")) {return VALUED_PARAM_timestamp;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 35;
        switch(h){

                case 2:

                    if(name == "progress" || name.equals("progress")) {return 5;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {return 4;}
break;
                case 7:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return 12;}
break;
                case 8:

                    if(name == "message" || name.equals("message")) {return 2;}
break;
                case 9:

                    if(name == "exception" || name.equals("exception")) {return 1;}
break;
                case 10:

                    if(name == "type" || name.equals("type")) {return 13;}
break;
                case 13:

                    if(name == "ok" || name.equals("ok")) {return 3;}
break;
                case 14:

                    if(name == "resultTableSample" || name.equals("resultTableSample")) {return 7;}
break;
                case 17:

                    if(name == "ticket" || name.equals("ticket")) {return 10;}
break;
                case 18:

                    if(name == "string" || name.equals("string")) {return 9;}
break;
                case 19:

                    if(name == "resultTableName" || name.equals("resultTableName")) {return 6;}
break;
                case 20:

                    if(name == "resultTableSize" || name.equals("resultTableSize")) {return 8;}
break;
                case 31:

                    if(name == "duration" || name.equals("duration")) {return 0;}
break;
                case 34:

                    if(name == "timestamp" || name.equals("timestamp")) {return 11;}
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
        return 14;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiCenterQueryDsTrackerEvent0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 35;
        switch(h){

                case 2:

                    if(name == "progress" || name.equals("progress")) {return true;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {return true;}
break;
                case 7:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return true;}
break;
                case 8:

                    if(name == "message" || name.equals("message")) {return true;}
break;
                case 9:

                    if(name == "exception" || name.equals("exception")) {return true;}
break;
                case 10:

                    if(name == "type" || name.equals("type")) {return true;}
break;
                case 13:

                    if(name == "ok" || name.equals("ok")) {return true;}
break;
                case 14:

                    if(name == "resultTableSample" || name.equals("resultTableSample")) {return true;}
break;
                case 17:

                    if(name == "ticket" || name.equals("ticket")) {return true;}
break;
                case 18:

                    if(name == "string" || name.equals("string")) {return true;}
break;
                case 19:

                    if(name == "resultTableName" || name.equals("resultTableName")) {return true;}
break;
                case 20:

                    if(name == "resultTableSize" || name.equals("resultTableSize")) {return true;}
break;
                case 31:

                    if(name == "duration" || name.equals("duration")) {return true;}
break;
                case 34:

                    if(name == "timestamp" || name.equals("timestamp")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 35;
        switch(h){

                case 2:

                    if(name == "progress" || name.equals("progress")) {return 7;}
break;
                case 4:

                    if(name == "priority" || name.equals("priority")) {return 4;}
break;
                case 7:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return 21;}
break;
                case 8:

                    if(name == "message" || name.equals("message")) {return 20;}
break;
                case 9:

                    if(name == "exception" || name.equals("exception")) {return 53;}
break;
                case 10:

                    if(name == "type" || name.equals("type")) {return 1;}
break;
                case 13:

                    if(name == "ok" || name.equals("ok")) {return 0;}
break;
                case 14:

                    if(name == "resultTableSample" || name.equals("resultTableSample")) {return 43;}
break;
                case 17:

                    if(name == "ticket" || name.equals("ticket")) {return 20;}
break;
                case 18:

                    if(name == "string" || name.equals("string")) {return 20;}
break;
                case 19:

                    if(name == "resultTableName" || name.equals("resultTableName")) {return 20;}
break;
                case 20:

                    if(name == "resultTableSize" || name.equals("resultTableSize")) {return 4;}
break;
                case 31:

                    if(name == "duration" || name.equals("duration")) {return 6;}
break;
                case 34:

                    if(name == "timestamp" || name.equals("timestamp")) {return 6;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _duration;

        case 1:return _exception;

        case 2:return _message;

        case 3:return _ok;

        case 4:return _priority;

        case 5:return _progress;

        case 6:return _resultTableName;

        case 7:return _resultTableSample;

        case 8:return _resultTableSize;

        case 9:return _string;

        case 10:return _ticket;

        case 11:return _timestamp;

        case 12:return _trackedEvents;

        case 13:return _type;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 14);
    }

    public long getDuration(){
        return this._duration;
    }
    public void setDuration(long _duration){
    
        this._duration=_duration;
    }

    public java.lang.Exception getException(){
        return this._exception;
    }
    public void setException(java.lang.Exception _exception){
    
        this._exception=_exception;
    }

    public java.lang.String getMessage(){
        return this._message;
    }
    public void setMessage(java.lang.String _message){
    
        this._message=_message;
    }

    public boolean getOk(){
        return this._ok;
    }
    public void setOk(boolean _ok){
    
        this._ok=_ok;
    }

    public int getPriority(){
        return this._priority;
    }
    public void setPriority(int _priority){
    
        this._priority=_priority;
    }

    public double getProgress(){
        return this._progress;
    }
    public void setProgress(double _progress){
    
        this._progress=_progress;
    }

    public java.lang.String getResultTableName(){
        return this._resultTableName;
    }
    public void setResultTableName(java.lang.String _resultTableName){
    
        this._resultTableName=_resultTableName;
    }

    public com.f1.base.Table getResultTableSample(){
        return this._resultTableSample;
    }
    public void setResultTableSample(com.f1.base.Table _resultTableSample){
    
        this._resultTableSample=_resultTableSample;
    }

    public int getResultTableSize(){
        return this._resultTableSize;
    }
    public void setResultTableSize(int _resultTableSize){
    
        this._resultTableSize=_resultTableSize;
    }

    public java.lang.String getString(){
        return this._string;
    }
    public void setString(java.lang.String _string){
    
        this._string=_string;
    }

    public java.lang.String getTicket(){
        return this._ticket;
    }
    public void setTicket(java.lang.String _ticket){
    
        this._ticket=_ticket;
    }

    public long getTimestamp(){
        return this._timestamp;
    }
    public void setTimestamp(long _timestamp){
    
        this._timestamp=_timestamp;
    }

    public java.util.List getTrackedEvents(){
        return this._trackedEvents;
    }
    public void setTrackedEvents(java.util.List _trackedEvents){
    
        this._trackedEvents=_trackedEvents;
    }

    public byte getType(){
        return this._type;
    }
    public void setType(byte _type){
    
        this._type=_type;
    }





  
    private static final class VALUED_PARAM_CLASS_duration extends AbstractValuedParam<AmiCenterQueryDsTrackerEvent0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsTrackerEvent0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getDuration());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsTrackerEvent0 valued, DataInput stream) throws IOException{
		    
		      valued.setDuration(stream.readLong());
		    
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
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "duration";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsTrackerEvent0 valued) {
		    return (long)((AmiCenterQueryDsTrackerEvent0)valued).getDuration();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsTrackerEvent0 valued, Object value) {
		    valued.setDuration((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
		    dest.setDuration(source.getDuration());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
	        return OH.eq(dest.getDuration(),source.getDuration());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterQueryDsTrackerEvent0 valued) {
		    return valued.getDuration();
	    }
    
	    @Override
	    public void setLong(AmiCenterQueryDsTrackerEvent0 valued, long value) {
		    valued.setDuration(value);
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
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuilder sb){
	        
	        sb.append(valued.getDuration());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuildable sb){
	        
	        sb.append(valued.getDuration());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long duration";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsTrackerEvent0 valued){
	       valued.setDuration(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_duration=new VALUED_PARAM_CLASS_duration();
  

  
    private static final class VALUED_PARAM_CLASS_exception extends AbstractValuedParam<AmiCenterQueryDsTrackerEvent0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 53;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsTrackerEvent0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Exception}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsTrackerEvent0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Exception}");
		    
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
            return 44;
	    }
    
	    @Override
	    public String getName() {
            return "exception";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsTrackerEvent0 valued) {
		    return (java.lang.Exception)((AmiCenterQueryDsTrackerEvent0)valued).getException();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsTrackerEvent0 valued, Object value) {
		    valued.setException((java.lang.Exception)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
		    dest.setException(source.getException());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
	        return OH.eq(dest.getException(),source.getException());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.Exception.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.Exception.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuilder sb){
	        
	        sb.append(valued.getException());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuildable sb){
	        
	        sb.append(valued.getException());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Exception exception";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsTrackerEvent0 valued){
	       valued.setException(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_exception=new VALUED_PARAM_CLASS_exception();
  

  
    private static final class VALUED_PARAM_CLASS_message extends AbstractValuedParam<AmiCenterQueryDsTrackerEvent0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsTrackerEvent0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsTrackerEvent0 valued, DataInput stream) throws IOException{
		    
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
            return 43;
	    }
    
	    @Override
	    public String getName() {
            return "message";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsTrackerEvent0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsTrackerEvent0)valued).getMessage();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsTrackerEvent0 valued, Object value) {
		    valued.setMessage((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
		    dest.setMessage(source.getMessage());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
	        return OH.eq(dest.getMessage(),source.getMessage());
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
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getMessage(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getMessage(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String message";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsTrackerEvent0 valued){
	       valued.setMessage(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_message=new VALUED_PARAM_CLASS_message();
  

  
    private static final class VALUED_PARAM_CLASS_ok extends AbstractValuedParam<AmiCenterQueryDsTrackerEvent0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsTrackerEvent0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getOk());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsTrackerEvent0 valued, DataInput stream) throws IOException{
		    
		      valued.setOk(stream.readBoolean());
		    
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
            return 42;
	    }
    
	    @Override
	    public String getName() {
            return "ok";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsTrackerEvent0 valued) {
		    return (boolean)((AmiCenterQueryDsTrackerEvent0)valued).getOk();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsTrackerEvent0 valued, Object value) {
		    valued.setOk((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
		    dest.setOk(source.getOk());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
	        return OH.eq(dest.getOk(),source.getOk());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterQueryDsTrackerEvent0 valued) {
		    return valued.getOk();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterQueryDsTrackerEvent0 valued, boolean value) {
		    valued.setOk(value);
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
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuilder sb){
	        
	        sb.append(valued.getOk());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuildable sb){
	        
	        sb.append(valued.getOk());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean ok";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsTrackerEvent0 valued){
	       valued.setOk(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_ok=new VALUED_PARAM_CLASS_ok();
  

  
    private static final class VALUED_PARAM_CLASS_priority extends AbstractValuedParam<AmiCenterQueryDsTrackerEvent0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsTrackerEvent0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getPriority());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsTrackerEvent0 valued, DataInput stream) throws IOException{
		    
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
		    return 4;
	    }
    
	    @Override
	    public byte getPid() {
            return 57;
	    }
    
	    @Override
	    public String getName() {
            return "priority";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsTrackerEvent0 valued) {
		    return (int)((AmiCenterQueryDsTrackerEvent0)valued).getPriority();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsTrackerEvent0 valued, Object value) {
		    valued.setPriority((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
		    dest.setPriority(source.getPriority());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
	        return OH.eq(dest.getPriority(),source.getPriority());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterQueryDsTrackerEvent0 valued) {
		    return valued.getPriority();
	    }
    
	    @Override
	    public void setInt(AmiCenterQueryDsTrackerEvent0 valued, int value) {
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
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int priority";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsTrackerEvent0 valued){
	       valued.setPriority(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_priority=new VALUED_PARAM_CLASS_priority();
  

  
    private static final class VALUED_PARAM_CLASS_progress extends AbstractValuedParam<AmiCenterQueryDsTrackerEvent0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 7;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsTrackerEvent0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeDouble(valued.getProgress());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsTrackerEvent0 valued, DataInput stream) throws IOException{
		    
		      valued.setProgress(stream.readDouble());
		    
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
            return "progress";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsTrackerEvent0 valued) {
		    return (double)((AmiCenterQueryDsTrackerEvent0)valued).getProgress();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsTrackerEvent0 valued, Object value) {
		    valued.setProgress((java.lang.Double)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
		    dest.setProgress(source.getProgress());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
	        return OH.eq(dest.getProgress(),source.getProgress());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public double getDouble(AmiCenterQueryDsTrackerEvent0 valued) {
		    return valued.getProgress();
	    }
    
	    @Override
	    public void setDouble(AmiCenterQueryDsTrackerEvent0 valued, double value) {
		    valued.setProgress(value);
	    }
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return double.class;
	    }
	    private static final Caster CASTER=OH.getCaster(double.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuilder sb){
	        
	        sb.append(valued.getProgress());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuildable sb){
	        
	        sb.append(valued.getProgress());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:double progress";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsTrackerEvent0 valued){
	       valued.setProgress(0D);
	    }
	};
    private static final ValuedParam VALUED_PARAM_progress=new VALUED_PARAM_CLASS_progress();
  

  
    private static final class VALUED_PARAM_CLASS_resultTableName extends AbstractValuedParam<AmiCenterQueryDsTrackerEvent0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsTrackerEvent0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsTrackerEvent0 valued, DataInput stream) throws IOException{
		    
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
            return 10;
	    }
    
	    @Override
	    public String getName() {
            return "resultTableName";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsTrackerEvent0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsTrackerEvent0)valued).getResultTableName();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsTrackerEvent0 valued, Object value) {
		    valued.setResultTableName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
		    dest.setResultTableName(source.getResultTableName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
	        return OH.eq(dest.getResultTableName(),source.getResultTableName());
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
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getResultTableName(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getResultTableName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String resultTableName";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsTrackerEvent0 valued){
	       valued.setResultTableName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_resultTableName=new VALUED_PARAM_CLASS_resultTableName();
  

  
    private static final class VALUED_PARAM_CLASS_resultTableSample extends AbstractValuedParam<AmiCenterQueryDsTrackerEvent0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 43;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsTrackerEvent0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.base.Table}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsTrackerEvent0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.base.Table}");
		    
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
            return 9;
	    }
    
	    @Override
	    public String getName() {
            return "resultTableSample";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsTrackerEvent0 valued) {
		    return (com.f1.base.Table)((AmiCenterQueryDsTrackerEvent0)valued).getResultTableSample();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsTrackerEvent0 valued, Object value) {
		    valued.setResultTableSample((com.f1.base.Table)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
		    dest.setResultTableSample(source.getResultTableSample());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
	        return OH.eq(dest.getResultTableSample(),source.getResultTableSample());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.base.Table.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.base.Table.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuilder sb){
	        
	        sb.append(valued.getResultTableSample());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuildable sb){
	        
	        sb.append(valued.getResultTableSample());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.base.Table resultTableSample";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsTrackerEvent0 valued){
	       valued.setResultTableSample(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_resultTableSample=new VALUED_PARAM_CLASS_resultTableSample();
  

  
    private static final class VALUED_PARAM_CLASS_resultTableSize extends AbstractValuedParam<AmiCenterQueryDsTrackerEvent0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsTrackerEvent0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getResultTableSize());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsTrackerEvent0 valued, DataInput stream) throws IOException{
		    
		      valued.setResultTableSize(stream.readInt());
		    
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
		    return 8;
	    }
    
	    @Override
	    public byte getPid() {
            return 8;
	    }
    
	    @Override
	    public String getName() {
            return "resultTableSize";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsTrackerEvent0 valued) {
		    return (int)((AmiCenterQueryDsTrackerEvent0)valued).getResultTableSize();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsTrackerEvent0 valued, Object value) {
		    valued.setResultTableSize((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
		    dest.setResultTableSize(source.getResultTableSize());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
	        return OH.eq(dest.getResultTableSize(),source.getResultTableSize());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterQueryDsTrackerEvent0 valued) {
		    return valued.getResultTableSize();
	    }
    
	    @Override
	    public void setInt(AmiCenterQueryDsTrackerEvent0 valued, int value) {
		    valued.setResultTableSize(value);
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
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuilder sb){
	        
	        sb.append(valued.getResultTableSize());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuildable sb){
	        
	        sb.append(valued.getResultTableSize());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int resultTableSize";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsTrackerEvent0 valued){
	       valued.setResultTableSize(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_resultTableSize=new VALUED_PARAM_CLASS_resultTableSize();
  

  
    private static final class VALUED_PARAM_CLASS_string extends AbstractValuedParam<AmiCenterQueryDsTrackerEvent0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsTrackerEvent0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsTrackerEvent0 valued, DataInput stream) throws IOException{
		    
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
		    return 9;
	    }
    
	    @Override
	    public byte getPid() {
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "string";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsTrackerEvent0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsTrackerEvent0)valued).getString();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsTrackerEvent0 valued, Object value) {
		    valued.setString((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
		    dest.setString(source.getString());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
	        return OH.eq(dest.getString(),source.getString());
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
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getString(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getString(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String string";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsTrackerEvent0 valued){
	       valued.setString(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_string=new VALUED_PARAM_CLASS_string();
  

  
    private static final class VALUED_PARAM_CLASS_ticket extends AbstractValuedParam<AmiCenterQueryDsTrackerEvent0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsTrackerEvent0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsTrackerEvent0 valued, DataInput stream) throws IOException{
		    
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
            return 55;
	    }
    
	    @Override
	    public String getName() {
            return "ticket";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsTrackerEvent0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsTrackerEvent0)valued).getTicket();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsTrackerEvent0 valued, Object value) {
		    valued.setTicket((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
		    dest.setTicket(source.getTicket());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
	        return OH.eq(dest.getTicket(),source.getTicket());
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
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getTicket(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getTicket(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String ticket";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsTrackerEvent0 valued){
	       valued.setTicket(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_ticket=new VALUED_PARAM_CLASS_ticket();
  

  
    private static final class VALUED_PARAM_CLASS_timestamp extends AbstractValuedParam<AmiCenterQueryDsTrackerEvent0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsTrackerEvent0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getTimestamp());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsTrackerEvent0 valued, DataInput stream) throws IOException{
		    
		      valued.setTimestamp(stream.readLong());
		    
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
		    return 11;
	    }
    
	    @Override
	    public byte getPid() {
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "timestamp";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsTrackerEvent0 valued) {
		    return (long)((AmiCenterQueryDsTrackerEvent0)valued).getTimestamp();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsTrackerEvent0 valued, Object value) {
		    valued.setTimestamp((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
		    dest.setTimestamp(source.getTimestamp());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
	        return OH.eq(dest.getTimestamp(),source.getTimestamp());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterQueryDsTrackerEvent0 valued) {
		    return valued.getTimestamp();
	    }
    
	    @Override
	    public void setLong(AmiCenterQueryDsTrackerEvent0 valued, long value) {
		    valued.setTimestamp(value);
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
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTimestamp());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTimestamp());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long timestamp";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsTrackerEvent0 valued){
	       valued.setTimestamp(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_timestamp=new VALUED_PARAM_CLASS_timestamp();
  

  
    private static final class VALUED_PARAM_CLASS_trackedEvents extends AbstractValuedParam<AmiCenterQueryDsTrackerEvent0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsTrackerEvent0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsTrackerEvent0 valued, DataInput stream) throws IOException{
		    
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
		    return 12;
	    }
    
	    @Override
	    public byte getPid() {
            return 58;
	    }
    
	    @Override
	    public String getName() {
            return "trackedEvents";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsTrackerEvent0 valued) {
		    return (java.util.List)((AmiCenterQueryDsTrackerEvent0)valued).getTrackedEvents();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsTrackerEvent0 valued, Object value) {
		    valued.setTrackedEvents((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
		    dest.setTrackedEvents(source.getTrackedEvents());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
	        return OH.eq(dest.getTrackedEvents(),source.getTrackedEvents());
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
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTrackedEvents());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTrackedEvents());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List trackedEvents";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsTrackerEvent0 valued){
	       valued.setTrackedEvents(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_trackedEvents=new VALUED_PARAM_CLASS_trackedEvents();
  

  
    private static final class VALUED_PARAM_CLASS_type extends AbstractValuedParam<AmiCenterQueryDsTrackerEvent0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsTrackerEvent0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getType());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsTrackerEvent0 valued, DataInput stream) throws IOException{
		    
		      valued.setType(stream.readByte());
		    
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
		    return 13;
	    }
    
	    @Override
	    public byte getPid() {
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "type";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsTrackerEvent0 valued) {
		    return (byte)((AmiCenterQueryDsTrackerEvent0)valued).getType();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsTrackerEvent0 valued, Object value) {
		    valued.setType((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
		    dest.setType(source.getType());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsTrackerEvent0 source, AmiCenterQueryDsTrackerEvent0 dest) {
	        return OH.eq(dest.getType(),source.getType());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiCenterQueryDsTrackerEvent0 valued) {
		    return valued.getType();
	    }
    
	    @Override
	    public void setByte(AmiCenterQueryDsTrackerEvent0 valued, byte value) {
		    valued.setType(value);
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
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuilder sb){
	        
	        sb.append(valued.getType());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsTrackerEvent0 valued, StringBuildable sb){
	        
	        sb.append(valued.getType());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte type";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsTrackerEvent0 valued){
	       valued.setType((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_type=new VALUED_PARAM_CLASS_type();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_duration, VALUED_PARAM_exception, VALUED_PARAM_message, VALUED_PARAM_ok, VALUED_PARAM_priority, VALUED_PARAM_progress, VALUED_PARAM_resultTableName, VALUED_PARAM_resultTableSample, VALUED_PARAM_resultTableSize, VALUED_PARAM_string, VALUED_PARAM_ticket, VALUED_PARAM_timestamp, VALUED_PARAM_trackedEvents, VALUED_PARAM_type, };



    private static final byte PIDS[]={ 7 ,44,43,42,57,53,10,9,8,4,55,3,58,5};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 7: return VALUED_PARAM_duration;
             case 44: return VALUED_PARAM_exception;
             case 43: return VALUED_PARAM_message;
             case 42: return VALUED_PARAM_ok;
             case 57: return VALUED_PARAM_priority;
             case 53: return VALUED_PARAM_progress;
             case 10: return VALUED_PARAM_resultTableName;
             case 9: return VALUED_PARAM_resultTableSample;
             case 8: return VALUED_PARAM_resultTableSize;
             case 4: return VALUED_PARAM_string;
             case 55: return VALUED_PARAM_ticket;
             case 3: return VALUED_PARAM_timestamp;
             case 58: return VALUED_PARAM_trackedEvents;
             case 5: return VALUED_PARAM_type;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 7: return true;
             case 44: return true;
             case 43: return true;
             case 42: return true;
             case 57: return true;
             case 53: return true;
             case 10: return true;
             case 9: return true;
             case 8: return true;
             case 4: return true;
             case 55: return true;
             case 3: return true;
             case 58: return true;
             case 5: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 7: return "duration";
             case 44: return "exception";
             case 43: return "message";
             case 42: return "ok";
             case 57: return "priority";
             case 53: return "progress";
             case 10: return "resultTableName";
             case 9: return "resultTableSample";
             case 8: return "resultTableSize";
             case 4: return "string";
             case 55: return "ticket";
             case 3: return "timestamp";
             case 58: return "trackedEvents";
             case 5: return "type";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 7: return 0;
             case 44: return 1;
             case 43: return 2;
             case 42: return 3;
             case 57: return 4;
             case 53: return 5;
             case 10: return 6;
             case 9: return 7;
             case 8: return 8;
             case 4: return 9;
             case 55: return 10;
             case 3: return 11;
             case 58: return 12;
             case 5: return 13;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="duration") return 7;
             if(name=="exception") return 44;
             if(name=="message") return 43;
             if(name=="ok") return 42;
             if(name=="priority") return 57;
             if(name=="progress") return 53;
             if(name=="resultTableName") return 10;
             if(name=="resultTableSample") return 9;
             if(name=="resultTableSize") return 8;
             if(name=="string") return 4;
             if(name=="ticket") return 55;
             if(name=="timestamp") return 3;
             if(name=="trackedEvents") return 58;
             if(name=="type") return 5;
            
             if("duration".equals(name)) return 7;
             if("exception".equals(name)) return 44;
             if("message".equals(name)) return 43;
             if("ok".equals(name)) return 42;
             if("priority".equals(name)) return 57;
             if("progress".equals(name)) return 53;
             if("resultTableName".equals(name)) return 10;
             if("resultTableSample".equals(name)) return 9;
             if("resultTableSize".equals(name)) return 8;
             if("string".equals(name)) return 4;
             if("ticket".equals(name)) return 55;
             if("timestamp".equals(name)) return 3;
             if("trackedEvents".equals(name)) return 58;
             if("type".equals(name)) return 5;
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
             case 7: return  OH.valueOf(this._duration); 
             case 44: return  this._exception; 
             case 43: return  this._message; 
             case 42: return  OH.valueOf(this._ok); 
             case 57: return  OH.valueOf(this._priority); 
             case 53: return  OH.valueOf(this._progress); 
             case 10: return  this._resultTableName; 
             case 9: return  this._resultTableSample; 
             case 8: return  OH.valueOf(this._resultTableSize); 
             case 4: return  this._string; 
             case 55: return  this._ticket; 
             case 3: return  OH.valueOf(this._timestamp); 
             case 58: return  this._trackedEvents; 
             case 5: return  OH.valueOf(this._type); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 7: return long.class;
             case 44: return java.lang.Exception.class;
             case 43: return java.lang.String.class;
             case 42: return boolean.class;
             case 57: return int.class;
             case 53: return double.class;
             case 10: return java.lang.String.class;
             case 9: return com.f1.base.Table.class;
             case 8: return int.class;
             case 4: return java.lang.String.class;
             case 55: return java.lang.String.class;
             case 3: return long.class;
             case 58: return java.util.List.class;
             case 5: return byte.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 7: return 6;
             case 44: return 53;
             case 43: return 20;
             case 42: return 0;
             case 57: return 4;
             case 53: return 7;
             case 10: return 20;
             case 9: return 43;
             case 8: return 4;
             case 4: return 20;
             case 55: return 20;
             case 3: return 6;
             case 58: return 21;
             case 5: return 1;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 7: this._duration=(java.lang.Long)value;return;
             case 44: this._exception=(java.lang.Exception)value;return;
             case 43: this._message=(java.lang.String)value;return;
             case 42: this._ok=(java.lang.Boolean)value;return;
             case 57: this._priority=(java.lang.Integer)value;return;
             case 53: this._progress=(java.lang.Double)value;return;
             case 10: this._resultTableName=(java.lang.String)value;return;
             case 9: this._resultTableSample=(com.f1.base.Table)value;return;
             case 8: this._resultTableSize=(java.lang.Integer)value;return;
             case 4: this._string=(java.lang.String)value;return;
             case 55: this._ticket=(java.lang.String)value;return;
             case 3: this._timestamp=(java.lang.Long)value;return;
             case 58: this._trackedEvents=(java.util.List)value;return;
             case 5: this._type=(java.lang.Byte)value;return;
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
             case 7: this._duration=(java.lang.Long)value;return true;
             case 44: this._exception=(java.lang.Exception)value;return true;
             case 43: this._message=(java.lang.String)value;return true;
             case 42: this._ok=(java.lang.Boolean)value;return true;
             case 57: this._priority=(java.lang.Integer)value;return true;
             case 53: this._progress=(java.lang.Double)value;return true;
             case 10: this._resultTableName=(java.lang.String)value;return true;
             case 9: this._resultTableSample=(com.f1.base.Table)value;return true;
             case 8: this._resultTableSize=(java.lang.Integer)value;return true;
             case 4: this._string=(java.lang.String)value;return true;
             case 55: this._ticket=(java.lang.String)value;return true;
             case 3: this._timestamp=(java.lang.Long)value;return true;
             case 58: this._trackedEvents=(java.util.List)value;return true;
             case 5: this._type=(java.lang.Byte)value;return true;
            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
             case 42: return this._ok;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askByte(byte pid){
        switch(pid){
             case 5: return this._type;
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
             case 57: return this._priority;
             case 8: return this._resultTableSize;
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
             case 7: return this._duration;
             case 3: return this._timestamp;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for long value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public double askDouble(byte pid){
        switch(pid){
             case 53: return this._progress;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for double value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putBoolean(byte pid,boolean value){
    
        switch(pid){
             case 42: this._ok=value;return;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putByte(byte pid,byte value){
    
        switch(pid){
             case 5: this._type=value;return;
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
             case 57: this._priority=value;return;
             case 8: this._resultTableSize=value;return;
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
             case 7: this._duration=value;return;
             case 3: this._timestamp=value;return;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for long value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putDouble(byte pid,double value){
    
        switch(pid){
             case 53: this._progress=value;return;
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
        
        case 3:
        
            if((basicType=in.readByte())!=6)
                break;
            this._timestamp=in.readLong();
        
            break;

        case 4:
        
            this._string=(java.lang.String)converter.read(session);
        
            break;

        case 5:
        
            if((basicType=in.readByte())!=1)
                break;
            this._type=in.readByte();
        
            break;

        case 7:
        
            if((basicType=in.readByte())!=6)
                break;
            this._duration=in.readLong();
        
            break;

        case 8:
        
            if((basicType=in.readByte())!=4)
                break;
            this._resultTableSize=in.readInt();
        
            break;

        case 9:
        
            this._resultTableSample=(com.f1.base.Table)converter.read(session);
        
            break;

        case 10:
        
            this._resultTableName=(java.lang.String)converter.read(session);
        
            break;

        case 42:
        
            if((basicType=in.readByte())!=0)
                break;
            this._ok=in.readBoolean();
        
            break;

        case 43:
        
            this._message=(java.lang.String)converter.read(session);
        
            break;

        case 44:
        
            this._exception=(java.lang.Exception)converter.read(session);
        
            break;

        case 53:
        
            if((basicType=in.readByte())!=7)
                break;
            this._progress=in.readDouble();
        
            break;

        case 55:
        
            this._ticket=(java.lang.String)converter.read(session);
        
            break;

        case 57:
        
            if((basicType=in.readByte())!=4)
                break;
            this._priority=in.readInt();
        
            break;

        case 58:
        
            this._trackedEvents=(java.util.List)converter.read(session);
        
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
        
if(this._timestamp!=0L && (0 & transience)==0){
    out.writeByte(3);
        
    out.writeByte(6);
    out.writeLong(this._timestamp);
        
}

if(this._string!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._string,session);
        
}

if(this._type!=(byte)0 && (0 & transience)==0){
    out.writeByte(5);
        
    out.writeByte(1);
    out.writeByte(this._type);
        
}

if(this._duration!=0L && (0 & transience)==0){
    out.writeByte(7);
        
    out.writeByte(6);
    out.writeLong(this._duration);
        
}

if(this._resultTableSize!=0 && (0 & transience)==0){
    out.writeByte(8);
        
    out.writeByte(4);
    out.writeInt(this._resultTableSize);
        
}

if(this._resultTableSample!=null && (0 & transience)==0){
    out.writeByte(9);
        
    converter.write(this._resultTableSample,session);
        
}

if(this._resultTableName!=null && (0 & transience)==0){
    out.writeByte(10);
        
    converter.write(this._resultTableName,session);
        
}

if(this._ok!=false && (0 & transience)==0){
    out.writeByte(42);
        
    out.writeByte(0);
    out.writeBoolean(this._ok);
        
}

if(this._message!=null && (0 & transience)==0){
    out.writeByte(43);
        
    converter.write(this._message,session);
        
}

if(this._exception!=null && (0 & transience)==0){
    out.writeByte(44);
        
    converter.write(this._exception,session);
        
}

if(this._progress!=0D && (0 & transience)==0){
    out.writeByte(53);
        
    out.writeByte(7);
    out.writeDouble(this._progress);
        
}

if(this._ticket!=null && (0 & transience)==0){
    out.writeByte(55);
        
    converter.write(this._ticket,session);
        
}

if(this._priority!=0 && (0 & transience)==0){
    out.writeByte(57);
        
    out.writeByte(4);
    out.writeInt(this._priority);
        
}

if(this._trackedEvents!=null && (0 & transience)==0){
    out.writeByte(58);
        
    converter.write(this._trackedEvents,session);
        
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