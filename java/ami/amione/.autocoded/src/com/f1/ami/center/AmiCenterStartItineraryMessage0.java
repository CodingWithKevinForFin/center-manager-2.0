//Coded by ValuedCodeTemplate
package com.f1.ami.center;

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

public abstract class AmiCenterStartItineraryMessage0 implements com.f1.ami.center.AmiCenterStartItineraryMessage ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,Cloneable{

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
    
    
    

    private com.f1.container.RequestMessage _initialRequest;

    private com.f1.ami.center.AmiCenterItinerary _itinerary;

    private int _priority;

    private static final String NAMES[]={ "initialRequest" ,"itinerary","priority"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 3;
        try{
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return;}
break;
                case 1:

                    if(name == "itinerary" || name.equals("itinerary")) {this._itinerary=(com.f1.ami.center.AmiCenterItinerary)value;return;}
break;
                case 2:

                    if(name == "initialRequest" || name.equals("initialRequest")) {this._initialRequest=(com.f1.container.RequestMessage)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return true;}
break;
                case 1:

                    if(name == "itinerary" || name.equals("itinerary")) {this._itinerary=(com.f1.ami.center.AmiCenterItinerary)value;return true;}
break;
                case 2:

                    if(name == "initialRequest" || name.equals("initialRequest")) {this._initialRequest=(com.f1.container.RequestMessage)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

		    
                    if(name == "priority" || name.equals("priority")) {return OH.valueOf(this._priority);}
		    
break;
                case 1:

		    
                    if(name == "itinerary" || name.equals("itinerary")) {return this._itinerary;}
            
break;
                case 2:

		    
                    if(name == "initialRequest" || name.equals("initialRequest")) {return this._initialRequest;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return int.class;}
break;
                case 1:

                    if(name == "itinerary" || name.equals("itinerary")) {return com.f1.ami.center.AmiCenterItinerary.class;}
break;
                case 2:

                    if(name == "initialRequest" || name.equals("initialRequest")) {return com.f1.container.RequestMessage.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return VALUED_PARAM_priority;}
break;
                case 1:

                    if(name == "itinerary" || name.equals("itinerary")) {return VALUED_PARAM_itinerary;}
break;
                case 2:

                    if(name == "initialRequest" || name.equals("initialRequest")) {return VALUED_PARAM_initialRequest;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return 2;}
break;
                case 1:

                    if(name == "itinerary" || name.equals("itinerary")) {return 1;}
break;
                case 2:

                    if(name == "initialRequest" || name.equals("initialRequest")) {return 0;}
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
        return 3;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiCenterStartItineraryMessage0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return true;}
break;
                case 1:

                    if(name == "itinerary" || name.equals("itinerary")) {return true;}
break;
                case 2:

                    if(name == "initialRequest" || name.equals("initialRequest")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "priority" || name.equals("priority")) {return 4;}
break;
                case 1:

                    if(name == "itinerary" || name.equals("itinerary")) {return 60;}
break;
                case 2:

                    if(name == "initialRequest" || name.equals("initialRequest")) {return 41;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _initialRequest;

        case 1:return _itinerary;

        case 2:return _priority;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 3);
    }

    public com.f1.container.RequestMessage getInitialRequest(){
        return this._initialRequest;
    }
    public void setInitialRequest(com.f1.container.RequestMessage _initialRequest){
    
        this._initialRequest=_initialRequest;
    }

    public com.f1.ami.center.AmiCenterItinerary getItinerary(){
        return this._itinerary;
    }
    public void setItinerary(com.f1.ami.center.AmiCenterItinerary _itinerary){
    
        this._itinerary=_itinerary;
    }

    public int getPriority(){
        return this._priority;
    }
    public void setPriority(int _priority){
    
        this._priority=_priority;
    }





  
    private static final class VALUED_PARAM_CLASS_initialRequest extends AbstractValuedParam<AmiCenterStartItineraryMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 41;
	    }
	    
	    @Override
	    public void write(AmiCenterStartItineraryMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.container.RequestMessage}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterStartItineraryMessage0 valued, DataInput stream) throws IOException{
		    
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
            return "initialRequest";
	    }
    
	    @Override
	    public Object getValue(AmiCenterStartItineraryMessage0 valued) {
		    return (com.f1.container.RequestMessage)((AmiCenterStartItineraryMessage0)valued).getInitialRequest();
	    }
    
	    @Override
	    public void setValue(AmiCenterStartItineraryMessage0 valued, Object value) {
		    valued.setInitialRequest((com.f1.container.RequestMessage)value);
	    }
    
	    @Override
	    public void copy(AmiCenterStartItineraryMessage0 source, AmiCenterStartItineraryMessage0 dest) {
		    dest.setInitialRequest(source.getInitialRequest());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterStartItineraryMessage0 source, AmiCenterStartItineraryMessage0 dest) {
	        return OH.eq(dest.getInitialRequest(),source.getInitialRequest());
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
	    public void append(AmiCenterStartItineraryMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getInitialRequest());
	        
	    }
	    @Override
	    public void append(AmiCenterStartItineraryMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getInitialRequest());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.container.RequestMessage initialRequest";
	    }
	    @Override
	    public void clear(AmiCenterStartItineraryMessage0 valued){
	       valued.setInitialRequest(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_initialRequest=new VALUED_PARAM_CLASS_initialRequest();
  

  
    private static final class VALUED_PARAM_CLASS_itinerary extends AbstractValuedParam<AmiCenterStartItineraryMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 60;
	    }
	    
	    @Override
	    public void write(AmiCenterStartItineraryMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.ami.center.AmiCenterItinerary}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterStartItineraryMessage0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.ami.center.AmiCenterItinerary}");
		    
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
            return "itinerary";
	    }
    
	    @Override
	    public Object getValue(AmiCenterStartItineraryMessage0 valued) {
		    return (com.f1.ami.center.AmiCenterItinerary)((AmiCenterStartItineraryMessage0)valued).getItinerary();
	    }
    
	    @Override
	    public void setValue(AmiCenterStartItineraryMessage0 valued, Object value) {
		    valued.setItinerary((com.f1.ami.center.AmiCenterItinerary)value);
	    }
    
	    @Override
	    public void copy(AmiCenterStartItineraryMessage0 source, AmiCenterStartItineraryMessage0 dest) {
		    dest.setItinerary(source.getItinerary());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterStartItineraryMessage0 source, AmiCenterStartItineraryMessage0 dest) {
	        return OH.eq(dest.getItinerary(),source.getItinerary());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.ami.center.AmiCenterItinerary.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.ami.center.AmiCenterItinerary.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiCenterStartItineraryMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getItinerary());
	        
	    }
	    @Override
	    public void append(AmiCenterStartItineraryMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getItinerary());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.ami.center.AmiCenterItinerary itinerary";
	    }
	    @Override
	    public void clear(AmiCenterStartItineraryMessage0 valued){
	       valued.setItinerary(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_itinerary=new VALUED_PARAM_CLASS_itinerary();
  

  
    private static final class VALUED_PARAM_CLASS_priority extends AbstractValuedParam<AmiCenterStartItineraryMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterStartItineraryMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getPriority());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterStartItineraryMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 2;
	    }
    
	    @Override
	    public byte getPid() {
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "priority";
	    }
    
	    @Override
	    public Object getValue(AmiCenterStartItineraryMessage0 valued) {
		    return (int)((AmiCenterStartItineraryMessage0)valued).getPriority();
	    }
    
	    @Override
	    public void setValue(AmiCenterStartItineraryMessage0 valued, Object value) {
		    valued.setPriority((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterStartItineraryMessage0 source, AmiCenterStartItineraryMessage0 dest) {
		    dest.setPriority(source.getPriority());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterStartItineraryMessage0 source, AmiCenterStartItineraryMessage0 dest) {
	        return OH.eq(dest.getPriority(),source.getPriority());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterStartItineraryMessage0 valued) {
		    return valued.getPriority();
	    }
    
	    @Override
	    public void setInt(AmiCenterStartItineraryMessage0 valued, int value) {
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
	    public void append(AmiCenterStartItineraryMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public void append(AmiCenterStartItineraryMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int priority";
	    }
	    @Override
	    public void clear(AmiCenterStartItineraryMessage0 valued){
	       valued.setPriority(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_priority=new VALUED_PARAM_CLASS_priority();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_initialRequest, VALUED_PARAM_itinerary, VALUED_PARAM_priority, };


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