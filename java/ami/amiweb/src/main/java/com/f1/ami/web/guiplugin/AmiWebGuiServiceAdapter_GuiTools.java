package com.f1.ami.web.guiplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebGuiServiceAdapter;
import com.f1.ami.web.AmiWebGuiServiceAdapterPeer;
import com.f1.base.Bytes;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.event.ExpiresEventListener;
import com.f1.utils.event.SimpleEventReaper;
import com.f1.utils.event.SimpleEventReaper.ExpiresEvent;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebGuiServiceAdapter_GuiTools implements AmiWebGuiServiceAdapter, ExpiresEventListener {
	private static final Logger log = LH.get();
	private static final String RESPONSE_TYPE = "type";
	private static final String RESPONSE_DATA_BASE64 = "dataBase64";
	private static final String RESPONSE_BLOB = "blob";
	//	private static final String RESPONSE_TEXT = "text";
	//	private static final String RESPONSE_ARRAY_BUFFER = "arrayBuffer";
	private static final String AMISCRIPT_METHOD_FETCH = "fetch";
	private static final String AMISCRIPT_CALLBACK_ON_INIT = "onInit";
	private static final String AMISCRIPT_CALLBACK_ON_RESPONSE = "onResponse";
	private static final String AMISCRIPT_CALLBACK_ON_ERROR = "onError";
	private static final Long NEXT_ID_UNKNOWN_STATE = -1l;
	private AmiWebGuiServiceAdapterPeer peer;
	private boolean isInitialized;
	private SimpleEventReaper reaper;
	private Map<Long, SimpleEventReaper.ExpiresEvent> fetchRequestsEvents;
	private long nextId = 0;

	class FetchRequest {
		private final long id;
		final private String resource;
		final private Map<String, ?> options;

		public FetchRequest(long id, String resource, Map<String, ?> options) {
			this.id = id;
			this.resource = resource;
			this.options = options;
		}

		public Map<String, ?> toMap() {
			return CH.m("resource", resource, "options", options);
		}

		public long getId() {
			return id;
		}
	}

	protected void incNextId() {
		// Reset nextId to 0
		if (nextId == Long.MAX_VALUE)
			nextId = 0L;

		nextId++;
		while (this.fetchRequestsEvents.get(nextId++) != null) {
			if (nextId < 0)
				nextId = NEXT_ID_UNKNOWN_STATE; // Unknown state
		}
		//		while (this.events.getNoThrow(nextId++) != null) {
		//			if (nextId < 0)
		//				throw new UnexpectedException("Could not generate a new id current next id " + nextId);
		//		}
	}

	@Override
	public void init(AmiWebGuiServiceAdapterPeer peer) {
		this.peer = peer;
		this.reaper = this.peer.getService().getEventReaper();
		this.fetchRequestsEvents = new HashMap<Long, SimpleEventReaper.ExpiresEvent>();
	}

	@Override
	public String getGuiServiceId() {
		return AmiWebGuiServicePlugin_GuiTools.PLUGINID;
	}

	@Override
	public String getDescription() {
		return "AMI Gui Tools";
	}

	@Override
	public String getAmiscriptClassname() {
		return "GuiTools";
	}

	@Override
	public List<ParamsDefinition> getAmiscriptMethods() {
		List<ParamsDefinition> r = new ArrayList<ParamsDefinition>();
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_FETCH, Object.class, "String resource,java.util.Map options,Long timeout"));
		return r;
	}

	@Override
	public Object onAmiScriptMethod(String name, Object[] args) {
		if (AMISCRIPT_METHOD_FETCH.equals(name)) {
			String resource = (String) args[0];
			Map<String, ?> options = (Map<String, ?>) args[1];
			Long timeout = Caster_Long.INSTANCE.cast(args[2]);

			ExpiresEvent<?> event = null;
			long newId = this.nextId;
			this.incNextId();
			if (nextId == NEXT_ID_UNKNOWN_STATE)
				throw new RuntimeException("Could not generate a new id. Current id is " + nextId);
			FetchRequest fr = new FetchRequest(newId, resource, options);
			//			try {
			if (timeout == null)
				event = this.reaper.newEvent(fr, this);
			else
				event = this.reaper.newEvent(fr, timeout, this);
			//			} catch (Exception e) {
			//				String msg = "Encountered unexpected error sending fetch request";
			//				LH.warning(log, msg, e);
			//				this.onAmiScriptError(CH.m("req", fr.toMap(), "msg", msg, "exception", e));
			//			}
			//			if (event != null) {
			this.fetchRequestsEvents.put(fr.getId(), event);
			peer.executeJavascriptCallback(name, new Object[] { resource, options, fr.getId() });
			return fr.getId();
			//			}
		}
		return null;
	}

	public void onAmiScriptError(Map<Object, Object> error) {
		peer.executeAmiScriptCallback(AMISCRIPT_CALLBACK_ON_ERROR, new Object[] { error });
	}

	@Override
	public List<ParamsDefinition> getAmiScriptCallbacks() {
		List<ParamsDefinition> r = new ArrayList<ParamsDefinition>();
		r.add(new ParamsDefinition(AMISCRIPT_CALLBACK_ON_INIT, Object.class, "java.util.Map metadata"));
		r.add(new ParamsDefinition(AMISCRIPT_CALLBACK_ON_ERROR, Object.class, "java.util.Map metadata"));
		r.add(new ParamsDefinition(AMISCRIPT_CALLBACK_ON_RESPONSE, Object.class, //
				"Long id,java.util.Map request,java.util.Map response,String mimeType,com.f1.base.Bytes responseBytes"));
		return r;
	}

	@Override
	public void onCallFromJavascript(String name, Object[] args) {
		if (AMISCRIPT_CALLBACK_ON_INIT.equals(name))
			this.isInitialized = true;
		if (AMISCRIPT_CALLBACK_ON_RESPONSE.equals(name)) {
			long correlationId = Caster_Long.PRIMITIVE.cast(args[0]);
			Map<String, Object> response = (Map<String, Object>) args[1];
			if (this.fetchRequestsEvents.containsKey(correlationId)) {
				ExpiresEvent<?> fetchRequestEvent = this.fetchRequestsEvents.remove(correlationId);
				this.reaper.expireNowNoFire(fetchRequestEvent);
				FetchRequest fr = (FetchRequest) fetchRequestEvent.getData();
				Bytes bytes = null;
				String mimeType = null;
				if (response.containsKey(RESPONSE_BLOB)) {
					Map<String, Object> blob = (Map<String, Object>) response.get(RESPONSE_BLOB);
					mimeType = (String) blob.get(RESPONSE_TYPE);
					String data_b64 = (String) blob.get(RESPONSE_DATA_BASE64);
					bytes = new Bytes(EncoderUtils.decode64(data_b64));
				}

				peer.executeAmiScriptCallback(name, new Object[] { correlationId, fr.toMap(), response, mimeType, bytes });
				return;
			} else {
				String msg = "Invalid state, received response for fetch request that could not be found";
				LH.warning(log, msg);
				this.onAmiScriptError(CH.m("msg", msg));

			}
		} else
			peer.executeAmiScriptCallback(name, args);
	}

	@Override
	public void onEventTimedOut(Object data) {
		FetchRequest fr = (FetchRequest) data;
		//		ExpiresEvent e = this.fetchRequestsEvents.get(fr.getId());
		//		if (event != e) {
		//			String msg = "Invalid state, expiring request has the same id as an existing request: expired id: " + fr.getId();
		//			LH.warning(log, msg);
		//			this.onAmiScriptError(CH.m("msg", msg));
		//			return;
		//		}
		this.fetchRequestsEvents.remove(fr.getId());
		String msg = "Fetch Request timed out";
		this.onAmiScriptError(CH.m("req", fr.toMap(), "msg", msg));
	}

	@Override
	public List<String> getJavascriptLibraries() {
		return CH.l("ami_guitools.js");
	}

	@Override
	public String getJavascriptInitialization() {
		return "";
	}

	@Override
	public String getJavascriptNewInstance() {
		return "new GuiTools()";
	}

	@Override
	public void onPageLoading() {
		this.isInitialized = false;
	}

	@Override
	public void onLayoutStartup() {
		if (this.isInitialized)
			peer.executeAmiScriptCallback(AMISCRIPT_CALLBACK_ON_INIT, OH.EMPTY_OBJECT_ARRAY);

	}
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	@Override
	public String getJavascriptCloseInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
