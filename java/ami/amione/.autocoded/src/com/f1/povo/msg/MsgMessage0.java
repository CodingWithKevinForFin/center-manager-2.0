//Coded by ValuedCodeTemplate
package com.f1.povo.msg;

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

public abstract class MsgMessage0 implements com.f1.povo.msg.MsgMessage ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private java.lang.Object _correlationId;

    private com.f1.base.Message _message;

    private java.lang.String _requestTopicSuffix;

    private java.lang.String _resultTopicSuffix;

    private java.lang.String _source;

    private static final String NAMES[]={ "correlationId" ,"message","requestTopicSuffix","resultTopicSuffix","source"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 11;
        try{
        switch(h){

                case 0:

                    if(name == "correlationId" || name.equals("correlationId")) {this._correlationId=(java.lang.Object)value;return;}
break;
                case 4:

                    if(name == "message" || name.equals("message")) {this._message=(com.f1.base.Message)value;return;}
break;
                case 6:

                    if(name == "requestTopicSuffix" || name.equals("requestTopicSuffix")) {this._requestTopicSuffix=(java.lang.String)value;return;}
break;
                case 7:

                    if(name == "resultTopicSuffix" || name.equals("resultTopicSuffix")) {this._resultTopicSuffix=(java.lang.String)value;return;}
break;
                case 10:

                    if(name == "source" || name.equals("source")) {this._source=(java.lang.String)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 0:

                    if(name == "correlationId" || name.equals("correlationId")) {this._correlationId=(java.lang.Object)value;return true;}
break;
                case 4:

                    if(name == "message" || name.equals("message")) {this._message=(com.f1.base.Message)value;return true;}
break;
                case 6:

                    if(name == "requestTopicSuffix" || name.equals("requestTopicSuffix")) {this._requestTopicSuffix=(java.lang.String)value;return true;}
break;
                case 7:

                    if(name == "resultTopicSuffix" || name.equals("resultTopicSuffix")) {this._resultTopicSuffix=(java.lang.String)value;return true;}
break;
                case 10:

                    if(name == "source" || name.equals("source")) {this._source=(java.lang.String)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 0:

		    
                    if(name == "correlationId" || name.equals("correlationId")) {return this._correlationId;}
            
break;
                case 4:

		    
                    if(name == "message" || name.equals("message")) {return this._message;}
            
break;
                case 6:

		    
                    if(name == "requestTopicSuffix" || name.equals("requestTopicSuffix")) {return this._requestTopicSuffix;}
            
break;
                case 7:

		    
                    if(name == "resultTopicSuffix" || name.equals("resultTopicSuffix")) {return this._resultTopicSuffix;}
            
break;
                case 10:

		    
                    if(name == "source" || name.equals("source")) {return this._source;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 0:

                    if(name == "correlationId" || name.equals("correlationId")) {return java.lang.Object.class;}
break;
                case 4:

                    if(name == "message" || name.equals("message")) {return com.f1.base.Message.class;}
break;
                case 6:

                    if(name == "requestTopicSuffix" || name.equals("requestTopicSuffix")) {return java.lang.String.class;}
break;
                case 7:

                    if(name == "resultTopicSuffix" || name.equals("resultTopicSuffix")) {return java.lang.String.class;}
break;
                case 10:

                    if(name == "source" || name.equals("source")) {return java.lang.String.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 0:

                    if(name == "correlationId" || name.equals("correlationId")) {return VALUED_PARAM_correlationId;}
break;
                case 4:

                    if(name == "message" || name.equals("message")) {return VALUED_PARAM_message;}
break;
                case 6:

                    if(name == "requestTopicSuffix" || name.equals("requestTopicSuffix")) {return VALUED_PARAM_requestTopicSuffix;}
break;
                case 7:

                    if(name == "resultTopicSuffix" || name.equals("resultTopicSuffix")) {return VALUED_PARAM_resultTopicSuffix;}
break;
                case 10:

                    if(name == "source" || name.equals("source")) {return VALUED_PARAM_source;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 0:

                    if(name == "correlationId" || name.equals("correlationId")) {return 0;}
break;
                case 4:

                    if(name == "message" || name.equals("message")) {return 1;}
break;
                case 6:

                    if(name == "requestTopicSuffix" || name.equals("requestTopicSuffix")) {return 2;}
break;
                case 7:

                    if(name == "resultTopicSuffix" || name.equals("resultTopicSuffix")) {return 3;}
break;
                case 10:

                    if(name == "source" || name.equals("source")) {return 4;}
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
	    return (Class)MsgMessage0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 0:

                    if(name == "correlationId" || name.equals("correlationId")) {return true;}
break;
                case 4:

                    if(name == "message" || name.equals("message")) {return true;}
break;
                case 6:

                    if(name == "requestTopicSuffix" || name.equals("requestTopicSuffix")) {return true;}
break;
                case 7:

                    if(name == "resultTopicSuffix" || name.equals("resultTopicSuffix")) {return true;}
break;
                case 10:

                    if(name == "source" || name.equals("source")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 0:

                    if(name == "correlationId" || name.equals("correlationId")) {return 18;}
break;
                case 4:

                    if(name == "message" || name.equals("message")) {return 41;}
break;
                case 6:

                    if(name == "requestTopicSuffix" || name.equals("requestTopicSuffix")) {return 20;}
break;
                case 7:

                    if(name == "resultTopicSuffix" || name.equals("resultTopicSuffix")) {return 20;}
break;
                case 10:

                    if(name == "source" || name.equals("source")) {return 20;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _correlationId;

        case 1:return _message;

        case 2:return _requestTopicSuffix;

        case 3:return _resultTopicSuffix;

        case 4:return _source;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 5);
    }

    public java.lang.Object getCorrelationId(){
        return this._correlationId;
    }
    public void setCorrelationId(java.lang.Object _correlationId){
    
        this._correlationId=_correlationId;
    }

    public com.f1.base.Message getMessage(){
        return this._message;
    }
    public void setMessage(com.f1.base.Message _message){
    
        this._message=_message;
    }

    public java.lang.String getRequestTopicSuffix(){
        return this._requestTopicSuffix;
    }
    public void setRequestTopicSuffix(java.lang.String _requestTopicSuffix){
    
        this._requestTopicSuffix=_requestTopicSuffix;
    }

    public java.lang.String getResultTopicSuffix(){
        return this._resultTopicSuffix;
    }
    public void setResultTopicSuffix(java.lang.String _resultTopicSuffix){
    
        this._resultTopicSuffix=_resultTopicSuffix;
    }

    public java.lang.String getSource(){
        return this._source;
    }
    public void setSource(java.lang.String _source){
    
        this._source=_source;
    }





  
    private static final class VALUED_PARAM_CLASS_correlationId extends AbstractValuedParam<MsgMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 18;
	    }
	    
	    @Override
	    public void write(MsgMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Object}");
		    
	    }
	    
	    @Override
	    public void read(MsgMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 0;
	    }
    
	    @Override
	    public byte getPid() {
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "correlationId";
	    }
    
	    @Override
	    public Object getValue(MsgMessage0 valued) {
		    return (java.lang.Object)((MsgMessage0)valued).getCorrelationId();
	    }
    
	    @Override
	    public void setValue(MsgMessage0 valued, Object value) {
		    valued.setCorrelationId((java.lang.Object)value);
	    }
    
	    @Override
	    public void copy(MsgMessage0 source, MsgMessage0 dest) {
		    dest.setCorrelationId(source.getCorrelationId());
	    }
	    
	    @Override
	    public boolean areEqual(MsgMessage0 source, MsgMessage0 dest) {
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
	    public void append(MsgMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getCorrelationId());
	        
	    }
	    @Override
	    public void append(MsgMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getCorrelationId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Object correlationId";
	    }
	    @Override
	    public void clear(MsgMessage0 valued){
	       valued.setCorrelationId(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_correlationId=new VALUED_PARAM_CLASS_correlationId();
  

  
    private static final class VALUED_PARAM_CLASS_message extends AbstractValuedParam<MsgMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 41;
	    }
	    
	    @Override
	    public void write(MsgMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.base.Message}");
		    
	    }
	    
	    @Override
	    public void read(MsgMessage0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.base.Message}");
		    
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
		    return 1;
	    }
    
	    @Override
	    public byte getPid() {
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "message";
	    }
    
	    @Override
	    public Object getValue(MsgMessage0 valued) {
		    return (com.f1.base.Message)((MsgMessage0)valued).getMessage();
	    }
    
	    @Override
	    public void setValue(MsgMessage0 valued, Object value) {
		    valued.setMessage((com.f1.base.Message)value);
	    }
    
	    @Override
	    public void copy(MsgMessage0 source, MsgMessage0 dest) {
		    dest.setMessage(source.getMessage());
	    }
	    
	    @Override
	    public boolean areEqual(MsgMessage0 source, MsgMessage0 dest) {
	        return OH.eq(dest.getMessage(),source.getMessage());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.base.Message.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.base.Message.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(MsgMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getMessage());
	        
	    }
	    @Override
	    public void append(MsgMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getMessage());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.base.Message message";
	    }
	    @Override
	    public void clear(MsgMessage0 valued){
	       valued.setMessage(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_message=new VALUED_PARAM_CLASS_message();
  

  
    private static final class VALUED_PARAM_CLASS_requestTopicSuffix extends AbstractValuedParam<MsgMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(MsgMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(MsgMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "requestTopicSuffix";
	    }
    
	    @Override
	    public Object getValue(MsgMessage0 valued) {
		    return (java.lang.String)((MsgMessage0)valued).getRequestTopicSuffix();
	    }
    
	    @Override
	    public void setValue(MsgMessage0 valued, Object value) {
		    valued.setRequestTopicSuffix((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(MsgMessage0 source, MsgMessage0 dest) {
		    dest.setRequestTopicSuffix(source.getRequestTopicSuffix());
	    }
	    
	    @Override
	    public boolean areEqual(MsgMessage0 source, MsgMessage0 dest) {
	        return OH.eq(dest.getRequestTopicSuffix(),source.getRequestTopicSuffix());
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
	    public void append(MsgMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getRequestTopicSuffix(),sb);
	        
	    }
	    @Override
	    public void append(MsgMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getRequestTopicSuffix(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String requestTopicSuffix";
	    }
	    @Override
	    public void clear(MsgMessage0 valued){
	       valued.setRequestTopicSuffix(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_requestTopicSuffix=new VALUED_PARAM_CLASS_requestTopicSuffix();
  

  
    private static final class VALUED_PARAM_CLASS_resultTopicSuffix extends AbstractValuedParam<MsgMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(MsgMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(MsgMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 3;
	    }
    
	    @Override
	    public byte getPid() {
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "resultTopicSuffix";
	    }
    
	    @Override
	    public Object getValue(MsgMessage0 valued) {
		    return (java.lang.String)((MsgMessage0)valued).getResultTopicSuffix();
	    }
    
	    @Override
	    public void setValue(MsgMessage0 valued, Object value) {
		    valued.setResultTopicSuffix((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(MsgMessage0 source, MsgMessage0 dest) {
		    dest.setResultTopicSuffix(source.getResultTopicSuffix());
	    }
	    
	    @Override
	    public boolean areEqual(MsgMessage0 source, MsgMessage0 dest) {
	        return OH.eq(dest.getResultTopicSuffix(),source.getResultTopicSuffix());
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
	    public void append(MsgMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getResultTopicSuffix(),sb);
	        
	    }
	    @Override
	    public void append(MsgMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getResultTopicSuffix(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String resultTopicSuffix";
	    }
	    @Override
	    public void clear(MsgMessage0 valued){
	       valued.setResultTopicSuffix(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_resultTopicSuffix=new VALUED_PARAM_CLASS_resultTopicSuffix();
  

  
    private static final class VALUED_PARAM_CLASS_source extends AbstractValuedParam<MsgMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(MsgMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(MsgMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 4;
	    }
    
	    @Override
	    public byte getPid() {
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "source";
	    }
    
	    @Override
	    public Object getValue(MsgMessage0 valued) {
		    return (java.lang.String)((MsgMessage0)valued).getSource();
	    }
    
	    @Override
	    public void setValue(MsgMessage0 valued, Object value) {
		    valued.setSource((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(MsgMessage0 source, MsgMessage0 dest) {
		    dest.setSource(source.getSource());
	    }
	    
	    @Override
	    public boolean areEqual(MsgMessage0 source, MsgMessage0 dest) {
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
	    public void append(MsgMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getSource(),sb);
	        
	    }
	    @Override
	    public void append(MsgMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getSource(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String source";
	    }
	    @Override
	    public void clear(MsgMessage0 valued){
	       valued.setSource(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_source=new VALUED_PARAM_CLASS_source();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_correlationId, VALUED_PARAM_message, VALUED_PARAM_requestTopicSuffix, VALUED_PARAM_resultTopicSuffix, VALUED_PARAM_source, };



    private static final byte PIDS[]={ 2 ,1,4,3,5};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 2: return VALUED_PARAM_correlationId;
             case 1: return VALUED_PARAM_message;
             case 4: return VALUED_PARAM_requestTopicSuffix;
             case 3: return VALUED_PARAM_resultTopicSuffix;
             case 5: return VALUED_PARAM_source;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 2: return true;
             case 1: return true;
             case 4: return true;
             case 3: return true;
             case 5: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 2: return "correlationId";
             case 1: return "message";
             case 4: return "requestTopicSuffix";
             case 3: return "resultTopicSuffix";
             case 5: return "source";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 2: return 0;
             case 1: return 1;
             case 4: return 2;
             case 3: return 3;
             case 5: return 4;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="correlationId") return 2;
             if(name=="message") return 1;
             if(name=="requestTopicSuffix") return 4;
             if(name=="resultTopicSuffix") return 3;
             if(name=="source") return 5;
            
             if("correlationId".equals(name)) return 2;
             if("message".equals(name)) return 1;
             if("requestTopicSuffix".equals(name)) return 4;
             if("resultTopicSuffix".equals(name)) return 3;
             if("source".equals(name)) return 5;
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
             case 2: return  this._correlationId; 
             case 1: return  this._message; 
             case 4: return  this._requestTopicSuffix; 
             case 3: return  this._resultTopicSuffix; 
             case 5: return  this._source; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 2: return java.lang.Object.class;
             case 1: return com.f1.base.Message.class;
             case 4: return java.lang.String.class;
             case 3: return java.lang.String.class;
             case 5: return java.lang.String.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 2: return 18;
             case 1: return 41;
             case 4: return 20;
             case 3: return 20;
             case 5: return 20;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 2: this._correlationId=(java.lang.Object)value;return;
             case 1: this._message=(com.f1.base.Message)value;return;
             case 4: this._requestTopicSuffix=(java.lang.String)value;return;
             case 3: this._resultTopicSuffix=(java.lang.String)value;return;
             case 5: this._source=(java.lang.String)value;return;
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
             case 2: this._correlationId=(java.lang.Object)value;return true;
             case 1: this._message=(com.f1.base.Message)value;return true;
             case 4: this._requestTopicSuffix=(java.lang.String)value;return true;
             case 3: this._resultTopicSuffix=(java.lang.String)value;return true;
             case 5: this._source=(java.lang.String)value;return true;
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
        
            this._message=(com.f1.base.Message)converter.read(session);
        
            break;

        case 2:
        
            this._correlationId=(java.lang.Object)converter.read(session);
        
            break;

        case 3:
        
            this._resultTopicSuffix=(java.lang.String)converter.read(session);
        
            break;

        case 4:
        
            this._requestTopicSuffix=(java.lang.String)converter.read(session);
        
            break;

        case 5:
        
            this._source=(java.lang.String)converter.read(session);
        
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
        
if(this._message!=null && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this._message,session);
        
}

if(this._correlationId!=null && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this._correlationId,session);
        
}

if(this._resultTopicSuffix!=null && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this._resultTopicSuffix,session);
        
}

if(this._requestTopicSuffix!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._requestTopicSuffix,session);
        
}

if(this._source!=null && (0 & transience)==0){
    out.writeByte(5);
        
    converter.write(this._source,session);
        
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