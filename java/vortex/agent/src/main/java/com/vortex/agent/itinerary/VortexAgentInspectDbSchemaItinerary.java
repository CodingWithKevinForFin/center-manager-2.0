package com.vortex.agent.itinerary;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.utils.DBH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.db.DriverToDataSource;
import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentInspectDbRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentInspectDbResponse;
import com.vortex.agent.dbadapter.DbInspector;
import com.vortex.agent.dbadapter.MsSqlServerDbInspector;
import com.vortex.agent.dbadapter.MysqlDbInspector;
import com.vortex.agent.dbadapter.OracleDbInspector;
import com.vortex.agent.dbadapter.SybaseDbInspector;
import com.vortex.agent.state.VortexAgentState;

public class VortexAgentInspectDbSchemaItinerary extends AbstractVortexAgentItinerary<VortexAgentInspectDbRequest> {
	private static final Logger log = LH.get(VortexAgentInspectDbSchemaItinerary.class);

	private VortexAgentInspectDbResponse r;

	@Override
	public byte startJourney(VortexAgentItineraryWorker worker) {
		final RequestMessage<VortexAgentInspectDbRequest> action = getInitialRequest();
		final VortexAgentState state = getState();
		r = nw(VortexAgentInspectDbResponse.class);
		final VortexAgentDbServer server = nw(VortexAgentDbServer.class);
		final VortexAgentInspectDbRequest req = action.getAction();
		server.setDbType(req.getDbType());
		server.setUrl(req.getUrl());
		server.setPassword(req.getPassword());
		server.setMachineUid(state.getMachineUid());
		r.setDbServer(server);
		r.setStartTime(getTools().getNow());
		r.setMachineUid(state.getMachineUid());
		//r.setMachineUid(state.getCurrentMachineSnapshot().getMachine().getMachineUid());
		final DriverToDataSource datasource;
		try {
			datasource = DBH.createDataSource(req.getUrl(), req.getPassword());
		} catch (Exception e) {
			LH.warning(log, "error connecting to db at ", req.getUrl(), e);
			if (SH.is(e.getMessage()))
				r.setMessage(e.getMessage());
			else
				r.setMessage("Unknown db error");
			server.setStatus(VortexAgentDbServer.STATUS_CONNECTION_ERROR);
			server.setMessage(r.getMessage());
			return STATUS_COMPLETE;
		}
		try {
			DbInspector dbi = null;
			switch (req.getDbType()) {
				case VortexAgentDbServer.TYPE_MYSQL:
					dbi = DbInspector.get(state.getPartition().getContainer(), MysqlDbInspector.ID);
					break;
				case VortexAgentDbServer.TYPE_SQLSERVER:
					dbi = DbInspector.get(state.getPartition().getContainer(), MsSqlServerDbInspector.ID);
					break;
				case VortexAgentDbServer.TYPE_ORACLE:
					dbi = DbInspector.get(state.getPartition().getContainer(), OracleDbInspector.ID);
					break;
				case VortexAgentDbServer.TYPE_SYBASE:
					dbi = DbInspector.get(state.getPartition().getContainer(), SybaseDbInspector.ID);
					break;
				default:
					throw new RuntimeException("Unkown db type: " + req.getDbType());
			}
			Map<String, VortexAgentDbDatabase> databases = dbi.inspectDatabase(datasource.getConnection());
			server.setDatabases(databases);
			r.setOk(true);
		} catch (Exception e) {
			LH.warning(log, "error inspecting db at ", req.getUrl(), e);
			if (SH.is(e.getMessage()))
				r.setMessage(e.getMessage());
			else
				r.setMessage("Unknown db error");
			server.setStatus(VortexAgentDbServer.STATUS_GENERAL_ERROR);
			server.setMessage(r.getMessage());
		}
		r.setEndTime(getTools().getNow());
		return STATUS_COMPLETE;
	}

	@Override
	public byte onResponse(ResultMessage<?> result, VortexAgentItineraryWorker worker) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Message endJourney(VortexAgentItineraryWorker worker) {
		return r;
	}

}
