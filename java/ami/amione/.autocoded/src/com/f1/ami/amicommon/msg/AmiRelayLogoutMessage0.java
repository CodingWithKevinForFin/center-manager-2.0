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

public abstract class AmiRelayLogoutMessage0 implements com.f1.ami.amicommon.msg.AmiRelayLogoutMessage ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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

    private boolean _cleanLogout;

    private int _connectionId;

    private long _origSeqNum;

    private byte[] _params;

    private byte _transformState;

    private static final String NAMES[]={ "appIdStringKey" ,"cleanLogout","connectionId","origSeqNum","params","transformState"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 7;
        try{
        switch(h){

                case 0:

                    if(name == "connectionId" || name.equals("connectionId")) {this._connectionId=(java.lang.Integer)value;return;}
break;
                case 1:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {this._appIdStringKey=(java.lang.Short)value;return;}
break;
                case 2:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {this._origSeqNum=(java.lang.Long)value;return;}
break;
                case 4:

                    if(name == "params" || name.equals("params")) {this._params=(byte[])value;return;}
break;
                case 5:

                    if(name == "transformState" || name.equals("transformState")) {this._transformState=(java.lang.Byte)value;return;}
break;
                case 6:

                    if(name == "cleanLogout" || name.equals("cleanLogout")) {this._cleanLogout=(java.lang.Boolean)value;return;}
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

                    if(name == "connectionId" || name.equals("connectionId")) {this._connectionId=(java.lang.Integer)value;return true;}
break;
                case 1:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {this._appIdStringKey=(java.lang.Short)value;return true;}
break;
                case 2:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {this._origSeqNum=(java.lang.Long)value;return true;}
break;
                case 4:

                    if(name == "params" || name.equals("params")) {this._params=(byte[])value;return true;}
break;
                case 5:

                    if(name == "transformState" || name.equals("transformState")) {this._transformState=(java.lang.Byte)value;return true;}
break;
                case 6:

                    if(name == "cleanLogout" || name.equals("cleanLogout")) {this._cleanLogout=(java.lang.Boolean)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 0:

		    
                    if(name == "connectionId" || name.equals("connectionId")) {return OH.valueOf(this._connectionId);}
		    
break;
                case 1:

		    
                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return OH.valueOf(this._appIdStringKey);}
		    
break;
                case 2:

		    
                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return OH.valueOf(this._origSeqNum);}
		    
break;
                case 4:

		    
                    if(name == "params" || name.equals("params")) {return this._params;}
            
break;
                case 5:

		    
                    if(name == "transformState" || name.equals("transformState")) {return OH.valueOf(this._transformState);}
		    
break;
                case 6:

		    
                    if(name == "cleanLogout" || name.equals("cleanLogout")) {return OH.valueOf(this._cleanLogout);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 0:

                    if(name == "connectionId" || name.equals("connectionId")) {return int.class;}
break;
                case 1:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return short.class;}
break;
                case 2:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return long.class;}
break;
                case 4:

                    if(name == "params" || name.equals("params")) {return byte[].class;}
break;
                case 5:

                    if(name == "transformState" || name.equals("transformState")) {return byte.class;}
break;
                case 6:

                    if(name == "cleanLogout" || name.equals("cleanLogout")) {return boolean.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 0:

                    if(name == "connectionId" || name.equals("connectionId")) {return VALUED_PARAM_connectionId;}
break;
                case 1:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return VALUED_PARAM_appIdStringKey;}
break;
                case 2:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return VALUED_PARAM_origSeqNum;}
break;
                case 4:

                    if(name == "params" || name.equals("params")) {return VALUED_PARAM_params;}
break;
                case 5:

                    if(name == "transformState" || name.equals("transformState")) {return VALUED_PARAM_transformState;}
break;
                case 6:

                    if(name == "cleanLogout" || name.equals("cleanLogout")) {return VALUED_PARAM_cleanLogout;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 0:

                    if(name == "connectionId" || name.equals("connectionId")) {return 2;}
break;
                case 1:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return 0;}
break;
                case 2:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return 3;}
break;
                case 4:

                    if(name == "params" || name.equals("params")) {return 4;}
break;
                case 5:

                    if(name == "transformState" || name.equals("transformState")) {return 5;}
break;
                case 6:

                    if(name == "cleanLogout" || name.equals("cleanLogout")) {return 1;}
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
        return 6;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiRelayLogoutMessage0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 0:

                    if(name == "connectionId" || name.equals("connectionId")) {return true;}
break;
                case 1:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return true;}
break;
                case 2:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return true;}
break;
                case 4:

                    if(name == "params" || name.equals("params")) {return true;}
break;
                case 5:

                    if(name == "transformState" || name.equals("transformState")) {return true;}
break;
                case 6:

                    if(name == "cleanLogout" || name.equals("cleanLogout")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 7;
        switch(h){

                case 0:

                    if(name == "connectionId" || name.equals("connectionId")) {return 4;}
break;
                case 1:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return 2;}
break;
                case 2:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return 6;}
break;
                case 4:

                    if(name == "params" || name.equals("params")) {return 101;}
break;
                case 5:

                    if(name == "transformState" || name.equals("transformState")) {return 1;}
break;
                case 6:

                    if(name == "cleanLogout" || name.equals("cleanLogout")) {return 0;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _appIdStringKey;

        case 1:return _cleanLogout;

        case 2:return _connectionId;

        case 3:return _origSeqNum;

        case 4:return _params;

        case 5:return _transformState;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 6);
    }

    public short getAppIdStringKey(){
        return this._appIdStringKey;
    }
    public void setAppIdStringKey(short _appIdStringKey){
    
        this._appIdStringKey=_appIdStringKey;
    }

    public boolean getCleanLogout(){
        return this._cleanLogout;
    }
    public void setCleanLogout(boolean _cleanLogout){
    
        this._cleanLogout=_cleanLogout;
    }

    public int getConnectionId(){
        return this._connectionId;
    }
    public void setConnectionId(int _connectionId){
    
        this._connectionId=_connectionId;
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





  
    private static final class VALUED_PARAM_CLASS_appIdStringKey extends AbstractValuedParam<AmiRelayLogoutMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 2;
	    }
	    
	    @Override
	    public void write(AmiRelayLogoutMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeShort(valued.getAppIdStringKey());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayLogoutMessage0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiRelayLogoutMessage0 valued) {
		    return (short)((AmiRelayLogoutMessage0)valued).getAppIdStringKey();
	    }
    
	    @Override
	    public void setValue(AmiRelayLogoutMessage0 valued, Object value) {
		    valued.setAppIdStringKey((java.lang.Short)value);
	    }
    
	    @Override
	    public void copy(AmiRelayLogoutMessage0 source, AmiRelayLogoutMessage0 dest) {
		    dest.setAppIdStringKey(source.getAppIdStringKey());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayLogoutMessage0 source, AmiRelayLogoutMessage0 dest) {
	        return OH.eq(dest.getAppIdStringKey(),source.getAppIdStringKey());
	    }
	    
	    
	    
	    
	    
	    
	    @Override
	    public short getShort(AmiRelayLogoutMessage0 valued) {
		    return valued.getAppIdStringKey();
	    }
    
	    @Override
	    public void setShort(AmiRelayLogoutMessage0 valued, short value) {
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
	    public void append(AmiRelayLogoutMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAppIdStringKey());
	        
	    }
	    @Override
	    public void append(AmiRelayLogoutMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAppIdStringKey());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:short appIdStringKey";
	    }
	    @Override
	    public void clear(AmiRelayLogoutMessage0 valued){
	       valued.setAppIdStringKey((short)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_appIdStringKey=new VALUED_PARAM_CLASS_appIdStringKey();
  

  
    private static final class VALUED_PARAM_CLASS_cleanLogout extends AbstractValuedParam<AmiRelayLogoutMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiRelayLogoutMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getCleanLogout());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayLogoutMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setCleanLogout(stream.readBoolean());
		    
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
            return 9;
	    }
    
	    @Override
	    public String getName() {
            return "cleanLogout";
	    }
    
	    @Override
	    public Object getValue(AmiRelayLogoutMessage0 valued) {
		    return (boolean)((AmiRelayLogoutMessage0)valued).getCleanLogout();
	    }
    
	    @Override
	    public void setValue(AmiRelayLogoutMessage0 valued, Object value) {
		    valued.setCleanLogout((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiRelayLogoutMessage0 source, AmiRelayLogoutMessage0 dest) {
		    dest.setCleanLogout(source.getCleanLogout());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayLogoutMessage0 source, AmiRelayLogoutMessage0 dest) {
	        return OH.eq(dest.getCleanLogout(),source.getCleanLogout());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiRelayLogoutMessage0 valued) {
		    return valued.getCleanLogout();
	    }
    
	    @Override
	    public void setBoolean(AmiRelayLogoutMessage0 valued, boolean value) {
		    valued.setCleanLogout(value);
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
	    public void append(AmiRelayLogoutMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getCleanLogout());
	        
	    }
	    @Override
	    public void append(AmiRelayLogoutMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getCleanLogout());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean cleanLogout";
	    }
	    @Override
	    public void clear(AmiRelayLogoutMessage0 valued){
	       valued.setCleanLogout(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_cleanLogout=new VALUED_PARAM_CLASS_cleanLogout();
  

  
    private static final class VALUED_PARAM_CLASS_connectionId extends AbstractValuedParam<AmiRelayLogoutMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiRelayLogoutMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getConnectionId());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayLogoutMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 2;
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
	    public Object getValue(AmiRelayLogoutMessage0 valued) {
		    return (int)((AmiRelayLogoutMessage0)valued).getConnectionId();
	    }
    
	    @Override
	    public void setValue(AmiRelayLogoutMessage0 valued, Object value) {
		    valued.setConnectionId((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiRelayLogoutMessage0 source, AmiRelayLogoutMessage0 dest) {
		    dest.setConnectionId(source.getConnectionId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayLogoutMessage0 source, AmiRelayLogoutMessage0 dest) {
	        return OH.eq(dest.getConnectionId(),source.getConnectionId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiRelayLogoutMessage0 valued) {
		    return valued.getConnectionId();
	    }
    
	    @Override
	    public void setInt(AmiRelayLogoutMessage0 valued, int value) {
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
	    public void append(AmiRelayLogoutMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getConnectionId());
	        
	    }
	    @Override
	    public void append(AmiRelayLogoutMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getConnectionId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int connectionId";
	    }
	    @Override
	    public void clear(AmiRelayLogoutMessage0 valued){
	       valued.setConnectionId(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_connectionId=new VALUED_PARAM_CLASS_connectionId();
  

  
    private static final class VALUED_PARAM_CLASS_origSeqNum extends AbstractValuedParam<AmiRelayLogoutMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiRelayLogoutMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getOrigSeqNum());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayLogoutMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 3;
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
	    public Object getValue(AmiRelayLogoutMessage0 valued) {
		    return (long)((AmiRelayLogoutMessage0)valued).getOrigSeqNum();
	    }
    
	    @Override
	    public void setValue(AmiRelayLogoutMessage0 valued, Object value) {
		    valued.setOrigSeqNum((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiRelayLogoutMessage0 source, AmiRelayLogoutMessage0 dest) {
		    dest.setOrigSeqNum(source.getOrigSeqNum());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayLogoutMessage0 source, AmiRelayLogoutMessage0 dest) {
	        return OH.eq(dest.getOrigSeqNum(),source.getOrigSeqNum());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiRelayLogoutMessage0 valued) {
		    return valued.getOrigSeqNum();
	    }
    
	    @Override
	    public void setLong(AmiRelayLogoutMessage0 valued, long value) {
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
	    public void append(AmiRelayLogoutMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getOrigSeqNum());
	        
	    }
	    @Override
	    public void append(AmiRelayLogoutMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getOrigSeqNum());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long origSeqNum";
	    }
	    @Override
	    public void clear(AmiRelayLogoutMessage0 valued){
	       valued.setOrigSeqNum(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_origSeqNum=new VALUED_PARAM_CLASS_origSeqNum();
  

  
    private static final class VALUED_PARAM_CLASS_params extends AbstractValuedParam<AmiRelayLogoutMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 101;
	    }
	    
	    @Override
	    public void write(AmiRelayLogoutMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [B}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayLogoutMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 4;
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
	    public Object getValue(AmiRelayLogoutMessage0 valued) {
		    return (byte[])((AmiRelayLogoutMessage0)valued).getParams();
	    }
    
	    @Override
	    public void setValue(AmiRelayLogoutMessage0 valued, Object value) {
		    valued.setParams((byte[])value);
	    }
    
	    @Override
	    public void copy(AmiRelayLogoutMessage0 source, AmiRelayLogoutMessage0 dest) {
		    dest.setParams(source.getParams());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayLogoutMessage0 source, AmiRelayLogoutMessage0 dest) {
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
	    public void append(AmiRelayLogoutMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getParams());
	        
	    }
	    @Override
	    public void append(AmiRelayLogoutMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getParams());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte[] params";
	    }
	    @Override
	    public void clear(AmiRelayLogoutMessage0 valued){
	       valued.setParams(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_params=new VALUED_PARAM_CLASS_params();
  

  
    private static final class VALUED_PARAM_CLASS_transformState extends AbstractValuedParam<AmiRelayLogoutMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiRelayLogoutMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getTransformState());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayLogoutMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 5;
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
	    public Object getValue(AmiRelayLogoutMessage0 valued) {
		    return (byte)((AmiRelayLogoutMessage0)valued).getTransformState();
	    }
    
	    @Override
	    public void setValue(AmiRelayLogoutMessage0 valued, Object value) {
		    valued.setTransformState((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiRelayLogoutMessage0 source, AmiRelayLogoutMessage0 dest) {
		    dest.setTransformState(source.getTransformState());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayLogoutMessage0 source, AmiRelayLogoutMessage0 dest) {
	        return OH.eq(dest.getTransformState(),source.getTransformState());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiRelayLogoutMessage0 valued) {
		    return valued.getTransformState();
	    }
    
	    @Override
	    public void setByte(AmiRelayLogoutMessage0 valued, byte value) {
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
	    public void append(AmiRelayLogoutMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTransformState());
	        
	    }
	    @Override
	    public void append(AmiRelayLogoutMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTransformState());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte transformState";
	    }
	    @Override
	    public void clear(AmiRelayLogoutMessage0 valued){
	       valued.setTransformState((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_transformState=new VALUED_PARAM_CLASS_transformState();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_appIdStringKey, VALUED_PARAM_cleanLogout, VALUED_PARAM_connectionId, VALUED_PARAM_origSeqNum, VALUED_PARAM_params, VALUED_PARAM_transformState, };



    private static final byte PIDS[]={ 44 ,9,10,45,4,46};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 44: return VALUED_PARAM_appIdStringKey;
             case 9: return VALUED_PARAM_cleanLogout;
             case 10: return VALUED_PARAM_connectionId;
             case 45: return VALUED_PARAM_origSeqNum;
             case 4: return VALUED_PARAM_params;
             case 46: return VALUED_PARAM_transformState;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 44: return true;
             case 9: return true;
             case 10: return true;
             case 45: return true;
             case 4: return true;
             case 46: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 44: return "appIdStringKey";
             case 9: return "cleanLogout";
             case 10: return "connectionId";
             case 45: return "origSeqNum";
             case 4: return "params";
             case 46: return "transformState";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 44: return 0;
             case 9: return 1;
             case 10: return 2;
             case 45: return 3;
             case 4: return 4;
             case 46: return 5;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="appIdStringKey") return 44;
             if(name=="cleanLogout") return 9;
             if(name=="connectionId") return 10;
             if(name=="origSeqNum") return 45;
             if(name=="params") return 4;
             if(name=="transformState") return 46;
            
             if("appIdStringKey".equals(name)) return 44;
             if("cleanLogout".equals(name)) return 9;
             if("connectionId".equals(name)) return 10;
             if("origSeqNum".equals(name)) return 45;
             if("params".equals(name)) return 4;
             if("transformState".equals(name)) return 46;
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
             case 9: return  OH.valueOf(this._cleanLogout); 
             case 10: return  OH.valueOf(this._connectionId); 
             case 45: return  OH.valueOf(this._origSeqNum); 
             case 4: return  this._params; 
             case 46: return  OH.valueOf(this._transformState); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 44: return short.class;
             case 9: return boolean.class;
             case 10: return int.class;
             case 45: return long.class;
             case 4: return byte[].class;
             case 46: return byte.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 44: return 2;
             case 9: return 0;
             case 10: return 4;
             case 45: return 6;
             case 4: return 101;
             case 46: return 1;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 44: this._appIdStringKey=(java.lang.Short)value;return;
             case 9: this._cleanLogout=(java.lang.Boolean)value;return;
             case 10: this._connectionId=(java.lang.Integer)value;return;
             case 45: this._origSeqNum=(java.lang.Long)value;return;
             case 4: this._params=(byte[])value;return;
             case 46: this._transformState=(java.lang.Byte)value;return;
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
             case 9: this._cleanLogout=(java.lang.Boolean)value;return true;
             case 10: this._connectionId=(java.lang.Integer)value;return true;
             case 45: this._origSeqNum=(java.lang.Long)value;return true;
             case 4: this._params=(byte[])value;return true;
             case 46: this._transformState=(java.lang.Byte)value;return true;
            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
             case 9: return this._cleanLogout;
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
             case 9: this._cleanLogout=value;return;
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
        
        case 4:
        
            this._params=(byte[])converter.read(session);
        
            break;

        case 9:
        
            if((basicType=in.readByte())!=0)
                break;
            this._cleanLogout=in.readBoolean();
        
            break;

        case 10:
        
            if((basicType=in.readByte())!=4)
                break;
            this._connectionId=in.readInt();
        
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
        
if(this._params!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._params,session);
        
}

if(this._cleanLogout!=false && (0 & transience)==0){
    out.writeByte(9);
        
    out.writeByte(0);
    out.writeBoolean(this._cleanLogout);
        
}

if(this._connectionId!=0 && (0 & transience)==0){
    out.writeByte(10);
        
    out.writeByte(4);
    out.writeInt(this._connectionId);
        
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