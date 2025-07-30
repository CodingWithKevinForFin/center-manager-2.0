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

public abstract class AmiCenterQuery0 implements com.f1.ami.amicommon.msg.AmiCenterQuery ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private boolean _allowSqlInjection;

    private java.util.Map _directives;

    private int _limit;

    private com.f1.utils.string.Node _parsedNode;

    private java.lang.String _query;

    private static final String NAMES[]={ "allowSqlInjection" ,"directives","limit","parsedNode","query"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 8;
        try{
        switch(h){

                case 0:

                    if(name == "query" || name.equals("query")) {this._query=(java.lang.String)value;return;}
break;
                case 2:

                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {this._allowSqlInjection=(java.lang.Boolean)value;return;}
break;
                case 3:

                    if(name == "limit" || name.equals("limit")) {this._limit=(java.lang.Integer)value;return;}
break;
                case 4:

                    if(name == "directives" || name.equals("directives")) {this._directives=(java.util.Map)value;return;}
break;
                case 5:

                    if(name == "parsedNode" || name.equals("parsedNode")) {this._parsedNode=(com.f1.utils.string.Node)value;return;}
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

                case 0:

                    if(name == "query" || name.equals("query")) {this._query=(java.lang.String)value;return true;}
break;
                case 2:

                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {this._allowSqlInjection=(java.lang.Boolean)value;return true;}
break;
                case 3:

                    if(name == "limit" || name.equals("limit")) {this._limit=(java.lang.Integer)value;return true;}
break;
                case 4:

                    if(name == "directives" || name.equals("directives")) {this._directives=(java.util.Map)value;return true;}
break;
                case 5:

                    if(name == "parsedNode" || name.equals("parsedNode")) {this._parsedNode=(com.f1.utils.string.Node)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 0:

		    
                    if(name == "query" || name.equals("query")) {return this._query;}
            
break;
                case 2:

		    
                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {return OH.valueOf(this._allowSqlInjection);}
		    
break;
                case 3:

		    
                    if(name == "limit" || name.equals("limit")) {return OH.valueOf(this._limit);}
		    
break;
                case 4:

		    
                    if(name == "directives" || name.equals("directives")) {return this._directives;}
            
break;
                case 5:

		    
                    if(name == "parsedNode" || name.equals("parsedNode")) {return this._parsedNode;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 0:

                    if(name == "query" || name.equals("query")) {return java.lang.String.class;}
break;
                case 2:

                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {return boolean.class;}
break;
                case 3:

                    if(name == "limit" || name.equals("limit")) {return int.class;}
break;
                case 4:

                    if(name == "directives" || name.equals("directives")) {return java.util.Map.class;}
break;
                case 5:

                    if(name == "parsedNode" || name.equals("parsedNode")) {return com.f1.utils.string.Node.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 0:

                    if(name == "query" || name.equals("query")) {return VALUED_PARAM_query;}
break;
                case 2:

                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {return VALUED_PARAM_allowSqlInjection;}
break;
                case 3:

                    if(name == "limit" || name.equals("limit")) {return VALUED_PARAM_limit;}
break;
                case 4:

                    if(name == "directives" || name.equals("directives")) {return VALUED_PARAM_directives;}
break;
                case 5:

                    if(name == "parsedNode" || name.equals("parsedNode")) {return VALUED_PARAM_parsedNode;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 0:

                    if(name == "query" || name.equals("query")) {return 4;}
break;
                case 2:

                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {return 0;}
break;
                case 3:

                    if(name == "limit" || name.equals("limit")) {return 2;}
break;
                case 4:

                    if(name == "directives" || name.equals("directives")) {return 1;}
break;
                case 5:

                    if(name == "parsedNode" || name.equals("parsedNode")) {return 3;}
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
	    return (Class)AmiCenterQuery0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 0:

                    if(name == "query" || name.equals("query")) {return true;}
break;
                case 2:

                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {return true;}
break;
                case 3:

                    if(name == "limit" || name.equals("limit")) {return true;}
break;
                case 4:

                    if(name == "directives" || name.equals("directives")) {return true;}
break;
                case 5:

                    if(name == "parsedNode" || name.equals("parsedNode")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 8;
        switch(h){

                case 0:

                    if(name == "query" || name.equals("query")) {return 20;}
break;
                case 2:

                    if(name == "allowSqlInjection" || name.equals("allowSqlInjection")) {return 0;}
break;
                case 3:

                    if(name == "limit" || name.equals("limit")) {return 4;}
break;
                case 4:

                    if(name == "directives" || name.equals("directives")) {return 23;}
break;
                case 5:

                    if(name == "parsedNode" || name.equals("parsedNode")) {return 60;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _allowSqlInjection;

        case 1:return _directives;

        case 2:return _limit;

        case 3:return _parsedNode;

        case 4:return _query;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 5);
    }

    public boolean getAllowSqlInjection(){
        return this._allowSqlInjection;
    }
    public void setAllowSqlInjection(boolean _allowSqlInjection){
    
        this._allowSqlInjection=_allowSqlInjection;
    }

    public java.util.Map getDirectives(){
        return this._directives;
    }
    public void setDirectives(java.util.Map _directives){
    
        this._directives=_directives;
    }

    public int getLimit(){
        return this._limit;
    }
    public void setLimit(int _limit){
    
        this._limit=_limit;
    }

    public com.f1.utils.string.Node getParsedNode(){
        return this._parsedNode;
    }
    public void setParsedNode(com.f1.utils.string.Node _parsedNode){
    
        this._parsedNode=_parsedNode;
    }

    public java.lang.String getQuery(){
        return this._query;
    }
    public void setQuery(java.lang.String _query){
    
        this._query=_query;
    }





  
    private static final class VALUED_PARAM_CLASS_allowSqlInjection extends AbstractValuedParam<AmiCenterQuery0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterQuery0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getAllowSqlInjection());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQuery0 valued, DataInput stream) throws IOException{
		    
		      valued.setAllowSqlInjection(stream.readBoolean());
		    
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
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "allowSqlInjection";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQuery0 valued) {
		    return (boolean)((AmiCenterQuery0)valued).getAllowSqlInjection();
	    }
    
	    @Override
	    public void setValue(AmiCenterQuery0 valued, Object value) {
		    valued.setAllowSqlInjection((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQuery0 source, AmiCenterQuery0 dest) {
		    dest.setAllowSqlInjection(source.getAllowSqlInjection());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQuery0 source, AmiCenterQuery0 dest) {
	        return OH.eq(dest.getAllowSqlInjection(),source.getAllowSqlInjection());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterQuery0 valued) {
		    return valued.getAllowSqlInjection();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterQuery0 valued, boolean value) {
		    valued.setAllowSqlInjection(value);
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
	    public void append(AmiCenterQuery0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAllowSqlInjection());
	        
	    }
	    @Override
	    public void append(AmiCenterQuery0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAllowSqlInjection());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean allowSqlInjection";
	    }
	    @Override
	    public void clear(AmiCenterQuery0 valued){
	       valued.setAllowSqlInjection(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_allowSqlInjection=new VALUED_PARAM_CLASS_allowSqlInjection();
  

  
    private static final class VALUED_PARAM_CLASS_directives extends AbstractValuedParam<AmiCenterQuery0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 23;
	    }
	    
	    @Override
	    public void write(AmiCenterQuery0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Map}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQuery0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Map}");
		    
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
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "directives";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQuery0 valued) {
		    return (java.util.Map)((AmiCenterQuery0)valued).getDirectives();
	    }
    
	    @Override
	    public void setValue(AmiCenterQuery0 valued, Object value) {
		    valued.setDirectives((java.util.Map)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQuery0 source, AmiCenterQuery0 dest) {
		    dest.setDirectives(source.getDirectives());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQuery0 source, AmiCenterQuery0 dest) {
	        return OH.eq(dest.getDirectives(),source.getDirectives());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.util.Map.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.util.Map.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiCenterQuery0 valued, StringBuilder sb){
	        
	        sb.append(valued.getDirectives());
	        
	    }
	    @Override
	    public void append(AmiCenterQuery0 valued, StringBuildable sb){
	        
	        sb.append(valued.getDirectives());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.Map directives";
	    }
	    @Override
	    public void clear(AmiCenterQuery0 valued){
	       valued.setDirectives(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_directives=new VALUED_PARAM_CLASS_directives();
  

  
    private static final class VALUED_PARAM_CLASS_limit extends AbstractValuedParam<AmiCenterQuery0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterQuery0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getLimit());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQuery0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiCenterQuery0 valued) {
		    return (int)((AmiCenterQuery0)valued).getLimit();
	    }
    
	    @Override
	    public void setValue(AmiCenterQuery0 valued, Object value) {
		    valued.setLimit((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQuery0 source, AmiCenterQuery0 dest) {
		    dest.setLimit(source.getLimit());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQuery0 source, AmiCenterQuery0 dest) {
	        return OH.eq(dest.getLimit(),source.getLimit());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterQuery0 valued) {
		    return valued.getLimit();
	    }
    
	    @Override
	    public void setInt(AmiCenterQuery0 valued, int value) {
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
	    public void append(AmiCenterQuery0 valued, StringBuilder sb){
	        
	        sb.append(valued.getLimit());
	        
	    }
	    @Override
	    public void append(AmiCenterQuery0 valued, StringBuildable sb){
	        
	        sb.append(valued.getLimit());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int limit";
	    }
	    @Override
	    public void clear(AmiCenterQuery0 valued){
	       valued.setLimit(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_limit=new VALUED_PARAM_CLASS_limit();
  

  
    private static final class VALUED_PARAM_CLASS_parsedNode extends AbstractValuedParam<AmiCenterQuery0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 60;
	    }
	    
	    @Override
	    public void write(AmiCenterQuery0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.utils.string.Node}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQuery0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.utils.string.Node}");
		    
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
		    return 3;
	    }
    
	    @Override
	    public byte getPid() {
            return 6;
	    }
    
	    @Override
	    public String getName() {
            return "parsedNode";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQuery0 valued) {
		    return (com.f1.utils.string.Node)((AmiCenterQuery0)valued).getParsedNode();
	    }
    
	    @Override
	    public void setValue(AmiCenterQuery0 valued, Object value) {
		    valued.setParsedNode((com.f1.utils.string.Node)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQuery0 source, AmiCenterQuery0 dest) {
		    dest.setParsedNode(source.getParsedNode());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQuery0 source, AmiCenterQuery0 dest) {
	        return OH.eq(dest.getParsedNode(),source.getParsedNode());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.utils.string.Node.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.utils.string.Node.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiCenterQuery0 valued, StringBuilder sb){
	        
	        sb.append(valued.getParsedNode());
	        
	    }
	    @Override
	    public void append(AmiCenterQuery0 valued, StringBuildable sb){
	        
	        sb.append(valued.getParsedNode());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.utils.string.Node parsedNode";
	    }
	    @Override
	    public void clear(AmiCenterQuery0 valued){
	       valued.setParsedNode(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_parsedNode=new VALUED_PARAM_CLASS_parsedNode();
  

  
    private static final class VALUED_PARAM_CLASS_query extends AbstractValuedParam<AmiCenterQuery0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterQuery0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterQuery0 valued, DataInput stream) throws IOException{
		    
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
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "query";
	    }
    
	    @Override
	    public Object getValue(AmiCenterQuery0 valued) {
		    return (java.lang.String)((AmiCenterQuery0)valued).getQuery();
	    }
    
	    @Override
	    public void setValue(AmiCenterQuery0 valued, Object value) {
		    valued.setQuery((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterQuery0 source, AmiCenterQuery0 dest) {
		    dest.setQuery(source.getQuery());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterQuery0 source, AmiCenterQuery0 dest) {
	        return OH.eq(dest.getQuery(),source.getQuery());
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
	    public void append(AmiCenterQuery0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getQuery(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterQuery0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getQuery(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String query";
	    }
	    @Override
	    public void clear(AmiCenterQuery0 valued){
	       valued.setQuery(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_query=new VALUED_PARAM_CLASS_query();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_allowSqlInjection, VALUED_PARAM_directives, VALUED_PARAM_limit, VALUED_PARAM_parsedNode, VALUED_PARAM_query, };



    private static final byte PIDS[]={ 7 ,4,3,6,1};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 7: return VALUED_PARAM_allowSqlInjection;
             case 4: return VALUED_PARAM_directives;
             case 3: return VALUED_PARAM_limit;
             case 6: return VALUED_PARAM_parsedNode;
             case 1: return VALUED_PARAM_query;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 7: return true;
             case 4: return true;
             case 3: return true;
             case 6: return true;
             case 1: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 7: return "allowSqlInjection";
             case 4: return "directives";
             case 3: return "limit";
             case 6: return "parsedNode";
             case 1: return "query";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 7: return 0;
             case 4: return 1;
             case 3: return 2;
             case 6: return 3;
             case 1: return 4;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="allowSqlInjection") return 7;
             if(name=="directives") return 4;
             if(name=="limit") return 3;
             if(name=="parsedNode") return 6;
             if(name=="query") return 1;
            
             if("allowSqlInjection".equals(name)) return 7;
             if("directives".equals(name)) return 4;
             if("limit".equals(name)) return 3;
             if("parsedNode".equals(name)) return 6;
             if("query".equals(name)) return 1;
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
             case 7: return  OH.valueOf(this._allowSqlInjection); 
             case 4: return  this._directives; 
             case 3: return  OH.valueOf(this._limit); 
             case 6: return  this._parsedNode; 
             case 1: return  this._query; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 7: return boolean.class;
             case 4: return java.util.Map.class;
             case 3: return int.class;
             case 6: return com.f1.utils.string.Node.class;
             case 1: return java.lang.String.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 7: return 0;
             case 4: return 23;
             case 3: return 4;
             case 6: return 60;
             case 1: return 20;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 7: this._allowSqlInjection=(java.lang.Boolean)value;return;
             case 4: this._directives=(java.util.Map)value;return;
             case 3: this._limit=(java.lang.Integer)value;return;
             case 6: this._parsedNode=(com.f1.utils.string.Node)value;return;
             case 1: this._query=(java.lang.String)value;return;
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
             case 7: this._allowSqlInjection=(java.lang.Boolean)value;return true;
             case 4: this._directives=(java.util.Map)value;return true;
             case 3: this._limit=(java.lang.Integer)value;return true;
             case 6: this._parsedNode=(com.f1.utils.string.Node)value;return true;
             case 1: this._query=(java.lang.String)value;return true;
            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
             case 7: return this._allowSqlInjection;
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
             case 7: this._allowSqlInjection=value;return;
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
        
            this._query=(java.lang.String)converter.read(session);
        
            break;

        case 3:
        
            if((basicType=in.readByte())!=4)
                break;
            this._limit=in.readInt();
        
            break;

        case 4:
        
            this._directives=(java.util.Map)converter.read(session);
        
            break;

        case 6:
        
            this._parsedNode=(com.f1.utils.string.Node)converter.read(session);
        
            break;

        case 7:
        
            if((basicType=in.readByte())!=0)
                break;
            this._allowSqlInjection=in.readBoolean();
        
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
        
if(this._query!=null && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this._query,session);
        
}

if(this._limit!=0 && (0 & transience)==0){
    out.writeByte(3);
        
    out.writeByte(4);
    out.writeInt(this._limit);
        
}

if(this._directives!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._directives,session);
        
}

if(this._parsedNode!=null && (0 & transience)==0){
    out.writeByte(6);
        
    converter.write(this._parsedNode,session);
        
}

if(this._allowSqlInjection!=false && (0 & transience)==0){
    out.writeByte(7);
        
    out.writeByte(0);
    out.writeBoolean(this._allowSqlInjection);
        
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