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

public abstract class MsgStatusMessage0 implements com.f1.povo.msg.MsgStatusMessage ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private boolean _isConnected;

    private boolean _isWrite;

    private java.lang.String _remoteHost;

    private int _remotePort;

    private java.lang.String _remoteProcessUid;

    private java.lang.String _source;

    private java.lang.String _suffix;

    private java.lang.String _topic;

    private static final String NAMES[]={ "isConnected" ,"isWrite","remoteHost","remotePort","remoteProcessUid","source","suffix","topic"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 23;
        try{
        switch(h){

                case 2:

                    if(name == "isWrite" || name.equals("isWrite")) {this._isWrite=(java.lang.Boolean)value;return;}
break;
                case 4:

                    if(name == "suffix" || name.equals("suffix")) {this._suffix=(java.lang.String)value;return;}
break;
                case 7:

                    if(name == "source" || name.equals("source")) {this._source=(java.lang.String)value;return;}
break;
                case 10:

                    if(name == "remotePort" || name.equals("remotePort")) {this._remotePort=(java.lang.Integer)value;return;}
break;
                case 12:

                    if(name == "topic" || name.equals("topic")) {this._topic=(java.lang.String)value;return;}
break;
                case 13:

                    if(name == "isConnected" || name.equals("isConnected")) {this._isConnected=(java.lang.Boolean)value;return;}
break;
                case 14:

                    if(name == "remoteProcessUid" || name.equals("remoteProcessUid")) {this._remoteProcessUid=(java.lang.String)value;return;}
break;
                case 16:

                    if(name == "remoteHost" || name.equals("remoteHost")) {this._remoteHost=(java.lang.String)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 23;
        switch(h){

                case 2:

                    if(name == "isWrite" || name.equals("isWrite")) {this._isWrite=(java.lang.Boolean)value;return true;}
break;
                case 4:

                    if(name == "suffix" || name.equals("suffix")) {this._suffix=(java.lang.String)value;return true;}
break;
                case 7:

                    if(name == "source" || name.equals("source")) {this._source=(java.lang.String)value;return true;}
break;
                case 10:

                    if(name == "remotePort" || name.equals("remotePort")) {this._remotePort=(java.lang.Integer)value;return true;}
break;
                case 12:

                    if(name == "topic" || name.equals("topic")) {this._topic=(java.lang.String)value;return true;}
break;
                case 13:

                    if(name == "isConnected" || name.equals("isConnected")) {this._isConnected=(java.lang.Boolean)value;return true;}
break;
                case 14:

                    if(name == "remoteProcessUid" || name.equals("remoteProcessUid")) {this._remoteProcessUid=(java.lang.String)value;return true;}
break;
                case 16:

                    if(name == "remoteHost" || name.equals("remoteHost")) {this._remoteHost=(java.lang.String)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 23;
        switch(h){

                case 2:

		    
                    if(name == "isWrite" || name.equals("isWrite")) {return OH.valueOf(this._isWrite);}
		    
break;
                case 4:

		    
                    if(name == "suffix" || name.equals("suffix")) {return this._suffix;}
            
break;
                case 7:

		    
                    if(name == "source" || name.equals("source")) {return this._source;}
            
break;
                case 10:

		    
                    if(name == "remotePort" || name.equals("remotePort")) {return OH.valueOf(this._remotePort);}
		    
break;
                case 12:

		    
                    if(name == "topic" || name.equals("topic")) {return this._topic;}
            
break;
                case 13:

		    
                    if(name == "isConnected" || name.equals("isConnected")) {return OH.valueOf(this._isConnected);}
		    
break;
                case 14:

		    
                    if(name == "remoteProcessUid" || name.equals("remoteProcessUid")) {return this._remoteProcessUid;}
            
break;
                case 16:

		    
                    if(name == "remoteHost" || name.equals("remoteHost")) {return this._remoteHost;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 23;
        switch(h){

                case 2:

                    if(name == "isWrite" || name.equals("isWrite")) {return boolean.class;}
break;
                case 4:

                    if(name == "suffix" || name.equals("suffix")) {return java.lang.String.class;}
break;
                case 7:

                    if(name == "source" || name.equals("source")) {return java.lang.String.class;}
break;
                case 10:

                    if(name == "remotePort" || name.equals("remotePort")) {return int.class;}
break;
                case 12:

                    if(name == "topic" || name.equals("topic")) {return java.lang.String.class;}
break;
                case 13:

                    if(name == "isConnected" || name.equals("isConnected")) {return boolean.class;}
break;
                case 14:

                    if(name == "remoteProcessUid" || name.equals("remoteProcessUid")) {return java.lang.String.class;}
break;
                case 16:

                    if(name == "remoteHost" || name.equals("remoteHost")) {return java.lang.String.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 23;
        switch(h){

                case 2:

                    if(name == "isWrite" || name.equals("isWrite")) {return VALUED_PARAM_isWrite;}
break;
                case 4:

                    if(name == "suffix" || name.equals("suffix")) {return VALUED_PARAM_suffix;}
break;
                case 7:

                    if(name == "source" || name.equals("source")) {return VALUED_PARAM_source;}
break;
                case 10:

                    if(name == "remotePort" || name.equals("remotePort")) {return VALUED_PARAM_remotePort;}
break;
                case 12:

                    if(name == "topic" || name.equals("topic")) {return VALUED_PARAM_topic;}
break;
                case 13:

                    if(name == "isConnected" || name.equals("isConnected")) {return VALUED_PARAM_isConnected;}
break;
                case 14:

                    if(name == "remoteProcessUid" || name.equals("remoteProcessUid")) {return VALUED_PARAM_remoteProcessUid;}
break;
                case 16:

                    if(name == "remoteHost" || name.equals("remoteHost")) {return VALUED_PARAM_remoteHost;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 23;
        switch(h){

                case 2:

                    if(name == "isWrite" || name.equals("isWrite")) {return 1;}
break;
                case 4:

                    if(name == "suffix" || name.equals("suffix")) {return 6;}
break;
                case 7:

                    if(name == "source" || name.equals("source")) {return 5;}
break;
                case 10:

                    if(name == "remotePort" || name.equals("remotePort")) {return 3;}
break;
                case 12:

                    if(name == "topic" || name.equals("topic")) {return 7;}
break;
                case 13:

                    if(name == "isConnected" || name.equals("isConnected")) {return 0;}
break;
                case 14:

                    if(name == "remoteProcessUid" || name.equals("remoteProcessUid")) {return 4;}
break;
                case 16:

                    if(name == "remoteHost" || name.equals("remoteHost")) {return 2;}
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
	    return (Class)MsgStatusMessage0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 23;
        switch(h){

                case 2:

                    if(name == "isWrite" || name.equals("isWrite")) {return true;}
break;
                case 4:

                    if(name == "suffix" || name.equals("suffix")) {return true;}
break;
                case 7:

                    if(name == "source" || name.equals("source")) {return true;}
break;
                case 10:

                    if(name == "remotePort" || name.equals("remotePort")) {return true;}
break;
                case 12:

                    if(name == "topic" || name.equals("topic")) {return true;}
break;
                case 13:

                    if(name == "isConnected" || name.equals("isConnected")) {return true;}
break;
                case 14:

                    if(name == "remoteProcessUid" || name.equals("remoteProcessUid")) {return true;}
break;
                case 16:

                    if(name == "remoteHost" || name.equals("remoteHost")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 23;
        switch(h){

                case 2:

                    if(name == "isWrite" || name.equals("isWrite")) {return 0;}
break;
                case 4:

                    if(name == "suffix" || name.equals("suffix")) {return 20;}
break;
                case 7:

                    if(name == "source" || name.equals("source")) {return 20;}
break;
                case 10:

                    if(name == "remotePort" || name.equals("remotePort")) {return 4;}
break;
                case 12:

                    if(name == "topic" || name.equals("topic")) {return 20;}
break;
                case 13:

                    if(name == "isConnected" || name.equals("isConnected")) {return 0;}
break;
                case 14:

                    if(name == "remoteProcessUid" || name.equals("remoteProcessUid")) {return 20;}
break;
                case 16:

                    if(name == "remoteHost" || name.equals("remoteHost")) {return 20;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _isConnected;

        case 1:return _isWrite;

        case 2:return _remoteHost;

        case 3:return _remotePort;

        case 4:return _remoteProcessUid;

        case 5:return _source;

        case 6:return _suffix;

        case 7:return _topic;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 8);
    }

    public boolean getIsConnected(){
        return this._isConnected;
    }
    public void setIsConnected(boolean _isConnected){
    
        this._isConnected=_isConnected;
    }

    public boolean getIsWrite(){
        return this._isWrite;
    }
    public void setIsWrite(boolean _isWrite){
    
        this._isWrite=_isWrite;
    }

    public java.lang.String getRemoteHost(){
        return this._remoteHost;
    }
    public void setRemoteHost(java.lang.String _remoteHost){
    
        this._remoteHost=_remoteHost;
    }

    public int getRemotePort(){
        return this._remotePort;
    }
    public void setRemotePort(int _remotePort){
    
        this._remotePort=_remotePort;
    }

    public java.lang.String getRemoteProcessUid(){
        return this._remoteProcessUid;
    }
    public void setRemoteProcessUid(java.lang.String _remoteProcessUid){
    
        this._remoteProcessUid=_remoteProcessUid;
    }

    public java.lang.String getSource(){
        return this._source;
    }
    public void setSource(java.lang.String _source){
    
        this._source=_source;
    }

    public java.lang.String getSuffix(){
        return this._suffix;
    }
    public void setSuffix(java.lang.String _suffix){
    
        this._suffix=_suffix;
    }

    public java.lang.String getTopic(){
        return this._topic;
    }
    public void setTopic(java.lang.String _topic){
    
        this._topic=_topic;
    }





  
    private static final class VALUED_PARAM_CLASS_isConnected extends AbstractValuedParam<MsgStatusMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(MsgStatusMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getIsConnected());
		    
	    }
	    
	    @Override
	    public void read(MsgStatusMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setIsConnected(stream.readBoolean());
		    
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
            return "isConnected";
	    }
    
	    @Override
	    public Object getValue(MsgStatusMessage0 valued) {
		    return (boolean)((MsgStatusMessage0)valued).getIsConnected();
	    }
    
	    @Override
	    public void setValue(MsgStatusMessage0 valued, Object value) {
		    valued.setIsConnected((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
		    dest.setIsConnected(source.getIsConnected());
	    }
	    
	    @Override
	    public boolean areEqual(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
	        return OH.eq(dest.getIsConnected(),source.getIsConnected());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(MsgStatusMessage0 valued) {
		    return valued.getIsConnected();
	    }
    
	    @Override
	    public void setBoolean(MsgStatusMessage0 valued, boolean value) {
		    valued.setIsConnected(value);
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
	    public void append(MsgStatusMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getIsConnected());
	        
	    }
	    @Override
	    public void append(MsgStatusMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getIsConnected());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean isConnected";
	    }
	    @Override
	    public void clear(MsgStatusMessage0 valued){
	       valued.setIsConnected(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_isConnected=new VALUED_PARAM_CLASS_isConnected();
  

  
    private static final class VALUED_PARAM_CLASS_isWrite extends AbstractValuedParam<MsgStatusMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(MsgStatusMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getIsWrite());
		    
	    }
	    
	    @Override
	    public void read(MsgStatusMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setIsWrite(stream.readBoolean());
		    
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
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "isWrite";
	    }
    
	    @Override
	    public Object getValue(MsgStatusMessage0 valued) {
		    return (boolean)((MsgStatusMessage0)valued).getIsWrite();
	    }
    
	    @Override
	    public void setValue(MsgStatusMessage0 valued, Object value) {
		    valued.setIsWrite((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
		    dest.setIsWrite(source.getIsWrite());
	    }
	    
	    @Override
	    public boolean areEqual(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
	        return OH.eq(dest.getIsWrite(),source.getIsWrite());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(MsgStatusMessage0 valued) {
		    return valued.getIsWrite();
	    }
    
	    @Override
	    public void setBoolean(MsgStatusMessage0 valued, boolean value) {
		    valued.setIsWrite(value);
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
	    public void append(MsgStatusMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getIsWrite());
	        
	    }
	    @Override
	    public void append(MsgStatusMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getIsWrite());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean isWrite";
	    }
	    @Override
	    public void clear(MsgStatusMessage0 valued){
	       valued.setIsWrite(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_isWrite=new VALUED_PARAM_CLASS_isWrite();
  

  
    private static final class VALUED_PARAM_CLASS_remoteHost extends AbstractValuedParam<MsgStatusMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(MsgStatusMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(MsgStatusMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "remoteHost";
	    }
    
	    @Override
	    public Object getValue(MsgStatusMessage0 valued) {
		    return (java.lang.String)((MsgStatusMessage0)valued).getRemoteHost();
	    }
    
	    @Override
	    public void setValue(MsgStatusMessage0 valued, Object value) {
		    valued.setRemoteHost((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
		    dest.setRemoteHost(source.getRemoteHost());
	    }
	    
	    @Override
	    public boolean areEqual(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
	        return OH.eq(dest.getRemoteHost(),source.getRemoteHost());
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
	    public void append(MsgStatusMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getRemoteHost(),sb);
	        
	    }
	    @Override
	    public void append(MsgStatusMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getRemoteHost(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String remoteHost";
	    }
	    @Override
	    public void clear(MsgStatusMessage0 valued){
	       valued.setRemoteHost(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_remoteHost=new VALUED_PARAM_CLASS_remoteHost();
  

  
    private static final class VALUED_PARAM_CLASS_remotePort extends AbstractValuedParam<MsgStatusMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(MsgStatusMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getRemotePort());
		    
	    }
	    
	    @Override
	    public void read(MsgStatusMessage0 valued, DataInput stream) throws IOException{
		    
		      valued.setRemotePort(stream.readInt());
		    
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
            return "remotePort";
	    }
    
	    @Override
	    public Object getValue(MsgStatusMessage0 valued) {
		    return (int)((MsgStatusMessage0)valued).getRemotePort();
	    }
    
	    @Override
	    public void setValue(MsgStatusMessage0 valued, Object value) {
		    valued.setRemotePort((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
		    dest.setRemotePort(source.getRemotePort());
	    }
	    
	    @Override
	    public boolean areEqual(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
	        return OH.eq(dest.getRemotePort(),source.getRemotePort());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(MsgStatusMessage0 valued) {
		    return valued.getRemotePort();
	    }
    
	    @Override
	    public void setInt(MsgStatusMessage0 valued, int value) {
		    valued.setRemotePort(value);
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
	    public void append(MsgStatusMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getRemotePort());
	        
	    }
	    @Override
	    public void append(MsgStatusMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getRemotePort());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int remotePort";
	    }
	    @Override
	    public void clear(MsgStatusMessage0 valued){
	       valued.setRemotePort(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_remotePort=new VALUED_PARAM_CLASS_remotePort();
  

  
    private static final class VALUED_PARAM_CLASS_remoteProcessUid extends AbstractValuedParam<MsgStatusMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(MsgStatusMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(MsgStatusMessage0 valued, DataInput stream) throws IOException{
		    
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
            return "remoteProcessUid";
	    }
    
	    @Override
	    public Object getValue(MsgStatusMessage0 valued) {
		    return (java.lang.String)((MsgStatusMessage0)valued).getRemoteProcessUid();
	    }
    
	    @Override
	    public void setValue(MsgStatusMessage0 valued, Object value) {
		    valued.setRemoteProcessUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
		    dest.setRemoteProcessUid(source.getRemoteProcessUid());
	    }
	    
	    @Override
	    public boolean areEqual(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
	        return OH.eq(dest.getRemoteProcessUid(),source.getRemoteProcessUid());
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
	    public void append(MsgStatusMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getRemoteProcessUid(),sb);
	        
	    }
	    @Override
	    public void append(MsgStatusMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getRemoteProcessUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String remoteProcessUid";
	    }
	    @Override
	    public void clear(MsgStatusMessage0 valued){
	       valued.setRemoteProcessUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_remoteProcessUid=new VALUED_PARAM_CLASS_remoteProcessUid();
  

  
    private static final class VALUED_PARAM_CLASS_source extends AbstractValuedParam<MsgStatusMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(MsgStatusMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(MsgStatusMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "source";
	    }
    
	    @Override
	    public Object getValue(MsgStatusMessage0 valued) {
		    return (java.lang.String)((MsgStatusMessage0)valued).getSource();
	    }
    
	    @Override
	    public void setValue(MsgStatusMessage0 valued, Object value) {
		    valued.setSource((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
		    dest.setSource(source.getSource());
	    }
	    
	    @Override
	    public boolean areEqual(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
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
	    public void append(MsgStatusMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getSource(),sb);
	        
	    }
	    @Override
	    public void append(MsgStatusMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getSource(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String source";
	    }
	    @Override
	    public void clear(MsgStatusMessage0 valued){
	       valued.setSource(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_source=new VALUED_PARAM_CLASS_source();
  

  
    private static final class VALUED_PARAM_CLASS_suffix extends AbstractValuedParam<MsgStatusMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(MsgStatusMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(MsgStatusMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "suffix";
	    }
    
	    @Override
	    public Object getValue(MsgStatusMessage0 valued) {
		    return (java.lang.String)((MsgStatusMessage0)valued).getSuffix();
	    }
    
	    @Override
	    public void setValue(MsgStatusMessage0 valued, Object value) {
		    valued.setSuffix((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
		    dest.setSuffix(source.getSuffix());
	    }
	    
	    @Override
	    public boolean areEqual(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
	        return OH.eq(dest.getSuffix(),source.getSuffix());
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
	    public void append(MsgStatusMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getSuffix(),sb);
	        
	    }
	    @Override
	    public void append(MsgStatusMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getSuffix(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String suffix";
	    }
	    @Override
	    public void clear(MsgStatusMessage0 valued){
	       valued.setSuffix(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_suffix=new VALUED_PARAM_CLASS_suffix();
  

  
    private static final class VALUED_PARAM_CLASS_topic extends AbstractValuedParam<MsgStatusMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(MsgStatusMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(MsgStatusMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "topic";
	    }
    
	    @Override
	    public Object getValue(MsgStatusMessage0 valued) {
		    return (java.lang.String)((MsgStatusMessage0)valued).getTopic();
	    }
    
	    @Override
	    public void setValue(MsgStatusMessage0 valued, Object value) {
		    valued.setTopic((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
		    dest.setTopic(source.getTopic());
	    }
	    
	    @Override
	    public boolean areEqual(MsgStatusMessage0 source, MsgStatusMessage0 dest) {
	        return OH.eq(dest.getTopic(),source.getTopic());
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
	    public void append(MsgStatusMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getTopic(),sb);
	        
	    }
	    @Override
	    public void append(MsgStatusMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getTopic(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String topic";
	    }
	    @Override
	    public void clear(MsgStatusMessage0 valued){
	       valued.setTopic(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_topic=new VALUED_PARAM_CLASS_topic();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_isConnected, VALUED_PARAM_isWrite, VALUED_PARAM_remoteHost, VALUED_PARAM_remotePort, VALUED_PARAM_remoteProcessUid, VALUED_PARAM_source, VALUED_PARAM_suffix, VALUED_PARAM_topic, };



    private static final byte PIDS[]={ 4 ,3,5,6,8,7,2,1};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 4: return VALUED_PARAM_isConnected;
             case 3: return VALUED_PARAM_isWrite;
             case 5: return VALUED_PARAM_remoteHost;
             case 6: return VALUED_PARAM_remotePort;
             case 8: return VALUED_PARAM_remoteProcessUid;
             case 7: return VALUED_PARAM_source;
             case 2: return VALUED_PARAM_suffix;
             case 1: return VALUED_PARAM_topic;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 4: return true;
             case 3: return true;
             case 5: return true;
             case 6: return true;
             case 8: return true;
             case 7: return true;
             case 2: return true;
             case 1: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 4: return "isConnected";
             case 3: return "isWrite";
             case 5: return "remoteHost";
             case 6: return "remotePort";
             case 8: return "remoteProcessUid";
             case 7: return "source";
             case 2: return "suffix";
             case 1: return "topic";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 4: return 0;
             case 3: return 1;
             case 5: return 2;
             case 6: return 3;
             case 8: return 4;
             case 7: return 5;
             case 2: return 6;
             case 1: return 7;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="isConnected") return 4;
             if(name=="isWrite") return 3;
             if(name=="remoteHost") return 5;
             if(name=="remotePort") return 6;
             if(name=="remoteProcessUid") return 8;
             if(name=="source") return 7;
             if(name=="suffix") return 2;
             if(name=="topic") return 1;
            
             if("isConnected".equals(name)) return 4;
             if("isWrite".equals(name)) return 3;
             if("remoteHost".equals(name)) return 5;
             if("remotePort".equals(name)) return 6;
             if("remoteProcessUid".equals(name)) return 8;
             if("source".equals(name)) return 7;
             if("suffix".equals(name)) return 2;
             if("topic".equals(name)) return 1;
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
             case 4: return  OH.valueOf(this._isConnected); 
             case 3: return  OH.valueOf(this._isWrite); 
             case 5: return  this._remoteHost; 
             case 6: return  OH.valueOf(this._remotePort); 
             case 8: return  this._remoteProcessUid; 
             case 7: return  this._source; 
             case 2: return  this._suffix; 
             case 1: return  this._topic; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 4: return boolean.class;
             case 3: return boolean.class;
             case 5: return java.lang.String.class;
             case 6: return int.class;
             case 8: return java.lang.String.class;
             case 7: return java.lang.String.class;
             case 2: return java.lang.String.class;
             case 1: return java.lang.String.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 4: return 0;
             case 3: return 0;
             case 5: return 20;
             case 6: return 4;
             case 8: return 20;
             case 7: return 20;
             case 2: return 20;
             case 1: return 20;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 4: this._isConnected=(java.lang.Boolean)value;return;
             case 3: this._isWrite=(java.lang.Boolean)value;return;
             case 5: this._remoteHost=(java.lang.String)value;return;
             case 6: this._remotePort=(java.lang.Integer)value;return;
             case 8: this._remoteProcessUid=(java.lang.String)value;return;
             case 7: this._source=(java.lang.String)value;return;
             case 2: this._suffix=(java.lang.String)value;return;
             case 1: this._topic=(java.lang.String)value;return;
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
             case 4: this._isConnected=(java.lang.Boolean)value;return true;
             case 3: this._isWrite=(java.lang.Boolean)value;return true;
             case 5: this._remoteHost=(java.lang.String)value;return true;
             case 6: this._remotePort=(java.lang.Integer)value;return true;
             case 8: this._remoteProcessUid=(java.lang.String)value;return true;
             case 7: this._source=(java.lang.String)value;return true;
             case 2: this._suffix=(java.lang.String)value;return true;
             case 1: this._topic=(java.lang.String)value;return true;
            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
             case 4: return this._isConnected;
             case 3: return this._isWrite;
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
             case 6: return this._remotePort;
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
             case 4: this._isConnected=value;return;
             case 3: this._isWrite=value;return;
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
             case 6: this._remotePort=value;return;
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
        
            this._topic=(java.lang.String)converter.read(session);
        
            break;

        case 2:
        
            this._suffix=(java.lang.String)converter.read(session);
        
            break;

        case 3:
        
            if((basicType=in.readByte())!=0)
                break;
            this._isWrite=in.readBoolean();
        
            break;

        case 4:
        
            if((basicType=in.readByte())!=0)
                break;
            this._isConnected=in.readBoolean();
        
            break;

        case 5:
        
            this._remoteHost=(java.lang.String)converter.read(session);
        
            break;

        case 6:
        
            if((basicType=in.readByte())!=4)
                break;
            this._remotePort=in.readInt();
        
            break;

        case 7:
        
            this._source=(java.lang.String)converter.read(session);
        
            break;

        case 8:
        
            this._remoteProcessUid=(java.lang.String)converter.read(session);
        
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
        
if(this._topic!=null && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this._topic,session);
        
}

if(this._suffix!=null && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this._suffix,session);
        
}

if(this._isWrite!=false && (0 & transience)==0){
    out.writeByte(3);
        
    out.writeByte(0);
    out.writeBoolean(this._isWrite);
        
}

if(this._isConnected!=false && (0 & transience)==0){
    out.writeByte(4);
        
    out.writeByte(0);
    out.writeBoolean(this._isConnected);
        
}

if(this._remoteHost!=null && (0 & transience)==0){
    out.writeByte(5);
        
    converter.write(this._remoteHost,session);
        
}

if(this._remotePort!=0 && (0 & transience)==0){
    out.writeByte(6);
        
    out.writeByte(4);
    out.writeInt(this._remotePort);
        
}

if(this._source!=null && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this._source,session);
        
}

if(this._remoteProcessUid!=null && (0 & transience)==0){
    out.writeByte(8);
        
    converter.write(this._remoteProcessUid,session);
        
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