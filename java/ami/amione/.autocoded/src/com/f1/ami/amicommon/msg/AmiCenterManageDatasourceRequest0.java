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

public abstract class AmiCenterManageDatasourceRequest0 implements com.f1.ami.amicommon.msg.AmiCenterManageDatasourceRequest ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private java.lang.String _adapter;

    private java.lang.String _comment;

    private boolean _delete;

    private boolean _edit;

    private long _id;

    private java.lang.String _invokedBy;

    private java.lang.String _name;

    private java.lang.String _options;

    private java.lang.String _password;

    private java.lang.String _permittedOverrides;

    private int _priority;

    private java.lang.String _relayId;

    private long _requestTime;

    private java.lang.String _selectedName;

    private boolean _skipTest;

    private java.lang.String _url;

    private java.lang.String _username;

    private static final String NAMES[]={ "adapter" ,"comment","delete","edit","id","invokedBy","name","options","password","permittedOverrides","priority","relayId","requestTime","selectedName","skipTest","url","username"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 77;
        try{
        switch(h){

                case 2:

                    if(name == "username" || name.equals("username")) {this._username=(java.lang.String)value;return;}
break;
                case 4:

                    if(name == "adapter" || name.equals("adapter")) {this._adapter=(java.lang.String)value;return;}
break;
                case 7:

                    if(name == "selectedName" || name.equals("selectedName")) {this._selectedName=(java.lang.String)value;return;}
break;
                case 12:

                    if(name == "relayId" || name.equals("relayId")) {this._relayId=(java.lang.String)value;return;}
break;
                case 18:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return;}
break;
                case 20:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return;}
break;
                case 26:

                    if(name == "edit" || name.equals("edit")) {this._edit=(java.lang.Boolean)value;return;}
break;
                case 29:

                    if(name == "name" || name.equals("name")) {this._name=(java.lang.String)value;return;}
break;
                case 33:

                    if(name == "comment" || name.equals("comment")) {this._comment=(java.lang.String)value;return;}
break;
                case 34:

                    if(name == "delete" || name.equals("delete")) {this._delete=(java.lang.Boolean)value;return;}
break;
                case 40:

                    if(name == "url" || name.equals("url")) {this._url=(java.lang.String)value;return;}
break;
                case 44:

                    if(name == "id" || name.equals("id")) {this._id=(java.lang.Long)value;return;}
break;
                case 46:

                    if(name == "permittedOverrides" || name.equals("permittedOverrides")) {this._permittedOverrides=(java.lang.String)value;return;}
break;
                case 61:

                    if(name == "requestTime" || name.equals("requestTime")) {this._requestTime=(java.lang.Long)value;return;}
break;
                case 62:

                    if(name == "password" || name.equals("password")) {this._password=(java.lang.String)value;return;}
break;
                case 67:

                    if(name == "skipTest" || name.equals("skipTest")) {this._skipTest=(java.lang.Boolean)value;return;}
break;
                case 72:

                    if(name == "options" || name.equals("options")) {this._options=(java.lang.String)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 77;
        switch(h){

                case 2:

                    if(name == "username" || name.equals("username")) {this._username=(java.lang.String)value;return true;}
break;
                case 4:

                    if(name == "adapter" || name.equals("adapter")) {this._adapter=(java.lang.String)value;return true;}
break;
                case 7:

                    if(name == "selectedName" || name.equals("selectedName")) {this._selectedName=(java.lang.String)value;return true;}
break;
                case 12:

                    if(name == "relayId" || name.equals("relayId")) {this._relayId=(java.lang.String)value;return true;}
break;
                case 18:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return true;}
break;
                case 20:

                    if(name == "invokedBy" || name.equals("invokedBy")) {this._invokedBy=(java.lang.String)value;return true;}
break;
                case 26:

                    if(name == "edit" || name.equals("edit")) {this._edit=(java.lang.Boolean)value;return true;}
break;
                case 29:

                    if(name == "name" || name.equals("name")) {this._name=(java.lang.String)value;return true;}
break;
                case 33:

                    if(name == "comment" || name.equals("comment")) {this._comment=(java.lang.String)value;return true;}
break;
                case 34:

                    if(name == "delete" || name.equals("delete")) {this._delete=(java.lang.Boolean)value;return true;}
break;
                case 40:

                    if(name == "url" || name.equals("url")) {this._url=(java.lang.String)value;return true;}
break;
                case 44:

                    if(name == "id" || name.equals("id")) {this._id=(java.lang.Long)value;return true;}
break;
                case 46:

                    if(name == "permittedOverrides" || name.equals("permittedOverrides")) {this._permittedOverrides=(java.lang.String)value;return true;}
break;
                case 61:

                    if(name == "requestTime" || name.equals("requestTime")) {this._requestTime=(java.lang.Long)value;return true;}
break;
                case 62:

                    if(name == "password" || name.equals("password")) {this._password=(java.lang.String)value;return true;}
break;
                case 67:

                    if(name == "skipTest" || name.equals("skipTest")) {this._skipTest=(java.lang.Boolean)value;return true;}
break;
                case 72:

                    if(name == "options" || name.equals("options")) {this._options=(java.lang.String)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 77;
        switch(h){

                case 2:

		    
                    if(name == "username" || name.equals("username")) {return this._username;}
            
break;
                case 4:

		    
                    if(name == "adapter" || name.equals("adapter")) {return this._adapter;}
            
break;
                case 7:

		    
                    if(name == "selectedName" || name.equals("selectedName")) {return this._selectedName;}
            
break;
                case 12:

		    
                    if(name == "relayId" || name.equals("relayId")) {return this._relayId;}
            
break;
                case 18:

		    
                    if(name == "priority" || name.equals("priority")) {return OH.valueOf(this._priority);}
		    
break;
                case 20:

		    
                    if(name == "invokedBy" || name.equals("invokedBy")) {return this._invokedBy;}
            
break;
                case 26:

		    
                    if(name == "edit" || name.equals("edit")) {return OH.valueOf(this._edit);}
		    
break;
                case 29:

		    
                    if(name == "name" || name.equals("name")) {return this._name;}
            
break;
                case 33:

		    
                    if(name == "comment" || name.equals("comment")) {return this._comment;}
            
break;
                case 34:

		    
                    if(name == "delete" || name.equals("delete")) {return OH.valueOf(this._delete);}
		    
break;
                case 40:

		    
                    if(name == "url" || name.equals("url")) {return this._url;}
            
break;
                case 44:

		    
                    if(name == "id" || name.equals("id")) {return OH.valueOf(this._id);}
		    
break;
                case 46:

		    
                    if(name == "permittedOverrides" || name.equals("permittedOverrides")) {return this._permittedOverrides;}
            
break;
                case 61:

		    
                    if(name == "requestTime" || name.equals("requestTime")) {return OH.valueOf(this._requestTime);}
		    
break;
                case 62:

		    
                    if(name == "password" || name.equals("password")) {return this._password;}
            
break;
                case 67:

		    
                    if(name == "skipTest" || name.equals("skipTest")) {return OH.valueOf(this._skipTest);}
		    
break;
                case 72:

		    
                    if(name == "options" || name.equals("options")) {return this._options;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 77;
        switch(h){

                case 2:

                    if(name == "username" || name.equals("username")) {return java.lang.String.class;}
break;
                case 4:

                    if(name == "adapter" || name.equals("adapter")) {return java.lang.String.class;}
break;
                case 7:

                    if(name == "selectedName" || name.equals("selectedName")) {return java.lang.String.class;}
break;
                case 12:

                    if(name == "relayId" || name.equals("relayId")) {return java.lang.String.class;}
break;
                case 18:

                    if(name == "priority" || name.equals("priority")) {return int.class;}
break;
                case 20:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return java.lang.String.class;}
break;
                case 26:

                    if(name == "edit" || name.equals("edit")) {return boolean.class;}
break;
                case 29:

                    if(name == "name" || name.equals("name")) {return java.lang.String.class;}
break;
                case 33:

                    if(name == "comment" || name.equals("comment")) {return java.lang.String.class;}
break;
                case 34:

                    if(name == "delete" || name.equals("delete")) {return boolean.class;}
break;
                case 40:

                    if(name == "url" || name.equals("url")) {return java.lang.String.class;}
break;
                case 44:

                    if(name == "id" || name.equals("id")) {return long.class;}
break;
                case 46:

                    if(name == "permittedOverrides" || name.equals("permittedOverrides")) {return java.lang.String.class;}
break;
                case 61:

                    if(name == "requestTime" || name.equals("requestTime")) {return long.class;}
break;
                case 62:

                    if(name == "password" || name.equals("password")) {return java.lang.String.class;}
break;
                case 67:

                    if(name == "skipTest" || name.equals("skipTest")) {return boolean.class;}
break;
                case 72:

                    if(name == "options" || name.equals("options")) {return java.lang.String.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 77;
        switch(h){

                case 2:

                    if(name == "username" || name.equals("username")) {return VALUED_PARAM_username;}
break;
                case 4:

                    if(name == "adapter" || name.equals("adapter")) {return VALUED_PARAM_adapter;}
break;
                case 7:

                    if(name == "selectedName" || name.equals("selectedName")) {return VALUED_PARAM_selectedName;}
break;
                case 12:

                    if(name == "relayId" || name.equals("relayId")) {return VALUED_PARAM_relayId;}
break;
                case 18:

                    if(name == "priority" || name.equals("priority")) {return VALUED_PARAM_priority;}
break;
                case 20:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return VALUED_PARAM_invokedBy;}
break;
                case 26:

                    if(name == "edit" || name.equals("edit")) {return VALUED_PARAM_edit;}
break;
                case 29:

                    if(name == "name" || name.equals("name")) {return VALUED_PARAM_name;}
break;
                case 33:

                    if(name == "comment" || name.equals("comment")) {return VALUED_PARAM_comment;}
break;
                case 34:

                    if(name == "delete" || name.equals("delete")) {return VALUED_PARAM_delete;}
break;
                case 40:

                    if(name == "url" || name.equals("url")) {return VALUED_PARAM_url;}
break;
                case 44:

                    if(name == "id" || name.equals("id")) {return VALUED_PARAM_id;}
break;
                case 46:

                    if(name == "permittedOverrides" || name.equals("permittedOverrides")) {return VALUED_PARAM_permittedOverrides;}
break;
                case 61:

                    if(name == "requestTime" || name.equals("requestTime")) {return VALUED_PARAM_requestTime;}
break;
                case 62:

                    if(name == "password" || name.equals("password")) {return VALUED_PARAM_password;}
break;
                case 67:

                    if(name == "skipTest" || name.equals("skipTest")) {return VALUED_PARAM_skipTest;}
break;
                case 72:

                    if(name == "options" || name.equals("options")) {return VALUED_PARAM_options;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 77;
        switch(h){

                case 2:

                    if(name == "username" || name.equals("username")) {return 16;}
break;
                case 4:

                    if(name == "adapter" || name.equals("adapter")) {return 0;}
break;
                case 7:

                    if(name == "selectedName" || name.equals("selectedName")) {return 13;}
break;
                case 12:

                    if(name == "relayId" || name.equals("relayId")) {return 11;}
break;
                case 18:

                    if(name == "priority" || name.equals("priority")) {return 10;}
break;
                case 20:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 5;}
break;
                case 26:

                    if(name == "edit" || name.equals("edit")) {return 3;}
break;
                case 29:

                    if(name == "name" || name.equals("name")) {return 6;}
break;
                case 33:

                    if(name == "comment" || name.equals("comment")) {return 1;}
break;
                case 34:

                    if(name == "delete" || name.equals("delete")) {return 2;}
break;
                case 40:

                    if(name == "url" || name.equals("url")) {return 15;}
break;
                case 44:

                    if(name == "id" || name.equals("id")) {return 4;}
break;
                case 46:

                    if(name == "permittedOverrides" || name.equals("permittedOverrides")) {return 9;}
break;
                case 61:

                    if(name == "requestTime" || name.equals("requestTime")) {return 12;}
break;
                case 62:

                    if(name == "password" || name.equals("password")) {return 8;}
break;
                case 67:

                    if(name == "skipTest" || name.equals("skipTest")) {return 14;}
break;
                case 72:

                    if(name == "options" || name.equals("options")) {return 7;}
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
        return 17;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiCenterManageDatasourceRequest0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 77;
        switch(h){

                case 2:

                    if(name == "username" || name.equals("username")) {return true;}
break;
                case 4:

                    if(name == "adapter" || name.equals("adapter")) {return true;}
break;
                case 7:

                    if(name == "selectedName" || name.equals("selectedName")) {return true;}
break;
                case 12:

                    if(name == "relayId" || name.equals("relayId")) {return true;}
break;
                case 18:

                    if(name == "priority" || name.equals("priority")) {return true;}
break;
                case 20:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return true;}
break;
                case 26:

                    if(name == "edit" || name.equals("edit")) {return true;}
break;
                case 29:

                    if(name == "name" || name.equals("name")) {return true;}
break;
                case 33:

                    if(name == "comment" || name.equals("comment")) {return true;}
break;
                case 34:

                    if(name == "delete" || name.equals("delete")) {return true;}
break;
                case 40:

                    if(name == "url" || name.equals("url")) {return true;}
break;
                case 44:

                    if(name == "id" || name.equals("id")) {return true;}
break;
                case 46:

                    if(name == "permittedOverrides" || name.equals("permittedOverrides")) {return true;}
break;
                case 61:

                    if(name == "requestTime" || name.equals("requestTime")) {return true;}
break;
                case 62:

                    if(name == "password" || name.equals("password")) {return true;}
break;
                case 67:

                    if(name == "skipTest" || name.equals("skipTest")) {return true;}
break;
                case 72:

                    if(name == "options" || name.equals("options")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 77;
        switch(h){

                case 2:

                    if(name == "username" || name.equals("username")) {return 20;}
break;
                case 4:

                    if(name == "adapter" || name.equals("adapter")) {return 20;}
break;
                case 7:

                    if(name == "selectedName" || name.equals("selectedName")) {return 20;}
break;
                case 12:

                    if(name == "relayId" || name.equals("relayId")) {return 20;}
break;
                case 18:

                    if(name == "priority" || name.equals("priority")) {return 4;}
break;
                case 20:

                    if(name == "invokedBy" || name.equals("invokedBy")) {return 20;}
break;
                case 26:

                    if(name == "edit" || name.equals("edit")) {return 0;}
break;
                case 29:

                    if(name == "name" || name.equals("name")) {return 20;}
break;
                case 33:

                    if(name == "comment" || name.equals("comment")) {return 20;}
break;
                case 34:

                    if(name == "delete" || name.equals("delete")) {return 0;}
break;
                case 40:

                    if(name == "url" || name.equals("url")) {return 20;}
break;
                case 44:

                    if(name == "id" || name.equals("id")) {return 6;}
break;
                case 46:

                    if(name == "permittedOverrides" || name.equals("permittedOverrides")) {return 20;}
break;
                case 61:

                    if(name == "requestTime" || name.equals("requestTime")) {return 6;}
break;
                case 62:

                    if(name == "password" || name.equals("password")) {return 20;}
break;
                case 67:

                    if(name == "skipTest" || name.equals("skipTest")) {return 0;}
break;
                case 72:

                    if(name == "options" || name.equals("options")) {return 20;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _adapter;

        case 1:return _comment;

        case 2:return _delete;

        case 3:return _edit;

        case 4:return _id;

        case 5:return _invokedBy;

        case 6:return _name;

        case 7:return _options;

        case 8:return _password;

        case 9:return _permittedOverrides;

        case 10:return _priority;

        case 11:return _relayId;

        case 12:return _requestTime;

        case 13:return _selectedName;

        case 14:return _skipTest;

        case 15:return _url;

        case 16:return _username;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 17);
    }

    public java.lang.String getAdapter(){
        return this._adapter;
    }
    public void setAdapter(java.lang.String _adapter){
    
        this._adapter=_adapter;
    }

    public java.lang.String getComment(){
        return this._comment;
    }
    public void setComment(java.lang.String _comment){
    
        this._comment=_comment;
    }

    public boolean getDelete(){
        return this._delete;
    }
    public void setDelete(boolean _delete){
    
        this._delete=_delete;
    }

    public boolean getEdit(){
        return this._edit;
    }
    public void setEdit(boolean _edit){
    
        this._edit=_edit;
    }

    public long getId(){
        return this._id;
    }
    public void setId(long _id){
    
        this._id=_id;
    }

    public java.lang.String getInvokedBy(){
        return this._invokedBy;
    }
    public void setInvokedBy(java.lang.String _invokedBy){
    
        this._invokedBy=_invokedBy;
    }

    public java.lang.String getName(){
        return this._name;
    }
    public void setName(java.lang.String _name){
    
        this._name=_name;
    }

    public java.lang.String getOptions(){
        return this._options;
    }
    public void setOptions(java.lang.String _options){
    
        this._options=_options;
    }

    public java.lang.String getPassword(){
        return this._password;
    }
    public void setPassword(java.lang.String _password){
    
        this._password=_password;
    }

    public java.lang.String getPermittedOverrides(){
        return this._permittedOverrides;
    }
    public void setPermittedOverrides(java.lang.String _permittedOverrides){
    
        this._permittedOverrides=_permittedOverrides;
    }

    public int getPriority(){
        return this._priority;
    }
    public void setPriority(int _priority){
    
        this._priority=_priority;
    }

    public java.lang.String getRelayId(){
        return this._relayId;
    }
    public void setRelayId(java.lang.String _relayId){
    
        this._relayId=_relayId;
    }

    public long getRequestTime(){
        return this._requestTime;
    }
    public void setRequestTime(long _requestTime){
    
        this._requestTime=_requestTime;
    }

    public java.lang.String getSelectedName(){
        return this._selectedName;
    }
    public void setSelectedName(java.lang.String _selectedName){
    
        this._selectedName=_selectedName;
    }

    public boolean getSkipTest(){
        return this._skipTest;
    }
    public void setSkipTest(boolean _skipTest){
    
        this._skipTest=_skipTest;
    }

    public java.lang.String getUrl(){
        return this._url;
    }
    public void setUrl(java.lang.String _url){
    
        this._url=_url;
    }

    public java.lang.String getUsername(){
        return this._username;
    }
    public void setUsername(java.lang.String _username){
    
        this._username=_username;
    }





  
    private static final class VALUED_PARAM_CLASS_adapter extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 3;
	    }
    
	    @Override
	    public String getName() {
            return "adapter";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceRequest0)valued).getAdapter();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setAdapter((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setAdapter(source.getAdapter());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
	        return OH.eq(dest.getAdapter(),source.getAdapter());
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getAdapter(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getAdapter(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String adapter";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setAdapter(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_adapter=new VALUED_PARAM_CLASS_adapter();
  

  
    private static final class VALUED_PARAM_CLASS_comment extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 1;
	    }
    
	    @Override
	    public byte getPid() {
            return 52;
	    }
    
	    @Override
	    public String getName() {
            return "comment";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceRequest0)valued).getComment();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setComment((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setComment(source.getComment());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
	        return OH.eq(dest.getComment(),source.getComment());
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getComment(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getComment(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String comment";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setComment(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_comment=new VALUED_PARAM_CLASS_comment();
  

  
    private static final class VALUED_PARAM_CLASS_delete extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getDelete());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setDelete(stream.readBoolean());
		    
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
            return 8;
	    }
    
	    @Override
	    public String getName() {
            return "delete";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (boolean)((AmiCenterManageDatasourceRequest0)valued).getDelete();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setDelete((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setDelete(source.getDelete());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
	        return OH.eq(dest.getDelete(),source.getDelete());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterManageDatasourceRequest0 valued) {
		    return valued.getDelete();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterManageDatasourceRequest0 valued, boolean value) {
		    valued.setDelete(value);
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getDelete());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getDelete());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean delete";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setDelete(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_delete=new VALUED_PARAM_CLASS_delete();
  

  
    private static final class VALUED_PARAM_CLASS_edit extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getEdit());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setEdit(stream.readBoolean());
		    
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
            return 9;
	    }
    
	    @Override
	    public String getName() {
            return "edit";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (boolean)((AmiCenterManageDatasourceRequest0)valued).getEdit();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setEdit((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setEdit(source.getEdit());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
	        return OH.eq(dest.getEdit(),source.getEdit());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterManageDatasourceRequest0 valued) {
		    return valued.getEdit();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterManageDatasourceRequest0 valued, boolean value) {
		    valued.setEdit(value);
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getEdit());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getEdit());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean edit";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setEdit(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_edit=new VALUED_PARAM_CLASS_edit();
  

  
    private static final class VALUED_PARAM_CLASS_id extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getId());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setId(stream.readLong());
		    
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
            return 1;
	    }
    
	    @Override
	    public String getName() {
            return "id";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (long)((AmiCenterManageDatasourceRequest0)valued).getId();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setId((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setId(source.getId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
	        return OH.eq(dest.getId(),source.getId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterManageDatasourceRequest0 valued) {
		    return valued.getId();
	    }
    
	    @Override
	    public void setLong(AmiCenterManageDatasourceRequest0 valued, long value) {
		    valued.setId(value);
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getId());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long id";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setId(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_id=new VALUED_PARAM_CLASS_id();
  

  
    private static final class VALUED_PARAM_CLASS_invokedBy extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 51;
	    }
    
	    @Override
	    public String getName() {
            return "invokedBy";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceRequest0)valued).getInvokedBy();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setInvokedBy((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setInvokedBy(source.getInvokedBy());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getInvokedBy(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String invokedBy";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setInvokedBy(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_invokedBy=new VALUED_PARAM_CLASS_invokedBy();
  

  
    private static final class VALUED_PARAM_CLASS_name extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
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
            return "name";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceRequest0)valued).getName();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setName(source.getName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getName(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String name";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_name=new VALUED_PARAM_CLASS_name();
  

  
    private static final class VALUED_PARAM_CLASS_options extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "options";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceRequest0)valued).getOptions();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setOptions((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setOptions(source.getOptions());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getOptions(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getOptions(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String options";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setOptions(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_options=new VALUED_PARAM_CLASS_options();
  

  
    private static final class VALUED_PARAM_CLASS_password extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 6;
	    }
    
	    @Override
	    public String getName() {
            return "password";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceRequest0)valued).getPassword();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setPassword((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setPassword(source.getPassword());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
	        return OH.eq(dest.getPassword(),source.getPassword());
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getPassword(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getPassword(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String password";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setPassword(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_password=new VALUED_PARAM_CLASS_password();
  

  
    private static final class VALUED_PARAM_CLASS_permittedOverrides extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 12;
	    }
    
	    @Override
	    public String getName() {
            return "permittedOverrides";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceRequest0)valued).getPermittedOverrides();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setPermittedOverrides((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setPermittedOverrides(source.getPermittedOverrides());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
	        return OH.eq(dest.getPermittedOverrides(),source.getPermittedOverrides());
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getPermittedOverrides(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getPermittedOverrides(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String permittedOverrides";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setPermittedOverrides(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_permittedOverrides=new VALUED_PARAM_CLASS_permittedOverrides();
  

  
    private static final class VALUED_PARAM_CLASS_priority extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getPriority());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setPriority(stream.readInt());
		    
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
		    return 10;
	    }
    
	    @Override
	    public byte getPid() {
            return 53;
	    }
    
	    @Override
	    public String getName() {
            return "priority";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (int)((AmiCenterManageDatasourceRequest0)valued).getPriority();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setPriority((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setPriority(source.getPriority());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
	        return OH.eq(dest.getPriority(),source.getPriority());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterManageDatasourceRequest0 valued) {
		    return valued.getPriority();
	    }
    
	    @Override
	    public void setInt(AmiCenterManageDatasourceRequest0 valued, int value) {
		    valued.setPriority(value);
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int priority";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setPriority(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_priority=new VALUED_PARAM_CLASS_priority();
  

  
    private static final class VALUED_PARAM_CLASS_relayId extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
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
            return 11;
	    }
    
	    @Override
	    public String getName() {
            return "relayId";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceRequest0)valued).getRelayId();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setRelayId((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setRelayId(source.getRelayId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
	        return OH.eq(dest.getRelayId(),source.getRelayId());
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getRelayId(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getRelayId(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String relayId";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setRelayId(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_relayId=new VALUED_PARAM_CLASS_relayId();
  

  
    private static final class VALUED_PARAM_CLASS_requestTime extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getRequestTime());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setRequestTime(stream.readLong());
		    
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
            return 54;
	    }
    
	    @Override
	    public String getName() {
            return "requestTime";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (long)((AmiCenterManageDatasourceRequest0)valued).getRequestTime();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setRequestTime((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setRequestTime(source.getRequestTime());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
	        return OH.eq(dest.getRequestTime(),source.getRequestTime());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterManageDatasourceRequest0 valued) {
		    return valued.getRequestTime();
	    }
    
	    @Override
	    public void setLong(AmiCenterManageDatasourceRequest0 valued, long value) {
		    valued.setRequestTime(value);
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getRequestTime());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getRequestTime());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long requestTime";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setRequestTime(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_requestTime=new VALUED_PARAM_CLASS_requestTime();
  

  
    private static final class VALUED_PARAM_CLASS_selectedName extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
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
            return "selectedName";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceRequest0)valued).getSelectedName();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setSelectedName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setSelectedName(source.getSelectedName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
	        return OH.eq(dest.getSelectedName(),source.getSelectedName());
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getSelectedName(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getSelectedName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String selectedName";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setSelectedName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_selectedName=new VALUED_PARAM_CLASS_selectedName();
  

  
    private static final class VALUED_PARAM_CLASS_skipTest extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getSkipTest());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
		      valued.setSkipTest(stream.readBoolean());
		    
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
		    return 14;
	    }
    
	    @Override
	    public byte getPid() {
            return 13;
	    }
    
	    @Override
	    public String getName() {
            return "skipTest";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (boolean)((AmiCenterManageDatasourceRequest0)valued).getSkipTest();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setSkipTest((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setSkipTest(source.getSkipTest());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
	        return OH.eq(dest.getSkipTest(),source.getSkipTest());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterManageDatasourceRequest0 valued) {
		    return valued.getSkipTest();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterManageDatasourceRequest0 valued, boolean value) {
		    valued.setSkipTest(value);
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	        sb.append(valued.getSkipTest());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	        sb.append(valued.getSkipTest());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean skipTest";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setSkipTest(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_skipTest=new VALUED_PARAM_CLASS_skipTest();
  

  
    private static final class VALUED_PARAM_CLASS_url extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 15;
	    }
    
	    @Override
	    public byte getPid() {
            return 4;
	    }
    
	    @Override
	    public String getName() {
            return "url";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceRequest0)valued).getUrl();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setUrl((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setUrl(source.getUrl());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
	        return OH.eq(dest.getUrl(),source.getUrl());
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getUrl(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getUrl(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String url";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setUrl(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_url=new VALUED_PARAM_CLASS_url();
  

  
    private static final class VALUED_PARAM_CLASS_username extends AbstractValuedParam<AmiCenterManageDatasourceRequest0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceRequest0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceRequest0 valued, DataInput stream) throws IOException{
		    
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
		    return 16;
	    }
    
	    @Override
	    public byte getPid() {
            return 5;
	    }
    
	    @Override
	    public String getName() {
            return "username";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceRequest0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceRequest0)valued).getUsername();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceRequest0 valued, Object value) {
		    valued.setUsername((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
		    dest.setUsername(source.getUsername());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceRequest0 source, AmiCenterManageDatasourceRequest0 dest) {
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
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getUsername(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceRequest0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getUsername(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String username";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceRequest0 valued){
	       valued.setUsername(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_username=new VALUED_PARAM_CLASS_username();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_adapter, VALUED_PARAM_comment, VALUED_PARAM_delete, VALUED_PARAM_edit, VALUED_PARAM_id, VALUED_PARAM_invokedBy, VALUED_PARAM_name, VALUED_PARAM_options, VALUED_PARAM_password, VALUED_PARAM_permittedOverrides, VALUED_PARAM_priority, VALUED_PARAM_relayId, VALUED_PARAM_requestTime, VALUED_PARAM_selectedName, VALUED_PARAM_skipTest, VALUED_PARAM_url, VALUED_PARAM_username, };



    private static final byte PIDS[]={ 3 ,52,8,9,1,51,2,7,6,12,53,11,54,10,13,4,5};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 3: return VALUED_PARAM_adapter;
             case 52: return VALUED_PARAM_comment;
             case 8: return VALUED_PARAM_delete;
             case 9: return VALUED_PARAM_edit;
             case 1: return VALUED_PARAM_id;
             case 51: return VALUED_PARAM_invokedBy;
             case 2: return VALUED_PARAM_name;
             case 7: return VALUED_PARAM_options;
             case 6: return VALUED_PARAM_password;
             case 12: return VALUED_PARAM_permittedOverrides;
             case 53: return VALUED_PARAM_priority;
             case 11: return VALUED_PARAM_relayId;
             case 54: return VALUED_PARAM_requestTime;
             case 10: return VALUED_PARAM_selectedName;
             case 13: return VALUED_PARAM_skipTest;
             case 4: return VALUED_PARAM_url;
             case 5: return VALUED_PARAM_username;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 3: return true;
             case 52: return true;
             case 8: return true;
             case 9: return true;
             case 1: return true;
             case 51: return true;
             case 2: return true;
             case 7: return true;
             case 6: return true;
             case 12: return true;
             case 53: return true;
             case 11: return true;
             case 54: return true;
             case 10: return true;
             case 13: return true;
             case 4: return true;
             case 5: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 3: return "adapter";
             case 52: return "comment";
             case 8: return "delete";
             case 9: return "edit";
             case 1: return "id";
             case 51: return "invokedBy";
             case 2: return "name";
             case 7: return "options";
             case 6: return "password";
             case 12: return "permittedOverrides";
             case 53: return "priority";
             case 11: return "relayId";
             case 54: return "requestTime";
             case 10: return "selectedName";
             case 13: return "skipTest";
             case 4: return "url";
             case 5: return "username";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 3: return 0;
             case 52: return 1;
             case 8: return 2;
             case 9: return 3;
             case 1: return 4;
             case 51: return 5;
             case 2: return 6;
             case 7: return 7;
             case 6: return 8;
             case 12: return 9;
             case 53: return 10;
             case 11: return 11;
             case 54: return 12;
             case 10: return 13;
             case 13: return 14;
             case 4: return 15;
             case 5: return 16;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="adapter") return 3;
             if(name=="comment") return 52;
             if(name=="delete") return 8;
             if(name=="edit") return 9;
             if(name=="id") return 1;
             if(name=="invokedBy") return 51;
             if(name=="name") return 2;
             if(name=="options") return 7;
             if(name=="password") return 6;
             if(name=="permittedOverrides") return 12;
             if(name=="priority") return 53;
             if(name=="relayId") return 11;
             if(name=="requestTime") return 54;
             if(name=="selectedName") return 10;
             if(name=="skipTest") return 13;
             if(name=="url") return 4;
             if(name=="username") return 5;
            
             if("adapter".equals(name)) return 3;
             if("comment".equals(name)) return 52;
             if("delete".equals(name)) return 8;
             if("edit".equals(name)) return 9;
             if("id".equals(name)) return 1;
             if("invokedBy".equals(name)) return 51;
             if("name".equals(name)) return 2;
             if("options".equals(name)) return 7;
             if("password".equals(name)) return 6;
             if("permittedOverrides".equals(name)) return 12;
             if("priority".equals(name)) return 53;
             if("relayId".equals(name)) return 11;
             if("requestTime".equals(name)) return 54;
             if("selectedName".equals(name)) return 10;
             if("skipTest".equals(name)) return 13;
             if("url".equals(name)) return 4;
             if("username".equals(name)) return 5;
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
             case 3: return  this._adapter; 
             case 52: return  this._comment; 
             case 8: return  OH.valueOf(this._delete); 
             case 9: return  OH.valueOf(this._edit); 
             case 1: return  OH.valueOf(this._id); 
             case 51: return  this._invokedBy; 
             case 2: return  this._name; 
             case 7: return  this._options; 
             case 6: return  this._password; 
             case 12: return  this._permittedOverrides; 
             case 53: return  OH.valueOf(this._priority); 
             case 11: return  this._relayId; 
             case 54: return  OH.valueOf(this._requestTime); 
             case 10: return  this._selectedName; 
             case 13: return  OH.valueOf(this._skipTest); 
             case 4: return  this._url; 
             case 5: return  this._username; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 3: return java.lang.String.class;
             case 52: return java.lang.String.class;
             case 8: return boolean.class;
             case 9: return boolean.class;
             case 1: return long.class;
             case 51: return java.lang.String.class;
             case 2: return java.lang.String.class;
             case 7: return java.lang.String.class;
             case 6: return java.lang.String.class;
             case 12: return java.lang.String.class;
             case 53: return int.class;
             case 11: return java.lang.String.class;
             case 54: return long.class;
             case 10: return java.lang.String.class;
             case 13: return boolean.class;
             case 4: return java.lang.String.class;
             case 5: return java.lang.String.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 3: return 20;
             case 52: return 20;
             case 8: return 0;
             case 9: return 0;
             case 1: return 6;
             case 51: return 20;
             case 2: return 20;
             case 7: return 20;
             case 6: return 20;
             case 12: return 20;
             case 53: return 4;
             case 11: return 20;
             case 54: return 6;
             case 10: return 20;
             case 13: return 0;
             case 4: return 20;
             case 5: return 20;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 3: this._adapter=(java.lang.String)value;return;
             case 52: this._comment=(java.lang.String)value;return;
             case 8: this._delete=(java.lang.Boolean)value;return;
             case 9: this._edit=(java.lang.Boolean)value;return;
             case 1: this._id=(java.lang.Long)value;return;
             case 51: this._invokedBy=(java.lang.String)value;return;
             case 2: this._name=(java.lang.String)value;return;
             case 7: this._options=(java.lang.String)value;return;
             case 6: this._password=(java.lang.String)value;return;
             case 12: this._permittedOverrides=(java.lang.String)value;return;
             case 53: this._priority=(java.lang.Integer)value;return;
             case 11: this._relayId=(java.lang.String)value;return;
             case 54: this._requestTime=(java.lang.Long)value;return;
             case 10: this._selectedName=(java.lang.String)value;return;
             case 13: this._skipTest=(java.lang.Boolean)value;return;
             case 4: this._url=(java.lang.String)value;return;
             case 5: this._username=(java.lang.String)value;return;
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
             case 3: this._adapter=(java.lang.String)value;return true;
             case 52: this._comment=(java.lang.String)value;return true;
             case 8: this._delete=(java.lang.Boolean)value;return true;
             case 9: this._edit=(java.lang.Boolean)value;return true;
             case 1: this._id=(java.lang.Long)value;return true;
             case 51: this._invokedBy=(java.lang.String)value;return true;
             case 2: this._name=(java.lang.String)value;return true;
             case 7: this._options=(java.lang.String)value;return true;
             case 6: this._password=(java.lang.String)value;return true;
             case 12: this._permittedOverrides=(java.lang.String)value;return true;
             case 53: this._priority=(java.lang.Integer)value;return true;
             case 11: this._relayId=(java.lang.String)value;return true;
             case 54: this._requestTime=(java.lang.Long)value;return true;
             case 10: this._selectedName=(java.lang.String)value;return true;
             case 13: this._skipTest=(java.lang.Boolean)value;return true;
             case 4: this._url=(java.lang.String)value;return true;
             case 5: this._username=(java.lang.String)value;return true;
            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
             case 8: return this._delete;
             case 9: return this._edit;
             case 13: return this._skipTest;
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
             case 53: return this._priority;
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
             case 1: return this._id;
             case 54: return this._requestTime;
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
             case 8: this._delete=value;return;
             case 9: this._edit=value;return;
             case 13: this._skipTest=value;return;
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
             case 53: this._priority=value;return;
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
             case 1: this._id=value;return;
             case 54: this._requestTime=value;return;
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
            this._id=in.readLong();
        
            break;

        case 2:
        
            this._name=(java.lang.String)converter.read(session);
        
            break;

        case 3:
        
            this._adapter=(java.lang.String)converter.read(session);
        
            break;

        case 4:
        
            this._url=(java.lang.String)converter.read(session);
        
            break;

        case 5:
        
            this._username=(java.lang.String)converter.read(session);
        
            break;

        case 6:
        
            this._password=(java.lang.String)converter.read(session);
        
            break;

        case 7:
        
            this._options=(java.lang.String)converter.read(session);
        
            break;

        case 8:
        
            if((basicType=in.readByte())!=0)
                break;
            this._delete=in.readBoolean();
        
            break;

        case 9:
        
            if((basicType=in.readByte())!=0)
                break;
            this._edit=in.readBoolean();
        
            break;

        case 10:
        
            this._selectedName=(java.lang.String)converter.read(session);
        
            break;

        case 11:
        
            this._relayId=(java.lang.String)converter.read(session);
        
            break;

        case 12:
        
            this._permittedOverrides=(java.lang.String)converter.read(session);
        
            break;

        case 13:
        
            if((basicType=in.readByte())!=0)
                break;
            this._skipTest=in.readBoolean();
        
            break;

        case 51:
        
            this._invokedBy=(java.lang.String)converter.read(session);
        
            break;

        case 52:
        
            this._comment=(java.lang.String)converter.read(session);
        
            break;

        case 53:
        
            if((basicType=in.readByte())!=4)
                break;
            this._priority=in.readInt();
        
            break;

        case 54:
        
            if((basicType=in.readByte())!=6)
                break;
            this._requestTime=in.readLong();
        
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
        
if(this._id!=0L && (0 & transience)==0){
    out.writeByte(1);
        
    out.writeByte(6);
    out.writeLong(this._id);
        
}

if(this._name!=null && (0 & transience)==0){
    out.writeByte(2);
        
    converter.write(this._name,session);
        
}

if(this._adapter!=null && (0 & transience)==0){
    out.writeByte(3);
        
    converter.write(this._adapter,session);
        
}

if(this._url!=null && (0 & transience)==0){
    out.writeByte(4);
        
    converter.write(this._url,session);
        
}

if(this._username!=null && (0 & transience)==0){
    out.writeByte(5);
        
    converter.write(this._username,session);
        
}

if(this._password!=null && (0 & transience)==0){
    out.writeByte(6);
        
    converter.write(this._password,session);
        
}

if(this._options!=null && (0 & transience)==0){
    out.writeByte(7);
        
    converter.write(this._options,session);
        
}

if(this._delete!=false && (0 & transience)==0){
    out.writeByte(8);
        
    out.writeByte(0);
    out.writeBoolean(this._delete);
        
}

if(this._edit!=false && (0 & transience)==0){
    out.writeByte(9);
        
    out.writeByte(0);
    out.writeBoolean(this._edit);
        
}

if(this._selectedName!=null && (0 & transience)==0){
    out.writeByte(10);
        
    converter.write(this._selectedName,session);
        
}

if(this._relayId!=null && (0 & transience)==0){
    out.writeByte(11);
        
    converter.write(this._relayId,session);
        
}

if(this._permittedOverrides!=null && (0 & transience)==0){
    out.writeByte(12);
        
    converter.write(this._permittedOverrides,session);
        
}

if(this._skipTest!=false && (0 & transience)==0){
    out.writeByte(13);
        
    out.writeByte(0);
    out.writeBoolean(this._skipTest);
        
}

if(this._invokedBy!=null && (0 & transience)==0){
    out.writeByte(51);
        
    converter.write(this._invokedBy,session);
        
}

if(this._comment!=null && (0 & transience)==0){
    out.writeByte(52);
        
    converter.write(this._comment,session);
        
}

if(this._priority!=0 && (0 & transience)==0){
    out.writeByte(53);
        
    out.writeByte(4);
    out.writeInt(this._priority);
        
}

if(this._requestTime!=0L && (0 & transience)==0){
    out.writeByte(54);
        
    out.writeByte(6);
    out.writeLong(this._requestTime);
        
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