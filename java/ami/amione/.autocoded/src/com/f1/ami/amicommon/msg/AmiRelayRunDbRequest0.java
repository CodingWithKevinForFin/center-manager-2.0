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

public abstract class AmiRelayRunDbRequest0 implements com.f1.ami.amicommon.msg.AmiRelayRunDbRequest ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private byte _centerId;

    private com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest _clientRequest;

    private java.lang.String _dsAdapter;

    private long _dsAmiId;

    private java.lang.String _dsName;

    private java.lang.String _dsOptions;

    private java.lang.String _dsPassword;

    private java.lang.String _dsRelayId;

    private java.lang.String _dsUrl;

    private java.lang.String _dsUsername;

    private java.lang.String _invokedBy;

    private java.lang.String _targetAgentProcessUid;

    private int _timeoutMs;

    private static final String NAMES[]={ "centerId" ,"clientRequest","dsAdapter","dsAmiId","dsName","dsOptions","dsPassword","dsRelayId","dsUrl","dsUsername","invokedBy","targetAgentProcessUid","timeoutMs"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 33;
        try{
        switch(h){

                case 0:

                    if(name == "dsAdapter" || name.equals("dsAdapter")) {this._dsAdapter=(java.lang.String)value;return;}
break;
                case 1:

                    if(name == "dsName" || name.equals("dsName")) {this._dsName=(java.lang.String)value;return;}
break;
                case 2:

                    if(name == "dsOptions" || name.equals("dsOptions")) {this._dsOptions=(java.lang.String)value;return;}
break;
                case 3:

                    if(name == "dsPassword" || name.equals("dsPassword")) {this._dsPassword=(java.lang.String)value;return;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {this._centerId=(java.lang.Byte)value;return;}
break;
                case 9:

                    if(name == "dsUrl" || name.equals("dsUrl")) {this._dsUrl=(java.lang.String)value;return;}
break;
                case 10:

                    if(name == "dsAmiId" || name.equals("dsAmiId")) {this._dsAmiId=(java.lang.Long)value;return;}
break;
                case 16:

                    if(name == "dsRelayId" || name.equals("dsRelayId")) {this._dsRelayId=(java.lang.String)value;return;}
break;
                case 19:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {this._targetAgentProcessUid=(java.lang.String)value;return;}
break;
                case 20:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return;}
break;
                case 24:

                    if(name == "clientRequest" || name.equals("clientRequest")) {this._clientRequest=(com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest)value;return;}
break;
                case 28:

                    if(name == "dsUsername" || name.equals("dsUsername")) {this._dsUsername=(java.lang.String)value;return;}
break;
                case 30:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {this._timeoutMs=(java.lang.Integer)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 33;
        switch(h){

                case 0:

                    if(name == "dsAdapter" || name.equals("dsAdapter")) {this._dsAdapter=(java.lang.String)value;return true;}
break;
                case 1:

                    if(name == "dsName" || name.equals("dsName")) {this._dsName=(java.lang.String)value;return true;}
break;
                case 2:

                    if(name == "dsOptions" || name.equals("dsOptions")) {this._dsOptions=(java.lang.String)value;return true;}
break;
                case 3:

                    if(name == "dsPassword" || name.equals("dsPassword")) {this._dsPassword=(java.lang.String)value;return true;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {this._centerId=(java.lang.Byte)value;return true;}
break;
                case 9:

                    if(name == "dsUrl" || name.equals("dsUrl")) {this._dsUrl=(java.lang.String)value;return true;}
break;
                case 10:

                    if(name == "dsAmiId" || name.equals("dsAmiId")) {this._dsAmiId=(java.lang.Long)value;return true;}
break;
                case 16:

                    if(name == "dsRelayId" || name.equals("dsRelayId")) {this._dsRelayId=(java.lang.String)value;return true;}
break;
                case 19:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {this._targetAgentProcessUid=(java.lang.String)value;return true;}
break;
                case 20:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return true;}
break;
                case 24:

                    if(name == "clientRequest" || name.equals("clientRequest")) {this._clientRequest=(com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest)value;return true;}
break;
                case 28:

                    if(name == "dsUsername" || name.equals("dsUsername")) {this._dsUsername=(java.lang.String)value;return true;}
break;
                case 30:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {this._timeoutMs=(java.lang.Integer)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 33;
        switch(h){

                case 0:

		    
                    if(name == "dsAdapter" || name.equals("dsAdapter")) {return this._dsAdapter;}
            
break;
                case 1:

		    
                    if(name == "dsName" || name.equals("dsName")) {return this._dsName;}
            
break;
                case 2:

		    
                    if(name == "dsOptions" || name.equals("dsOptions")) {return this._dsOptions;}
            
break;
                case 3:

		    
                    if(name == "dsPassword" || name.equals("dsPassword")) {return this._dsPassword;}
            
break;
                case 4:

		    
                    if(name == "centerId" || name.equals("centerId")) {return OH.valueOf(this._centerId);}
		    
break;
                case 9:

		    
                    if(name == "dsUrl" || name.equals("dsUrl")) {return this._dsUrl;}
            
break;
                case 10:

		    
                    if(name == "dsAmiId" || name.equals("dsAmiId")) {return OH.valueOf(this._dsAmiId);}
		    
break;
                case 16:

		    
                    if(name == "dsRelayId" || name.equals("dsRelayId")) {return this._dsRelayId;}
            
break;
                case 19:

		    
                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return this._targetAgentProcessUid;}
            
break;
                case 20:

		    
                    if(name == "invokedBy" || name.equals("invokedBy")) {return this._invokedBy;}
            
break;
                case 24:

		    
                    if(name == "clientRequest" || name.equals("clientRequest")) {return this._clientRequest;}
            
break;
                case 28:

		    
                    if(name == "dsUsername" || name.equals("dsUsername")) {return this._dsUsername;}
            
break;
                case 30:

		    
                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return OH.valueOf(this._timeoutMs);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 33;
        switch(h){

                case 0:

                    if(name == "dsAdapter" || name.equals("dsAdapter")) {return java.lang.String.class;}
break;
                case 1:

                    if(name == "dsName" || name.equals("dsName")) {return java.lang.String.class;}
break;
                case 2:

                    if(name == "dsOptions" || name.equals("dsOptions")) {return java.lang.String.class;}
break;
                case 3:

                    if(name == "dsPassword" || name.equals("dsPassword")) {return java.lang.String.class;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return byte.class;}
break;
                case 9:

                    if(name == "dsUrl" || name.equals("dsUrl")) {return java.lang.String.class;}
break;
                case 10:

                    if(name == "dsAmiId" || name.equals("dsAmiId")) {return long.class;}
break;
                case 16:

                    if(name == "dsRelayId" || name.equals("dsRelayId")) {return java.lang.String.class;}
break;
                case 19:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return java.lang.String.class;}
break;
                case 20:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return java.lang.String.class;}
break;
                case 24:

                    if(name == "clientRequest" || name.equals("clientRequest")) {return com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest.class;}
break;
                case 28:

                    if(name == "dsUsername" || name.equals("dsUsername")) {return java.lang.String.class;}
break;
                case 30:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return int.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 33;
        switch(h){

                case 0:

                    if(name == "dsAdapter" || name.equals("dsAdapter")) {return VALUED_PARAM_dsAdapter;}
break;
                case 1:

                    if(name == "dsName" || name.equals("dsName")) {return VALUED_PARAM_dsName;}
break;
                case 2:

                    if(name == "dsOptions" || name.equals("dsOptions")) {return VALUED_PARAM_dsOptions;}
break;
                case 3:

                    if(name == "dsPassword" || name.equals("dsPassword")) {return VALUED_PARAM_dsPassword;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return VALUED_PARAM_centerId;}
break;
                case 9:

                    if(name == "dsUrl" || name.equals("dsUrl")) {return VALUED_PARAM_dsUrl;}
break;
                case 10:

                    if(name == "dsAmiId" || name.equals("dsAmiId")) {return VALUED_PARAM_dsAmiId;}
break;
                case 16:

                    if(name == "dsRelayId" || name.equals("dsRelayId")) {return VALUED_PARAM_dsRelayId;}
break;
                case 19:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return VALUED_PARAM_targetAgentProcessUid;}
break;
                case 20:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return VALUED_PARAM_invokedBy;}
break;
                case 24:

                    if(name == "clientRequest" || name.equals("clientRequest")) {return VALUED_PARAM_clientRequest;}
break;
                case 28:

                    if(name == "dsUsername" || name.equals("dsUsername")) {return VALUED_PARAM_dsUsername;}
break;
                case 30:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return VALUED_PARAM_timeoutMs;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 33;
        switch(h){

                case 0:

                    if(name == "dsAdapter" || name.equals("dsAdapter")) {return 2;}
break;
                case 1:

                    if(name == "dsName" || name.equals("dsName")) {return 4;}
break;
                case 2:

                    if(name == "dsOptions" || name.equals("dsOptions")) {return 5;}
break;
                case 3:

                    if(name == "dsPassword" || name.equals("dsPassword")) {return 6;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return 0;}
break;
                case 9:

                    if(name == "dsUrl" || name.equals("dsUrl")) {return 8;}
break;
                case 10:

                    if(name == "dsAmiId" || name.equals("dsAmiId")) {return 3;}
break;
                case 16:

                    if(name == "dsRelayId" || name.equals("dsRelayId")) {return 7;}
break;
                case 19:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return 11;}
break;
                case 20:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 10;}
break;
                case 24:

                    if(name == "clientRequest" || name.equals("clientRequest")) {return 1;}
break;
                case 28:

                    if(name == "dsUsername" || name.equals("dsUsername")) {return 9;}
break;
                case 30:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return 12;}
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
        return 13;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiRelayRunDbRequest0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 33;
        switch(h){

                case 0:

                    if(name == "dsAdapter" || name.equals("dsAdapter")) {return true;}
break;
                case 1:

                    if(name == "dsName" || name.equals("dsName")) {return true;}
break;
                case 2:

                    if(name == "dsOptions" || name.equals("dsOptions")) {return true;}
break;
                case 3:

                    if(name == "dsPassword" || name.equals("dsPassword")) {return true;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return true;}
break;
                case 9:

                    if(name == "dsUrl" || name.equals("dsUrl")) {return true;}
break;
                case 10:

                    if(name == "dsAmiId" || name.equals("dsAmiId")) {return true;}
break;
                case 16:

                    if(name == "dsRelayId" || name.equals("dsRelayId")) {return true;}
break;
                case 19:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return true;}
break;
                case 20:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return true;}
break;
                case 24:

                    if(name == "clientRequest" || name.equals("clientRequest")) {return true;}
break;
                case 28:

                    if(name == "dsUsername" || name.equals("dsUsername")) {return true;}
break;
                case 30:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 33;
        switch(h){

                case 0:

                    if(name == "dsAdapter" || name.equals("dsAdapter")) {return 20;}
break;
                case 1:

                    if(name == "dsName" || name.equals("dsName")) {return 20;}
break;
                case 2:

                    if(name == "dsOptions" || name.equals("dsOptions")) {return 20;}
break;
                case 3:

                    if(name == "dsPassword" || name.equals("dsPassword")) {return 20;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return 1;}
break;
                case 9:

                    if(name == "dsUrl" || name.equals("dsUrl")) {return 20;}
break;
                case 10:

                    if(name == "dsAmiId" || name.equals("dsAmiId")) {return 6;}
break;
                case 16:

                    if(name == "dsRelayId" || name.equals("dsRelayId")) {return 20;}
break;
                case 19:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return 20;}
break;
                case 20:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 20;}
break;
                case 24:

                    if(name == "clientRequest" || name.equals("clientRequest")) {return 41;}
break;
                case 28:

                    if(name == "dsUsername" || name.equals("dsUsername")) {return 20;}
break;
                case 30:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return 4;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _centerId;

        case 1:return _clientRequest;

        case 2:return _dsAdapter;

        case 3:return _dsAmiId;

        case 4:return _dsName;

        case 5:return _dsOptions;

        case 6:return _dsPassword;

        case 7:return _dsRelayId;

        case 8:return _dsUrl;

        case 9:return _dsUsername;

        case 10:return _invokedBy;

        case 11:return _targetAgentProcessUid;

        case 12:return _timeoutMs;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 13);
    }

    public byte getCenterId(){
        return this._centerId;
    }
    public void setCenterId(byte _centerId){
    
        this._centerId=_centerId;
    }

    public com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest getClientRequest(){
        return this._clientRequest;
    }
    public void setClientRequest(com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest _clientRequest){
    
        this._clientRequest=_clientRequest;
    }

    public java.lang.String getDsAdapter(){
        return this._dsAdapter;
    }
    public void setDsAdapter(java.lang.String _dsAdapter){
    
        this._dsAdapter=_dsAdapter;
    }

    public long getDsAmiId(){
        return this._dsAmiId;
    }
    public void setDsAmiId(long _dsAmiId){
    
        this._dsAmiId=_dsAmiId;
    }

    public java.lang.String getDsName(){
        return this._dsName;
    }
    public void setDsName(java.lang.String _dsName){
    
        this._dsName=_dsName;
    }

    public java.lang.String getDsOptions(){
        return this._dsOptions;
    }
    public void setDsOptions(java.lang.String _dsOptions){
    
        this._dsOptions=_dsOptions;
    }

    public java.lang.String getDsPassword(){
        return this._dsPassword;
    }
    public void setDsPassword(java.lang.String _dsPassword){
    
        this._dsPassword=_dsPassword;
    }

    public java.lang.String getDsRelayId(){
        return this._dsRelayId;
    }
    public void setDsRelayId(java.lang.String _dsRelayId){
    
        this._dsRelayId=_dsRelayId;
    }

    public java.lang.String getDsUrl(){
        return this._dsUrl;
    }
    public void setDsUrl(java.lang.String _dsUrl){
    
        this._dsUrl=_dsUrl;
    }

    public java.lang.String getDsUsername(){
        return this._dsUsername;
    }
    public void setDsUsername(java.lang.String _dsUsername){
    
        this._dsUsername=_dsUsername;
    }

    public java.lang.String getInvokedBy(){
        return this._invokedBy;
    }
    public void setInvokedBy(java.lang.String _invokedBy){
    
        this._invokedBy=_invokedBy;
    }

    public java.lang.String getTargetAgentProcessUid(){
        return this._targetAgentProcessUid;
    }
    public void setTargetAgentProcessUid(java.lang.String _targetAgentProcessUid){
    
        this._targetAgentProcessUid=_targetAgentProcessUid;
    }

    public int getTimeoutMs(){
        return this._timeoutMs;
    }
    public void setTimeoutMs(int _timeoutMs){
    
        this._timeoutMs=_timeoutMs;
    }





  
    private static final class VALUED_PARAM_CLASS_centerId extends AbstractValuedParam<AmiRelayRunDbRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiRelayRunDbRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getCenterId());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunDbRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setCenterId(stream.readByte());
		    
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
            return 42;
	    }
    
	    @Override
	    public String getName() {
            return "centerId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunDbRequest0 valued) {
		    return (byte)((AmiRelayRunDbRequest0)valued).getCenterId();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunDbRequest0 valued, Object value) {
		    valued.setCenterId((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
		    dest.setCenterId(source.getCenterId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
	        return OH.eq(dest.getCenterId(),source.getCenterId());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiRelayRunDbRequest0 valued) {
		    return valued.getCenterId();
	    }
    
	    @Override
	    public void setByte(AmiRelayRunDbRequest0 valued, byte value) {
		    valued.setCenterId(value);
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
	    public void append(AmiRelayRunDbRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getCenterId());
	        
	    }
	    @Override
	    public void append(AmiRelayRunDbRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getCenterId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte centerId";
	    }
	    @Override
	    public void clear(AmiRelayRunDbRequest0 valued){
	       valued.setCenterId((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_centerId=new VALUED_PARAM_CLASS_centerId();
  

  
    private static final class VALUED_PARAM_CLASS_clientRequest extends AbstractValuedParam<AmiRelayRunDbRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 41;
	    }
	    
	    @Override
	    public void write(AmiRelayRunDbRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunDbRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest}");
		    
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
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "clientRequest";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunDbRequest0 valued) {
		    return (com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest)((AmiRelayRunDbRequest0)valued).getClientRequest();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunDbRequest0 valued, Object value) {
		    valued.setClientRequest((com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
		    dest.setClientRequest(source.getClientRequest());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
	        return OH.eq(dest.getClientRequest(),source.getClientRequest());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayRunDbRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getClientRequest());
	        
	    }
	    @Override
	    public void append(AmiRelayRunDbRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getClientRequest());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest clientRequest";
	    }
	    @Override
	    public void clear(AmiRelayRunDbRequest0 valued){
	       valued.setClientRequest(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_clientRequest=new VALUED_PARAM_CLASS_clientRequest();
  

  
    private static final class VALUED_PARAM_CLASS_dsAdapter extends AbstractValuedParam<AmiRelayRunDbRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunDbRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunDbRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "dsAdapter";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunDbRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunDbRequest0)valued).getDsAdapter();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunDbRequest0 valued, Object value) {
		    valued.setDsAdapter((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
		    dest.setDsAdapter(source.getDsAdapter());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
	        return OH.eq(dest.getDsAdapter(),source.getDsAdapter());
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
	    public void append(AmiRelayRunDbRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getDsAdapter(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunDbRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getDsAdapter(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String dsAdapter";
	    }
	    @Override
	    public void clear(AmiRelayRunDbRequest0 valued){
	       valued.setDsAdapter(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_dsAdapter=new VALUED_PARAM_CLASS_dsAdapter();
  

  
    private static final class VALUED_PARAM_CLASS_dsAmiId extends AbstractValuedParam<AmiRelayRunDbRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiRelayRunDbRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getDsAmiId());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunDbRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setDsAmiId(stream.readLong());
		    
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
            return 10;
	    }
    
	    @Override
	    public String getName() {
            return "dsAmiId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunDbRequest0 valued) {
		    return (long)((AmiRelayRunDbRequest0)valued).getDsAmiId();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunDbRequest0 valued, Object value) {
		    valued.setDsAmiId((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
		    dest.setDsAmiId(source.getDsAmiId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
	        return OH.eq(dest.getDsAmiId(),source.getDsAmiId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiRelayRunDbRequest0 valued) {
		    return valued.getDsAmiId();
	    }
    
	    @Override
	    public void setLong(AmiRelayRunDbRequest0 valued, long value) {
		    valued.setDsAmiId(value);
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
	    public void append(AmiRelayRunDbRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getDsAmiId());
	        
	    }
	    @Override
	    public void append(AmiRelayRunDbRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getDsAmiId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long dsAmiId";
	    }
	    @Override
	    public void clear(AmiRelayRunDbRequest0 valued){
	       valued.setDsAmiId(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_dsAmiId=new VALUED_PARAM_CLASS_dsAmiId();
  

  
    private static final class VALUED_PARAM_CLASS_dsName extends AbstractValuedParam<AmiRelayRunDbRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunDbRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunDbRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 6;
	    }
    
	    @Override
	    public String getName() {
            return "dsName";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunDbRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunDbRequest0)valued).getDsName();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunDbRequest0 valued, Object value) {
		    valued.setDsName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
		    dest.setDsName(source.getDsName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
	        return OH.eq(dest.getDsName(),source.getDsName());
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
	    public void append(AmiRelayRunDbRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getDsName(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunDbRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getDsName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String dsName";
	    }
	    @Override
	    public void clear(AmiRelayRunDbRequest0 valued){
	       valued.setDsName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_dsName=new VALUED_PARAM_CLASS_dsName();
  

  
    private static final class VALUED_PARAM_CLASS_dsOptions extends AbstractValuedParam<AmiRelayRunDbRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunDbRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunDbRequest0 valued, DataInput stream) throws IOException{
		    
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
            return "dsOptions";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunDbRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunDbRequest0)valued).getDsOptions();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunDbRequest0 valued, Object value) {
		    valued.setDsOptions((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
		    dest.setDsOptions(source.getDsOptions());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
	        return OH.eq(dest.getDsOptions(),source.getDsOptions());
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
	    public void append(AmiRelayRunDbRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getDsOptions(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunDbRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getDsOptions(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String dsOptions";
	    }
	    @Override
	    public void clear(AmiRelayRunDbRequest0 valued){
	       valued.setDsOptions(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_dsOptions=new VALUED_PARAM_CLASS_dsOptions();
  

  
    private static final class VALUED_PARAM_CLASS_dsPassword extends AbstractValuedParam<AmiRelayRunDbRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunDbRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunDbRequest0 valued, DataInput stream) throws IOException{
		    
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
            return "dsPassword";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunDbRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunDbRequest0)valued).getDsPassword();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunDbRequest0 valued, Object value) {
		    valued.setDsPassword((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
		    dest.setDsPassword(source.getDsPassword());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
	        return OH.eq(dest.getDsPassword(),source.getDsPassword());
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
	    public void append(AmiRelayRunDbRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getDsPassword(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunDbRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getDsPassword(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String dsPassword";
	    }
	    @Override
	    public void clear(AmiRelayRunDbRequest0 valued){
	       valued.setDsPassword(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_dsPassword=new VALUED_PARAM_CLASS_dsPassword();
  

  
    private static final class VALUED_PARAM_CLASS_dsRelayId extends AbstractValuedParam<AmiRelayRunDbRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunDbRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunDbRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 9;
	    }
    
	    @Override
	    public String getName() {
            return "dsRelayId";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunDbRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunDbRequest0)valued).getDsRelayId();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunDbRequest0 valued, Object value) {
		    valued.setDsRelayId((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
		    dest.setDsRelayId(source.getDsRelayId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
	        return OH.eq(dest.getDsRelayId(),source.getDsRelayId());
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
	    public void append(AmiRelayRunDbRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getDsRelayId(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunDbRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getDsRelayId(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String dsRelayId";
	    }
	    @Override
	    public void clear(AmiRelayRunDbRequest0 valued){
	       valued.setDsRelayId(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_dsRelayId=new VALUED_PARAM_CLASS_dsRelayId();
  

  
    private static final class VALUED_PARAM_CLASS_dsUrl extends AbstractValuedParam<AmiRelayRunDbRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunDbRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunDbRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "dsUrl";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunDbRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunDbRequest0)valued).getDsUrl();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunDbRequest0 valued, Object value) {
		    valued.setDsUrl((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
		    dest.setDsUrl(source.getDsUrl());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
	        return OH.eq(dest.getDsUrl(),source.getDsUrl());
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
	    public void append(AmiRelayRunDbRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getDsUrl(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunDbRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getDsUrl(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String dsUrl";
	    }
	    @Override
	    public void clear(AmiRelayRunDbRequest0 valued){
	       valued.setDsUrl(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_dsUrl=new VALUED_PARAM_CLASS_dsUrl();
  

  
    private static final class VALUED_PARAM_CLASS_dsUsername extends AbstractValuedParam<AmiRelayRunDbRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunDbRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunDbRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "dsUsername";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunDbRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunDbRequest0)valued).getDsUsername();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunDbRequest0 valued, Object value) {
		    valued.setDsUsername((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
		    dest.setDsUsername(source.getDsUsername());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
	        return OH.eq(dest.getDsUsername(),source.getDsUsername());
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
	    public void append(AmiRelayRunDbRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getDsUsername(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunDbRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getDsUsername(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String dsUsername";
	    }
	    @Override
	    public void clear(AmiRelayRunDbRequest0 valued){
	       valued.setDsUsername(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_dsUsername=new VALUED_PARAM_CLASS_dsUsername();
  

  
    private static final class VALUED_PARAM_CLASS_invokedBy extends AbstractValuedParam<AmiRelayRunDbRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunDbRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunDbRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 41;
	    }
    
	    @Override
	    public String getName() {
            return "invokedBy";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunDbRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunDbRequest0)valued).getInvokedBy();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunDbRequest0 valued, Object value) {
		    valued.setInvokedBy((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
		    dest.setInvokedBy(source.getInvokedBy());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
	        return OH.eq(dest.getInvokedBy(),source.getInvokedBy());
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
	    public void append(AmiRelayRunDbRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunDbRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String invokedBy";
	    }
	    @Override
	    public void clear(AmiRelayRunDbRequest0 valued){
	       valued.setInvokedBy(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_invokedBy=new VALUED_PARAM_CLASS_invokedBy();
  

  
    private static final class VALUED_PARAM_CLASS_targetAgentProcessUid extends AbstractValuedParam<AmiRelayRunDbRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayRunDbRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunDbRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 11;
	    }
    
	    @Override
	    public byte getPid() {
            return 40;
	    }
    
	    @Override
	    public String getName() {
            return "targetAgentProcessUid";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunDbRequest0 valued) {
		    return (java.lang.String)((AmiRelayRunDbRequest0)valued).getTargetAgentProcessUid();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunDbRequest0 valued, Object value) {
		    valued.setTargetAgentProcessUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
		    dest.setTargetAgentProcessUid(source.getTargetAgentProcessUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
	        return OH.eq(dest.getTargetAgentProcessUid(),source.getTargetAgentProcessUid());
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
	    public void append(AmiRelayRunDbRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getTargetAgentProcessUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayRunDbRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getTargetAgentProcessUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String targetAgentProcessUid";
	    }
	    @Override
	    public void clear(AmiRelayRunDbRequest0 valued){
	       valued.setTargetAgentProcessUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_targetAgentProcessUid=new VALUED_PARAM_CLASS_targetAgentProcessUid();
  

  
    private static final class VALUED_PARAM_CLASS_timeoutMs extends AbstractValuedParam<AmiRelayRunDbRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiRelayRunDbRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getTimeoutMs());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayRunDbRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setTimeoutMs(stream.readInt());
		    
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
		    return 12;
	    }
    
	    @Override
	    public byte getPid() {
            return 8;
	    }
    
	    @Override
	    public String getName() {
            return "timeoutMs";
	    }
    
	    @Override
	    public Object getValue(AmiRelayRunDbRequest0 valued) {
		    return (int)((AmiRelayRunDbRequest0)valued).getTimeoutMs();
	    }
    
	    @Override
	    public void setValue(AmiRelayRunDbRequest0 valued, Object value) {
		    valued.setTimeoutMs((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
		    dest.setTimeoutMs(source.getTimeoutMs());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayRunDbRequest0 source, AmiRelayRunDbRequest0 dest) {
	        return OH.eq(dest.getTimeoutMs(),source.getTimeoutMs());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiRelayRunDbRequest0 valued) {
		    return valued.getTimeoutMs();
	    }
    
	    @Override
	    public void setInt(AmiRelayRunDbRequest0 valued, int value) {
		    valued.setTimeoutMs(value);
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
	    public void append(AmiRelayRunDbRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTimeoutMs());
	        
	    }
	    @Override
	    public void append(AmiRelayRunDbRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTimeoutMs());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int timeoutMs";
	    }
	    @Override
	    public void clear(AmiRelayRunDbRequest0 valued){
	       valued.setTimeoutMs(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_timeoutMs=new VALUED_PARAM_CLASS_timeoutMs();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_centerId, VALUED_PARAM_clientRequest, VALUED_PARAM_dsAdapter, VALUED_PARAM_dsAmiId, VALUED_PARAM_dsName, VALUED_PARAM_dsOptions, VALUED_PARAM_dsPassword, VALUED_PARAM_dsRelayId, VALUED_PARAM_dsUrl, VALUED_PARAM_dsUsername, VALUED_PARAM_invokedBy, VALUED_PARAM_targetAgentProcessUid, VALUED_PARAM_timeoutMs, };



    private static final byte PIDS[]={ 42 ,7,1,10,6,4,2,9,3,5,41,40,8};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 42: return VALUED_PARAM_centerId;
             case 7: return VALUED_PARAM_clientRequest;
             case 1: return VALUED_PARAM_dsAdapter;
             case 10: return VALUED_PARAM_dsAmiId;
             case 6: return VALUED_PARAM_dsName;
             case 4: return VALUED_PARAM_dsOptions;
             case 2: return VALUED_PARAM_dsPassword;
             case 9: return VALUED_PARAM_dsRelayId;
             case 3: return VALUED_PARAM_dsUrl;
             case 5: return VALUED_PARAM_dsUsername;
             case 41: return VALUED_PARAM_invokedBy;
             case 40: return VALUED_PARAM_targetAgentProcessUid;
             case 8: return VALUED_PARAM_timeoutMs;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 42: return true;
             case 7: return true;
             case 1: return true;
             case 10: return true;
             case 6: return true;
             case 4: return true;
             case 2: return true;
             case 9: return true;
             case 3: return true;
             case 5: return true;
             case 41: return true;
             case 40: return true;
             case 8: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 42: return "centerId";
             case 7: return "clientRequest";
             case 1: return "dsAdapter";
             case 10: return "dsAmiId";
             case 6: return "dsName";
             case 4: return "dsOptions";
             case 2: return "dsPassword";
             case 9: return "dsRelayId";
             case 3: return "dsUrl";
             case 5: return "dsUsername";
             case 41: return "invokedBy";
             case 40: return "targetAgentProcessUid";
             case 8: return "timeoutMs";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 42: return 0;
             case 7: return 1;
             case 1: return 2;
             case 10: return 3;
             case 6: return 4;
             case 4: return 5;
             case 2: return 6;
             case 9: return 7;
             case 3: return 8;
             case 5: return 9;
             case 41: return 10;
             case 40: return 11;
             case 8: return 12;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="centerId") return 42;
             if(name=="clientRequest") return 7;
             if(name=="dsAdapter") return 1;
             if(name=="dsAmiId") return 10;
             if(name=="dsName") return 6;
             if(name=="dsOptions") return 4;
             if(name=="dsPassword") return 2;
             if(name=="dsRelayId") return 9;
             if(name=="dsUrl") return 3;
             if(name=="dsUsername") return 5;
             if(name=="invokedBy") return 41;
             if(name=="targetAgentProcessUid") return 40;
             if(name=="timeoutMs") return 8;
            
             if("centerId".equals(name)) return 42;
             if("clientRequest".equals(name)) return 7;
             if("dsAdapter".equals(name)) return 1;
             if("dsAmiId".equals(name)) return 10;
             if("dsName".equals(name)) return 6;
             if("dsOptions".equals(name)) return 4;
             if("dsPassword".equals(name)) return 2;
             if("dsRelayId".equals(name)) return 9;
             if("dsUrl".equals(name)) return 3;
             if("dsUsername".equals(name)) return 5;
             if("invokedBy".equals(name)) return 41;
             if("targetAgentProcessUid".equals(name)) return 40;
             if("timeoutMs".equals(name)) return 8;
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
             case 42: return  OH.valueOf(this._centerId); 
             case 7: return  this._clientRequest; 
             case 1: return  this._dsAdapter; 
             case 10: return  OH.valueOf(this._dsAmiId); 
             case 6: return  this._dsName; 
             case 4: return  this._dsOptions; 
             case 2: return  this._dsPassword; 
             case 9: return  this._dsRelayId; 
             case 3: return  this._dsUrl; 
             case 5: return  this._dsUsername; 
             case 41: return  this._invokedBy; 
             case 40: return  this._targetAgentProcessUid; 
             case 8: return  OH.valueOf(this._timeoutMs); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 42: return byte.class;
             case 7: return com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest.class;
             case 1: return java.lang.String.class;
             case 10: return long.class;
             case 6: return java.lang.String.class;
             case 4: return java.lang.String.class;
             case 2: return java.lang.String.class;
             case 9: return java.lang.String.class;
             case 3: return java.lang.String.class;
             case 5: return java.lang.String.class;
             case 41: return java.lang.String.class;
             case 40: return java.lang.String.class;
             case 8: return int.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 42: return 1;
             case 7: return 41;
             case 1: return 20;
             case 10: return 6;
             case 6: return 20;
             case 4: return 20;
             case 2: return 20;
             case 9: return 20;
             case 3: return 20;
             case 5: return 20;
             case 41: return 20;
             case 40: return 20;
             case 8: return 4;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 42: this._centerId=(java.lang.Byte)value;return;
             case 7: this._clientRequest=(com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest)value;return;
             case 1: this._dsAdapter=(java.lang.String)value;return;
             case 10: this._dsAmiId=(java.lang.Long)value;return;
             case 6: this._dsName=(java.lang.String)value;return;
             case 4: this._dsOptions=(java.lang.String)value;return;
             case 2: this._dsPassword=(java.lang.String)value;return;
             case 9: this._dsRelayId=(java.lang.String)value;return;
             case 3: this._dsUrl=(java.lang.String)value;return;
             case 5: this._dsUsername=(java.lang.String)value;return;
             case 41: this._invokedBy=(java.lang.String)value;return;
             case 40: this._targetAgentProcessUid=(java.lang.String)value;return;
             case 8: this._timeoutMs=(java.lang.Integer)value;return;
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
             case 42: this._centerId=(java.lang.Byte)value;return true;
             case 7: this._clientRequest=(com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest)value;return true;
             case 1: this._dsAdapter=(java.lang.String)value;return true;
             case 10: this._dsAmiId=(java.lang.Long)value;return true;
             case 6: this._dsName=(java.lang.String)value;return true;
             case 4: this._dsOptions=(java.lang.String)value;return true;
             case 2: this._dsPassword=(java.lang.String)value;return true;
             case 9: this._dsRelayId=(java.lang.String)value;return true;
             case 3: this._dsUrl=(java.lang.String)value;return true;
             case 5: this._dsUsername=(java.lang.String)value;return true;
             case 41: this._invokedBy=(java.lang.String)value;return true;
             case 40: this._targetAgentProcessUid=(java.lang.String)value;return true;
             case 8: this._timeoutMs=(java.lang.Integer)value;return true;
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
             case 42: return this._centerId;
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
             case 8: return this._timeoutMs;
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
             case 10: return this._dsAmiId;
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
             case 42: this._centerId=value;return;
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
             case 8: this._timeoutMs=value;return;
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
             case 10: this._dsAmiId=value;return;
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
        
            this._dsAdapter=(java.lang.String)converter.read(session);
        
            break;

        case 2:
        
            this._dsPassword=(java.lang.String)converter.read(session);
        
            break;

        case 3:
        
            this._dsUrl=(java.lang.String)converter.read(session);
        
            break;

        case 4:
        
            this._dsOptions=(java.lang.String)converter.read(session);
        
            break;

        case 5:
        
            this._dsUsername=(java.lang.String)converter.read(session);
        
            break;

        case 6:
        
            this._dsName=(java.lang.String)converter.read(session);
        
            break;

        case 7:
        
            this._clientRequest=(com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest)converter.read(session);
        
            break;

        case 8:
        
            if((basicType=in.readByte())!=4)
                break;
            this._timeoutMs=in.readInt();
        
            break;

        case 9:
        
            this._dsRelayId=(java.lang.String)converter.read(session);
        
            break;

        case 10:
        
            if((basicType=in.readByte())!=6)
                break;
            this._dsAmiId=in.readLong();
        
            break;

        case 40:
        
            this._targetAgentProcessUid=(java.lang.String)converter.read(session);
        
            break;

        case 41:
        
            this._invokedBy=(java.lang.String)converter.read(session);
        
            break;

        case 42:
        
            if((basicType=in.readByte())!=1)
                break;
            this._centerId=in.readByte();
        
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
        
if(this._dsAdapter!=null && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this._dsAdapter,session);
        
}

if(this._dsPassword!=null && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this._dsPassword,session);
        
}

if(this._dsUrl!=null && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this._dsUrl,session);
        
}

if(this._dsOptions!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._dsOptions,session);
        
}

if(this._dsUsername!=null && (0 & transience)==0){
    out.writeByte(5);
        
    converter.write(this._dsUsername,session);
        
}

if(this._dsName!=null && (0 & transience)==0){
    out.writeByte(6);
        
    converter.write(this._dsName,session);
        
}

if(this._clientRequest!=null && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this._clientRequest,session);
        
}

if(this._timeoutMs!=0 && (0 & transience)==0){
    out.writeByte(8);
        
    out.writeByte(4);
    out.writeInt(this._timeoutMs);
        
}

if(this._dsRelayId!=null && (0 & transience)==0){
    out.writeByte(9);
        
    converter.write(this._dsRelayId,session);
        
}

if(this._dsAmiId!=0L && (0 & transience)==0){
    out.writeByte(10);
        
    out.writeByte(6);
    out.writeLong(this._dsAmiId);
        
}

if(this._targetAgentProcessUid!=null && (0 & transience)==0){
    out.writeByte(40);
        
    converter.write(this._targetAgentProcessUid,session);
        
}

if(this._invokedBy!=null && (0 & transience)==0){
    out.writeByte(41);
        
    converter.write(this._invokedBy,session);
        
}

if(this._centerId!=(byte)0 && (0 & transience)==0){
    out.writeByte(42);
        
    out.writeByte(1);
    out.writeByte(this._centerId);
        
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