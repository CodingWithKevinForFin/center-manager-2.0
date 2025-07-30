package com.f1.bootstrap.appmonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;

import com.f1.container.ContainerScope;
import com.f1.container.Partition;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.povo.f1app.F1AppPartition;
import com.f1.povo.f1app.inspect.F1AppInspectionEntity;
import com.f1.povo.f1app.reqres.F1AppInspectPartitionRequest;
import com.f1.povo.f1app.reqres.F1AppInspectPartitionResponse;
import com.f1.speedlogger.SpeedLogger;
import com.f1.utils.CH;
import com.f1.utils.OfflineConverter;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.assist.analysis.AnalyzerManager;
import com.f1.utils.assist.analysis.ClassAnalyzer;

public class AppMonitorInspectPartitionProcessor extends BasicRequestProcessor<F1AppInspectPartitionRequest, AppMonitorState, F1AppInspectPartitionResponse> {

	public AppMonitorInspectPartitionProcessor() {
		super(F1AppInspectPartitionRequest.class, AppMonitorState.class, F1AppInspectPartitionResponse.class);
	}

	@Override
	protected F1AppInspectPartitionResponse processRequest(RequestMessage<F1AppInspectPartitionRequest> req, AppMonitorState state, ThreadScope threadScope) throws Exception {
		final F1AppInspectPartitionRequest action = req.getAction();
		long id = action.getPartitionId();
		long timeout = action.getTimeoutMs();

		final F1AppInspectPartitionResponse r = nw(F1AppInspectPartitionResponse.class);
		if (timeout < 0 || timeout > 5000) {
			r.setMessage("Invalid timeout: " + timeout);
			return r;
		}
		Partition partition = null;
		for (AppMonitorPartitionListener tsl : state.getListeners(AppMonitorPartitionListener.class)) {
			F1AppPartition ao = tsl.getAgentObject();
			if (ao != null && ao.getId() == id) {
				partition = tsl.getObject();
				break;
			}
		}
		if (partition == null) {
			r.setMessage("partion not found: " + id);
			return r;
		}
		final AnalyzerManager am = new AnalyzerManager();
		am.ignore(Class.class);
		am.ignore(ContainerScope.class);
		am.ignore(Logger.class);
		am.ignore(SpeedLogger.class);
		am.ignore(Thread.class);
		am.ignore(AppMonitorObjectListener.class);
		am.ignore(OfflineConverter.class);
		am.ignore(Lock.class);
		AppMonitorPartitionAnalyzer sa = new AppMonitorPartitionAnalyzer(getGenerator());
		if (partition.lockForRead(timeout, TimeUnit.MILLISECONDS)) {
			try {
				ClassAnalyzer ca = am.getClassAnalyzer(partition.getClass());
				if (ca != null)
					sa.process(partition, ca);
			} finally {
				partition.unlockForRead();
			}
			ArrayList<F1AppInspectionEntity> inspectionEntities = new ArrayList<F1AppInspectionEntity>(sa.getEntitiesCount());
			CH.addAll(inspectionEntities, sa.getEntities());
			r.setInspectionEntities(inspectionEntities);
			r.setPartitionSize(sa.getSize());
			final Set<Class<?>> classes = sa.getClasses();
			final Map<String, Long> instances = new HashMap<String, Long>(classes.size());
			final StringBuilder sb = new StringBuilder();
			for (Class<?> clazz : classes)
				instances.put(RH.toLegibleString(clazz, SH.clear(sb)).toString(), sa.getCount(clazz));
			r.setInstances(instances);
			r.setOk(true);
		} else {
			r.setMessage("could not aquire lock in timeout period: " + timeout + "ms");
		}
		return r;
	}

}
