package com.sjls.f1.start.oms;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PreferencesWrapper {

	private Preferences inner;
	private PreferencesWrapper parent;
	private String relativePath;
	private Preferences parentPreferences;

	public PreferencesWrapper(PreferencesWrapper parent, String relativePath) {
		this.parent = parent;
		this.relativePath = relativePath;
	}

	public PreferencesWrapper(Preferences parentPreferences, String relativePath) {
		this.parentPreferences = parentPreferences;
		this.relativePath = relativePath;
	}

	public String getAbsolutePath() {
		if (inner != null)
			return inner.absolutePath();
		if (parent != null)
			return parent.getAbsolutePath() + '/' + relativePath;
		return parentPreferences.absolutePath() + '/' + relativePath;
	}

	private Preferences getInner() {
		if (inner == null) {
			if (parentPreferences != null)
				inner = parentPreferences.node(relativePath);
			else
				inner = parent.getInner().node(relativePath);
		}
		return inner;
	}

	public boolean getBoolean(String key, boolean def) {
		return inner.getBoolean(key, def);
	}
	public void putBoolean(String key, boolean val) {
		inner.putBoolean(key, val);
	}
	public byte[] getByteArray(String key, byte[] def) {
		return inner.getByteArray(key, def);
	}
	public void putByteArray(String key, byte[] val) {
		inner.putByteArray(key, val);
	}
	public float getFloat(String key, float def) {
		return inner.getFloat(key, def);
	}
	public void putFloat(String key, float val) {
		inner.putFloat(key, val);
	}
	public double getDouble(String key, double def) {
		return inner.getDouble(key, def);
	}
	public void putDouble(String key, double val) {
		inner.putDouble(key, val);
	}
	public long getLong(String key, long def) {
		return inner.getLong(key, def);
	}
	public void putLong(String key, long val) {
		inner.putLong(key, val);
	}
	public int getInt(String key, int def) {
		return inner.getInt(key, def);
	}
	public void putInt(String key, int val) {
		inner.putInt(key, val);
	}
	public String getString(String key, String def) {
		return inner.get(key, def);
	}
	public void putString(String key, String val) {
		inner.put(key, val);
	}
	// protected PreferencesWrapper createChild(String relativePath, boolean init) {
	// if()
	// PreferencesWrapper r = new PreferencesWrapper(this, relativePath);
	// children.add()
	// return r;
	// }

	private List<PreferencesWrapper> children = new ArrayList<PreferencesWrapper>();
	public <T extends PreferencesWrapper> T addChild(T child) {
		children.add(child);
		return child;
	}

	private void flush() throws BackingStoreException {
		if (inner != null)
			inner.flush();
	}

}
