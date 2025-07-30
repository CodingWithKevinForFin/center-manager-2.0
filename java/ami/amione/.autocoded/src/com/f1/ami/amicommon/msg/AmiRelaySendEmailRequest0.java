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

public abstract class AmiRelaySendEmailRequest0 implements com.f1.ami.amicommon.msg.AmiRelaySendEmailRequest ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private java.util.List _attachmentDatas;

    private java.util.List _attachmentNames;

    private java.lang.String _body;

    private byte _centerId;

    private java.lang.String _from;

    private java.lang.String _invokedBy;

    private boolean _isHtml;

    private com.f1.base.Password _password;

    private java.lang.String _sendEmailUid;

    private java.lang.String _subject;

    private java.lang.String _targetAgentProcessUid;

    private int _timeoutMs;

    private java.util.List _toList;

    private java.lang.String _username;

    private static final String NAMES[]={ "attachmentDatas" ,"attachmentNames","body","centerId","from","invokedBy","isHtml","password","sendEmailUid","subject","targetAgentProcessUid","timeoutMs","toList","username"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 34;
        try{
        switch(h){

                case 0:

                    if(name == "subject" || name.equals("subject")) {this._subject=(java.lang.String)value;return;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {this._centerId=(java.lang.Byte)value;return;}
break;
                case 5:

                    if(name == "attachmentNames" || name.equals("attachmentNames")) {this._attachmentNames=(java.util.List)value;return;}
break;
                case 7:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {this._timeoutMs=(java.lang.Integer)value;return;}
break;
                case 8:

                    if(name == "sendEmailUid" || name.equals("sendEmailUid")) {this._sendEmailUid=(java.lang.String)value;return;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return;}
break;
                case 10:

                    if(name == "body" || name.equals("body")) {this._body=(java.lang.String)value;return;}
break;
                case 15:

                    if(name == "toList" || name.equals("toList")) {this._toList=(java.util.List)value;return;}
break;
                case 16:

                    if(name == "username" || name.equals("username")) {this._username=(java.lang.String)value;return;}
break;
                case 20:

                    if(name == "from" || name.equals("from")) {this._from=(java.lang.String)value;return;}
break;
                case 23:

                    if(name == "password" || name.equals("password")) {this._password=(com.f1.base.Password)value;return;}
break;
                case 26:

                    if(name == "attachmentDatas" || name.equals("attachmentDatas")) {this._attachmentDatas=(java.util.List)value;return;}
break;
                case 27:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {this._targetAgentProcessUid=(java.lang.String)value;return;}
break;
                case 31:

                    if(name == "isHtml" || name.equals("isHtml")) {this._isHtml=(java.lang.Boolean)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 34;
        switch(h){

                case 0:

                    if(name == "subject" || name.equals("subject")) {this._subject=(java.lang.String)value;return true;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {this._centerId=(java.lang.Byte)value;return true;}
break;
                case 5:

                    if(name == "attachmentNames" || name.equals("attachmentNames")) {this._attachmentNames=(java.util.List)value;return true;}
break;
                case 7:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {this._timeoutMs=(java.lang.Integer)value;return true;}
break;
                case 8:

                    if(name == "sendEmailUid" || name.equals("sendEmailUid")) {this._sendEmailUid=(java.lang.String)value;return true;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return true;}
break;
                case 10:

                    if(name == "body" || name.equals("body")) {this._body=(java.lang.String)value;return true;}
break;
                case 15:

                    if(name == "toList" || name.equals("toList")) {this._toList=(java.util.List)value;return true;}
break;
                case 16:

                    if(name == "username" || name.equals("username")) {this._username=(java.lang.String)value;return true;}
break;
                case 20:

                    if(name == "from" || name.equals("from")) {this._from=(java.lang.String)value;return true;}
break;
                case 23:

                    if(name == "password" || name.equals("password")) {this._password=(com.f1.base.Password)value;return true;}
break;
                case 26:

                    if(name == "attachmentDatas" || name.equals("attachmentDatas")) {this._attachmentDatas=(java.util.List)value;return true;}
break;
                case 27:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {this._targetAgentProcessUid=(java.lang.String)value;return true;}
break;
                case 31:

                    if(name == "isHtml" || name.equals("isHtml")) {this._isHtml=(java.lang.Boolean)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 34;
        switch(h){

                case 0:

		    
                    if(name == "subject" || name.equals("subject")) {return this._subject;}
            
break;
                case 4:

		    
                    if(name == "centerId" || name.equals("centerId")) {return OH.valueOf(this._centerId);}
		    
break;
                case 5:

		    
                    if(name == "attachmentNames" || name.equals("attachmentNames")) {return this._attachmentNames;}
            
break;
                case 7:

		    
                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return OH.valueOf(this._timeoutMs);}
		    
break;
                case 8:

		    
                    if(name == "sendEmailUid" || name.equals("sendEmailUid")) {return this._sendEmailUid;}
            
break;
                case 9:

		    
                    if(name == "invokedBy" || name.equals("invokedBy")) {return this._invokedBy;}
            
break;
                case 10:

		    
                    if(name == "body" || name.equals("body")) {return this._body;}
            
break;
                case 15:

		    
                    if(name == "toList" || name.equals("toList")) {return this._toList;}
            
break;
                case 16:

		    
                    if(name == "username" || name.equals("username")) {return this._username;}
            
break;
                case 20:

		    
                    if(name == "from" || name.equals("from")) {return this._from;}
            
break;
                case 23:

		    
                    if(name == "password" || name.equals("password")) {return this._password;}
            
break;
                case 26:

		    
                    if(name == "attachmentDatas" || name.equals("attachmentDatas")) {return this._attachmentDatas;}
            
break;
                case 27:

		    
                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return this._targetAgentProcessUid;}
            
break;
                case 31:

		    
                    if(name == "isHtml" || name.equals("isHtml")) {return OH.valueOf(this._isHtml);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 34;
        switch(h){

                case 0:

                    if(name == "subject" || name.equals("subject")) {return java.lang.String.class;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return byte.class;}
break;
                case 5:

                    if(name == "attachmentNames" || name.equals("attachmentNames")) {return java.util.List.class;}
break;
                case 7:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return int.class;}
break;
                case 8:

                    if(name == "sendEmailUid" || name.equals("sendEmailUid")) {return java.lang.String.class;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return java.lang.String.class;}
break;
                case 10:

                    if(name == "body" || name.equals("body")) {return java.lang.String.class;}
break;
                case 15:

                    if(name == "toList" || name.equals("toList")) {return java.util.List.class;}
break;
                case 16:

                    if(name == "username" || name.equals("username")) {return java.lang.String.class;}
break;
                case 20:

                    if(name == "from" || name.equals("from")) {return java.lang.String.class;}
break;
                case 23:

                    if(name == "password" || name.equals("password")) {return com.f1.base.Password.class;}
break;
                case 26:

                    if(name == "attachmentDatas" || name.equals("attachmentDatas")) {return java.util.List.class;}
break;
                case 27:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return java.lang.String.class;}
break;
                case 31:

                    if(name == "isHtml" || name.equals("isHtml")) {return boolean.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 34;
        switch(h){

                case 0:

                    if(name == "subject" || name.equals("subject")) {return VALUED_PARAM_subject;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return VALUED_PARAM_centerId;}
break;
                case 5:

                    if(name == "attachmentNames" || name.equals("attachmentNames")) {return VALUED_PARAM_attachmentNames;}
break;
                case 7:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return VALUED_PARAM_timeoutMs;}
break;
                case 8:

                    if(name == "sendEmailUid" || name.equals("sendEmailUid")) {return VALUED_PARAM_sendEmailUid;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return VALUED_PARAM_invokedBy;}
break;
                case 10:

                    if(name == "body" || name.equals("body")) {return VALUED_PARAM_body;}
break;
                case 15:

                    if(name == "toList" || name.equals("toList")) {return VALUED_PARAM_toList;}
break;
                case 16:

                    if(name == "username" || name.equals("username")) {return VALUED_PARAM_username;}
break;
                case 20:

                    if(name == "from" || name.equals("from")) {return VALUED_PARAM_from;}
break;
                case 23:

                    if(name == "password" || name.equals("password")) {return VALUED_PARAM_password;}
break;
                case 26:

                    if(name == "attachmentDatas" || name.equals("attachmentDatas")) {return VALUED_PARAM_attachmentDatas;}
break;
                case 27:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return VALUED_PARAM_targetAgentProcessUid;}
break;
                case 31:

                    if(name == "isHtml" || name.equals("isHtml")) {return VALUED_PARAM_isHtml;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 34;
        switch(h){

                case 0:

                    if(name == "subject" || name.equals("subject")) {return 9;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return 3;}
break;
                case 5:

                    if(name == "attachmentNames" || name.equals("attachmentNames")) {return 1;}
break;
                case 7:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return 11;}
break;
                case 8:

                    if(name == "sendEmailUid" || name.equals("sendEmailUid")) {return 8;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 5;}
break;
                case 10:

                    if(name == "body" || name.equals("body")) {return 2;}
break;
                case 15:

                    if(name == "toList" || name.equals("toList")) {return 12;}
break;
                case 16:

                    if(name == "username" || name.equals("username")) {return 13;}
break;
                case 20:

                    if(name == "from" || name.equals("from")) {return 4;}
break;
                case 23:

                    if(name == "password" || name.equals("password")) {return 7;}
break;
                case 26:

                    if(name == "attachmentDatas" || name.equals("attachmentDatas")) {return 0;}
break;
                case 27:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return 10;}
break;
                case 31:

                    if(name == "isHtml" || name.equals("isHtml")) {return 6;}
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
        return 14;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiRelaySendEmailRequest0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 34;
        switch(h){

                case 0:

                    if(name == "subject" || name.equals("subject")) {return true;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return true;}
break;
                case 5:

                    if(name == "attachmentNames" || name.equals("attachmentNames")) {return true;}
break;
                case 7:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return true;}
break;
                case 8:

                    if(name == "sendEmailUid" || name.equals("sendEmailUid")) {return true;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return true;}
break;
                case 10:

                    if(name == "body" || name.equals("body")) {return true;}
break;
                case 15:

                    if(name == "toList" || name.equals("toList")) {return true;}
break;
                case 16:

                    if(name == "username" || name.equals("username")) {return true;}
break;
                case 20:

                    if(name == "from" || name.equals("from")) {return true;}
break;
                case 23:

                    if(name == "password" || name.equals("password")) {return true;}
break;
                case 26:

                    if(name == "attachmentDatas" || name.equals("attachmentDatas")) {return true;}
break;
                case 27:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return true;}
break;
                case 31:

                    if(name == "isHtml" || name.equals("isHtml")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 34;
        switch(h){

                case 0:

                    if(name == "subject" || name.equals("subject")) {return 20;}
break;
                case 4:

                    if(name == "centerId" || name.equals("centerId")) {return 1;}
break;
                case 5:

                    if(name == "attachmentNames" || name.equals("attachmentNames")) {return 21;}
break;
                case 7:

                    if(name == "timeoutMs" || name.equals("timeoutMs")) {return 4;}
break;
                case 8:

                    if(name == "sendEmailUid" || name.equals("sendEmailUid")) {return 20;}
break;
                case 9:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 20;}
break;
                case 10:

                    if(name == "body" || name.equals("body")) {return 20;}
break;
                case 15:

                    if(name == "toList" || name.equals("toList")) {return 21;}
break;
                case 16:

                    if(name == "username" || name.equals("username")) {return 20;}
break;
                case 20:

                    if(name == "from" || name.equals("from")) {return 20;}
break;
                case 23:

                    if(name == "password" || name.equals("password")) {return 24;}
break;
                case 26:

                    if(name == "attachmentDatas" || name.equals("attachmentDatas")) {return 21;}
break;
                case 27:

                    if(name == "targetAgentProcessUid" || name.equals("targetAgentProcessUid")) {return 20;}
break;
                case 31:

                    if(name == "isHtml" || name.equals("isHtml")) {return 0;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _attachmentDatas;

        case 1:return _attachmentNames;

        case 2:return _body;

        case 3:return _centerId;

        case 4:return _from;

        case 5:return _invokedBy;

        case 6:return _isHtml;

        case 7:return _password;

        case 8:return _sendEmailUid;

        case 9:return _subject;

        case 10:return _targetAgentProcessUid;

        case 11:return _timeoutMs;

        case 12:return _toList;

        case 13:return _username;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 14);
    }

    public java.util.List getAttachmentDatas(){
        return this._attachmentDatas;
    }
    public void setAttachmentDatas(java.util.List _attachmentDatas){
    
        this._attachmentDatas=_attachmentDatas;
    }

    public java.util.List getAttachmentNames(){
        return this._attachmentNames;
    }
    public void setAttachmentNames(java.util.List _attachmentNames){
    
        this._attachmentNames=_attachmentNames;
    }

    public java.lang.String getBody(){
        return this._body;
    }
    public void setBody(java.lang.String _body){
    
        this._body=_body;
    }

    public byte getCenterId(){
        return this._centerId;
    }
    public void setCenterId(byte _centerId){
    
        this._centerId=_centerId;
    }

    public java.lang.String getFrom(){
        return this._from;
    }
    public void setFrom(java.lang.String _from){
    
        this._from=_from;
    }

    public java.lang.String getInvokedBy(){
        return this._invokedBy;
    }
    public void setInvokedBy(java.lang.String _invokedBy){
    
        this._invokedBy=_invokedBy;
    }

    public boolean getIsHtml(){
        return this._isHtml;
    }
    public void setIsHtml(boolean _isHtml){
    
        this._isHtml=_isHtml;
    }

    public com.f1.base.Password getPassword(){
        return this._password;
    }
    public void setPassword(com.f1.base.Password _password){
    
        this._password=_password;
    }

    public java.lang.String getSendEmailUid(){
        return this._sendEmailUid;
    }
    public void setSendEmailUid(java.lang.String _sendEmailUid){
    
        this._sendEmailUid=_sendEmailUid;
    }

    public java.lang.String getSubject(){
        return this._subject;
    }
    public void setSubject(java.lang.String _subject){
    
        this._subject=_subject;
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

    public java.util.List getToList(){
        return this._toList;
    }
    public void setToList(java.util.List _toList){
    
        this._toList=_toList;
    }

    public java.lang.String getUsername(){
        return this._username;
    }
    public void setUsername(java.lang.String _username){
    
        this._username=_username;
    }





  
    private static final class VALUED_PARAM_CLASS_attachmentDatas extends AbstractValuedParam<AmiRelaySendEmailRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiRelaySendEmailRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelaySendEmailRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 0;
	    }
    
	    @Override
	    public byte getPid() {
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "attachmentDatas";
	    }
    
	    @Override
	    public Object getValue(AmiRelaySendEmailRequest0 valued) {
		    return (java.util.List)((AmiRelaySendEmailRequest0)valued).getAttachmentDatas();
	    }
    
	    @Override
	    public void setValue(AmiRelaySendEmailRequest0 valued, Object value) {
		    valued.setAttachmentDatas((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
		    dest.setAttachmentDatas(source.getAttachmentDatas());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
	        return OH.eq(dest.getAttachmentDatas(),source.getAttachmentDatas());
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
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAttachmentDatas());
	        
	    }
	    @Override
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAttachmentDatas());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List attachmentDatas";
	    }
	    @Override
	    public void clear(AmiRelaySendEmailRequest0 valued){
	       valued.setAttachmentDatas(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_attachmentDatas=new VALUED_PARAM_CLASS_attachmentDatas();
  

  
    private static final class VALUED_PARAM_CLASS_attachmentNames extends AbstractValuedParam<AmiRelaySendEmailRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiRelaySendEmailRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelaySendEmailRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 6;
	    }
    
	    @Override
	    public String getName() {
            return "attachmentNames";
	    }
    
	    @Override
	    public Object getValue(AmiRelaySendEmailRequest0 valued) {
		    return (java.util.List)((AmiRelaySendEmailRequest0)valued).getAttachmentNames();
	    }
    
	    @Override
	    public void setValue(AmiRelaySendEmailRequest0 valued, Object value) {
		    valued.setAttachmentNames((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
		    dest.setAttachmentNames(source.getAttachmentNames());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
	        return OH.eq(dest.getAttachmentNames(),source.getAttachmentNames());
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
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAttachmentNames());
	        
	    }
	    @Override
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAttachmentNames());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List attachmentNames";
	    }
	    @Override
	    public void clear(AmiRelaySendEmailRequest0 valued){
	       valued.setAttachmentNames(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_attachmentNames=new VALUED_PARAM_CLASS_attachmentNames();
  

  
    private static final class VALUED_PARAM_CLASS_body extends AbstractValuedParam<AmiRelaySendEmailRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelaySendEmailRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelaySendEmailRequest0 valued, DataInput stream) throws IOException{
		    
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
            return "body";
	    }
    
	    @Override
	    public Object getValue(AmiRelaySendEmailRequest0 valued) {
		    return (java.lang.String)((AmiRelaySendEmailRequest0)valued).getBody();
	    }
    
	    @Override
	    public void setValue(AmiRelaySendEmailRequest0 valued, Object value) {
		    valued.setBody((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
		    dest.setBody(source.getBody());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
	        return OH.eq(dest.getBody(),source.getBody());
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
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getBody(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getBody(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String body";
	    }
	    @Override
	    public void clear(AmiRelaySendEmailRequest0 valued){
	       valued.setBody(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_body=new VALUED_PARAM_CLASS_body();
  

  
    private static final class VALUED_PARAM_CLASS_centerId extends AbstractValuedParam<AmiRelaySendEmailRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiRelaySendEmailRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getCenterId());
		    
	    }
	    
	    @Override
	    public void read(AmiRelaySendEmailRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 3;
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
	    public Object getValue(AmiRelaySendEmailRequest0 valued) {
		    return (byte)((AmiRelaySendEmailRequest0)valued).getCenterId();
	    }
    
	    @Override
	    public void setValue(AmiRelaySendEmailRequest0 valued, Object value) {
		    valued.setCenterId((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
		    dest.setCenterId(source.getCenterId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
	        return OH.eq(dest.getCenterId(),source.getCenterId());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiRelaySendEmailRequest0 valued) {
		    return valued.getCenterId();
	    }
    
	    @Override
	    public void setByte(AmiRelaySendEmailRequest0 valued, byte value) {
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
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getCenterId());
	        
	    }
	    @Override
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getCenterId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte centerId";
	    }
	    @Override
	    public void clear(AmiRelaySendEmailRequest0 valued){
	       valued.setCenterId((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_centerId=new VALUED_PARAM_CLASS_centerId();
  

  
    private static final class VALUED_PARAM_CLASS_from extends AbstractValuedParam<AmiRelaySendEmailRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelaySendEmailRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelaySendEmailRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "from";
	    }
    
	    @Override
	    public Object getValue(AmiRelaySendEmailRequest0 valued) {
		    return (java.lang.String)((AmiRelaySendEmailRequest0)valued).getFrom();
	    }
    
	    @Override
	    public void setValue(AmiRelaySendEmailRequest0 valued, Object value) {
		    valued.setFrom((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
		    dest.setFrom(source.getFrom());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
	        return OH.eq(dest.getFrom(),source.getFrom());
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
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getFrom(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getFrom(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String from";
	    }
	    @Override
	    public void clear(AmiRelaySendEmailRequest0 valued){
	       valued.setFrom(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_from=new VALUED_PARAM_CLASS_from();
  

  
    private static final class VALUED_PARAM_CLASS_invokedBy extends AbstractValuedParam<AmiRelaySendEmailRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelaySendEmailRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelaySendEmailRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 41;
	    }
    
	    @Override
	    public String getName() {
            return "invokedBy";
	    }
    
	    @Override
	    public Object getValue(AmiRelaySendEmailRequest0 valued) {
		    return (java.lang.String)((AmiRelaySendEmailRequest0)valued).getInvokedBy();
	    }
    
	    @Override
	    public void setValue(AmiRelaySendEmailRequest0 valued, Object value) {
		    valued.setInvokedBy((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
		    dest.setInvokedBy(source.getInvokedBy());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
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
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String invokedBy";
	    }
	    @Override
	    public void clear(AmiRelaySendEmailRequest0 valued){
	       valued.setInvokedBy(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_invokedBy=new VALUED_PARAM_CLASS_invokedBy();
  

  
    private static final class VALUED_PARAM_CLASS_isHtml extends AbstractValuedParam<AmiRelaySendEmailRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiRelaySendEmailRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getIsHtml());
		    
	    }
	    
	    @Override
	    public void read(AmiRelaySendEmailRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setIsHtml(stream.readBoolean());
		    
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
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "isHtml";
	    }
    
	    @Override
	    public Object getValue(AmiRelaySendEmailRequest0 valued) {
		    return (boolean)((AmiRelaySendEmailRequest0)valued).getIsHtml();
	    }
    
	    @Override
	    public void setValue(AmiRelaySendEmailRequest0 valued, Object value) {
		    valued.setIsHtml((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
		    dest.setIsHtml(source.getIsHtml());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
	        return OH.eq(dest.getIsHtml(),source.getIsHtml());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiRelaySendEmailRequest0 valued) {
		    return valued.getIsHtml();
	    }
    
	    @Override
	    public void setBoolean(AmiRelaySendEmailRequest0 valued, boolean value) {
		    valued.setIsHtml(value);
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
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getIsHtml());
	        
	    }
	    @Override
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getIsHtml());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean isHtml";
	    }
	    @Override
	    public void clear(AmiRelaySendEmailRequest0 valued){
	       valued.setIsHtml(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_isHtml=new VALUED_PARAM_CLASS_isHtml();
  

  
    private static final class VALUED_PARAM_CLASS_password extends AbstractValuedParam<AmiRelaySendEmailRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 24;
	    }
	    
	    @Override
	    public void write(AmiRelaySendEmailRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.base.Password}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelaySendEmailRequest0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.base.Password}");
		    
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
            return 11;
	    }
    
	    @Override
	    public String getName() {
            return "password";
	    }
    
	    @Override
	    public Object getValue(AmiRelaySendEmailRequest0 valued) {
		    return (com.f1.base.Password)((AmiRelaySendEmailRequest0)valued).getPassword();
	    }
    
	    @Override
	    public void setValue(AmiRelaySendEmailRequest0 valued, Object value) {
		    valued.setPassword((com.f1.base.Password)value);
	    }
    
	    @Override
	    public void copy(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
		    dest.setPassword(source.getPassword());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
	        return OH.eq(dest.getPassword(),source.getPassword());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.base.Password.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.base.Password.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPassword());
	        
	    }
	    @Override
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPassword());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.base.Password password";
	    }
	    @Override
	    public void clear(AmiRelaySendEmailRequest0 valued){
	       valued.setPassword(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_password=new VALUED_PARAM_CLASS_password();
  

  
    private static final class VALUED_PARAM_CLASS_sendEmailUid extends AbstractValuedParam<AmiRelaySendEmailRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelaySendEmailRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelaySendEmailRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 8;
	    }
    
	    @Override
	    public String getName() {
            return "sendEmailUid";
	    }
    
	    @Override
	    public Object getValue(AmiRelaySendEmailRequest0 valued) {
		    return (java.lang.String)((AmiRelaySendEmailRequest0)valued).getSendEmailUid();
	    }
    
	    @Override
	    public void setValue(AmiRelaySendEmailRequest0 valued, Object value) {
		    valued.setSendEmailUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
		    dest.setSendEmailUid(source.getSendEmailUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
	        return OH.eq(dest.getSendEmailUid(),source.getSendEmailUid());
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
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getSendEmailUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getSendEmailUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String sendEmailUid";
	    }
	    @Override
	    public void clear(AmiRelaySendEmailRequest0 valued){
	       valued.setSendEmailUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_sendEmailUid=new VALUED_PARAM_CLASS_sendEmailUid();
  

  
    private static final class VALUED_PARAM_CLASS_subject extends AbstractValuedParam<AmiRelaySendEmailRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelaySendEmailRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelaySendEmailRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 2;
	    }
    
	    @Override
	    public String getName() {
            return "subject";
	    }
    
	    @Override
	    public Object getValue(AmiRelaySendEmailRequest0 valued) {
		    return (java.lang.String)((AmiRelaySendEmailRequest0)valued).getSubject();
	    }
    
	    @Override
	    public void setValue(AmiRelaySendEmailRequest0 valued, Object value) {
		    valued.setSubject((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
		    dest.setSubject(source.getSubject());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
	        return OH.eq(dest.getSubject(),source.getSubject());
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
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getSubject(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getSubject(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String subject";
	    }
	    @Override
	    public void clear(AmiRelaySendEmailRequest0 valued){
	       valued.setSubject(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_subject=new VALUED_PARAM_CLASS_subject();
  

  
    private static final class VALUED_PARAM_CLASS_targetAgentProcessUid extends AbstractValuedParam<AmiRelaySendEmailRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelaySendEmailRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelaySendEmailRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 40;
	    }
    
	    @Override
	    public String getName() {
            return "targetAgentProcessUid";
	    }
    
	    @Override
	    public Object getValue(AmiRelaySendEmailRequest0 valued) {
		    return (java.lang.String)((AmiRelaySendEmailRequest0)valued).getTargetAgentProcessUid();
	    }
    
	    @Override
	    public void setValue(AmiRelaySendEmailRequest0 valued, Object value) {
		    valued.setTargetAgentProcessUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
		    dest.setTargetAgentProcessUid(source.getTargetAgentProcessUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
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
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getTargetAgentProcessUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getTargetAgentProcessUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String targetAgentProcessUid";
	    }
	    @Override
	    public void clear(AmiRelaySendEmailRequest0 valued){
	       valued.setTargetAgentProcessUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_targetAgentProcessUid=new VALUED_PARAM_CLASS_targetAgentProcessUid();
  

  
    private static final class VALUED_PARAM_CLASS_timeoutMs extends AbstractValuedParam<AmiRelaySendEmailRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiRelaySendEmailRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getTimeoutMs());
		    
	    }
	    
	    @Override
	    public void read(AmiRelaySendEmailRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 11;
	    }
    
	    @Override
	    public byte getPid() {
            return 9;
	    }
    
	    @Override
	    public String getName() {
            return "timeoutMs";
	    }
    
	    @Override
	    public Object getValue(AmiRelaySendEmailRequest0 valued) {
		    return (int)((AmiRelaySendEmailRequest0)valued).getTimeoutMs();
	    }
    
	    @Override
	    public void setValue(AmiRelaySendEmailRequest0 valued, Object value) {
		    valued.setTimeoutMs((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
		    dest.setTimeoutMs(source.getTimeoutMs());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
	        return OH.eq(dest.getTimeoutMs(),source.getTimeoutMs());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiRelaySendEmailRequest0 valued) {
		    return valued.getTimeoutMs();
	    }
    
	    @Override
	    public void setInt(AmiRelaySendEmailRequest0 valued, int value) {
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
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTimeoutMs());
	        
	    }
	    @Override
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTimeoutMs());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int timeoutMs";
	    }
	    @Override
	    public void clear(AmiRelaySendEmailRequest0 valued){
	       valued.setTimeoutMs(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_timeoutMs=new VALUED_PARAM_CLASS_timeoutMs();
  

  
    private static final class VALUED_PARAM_CLASS_toList extends AbstractValuedParam<AmiRelaySendEmailRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiRelaySendEmailRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelaySendEmailRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 12;
	    }
    
	    @Override
	    public byte getPid() {
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "toList";
	    }
    
	    @Override
	    public Object getValue(AmiRelaySendEmailRequest0 valued) {
		    return (java.util.List)((AmiRelaySendEmailRequest0)valued).getToList();
	    }
    
	    @Override
	    public void setValue(AmiRelaySendEmailRequest0 valued, Object value) {
		    valued.setToList((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
		    dest.setToList(source.getToList());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
	        return OH.eq(dest.getToList(),source.getToList());
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
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getToList());
	        
	    }
	    @Override
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getToList());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List toList";
	    }
	    @Override
	    public void clear(AmiRelaySendEmailRequest0 valued){
	       valued.setToList(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_toList=new VALUED_PARAM_CLASS_toList();
  

  
    private static final class VALUED_PARAM_CLASS_username extends AbstractValuedParam<AmiRelaySendEmailRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelaySendEmailRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelaySendEmailRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 13;
	    }
    
	    @Override
	    public byte getPid() {
            return 10;
	    }
    
	    @Override
	    public String getName() {
            return "username";
	    }
    
	    @Override
	    public Object getValue(AmiRelaySendEmailRequest0 valued) {
		    return (java.lang.String)((AmiRelaySendEmailRequest0)valued).getUsername();
	    }
    
	    @Override
	    public void setValue(AmiRelaySendEmailRequest0 valued, Object value) {
		    valued.setUsername((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
		    dest.setUsername(source.getUsername());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelaySendEmailRequest0 source, AmiRelaySendEmailRequest0 dest) {
	        return OH.eq(dest.getUsername(),source.getUsername());
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
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getUsername(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelaySendEmailRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getUsername(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String username";
	    }
	    @Override
	    public void clear(AmiRelaySendEmailRequest0 valued){
	       valued.setUsername(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_username=new VALUED_PARAM_CLASS_username();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_attachmentDatas, VALUED_PARAM_attachmentNames, VALUED_PARAM_body, VALUED_PARAM_centerId, VALUED_PARAM_from, VALUED_PARAM_invokedBy, VALUED_PARAM_isHtml, VALUED_PARAM_password, VALUED_PARAM_sendEmailUid, VALUED_PARAM_subject, VALUED_PARAM_targetAgentProcessUid, VALUED_PARAM_timeoutMs, VALUED_PARAM_toList, VALUED_PARAM_username, };



    private static final byte PIDS[]={ 7 ,6,1,42,4,41,5,11,8,2,40,9,3,10};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 7: return VALUED_PARAM_attachmentDatas;
             case 6: return VALUED_PARAM_attachmentNames;
             case 1: return VALUED_PARAM_body;
             case 42: return VALUED_PARAM_centerId;
             case 4: return VALUED_PARAM_from;
             case 41: return VALUED_PARAM_invokedBy;
             case 5: return VALUED_PARAM_isHtml;
             case 11: return VALUED_PARAM_password;
             case 8: return VALUED_PARAM_sendEmailUid;
             case 2: return VALUED_PARAM_subject;
             case 40: return VALUED_PARAM_targetAgentProcessUid;
             case 9: return VALUED_PARAM_timeoutMs;
             case 3: return VALUED_PARAM_toList;
             case 10: return VALUED_PARAM_username;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 7: return true;
             case 6: return true;
             case 1: return true;
             case 42: return true;
             case 4: return true;
             case 41: return true;
             case 5: return true;
             case 11: return true;
             case 8: return true;
             case 2: return true;
             case 40: return true;
             case 9: return true;
             case 3: return true;
             case 10: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 7: return "attachmentDatas";
             case 6: return "attachmentNames";
             case 1: return "body";
             case 42: return "centerId";
             case 4: return "from";
             case 41: return "invokedBy";
             case 5: return "isHtml";
             case 11: return "password";
             case 8: return "sendEmailUid";
             case 2: return "subject";
             case 40: return "targetAgentProcessUid";
             case 9: return "timeoutMs";
             case 3: return "toList";
             case 10: return "username";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 7: return 0;
             case 6: return 1;
             case 1: return 2;
             case 42: return 3;
             case 4: return 4;
             case 41: return 5;
             case 5: return 6;
             case 11: return 7;
             case 8: return 8;
             case 2: return 9;
             case 40: return 10;
             case 9: return 11;
             case 3: return 12;
             case 10: return 13;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="attachmentDatas") return 7;
             if(name=="attachmentNames") return 6;
             if(name=="body") return 1;
             if(name=="centerId") return 42;
             if(name=="from") return 4;
             if(name=="invokedBy") return 41;
             if(name=="isHtml") return 5;
             if(name=="password") return 11;
             if(name=="sendEmailUid") return 8;
             if(name=="subject") return 2;
             if(name=="targetAgentProcessUid") return 40;
             if(name=="timeoutMs") return 9;
             if(name=="toList") return 3;
             if(name=="username") return 10;
            
             if("attachmentDatas".equals(name)) return 7;
             if("attachmentNames".equals(name)) return 6;
             if("body".equals(name)) return 1;
             if("centerId".equals(name)) return 42;
             if("from".equals(name)) return 4;
             if("invokedBy".equals(name)) return 41;
             if("isHtml".equals(name)) return 5;
             if("password".equals(name)) return 11;
             if("sendEmailUid".equals(name)) return 8;
             if("subject".equals(name)) return 2;
             if("targetAgentProcessUid".equals(name)) return 40;
             if("timeoutMs".equals(name)) return 9;
             if("toList".equals(name)) return 3;
             if("username".equals(name)) return 10;
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
             case 7: return  this._attachmentDatas; 
             case 6: return  this._attachmentNames; 
             case 1: return  this._body; 
             case 42: return  OH.valueOf(this._centerId); 
             case 4: return  this._from; 
             case 41: return  this._invokedBy; 
             case 5: return  OH.valueOf(this._isHtml); 
             case 11: return  this._password; 
             case 8: return  this._sendEmailUid; 
             case 2: return  this._subject; 
             case 40: return  this._targetAgentProcessUid; 
             case 9: return  OH.valueOf(this._timeoutMs); 
             case 3: return  this._toList; 
             case 10: return  this._username; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 7: return java.util.List.class;
             case 6: return java.util.List.class;
             case 1: return java.lang.String.class;
             case 42: return byte.class;
             case 4: return java.lang.String.class;
             case 41: return java.lang.String.class;
             case 5: return boolean.class;
             case 11: return com.f1.base.Password.class;
             case 8: return java.lang.String.class;
             case 2: return java.lang.String.class;
             case 40: return java.lang.String.class;
             case 9: return int.class;
             case 3: return java.util.List.class;
             case 10: return java.lang.String.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 7: return 21;
             case 6: return 21;
             case 1: return 20;
             case 42: return 1;
             case 4: return 20;
             case 41: return 20;
             case 5: return 0;
             case 11: return 24;
             case 8: return 20;
             case 2: return 20;
             case 40: return 20;
             case 9: return 4;
             case 3: return 21;
             case 10: return 20;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 7: this._attachmentDatas=(java.util.List)value;return;
             case 6: this._attachmentNames=(java.util.List)value;return;
             case 1: this._body=(java.lang.String)value;return;
             case 42: this._centerId=(java.lang.Byte)value;return;
             case 4: this._from=(java.lang.String)value;return;
             case 41: this._invokedBy=(java.lang.String)value;return;
             case 5: this._isHtml=(java.lang.Boolean)value;return;
             case 11: this._password=(com.f1.base.Password)value;return;
             case 8: this._sendEmailUid=(java.lang.String)value;return;
             case 2: this._subject=(java.lang.String)value;return;
             case 40: this._targetAgentProcessUid=(java.lang.String)value;return;
             case 9: this._timeoutMs=(java.lang.Integer)value;return;
             case 3: this._toList=(java.util.List)value;return;
             case 10: this._username=(java.lang.String)value;return;
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
             case 7: this._attachmentDatas=(java.util.List)value;return true;
             case 6: this._attachmentNames=(java.util.List)value;return true;
             case 1: this._body=(java.lang.String)value;return true;
             case 42: this._centerId=(java.lang.Byte)value;return true;
             case 4: this._from=(java.lang.String)value;return true;
             case 41: this._invokedBy=(java.lang.String)value;return true;
             case 5: this._isHtml=(java.lang.Boolean)value;return true;
             case 11: this._password=(com.f1.base.Password)value;return true;
             case 8: this._sendEmailUid=(java.lang.String)value;return true;
             case 2: this._subject=(java.lang.String)value;return true;
             case 40: this._targetAgentProcessUid=(java.lang.String)value;return true;
             case 9: this._timeoutMs=(java.lang.Integer)value;return true;
             case 3: this._toList=(java.util.List)value;return true;
             case 10: this._username=(java.lang.String)value;return true;
            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
             case 5: return this._isHtml;
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
             case 9: return this._timeoutMs;
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
             case 5: this._isHtml=value;return;
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
             case 9: this._timeoutMs=value;return;
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
        
            this._body=(java.lang.String)converter.read(session);
        
            break;

        case 2:
        
            this._subject=(java.lang.String)converter.read(session);
        
            break;

        case 3:
        
            this._toList=(java.util.List)converter.read(session);
        
            break;

        case 4:
        
            this._from=(java.lang.String)converter.read(session);
        
            break;

        case 5:
        
            if((basicType=in.readByte())!=0)
                break;
            this._isHtml=in.readBoolean();
        
            break;

        case 6:
        
            this._attachmentNames=(java.util.List)converter.read(session);
        
            break;

        case 7:
        
            this._attachmentDatas=(java.util.List)converter.read(session);
        
            break;

        case 8:
        
            this._sendEmailUid=(java.lang.String)converter.read(session);
        
            break;

        case 9:
        
            if((basicType=in.readByte())!=4)
                break;
            this._timeoutMs=in.readInt();
        
            break;

        case 10:
        
            this._username=(java.lang.String)converter.read(session);
        
            break;

        case 11:
        
            this._password=(com.f1.base.Password)converter.read(session);
        
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
        
if(this._body!=null && (0 & transience)==0){
    out.writeByte(1);
        
    converter.write(this._body,session);
        
}

if(this._subject!=null && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this._subject,session);
        
}

if(this._toList!=null && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this._toList,session);
        
}

if(this._from!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._from,session);
        
}

if(this._isHtml!=false && (0 & transience)==0){
    out.writeByte(5);
        
    out.writeByte(0);
    out.writeBoolean(this._isHtml);
        
}

if(this._attachmentNames!=null && (0 & transience)==0){
    out.writeByte(6);
        
    converter.write(this._attachmentNames,session);
        
}

if(this._attachmentDatas!=null && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this._attachmentDatas,session);
        
}

if(this._sendEmailUid!=null && (0 & transience)==0){
    out.writeByte(8);
        
    converter.write(this._sendEmailUid,session);
        
}

if(this._timeoutMs!=0 && (0 & transience)==0){
    out.writeByte(9);
        
    out.writeByte(4);
    out.writeInt(this._timeoutMs);
        
}

if(this._username!=null && (0 & transience)==0){
    out.writeByte(10);
        
    converter.write(this._username,session);
        
}

if(this._password!=null && (0 & transience)==0){
    out.writeByte(11);
        
    converter.write(this._password,session);
        
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