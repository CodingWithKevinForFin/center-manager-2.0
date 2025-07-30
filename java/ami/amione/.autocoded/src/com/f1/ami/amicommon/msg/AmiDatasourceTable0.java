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

public abstract class AmiDatasourceTable0 implements com.f1.ami.amicommon.msg.AmiDatasourceTable ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

    private int hashCode=ValuedHashCodeGenerator.next(this);
    
    @Override
    public int hashCode(){
      return this.hashCode;
    }
    
    @Override
	public com.f1.ami.amicommon.msg.AmiDatasourceTable clone(){
       try{
         
	       final com.f1.ami.amicommon.msg.AmiDatasourceTable r= (com.f1.ami.amicommon.msg.AmiDatasourceTable)super.clone();
	       ((AmiDatasourceTable0)r).__locked=false;
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
	
	
    

    private java.lang.String _collectionName;

    private java.util.List _columns;

    private java.lang.String _createTableClause;

    private java.lang.String _customQuery;

    private java.lang.String _customUse;

    private java.lang.String _datasourceName;

    private java.lang.String _name;

    private com.f1.base.Table _previewData;

    private java.lang.Long _previewTableSize;

    private static final String NAMES[]={ "collectionName" ,"columns","createTableClause","customQuery","customUse","datasourceName","name","previewData","previewTableSize"};

	@Override
    public void put(String name, Object value){//asdf
    
	   if(__locked)
	       throw newLockedException(name,value);
	
        final int h=Math.abs(name.hashCode()) % 15;
        try{
        switch(h){

                case 0:

                    if(name == "collectionName" || name.equals("collectionName")) {this._collectionName=(java.lang.String)value;return;}
break;
                case 1:

                    if(name == "previewTableSize" || name.equals("previewTableSize")) {this._previewTableSize=(java.lang.Long)value;return;}
break;
                case 3:

                    if(name == "customQuery" || name.equals("customQuery")) {this._customQuery=(java.lang.String)value;return;}
break;
                case 5:

                    if(name == "customUse" || name.equals("customUse")) {this._customUse=(java.lang.String)value;return;}
break;
                case 9:

                    if(name == "datasourceName" || name.equals("datasourceName")) {this._datasourceName=(java.lang.String)value;return;}
break;
                case 11:

                    if(name == "createTableClause" || name.equals("createTableClause")) {this._createTableClause=(java.lang.String)value;return;}
break;
                case 12:

                    if(name == "name" || name.equals("name")) {this._name=(java.lang.String)value;return;}
break;
                case 13:

                    if(name == "columns" || name.equals("columns")) {this._columns=(java.util.List)value;return;}
break;
                case 14:

                    if(name == "previewData" || name.equals("previewData")) {this._previewData=(com.f1.base.Table)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 15;
        switch(h){

                case 0:

                    if(name == "collectionName" || name.equals("collectionName")) {this._collectionName=(java.lang.String)value;return true;}
break;
                case 1:

                    if(name == "previewTableSize" || name.equals("previewTableSize")) {this._previewTableSize=(java.lang.Long)value;return true;}
break;
                case 3:

                    if(name == "customQuery" || name.equals("customQuery")) {this._customQuery=(java.lang.String)value;return true;}
break;
                case 5:

                    if(name == "customUse" || name.equals("customUse")) {this._customUse=(java.lang.String)value;return true;}
break;
                case 9:

                    if(name == "datasourceName" || name.equals("datasourceName")) {this._datasourceName=(java.lang.String)value;return true;}
break;
                case 11:

                    if(name == "createTableClause" || name.equals("createTableClause")) {this._createTableClause=(java.lang.String)value;return true;}
break;
                case 12:

                    if(name == "name" || name.equals("name")) {this._name=(java.lang.String)value;return true;}
break;
                case 13:

                    if(name == "columns" || name.equals("columns")) {this._columns=(java.util.List)value;return true;}
break;
                case 14:

                    if(name == "previewData" || name.equals("previewData")) {this._previewData=(com.f1.base.Table)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 15;
        switch(h){

                case 0:

		    
                    if(name == "collectionName" || name.equals("collectionName")) {return this._collectionName;}
            
break;
                case 1:

		    
                    if(name == "previewTableSize" || name.equals("previewTableSize")) {return this._previewTableSize;}
            
break;
                case 3:

		    
                    if(name == "customQuery" || name.equals("customQuery")) {return this._customQuery;}
            
break;
                case 5:

		    
                    if(name == "customUse" || name.equals("customUse")) {return this._customUse;}
            
break;
                case 9:

		    
                    if(name == "datasourceName" || name.equals("datasourceName")) {return this._datasourceName;}
            
break;
                case 11:

		    
                    if(name == "createTableClause" || name.equals("createTableClause")) {return this._createTableClause;}
            
break;
                case 12:

		    
                    if(name == "name" || name.equals("name")) {return this._name;}
            
break;
                case 13:

		    
                    if(name == "columns" || name.equals("columns")) {return this._columns;}
            
break;
                case 14:

		    
                    if(name == "previewData" || name.equals("previewData")) {return this._previewData;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 15;
        switch(h){

                case 0:

                    if(name == "collectionName" || name.equals("collectionName")) {return java.lang.String.class;}
break;
                case 1:

                    if(name == "previewTableSize" || name.equals("previewTableSize")) {return java.lang.Long.class;}
break;
                case 3:

                    if(name == "customQuery" || name.equals("customQuery")) {return java.lang.String.class;}
break;
                case 5:

                    if(name == "customUse" || name.equals("customUse")) {return java.lang.String.class;}
break;
                case 9:

                    if(name == "datasourceName" || name.equals("datasourceName")) {return java.lang.String.class;}
break;
                case 11:

                    if(name == "createTableClause" || name.equals("createTableClause")) {return java.lang.String.class;}
break;
                case 12:

                    if(name == "name" || name.equals("name")) {return java.lang.String.class;}
break;
                case 13:

                    if(name == "columns" || name.equals("columns")) {return java.util.List.class;}
break;
                case 14:

                    if(name == "previewData" || name.equals("previewData")) {return com.f1.base.Table.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 15;
        switch(h){

                case 0:

                    if(name == "collectionName" || name.equals("collectionName")) {return VALUED_PARAM_collectionName;}
break;
                case 1:

                    if(name == "previewTableSize" || name.equals("previewTableSize")) {return VALUED_PARAM_previewTableSize;}
break;
                case 3:

                    if(name == "customQuery" || name.equals("customQuery")) {return VALUED_PARAM_customQuery;}
break;
                case 5:

                    if(name == "customUse" || name.equals("customUse")) {return VALUED_PARAM_customUse;}
break;
                case 9:

                    if(name == "datasourceName" || name.equals("datasourceName")) {return VALUED_PARAM_datasourceName;}
break;
                case 11:

                    if(name == "createTableClause" || name.equals("createTableClause")) {return VALUED_PARAM_createTableClause;}
break;
                case 12:

                    if(name == "name" || name.equals("name")) {return VALUED_PARAM_name;}
break;
                case 13:

                    if(name == "columns" || name.equals("columns")) {return VALUED_PARAM_columns;}
break;
                case 14:

                    if(name == "previewData" || name.equals("previewData")) {return VALUED_PARAM_previewData;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 15;
        switch(h){

                case 0:

                    if(name == "collectionName" || name.equals("collectionName")) {return 0;}
break;
                case 1:

                    if(name == "previewTableSize" || name.equals("previewTableSize")) {return 8;}
break;
                case 3:

                    if(name == "customQuery" || name.equals("customQuery")) {return 3;}
break;
                case 5:

                    if(name == "customUse" || name.equals("customUse")) {return 4;}
break;
                case 9:

                    if(name == "datasourceName" || name.equals("datasourceName")) {return 5;}
break;
                case 11:

                    if(name == "createTableClause" || name.equals("createTableClause")) {return 2;}
break;
                case 12:

                    if(name == "name" || name.equals("name")) {return 6;}
break;
                case 13:

                    if(name == "columns" || name.equals("columns")) {return 1;}
break;
                case 14:

                    if(name == "previewData" || name.equals("previewData")) {return 7;}
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
        return 9;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiDatasourceTable0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 15;
        switch(h){

                case 0:

                    if(name == "collectionName" || name.equals("collectionName")) {return true;}
break;
                case 1:

                    if(name == "previewTableSize" || name.equals("previewTableSize")) {return true;}
break;
                case 3:

                    if(name == "customQuery" || name.equals("customQuery")) {return true;}
break;
                case 5:

                    if(name == "customUse" || name.equals("customUse")) {return true;}
break;
                case 9:

                    if(name == "datasourceName" || name.equals("datasourceName")) {return true;}
break;
                case 11:

                    if(name == "createTableClause" || name.equals("createTableClause")) {return true;}
break;
                case 12:

                    if(name == "name" || name.equals("name")) {return true;}
break;
                case 13:

                    if(name == "columns" || name.equals("columns")) {return true;}
break;
                case 14:

                    if(name == "previewData" || name.equals("previewData")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 15;
        switch(h){

                case 0:

                    if(name == "collectionName" || name.equals("collectionName")) {return 20;}
break;
                case 1:

                    if(name == "previewTableSize" || name.equals("previewTableSize")) {return 16;}
break;
                case 3:

                    if(name == "customQuery" || name.equals("customQuery")) {return 20;}
break;
                case 5:

                    if(name == "customUse" || name.equals("customUse")) {return 20;}
break;
                case 9:

                    if(name == "datasourceName" || name.equals("datasourceName")) {return 20;}
break;
                case 11:

                    if(name == "createTableClause" || name.equals("createTableClause")) {return 20;}
break;
                case 12:

                    if(name == "name" || name.equals("name")) {return 20;}
break;
                case 13:

                    if(name == "columns" || name.equals("columns")) {return 21;}
break;
                case 14:

                    if(name == "previewData" || name.equals("previewData")) {return 43;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _collectionName;

        case 1:return _columns;

        case 2:return _createTableClause;

        case 3:return _customQuery;

        case 4:return _customUse;

        case 5:return _datasourceName;

        case 6:return _name;

        case 7:return _previewData;

        case 8:return _previewTableSize;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 9);
    }

    public java.lang.String getCollectionName(){
        return this._collectionName;
    }
    public void setCollectionName(java.lang.String _collectionName){
    
	   if(__locked)
	       throw newLockedException("collectionName",_collectionName);
	
        this._collectionName=_collectionName;
    }

    public java.util.List getColumns(){
        return this._columns;
    }
    public void setColumns(java.util.List _columns){
    
	   if(__locked)
	       throw newLockedException("columns",_columns);
	
        this._columns=_columns;
    }

    public java.lang.String getCreateTableClause(){
        return this._createTableClause;
    }
    public void setCreateTableClause(java.lang.String _createTableClause){
    
	   if(__locked)
	       throw newLockedException("createTableClause",_createTableClause);
	
        this._createTableClause=_createTableClause;
    }

    public java.lang.String getCustomQuery(){
        return this._customQuery;
    }
    public void setCustomQuery(java.lang.String _customQuery){
    
	   if(__locked)
	       throw newLockedException("customQuery",_customQuery);
	
        this._customQuery=_customQuery;
    }

    public java.lang.String getCustomUse(){
        return this._customUse;
    }
    public void setCustomUse(java.lang.String _customUse){
    
	   if(__locked)
	       throw newLockedException("customUse",_customUse);
	
        this._customUse=_customUse;
    }

    public java.lang.String getDatasourceName(){
        return this._datasourceName;
    }
    public void setDatasourceName(java.lang.String _datasourceName){
    
	   if(__locked)
	       throw newLockedException("datasourceName",_datasourceName);
	
        this._datasourceName=_datasourceName;
    }

    public java.lang.String getName(){
        return this._name;
    }
    public void setName(java.lang.String _name){
    
	   if(__locked)
	       throw newLockedException("name",_name);
	
        this._name=_name;
    }

    public com.f1.base.Table getPreviewData(){
        return this._previewData;
    }
    public void setPreviewData(com.f1.base.Table _previewData){
    
	   if(__locked)
	       throw newLockedException("previewData",_previewData);
	
        this._previewData=_previewData;
    }

    public java.lang.Long getPreviewTableSize(){
        return this._previewTableSize;
    }
    public void setPreviewTableSize(java.lang.Long _previewTableSize){
    
	   if(__locked)
	       throw newLockedException("previewTableSize",_previewTableSize);
	
        this._previewTableSize=_previewTableSize;
    }





  
    private static final class VALUED_PARAM_CLASS_collectionName extends AbstractValuedParam<AmiDatasourceTable0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiDatasourceTable0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiDatasourceTable0 valued, DataInput stream) throws IOException{
		    
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
		    return 0;
	    }
    
	    @Override
	    public byte getPid() {
            return 6;
	    }
    
	    @Override
	    public String getName() {
            return "collectionName";
	    }
    
	    @Override
	    public Object getValue(AmiDatasourceTable0 valued) {
		    return (java.lang.String)((AmiDatasourceTable0)valued).getCollectionName();
	    }
    
	    @Override
	    public void setValue(AmiDatasourceTable0 valued, Object value) {
		    valued.setCollectionName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
		    dest.setCollectionName(source.getCollectionName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
	        return OH.eq(dest.getCollectionName(),source.getCollectionName());
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
	    public void append(AmiDatasourceTable0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getCollectionName(),sb);
	        
	    }
	    @Override
	    public void append(AmiDatasourceTable0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getCollectionName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String collectionName";
	    }
	    @Override
	    public void clear(AmiDatasourceTable0 valued){
	       valued.setCollectionName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_collectionName=new VALUED_PARAM_CLASS_collectionName();
  

  
    private static final class VALUED_PARAM_CLASS_columns extends AbstractValuedParam<AmiDatasourceTable0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiDatasourceTable0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiDatasourceTable0 valued, DataInput stream) throws IOException{
		    
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
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "columns";
	    }
    
	    @Override
	    public Object getValue(AmiDatasourceTable0 valued) {
		    return (java.util.List)((AmiDatasourceTable0)valued).getColumns();
	    }
    
	    @Override
	    public void setValue(AmiDatasourceTable0 valued, Object value) {
		    valued.setColumns((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
		    dest.setColumns(source.getColumns());
	    }
	    
	    @Override
	    public boolean areEqual(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
	        return OH.eq(dest.getColumns(),source.getColumns());
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
	    public void append(AmiDatasourceTable0 valued, StringBuilder sb){
	        
	        sb.append(valued.getColumns());
	        
	    }
	    @Override
	    public void append(AmiDatasourceTable0 valued, StringBuildable sb){
	        
	        sb.append(valued.getColumns());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List columns";
	    }
	    @Override
	    public void clear(AmiDatasourceTable0 valued){
	       valued.setColumns(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_columns=new VALUED_PARAM_CLASS_columns();
  

  
    private static final class VALUED_PARAM_CLASS_createTableClause extends AbstractValuedParam<AmiDatasourceTable0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiDatasourceTable0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiDatasourceTable0 valued, DataInput stream) throws IOException{
		    
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
            return 9;
	    }
    
	    @Override
	    public String getName() {
            return "createTableClause";
	    }
    
	    @Override
	    public Object getValue(AmiDatasourceTable0 valued) {
		    return (java.lang.String)((AmiDatasourceTable0)valued).getCreateTableClause();
	    }
    
	    @Override
	    public void setValue(AmiDatasourceTable0 valued, Object value) {
		    valued.setCreateTableClause((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
		    dest.setCreateTableClause(source.getCreateTableClause());
	    }
	    
	    @Override
	    public boolean areEqual(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
	        return OH.eq(dest.getCreateTableClause(),source.getCreateTableClause());
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
	    public void append(AmiDatasourceTable0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getCreateTableClause(),sb);
	        
	    }
	    @Override
	    public void append(AmiDatasourceTable0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getCreateTableClause(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String createTableClause";
	    }
	    @Override
	    public void clear(AmiDatasourceTable0 valued){
	       valued.setCreateTableClause(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_createTableClause=new VALUED_PARAM_CLASS_createTableClause();
  

  
    private static final class VALUED_PARAM_CLASS_customQuery extends AbstractValuedParam<AmiDatasourceTable0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiDatasourceTable0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiDatasourceTable0 valued, DataInput stream) throws IOException{
		    
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
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "customQuery";
	    }
    
	    @Override
	    public Object getValue(AmiDatasourceTable0 valued) {
		    return (java.lang.String)((AmiDatasourceTable0)valued).getCustomQuery();
	    }
    
	    @Override
	    public void setValue(AmiDatasourceTable0 valued, Object value) {
		    valued.setCustomQuery((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
		    dest.setCustomQuery(source.getCustomQuery());
	    }
	    
	    @Override
	    public boolean areEqual(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
	        return OH.eq(dest.getCustomQuery(),source.getCustomQuery());
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
	    public void append(AmiDatasourceTable0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getCustomQuery(),sb);
	        
	    }
	    @Override
	    public void append(AmiDatasourceTable0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getCustomQuery(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String customQuery";
	    }
	    @Override
	    public void clear(AmiDatasourceTable0 valued){
	       valued.setCustomQuery(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_customQuery=new VALUED_PARAM_CLASS_customQuery();
  

  
    private static final class VALUED_PARAM_CLASS_customUse extends AbstractValuedParam<AmiDatasourceTable0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiDatasourceTable0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiDatasourceTable0 valued, DataInput stream) throws IOException{
		    
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
            return 8;
	    }
    
	    @Override
	    public String getName() {
            return "customUse";
	    }
    
	    @Override
	    public Object getValue(AmiDatasourceTable0 valued) {
		    return (java.lang.String)((AmiDatasourceTable0)valued).getCustomUse();
	    }
    
	    @Override
	    public void setValue(AmiDatasourceTable0 valued, Object value) {
		    valued.setCustomUse((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
		    dest.setCustomUse(source.getCustomUse());
	    }
	    
	    @Override
	    public boolean areEqual(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
	        return OH.eq(dest.getCustomUse(),source.getCustomUse());
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
	    public void append(AmiDatasourceTable0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getCustomUse(),sb);
	        
	    }
	    @Override
	    public void append(AmiDatasourceTable0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getCustomUse(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String customUse";
	    }
	    @Override
	    public void clear(AmiDatasourceTable0 valued){
	       valued.setCustomUse(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_customUse=new VALUED_PARAM_CLASS_customUse();
  

  
    private static final class VALUED_PARAM_CLASS_datasourceName extends AbstractValuedParam<AmiDatasourceTable0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiDatasourceTable0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiDatasourceTable0 valued, DataInput stream) throws IOException{
		    
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
		    return 5;
	    }
    
	    @Override
	    public byte getPid() {
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "datasourceName";
	    }
    
	    @Override
	    public Object getValue(AmiDatasourceTable0 valued) {
		    return (java.lang.String)((AmiDatasourceTable0)valued).getDatasourceName();
	    }
    
	    @Override
	    public void setValue(AmiDatasourceTable0 valued, Object value) {
		    valued.setDatasourceName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
		    dest.setDatasourceName(source.getDatasourceName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
	        return OH.eq(dest.getDatasourceName(),source.getDatasourceName());
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
	    public void append(AmiDatasourceTable0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getDatasourceName(),sb);
	        
	    }
	    @Override
	    public void append(AmiDatasourceTable0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getDatasourceName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String datasourceName";
	    }
	    @Override
	    public void clear(AmiDatasourceTable0 valued){
	       valued.setDatasourceName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_datasourceName=new VALUED_PARAM_CLASS_datasourceName();
  

  
    private static final class VALUED_PARAM_CLASS_name extends AbstractValuedParam<AmiDatasourceTable0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiDatasourceTable0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiDatasourceTable0 valued, DataInput stream) throws IOException{
		    
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
		    return 6;
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
	    public Object getValue(AmiDatasourceTable0 valued) {
		    return (java.lang.String)((AmiDatasourceTable0)valued).getName();
	    }
    
	    @Override
	    public void setValue(AmiDatasourceTable0 valued, Object value) {
		    valued.setName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
		    dest.setName(source.getName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
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
	    public void append(AmiDatasourceTable0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getName(),sb);
	        
	    }
	    @Override
	    public void append(AmiDatasourceTable0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String name";
	    }
	    @Override
	    public void clear(AmiDatasourceTable0 valued){
	       valued.setName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_name=new VALUED_PARAM_CLASS_name();
  

  
    private static final class VALUED_PARAM_CLASS_previewData extends AbstractValuedParam<AmiDatasourceTable0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 43;
	    }
	    
	    @Override
	    public void write(AmiDatasourceTable0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.base.Table}");
		    
	    }
	    
	    @Override
	    public void read(AmiDatasourceTable0 valued, DataInput stream) throws IOException{
		    
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
		    return 7;
	    }
    
	    @Override
	    public byte getPid() {
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "previewData";
	    }
    
	    @Override
	    public Object getValue(AmiDatasourceTable0 valued) {
		    return (com.f1.base.Table)((AmiDatasourceTable0)valued).getPreviewData();
	    }
    
	    @Override
	    public void setValue(AmiDatasourceTable0 valued, Object value) {
		    valued.setPreviewData((com.f1.base.Table)value);
	    }
    
	    @Override
	    public void copy(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
		    dest.setPreviewData(source.getPreviewData());
	    }
	    
	    @Override
	    public boolean areEqual(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
	        return OH.eq(dest.getPreviewData(),source.getPreviewData());
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
	    public void append(AmiDatasourceTable0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPreviewData());
	        
	    }
	    @Override
	    public void append(AmiDatasourceTable0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPreviewData());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.base.Table previewData";
	    }
	    @Override
	    public void clear(AmiDatasourceTable0 valued){
	       valued.setPreviewData(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_previewData=new VALUED_PARAM_CLASS_previewData();
  

  
    private static final class VALUED_PARAM_CLASS_previewTableSize extends AbstractValuedParam<AmiDatasourceTable0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 16;
	    }
	    
	    @Override
	    public void write(AmiDatasourceTable0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Long}");
		    
	    }
	    
	    @Override
	    public void read(AmiDatasourceTable0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Long}");
		    
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
		    return true;
	    }
    
	    @Override
	    public boolean isPrimitiveOrBoxed() {
		    return true || false;
	    }
	    @Override
	    public boolean isImmutable() {
		    return true;
	    }
    
	    @Override
	    public int askPosition() {
		    return 8;
	    }
    
	    @Override
	    public byte getPid() {
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "previewTableSize";
	    }
    
	    @Override
	    public Object getValue(AmiDatasourceTable0 valued) {
		    return (java.lang.Long)((AmiDatasourceTable0)valued).getPreviewTableSize();
	    }
    
	    @Override
	    public void setValue(AmiDatasourceTable0 valued, Object value) {
		    valued.setPreviewTableSize((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
		    dest.setPreviewTableSize(source.getPreviewTableSize());
	    }
	    
	    @Override
	    public boolean areEqual(AmiDatasourceTable0 source, AmiDatasourceTable0 dest) {
	        return OH.eq(dest.getPreviewTableSize(),source.getPreviewTableSize());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.Long.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.Long.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiDatasourceTable0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPreviewTableSize());
	        
	    }
	    @Override
	    public void append(AmiDatasourceTable0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPreviewTableSize());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Long previewTableSize";
	    }
	    @Override
	    public void clear(AmiDatasourceTable0 valued){
	       valued.setPreviewTableSize(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_previewTableSize=new VALUED_PARAM_CLASS_previewTableSize();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_collectionName, VALUED_PARAM_columns, VALUED_PARAM_createTableClause, VALUED_PARAM_customQuery, VALUED_PARAM_customUse, VALUED_PARAM_datasourceName, VALUED_PARAM_name, VALUED_PARAM_previewData, VALUED_PARAM_previewTableSize, };



    private static final byte PIDS[]={ 6 ,2,9,4,8,3,1,5,7};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 6: return VALUED_PARAM_collectionName;
             case 2: return VALUED_PARAM_columns;
             case 9: return VALUED_PARAM_createTableClause;
             case 4: return VALUED_PARAM_customQuery;
             case 8: return VALUED_PARAM_customUse;
             case 3: return VALUED_PARAM_datasourceName;
             case 1: return VALUED_PARAM_name;
             case 5: return VALUED_PARAM_previewData;
             case 7: return VALUED_PARAM_previewTableSize;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 6: return true;
             case 2: return true;
             case 9: return true;
             case 4: return true;
             case 8: return true;
             case 3: return true;
             case 1: return true;
             case 5: return true;
             case 7: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 6: return "collectionName";
             case 2: return "columns";
             case 9: return "createTableClause";
             case 4: return "customQuery";
             case 8: return "customUse";
             case 3: return "datasourceName";
             case 1: return "name";
             case 5: return "previewData";
             case 7: return "previewTableSize";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 6: return 0;
             case 2: return 1;
             case 9: return 2;
             case 4: return 3;
             case 8: return 4;
             case 3: return 5;
             case 1: return 6;
             case 5: return 7;
             case 7: return 8;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="collectionName") return 6;
             if(name=="columns") return 2;
             if(name=="createTableClause") return 9;
             if(name=="customQuery") return 4;
             if(name=="customUse") return 8;
             if(name=="datasourceName") return 3;
             if(name=="name") return 1;
             if(name=="previewData") return 5;
             if(name=="previewTableSize") return 7;
            
             if("collectionName".equals(name)) return 6;
             if("columns".equals(name)) return 2;
             if("createTableClause".equals(name)) return 9;
             if("customQuery".equals(name)) return 4;
             if("customUse".equals(name)) return 8;
             if("datasourceName".equals(name)) return 3;
             if("name".equals(name)) return 1;
             if("previewData".equals(name)) return 5;
             if("previewTableSize".equals(name)) return 7;
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
             case 6: return  this._collectionName; 
             case 2: return  this._columns; 
             case 9: return  this._createTableClause; 
             case 4: return  this._customQuery; 
             case 8: return  this._customUse; 
             case 3: return  this._datasourceName; 
             case 1: return  this._name; 
             case 5: return  this._previewData; 
             case 7: return  this._previewTableSize; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 6: return java.lang.String.class;
             case 2: return java.util.List.class;
             case 9: return java.lang.String.class;
             case 4: return java.lang.String.class;
             case 8: return java.lang.String.class;
             case 3: return java.lang.String.class;
             case 1: return java.lang.String.class;
             case 5: return com.f1.base.Table.class;
             case 7: return java.lang.Long.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 6: return 20;
             case 2: return 21;
             case 9: return 20;
             case 4: return 20;
             case 8: return 20;
             case 3: return 20;
             case 1: return 20;
             case 5: return 43;
             case 7: return 16;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 6: this._collectionName=(java.lang.String)value;return;
             case 2: this._columns=(java.util.List)value;return;
             case 9: this._createTableClause=(java.lang.String)value;return;
             case 4: this._customQuery=(java.lang.String)value;return;
             case 8: this._customUse=(java.lang.String)value;return;
             case 3: this._datasourceName=(java.lang.String)value;return;
             case 1: this._name=(java.lang.String)value;return;
             case 5: this._previewData=(com.f1.base.Table)value;return;
             case 7: this._previewTableSize=(java.lang.Long)value;return;
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
             case 6: this._collectionName=(java.lang.String)value;return true;
             case 2: this._columns=(java.util.List)value;return true;
             case 9: this._createTableClause=(java.lang.String)value;return true;
             case 4: this._customQuery=(java.lang.String)value;return true;
             case 8: this._customUse=(java.lang.String)value;return true;
             case 3: this._datasourceName=(java.lang.String)value;return true;
             case 1: this._name=(java.lang.String)value;return true;
             case 5: this._previewData=(com.f1.base.Table)value;return true;
             case 7: this._previewTableSize=(java.lang.Long)value;return true;
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
             case 7: return this._previewTableSize;
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
             case 7: this._previewTableSize=value;return;
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
        
            this._columns=(java.util.List)converter.read(session);
        
            break;

        case 3:
        
            this._datasourceName=(java.lang.String)converter.read(session);
        
            break;

        case 4:
        
            this._customQuery=(java.lang.String)converter.read(session);
        
            break;

        case 5:
        
            this._previewData=(com.f1.base.Table)converter.read(session);
        
            break;

        case 6:
        
            this._collectionName=(java.lang.String)converter.read(session);
        
            break;

        case 7:
        
            this._previewTableSize=(java.lang.Long)converter.read(session);
        
            break;

        case 8:
        
            this._customUse=(java.lang.String)converter.read(session);
        
            break;

        case 9:
        
            this._createTableClause=(java.lang.String)converter.read(session);
        
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

if(this._columns!=null && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this._columns,session);
        
}

if(this._datasourceName!=null && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this._datasourceName,session);
        
}

if(this._customQuery!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._customQuery,session);
        
}

if(this._previewData!=null && (0 & transience)==0){
    out.writeByte(5);
        
    converter.write(this._previewData,session);
        
}

if(this._collectionName!=null && (0 & transience)==0){
    out.writeByte(6);
        
    converter.write(this._collectionName,session);
        
}

if(this._previewTableSize!=null && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this._previewTableSize,session);
        
}

if(this._customUse!=null && (0 & transience)==0){
    out.writeByte(8);
        
    converter.write(this._customUse,session);
        
}

if(this._createTableClause!=null && (0 & transience)==0){
    out.writeByte(9);
        
    converter.write(this._createTableClause,session);
        
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