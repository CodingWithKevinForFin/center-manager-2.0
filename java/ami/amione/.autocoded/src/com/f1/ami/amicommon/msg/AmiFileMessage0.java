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

public abstract class AmiFileMessage0 implements com.f1.ami.amicommon.msg.AmiFileMessage ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private java.lang.String _absolutePath;

    private byte[] _data;

    private java.lang.String[] _fileNames;

    private java.util.List _files;

    private short _flags;

    private java.lang.String _fullPath;

    private long _lastModified;

    private long _length;

    private java.lang.String _name;

    private java.lang.String _parentPath;

    private java.lang.String _path;

    private static final String NAMES[]={ "absolutePath" ,"data","fileNames","files","flags","fullPath","lastModified","length","name","parentPath","path"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 35;
        try{
        switch(h){

                case 0:

                    if(name == "data" || name.equals("data")) {this._data=(byte[])value;return;}
break;
                case 5:

                    if(name == "fileNames" || name.equals("fileNames")) {this._fileNames=(java.lang.String[])value;return;}
break;
                case 6:

                    if(name == "files" || name.equals("files")) {this._files=(java.util.List)value;return;}
break;
                case 9:

                    if(name == "path" || name.equals("path")) {this._path=(java.lang.String)value;return;}
break;
                case 10:

                    if(name == "absolutePath" || name.equals("absolutePath")) {this._absolutePath=(java.lang.String)value;return;}
break;
                case 14:

                    if(name == "parentPath" || name.equals("parentPath")) {this._parentPath=(java.lang.String)value;return;}
break;
                case 15:

                    if(name == "flags" || name.equals("flags")) {this._flags=(java.lang.Short)value;return;}
break;
                case 17:

                    if(name == "lastModified" || name.equals("lastModified")) {this._lastModified=(java.lang.Long)value;return;}
break;
                case 19:

                    if(name == "fullPath" || name.equals("fullPath")) {this._fullPath=(java.lang.String)value;return;}
break;
                case 22:

                    if(name == "name" || name.equals("name")) {this._name=(java.lang.String)value;return;}
break;
                case 24:

                    if(name == "length" || name.equals("length")) {this._length=(java.lang.Long)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 35;
        switch(h){

                case 0:

                    if(name == "data" || name.equals("data")) {this._data=(byte[])value;return true;}
break;
                case 5:

                    if(name == "fileNames" || name.equals("fileNames")) {this._fileNames=(java.lang.String[])value;return true;}
break;
                case 6:

                    if(name == "files" || name.equals("files")) {this._files=(java.util.List)value;return true;}
break;
                case 9:

                    if(name == "path" || name.equals("path")) {this._path=(java.lang.String)value;return true;}
break;
                case 10:

                    if(name == "absolutePath" || name.equals("absolutePath")) {this._absolutePath=(java.lang.String)value;return true;}
break;
                case 14:

                    if(name == "parentPath" || name.equals("parentPath")) {this._parentPath=(java.lang.String)value;return true;}
break;
                case 15:

                    if(name == "flags" || name.equals("flags")) {this._flags=(java.lang.Short)value;return true;}
break;
                case 17:

                    if(name == "lastModified" || name.equals("lastModified")) {this._lastModified=(java.lang.Long)value;return true;}
break;
                case 19:

                    if(name == "fullPath" || name.equals("fullPath")) {this._fullPath=(java.lang.String)value;return true;}
break;
                case 22:

                    if(name == "name" || name.equals("name")) {this._name=(java.lang.String)value;return true;}
break;
                case 24:

                    if(name == "length" || name.equals("length")) {this._length=(java.lang.Long)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 35;
        switch(h){

                case 0:

		    
                    if(name == "data" || name.equals("data")) {return this._data;}
            
break;
                case 5:

		    
                    if(name == "fileNames" || name.equals("fileNames")) {return this._fileNames;}
            
break;
                case 6:

		    
                    if(name == "files" || name.equals("files")) {return this._files;}
            
break;
                case 9:

		    
                    if(name == "path" || name.equals("path")) {return this._path;}
            
break;
                case 10:

		    
                    if(name == "absolutePath" || name.equals("absolutePath")) {return this._absolutePath;}
            
break;
                case 14:

		    
                    if(name == "parentPath" || name.equals("parentPath")) {return this._parentPath;}
            
break;
                case 15:

		    
                    if(name == "flags" || name.equals("flags")) {return OH.valueOf(this._flags);}
		    
break;
                case 17:

		    
                    if(name == "lastModified" || name.equals("lastModified")) {return OH.valueOf(this._lastModified);}
		    
break;
                case 19:

		    
                    if(name == "fullPath" || name.equals("fullPath")) {return this._fullPath;}
            
break;
                case 22:

		    
                    if(name == "name" || name.equals("name")) {return this._name;}
            
break;
                case 24:

		    
                    if(name == "length" || name.equals("length")) {return OH.valueOf(this._length);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 35;
        switch(h){

                case 0:

                    if(name == "data" || name.equals("data")) {return byte[].class;}
break;
                case 5:

                    if(name == "fileNames" || name.equals("fileNames")) {return java.lang.String[].class;}
break;
                case 6:

                    if(name == "files" || name.equals("files")) {return java.util.List.class;}
break;
                case 9:

                    if(name == "path" || name.equals("path")) {return java.lang.String.class;}
break;
                case 10:

                    if(name == "absolutePath" || name.equals("absolutePath")) {return java.lang.String.class;}
break;
                case 14:

                    if(name == "parentPath" || name.equals("parentPath")) {return java.lang.String.class;}
break;
                case 15:

                    if(name == "flags" || name.equals("flags")) {return short.class;}
break;
                case 17:

                    if(name == "lastModified" || name.equals("lastModified")) {return long.class;}
break;
                case 19:

                    if(name == "fullPath" || name.equals("fullPath")) {return java.lang.String.class;}
break;
                case 22:

                    if(name == "name" || name.equals("name")) {return java.lang.String.class;}
break;
                case 24:

                    if(name == "length" || name.equals("length")) {return long.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 35;
        switch(h){

                case 0:

                    if(name == "data" || name.equals("data")) {return VALUED_PARAM_data;}
break;
                case 5:

                    if(name == "fileNames" || name.equals("fileNames")) {return VALUED_PARAM_fileNames;}
break;
                case 6:

                    if(name == "files" || name.equals("files")) {return VALUED_PARAM_files;}
break;
                case 9:

                    if(name == "path" || name.equals("path")) {return VALUED_PARAM_path;}
break;
                case 10:

                    if(name == "absolutePath" || name.equals("absolutePath")) {return VALUED_PARAM_absolutePath;}
break;
                case 14:

                    if(name == "parentPath" || name.equals("parentPath")) {return VALUED_PARAM_parentPath;}
break;
                case 15:

                    if(name == "flags" || name.equals("flags")) {return VALUED_PARAM_flags;}
break;
                case 17:

                    if(name == "lastModified" || name.equals("lastModified")) {return VALUED_PARAM_lastModified;}
break;
                case 19:

                    if(name == "fullPath" || name.equals("fullPath")) {return VALUED_PARAM_fullPath;}
break;
                case 22:

                    if(name == "name" || name.equals("name")) {return VALUED_PARAM_name;}
break;
                case 24:

                    if(name == "length" || name.equals("length")) {return VALUED_PARAM_length;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 35;
        switch(h){

                case 0:

                    if(name == "data" || name.equals("data")) {return 1;}
break;
                case 5:

                    if(name == "fileNames" || name.equals("fileNames")) {return 2;}
break;
                case 6:

                    if(name == "files" || name.equals("files")) {return 3;}
break;
                case 9:

                    if(name == "path" || name.equals("path")) {return 10;}
break;
                case 10:

                    if(name == "absolutePath" || name.equals("absolutePath")) {return 0;}
break;
                case 14:

                    if(name == "parentPath" || name.equals("parentPath")) {return 9;}
break;
                case 15:

                    if(name == "flags" || name.equals("flags")) {return 4;}
break;
                case 17:

                    if(name == "lastModified" || name.equals("lastModified")) {return 6;}
break;
                case 19:

                    if(name == "fullPath" || name.equals("fullPath")) {return 5;}
break;
                case 22:

                    if(name == "name" || name.equals("name")) {return 8;}
break;
                case 24:

                    if(name == "length" || name.equals("length")) {return 7;}
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
        return 11;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiFileMessage0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 35;
        switch(h){

                case 0:

                    if(name == "data" || name.equals("data")) {return true;}
break;
                case 5:

                    if(name == "fileNames" || name.equals("fileNames")) {return true;}
break;
                case 6:

                    if(name == "files" || name.equals("files")) {return true;}
break;
                case 9:

                    if(name == "path" || name.equals("path")) {return true;}
break;
                case 10:

                    if(name == "absolutePath" || name.equals("absolutePath")) {return true;}
break;
                case 14:

                    if(name == "parentPath" || name.equals("parentPath")) {return true;}
break;
                case 15:

                    if(name == "flags" || name.equals("flags")) {return true;}
break;
                case 17:

                    if(name == "lastModified" || name.equals("lastModified")) {return true;}
break;
                case 19:

                    if(name == "fullPath" || name.equals("fullPath")) {return true;}
break;
                case 22:

                    if(name == "name" || name.equals("name")) {return true;}
break;
                case 24:

                    if(name == "length" || name.equals("length")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 35;
        switch(h){

                case 0:

                    if(name == "data" || name.equals("data")) {return 101;}
break;
                case 5:

                    if(name == "fileNames" || name.equals("fileNames")) {return 56;}
break;
                case 6:

                    if(name == "files" || name.equals("files")) {return 21;}
break;
                case 9:

                    if(name == "path" || name.equals("path")) {return 20;}
break;
                case 10:

                    if(name == "absolutePath" || name.equals("absolutePath")) {return 20;}
break;
                case 14:

                    if(name == "parentPath" || name.equals("parentPath")) {return 20;}
break;
                case 15:

                    if(name == "flags" || name.equals("flags")) {return 2;}
break;
                case 17:

                    if(name == "lastModified" || name.equals("lastModified")) {return 6;}
break;
                case 19:

                    if(name == "fullPath" || name.equals("fullPath")) {return 20;}
break;
                case 22:

                    if(name == "name" || name.equals("name")) {return 20;}
break;
                case 24:

                    if(name == "length" || name.equals("length")) {return 6;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _absolutePath;

        case 1:return _data;

        case 2:return _fileNames;

        case 3:return _files;

        case 4:return _flags;

        case 5:return _fullPath;

        case 6:return _lastModified;

        case 7:return _length;

        case 8:return _name;

        case 9:return _parentPath;

        case 10:return _path;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 11);
    }

    public java.lang.String getAbsolutePath(){
        return this._absolutePath;
    }
    public void setAbsolutePath(java.lang.String _absolutePath){
    
        this._absolutePath=_absolutePath;
    }

    public byte[] getData(){
        return this._data;
    }
    public void setData(byte[] _data){
    
        this._data=_data;
    }

    public java.lang.String[] getFileNames(){
        return this._fileNames;
    }
    public void setFileNames(java.lang.String[] _fileNames){
    
        this._fileNames=_fileNames;
    }

    public java.util.List getFiles(){
        return this._files;
    }
    public void setFiles(java.util.List _files){
    
        this._files=_files;
    }

    public short getFlags(){
        return this._flags;
    }
    public void setFlags(short _flags){
    
        this._flags=_flags;
    }

    public java.lang.String getFullPath(){
        return this._fullPath;
    }
    public void setFullPath(java.lang.String _fullPath){
    
        this._fullPath=_fullPath;
    }

    public long getLastModified(){
        return this._lastModified;
    }
    public void setLastModified(long _lastModified){
    
        this._lastModified=_lastModified;
    }

    public long getLength(){
        return this._length;
    }
    public void setLength(long _length){
    
        this._length=_length;
    }

    public java.lang.String getName(){
        return this._name;
    }
    public void setName(java.lang.String _name){
    
        this._name=_name;
    }

    public java.lang.String getParentPath(){
        return this._parentPath;
    }
    public void setParentPath(java.lang.String _parentPath){
    
        this._parentPath=_parentPath;
    }

    public java.lang.String getPath(){
        return this._path;
    }
    public void setPath(java.lang.String _path){
    
        this._path=_path;
    }





  
    private static final class VALUED_PARAM_CLASS_absolutePath extends AbstractValuedParam<AmiFileMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiFileMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiFileMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "absolutePath";
	    }
    
	    @Override
	    public Object getValue(AmiFileMessage0 valued) {
		    return (java.lang.String)((AmiFileMessage0)valued).getAbsolutePath();
	    }
    
	    @Override
	    public void setValue(AmiFileMessage0 valued, Object value) {
		    valued.setAbsolutePath((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiFileMessage0 source, AmiFileMessage0 dest) {
		    dest.setAbsolutePath(source.getAbsolutePath());
	    }
	    
	    @Override
	    public boolean areEqual(AmiFileMessage0 source, AmiFileMessage0 dest) {
	        return OH.eq(dest.getAbsolutePath(),source.getAbsolutePath());
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
	    public void append(AmiFileMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getAbsolutePath(),sb);
	        
	    }
	    @Override
	    public void append(AmiFileMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getAbsolutePath(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String absolutePath";
	    }
	    @Override
	    public void clear(AmiFileMessage0 valued){
	       valued.setAbsolutePath(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_absolutePath=new VALUED_PARAM_CLASS_absolutePath();
  

  
    private static final class VALUED_PARAM_CLASS_data extends AbstractValuedParam<AmiFileMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 101;
	    }
	    
	    @Override
	    public void write(AmiFileMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [B}");
		    
	    }
	    
	    @Override
	    public void read(AmiFileMessage0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [B}");
		    
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
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "data";
	    }
    
	    @Override
	    public Object getValue(AmiFileMessage0 valued) {
		    return (byte[])((AmiFileMessage0)valued).getData();
	    }
    
	    @Override
	    public void setValue(AmiFileMessage0 valued, Object value) {
		    valued.setData((byte[])value);
	    }
    
	    @Override
	    public void copy(AmiFileMessage0 source, AmiFileMessage0 dest) {
		    dest.setData(source.getData());
	    }
	    
	    @Override
	    public boolean areEqual(AmiFileMessage0 source, AmiFileMessage0 dest) {
	        return OH.eq(dest.getData(),source.getData());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return byte[].class;
	    }
	    private static final Caster CASTER=OH.getCaster(byte[].class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiFileMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getData());
	        
	    }
	    @Override
	    public void append(AmiFileMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getData());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte[] data";
	    }
	    @Override
	    public void clear(AmiFileMessage0 valued){
	       valued.setData(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_data=new VALUED_PARAM_CLASS_data();
  

  
    private static final class VALUED_PARAM_CLASS_fileNames extends AbstractValuedParam<AmiFileMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 56;
	    }
	    
	    @Override
	    public void write(AmiFileMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [Ljava.lang.String;}");
		    
	    }
	    
	    @Override
	    public void read(AmiFileMessage0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [Ljava.lang.String;}");
		    
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
            return 8;
	    }
    
	    @Override
	    public String getName() {
            return "fileNames";
	    }
    
	    @Override
	    public Object getValue(AmiFileMessage0 valued) {
		    return (java.lang.String[])((AmiFileMessage0)valued).getFileNames();
	    }
    
	    @Override
	    public void setValue(AmiFileMessage0 valued, Object value) {
		    valued.setFileNames((java.lang.String[])value);
	    }
    
	    @Override
	    public void copy(AmiFileMessage0 source, AmiFileMessage0 dest) {
		    dest.setFileNames(source.getFileNames());
	    }
	    
	    @Override
	    public boolean areEqual(AmiFileMessage0 source, AmiFileMessage0 dest) {
	        return OH.eq(dest.getFileNames(),source.getFileNames());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.String[].class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.String[].class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiFileMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getFileNames());
	        
	    }
	    @Override
	    public void append(AmiFileMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getFileNames());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String[] fileNames";
	    }
	    @Override
	    public void clear(AmiFileMessage0 valued){
	       valued.setFileNames(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_fileNames=new VALUED_PARAM_CLASS_fileNames();
  

  
    private static final class VALUED_PARAM_CLASS_files extends AbstractValuedParam<AmiFileMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiFileMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiFileMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 3;
	    }
    
	    @Override
	    public byte getPid() {
            return 9;
	    }
    
	    @Override
	    public String getName() {
            return "files";
	    }
    
	    @Override
	    public Object getValue(AmiFileMessage0 valued) {
		    return (java.util.List)((AmiFileMessage0)valued).getFiles();
	    }
    
	    @Override
	    public void setValue(AmiFileMessage0 valued, Object value) {
		    valued.setFiles((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiFileMessage0 source, AmiFileMessage0 dest) {
		    dest.setFiles(source.getFiles());
	    }
	    
	    @Override
	    public boolean areEqual(AmiFileMessage0 source, AmiFileMessage0 dest) {
	        return OH.eq(dest.getFiles(),source.getFiles());
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
	    public void append(AmiFileMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getFiles());
	        
	    }
	    @Override
	    public void append(AmiFileMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getFiles());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List files";
	    }
	    @Override
	    public void clear(AmiFileMessage0 valued){
	       valued.setFiles(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_files=new VALUED_PARAM_CLASS_files();
  

  
    private static final class VALUED_PARAM_CLASS_flags extends AbstractValuedParam<AmiFileMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 2;
	    }
	    
	    @Override
	    public void write(AmiFileMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeShort(valued.getFlags());
		    
	    }
	    
	    @Override
	    public void read(AmiFileMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setFlags(stream.readShort());
		    
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
		    return 4;
	    }
    
	    @Override
	    public byte getPid() {
            return 10;
	    }
    
	    @Override
	    public String getName() {
            return "flags";
	    }
    
	    @Override
	    public Object getValue(AmiFileMessage0 valued) {
		    return (short)((AmiFileMessage0)valued).getFlags();
	    }
    
	    @Override
	    public void setValue(AmiFileMessage0 valued, Object value) {
		    valued.setFlags((java.lang.Short)value);
	    }
    
	    @Override
	    public void copy(AmiFileMessage0 source, AmiFileMessage0 dest) {
		    dest.setFlags(source.getFlags());
	    }
	    
	    @Override
	    public boolean areEqual(AmiFileMessage0 source, AmiFileMessage0 dest) {
	        return OH.eq(dest.getFlags(),source.getFlags());
	    }
	    
	    
	    
	    
	    
	    
	    @Override
	    public short getShort(AmiFileMessage0 valued) {
		    return valued.getFlags();
	    }
    
	    @Override
	    public void setShort(AmiFileMessage0 valued, short value) {
		    valued.setFlags(value);
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
	    public void append(AmiFileMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getFlags());
	        
	    }
	    @Override
	    public void append(AmiFileMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getFlags());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:short flags";
	    }
	    @Override
	    public void clear(AmiFileMessage0 valued){
	       valued.setFlags((short)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_flags=new VALUED_PARAM_CLASS_flags();
  

  
    private static final class VALUED_PARAM_CLASS_fullPath extends AbstractValuedParam<AmiFileMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiFileMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiFileMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "fullPath";
	    }
    
	    @Override
	    public Object getValue(AmiFileMessage0 valued) {
		    return (java.lang.String)((AmiFileMessage0)valued).getFullPath();
	    }
    
	    @Override
	    public void setValue(AmiFileMessage0 valued, Object value) {
		    valued.setFullPath((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiFileMessage0 source, AmiFileMessage0 dest) {
		    dest.setFullPath(source.getFullPath());
	    }
	    
	    @Override
	    public boolean areEqual(AmiFileMessage0 source, AmiFileMessage0 dest) {
	        return OH.eq(dest.getFullPath(),source.getFullPath());
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
	    public void append(AmiFileMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getFullPath(),sb);
	        
	    }
	    @Override
	    public void append(AmiFileMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getFullPath(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String fullPath";
	    }
	    @Override
	    public void clear(AmiFileMessage0 valued){
	       valued.setFullPath(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_fullPath=new VALUED_PARAM_CLASS_fullPath();
  

  
    private static final class VALUED_PARAM_CLASS_lastModified extends AbstractValuedParam<AmiFileMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiFileMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getLastModified());
		    
	    }
	    
	    @Override
	    public void read(AmiFileMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setLastModified(stream.readLong());
		    
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
		    return 6;
	    }
    
	    @Override
	    public byte getPid() {
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "lastModified";
	    }
    
	    @Override
	    public Object getValue(AmiFileMessage0 valued) {
		    return (long)((AmiFileMessage0)valued).getLastModified();
	    }
    
	    @Override
	    public void setValue(AmiFileMessage0 valued, Object value) {
		    valued.setLastModified((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiFileMessage0 source, AmiFileMessage0 dest) {
		    dest.setLastModified(source.getLastModified());
	    }
	    
	    @Override
	    public boolean areEqual(AmiFileMessage0 source, AmiFileMessage0 dest) {
	        return OH.eq(dest.getLastModified(),source.getLastModified());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiFileMessage0 valued) {
		    return valued.getLastModified();
	    }
    
	    @Override
	    public void setLong(AmiFileMessage0 valued, long value) {
		    valued.setLastModified(value);
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
	    public void append(AmiFileMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getLastModified());
	        
	    }
	    @Override
	    public void append(AmiFileMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getLastModified());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long lastModified";
	    }
	    @Override
	    public void clear(AmiFileMessage0 valued){
	       valued.setLastModified(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_lastModified=new VALUED_PARAM_CLASS_lastModified();
  

  
    private static final class VALUED_PARAM_CLASS_length extends AbstractValuedParam<AmiFileMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiFileMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getLength());
		    
	    }
	    
	    @Override
	    public void read(AmiFileMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setLength(stream.readLong());
		    
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
		    return 7;
	    }
    
	    @Override
	    public byte getPid() {
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "length";
	    }
    
	    @Override
	    public Object getValue(AmiFileMessage0 valued) {
		    return (long)((AmiFileMessage0)valued).getLength();
	    }
    
	    @Override
	    public void setValue(AmiFileMessage0 valued, Object value) {
		    valued.setLength((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiFileMessage0 source, AmiFileMessage0 dest) {
		    dest.setLength(source.getLength());
	    }
	    
	    @Override
	    public boolean areEqual(AmiFileMessage0 source, AmiFileMessage0 dest) {
	        return OH.eq(dest.getLength(),source.getLength());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiFileMessage0 valued) {
		    return valued.getLength();
	    }
    
	    @Override
	    public void setLong(AmiFileMessage0 valued, long value) {
		    valued.setLength(value);
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
	    public void append(AmiFileMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getLength());
	        
	    }
	    @Override
	    public void append(AmiFileMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getLength());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long length";
	    }
	    @Override
	    public void clear(AmiFileMessage0 valued){
	       valued.setLength(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_length=new VALUED_PARAM_CLASS_length();
  

  
    private static final class VALUED_PARAM_CLASS_name extends AbstractValuedParam<AmiFileMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiFileMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiFileMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 8;
	    }
    
	    @Override
	    public byte getPid() {
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "name";
	    }
    
	    @Override
	    public Object getValue(AmiFileMessage0 valued) {
		    return (java.lang.String)((AmiFileMessage0)valued).getName();
	    }
    
	    @Override
	    public void setValue(AmiFileMessage0 valued, Object value) {
		    valued.setName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiFileMessage0 source, AmiFileMessage0 dest) {
		    dest.setName(source.getName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiFileMessage0 source, AmiFileMessage0 dest) {
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
	    public void append(AmiFileMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getName(),sb);
	        
	    }
	    @Override
	    public void append(AmiFileMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String name";
	    }
	    @Override
	    public void clear(AmiFileMessage0 valued){
	       valued.setName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_name=new VALUED_PARAM_CLASS_name();
  

  
    private static final class VALUED_PARAM_CLASS_parentPath extends AbstractValuedParam<AmiFileMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiFileMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiFileMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 9;
	    }
    
	    @Override
	    public byte getPid() {
            return 11;
	    }
    
	    @Override
	    public String getName() {
            return "parentPath";
	    }
    
	    @Override
	    public Object getValue(AmiFileMessage0 valued) {
		    return (java.lang.String)((AmiFileMessage0)valued).getParentPath();
	    }
    
	    @Override
	    public void setValue(AmiFileMessage0 valued, Object value) {
		    valued.setParentPath((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiFileMessage0 source, AmiFileMessage0 dest) {
		    dest.setParentPath(source.getParentPath());
	    }
	    
	    @Override
	    public boolean areEqual(AmiFileMessage0 source, AmiFileMessage0 dest) {
	        return OH.eq(dest.getParentPath(),source.getParentPath());
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
	    public void append(AmiFileMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getParentPath(),sb);
	        
	    }
	    @Override
	    public void append(AmiFileMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getParentPath(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String parentPath";
	    }
	    @Override
	    public void clear(AmiFileMessage0 valued){
	       valued.setParentPath(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_parentPath=new VALUED_PARAM_CLASS_parentPath();
  

  
    private static final class VALUED_PARAM_CLASS_path extends AbstractValuedParam<AmiFileMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiFileMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiFileMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 10;
	    }
    
	    @Override
	    public byte getPid() {
            return 6;
	    }
    
	    @Override
	    public String getName() {
            return "path";
	    }
    
	    @Override
	    public Object getValue(AmiFileMessage0 valued) {
		    return (java.lang.String)((AmiFileMessage0)valued).getPath();
	    }
    
	    @Override
	    public void setValue(AmiFileMessage0 valued, Object value) {
		    valued.setPath((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiFileMessage0 source, AmiFileMessage0 dest) {
		    dest.setPath(source.getPath());
	    }
	    
	    @Override
	    public boolean areEqual(AmiFileMessage0 source, AmiFileMessage0 dest) {
	        return OH.eq(dest.getPath(),source.getPath());
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
	    public void append(AmiFileMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getPath(),sb);
	        
	    }
	    @Override
	    public void append(AmiFileMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getPath(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String path";
	    }
	    @Override
	    public void clear(AmiFileMessage0 valued){
	       valued.setPath(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_path=new VALUED_PARAM_CLASS_path();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_absolutePath, VALUED_PARAM_data, VALUED_PARAM_fileNames, VALUED_PARAM_files, VALUED_PARAM_flags, VALUED_PARAM_fullPath, VALUED_PARAM_lastModified, VALUED_PARAM_length, VALUED_PARAM_name, VALUED_PARAM_parentPath, VALUED_PARAM_path, };



    private static final byte PIDS[]={ 5 ,7,8,9,10,4,3,1,2,11,6};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 5: return VALUED_PARAM_absolutePath;
             case 7: return VALUED_PARAM_data;
             case 8: return VALUED_PARAM_fileNames;
             case 9: return VALUED_PARAM_files;
             case 10: return VALUED_PARAM_flags;
             case 4: return VALUED_PARAM_fullPath;
             case 3: return VALUED_PARAM_lastModified;
             case 1: return VALUED_PARAM_length;
             case 2: return VALUED_PARAM_name;
             case 11: return VALUED_PARAM_parentPath;
             case 6: return VALUED_PARAM_path;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 5: return true;
             case 7: return true;
             case 8: return true;
             case 9: return true;
             case 10: return true;
             case 4: return true;
             case 3: return true;
             case 1: return true;
             case 2: return true;
             case 11: return true;
             case 6: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 5: return "absolutePath";
             case 7: return "data";
             case 8: return "fileNames";
             case 9: return "files";
             case 10: return "flags";
             case 4: return "fullPath";
             case 3: return "lastModified";
             case 1: return "length";
             case 2: return "name";
             case 11: return "parentPath";
             case 6: return "path";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 5: return 0;
             case 7: return 1;
             case 8: return 2;
             case 9: return 3;
             case 10: return 4;
             case 4: return 5;
             case 3: return 6;
             case 1: return 7;
             case 2: return 8;
             case 11: return 9;
             case 6: return 10;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="absolutePath") return 5;
             if(name=="data") return 7;
             if(name=="fileNames") return 8;
             if(name=="files") return 9;
             if(name=="flags") return 10;
             if(name=="fullPath") return 4;
             if(name=="lastModified") return 3;
             if(name=="length") return 1;
             if(name=="name") return 2;
             if(name=="parentPath") return 11;
             if(name=="path") return 6;
            
             if("absolutePath".equals(name)) return 5;
             if("data".equals(name)) return 7;
             if("fileNames".equals(name)) return 8;
             if("files".equals(name)) return 9;
             if("flags".equals(name)) return 10;
             if("fullPath".equals(name)) return 4;
             if("lastModified".equals(name)) return 3;
             if("length".equals(name)) return 1;
             if("name".equals(name)) return 2;
             if("parentPath".equals(name)) return 11;
             if("path".equals(name)) return 6;
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
             case 5: return  this._absolutePath; 
             case 7: return  this._data; 
             case 8: return  this._fileNames; 
             case 9: return  this._files; 
             case 10: return  OH.valueOf(this._flags); 
             case 4: return  this._fullPath; 
             case 3: return  OH.valueOf(this._lastModified); 
             case 1: return  OH.valueOf(this._length); 
             case 2: return  this._name; 
             case 11: return  this._parentPath; 
             case 6: return  this._path; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 5: return java.lang.String.class;
             case 7: return byte[].class;
             case 8: return java.lang.String[].class;
             case 9: return java.util.List.class;
             case 10: return short.class;
             case 4: return java.lang.String.class;
             case 3: return long.class;
             case 1: return long.class;
             case 2: return java.lang.String.class;
             case 11: return java.lang.String.class;
             case 6: return java.lang.String.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 5: return 20;
             case 7: return 101;
             case 8: return 56;
             case 9: return 21;
             case 10: return 2;
             case 4: return 20;
             case 3: return 6;
             case 1: return 6;
             case 2: return 20;
             case 11: return 20;
             case 6: return 20;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 5: this._absolutePath=(java.lang.String)value;return;
             case 7: this._data=(byte[])value;return;
             case 8: this._fileNames=(java.lang.String[])value;return;
             case 9: this._files=(java.util.List)value;return;
             case 10: this._flags=(java.lang.Short)value;return;
             case 4: this._fullPath=(java.lang.String)value;return;
             case 3: this._lastModified=(java.lang.Long)value;return;
             case 1: this._length=(java.lang.Long)value;return;
             case 2: this._name=(java.lang.String)value;return;
             case 11: this._parentPath=(java.lang.String)value;return;
             case 6: this._path=(java.lang.String)value;return;
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
             case 5: this._absolutePath=(java.lang.String)value;return true;
             case 7: this._data=(byte[])value;return true;
             case 8: this._fileNames=(java.lang.String[])value;return true;
             case 9: this._files=(java.util.List)value;return true;
             case 10: this._flags=(java.lang.Short)value;return true;
             case 4: this._fullPath=(java.lang.String)value;return true;
             case 3: this._lastModified=(java.lang.Long)value;return true;
             case 1: this._length=(java.lang.Long)value;return true;
             case 2: this._name=(java.lang.String)value;return true;
             case 11: this._parentPath=(java.lang.String)value;return true;
             case 6: this._path=(java.lang.String)value;return true;
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
             case 10: return this._flags;
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
             case 3: return this._lastModified;
             case 1: return this._length;
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
             case 10: this._flags=value;return;
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
             case 3: this._lastModified=value;return;
             case 1: this._length=value;return;
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
        
            if((basicType=in.readByte())!=6)
                break;
            this._length=in.readLong();
        
            break;

        case 2:
        
            this._name=(java.lang.String)converter.read(session);
        
            break;

        case 3:
        
            if((basicType=in.readByte())!=6)
                break;
            this._lastModified=in.readLong();
        
            break;

        case 4:
        
            this._fullPath=(java.lang.String)converter.read(session);
        
            break;

        case 5:
        
            this._absolutePath=(java.lang.String)converter.read(session);
        
            break;

        case 6:
        
            this._path=(java.lang.String)converter.read(session);
        
            break;

        case 7:
        
            this._data=(byte[])converter.read(session);
        
            break;

        case 8:
        
            this._fileNames=(java.lang.String[])converter.read(session);
        
            break;

        case 9:
        
            this._files=(java.util.List)converter.read(session);
        
            break;

        case 10:
        
            if((basicType=in.readByte())!=2)
                break;
            this._flags=in.readShort();
        
            break;

        case 11:
        
            this._parentPath=(java.lang.String)converter.read(session);
        
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
        
if(this._length!=0L && (0 & transience)==0){
    out.writeByte(1);
        
    out.writeByte(6);
    out.writeLong(this._length);
        
}

if(this._name!=null && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this._name,session);
        
}

if(this._lastModified!=0L && (0 & transience)==0){
    out.writeByte(3);
        
    out.writeByte(6);
    out.writeLong(this._lastModified);
        
}

if(this._fullPath!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._fullPath,session);
        
}

if(this._absolutePath!=null && (0 & transience)==0){
    out.writeByte(5);
        
    converter.write(this._absolutePath,session);
        
}

if(this._path!=null && (0 & transience)==0){
    out.writeByte(6);
        
    converter.write(this._path,session);
        
}

if(this._data!=null && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this._data,session);
        
}

if(this._fileNames!=null && (0 & transience)==0){
    out.writeByte(8);
        
    converter.write(this._fileNames,session);
        
}

if(this._files!=null && (0 & transience)==0){
    out.writeByte(9);
        
    converter.write(this._files,session);
        
}

if(this._flags!=(short)0 && (0 & transience)==0){
    out.writeByte(10);
        
    out.writeByte(2);
    out.writeShort(this._flags);
        
}

if(this._parentPath!=null && (0 & transience)==0){
    out.writeByte(11);
        
    converter.write(this._parentPath,session);
        
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