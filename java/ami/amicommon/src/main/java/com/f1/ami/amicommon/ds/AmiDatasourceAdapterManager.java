package com.f1.ami.amicommon.ds;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiChainedNamingServiceResolver;
import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourcePlugin;
import com.f1.ami.amicommon.AmiNamingServiceResolver;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.msg.AmiRelayRunDbRequest;
import com.f1.ami.amicommon.msg.AmiResponse;
import com.f1.container.ContainerTools;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiDatasourceAdapterManager {
	private static final Logger log = LH.get();

	private Map<String, AmiDatasourcePlugin> datasourcePlugins = new LinkedHashMap<String, AmiDatasourcePlugin>();
	private Map<String, Integer> nextDsNumForUser = new HashMap<String, Integer>();

	private int maxConcurrentDsQueriesPerUser = 16;
	private ContainerTools tools;
	private AmiNamingServiceResolver namingServiceResolver;

	public AmiDatasourceAdapterManager(ContainerTools tools, AmiNamingServiceResolver namingServiceResolver) {
		this.tools = tools;
		this.namingServiceResolver = namingServiceResolver;
	}

	public String getDsRunnerPartitionId(String datasourceName, String invokedBy) {
		int num = OH.noNull(nextDsNumForUser.get(invokedBy), 0);
		nextDsNumForUser.put(invokedBy, (num + 1) % maxConcurrentDsQueriesPerUser);
		String r = "DS-" + datasourceName + "." + invokedBy + "." + num;
		return r;
	}

	public int getMaxConcurrentDsQueriesPerUser() {
		return maxConcurrentDsQueriesPerUser;
	}

	public void setMaxConcurrentDsQueriesPerUser(int maxConcurrentDsQueriesPerUser) {
		this.maxConcurrentDsQueriesPerUser = maxConcurrentDsQueriesPerUser;
	}
	public AmiDatasourceAdapter createDatasourceAdapter(String adapter, AmiResponse r) {
		AmiDatasourcePlugin plugin = datasourcePlugins.get(adapter);
		if (plugin == null) {
			r.setMessage("Datasource Adapter not found: " + adapter);
			r.setOk(false);
			return null;
		}
		return plugin.createDatasourceAdapter();
	}
	public void addAmiDatasourcePlugin(AmiDatasourcePlugin i) {
		CH.putOrThrow(this.datasourcePlugins, i.getPluginId(), i);
	}

	public Collection<AmiDatasourcePlugin> getAmiDatasourcePlugins() {
		return this.datasourcePlugins.values();
	}
	public AmiDatasourcePlugin getAmiDatasourcePlugin(String name) {
		return this.datasourcePlugins.get(name);
	}

	public RunnableRequestMessage createRunnableMessage(AmiRelayRunDbRequest req, AmiDatasourceRunner runner) {
		String name = req.getDsName();
		RunnableRequestMessage runnable = tools.nw(RunnableRequestMessage.class);
		OH.assertNe(AmiConsts.DATASOURCE_ADAPTER_NAME_AMI, name);
		runnable.setPartitionId(getDsRunnerPartitionId(name, req.getInvokedBy()));
		runnable.setTimeoutMs(req.getTimeoutMs());
		runnable.setRunnable(runner);
		return runnable;
	}

	public void setNamingServiceResolver(AmiChainedNamingServiceResolver namingServiceResolver) {
		this.namingServiceResolver = namingServiceResolver;
	}

	public RunnableRequestMessage newLocatorRunnable(AmiServiceLocator locator, AmiRelayRunDbRequest agentRequest) {
		RunnableRequestMessage r = tools.nw(RunnableRequestMessage.class);
		r.setRunnable(new LocatorRunner(this.namingServiceResolver, locator, agentRequest));
		r.setTimeoutMs(agentRequest.getTimeoutMs());
		return r;
	}

	public class LocatorRunner implements Runnable {

		private AmiNamingServiceResolver namingServiceResolver;
		private AmiServiceLocator locator;
		private AmiRelayRunDbRequest sink;

		public LocatorRunner(AmiNamingServiceResolver namingServiceResolver, AmiServiceLocator locator, AmiRelayRunDbRequest sink) {
			this.namingServiceResolver = namingServiceResolver;
			this.locator = locator;
			this.sink = sink;
		}

		@Override
		public void run() {
			AmiServiceLocator t;
			try {
				t = this.namingServiceResolver.resolve(locator);
				if (t != null) {
					sink.setDsAdapter(t.getTargetPluginId());
					sink.setDsName(t.getTargetName());
					sink.setDsOptions(t.getOptions());
					sink.setDsPassword(t.getPassword() == null ? null : new String(t.getPassword()));
					sink.setDsUrl(t.getUrl());
					sink.setDsUsername(t.getUsername());
				}
			} catch (UnknownHostException e) {
				LH.warning(log, "Error: ", e);
			}
		}

	}

}
