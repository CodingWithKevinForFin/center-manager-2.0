//Coded by ValuedCodeTemplate
package com.f1.ami.amicommon.centerclient;

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

public abstract class AmiCenterClientGetSnapshotResponse0 implements com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotResponse ,  com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,Cloneable{

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

    private java.lang.String _processUid;

    private long _seqNum;

    private static final String NAMES[]={ "centerId" ,"processUid","seqNum"};

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 5;
        try{
        switch(h){

                case 0:

                    if(name == "processUid" || name.equals("processUid")) {this._processUid=(java.lang.String)value;return;}
break;
                case 2:

                    if(name == "centerId" || name.equals("centerId")) {this._centerId=(java.lang.Byte)value;return;}
break;
                case 3:

                    if(name == "seqNum" || name.equals("seqNum")) {this._seqNum=(java.lang.Long)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 0:

                    if(name == "processUid" || name.equals("processUid")) {this._processUid=(java.lang.String)value;return true;}
break;
                case 2:

                    if(name == "centerId" || name.equals("centerId")) {this._centerId=(java.lang.Byte)value;return true;}
break;
                case 3:

                    if(name == "seqNum" || name.equals("seqNum")) {this._seqNum=(java.lang.Long)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 0:

		    
                    if(name == "processUid" || name.equals("processUid")) {return this._processUid;}
            
break;
                case 2:

		    
                    if(name == "centerId" || name.equals("centerId")) {return OH.valueOf(this._centerId);}
		    
break;
                case 3:

		    
                    if(name == "seqNum" || name.equals("seqNum")) {return OH.valueOf(this._seqNum);}
		    
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 0:

                    if(name == "processUid" || name.equals("processUid")) {return java.lang.String.class;}
break;
                case 2:

                    if(name == "centerId" || name.equals("centerId")) {return byte.class;}
break;
                case 3:

                    if(name == "seqNum" || name.equals("seqNum")) {return long.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 0:

                    if(name == "processUid" || name.equals("processUid")) {return VALUED_PARAM_processUid;}
break;
                case 2:

                    if(name == "centerId" || name.equals("centerId")) {return VALUED_PARAM_centerId;}
break;
                case 3:

                    if(name == "seqNum" || name.equals("seqNum")) {return VALUED_PARAM_seqNum;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 0:

                    if(name == "processUid" || name.equals("processUid")) {return 1;}
break;
                case 2:

                    if(name == "centerId" || name.equals("centerId")) {return 0;}
break;
                case 3:

                    if(name == "seqNum" || name.equals("seqNum")) {return 2;}
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
        return 3;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)AmiCenterClientGetSnapshotResponse0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 0:

                    if(name == "processUid" || name.equals("processUid")) {return true;}
break;
                case 2:

                    if(name == "centerId" || name.equals("centerId")) {return true;}
break;
                case 3:

                    if(name == "seqNum" || name.equals("seqNum")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 5;
        switch(h){

                case 0:

                    if(name == "processUid" || name.equals("processUid")) {return 20;}
break;
                case 2:

                    if(name == "centerId" || name.equals("centerId")) {return 1;}
break;
                case 3:

                    if(name == "seqNum" || name.equals("seqNum")) {return 6;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _centerId;

        case 1:return _processUid;

        case 2:return _seqNum;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 3);
    }

    public byte getCenterId(){
        return this._centerId;
    }
    public void setCenterId(byte _centerId){
    
        this._centerId=_centerId;
    }

    public java.lang.String getProcessUid(){
        return this._processUid;
    }
    public void setProcessUid(java.lang.String _processUid){
    
        this._processUid=_processUid;
    }

    public long getSeqNum(){
        return this._seqNum;
    }
    public void setSeqNum(long _seqNum){
    
        this._seqNum=_seqNum;
    }





  
    private static final class VALUED_PARAM_CLASS_centerId extends AbstractValuedParam<AmiCenterClientGetSnapshotResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 1;
	    }
	    
	    @Override
	    public void write(AmiCenterClientGetSnapshotResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeByte(valued.getCenterId());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientGetSnapshotResponse0 valued, DataInput stream) throws IOException{
		    
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "centerId";
	    }
    
	    @Override
	    public Object getValue(AmiCenterClientGetSnapshotResponse0 valued) {
		    return (byte)((AmiCenterClientGetSnapshotResponse0)valued).getCenterId();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientGetSnapshotResponse0 valued, Object value) {
		    valued.setCenterId((java.lang.Byte)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientGetSnapshotResponse0 source, AmiCenterClientGetSnapshotResponse0 dest) {
		    dest.setCenterId(source.getCenterId());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientGetSnapshotResponse0 source, AmiCenterClientGetSnapshotResponse0 dest) {
	        return OH.eq(dest.getCenterId(),source.getCenterId());
	    }
	    
	    
	    
	    
	    @Override
	    public byte getByte(AmiCenterClientGetSnapshotResponse0 valued) {
		    return valued.getCenterId();
	    }
    
	    @Override
	    public void setByte(AmiCenterClientGetSnapshotResponse0 valued, byte value) {
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
	    public void append(AmiCenterClientGetSnapshotResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getCenterId());
	        
	    }
	    @Override
	    public void append(AmiCenterClientGetSnapshotResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getCenterId());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:byte centerId";
	    }
	    @Override
	    public void clear(AmiCenterClientGetSnapshotResponse0 valued){
	       valued.setCenterId((byte)0);
	    }
	};
    private static final ValuedParam VALUED_PARAM_centerId=new VALUED_PARAM_CLASS_centerId();
  

  
    private static final class VALUED_PARAM_CLASS_processUid extends AbstractValuedParam<AmiCenterClientGetSnapshotResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 20;
	    }
	    
	    @Override
	    public void write(AmiCenterClientGetSnapshotResponse0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: java.lang.String}");
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientGetSnapshotResponse0 valued, DataInput stream) throws IOException{
		    
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "processUid";
	    }
    
	    @Override
	    public Object getValue(AmiCenterClientGetSnapshotResponse0 valued) {
		    return (java.lang.String)((AmiCenterClientGetSnapshotResponse0)valued).getProcessUid();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientGetSnapshotResponse0 valued, Object value) {
		    valued.setProcessUid((java.lang.String)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientGetSnapshotResponse0 source, AmiCenterClientGetSnapshotResponse0 dest) {
		    dest.setProcessUid(source.getProcessUid());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientGetSnapshotResponse0 source, AmiCenterClientGetSnapshotResponse0 dest) {
	        return OH.eq(dest.getProcessUid(),source.getProcessUid());
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
	    public void append(AmiCenterClientGetSnapshotResponse0 valued, StringBuilder sb){
	        
	           SH.quote('"',valued.getProcessUid(),sb);
	        
	    }
	    @Override
	    public void append(AmiCenterClientGetSnapshotResponse0 valued, StringBuildable sb){
	        
	           SH.quote('"',valued.getProcessUid(),sb);
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:java.lang.String processUid";
	    }
	    @Override
	    public void clear(AmiCenterClientGetSnapshotResponse0 valued){
	       valued.setProcessUid(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_processUid=new VALUED_PARAM_CLASS_processUid();
  

  
    private static final class VALUED_PARAM_CLASS_seqNum extends AbstractValuedParam<AmiCenterClientGetSnapshotResponse0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 6;
	    }
	    
	    @Override
	    public void write(AmiCenterClientGetSnapshotResponse0 valued, DataOutput stream) throws IOException{
		    
		      stream.writeLong(valued.getSeqNum());
		    
	    }
	    
	    @Override
	    public void read(AmiCenterClientGetSnapshotResponse0 valued, DataInput stream) throws IOException{
		    
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
		    return 2;
	    }
    
	    @Override
	    public byte getPid() {
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "seqNum";
	    }
    
	    @Override
	    public Object getValue(AmiCenterClientGetSnapshotResponse0 valued) {
		    return (long)((AmiCenterClientGetSnapshotResponse0)valued).getSeqNum();
	    }
    
	    @Override
	    public void setValue(AmiCenterClientGetSnapshotResponse0 valued, Object value) {
		    valued.setSeqNum((java.lang.Long)value);
	    }
    
	    @Override
	    public void copy(AmiCenterClientGetSnapshotResponse0 source, AmiCenterClientGetSnapshotResponse0 dest) {
		    dest.setSeqNum(source.getSeqNum());
	    }
	    
	    @Override
	    public boolean areEqual(AmiCenterClientGetSnapshotResponse0 source, AmiCenterClientGetSnapshotResponse0 dest) {
	        return OH.eq(dest.getSeqNum(),source.getSeqNum());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @Override
	    public long getLong(AmiCenterClientGetSnapshotResponse0 valued) {
		    return valued.getSeqNum();
	    }
    
	    @Override
	    public void setLong(AmiCenterClientGetSnapshotResponse0 valued, long value) {
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
	    public void append(AmiCenterClientGetSnapshotResponse0 valued, StringBuilder sb){
	        
	        sb.append(valued.getSeqNum());
	        
	    }
	    @Override
	    public void append(AmiCenterClientGetSnapshotResponse0 valued, StringBuildable sb){
	        
	        sb.append(valued.getSeqNum());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:long seqNum";
	    }
	    @Override
	    public void clear(AmiCenterClientGetSnapshotResponse0 valued){
	       valued.setSeqNum(0L);
	    }
	};
    private static final ValuedParam VALUED_PARAM_seqNum=new VALUED_PARAM_CLASS_seqNum();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_centerId, VALUED_PARAM_processUid, VALUED_PARAM_seqNum, };


    public boolean askSupportsPids(){
        return false;
    }
    
    public byte[] askPids(){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
        
    }

    public String askParam(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public int askPosition(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public byte askPid(String name){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public Object ask(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public Class askClass(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public byte askBasicType(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public void put(byte pid,Object value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public boolean putNoThrow(byte pid,Object value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public boolean askBoolean(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public byte askByte(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public short askShort(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public char askChar(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public int askInt(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public float askFloat(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public long askLong(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public double askDouble(byte pid){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putBoolean(byte pid, boolean value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putByte(byte pid, byte value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putShort(byte pid, short value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putChar(byte pid, char value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putInt(byte pid, int value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putFloat(byte pid, float value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putLong(byte pid, long value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }

    public void putDouble(byte pid, double value){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
        
    public boolean askPidValid(byte param){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
    }
    
    public ValuedParam askValuedParam(byte param){
        throw new UnsupportedOperationException("PIDS NOT SUPPORTED");
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