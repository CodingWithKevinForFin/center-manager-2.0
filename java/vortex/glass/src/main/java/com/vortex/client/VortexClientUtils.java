package com.vortex.client;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.logging.Logger;

import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppEvent;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.OfflineConverter;
import com.f1.utils.ToDoException;
import com.f1.utils.converter.bytes.BasicFromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.encrypt.RsaEncryptUtils;
import com.f1.utils.structs.Tuple2;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.eye.VortexEyeChanges;
import com.f1.vortexcommon.msg.eye.VortexEyeEntity;
import com.vortex.client.VortexClientF1AppState.AgentWebObject;

public class VortexClientUtils {

	public static final Logger log = LH.get(VortexClientUtils.class);

	public static void processSnapshot(VortexClientManager agentManager, VortexEyeChanges changes, OfflineConverter offlineConverter) {
		//log.info("Received snapshot: " + CH.size(changes.getAgentEntitiesAdded()) + "," + CH.size(changes.getF1AppEntitiesAdded()) + "," + CH.size(changes.getEyeEntitiesAdded()));
		agentManager.setCurrentSeqNum(changes.getSeqNum());
		if (changes.getAgentEntitiesRemoved() != null)
			throw new RuntimeException("snapshot should not have entity removes");
		if (changes.getAgentEntitiesUpdated() != null)
			throw new RuntimeException("snapshot should not have entity updates");
		if (changes.getF1AppEntitiesRemoved() != null)
			throw new RuntimeException("snapshot should not have f1app removes");
		if (changes.getF1AppEntitiesUpdated() != null)
			throw new RuntimeException("snapshot should not have f1app updates");
		if (changes.getF1AppEvents() != null)
			throw new RuntimeException("snapshot should not have f1app events");
		if (changes.getEyeEntitiesUpdated() != null)
			throw new RuntimeException("snapshot should not have eye updates");
		if (changes.getEyeEntitiesRemoved() != null)
			throw new RuntimeException("snapshot should not have eye removes");

		processAgentEntityAdds(agentManager, changes.getAgentEntitiesAdded());
		processF1AppEntityAdds(agentManager, changes.getF1AppEntitiesAdded());
		processEyeEntityAdds(agentManager, changes.getEyeEntitiesAdded());

	}
	public static void processChanges(VortexClientManager agentManager, VortexEyeChanges changes, ObjectToByteArrayConverter converter) {
		if (agentManager.getCurrentSeqNum() + 1 != changes.getSeqNum()) {
			LH.warning(log, "BAD SEQUENCE NUMBER: ", changes, new RuntimeException("bad seqnum at " + agentManager.getCurrentSeqNum() + ": " + changes.getSeqNum()));
		}
		agentManager.setCurrentSeqNum(changes.getSeqNum());

		processAgentEntityAdds(agentManager, changes.getAgentEntitiesAdded());
		processAgentEntityUpdates(agentManager, changes.getAgentEntitiesUpdated(), converter);
		processAgentEntityRemoves(agentManager, changes.getAgentEntitiesRemoved());

		processF1AppEntityAdds(agentManager, changes.getF1AppEntitiesAdded());
		processF1AppEntityUpdates(agentManager, changes.getF1AppEntitiesUpdated(), converter);
		processF1AppEntityRemoves(agentManager, changes.getF1AppEntitiesRemoved(), converter);
		processF1AppEntityEvents(agentManager, changes.getF1AppEvents(), converter);

		processEyeEntityAdds(agentManager, changes.getEyeEntitiesAdded());
		processEyeEntityUpdates(agentManager, changes.getEyeEntitiesUpdated(), converter);
		processEyeEntityRemoves(agentManager, changes.getEyeEntitiesRemoved());

	}

	public static void processF1AppEntityAdds(VortexClientManager manager, List<F1AppEntity> adds) {
		if (CH.isEmpty(adds))
			return;
		manager.addF1AppEntities(adds);
	}
	public static void processAgentEntityAdds(VortexClientManager agentManager, List<VortexAgentEntity> adds) {
		if (CH.isEmpty(adds))
			return;
		for (VortexEntity entity : adds) {
			try {
				if (entity instanceof VortexAgentMachine)
					agentManager.onAgentSnapshot((VortexAgentMachine) entity);
			} catch (Exception e) {
				LH.warning(log, "Error processing entity add: " + entity, e);
			}
		}
		for (VortexEntity entity : adds) {
			try {
				if (!(entity instanceof VortexAgentMachine))
					agentManager.onChange(entity);
			} catch (Exception e) {
				LH.warning(log, "Error processing entity add: " + entity, e);
			}
		}

	}
	private static void processAgentEntityRemoves(VortexClientManager agentManager, long[] removed) {
		if (AH.isEmpty(removed))
			return;
		for (int i = 0; i < removed.length; i += 2) {
			VortexEntity existing = null;
			long miid = removed[i];
			long id = removed[i + 1];
			try {
				existing = agentManager.getEntityById(id);
				if (existing == null)
					throw new RuntimeException("remove for unknown id: " + id);
				existing.setRevision(VortexAgentEntity.REVISION_DONE);
				agentManager.onChange(existing);
			} catch (Exception e) {
				LH.warning(log, "Exception processing entity miid: ", miid, ",id: ", id, " , remove: ", existing, e);
			}
		}

	}

	private static void processAgentEntityUpdates(VortexClientManager agentManager, byte[] updated, ObjectToByteArrayConverter converter) {
		if (AH.isEmpty(updated))
			return;
		try {
			FastByteArrayDataInputStream in = new FastByteArrayDataInputStream(updated);
			final BasicFromByteArrayConverterSession inSession = new BasicFromByteArrayConverterSession(converter, in);
			while (in.available() > 0) {
				long aiid = in.readLong();
				long id = in.readLong();
				VortexEntity current = agentManager.getEntityById(id);
				if (current == null) {
					log.warning("Unknown agent Id: " + id);
					for (;;) {
						final byte pid = in.readByte();
						if (pid == Valued.NO_PID)
							break;
						byte type = in.readByte();
						converter.getConverter(type).read(inSession);//ignore results
					}
				} else {
					final VortexEntity existing = (VortexEntity) current.nw();
					existing.setId(current.getId());
					if (existing instanceof VortexAgentEntity)
						((VortexAgentEntity) existing).setMachineInstanceId(((VortexAgentEntity) current).getMachineInstanceId());
					ValuedSchema<Valued> schema = current.askSchema();
					for (;;) {
						final byte pid = in.readByte();
						if (pid == Valued.NO_PID)
							break;
						ValuedParam<Valued> param = schema.askValuedParam(pid);
						Object old = param.getValue(existing);
						if (param.isPrimitive()) {
							in.readByte();//type, not necessary, but perhaps we could validate it matches param type (at a cost).
							param.read(existing, in);
						} else {
							Object value = converter.read(inSession);
							param.setValue(existing, value);
						}
					}
					try {
						agentManager.onChange(existing);
					} catch (Exception e) {
						LH.warning(log, "Error processing update: ", existing, e);
					}
				}
			}

		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	public static void processEyeEntityAdds(VortexClientManager agentManager, List<VortexEyeEntity> adds) {
		if (CH.isEmpty(adds))
			return;
		for (VortexEntity entity : adds) {
			try {
				if (entity instanceof VortexAgentMachine)
					agentManager.onAgentSnapshot((VortexAgentMachine) entity);
				else
					agentManager.onChange(entity);
			} catch (Exception e) {
				LH.warning(log, "Error processing add: ", entity, e);
			}
		}
	}

	private static void processEyeEntityRemoves(VortexClientManager agentManager, long[] removed) {
		if (AH.isEmpty(removed))
			return;
		for (int i = 0; i < removed.length; i++) {
			VortexEntity existing = null;
			long id = removed[i];
			try {
				existing = agentManager.getEntityById(id);
				if (existing == null)
					throw new RuntimeException("remove for unknown id: " + id);
				existing.setRevision(VortexAgentEntity.REVISION_DONE);
				agentManager.onChange(existing);
			} catch (Exception e) {
				LH.warning(log, "Error processing remove. id: ", id, ", entity: ", existing, e);
			}
		}

	}

	public static void processF1AppEntityUpdates(VortexClientManager manager, byte[] changes, ObjectToByteArrayConverter converter) {
		if (changes == null)
			return;
		try {
			final FastByteArrayDataInputStream in = new FastByteArrayDataInputStream(changes);
			final BasicFromByteArrayConverterSession inSession = new BasicFromByteArrayConverterSession(converter, in);

			while (in.available() > 0) {
				long aiid = in.readLong();
				long id = in.readLong();
				VortexClientF1AppState appstate = manager.getJavaAppState(aiid);
				AgentWebObject wobj = appstate.getObject(id);
				F1AppEntity obj = wobj.getObject();
				ValuedSchema<Valued> schema = obj.askSchema();
				for (;;) {
					byte pid = in.readByte();
					if (pid == Valued.NO_PID) {
						break;
					}
					ValuedParam<Valued> vp = schema.askValuedParam(pid);
					if (vp.isPrimitive()) {
						byte basicType = in.readByte();
						vp.read(obj, in);
					} else {
						Object value = converter.read(inSession);
						vp.setValue(obj, value);
					}
				}
				try {
					manager.onF1AppEntityUpdate(wobj);
				} catch (Exception e) {
					LH.warning(log, "Error processing f1 app update: ", wobj, e);
				}
			}
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	private static void processF1AppEntityRemoves(VortexClientManager agentManager, long[] removed, ObjectToByteArrayConverter converter) {
		if (AH.isEmpty(removed))
			return;
		VortexClientF1AppState lastAppState = null;
		long lastaiid = -1;
		for (int i = 0; i < removed.length; i += 2) {
			long aiid = removed[i];
			long id = removed[i + 1];
			try {
				if (aiid == id) {
					agentManager.removeF1App(aiid);
					lastAppState = null;
				} else {
					if (lastaiid != aiid)
						lastAppState = agentManager.getJavaAppState(lastaiid = aiid);
					lastAppState.removeObject(id);
					//TODO: fire update: manager.fireF1AppEntityUpdates(lastAppState, changedObjects);
				}
			} catch (Exception e) {
				LH.warning(log, "Error processing f1 app entity removes. aiid: ", aiid, " id: ", id, e);
			}
		}
	}
	private static void processF1AppEntityEvents(VortexClientManager agentManager, List<F1AppEvent> f1AppEvents, ObjectToByteArrayConverter converter) {
		if (CH.isEmpty(f1AppEvents))
			return;
		agentManager.fireAgentAuditEventsList(f1AppEvents);

	}
	private static void processEyeEntityUpdates(VortexClientManager agentManager, byte[] updated, ObjectToByteArrayConverter converter) {
		if (AH.isEmpty(updated))
			return;
		try {
			FastByteArrayDataInputStream in = new FastByteArrayDataInputStream(updated);
			final BasicFromByteArrayConverterSession inSession = new BasicFromByteArrayConverterSession(converter, in);
			while (in.available() > 0) {
				long id = in.readLong();
				VortexEntity current = agentManager.getEntityById(id);
				if (current == null)
					throw new ToDoException("handle ignoring unknown agent id: " + id);
				VortexEntity existing = (VortexEntity) current.nw();
				existing.setId(current.getId());
				if (existing instanceof VortexAgentEntity)
					((VortexAgentEntity) existing).setMachineInstanceId(((VortexAgentEntity) current).getMachineInstanceId());
				ValuedSchema<Valued> schema = current.askSchema();
				for (;;) {
					final byte pid = in.readByte();
					if (pid == Valued.NO_PID)
						break;
					ValuedParam<Valued> param = schema.askValuedParam(pid);
					Object old = param.getValue(existing);
					if (param.isPrimitive()) {
						in.readByte();//type, not necessary, but perhaps we could validate it matches param type (at a cost).
						param.read(existing, in);
					} else {
						Object value = converter.read(inSession);
						param.setValue(existing, value);
					}
				}
				try {
					agentManager.onChange(existing);
				} catch (Exception e) {
					LH.warning(log, "Error processing entity update: ", existing, e);
				}
			}

		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	public static VortexAgentFile decompressFile(VortexAgentFile f) {
		if (f == null)
			return null;
		if (f.getData() != null && MH.anyBits(f.getMask(), VortexAgentFile.DATA_DEFPLATED)) {
			f.setData(IOH.decompress(f.getData()));
			f.setMask(MH.clearBits(f.getMask(), VortexAgentFile.DATA_DEFPLATED));
		}
		return f;
	}

	final private static Tuple2<RSAPublicKey, RSAPrivateKey> key = RsaEncryptUtils.generateKey("vortex123");

	static public byte[] encrypt(byte data[]) {
		return RsaEncryptUtils.encrypt(key.getB(), data, true);
	}
	static public byte[] encryptString(String data) {
		if (data == null)
			return null;
		return RsaEncryptUtils.encrypt(key.getB(), data.getBytes(), true);
	}

	static public byte[] decrypt(byte data[]) {
		return RsaEncryptUtils.decrypt(key.getA(), data);
	}
	public static String decryptToString(byte[] keyContents) {
		return keyContents == null ? null : new String(decrypt(keyContents));
	}
	//	public static Tuple2<RSAPublicKey, RSAPrivateKey> generateKey(String text) {
	//		KeyPairGenerator keyGen;
	//		try {
	//			keyGen = KeyPairGenerator.getInstance("RSA");
	//			keyGen.initialize(512, new RsaEncryptHelper.TextBasedSecureRandom(text));
	//			KeyPair pair = keyGen.generateKeyPair();
	//			return new Tuple2<RSAPublicKey, RSAPrivateKey>((RSAPublicKey) pair.getPublic(), (RSAPrivateKey) pair.getPrivate());
	//		} catch (NoSuchAlgorithmException e) {
	//			throw new RuntimeException("error generating " + 512 + "-bit RSA key for text: " + SH.password(text), e);
	//		}
	//	}

}
