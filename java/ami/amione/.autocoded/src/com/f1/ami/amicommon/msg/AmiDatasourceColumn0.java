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

public abstract class AmiDatasourceColumn0 implements com.f1.ami.amicommon.msg.AmiDatasourceColumn ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

    private int hashCode=ValuedHashCodeGenerator.next(this);
    
    @Override
    public int hashCode(){
      return this.hashCode;
    }
    
    @Override
	public com.f1.base.Message clone(){
       try{
         
	       final com.f1.base.Message r= (com.f1.base.Message)super.clone();
	       ((AmiDatasourceColumn0)r).__locked=false;
	       return r;
	     
       } catch( Exception e){
          throw new RuntimeException("error cloning",e);
       }
    }
    
    
    private boolean __locked;
    @Override
    public void lock(){
       this.__locked=true;
    }

    @Override
	public boolean isLocked(){
	   return this.__locked;
	}
	
	
    

    private byte _hint;

    private java.lang.String _name;

    private byte _type;

    private static final String NAMES[]={ "hint" ,"name","type"};

	@Override
    public void put(String name, Object value){//asdf
    
	   if(__locked)
	       throw newLockedException(name,value);
	
        final int h=Math.abs(name.hashCode()) % 7;
        try{
        switch(h){

                case 1:

                    if(name == "name" || name.equals("name")) {this._name=(java.lang.String)value;return;}
break;
                case 3:

                    if(name == "type" || name.equals("type")) {this._type=(java.lang.Byte)value;return;}
break;
                case 6:

                    if(name == "hint" || name.equals("hint")) {this._hint=(java.lang.Byte)value;return;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
        }catch(NullPointerException e){
            throw new NullPointerException("primitive value can not be null: "+name);
        }
    }


	@Override
    public boolean putNoThrow(String name, Object value){
    
	  if(this.__locked) return false;
	
           if(name==null)
               return false;
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "name" || name.equals("name")) {this._name=(java.lang.String)value;return true;}
break;
                case 3:

                    if(name == "type" || name.equals("type")) {this._type=(java.lang.Byte)value;return true;}
break;
                case 6:

                    if(name == "hint" || name.equals("hint")) {this._hint=(java.lang.Byte)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

		    
                    if(name == "name" || name.equals("name")) {return this._name;}
            
break;
                case 3:

		    
                    if(name == "type" || name.equals("type")) {return OH.valueOf(this._type);}
		    
break;
                case 6:

		    
                    if(name == "hint" || name.equals("hint")) {return OH.valueOf(this._hint);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "name" || name.equals("name")) {return java.lang.String.class;}
break;
                case 3:

                    if(name == "type" || name.equals("type")) {return byte.class;}
break;
                case 6:

                    if(name == "hint" || name.equals("hint")) {return byte.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "name" || name.equals("name")) {return VALUED_PARAM_name;}
break;
                case 3:

                    if(name == "type" || name.equals("type")) {return VALUED_PARAM_type;}
break;
                case 6:

                    if(name == "hint" || name.equals("hint")) {return VALUED_PARAM_hint;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "name" || name.equals("name")) {return 1;}
break;
                case 3:

                    if(name == "type" || name.equals("type")) {return 2;}
break;
                case 6:

                    if(name == "hint" || name.equals("hint")) {return 0;}
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
	    return (Class)AmiDatasourceColumn0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "name" || name.equals("name")) {return true;}
break;
                case 3:

                    if(name == "type" || name.equals("type")) {return true;}
break;
                case 6:

                    if(name == "hint" || name.equals("hint")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 1:

                    if(name == "name" || name.equals("name")) {return 20;}
break;
                case 3:

                    if(name == "type" || name.equals("type")) {return 1;}
break;
                case 6:

                    if(name == "hint" || name.equals("hint")) {return 1;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _hint;

        case 1:return _name;

        case 2:return _type;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 3);
    }

    public byte getHint(){
        return this._hint;
    }
    public void setHint(byte _hint){
    
	   if(__locked)
	       throw newLockedException("hint",_hint);
	
        this._hint=_hint;
    }

    public java.lang.String getName(){
        return this._name;
    }
    public void setName(java.lang.String _name){
    
	   if(__locked)
	       throw newLockedException("name",_name);
	
        this._name=_name;
    }

    public byte getType(){
        return this._type;
    }
    public void setType(byte _type){
    
	   if(__locked)
	       throw newLockedException("type",_type);
	
        this._type=_type;
    }





  
    private static final class VALUED_PARAM_CLASS_hint extends AbstractValuedParam<AmiDatasourceColumn0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiDatasourceColumn0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getHint());
		    
	    }
	    
	    @Override
	    public void read(AmiDatasourceColumn0 valued, DataInput stream) throws IOException{
		    
		      valued.setHint(stream.readByte());
		    
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
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "hint";
	    }
    
	    @Override
	    public Object getValue(AmiDatasourceColumn0 valued) {
		    return (byte)((AmiDatasourceColumn0)valued).getHint();
	    }
    
	    @Override
	    public void setValue(AmiDatasourceColumn0 valued, Object value) {
		    valued.setHint((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiDatasourceColumn0 source, AmiDatasourceColumn0 dest) {
		    dest.setHint(source.getHint());
	    }
	    
	    @Override
	    public boolean areEqual(AmiDatasourceColumn0 source, AmiDatasourceColumn0 dest) {
	        return OH.eq(dest.getHint(),source.getHint());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiDatasourceColumn0 valued) {
		    return valued.getHint();
	    }
    
	    @Override
	    public void setByte(AmiDatasourceColumn0 valued, byte value) {
		    valued.setHint(value);
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
	    public void append(AmiDatasourceColumn0 valued, StringBuilder sb){
	        
	        sb.append(valued.getHint());
	        
	    }
	    @Override
	    public void append(AmiDatasourceColumn0 valued, StringBuildable sb){
	        
	        sb.append(valued.getHint());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte hint";
	    }
	    @Override
	    public void clear(AmiDatasourceColumn0 valued){
	       valued.setHint((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_hint=new VALUED_PARAM_CLASS_hint();
  

  
    private static final class VALUED_PARAM_CLASS_name extends AbstractValuedParam<AmiDatasourceColumn0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiDatasourceColumn0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiDatasourceColumn0 valued, DataInput stream) throws IOException{
		    
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
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "name";
	    }
    
	    @Override
	    public Object getValue(AmiDatasourceColumn0 valued) {
		    return (java.lang.String)((AmiDatasourceColumn0)valued).getName();
	    }
    
	    @Override
	    public void setValue(AmiDatasourceColumn0 valued, Object value) {
		    valued.setName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiDatasourceColumn0 source, AmiDatasourceColumn0 dest) {
		    dest.setName(source.getName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiDatasourceColumn0 source, AmiDatasourceColumn0 dest) {
	        return OH.eq(dest.getName(),source.getName());
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
	    public void append(AmiDatasourceColumn0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getName(),sb);
	        
	    }
	    @Override
	    public void append(AmiDatasourceColumn0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String name";
	    }
	    @Override
	    public void clear(AmiDatasourceColumn0 valued){
	       valued.setName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_name=new VALUED_PARAM_CLASS_name();
  

  
    private static final class VALUED_PARAM_CLASS_type extends AbstractValuedParam<AmiDatasourceColumn0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiDatasourceColumn0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getType());
		    
	    }
	    
	    @Override
	    public void read(AmiDatasourceColumn0 valued, DataInput stream) throws IOException{
		    
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
		    return 2;
	    }
    
	    @Override
	    public byte getPid() {
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "type";
	    }
    
	    @Override
	    public Object getValue(AmiDatasourceColumn0 valued) {
		    return (byte)((AmiDatasourceColumn0)valued).getType();
	    }
    
	    @Override
	    public void setValue(AmiDatasourceColumn0 valued, Object value) {
		    valued.setType((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiDatasourceColumn0 source, AmiDatasourceColumn0 dest) {
		    dest.setType(source.getType());
	    }
	    
	    @Override
	    public boolean areEqual(AmiDatasourceColumn0 source, AmiDatasourceColumn0 dest) {
	        return OH.eq(dest.getType(),source.getType());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiDatasourceColumn0 valued) {
		    return valued.getType();
	    }
    
	    @Override
	    public void setByte(AmiDatasourceColumn0 valued, byte value) {
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
	    public void append(AmiDatasourceColumn0 valued, StringBuilder sb){
	        
	        sb.append(valued.getType());
	        
	    }
	    @Override
	    public void append(AmiDatasourceColumn0 valued, StringBuildable sb){
	        
	        sb.append(valued.getType());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte type";
	    }
	    @Override
	    public void clear(AmiDatasourceColumn0 valued){
	       valued.setType((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_type=new VALUED_PARAM_CLASS_type();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_hint, VALUED_PARAM_name, VALUED_PARAM_type, };



    private static final byte PIDS[]={ 3 ,1,2};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 3: return VALUED_PARAM_hint;
             case 1: return VALUED_PARAM_name;
             case 2: return VALUED_PARAM_type;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 3: return true;
             case 1: return true;
             case 2: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 3: return "hint";
             case 1: return "name";
             case 2: return "type";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 3: return 0;
             case 1: return 1;
             case 2: return 2;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="hint") return 3;
             if(name=="name") return 1;
             if(name=="type") return 2;
            
             if("hint".equals(name)) return 3;
             if("name".equals(name)) return 1;
             if("type".equals(name)) return 2;
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
             case 3: return  OH.valueOf(this._hint); 
             case 1: return  this._name; 
             case 2: return  OH.valueOf(this._type); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 3: return byte.class;
             case 1: return java.lang.String.class;
             case 2: return byte.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 3: return 1;
             case 1: return 20;
             case 2: return 1;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 3: this._hint=(java.lang.Byte)value;return;
             case 1: this._name=(java.lang.String)value;return;
             case 2: this._type=(java.lang.Byte)value;return;
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
             case 3: this._hint=(java.lang.Byte)value;return true;
             case 1: this._name=(java.lang.String)value;return true;
             case 2: this._type=(java.lang.Byte)value;return true;
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
             case 3: return this._hint;
             case 2: return this._type;
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
    
	   if(__locked)
	       throw newLockedException(pid,value);
	
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for boolean value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putByte(byte pid,byte value){
    
	   if(__locked)
	       throw newLockedException(pid,value);
	
        switch(pid){
             case 3: this._hint=value;return;
             case 2: this._type=value;return;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putChar(byte pid,char value){
    
	   if(__locked)
	       throw newLockedException(pid,value);
	
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for char value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putShort(byte pid,short value){
    
	   if(__locked)
	       throw newLockedException(pid,value);
	
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for short value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putInt(byte pid,int value){
    
	   if(__locked)
	       throw newLockedException(pid,value);
	
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for int value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putFloat(byte pid,float value){
    
	   if(__locked)
	       throw newLockedException(pid,value);
	
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for float value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putLong(byte pid,long value){
    
	   if(__locked)
	       throw newLockedException(pid,value);
	
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for long value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putDouble(byte pid,double value){
    
	   if(__locked)
	       throw newLockedException(pid,value);
	
        switch(pid){
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for double value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void read(FromByteArrayConverterSession session) throws IOException{
    
	   if(__locked)
	       throw new com.f1.base.LockedException("can not modify locked object, hence can not deserialize from stream: " + this.toString());
	
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
        
            this._name=(java.lang.String)converter.read(session);
        
            break;

        case 2:
        
            if((basicType=in.readByte())!=1)
                break;
            this._type=in.readByte();
        
            break;

        case 3:
        
            if((basicType=in.readByte())!=1)
                break;
            this._hint=in.readByte();
        
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
        
if(this._name!=null && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this._name,session);
        
}

if(this._type!=(byte)0 && (0 & transience)==0){
    out.writeByte(2);
        
    out.writeByte(1);
    out.writeByte(this._type);
        
}

if(this._hint!=(byte)0 && (0 & transience)==0){
    out.writeByte(3);
        
    out.writeByte(1);
    out.writeByte(this._hint);
        
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