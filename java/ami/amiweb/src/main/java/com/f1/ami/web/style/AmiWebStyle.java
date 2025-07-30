package com.f1.ami.web.style;

import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebCss;
import com.f1.utils.structs.BasicIndexedList;

public interface AmiWebStyle extends AmiWebStyleManagerListener {

	//fundamentals(id is null if associated to a portlet)
	String getId();
	void setId(String id);
	String getLabel();
	void setLabel(String label);

	boolean hasType(String type);
	public Set<String> getTypes();

	//saving & loading
	void initStyle(Map<String, Object> configuration);
	Map<String, Object> getStyleConfiguration();

	//parents(zero index takes priority)
	String getParentStyle();

	//direct values (does not include cascading, or overriding)
	Object getValue(String styleType, short key);
	void putValue(String styleType, short key, Object value);

	//resolved values (includes cascading and overriding)
	Object resolveValue(String styleType, short name);

	//listeners
	void addListener(String type, AmiWebStyleListener listener);
	void removeListener(String type, AmiWebStyleListener listener);

	void close();

	Set<Short> getDeclaredKeys(String styleType);
	void setParentStyle(String l);
	boolean inheritsFrom(String styleId);
	AmiWebStyleManager getStyleManager();
	String getUrl();
	boolean getReadOnly();
	AmiWebStyle setReadOnly(boolean isReadonly);
	Set<String> getDeclaredVarnames();

	void putValueOverride(String styleType, short key, Object value);
	Object getValueOverride(String styleType, short key);
	Object removeValueOverride(String styleType, short key);
	boolean isValueOverride(String styleType, short key);
	void resetOverrides();

	public boolean isParentStyleOverride();
	public Set<Short> getOverrides(String styleType);
	void resetParentStyleOverride();
	boolean setParentStyleOverride(String id);

	AmiWebCss getCss();
	AmiWebStyleVars getVars();
	BasicIndexedList<String, String> getVarValues();

}
