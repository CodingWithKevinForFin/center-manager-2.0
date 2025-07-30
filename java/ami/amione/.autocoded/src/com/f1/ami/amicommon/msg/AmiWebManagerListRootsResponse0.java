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

public abstract class AmiWebManagerListRootsResponse0 implements com.f1.ami.amicommon.msg.AmiWebManagerListRootsResponse ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private java.lang.Exception _exception;

    private java.util.List _files;

    private java.lang.String _message;

    private boolean _ok;

    private int _priority;

    private double _progress;

    private java.lang.String _ticket;

    private java.util.List _trackedEvents;

    private static final String NAMES[]={ "exception" ,"files","message","ok","priority","progress","ticket","trackedEvents"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 18;
        try{
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return;}
break;
                case 1:

                    if(name == "progress" || name.equals("progress")) {this._progress=(java.lang.Double)value;return;}
break;
                case 2:

                    if(name == "ok" || name.equals("ok")) {this._ok=(java.lang.Boolean)value;return;}
break;
                case 5:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {this._trackedEvents=(java.util.List)value;return;}
break;
                case 7:

                    if(name == "message" || name.equals("message")) {this._message=(java.lang.String)value;return;}
break;
                case 13:

                    if(name == "exception" || name.equals("exception")) {this._exception=(java.lang.Exception)value;return;}
break;
                case 14:

                    if(name == "ticket" || name.equals("ticket")) {this._ticket=(java.lang.String)value;return;}
break;
                case 15:

                    if(name == "files" || name.equals("files")) {this._files=(java.util.List)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 18;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return true;}
break;
                case 1:

                    if(name == "progress" || name.equals("progress")) {this._progress=(java.lang.Double)value;return true;}
break;
                case 2:

                    if(name == "ok" || name.equals("ok")) {this._ok=(java.lang.Boolean)value;return true;}
break;
                case 5:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {this._trackedEvents=(java.util.List)value;return true;}
break;
                case 7:

                    if(name == "message" || name.equals("message")) {this._message=(java.lang.String)value;return true;}
break;
                case 13:

                    if(name == "exception" || name.equals("exception")) {this._exception=(java.lang.Exception)value;return true;}
break;
                case 14:

                    if(name == "ticket" || name.equals("ticket")) {this._ticket=(java.lang.String)value;return true;}
break;
                case 15:

                    if(name == "files" || name.equals("files")) {this._files=(java.util.List)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 18;
        switch(h){

                case 0:

		    
                    if(name == "priority" || name.equals("priority")) {return OH.valueOf(this._priority);}
		    
break;
                case 1:

		    
                    if(name == "progress" || name.equals("progress")) {return OH.valueOf(this._progress);}
		    
break;
                case 2:

		    
                    if(name == "ok" || name.equals("ok")) {return OH.valueOf(this._ok);}
		    
break;
                case 5:

		    
                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return this._trackedEvents;}
            
break;
                case 7:

		    
                    if(name == "message" || name.equals("message")) {return this._message;}
            
break;
                case 13:

		    
                    if(name == "exception" || name.equals("exception")) {return this._exception;}
            
break;
                case 14:

		    
                    if(name == "ticket" || name.equals("ticket")) {return this._ticket;}
            
break;
                case 15:

		    
                    if(name == "files" || name.equals("files")) {return this._files;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 18;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return int.class;}
break;
                case 1:

                    if(name == "progress" || name.equals("progress")) {return double.class;}
break;
                case 2:

                    if(name == "ok" || name.equals("ok")) {return boolean.class;}
break;
                case 5:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return java.util.List.class;}
break;
                case 7:

                    if(name == "message" || name.equals("message")) {return java.lang.String.class;}
break;
                case 13:

                    if(name == "exception" || name.equals("exception")) {return java.lang.Exception.class;}
break;
                case 14:

                    if(name == "ticket" || name.equals("ticket")) {return java.lang.String.class;}
break;
                case 15:

                    if(name == "files" || name.equals("files")) {return java.util.List.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 18;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return VALUED_PARAM_priority;}
break;
                case 1:

                    if(name == "progress" || name.equals("progress")) {return VALUED_PARAM_progress;}
break;
                case 2:

                    if(name == "ok" || name.equals("ok")) {return VALUED_PARAM_ok;}
break;
                case 5:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return VALUED_PARAM_trackedEvents;}
break;
                case 7:

                    if(name == "message" || name.equals("message")) {return VALUED_PARAM_message;}
break;
                case 13:

                    if(name == "exception" || name.equals("exception")) {return VALUED_PARAM_exception;}
break;
                case 14:

                    if(name == "ticket" || name.equals("ticket")) {return VALUED_PARAM_ticket;}
break;
                case 15:

                    if(name == "files" || name.equals("files")) {return VALUED_PARAM_files;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 18;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return 4;}
break;
                case 1:

                    if(name == "progress" || name.equals("progress")) {return 5;}
break;
                case 2:

                    if(name == "ok" || name.equals("ok")) {return 3;}
break;
                case 5:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return 7;}
break;
                case 7:

                    if(name == "message" || name.equals("message")) {return 2;}
break;
                case 13:

                    if(name == "exception" || name.equals("exception")) {return 0;}
break;
                case 14:

                    if(name == "ticket" || name.equals("ticket")) {return 6;}
break;
                case 15:

                    if(name == "files" || name.equals("files")) {return 1;}
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
	    return (Class)AmiWebManagerListRootsResponse0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 18;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return true;}
break;
                case 1:

                    if(name == "progress" || name.equals("progress")) {return true;}
break;
                case 2:

                    if(name == "ok" || name.equals("ok")) {return true;}
break;
                case 5:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return true;}
break;
                case 7:

                    if(name == "message" || name.equals("message")) {return true;}
break;
                case 13:

                    if(name == "exception" || name.equals("exception")) {return true;}
break;
                case 14:

                    if(name == "ticket" || name.equals("ticket")) {return true;}
break;
                case 15:

                    if(name == "files" || name.equals("files")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 18;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return 4;}
break;
                case 1:

                    if(name == "progress" || name.equals("progress")) {return 7;}
break;
                case 2:

                    if(name == "ok" || name.equals("ok")) {return 0;}
break;
                case 5:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return 21;}
break;
                case 7:

                    if(name == "message" || name.equals("message")) {return 20;}
break;
                case 13:

                    if(name == "exception" || name.equals("exception")) {return 53;}
break;
                case 14:

                    if(name == "ticket" || name.equals("ticket")) {return 20;}
break;
                case 15:

                    if(name == "files" || name.equals("files")) {return 21;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _exception;

        case 1:return _files;

        case 2:return _message;

        case 3:return _ok;

        case 4:return _priority;

        case 5:return _progress;

        case 6:return _ticket;

        case 7:return _trackedEvents;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 8);
    }

    public java.lang.Exception getException(){
        return this._exception;
    }
    public void setException(java.lang.Exception _exception){
    
        this._exception=_exception;
    }

    public java.util.List getFiles(){
        return this._files;
    }
    public void setFiles(java.util.List _files){
    
        this._files=_files;
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





  
    private static final class VALUED_PARAM_CLASS_exception extends AbstractValuedParam<AmiWebManagerListRootsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 53;
	    }
	    
	    @Override
	    public void write(AmiWebManagerListRootsResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Exception}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerListRootsResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 0;
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
	    public Object getValue(AmiWebManagerListRootsResponse0 valued) {
		    return (java.lang.Exception)((AmiWebManagerListRootsResponse0)valued).getException();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerListRootsResponse0 valued, Object value) {
		    valued.setException((java.lang.Exception)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
		    dest.setException(source.getException());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
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
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getException());
	        
	    }
	    @Override
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getException());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Exception exception";
	    }
	    @Override
	    public void clear(AmiWebManagerListRootsResponse0 valued){
	       valued.setException(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_exception=new VALUED_PARAM_CLASS_exception();
  

  
    private static final class VALUED_PARAM_CLASS_files extends AbstractValuedParam<AmiWebManagerListRootsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiWebManagerListRootsResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerListRootsResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 1;
	    }
    
	    @Override
	    public byte getPid() {
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "files";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerListRootsResponse0 valued) {
		    return (java.util.List)((AmiWebManagerListRootsResponse0)valued).getFiles();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerListRootsResponse0 valued, Object value) {
		    valued.setFiles((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
		    dest.setFiles(source.getFiles());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
	        return OH.eq(dest.getFiles(),source.getFiles());
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
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getFiles());
	        
	    }
	    @Override
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getFiles());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List files";
	    }
	    @Override
	    public void clear(AmiWebManagerListRootsResponse0 valued){
	       valued.setFiles(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_files=new VALUED_PARAM_CLASS_files();
  

  
    private static final class VALUED_PARAM_CLASS_message extends AbstractValuedParam<AmiWebManagerListRootsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebManagerListRootsResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerListRootsResponse0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiWebManagerListRootsResponse0 valued) {
		    return (java.lang.String)((AmiWebManagerListRootsResponse0)valued).getMessage();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerListRootsResponse0 valued, Object value) {
		    valued.setMessage((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
		    dest.setMessage(source.getMessage());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
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
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getMessage(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getMessage(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String message";
	    }
	    @Override
	    public void clear(AmiWebManagerListRootsResponse0 valued){
	       valued.setMessage(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_message=new VALUED_PARAM_CLASS_message();
  

  
    private static final class VALUED_PARAM_CLASS_ok extends AbstractValuedParam<AmiWebManagerListRootsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiWebManagerListRootsResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getOk());
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerListRootsResponse0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiWebManagerListRootsResponse0 valued) {
		    return (boolean)((AmiWebManagerListRootsResponse0)valued).getOk();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerListRootsResponse0 valued, Object value) {
		    valued.setOk((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
		    dest.setOk(source.getOk());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
	        return OH.eq(dest.getOk(),source.getOk());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiWebManagerListRootsResponse0 valued) {
		    return valued.getOk();
	    }
    
	    @Override
	    public void setBoolean(AmiWebManagerListRootsResponse0 valued, boolean value) {
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
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getOk());
	        
	    }
	    @Override
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getOk());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean ok";
	    }
	    @Override
	    public void clear(AmiWebManagerListRootsResponse0 valued){
	       valued.setOk(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_ok=new VALUED_PARAM_CLASS_ok();
  

  
    private static final class VALUED_PARAM_CLASS_priority extends AbstractValuedParam<AmiWebManagerListRootsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiWebManagerListRootsResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getPriority());
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerListRootsResponse0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiWebManagerListRootsResponse0 valued) {
		    return (int)((AmiWebManagerListRootsResponse0)valued).getPriority();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerListRootsResponse0 valued, Object value) {
		    valued.setPriority((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
		    dest.setPriority(source.getPriority());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
	        return OH.eq(dest.getPriority(),source.getPriority());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiWebManagerListRootsResponse0 valued) {
		    return valued.getPriority();
	    }
    
	    @Override
	    public void setInt(AmiWebManagerListRootsResponse0 valued, int value) {
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
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int priority";
	    }
	    @Override
	    public void clear(AmiWebManagerListRootsResponse0 valued){
	       valued.setPriority(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_priority=new VALUED_PARAM_CLASS_priority();
  

  
    private static final class VALUED_PARAM_CLASS_progress extends AbstractValuedParam<AmiWebManagerListRootsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 7;
	    }
	    
	    @Override
	    public void write(AmiWebManagerListRootsResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeDouble(valued.getProgress());
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerListRootsResponse0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiWebManagerListRootsResponse0 valued) {
		    return (double)((AmiWebManagerListRootsResponse0)valued).getProgress();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerListRootsResponse0 valued, Object value) {
		    valued.setProgress((java.lang.Double)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
		    dest.setProgress(source.getProgress());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
	        return OH.eq(dest.getProgress(),source.getProgress());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public double getDouble(AmiWebManagerListRootsResponse0 valued) {
		    return valued.getProgress();
	    }
    
	    @Override
	    public void setDouble(AmiWebManagerListRootsResponse0 valued, double value) {
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
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getProgress());
	        
	    }
	    @Override
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getProgress());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:double progress";
	    }
	    @Override
	    public void clear(AmiWebManagerListRootsResponse0 valued){
	       valued.setProgress(0D);
	    }
	};
    private static final ValuedParam VALUED_PARAM_progress=new VALUED_PARAM_CLASS_progress();
  

  
    private static final class VALUED_PARAM_CLASS_ticket extends AbstractValuedParam<AmiWebManagerListRootsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiWebManagerListRootsResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerListRootsResponse0 valued, DataInput stream) throws IOException{
		    
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
            return 55;
	    }
    
	    @Override
	    public String getName() {
            return "ticket";
	    }
    
	    @Override
	    public Object getValue(AmiWebManagerListRootsResponse0 valued) {
		    return (java.lang.String)((AmiWebManagerListRootsResponse0)valued).getTicket();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerListRootsResponse0 valued, Object value) {
		    valued.setTicket((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
		    dest.setTicket(source.getTicket());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
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
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getTicket(),sb);
	        
	    }
	    @Override
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getTicket(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String ticket";
	    }
	    @Override
	    public void clear(AmiWebManagerListRootsResponse0 valued){
	       valued.setTicket(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_ticket=new VALUED_PARAM_CLASS_ticket();
  

  
    private static final class VALUED_PARAM_CLASS_trackedEvents extends AbstractValuedParam<AmiWebManagerListRootsResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiWebManagerListRootsResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiWebManagerListRootsResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 7;
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
	    public Object getValue(AmiWebManagerListRootsResponse0 valued) {
		    return (java.util.List)((AmiWebManagerListRootsResponse0)valued).getTrackedEvents();
	    }
    
	    @Override
	    public void setValue(AmiWebManagerListRootsResponse0 valued, Object value) {
		    valued.setTrackedEvents((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
		    dest.setTrackedEvents(source.getTrackedEvents());
	    }
	    
	    @Override
	    public boolean areEqual(AmiWebManagerListRootsResponse0 source, AmiWebManagerListRootsResponse0 dest) {
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
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTrackedEvents());
	        
	    }
	    @Override
	    public void append(AmiWebManagerListRootsResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTrackedEvents());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List trackedEvents";
	    }
	    @Override
	    public void clear(AmiWebManagerListRootsResponse0 valued){
	       valued.setTrackedEvents(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_trackedEvents=new VALUED_PARAM_CLASS_trackedEvents();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_exception, VALUED_PARAM_files, VALUED_PARAM_message, VALUED_PARAM_ok, VALUED_PARAM_priority, VALUED_PARAM_progress, VALUED_PARAM_ticket, VALUED_PARAM_trackedEvents, };



    private static final byte PIDS[]={ 44 ,1,43,42,57,53,55,58};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 44: return VALUED_PARAM_exception;
             case 1: return VALUED_PARAM_files;
             case 43: return VALUED_PARAM_message;
             case 42: return VALUED_PARAM_ok;
             case 57: return VALUED_PARAM_priority;
             case 53: return VALUED_PARAM_progress;
             case 55: return VALUED_PARAM_ticket;
             case 58: return VALUED_PARAM_trackedEvents;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 44: return true;
             case 1: return true;
             case 43: return true;
             case 42: return true;
             case 57: return true;
             case 53: return true;
             case 55: return true;
             case 58: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 44: return "exception";
             case 1: return "files";
             case 43: return "message";
             case 42: return "ok";
             case 57: return "priority";
             case 53: return "progress";
             case 55: return "ticket";
             case 58: return "trackedEvents";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 44: return 0;
             case 1: return 1;
             case 43: return 2;
             case 42: return 3;
             case 57: return 4;
             case 53: return 5;
             case 55: return 6;
             case 58: return 7;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="exception") return 44;
             if(name=="files") return 1;
             if(name=="message") return 43;
             if(name=="ok") return 42;
             if(name=="priority") return 57;
             if(name=="progress") return 53;
             if(name=="ticket") return 55;
             if(name=="trackedEvents") return 58;
            
             if("exception".equals(name)) return 44;
             if("files".equals(name)) return 1;
             if("message".equals(name)) return 43;
             if("ok".equals(name)) return 42;
             if("priority".equals(name)) return 57;
             if("progress".equals(name)) return 53;
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
             case 44: return  this._exception; 
             case 1: return  this._files; 
             case 43: return  this._message; 
             case 42: return  OH.valueOf(this._ok); 
             case 57: return  OH.valueOf(this._priority); 
             case 53: return  OH.valueOf(this._progress); 
             case 55: return  this._ticket; 
             case 58: return  this._trackedEvents; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 44: return java.lang.Exception.class;
             case 1: return java.util.List.class;
             case 43: return java.lang.String.class;
             case 42: return boolean.class;
             case 57: return int.class;
             case 53: return double.class;
             case 55: return java.lang.String.class;
             case 58: return java.util.List.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 44: return 53;
             case 1: return 21;
             case 43: return 20;
             case 42: return 0;
             case 57: return 4;
             case 53: return 7;
             case 55: return 20;
             case 58: return 21;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 44: this._exception=(java.lang.Exception)value;return;
             case 1: this._files=(java.util.List)value;return;
             case 43: this._message=(java.lang.String)value;return;
             case 42: this._ok=(java.lang.Boolean)value;return;
             case 57: this._priority=(java.lang.Integer)value;return;
             case 53: this._progress=(java.lang.Double)value;return;
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
             case 44: this._exception=(java.lang.Exception)value;return true;
             case 1: this._files=(java.util.List)value;return true;
             case 43: this._message=(java.lang.String)value;return true;
             case 42: this._ok=(java.lang.Boolean)value;return true;
             case 57: this._priority=(java.lang.Integer)value;return true;
             case 53: this._progress=(java.lang.Double)value;return true;
             case 55: this._ticket=(java.lang.String)value;return true;
             case 58: this._trackedEvents=(java.util.List)value;return true;
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
        
        case 1:
        
            this._files=(java.util.List)converter.read(session);
        
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
        
if(this._files!=null && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this._files,session);
        
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