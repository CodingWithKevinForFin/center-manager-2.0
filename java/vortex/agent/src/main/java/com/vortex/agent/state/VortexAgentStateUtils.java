package com.vortex.agent.state;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.OH;
import com.f1.utils.converter.bytes.BasicFromByteArrayConverterSession;
import com.f1.utils.converter.bytes.BasicToByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;

public class VortexAgentStateUtils {

	public static ArrayList<F1AppEntity> processAdds(VortexAgentF1AppState f1State, List<F1AppEntity> adds) {
		if (adds == null)
			return null;
		final ArrayList<F1AppEntity> r = new ArrayList<F1AppEntity>(adds.size());
		final VortexAgentState state = f1State.getState();
		final long aiid = f1State.getF1AppInstance().getId();
		final long origAiid = f1State.getSnapshotOrigId();
		for (F1AppEntity entity : adds) {
			if (entity.getF1AppInstanceId() != origAiid)
				throw new RuntimeException("bad aiid: " + entity + " , expecting: " + origAiid);
			final long origId = entity.getId();
			entity.setId(state.createNextId());
			entity.setF1AppInstanceId(aiid);
			f1State.addEntity(origId, entity);
			r.add(entity);
		}
		return r;
	}
	public static long[] processRemoves(VortexAgentF1AppState f1State, long[] removed) {
		if (removed == null)
			return null;
		long[] r = new long[removed.length];
		final long origAiid = f1State.getSnapshotOrigId();
		for (int i = 0; i < removed.length; i += 2) {
			if (removed[i] != origAiid)
				throw new RuntimeException("bad aiid: " + removed[i] + " , expecting: " + origAiid);
			final F1AppEntity obj = f1State.removeByOrigId(removed[i + 1]);
			r[i] = obj.getF1AppInstanceId();
			r[i + 1] = obj.getId();
		}
		return r;
	}

	public static byte[] processUpdates(VortexAgentF1AppState f1State, byte[] changes) {
		if (changes == null)
			return null;
		try {
			final FastByteArrayDataOutputStream out = new FastByteArrayDataOutputStream();
			final ObjectToByteArrayConverter converter = (ObjectToByteArrayConverter) f1State.getState().getPartition().getContainer().getServices().getConverter();
			final FastByteArrayDataInputStream in = new FastByteArrayDataInputStream(changes);
			final BasicToByteArrayConverterSession outSession = new BasicToByteArrayConverterSession(converter, out, false);
			final BasicFromByteArrayConverterSession inSession = new BasicFromByteArrayConverterSession(converter, in);

			// [(byte)PID,(byte)BasicType,(?)value]+(byte)NO_PID]+EOF
			while (in.available() > 0) {
				long id = in.readLong();
				F1AppEntity obj = f1State.getByOrigId(id);
				out.writeLong(obj.getF1AppInstanceId());
				out.writeLong(obj.getId());

				ValuedSchema<Valued> schema = obj.askSchema();
				for (;;) {
					byte pid = in.readByte();
					out.write(pid);
					if (pid == Valued.NO_PID) {
						break;
					}
					ValuedParam<Valued> vp = schema.askValuedParam(pid);
					if (vp.isPrimitive()) {
						byte basicType = in.readByte();
						out.writeByte(basicType);
						vp.read(obj, in);
						vp.write(obj, out);
					} else {
						Object value = converter.read(inSession);
						vp.setValue(obj, value);
						converter.write(value, outSession);
					}
				}
			}
			return out.toByteArray();
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
}
