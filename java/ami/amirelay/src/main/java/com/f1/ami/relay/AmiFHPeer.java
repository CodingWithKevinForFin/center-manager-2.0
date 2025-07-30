package com.f1.ami.relay;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import com.f1.ami.relay.fh.AmiFH;
import com.f1.ami.relay.plugins.AmiRelayInvokablePlugin;
import com.f1.container.ContainerTools;

public class AmiFHPeer implements AmiRelayIn {

	final private AmiFH fh;
	final private int fhId;
	final private String fhName;
	final private AmiRelayServer server;
	private String fhAppId;
	private short fhAppIdKey;
	private AtomicLong messageCount = new AtomicLong();
	private AtomicLong errorCount = new AtomicLong();

	public AmiFHPeer(AmiRelayServer server, AmiFH fh, int id, String name) {
		this.server = server;
		this.fh = fh;
		this.fhId = id;
		this.fhName = name;
	}

	@Override
	public void onStatus(byte[] params) {
		server.onStatus(this, params);
		incMessageCount();
	}

	private void incMessageCount() {
		this.messageCount.incrementAndGet();
	}

	@Override
	public void onResponse(String id, int status, String msg, String amiScript, Map<String, Object> params) {
		server.onResponse(this, id, status, msg, amiScript, params);
	}

	@Override
	public void onError(byte[] params, CharSequence bufout) {
		server.onError(this, params, bufout);
		this.errorCount.incrementAndGet();
	}

	@Override
	public void onLogout(byte[] params, boolean clean) {
		server.onLogout(this, params, clean);
		incMessageCount();
	}

	@Override
	public void onLogin(String options, String plugin, byte[] params) {
		server.onLogin(this, options, plugin, params);
		incMessageCount();
	}

	@Override
	public void onConnection(byte[] params) {
		server.onConnection(this, params);
	}

	@Override
	public void onCommandDef(String id, String title, int lvl, String whereClause, String filterClause, String help, String arguments, String script, int priority,
			String enableClause, String style, String selectMode, String fields, byte[] params, int callbacksMask) {
		server.onCommandDef(this, id, title, lvl, whereClause, filterClause, help, arguments, script, priority, enableClause, style, selectMode, fields, params, callbacksMask);
		incMessageCount();
	}

	//	@Override
	//	public void onObjects(long origSeqnum, String id, String[] types, long expires, byte[] params) {
	//		server.onObjects(this, origSeqnum, ids, types, expires, params);
	//	}

	@Override
	public void onObject(long origSeqnum, String id, String type, long expires, byte[] params) {
		server.onObject(this, origSeqnum, id, type, expires, params);
		incMessageCount();
	}

	@Override
	public void onObjectDelete(long origSeqnum, String ids, String type, byte[] params) {
		server.onObjectDelete(this, origSeqnum, ids, type, params);
		incMessageCount();
	}

	@Override
	public ContainerTools getTools() {
		return server.getTools();
	}

	@Override
	public ThreadFactory getThreadFactory() {
		return server.getThreadFactory();
	}

	@Override
	public void initAndStartFH(AmiFH fh2, String name) {
		server.initAndStartFH(fh2, name);
	}

	@Override
	public AmiRelayInvokablePlugin getInvokable(String typ) {
		return server.getInvokable(typ);
	}

	@Override
	public Set<String> getInvokableTypes() {
		return server.getInvokableTypes();
	}

	public int getFhId() {
		return this.fhId;
	}

	public String getFhName() {
		return this.fhName;
	}

	public AmiFH getFh() {
		return this.fh;
	}

	public short getFhAppIdKey() {
		ensureAppId();
		return this.fhAppIdKey;
	}
	public String getAppId() {
		return this.fh.getAppId();
	}

	private void ensureAppId() {
		if (this.fhAppId != this.fh.getAppId())
			this.fhAppIdKey = this.server.getStringkey(this.fhAppId = this.fh.getAppId());
	}

	public long getMessageCount() {
		return this.messageCount.get();
	}

	public long getErrorsCount() {
		return this.errorCount.get();
	}
}
