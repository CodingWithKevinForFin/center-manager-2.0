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

public abstract class AmiCenterSchemaTypeRecord0 implements com.f1.ami.amicommon.msg.AmiCenterSchemaTypeRecord ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private long _count;

    private short _objectType;

    private static final String NAMES[]={ "count" ,"objectType"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 3;
        try{
        switch(h){

                case 0:

                    if(name == "objectType" || name.equals("objectType")) {this._objectType=(java.lang.Short)value;return;}
break;
                case 1:

                    if(name == "count" || name.equals("count")) {this._count=(java.lang.Long)value;return;}
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

                    if(name == "objectType" || name.equals("objectType")) {this._objectType=(java.lang.Short)value;return true;}
break;
                case 1:

                    if(name == "count" || name.equals("count")) {this._count=(java.lang.Long)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

		    
                    if(name == "objectType" || name.equals("objectType")) {return OH.valueOf(this._objectType);}
		    
break;
                case 1:

		    
                    if(name == "count" || name.equals("count")) {return OH.valueOf(this._count);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "objectType" || name.equals("objectType")) {return short.class;}
break;
                case 1:

                    if(name == "count" || name.equals("count")) {return long.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "objectType" || name.equals("objectType")) {return VALUED_PARAM_objectType;}
break;
                case 1:

                    if(name == "count" || name.equals("count")) {return VALUED_PARAM_count;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "objectType" || name.equals("objectType")) {return 1;}
break;
                case 1:

                    if(name == "count" || name.equals("count")) {return 0;}
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
        return 2;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiCenterSchemaTypeRecord0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "objectType" || name.equals("objectType")) {return true;}
break;
                case 1:

                    if(name == "count" || name.equals("count")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 3;
        switch(h){

                case 0:

                    if(name == "objectType" || name.equals("objectType")) {return 2;}
break;
                case 1:

                    if(name == "count" || name.equals("count")) {return 6;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _count;

        case 1:return _objectType;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 2);
    }

    public long getCount(){
        return this._count;
    }
    public void setCount(long _count){
    
        this._count=_count;
    }

    public short getObjectType(){
        return this._objectType;
    }
    public void setObjectType(short _objectType){
    
        this._objectType=_objectType;
    }





  
    private static final class VALUED_PARAM_CLASS_count extends AbstractValuedParam<AmiCenterSchemaTypeRecord0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterSchemaTypeRecord0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getCount());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterSchemaTypeRecord0 valued, DataInput stream) throws IOException{
		    
		      valued.setCount(stream.readLong());
		    
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
            return "count";
	    }
    
	    @Override
	    public Object getValue(AmiCenterSchemaTypeRecord0 valued) {
		    return (long)((AmiCenterSchemaTypeRecord0)valued).getCount();
	    }
    
	    @Override
	    public void setValue(AmiCenterSchemaTypeRecord0 valued, Object value) {
		    valued.setCount((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterSchemaTypeRecord0 source, AmiCenterSchemaTypeRecord0 dest) {
		    dest.setCount(source.getCount());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterSchemaTypeRecord0 source, AmiCenterSchemaTypeRecord0 dest) {
	        return OH.eq(dest.getCount(),source.getCount());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterSchemaTypeRecord0 valued) {
		    return valued.getCount();
	    }
    
	    @Override
	    public void setLong(AmiCenterSchemaTypeRecord0 valued, long value) {
		    valued.setCount(value);
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
	    public void append(AmiCenterSchemaTypeRecord0 valued, StringBuilder sb){
	        
	        sb.append(valued.getCount());
	        
	    }
	    @Override
	    public void append(AmiCenterSchemaTypeRecord0 valued, StringBuildable sb){
	        
	        sb.append(valued.getCount());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long count";
	    }
	    @Override
	    public void clear(AmiCenterSchemaTypeRecord0 valued){
	       valued.setCount(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_count=new VALUED_PARAM_CLASS_count();
  

  
    private static final class VALUED_PARAM_CLASS_objectType extends AbstractValuedParam<AmiCenterSchemaTypeRecord0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 2;
	    }
	    
	    @Override
	    public void write(AmiCenterSchemaTypeRecord0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeShort(valued.getObjectType());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterSchemaTypeRecord0 valued, DataInput stream) throws IOException{
		    
		      valued.setObjectType(stream.readShort());
		    
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
            return 6;
	    }
    
	    @Override
	    public String getName() {
            return "objectType";
	    }
    
	    @Override
	    public Object getValue(AmiCenterSchemaTypeRecord0 valued) {
		    return (short)((AmiCenterSchemaTypeRecord0)valued).getObjectType();
	    }
    
	    @Override
	    public void setValue(AmiCenterSchemaTypeRecord0 valued, Object value) {
		    valued.setObjectType((java.lang.Short)value);
	    }
    
	    @Override
	    public void copy(AmiCenterSchemaTypeRecord0 source, AmiCenterSchemaTypeRecord0 dest) {
		    dest.setObjectType(source.getObjectType());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterSchemaTypeRecord0 source, AmiCenterSchemaTypeRecord0 dest) {
	        return OH.eq(dest.getObjectType(),source.getObjectType());
	    }
	    
	    
	    
	    
	    
	    
	    @Override
	    public short getShort(AmiCenterSchemaTypeRecord0 valued) {
		    return valued.getObjectType();
	    }
    
	    @Override
	    public void setShort(AmiCenterSchemaTypeRecord0 valued, short value) {
		    valued.setObjectType(value);
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return short.class;
	    }
	    private static final Caster CASTER=OH.getCaster(short.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiCenterSchemaTypeRecord0 valued, StringBuilder sb){
	        
	        sb.append(valued.getObjectType());
	        
	    }
	    @Override
	    public void append(AmiCenterSchemaTypeRecord0 valued, StringBuildable sb){
	        
	        sb.append(valued.getObjectType());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:short objectType";
	    }
	    @Override
	    public void clear(AmiCenterSchemaTypeRecord0 valued){
	       valued.setObjectType((short)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_objectType=new VALUED_PARAM_CLASS_objectType();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_count, VALUED_PARAM_objectType, };



    private static final byte PIDS[]={ 11 ,6};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 11: return VALUED_PARAM_count;
             case 6: return VALUED_PARAM_objectType;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 11: return true;
             case 6: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 11: return "count";
             case 6: return "objectType";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 11: return 0;
             case 6: return 1;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="count") return 11;
             if(name=="objectType") return 6;
            
             if("count".equals(name)) return 11;
             if("objectType".equals(name)) return 6;
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
             case 11: return  OH.valueOf(this._count); 
             case 6: return  OH.valueOf(this._objectType); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 11: return long.class;
             case 6: return short.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 11: return 6;
             case 6: return 2;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 11: this._count=(java.lang.Long)value;return;
             case 6: this._objectType=(java.lang.Short)value;return;
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
             case 11: this._count=(java.lang.Long)value;return true;
             case 6: this._objectType=(java.lang.Short)value;return true;
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
             case 6: return this._objectType;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for short value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askInt(byte pid){
        switch(pid){
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
             case 11: return this._count;
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
             case 6: this._objectType=value;return;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for short value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putInt(byte pid,int value){
    
        switch(pid){
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
             case 11: this._count=value;return;
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
        
        case 6:
        
            if((basicType=in.readByte())!=2)
                break;
            this._objectType=in.readShort();
        
            break;

        case 11:
        
            if((basicType=in.readByte())!=6)
                break;
            this._count=in.readLong();
        
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
        
if(this._objectType!=(short)0 && (0 & transience)==0){
    out.writeByte(6);
        
    out.writeByte(2);
    out.writeShort(this._objectType);
        
}

if(this._count!=0L && (0 & transience)==0){
    out.writeByte(11);
        
    out.writeByte(6);
    out.writeLong(this._count);
        
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