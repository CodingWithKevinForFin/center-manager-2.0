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

public abstract class AmiCenterQueryDsResponse0 implements com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private boolean _disableLogging;

    private long _durrationNanos;

    private java.lang.Exception _exception;

    private java.util.List _generatedKeys;

    private java.lang.String _message;

    private boolean _ok;

    private java.util.List _previewTables;

    private int _priority;

    private double _progress;

    private long _querySessionId;

    private java.lang.Class _returnType;

    private java.lang.Object _returnValue;

    private int _returnValueTablePos;

    private long _rowsEffected;

    private java.util.List _tables;

    private java.lang.String _ticket;

    private java.util.List _trackedEvents;

    private static final String NAMES[]={ "disableLogging" ,"durrationNanos","exception","generatedKeys","message","ok","previewTables","priority","progress","querySessionId","returnType","returnValue","returnValueTablePos","rowsEffected","tables","ticket","trackedEvents"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 58;
        try{
        switch(h){

                case 1:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {this._trackedEvents=(java.util.List)value;return;}
break;
                case 5:

                    if(name == "tables" || name.equals("tables")) {this._tables=(java.util.List)value;return;}
break;
                case 10:

                    if(name == "ok" || name.equals("ok")) {this._ok=(java.lang.Boolean)value;return;}
break;
                case 11:

                    if(name == "querySessionId" || name.equals("querySessionId")) {this._querySessionId=(java.lang.Long)value;return;}
break;
                case 13:

                    if(name == "message" || name.equals("message")) {this._message=(java.lang.String)value;return;}
break;
                case 19:

                    if(name == "exception" || name.equals("exception")) {this._exception=(java.lang.Exception)value;return;}
break;
                case 23:

                    if(name == "rowsEffected" || name.equals("rowsEffected")) {this._rowsEffected=(java.lang.Long)value;return;}
break;
                case 25:

                    if(name == "progress" || name.equals("progress")) {this._progress=(java.lang.Double)value;return;}
break;
                case 29:

                    if(name == "returnValueTablePos" || name.equals("returnValueTablePos")) {this._returnValueTablePos=(java.lang.Integer)value;return;}
break;
                case 31:

                    if(name == "previewTables" || name.equals("previewTables")) {this._previewTables=(java.util.List)value;return;}
break;
                case 36:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return;}
break;
                case 37:

                    if(name == "durrationNanos" || name.equals("durrationNanos")) {this._durrationNanos=(java.lang.Long)value;return;}
break;
                case 39:

                    if(name == "disableLogging" || name.equals("disableLogging")) {this._disableLogging=(java.lang.Boolean)value;return;}
break;
                case 43:

                    if(name == "generatedKeys" || name.equals("generatedKeys")) {this._generatedKeys=(java.util.List)value;return;}
break;
                case 44:

                    if(name == "returnType" || name.equals("returnType")) {this._returnType=(java.lang.Class)value;return;}
break;
                case 46:

                    if(name == "ticket" || name.equals("ticket")) {this._ticket=(java.lang.String)value;return;}
break;
                case 57:

                    if(name == "returnValue" || name.equals("returnValue")) {this._returnValue=(java.lang.Object)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 58;
        switch(h){

                case 1:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {this._trackedEvents=(java.util.List)value;return true;}
break;
                case 5:

                    if(name == "tables" || name.equals("tables")) {this._tables=(java.util.List)value;return true;}
break;
                case 10:

                    if(name == "ok" || name.equals("ok")) {this._ok=(java.lang.Boolean)value;return true;}
break;
                case 11:

                    if(name == "querySessionId" || name.equals("querySessionId")) {this._querySessionId=(java.lang.Long)value;return true;}
break;
                case 13:

                    if(name == "message" || name.equals("message")) {this._message=(java.lang.String)value;return true;}
break;
                case 19:

                    if(name == "exception" || name.equals("exception")) {this._exception=(java.lang.Exception)value;return true;}
break;
                case 23:

                    if(name == "rowsEffected" || name.equals("rowsEffected")) {this._rowsEffected=(java.lang.Long)value;return true;}
break;
                case 25:

                    if(name == "progress" || name.equals("progress")) {this._progress=(java.lang.Double)value;return true;}
break;
                case 29:

                    if(name == "returnValueTablePos" || name.equals("returnValueTablePos")) {this._returnValueTablePos=(java.lang.Integer)value;return true;}
break;
                case 31:

                    if(name == "previewTables" || name.equals("previewTables")) {this._previewTables=(java.util.List)value;return true;}
break;
                case 36:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return true;}
break;
                case 37:

                    if(name == "durrationNanos" || name.equals("durrationNanos")) {this._durrationNanos=(java.lang.Long)value;return true;}
break;
                case 39:

                    if(name == "disableLogging" || name.equals("disableLogging")) {this._disableLogging=(java.lang.Boolean)value;return true;}
break;
                case 43:

                    if(name == "generatedKeys" || name.equals("generatedKeys")) {this._generatedKeys=(java.util.List)value;return true;}
break;
                case 44:

                    if(name == "returnType" || name.equals("returnType")) {this._returnType=(java.lang.Class)value;return true;}
break;
                case 46:

                    if(name == "ticket" || name.equals("ticket")) {this._ticket=(java.lang.String)value;return true;}
break;
                case 57:

                    if(name == "returnValue" || name.equals("returnValue")) {this._returnValue=(java.lang.Object)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 58;
        switch(h){

                case 1:

		    
                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return this._trackedEvents;}
            
break;
                case 5:

		    
                    if(name == "tables" || name.equals("tables")) {return this._tables;}
            
break;
                case 10:

		    
                    if(name == "ok" || name.equals("ok")) {return OH.valueOf(this._ok);}
		    
break;
                case 11:

		    
                    if(name == "querySessionId" || name.equals("querySessionId")) {return OH.valueOf(this._querySessionId);}
		    
break;
                case 13:

		    
                    if(name == "message" || name.equals("message")) {return this._message;}
            
break;
                case 19:

		    
                    if(name == "exception" || name.equals("exception")) {return this._exception;}
            
break;
                case 23:

		    
                    if(name == "rowsEffected" || name.equals("rowsEffected")) {return OH.valueOf(this._rowsEffected);}
		    
break;
                case 25:

		    
                    if(name == "progress" || name.equals("progress")) {return OH.valueOf(this._progress);}
		    
break;
                case 29:

		    
                    if(name == "returnValueTablePos" || name.equals("returnValueTablePos")) {return OH.valueOf(this._returnValueTablePos);}
		    
break;
                case 31:

		    
                    if(name == "previewTables" || name.equals("previewTables")) {return this._previewTables;}
            
break;
                case 36:

		    
                    if(name == "priority" || name.equals("priority")) {return OH.valueOf(this._priority);}
		    
break;
                case 37:

		    
                    if(name == "durrationNanos" || name.equals("durrationNanos")) {return OH.valueOf(this._durrationNanos);}
		    
break;
                case 39:

		    
                    if(name == "disableLogging" || name.equals("disableLogging")) {return OH.valueOf(this._disableLogging);}
		    
break;
                case 43:

		    
                    if(name == "generatedKeys" || name.equals("generatedKeys")) {return this._generatedKeys;}
            
break;
                case 44:

		    
                    if(name == "returnType" || name.equals("returnType")) {return this._returnType;}
            
break;
                case 46:

		    
                    if(name == "ticket" || name.equals("ticket")) {return this._ticket;}
            
break;
                case 57:

		    
                    if(name == "returnValue" || name.equals("returnValue")) {return this._returnValue;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 58;
        switch(h){

                case 1:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return java.util.List.class;}
break;
                case 5:

                    if(name == "tables" || name.equals("tables")) {return java.util.List.class;}
break;
                case 10:

                    if(name == "ok" || name.equals("ok")) {return boolean.class;}
break;
                case 11:

                    if(name == "querySessionId" || name.equals("querySessionId")) {return long.class;}
break;
                case 13:

                    if(name == "message" || name.equals("message")) {return java.lang.String.class;}
break;
                case 19:

                    if(name == "exception" || name.equals("exception")) {return java.lang.Exception.class;}
break;
                case 23:

                    if(name == "rowsEffected" || name.equals("rowsEffected")) {return long.class;}
break;
                case 25:

                    if(name == "progress" || name.equals("progress")) {return double.class;}
break;
                case 29:

                    if(name == "returnValueTablePos" || name.equals("returnValueTablePos")) {return int.class;}
break;
                case 31:

                    if(name == "previewTables" || name.equals("previewTables")) {return java.util.List.class;}
break;
                case 36:

                    if(name == "priority" || name.equals("priority")) {return int.class;}
break;
                case 37:

                    if(name == "durrationNanos" || name.equals("durrationNanos")) {return long.class;}
break;
                case 39:

                    if(name == "disableLogging" || name.equals("disableLogging")) {return boolean.class;}
break;
                case 43:

                    if(name == "generatedKeys" || name.equals("generatedKeys")) {return java.util.List.class;}
break;
                case 44:

                    if(name == "returnType" || name.equals("returnType")) {return java.lang.Class.class;}
break;
                case 46:

                    if(name == "ticket" || name.equals("ticket")) {return java.lang.String.class;}
break;
                case 57:

                    if(name == "returnValue" || name.equals("returnValue")) {return java.lang.Object.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 58;
        switch(h){

                case 1:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return VALUED_PARAM_trackedEvents;}
break;
                case 5:

                    if(name == "tables" || name.equals("tables")) {return VALUED_PARAM_tables;}
break;
                case 10:

                    if(name == "ok" || name.equals("ok")) {return VALUED_PARAM_ok;}
break;
                case 11:

                    if(name == "querySessionId" || name.equals("querySessionId")) {return VALUED_PARAM_querySessionId;}
break;
                case 13:

                    if(name == "message" || name.equals("message")) {return VALUED_PARAM_message;}
break;
                case 19:

                    if(name == "exception" || name.equals("exception")) {return VALUED_PARAM_exception;}
break;
                case 23:

                    if(name == "rowsEffected" || name.equals("rowsEffected")) {return VALUED_PARAM_rowsEffected;}
break;
                case 25:

                    if(name == "progress" || name.equals("progress")) {return VALUED_PARAM_progress;}
break;
                case 29:

                    if(name == "returnValueTablePos" || name.equals("returnValueTablePos")) {return VALUED_PARAM_returnValueTablePos;}
break;
                case 31:

                    if(name == "previewTables" || name.equals("previewTables")) {return VALUED_PARAM_previewTables;}
break;
                case 36:

                    if(name == "priority" || name.equals("priority")) {return VALUED_PARAM_priority;}
break;
                case 37:

                    if(name == "durrationNanos" || name.equals("durrationNanos")) {return VALUED_PARAM_durrationNanos;}
break;
                case 39:

                    if(name == "disableLogging" || name.equals("disableLogging")) {return VALUED_PARAM_disableLogging;}
break;
                case 43:

                    if(name == "generatedKeys" || name.equals("generatedKeys")) {return VALUED_PARAM_generatedKeys;}
break;
                case 44:

                    if(name == "returnType" || name.equals("returnType")) {return VALUED_PARAM_returnType;}
break;
                case 46:

                    if(name == "ticket" || name.equals("ticket")) {return VALUED_PARAM_ticket;}
break;
                case 57:

                    if(name == "returnValue" || name.equals("returnValue")) {return VALUED_PARAM_returnValue;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 58;
        switch(h){

                case 1:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return 16;}
break;
                case 5:

                    if(name == "tables" || name.equals("tables")) {return 14;}
break;
                case 10:

                    if(name == "ok" || name.equals("ok")) {return 5;}
break;
                case 11:

                    if(name == "querySessionId" || name.equals("querySessionId")) {return 9;}
break;
                case 13:

                    if(name == "message" || name.equals("message")) {return 4;}
break;
                case 19:

                    if(name == "exception" || name.equals("exception")) {return 2;}
break;
                case 23:

                    if(name == "rowsEffected" || name.equals("rowsEffected")) {return 13;}
break;
                case 25:

                    if(name == "progress" || name.equals("progress")) {return 8;}
break;
                case 29:

                    if(name == "returnValueTablePos" || name.equals("returnValueTablePos")) {return 12;}
break;
                case 31:

                    if(name == "previewTables" || name.equals("previewTables")) {return 6;}
break;
                case 36:

                    if(name == "priority" || name.equals("priority")) {return 7;}
break;
                case 37:

                    if(name == "durrationNanos" || name.equals("durrationNanos")) {return 1;}
break;
                case 39:

                    if(name == "disableLogging" || name.equals("disableLogging")) {return 0;}
break;
                case 43:

                    if(name == "generatedKeys" || name.equals("generatedKeys")) {return 3;}
break;
                case 44:

                    if(name == "returnType" || name.equals("returnType")) {return 10;}
break;
                case 46:

                    if(name == "ticket" || name.equals("ticket")) {return 15;}
break;
                case 57:

                    if(name == "returnValue" || name.equals("returnValue")) {return 11;}
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
        return 17;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiCenterQueryDsResponse0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 58;
        switch(h){

                case 1:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return true;}
break;
                case 5:

                    if(name == "tables" || name.equals("tables")) {return true;}
break;
                case 10:

                    if(name == "ok" || name.equals("ok")) {return true;}
break;
                case 11:

                    if(name == "querySessionId" || name.equals("querySessionId")) {return true;}
break;
                case 13:

                    if(name == "message" || name.equals("message")) {return true;}
break;
                case 19:

                    if(name == "exception" || name.equals("exception")) {return true;}
break;
                case 23:

                    if(name == "rowsEffected" || name.equals("rowsEffected")) {return true;}
break;
                case 25:

                    if(name == "progress" || name.equals("progress")) {return true;}
break;
                case 29:

                    if(name == "returnValueTablePos" || name.equals("returnValueTablePos")) {return true;}
break;
                case 31:

                    if(name == "previewTables" || name.equals("previewTables")) {return true;}
break;
                case 36:

                    if(name == "priority" || name.equals("priority")) {return true;}
break;
                case 37:

                    if(name == "durrationNanos" || name.equals("durrationNanos")) {return true;}
break;
                case 39:

                    if(name == "disableLogging" || name.equals("disableLogging")) {return true;}
break;
                case 43:

                    if(name == "generatedKeys" || name.equals("generatedKeys")) {return true;}
break;
                case 44:

                    if(name == "returnType" || name.equals("returnType")) {return true;}
break;
                case 46:

                    if(name == "ticket" || name.equals("ticket")) {return true;}
break;
                case 57:

                    if(name == "returnValue" || name.equals("returnValue")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 58;
        switch(h){

                case 1:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return 21;}
break;
                case 5:

                    if(name == "tables" || name.equals("tables")) {return 21;}
break;
                case 10:

                    if(name == "ok" || name.equals("ok")) {return 0;}
break;
                case 11:

                    if(name == "querySessionId" || name.equals("querySessionId")) {return 6;}
break;
                case 13:

                    if(name == "message" || name.equals("message")) {return 20;}
break;
                case 19:

                    if(name == "exception" || name.equals("exception")) {return 53;}
break;
                case 23:

                    if(name == "rowsEffected" || name.equals("rowsEffected")) {return 6;}
break;
                case 25:

                    if(name == "progress" || name.equals("progress")) {return 7;}
break;
                case 29:

                    if(name == "returnValueTablePos" || name.equals("returnValueTablePos")) {return 4;}
break;
                case 31:

                    if(name == "previewTables" || name.equals("previewTables")) {return 21;}
break;
                case 36:

                    if(name == "priority" || name.equals("priority")) {return 4;}
break;
                case 37:

                    if(name == "durrationNanos" || name.equals("durrationNanos")) {return 6;}
break;
                case 39:

                    if(name == "disableLogging" || name.equals("disableLogging")) {return 0;}
break;
                case 43:

                    if(name == "generatedKeys" || name.equals("generatedKeys")) {return 21;}
break;
                case 44:

                    if(name == "returnType" || name.equals("returnType")) {return 51;}
break;
                case 46:

                    if(name == "ticket" || name.equals("ticket")) {return 20;}
break;
                case 57:

                    if(name == "returnValue" || name.equals("returnValue")) {return 18;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _disableLogging;

        case 1:return _durrationNanos;

        case 2:return _exception;

        case 3:return _generatedKeys;

        case 4:return _message;

        case 5:return _ok;

        case 6:return _previewTables;

        case 7:return _priority;

        case 8:return _progress;

        case 9:return _querySessionId;

        case 10:return _returnType;

        case 11:return _returnValue;

        case 12:return _returnValueTablePos;

        case 13:return _rowsEffected;

        case 14:return _tables;

        case 15:return _ticket;

        case 16:return _trackedEvents;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 17);
    }

    public boolean getDisableLogging(){
        return this._disableLogging;
    }
    public void setDisableLogging(boolean _disableLogging){
    
        this._disableLogging=_disableLogging;
    }

    public long getDurrationNanos(){
        return this._durrationNanos;
    }
    public void setDurrationNanos(long _durrationNanos){
    
        this._durrationNanos=_durrationNanos;
    }

    public java.lang.Exception getException(){
        return this._exception;
    }
    public void setException(java.lang.Exception _exception){
    
        this._exception=_exception;
    }

    public java.util.List getGeneratedKeys(){
        return this._generatedKeys;
    }
    public void setGeneratedKeys(java.util.List _generatedKeys){
    
        this._generatedKeys=_generatedKeys;
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

    public java.util.List getPreviewTables(){
        return this._previewTables;
    }
    public void setPreviewTables(java.util.List _previewTables){
    
        this._previewTables=_previewTables;
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

    public long getQuerySessionId(){
        return this._querySessionId;
    }
    public void setQuerySessionId(long _querySessionId){
    
        this._querySessionId=_querySessionId;
    }

    public java.lang.Class getReturnType(){
        return this._returnType;
    }
    public void setReturnType(java.lang.Class _returnType){
    
        this._returnType=_returnType;
    }

    public java.lang.Object getReturnValue(){
        return this._returnValue;
    }
    public void setReturnValue(java.lang.Object _returnValue){
    
        this._returnValue=_returnValue;
    }

    public int getReturnValueTablePos(){
        return this._returnValueTablePos;
    }
    public void setReturnValueTablePos(int _returnValueTablePos){
    
        this._returnValueTablePos=_returnValueTablePos;
    }

    public long getRowsEffected(){
        return this._rowsEffected;
    }
    public void setRowsEffected(long _rowsEffected){
    
        this._rowsEffected=_rowsEffected;
    }

    public java.util.List getTables(){
        return this._tables;
    }
    public void setTables(java.util.List _tables){
    
        this._tables=_tables;
    }

    public java.lang.String getTicket(){
        return this._ticket;
    }
    public void setTicket(java.lang.String _ticket){
    
        this._ticket=_ticket;
    }

    public java.util.List getTrackedEvents(){
        return this._trackedEvents;
    }
    public void setTrackedEvents(java.util.List _trackedEvents){
    
        this._trackedEvents=_trackedEvents;
    }





  
    private static final class VALUED_PARAM_CLASS_disableLogging extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getDisableLogging());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
		      valued.setDisableLogging(stream.readBoolean());
		    
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
            return 11;
	    }
    
	    @Override
	    public String getName() {
            return "disableLogging";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (boolean)((AmiCenterQueryDsResponse0)valued).getDisableLogging();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setDisableLogging((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setDisableLogging(source.getDisableLogging());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
	        return OH.eq(dest.getDisableLogging(),source.getDisableLogging());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterQueryDsResponse0 valued) {
		    return valued.getDisableLogging();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterQueryDsResponse0 valued, boolean value) {
		    valued.setDisableLogging(value);
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getDisableLogging());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getDisableLogging());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean disableLogging";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setDisableLogging(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_disableLogging=new VALUED_PARAM_CLASS_disableLogging();
  

  
    private static final class VALUED_PARAM_CLASS_durrationNanos extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getDurrationNanos());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
		      valued.setDurrationNanos(stream.readLong());
		    
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
            return "durrationNanos";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (long)((AmiCenterQueryDsResponse0)valued).getDurrationNanos();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setDurrationNanos((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setDurrationNanos(source.getDurrationNanos());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
	        return OH.eq(dest.getDurrationNanos(),source.getDurrationNanos());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterQueryDsResponse0 valued) {
		    return valued.getDurrationNanos();
	    }
    
	    @Override
	    public void setLong(AmiCenterQueryDsResponse0 valued, long value) {
		    valued.setDurrationNanos(value);
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getDurrationNanos());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getDurrationNanos());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long durrationNanos";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setDurrationNanos(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_durrationNanos=new VALUED_PARAM_CLASS_durrationNanos();
  

  
    private static final class VALUED_PARAM_CLASS_exception extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 53;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Exception}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 2;
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
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (java.lang.Exception)((AmiCenterQueryDsResponse0)valued).getException();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setException((java.lang.Exception)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setException(source.getException());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getException());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getException());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Exception exception";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setException(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_exception=new VALUED_PARAM_CLASS_exception();
  

  
    private static final class VALUED_PARAM_CLASS_generatedKeys extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 3;
	    }
    
	    @Override
	    public byte getPid() {
            return 9;
	    }
    
	    @Override
	    public String getName() {
            return "generatedKeys";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (java.util.List)((AmiCenterQueryDsResponse0)valued).getGeneratedKeys();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setGeneratedKeys((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setGeneratedKeys(source.getGeneratedKeys());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
	        return OH.eq(dest.getGeneratedKeys(),source.getGeneratedKeys());
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getGeneratedKeys());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getGeneratedKeys());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List generatedKeys";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setGeneratedKeys(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_generatedKeys=new VALUED_PARAM_CLASS_generatedKeys();
  

  
    private static final class VALUED_PARAM_CLASS_message extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
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
            return 43;
	    }
    
	    @Override
	    public String getName() {
            return "message";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsResponse0)valued).getMessage();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setMessage((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setMessage(source.getMessage());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getMessage(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getMessage(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String message";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setMessage(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_message=new VALUED_PARAM_CLASS_message();
  

  
    private static final class VALUED_PARAM_CLASS_ok extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getOk());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 5;
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
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (boolean)((AmiCenterQueryDsResponse0)valued).getOk();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setOk((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setOk(source.getOk());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
	        return OH.eq(dest.getOk(),source.getOk());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterQueryDsResponse0 valued) {
		    return valued.getOk();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterQueryDsResponse0 valued, boolean value) {
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getOk());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getOk());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean ok";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setOk(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_ok=new VALUED_PARAM_CLASS_ok();
  

  
    private static final class VALUED_PARAM_CLASS_previewTables extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 6;
	    }
    
	    @Override
	    public byte getPid() {
            return 6;
	    }
    
	    @Override
	    public String getName() {
            return "previewTables";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (java.util.List)((AmiCenterQueryDsResponse0)valued).getPreviewTables();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setPreviewTables((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setPreviewTables(source.getPreviewTables());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
	        return OH.eq(dest.getPreviewTables(),source.getPreviewTables());
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPreviewTables());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPreviewTables());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List previewTables";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setPreviewTables(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_previewTables=new VALUED_PARAM_CLASS_previewTables();
  

  
    private static final class VALUED_PARAM_CLASS_priority extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getPriority());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
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
            return 57;
	    }
    
	    @Override
	    public String getName() {
            return "priority";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (int)((AmiCenterQueryDsResponse0)valued).getPriority();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setPriority((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setPriority(source.getPriority());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
	        return OH.eq(dest.getPriority(),source.getPriority());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterQueryDsResponse0 valued) {
		    return valued.getPriority();
	    }
    
	    @Override
	    public void setInt(AmiCenterQueryDsResponse0 valued, int value) {
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int priority";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setPriority(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_priority=new VALUED_PARAM_CLASS_priority();
  

  
    private static final class VALUED_PARAM_CLASS_progress extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 7;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeDouble(valued.getProgress());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 8;
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
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (double)((AmiCenterQueryDsResponse0)valued).getProgress();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setProgress((java.lang.Double)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setProgress(source.getProgress());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
	        return OH.eq(dest.getProgress(),source.getProgress());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public double getDouble(AmiCenterQueryDsResponse0 valued) {
		    return valued.getProgress();
	    }
    
	    @Override
	    public void setDouble(AmiCenterQueryDsResponse0 valued, double value) {
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getProgress());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getProgress());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:double progress";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setProgress(0D);
	    }
	};
    private static final ValuedParam VALUED_PARAM_progress=new VALUED_PARAM_CLASS_progress();
  

  
    private static final class VALUED_PARAM_CLASS_querySessionId extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getQuerySessionId());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
		      valued.setQuerySessionId(stream.readLong());
		    
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
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "querySessionId";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (long)((AmiCenterQueryDsResponse0)valued).getQuerySessionId();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setQuerySessionId((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setQuerySessionId(source.getQuerySessionId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
	        return OH.eq(dest.getQuerySessionId(),source.getQuerySessionId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterQueryDsResponse0 valued) {
		    return valued.getQuerySessionId();
	    }
    
	    @Override
	    public void setLong(AmiCenterQueryDsResponse0 valued, long value) {
		    valued.setQuerySessionId(value);
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getQuerySessionId());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getQuerySessionId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long querySessionId";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setQuerySessionId(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_querySessionId=new VALUED_PARAM_CLASS_querySessionId();
  

  
    private static final class VALUED_PARAM_CLASS_returnType extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 51;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Class}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Class}");
		    
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
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "returnType";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (java.lang.Class)((AmiCenterQueryDsResponse0)valued).getReturnType();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setReturnType((java.lang.Class)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setReturnType(source.getReturnType());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
	        return OH.eq(dest.getReturnType(),source.getReturnType());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.Class.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.Class.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getReturnType());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getReturnType());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Class returnType";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setReturnType(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_returnType=new VALUED_PARAM_CLASS_returnType();
  

  
    private static final class VALUED_PARAM_CLASS_returnValue extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 18;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Object}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 11;
	    }
    
	    @Override
	    public byte getPid() {
            return 8;
	    }
    
	    @Override
	    public String getName() {
            return "returnValue";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (java.lang.Object)((AmiCenterQueryDsResponse0)valued).getReturnValue();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setReturnValue((java.lang.Object)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setReturnValue(source.getReturnValue());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
	        return OH.eq(dest.getReturnValue(),source.getReturnValue());
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getReturnValue());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getReturnValue());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Object returnValue";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setReturnValue(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_returnValue=new VALUED_PARAM_CLASS_returnValue();
  

  
    private static final class VALUED_PARAM_CLASS_returnValueTablePos extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getReturnValueTablePos());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
		      valued.setReturnValueTablePos(stream.readInt());
		    
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
		    return 12;
	    }
    
	    @Override
	    public byte getPid() {
            return 10;
	    }
    
	    @Override
	    public String getName() {
            return "returnValueTablePos";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (int)((AmiCenterQueryDsResponse0)valued).getReturnValueTablePos();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setReturnValueTablePos((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setReturnValueTablePos(source.getReturnValueTablePos());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
	        return OH.eq(dest.getReturnValueTablePos(),source.getReturnValueTablePos());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterQueryDsResponse0 valued) {
		    return valued.getReturnValueTablePos();
	    }
    
	    @Override
	    public void setInt(AmiCenterQueryDsResponse0 valued, int value) {
		    valued.setReturnValueTablePos(value);
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getReturnValueTablePos());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getReturnValueTablePos());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int returnValueTablePos";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setReturnValueTablePos(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_returnValueTablePos=new VALUED_PARAM_CLASS_returnValueTablePos();
  

  
    private static final class VALUED_PARAM_CLASS_rowsEffected extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getRowsEffected());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
		      valued.setRowsEffected(stream.readLong());
		    
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
            return "rowsEffected";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (long)((AmiCenterQueryDsResponse0)valued).getRowsEffected();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setRowsEffected((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setRowsEffected(source.getRowsEffected());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
	        return OH.eq(dest.getRowsEffected(),source.getRowsEffected());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterQueryDsResponse0 valued) {
		    return valued.getRowsEffected();
	    }
    
	    @Override
	    public void setLong(AmiCenterQueryDsResponse0 valued, long value) {
		    valued.setRowsEffected(value);
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getRowsEffected());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getRowsEffected());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long rowsEffected";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setRowsEffected(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_rowsEffected=new VALUED_PARAM_CLASS_rowsEffected();
  

  
    private static final class VALUED_PARAM_CLASS_tables extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 14;
	    }
    
	    @Override
	    public byte getPid() {
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "tables";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (java.util.List)((AmiCenterQueryDsResponse0)valued).getTables();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setTables((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setTables(source.getTables());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
	        return OH.eq(dest.getTables(),source.getTables());
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTables());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTables());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List tables";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setTables(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_tables=new VALUED_PARAM_CLASS_tables();
  

  
    private static final class VALUED_PARAM_CLASS_ticket extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 15;
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
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (java.lang.String)((AmiCenterQueryDsResponse0)valued).getTicket();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setTicket((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setTicket(source.getTicket());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getTicket(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getTicket(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String ticket";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setTicket(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_ticket=new VALUED_PARAM_CLASS_ticket();
  

  
    private static final class VALUED_PARAM_CLASS_trackedEvents extends AbstractValuedParam<AmiCenterQueryDsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryDsResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryDsResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 16;
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
	    public Object getValue(AmiCenterQueryDsResponse0 valued) {
		    return (java.util.List)((AmiCenterQueryDsResponse0)valued).getTrackedEvents();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryDsResponse0 valued, Object value) {
		    valued.setTrackedEvents((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
		    dest.setTrackedEvents(source.getTrackedEvents());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryDsResponse0 source, AmiCenterQueryDsResponse0 dest) {
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
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTrackedEvents());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryDsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTrackedEvents());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List trackedEvents";
	    }
	    @Override
	    public void clear(AmiCenterQueryDsResponse0 valued){
	       valued.setTrackedEvents(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_trackedEvents=new VALUED_PARAM_CLASS_trackedEvents();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_disableLogging, VALUED_PARAM_durrationNanos, VALUED_PARAM_exception, VALUED_PARAM_generatedKeys, VALUED_PARAM_message, VALUED_PARAM_ok, VALUED_PARAM_previewTables, VALUED_PARAM_priority, VALUED_PARAM_progress, VALUED_PARAM_querySessionId, VALUED_PARAM_returnType, VALUED_PARAM_returnValue, VALUED_PARAM_returnValueTablePos, VALUED_PARAM_rowsEffected, VALUED_PARAM_tables, VALUED_PARAM_ticket, VALUED_PARAM_trackedEvents, };



    private static final byte PIDS[]={ 11 ,3,44,9,43,42,6,57,53,4,7,8,10,5,2,55,58};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 11: return VALUED_PARAM_disableLogging;
             case 3: return VALUED_PARAM_durrationNanos;
             case 44: return VALUED_PARAM_exception;
             case 9: return VALUED_PARAM_generatedKeys;
             case 43: return VALUED_PARAM_message;
             case 42: return VALUED_PARAM_ok;
             case 6: return VALUED_PARAM_previewTables;
             case 57: return VALUED_PARAM_priority;
             case 53: return VALUED_PARAM_progress;
             case 4: return VALUED_PARAM_querySessionId;
             case 7: return VALUED_PARAM_returnType;
             case 8: return VALUED_PARAM_returnValue;
             case 10: return VALUED_PARAM_returnValueTablePos;
             case 5: return VALUED_PARAM_rowsEffected;
             case 2: return VALUED_PARAM_tables;
             case 55: return VALUED_PARAM_ticket;
             case 58: return VALUED_PARAM_trackedEvents;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 11: return true;
             case 3: return true;
             case 44: return true;
             case 9: return true;
             case 43: return true;
             case 42: return true;
             case 6: return true;
             case 57: return true;
             case 53: return true;
             case 4: return true;
             case 7: return true;
             case 8: return true;
             case 10: return true;
             case 5: return true;
             case 2: return true;
             case 55: return true;
             case 58: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 11: return "disableLogging";
             case 3: return "durrationNanos";
             case 44: return "exception";
             case 9: return "generatedKeys";
             case 43: return "message";
             case 42: return "ok";
             case 6: return "previewTables";
             case 57: return "priority";
             case 53: return "progress";
             case 4: return "querySessionId";
             case 7: return "returnType";
             case 8: return "returnValue";
             case 10: return "returnValueTablePos";
             case 5: return "rowsEffected";
             case 2: return "tables";
             case 55: return "ticket";
             case 58: return "trackedEvents";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 11: return 0;
             case 3: return 1;
             case 44: return 2;
             case 9: return 3;
             case 43: return 4;
             case 42: return 5;
             case 6: return 6;
             case 57: return 7;
             case 53: return 8;
             case 4: return 9;
             case 7: return 10;
             case 8: return 11;
             case 10: return 12;
             case 5: return 13;
             case 2: return 14;
             case 55: return 15;
             case 58: return 16;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="disableLogging") return 11;
             if(name=="durrationNanos") return 3;
             if(name=="exception") return 44;
             if(name=="generatedKeys") return 9;
             if(name=="message") return 43;
             if(name=="ok") return 42;
             if(name=="previewTables") return 6;
             if(name=="priority") return 57;
             if(name=="progress") return 53;
             if(name=="querySessionId") return 4;
             if(name=="returnType") return 7;
             if(name=="returnValue") return 8;
             if(name=="returnValueTablePos") return 10;
             if(name=="rowsEffected") return 5;
             if(name=="tables") return 2;
             if(name=="ticket") return 55;
             if(name=="trackedEvents") return 58;
            
             if("disableLogging".equals(name)) return 11;
             if("durrationNanos".equals(name)) return 3;
             if("exception".equals(name)) return 44;
             if("generatedKeys".equals(name)) return 9;
             if("message".equals(name)) return 43;
             if("ok".equals(name)) return 42;
             if("previewTables".equals(name)) return 6;
             if("priority".equals(name)) return 57;
             if("progress".equals(name)) return 53;
             if("querySessionId".equals(name)) return 4;
             if("returnType".equals(name)) return 7;
             if("returnValue".equals(name)) return 8;
             if("returnValueTablePos".equals(name)) return 10;
             if("rowsEffected".equals(name)) return 5;
             if("tables".equals(name)) return 2;
             if("ticket".equals(name)) return 55;
             if("trackedEvents".equals(name)) return 58;
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
             case 11: return  OH.valueOf(this._disableLogging); 
             case 3: return  OH.valueOf(this._durrationNanos); 
             case 44: return  this._exception; 
             case 9: return  this._generatedKeys; 
             case 43: return  this._message; 
             case 42: return  OH.valueOf(this._ok); 
             case 6: return  this._previewTables; 
             case 57: return  OH.valueOf(this._priority); 
             case 53: return  OH.valueOf(this._progress); 
             case 4: return  OH.valueOf(this._querySessionId); 
             case 7: return  this._returnType; 
             case 8: return  this._returnValue; 
             case 10: return  OH.valueOf(this._returnValueTablePos); 
             case 5: return  OH.valueOf(this._rowsEffected); 
             case 2: return  this._tables; 
             case 55: return  this._ticket; 
             case 58: return  this._trackedEvents; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 11: return boolean.class;
             case 3: return long.class;
             case 44: return java.lang.Exception.class;
             case 9: return java.util.List.class;
             case 43: return java.lang.String.class;
             case 42: return boolean.class;
             case 6: return java.util.List.class;
             case 57: return int.class;
             case 53: return double.class;
             case 4: return long.class;
             case 7: return java.lang.Class.class;
             case 8: return java.lang.Object.class;
             case 10: return int.class;
             case 5: return long.class;
             case 2: return java.util.List.class;
             case 55: return java.lang.String.class;
             case 58: return java.util.List.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 11: return 0;
             case 3: return 6;
             case 44: return 53;
             case 9: return 21;
             case 43: return 20;
             case 42: return 0;
             case 6: return 21;
             case 57: return 4;
             case 53: return 7;
             case 4: return 6;
             case 7: return 51;
             case 8: return 18;
             case 10: return 4;
             case 5: return 6;
             case 2: return 21;
             case 55: return 20;
             case 58: return 21;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 11: this._disableLogging=(java.lang.Boolean)value;return;
             case 3: this._durrationNanos=(java.lang.Long)value;return;
             case 44: this._exception=(java.lang.Exception)value;return;
             case 9: this._generatedKeys=(java.util.List)value;return;
             case 43: this._message=(java.lang.String)value;return;
             case 42: this._ok=(java.lang.Boolean)value;return;
             case 6: this._previewTables=(java.util.List)value;return;
             case 57: this._priority=(java.lang.Integer)value;return;
             case 53: this._progress=(java.lang.Double)value;return;
             case 4: this._querySessionId=(java.lang.Long)value;return;
             case 7: this._returnType=(java.lang.Class)value;return;
             case 8: this._returnValue=(java.lang.Object)value;return;
             case 10: this._returnValueTablePos=(java.lang.Integer)value;return;
             case 5: this._rowsEffected=(java.lang.Long)value;return;
             case 2: this._tables=(java.util.List)value;return;
             case 55: this._ticket=(java.lang.String)value;return;
             case 58: this._trackedEvents=(java.util.List)value;return;
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
             case 11: this._disableLogging=(java.lang.Boolean)value;return true;
             case 3: this._durrationNanos=(java.lang.Long)value;return true;
             case 44: this._exception=(java.lang.Exception)value;return true;
             case 9: this._generatedKeys=(java.util.List)value;return true;
             case 43: this._message=(java.lang.String)value;return true;
             case 42: this._ok=(java.lang.Boolean)value;return true;
             case 6: this._previewTables=(java.util.List)value;return true;
             case 57: this._priority=(java.lang.Integer)value;return true;
             case 53: this._progress=(java.lang.Double)value;return true;
             case 4: this._querySessionId=(java.lang.Long)value;return true;
             case 7: this._returnType=(java.lang.Class)value;return true;
             case 8: this._returnValue=(java.lang.Object)value;return true;
             case 10: this._returnValueTablePos=(java.lang.Integer)value;return true;
             case 5: this._rowsEffected=(java.lang.Long)value;return true;
             case 2: this._tables=(java.util.List)value;return true;
             case 55: this._ticket=(java.lang.String)value;return true;
             case 58: this._trackedEvents=(java.util.List)value;return true;
            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
             case 11: return this._disableLogging;
             case 42: return this._ok;
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
             case 57: return this._priority;
             case 10: return this._returnValueTablePos;
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
             case 3: return this._durrationNanos;
             case 4: return this._querySessionId;
             case 5: return this._rowsEffected;
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
             case 11: this._disableLogging=value;return;
             case 42: this._ok=value;return;
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
             case 57: this._priority=value;return;
             case 10: this._returnValueTablePos=value;return;
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
             case 3: this._durrationNanos=value;return;
             case 4: this._querySessionId=value;return;
             case 5: this._rowsEffected=value;return;
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
        
        case 2:
        
            this._tables=(java.util.List)converter.read(session);
        
            break;

        case 3:
        
            if((basicType=in.readByte())!=6)
                break;
            this._durrationNanos=in.readLong();
        
            break;

        case 4:
        
            if((basicType=in.readByte())!=6)
                break;
            this._querySessionId=in.readLong();
        
            break;

        case 5:
        
            if((basicType=in.readByte())!=6)
                break;
            this._rowsEffected=in.readLong();
        
            break;

        case 6:
        
            this._previewTables=(java.util.List)converter.read(session);
        
            break;

        case 7:
        
            this._returnType=(java.lang.Class)converter.read(session);
        
            break;

        case 8:
        
            this._returnValue=(java.lang.Object)converter.read(session);
        
            break;

        case 9:
        
            this._generatedKeys=(java.util.List)converter.read(session);
        
            break;

        case 10:
        
            if((basicType=in.readByte())!=4)
                break;
            this._returnValueTablePos=in.readInt();
        
            break;

        case 11:
        
            if((basicType=in.readByte())!=0)
                break;
            this._disableLogging=in.readBoolean();
        
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
        
if(this._tables!=null && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this._tables,session);
        
}

if(this._durrationNanos!=0L && (0 & transience)==0){
    out.writeByte(3);
        
    out.writeByte(6);
    out.writeLong(this._durrationNanos);
        
}

if(this._querySessionId!=0L && (0 & transience)==0){
    out.writeByte(4);
        
    out.writeByte(6);
    out.writeLong(this._querySessionId);
        
}

if(this._rowsEffected!=0L && (0 & transience)==0){
    out.writeByte(5);
        
    out.writeByte(6);
    out.writeLong(this._rowsEffected);
        
}

if(this._previewTables!=null && (0 & transience)==0){
    out.writeByte(6);
        
    converter.write(this._previewTables,session);
        
}

if(this._returnType!=null && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this._returnType,session);
        
}

if(this._returnValue!=null && (0 & transience)==0){
    out.writeByte(8);
        
    converter.write(this._returnValue,session);
        
}

if(this._generatedKeys!=null && (0 & transience)==0){
    out.writeByte(9);
        
    converter.write(this._generatedKeys,session);
        
}

if(this._returnValueTablePos!=0 && (0 & transience)==0){
    out.writeByte(10);
        
    out.writeByte(4);
    out.writeInt(this._returnValueTablePos);
        
}

if(this._disableLogging!=false && (0 & transience)==0){
    out.writeByte(11);
        
    out.writeByte(0);
    out.writeBoolean(this._disableLogging);
        
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