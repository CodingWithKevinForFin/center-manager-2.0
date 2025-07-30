package com.vortex.eye.processors;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.vortex.eye.VortexEyeDbService;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.messages.VortexVaultRequest;
import com.vortex.eye.messages.VortexVaultResponse;
import com.vortex.eye.state.VortexVaultState;

public class VortexEyeVaultRequestProcessor extends BasicRequestProcessor<VortexVaultRequest, VortexVaultState, VortexVaultResponse> {
	private static Logger log = Logger.getLogger(VortexEyeVaultRequestProcessor.class.getName());

	public VortexEyeVaultRequestProcessor() {
		super(VortexVaultRequest.class, VortexVaultState.class, VortexVaultResponse.class);
	}

	@Override
	protected VortexVaultResponse processRequest(RequestMessage<VortexVaultRequest> action, VortexVaultState state, ThreadScope threadScope) throws Exception {
		VortexVaultResponse r = nw(VortexVaultResponse.class);
		try {
			VortexVaultRequest req = action.getAction();
			if (req.getDataToStore() != null) {
				for (Entry<Long, byte[]> i : req.getDataToStore().entrySet())
					state.storeDataToVault(i.getKey().longValue(), i.getValue());
			}
			Map<Long, byte[]> vals = new HashMap<Long, byte[]>();
			if (!CH.isEmpty(req.getVvidsToRetrieve())) {
				for (long vvid : req.getVvidsToRetrieve())
					vals.put(vvid, state.getDataFromVault(vvid));
			}
			r.setData(vals);
			r.setOk(true);
		} catch (Exception e) {
			r.setMessage("Failed to retrieve / store data in Vortex Vault");
			r.setOk(false);
			log.log(Level.SEVERE, "Failed to retrieve / store data in Vortex Vault", e);
		}
		return r;
	}

	@Override
	public void init() {
		super.init();
		try {
			VortexEyeDbService dbservice = VortexEyeUtils.getVortexDb(this);
			Object partitionId = getPartitionResolver().getPartitionId(null);
			VortexVaultState state = new VortexVaultState(getContainer(), dbservice);
			state.init();
			getContainer().getPartitionController().putState((String) partitionId, state);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}
}
