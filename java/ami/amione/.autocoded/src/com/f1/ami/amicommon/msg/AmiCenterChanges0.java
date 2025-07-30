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

public abstract class AmiCenterChanges0 implements com.f1.ami.amicommon.msg.AmiCenterChanges ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private byte[] _amiEntitiesAdded;

    private byte[] _amiEntitiesRemoved;

    private byte[] _amiEntitiesUpdated;

    private byte[] _amiValuesStringPoolMap;

    private java.lang.String _eyeProcessUid;

    private int _responseNum;

    private long _seqNum;

    private static final String NAMES[]={ "amiEntitiesAdded" ,"amiEntitiesRemoved","amiEntitiesUpdated","amiValuesStringPoolMap","eyeProcessUid","responseNum","seqNum"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 13;
        try{
        switch(h){

                case 0:

                    if(name == "eyeProcessUid" || name.equals("eyeProcessUid")) {this._eyeProcessUid=(java.lang.String)value;return;}
break;
                case 1:

                    if(name == "amiEntitiesRemoved" || name.equals("amiEntitiesRemoved")) {this._amiEntitiesRemoved=(byte[])value;return;}
break;
                case 3:

                    if(name == "amiEntitiesUpdated" || name.equals("amiEntitiesUpdated")) {this._amiEntitiesUpdated=(byte[])value;return;}
break;
                case 7:

                    if(name == "amiValuesStringPoolMap" || name.equals("amiValuesStringPoolMap")) {this._amiValuesStringPoolMap=(byte[])value;return;}
break;
                case 8:

                    if(name == "responseNum" || name.equals("responseNum")) {this._responseNum=(java.lang.Integer)value;return;}
break;
                case 10:

                    if(name == "seqNum" || name.equals("seqNum")) {this._seqNum=(java.lang.Long)value;return;}
break;
                case 12:

                    if(name == "amiEntitiesAdded" || name.equals("amiEntitiesAdded")) {this._amiEntitiesAdded=(byte[])value;return;}
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
        final int h=Math.abs(name.hashCode()) % 13;
        switch(h){

                case 0:

                    if(name == "eyeProcessUid" || name.equals("eyeProcessUid")) {this._eyeProcessUid=(java.lang.String)value;return true;}
break;
                case 1:

                    if(name == "amiEntitiesRemoved" || name.equals("amiEntitiesRemoved")) {this._amiEntitiesRemoved=(byte[])value;return true;}
break;
                case 3:

                    if(name == "amiEntitiesUpdated" || name.equals("amiEntitiesUpdated")) {this._amiEntitiesUpdated=(byte[])value;return true;}
break;
                case 7:

                    if(name == "amiValuesStringPoolMap" || name.equals("amiValuesStringPoolMap")) {this._amiValuesStringPoolMap=(byte[])value;return true;}
break;
                case 8:

                    if(name == "responseNum" || name.equals("responseNum")) {this._responseNum=(java.lang.Integer)value;return true;}
break;
                case 10:

                    if(name == "seqNum" || name.equals("seqNum")) {this._seqNum=(java.lang.Long)value;return true;}
break;
                case 12:

                    if(name == "amiEntitiesAdded" || name.equals("amiEntitiesAdded")) {this._amiEntitiesAdded=(byte[])value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 13;
        switch(h){

                case 0:

		    
                    if(name == "eyeProcessUid" || name.equals("eyeProcessUid")) {return this._eyeProcessUid;}
            
break;
                case 1:

		    
                    if(name == "amiEntitiesRemoved" || name.equals("amiEntitiesRemoved")) {return this._amiEntitiesRemoved;}
            
break;
                case 3:

		    
                    if(name == "amiEntitiesUpdated" || name.equals("amiEntitiesUpdated")) {return this._amiEntitiesUpdated;}
            
break;
                case 7:

		    
                    if(name == "amiValuesStringPoolMap" || name.equals("amiValuesStringPoolMap")) {return this._amiValuesStringPoolMap;}
            
break;
                case 8:

		    
                    if(name == "responseNum" || name.equals("responseNum")) {return OH.valueOf(this._responseNum);}
		    
break;
                case 10:

		    
                    if(name == "seqNum" || name.equals("seqNum")) {return OH.valueOf(this._seqNum);}
		    
break;
                case 12:

		    
                    if(name == "amiEntitiesAdded" || name.equals("amiEntitiesAdded")) {return this._amiEntitiesAdded;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 13;
        switch(h){

                case 0:

                    if(name == "eyeProcessUid" || name.equals("eyeProcessUid")) {return java.lang.String.class;}
break;
                case 1:

                    if(name == "amiEntitiesRemoved" || name.equals("amiEntitiesRemoved")) {return byte[].class;}
break;
                case 3:

                    if(name == "amiEntitiesUpdated" || name.equals("amiEntitiesUpdated")) {return byte[].class;}
break;
                case 7:

                    if(name == "amiValuesStringPoolMap" || name.equals("amiValuesStringPoolMap")) {return byte[].class;}
break;
                case 8:

                    if(name == "responseNum" || name.equals("responseNum")) {return int.class;}
break;
                case 10:

                    if(name == "seqNum" || name.equals("seqNum")) {return long.class;}
break;
                case 12:

                    if(name == "amiEntitiesAdded" || name.equals("amiEntitiesAdded")) {return byte[].class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 13;
        switch(h){

                case 0:

                    if(name == "eyeProcessUid" || name.equals("eyeProcessUid")) {return VALUED_PARAM_eyeProcessUid;}
break;
                case 1:

                    if(name == "amiEntitiesRemoved" || name.equals("amiEntitiesRemoved")) {return VALUED_PARAM_amiEntitiesRemoved;}
break;
                case 3:

                    if(name == "amiEntitiesUpdated" || name.equals("amiEntitiesUpdated")) {return VALUED_PARAM_amiEntitiesUpdated;}
break;
                case 7:

                    if(name == "amiValuesStringPoolMap" || name.equals("amiValuesStringPoolMap")) {return VALUED_PARAM_amiValuesStringPoolMap;}
break;
                case 8:

                    if(name == "responseNum" || name.equals("responseNum")) {return VALUED_PARAM_responseNum;}
break;
                case 10:

                    if(name == "seqNum" || name.equals("seqNum")) {return VALUED_PARAM_seqNum;}
break;
                case 12:

                    if(name == "amiEntitiesAdded" || name.equals("amiEntitiesAdded")) {return VALUED_PARAM_amiEntitiesAdded;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 13;
        switch(h){

                case 0:

                    if(name == "eyeProcessUid" || name.equals("eyeProcessUid")) {return 4;}
break;
                case 1:

                    if(name == "amiEntitiesRemoved" || name.equals("amiEntitiesRemoved")) {return 1;}
break;
                case 3:

                    if(name == "amiEntitiesUpdated" || name.equals("amiEntitiesUpdated")) {return 2;}
break;
                case 7:

                    if(name == "amiValuesStringPoolMap" || name.equals("amiValuesStringPoolMap")) {return 3;}
break;
                case 8:

                    if(name == "responseNum" || name.equals("responseNum")) {return 5;}
break;
                case 10:

                    if(name == "seqNum" || name.equals("seqNum")) {return 6;}
break;
                case 12:

                    if(name == "amiEntitiesAdded" || name.equals("amiEntitiesAdded")) {return 0;}
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
	    return (Class)AmiCenterChanges0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 13;
        switch(h){

                case 0:

                    if(name == "eyeProcessUid" || name.equals("eyeProcessUid")) {return true;}
break;
                case 1:

                    if(name == "amiEntitiesRemoved" || name.equals("amiEntitiesRemoved")) {return true;}
break;
                case 3:

                    if(name == "amiEntitiesUpdated" || name.equals("amiEntitiesUpdated")) {return true;}
break;
                case 7:

                    if(name == "amiValuesStringPoolMap" || name.equals("amiValuesStringPoolMap")) {return true;}
break;
                case 8:

                    if(name == "responseNum" || name.equals("responseNum")) {return true;}
break;
                case 10:

                    if(name == "seqNum" || name.equals("seqNum")) {return true;}
break;
                case 12:

                    if(name == "amiEntitiesAdded" || name.equals("amiEntitiesAdded")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 13;
        switch(h){

                case 0:

                    if(name == "eyeProcessUid" || name.equals("eyeProcessUid")) {return 20;}
break;
                case 1:

                    if(name == "amiEntitiesRemoved" || name.equals("amiEntitiesRemoved")) {return 101;}
break;
                case 3:

                    if(name == "amiEntitiesUpdated" || name.equals("amiEntitiesUpdated")) {return 101;}
break;
                case 7:

                    if(name == "amiValuesStringPoolMap" || name.equals("amiValuesStringPoolMap")) {return 101;}
break;
                case 8:

                    if(name == "responseNum" || name.equals("responseNum")) {return 4;}
break;
                case 10:

                    if(name == "seqNum" || name.equals("seqNum")) {return 6;}
break;
                case 12:

                    if(name == "amiEntitiesAdded" || name.equals("amiEntitiesAdded")) {return 101;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _amiEntitiesAdded;

        case 1:return _amiEntitiesRemoved;

        case 2:return _amiEntitiesUpdated;

        case 3:return _amiValuesStringPoolMap;

        case 4:return _eyeProcessUid;

        case 5:return _responseNum;

        case 6:return _seqNum;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 7);
    }

    public byte[] getAmiEntitiesAdded(){
        return this._amiEntitiesAdded;
    }
    public void setAmiEntitiesAdded(byte[] _amiEntitiesAdded){
    
        this._amiEntitiesAdded=_amiEntitiesAdded;
    }

    public byte[] getAmiEntitiesRemoved(){
        return this._amiEntitiesRemoved;
    }
    public void setAmiEntitiesRemoved(byte[] _amiEntitiesRemoved){
    
        this._amiEntitiesRemoved=_amiEntitiesRemoved;
    }

    public byte[] getAmiEntitiesUpdated(){
        return this._amiEntitiesUpdated;
    }
    public void setAmiEntitiesUpdated(byte[] _amiEntitiesUpdated){
    
        this._amiEntitiesUpdated=_amiEntitiesUpdated;
    }

    public byte[] getAmiValuesStringPoolMap(){
        return this._amiValuesStringPoolMap;
    }
    public void setAmiValuesStringPoolMap(byte[] _amiValuesStringPoolMap){
    
        this._amiValuesStringPoolMap=_amiValuesStringPoolMap;
    }

    public java.lang.String getEyeProcessUid(){
        return this._eyeProcessUid;
    }
    public void setEyeProcessUid(java.lang.String _eyeProcessUid){
    
        this._eyeProcessUid=_eyeProcessUid;
    }

    public int getResponseNum(){
        return this._responseNum;
    }
    public void setResponseNum(int _responseNum){
    
        this._responseNum=_responseNum;
    }

    public long getSeqNum(){
        return this._seqNum;
    }
    public void setSeqNum(long _seqNum){
    
        this._seqNum=_seqNum;
    }





  
    private static final class VALUED_PARAM_CLASS_amiEntitiesAdded extends AbstractValuedParam<AmiCenterChanges0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 101;
	    }
	    
	    @Override
	    public void write(AmiCenterChanges0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [B}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterChanges0 valued, DataInput stream) throws IOException{
		    
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
		    return 0;
	    }
    
	    @Override
	    public byte getPid() {
            return 24;
	    }
    
	    @Override
	    public String getName() {
            return "amiEntitiesAdded";
	    }
    
	    @Override
	    public Object getValue(AmiCenterChanges0 valued) {
		    return (byte[])((AmiCenterChanges0)valued).getAmiEntitiesAdded();
	    }
    
	    @Override
	    public void setValue(AmiCenterChanges0 valued, Object value) {
		    valued.setAmiEntitiesAdded((byte[])value);
	    }
    
	    @Override
	    public void copy(AmiCenterChanges0 source, AmiCenterChanges0 dest) {
		    dest.setAmiEntitiesAdded(source.getAmiEntitiesAdded());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterChanges0 source, AmiCenterChanges0 dest) {
	        return OH.eq(dest.getAmiEntitiesAdded(),source.getAmiEntitiesAdded());
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
	    public void append(AmiCenterChanges0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAmiEntitiesAdded());
	        
	    }
	    @Override
	    public void append(AmiCenterChanges0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAmiEntitiesAdded());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte[] amiEntitiesAdded";
	    }
	    @Override
	    public void clear(AmiCenterChanges0 valued){
	       valued.setAmiEntitiesAdded(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_amiEntitiesAdded=new VALUED_PARAM_CLASS_amiEntitiesAdded();
  

  
    private static final class VALUED_PARAM_CLASS_amiEntitiesRemoved extends AbstractValuedParam<AmiCenterChanges0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 101;
	    }
	    
	    @Override
	    public void write(AmiCenterChanges0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [B}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterChanges0 valued, DataInput stream) throws IOException{
		    
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
            return 26;
	    }
    
	    @Override
	    public String getName() {
            return "amiEntitiesRemoved";
	    }
    
	    @Override
	    public Object getValue(AmiCenterChanges0 valued) {
		    return (byte[])((AmiCenterChanges0)valued).getAmiEntitiesRemoved();
	    }
    
	    @Override
	    public void setValue(AmiCenterChanges0 valued, Object value) {
		    valued.setAmiEntitiesRemoved((byte[])value);
	    }
    
	    @Override
	    public void copy(AmiCenterChanges0 source, AmiCenterChanges0 dest) {
		    dest.setAmiEntitiesRemoved(source.getAmiEntitiesRemoved());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterChanges0 source, AmiCenterChanges0 dest) {
	        return OH.eq(dest.getAmiEntitiesRemoved(),source.getAmiEntitiesRemoved());
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
	    public void append(AmiCenterChanges0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAmiEntitiesRemoved());
	        
	    }
	    @Override
	    public void append(AmiCenterChanges0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAmiEntitiesRemoved());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte[] amiEntitiesRemoved";
	    }
	    @Override
	    public void clear(AmiCenterChanges0 valued){
	       valued.setAmiEntitiesRemoved(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_amiEntitiesRemoved=new VALUED_PARAM_CLASS_amiEntitiesRemoved();
  

  
    private static final class VALUED_PARAM_CLASS_amiEntitiesUpdated extends AbstractValuedParam<AmiCenterChanges0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 101;
	    }
	    
	    @Override
	    public void write(AmiCenterChanges0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [B}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterChanges0 valued, DataInput stream) throws IOException{
		    
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
		    return 2;
	    }
    
	    @Override
	    public byte getPid() {
            return 25;
	    }
    
	    @Override
	    public String getName() {
            return "amiEntitiesUpdated";
	    }
    
	    @Override
	    public Object getValue(AmiCenterChanges0 valued) {
		    return (byte[])((AmiCenterChanges0)valued).getAmiEntitiesUpdated();
	    }
    
	    @Override
	    public void setValue(AmiCenterChanges0 valued, Object value) {
		    valued.setAmiEntitiesUpdated((byte[])value);
	    }
    
	    @Override
	    public void copy(AmiCenterChanges0 source, AmiCenterChanges0 dest) {
		    dest.setAmiEntitiesUpdated(source.getAmiEntitiesUpdated());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterChanges0 source, AmiCenterChanges0 dest) {
	        return OH.eq(dest.getAmiEntitiesUpdated(),source.getAmiEntitiesUpdated());
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
	    public void append(AmiCenterChanges0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAmiEntitiesUpdated());
	        
	    }
	    @Override
	    public void append(AmiCenterChanges0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAmiEntitiesUpdated());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte[] amiEntitiesUpdated";
	    }
	    @Override
	    public void clear(AmiCenterChanges0 valued){
	       valued.setAmiEntitiesUpdated(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_amiEntitiesUpdated=new VALUED_PARAM_CLASS_amiEntitiesUpdated();
  

  
    private static final class VALUED_PARAM_CLASS_amiValuesStringPoolMap extends AbstractValuedParam<AmiCenterChanges0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 101;
	    }
	    
	    @Override
	    public void write(AmiCenterChanges0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [B}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterChanges0 valued, DataInput stream) throws IOException{
		    
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
		    return 3;
	    }
    
	    @Override
	    public byte getPid() {
            return 28;
	    }
    
	    @Override
	    public String getName() {
            return "amiValuesStringPoolMap";
	    }
    
	    @Override
	    public Object getValue(AmiCenterChanges0 valued) {
		    return (byte[])((AmiCenterChanges0)valued).getAmiValuesStringPoolMap();
	    }
    
	    @Override
	    public void setValue(AmiCenterChanges0 valued, Object value) {
		    valued.setAmiValuesStringPoolMap((byte[])value);
	    }
    
	    @Override
	    public void copy(AmiCenterChanges0 source, AmiCenterChanges0 dest) {
		    dest.setAmiValuesStringPoolMap(source.getAmiValuesStringPoolMap());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterChanges0 source, AmiCenterChanges0 dest) {
	        return OH.eq(dest.getAmiValuesStringPoolMap(),source.getAmiValuesStringPoolMap());
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
	    public void append(AmiCenterChanges0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAmiValuesStringPoolMap());
	        
	    }
	    @Override
	    public void append(AmiCenterChanges0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAmiValuesStringPoolMap());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte[] amiValuesStringPoolMap";
	    }
	    @Override
	    public void clear(AmiCenterChanges0 valued){
	       valued.setAmiValuesStringPoolMap(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_amiValuesStringPoolMap=new VALUED_PARAM_CLASS_amiValuesStringPoolMap();
  

  
    private static final class VALUED_PARAM_CLASS_eyeProcessUid extends AbstractValuedParam<AmiCenterChanges0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterChanges0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterChanges0 valued, DataInput stream) throws IOException{
		    
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
            return 21;
	    }
    
	    @Override
	    public String getName() {
            return "eyeProcessUid";
	    }
    
	    @Override
	    public Object getValue(AmiCenterChanges0 valued) {
		    return (java.lang.String)((AmiCenterChanges0)valued).getEyeProcessUid();
	    }
    
	    @Override
	    public void setValue(AmiCenterChanges0 valued, Object value) {
		    valued.setEyeProcessUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterChanges0 source, AmiCenterChanges0 dest) {
		    dest.setEyeProcessUid(source.getEyeProcessUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterChanges0 source, AmiCenterChanges0 dest) {
	        return OH.eq(dest.getEyeProcessUid(),source.getEyeProcessUid());
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
	    public void append(AmiCenterChanges0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getEyeProcessUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterChanges0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getEyeProcessUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String eyeProcessUid";
	    }
	    @Override
	    public void clear(AmiCenterChanges0 valued){
	       valued.setEyeProcessUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_eyeProcessUid=new VALUED_PARAM_CLASS_eyeProcessUid();
  

  
    private static final class VALUED_PARAM_CLASS_responseNum extends AbstractValuedParam<AmiCenterChanges0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterChanges0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getResponseNum());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterChanges0 valued, DataInput stream) throws IOException{
		    
		      valued.setResponseNum(stream.readInt());
		    
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
		    return 5;
	    }
    
	    @Override
	    public byte getPid() {
            return 29;
	    }
    
	    @Override
	    public String getName() {
            return "responseNum";
	    }
    
	    @Override
	    public Object getValue(AmiCenterChanges0 valued) {
		    return (int)((AmiCenterChanges0)valued).getResponseNum();
	    }
    
	    @Override
	    public void setValue(AmiCenterChanges0 valued, Object value) {
		    valued.setResponseNum((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterChanges0 source, AmiCenterChanges0 dest) {
		    dest.setResponseNum(source.getResponseNum());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterChanges0 source, AmiCenterChanges0 dest) {
	        return OH.eq(dest.getResponseNum(),source.getResponseNum());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterChanges0 valued) {
		    return valued.getResponseNum();
	    }
    
	    @Override
	    public void setInt(AmiCenterChanges0 valued, int value) {
		    valued.setResponseNum(value);
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
	    public void append(AmiCenterChanges0 valued, StringBuilder sb){
	        
	        sb.append(valued.getResponseNum());
	        
	    }
	    @Override
	    public void append(AmiCenterChanges0 valued, StringBuildable sb){
	        
	        sb.append(valued.getResponseNum());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int responseNum";
	    }
	    @Override
	    public void clear(AmiCenterChanges0 valued){
	       valued.setResponseNum(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_responseNum=new VALUED_PARAM_CLASS_responseNum();
  

  
    private static final class VALUED_PARAM_CLASS_seqNum extends AbstractValuedParam<AmiCenterChanges0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterChanges0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getSeqNum());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterChanges0 valued, DataInput stream) throws IOException{
		    
		      valued.setSeqNum(stream.readLong());
		    
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
            return 20;
	    }
    
	    @Override
	    public String getName() {
            return "seqNum";
	    }
    
	    @Override
	    public Object getValue(AmiCenterChanges0 valued) {
		    return (long)((AmiCenterChanges0)valued).getSeqNum();
	    }
    
	    @Override
	    public void setValue(AmiCenterChanges0 valued, Object value) {
		    valued.setSeqNum((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterChanges0 source, AmiCenterChanges0 dest) {
		    dest.setSeqNum(source.getSeqNum());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterChanges0 source, AmiCenterChanges0 dest) {
	        return OH.eq(dest.getSeqNum(),source.getSeqNum());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterChanges0 valued) {
		    return valued.getSeqNum();
	    }
    
	    @Override
	    public void setLong(AmiCenterChanges0 valued, long value) {
		    valued.setSeqNum(value);
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
	    public void append(AmiCenterChanges0 valued, StringBuilder sb){
	        
	        sb.append(valued.getSeqNum());
	        
	    }
	    @Override
	    public void append(AmiCenterChanges0 valued, StringBuildable sb){
	        
	        sb.append(valued.getSeqNum());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long seqNum";
	    }
	    @Override
	    public void clear(AmiCenterChanges0 valued){
	       valued.setSeqNum(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_seqNum=new VALUED_PARAM_CLASS_seqNum();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_amiEntitiesAdded, VALUED_PARAM_amiEntitiesRemoved, VALUED_PARAM_amiEntitiesUpdated, VALUED_PARAM_amiValuesStringPoolMap, VALUED_PARAM_eyeProcessUid, VALUED_PARAM_responseNum, VALUED_PARAM_seqNum, };



    private static final byte PIDS[]={ 24 ,26,25,28,21,29,20};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 24: return VALUED_PARAM_amiEntitiesAdded;
             case 26: return VALUED_PARAM_amiEntitiesRemoved;
             case 25: return VALUED_PARAM_amiEntitiesUpdated;
             case 28: return VALUED_PARAM_amiValuesStringPoolMap;
             case 21: return VALUED_PARAM_eyeProcessUid;
             case 29: return VALUED_PARAM_responseNum;
             case 20: return VALUED_PARAM_seqNum;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 24: return true;
             case 26: return true;
             case 25: return true;
             case 28: return true;
             case 21: return true;
             case 29: return true;
             case 20: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 24: return "amiEntitiesAdded";
             case 26: return "amiEntitiesRemoved";
             case 25: return "amiEntitiesUpdated";
             case 28: return "amiValuesStringPoolMap";
             case 21: return "eyeProcessUid";
             case 29: return "responseNum";
             case 20: return "seqNum";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 24: return 0;
             case 26: return 1;
             case 25: return 2;
             case 28: return 3;
             case 21: return 4;
             case 29: return 5;
             case 20: return 6;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="amiEntitiesAdded") return 24;
             if(name=="amiEntitiesRemoved") return 26;
             if(name=="amiEntitiesUpdated") return 25;
             if(name=="amiValuesStringPoolMap") return 28;
             if(name=="eyeProcessUid") return 21;
             if(name=="responseNum") return 29;
             if(name=="seqNum") return 20;
            
             if("amiEntitiesAdded".equals(name)) return 24;
             if("amiEntitiesRemoved".equals(name)) return 26;
             if("amiEntitiesUpdated".equals(name)) return 25;
             if("amiValuesStringPoolMap".equals(name)) return 28;
             if("eyeProcessUid".equals(name)) return 21;
             if("responseNum".equals(name)) return 29;
             if("seqNum".equals(name)) return 20;
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
             case 24: return  this._amiEntitiesAdded; 
             case 26: return  this._amiEntitiesRemoved; 
             case 25: return  this._amiEntitiesUpdated; 
             case 28: return  this._amiValuesStringPoolMap; 
             case 21: return  this._eyeProcessUid; 
             case 29: return  OH.valueOf(this._responseNum); 
             case 20: return  OH.valueOf(this._seqNum); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 24: return byte[].class;
             case 26: return byte[].class;
             case 25: return byte[].class;
             case 28: return byte[].class;
             case 21: return java.lang.String.class;
             case 29: return int.class;
             case 20: return long.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 24: return 101;
             case 26: return 101;
             case 25: return 101;
             case 28: return 101;
             case 21: return 20;
             case 29: return 4;
             case 20: return 6;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 24: this._amiEntitiesAdded=(byte[])value;return;
             case 26: this._amiEntitiesRemoved=(byte[])value;return;
             case 25: this._amiEntitiesUpdated=(byte[])value;return;
             case 28: this._amiValuesStringPoolMap=(byte[])value;return;
             case 21: this._eyeProcessUid=(java.lang.String)value;return;
             case 29: this._responseNum=(java.lang.Integer)value;return;
             case 20: this._seqNum=(java.lang.Long)value;return;
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
             case 24: this._amiEntitiesAdded=(byte[])value;return true;
             case 26: this._amiEntitiesRemoved=(byte[])value;return true;
             case 25: this._amiEntitiesUpdated=(byte[])value;return true;
             case 28: this._amiValuesStringPoolMap=(byte[])value;return true;
             case 21: this._eyeProcessUid=(java.lang.String)value;return true;
             case 29: this._responseNum=(java.lang.Integer)value;return true;
             case 20: this._seqNum=(java.lang.Long)value;return true;
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
             case 29: return this._responseNum;
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
             case 20: return this._seqNum;
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
             case 29: this._responseNum=value;return;
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
             case 20: this._seqNum=value;return;
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
        
        case 20:
        
            if((basicType=in.readByte())!=6)
                break;
            this._seqNum=in.readLong();
        
            break;

        case 21:
        
            this._eyeProcessUid=(java.lang.String)converter.read(session);
        
            break;

        case 24:
        
            this._amiEntitiesAdded=(byte[])converter.read(session);
        
            break;

        case 25:
        
            this._amiEntitiesUpdated=(byte[])converter.read(session);
        
            break;

        case 26:
        
            this._amiEntitiesRemoved=(byte[])converter.read(session);
        
            break;

        case 28:
        
            this._amiValuesStringPoolMap=(byte[])converter.read(session);
        
            break;

        case 29:
        
            if((basicType=in.readByte())!=4)
                break;
            this._responseNum=in.readInt();
        
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
        
if(this._seqNum!=0L && (0 & transience)==0){
    out.writeByte(20);
        
    out.writeByte(6);
    out.writeLong(this._seqNum);
        
}

if(this._eyeProcessUid!=null && (0 & transience)==0){
    out.writeByte(21);
        
    converter.write(this._eyeProcessUid,session);
        
}

if(this._amiEntitiesAdded!=null && (0 & transience)==0){
    out.writeByte(24);
        
    converter.write(this._amiEntitiesAdded,session);
        
}

if(this._amiEntitiesUpdated!=null && (0 & transience)==0){
    out.writeByte(25);
        
    converter.write(this._amiEntitiesUpdated,session);
        
}

if(this._amiEntitiesRemoved!=null && (0 & transience)==0){
    out.writeByte(26);
        
    converter.write(this._amiEntitiesRemoved,session);
        
}

if(this._amiValuesStringPoolMap!=null && (0 & transience)==0){
    out.writeByte(28);
        
    converter.write(this._amiValuesStringPoolMap,session);
        
}

if(this._responseNum!=0 && (0 & transience)==0){
    out.writeByte(29);
        
    out.writeByte(4);
    out.writeInt(this._responseNum);
        
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