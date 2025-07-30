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

public abstract class AmiRelayLoginMessage0 implements com.f1.ami.amicommon.msg.AmiRelayLoginMessage ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private java.lang.String _appId;

    private short _appIdStringKey;

    private int _connectionId;

    private java.lang.String _options;

    private long _origSeqNum;

    private byte[] _params;

    private java.lang.String _plugin;

    private byte _transformState;

    private static final String NAMES[]={ "appId" ,"appIdStringKey","connectionId","options","origSeqNum","params","plugin","transformState"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 16;
        try{
        switch(h){

                case 2:

                    if(name == "options" || name.equals("options")) {this._options=(java.lang.String)value;return;}
break;
                case 8:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {this._origSeqNum=(java.lang.Long)value;return;}
break;
                case 9:

                    if(name == "connectionId" || name.equals("connectionId")) {this._connectionId=(java.lang.Integer)value;return;}
break;
                case 10:

                    if(name == "params" || name.equals("params")) {this._params=(byte[])value;return;}
break;
                case 11:

                    if(name == "transformState" || name.equals("transformState")) {this._transformState=(java.lang.Byte)value;return;}
break;
                case 12:

                    if(name == "appId" || name.equals("appId")) {this._appId=(java.lang.String)value;return;}
break;
                case 13:

                    if(name == "plugin" || name.equals("plugin")) {this._plugin=(java.lang.String)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 16;
        switch(h){

                case 2:

                    if(name == "options" || name.equals("options")) {this._options=(java.lang.String)value;return true;}
break;
                case 8:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {this._origSeqNum=(java.lang.Long)value;return true;}
break;
                case 9:

                    if(name == "connectionId" || name.equals("connectionId")) {this._connectionId=(java.lang.Integer)value;return true;}
break;
                case 10:

                    if(name == "params" || name.equals("params")) {this._params=(byte[])value;return true;}
break;
                case 11:

                    if(name == "transformState" || name.equals("transformState")) {this._transformState=(java.lang.Byte)value;return true;}
break;
                case 12:

                    if(name == "appId" || name.equals("appId")) {this._appId=(java.lang.String)value;return true;}
break;
                case 13:

                    if(name == "plugin" || name.equals("plugin")) {this._plugin=(java.lang.String)value;return true;}
break;
                case 14:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {this._appIdStringKey=(java.lang.Short)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 16;
        switch(h){

                case 2:

		    
                    if(name == "options" || name.equals("options")) {return this._options;}
            
break;
                case 8:

		    
                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return OH.valueOf(this._origSeqNum);}
		    
break;
                case 9:

		    
                    if(name == "connectionId" || name.equals("connectionId")) {return OH.valueOf(this._connectionId);}
		    
break;
                case 10:

		    
                    if(name == "params" || name.equals("params")) {return this._params;}
            
break;
                case 11:

		    
                    if(name == "transformState" || name.equals("transformState")) {return OH.valueOf(this._transformState);}
		    
break;
                case 12:

		    
                    if(name == "appId" || name.equals("appId")) {return this._appId;}
            
break;
                case 13:

		    
                    if(name == "plugin" || name.equals("plugin")) {return this._plugin;}
            
break;
                case 14:

		    
                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return OH.valueOf(this._appIdStringKey);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 16;
        switch(h){

                case 2:

                    if(name == "options" || name.equals("options")) {return java.lang.String.class;}
break;
                case 8:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return long.class;}
break;
                case 9:

                    if(name == "connectionId" || name.equals("connectionId")) {return int.class;}
break;
                case 10:

                    if(name == "params" || name.equals("params")) {return byte[].class;}
break;
                case 11:

                    if(name == "transformState" || name.equals("transformState")) {return byte.class;}
break;
                case 12:

                    if(name == "appId" || name.equals("appId")) {return java.lang.String.class;}
break;
                case 13:

                    if(name == "plugin" || name.equals("plugin")) {return java.lang.String.class;}
break;
                case 14:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return short.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 16;
        switch(h){

                case 2:

                    if(name == "options" || name.equals("options")) {return VALUED_PARAM_options;}
break;
                case 8:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return VALUED_PARAM_origSeqNum;}
break;
                case 9:

                    if(name == "connectionId" || name.equals("connectionId")) {return VALUED_PARAM_connectionId;}
break;
                case 10:

                    if(name == "params" || name.equals("params")) {return VALUED_PARAM_params;}
break;
                case 11:

                    if(name == "transformState" || name.equals("transformState")) {return VALUED_PARAM_transformState;}
break;
                case 12:

                    if(name == "appId" || name.equals("appId")) {return VALUED_PARAM_appId;}
break;
                case 13:

                    if(name == "plugin" || name.equals("plugin")) {return VALUED_PARAM_plugin;}
break;
                case 14:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return VALUED_PARAM_appIdStringKey;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 16;
        switch(h){

                case 2:

                    if(name == "options" || name.equals("options")) {return 3;}
break;
                case 8:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return 4;}
break;
                case 9:

                    if(name == "connectionId" || name.equals("connectionId")) {return 2;}
break;
                case 10:

                    if(name == "params" || name.equals("params")) {return 5;}
break;
                case 11:

                    if(name == "transformState" || name.equals("transformState")) {return 7;}
break;
                case 12:

                    if(name == "appId" || name.equals("appId")) {return 0;}
break;
                case 13:

                    if(name == "plugin" || name.equals("plugin")) {return 6;}
break;
                case 14:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return 1;}
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
	    return (Class)AmiRelayLoginMessage0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 16;
        switch(h){

                case 2:

                    if(name == "options" || name.equals("options")) {return true;}
break;
                case 8:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return true;}
break;
                case 9:

                    if(name == "connectionId" || name.equals("connectionId")) {return true;}
break;
                case 10:

                    if(name == "params" || name.equals("params")) {return true;}
break;
                case 11:

                    if(name == "transformState" || name.equals("transformState")) {return true;}
break;
                case 12:

                    if(name == "appId" || name.equals("appId")) {return true;}
break;
                case 13:

                    if(name == "plugin" || name.equals("plugin")) {return true;}
break;
                case 14:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 16;
        switch(h){

                case 2:

                    if(name == "options" || name.equals("options")) {return 20;}
break;
                case 8:

                    if(name == "origSeqNum" || name.equals("origSeqNum")) {return 6;}
break;
                case 9:

                    if(name == "connectionId" || name.equals("connectionId")) {return 4;}
break;
                case 10:

                    if(name == "params" || name.equals("params")) {return 101;}
break;
                case 11:

                    if(name == "transformState" || name.equals("transformState")) {return 1;}
break;
                case 12:

                    if(name == "appId" || name.equals("appId")) {return 20;}
break;
                case 13:

                    if(name == "plugin" || name.equals("plugin")) {return 20;}
break;
                case 14:

                    if(name == "appIdStringKey" || name.equals("appIdStringKey")) {return 2;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _appId;

        case 1:return _appIdStringKey;

        case 2:return _connectionId;

        case 3:return _options;

        case 4:return _origSeqNum;

        case 5:return _params;

        case 6:return _plugin;

        case 7:return _transformState;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 8);
    }

    public java.lang.String getAppId(){
        return this._appId;
    }
    public void setAppId(java.lang.String _appId){
    
        this._appId=_appId;
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

    public java.lang.String getOptions(){
        return this._options;
    }
    public void setOptions(java.lang.String _options){
    
        this._options=_options;
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

    public java.lang.String getPlugin(){
        return this._plugin;
    }
    public void setPlugin(java.lang.String _plugin){
    
        this._plugin=_plugin;
    }

    public byte getTransformState(){
        return this._transformState;
    }
    public void setTransformState(byte _transformState){
    
        this._transformState=_transformState;
    }





  
    private static final class VALUED_PARAM_CLASS_appId extends AbstractValuedParam<AmiRelayLoginMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayLoginMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayLoginMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 29;
	    }
    
	    @Override
	    public String getName() {
            return "appId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayLoginMessage0 valued) {
		    return (java.lang.String)((AmiRelayLoginMessage0)valued).getAppId();
	    }
    
	    @Override
	    public void setValue(AmiRelayLoginMessage0 valued, Object value) {
		    valued.setAppId((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
		    dest.setAppId(source.getAppId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
	        return OH.eq(dest.getAppId(),source.getAppId());
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
	    public void append(AmiRelayLoginMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getAppId(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayLoginMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getAppId(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String appId";
	    }
	    @Override
	    public void clear(AmiRelayLoginMessage0 valued){
	       valued.setAppId(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_appId=new VALUED_PARAM_CLASS_appId();
  

  
    private static final class VALUED_PARAM_CLASS_appIdStringKey extends AbstractValuedParam<AmiRelayLoginMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 2;
	    }
	    
	    @Override
	    public void write(AmiRelayLoginMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeShort(valued.getAppIdStringKey());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayLoginMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 1;
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
	    public Object getValue(AmiRelayLoginMessage0 valued) {
		    return (short)((AmiRelayLoginMessage0)valued).getAppIdStringKey();
	    }
    
	    @Override
	    public void setValue(AmiRelayLoginMessage0 valued, Object value) {
		    valued.setAppIdStringKey((java.lang.Short)value);
	    }
    
	    @Override
	    public void copy(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
		    dest.setAppIdStringKey(source.getAppIdStringKey());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
	        return OH.eq(dest.getAppIdStringKey(),source.getAppIdStringKey());
	    }
	    
	    
	    
	    
	    
	    
	    @Override
	    public short getShort(AmiRelayLoginMessage0 valued) {
		    return valued.getAppIdStringKey();
	    }
    
	    @Override
	    public void setShort(AmiRelayLoginMessage0 valued, short value) {
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
	    public void append(AmiRelayLoginMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAppIdStringKey());
	        
	    }
	    @Override
	    public void append(AmiRelayLoginMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAppIdStringKey());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:short appIdStringKey";
	    }
	    @Override
	    public void clear(AmiRelayLoginMessage0 valued){
	       valued.setAppIdStringKey((short)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_appIdStringKey=new VALUED_PARAM_CLASS_appIdStringKey();
  

  
    private static final class VALUED_PARAM_CLASS_connectionId extends AbstractValuedParam<AmiRelayLoginMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiRelayLoginMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getConnectionId());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayLoginMessage0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiRelayLoginMessage0 valued) {
		    return (int)((AmiRelayLoginMessage0)valued).getConnectionId();
	    }
    
	    @Override
	    public void setValue(AmiRelayLoginMessage0 valued, Object value) {
		    valued.setConnectionId((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
		    dest.setConnectionId(source.getConnectionId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
	        return OH.eq(dest.getConnectionId(),source.getConnectionId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiRelayLoginMessage0 valued) {
		    return valued.getConnectionId();
	    }
    
	    @Override
	    public void setInt(AmiRelayLoginMessage0 valued, int value) {
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
	    public void append(AmiRelayLoginMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getConnectionId());
	        
	    }
	    @Override
	    public void append(AmiRelayLoginMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getConnectionId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int connectionId";
	    }
	    @Override
	    public void clear(AmiRelayLoginMessage0 valued){
	       valued.setConnectionId(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_connectionId=new VALUED_PARAM_CLASS_connectionId();
  

  
    private static final class VALUED_PARAM_CLASS_options extends AbstractValuedParam<AmiRelayLoginMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayLoginMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayLoginMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 30;
	    }
    
	    @Override
	    public String getName() {
            return "options";
	    }
    
	    @Override
	    public Object getValue(AmiRelayLoginMessage0 valued) {
		    return (java.lang.String)((AmiRelayLoginMessage0)valued).getOptions();
	    }
    
	    @Override
	    public void setValue(AmiRelayLoginMessage0 valued, Object value) {
		    valued.setOptions((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
		    dest.setOptions(source.getOptions());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
	        return OH.eq(dest.getOptions(),source.getOptions());
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
	    public void append(AmiRelayLoginMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getOptions(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayLoginMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getOptions(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String options";
	    }
	    @Override
	    public void clear(AmiRelayLoginMessage0 valued){
	       valued.setOptions(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_options=new VALUED_PARAM_CLASS_options();
  

  
    private static final class VALUED_PARAM_CLASS_origSeqNum extends AbstractValuedParam<AmiRelayLoginMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiRelayLoginMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getOrigSeqNum());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayLoginMessage0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiRelayLoginMessage0 valued) {
		    return (long)((AmiRelayLoginMessage0)valued).getOrigSeqNum();
	    }
    
	    @Override
	    public void setValue(AmiRelayLoginMessage0 valued, Object value) {
		    valued.setOrigSeqNum((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
		    dest.setOrigSeqNum(source.getOrigSeqNum());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
	        return OH.eq(dest.getOrigSeqNum(),source.getOrigSeqNum());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiRelayLoginMessage0 valued) {
		    return valued.getOrigSeqNum();
	    }
    
	    @Override
	    public void setLong(AmiRelayLoginMessage0 valued, long value) {
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
	    public void append(AmiRelayLoginMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getOrigSeqNum());
	        
	    }
	    @Override
	    public void append(AmiRelayLoginMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getOrigSeqNum());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long origSeqNum";
	    }
	    @Override
	    public void clear(AmiRelayLoginMessage0 valued){
	       valued.setOrigSeqNum(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_origSeqNum=new VALUED_PARAM_CLASS_origSeqNum();
  

  
    private static final class VALUED_PARAM_CLASS_params extends AbstractValuedParam<AmiRelayLoginMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 101;
	    }
	    
	    @Override
	    public void write(AmiRelayLoginMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: [B}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayLoginMessage0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiRelayLoginMessage0 valued) {
		    return (byte[])((AmiRelayLoginMessage0)valued).getParams();
	    }
    
	    @Override
	    public void setValue(AmiRelayLoginMessage0 valued, Object value) {
		    valued.setParams((byte[])value);
	    }
    
	    @Override
	    public void copy(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
		    dest.setParams(source.getParams());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
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
	    public void append(AmiRelayLoginMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getParams());
	        
	    }
	    @Override
	    public void append(AmiRelayLoginMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getParams());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte[] params";
	    }
	    @Override
	    public void clear(AmiRelayLoginMessage0 valued){
	       valued.setParams(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_params=new VALUED_PARAM_CLASS_params();
  

  
    private static final class VALUED_PARAM_CLASS_plugin extends AbstractValuedParam<AmiRelayLoginMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayLoginMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayLoginMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 31;
	    }
    
	    @Override
	    public String getName() {
            return "plugin";
	    }
    
	    @Override
	    public Object getValue(AmiRelayLoginMessage0 valued) {
		    return (java.lang.String)((AmiRelayLoginMessage0)valued).getPlugin();
	    }
    
	    @Override
	    public void setValue(AmiRelayLoginMessage0 valued, Object value) {
		    valued.setPlugin((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
		    dest.setPlugin(source.getPlugin());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
	        return OH.eq(dest.getPlugin(),source.getPlugin());
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
	    public void append(AmiRelayLoginMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getPlugin(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayLoginMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getPlugin(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String plugin";
	    }
	    @Override
	    public void clear(AmiRelayLoginMessage0 valued){
	       valued.setPlugin(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_plugin=new VALUED_PARAM_CLASS_plugin();
  

  
    private static final class VALUED_PARAM_CLASS_transformState extends AbstractValuedParam<AmiRelayLoginMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiRelayLoginMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getTransformState());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayLoginMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 7;
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
	    public Object getValue(AmiRelayLoginMessage0 valued) {
		    return (byte)((AmiRelayLoginMessage0)valued).getTransformState();
	    }
    
	    @Override
	    public void setValue(AmiRelayLoginMessage0 valued, Object value) {
		    valued.setTransformState((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
		    dest.setTransformState(source.getTransformState());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayLoginMessage0 source, AmiRelayLoginMessage0 dest) {
	        return OH.eq(dest.getTransformState(),source.getTransformState());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiRelayLoginMessage0 valued) {
		    return valued.getTransformState();
	    }
    
	    @Override
	    public void setByte(AmiRelayLoginMessage0 valued, byte value) {
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
	    public void append(AmiRelayLoginMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTransformState());
	        
	    }
	    @Override
	    public void append(AmiRelayLoginMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTransformState());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte transformState";
	    }
	    @Override
	    public void clear(AmiRelayLoginMessage0 valued){
	       valued.setTransformState((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_transformState=new VALUED_PARAM_CLASS_transformState();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_appId, VALUED_PARAM_appIdStringKey, VALUED_PARAM_connectionId, VALUED_PARAM_options, VALUED_PARAM_origSeqNum, VALUED_PARAM_params, VALUED_PARAM_plugin, VALUED_PARAM_transformState, };



    private static final byte PIDS[]={ 29 ,44,10,30,45,4,31,46};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 29: return VALUED_PARAM_appId;
             case 44: return VALUED_PARAM_appIdStringKey;
             case 10: return VALUED_PARAM_connectionId;
             case 30: return VALUED_PARAM_options;
             case 45: return VALUED_PARAM_origSeqNum;
             case 4: return VALUED_PARAM_params;
             case 31: return VALUED_PARAM_plugin;
             case 46: return VALUED_PARAM_transformState;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 29: return true;
             case 44: return true;
             case 10: return true;
             case 30: return true;
             case 45: return true;
             case 4: return true;
             case 31: return true;
             case 46: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 29: return "appId";
             case 44: return "appIdStringKey";
             case 10: return "connectionId";
             case 30: return "options";
             case 45: return "origSeqNum";
             case 4: return "params";
             case 31: return "plugin";
             case 46: return "transformState";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 29: return 0;
             case 44: return 1;
             case 10: return 2;
             case 30: return 3;
             case 45: return 4;
             case 4: return 5;
             case 31: return 6;
             case 46: return 7;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="appId") return 29;
             if(name=="appIdStringKey") return 44;
             if(name=="connectionId") return 10;
             if(name=="options") return 30;
             if(name=="origSeqNum") return 45;
             if(name=="params") return 4;
             if(name=="plugin") return 31;
             if(name=="transformState") return 46;
            
             if("appId".equals(name)) return 29;
             if("appIdStringKey".equals(name)) return 44;
             if("connectionId".equals(name)) return 10;
             if("options".equals(name)) return 30;
             if("origSeqNum".equals(name)) return 45;
             if("params".equals(name)) return 4;
             if("plugin".equals(name)) return 31;
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
             case 29: return  this._appId; 
             case 44: return  OH.valueOf(this._appIdStringKey); 
             case 10: return  OH.valueOf(this._connectionId); 
             case 30: return  this._options; 
             case 45: return  OH.valueOf(this._origSeqNum); 
             case 4: return  this._params; 
             case 31: return  this._plugin; 
             case 46: return  OH.valueOf(this._transformState); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 29: return java.lang.String.class;
             case 44: return short.class;
             case 10: return int.class;
             case 30: return java.lang.String.class;
             case 45: return long.class;
             case 4: return byte[].class;
             case 31: return java.lang.String.class;
             case 46: return byte.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 29: return 20;
             case 44: return 2;
             case 10: return 4;
             case 30: return 20;
             case 45: return 6;
             case 4: return 101;
             case 31: return 20;
             case 46: return 1;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 29: this._appId=(java.lang.String)value;return;
             case 44: this._appIdStringKey=(java.lang.Short)value;return;
             case 10: this._connectionId=(java.lang.Integer)value;return;
             case 30: this._options=(java.lang.String)value;return;
             case 45: this._origSeqNum=(java.lang.Long)value;return;
             case 4: this._params=(byte[])value;return;
             case 31: this._plugin=(java.lang.String)value;return;
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
             case 29: this._appId=(java.lang.String)value;return true;
             case 44: this._appIdStringKey=(java.lang.Short)value;return true;
             case 10: this._connectionId=(java.lang.Integer)value;return true;
             case 30: this._options=(java.lang.String)value;return true;
             case 45: this._origSeqNum=(java.lang.Long)value;return true;
             case 4: this._params=(byte[])value;return true;
             case 31: this._plugin=(java.lang.String)value;return true;
             case 46: this._transformState=(java.lang.Byte)value;return true;
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

        case 10:
        
            if((basicType=in.readByte())!=4)
                break;
            this._connectionId=in.readInt();
        
            break;

        case 29:
        
            this._appId=(java.lang.String)converter.read(session);
        
            break;

        case 30:
        
            this._options=(java.lang.String)converter.read(session);
        
            break;

        case 31:
        
            this._plugin=(java.lang.String)converter.read(session);
        
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

if(this._connectionId!=0 && (0 & transience)==0){
    out.writeByte(10);
        
    out.writeByte(4);
    out.writeInt(this._connectionId);
        
}

if(this._appId!=null && (0 & transience)==0){
    out.writeByte(29);
        
    converter.write(this._appId,session);
        
}

if(this._options!=null && (0 & transience)==0){
    out.writeByte(30);
        
    converter.write(this._options,session);
        
}

if(this._plugin!=null && (0 & transience)==0){
    out.writeByte(31);
        
    converter.write(this._plugin,session);
        
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