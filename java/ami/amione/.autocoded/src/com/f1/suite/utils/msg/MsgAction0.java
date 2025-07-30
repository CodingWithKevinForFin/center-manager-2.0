//Coded by ValuedCodeTemplate
package com.f1.suite.utils.msg;

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

public abstract class MsgAction0 implements com.f1.suite.utils.msg.MsgAction ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,Cloneable{

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
    
    
    

    private com.f1.msg.MsgEvent _msgEvent;

    private java.lang.String _source;

    private java.lang.String _topic;

    private static final String NAMES[]={ "msgEvent" ,"source","topic"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 3;
        try{
        switch(h){

                case 0:

                    if(name == "topic" || name.equals("topic")) {this._topic=(java.lang.String)value;return;}
break;
                case 1:

                    if(name == "source" || name.equals("source")) {this._source=(java.lang.String)value;return;}
break;
                case 2:

                    if(name == "msgEvent" || name.equals("msgEvent")) {this._msgEvent=(com.f1.msg.MsgEvent)value;return;}
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

                    if(name == "topic" || name.equals("topic")) {this._topic=(java.lang.String)value;return true;}
break;
                case 1:

                    if(name == "source" || name.equals("source")) {this._source=(java.lang.String)value;return true;}
break;
                case 2:

                    if(name == "msgEvent" || name.equals("msgEvent")) {this._msgEvent=(com.f1.msg.MsgEvent)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

		    
                    if(name == "topic" || name.equals("topic")) {return this._topic;}
            
break;
                case 1:

		    
                    if(name == "source" || name.equals("source")) {return this._source;}
            
break;
                case 2:

		    
                    if(name == "msgEvent" || name.equals("msgEvent")) {return this._msgEvent;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "topic" || name.equals("topic")) {return java.lang.String.class;}
break;
                case 1:

                    if(name == "source" || name.equals("source")) {return java.lang.String.class;}
break;
                case 2:

                    if(name == "msgEvent" || name.equals("msgEvent")) {return com.f1.msg.MsgEvent.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "topic" || name.equals("topic")) {return VALUED_PARAM_topic;}
break;
                case 1:

                    if(name == "source" || name.equals("source")) {return VALUED_PARAM_source;}
break;
                case 2:

                    if(name == "msgEvent" || name.equals("msgEvent")) {return VALUED_PARAM_msgEvent;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "topic" || name.equals("topic")) {return 2;}
break;
                case 1:

                    if(name == "source" || name.equals("source")) {return 1;}
break;
                case 2:

                    if(name == "msgEvent" || name.equals("msgEvent")) {return 0;}
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
	    return (Class)MsgAction0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "topic" || name.equals("topic")) {return true;}
break;
                case 1:

                    if(name == "source" || name.equals("source")) {return true;}
break;
                case 2:

                    if(name == "msgEvent" || name.equals("msgEvent")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "topic" || name.equals("topic")) {return 20;}
break;
                case 1:

                    if(name == "source" || name.equals("source")) {return 20;}
break;
                case 2:

                    if(name == "msgEvent" || name.equals("msgEvent")) {return 60;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _msgEvent;

        case 1:return _source;

        case 2:return _topic;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 3);
    }

    public com.f1.msg.MsgEvent getMsgEvent(){
        return this._msgEvent;
    }
    public void setMsgEvent(com.f1.msg.MsgEvent _msgEvent){
    
        this._msgEvent=_msgEvent;
    }

    public java.lang.String getSource(){
        return this._source;
    }
    public void setSource(java.lang.String _source){
    
        this._source=_source;
    }

    public java.lang.String getTopic(){
        return this._topic;
    }
    public void setTopic(java.lang.String _topic){
    
        this._topic=_topic;
    }





  
    private static final class VALUED_PARAM_CLASS_msgEvent extends AbstractValuedParam<MsgAction0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 60;
	    }
	    
	    @Override
	    public void write(MsgAction0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.msg.MsgEvent}");
		    
	    }
	    
	    @Override
	    public void read(MsgAction0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.msg.MsgEvent}");
		    
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "msgEvent";
	    }
    
	    @Override
	    public Object getValue(MsgAction0 valued) {
		    return (com.f1.msg.MsgEvent)((MsgAction0)valued).getMsgEvent();
	    }
    
	    @Override
	    public void setValue(MsgAction0 valued, Object value) {
		    valued.setMsgEvent((com.f1.msg.MsgEvent)value);
	    }
    
	    @Override
	    public void copy(MsgAction0 source, MsgAction0 dest) {
		    dest.setMsgEvent(source.getMsgEvent());
	    }
	    
	    @Override
	    public boolean areEqual(MsgAction0 source, MsgAction0 dest) {
	        return OH.eq(dest.getMsgEvent(),source.getMsgEvent());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.msg.MsgEvent.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.msg.MsgEvent.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(MsgAction0 valued, StringBuilder sb){
	        
	        sb.append(valued.getMsgEvent());
	        
	    }
	    @Override
	    public void append(MsgAction0 valued, StringBuildable sb){
	        
	        sb.append(valued.getMsgEvent());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.msg.MsgEvent msgEvent";
	    }
	    @Override
	    public void clear(MsgAction0 valued){
	       valued.setMsgEvent(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_msgEvent=new VALUED_PARAM_CLASS_msgEvent();
  

  
    private static final class VALUED_PARAM_CLASS_source extends AbstractValuedParam<MsgAction0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(MsgAction0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(MsgAction0 valued, DataInput stream) throws IOException{
		    
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "source";
	    }
    
	    @Override
	    public Object getValue(MsgAction0 valued) {
		    return (java.lang.String)((MsgAction0)valued).getSource();
	    }
    
	    @Override
	    public void setValue(MsgAction0 valued, Object value) {
		    valued.setSource((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(MsgAction0 source, MsgAction0 dest) {
		    dest.setSource(source.getSource());
	    }
	    
	    @Override
	    public boolean areEqual(MsgAction0 source, MsgAction0 dest) {
	        return OH.eq(dest.getSource(),source.getSource());
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
	    public void append(MsgAction0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getSource(),sb);
	        
	    }
	    @Override
	    public void append(MsgAction0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getSource(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String source";
	    }
	    @Override
	    public void clear(MsgAction0 valued){
	       valued.setSource(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_source=new VALUED_PARAM_CLASS_source();
  

  
    private static final class VALUED_PARAM_CLASS_topic extends AbstractValuedParam<MsgAction0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(MsgAction0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(MsgAction0 valued, DataInput stream) throws IOException{
		    
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "topic";
	    }
    
	    @Override
	    public Object getValue(MsgAction0 valued) {
		    return (java.lang.String)((MsgAction0)valued).getTopic();
	    }
    
	    @Override
	    public void setValue(MsgAction0 valued, Object value) {
		    valued.setTopic((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(MsgAction0 source, MsgAction0 dest) {
		    dest.setTopic(source.getTopic());
	    }
	    
	    @Override
	    public boolean areEqual(MsgAction0 source, MsgAction0 dest) {
	        return OH.eq(dest.getTopic(),source.getTopic());
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
	    public void append(MsgAction0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getTopic(),sb);
	        
	    }
	    @Override
	    public void append(MsgAction0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getTopic(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String topic";
	    }
	    @Override
	    public void clear(MsgAction0 valued){
	       valued.setTopic(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_topic=new VALUED_PARAM_CLASS_topic();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_msgEvent, VALUED_PARAM_source, VALUED_PARAM_topic, };


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