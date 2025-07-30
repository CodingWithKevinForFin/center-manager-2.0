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

public abstract class AmiRelayChangesMessage0 implements com.f1.ami.amicommon.msg.AmiRelayChangesMessage ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,ByteArraySelfConverter,Cloneable{

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
    
    
    

    private java.lang.String _agentProcessUid;

    private java.util.List _amiEvents;

    private java.util.Map _amiStringPoolMap;

    private long _seqNum;

    private static final String NAMES[]={ "agentProcessUid" ,"amiEvents","amiStringPoolMap","seqNum"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 9;
        try{
        switch(h){

                case 2:

                    if(name == "amiStringPoolMap" || name.equals("amiStringPoolMap")) {this._amiStringPoolMap=(java.util.Map)value;return;}
break;
                case 5:

                    if(name == "amiEvents" || name.equals("amiEvents")) {this._amiEvents=(java.util.List)value;return;}
break;
                case 7:

                    if(name == "seqNum" || name.equals("seqNum")) {this._seqNum=(java.lang.Long)value;return;}
break;
                case 8:

                    if(name == "agentProcessUid" || name.equals("agentProcessUid")) {this._agentProcessUid=(java.lang.String)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 9;
        switch(h){

                case 2:

                    if(name == "amiStringPoolMap" || name.equals("amiStringPoolMap")) {this._amiStringPoolMap=(java.util.Map)value;return true;}
break;
                case 5:

                    if(name == "amiEvents" || name.equals("amiEvents")) {this._amiEvents=(java.util.List)value;return true;}
break;
                case 7:

                    if(name == "seqNum" || name.equals("seqNum")) {this._seqNum=(java.lang.Long)value;return true;}
break;
                case 8:

                    if(name == "agentProcessUid" || name.equals("agentProcessUid")) {this._agentProcessUid=(java.lang.String)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 9;
        switch(h){

                case 2:

		    
                    if(name == "amiStringPoolMap" || name.equals("amiStringPoolMap")) {return this._amiStringPoolMap;}
            
break;
                case 5:

		    
                    if(name == "amiEvents" || name.equals("amiEvents")) {return this._amiEvents;}
            
break;
                case 7:

		    
                    if(name == "seqNum" || name.equals("seqNum")) {return OH.valueOf(this._seqNum);}
		    
break;
                case 8:

		    
                    if(name == "agentProcessUid" || name.equals("agentProcessUid")) {return this._agentProcessUid;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 9;
        switch(h){

                case 2:

                    if(name == "amiStringPoolMap" || name.equals("amiStringPoolMap")) {return java.util.Map.class;}
break;
                case 5:

                    if(name == "amiEvents" || name.equals("amiEvents")) {return java.util.List.class;}
break;
                case 7:

                    if(name == "seqNum" || name.equals("seqNum")) {return long.class;}
break;
                case 8:

                    if(name == "agentProcessUid" || name.equals("agentProcessUid")) {return java.lang.String.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 9;
        switch(h){

                case 2:

                    if(name == "amiStringPoolMap" || name.equals("amiStringPoolMap")) {return VALUED_PARAM_amiStringPoolMap;}
break;
                case 5:

                    if(name == "amiEvents" || name.equals("amiEvents")) {return VALUED_PARAM_amiEvents;}
break;
                case 7:

                    if(name == "seqNum" || name.equals("seqNum")) {return VALUED_PARAM_seqNum;}
break;
                case 8:

                    if(name == "agentProcessUid" || name.equals("agentProcessUid")) {return VALUED_PARAM_agentProcessUid;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 9;
        switch(h){

                case 2:

                    if(name == "amiStringPoolMap" || name.equals("amiStringPoolMap")) {return 2;}
break;
                case 5:

                    if(name == "amiEvents" || name.equals("amiEvents")) {return 1;}
break;
                case 7:

                    if(name == "seqNum" || name.equals("seqNum")) {return 3;}
break;
                case 8:

                    if(name == "agentProcessUid" || name.equals("agentProcessUid")) {return 0;}
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
        return 4;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiRelayChangesMessage0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 9;
        switch(h){

                case 2:

                    if(name == "amiStringPoolMap" || name.equals("amiStringPoolMap")) {return true;}
break;
                case 5:

                    if(name == "amiEvents" || name.equals("amiEvents")) {return true;}
break;
                case 7:

                    if(name == "seqNum" || name.equals("seqNum")) {return true;}
break;
                case 8:

                    if(name == "agentProcessUid" || name.equals("agentProcessUid")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 9;
        switch(h){

                case 2:

                    if(name == "amiStringPoolMap" || name.equals("amiStringPoolMap")) {return 23;}
break;
                case 5:

                    if(name == "amiEvents" || name.equals("amiEvents")) {return 21;}
break;
                case 7:

                    if(name == "seqNum" || name.equals("seqNum")) {return 6;}
break;
                case 8:

                    if(name == "agentProcessUid" || name.equals("agentProcessUid")) {return 20;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _agentProcessUid;

        case 1:return _amiEvents;

        case 2:return _amiStringPoolMap;

        case 3:return _seqNum;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 4);
    }

    public java.lang.String getAgentProcessUid(){
        return this._agentProcessUid;
    }
    public void setAgentProcessUid(java.lang.String _agentProcessUid){
    
        this._agentProcessUid=_agentProcessUid;
    }

    public java.util.List getAmiEvents(){
        return this._amiEvents;
    }
    public void setAmiEvents(java.util.List _amiEvents){
    
        this._amiEvents=_amiEvents;
    }

    public java.util.Map getAmiStringPoolMap(){
        return this._amiStringPoolMap;
    }
    public void setAmiStringPoolMap(java.util.Map _amiStringPoolMap){
    
        this._amiStringPoolMap=_amiStringPoolMap;
    }

    public long getSeqNum(){
        return this._seqNum;
    }
    public void setSeqNum(long _seqNum){
    
        this._seqNum=_seqNum;
    }





  
    private static final class VALUED_PARAM_CLASS_agentProcessUid extends AbstractValuedParam<AmiRelayChangesMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiRelayChangesMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayChangesMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 9;
	    }
    
	    @Override
	    public String getName() {
            return "agentProcessUid";
	    }
    
	    @Override
	    public Object getValue(AmiRelayChangesMessage0 valued) {
		    return (java.lang.String)((AmiRelayChangesMessage0)valued).getAgentProcessUid();
	    }
    
	    @Override
	    public void setValue(AmiRelayChangesMessage0 valued, Object value) {
		    valued.setAgentProcessUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiRelayChangesMessage0 source, AmiRelayChangesMessage0 dest) {
		    dest.setAgentProcessUid(source.getAgentProcessUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayChangesMessage0 source, AmiRelayChangesMessage0 dest) {
	        return OH.eq(dest.getAgentProcessUid(),source.getAgentProcessUid());
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
	    public void append(AmiRelayChangesMessage0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getAgentProcessUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiRelayChangesMessage0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getAgentProcessUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String agentProcessUid";
	    }
	    @Override
	    public void clear(AmiRelayChangesMessage0 valued){
	       valued.setAgentProcessUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_agentProcessUid=new VALUED_PARAM_CLASS_agentProcessUid();
  

  
    private static final class VALUED_PARAM_CLASS_amiEvents extends AbstractValuedParam<AmiRelayChangesMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 21;
	    }
	    
	    @Override
	    public void write(AmiRelayChangesMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.List}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayChangesMessage0 valued, DataInput stream) throws IOException{
		    
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
            return 10;
	    }
    
	    @Override
	    public String getName() {
            return "amiEvents";
	    }
    
	    @Override
	    public Object getValue(AmiRelayChangesMessage0 valued) {
		    return (java.util.List)((AmiRelayChangesMessage0)valued).getAmiEvents();
	    }
    
	    @Override
	    public void setValue(AmiRelayChangesMessage0 valued, Object value) {
		    valued.setAmiEvents((java.util.List)value);
	    }
    
	    @Override
	    public void copy(AmiRelayChangesMessage0 source, AmiRelayChangesMessage0 dest) {
		    dest.setAmiEvents(source.getAmiEvents());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayChangesMessage0 source, AmiRelayChangesMessage0 dest) {
	        return OH.eq(dest.getAmiEvents(),source.getAmiEvents());
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
	    public void append(AmiRelayChangesMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAmiEvents());
	        
	    }
	    @Override
	    public void append(AmiRelayChangesMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAmiEvents());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.List amiEvents";
	    }
	    @Override
	    public void clear(AmiRelayChangesMessage0 valued){
	       valued.setAmiEvents(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_amiEvents=new VALUED_PARAM_CLASS_amiEvents();
  

  
    private static final class VALUED_PARAM_CLASS_amiStringPoolMap extends AbstractValuedParam<AmiRelayChangesMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 23;
	    }
	    
	    @Override
	    public void write(AmiRelayChangesMessage0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Map}");
		    
	    }
	    
	    @Override
	    public void read(AmiRelayChangesMessage0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.util.Map}");
		    
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
            return 11;
	    }
    
	    @Override
	    public String getName() {
            return "amiStringPoolMap";
	    }
    
	    @Override
	    public Object getValue(AmiRelayChangesMessage0 valued) {
		    return (java.util.Map)((AmiRelayChangesMessage0)valued).getAmiStringPoolMap();
	    }
    
	    @Override
	    public void setValue(AmiRelayChangesMessage0 valued, Object value) {
		    valued.setAmiStringPoolMap((java.util.Map)value);
	    }
    
	    @Override
	    public void copy(AmiRelayChangesMessage0 source, AmiRelayChangesMessage0 dest) {
		    dest.setAmiStringPoolMap(source.getAmiStringPoolMap());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayChangesMessage0 source, AmiRelayChangesMessage0 dest) {
	        return OH.eq(dest.getAmiStringPoolMap(),source.getAmiStringPoolMap());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return java.util.Map.class;
	    }
	    private static final Caster CASTER=OH.getCaster(java.util.Map.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(AmiRelayChangesMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getAmiStringPoolMap());
	        
	    }
	    @Override
	    public void append(AmiRelayChangesMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getAmiStringPoolMap());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.util.Map amiStringPoolMap";
	    }
	    @Override
	    public void clear(AmiRelayChangesMessage0 valued){
	       valued.setAmiStringPoolMap(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_amiStringPoolMap=new VALUED_PARAM_CLASS_amiStringPoolMap();
  

  
    private static final class VALUED_PARAM_CLASS_seqNum extends AbstractValuedParam<AmiRelayChangesMessage0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiRelayChangesMessage0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getSeqNum());
		    
	    }
	    
	    @Override
	    public void read(AmiRelayChangesMessage0 valued, DataInput stream) throws IOException{
		    
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
		    return 3;
	    }
    
	    @Override
	    public byte getPid() {
            return 8;
	    }
    
	    @Override
	    public String getName() {
            return "seqNum";
	    }
    
	    @Override
	    public Object getValue(AmiRelayChangesMessage0 valued) {
		    return (long)((AmiRelayChangesMessage0)valued).getSeqNum();
	    }
    
	    @Override
	    public void setValue(AmiRelayChangesMessage0 valued, Object value) {
		    valued.setSeqNum((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiRelayChangesMessage0 source, AmiRelayChangesMessage0 dest) {
		    dest.setSeqNum(source.getSeqNum());
	    }
	    
	    @Override
	    public boolean areEqual(AmiRelayChangesMessage0 source, AmiRelayChangesMessage0 dest) {
	        return OH.eq(dest.getSeqNum(),source.getSeqNum());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiRelayChangesMessage0 valued) {
		    return valued.getSeqNum();
	    }
    
	    @Override
	    public void setLong(AmiRelayChangesMessage0 valued, long value) {
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
	    public void append(AmiRelayChangesMessage0 valued, StringBuilder sb){
	        
	        sb.append(valued.getSeqNum());
	        
	    }
	    @Override
	    public void append(AmiRelayChangesMessage0 valued, StringBuildable sb){
	        
	        sb.append(valued.getSeqNum());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long seqNum";
	    }
	    @Override
	    public void clear(AmiRelayChangesMessage0 valued){
	       valued.setSeqNum(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_seqNum=new VALUED_PARAM_CLASS_seqNum();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_agentProcessUid, VALUED_PARAM_amiEvents, VALUED_PARAM_amiStringPoolMap, VALUED_PARAM_seqNum, };



    private static final byte PIDS[]={ 9 ,10,11,8};
    
    @Override
    public ValuedParam askValuedParam(byte pid){
        switch(pid){
             case 9: return VALUED_PARAM_agentProcessUid;
             case 10: return VALUED_PARAM_amiEvents;
             case 11: return VALUED_PARAM_amiStringPoolMap;
             case 8: return VALUED_PARAM_seqNum;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    
    public boolean askPidValid(byte pid){
        switch(pid){
             case 9: return true;
             case 10: return true;
             case 11: return true;
             case 8: return true;
            default:return false;
        }
    }
    
    
    public String askParam(byte pid){
        switch(pid){
             case 9: return "agentProcessUid";
             case 10: return "amiEvents";
             case 11: return "amiStringPoolMap";
             case 8: return "seqNum";
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public int askPosition(byte pid){
        switch(pid){
             case 9: return 0;
             case 10: return 1;
             case 11: return 2;
             case 8: return 3;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public byte askPid(String name){
             if(name=="agentProcessUid") return 9;
             if(name=="amiEvents") return 10;
             if(name=="amiStringPoolMap") return 11;
             if(name=="seqNum") return 8;
            
             if("agentProcessUid".equals(name)) return 9;
             if("amiEvents".equals(name)) return 10;
             if("amiStringPoolMap".equals(name)) return 11;
             if("seqNum".equals(name)) return 8;
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
             case 9: return  this._agentProcessUid; 
             case 10: return  this._amiEvents; 
             case 11: return  this._amiStringPoolMap; 
             case 8: return  OH.valueOf(this._seqNum); 
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public Class askClass(byte pid){
        switch(pid){
             case 9: return java.lang.String.class;
             case 10: return java.util.List.class;
             case 11: return java.util.Map.class;
             case 8: return long.class;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid").set("class",askSchema().askOriginalType().getName());
        }
    }

    public byte askBasicType(byte pid){
        switch(pid){
             case 9: return 20;
             case 10: return 21;
             case 11: return 23;
             case 8: return 6;
            default:throw newMissingValueException(Byte.toString(pid),PIDS,"no such pid for byte").set("class",askSchema().askOriginalType().getName());
        }
    }
    
    public void put(byte pid,Object value){
        try{
        switch(pid){
             case 9: this._agentProcessUid=(java.lang.String)value;return;
             case 10: this._amiEvents=(java.util.List)value;return;
             case 11: this._amiStringPoolMap=(java.util.Map)value;return;
             case 8: this._seqNum=(java.lang.Long)value;return;
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
             case 9: this._agentProcessUid=(java.lang.String)value;return true;
             case 10: this._amiEvents=(java.util.List)value;return true;
             case 11: this._amiStringPoolMap=(java.util.Map)value;return true;
             case 8: this._seqNum=(java.lang.Long)value;return true;
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
             case 8: return this._seqNum;
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
             case 8: this._seqNum=value;return;
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
        
        case 8:
        
            if((basicType=in.readByte())!=6)
                break;
            this._seqNum=in.readLong();
        
            break;

        case 9:
        
            this._agentProcessUid=(java.lang.String)converter.read(session);
        
            break;

        case 10:
        
            this._amiEvents=(java.util.List)converter.read(session);
        
            break;

        case 11:
        
            this._amiStringPoolMap=(java.util.Map)converter.read(session);
        
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
    out.writeByte(8);
        
    out.writeByte(6);
    out.writeLong(this._seqNum);
        
}

if(this._agentProcessUid!=null && (0 & transience)==0){
    out.writeByte(9);
        
    converter.write(this._agentProcessUid,session);
        
}

if(this._amiEvents!=null && (0 & transience)==0){
    out.writeByte(10);
        
    converter.write(this._amiEvents,session);
        
}

if(this._amiStringPoolMap!=null && (0 & transience)==0){
    out.writeByte(11);
        
    converter.write(this._amiStringPoolMap,session);
        
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