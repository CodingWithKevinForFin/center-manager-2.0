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

public abstract class RequestMessage0 implements com.f1.container.RequestMessage ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,Cloneable{

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
    
    
    

    private com.f1.base.Action _action;

    private java.lang.Object _correlationId;

    private com.f1.container.ResultActionFuture _future;

    private boolean _posDup;

    private com.f1.container.OutputPort _resultPort;

    private static final String NAMES[]={ "action" ,"correlationId","future","posDup","resultPort"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 5;
        try{
        switch(h){

                case 0:

                    if(name == "resultPort" || name.equals("resultPort")) {this._resultPort=(com.f1.container.OutputPort)value;return;}
break;
                case 1:

                    if(name == "posDup" || name.equals("posDup")) {this._posDup=(java.lang.Boolean)value;return;}
break;
                case 2:

                    if(name == "correlationId" || name.equals("correlationId")) {this._correlationId=(java.lang.Object)value;return;}
break;
                case 3:

                    if(name == "action" || name.equals("action")) {this._action=(com.f1.base.Action)value;return;}
break;
                case 4:

                    if(name == "future" || name.equals("future")) {this._future=(com.f1.container.ResultActionFuture)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 0:

                    if(name == "resultPort" || name.equals("resultPort")) {this._resultPort=(com.f1.container.OutputPort)value;return true;}
break;
                case 1:

                    if(name == "posDup" || name.equals("posDup")) {this._posDup=(java.lang.Boolean)value;return true;}
break;
                case 2:

                    if(name == "correlationId" || name.equals("correlationId")) {this._correlationId=(java.lang.Object)value;return true;}
break;
                case 3:

                    if(name == "action" || name.equals("action")) {this._action=(com.f1.base.Action)value;return true;}
break;
                case 4:

                    if(name == "future" || name.equals("future")) {this._future=(com.f1.container.ResultActionFuture)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 0:

		    
                    if(name == "resultPort" || name.equals("resultPort")) {return this._resultPort;}
            
break;
                case 1:

		    
                    if(name == "posDup" || name.equals("posDup")) {return OH.valueOf(this._posDup);}
		    
break;
                case 2:

		    
                    if(name == "correlationId" || name.equals("correlationId")) {return this._correlationId;}
            
break;
                case 3:

		    
                    if(name == "action" || name.equals("action")) {return this._action;}
            
break;
                case 4:

		    
                    if(name == "future" || name.equals("future")) {return this._future;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 0:

                    if(name == "resultPort" || name.equals("resultPort")) {return com.f1.container.OutputPort.class;}
break;
                case 1:

                    if(name == "posDup" || name.equals("posDup")) {return boolean.class;}
break;
                case 2:

                    if(name == "correlationId" || name.equals("correlationId")) {return java.lang.Object.class;}
break;
                case 3:

                    if(name == "action" || name.equals("action")) {return com.f1.base.Action.class;}
break;
                case 4:

                    if(name == "future" || name.equals("future")) {return com.f1.container.ResultActionFuture.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 0:

                    if(name == "resultPort" || name.equals("resultPort")) {return VALUED_PARAM_resultPort;}
break;
                case 1:

                    if(name == "posDup" || name.equals("posDup")) {return VALUED_PARAM_posDup;}
break;
                case 2:

                    if(name == "correlationId" || name.equals("correlationId")) {return VALUED_PARAM_correlationId;}
break;
                case 3:

                    if(name == "action" || name.equals("action")) {return VALUED_PARAM_action;}
break;
                case 4:

                    if(name == "future" || name.equals("future")) {return VALUED_PARAM_future;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 0:

                    if(name == "resultPort" || name.equals("resultPort")) {return 4;}
break;
                case 1:

                    if(name == "posDup" || name.equals("posDup")) {return 3;}
break;
                case 2:

                    if(name == "correlationId" || name.equals("correlationId")) {return 1;}
break;
                case 3:

                    if(name == "action" || name.equals("action")) {return 0;}
break;
                case 4:

                    if(name == "future" || name.equals("future")) {return 2;}
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
        return 5;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)RequestMessage0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 0:

                    if(name == "resultPort" || name.equals("resultPort")) {return true;}
break;
                case 1:

                    if(name == "posDup" || name.equals("posDup")) {return true;}
break;
                case 2:

                    if(name == "correlationId" || name.equals("correlationId")) {return true;}
break;
                case 3:

                    if(name == "action" || name.equals("action")) {return true;}
break;
                case 4:

                    if(name == "future" || name.equals("future")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 0:

                    if(name == "resultPort" || name.equals("resultPort")) {return 60;}
break;
                case 1:

                    if(name == "posDup" || name.equals("posDup")) {return 0;}
break;
                case 2:

                    if(name == "correlationId" || name.equals("correlationId")) {return 18;}
break;
                case 3:

                    if(name == "action" || name.equals("action")) {return 60;}
break;
                case 4:

                    if(name == "future" || name.equals("future")) {return 60;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _action;

        case 1:return _correlationId;

        case 2:return _future;

        case 3:return _posDup;

        case 4:return _resultPort;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 5);
    }

    public com.f1.base.Action getAction(){
        return this._action;
    }
    public void setAction(com.f1.base.Action _action){
    
        this._action=_action;
    }

    public java.lang.Object getCorrelationId(){
        return this._correlationId;
    }
    public void setCorrelationId(java.lang.Object _correlationId){
    
        this._correlationId=_correlationId;
    }

    public com.f1.container.ResultActionFuture getFuture(){
        return this._future;
    }
    public void setFuture(com.f1.container.ResultActionFuture _future){
    
        this._future=_future;
    }

    public boolean getPosDup(){
        return this._posDup;
    }
    public void setPosDup(boolean _posDup){
    
        this._posDup=_posDup;
    }

    public com.f1.container.OutputPort getResultPort(){
        return this._resultPort;
    }
    public void setResultPort(com.f1.container.OutputPort _resultPort){
    
        this._resultPort=_resultPort;
    }





  
    private static final class VALUED_PARAM_CLASS_action extends AbstractValuedParam<RequestMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 60;
	    }
	    
	    @Override
	    public void write(RequestMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.base.Action}");
		    
	    }
	    
	    @Override
	    public void read(RequestMessage0 valued, DataInput stream) throws IOException{
		    
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
            return "action";
	    }
    
	    @Override
	    public Object getValue(RequestMessage0 valued) {
		    return (com.f1.base.Action)((RequestMessage0)valued).getAction();
	    }
    
	    @Override
	    public void setValue(RequestMessage0 valued, Object value) {
		    valued.setAction((com.f1.base.Action)value);
	    }
    
	    @Override
	    public void copy(RequestMessage0 source, RequestMessage0 dest) {
		    dest.setAction(source.getAction());
	    }
	    
	    @Override
	    public boolean areEqual(RequestMessage0 source, RequestMessage0 dest) {
	        return OH.eq(dest.getAction(),source.getAction());
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
	    public void append(RequestMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAction());
	        
	    }
	    @Override
	    public void append(RequestMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAction());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.base.Action action";
	    }
	    @Override
	    public void clear(RequestMessage0 valued){
	       valued.setAction(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_action=new VALUED_PARAM_CLASS_action();
  

  
    private static final class VALUED_PARAM_CLASS_correlationId extends AbstractValuedParam<RequestMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 18;
	    }
	    
	    @Override
	    public void write(RequestMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Object}");
		    
	    }
	    
	    @Override
	    public void read(RequestMessage0 valued, DataInput stream) throws IOException{
		    
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
            return "correlationId";
	    }
    
	    @Override
	    public Object getValue(RequestMessage0 valued) {
		    return (java.lang.Object)((RequestMessage0)valued).getCorrelationId();
	    }
    
	    @Override
	    public void setValue(RequestMessage0 valued, Object value) {
		    valued.setCorrelationId((java.lang.Object)value);
	    }
    
	    @Override
	    public void copy(RequestMessage0 source, RequestMessage0 dest) {
		    dest.setCorrelationId(source.getCorrelationId());
	    }
	    
	    @Override
	    public boolean areEqual(RequestMessage0 source, RequestMessage0 dest) {
	        return OH.eq(dest.getCorrelationId(),source.getCorrelationId());
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
	    public void append(RequestMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getCorrelationId());
	        
	    }
	    @Override
	    public void append(RequestMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getCorrelationId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Object correlationId";
	    }
	    @Override
	    public void clear(RequestMessage0 valued){
	       valued.setCorrelationId(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_correlationId=new VALUED_PARAM_CLASS_correlationId();
  

  
    private static final class VALUED_PARAM_CLASS_future extends AbstractValuedParam<RequestMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 60;
	    }
	    
	    @Override
	    public void write(RequestMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.container.ResultActionFuture}");
		    
	    }
	    
	    @Override
	    public void read(RequestMessage0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.container.ResultActionFuture}");
		    
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
		    return 2;
	    }
    
	    @Override
	    public byte getPid() {
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "future";
	    }
    
	    @Override
	    public Object getValue(RequestMessage0 valued) {
		    return (com.f1.container.ResultActionFuture)((RequestMessage0)valued).getFuture();
	    }
    
	    @Override
	    public void setValue(RequestMessage0 valued, Object value) {
		    valued.setFuture((com.f1.container.ResultActionFuture)value);
	    }
    
	    @Override
	    public void copy(RequestMessage0 source, RequestMessage0 dest) {
		    dest.setFuture(source.getFuture());
	    }
	    
	    @Override
	    public boolean areEqual(RequestMessage0 source, RequestMessage0 dest) {
	        return OH.eq(dest.getFuture(),source.getFuture());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.container.ResultActionFuture.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.container.ResultActionFuture.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(RequestMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getFuture());
	        
	    }
	    @Override
	    public void append(RequestMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getFuture());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.container.ResultActionFuture future";
	    }
	    @Override
	    public void clear(RequestMessage0 valued){
	       valued.setFuture(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_future=new VALUED_PARAM_CLASS_future();
  

  
    private static final class VALUED_PARAM_CLASS_posDup extends AbstractValuedParam<RequestMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(RequestMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getPosDup());
		    
	    }
	    
	    @Override
	    public void read(RequestMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setPosDup(stream.readBoolean());
		    
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
		    return 3;
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "posDup";
	    }
    
	    @Override
	    public Object getValue(RequestMessage0 valued) {
		    return (boolean)((RequestMessage0)valued).getPosDup();
	    }
    
	    @Override
	    public void setValue(RequestMessage0 valued, Object value) {
		    valued.setPosDup((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(RequestMessage0 source, RequestMessage0 dest) {
		    dest.setPosDup(source.getPosDup());
	    }
	    
	    @Override
	    public boolean areEqual(RequestMessage0 source, RequestMessage0 dest) {
	        return OH.eq(dest.getPosDup(),source.getPosDup());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(RequestMessage0 valued) {
		    return valued.getPosDup();
	    }
    
	    @Override
	    public void setBoolean(RequestMessage0 valued, boolean value) {
		    valued.setPosDup(value);
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
	    public void append(RequestMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPosDup());
	        
	    }
	    @Override
	    public void append(RequestMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPosDup());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean posDup";
	    }
	    @Override
	    public void clear(RequestMessage0 valued){
	       valued.setPosDup(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_posDup=new VALUED_PARAM_CLASS_posDup();
  

  
    private static final class VALUED_PARAM_CLASS_resultPort extends AbstractValuedParam<RequestMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 60;
	    }
	    
	    @Override
	    public void write(RequestMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.container.OutputPort}");
		    
	    }
	    
	    @Override
	    public void read(RequestMessage0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.container.OutputPort}");
		    
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
		    return 4;
	    }
    
	    @Override
	    public byte getPid() {
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "resultPort";
	    }
    
	    @Override
	    public Object getValue(RequestMessage0 valued) {
		    return (com.f1.container.OutputPort)((RequestMessage0)valued).getResultPort();
	    }
    
	    @Override
	    public void setValue(RequestMessage0 valued, Object value) {
		    valued.setResultPort((com.f1.container.OutputPort)value);
	    }
    
	    @Override
	    public void copy(RequestMessage0 source, RequestMessage0 dest) {
		    dest.setResultPort(source.getResultPort());
	    }
	    
	    @Override
	    public boolean areEqual(RequestMessage0 source, RequestMessage0 dest) {
	        return OH.eq(dest.getResultPort(),source.getResultPort());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.container.OutputPort.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.container.OutputPort.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(RequestMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getResultPort());
	        
	    }
	    @Override
	    public void append(RequestMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getResultPort());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.container.OutputPort resultPort";
	    }
	    @Override
	    public void clear(RequestMessage0 valued){
	       valued.setResultPort(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_resultPort=new VALUED_PARAM_CLASS_resultPort();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_action, VALUED_PARAM_correlationId, VALUED_PARAM_future, VALUED_PARAM_posDup, VALUED_PARAM_resultPort, };


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