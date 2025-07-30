package com.vortex.eye;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.utils.EH;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.LongArrayList;
import com.f1.utils.OH;
import com.f1.utils.VH;
import com.f1.utils.converter.bytes.BasicToByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeChanges;
import com.f1.vortexcommon.msg.eye.VortexEyeEntity;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeChangesMessageBuilder {
	private static final Logger log = Logger.getLogger(VortexEyeChangesMessageBuilder.class.getName());

	private static final int MAX_BUFFER_SIZE = 2048;

	final private VortexEyeState state;
	final private ObjectToByteArrayConverter converter;

	//agents
	final private FastByteArrayDataOutputStream agentEntityUpdatesOut;
	final private BasicToByteArrayConverterSession agentEntityUpdatesSession;
	private List<VortexAgentEntity> agentEntityAdds;
	final private LongArrayList agentEntityRemoves = new LongArrayList();

	//f1Apps
	final private FastByteArrayDataOutputStream f1AppEntityUpdatesOut;
	final private BasicToByteArrayConverterSession f1AppEntityUpdatesSession;
	private List<F1AppEntity> f1AppEntityAdds;
	final private LongArrayList f1AppEntityRemoves = new LongArrayList();
	final private ObjectGeneratorForClass<VortexEyeChanges> changesMsgGenerator;

	//eye
	final private FastByteArrayDataOutputStream eyeEntityUpdatesOut;
	final private BasicToByteArrayConverterSession eyeEntityUpdatesSession;
	private List<VortexEyeEntity> eyeEntityAdds;
	final private LongArrayList eyeEntityRemoves = new LongArrayList();

	public VortexEyeChangesMessageBuilder(VortexEyeState state) {
		this.state = state;
		this.changesMsgGenerator = state.getPartition().getContainer().getGenerator(VortexEyeChanges.class);
		converter = (ObjectToByteArrayConverter) state.getPartition().getContainer().getServices().getConverter();

		agentEntityUpdatesOut = new FastByteArrayDataOutputStream();
		agentEntityUpdatesSession = new BasicToByteArrayConverterSession(converter, agentEntityUpdatesOut, false);

		eyeEntityUpdatesOut = new FastByteArrayDataOutputStream();
		eyeEntityUpdatesSession = new BasicToByteArrayConverterSession(converter, eyeEntityUpdatesOut, false);

		f1AppEntityUpdatesOut = new FastByteArrayDataOutputStream();
		f1AppEntityUpdatesSession = new BasicToByteArrayConverterSession(converter, f1AppEntityUpdatesOut, false);

	}

	public void reset() {
		agentEntityUpdatesOut.reset(MAX_BUFFER_SIZE);
		agentEntityAdds = null;
		agentEntityRemoves.clear();

		eyeEntityUpdatesOut.reset(MAX_BUFFER_SIZE);
		eyeEntityAdds = null;
		eyeEntityRemoves.clear();

		f1AppEntityUpdatesOut.reset(MAX_BUFFER_SIZE);
		f1AppEntityAdds = null;
		f1AppEntityRemoves.clear();

	}
	public boolean hasChanges() {
		return (agentEntityAdds != null || eyeEntityAdds != null || f1AppEntityAdds != null || agentEntityUpdatesOut.getCount() > 0 || agentEntityRemoves.size() > 0
				|| eyeEntityUpdatesOut.getCount() > 0 || eyeEntityRemoves.size() > 0 || f1AppEntityUpdatesOut.getCount() > 0 || f1AppEntityRemoves.size() > 0);
	}
	//eye popping :)
	public byte[] popEyeEntityChanges() {
		if (eyeEntityUpdatesOut.size() == 0)
			return null;
		final byte[] r = eyeEntityUpdatesOut.toByteArray();
		eyeEntityUpdatesOut.reset();
		return r;
	}

	public List<VortexEyeEntity> popEyeEntityAdds() {
		List<VortexEyeEntity> r = eyeEntityAdds;
		eyeEntityAdds = null;
		return r;
	}

	public long[] popEyeEntityRemoves() {
		return eyeEntityRemoves.isEmpty() ? null : eyeEntityRemoves.toLongArray();
	}

	//agent popping
	public byte[] popAgentEntityChanges() {
		if (agentEntityUpdatesOut.size() == 0)
			return null;
		final byte[] r = agentEntityUpdatesOut.toByteArray();
		agentEntityUpdatesOut.reset();
		return r;
	}

	public List<VortexAgentEntity> popAgentEntityAdds() {
		List<VortexAgentEntity> r = agentEntityAdds;
		agentEntityAdds = null;
		return r;
	}

	public long[] popAgentEntityRemoves() {
		return agentEntityRemoves.isEmpty() ? null : agentEntityRemoves.toLongArray();
	}

	//f1app popping 
	public byte[] popF1AppEntityChanges() {
		if (f1AppEntityUpdatesOut.size() == 0)
			return null;
		final byte[] r = f1AppEntityUpdatesOut.toByteArray();
		f1AppEntityUpdatesOut.reset();
		return r;
	}

	public List<F1AppEntity> popF1AppEntityAdds() {
		List<F1AppEntity> r = f1AppEntityAdds;
		f1AppEntityAdds = null;
		return r;
	}

	public long[] popF1AppEntityRemoves() {
		return f1AppEntityRemoves.isEmpty() ? null : f1AppEntityRemoves.toLongArray();
	}

	// convenience method
	public VortexEyeChanges popToChangesMsg(long seqNum) {
		final VortexEyeChanges r = this.changesMsgGenerator.nw();

		r.setEyeEntitiesAdded(popEyeEntityAdds());
		r.setEyeEntitiesRemoved(popEyeEntityRemoves());
		r.setEyeEntitiesUpdated(popEyeEntityChanges());

		r.setAgentEntitiesAdded(popAgentEntityAdds());
		r.setAgentEntitiesRemoved(popAgentEntityRemoves());
		r.setAgentEntitiesUpdated(popAgentEntityChanges());

		r.setF1AppEntitiesAdded(popF1AppEntityAdds());
		r.setF1AppEntitiesRemoved(popF1AppEntityRemoves());
		r.setF1AppEntitiesUpdated(popF1AppEntityChanges());

		r.setEyeProcessUid(EH.getProcessUid());
		r.setSeqNum(seqNum);

		return r;
	}

	//Agent writing 
	public void writeUpdate(VortexAgentEntity entity, byte... pids) {
		try {
			agentEntityUpdatesOut.writeLong(entity.getMachineInstanceId());
			agentEntityUpdatesOut.writeLong(entity.getId());
			for (byte pid : pids) {
				if (pid == Valued.NO_PID)
					break;
				ValuedParam<Valued> param = entity.askSchema().askValuedParam(pid);
				agentEntityUpdatesOut.writeByte(pid);

				if (param.isPrimitive()) {
					agentEntityUpdatesOut.writeByte(param.getBasicType());
					param.write(entity, agentEntityUpdatesOut);
				} else
					converter.write(param.getValue(entity), agentEntityUpdatesSession);
			}
			agentEntityUpdatesOut.writeByte(Valued.NO_PID);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	public void writeUpdate(VortexAgentEntity old, VortexAgentEntity nuw) {
		try {
			agentEntityUpdatesOut.writeLong(old.getMachineInstanceId());
			agentEntityUpdatesOut.writeLong(old.getId());
			for (ValuedParam<VortexAgentEntity> param : VH.getValuedParams(old)) {
				final byte pid = param.getPid();
				if (param.areEqual(old, nuw) || pid == VortexAgentEntity.PID_MACHINE_INSTANCE_ID || pid == VortexAgentEntity.PID_ID)
					continue;
				agentEntityUpdatesOut.writeByte(pid);
				if (param.isPrimitive()) {
					agentEntityUpdatesOut.writeByte(param.getBasicType());
					param.write(nuw, agentEntityUpdatesOut);
				} else
					converter.write(param.getValue(nuw), agentEntityUpdatesSession);
			}
			agentEntityUpdatesOut.writeByte(Valued.NO_PID);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	public void writeAdd(VortexAgentEntity entity) {
		if (agentEntityAdds == null)
			agentEntityAdds = new ArrayList<VortexAgentEntity>();
		agentEntityAdds.add(VH.cloneIfUnlocked(entity));
	}
	public void writeRemove(VortexAgentEntity entity) {
		agentEntityRemoves.add(entity.getMachineInstanceId());
		agentEntityRemoves.add(entity.getId());
	}

	// Eye writing
	public void writeUpdate(VortexEyeEntity old, VortexEyeEntity nuw) {
		try {
			eyeEntityUpdatesOut.writeLong(nuw.getId());
			for (ValuedParam<VortexEyeEntity> param : VH.getValuedParams(old)) {
				final byte pid = param.getPid();
				if (param.areEqual(old, nuw))
					continue;
				eyeEntityUpdatesOut.writeByte(pid);
				if (param.isPrimitive()) {
					eyeEntityUpdatesOut.writeByte(param.getBasicType());
					param.write(nuw, eyeEntityUpdatesOut);
				} else
					converter.write(param.getValue(nuw), eyeEntityUpdatesSession);
			}
			eyeEntityUpdatesOut.writeByte(Valued.NO_PID);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	public void writeUpdate(VortexEyeEntity entity, byte... pids) {
		try {
			eyeEntityUpdatesOut.writeLong(entity.getId());
			for (byte pid : pids) {
				if (pid == Valued.NO_PID)
					break;
				ValuedParam<Valued> param = entity.askSchema().askValuedParam(pid);
				eyeEntityUpdatesOut.writeByte(pid);

				if (param.isPrimitive()) {
					eyeEntityUpdatesOut.writeByte(param.getBasicType());
					param.write(entity, eyeEntityUpdatesOut);
				} else
					converter.write(param.getValue(entity), eyeEntityUpdatesSession);
			}
			eyeEntityUpdatesOut.writeByte(Valued.NO_PID);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	public void writeAdd(VortexEyeEntity entity) {
		if (eyeEntityAdds == null)
			eyeEntityAdds = new ArrayList<VortexEyeEntity>();
		eyeEntityAdds.add(VH.cloneIfUnlocked(entity));
	}
	public void writeRemove(VortexEyeEntity entity) {
		eyeEntityRemoves.add(entity.getId());
	}

	//F1 App Writing
	public void writeAdd(F1AppEntity entity) {
		if (f1AppEntityAdds == null)
			f1AppEntityAdds = new ArrayList<F1AppEntity>();
		f1AppEntityAdds.add(VH.cloneIfUnlocked(entity));
	}
	public void writeRemove(F1AppEntity entity) {
		f1AppEntityRemoves.add(entity.getF1AppInstanceId());
		f1AppEntityRemoves.add(entity.getId());
	}
	public void writeUpdate(F1AppEntity entity, byte... pids) {
		try {
			f1AppEntityUpdatesOut.writeLong(entity.getF1AppInstanceId());
			f1AppEntityUpdatesOut.writeLong(entity.getId());
			for (byte pid : pids) {
				if (pid == Valued.NO_PID)
					break;
				ValuedParam<Valued> param = entity.askSchema().askValuedParam(pid);
				f1AppEntityUpdatesOut.writeByte(pid);
				if (param.isPrimitive()) {
					f1AppEntityUpdatesOut.writeByte(param.getBasicType());
					param.write(entity, f1AppEntityUpdatesOut);
				} else
					converter.write(param.getValue(entity), f1AppEntityUpdatesSession);
			}
			f1AppEntityUpdatesOut.writeByte(Valued.NO_PID);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	public void writeTransition(VortexEyeEntity old, VortexEyeEntity nuw) {
		if (old == null) {
			if (nuw.getRevision() != 0)
				throw new RuntimeException("bad revision: " + nuw);
			else
				writeAdd(nuw);
		} else {
			//if (nuw.getRevision() == 0)
			//throw new RuntimeException("bad revision: " + nuw);
			if (nuw.getRevision() == VortexEyeUtils.REVISION_DONE)
				writeRemove(nuw);
			else
				writeUpdate(old, nuw);
		}
	}

}
