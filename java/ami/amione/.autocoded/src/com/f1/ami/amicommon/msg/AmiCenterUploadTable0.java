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

public abstract class AmiCenterUploadTable0 implements com.f1.ami.amicommon.msg.AmiCenterUploadTable ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private com.f1.base.Table _data;

    private java.util.List _targetColumns;

    private java.lang.String _targetTable;

    private static final String NAMES[]={ "data" ,"targetColumns","targetTable"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 4;
        try{
        switch(h){

                case 0:

                    if(name == "targetColumns" || name.equals("targetColumns")) {this._targetColumns=(java.util.List)value;return;}
break;
                case 2:

                    if(name == "data" || name.equals("data")) {this._data=(com.f1.base.Table)value;return;}
break;
                case 3:

                    if(name == "targetTable" || name.equals("targetTable")) {this._targetTable=(java.lang.String)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 4;
        switch(h){

                case 0:

                    if(name == "targetColumns" || name.equals("targetColumns")) {this._targetColumns=(java.util.List)value;return true;}
break;
                case 2:

                    if(name == "data" || name.equals("data")) {this._data=(com.f1.base.Table)value;return true;}
break;
                case 3:

                    if(name == "targetTable" || name.equals("targetTable")) {this._targetTable=(java.lang.String)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 4;
        switch(h){

                case 0:

		    
                    if(name == "targetColumns" || name.equals("targetColumns")) {return this._targetColumns;}
            
break;
                case 2:

		    
                    if(name == "data" || name.equals("data")) {return this._data;}
            
break;
                case 3:

		    
                    if(name == "targetTable" || name.equals("targetTable")) {return this._targetTable;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 4;
        switch(h){

                case 0:

                    if(name == "targetColumns" || name.equals("targetColumns")) {return java.util.List.class;}
break;
                case 2:

                    if(name == "data" || name.equals("data")) {return com.f1.base.Table.class;}
break;
                case 3:

                    if(name == "targetTable" || name.equals("targetTable")) {return java.lang.String.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 4;
        switch(h){

                case 0:

                    if(name == "targetColumns" || name.equals("targetColumns")) {return VALUED_PARAM_targetColumns;}
break;
                case 2:

                    if(name == "data" || name.equals("data")) {return VALUED_PARAM_data;}
break;
                case 3:

                    if(name == "targetTable" || name.equals("targetTable")) {return VALUED_PARAM_targetTable;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 4;
        switch(h){

                case 0:

                    if(name == "targetColumns" || name.equals("targetColumns")) {return 1;}
break;
                case 2:

                    if(name == "data" || name.equals("data")) {return 0;}
break;
                case 3:

                    if(name == "targetTable" || name.equals("targetTable")) {return 2;}
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
	    return (Class)AmiCenterUploadTable0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 4;
        switch(h){

                case 0:

                    if(name == "targetColumns" || name.equals("targetColumns")) {return true;}
break;
                case 2:

                    if(name == "data" || name.equals("data")) {return true;}
break;
                case 3:

                    if(name == "targetTable" || name.equals("targetTable")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 4;
        switch(h){

                case 0:

                    if(name == "targetColumns" || name.equals("targetColumns")) {return 21;}
break;
                case 2:

                    if(name == "data" || name.equals("data")) {return 43;}
break;
                case 3:

                    if(name == "targetTable" || name.equals("targetTable")) {return 20;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _data;

        case 1:return _targetColumns;

        case 2:return _targetTable;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 3);
    }

    public com.f1.base.Table getData(){
        return this._data;
    }
    public void setData(com.f1.base.Table _data){
    
        this._data=_data;
    }

    public java.util.List getTargetColumns(){
        return this._targetColumns;
    }
    public void setTargetColumns(java.util.List _targetColumns){
    
        this._targetColumns=_targetColumns;
    }

    public java.lang.String getTargetTable(){
        return this._targetTable;
    }
    public void setTargetTable(java.lang.String _targetTable){
    
        this._targetTable=_targetTable;
    }





  
    private static final class VALUED_PARAM_CLASS_data extends AbstractValuedParam<AmiCenterUploadTable0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 43;
	    }
	    
	    @Override
	    public void write(AmiCenterUploadTable0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.base.Table}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterUploadTable0 valued, DataInput stream) throws IOException{
		    
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
		    return 0;
	    }
    
	    @Override
	    public byte getPid() {
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "data";
	    }
    
	    @Override
	    public Object getValue(AmiCenterUploadTable0 valued) {
		    return (com.f1.base.Table)((AmiCenterUploadTable0)valued).getData();
	    }
    
	    @Override
	    public void setValue(AmiCenterUploadTable0 valued, Object value) {
		    valued.setData((com.f1.base.Table)value);
	    }
    
	    @Override
	    public void copy(AmiCenterUploadTable0 source, AmiCenterUploadTable0 dest) {
		    dest.setData(source.getData());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterUploadTable0 source, AmiCenterUploadTable0 dest) {
	        return OH.eq(dest.getData(),source.getData());
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
	    public void append(AmiCenterUploadTable0 valued, StringBuilder sb){
	        
	        sb.append(valued.getData());
	        
	    }
	    @Override
	    public void append(AmiCenterUploadTable0 valued, StringBuildable sb){
	        
	        sb.append(valued.getData());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.base.Table data";
	    }
	    @Override
	    public void clear(AmiCenterUploadTable0 valued){
	       valued.setData(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_data=new VALUED_PARAM_CLASS_data();
  

  
    private static final class VALUED_PARAM_CLASS_targetColumns extends AbstractValuedParam<AmiCenterUploadTable0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiCenterUploadTable0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterUploadTable0 valued, DataInput stream) throws IOException{
		    
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
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "targetColumns";
	    }
    
	    @Override
	    public Object getValue(AmiCenterUploadTable0 valued) {
		    return (java.util.List)((AmiCenterUploadTable0)valued).getTargetColumns();
	    }
    
	    @Override
	    public void setValue(AmiCenterUploadTable0 valued, Object value) {
		    valued.setTargetColumns((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiCenterUploadTable0 source, AmiCenterUploadTable0 dest) {
		    dest.setTargetColumns(source.getTargetColumns());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterUploadTable0 source, AmiCenterUploadTable0 dest) {
	        return OH.eq(dest.getTargetColumns(),source.getTargetColumns());
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
	    public void append(AmiCenterUploadTable0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTargetColumns());
	        
	    }
	    @Override
	    public void append(AmiCenterUploadTable0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTargetColumns());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List targetColumns";
	    }
	    @Override
	    public void clear(AmiCenterUploadTable0 valued){
	       valued.setTargetColumns(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_targetColumns=new VALUED_PARAM_CLASS_targetColumns();
  

  
    private static final class VALUED_PARAM_CLASS_targetTable extends AbstractValuedParam<AmiCenterUploadTable0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterUploadTable0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterUploadTable0 valued, DataInput stream) throws IOException{
		    
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
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "targetTable";
	    }
    
	    @Override
	    public Object getValue(AmiCenterUploadTable0 valued) {
		    return (java.lang.String)((AmiCenterUploadTable0)valued).getTargetTable();
	    }
    
	    @Override
	    public void setValue(AmiCenterUploadTable0 valued, Object value) {
		    valued.setTargetTable((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterUploadTable0 source, AmiCenterUploadTable0 dest) {
		    dest.setTargetTable(source.getTargetTable());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterUploadTable0 source, AmiCenterUploadTable0 dest) {
	        return OH.eq(dest.getTargetTable(),source.getTargetTable());
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
	    public void append(AmiCenterUploadTable0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getTargetTable(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterUploadTable0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getTargetTable(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String targetTable";
	    }
	    @Override
	    public void clear(AmiCenterUploadTable0 valued){
	       valued.setTargetTable(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_targetTable=new VALUED_PARAM_CLASS_targetTable();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_data, VALUED_PARAM_targetColumns, VALUED_PARAM_targetTable, };



    private static final byte PIDS[]={ 1 ,3,2};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 1: return VALUED_PARAM_data;
             case 3: return VALUED_PARAM_targetColumns;
             case 2: return VALUED_PARAM_targetTable;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 1: return true;
             case 3: return true;
             case 2: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 1: return "data";
             case 3: return "targetColumns";
             case 2: return "targetTable";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 1: return 0;
             case 3: return 1;
             case 2: return 2;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="data") return 1;
             if(name=="targetColumns") return 3;
             if(name=="targetTable") return 2;
            
             if("data".equals(name)) return 1;
             if("targetColumns".equals(name)) return 3;
             if("targetTable".equals(name)) return 2;
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
             case 1: return  this._data; 
             case 3: return  this._targetColumns; 
             case 2: return  this._targetTable; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 1: return com.f1.base.Table.class;
             case 3: return java.util.List.class;
             case 2: return java.lang.String.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 1: return 43;
             case 3: return 21;
             case 2: return 20;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 1: this._data=(com.f1.base.Table)value;return;
             case 3: this._targetColumns=(java.util.List)value;return;
             case 2: this._targetTable=(java.lang.String)value;return;
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
             case 1: this._data=(com.f1.base.Table)value;return true;
             case 3: this._targetColumns=(java.util.List)value;return true;
             case 2: this._targetTable=(java.lang.String)value;return true;
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
        
            this._data=(com.f1.base.Table)converter.read(session);
        
            break;

        case 2:
        
            this._targetTable=(java.lang.String)converter.read(session);
        
            break;

        case 3:
        
            this._targetColumns=(java.util.List)converter.read(session);
        
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
        
if(this._data!=null && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this._data,session);
        
}

if(this._targetTable!=null && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this._targetTable,session);
        
}

if(this._targetColumns!=null && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this._targetColumns,session);
        
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