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

public abstract class AmiRelayObjectMessage0 implements com.f1.ami.amicommon.msg.AmiRelayObjectMessage ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private short _appIdStringKey;

    private int _connectionId;

    private long _expires;

    private java.lang.String _id;

    private long _origSeqNum;

    private byte[] _params;

    private byte _transformState;

    private java.lang.String _type;

    private static final String NAMES[]={ "appIdStringKey" ,"connectionId","expires","id","origSeqNum","params","transformState","type"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 18;
        try{
        switch(h){

                case 0:

                    if(name == "type" || name.equals("type")) {this._type=(java.lang.String)value;return;}
break;
                case 1:

                    if(name == "connectionId" || name.equals("connectionId")) {this._connectionId=(java.lang.Integer)value;return;}
break;
                case 3:

                    if(name == "transformState" || name.equals("transformState")) {this._transformState=(java.lang.Byte)value;return;}
break;
                case 4:

                    if(name == "expires" || name.equals("expires")) {this._expires=(java.lang.Long)value;return;}
break;
                case 7:

                    if(name == "id" || name.equals("id")) {this._id=(java.lang.String)value;return;}
break;
                case 8:

                    if(name == "params" || name.equals("params")) {this._params=(byte[])value;return;}
break;
                case 12:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {this._origSeqNum=(java.lang.Long)value;return;}
break;
                case 14:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {this._appIdStringKey=(java.lang.Short)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 18;
        switch(h){

                case 0:

                    if(name == "type" || name.equals("type")) {this._type=(java.lang.String)value;return true;}
break;
                case 1:

                    if(name == "connectionId" || name.equals("connectionId")) {this._connectionId=(java.lang.Integer)value;return true;}
break;
                case 3:

                    if(name == "transformState" || name.equals("transformState")) {this._transformState=(java.lang.Byte)value;return true;}
break;
                case 4:

                    if(name == "expires" || name.equals("expires")) {this._expires=(java.lang.Long)value;return true;}
break;
                case 7:

                    if(name == "id" || name.equals("id")) {this._id=(java.lang.String)value;return true;}
break;
                case 8:

                    if(name == "params" || name.equals("params")) {this._params=(byte[])value;return true;}
break;
                case 12:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {this._origSeqNum=(java.lang.Long)value;return true;}
break;
                case 14:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {this._appIdStringKey=(java.lang.Short)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 18;
        switch(h){

                case 0:

		    
                    if(name == "type" || name.equals("type")) {return this._type;}
            
break;
                case 1:

		    
                    if(name == "connectionId" || name.equals("connectionId")) {return OH.valueOf(this._connectionId);}
		    
break;
                case 3:

		    
                    if(name == "transformState" || name.equals("transformState")) {return OH.valueOf(this._transformState);}
		    
break;
                case 4:

		    
                    if(name == "expires" || name.equals("expires")) {return OH.valueOf(this._expires);}
		    
break;
                case 7:

		    
                    if(name == "id" || name.equals("id")) {return this._id;}
            
break;
                case 8:

		    
                    if(name == "params" || name.equals("params")) {return this._params;}
            
break;
                case 12:

		    
                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return OH.valueOf(this._origSeqNum);}
		    
break;
                case 14:

		    
                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return OH.valueOf(this._appIdStringKey);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 18;
        switch(h){

                case 0:

                    if(name == "type" || name.equals("type")) {return java.lang.String.class;}
break;
                case 1:

                    if(name == "connectionId" || name.equals("connectionId")) {return int.class;}
break;
                case 3:

                    if(name == "transformState" || name.equals("transformState")) {return byte.class;}
break;
                case 4:

                    if(name == "expires" || name.equals("expires")) {return long.class;}
break;
                case 7:

                    if(name == "id" || name.equals("id")) {return java.lang.String.class;}
break;
                case 8:

                    if(name == "params" || name.equals("params")) {return byte[].class;}
break;
                case 12:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return long.class;}
break;
                case 14:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return short.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 18;
        switch(h){

                case 0:

                    if(name == "type" || name.equals("type")) {return VALUED_PARAM_type;}
break;
                case 1:

                    if(name == "connectionId" || name.equals("connectionId")) {return VALUED_PARAM_connectionId;}
break;
                case 3:

                    if(name == "transformState" || name.equals("transformState")) {return VALUED_PARAM_transformState;}
break;
                case 4:

                    if(name == "expires" || name.equals("expires")) {return VALUED_PARAM_expires;}
break;
                case 7:

                    if(name == "id" || name.equals("id")) {return VALUED_PARAM_id;}
break;
                case 8:

                    if(name == "params" || name.equals("params")) {return VALUED_PARAM_params;}
break;
                case 12:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return VALUED_PARAM_origSeqNum;}
break;
                case 14:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return VALUED_PARAM_appIdStringKey;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 18;
        switch(h){

                case 0:

                    if(name == "type" || name.equals("type")) {return 7;}
break;
                case 1:

                    if(name == "connectionId" || name.equals("connectionId")) {return 1;}
break;
                case 3:

                    if(name == "transformState" || name.equals("transformState")) {return 6;}
break;
                case 4:

                    if(name == "expires" || name.equals("expires")) {return 2;}
break;
                case 7:

                    if(name == "id" || name.equals("id")) {return 3;}
break;
                case 8:

                    if(name == "params" || name.equals("params")) {return 5;}
break;
                case 12:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return 4;}
break;
                case 14:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return 0;}
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
        return 8;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiRelayObjectMessage0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 18;
        switch(h){

                case 0:

                    if(name == "type" || name.equals("type")) {return true;}
break;
                case 1:

                    if(name == "connectionId" || name.equals("connectionId")) {return true;}
break;
                case 3:

                    if(name == "transformState" || name.equals("transformState")) {return true;}
break;
                case 4:

                    if(name == "expires" || name.equals("expires")) {return true;}
break;
                case 7:

                    if(name == "id" || name.equals("id")) {return true;}
break;
                case 8:

                    if(name == "params" || name.equals("params")) {return true;}
break;
                case 12:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return true;}
break;
                case 14:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 18;
        switch(h){

                case 0:

                    if(name == "type" || name.equals("type")) {return 20;}
break;
                case 1:

                    if(name == "connectionId" || name.equals("connectionId")) {return 4;}
break;
                case 3:

                    if(name == "transformState" || name.equals("transformState")) {return 1;}
break;
                case 4:

                    if(name == "expires" || name.equals("expires")) {return 6;}
break;
                case 7:

                    if(name == "id" || name.equals("id")) {return 20;}
break;
                case 8:

                    if(name == "params" || name.equals("params")) {return 101;}
break;
                case 12:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return 6;}
break;
                case 14:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return 2;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _appIdStringKey;

        case 1:return _connectionId;

        case 2:return _expires;

        case 3:return _id;

        case 4:return _origSeqNum;

        case 5:return _params;

        case 6:return _transformState;

        case 7:return _type;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 8);
    }

    public short getAppIdStringKey(){
        return this._appIdStringKey;
    }
    public void setAppIdStringKey(short _appIdStringKey){
    
        this._appIdStringKey=_appIdStringKey;
    }

    public int getConnectionId(){
        return this._connectionId;
    }
    public void setConnectionId(int _connectionId){
    
        this._connectionId=_connectionId;
    }

    public long getExpires(){
        return this._expires;
    }
    public void setExpires(long _expires){
    
        this._expires=_expires;
    }

    public java.lang.String getId(){
        return this._id;
    }
    public void setId(java.lang.String _id){
    
        this._id=_id;
    }

    public long getOrigSeqNum(){
        return this._origSeqNum;
    }
    public void setOrigSeqNum(long _origSeqNum){
    
        this._origSeqNum=_origSeqNum;
    }

    public byte[] getParams(){
        return this._params;
    }
    public void setParams(byte[] _params){
    
        this._params=_params;
    }

    public byte getTransformState(){
        return this._transformState;
    }
    public void setTransformState(byte _transformState){
    
        this._transformState=_transformState;
    }

    public java.lang.String getType(){
        return this._type;
    }
    public void setType(java.lang.String _type){
    
        this._type=_type;
    }





  
    private static final class VALUED_PARAM_CLASS_appIdStringKey extends AbstractValuedParam<AmiRelayObjectMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 2;
	    }
	    
	    @Override
	    public void write(AmiRelayObjectMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeShort(valued.getAppIdStringKey());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayObjectMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setAppIdStringKey(stream.readShort());
		    
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
            return 44;
	    }
    
	    @Override
	    public String getName() {
            return "appIdStringKey";
	    }
    
	    @Override
	    public Object getValue(AmiRelayObjectMessage0 valued) {
		    return (short)((AmiRelayObjectMessage0)valued).getAppIdStringKey();
	    }
    
	    @Override
	    public void setValue(AmiRelayObjectMessage0 valued, Object value) {
		    valued.setAppIdStringKey((java.lang.Short)value);
	    }
    
	    @Override
	    public void copy(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
		    dest.setAppIdStringKey(source.getAppIdStringKey());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
	        return OH.eq(dest.getAppIdStringKey(),source.getAppIdStringKey());
	    }
	    
	    
	    
	    
	    
	    
	    @Override
	    public short getShort(AmiRelayObjectMessage0 valued) {
		    return valued.getAppIdStringKey();
	    }
    
	    @Override
	    public void setShort(AmiRelayObjectMessage0 valued, short value) {
		    valued.setAppIdStringKey(value);
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
	    public void append(AmiRelayObjectMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAppIdStringKey());
	        
	    }
	    @Override
	    public void append(AmiRelayObjectMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAppIdStringKey());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:short appIdStringKey";
	    }
	    @Override
	    public void clear(AmiRelayObjectMessage0 valued){
	       valued.setAppIdStringKey((short)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_appIdStringKey=new VALUED_PARAM_CLASS_appIdStringKey();
  

  
    private static final class VALUED_PARAM_CLASS_connectionId extends AbstractValuedParam<AmiRelayObjectMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiRelayObjectMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getConnectionId());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayObjectMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setConnectionId(stream.readInt());
		    
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
		    return 1;
	    }
    
	    @Override
	    public byte getPid() {
            return 10;
	    }
    
	    @Override
	    public String getName() {
            return "connectionId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayObjectMessage0 valued) {
		    return (int)((AmiRelayObjectMessage0)valued).getConnectionId();
	    }
    
	    @Override
	    public void setValue(AmiRelayObjectMessage0 valued, Object value) {
		    valued.setConnectionId((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
		    dest.setConnectionId(source.getConnectionId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
	        return OH.eq(dest.getConnectionId(),source.getConnectionId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiRelayObjectMessage0 valued) {
		    return valued.getConnectionId();
	    }
    
	    @Override
	    public void setInt(AmiRelayObjectMessage0 valued, int value) {
		    valued.setConnectionId(value);
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
	    public void append(AmiRelayObjectMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getConnectionId());
	        
	    }
	    @Override
	    public void append(AmiRelayObjectMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getConnectionId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int connectionId";
	    }
	    @Override
	    public void clear(AmiRelayObjectMessage0 valued){
	       valued.setConnectionId(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_connectionId=new VALUED_PARAM_CLASS_connectionId();
  

  
    private static final class VALUED_PARAM_CLASS_expires extends AbstractValuedParam<AmiRelayObjectMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiRelayObjectMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getExpires());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayObjectMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setExpires(stream.readLong());
		    
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
            return 11;
	    }
    
	    @Override
	    public String getName() {
            return "expires";
	    }
    
	    @Override
	    public Object getValue(AmiRelayObjectMessage0 valued) {
		    return (long)((AmiRelayObjectMessage0)valued).getExpires();
	    }
    
	    @Override
	    public void setValue(AmiRelayObjectMessage0 valued, Object value) {
		    valued.setExpires((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
		    dest.setExpires(source.getExpires());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
	        return OH.eq(dest.getExpires(),source.getExpires());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiRelayObjectMessage0 valued) {
		    return valued.getExpires();
	    }
    
	    @Override
	    public void setLong(AmiRelayObjectMessage0 valued, long value) {
		    valued.setExpires(value);
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
	    public void append(AmiRelayObjectMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getExpires());
	        
	    }
	    @Override
	    public void append(AmiRelayObjectMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getExpires());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long expires";
	    }
	    @Override
	    public void clear(AmiRelayObjectMessage0 valued){
	       valued.setExpires(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_expires=new VALUED_PARAM_CLASS_expires();
  

  
    private static final class VALUED_PARAM_CLASS_id extends AbstractValuedParam<AmiRelayObjectMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayObjectMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayObjectMessage0 valued, DataInput stream) throws IOException{
		    
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
            return "id";
	    }
    
	    @Override
	    public Object getValue(AmiRelayObjectMessage0 valued) {
		    return (java.lang.String)((AmiRelayObjectMessage0)valued).getId();
	    }
    
	    @Override
	    public void setValue(AmiRelayObjectMessage0 valued, Object value) {
		    valued.setId((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
		    dest.setId(source.getId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
	        return OH.eq(dest.getId(),source.getId());
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
	    public void append(AmiRelayObjectMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getId(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayObjectMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getId(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String id";
	    }
	    @Override
	    public void clear(AmiRelayObjectMessage0 valued){
	       valued.setId(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_id=new VALUED_PARAM_CLASS_id();
  

  
    private static final class VALUED_PARAM_CLASS_origSeqNum extends AbstractValuedParam<AmiRelayObjectMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiRelayObjectMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getOrigSeqNum());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayObjectMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setOrigSeqNum(stream.readLong());
		    
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
            return 45;
	    }
    
	    @Override
	    public String getName() {
            return "origSeqNum";
	    }
    
	    @Override
	    public Object getValue(AmiRelayObjectMessage0 valued) {
		    return (long)((AmiRelayObjectMessage0)valued).getOrigSeqNum();
	    }
    
	    @Override
	    public void setValue(AmiRelayObjectMessage0 valued, Object value) {
		    valued.setOrigSeqNum((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
		    dest.setOrigSeqNum(source.getOrigSeqNum());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
	        return OH.eq(dest.getOrigSeqNum(),source.getOrigSeqNum());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiRelayObjectMessage0 valued) {
		    return valued.getOrigSeqNum();
	    }
    
	    @Override
	    public void setLong(AmiRelayObjectMessage0 valued, long value) {
		    valued.setOrigSeqNum(value);
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
	    public void append(AmiRelayObjectMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getOrigSeqNum());
	        
	    }
	    @Override
	    public void append(AmiRelayObjectMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getOrigSeqNum());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long origSeqNum";
	    }
	    @Override
	    public void clear(AmiRelayObjectMessage0 valued){
	       valued.setOrigSeqNum(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_origSeqNum=new VALUED_PARAM_CLASS_origSeqNum();
  

  
    private static final class VALUED_PARAM_CLASS_params extends AbstractValuedParam<AmiRelayObjectMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 101;
	    }
	    
	    @Override
	    public void write(AmiRelayObjectMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [B}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayObjectMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 5;
	    }
    
	    @Override
	    public byte getPid() {
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "params";
	    }
    
	    @Override
	    public Object getValue(AmiRelayObjectMessage0 valued) {
		    return (byte[])((AmiRelayObjectMessage0)valued).getParams();
	    }
    
	    @Override
	    public void setValue(AmiRelayObjectMessage0 valued, Object value) {
		    valued.setParams((byte[])value);
	    }
    
	    @Override
	    public void copy(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
		    dest.setParams(source.getParams());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
	        return OH.eq(dest.getParams(),source.getParams());
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
	    public void append(AmiRelayObjectMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getParams());
	        
	    }
	    @Override
	    public void append(AmiRelayObjectMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getParams());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte[] params";
	    }
	    @Override
	    public void clear(AmiRelayObjectMessage0 valued){
	       valued.setParams(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_params=new VALUED_PARAM_CLASS_params();
  

  
    private static final class VALUED_PARAM_CLASS_transformState extends AbstractValuedParam<AmiRelayObjectMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiRelayObjectMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getTransformState());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayObjectMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setTransformState(stream.readByte());
		    
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
            return 46;
	    }
    
	    @Override
	    public String getName() {
            return "transformState";
	    }
    
	    @Override
	    public Object getValue(AmiRelayObjectMessage0 valued) {
		    return (byte)((AmiRelayObjectMessage0)valued).getTransformState();
	    }
    
	    @Override
	    public void setValue(AmiRelayObjectMessage0 valued, Object value) {
		    valued.setTransformState((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
		    dest.setTransformState(source.getTransformState());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
	        return OH.eq(dest.getTransformState(),source.getTransformState());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiRelayObjectMessage0 valued) {
		    return valued.getTransformState();
	    }
    
	    @Override
	    public void setByte(AmiRelayObjectMessage0 valued, byte value) {
		    valued.setTransformState(value);
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
	    public void append(AmiRelayObjectMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTransformState());
	        
	    }
	    @Override
	    public void append(AmiRelayObjectMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTransformState());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte transformState";
	    }
	    @Override
	    public void clear(AmiRelayObjectMessage0 valued){
	       valued.setTransformState((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_transformState=new VALUED_PARAM_CLASS_transformState();
  

  
    private static final class VALUED_PARAM_CLASS_type extends AbstractValuedParam<AmiRelayObjectMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayObjectMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayObjectMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 7;
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
	    public Object getValue(AmiRelayObjectMessage0 valued) {
		    return (java.lang.String)((AmiRelayObjectMessage0)valued).getType();
	    }
    
	    @Override
	    public void setValue(AmiRelayObjectMessage0 valued, Object value) {
		    valued.setType((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
		    dest.setType(source.getType());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayObjectMessage0 source, AmiRelayObjectMessage0 dest) {
	        return OH.eq(dest.getType(),source.getType());
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
	    public void append(AmiRelayObjectMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getType(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayObjectMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getType(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String type";
	    }
	    @Override
	    public void clear(AmiRelayObjectMessage0 valued){
	       valued.setType(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_type=new VALUED_PARAM_CLASS_type();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_appIdStringKey, VALUED_PARAM_connectionId, VALUED_PARAM_expires, VALUED_PARAM_id, VALUED_PARAM_origSeqNum, VALUED_PARAM_params, VALUED_PARAM_transformState, VALUED_PARAM_type, };



    private static final byte PIDS[]={ 44 ,10,11,3,45,4,46,2};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 44: return VALUED_PARAM_appIdStringKey;
             case 10: return VALUED_PARAM_connectionId;
             case 11: return VALUED_PARAM_expires;
             case 3: return VALUED_PARAM_id;
             case 45: return VALUED_PARAM_origSeqNum;
             case 4: return VALUED_PARAM_params;
             case 46: return VALUED_PARAM_transformState;
             case 2: return VALUED_PARAM_type;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 44: return true;
             case 10: return true;
             case 11: return true;
             case 3: return true;
             case 45: return true;
             case 4: return true;
             case 46: return true;
             case 2: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 44: return "appIdStringKey";
             case 10: return "connectionId";
             case 11: return "expires";
             case 3: return "id";
             case 45: return "origSeqNum";
             case 4: return "params";
             case 46: return "transformState";
             case 2: return "type";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 44: return 0;
             case 10: return 1;
             case 11: return 2;
             case 3: return 3;
             case 45: return 4;
             case 4: return 5;
             case 46: return 6;
             case 2: return 7;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="appIdStringKey") return 44;
             if(name=="connectionId") return 10;
             if(name=="expires") return 11;
             if(name=="id") return 3;
             if(name=="origSeqNum") return 45;
             if(name=="params") return 4;
             if(name=="transformState") return 46;
             if(name=="type") return 2;
            
             if("appIdStringKey".equals(name)) return 44;
             if("connectionId".equals(name)) return 10;
             if("expires".equals(name)) return 11;
             if("id".equals(name)) return 3;
             if("origSeqNum".equals(name)) return 45;
             if("params".equals(name)) return 4;
             if("transformState".equals(name)) return 46;
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
             case 44: return  OH.valueOf(this._appIdStringKey); 
             case 10: return  OH.valueOf(this._connectionId); 
             case 11: return  OH.valueOf(this._expires); 
             case 3: return  this._id; 
             case 45: return  OH.valueOf(this._origSeqNum); 
             case 4: return  this._params; 
             case 46: return  OH.valueOf(this._transformState); 
             case 2: return  this._type; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 44: return short.class;
             case 10: return int.class;
             case 11: return long.class;
             case 3: return java.lang.String.class;
             case 45: return long.class;
             case 4: return byte[].class;
             case 46: return byte.class;
             case 2: return java.lang.String.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 44: return 2;
             case 10: return 4;
             case 11: return 6;
             case 3: return 20;
             case 45: return 6;
             case 4: return 101;
             case 46: return 1;
             case 2: return 20;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 44: this._appIdStringKey=(java.lang.Short)value;return;
             case 10: this._connectionId=(java.lang.Integer)value;return;
             case 11: this._expires=(java.lang.Long)value;return;
             case 3: this._id=(java.lang.String)value;return;
             case 45: this._origSeqNum=(java.lang.Long)value;return;
             case 4: this._params=(byte[])value;return;
             case 46: this._transformState=(java.lang.Byte)value;return;
             case 2: this._type=(java.lang.String)value;return;
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
             case 44: this._appIdStringKey=(java.lang.Short)value;return true;
             case 10: this._connectionId=(java.lang.Integer)value;return true;
             case 11: this._expires=(java.lang.Long)value;return true;
             case 3: this._id=(java.lang.String)value;return true;
             case 45: this._origSeqNum=(java.lang.Long)value;return true;
             case 4: this._params=(byte[])value;return true;
             case 46: this._transformState=(java.lang.Byte)value;return true;
             case 2: this._type=(java.lang.String)value;return true;
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
             case 46: return this._transformState;
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
             case 44: return this._appIdStringKey;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for short value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askInt(byte pid){
        switch(pid){
             case 10: return this._connectionId;
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
             case 11: return this._expires;
             case 45: return this._origSeqNum;
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
             case 46: this._transformState=value;return;
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
             case 44: this._appIdStringKey=value;return;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for short value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putInt(byte pid,int value){
    
        switch(pid){
             case 10: this._connectionId=value;return;
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
             case 11: this._expires=value;return;
             case 45: this._origSeqNum=value;return;
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
        
        case 2:
        
            this._type=(java.lang.String)converter.read(session);
        
            break;

        case 3:
        
            this._id=(java.lang.String)converter.read(session);
        
            break;

        case 4:
        
            this._params=(byte[])converter.read(session);
        
            break;

        case 10:
        
            if((basicType=in.readByte())!=4)
                break;
            this._connectionId=in.readInt();
        
            break;

        case 11:
        
            if((basicType=in.readByte())!=6)
                break;
            this._expires=in.readLong();
        
            break;

        case 44:
        
            if((basicType=in.readByte())!=2)
                break;
            this._appIdStringKey=in.readShort();
        
            break;

        case 45:
        
            if((basicType=in.readByte())!=6)
                break;
            this._origSeqNum=in.readLong();
        
            break;

        case 46:
        
            if((basicType=in.readByte())!=1)
                break;
            this._transformState=in.readByte();
        
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
        
if(this._type!=null && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this._type,session);
        
}

if(this._id!=null && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this._id,session);
        
}

if(this._params!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._params,session);
        
}

if(this._connectionId!=0 && (0 & transience)==0){
    out.writeByte(10);
        
    out.writeByte(4);
    out.writeInt(this._connectionId);
        
}

if(this._expires!=0L && (0 & transience)==0){
    out.writeByte(11);
        
    out.writeByte(6);
    out.writeLong(this._expires);
        
}

if(this._appIdStringKey!=(short)0 && (0 & transience)==0){
    out.writeByte(44);
        
    out.writeByte(2);
    out.writeShort(this._appIdStringKey);
        
}

if(this._origSeqNum!=0L && (0 & transience)==0){
    out.writeByte(45);
        
    out.writeByte(6);
    out.writeLong(this._origSeqNum);
        
}

if(this._transformState!=(byte)0 && (0 & transience)==0){
    out.writeByte(46);
        
    out.writeByte(1);
    out.writeByte(this._transformState);
        
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