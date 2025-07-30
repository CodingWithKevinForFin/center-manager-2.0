package com.f1.ami.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.ami.amicommon.webfilespecial.AmiSpecialFileProcessor;
import com.f1.ami.web.amiscript.AmiWebFileSystem;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.suite.web.WebState;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AmiWebAutosaveManager {
	private static final Logger log = LH.get();
	private static final String DEFAULT_DIRECTORY = "data/autosave";
	private static final int DEFAULT_COUNT = 100;
	private static final String REASON_SHUTDOWN = "shutdown";
	private static final String REASON_LOGOUT = "logout";
	private static final String REASON_TIMER = "timer";
	private static final String REASON_EDITMODE_TOGGLED = "editmode-toggled";
	private static final String REASON_LOAD_ACTION = "load-action";
	private static final String REASON_SAVE_ACTION = "save-action";
	private static final String REASON_REBUILD_ACTION = "rebuild-action";

	final private AmiWebService service;
	final private boolean enabled;
	private AutosaveOnShutdown hook;

	//AmiWebProperties
	private long autosaveFrequency;
	private AmiWebFile autosaveDir;
	private int backupCount;

	private AmiWebSafeFile cntFile;
	private AmiWebFile curFile;
	private AmiWebFile pastFile;

	private long lastSave = 0;
	private String lastLayoutAutosaved = null;
	private boolean wasInEditModeSinceLastSave = false;

	private final StringBuilder tmpBuf = new StringBuilder();
	private AmiWebFileSystem fs;

	public AmiWebAutosaveManager(AmiWebService service) {
		this.service = service;
		this.fs = this.service.getAmiFileSystem();
		AmiWebVarsManager varsManager = this.service.getVarsManager();
		this.enabled = (varsManager.isUserAdmin() || varsManager.isUserDev());
		if (enabled)
			Runtime.getRuntime().addShutdownHook(hook = new AutosaveOnShutdown());
		ContainerTools tools = this.service.getPortletManager().getTools();
		this.autosaveFrequency = SH.parseDurationTo(tools.getOptional(AmiWebProperties.PROPERTY_AMI_AUTOSAVE_FREQUENCY, "15 MINUTES"), TimeUnit.MILLISECONDS);
		this.autosaveDir = fs.getFile(tools.getOptional(AmiWebProperties.PROPERTY_AMI_AUTOSAVE_DIR, DEFAULT_DIRECTORY));
		this.backupCount = tools.getOptional(AmiWebProperties.PROPERTY_AMI_AUTOSAVE_COUNT, DEFAULT_COUNT);
		try {
			this.autosaveDir.mkdirForce();
			String name = AmiWebUtils.toValidVarname(varsManager.getUsername());
			//			this.cntFile = new SafeFile(new File(this.autosaveDir, name + ".cnt"));
			this.cntFile = fs.getSafeFile(fs.getFile(this.autosaveDir, name + ".cnt"));
			this.curFile = fs.getFile(this.autosaveDir, name + ".cur");
			this.pastFile = fs.getFile(this.autosaveDir, name + ".old");
		} catch (IOException e) {
			throw new RuntimeException("Autosave setup failed", e);
		}

	}

	public List<AutoSaveFile> getHistory() {
		List<AutoSaveFile> r = new ArrayList<AutoSaveFile>();
		if (this.pastFile.exists())
			read(this.pastFile, r);
		if (this.curFile.exists())
			read(this.curFile, r);
		return r;
	}
	public AutoSaveFile getLastAutoSave(String layoutName) {
		List<AutoSaveFile> history = getHistory();
		if (CH.isEmpty(history))
			return null;
		for (int i = history.size() - 1; i >= 0; i--) {
			AutoSaveFile autosave = history.get(i);
			if (SH.equals(autosave.getLayoutName(), layoutName))
				return autosave;
		}
		return null;
	}
	public AutoSaveFile getLayout(int id) {
		AutoSaveFile r = null;
		if (this.pastFile.exists())
			r = read(this.pastFile, id);
		if (r == null && this.curFile.exists())
			r = read(this.curFile, id);
		return r;
	}

	public void onLoadLocal() {
		autoSave(REASON_LOAD_ACTION);
	}
	public void onSaveLayout() {
		autoSave(REASON_REBUILD_ACTION);
	}
	public void onLayoutSaved() {
		autoSave(REASON_SAVE_ACTION);
	}
	public void onRebuildLayout() {
		autoSave(REASON_REBUILD_ACTION);
	}

	public void onEditModeChanged(AmiWebDesktopPortlet amiWebDesktopPortlet, boolean inEditMode) {
		if (inEditMode)
			wasInEditModeSinceLastSave = true;
		else
			autoSave(REASON_EDITMODE_TOGGLED);

	}

	public void onPortletManagerClosed() {
		if (this.hook != null)
			Runtime.getRuntime().removeShutdownHook(hook);
		this.hook = null;
	}

	public void onClosed() {
		if (this.hook != null)
			Runtime.getRuntime().removeShutdownHook(hook);
		this.hook = null;
	}
	public void onLogout() {
		autoSave(REASON_LOGOUT);
	}

	public void onFrontendCalled() {
		if (!getDesktop().getIsLocked()) {
			long now = service.getPortletManager().getNow();
			if (lastSave == 0)
				lastSave = now;
			else if (lastSave + autosaveFrequency <= now) {
				lastSave = now;
				if (wasInEditModeSinceLastSave) {
					autoSave(REASON_TIMER);
					wasInEditModeSinceLastSave = getDesktop().getInEditMode();
				}
			}
		}

	}

	private AmiWebDesktopPortlet getDesktop() {
		return service.getDesktop();
	}
	private void saveToLog(String txt, String reason) {
		if (getDesktop().getIsLocked())
			return;
		if (txt == null || OH.eq(txt, lastLayoutAutosaved))
			return;
		if (txt.indexOf('\n') != -1)
			throw new IllegalArgumentException("Layout contains newline");
		if (reason.indexOf('\n') != -1)
			throw new IllegalArgumentException("reason contains newline");
		SH.clear(tmpBuf);
		final WebState ws = service.getPortletManager().getState();

		int recordsCount;
		try {
			recordsCount = SH.parseInt(OH.noNull(this.cntFile.getText(), "0").trim());
			this.cntFile.setText(SH.toString(recordsCount + 1) + SH.NEWLINE);
		} catch (Exception e) {
			LH.warning(log, service.getUserName(), " Could not retrieve revision number at ", this.cntFile.getFile().getAbsolutePath(), " so autosave disabled", e);
			return;
		}
		long now = service.getPortletManager().getNow();
		tmpBuf.append(recordsCount);
		tmpBuf.append('|').append(now);
		tmpBuf.append('|').append(reason);
		AmiWebLayoutFilesManager layoutFilesManager = service.getLayoutFilesManager();
		tmpBuf.append('|').append(layoutFilesManager.getLayoutSource() + ":" + OH.noNull(layoutFilesManager.getLayoutName(), ""));
		tmpBuf.append('|').append(this.service.getFormatterManager().getDateTimeSecsWebCellFormatter().formatCellToText(this.service.getPortletManager().getNow()));
		tmpBuf.append('|').append(ws.getPartitionId());
		tmpBuf.append('|').append(txt).append(SH.NEWLINE);
		lastLayoutAutosaved = txt;
		try {
			this.curFile.appendText(SH.toStringAndClear(tmpBuf));
			if (recordsCount % backupCount == 0) {
				this.pastFile.deleteForceRecursive();
				this.curFile.moveForce(pastFile);
				this.curFile.appendText("");
			}

		} catch (IOException e) {
			LH.warning(log, "Error saving autosave", e);
		}
	}
	public void onLayoutLoaded() {
		this.lastLayoutAutosaved = AmiWebLayoutHelper.toJson(service.getLayoutFilesManager().getLayoutConfiguration(true), ObjectToJsonConverter.MODE_COMPACT);
	}
	private void autoSave(String reason) {
		if (getDesktop().getIsLocked())
			return;
		if (getDesktop().getDesktop() == null)
			return;
		AmiWebLayoutFile layout = service.getLayoutFilesManager().getLayout();
		if (layout.getSource() == AmiWebConsts.LAYOUT_SOURCE_TMP && layout.getLocation() != null)
			return;
		String txt = AmiWebLayoutHelper.toJson(service.getLayoutFilesManager().getLayoutConfiguration(true), ObjectToJsonConverter.MODE_COMPACT);
		saveToLog(txt, reason);
	}

	private void read(AmiWebFile file, List<AutoSaveFile> r) {
		try {
			Table table = fs.getSpecial(file, AmiSpecialFileProcessor.GET_AUTOSAVE_LIST, null);
			if (table != null)
				for (Row row : table.getRows()) {
					int id = row.get("id", Caster_Integer.INSTANCE);
					long now = row.get("now", Caster_Long.INSTANCE);
					String reason = row.get("reason", Caster_String.INSTANCE);
					String name = row.get("name", Caster_String.INSTANCE);
					String layout = row.get("layout", Caster_String.INSTANCE);
					r.add(new AutoSaveFile(id, now, reason, name, layout));
				}
		} catch (Exception e) {
			LH.warning(log, e);
		}
	}
	private AutoSaveFile read(AmiWebFile file, int id) {
		try {
			Table table = fs.getSpecial(file, AmiSpecialFileProcessor.GET_AUTOSAVE_INSTANCE, (Map) CH.m("id", id));
			if (table != null)
				for (Row row : table.getRows()) {
					id = row.get("id", Caster_Integer.INSTANCE);//should match!
					long now = row.get("now", Caster_Long.INSTANCE);
					String reason = row.get("reason", Caster_String.INSTANCE);
					String name = row.get("name", Caster_String.INSTANCE);
					String layout = row.get("layout", Caster_String.INSTANCE);
					return new AutoSaveFile(id, now, reason, name, layout);
				}
		} catch (Exception e) {
			LH.warning(log, e);
			return null;
		}
		return null;
	}

	public static class AutoSaveFile {
		final private int id;
		final private long timestamp;
		final private String reason;
		final private String layoutName;
		final private String layout;

		public AutoSaveFile(int id, long timestamp, String reason, String layoutName, String layout) {
			this.id = id;
			this.timestamp = timestamp;
			this.reason = reason;
			this.layoutName = layoutName;
			this.layout = layout;
		}

		public int getNumber() {
			return id;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public String getReason() {
			return reason;
		}

		public String getLayoutName() {
			return layoutName;
		}
		public String getLayout() {
			return layout;
		}
	}

	private class AutosaveOnShutdown extends Thread {

		public void run() {
			try {
				System.err.println("System shutdown, auto-saving layout on shutdown for '" + AmiWebAutosaveManager.this.service.getUserName() + "'...");
				autoSave(AmiWebAutosaveManager.REASON_SHUTDOWN);
			} catch (Throwable e) {
				System.err.println("Could not save layout on shutdown to " + curFile.getFullPath());
				e.printStackTrace(System.err);
			}
		}
	}

}
