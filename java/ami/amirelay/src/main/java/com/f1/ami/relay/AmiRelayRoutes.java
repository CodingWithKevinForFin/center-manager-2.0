package com.f1.ami.relay;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.container.ContainerTools;
import com.f1.utils.AH;
import com.f1.utils.CachedFile;
import com.f1.utils.CachedFile.Cache;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiRelayRoutes {
	private static final Logger log = LH.get();

	final private CachedFile file;
	final private String fullPath;
	private Cache lastData;
	final private AmiRelayState state;

	private AmiRelayRouter threadSafeRouter;

	private boolean debug;

	private Throwable parseException;

	private int maxCacheSize;

	public AmiRelayRoutes(ContainerTools tools, AmiRelayState state, long delay) {
		this.state = state;
		this.debug = tools.getOptional(AmiRelayProperties.OPTION_AMI_RELAY_ROUTING_DEBUG, Boolean.FALSE);
		File f = tools.getRequired(AmiRelayProperties.OPTION_AMI_RELAY_ROUTING, File.class);
		if (f.isDirectory())
			throw new RuntimeException(AmiRelayProperties.OPTION_AMI_RELAY_ROUTING + " can not point to a directory: " + IOH.getFullPath(f));
		if (!f.exists()) {
			try {
				IOH.writeText(f, IOH.readTextFromResource("relay.routes.template"));
				LH.info(log, "Created default routing file at " + IOH.getFullPath(f));
			} catch (Exception e) {
				LH.info(log, "Could not create default relay routes file at ", IOH.getFullPath(f), ": ", e.getMessage());
			}
		}
		this.file = new CachedFile(f, delay);
		this.fullPath = IOH.getFullPath(this.file.getFile());
		ReusableCalcFrameStack sf = new ReusableCalcFrameStack(state.createStackFrame());
		this.maxCacheSize = tools.getOptional(AmiRelayProperties.OPTION_AMI_RELAY_ROUTES_RULES_CACHE_SIZE, 10000);
		this.threadSafeRouter = new AmiRelayRouter(fullPath, null, sf, false, null, debug, maxCacheSize);
		//		Cache data = this.file.getData();
		//		parse(data);
		//		this.lastData = data;
	}

	private void parse(Cache data) {
		state.getPartition().lockForWrite(60000, TimeUnit.MILLISECONDS);
		try {
			String text = data.getText();
			LH.info(log, "Parsing Relay Route Table from: " + fullPath);
			String[] lines = SH.splitLines(text);
			List<AmiRelayRoute> routes = new ArrayList<AmiRelayRoute>();
			Map<String, AmiRelayRoute> routesByName = new HasherMap<String, AmiRelayRoute>();
			for (int i = 0; i < lines.length; i++) {
				String line = SH.trim(lines[i]);
				if (SH.isnt(line) || SH.startsWith(line, '#'))
					continue;
				try {
					AmiRelayRoute arr = new AmiRelayRoute(line, this.state);
					routes.add(arr);
					if (routesByName.containsKey(arr.getRouteName()))
						throw new RuntimeException("Duplicate RouteName: " + arr.getRouteName());
					routesByName.put(arr.getRouteName(), arr);
				} catch (Exception e) {
					throw new RuntimeException("Error at line " + (i + 1) + " in " + fullPath + ": " + e.getMessage(), e);
				}
			}
			AmiRelayRouteChain chain = new AmiRelayRouteChain(state, routes);
			boolean hasRules = !routes.isEmpty();

			ReusableCalcFrameStack sf = new ReusableCalcFrameStack(state.createStackFrame());
			this.threadSafeRouter = new AmiRelayRouter(fullPath, AH.toArray(routesByName.values(), AmiRelayRoute.class), sf, hasRules, chain, debug, this.maxCacheSize);
		} finally {
			state.getPartition().unlockForWrite();
		}
	}

	public boolean parseIfChanged(boolean throwOnError) {
		if (lastData == null || lastData.isOld()) {
			if (lastData == null)
				this.lastData = this.file.getData();
			else
				lastData = lastData.getUpdated();
			try {
				this.parse(lastData);
				this.parseException = null;
				return true;
			} catch (Throwable e) {
				this.parseException = e;
				if (throwOnError)
					throw OH.toRuntime(e);
				LH.info(log, "Ignoring changes to ", fullPath, " due to parsing error: ", e.getMessage(), e);
			}
		}
		return false;
	}

	public AmiRelayRouter getThreadSafeRouter() {
		return this.threadSafeRouter;
	}

	public boolean getDebug() {
		return this.debug;
	}

	public void setDebugMode(boolean b) {
		this.debug = b;
		this.threadSafeRouter.setDebug(b);
	}

	public Throwable getParseException() {
		return this.parseException;
	}

}
