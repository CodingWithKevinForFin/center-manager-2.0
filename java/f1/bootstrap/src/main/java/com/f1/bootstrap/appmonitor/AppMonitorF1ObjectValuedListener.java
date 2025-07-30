package com.f1.bootstrap.appmonitor;

import com.f1.base.BasicTypes;
import com.f1.base.Valued;
import com.f1.base.ValuedListenable;
import com.f1.base.ValuedListener;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.utils.FastByteArrayDataOutputStream;

/**
 * Format: (note, [...] indicates recurring pattern)<BR>
 * [(long)MACHINEID[(byte)PID,(byte)BasicType,(?)value]+(byte)NO_PID]+EOF
 * 
 * @author rcooke
 * 
 */
public class AppMonitorF1ObjectValuedListener implements ValuedListener {

	private boolean isRecording = false;
	private FastByteArrayDataOutputStream buf = new FastByteArrayDataOutputStream();
	private F1AppEntity lastTarget;
	public byte[] dump() {
		if (!this.isRecording)
			throw new IllegalStateException("not recording");
		if (lastTarget != null) {
			buf.writeByte(Valued.NO_PID);
			lastTarget = null;
		}
		byte[] r = buf.toByteArray();
		buf.reset();
		this.isRecording = false;
		return r;
	}

	public void startRecording() {
		if (this.isRecording)
			throw new IllegalStateException("already recording");
		this.isRecording = true;
	}
	@Override
	public void onValuedAdded(ValuedListenable target) {
	}

	@Override
	public void onValuedRemoved(ValuedListenable target) {
	}

	@Override
	public void onValued(ValuedListenable target, String name, byte pid, Object old, Object value) {
	}

	@Override
	public void onValuedBoolean(ValuedListenable target, String name, byte pid, boolean old, boolean value) {
		if (old == value)
			return;
		onTarget(target, pid);
		buf.writeByte(BasicTypes.PRIMITIVE_BOOLEAN);
		buf.writeBoolean(value);
	}

	@Override
	public void onValuedByte(ValuedListenable target, String name, byte pid, byte old, byte value) {
		if (old == value)
			return;
		onTarget(target, pid);
		buf.writeByte(BasicTypes.PRIMITIVE_BYTE);
		buf.writeByte(value);
	}

	@Override
	public void onValuedChar(ValuedListenable target, String name, byte pid, char old, char value) {
		if (old == value)
			return;
		onTarget(target, pid);
		buf.writeByte(BasicTypes.PRIMITIVE_CHAR);
		buf.writeChar(value);

	}

	@Override
	public void onValuedShort(ValuedListenable target, String name, byte pid, short old, short value) {
		if (old == value)
			return;
		onTarget(target, pid);
		buf.writeByte(BasicTypes.PRIMITIVE_SHORT);
		buf.writeShort(value);

	}

	@Override
	public void onValuedInt(ValuedListenable target, String name, byte pid, int old, int value) {
		if (old == value)
			return;
		onTarget(target, pid);
		buf.writeByte(BasicTypes.PRIMITIVE_INT);
		buf.writeInt(value);

	}

	@Override
	public void onValuedLong(ValuedListenable target, String name, byte pid, long old, long value) {
		if (old == value)
			return;
		onTarget(target, pid);
		buf.writeByte(BasicTypes.PRIMITIVE_LONG);
		buf.writeLong(value);

	}

	@Override
	public void onValuedFloat(ValuedListenable target, String name, byte pid, float old, float value) {
		if (old == value)
			return;
		onTarget(target, pid);
		buf.writeByte(BasicTypes.PRIMITIVE_FLOAT);
		buf.writeFloat(value);
	}

	@Override
	public void onValuedDouble(ValuedListenable target, String name, byte pid, double old, double value) {
		if (old == value)
			return;
		onTarget(target, pid);
		buf.writeByte(BasicTypes.PRIMITIVE_DOUBLE);
		buf.writeDouble(value);

	}

	private void onTarget(ValuedListenable target2, byte pid) {
		F1AppEntity target = (F1AppEntity) target2;
		if (!isRecording)
			throw new IllegalStateException("Not recording");
		if (target != this.lastTarget) {
			if (this.lastTarget != null) {//this is not the first, so end the last one
				buf.writeByte(Valued.NO_PID);
			}
			this.lastTarget = target;
			buf.writeLong(target.getId());
		}
		buf.writeByte(pid);
	}
}
