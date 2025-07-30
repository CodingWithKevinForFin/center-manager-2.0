//Coded by ValuedCodeTemplate
package com.f1.ami.relay;

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

public abstract class StartAmiRelayItineraryMessage0 implements com.f1.ami.relay.StartAmiRelayItineraryMessage ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,Cloneable{

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

    private boolean _isContinue;

    private com.f1.ami.relay.AmiRelayItinerary _itinerary;

    private static final String NAMES[]={ "initialRequest" ,"isContinue","itinerary"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 5;
        try{
        switch(h){

                case 1:

                    if(name == "initialRequest" || name.equals("initialRequest")) {this._initialRequest=(com.f1.container.RequestMessage)value;return;}
break;
                case 2:

                    if(name == "itinerary" || name.equals("itinerary")) {this._itinerary=(com.f1.ami.relay.AmiRelayItinerary)value;return;}
break;
                case 3:

                    if(name == "isContinue" || name.equals("isContinue")) {this._isContinue=(java.lang.Boolean)value;return;}
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

                case 1:

                    if(name == "initialRequest" || name.equals("initialRequest")) {this._initialRequest=(com.f1.container.RequestMessage)value;return true;}
break;
                case 2:

                    if(name == "itinerary" || name.equals("itinerary")) {this._itinerary=(com.f1.ami.relay.AmiRelayItinerary)value;return true;}
break;
                case 3:

                    if(name == "isContinue" || name.equals("isContinue")) {this._isContinue=(java.lang.Boolean)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 1:

		    
                    if(name == "initialRequest" || name.equals("initialRequest")) {return this._initialRequest;}
            
break;
                case 2:

		    
                    if(name == "itinerary" || name.equals("itinerary")) {return this._itinerary;}
            
break;
                case 3:

		    
                    if(name == "isContinue" || name.equals("isContinue")) {return OH.valueOf(this._isContinue);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 1:

                    if(name == "initialRequest" || name.equals("initialRequest")) {return com.f1.container.RequestMessage.class;}
break;
                case 2:

                    if(name == "itinerary" || name.equals("itinerary")) {return com.f1.ami.relay.AmiRelayItinerary.class;}
break;
                case 3:

                    if(name == "isContinue" || name.equals("isContinue")) {return boolean.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 1:

                    if(name == "initialRequest" || name.equals("initialRequest")) {return VALUED_PARAM_initialRequest;}
break;
                case 2:

                    if(name == "itinerary" || name.equals("itinerary")) {return VALUED_PARAM_itinerary;}
break;
                case 3:

                    if(name == "isContinue" || name.equals("isContinue")) {return VALUED_PARAM_isContinue;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 1:

                    if(name == "initialRequest" || name.equals("initialRequest")) {return 0;}
break;
                case 2:

                    if(name == "itinerary" || name.equals("itinerary")) {return 2;}
break;
                case 3:

                    if(name == "isContinue" || name.equals("isContinue")) {return 1;}
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
	    return (Class)StartAmiRelayItineraryMessage0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 1:

                    if(name == "initialRequest" || name.equals("initialRequest")) {return true;}
break;
                case 2:

                    if(name == "itinerary" || name.equals("itinerary")) {return true;}
break;
                case 3:

                    if(name == "isContinue" || name.equals("isContinue")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 1:

                    if(name == "initialRequest" || name.equals("initialRequest")) {return 41;}
break;
                case 2:

                    if(name == "itinerary" || name.equals("itinerary")) {return 60;}
break;
                case 3:

                    if(name == "isContinue" || name.equals("isContinue")) {return 0;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _initialRequest;

        case 1:return _isContinue;

        case 2:return _itinerary;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 3);
    }

    public com.f1.container.RequestMessage getInitialRequest(){
        return this._initialRequest;
    }
    public void setInitialRequest(com.f1.container.RequestMessage _initialRequest){
    
        this._initialRequest=_initialRequest;
    }

    public boolean getIsContinue(){
        return this._isContinue;
    }
    public void setIsContinue(boolean _isContinue){
    
        this._isContinue=_isContinue;
    }

    public com.f1.ami.relay.AmiRelayItinerary getItinerary(){
        return this._itinerary;
    }
    public void setItinerary(com.f1.ami.relay.AmiRelayItinerary _itinerary){
    
        this._itinerary=_itinerary;
    }





  
    private static final class VALUED_PARAM_CLASS_initialRequest extends AbstractValuedParam<StartAmiRelayItineraryMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 41;
	    }
	    
	    @Override
	    public void write(StartAmiRelayItineraryMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.container.RequestMessage}");
		    
	    }
	    
	    @Override
	    public void read(StartAmiRelayItineraryMessage0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(StartAmiRelayItineraryMessage0 valued) {
		    return (com.f1.container.RequestMessage)((StartAmiRelayItineraryMessage0)valued).getInitialRequest();
	    }
    
	    @Override
	    public void setValue(StartAmiRelayItineraryMessage0 valued, Object value) {
		    valued.setInitialRequest((com.f1.container.RequestMessage)value);
	    }
    
	    @Override
	    public void copy(StartAmiRelayItineraryMessage0 source, StartAmiRelayItineraryMessage0 dest) {
		    dest.setInitialRequest(source.getInitialRequest());
	    }
	    
	    @Override
	    public boolean areEqual(StartAmiRelayItineraryMessage0 source, StartAmiRelayItineraryMessage0 dest) {
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
	    public void append(StartAmiRelayItineraryMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getInitialRequest());
	        
	    }
	    @Override
	    public void append(StartAmiRelayItineraryMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getInitialRequest());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.container.RequestMessage initialRequest";
	    }
	    @Override
	    public void clear(StartAmiRelayItineraryMessage0 valued){
	       valued.setInitialRequest(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_initialRequest=new VALUED_PARAM_CLASS_initialRequest();
  

  
    private static final class VALUED_PARAM_CLASS_isContinue extends AbstractValuedParam<StartAmiRelayItineraryMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(StartAmiRelayItineraryMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getIsContinue());
		    
	    }
	    
	    @Override
	    public void read(StartAmiRelayItineraryMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setIsContinue(stream.readBoolean());
		    
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "isContinue";
	    }
    
	    @Override
	    public Object getValue(StartAmiRelayItineraryMessage0 valued) {
		    return (boolean)((StartAmiRelayItineraryMessage0)valued).getIsContinue();
	    }
    
	    @Override
	    public void setValue(StartAmiRelayItineraryMessage0 valued, Object value) {
		    valued.setIsContinue((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(StartAmiRelayItineraryMessage0 source, StartAmiRelayItineraryMessage0 dest) {
		    dest.setIsContinue(source.getIsContinue());
	    }
	    
	    @Override
	    public boolean areEqual(StartAmiRelayItineraryMessage0 source, StartAmiRelayItineraryMessage0 dest) {
	        return OH.eq(dest.getIsContinue(),source.getIsContinue());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(StartAmiRelayItineraryMessage0 valued) {
		    return valued.getIsContinue();
	    }
    
	    @Override
	    public void setBoolean(StartAmiRelayItineraryMessage0 valued, boolean value) {
		    valued.setIsContinue(value);
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
	    public void append(StartAmiRelayItineraryMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getIsContinue());
	        
	    }
	    @Override
	    public void append(StartAmiRelayItineraryMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getIsContinue());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean isContinue";
	    }
	    @Override
	    public void clear(StartAmiRelayItineraryMessage0 valued){
	       valued.setIsContinue(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_isContinue=new VALUED_PARAM_CLASS_isContinue();
  

  
    private static final class VALUED_PARAM_CLASS_itinerary extends AbstractValuedParam<StartAmiRelayItineraryMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 60;
	    }
	    
	    @Override
	    public void write(StartAmiRelayItineraryMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.ami.relay.AmiRelayItinerary}");
		    
	    }
	    
	    @Override
	    public void read(StartAmiRelayItineraryMessage0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.ami.relay.AmiRelayItinerary}");
		    
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "itinerary";
	    }
    
	    @Override
	    public Object getValue(StartAmiRelayItineraryMessage0 valued) {
		    return (com.f1.ami.relay.AmiRelayItinerary)((StartAmiRelayItineraryMessage0)valued).getItinerary();
	    }
    
	    @Override
	    public void setValue(StartAmiRelayItineraryMessage0 valued, Object value) {
		    valued.setItinerary((com.f1.ami.relay.AmiRelayItinerary)value);
	    }
    
	    @Override
	    public void copy(StartAmiRelayItineraryMessage0 source, StartAmiRelayItineraryMessage0 dest) {
		    dest.setItinerary(source.getItinerary());
	    }
	    
	    @Override
	    public boolean areEqual(StartAmiRelayItineraryMessage0 source, StartAmiRelayItineraryMessage0 dest) {
	        return OH.eq(dest.getItinerary(),source.getItinerary());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.ami.relay.AmiRelayItinerary.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.ami.relay.AmiRelayItinerary.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(StartAmiRelayItineraryMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getItinerary());
	        
	    }
	    @Override
	    public void append(StartAmiRelayItineraryMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getItinerary());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.ami.relay.AmiRelayItinerary itinerary";
	    }
	    @Override
	    public void clear(StartAmiRelayItineraryMessage0 valued){
	       valued.setItinerary(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_itinerary=new VALUED_PARAM_CLASS_itinerary();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_initialRequest, VALUED_PARAM_isContinue, VALUED_PARAM_itinerary, };


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