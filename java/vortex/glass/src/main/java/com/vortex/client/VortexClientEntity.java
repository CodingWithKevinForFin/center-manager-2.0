package com.vortex.client;

import com.f1.base.PartialMessage;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.VH;
import com.f1.vortexcommon.msg.VortexEntity;

public class VortexClientEntity<T extends VortexEntity> {

	public static final byte STATE_MATCHED = 1;
	public static final byte STATE_NO_MATCH = 2;
	private T data;
	private VortexClientMachine parent;
	final private byte type;
	private VortexClientExpectation matchingExpectation;

	public VortexClientEntity(byte type, T data) {
		this.data = (T) data.clone();
		this.type = type;
	}

	public void setData(T data) {
		if (this.data != null) {
			OH.assertEq(data.getId(), this.data.getId());
		}
		this.data = data;
	}

	public void setMachine(VortexClientMachine machine) {
		this.parent = machine;
	}
	public VortexClientMachine getMachine() {
		return parent;
	}

	public T getData() {
		return data;
	}

	public long getId() {
		return data.getId();
	}
	public void update(T data) {
		if (data instanceof PartialMessage) {
			VH.copyPartialFields((PartialMessage) data, (PartialMessage) this.data);
		} else
			VH.copyFields(data, this.data);
	}

	public byte getType() {
		return type;
	}

	public String getHostName() {
		if (parent == null)
			return "N/A";
		//throw new NullPointerException("for type: " + getClass().getSimpleName());
		return parent.getData().getHostName();
	}

	public void unbind() {
		setMachine(null);
	}

	public VortexClientExpectation getMatchingExpectation() {
		return matchingExpectation;
	}
	public void setMatchingExpectation(VortexClientExpectation expectation) {
		this.matchingExpectation = expectation;
	}

	@Override
	public String toString() {
		return SH.s(getData(), new StringBuilder().append(getClass()).append(':')).toString();
	}

	public byte getExpectationState() {
		return getMatchingExpectation() == null ? STATE_NO_MATCH : STATE_MATCHED;
	}

}
