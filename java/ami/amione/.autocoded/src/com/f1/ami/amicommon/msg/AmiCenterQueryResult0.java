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

public abstract class AmiCenterQueryResult0 implements com.f1.ami.amicommon.msg.AmiCenterQueryResult ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private java.util.List _generatedKeys;

    private java.lang.Class _returnType;

    private java.lang.Object _returnValue;

    private long _rowsEffected;

    private java.util.List _tables;

    private static final String NAMES[]={ "generatedKeys" ,"returnType","returnValue","rowsEffected","tables"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 7;
        try{
        switch(h){

                case 0:

                    if(name == "generatedKeys" || name.equals("generatedKeys")) {this._generatedKeys=(java.util.List)value;return;}
break;
                case 1:

                    if(name == "returnValue" || name.equals("returnValue")) {this._returnValue=(java.lang.Object)value;return;}
break;
                case 2:

                    if(name == "returnType" || name.equals("returnType")) {this._returnType=(java.lang.Class)value;return;}
break;
                case 5:

                    if(name == "tables" || name.equals("tables")) {this._tables=(java.util.List)value;return;}
break;
                case 6:

                    if(name == "rowsEffected" || name.equals("rowsEffected")) {this._rowsEffected=(java.lang.Long)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 0:

                    if(name == "generatedKeys" || name.equals("generatedKeys")) {this._generatedKeys=(java.util.List)value;return true;}
break;
                case 1:

                    if(name == "returnValue" || name.equals("returnValue")) {this._returnValue=(java.lang.Object)value;return true;}
break;
                case 2:

                    if(name == "returnType" || name.equals("returnType")) {this._returnType=(java.lang.Class)value;return true;}
break;
                case 5:

                    if(name == "tables" || name.equals("tables")) {this._tables=(java.util.List)value;return true;}
break;
                case 6:

                    if(name == "rowsEffected" || name.equals("rowsEffected")) {this._rowsEffected=(java.lang.Long)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 0:

		    
                    if(name == "generatedKeys" || name.equals("generatedKeys")) {return this._generatedKeys;}
            
break;
                case 1:

		    
                    if(name == "returnValue" || name.equals("returnValue")) {return this._returnValue;}
            
break;
                case 2:

		    
                    if(name == "returnType" || name.equals("returnType")) {return this._returnType;}
            
break;
                case 5:

		    
                    if(name == "tables" || name.equals("tables")) {return this._tables;}
            
break;
                case 6:

		    
                    if(name == "rowsEffected" || name.equals("rowsEffected")) {return OH.valueOf(this._rowsEffected);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 0:

                    if(name == "generatedKeys" || name.equals("generatedKeys")) {return java.util.List.class;}
break;
                case 1:

                    if(name == "returnValue" || name.equals("returnValue")) {return java.lang.Object.class;}
break;
                case 2:

                    if(name == "returnType" || name.equals("returnType")) {return java.lang.Class.class;}
break;
                case 5:

                    if(name == "tables" || name.equals("tables")) {return java.util.List.class;}
break;
                case 6:

                    if(name == "rowsEffected" || name.equals("rowsEffected")) {return long.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 0:

                    if(name == "generatedKeys" || name.equals("generatedKeys")) {return VALUED_PARAM_generatedKeys;}
break;
                case 1:

                    if(name == "returnValue" || name.equals("returnValue")) {return VALUED_PARAM_returnValue;}
break;
                case 2:

                    if(name == "returnType" || name.equals("returnType")) {return VALUED_PARAM_returnType;}
break;
                case 5:

                    if(name == "tables" || name.equals("tables")) {return VALUED_PARAM_tables;}
break;
                case 6:

                    if(name == "rowsEffected" || name.equals("rowsEffected")) {return VALUED_PARAM_rowsEffected;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 0:

                    if(name == "generatedKeys" || name.equals("generatedKeys")) {return 0;}
break;
                case 1:

                    if(name == "returnValue" || name.equals("returnValue")) {return 2;}
break;
                case 2:

                    if(name == "returnType" || name.equals("returnType")) {return 1;}
break;
                case 5:

                    if(name == "tables" || name.equals("tables")) {return 4;}
break;
                case 6:

                    if(name == "rowsEffected" || name.equals("rowsEffected")) {return 3;}
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
	    return (Class)AmiCenterQueryResult0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 0:

                    if(name == "generatedKeys" || name.equals("generatedKeys")) {return true;}
break;
                case 1:

                    if(name == "returnValue" || name.equals("returnValue")) {return true;}
break;
                case 2:

                    if(name == "returnType" || name.equals("returnType")) {return true;}
break;
                case 5:

                    if(name == "tables" || name.equals("tables")) {return true;}
break;
                case 6:

                    if(name == "rowsEffected" || name.equals("rowsEffected")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 0:

                    if(name == "generatedKeys" || name.equals("generatedKeys")) {return 21;}
break;
                case 1:

                    if(name == "returnValue" || name.equals("returnValue")) {return 18;}
break;
                case 2:

                    if(name == "returnType" || name.equals("returnType")) {return 51;}
break;
                case 5:

                    if(name == "tables" || name.equals("tables")) {return 21;}
break;
                case 6:

                    if(name == "rowsEffected" || name.equals("rowsEffected")) {return 6;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _generatedKeys;

        case 1:return _returnType;

        case 2:return _returnValue;

        case 3:return _rowsEffected;

        case 4:return _tables;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 5);
    }

    public java.util.List getGeneratedKeys(){
        return this._generatedKeys;
    }
    public void setGeneratedKeys(java.util.List _generatedKeys){
    
        this._generatedKeys=_generatedKeys;
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





  
    private static final class VALUED_PARAM_CLASS_generatedKeys extends AbstractValuedParam<AmiCenterQueryResult0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryResult0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryResult0 valued, DataInput stream) throws IOException{
		    
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
		    return 0;
	    }
    
	    @Override
	    public byte getPid() {
            return 6;
	    }
    
	    @Override
	    public String getName() {
            return "generatedKeys";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryResult0 valued) {
		    return (java.util.List)((AmiCenterQueryResult0)valued).getGeneratedKeys();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryResult0 valued, Object value) {
		    valued.setGeneratedKeys((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryResult0 source, AmiCenterQueryResult0 dest) {
		    dest.setGeneratedKeys(source.getGeneratedKeys());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryResult0 source, AmiCenterQueryResult0 dest) {
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
	    public void append(AmiCenterQueryResult0 valued, StringBuilder sb){
	        
	        sb.append(valued.getGeneratedKeys());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryResult0 valued, StringBuildable sb){
	        
	        sb.append(valued.getGeneratedKeys());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List generatedKeys";
	    }
	    @Override
	    public void clear(AmiCenterQueryResult0 valued){
	       valued.setGeneratedKeys(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_generatedKeys=new VALUED_PARAM_CLASS_generatedKeys();
  

  
    private static final class VALUED_PARAM_CLASS_returnType extends AbstractValuedParam<AmiCenterQueryResult0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 51;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryResult0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Class}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryResult0 valued, DataInput stream) throws IOException{
		    
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
		    return 1;
	    }
    
	    @Override
	    public byte getPid() {
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "returnType";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryResult0 valued) {
		    return (java.lang.Class)((AmiCenterQueryResult0)valued).getReturnType();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryResult0 valued, Object value) {
		    valued.setReturnType((java.lang.Class)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryResult0 source, AmiCenterQueryResult0 dest) {
		    dest.setReturnType(source.getReturnType());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryResult0 source, AmiCenterQueryResult0 dest) {
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
	    public void append(AmiCenterQueryResult0 valued, StringBuilder sb){
	        
	        sb.append(valued.getReturnType());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryResult0 valued, StringBuildable sb){
	        
	        sb.append(valued.getReturnType());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Class returnType";
	    }
	    @Override
	    public void clear(AmiCenterQueryResult0 valued){
	       valued.setReturnType(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_returnType=new VALUED_PARAM_CLASS_returnType();
  

  
    private static final class VALUED_PARAM_CLASS_returnValue extends AbstractValuedParam<AmiCenterQueryResult0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 18;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryResult0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Object}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryResult0 valued, DataInput stream) throws IOException{
		    
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
		    return 2;
	    }
    
	    @Override
	    public byte getPid() {
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "returnValue";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryResult0 valued) {
		    return (java.lang.Object)((AmiCenterQueryResult0)valued).getReturnValue();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryResult0 valued, Object value) {
		    valued.setReturnValue((java.lang.Object)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryResult0 source, AmiCenterQueryResult0 dest) {
		    dest.setReturnValue(source.getReturnValue());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryResult0 source, AmiCenterQueryResult0 dest) {
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
	    public void append(AmiCenterQueryResult0 valued, StringBuilder sb){
	        
	        sb.append(valued.getReturnValue());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryResult0 valued, StringBuildable sb){
	        
	        sb.append(valued.getReturnValue());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Object returnValue";
	    }
	    @Override
	    public void clear(AmiCenterQueryResult0 valued){
	       valued.setReturnValue(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_returnValue=new VALUED_PARAM_CLASS_returnValue();
  

  
    private static final class VALUED_PARAM_CLASS_rowsEffected extends AbstractValuedParam<AmiCenterQueryResult0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryResult0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getRowsEffected());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryResult0 valued, DataInput stream) throws IOException{
		    
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
		    return 3;
	    }
    
	    @Override
	    public byte getPid() {
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "rowsEffected";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryResult0 valued) {
		    return (long)((AmiCenterQueryResult0)valued).getRowsEffected();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryResult0 valued, Object value) {
		    valued.setRowsEffected((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryResult0 source, AmiCenterQueryResult0 dest) {
		    dest.setRowsEffected(source.getRowsEffected());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryResult0 source, AmiCenterQueryResult0 dest) {
	        return OH.eq(dest.getRowsEffected(),source.getRowsEffected());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterQueryResult0 valued) {
		    return valued.getRowsEffected();
	    }
    
	    @Override
	    public void setLong(AmiCenterQueryResult0 valued, long value) {
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
	    public void append(AmiCenterQueryResult0 valued, StringBuilder sb){
	        
	        sb.append(valued.getRowsEffected());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryResult0 valued, StringBuildable sb){
	        
	        sb.append(valued.getRowsEffected());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long rowsEffected";
	    }
	    @Override
	    public void clear(AmiCenterQueryResult0 valued){
	       valued.setRowsEffected(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_rowsEffected=new VALUED_PARAM_CLASS_rowsEffected();
  

  
    private static final class VALUED_PARAM_CLASS_tables extends AbstractValuedParam<AmiCenterQueryResult0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiCenterQueryResult0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQueryResult0 valued, DataInput stream) throws IOException{
		    
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
		    return 4;
	    }
    
	    @Override
	    public byte getPid() {
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "tables";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQueryResult0 valued) {
		    return (java.util.List)((AmiCenterQueryResult0)valued).getTables();
	    }
    
	    @Override
	    public void setValue(AmiCenterQueryResult0 valued, Object value) {
		    valued.setTables((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQueryResult0 source, AmiCenterQueryResult0 dest) {
		    dest.setTables(source.getTables());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQueryResult0 source, AmiCenterQueryResult0 dest) {
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
	    public void append(AmiCenterQueryResult0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTables());
	        
	    }
	    @Override
	    public void append(AmiCenterQueryResult0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTables());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List tables";
	    }
	    @Override
	    public void clear(AmiCenterQueryResult0 valued){
	       valued.setTables(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_tables=new VALUED_PARAM_CLASS_tables();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_generatedKeys, VALUED_PARAM_returnType, VALUED_PARAM_returnValue, VALUED_PARAM_rowsEffected, VALUED_PARAM_tables, };



    private static final byte PIDS[]={ 6 ,4,5,3,1};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 6: return VALUED_PARAM_generatedKeys;
             case 4: return VALUED_PARAM_returnType;
             case 5: return VALUED_PARAM_returnValue;
             case 3: return VALUED_PARAM_rowsEffected;
             case 1: return VALUED_PARAM_tables;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 6: return true;
             case 4: return true;
             case 5: return true;
             case 3: return true;
             case 1: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 6: return "generatedKeys";
             case 4: return "returnType";
             case 5: return "returnValue";
             case 3: return "rowsEffected";
             case 1: return "tables";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 6: return 0;
             case 4: return 1;
             case 5: return 2;
             case 3: return 3;
             case 1: return 4;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="generatedKeys") return 6;
             if(name=="returnType") return 4;
             if(name=="returnValue") return 5;
             if(name=="rowsEffected") return 3;
             if(name=="tables") return 1;
            
             if("generatedKeys".equals(name)) return 6;
             if("returnType".equals(name)) return 4;
             if("returnValue".equals(name)) return 5;
             if("rowsEffected".equals(name)) return 3;
             if("tables".equals(name)) return 1;
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
             case 6: return  this._generatedKeys; 
             case 4: return  this._returnType; 
             case 5: return  this._returnValue; 
             case 3: return  OH.valueOf(this._rowsEffected); 
             case 1: return  this._tables; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 6: return java.util.List.class;
             case 4: return java.lang.Class.class;
             case 5: return java.lang.Object.class;
             case 3: return long.class;
             case 1: return java.util.List.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 6: return 21;
             case 4: return 51;
             case 5: return 18;
             case 3: return 6;
             case 1: return 21;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 6: this._generatedKeys=(java.util.List)value;return;
             case 4: this._returnType=(java.lang.Class)value;return;
             case 5: this._returnValue=(java.lang.Object)value;return;
             case 3: this._rowsEffected=(java.lang.Long)value;return;
             case 1: this._tables=(java.util.List)value;return;
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
             case 6: this._generatedKeys=(java.util.List)value;return true;
             case 4: this._returnType=(java.lang.Class)value;return true;
             case 5: this._returnValue=(java.lang.Object)value;return true;
             case 3: this._rowsEffected=(java.lang.Long)value;return true;
             case 1: this._tables=(java.util.List)value;return true;
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
             case 3: return this._rowsEffected;
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
             case 3: this._rowsEffected=value;return;
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
        
            this._tables=(java.util.List)converter.read(session);
        
            break;

        case 3:
        
            if((basicType=in.readByte())!=6)
                break;
            this._rowsEffected=in.readLong();
        
            break;

        case 4:
        
            this._returnType=(java.lang.Class)converter.read(session);
        
            break;

        case 5:
        
            this._returnValue=(java.lang.Object)converter.read(session);
        
            break;

        case 6:
        
            this._generatedKeys=(java.util.List)converter.read(session);
        
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
    out.writeByte(1);
        
    converter.write(this._tables,session);
        
}

if(this._rowsEffected!=0L && (0 & transience)==0){
    out.writeByte(3);
        
    out.writeByte(6);
    out.writeLong(this._rowsEffected);
        
}

if(this._returnType!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._returnType,session);
        
}

if(this._returnValue!=null && (0 & transience)==0){
    out.writeByte(5);
        
    converter.write(this._returnValue,session);
        
}

if(this._generatedKeys!=null && (0 & transience)==0){
    out.writeByte(6);
        
    converter.write(this._generatedKeys,session);
        
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