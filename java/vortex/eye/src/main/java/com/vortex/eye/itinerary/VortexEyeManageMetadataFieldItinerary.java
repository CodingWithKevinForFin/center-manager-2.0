package com.vortex.eye.itinerary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexMetadatable;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.VortexEyeMetadataField;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageMetadataFieldRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageMetadataFieldResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeMachineState;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeManageMetadataFieldItinerary extends AbstractVortexEyeItinerary<VortexEyeManageMetadataFieldRequest> {

	private static final Set<Character> INVALID_KEY_CHARS = CH.asSet("|= !@#$%^&*()[]\n".toCharArray());
	private static final Set<Character> INVALID_VAL_CHARS = CH.asSet("|=\n".toCharArray());

	private VortexEyeMetadataField old, nuw;
	private VortexEyeManageMetadataFieldResponse r;
	private boolean isDelete;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		r = getState().nw(VortexEyeManageMetadataFieldResponse.class);
		long now = getState().getPartition().getContainer().getTools().getNow();
		nuw = getInitialRequest().getAction().getMetadataField();
		VortexEyeState state = getState();
		isDelete = nuw.getRevision() == VortexAgentEntity.REVISION_DONE;
		if (nuw.getId() != 0) {
			old = state.getMetadataField(nuw.getId());
			if (old == null) {
				r.setMessage("MetadataField not found for update / delete: " + nuw.getId());
				return STATUS_COMPLETE;
			}
			if (isDelete) {
				if (!validateDelete(old))
					return STATUS_COMPLETE;
				nuw = old.clone();
				nuw.setRevision(VortexEyeUtils.REVISION_DONE);
			} else {
				if (!validate(nuw, r))
					return STATUS_COMPLETE;
				nuw.setRevision(old.getRevision() + 1);
			}
		} else {
			if (!validate(nuw, r))
				return STATUS_COMPLETE;
			nuw.setId(getState().createNextId());
			nuw.setRevision(0);
		}
		nuw.setNow(now);
		nuw.lock();
		sendToDb(nuw, worker);
		return STATUS_ACTIVE;
	}
	private boolean validateDelete(VortexEyeMetadataField existing) {
		final String keyCode = existing.getKeyCode();
		Iterable<VortexMetadatable> metadatable = getVortexMetadatable(existing.getTargetTypes());
		for (VortexMetadatable t : metadatable) {
			if (t.getMetadata() != null && t.getMetadata().containsKey(keyCode)) {
				r.setMessage("Found a " + VortexEyeUtils.describeType(t) + " with ID " + t.getId() + " which depends on key code '" + keyCode
						+ "', can not remove until this dependency is resolved.");
				return false;
			}
		}
		return true;

	}
	private boolean validate(VortexEyeMetadataField nuw, VortexEyeManageMetadataFieldResponse r) {

		final String keyCode = nuw.getKeyCode();
		//simple validations
		if (SH.isnt(keyCode)) {
			r.setMessage("KeyCode required");
			return false;
		} else if (SH.isnt(nuw.getTitle())) {
			r.setMessage("Title required");
			return false;
		} else if (nuw.getTargetTypes() == 0L) {
			r.setMessage("At least one target type requred");
			return false;
		}

		//check for uniqueness
		VortexEyeMetadataField existing = getState().getMetadataFieldByKeyCode(nuw.getKeyCode());
		if (existing != null && existing.getId() != nuw.getId()) {
			r.setMessage("Key code already exists: " + nuw.getKeyCode());
			return false;
		}

		//conditional validations
		switch (nuw.getValueType()) {
			case VortexEyeMetadataField.VALUE_TYPE_BOOLEAN:
				nuw.setMaxLength(null);
				nuw.setMaxValue(null);
				nuw.setMinValue(null);
				nuw.setEnums(null);
				break;
			case VortexEyeMetadataField.VALUE_TYPE_DOUBLE:
				nuw.setMaxLength(null);
				nuw.setEnums(null);
				if (nuw.getMaxValue() != null && nuw.getMinValue() != null && nuw.getMaxValue() <= nuw.getMinValue()) {
					r.setMessage("Max value must be greater that min value");
					return false;
				}
				break;
			case VortexEyeMetadataField.VALUE_TYPE_INT:
				nuw.setMaxLength(null);
				nuw.setEnums(null);
				if (nuw.getMaxValue() != null && nuw.getMinValue() != null && nuw.getMaxValue() <= nuw.getMinValue()) {
					r.setMessage("Max value must be greater that min value");
					return false;
				}
				break;
			case VortexEyeMetadataField.VALUE_TYPE_ENUM:
				nuw.setMaxLength(null);
				nuw.setMaxValue(null);
				nuw.setMinValue(null);
				if (CH.isEmpty(nuw.getEnums())) {
					r.setMessage("At least one enum value required");
					return false;
				}
				for (Map.Entry<String, String> e : nuw.getEnums().entrySet()) {
					String key = e.getKey();
					if (key.length() > 8) {
						r.setMessage("Enum key exceeds 8 char max: " + key);
						return false;
					}
					if (SH.indexOf(key, 0, INVALID_KEY_CHARS) != -1) {
						r.setMessage("Enum key has invalid char: " + key);
						return false;
					}
					String val = e.getValue();
					if (val.length() > 32) {
						r.setMessage("Enum value exceeds 32 char limit: " + val);
						return false;
					}
					if (SH.indexOf(val, 0, INVALID_VAL_CHARS) != -1) {
						r.setMessage("Enum value has invalid char: " + val);
						return false;
					}
				}
				break;
			case VortexEyeMetadataField.VALUE_TYPE_STRING:
				nuw.setMaxValue(null);
				nuw.setMinValue(null);
				nuw.setEnums(null);
				if (nuw.getMaxLength() == null || !OH.isBetween((int) nuw.getMaxLength(), 1, 100)) {
					r.setMessage("Max length required");
					return false;
				}
				break;
		}

		if (existing != null) {
			//check removed target-to
			if (existing.getTargetTypes() != nuw.getTargetTypes()) {
				long removedTypes = MH.commBits(existing.getTargetTypes(), nuw.getTargetTypes(), MH.LEFT);
				Iterable<VortexMetadatable> metadatable = getVortexMetadatable(removedTypes);
				for (VortexMetadatable t : metadatable) {
					if (t.getMetadata() != null && t.getMetadata().containsKey(keyCode)) {
						r.setMessage("Found a " + VortexEyeUtils.describeType(t) + " with ID " + t.getId() + " which depends on key code '" + keyCode
								+ "', can not remove from applies-to list until this dependency is resolved.");
						return false;
					}
				}
			}
			Iterable<VortexMetadatable> metadatable = getVortexMetadatable(nuw.getTargetTypes());
			for (VortexMetadatable t : metadatable) {
				Map<String, String> md = t.getMetadata();
				if (md == null)
					continue;
				String val = md.get(keyCode);
				if (val != null && !validateValueForField(nuw, val)) {
					r.setMessage("Found a " + VortexEyeUtils.describeType(t) + " with ID " + t.getId() + " which violates keyCode '" + keyCode + "'. Value: '" + val + "'");
					return false;
				}
			}
		}

		return true;
	}
	static private boolean validateValueForField(VortexEyeMetadataField nuw, String val) {
		try {
			switch (nuw.getValueType()) {
				case VortexEyeMetadataField.VALUE_TYPE_STRING:
					return val.length() <= nuw.getMaxLength();
				case VortexEyeMetadataField.VALUE_TYPE_BOOLEAN:
					return VortexEyeMetadataField.BOOLEAN_FALSE.equals(val) || VortexEyeMetadataField.BOOLEAN_TRUE.equals(val);
				case VortexEyeMetadataField.VALUE_TYPE_ENUM:
					return nuw.getEnums().containsKey(val);
				case VortexEyeMetadataField.VALUE_TYPE_INT:
					return isBetween(Integer.parseInt(val), nuw.getMinValue(), nuw.getMaxValue());
				case VortexEyeMetadataField.VALUE_TYPE_DOUBLE:
					return isBetween(Integer.parseInt(val), nuw.getMinValue(), nuw.getMaxValue());
				default:
					throw new RuntimeException("bad type: " + nuw.getValueType());
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}
	static private boolean isBetween(double val, Double minValue, Double maxValue) {
		return (minValue == null || val >= minValue.doubleValue()) || (maxValue != null && val <= maxValue.doubleValue());
	}
	private Iterable<VortexMetadatable> getVortexMetadatable(long types) {
		List<VortexMetadatable> l = new ArrayList<VortexMetadatable>();
		if (MH.anyBits(types, VortexAgentEntity.MASK_TYPE_MACHINE)) {
			for (VortexEyeMachineState mac : getState().getAllMachines()) {
				l.add(mac.getMachine());
			}
		}
		return l;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		DbResultMessage dbResult = (DbResultMessage) result.getAction();
		if (!dbResult.getOk()) {
			r.setMessage(dbResult.getMessage());
		} else {
			if (isDelete)
				getState().removeMetadataField(nuw.getId());
			else
				getState().addMetadataField(nuw);
			r.setOk(true);
		}
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		if (r.getOk()) {
			r.setMetadataField(nuw);
			VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
			cmb.writeTransition(old, nuw);
			worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
		}
		return r;
	}

	public void sendToDb(VortexEyeMetadataField md, VortexEyeItineraryWorker worker) {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		boolean active = md.getRevision() < VortexAgentEntity.REVISION_DONE;
		params.put("now", md.getNow());
		params.put("active", active);
		params.put("id", md.getId());
		params.put("revision", md.getRevision());
		params.put("description", md.getDescription());
		params.put("target_types", md.getTargetTypes());
		params.put("value_type", md.getValueType());
		params.put("required", md.getRequired());
		params.put("key_code", md.getKeyCode());
		params.put("title", md.getTitle());
		params.put("max_length", md.getMaxLength());
		params.put("enums", md.getEnums() == null ? null : VortexEyeUtils.joinMap(md.getEnums()));
		params.put("max_value", md.getMaxValue());
		params.put("min_value", md.getMinValue());
		DbRequestMessage msg = getTools().nw(DbRequestMessage.class);
		msg.setId("insert_metadata_field");
		msg.setParams(params);
		worker.sendToDb(this, msg);
	}
	public static boolean validateMetadata(VortexMetadatable source, VortexEyeState state, VortexEyeResponse response) {
		Map<String, String> metaData = source.getMetadata();
		if (metaData == null) {
			response.setMessage("Missing metadata");
			return false;
		}
		for (Map.Entry<String, String> e : metaData.entrySet()) {
			VortexEyeMetadataField field = state.getMetadataFieldByKeyCode(e.getKey());
			if (field == null) {
				response.setMessage("Invalid metadata key code: '" + e.getKey() + "'");
				return false;
			} else if (!validateValueForField(field, e.getValue())) {
				response.setMessage("Invalid value for field '" + field.getTitle() + "' (keycode '" + e.getKey() + "'). Value: '" + e.getValue() + "'");
				return false;
			}

		}
		return true;

	}
	@Override
	protected void populateAuditEvent(VortexEyeManageMetadataFieldRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_MANAGE_METADATA_FIELD);
		auditEntity(sink, "MAID", action.getMetadataField());
	}
}
