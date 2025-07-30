//Coded by ValuedCodeTemplate
package com.f1.ami.center.procs;

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

public abstract class AmiStoredProcRequest0 implements com.f1.ami.center.procs.AmiStoredProcRequest ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private java.util.List _arguments;

    private java.lang.String _invokedBy;

    private int _limit;

    private int _limitOffset;

    private static final String NAMES[]={ "arguments" ,"invokedBy","limit","limitOffset"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 8;
        try{
        switch(h){

                case 2:

                    if(name == "arguments" || name.equals("arguments")) {this._arguments=(java.util.List)value;return;}
break;
                case 3:

                    if(name == "limit" || name.equals("limit")) {this._limit=(java.lang.Integer)value;return;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return;}
break;
                case 6:

                    if(name == "limitOffset" || name.equals("limitOffset")) {this._limitOffset=(java.lang.Integer)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 2:

                    if(name == "arguments" || name.equals("arguments")) {this._arguments=(java.util.List)value;return true;}
break;
                case 3:

                    if(name == "limit" || name.equals("limit")) {this._limit=(java.lang.Integer)value;return true;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return true;}
break;
                case 6:

                    if(name == "limitOffset" || name.equals("limitOffset")) {this._limitOffset=(java.lang.Integer)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 2:

		    
                    if(name == "arguments" || name.equals("arguments")) {return this._arguments;}
            
break;
                case 3:

		    
                    if(name == "limit" || name.equals("limit")) {return OH.valueOf(this._limit);}
		    
break;
                case 5:

		    
                    if(name == "invokedBy" || name.equals("invokedBy")) {return this._invokedBy;}
            
break;
                case 6:

		    
                    if(name == "limitOffset" || name.equals("limitOffset")) {return OH.valueOf(this._limitOffset);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 2:

                    if(name == "arguments" || name.equals("arguments")) {return java.util.List.class;}
break;
                case 3:

                    if(name == "limit" || name.equals("limit")) {return int.class;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return java.lang.String.class;}
break;
                case 6:

                    if(name == "limitOffset" || name.equals("limitOffset")) {return int.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 2:

                    if(name == "arguments" || name.equals("arguments")) {return VALUED_PARAM_arguments;}
break;
                case 3:

                    if(name == "limit" || name.equals("limit")) {return VALUED_PARAM_limit;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return VALUED_PARAM_invokedBy;}
break;
                case 6:

                    if(name == "limitOffset" || name.equals("limitOffset")) {return VALUED_PARAM_limitOffset;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 2:

                    if(name == "arguments" || name.equals("arguments")) {return 0;}
break;
                case 3:

                    if(name == "limit" || name.equals("limit")) {return 2;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 1;}
break;
                case 6:

                    if(name == "limitOffset" || name.equals("limitOffset")) {return 3;}
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
        return 4;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiStoredProcRequest0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 2:

                    if(name == "arguments" || name.equals("arguments")) {return true;}
break;
                case 3:

                    if(name == "limit" || name.equals("limit")) {return true;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return true;}
break;
                case 6:

                    if(name == "limitOffset" || name.equals("limitOffset")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 2:

                    if(name == "arguments" || name.equals("arguments")) {return 21;}
break;
                case 3:

                    if(name == "limit" || name.equals("limit")) {return 4;}
break;
                case 5:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 20;}
break;
                case 6:

                    if(name == "limitOffset" || name.equals("limitOffset")) {return 4;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _arguments;

        case 1:return _invokedBy;

        case 2:return _limit;

        case 3:return _limitOffset;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 4);
    }

    public java.util.List getArguments(){
        return this._arguments;
    }
    public void setArguments(java.util.List _arguments){
    
        this._arguments=_arguments;
    }

    public java.lang.String getInvokedBy(){
        return this._invokedBy;
    }
    public void setInvokedBy(java.lang.String _invokedBy){
    
        this._invokedBy=_invokedBy;
    }

    public int getLimit(){
        return this._limit;
    }
    public void setLimit(int _limit){
    
        this._limit=_limit;
    }

    public int getLimitOffset(){
        return this._limitOffset;
    }
    public void setLimitOffset(int _limitOffset){
    
        this._limitOffset=_limitOffset;
    }





  
    private static final class VALUED_PARAM_CLASS_arguments extends AbstractValuedParam<AmiStoredProcRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiStoredProcRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiStoredProcRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "arguments";
	    }
    
	    @Override
	    public Object getValue(AmiStoredProcRequest0 valued) {
		    return (java.util.List)((AmiStoredProcRequest0)valued).getArguments();
	    }
    
	    @Override
	    public void setValue(AmiStoredProcRequest0 valued, Object value) {
		    valued.setArguments((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiStoredProcRequest0 source, AmiStoredProcRequest0 dest) {
		    dest.setArguments(source.getArguments());
	    }
	    
	    @Override
	    public boolean areEqual(AmiStoredProcRequest0 source, AmiStoredProcRequest0 dest) {
	        return OH.eq(dest.getArguments(),source.getArguments());
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
	    public void append(AmiStoredProcRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getArguments());
	        
	    }
	    @Override
	    public void append(AmiStoredProcRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getArguments());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List arguments";
	    }
	    @Override
	    public void clear(AmiStoredProcRequest0 valued){
	       valued.setArguments(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_arguments=new VALUED_PARAM_CLASS_arguments();
  

  
    private static final class VALUED_PARAM_CLASS_invokedBy extends AbstractValuedParam<AmiStoredProcRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiStoredProcRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiStoredProcRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "invokedBy";
	    }
    
	    @Override
	    public Object getValue(AmiStoredProcRequest0 valued) {
		    return (java.lang.String)((AmiStoredProcRequest0)valued).getInvokedBy();
	    }
    
	    @Override
	    public void setValue(AmiStoredProcRequest0 valued, Object value) {
		    valued.setInvokedBy((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiStoredProcRequest0 source, AmiStoredProcRequest0 dest) {
		    dest.setInvokedBy(source.getInvokedBy());
	    }
	    
	    @Override
	    public boolean areEqual(AmiStoredProcRequest0 source, AmiStoredProcRequest0 dest) {
	        return OH.eq(dest.getInvokedBy(),source.getInvokedBy());
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
	    public void append(AmiStoredProcRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public void append(AmiStoredProcRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String invokedBy";
	    }
	    @Override
	    public void clear(AmiStoredProcRequest0 valued){
	       valued.setInvokedBy(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_invokedBy=new VALUED_PARAM_CLASS_invokedBy();
  

  
    private static final class VALUED_PARAM_CLASS_limit extends AbstractValuedParam<AmiStoredProcRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiStoredProcRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getLimit());
		    
	    }
	    
	    @Override
	    public void read(AmiStoredProcRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setLimit(stream.readInt());
		    
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
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "limit";
	    }
    
	    @Override
	    public Object getValue(AmiStoredProcRequest0 valued) {
		    return (int)((AmiStoredProcRequest0)valued).getLimit();
	    }
    
	    @Override
	    public void setValue(AmiStoredProcRequest0 valued, Object value) {
		    valued.setLimit((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiStoredProcRequest0 source, AmiStoredProcRequest0 dest) {
		    dest.setLimit(source.getLimit());
	    }
	    
	    @Override
	    public boolean areEqual(AmiStoredProcRequest0 source, AmiStoredProcRequest0 dest) {
	        return OH.eq(dest.getLimit(),source.getLimit());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiStoredProcRequest0 valued) {
		    return valued.getLimit();
	    }
    
	    @Override
	    public void setInt(AmiStoredProcRequest0 valued, int value) {
		    valued.setLimit(value);
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
	    public void append(AmiStoredProcRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getLimit());
	        
	    }
	    @Override
	    public void append(AmiStoredProcRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getLimit());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int limit";
	    }
	    @Override
	    public void clear(AmiStoredProcRequest0 valued){
	       valued.setLimit(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_limit=new VALUED_PARAM_CLASS_limit();
  

  
    private static final class VALUED_PARAM_CLASS_limitOffset extends AbstractValuedParam<AmiStoredProcRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiStoredProcRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getLimitOffset());
		    
	    }
	    
	    @Override
	    public void read(AmiStoredProcRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setLimitOffset(stream.readInt());
		    
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
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "limitOffset";
	    }
    
	    @Override
	    public Object getValue(AmiStoredProcRequest0 valued) {
		    return (int)((AmiStoredProcRequest0)valued).getLimitOffset();
	    }
    
	    @Override
	    public void setValue(AmiStoredProcRequest0 valued, Object value) {
		    valued.setLimitOffset((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiStoredProcRequest0 source, AmiStoredProcRequest0 dest) {
		    dest.setLimitOffset(source.getLimitOffset());
	    }
	    
	    @Override
	    public boolean areEqual(AmiStoredProcRequest0 source, AmiStoredProcRequest0 dest) {
	        return OH.eq(dest.getLimitOffset(),source.getLimitOffset());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiStoredProcRequest0 valued) {
		    return valued.getLimitOffset();
	    }
    
	    @Override
	    public void setInt(AmiStoredProcRequest0 valued, int value) {
		    valued.setLimitOffset(value);
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
	    public void append(AmiStoredProcRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getLimitOffset());
	        
	    }
	    @Override
	    public void append(AmiStoredProcRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getLimitOffset());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int limitOffset";
	    }
	    @Override
	    public void clear(AmiStoredProcRequest0 valued){
	       valued.setLimitOffset(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_limitOffset=new VALUED_PARAM_CLASS_limitOffset();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_arguments, VALUED_PARAM_invokedBy, VALUED_PARAM_limit, VALUED_PARAM_limitOffset, };



    private static final byte PIDS[]={ 1 ,4,3,2};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 1: return VALUED_PARAM_arguments;
             case 4: return VALUED_PARAM_invokedBy;
             case 3: return VALUED_PARAM_limit;
             case 2: return VALUED_PARAM_limitOffset;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 1: return true;
             case 4: return true;
             case 3: return true;
             case 2: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 1: return "arguments";
             case 4: return "invokedBy";
             case 3: return "limit";
             case 2: return "limitOffset";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 1: return 0;
             case 4: return 1;
             case 3: return 2;
             case 2: return 3;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="arguments") return 1;
             if(name=="invokedBy") return 4;
             if(name=="limit") return 3;
             if(name=="limitOffset") return 2;
            
             if("arguments".equals(name)) return 1;
             if("invokedBy".equals(name)) return 4;
             if("limit".equals(name)) return 3;
             if("limitOffset".equals(name)) return 2;
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
             case 1: return  this._arguments; 
             case 4: return  this._invokedBy; 
             case 3: return  OH.valueOf(this._limit); 
             case 2: return  OH.valueOf(this._limitOffset); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 1: return java.util.List.class;
             case 4: return java.lang.String.class;
             case 3: return int.class;
             case 2: return int.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 1: return 21;
             case 4: return 20;
             case 3: return 4;
             case 2: return 4;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 1: this._arguments=(java.util.List)value;return;
             case 4: this._invokedBy=(java.lang.String)value;return;
             case 3: this._limit=(java.lang.Integer)value;return;
             case 2: this._limitOffset=(java.lang.Integer)value;return;
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
             case 1: this._arguments=(java.util.List)value;return true;
             case 4: this._invokedBy=(java.lang.String)value;return true;
             case 3: this._limit=(java.lang.Integer)value;return true;
             case 2: this._limitOffset=(java.lang.Integer)value;return true;
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
             case 3: return this._limit;
             case 2: return this._limitOffset;
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
             case 3: this._limit=value;return;
             case 2: this._limitOffset=value;return;
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
        
            this._arguments=(java.util.List)converter.read(session);
        
            break;

        case 2:
        
            if((basicType=in.readByte())!=4)
                break;
            this._limitOffset=in.readInt();
        
            break;

        case 3:
        
            if((basicType=in.readByte())!=4)
                break;
            this._limit=in.readInt();
        
            break;

        case 4:
        
            this._invokedBy=(java.lang.String)converter.read(session);
        
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
        
if(this._arguments!=null && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this._arguments,session);
        
}

if(this._limitOffset!=0 && (0 & transience)==0){
    out.writeByte(2);
        
    out.writeByte(4);
    out.writeInt(this._limitOffset);
        
}

if(this._limit!=0 && (0 & transience)==0){
    out.writeByte(3);
        
    out.writeByte(4);
    out.writeInt(this._limit);
        
}

if(this._invokedBy!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._invokedBy,session);
        
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