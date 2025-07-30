//Coded by ValuedCodeTemplate
package com.f1.suite.web;

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

public abstract class HttpRequestAction0 extends com.f1.suite.web.HttpRequestAction implements com.f1.base.ValuedSchema, com.f1.base.CodeGenerated ,Cloneable{

    private int hashCode=ValuedHashCodeGenerator.next(this);
    
    @Override
    public int hashCode(){
      return this.hashCode;
    }
    
    @Override
	public com.f1.suite.web.HttpRequestAction clone(){
       try{
         
	       return (com.f1.suite.web.HttpRequestAction)super.clone();
	     
       } catch( Exception e){
          throw new RuntimeException("error cloning",e);
       }
    }
    
    
    

    private com.f1.http.HttpRequestResponse _request;

    private static final String NAMES[]={ "request" };

	@Override
    public void put(String name, Object value){//asdf
    
        final int h=Math.abs(name.hashCode()) % 1;
        try{
        switch(h){

                case 0:

                    if(name == "request" || name.equals("request")) {this._request=(com.f1.http.HttpRequestResponse)value;return;}
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
        final int h=Math.abs(name.hashCode()) % 1;
        switch(h){

                case 0:

                    if(name == "request" || name.equals("request")) {this._request=(com.f1.http.HttpRequestResponse)value;return true;}
break;
        }
        return false;
    }

	@Override
    public Object ask(String name){
        final int h=Math.abs(name.hashCode()) % 1;
        switch(h){

                case 0:

		    
                    if(name == "request" || name.equals("request")) {return this._request;}
            
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public Class askClass(String name){
        final int h=Math.abs(name.hashCode()) % 1;
        switch(h){

                case 0:

                    if(name == "request" || name.equals("request")) {return com.f1.http.HttpRequestResponse.class;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
	@Override
    public ValuedParam askValuedParam(String name){
        final int h=Math.abs(name.hashCode()) % 1;
        switch(h){

                case 0:

                    if(name == "request" || name.equals("request")) {return VALUED_PARAM_request;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }

	@Override
    public int askPosition(String name){
        final int h=Math.abs(name.hashCode()) % 1;
        switch(h){

                case 0:

                    if(name == "request" || name.equals("request")) {return 0;}
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
        return 1;
    }

	@Override
	public Class<Valued> askType(){
	    return (Class)HttpRequestAction0.class;
	}

	@Override
    public boolean askParamValid(String name){
        final int h=Math.abs(name.hashCode()) % 1;
        switch(h){

                case 0:

                    if(name == "request" || name.equals("request")) {return true;}
break;
        }
        return false;
    }
    
	@Override
    public byte askBasicType(String name){
        final int h=Math.abs(name.hashCode()) % 1;
        switch(h){

                case 0:

                    if(name == "request" || name.equals("request")) {return 60;}
break;
        }
        throw newMissingValueException(name,NAMES,"no such param name").set("class",askSchema().askOriginalType().getName());
    }
    
    public Object askAtPosition(int position){
        switch(position){

        case 0:return _request;

        }
        throw new IndexOutOfBoundsException("supplied position > param count: "+position+" >= "+ 1);
    }

    public com.f1.http.HttpRequestResponse getRequest(){
        return this._request;
    }
    public void setRequest(com.f1.http.HttpRequestResponse _request){
    
        this._request=_request;
    }





  
    private static final class VALUED_PARAM_CLASS_request extends AbstractValuedParam<HttpRequestAction0> implements  com.f1.base.CodeGenerated {

	    @Override
	    public byte getBasicType(){
	       return 60;
	    }
	    
	    @Override
	    public void write(HttpRequestAction0 valued, DataOutput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.http.HttpRequestResponse}");
		    
	    }
	    
	    @Override
	    public void read(HttpRequestAction0 valued, DataInput stream) throws IOException{
		    
		       throw new IOException("can not write to type to dataStream: com.f1.http.HttpRequestResponse}");
		    
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
            return NO_PID;
	    }
    
	    @Override
	    public String getName() {
            return "request";
	    }
    
	    @Override
	    public Object getValue(HttpRequestAction0 valued) {
		    return (com.f1.http.HttpRequestResponse)((HttpRequestAction0)valued).getRequest();
	    }
    
	    @Override
	    public void setValue(HttpRequestAction0 valued, Object value) {
		    valued.setRequest((com.f1.http.HttpRequestResponse)value);
	    }
    
	    @Override
	    public void copy(HttpRequestAction0 source, HttpRequestAction0 dest) {
		    dest.setRequest(source.getRequest());
	    }
	    
	    @Override
	    public boolean areEqual(HttpRequestAction0 source, HttpRequestAction0 dest) {
	        return OH.eq(dest.getRequest(),source.getRequest());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    @Override
	    public Class getReturnType() {
		    return com.f1.http.HttpRequestResponse.class;
	    }
	    private static final Caster CASTER=OH.getCaster(com.f1.http.HttpRequestResponse.class);
	    @Override
	    public Caster getCaster() {
		    return CASTER;
	    }
	    
	    @Override
	    public void append(HttpRequestAction0 valued, StringBuilder sb){
	        
	        sb.append(valued.getRequest());
	        
	    }
	    @Override
	    public void append(HttpRequestAction0 valued, StringBuildable sb){
	        
	        sb.append(valued.getRequest());
	        
	    }
	    @Override
	    public String toString(){
	        return "ValuedParam:com.f1.http.HttpRequestResponse request";
	    }
	    @Override
	    public void clear(HttpRequestAction0 valued){
	       valued.setRequest(null);
	    }
	};
    private static final ValuedParam VALUED_PARAM_request=new VALUED_PARAM_CLASS_request();
  


    private static final ValuedParam VALUED_PARAMS[]=new ValuedParam[]{ VALUED_PARAM_request, };


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