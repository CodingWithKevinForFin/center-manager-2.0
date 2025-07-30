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

public abstract class AmiRelayMachine0 implements com.f1.ami.amicommon.msg.AmiRelayMachine ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

    private int hashCode=ValuedHashCodeGenerator.next(this);
    
    @Override
    public int hashCode(){
      return this.hashCode;
    }
    
    @Override
	public com.f1.ami.amicommon.msg.AmiRelayMachine clone(){
       try{
         
	       final com.f1.ami.amicommon.msg.AmiRelayMachine r= (com.f1.ami.amicommon.msg.AmiRelayMachine)super.clone();
	       ((AmiRelayMachine0)r).__locked=false;
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
	
	
    

    private int _amiServerPort;

    private java.lang.String _hostName;

    private long _id;

    private java.lang.String _machineUid;

    private long _modifiedOn;

    private java.lang.String _relayProcessUid;

    private long _startTime;

    private static final String NAMES[]={ "amiServerPort" ,"hostName","id","machineUid","modifiedOn","relayProcessUid","startTime"};

	@Override
    public void put(String name, Object value){//asdf
    
	   if(__locked)
	       throw newLockedException(name,value);
	
        final int h=Math.abs(name.hashCode()) % 16;
        try{
        switch(h){

                case 1:

                    if(name == "startTime" || name.equals("startTime")) {this._startTime=(java.lang.Long)value;return;}
break;
                case 7:

                    if(name == "machineUid" || name.equals("machineUid")) {this._machineUid=(java.lang.String)value;return;}
break;
                case 8:

                    if(name == "modifiedOn" || name.equals("modifiedOn")) {this._modifiedOn=(java.lang.Long)value;return;}
break;
                case 11:

                    if(name == "id" || name.equals("id")) {this._id=(java.lang.Long)value;return;}
break;
                case 13:

                    if(name == "hostName" || name.equals("hostName")) {this._hostName=(java.lang.String)value;return;}
break;
                case 14:

                    if(name == "relayProcessUid" || name.equals("relayProcessUid")) {this._relayProcessUid=(java.lang.String)value;return;}
break;
                case 15:

                    if(name == "amiServerPort" || name.equals("amiServerPort")) {this._amiServerPort=(java.lang.Integer)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 16;
        switch(h){

                case 1:

                    if(name == "startTime" || name.equals("startTime")) {this._startTime=(java.lang.Long)value;return true;}
break;
                case 7:

                    if(name == "machineUid" || name.equals("machineUid")) {this._machineUid=(java.lang.String)value;return true;}
break;
                case 8:

                    if(name == "modifiedOn" || name.equals("modifiedOn")) {this._modifiedOn=(java.lang.Long)value;return true;}
break;
                case 11:

                    if(name == "id" || name.equals("id")) {this._id=(java.lang.Long)value;return true;}
break;
                case 13:

                    if(name == "hostName" || name.equals("hostName")) {this._hostName=(java.lang.String)value;return true;}
break;
                case 14:

                    if(name == "relayProcessUid" || name.equals("relayProcessUid")) {this._relayProcessUid=(java.lang.String)value;return true;}
break;
                case 15:

                    if(name == "amiServerPort" || name.equals("amiServerPort")) {this._amiServerPort=(java.lang.Integer)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 16;
        switch(h){

                case 1:

		    
                    if(name == "startTime" || name.equals("startTime")) {return OH.valueOf(this._startTime);}
		    
break;
                case 7:

		    
                    if(name == "machineUid" || name.equals("machineUid")) {return this._machineUid;}
            
break;
                case 8:

		    
                    if(name == "modifiedOn" || name.equals("modifiedOn")) {return OH.valueOf(this._modifiedOn);}
		    
break;
                case 11:

		    
                    if(name == "id" || name.equals("id")) {return OH.valueOf(this._id);}
		    
break;
                case 13:

		    
                    if(name == "hostName" || name.equals("hostName")) {return this._hostName;}
            
break;
                case 14:

		    
                    if(name == "relayProcessUid" || name.equals("relayProcessUid")) {return this._relayProcessUid;}
            
break;
                case 15:

		    
                    if(name == "amiServerPort" || name.equals("amiServerPort")) {return OH.valueOf(this._amiServerPort);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 16;
        switch(h){

                case 1:

                    if(name == "startTime" || name.equals("startTime")) {return long.class;}
break;
                case 7:

                    if(name == "machineUid" || name.equals("machineUid")) {return java.lang.String.class;}
break;
                case 8:

                    if(name == "modifiedOn" || name.equals("modifiedOn")) {return long.class;}
break;
                case 11:

                    if(name == "id" || name.equals("id")) {return long.class;}
break;
                case 13:

                    if(name == "hostName" || name.equals("hostName")) {return java.lang.String.class;}
break;
                case 14:

                    if(name == "relayProcessUid" || name.equals("relayProcessUid")) {return java.lang.String.class;}
break;
                case 15:

                    if(name == "amiServerPort" || name.equals("amiServerPort")) {return int.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 16;
        switch(h){

                case 1:

                    if(name == "startTime" || name.equals("startTime")) {return VALUED_PARAM_startTime;}
break;
                case 7:

                    if(name == "machineUid" || name.equals("machineUid")) {return VALUED_PARAM_machineUid;}
break;
                case 8:

                    if(name == "modifiedOn" || name.equals("modifiedOn")) {return VALUED_PARAM_modifiedOn;}
break;
                case 11:

                    if(name == "id" || name.equals("id")) {return VALUED_PARAM_id;}
break;
                case 13:

                    if(name == "hostName" || name.equals("hostName")) {return VALUED_PARAM_hostName;}
break;
                case 14:

                    if(name == "relayProcessUid" || name.equals("relayProcessUid")) {return VALUED_PARAM_relayProcessUid;}
break;
                case 15:

                    if(name == "amiServerPort" || name.equals("amiServerPort")) {return VALUED_PARAM_amiServerPort;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 16;
        switch(h){

                case 1:

                    if(name == "startTime" || name.equals("startTime")) {return 6;}
break;
                case 7:

                    if(name == "machineUid" || name.equals("machineUid")) {return 3;}
break;
                case 8:

                    if(name == "modifiedOn" || name.equals("modifiedOn")) {return 4;}
break;
                case 11:

                    if(name == "id" || name.equals("id")) {return 2;}
break;
                case 13:

                    if(name == "hostName" || name.equals("hostName")) {return 1;}
break;
                case 14:

                    if(name == "relayProcessUid" || name.equals("relayProcessUid")) {return 5;}
break;
                case 15:

                    if(name == "amiServerPort" || name.equals("amiServerPort")) {return 0;}
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
	    return (Class)AmiRelayMachine0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 16;
        switch(h){

                case 1:

                    if(name == "startTime" || name.equals("startTime")) {return true;}
break;
                case 7:

                    if(name == "machineUid" || name.equals("machineUid")) {return true;}
break;
                case 8:

                    if(name == "modifiedOn" || name.equals("modifiedOn")) {return true;}
break;
                case 11:

                    if(name == "id" || name.equals("id")) {return true;}
break;
                case 13:

                    if(name == "hostName" || name.equals("hostName")) {return true;}
break;
                case 14:

                    if(name == "relayProcessUid" || name.equals("relayProcessUid")) {return true;}
break;
                case 15:

                    if(name == "amiServerPort" || name.equals("amiServerPort")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 16;
        switch(h){

                case 1:

                    if(name == "startTime" || name.equals("startTime")) {return 6;}
break;
                case 7:

                    if(name == "machineUid" || name.equals("machineUid")) {return 20;}
break;
                case 8:

                    if(name == "modifiedOn" || name.equals("modifiedOn")) {return 6;}
break;
                case 11:

                    if(name == "id" || name.equals("id")) {return 6;}
break;
                case 13:

                    if(name == "hostName" || name.equals("hostName")) {return 20;}
break;
                case 14:

                    if(name == "relayProcessUid" || name.equals("relayProcessUid")) {return 20;}
break;
                case 15:

                    if(name == "amiServerPort" || name.equals("amiServerPort")) {return 4;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _amiServerPort;

        case 1:return _hostName;

        case 2:return _id;

        case 3:return _machineUid;

        case 4:return _modifiedOn;

        case 5:return _relayProcessUid;

        case 6:return _startTime;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 7);
    }

    public int getAmiServerPort(){
        return this._amiServerPort;
    }
    public void setAmiServerPort(int _amiServerPort){
    
	   if(__locked)
	       throw newLockedException("amiServerPort",_amiServerPort);
	
        this._amiServerPort=_amiServerPort;
    }

    public java.lang.String getHostName(){
        return this._hostName;
    }
    public void setHostName(java.lang.String _hostName){
    
	   if(__locked)
	       throw newLockedException("hostName",_hostName);
	
        this._hostName=_hostName;
    }

    public long getId(){
        return this._id;
    }
    public void setId(long _id){
    
	   if(__locked)
	       throw newLockedException("id",_id);
	
        this._id=_id;
    }

    public java.lang.String getMachineUid(){
        return this._machineUid;
    }
    public void setMachineUid(java.lang.String _machineUid){
    
	   if(__locked)
	       throw newLockedException("machineUid",_machineUid);
	
        this._machineUid=_machineUid;
    }

    public long getModifiedOn(){
        return this._modifiedOn;
    }
    public void setModifiedOn(long _modifiedOn){
    
	   if(__locked)
	       throw newLockedException("modifiedOn",_modifiedOn);
	
        this._modifiedOn=_modifiedOn;
    }

    public java.lang.String getRelayProcessUid(){
        return this._relayProcessUid;
    }
    public void setRelayProcessUid(java.lang.String _relayProcessUid){
    
	   if(__locked)
	       throw newLockedException("relayProcessUid",_relayProcessUid);
	
        this._relayProcessUid=_relayProcessUid;
    }

    public long getStartTime(){
        return this._startTime;
    }
    public void setStartTime(long _startTime){
    
	   if(__locked)
	       throw newLockedException("startTime",_startTime);
	
        this._startTime=_startTime;
    }





  
    private static final class VALUED_PARAM_CLASS_amiServerPort extends AbstractValuedParam<AmiRelayMachine0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 4;
	    }
	    
	    @Override
	    public void write(AmiRelayMachine0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeInt(valued.getAmiServerPort());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayMachine0 valued, DataInput stream) throws IOException{
		    
		      valued.setAmiServerPort(stream.readInt());
		    
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
            return 49;
	    }
    
	    @Override
	    public String getName() {
            return "amiServerPort";
	    }
    
	    @Override
	    public Object getValue(AmiRelayMachine0 valued) {
		    return (int)((AmiRelayMachine0)valued).getAmiServerPort();
	    }
    
	    @Override
	    public void setValue(AmiRelayMachine0 valued, Object value) {
		    valued.setAmiServerPort((java.lang.Integer)value);
	    }
    
	    @Override
	    public void copy(AmiRelayMachine0 source, AmiRelayMachine0 dest) {
		    dest.setAmiServerPort(source.getAmiServerPort());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayMachine0 source, AmiRelayMachine0 dest) {
	        return OH.eq(dest.getAmiServerPort(),source.getAmiServerPort());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public int getInt(AmiRelayMachine0 valued) {
		    return valued.getAmiServerPort();
	    }
    
	    @Override
	    public void setInt(AmiRelayMachine0 valued, int value) {
		    valued.setAmiServerPort(value);
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
	    public void append(AmiRelayMachine0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAmiServerPort());
	        
	    }
	    @Override
	    public void append(AmiRelayMachine0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAmiServerPort());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:int amiServerPort";
	    }
	    @Override
	    public void clear(AmiRelayMachine0 valued){
	       valued.setAmiServerPort(0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_amiServerPort=new VALUED_PARAM_CLASS_amiServerPort();
  

  
    private static final class VALUED_PARAM_CLASS_hostName extends AbstractValuedParam<AmiRelayMachine0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayMachine0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayMachine0 valued, DataInput stream) throws IOException{
		    
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
            return 13;
	    }
    
	    @Override
	    public String getName() {
            return "hostName";
	    }
    
	    @Override
	    public Object getValue(AmiRelayMachine0 valued) {
		    return (java.lang.String)((AmiRelayMachine0)valued).getHostName();
	    }
    
	    @Override
	    public void setValue(AmiRelayMachine0 valued, Object value) {
		    valued.setHostName((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayMachine0 source, AmiRelayMachine0 dest) {
		    dest.setHostName(source.getHostName());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayMachine0 source, AmiRelayMachine0 dest) {
	        return OH.eq(dest.getHostName(),source.getHostName());
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
	    public void append(AmiRelayMachine0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getHostName(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayMachine0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getHostName(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String hostName";
	    }
	    @Override
	    public void clear(AmiRelayMachine0 valued){
	       valued.setHostName(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_hostName=new VALUED_PARAM_CLASS_hostName();
  

  
    private static final class VALUED_PARAM_CLASS_id extends AbstractValuedParam<AmiRelayMachine0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiRelayMachine0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getId());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayMachine0 valued, DataInput stream) throws IOException{
		    
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
		    return 2;
	    }
    
	    @Override
	    public byte getPid() {
            return 22;
	    }
    
	    @Override
	    public String getName() {
            return "id";
	    }
    
	    @Override
	    public Object getValue(AmiRelayMachine0 valued) {
		    return (long)((AmiRelayMachine0)valued).getId();
	    }
    
	    @Override
	    public void setValue(AmiRelayMachine0 valued, Object value) {
		    valued.setId((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiRelayMachine0 source, AmiRelayMachine0 dest) {
		    dest.setId(source.getId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayMachine0 source, AmiRelayMachine0 dest) {
	        return OH.eq(dest.getId(),source.getId());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiRelayMachine0 valued) {
		    return valued.getId();
	    }
    
	    @Override
	    public void setLong(AmiRelayMachine0 valued, long value) {
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
	    public void append(AmiRelayMachine0 valued, StringBuilder sb){
	        
	        sb.append(valued.getId());
	        
	    }
	    @Override
	    public void append(AmiRelayMachine0 valued, StringBuildable sb){
	        
	        sb.append(valued.getId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long id";
	    }
	    @Override
	    public void clear(AmiRelayMachine0 valued){
	       valued.setId(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_id=new VALUED_PARAM_CLASS_id();
  

  
    private static final class VALUED_PARAM_CLASS_machineUid extends AbstractValuedParam<AmiRelayMachine0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayMachine0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayMachine0 valued, DataInput stream) throws IOException{
		    
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
            return 8;
	    }
    
	    @Override
	    public String getName() {
            return "machineUid";
	    }
    
	    @Override
	    public Object getValue(AmiRelayMachine0 valued) {
		    return (java.lang.String)((AmiRelayMachine0)valued).getMachineUid();
	    }
    
	    @Override
	    public void setValue(AmiRelayMachine0 valued, Object value) {
		    valued.setMachineUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayMachine0 source, AmiRelayMachine0 dest) {
		    dest.setMachineUid(source.getMachineUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayMachine0 source, AmiRelayMachine0 dest) {
	        return OH.eq(dest.getMachineUid(),source.getMachineUid());
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
	    public void append(AmiRelayMachine0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getMachineUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayMachine0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getMachineUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String machineUid";
	    }
	    @Override
	    public void clear(AmiRelayMachine0 valued){
	       valued.setMachineUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_machineUid=new VALUED_PARAM_CLASS_machineUid();
  

  
    private static final class VALUED_PARAM_CLASS_modifiedOn extends AbstractValuedParam<AmiRelayMachine0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiRelayMachine0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getModifiedOn());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayMachine0 valued, DataInput stream) throws IOException{
		    
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
            return 23;
	    }
    
	    @Override
	    public String getName() {
            return "modifiedOn";
	    }
    
	    @Override
	    public Object getValue(AmiRelayMachine0 valued) {
		    return (long)((AmiRelayMachine0)valued).getModifiedOn();
	    }
    
	    @Override
	    public void setValue(AmiRelayMachine0 valued, Object value) {
		    valued.setModifiedOn((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiRelayMachine0 source, AmiRelayMachine0 dest) {
		    dest.setModifiedOn(source.getModifiedOn());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayMachine0 source, AmiRelayMachine0 dest) {
	        return OH.eq(dest.getModifiedOn(),source.getModifiedOn());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiRelayMachine0 valued) {
		    return valued.getModifiedOn();
	    }
    
	    @Override
	    public void setLong(AmiRelayMachine0 valued, long value) {
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
	    public void append(AmiRelayMachine0 valued, StringBuilder sb){
	        
	        sb.append(valued.getModifiedOn());
	        
	    }
	    @Override
	    public void append(AmiRelayMachine0 valued, StringBuildable sb){
	        
	        sb.append(valued.getModifiedOn());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long modifiedOn";
	    }
	    @Override
	    public void clear(AmiRelayMachine0 valued){
	       valued.setModifiedOn(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_modifiedOn=new VALUED_PARAM_CLASS_modifiedOn();
  

  
    private static final class VALUED_PARAM_CLASS_relayProcessUid extends AbstractValuedParam<AmiRelayMachine0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayMachine0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayMachine0 valued, DataInput stream) throws IOException{
		    
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
            return 47;
	    }
    
	    @Override
	    public String getName() {
            return "relayProcessUid";
	    }
    
	    @Override
	    public Object getValue(AmiRelayMachine0 valued) {
		    return (java.lang.String)((AmiRelayMachine0)valued).getRelayProcessUid();
	    }
    
	    @Override
	    public void setValue(AmiRelayMachine0 valued, Object value) {
		    valued.setRelayProcessUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayMachine0 source, AmiRelayMachine0 dest) {
		    dest.setRelayProcessUid(source.getRelayProcessUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayMachine0 source, AmiRelayMachine0 dest) {
	        return OH.eq(dest.getRelayProcessUid(),source.getRelayProcessUid());
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
	    public void append(AmiRelayMachine0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getRelayProcessUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayMachine0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getRelayProcessUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String relayProcessUid";
	    }
	    @Override
	    public void clear(AmiRelayMachine0 valued){
	       valued.setRelayProcessUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_relayProcessUid=new VALUED_PARAM_CLASS_relayProcessUid();
  

  
    private static final class VALUED_PARAM_CLASS_startTime extends AbstractValuedParam<AmiRelayMachine0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiRelayMachine0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getStartTime());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayMachine0 valued, DataInput stream) throws IOException{
		    
		      valued.setStartTime(stream.readLong());
		    
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
            return 11;
	    }
    
	    @Override
	    public String getName() {
            return "startTime";
	    }
    
	    @Override
	    public Object getValue(AmiRelayMachine0 valued) {
		    return (long)((AmiRelayMachine0)valued).getStartTime();
	    }
    
	    @Override
	    public void setValue(AmiRelayMachine0 valued, Object value) {
		    valued.setStartTime((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiRelayMachine0 source, AmiRelayMachine0 dest) {
		    dest.setStartTime(source.getStartTime());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayMachine0 source, AmiRelayMachine0 dest) {
	        return OH.eq(dest.getStartTime(),source.getStartTime());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiRelayMachine0 valued) {
		    return valued.getStartTime();
	    }
    
	    @Override
	    public void setLong(AmiRelayMachine0 valued, long value) {
		    valued.setStartTime(value);
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
	    public void append(AmiRelayMachine0 valued, StringBuilder sb){
	        
	        sb.append(valued.getStartTime());
	        
	    }
	    @Override
	    public void append(AmiRelayMachine0 valued, StringBuildable sb){
	        
	        sb.append(valued.getStartTime());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long startTime";
	    }
	    @Override
	    public void clear(AmiRelayMachine0 valued){
	       valued.setStartTime(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_startTime=new VALUED_PARAM_CLASS_startTime();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_amiServerPort, VALUED_PARAM_hostName, VALUED_PARAM_id, VALUED_PARAM_machineUid, VALUED_PARAM_modifiedOn, VALUED_PARAM_relayProcessUid, VALUED_PARAM_startTime, };



    private static final byte PIDS[]={ 49 ,13,22,8,23,47,11};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 49: return VALUED_PARAM_amiServerPort;
             case 13: return VALUED_PARAM_hostName;
             case 22: return VALUED_PARAM_id;
             case 8: return VALUED_PARAM_machineUid;
             case 23: return VALUED_PARAM_modifiedOn;
             case 47: return VALUED_PARAM_relayProcessUid;
             case 11: return VALUED_PARAM_startTime;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 49: return true;
             case 13: return true;
             case 22: return true;
             case 8: return true;
             case 23: return true;
             case 47: return true;
             case 11: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 49: return "amiServerPort";
             case 13: return "hostName";
             case 22: return "id";
             case 8: return "machineUid";
             case 23: return "modifiedOn";
             case 47: return "relayProcessUid";
             case 11: return "startTime";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 49: return 0;
             case 13: return 1;
             case 22: return 2;
             case 8: return 3;
             case 23: return 4;
             case 47: return 5;
             case 11: return 6;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="amiServerPort") return 49;
             if(name=="hostName") return 13;
             if(name=="id") return 22;
             if(name=="machineUid") return 8;
             if(name=="modifiedOn") return 23;
             if(name=="relayProcessUid") return 47;
             if(name=="startTime") return 11;
            
             if("amiServerPort".equals(name)) return 49;
             if("hostName".equals(name)) return 13;
             if("id".equals(name)) return 22;
             if("machineUid".equals(name)) return 8;
             if("modifiedOn".equals(name)) return 23;
             if("relayProcessUid".equals(name)) return 47;
             if("startTime".equals(name)) return 11;
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
             case 49: return  OH.valueOf(this._amiServerPort); 
             case 13: return  this._hostName; 
             case 22: return  OH.valueOf(this._id); 
             case 8: return  this._machineUid; 
             case 23: return  OH.valueOf(this._modifiedOn); 
             case 47: return  this._relayProcessUid; 
             case 11: return  OH.valueOf(this._startTime); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 49: return int.class;
             case 13: return java.lang.String.class;
             case 22: return long.class;
             case 8: return java.lang.String.class;
             case 23: return long.class;
             case 47: return java.lang.String.class;
             case 11: return long.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 49: return 4;
             case 13: return 20;
             case 22: return 6;
             case 8: return 20;
             case 23: return 6;
             case 47: return 20;
             case 11: return 6;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 49: this._amiServerPort=(java.lang.Integer)value;return;
             case 13: this._hostName=(java.lang.String)value;return;
             case 22: this._id=(java.lang.Long)value;return;
             case 8: this._machineUid=(java.lang.String)value;return;
             case 23: this._modifiedOn=(java.lang.Long)value;return;
             case 47: this._relayProcessUid=(java.lang.String)value;return;
             case 11: this._startTime=(java.lang.Long)value;return;
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
             case 49: this._amiServerPort=(java.lang.Integer)value;return true;
             case 13: this._hostName=(java.lang.String)value;return true;
             case 22: this._id=(java.lang.Long)value;return true;
             case 8: this._machineUid=(java.lang.String)value;return true;
             case 23: this._modifiedOn=(java.lang.Long)value;return true;
             case 47: this._relayProcessUid=(java.lang.String)value;return true;
             case 11: this._startTime=(java.lang.Long)value;return true;
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
             case 49: return this._amiServerPort;
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
             case 22: return this._id;
             case 23: return this._modifiedOn;
             case 11: return this._startTime;
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
             case 49: this._amiServerPort=value;return;
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
             case 22: this._id=value;return;
             case 23: this._modifiedOn=value;return;
             case 11: this._startTime=value;return;
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
        
        case 8:
        
            this._machineUid=(java.lang.String)converter.read(session);
        
            break;

        case 11:
        
            if((basicType=in.readByte())!=6)
                break;
            this._startTime=in.readLong();
        
            break;

        case 13:
        
            this._hostName=(java.lang.String)converter.read(session);
        
            break;

        case 22:
        
            if((basicType=in.readByte())!=6)
                break;
            this._id=in.readLong();
        
            break;

        case 23:
        
            if((basicType=in.readByte())!=6)
                break;
            this._modifiedOn=in.readLong();
        
            break;

        case 47:
        
            this._relayProcessUid=(java.lang.String)converter.read(session);
        
            break;

        case 49:
        
            if((basicType=in.readByte())!=4)
                break;
            this._amiServerPort=in.readInt();
        
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
        
if(this._machineUid!=null && (0 & transience)==0){
    out.writeByte(8);
        
    converter.write(this._machineUid,session);
        
}

if(this._startTime!=0L && (0 & transience)==0){
    out.writeByte(11);
        
    out.writeByte(6);
    out.writeLong(this._startTime);
        
}

if(this._hostName!=null && (0 & transience)==0){
    out.writeByte(13);
        
    converter.write(this._hostName,session);
        
}

if(this._id!=0L && (0 & transience)==0){
    out.writeByte(22);
        
    out.writeByte(6);
    out.writeLong(this._id);
        
}

if(this._modifiedOn!=0L && (0 & transience)==0){
    out.writeByte(23);
        
    out.writeByte(6);
    out.writeLong(this._modifiedOn);
        
}

if(this._relayProcessUid!=null && (0 & transience)==0){
    out.writeByte(47);
        
    converter.write(this._relayProcessUid,session);
        
}

if(this._amiServerPort!=0 && (0 & transience)==0){
    out.writeByte(49);
        
    out.writeByte(4);
    out.writeInt(this._amiServerPort);
        
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