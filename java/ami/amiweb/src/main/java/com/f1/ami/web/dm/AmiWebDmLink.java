package com.f1.ami.web.dm;

import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebFormulasImpl;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.base.Table;

public interface AmiWebDmLink extends AmiWebDomObject {

	public static final String CALLBACK_ONPROCESS = "onProcess";

	short OPTION_ON_RIGHT_CLICK_MENU = 0;
	short OPTION_BRING_TO_FRONT = 1;
	short OPTION_ON_SELECT = 2;
	short OPTION_ON_SELECT_FORCE = 128;
	short OPTION_ON_USER_DBL_CLICK = 64;
	short OPTION_ON_AMISCRIPT = 512;

	short OPTION_EMPTYSEL_IGNORE = 4;
	short OPTION_EMPTYSEL_CLEAR = 8;
	short OPTION_EMPTYSEL_SHOWALL = 16;
	short OPTION_EMPTYSEL_ALLSEL = 32;
	short OPTION_RUN_ON_STARTUP = 256;

	short OPTIONS_FOR_EMPTYSEL = OPTION_EMPTYSEL_IGNORE | OPTION_EMPTYSEL_CLEAR | OPTION_EMPTYSEL_SHOWALL | OPTION_EMPTYSEL_ALLSEL;
	short OPTIONS_MASK_FOR_UPDATE_RELATIONSHIP = OPTION_ON_RIGHT_CLICK_MENU | OPTION_ON_SELECT | OPTION_ON_SELECT_FORCE | OPTION_ON_USER_DBL_CLICK | OPTION_ON_AMISCRIPT;
	public static final String CONFIG_RELID = "relid";

	//key is where varnname, value is whereclause
	Set<String> getWhereClauseVarNames();
	String getWhereClause(String varname);
	AmiWebDmLink addWhereClause(AmiWebDmLinkWhereClause whereClause);
	AmiWebAmiScriptCallbacks getAmiScript();
	AmiWebDmLinkWhereClause getWhereClauseO(String varname);
	AmiWebDmLinkWhereClause removeWhereClauseO(String varname);
	void setCurrentRequest(AmiWebDmRequest req);
	AmiWebDmRequest getCurrentRequest();

	//Identifiers
	void setLinkUid(String linkId);
	String getLinkUid();
	String getTitle();
	@Override
	String getAmiLayoutFullAlias();
	void setAmiLayoutFullAlias(String alias);
	String getRelationshipId();
	void setRelationshipId(String relationshipId);

	//Source Panel
	String getSourcePanelAliasDotId();
	void setSourcePanelAliasDotId(String alias);

	//Target Panel
	String getTargetPanelAliasDotId();
	void setTargetPanelAliasDotId(String alias);

	//Source DM
	void setSourceDm(String fullAliasDotName, String sourceDmTableName);
	String getSourceDmAliasDotName();
	AmiWebDmsImpl getSourceDm();
	String getSourceDmTableName();

	//Target DM
	void setTargetDm(String fullAliasDotName);
	String getTargetDmAliasDotName();
	AmiWebDmsImpl getTargetDm();

	//options 
	short getOptions();
	void setOptions(short options);
	boolean isRunOnSelect();
	boolean isRunOnDoubleClick();
	boolean isRunOnRightClickMenu();
	boolean isRunOnAmiScript();

	//helpers
	boolean ensureValid(StringBuilder errorSink);
	boolean involvesAmiAliasDotPanelId(String adn);
	AmiWebPortlet getSourcePanel();
	AmiWebPortlet getTargetPanel();
	AmiWebPortlet getSourcePanelNoThrow();
	AmiWebPortlet getTargetPanelNoThrow();
	AmiWebDmTableSchema getSourceTable();
	Table getSourceTableData();

	//[de]serialize
	Map<String, Object> getConfiguration();
	void init(Map<String, Object> link, Map<String, String> origToNewPortletIdMapping, StringBuilder warningsSink);
	void onInitDone();

	void bind();
	void onAmiPanelAdnChanged(AmiWebPortlet portlet, String oldPanelId, String panelId);

	@Override
	String getAmiLayoutFullAliasDotId();
	public boolean isSelectionStillEmpty(Table values);
	void close();

	@Override
	AmiWebFormulasImpl getFormulas();
}
