package com.f1.ami.web;

import java.util.List;

import com.f1.utils.converter.json2.Jsonable;
import com.f1.utils.structs.table.derived.ToDerivedString;

public interface AmiWebDomObject extends ToDerivedString, Jsonable {
	public static final byte DOM_EVENT_CODE_NONE = 0;
	public static final byte DOM_EVENT_CODE_ONCHANGE = 1;
	public static final String DOM_EVENT_NAME_NONE = "None";
	public static final String DOM_EVENT_NAME_ONCHANGE = "Onchange";
	public static final byte[] DOM_EVENTS = { DOM_EVENT_CODE_NONE, DOM_EVENT_CODE_ONCHANGE };

	public static final String ARI_TYPE_SESSION = "SESSION";
	public static final String ARI_TYPE_LAYOUT = "LAYOUT";
	public static final String ARI_TYPE_PANEL = "PANEL";
	public static final String ARI_TYPE_DATAMODEL = "DATAMODEL";
	public static final String ARI_TYPE_RELATIONSHIP = "RELATIONSHIP";
	public static final String ARI_TYPE_FIELD = "FIELD";
	public static final String ARI_TYPE_FIELD_VALUE = "FIELDVALUE";//TODO:
	public static final String ARI_TYPE_MENUITEM = "MENUITEM";
	public static final String ARI_TYPE_FORMBUTTON = "FORMBUTTON";
	public static final String ARI_TYPE_CHART_LAYER = "CHART_LAYER";
	public static final String ARI_TYPE_CHART_PLOT = "CHART_PLOT";
	public static final String ARI_TYPE_CHART_AXIS = "CHART_AXIS";
	public static final String ARI_TYPE_TAB_ENTRY = "TAB_ENTRY";
	public static final String ARI_TYPE_PROCESSOR = "PROCESSOR";
	public static final String ARI_TYPE_COLUMN = "COLUMN";
	public static final String ARI_TYPE_GROUPING = "GROUPING";
	public static final String ARI_TYPE_JOIN = "JOIN";

	public static final byte ARI_CODE_SESSION = 1;
	public static final byte ARI_CODE_LAYOUT = 2;
	public static final byte ARI_CODE_PANEL = 3;
	public static final byte ARI_CODE_DATAMODEL = 4;
	public static final byte ARI_CODE_RELATIONSHIP = 5;
	public static final byte ARI_CODE_FIELD = 6;
	public static final byte ARI_CODE_FIELD_VALUE = 7;
	public static final byte ARI_CODE_MENUITEM = 8;
	public static final byte ARI_CODE_FORMBUTTON = 9;
	public static final byte ARI_CODE_CHART_LAYER = 10;
	public static final byte ARI_CODE_CHART_PLOT = 11;
	public static final byte ARI_CODE_CHART_AXIS = 12;
	public static final byte ARI_CODE_TAB_ENTRY = 13;
	public static final byte ARI_CODE_PROCESSOR = 14;
	public static final byte ARI_CODE_COLUMN = 15;
	public static final byte ARI_CODE_GROUPING = 16;
	public static final byte ARI_CODE_JOIN = 17;

	public String getAriType();//aritype
	public String getDomLabel();//domlabel
	public String getAmiLayoutFullAlias();//layout1.layout2.layout3
	public String getAmiLayoutFullAliasDotId();//layout1.layout2.layout3.domlabel<extras>
	public String getAri();//aritype:layout1.layout2.layout3.domlabel<extras>
	public void updateAri();
	public List<AmiWebDomObject> getChildDomObjects();
	public AmiWebDomObject getParentDomObject();
	public Class<?> getDomClassType();
	public Object getDomValue();
	public boolean isTransient();
	public void setTransient(boolean isTransient);

	//May return null
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks();
	public void addToDomManager();
	public void removeFromDomManager();
	public AmiWebFormulas getFormulas();
	public AmiWebService getService();
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula formula);
}
