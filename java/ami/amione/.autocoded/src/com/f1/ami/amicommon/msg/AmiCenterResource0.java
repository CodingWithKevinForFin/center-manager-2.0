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

public abstract class AmiCenterResource0 implements com.f1.ami.amicommon.msg.AmiCenterResource ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

    private int hashCode=ValuedHashCodeGenerator.next(this);
    
    @Override
    public int hashCode(){
      return this.hashCode;
    }
    
    @Override
	public com.f1.base.Message clone(){
       try{
         
	       final com.f1.base.Message r= (com.f1.base.Message)super.clone();
	       ((AmiCenterResource0)r).__locked=false;
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
	
	
    

    private long _checksum;

    private byte[] _data;

    private int _imageHeight;

    private int _imageWidth;

    private long _modifiedOn;

    private java.lang.String _path;

    private long _size;

    private static final String NAMES[]={ "checksum" ,"data","imageHeight","imageWidth","modifiedOn","path","size"};

	@Override
    public void put(String name, Object value){//asdf
    
	   if(__locked)
	       throw newLockedException(name,value);
	
        final int h=Math.abs(name.hashCode()) % 11;
        try{
        switch(h){

                case 2:

                    if(name == "path" || name.equals("path")) {this._path=(java.lang.String)value;return;}
break;
                case 3:

                    if(name == "data" || name.equals("data")) {this._data=(byte[])value;return;}
break;
                case 4:

                    if(name == "checksum" || name.equals("checksum")) {this._checksum=(java.lang.Long)value;return;}
break;
                case 5:

                    if(name == "imageWidth" || name.equals("imageWidth")) {this._imageWidth=(java.lang.Integer)value;return;}
break;
                case 6:

                    if(name == "size" || name.equals("size")) {this._size=(java.lang.Long)value;return;}
break;
                case 7:

                    if(name == "modifiedOn" || name.equals("modifiedOn")) {this._modifiedOn=(java.lang.Long)value;return;}
break;
                case 10:

                    if(name == "imageHeight" || name.equals("imageHeight")) {this._imageHeight=(java.lang.Integer)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 2:

                    if(name == "path" || name.equals("path")) {this._path=(java.lang.String)value;return true;}
break;
                case 3:

                    if(name == "data" || name.equals("data")) {this._data=(byte[])value;return true;}
break;
                case 4:

                    if(name == "checksum" || name.equals("checksum")) {this._checksum=(java.lang.Long)value;return true;}
break;
                case 5:

                    if(name == "imageWidth" || name.equals("imageWidth")) {this._imageWidth=(java.lang.Integer)value;return true;}
break;
                case 6:

                    if(name == "size" || name.equals("size")) {this._size=(java.lang.Long)value;return true;}
break;
                case 7:

                    if(name == "modifiedOn" || name.equals("modifiedOn")) {this._modifiedOn=(java.lang.Long)value;return true;}
break;
                case 10:

                    if(name == "imageHeight" || name.equals("imageHeight")) {this._imageHeight=(java.lang.Integer)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 2:

		    
                    if(name == "path" || name.equals("path")) {return this._path;}
            
break;
                case 3:

		    
                    if(name == "data" || name.equals("data")) {return this._data;}
            
break;
                case 4:

		    
                    if(name == "checksum" || name.equals("checksum")) {return OH.valueOf(this._checksum);}
		    
break;
                case 5:

		    
                    if(name == "imageWidth" || name.equals("imageWidth")) {return OH.valueOf(this._imageWidth);}
		    
break;
                case 6:

		    
                    if(name == "size" || name.equals("size")) {return OH.valueOf(this._size);}
		    
break;
                case 7:

		    
                    if(name == "modifiedOn" || name.equals("modifiedOn")) {return OH.valueOf(this._modifiedOn);}
		    
break;
                case 10:

		    
                    if(name == "imageHeight" || name.equals("imageHeight")) {return OH.valueOf(this._imageHeight);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 2:

                    if(name == "path" || name.equals("path")) {return java.lang.String.class;}
break;
                case 3:

                    if(name == "data" || name.equals("data")) {return byte[].class;}
break;
                case 4:

                    if(name == "checksum" || name.equals("checksum")) {return long.class;}
break;
                case 5:

                    if(name == "imageWidth" || name.equals("imageWidth")) {return int.class;}
break;
                case 6:

                    if(name == "size" || name.equals("size")) {return long.class;}
break;
                case 7:

                    if(name == "modifiedOn" || name.equals("modifiedOn")) {return long.class;}
break;
                case 10:

                    if(name == "imageHeight" || name.equals("imageHeight")) {return int.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 2:

                    if(name == "path" || name.equals("path")) {return VALUED_PARAM_path;}
break;
                case 3:

                    if(name == "data" || name.equals("data")) {return VALUED_PARAM_data;}
break;
                case 4:

                    if(name == "checksum" || name.equals("checksum")) {return VALUED_PARAM_checksum;}
break;
                case 5:

                    if(name == "imageWidth" || name.equals("imageWidth")) {return VALUED_PARAM_imageWidth;}
break;
                case 6:

                    if(name == "size" || name.equals("size")) {return VALUED_PARAM_size;}
break;
                case 7:

                    if(name == "modifiedOn" || name.equals("modifiedOn")) {return VALUED_PARAM_modifiedOn;}
break;
                case 10:

                    if(name == "imageHeight" || name.equals("imageHeight")) {return VALUED_PARAM_imageHeight;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 2:

                    if(name == "path" || name.equals("path")) {return 5;}
break;
                case 3:

                    if(name == "data" || name.equals("data")) {return 1;}
break;
                case 4:

                    if(name == "checksum" || name.equals("checksum")) {return 0;}
break;
                case 5:

                    if(name == "imageWidth" || name.equals("imageWidth")) {return 3;}
break;
                case 6:

                    if(name == "size" || name.equals("size")) {return 6;}
break;
                case 7:

                    if(name == "modifiedOn" || name.equals("modifiedOn")) {return 4;}
break;
                case 10:

                    if(name == "imageHeight" || name.equals("imageHeight")) {return 2;}
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
        return 7;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiCenterResource0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 2:

                    if(name == "path" || name.equals("path")) {return true;}
break;
                case 3:

                    if(name == "data" || name.equals("data")) {return true;}
break;
                case 4:

                    if(name == "checksum" || name.equals("checksum")) {return true;}
break;
                case 5:

                    if(name == "imageWidth" || name.equals("imageWidth")) {return true;}
break;
                case 6:

                    if(name == "size" || name.equals("size")) {return true;}
break;
                case 7:

                    if(name == "modifiedOn" || name.equals("modifiedOn")) {return true;}
break;
                case 10:

                    if(name == "imageHeight" || name.equals("imageHeight")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 11;
        switch(h){

                case 2:

                    if(name == "path" || name.equals("path")) {return 20;}
break;
                case 3:

                    if(name == "data" || name.equals("data")) {return 101;}
break;
                case 4:

                    if(name == "checksum" || name.equals("checksum")) {return 6;}
break;
                case 5:

                    if(name == "imageWidth" || name.equals("imageWidth")) {return 4;}
break;
                case 6:

                    if(name == "size" || name.equals("size")) {return 6;}
break;
                case 7:

                    if(name == "modifiedOn" || name.equals("modifiedOn")) {return 6;}
break;
                case 10:

                    if(name == "imageHeight" || name.equals("imageHeight")) {return 4;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _checksum;

        case 1:return _data;

        case 2:return _imageHeight;

        case 3:return _imageWidth;

        case 4:return _modifiedOn;

        case 5:return _path;

        case 6:return _size;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 7);
    }

    public long getChecksum(){
        return this._checksum;
    }
    public void setChecksum(long _checksum){
    
	   if(__locked)
	       throw newLockedException("checksum",_checksum);
	
        this._checksum=_checksum;
    }

    public byte[] getData(){
        return this._data;
    }
    public void setData(byte[] _data){
    
	   if(__locked)
	       throw newLockedException("data",_data);
	
        this._data=_data;
    }

    public int getImageHeight(){
        return this._imageHeight;
    }
    public void setImageHeight(int _imageHeight){
    
	   if(__locked)
	       throw newLockedException("imageHeight",_imageHeight);
	
        this._imageHeight=_imageHeight;
    }

    public int getImageWidth(){
        return this._imageWidth;
    }
    public void setImageWidth(int _imageWidth){
    
	   if(__locked)
	       throw newLockedException("imageWidth",_imageWidth);
	
        this._imageWidth=_imageWidth;
    }

    public long getModifiedOn(){
        return this._modifiedOn;
    }
    public void setModifiedOn(long _modifiedOn){
    
	   if(__locked)
	       throw newLockedException("modifiedOn",_modifiedOn);
	
        this._modifiedOn=_modifiedOn;
    }

    public java.lang.String getPath(){
        return this._path;
    }
    public void setPath(java.lang.String _path){
    
	   if(__locked)
	       throw newLockedException("path",_path);
	
        this._path=_path;
    }

    public long getSize(){
        return this._size;
    }
    public void setSize(long _size){
    
	   if(__locked)
	       throw newLockedException("size",_size);
	
        this._size=_size;
    }





  
    private static final class VALUED_PARAM_CLASS_checksum extends AbstractValuedParam<AmiCenterResource0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterResource0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getChecksum());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterResource0 valued, DataInput stream) throws IOException{
		    
		      valued.setChecksum(stream.readLong());
		    
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
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "checksum";
	    }
    
	    @Override
	    public Object getValue(AmiCenterResource0 valued) {
		    return (long)((AmiCenterResource0)valued).getChecksum();
	    }
    
	    @Override
	    public void setValue(AmiCenterResource0 valued, Object value) {
		    valued.setChecksum((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterResource0 source, AmiCenterResource0 dest) {
		    dest.setChecksum(source.getChecksum());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterResource0 source, AmiCenterResource0 dest) {
	        return OH.eq(dest.getChecksum(),source.getChecksum());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterResource0 valued) {
		    return valued.getChecksum();
	    }
    
	    @Override
	    public void setLong(AmiCenterResource0 valued, long value) {
		    valued.setChecksum(value);
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
	    public void append(AmiCenterResource0 valued, StringBuilder sb){
	        
	        sb.append(valued.getChecksum());
	        
	    }
	    @Override
	    public void append(AmiCenterResource0 valued, StringBuildable sb){
	        
	        sb.append(valued.getChecksum());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long checksum";
	    }
	    @Override
	    public void clear(AmiCenterResource0 valued){
	       valued.setChecksum(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_checksum=new VALUED_PARAM_CLASS_checksum();
  

  
    private static final class VALUED_PARAM_CLASS_data extends AbstractValuedParam<AmiCenterResource0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 101;
	    }
	    
	    @Override
	    public void write(AmiCenterResource0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [B}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterResource0 valued, DataInput stream) throws IOException{
		    
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
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "data";
	    }
    
	    @Override
	    public Object getValue(AmiCenterResource0 valued) {
		    return (byte[])((AmiCenterResource0)valued).getData();
	    }
    
	    @Override
	    public void setValue(AmiCenterResource0 valued, Object value) {
		    valued.setData((byte[])value);
	    }
    
	    @Override
	    public void copy(AmiCenterResource0 source, AmiCenterResource0 dest) {
		    dest.setData(source.getData());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterResource0 source, AmiCenterResource0 dest) {
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
	    public void append(AmiCenterResource0 valued, StringBuilder sb){
	        
	        sb.append(valued.getData());
	        
	    }
	    @Override
	    public void append(AmiCenterResource0 valued, StringBuildable sb){
	        
	        sb.append(valued.getData());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte[] data";
	    }
	    @Override
	    public void clear(AmiCenterResource0 valued){
	       valued.setData(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_data=new VALUED_PARAM_CLASS_data();
  

  
    private static final class VALUED_PARAM_CLASS_imageHeight extends AbstractValuedParam<AmiCenterResource0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterResource0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getImageHeight());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterResource0 valued, DataInput stream) throws IOException{
		    
		      valued.setImageHeight(stream.readInt());
		    
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
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "imageHeight";
	    }
    
	    @Override
	    public Object getValue(AmiCenterResource0 valued) {
		    return (int)((AmiCenterResource0)valued).getImageHeight();
	    }
    
	    @Override
	    public void setValue(AmiCenterResource0 valued, Object value) {
		    valued.setImageHeight((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterResource0 source, AmiCenterResource0 dest) {
		    dest.setImageHeight(source.getImageHeight());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterResource0 source, AmiCenterResource0 dest) {
	        return OH.eq(dest.getImageHeight(),source.getImageHeight());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterResource0 valued) {
		    return valued.getImageHeight();
	    }
    
	    @Override
	    public void setInt(AmiCenterResource0 valued, int value) {
		    valued.setImageHeight(value);
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
	    public void append(AmiCenterResource0 valued, StringBuilder sb){
	        
	        sb.append(valued.getImageHeight());
	        
	    }
	    @Override
	    public void append(AmiCenterResource0 valued, StringBuildable sb){
	        
	        sb.append(valued.getImageHeight());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int imageHeight";
	    }
	    @Override
	    public void clear(AmiCenterResource0 valued){
	       valued.setImageHeight(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_imageHeight=new VALUED_PARAM_CLASS_imageHeight();
  

  
    private static final class VALUED_PARAM_CLASS_imageWidth extends AbstractValuedParam<AmiCenterResource0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterResource0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getImageWidth());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterResource0 valued, DataInput stream) throws IOException{
		    
		      valued.setImageWidth(stream.readInt());
		    
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
            return 6;
	    }
    
	    @Override
	    public String getName() {
            return "imageWidth";
	    }
    
	    @Override
	    public Object getValue(AmiCenterResource0 valued) {
		    return (int)((AmiCenterResource0)valued).getImageWidth();
	    }
    
	    @Override
	    public void setValue(AmiCenterResource0 valued, Object value) {
		    valued.setImageWidth((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterResource0 source, AmiCenterResource0 dest) {
		    dest.setImageWidth(source.getImageWidth());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterResource0 source, AmiCenterResource0 dest) {
	        return OH.eq(dest.getImageWidth(),source.getImageWidth());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterResource0 valued) {
		    return valued.getImageWidth();
	    }
    
	    @Override
	    public void setInt(AmiCenterResource0 valued, int value) {
		    valued.setImageWidth(value);
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
	    public void append(AmiCenterResource0 valued, StringBuilder sb){
	        
	        sb.append(valued.getImageWidth());
	        
	    }
	    @Override
	    public void append(AmiCenterResource0 valued, StringBuildable sb){
	        
	        sb.append(valued.getImageWidth());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int imageWidth";
	    }
	    @Override
	    public void clear(AmiCenterResource0 valued){
	       valued.setImageWidth(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_imageWidth=new VALUED_PARAM_CLASS_imageWidth();
  

  
    private static final class VALUED_PARAM_CLASS_modifiedOn extends AbstractValuedParam<AmiCenterResource0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterResource0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getModifiedOn());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterResource0 valued, DataInput stream) throws IOException{
		    
		      valued.setModifiedOn(stream.readLong());
		    
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
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "modifiedOn";
	    }
    
	    @Override
	    public Object getValue(AmiCenterResource0 valued) {
		    return (long)((AmiCenterResource0)valued).getModifiedOn();
	    }
    
	    @Override
	    public void setValue(AmiCenterResource0 valued, Object value) {
		    valued.setModifiedOn((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterResource0 source, AmiCenterResource0 dest) {
		    dest.setModifiedOn(source.getModifiedOn());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterResource0 source, AmiCenterResource0 dest) {
	        return OH.eq(dest.getModifiedOn(),source.getModifiedOn());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterResource0 valued) {
		    return valued.getModifiedOn();
	    }
    
	    @Override
	    public void setLong(AmiCenterResource0 valued, long value) {
		    valued.setModifiedOn(value);
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
	    public void append(AmiCenterResource0 valued, StringBuilder sb){
	        
	        sb.append(valued.getModifiedOn());
	        
	    }
	    @Override
	    public void append(AmiCenterResource0 valued, StringBuildable sb){
	        
	        sb.append(valued.getModifiedOn());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long modifiedOn";
	    }
	    @Override
	    public void clear(AmiCenterResource0 valued){
	       valued.setModifiedOn(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_modifiedOn=new VALUED_PARAM_CLASS_modifiedOn();
  

  
    private static final class VALUED_PARAM_CLASS_path extends AbstractValuedParam<AmiCenterResource0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterResource0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterResource0 valued, DataInput stream) throws IOException{
		    
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
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "path";
	    }
    
	    @Override
	    public Object getValue(AmiCenterResource0 valued) {
		    return (java.lang.String)((AmiCenterResource0)valued).getPath();
	    }
    
	    @Override
	    public void setValue(AmiCenterResource0 valued, Object value) {
		    valued.setPath((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterResource0 source, AmiCenterResource0 dest) {
		    dest.setPath(source.getPath());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterResource0 source, AmiCenterResource0 dest) {
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
	    public void append(AmiCenterResource0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getPath(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterResource0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getPath(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String path";
	    }
	    @Override
	    public void clear(AmiCenterResource0 valued){
	       valued.setPath(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_path=new VALUED_PARAM_CLASS_path();
  

  
    private static final class VALUED_PARAM_CLASS_size extends AbstractValuedParam<AmiCenterResource0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterResource0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getSize());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterResource0 valued, DataInput stream) throws IOException{
		    
		      valued.setSize(stream.readLong());
		    
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
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "size";
	    }
    
	    @Override
	    public Object getValue(AmiCenterResource0 valued) {
		    return (long)((AmiCenterResource0)valued).getSize();
	    }
    
	    @Override
	    public void setValue(AmiCenterResource0 valued, Object value) {
		    valued.setSize((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterResource0 source, AmiCenterResource0 dest) {
		    dest.setSize(source.getSize());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterResource0 source, AmiCenterResource0 dest) {
	        return OH.eq(dest.getSize(),source.getSize());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterResource0 valued) {
		    return valued.getSize();
	    }
    
	    @Override
	    public void setLong(AmiCenterResource0 valued, long value) {
		    valued.setSize(value);
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
	    public void append(AmiCenterResource0 valued, StringBuilder sb){
	        
	        sb.append(valued.getSize());
	        
	    }
	    @Override
	    public void append(AmiCenterResource0 valued, StringBuildable sb){
	        
	        sb.append(valued.getSize());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long size";
	    }
	    @Override
	    public void clear(AmiCenterResource0 valued){
	       valued.setSize(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_size=new VALUED_PARAM_CLASS_size();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_checksum, VALUED_PARAM_data, VALUED_PARAM_imageHeight, VALUED_PARAM_imageWidth, VALUED_PARAM_modifiedOn, VALUED_PARAM_path, VALUED_PARAM_size, };



    private static final byte PIDS[]={ 4 ,3,7,6,5,1,2};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 4: return VALUED_PARAM_checksum;
             case 3: return VALUED_PARAM_data;
             case 7: return VALUED_PARAM_imageHeight;
             case 6: return VALUED_PARAM_imageWidth;
             case 5: return VALUED_PARAM_modifiedOn;
             case 1: return VALUED_PARAM_path;
             case 2: return VALUED_PARAM_size;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 4: return true;
             case 3: return true;
             case 7: return true;
             case 6: return true;
             case 5: return true;
             case 1: return true;
             case 2: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 4: return "checksum";
             case 3: return "data";
             case 7: return "imageHeight";
             case 6: return "imageWidth";
             case 5: return "modifiedOn";
             case 1: return "path";
             case 2: return "size";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 4: return 0;
             case 3: return 1;
             case 7: return 2;
             case 6: return 3;
             case 5: return 4;
             case 1: return 5;
             case 2: return 6;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="checksum") return 4;
             if(name=="data") return 3;
             if(name=="imageHeight") return 7;
             if(name=="imageWidth") return 6;
             if(name=="modifiedOn") return 5;
             if(name=="path") return 1;
             if(name=="size") return 2;
            
             if("checksum".equals(name)) return 4;
             if("data".equals(name)) return 3;
             if("imageHeight".equals(name)) return 7;
             if("imageWidth".equals(name)) return 6;
             if("modifiedOn".equals(name)) return 5;
             if("path".equals(name)) return 1;
             if("size".equals(name)) return 2;
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
             case 4: return  OH.valueOf(this._checksum); 
             case 3: return  this._data; 
             case 7: return  OH.valueOf(this._imageHeight); 
             case 6: return  OH.valueOf(this._imageWidth); 
             case 5: return  OH.valueOf(this._modifiedOn); 
             case 1: return  this._path; 
             case 2: return  OH.valueOf(this._size); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 4: return long.class;
             case 3: return byte[].class;
             case 7: return int.class;
             case 6: return int.class;
             case 5: return long.class;
             case 1: return java.lang.String.class;
             case 2: return long.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 4: return 6;
             case 3: return 101;
             case 7: return 4;
             case 6: return 4;
             case 5: return 6;
             case 1: return 20;
             case 2: return 6;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 4: this._checksum=(java.lang.Long)value;return;
             case 3: this._data=(byte[])value;return;
             case 7: this._imageHeight=(java.lang.Integer)value;return;
             case 6: this._imageWidth=(java.lang.Integer)value;return;
             case 5: this._modifiedOn=(java.lang.Long)value;return;
             case 1: this._path=(java.lang.String)value;return;
             case 2: this._size=(java.lang.Long)value;return;
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
             case 4: this._checksum=(java.lang.Long)value;return true;
             case 3: this._data=(byte[])value;return true;
             case 7: this._imageHeight=(java.lang.Integer)value;return true;
             case 6: this._imageWidth=(java.lang.Integer)value;return true;
             case 5: this._modifiedOn=(java.lang.Long)value;return true;
             case 1: this._path=(java.lang.String)value;return true;
             case 2: this._size=(java.lang.Long)value;return true;
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
             case 7: return this._imageHeight;
             case 6: return this._imageWidth;
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
             case 4: return this._checksum;
             case 5: return this._modifiedOn;
             case 2: return this._size;
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
             case 7: this._imageHeight=value;return;
             case 6: this._imageWidth=value;return;
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
             case 4: this._checksum=value;return;
             case 5: this._modifiedOn=value;return;
             case 2: this._size=value;return;
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
        
            this._path=(java.lang.String)converter.read(session);
        
            break;

        case 2:
        
            if((basicType=in.readByte())!=6)
                break;
            this._size=in.readLong();
        
            break;

        case 3:
        
            this._data=(byte[])converter.read(session);
        
            break;

        case 4:
        
            if((basicType=in.readByte())!=6)
                break;
            this._checksum=in.readLong();
        
            break;

        case 5:
        
            if((basicType=in.readByte())!=6)
                break;
            this._modifiedOn=in.readLong();
        
            break;

        case 6:
        
            if((basicType=in.readByte())!=4)
                break;
            this._imageWidth=in.readInt();
        
            break;

        case 7:
        
            if((basicType=in.readByte())!=4)
                break;
            this._imageHeight=in.readInt();
        
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
        
if(this._path!=null && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this._path,session);
        
}

if(this._size!=0L && (0 & transience)==0){
    out.writeByte(2);
        
    out.writeByte(6);
    out.writeLong(this._size);
        
}

if(this._data!=null && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this._data,session);
        
}

if(this._checksum!=0L && (0 & transience)==0){
    out.writeByte(4);
        
    out.writeByte(6);
    out.writeLong(this._checksum);
        
}

if(this._modifiedOn!=0L && (0 & transience)==0){
    out.writeByte(5);
        
    out.writeByte(6);
    out.writeLong(this._modifiedOn);
        
}

if(this._imageWidth!=0 && (0 & transience)==0){
    out.writeByte(6);
        
    out.writeByte(4);
    out.writeInt(this._imageWidth);
        
}

if(this._imageHeight!=0 && (0 & transience)==0){
    out.writeByte(7);
        
    out.writeByte(4);
    out.writeInt(this._imageHeight);
        
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