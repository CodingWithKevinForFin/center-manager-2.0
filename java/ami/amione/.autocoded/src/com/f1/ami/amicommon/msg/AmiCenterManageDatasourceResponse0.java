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

public abstract class AmiCenterManageDatasourceResponse0 implements com.f1.ami.amicommon.msg.AmiCenterManageDatasourceResponse ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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

    private boolean _addSuccessful;

    private boolean _delete;

    private boolean _edit;

    private java.lang.Exception _exception;

    private long _id;

    private java.lang.String _message;

    private java.lang.String _name;

    private boolean _ok;

    private java.lang.String _options;

    private java.lang.String _password;

    private int _priority;

    private double _progress;

    private java.lang.String _selectedName;

    private java.util.List _tables;

    private java.lang.String _ticket;

    private java.util.List _trackedEvents;

    private java.lang.String _url;

    private java.lang.String _username;

    private static final String NAMES[]={ "adapter" ,"addSuccessful","delete","edit","exception","id","message","name","ok","options","password","priority","progress","selectedName","tables","ticket","trackedEvents","url","username"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 65;
        try{
        switch(h){

                case 7:

                    if(name == "ticket" || name.equals("ticket")) {this._ticket=(java.lang.String)value;return;}
break;
                case 9:

                    if(name == "selectedName" || name.equals("selectedName")) {this._selectedName=(java.lang.String)value;return;}
break;
                case 12:

                    if(name == "name" || name.equals("name")) {this._name=(java.lang.String)value;return;}
break;
                case 14:

                    if(name == "exception" || name.equals("exception")) {this._exception=(java.lang.Exception)value;return;}
break;
                case 15:

                    if(name == "username" || name.equals("username")) {this._username=(java.lang.String)value;return;}
break;
                case 19:

                    if(name == "adapter" || name.equals("adapter")) {this._adapter=(java.lang.String)value;return;}
break;
                case 31:

                    if(name == "addSuccessful" || name.equals("addSuccessful")) {this._addSuccessful=(java.lang.Boolean)value;return;}
break;
                case 32:

                    if(name == "progress" || name.equals("progress")) {this._progress=(java.lang.Double)value;return;}
break;
                case 34:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return;}
break;
                case 38:

                    if(name == "ok" || name.equals("ok")) {this._ok=(java.lang.Boolean)value;return;}
break;
                case 40:

                    if(name == "id" || name.equals("id")) {this._id=(java.lang.Long)value;return;}
break;
                case 44:

                    if(name == "delete" || name.equals("delete")) {this._delete=(java.lang.Boolean)value;return;}
break;
                case 50:

                    if(name == "password" || name.equals("password")) {this._password=(java.lang.String)value;return;}
break;
                case 51:

                    if(name == "tables" || name.equals("tables")) {this._tables=(java.util.List)value;return;}
break;
                case 53:

                    if(name == "message" || name.equals("message")) {this._message=(java.lang.String)value;return;}
break;
                case 54:

                    if(name == "url" || name.equals("url")) {this._url=(java.lang.String)value;return;}
break;
                case 57:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {this._trackedEvents=(java.util.List)value;return;}
break;
                case 62:

                    if(name == "edit" || name.equals("edit")) {this._edit=(java.lang.Boolean)value;return;}
break;
                case 64:

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
        final int h=Math.abs(name.hashCode()) % 65;
        switch(h){

                case 7:

                    if(name == "ticket" || name.equals("ticket")) {this._ticket=(java.lang.String)value;return true;}
break;
                case 9:

                    if(name == "selectedName" || name.equals("selectedName")) {this._selectedName=(java.lang.String)value;return true;}
break;
                case 12:

                    if(name == "name" || name.equals("name")) {this._name=(java.lang.String)value;return true;}
break;
                case 14:

                    if(name == "exception" || name.equals("exception")) {this._exception=(java.lang.Exception)value;return true;}
break;
                case 15:

                    if(name == "username" || name.equals("username")) {this._username=(java.lang.String)value;return true;}
break;
                case 19:

                    if(name == "adapter" || name.equals("adapter")) {this._adapter=(java.lang.String)value;return true;}
break;
                case 31:

                    if(name == "addSuccessful" || name.equals("addSuccessful")) {this._addSuccessful=(java.lang.Boolean)value;return true;}
break;
                case 32:

                    if(name == "progress" || name.equals("progress")) {this._progress=(java.lang.Double)value;return true;}
break;
                case 34:

                    if(name == "priority" || name.equals("priority")) {this._priority=(java.lang.Integer)value;return true;}
break;
                case 38:

                    if(name == "ok" || name.equals("ok")) {this._ok=(java.lang.Boolean)value;return true;}
break;
                case 40:

                    if(name == "id" || name.equals("id")) {this._id=(java.lang.Long)value;return true;}
break;
                case 44:

                    if(name == "delete" || name.equals("delete")) {this._delete=(java.lang.Boolean)value;return true;}
break;
                case 50:

                    if(name == "password" || name.equals("password")) {this._password=(java.lang.String)value;return true;}
break;
                case 51:

                    if(name == "tables" || name.equals("tables")) {this._tables=(java.util.List)value;return true;}
break;
                case 53:

                    if(name == "message" || name.equals("message")) {this._message=(java.lang.String)value;return true;}
break;
                case 54:

                    if(name == "url" || name.equals("url")) {this._url=(java.lang.String)value;return true;}
break;
                case 57:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {this._trackedEvents=(java.util.List)value;return true;}
break;
                case 62:

                    if(name == "edit" || name.equals("edit")) {this._edit=(java.lang.Boolean)value;return true;}
break;
                case 64:

                    if(name == "options" || name.equals("options")) {this._options=(java.lang.String)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 65;
        switch(h){

                case 7:

		    
                    if(name == "ticket" || name.equals("ticket")) {return this._ticket;}
            
break;
                case 9:

		    
                    if(name == "selectedName" || name.equals("selectedName")) {return this._selectedName;}
            
break;
                case 12:

		    
                    if(name == "name" || name.equals("name")) {return this._name;}
            
break;
                case 14:

		    
                    if(name == "exception" || name.equals("exception")) {return this._exception;}
            
break;
                case 15:

		    
                    if(name == "username" || name.equals("username")) {return this._username;}
            
break;
                case 19:

		    
                    if(name == "adapter" || name.equals("adapter")) {return this._adapter;}
            
break;
                case 31:

		    
                    if(name == "addSuccessful" || name.equals("addSuccessful")) {return OH.valueOf(this._addSuccessful);}
		    
break;
                case 32:

		    
                    if(name == "progress" || name.equals("progress")) {return OH.valueOf(this._progress);}
		    
break;
                case 34:

		    
                    if(name == "priority" || name.equals("priority")) {return OH.valueOf(this._priority);}
		    
break;
                case 38:

		    
                    if(name == "ok" || name.equals("ok")) {return OH.valueOf(this._ok);}
		    
break;
                case 40:

		    
                    if(name == "id" || name.equals("id")) {return OH.valueOf(this._id);}
		    
break;
                case 44:

		    
                    if(name == "delete" || name.equals("delete")) {return OH.valueOf(this._delete);}
		    
break;
                case 50:

		    
                    if(name == "password" || name.equals("password")) {return this._password;}
            
break;
                case 51:

		    
                    if(name == "tables" || name.equals("tables")) {return this._tables;}
            
break;
                case 53:

		    
                    if(name == "message" || name.equals("message")) {return this._message;}
            
break;
                case 54:

		    
                    if(name == "url" || name.equals("url")) {return this._url;}
            
break;
                case 57:

		    
                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return this._trackedEvents;}
            
break;
                case 62:

		    
                    if(name == "edit" || name.equals("edit")) {return OH.valueOf(this._edit);}
		    
break;
                case 64:

		    
                    if(name == "options" || name.equals("options")) {return this._options;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 65;
        switch(h){

                case 7:

                    if(name == "ticket" || name.equals("ticket")) {return java.lang.String.class;}
break;
                case 9:

                    if(name == "selectedName" || name.equals("selectedName")) {return java.lang.String.class;}
break;
                case 12:

                    if(name == "name" || name.equals("name")) {return java.lang.String.class;}
break;
                case 14:

                    if(name == "exception" || name.equals("exception")) {return java.lang.Exception.class;}
break;
                case 15:

                    if(name == "username" || name.equals("username")) {return java.lang.String.class;}
break;
                case 19:

                    if(name == "adapter" || name.equals("adapter")) {return java.lang.String.class;}
break;
                case 31:

                    if(name == "addSuccessful" || name.equals("addSuccessful")) {return boolean.class;}
break;
                case 32:

                    if(name == "progress" || name.equals("progress")) {return double.class;}
break;
                case 34:

                    if(name == "priority" || name.equals("priority")) {return int.class;}
break;
                case 38:

                    if(name == "ok" || name.equals("ok")) {return boolean.class;}
break;
                case 40:

                    if(name == "id" || name.equals("id")) {return long.class;}
break;
                case 44:

                    if(name == "delete" || name.equals("delete")) {return boolean.class;}
break;
                case 50:

                    if(name == "password" || name.equals("password")) {return java.lang.String.class;}
break;
                case 51:

                    if(name == "tables" || name.equals("tables")) {return java.util.List.class;}
break;
                case 53:

                    if(name == "message" || name.equals("message")) {return java.lang.String.class;}
break;
                case 54:

                    if(name == "url" || name.equals("url")) {return java.lang.String.class;}
break;
                case 57:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return java.util.List.class;}
break;
                case 62:

                    if(name == "edit" || name.equals("edit")) {return boolean.class;}
break;
                case 64:

                    if(name == "options" || name.equals("options")) {return java.lang.String.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 65;
        switch(h){

                case 7:

                    if(name == "ticket" || name.equals("ticket")) {return VALUED_PARAM_ticket;}
break;
                case 9:

                    if(name == "selectedName" || name.equals("selectedName")) {return VALUED_PARAM_selectedName;}
break;
                case 12:

                    if(name == "name" || name.equals("name")) {return VALUED_PARAM_name;}
break;
                case 14:

                    if(name == "exception" || name.equals("exception")) {return VALUED_PARAM_exception;}
break;
                case 15:

                    if(name == "username" || name.equals("username")) {return VALUED_PARAM_username;}
break;
                case 19:

                    if(name == "adapter" || name.equals("adapter")) {return VALUED_PARAM_adapter;}
break;
                case 31:

                    if(name == "addSuccessful" || name.equals("addSuccessful")) {return VALUED_PARAM_addSuccessful;}
break;
                case 32:

                    if(name == "progress" || name.equals("progress")) {return VALUED_PARAM_progress;}
break;
                case 34:

                    if(name == "priority" || name.equals("priority")) {return VALUED_PARAM_priority;}
break;
                case 38:

                    if(name == "ok" || name.equals("ok")) {return VALUED_PARAM_ok;}
break;
                case 40:

                    if(name == "id" || name.equals("id")) {return VALUED_PARAM_id;}
break;
                case 44:

                    if(name == "delete" || name.equals("delete")) {return VALUED_PARAM_delete;}
break;
                case 50:

                    if(name == "password" || name.equals("password")) {return VALUED_PARAM_password;}
break;
                case 51:

                    if(name == "tables" || name.equals("tables")) {return VALUED_PARAM_tables;}
break;
                case 53:

                    if(name == "message" || name.equals("message")) {return VALUED_PARAM_message;}
break;
                case 54:

                    if(name == "url" || name.equals("url")) {return VALUED_PARAM_url;}
break;
                case 57:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return VALUED_PARAM_trackedEvents;}
break;
                case 62:

                    if(name == "edit" || name.equals("edit")) {return VALUED_PARAM_edit;}
break;
                case 64:

                    if(name == "options" || name.equals("options")) {return VALUED_PARAM_options;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 65;
        switch(h){

                case 7:

                    if(name == "ticket" || name.equals("ticket")) {return 15;}
break;
                case 9:

                    if(name == "selectedName" || name.equals("selectedName")) {return 13;}
break;
                case 12:

                    if(name == "name" || name.equals("name")) {return 7;}
break;
                case 14:

                    if(name == "exception" || name.equals("exception")) {return 4;}
break;
                case 15:

                    if(name == "username" || name.equals("username")) {return 18;}
break;
                case 19:

                    if(name == "adapter" || name.equals("adapter")) {return 0;}
break;
                case 31:

                    if(name == "addSuccessful" || name.equals("addSuccessful")) {return 1;}
break;
                case 32:

                    if(name == "progress" || name.equals("progress")) {return 12;}
break;
                case 34:

                    if(name == "priority" || name.equals("priority")) {return 11;}
break;
                case 38:

                    if(name == "ok" || name.equals("ok")) {return 8;}
break;
                case 40:

                    if(name == "id" || name.equals("id")) {return 5;}
break;
                case 44:

                    if(name == "delete" || name.equals("delete")) {return 2;}
break;
                case 50:

                    if(name == "password" || name.equals("password")) {return 10;}
break;
                case 51:

                    if(name == "tables" || name.equals("tables")) {return 14;}
break;
                case 53:

                    if(name == "message" || name.equals("message")) {return 6;}
break;
                case 54:

                    if(name == "url" || name.equals("url")) {return 17;}
break;
                case 57:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return 16;}
break;
                case 62:

                    if(name == "edit" || name.equals("edit")) {return 3;}
break;
                case 64:

                    if(name == "options" || name.equals("options")) {return 9;}
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
        return 19;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiCenterManageDatasourceResponse0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 65;
        switch(h){

                case 7:

                    if(name == "ticket" || name.equals("ticket")) {return true;}
break;
                case 9:

                    if(name == "selectedName" || name.equals("selectedName")) {return true;}
break;
                case 12:

                    if(name == "name" || name.equals("name")) {return true;}
break;
                case 14:

                    if(name == "exception" || name.equals("exception")) {return true;}
break;
                case 15:

                    if(name == "username" || name.equals("username")) {return true;}
break;
                case 19:

                    if(name == "adapter" || name.equals("adapter")) {return true;}
break;
                case 31:

                    if(name == "addSuccessful" || name.equals("addSuccessful")) {return true;}
break;
                case 32:

                    if(name == "progress" || name.equals("progress")) {return true;}
break;
                case 34:

                    if(name == "priority" || name.equals("priority")) {return true;}
break;
                case 38:

                    if(name == "ok" || name.equals("ok")) {return true;}
break;
                case 40:

                    if(name == "id" || name.equals("id")) {return true;}
break;
                case 44:

                    if(name == "delete" || name.equals("delete")) {return true;}
break;
                case 50:

                    if(name == "password" || name.equals("password")) {return true;}
break;
                case 51:

                    if(name == "tables" || name.equals("tables")) {return true;}
break;
                case 53:

                    if(name == "message" || name.equals("message")) {return true;}
break;
                case 54:

                    if(name == "url" || name.equals("url")) {return true;}
break;
                case 57:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return true;}
break;
                case 62:

                    if(name == "edit" || name.equals("edit")) {return true;}
break;
                case 64:

                    if(name == "options" || name.equals("options")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 65;
        switch(h){

                case 7:

                    if(name == "ticket" || name.equals("ticket")) {return 20;}
break;
                case 9:

                    if(name == "selectedName" || name.equals("selectedName")) {return 20;}
break;
                case 12:

                    if(name == "name" || name.equals("name")) {return 20;}
break;
                case 14:

                    if(name == "exception" || name.equals("exception")) {return 53;}
break;
                case 15:

                    if(name == "username" || name.equals("username")) {return 20;}
break;
                case 19:

                    if(name == "adapter" || name.equals("adapter")) {return 20;}
break;
                case 31:

                    if(name == "addSuccessful" || name.equals("addSuccessful")) {return 0;}
break;
                case 32:

                    if(name == "progress" || name.equals("progress")) {return 7;}
break;
                case 34:

                    if(name == "priority" || name.equals("priority")) {return 4;}
break;
                case 38:

                    if(name == "ok" || name.equals("ok")) {return 0;}
break;
                case 40:

                    if(name == "id" || name.equals("id")) {return 6;}
break;
                case 44:

                    if(name == "delete" || name.equals("delete")) {return 0;}
break;
                case 50:

                    if(name == "password" || name.equals("password")) {return 20;}
break;
                case 51:

                    if(name == "tables" || name.equals("tables")) {return 21;}
break;
                case 53:

                    if(name == "message" || name.equals("message")) {return 20;}
break;
                case 54:

                    if(name == "url" || name.equals("url")) {return 20;}
break;
                case 57:

                    if(name == "trackedEvents" || name.equals("trackedEvents")) {return 21;}
break;
                case 62:

                    if(name == "edit" || name.equals("edit")) {return 0;}
break;
                case 64:

                    if(name == "options" || name.equals("options")) {return 20;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _adapter;

        case 1:return _addSuccessful;

        case 2:return _delete;

        case 3:return _edit;

        case 4:return _exception;

        case 5:return _id;

        case 6:return _message;

        case 7:return _name;

        case 8:return _ok;

        case 9:return _options;

        case 10:return _password;

        case 11:return _priority;

        case 12:return _progress;

        case 13:return _selectedName;

        case 14:return _tables;

        case 15:return _ticket;

        case 16:return _trackedEvents;

        case 17:return _url;

        case 18:return _username;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 19);
    }

    public java.lang.String getAdapter(){
        return this._adapter;
    }
    public void setAdapter(java.lang.String _adapter){
    
        this._adapter=_adapter;
    }

    public boolean getAddSuccessful(){
        return this._addSuccessful;
    }
    public void setAddSuccessful(boolean _addSuccessful){
    
        this._addSuccessful=_addSuccessful;
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

    public java.lang.Exception getException(){
        return this._exception;
    }
    public void setException(java.lang.Exception _exception){
    
        this._exception=_exception;
    }

    public long getId(){
        return this._id;
    }
    public void setId(long _id){
    
        this._id=_id;
    }

    public java.lang.String getMessage(){
        return this._message;
    }
    public void setMessage(java.lang.String _message){
    
        this._message=_message;
    }

    public java.lang.String getName(){
        return this._name;
    }
    public void setName(java.lang.String _name){
    
        this._name=_name;
    }

    public boolean getOk(){
        return this._ok;
    }
    public void setOk(boolean _ok){
    
        this._ok=_ok;
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

    public int getPriority(){
        return this._priority;
    }
    public void setPriority(int _priority){
    
        this._priority=_priority;
    }

    public double getProgress(){
        return this._progress;
    }
    public void setProgress(double _progress){
    
        this._progress=_progress;
    }

    public java.lang.String getSelectedName(){
        return this._selectedName;
    }
    public void setSelectedName(java.lang.String _selectedName){
    
        this._selectedName=_selectedName;
    }

    public java.util.List getTables(){
        return this._tables;
    }
    public void setTables(java.util.List _tables){
    
        this._tables=_tables;
    }

    public java.lang.String getTicket(){
        return this._ticket;
    }
    public void setTicket(java.lang.String _ticket){
    
        this._ticket=_ticket;
    }

    public java.util.List getTrackedEvents(){
        return this._trackedEvents;
    }
    public void setTrackedEvents(java.util.List _trackedEvents){
    
        this._trackedEvents=_trackedEvents;
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





  
    private static final class VALUED_PARAM_CLASS_adapter extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceResponse0)valued).getAdapter();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setAdapter((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setAdapter(source.getAdapter());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getAdapter(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getAdapter(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String adapter";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setAdapter(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_adapter=new VALUED_PARAM_CLASS_adapter();
  

  
    private static final class VALUED_PARAM_CLASS_addSuccessful extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getAddSuccessful());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
		      valued.setAddSuccessful(stream.readBoolean());
		    
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
            return 11;
	    }
    
	    @Override
	    public String getName() {
            return "addSuccessful";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (boolean)((AmiCenterManageDatasourceResponse0)valued).getAddSuccessful();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setAddSuccessful((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setAddSuccessful(source.getAddSuccessful());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
	        return OH.eq(dest.getAddSuccessful(),source.getAddSuccessful());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterManageDatasourceResponse0 valued) {
		    return valued.getAddSuccessful();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterManageDatasourceResponse0 valued, boolean value) {
		    valued.setAddSuccessful(value);
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAddSuccessful());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAddSuccessful());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean addSuccessful";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setAddSuccessful(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_addSuccessful=new VALUED_PARAM_CLASS_addSuccessful();
  

  
    private static final class VALUED_PARAM_CLASS_delete extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getDelete());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (boolean)((AmiCenterManageDatasourceResponse0)valued).getDelete();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setDelete((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setDelete(source.getDelete());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
	        return OH.eq(dest.getDelete(),source.getDelete());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterManageDatasourceResponse0 valued) {
		    return valued.getDelete();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterManageDatasourceResponse0 valued, boolean value) {
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getDelete());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getDelete());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean delete";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setDelete(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_delete=new VALUED_PARAM_CLASS_delete();
  

  
    private static final class VALUED_PARAM_CLASS_edit extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getEdit());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (boolean)((AmiCenterManageDatasourceResponse0)valued).getEdit();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setEdit((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setEdit(source.getEdit());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
	        return OH.eq(dest.getEdit(),source.getEdit());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterManageDatasourceResponse0 valued) {
		    return valued.getEdit();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterManageDatasourceResponse0 valued, boolean value) {
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getEdit());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getEdit());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean edit";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setEdit(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_edit=new VALUED_PARAM_CLASS_edit();
  

  
    private static final class VALUED_PARAM_CLASS_exception extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 53;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Exception}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.Exception}");
		    
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
            return 44;
	    }
    
	    @Override
	    public String getName() {
            return "exception";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (java.lang.Exception)((AmiCenterManageDatasourceResponse0)valued).getException();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setException((java.lang.Exception)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setException(source.getException());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
	        return OH.eq(dest.getException(),source.getException());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.lang.Exception.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.lang.Exception.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getException());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getException());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.Exception exception";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setException(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_exception=new VALUED_PARAM_CLASS_exception();
  

  
    private static final class VALUED_PARAM_CLASS_id extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getId());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 5;
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
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (long)((AmiCenterManageDatasourceResponse0)valued).getId();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setId((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setId(source.getId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
	        return OH.eq(dest.getId(),source.getId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterManageDatasourceResponse0 valued) {
		    return valued.getId();
	    }
    
	    @Override
	    public void setLong(AmiCenterManageDatasourceResponse0 valued, long value) {
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getId());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long id";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setId(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_id=new VALUED_PARAM_CLASS_id();
  

  
    private static final class VALUED_PARAM_CLASS_message extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
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
            return 43;
	    }
    
	    @Override
	    public String getName() {
            return "message";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceResponse0)valued).getMessage();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setMessage((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setMessage(source.getMessage());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
	        return OH.eq(dest.getMessage(),source.getMessage());
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getMessage(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getMessage(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String message";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setMessage(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_message=new VALUED_PARAM_CLASS_message();
  

  
    private static final class VALUED_PARAM_CLASS_name extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
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
            return "name";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceResponse0)valued).getName();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setName(source.getName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getName(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String name";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_name=new VALUED_PARAM_CLASS_name();
  

  
    private static final class VALUED_PARAM_CLASS_ok extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 0;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeBoolean(valued.getOk());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
		      valued.setOk(stream.readBoolean());
		    
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
		    return 8;
	    }
    
	    @Override
	    public byte getPid() {
            return 42;
	    }
    
	    @Override
	    public String getName() {
            return "ok";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (boolean)((AmiCenterManageDatasourceResponse0)valued).getOk();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setOk((java.lang.Boolean)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setOk(source.getOk());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
	        return OH.eq(dest.getOk(),source.getOk());
	    }
	    
	    
	    @Override
	    public boolean getBoolean(AmiCenterManageDatasourceResponse0 valued) {
		    return valued.getOk();
	    }
    
	    @Override
	    public void setBoolean(AmiCenterManageDatasourceResponse0 valued, boolean value) {
		    valued.setOk(value);
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getOk());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getOk());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:boolean ok";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setOk(false);
	    }
	};
    private static final ValuedParam VALUED_PARAM_ok=new VALUED_PARAM_CLASS_ok();
  

  
    private static final class VALUED_PARAM_CLASS_options extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
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
            return 7;
	    }
    
	    @Override
	    public String getName() {
            return "options";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceResponse0)valued).getOptions();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setOptions((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setOptions(source.getOptions());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getOptions(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getOptions(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String options";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setOptions(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_options=new VALUED_PARAM_CLASS_options();
  

  
    private static final class VALUED_PARAM_CLASS_password extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
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
            return "password";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceResponse0)valued).getPassword();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setPassword((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setPassword(source.getPassword());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getPassword(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getPassword(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String password";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setPassword(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_password=new VALUED_PARAM_CLASS_password();
  

  
    private static final class VALUED_PARAM_CLASS_priority extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getPriority());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 11;
	    }
    
	    @Override
	    public byte getPid() {
            return 57;
	    }
    
	    @Override
	    public String getName() {
            return "priority";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (int)((AmiCenterManageDatasourceResponse0)valued).getPriority();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setPriority((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setPriority(source.getPriority());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
	        return OH.eq(dest.getPriority(),source.getPriority());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiCenterManageDatasourceResponse0 valued) {
		    return valued.getPriority();
	    }
    
	    @Override
	    public void setInt(AmiCenterManageDatasourceResponse0 valued, int value) {
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getPriority());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int priority";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setPriority(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_priority=new VALUED_PARAM_CLASS_priority();
  

  
    private static final class VALUED_PARAM_CLASS_progress extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 7;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeDouble(valued.getProgress());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
		      valued.setProgress(stream.readDouble());
		    
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
            return 53;
	    }
    
	    @Override
	    public String getName() {
            return "progress";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (double)((AmiCenterManageDatasourceResponse0)valued).getProgress();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setProgress((java.lang.Double)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setProgress(source.getProgress());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
	        return OH.eq(dest.getProgress(),source.getProgress());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public double getDouble(AmiCenterManageDatasourceResponse0 valued) {
		    return valued.getProgress();
	    }
    
	    @Override
	    public void setDouble(AmiCenterManageDatasourceResponse0 valued, double value) {
		    valued.setProgress(value);
	    }
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return double.class;
	    }
	    private static final Caster CASTER=OH.getCaster(double.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getProgress());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getProgress());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:double progress";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setProgress(0D);
	    }
	};
    private static final ValuedParam VALUED_PARAM_progress=new VALUED_PARAM_CLASS_progress();
  

  
    private static final class VALUED_PARAM_CLASS_selectedName extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
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
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceResponse0)valued).getSelectedName();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setSelectedName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setSelectedName(source.getSelectedName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getSelectedName(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getSelectedName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String selectedName";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setSelectedName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_selectedName=new VALUED_PARAM_CLASS_selectedName();
  

  
    private static final class VALUED_PARAM_CLASS_tables extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 14;
	    }
    
	    @Override
	    public byte getPid() {
            return 12;
	    }
    
	    @Override
	    public String getName() {
            return "tables";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (java.util.List)((AmiCenterManageDatasourceResponse0)valued).getTables();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setTables((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setTables(source.getTables());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
	        return OH.eq(dest.getTables(),source.getTables());
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTables());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTables());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List tables";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setTables(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_tables=new VALUED_PARAM_CLASS_tables();
  

  
    private static final class VALUED_PARAM_CLASS_ticket extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
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
            return 55;
	    }
    
	    @Override
	    public String getName() {
            return "ticket";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceResponse0)valued).getTicket();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setTicket((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setTicket(source.getTicket());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
	        return OH.eq(dest.getTicket(),source.getTicket());
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getTicket(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getTicket(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String ticket";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setTicket(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_ticket=new VALUED_PARAM_CLASS_ticket();
  

  
    private static final class VALUED_PARAM_CLASS_trackedEvents extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 16;
	    }
    
	    @Override
	    public byte getPid() {
            return 58;
	    }
    
	    @Override
	    public String getName() {
            return "trackedEvents";
	    }
    
	    @Override
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (java.util.List)((AmiCenterManageDatasourceResponse0)valued).getTrackedEvents();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setTrackedEvents((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setTrackedEvents(source.getTrackedEvents());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
	        return OH.eq(dest.getTrackedEvents(),source.getTrackedEvents());
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getTrackedEvents());
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getTrackedEvents());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List trackedEvents";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setTrackedEvents(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_trackedEvents=new VALUED_PARAM_CLASS_trackedEvents();
  

  
    private static final class VALUED_PARAM_CLASS_url extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 17;
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
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceResponse0)valued).getUrl();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setUrl((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setUrl(source.getUrl());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getUrl(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getUrl(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String url";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setUrl(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_url=new VALUED_PARAM_CLASS_url();
  

  
    private static final class VALUED_PARAM_CLASS_username extends AbstractValuedParam<AmiCenterManageDatasourceResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterManageDatasourceResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterManageDatasourceResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 18;
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
	    public Object getValue(AmiCenterManageDatasourceResponse0 valued) {
		    return (java.lang.String)((AmiCenterManageDatasourceResponse0)valued).getUsername();
	    }
    
	    @Override
	    public void setValue(AmiCenterManageDatasourceResponse0 valued, Object value) {
		    valued.setUsername((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
		    dest.setUsername(source.getUsername());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterManageDatasourceResponse0 source, AmiCenterManageDatasourceResponse0 dest) {
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
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getUsername(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterManageDatasourceResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getUsername(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String username";
	    }
	    @Override
	    public void clear(AmiCenterManageDatasourceResponse0 valued){
	       valued.setUsername(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_username=new VALUED_PARAM_CLASS_username();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_adapter, VALUED_PARAM_addSuccessful, VALUED_PARAM_delete, VALUED_PARAM_edit, VALUED_PARAM_exception, VALUED_PARAM_id, VALUED_PARAM_message, VALUED_PARAM_name, VALUED_PARAM_ok, VALUED_PARAM_options, VALUED_PARAM_password, VALUED_PARAM_priority, VALUED_PARAM_progress, VALUED_PARAM_selectedName, VALUED_PARAM_tables, VALUED_PARAM_ticket, VALUED_PARAM_trackedEvents, VALUED_PARAM_url, VALUED_PARAM_username, };



    private static final byte PIDS[]={ 3 ,11,8,9,44,1,43,2,42,7,6,57,53,10,12,55,58,4,5};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 3: return VALUED_PARAM_adapter;
             case 11: return VALUED_PARAM_addSuccessful;
             case 8: return VALUED_PARAM_delete;
             case 9: return VALUED_PARAM_edit;
             case 44: return VALUED_PARAM_exception;
             case 1: return VALUED_PARAM_id;
             case 43: return VALUED_PARAM_message;
             case 2: return VALUED_PARAM_name;
             case 42: return VALUED_PARAM_ok;
             case 7: return VALUED_PARAM_options;
             case 6: return VALUED_PARAM_password;
             case 57: return VALUED_PARAM_priority;
             case 53: return VALUED_PARAM_progress;
             case 10: return VALUED_PARAM_selectedName;
             case 12: return VALUED_PARAM_tables;
             case 55: return VALUED_PARAM_ticket;
             case 58: return VALUED_PARAM_trackedEvents;
             case 4: return VALUED_PARAM_url;
             case 5: return VALUED_PARAM_username;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 3: return true;
             case 11: return true;
             case 8: return true;
             case 9: return true;
             case 44: return true;
             case 1: return true;
             case 43: return true;
             case 2: return true;
             case 42: return true;
             case 7: return true;
             case 6: return true;
             case 57: return true;
             case 53: return true;
             case 10: return true;
             case 12: return true;
             case 55: return true;
             case 58: return true;
             case 4: return true;
             case 5: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 3: return "adapter";
             case 11: return "addSuccessful";
             case 8: return "delete";
             case 9: return "edit";
             case 44: return "exception";
             case 1: return "id";
             case 43: return "message";
             case 2: return "name";
             case 42: return "ok";
             case 7: return "options";
             case 6: return "password";
             case 57: return "priority";
             case 53: return "progress";
             case 10: return "selectedName";
             case 12: return "tables";
             case 55: return "ticket";
             case 58: return "trackedEvents";
             case 4: return "url";
             case 5: return "username";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 3: return 0;
             case 11: return 1;
             case 8: return 2;
             case 9: return 3;
             case 44: return 4;
             case 1: return 5;
             case 43: return 6;
             case 2: return 7;
             case 42: return 8;
             case 7: return 9;
             case 6: return 10;
             case 57: return 11;
             case 53: return 12;
             case 10: return 13;
             case 12: return 14;
             case 55: return 15;
             case 58: return 16;
             case 4: return 17;
             case 5: return 18;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="adapter") return 3;
             if(name=="addSuccessful") return 11;
             if(name=="delete") return 8;
             if(name=="edit") return 9;
             if(name=="exception") return 44;
             if(name=="id") return 1;
             if(name=="message") return 43;
             if(name=="name") return 2;
             if(name=="ok") return 42;
             if(name=="options") return 7;
             if(name=="password") return 6;
             if(name=="priority") return 57;
             if(name=="progress") return 53;
             if(name=="selectedName") return 10;
             if(name=="tables") return 12;
             if(name=="ticket") return 55;
             if(name=="trackedEvents") return 58;
             if(name=="url") return 4;
             if(name=="username") return 5;
            
             if("adapter".equals(name)) return 3;
             if("addSuccessful".equals(name)) return 11;
             if("delete".equals(name)) return 8;
             if("edit".equals(name)) return 9;
             if("exception".equals(name)) return 44;
             if("id".equals(name)) return 1;
             if("message".equals(name)) return 43;
             if("name".equals(name)) return 2;
             if("ok".equals(name)) return 42;
             if("options".equals(name)) return 7;
             if("password".equals(name)) return 6;
             if("priority".equals(name)) return 57;
             if("progress".equals(name)) return 53;
             if("selectedName".equals(name)) return 10;
             if("tables".equals(name)) return 12;
             if("ticket".equals(name)) return 55;
             if("trackedEvents".equals(name)) return 58;
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
             case 11: return  OH.valueOf(this._addSuccessful); 
             case 8: return  OH.valueOf(this._delete); 
             case 9: return  OH.valueOf(this._edit); 
             case 44: return  this._exception; 
             case 1: return  OH.valueOf(this._id); 
             case 43: return  this._message; 
             case 2: return  this._name; 
             case 42: return  OH.valueOf(this._ok); 
             case 7: return  this._options; 
             case 6: return  this._password; 
             case 57: return  OH.valueOf(this._priority); 
             case 53: return  OH.valueOf(this._progress); 
             case 10: return  this._selectedName; 
             case 12: return  this._tables; 
             case 55: return  this._ticket; 
             case 58: return  this._trackedEvents; 
             case 4: return  this._url; 
             case 5: return  this._username; 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 3: return java.lang.String.class;
             case 11: return boolean.class;
             case 8: return boolean.class;
             case 9: return boolean.class;
             case 44: return java.lang.Exception.class;
             case 1: return long.class;
             case 43: return java.lang.String.class;
             case 2: return java.lang.String.class;
             case 42: return boolean.class;
             case 7: return java.lang.String.class;
             case 6: return java.lang.String.class;
             case 57: return int.class;
             case 53: return double.class;
             case 10: return java.lang.String.class;
             case 12: return java.util.List.class;
             case 55: return java.lang.String.class;
             case 58: return java.util.List.class;
             case 4: return java.lang.String.class;
             case 5: return java.lang.String.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 3: return 20;
             case 11: return 0;
             case 8: return 0;
             case 9: return 0;
             case 44: return 53;
             case 1: return 6;
             case 43: return 20;
             case 2: return 20;
             case 42: return 0;
             case 7: return 20;
             case 6: return 20;
             case 57: return 4;
             case 53: return 7;
             case 10: return 20;
             case 12: return 21;
             case 55: return 20;
             case 58: return 21;
             case 4: return 20;
             case 5: return 20;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 3: this._adapter=(java.lang.String)value;return;
             case 11: this._addSuccessful=(java.lang.Boolean)value;return;
             case 8: this._delete=(java.lang.Boolean)value;return;
             case 9: this._edit=(java.lang.Boolean)value;return;
             case 44: this._exception=(java.lang.Exception)value;return;
             case 1: this._id=(java.lang.Long)value;return;
             case 43: this._message=(java.lang.String)value;return;
             case 2: this._name=(java.lang.String)value;return;
             case 42: this._ok=(java.lang.Boolean)value;return;
             case 7: this._options=(java.lang.String)value;return;
             case 6: this._password=(java.lang.String)value;return;
             case 57: this._priority=(java.lang.Integer)value;return;
             case 53: this._progress=(java.lang.Double)value;return;
             case 10: this._selectedName=(java.lang.String)value;return;
             case 12: this._tables=(java.util.List)value;return;
             case 55: this._ticket=(java.lang.String)value;return;
             case 58: this._trackedEvents=(java.util.List)value;return;
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
             case 11: this._addSuccessful=(java.lang.Boolean)value;return true;
             case 8: this._delete=(java.lang.Boolean)value;return true;
             case 9: this._edit=(java.lang.Boolean)value;return true;
             case 44: this._exception=(java.lang.Exception)value;return true;
             case 1: this._id=(java.lang.Long)value;return true;
             case 43: this._message=(java.lang.String)value;return true;
             case 2: this._name=(java.lang.String)value;return true;
             case 42: this._ok=(java.lang.Boolean)value;return true;
             case 7: this._options=(java.lang.String)value;return true;
             case 6: this._password=(java.lang.String)value;return true;
             case 57: this._priority=(java.lang.Integer)value;return true;
             case 53: this._progress=(java.lang.Double)value;return true;
             case 10: this._selectedName=(java.lang.String)value;return true;
             case 12: this._tables=(java.util.List)value;return true;
             case 55: this._ticket=(java.lang.String)value;return true;
             case 58: this._trackedEvents=(java.util.List)value;return true;
             case 4: this._url=(java.lang.String)value;return true;
             case 5: this._username=(java.lang.String)value;return true;
            default:return false;
        }
    }
    
    public boolean askBoolean(byte pid){
        switch(pid){
             case 11: return this._addSuccessful;
             case 8: return this._delete;
             case 9: return this._edit;
             case 42: return this._ok;
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
             case 57: return this._priority;
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
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for long value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public double askDouble(byte pid){
        switch(pid){
             case 53: return this._progress;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for double value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putBoolean(byte pid,boolean value){
    
        switch(pid){
             case 11: this._addSuccessful=value;return;
             case 8: this._delete=value;return;
             case 9: this._edit=value;return;
             case 42: this._ok=value;return;
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
             case 57: this._priority=value;return;
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
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for long value").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void putDouble(byte pid,double value){
    
        switch(pid){
             case 53: this._progress=value;return;
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
        
            if((basicType=in.readByte())!=0)
                break;
            this._addSuccessful=in.readBoolean();
        
            break;

        case 12:
        
            this._tables=(java.util.List)converter.read(session);
        
            break;

        case 42:
        
            if((basicType=in.readByte())!=0)
                break;
            this._ok=in.readBoolean();
        
            break;

        case 43:
        
            this._message=(java.lang.String)converter.read(session);
        
            break;

        case 44:
        
            this._exception=(java.lang.Exception)converter.read(session);
        
            break;

        case 53:
        
            if((basicType=in.readByte())!=7)
                break;
            this._progress=in.readDouble();
        
            break;

        case 55:
        
            this._ticket=(java.lang.String)converter.read(session);
        
            break;

        case 57:
        
            if((basicType=in.readByte())!=4)
                break;
            this._priority=in.readInt();
        
            break;

        case 58:
        
            this._trackedEvents=(java.util.List)converter.read(session);
        
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

if(this._addSuccessful!=false && (0 & transience)==0){
    out.writeByte(11);
        
    out.writeByte(0);
    out.writeBoolean(this._addSuccessful);
        
}

if(this._tables!=null && (0 & transience)==0){
    out.writeByte(12);
        
    converter.write(this._tables,session);
        
}

if(this._ok!=false && (0 & transience)==0){
    out.writeByte(42);
        
    out.writeByte(0);
    out.writeBoolean(this._ok);
        
}

if(this._message!=null && (0 & transience)==0){
    out.writeByte(43);
        
    converter.write(this._message,session);
        
}

if(this._exception!=null && (0 & transience)==0){
    out.writeByte(44);
        
    converter.write(this._exception,session);
        
}

if(this._progress!=0D && (0 & transience)==0){
    out.writeByte(53);
        
    out.writeByte(7);
    out.writeDouble(this._progress);
        
}

if(this._ticket!=null && (0 & transience)==0){
    out.writeByte(55);
        
    converter.write(this._ticket,session);
        
}

if(this._priority!=0 && (0 & transience)==0){
    out.writeByte(57);
        
    out.writeByte(4);
    out.writeInt(this._priority);
        
}

if(this._trackedEvents!=null && (0 & transience)==0){
    out.writeByte(58);
        
    converter.write(this._trackedEvents,session);
        
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