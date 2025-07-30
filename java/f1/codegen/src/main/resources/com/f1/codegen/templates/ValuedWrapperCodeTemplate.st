package $c.packageName$;

import java.io.IOException;
import com.f1.base.*;
import com.f1.utils.converter.bytes.ByteArraySelfConverter;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;


public $if(isAbstract)$abstract$endif$ class $c.simpleClassName$ $c.extendsClause$ $c.innerClass.canonicalName$ $c.implementsClause$ com.f1.base.CodeGenerated $if(c.supportsPids)$,ByteArraySelfConverter$endif${

	protected $wrappedClass$ inner; 
	
	@Override
	public Class<? extends Valued> getInnerType(){
	    return $wrappedClass$.class;
	}
	
	@Override
	public Valued getInner(){
	    return inner;
	}
	
	
	@Override
	public Class askClass(String name) {
		return inner.askClass(name);
	}

    @Override
	public Class<Valued> askType() {
		return inner.askType();
	}

    @Override
	public boolean askAckIsDup() {
		return inner.askAckIsDup();
	}

    @Override
	public byte askBasicType(String name) {
		return inner.askBasicType(name);
	}

    @Override
	public Valued nw() {
		return inner.nw();
	}

    @Override
	public Valued nw(Object[] args) {
		return inner.nw(args);
	}

    @Override
	public int askAckId() {
		return inner.askAckId();
	}

    @Override
	public ValuedSchema askSchema() {
		return inner.askSchema();
	}

    @Override
	public String[] askParams() {
		return inner.askParams();
	}

    @Override
	public Valued nwCast(Class[] types, Object[] args) {
		return inner.nwCast(types, args);
	}

    @Override
	public void putAckId(int ackId) {
		inner.putAckId(ackId);
	}

	@Override
	public ValuedParam[] askValuedParams() {
		return inner.askValuedParams();
	}

	@Override
	public ValuedParam askValuedParam(byte pid) {
		return inner.askValuedParam(pid);
	}
	@Override
	public ValuedParam askValuedParam(String param) {
		return inner.askValuedParam(param);
	}

    @Override
	public void put(String name, Object value) {
		inner.put(name, value);
	}
	
	@Override
	public int askPosition(byte pid) {
		return inner.askPosition(pid);
	}

	@Override
	public int askPosition(String name) {
		return inner.askPosition(name);
	}

    @Override
	public void ack() {
		inner.ack();
	}

    @Override
	public Class askClass(byte pid) {
		return inner.askClass(pid);
	}

    @Override
	public boolean putNoThrow(String name, Object value) {
		return inner.putNoThrow(name, value);
	}

    @Override
	public void registerAcker(Acker acker) {
		inner.registerAcker(acker);
	}

    @Override
	public byte askBasicType(byte pid) {
		return inner.askBasicType(pid);
	}

    @Override
	public void transferAckerTo(Ackable ackable) {
		inner.transferAckerTo(ackable);
	}

    @Override
	public Object ask(String name) {
		return inner.ask(name);
	}

    @Override
	public int askParamsCount() {
		return inner.askParamsCount();
	}

    @Override
	public boolean askParamValid(String param) {
		return inner.askParamValid(param);
	}

    @Override
	public void put(byte pid, Object value) {
		inner.put(pid, value);
	}

    @Override
	public boolean askPidValid(byte pid) {
		return inner.askPidValid(pid);
	}

    @Override
	public boolean putNoThrow(byte pid, Object value) {
		return inner.putNoThrow(pid, value);
	}

    @Override
	public boolean askSupportsPids() {
		return inner.askSupportsPids();
	}

    @Override
	public Object ask(byte pid) {
		return inner.ask(pid);
	}

    @Override
	public byte askPid(String param) {
		return inner.askPid(param);
	}

    @Override
	public boolean askBoolean(byte pid) {
		return inner.askBoolean(pid);
	}

    @Override
	public String askParam(byte pid) {
		return inner.askParam(pid);
	}

    @Override
	public byte[] askPids() {
		return inner.askPids();
	}

    @Override
	public byte askByte(byte pid) {
		return inner.askByte(pid);
	}

    @Override
	public Class askOriginalType() {
		return inner.askOriginalType();
	}

    @Override
	public short askShort(byte pid) {
		return inner.askShort(pid);
	}

    @Override
	public char askChar(byte pid) {
		return inner.askChar(pid);
	}

    @Override
	public int askInt(byte pid) {
		return inner.askInt(pid);
	}

    @Override
	public float askFloat(byte pid) {
		return inner.askFloat(pid);
	}

    @Override
	public long askLong(byte pid) {
		return inner.askLong(pid);
	}

    @Override
	public double askDouble(byte pid) {
		return inner.askDouble(pid);
	}

    @Override
	public void putBoolean(byte pid, boolean value) {
		inner.putBoolean(pid, value);
	}

    @Override
	public void putByte(byte pid, byte value) {
		inner.putByte(pid, value);
	}

    @Override
	public void putShort(byte pid, short value) {
		inner.putShort(pid, value);
	}

    @Override
	public void putChar(byte pid, char value) {
		inner.putChar(pid, value);
	}

    @Override
	public void putInt(byte pid, int value) {
		inner.putInt(pid, value);
	}

    @Override
	public void putFloat(byte pid, float value) {
		inner.putFloat(pid, value);
	}

    @Override
	public void putLong(byte pid, long value) {
		inner.putLong(pid, value);
	}

    @Override
	public void putDouble(byte pid, double value) {
		inner.putDouble(pid, value);
	}


	@Override
	public void init(Valued inner) {
		this.inner = ($wrappedClass$)inner;
	}
	
	
$c.params:{ x |
$if(c.supportsPids)$
    @PID($x.annotations.PID_value$)
$endif$

	@Override
    public $x.type.canonicalName$ get$x.upperCasedName$(){
        return inner.get$x.upperCasedName$();
    }
	@Override
    public void set$x.upperCasedName$($x.type.canonicalName$ $x.name$){
        inner.set$x.upperCasedName$($x.name$);
    }
}$

$if(c.supportsPids)$
	@Override
	public void read(FromByteArrayConverterSession session) throws IOException{
	    ((ByteArraySelfConverter)inner).read(session);
	}

	@Override
	public void write(ToByteArrayConverterSession session,byte skipTransienceMask) throws IOException{
	    ((ByteArraySelfConverter)inner).write(session,skipTransienceMask);
	}
$endif$

}