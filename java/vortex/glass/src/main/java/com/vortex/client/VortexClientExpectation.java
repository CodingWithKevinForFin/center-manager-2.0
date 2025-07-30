package com.vortex.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.impl.ConstTextMatcher;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexExpectation;

public class VortexClientExpectation extends VortexClientEntity<VortexExpectation> {

	private VortexClientEntity<?> match;
	private final Map<Byte, TextMatcher> masks = new HashMap<Byte, TextMatcher>();
	private final byte type;

	public VortexClientExpectation(VortexExpectation data) {
		super(VortexAgentEntity.TYPE_EXPECTATION, data);
		this.type = data.getTargetType();
		update(data);
	}

	@Override
	public void update(VortexExpectation data) {
		super.update(data);
		for (Entry<Byte, String> i : CH.entrySet(data.getFieldMasks())) {
			masks.put(i.getKey(), SH.m(i.getValue()));
		}

	}
	public VortexClientEntity<?> getMatch() {
		return match;
	}

	public void bind(VortexClientEntity<?> match) {
		if (match == this.match) {
			return;
		} else if (match == null) {//clearing
			this.match.setMatchingExpectation(null);
			this.match = null;
		} else {//setting
			OH.assertEq(getData().getMachineUid(), match.getMachine().getData().getMachineUid());
			if (this.match != null) {
				this.match.setMatchingExpectation(null);
			}
			this.match = match;
			this.match.setMatchingExpectation(this);
		}
	}

	@Override
	public void setMachine(VortexClientMachine machine) {
		throw new UnsupportedOperationException("I've got the object model wrong :(");
	}

	@Override
	public VortexClientMachine getMachine() {
		throw new UnsupportedOperationException("I've got the object model wrong :(");
	}

	@Override
	public VortexClientExpectation getMatchingExpectation() {
		throw new UnsupportedOperationException("I've got the object model wrong :(");
	}
	@Override
	public void setMatchingExpectation(VortexClientExpectation expectation) {
		throw new UnsupportedOperationException("I've got the object model wrong :(");
	}

	public String getMachineUid() {
		return getData().getMachineUid();
	}
	public byte getTargetType() {
		return getData().getTargetType();
	}

	public boolean matches(VortexClientEntity<?> candidate) {
		switch (type) {
			case VortexAgentEntity.TYPE_FILE_SYSTEM: {
				VortexClientFileSystem node = (VortexClientFileSystem) candidate;
				if (!testMask(VortexExpectation.MASK_TYPE_TYPE, node.getData().getType()))
					return false;
				if (!testMask(VortexExpectation.MASK_TYPE_NAME, node.getData().getName()))
					return false;
				break;
			}
			case VortexAgentEntity.TYPE_MACHINE: {
				VortexClientMachine node = (VortexClientMachine) candidate;
				if (!node.getIsRunning())
					return false;
				break;
			}
			case VortexAgentEntity.TYPE_PROCESS: {
				VortexClientProcess node = (VortexClientProcess) candidate;
				if (!testMask(VortexExpectation.MASK_TYPE_COMMAND, node.getData().getCommand()))
					return false;
				if (!testMask(VortexExpectation.MASK_TYPE_USER, node.getData().getUser()))
					return false;
				break;
			}
			case VortexAgentEntity.TYPE_NET_CONNECTION: {
				VortexClientNetConnection node = (VortexClientNetConnection) candidate;
				if (!testMask(VortexExpectation.MASK_TYPE_STATE, node.getData().getState()))
					return false;
				if (!testMask(VortexExpectation.MASK_TYPE_LOCAL_PORT, node.getData().getLocalPort()))
					return false;
				if (!testMask(VortexExpectation.MASK_TYPE_FOREIGN_PORT, node.getData().getForeignPort()))
					return false;
				if (!testMask(VortexExpectation.MASK_TYPE_LOCAL_HOST, node.getData().getLocalHost()))
					return false;
				if (!testMask(VortexExpectation.MASK_TYPE_FOREIGN_HOST, node.getData().getForeignHost()))
					return false;
				break;
			}
			case VortexAgentEntity.TYPE_CRON: {
				VortexClientCron node = (VortexClientCron) candidate;
				if (!testMask(VortexExpectation.MASK_TYPE_COMMAND, node.getData().getCommand()))
					return false;
				if (!testMask(VortexExpectation.MASK_TYPE_SCHEDULE, node.getScheduleText()))
					return false;
				break;
			}

		}
		return true;
	}

	private boolean testMask(byte id, int value) {
		return CH.getOr(masks, id, ConstTextMatcher.TRUE).matches(SH.toString(value));
	}
	private boolean testMask(byte id, String value) {
		return CH.getOr(masks, id, ConstTextMatcher.TRUE).matches(value);
	}
	@Override
	public byte getExpectationState() {
		throw new UnsupportedOperationException("I've got the object model wrong :(");
	}
	public byte getState() {
		return match == null ? STATE_NO_MATCH : STATE_MATCHED;
	}

}
