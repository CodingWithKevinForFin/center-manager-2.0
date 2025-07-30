package com.f1.ami.web;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.BasicPortletDownload;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AmiWebPreferencesManager {
	final private AmiWebService service;

	private String userPrefId;

	public AmiWebPreferencesManager(AmiWebService service) {
		this.service = service;
	}
	public WebMenu createMenu(WebMenu sink) {
		boolean b = SH.is(this.service.getVarsManager().getUserPrefNamespace());
		BasicWebMenu r = (BasicWebMenu) sink;
		r.addChild(new BasicWebMenuDivider());
		r.addChild(new BasicWebMenuLink("Save Preferences", b, "save_userprefs").setBackgroundImage(AmiWebConsts.ICON_SAVE));
		r.addChild(new BasicWebMenuLink("Load Preferences", b, "load_userprefs").setBackgroundImage(AmiWebConsts.ICON_LOAD_DATA));
		r.addChild(new BasicWebMenuLink("Export Preferences", true, "export_userprefs").setBackgroundImage(AmiWebConsts.ICON_EXPORT_ROOT));
		r.addChild(new BasicWebMenuLink("Import Preferences", true, "import_userprefs").setBackgroundImage(AmiWebConsts.ICON_IMPORT));
		r.addChild(new BasicWebMenuLink("Upload Preferences", true, "upload_userprefs").setBackgroundImage(AmiWebConsts.ICON_UPLOAD));
		r.addChild(new BasicWebMenuLink("Download Preferences", true, "download_userprefs").setBackgroundImage(AmiWebConsts.ICON_DOWNLOAD));
		r.addChild(new BasicWebMenuLink("Reset Preferences", true, "reset_userprefs").setBackgroundImage(AmiWebConsts.ICON_RESET));
		return r;
	}
	public boolean onAmiWebDesktopAction(String action) {
		boolean handled = false;
		if ("export_userprefs".equals(action)) { //AmiWebDesktop onAmiContextMenu
			getManager().showDialog("Layout Preferences", new AmiWebUserExportUserPrefsPortlet(generateConfig(), getService())).setStyle(service.getUserDialogStyleManager());
			handled = true;
		} else if ("save_userprefs".equals(action)) {
			saveUserPrefs();
			getManager().showAlert("User Preferences saved");
			handled = true;
		} else if ("load_userprefs".equals(action)) {
			loadUserPrefs(true);
			handled = true;
		} else if ("import_userprefs".equals(action)) {
			getManager().showDialog("Layout Preferences", new AmiWebUserImportUserPrefsPortlet(generateConfig(), getService())).setStyle(service.getUserDialogStyleManager());
			handled = true;
		} else if ("upload_userprefs".equals(action)) {
			int width = 500;
			int height = 150;
			AmiWebUploadUserPrefsPortlet upp = new AmiWebUploadUserPrefsPortlet(generateConfig(), getService());
			getManager().showDialog("Upload User Preference", upp, width, height).setStyle(service.getUserDialogStyleManager());
			handled = true;
		} else if ("download_userprefs".equals(action)) {
			String ns = this.service.getVarsManager().getUserPrefNamespace();
			String json = this.service.getUserFilesManager().loadFile(AmiWebConsts.USER_SETTING_AMI_PREFS_PREFIX + ns);
			if (SH.is(json))
				getManager().pushPendingDownload(new BasicPortletDownload(ns + ".ami", json.getBytes()));
			else
				getManager().showAlert("You do not have any User preferences saved for this layout.");
			handled = true;
		} else if ("reset_userprefs".equals(action)) {
			resetUserPrefs();
			handled = true;
		} else if ("LOAD_USER_PREFS".equals(action)) { // AmiWebDesktop onButton

			this.loadUserPrefs(true);
			handled = true;
		}
		return handled;
	}
	public void resetUserPrefs() {
		List<Map<String, Object>> prefs = this.getService().getDefaultPrefs();
		this.getService().getVarsManager().applyCustomPrefs((List<Map<String, Object>>) prefs, true);
		this.getService().getDesktop().getCallbacks().execute("onUserPrefsLoading", prefs);
		this.getService().applyUserPrefs(prefs);
		this.getService().getDesktop().getCallbacks().execute("onUserPrefsLoaded", prefs);
	}
	public void loadUserPrefs(boolean showError) {
		String ns = this.getService().getVarsManager().getUserPrefNamespace();
		String json = this.getService().getUserFilesManager().loadFile(AmiWebConsts.USER_SETTING_AMI_PREFS_PREFIX + ns);
		if (SH.is(json)) {
			Object obj = ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(json);
			this.getService().getVarsManager().applyCustomPrefs((List<Map<String, Object>>) obj, true);

			this.getService().getDesktop().getCallbacks().execute("onUserPrefsLoading", obj);
			this.getService().applyUserPrefs((List<Map<String, Object>>) obj);
			this.getService().getDesktop().getCallbacks().execute("onUserPrefsLoaded", obj);
		} else if (showError)
			getManager().showAlert("You do not have any User preferences saved for this layout");

	}
	public void saveUserPrefs() {
		String ns = this.getService().getVarsManager().getUserPrefNamespace();
		List<Map<String, Object>> userPrefs = this.getService().getUserPrefs();
		this.getService().getDesktop().getCallbacks().execute("onUserPrefsSaving", userPrefs);
		String json = this.getService().getLayoutFilesManager().toJson(userPrefs);
		this.getService().getUserFilesManager().saveFile(AmiWebConsts.USER_SETTING_AMI_PREFS_PREFIX + ns, json);
		this.getService().getDesktop().getCallbacks().execute("onUserPrefsSaved", userPrefs);
	};
	public void applyUserPref(Map<String, Object> values) {
		AmiWebDesktopPortlet desktop = getService().getDesktop();
		LinkedHashMap<String, Window> wins = AmiWebUtils.getWindowsWithUniqueNames(desktop);
		List<Map<String, Object>> windows = (List<Map<String, Object>>) values.get("windows");
		if (windows != null) {
			Window activeWindow = null;
			int activeWindowZIndex = -1;
			for (Map<String, Object> m : windows) {

				String name = CH.getOrThrow(Caster_String.INSTANCE, m, "name");
				Window sink = wins.get(name);
				if (sink == null)
					continue;
				importUserPrefs(sink, m);
				// Find the active window
				String state = CH.getOrThrow(Caster_String.INSTANCE, m, "state");
				int zIndex = CH.getOr(Caster_Integer.PRIMITIVE, m, "zindex", -1);
				if (zIndex > activeWindowZIndex && (SH.equals("active", state) || SH.equals("maximized", state))) {
					activeWindowZIndex = zIndex;
					activeWindow = sink;
				}
			}
			//set z-indexes after all the poping-out has been handled
			for (Map<String, Object> m : windows) {
				String name = CH.getOrThrow(Caster_String.INSTANCE, m, "name");
				Window sink = wins.get(name);
				if (sink == null)
					continue;
				int z = CH.getOrThrow(Caster_Integer.INSTANCE, m, "zindex");
				if (z != -1)
					sink.setZindex(z);
			}

			// Set the active window
			service.getDesktop().getDesktop().setActiveWindow(activeWindow);
			service.getDesktop().getDesktop().organizeZIndexes();

		}
	}

	public Map<String, Object> exportWindowsPreferences(Map<String, Object> sink) {
		AmiWebDesktopPortlet desktop = getService().getDesktop();
		List<Map<String, Object>> windows = new ArrayList<Map<String, Object>>();
		for (Entry<String, Window> e : AmiWebUtils.getWindowsWithUniqueNames(desktop).entrySet()) {
			Window win = e.getValue();
			if (desktop.isSpecialPortlet(win.getPortlet()) || desktop.getWindowType(win, false) == AmiWebDesktopPortlet.WIN_TYPE_HIDDEN)
				continue;
			if (SH.endsWith(win.getName(), " [undocked]"))//TODO: this is a hack!
				continue;
			LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
			m.put("name", win.getName());
			exportWindowUserPref(win, m);
			windows.add(m);
		}
		sink.put("windows", windows);
		return sink;
	}
	public static void exportWindowUserPref(Window win, LinkedHashMap<String, Object> m) {
		if (win.isMinimized()) {
			m.put("state", "minimized");
			m.put("left", win.getLeft());
			m.put("top", win.getTop());
			m.put("width", win.getWidth());
			m.put("height", win.getHeight());
			m.put("zindex", win.getZindex());
		} else if (win.isWindowMaximized()) {
			m.put("state", "maximized");
			m.put("left", win.getRestoreLeft());
			m.put("top", win.getRestoreTop());
			m.put("width", win.getRestoreWidth());
			m.put("height", win.getRestoreHeight());
			m.put("zindex", win.getZindex());
		} else {
			m.put("state", win.getDesktop().getActiveWindow() == win ? "active" : "regular");
			m.put("left", win.getLeft());
			m.put("top", win.getTop());
			m.put("width", win.getWidth());
			m.put("height", win.getHeight());
			m.put("zindex", win.getZindex());
		}
		if (win.isPoppedOut()) {
			m.put("pop", win.isPoppedOut());
			RootPortlet p = win.getDesktop().getManager().getPopoutForPortletId(win.getPortletId());
			m.put("screenW", p.getWidth());
			m.put("screenH", p.getHeight());
			m.put("screenX", p.getScreenX());
			m.put("screenY", p.getScreenY());
		}
	}
	public static void importUserPrefs(Window sink, Map<String, Object> m) {
		String state = CH.getOrThrow(Caster_String.INSTANCE, m, "state");
		int l = CH.getOrThrow(Caster_Integer.INSTANCE, m, "left");
		int t = CH.getOrThrow(Caster_Integer.INSTANCE, m, "top");
		int w = CH.getOrThrow(Caster_Integer.INSTANCE, m, "width");
		int h = CH.getOrThrow(Caster_Integer.INSTANCE, m, "height");
		boolean pop = CH.getOr(Caster_Boolean.INSTANCE, m, "pop", Boolean.FALSE);
		if (pop && sink.isPoppedOut()) {
			int screenX = CH.getOrThrow(Caster_Integer.INSTANCE, m, "screenX");
			int screenY = CH.getOrThrow(Caster_Integer.INSTANCE, m, "screenY");
			int screenW = CH.getOr(Caster_Integer.INSTANCE, m, "screenW", w);
			int screenH = CH.getOr(Caster_Integer.INSTANCE, m, "screenH", h);
			sink.getDesktop().getManager().getPopoutForPortletId(sink.getPortletId()).resizeTo(screenX, screenY, screenW, screenH);
		}
		if (pop != sink.isPoppedOut()) {
			if (pop) {
				int screenX = CH.getOr(Caster_Integer.INSTANCE, m, "screenX", 0);
				int screenY = CH.getOr(Caster_Integer.INSTANCE, m, "screenY", 0);
				int screenW = CH.getOr(Caster_Integer.INSTANCE, m, "screenW", w);
				int screenH = CH.getOr(Caster_Integer.INSTANCE, m, "screenH", h);
				sink.floatWindow();
				sink.setPosition(l, t, w, h);
				sink.setRestorePosition(l, t, w, h);
				sink.popoutWindow(screenX, screenY, screenW, screenH);
			} else
				sink.closePopup();
		}
		if ("maximized".equals(state)) {
			if (!sink.isPoppedOut())
				sink.maximizeWindow();
			sink.setRestorePosition(l, t, w, h);
		} else if ("minimized".equals(state)) {
			if (!sink.isPoppedOut())
				sink.minimizeWindowForce();
			sink.setPosition(l, t, w, h);
		} else if ("active".equals(state)) {
			if (!sink.isPoppedOut())
				sink.floatWindow();
			sink.makeActiveWindow();
			sink.setPosition(l, t, w, h);
		} else {
			if (!sink.isPoppedOut())
				sink.floatWindow();
			sink.setPosition(l, t, w, h);
		}
	}
	public String getUserPrefId() {
		return userPrefId;
	}
	public void setUserPrefId(String userPrefId) {
		this.userPrefId = userPrefId;
	}
	private AmiWebService getService() {
		return service;
	}
	private PortletManager getManager() {
		return getService().getPortletManager();
	}
	private PortletConfig generateConfig() {
		return getManager().generateConfig();
	}
}
