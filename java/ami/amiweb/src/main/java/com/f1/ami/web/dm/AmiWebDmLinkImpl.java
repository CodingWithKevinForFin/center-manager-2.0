package com.f1.ami.web.dm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebDesktopLinkHelper;
import com.f1.ami.web.AmiWebDmPortlet;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulasImpl;
import com.f1.ami.web.AmiWebLinkableVarsPortlet;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.ami.web.filter.SourceTargetHelper;
import com.f1.base.Table;
import com.f1.base.ToStringable;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebDmLinkImpl implements AmiWebDmLink, ToStringable {

	public static final ParamsDefinition CALLBACK_DEF_ONPROCESS = new ParamsDefinition(CALLBACK_ONPROCESS, Boolean.class, "com.f1.base.Table selected");
	static {
		CALLBACK_DEF_ONPROCESS.addDesc("Called before the relationship is executed. Note: returning false will stop the relationship from being run");
		CALLBACK_DEF_ONPROCESS.addParamDesc(0, "table of selected values");
		CALLBACK_DEF_ONPROCESS.addRetDesc("Returning the boolean value false will cause the relationship to not run, any other value and the relationship will execute");
	}
	final private AmiWebDmManager dmManager;
	final private AmiWebService service;
	private Map<String, AmiWebDmLinkWhereClause> whereStatements = new TreeMap<String, AmiWebDmLinkWhereClause>();

	private String title;
	private String relationshipId;

	private String sourcePanelAliasDotName;//this is for caching
	private AmiWebPortlet sourcePanel = null;//this is for caching

	private String targetPanelAliasDotName;
	private AmiWebPortlet targetPanel = null;//this is for caching

	private AmiWebDmsImpl sourceDm;//this is for caching
	private String sourceDmAliasDotName;
	private String sourceDmTableName;

	private AmiWebDmsImpl targetDm;//this is for caching
	private String targetDmAliasDotName = null;

	private short options;
	private AmiWebAmiScriptCallbacks amiScript;
	private AmiWebDmRequest currentRequest;
	private String alias = "";
	private String aliasDotName;
	private String linkId;

	public AmiWebDmLinkImpl(AmiWebDmManager manager) {
		this.dmManager = manager;
		this.service = dmManager.getService();
		this.formulas = new AmiWebFormulasImpl(this);
		this.amiScript = new AmiWebAmiScriptCallbacks(service, this);
		for (ParamsDefinition i : service.getScriptManager().getCallbackDefinitions(getClass()))
			this.amiScript.registerCallbackDefinition(i);
		this.amiScript.setAmiLayoutAlias("");
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("DMLINK[").append(sourcePanelAliasDotName).append(" --> ").append(targetPanelAliasDotName).append(": ");
		sink.append(": ").append(whereStatements);
		sink.append("]");
		return sink;
	}

	public AmiWebDmLinkImpl(AmiWebDmManager manager, String alias, String linkId, String title, short options) {
		this(manager);
		this.alias = alias;
		this.amiScript.setAmiLayoutAlias(alias);
		this.linkId = linkId;
		this.title = title;
		this.options = options;
	}

	@Override
	public AmiWebDmLinkImpl addWhereClause(AmiWebDmLinkWhereClause whereClause) {
		CH.putOrThrow(this.whereStatements, whereClause.getVarName(), whereClause);
		return this;
	}

	public AmiWebDmLinkImpl clearWhereClause() {
		whereStatements.clear();
		return this;
	}

	@Override
	public short getOptions() {
		return options;
	}

	@Override
	public AmiWebDmsImpl getSourceDm() {
		return this.sourceDm != null ? this.sourceDm : dmManager.getDmByAliasDotName(this.sourceDmAliasDotName);
	}

	@Override
	public AmiWebDmsImpl getTargetDm() {
		return this.targetDm != null ? this.targetDm : dmManager.getDmByAliasDotName(this.targetDmAliasDotName);
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public String getLinkUid() {
		return this.linkId;
	}

	@Override
	public String getSourceDmTableName() {
		return this.sourceDmTableName;
	}

	@Override
	public boolean involvesAmiAliasDotPanelId(String adn) {
		return OH.eq(adn, this.sourcePanelAliasDotName) || OH.eq(adn, this.targetPanelAliasDotName);
	}

	@Override
	public AmiWebDmTableSchema getSourceTable() {
		return getSourceDm().getResponseOutSchema().getTable(getSourceDmTableName());
	}

	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public void setSourcePanelAliasDotId(String adn) {
		if (OH.eq(this.sourcePanel, adn))
			return;
		this.sourcePanelAliasDotName = adn;
		this.sourcePanel = null;
	}

	@Override
	public void close() {
		if (this.sourcePanel != null) {
			sourcePanel.removeDmLinkFromThisPortlet(this);
			this.sourcePanel = null;
		}
		if (this.targetPanel != null) {
			targetPanel.removeDmLinkToThisPortlet(this);
			this.targetPanel = null;
		}
		if (this.sourceDm != null) {
			sourceDm.removeDmLinkFromThisDm(this);
			this.sourceDm = null;
		}
		if (this.targetDm != null) {
			targetDm.removeDmLinkToThisDm(this);
			this.targetDm = null;
		}

		this.amiScript.close();
		this.removeFromDomManager();
	}
	public void bind() {
		AmiWebPortlet origSourcePanel = this.sourcePanel;
		this.sourcePanel = (AmiWebPortlet) service.getPortletByAliasDotPanelId(this.sourcePanelAliasDotName);
		if (this.sourcePanel != origSourcePanel) {
			if (origSourcePanel != null)
				origSourcePanel.removeDmLinkFromThisPortlet(this);
			if (this.sourcePanel != null)
				this.sourcePanel.addDmLinkFromThisPortlet(this);
		}
		AmiWebPortlet origTargetPanel = this.targetPanel;
		this.targetPanel = (AmiWebPortlet) service.getPortletByAliasDotPanelId(this.targetPanelAliasDotName);
		if (this.targetPanel != origTargetPanel) {
			if (origTargetPanel != null)
				origTargetPanel.removeDmLinkToThisPortlet(this);
			if (this.targetPanel != null)
				this.targetPanel.addDmLinkToThisPortlet(this);
		}
		AmiWebDm origSourceDm = this.sourceDm;
		this.sourceDm = dmManager.getDmByAliasDotName(sourceDmAliasDotName);
		if (this.sourceDm != origSourceDm) {
			if (origSourceDm != null)
				origSourceDm.removeDmLinkFromThisDm(this);
			if (this.sourceDm != null)
				this.sourceDm.addDmLinkFromThisDm(this);
		}
		AmiWebDm origTargetDm = this.targetDm;
		this.targetDm = dmManager.getDmByAliasDotName(targetDmAliasDotName);
		if (this.targetDm != origTargetDm) {
			if (origTargetDm != null)
				origTargetDm.removeDmLinkToThisDm(this);
			if (this.targetDm != null)
				this.targetDm.addDmLinkToThisDm(this);
		}
	}

	@Override
	public void setTargetPanelAliasDotId(String adn) {
		if (OH.eq(this.targetPanelAliasDotName, adn))
			return;
		this.targetPanelAliasDotName = adn;
		this.targetPanel = null;
	}

	@Override
	public AmiWebAmiScriptCallbacks getAmiScript() {
		return amiScript;
	}
	//	public void setAmiScript(String amiScript) {
	//		this.amiScript = amiScript;
	//	}

	@Override
	public void init(Map<String, Object> link, Map<String, String> origToNewPortletIdMapping, StringBuilder warningsSink) {
		linkId = BasicPortletManager.generateIdStatic();

		this.sourceDmAliasDotName = AmiWebUtils.getFullAlias(alias, CH.getOr(Caster_String.INSTANCE, link, "sdmadn", null));
		this.sourceDmTableName = CH.getOr(Caster_String.INSTANCE, link, "sdmtb", null);
		this.targetDmAliasDotName = AmiWebUtils.getFullAlias(alias, CH.getOr(Caster_String.INSTANCE, link, "tdmadn", null));
		this.sourcePanelAliasDotName = AmiWebUtils.getFullAlias(alias, CH.getOrThrow(Caster_String.INSTANCE, link, "spadn"));
		this.targetPanelAliasDotName = AmiWebUtils.getFullAlias(alias, CH.getOrThrow(Caster_String.INSTANCE, link, "tpadn"));
		OH.assertEq(this.sourcePanelAliasDotName != null, this.targetPanelAliasDotName != null);

		String whereClause = CH.getOr(Caster_String.INSTANCE, link, "wc", null);

		if (whereClause != null) { //backwardsCompatible
			whereClause = SH.replaceAll(whereClause, "{", "${");
			this.addWhereClause(new AmiWebDmLinkWhereClause(this, "WHERE", whereClause));
		} else if (link.containsKey("wcs")) {
			Map<String, String> mapWheres = (Map<String, String>) CH.getOrThrow(Caster_Simple.OBJECT, link, "wcs");
			for (String varname : mapWheres.keySet()) {
				this.addWhereClause(new AmiWebDmLinkWhereClause(this, varname, mapWheres.get(varname)));
			}
		} else if (link.containsKey("awcs")) {
			HashMap<String, Object> awcs = new HashMap<String, Object>();
			Object o = link.get("awcs");
			Collection<Map> values = o instanceof Map ? (((Map) o).values()) : (Collection<Map>) o;
			for (Object whereJson : values)
				this.addWhereClause(new AmiWebDmLinkWhereClause(this, (Map<String, Object>) whereJson, warningsSink));
		}
		title = CH.getOrThrow(Caster_String.INSTANCE, link, "title");

		//backwards comp.
		String amiScript = CH.getOr(Caster_String.INSTANCE, link, "as", null);
		if (amiScript != null)
			this.amiScript.getCallback(CALLBACK_ONPROCESS).setAmiscript(amiScript, false);
		else {
			this.amiScript.init(null, this.alias, (Map<String, Object>) link.get("callbacks"), warningsSink);
		}

		options = CH.getOrThrow(Caster_Short.PRIMITIVE, link, "op");
		relationshipId = CH.getOr(Caster_String.INSTANCE, link, "relid", null);
		this.aliasDotName = AmiWebUtils.getFullAlias(this.alias, this.relationshipId);
		updateAri();
	}
	@Override
	public void onInitDone() {
		this.amiScript.initCallbacksLinkedVariables();
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("spadn", AmiWebUtils.getRelativeAlias(this.alias, getSourcePanelAliasDotId()));
		CH.putNoNull(m, "sdmadn", AmiWebUtils.getRelativeAlias(this.alias, getSourceDmAliasDotName()));
		CH.putNoNull(m, "sdmtb", getSourceDmTableName());
		m.put("callbacks", getAmiScript().getConfiguration());
		m.put("tpadn", AmiWebUtils.getRelativeAlias(this.alias, getTargetPanelAliasDotId()));
		if (SH.is(getTargetDmAliasDotName()))
			m.put("tdmadn", AmiWebUtils.getRelativeAlias(this.alias, getTargetDmAliasDotName()));
		List<Object> awcs = new ArrayList<Object>(this.whereStatements.size());
		for (AmiWebDmLinkWhereClause wc : this.whereStatements.values())
			awcs.add(wc.getConfiguration());
		m.put("awcs", awcs);
		m.put("title", getTitle());
		m.put("relid", getRelationshipId());
		m.put("op", getOptions());
		return m;
	}

	@Override
	public String getSourcePanelAliasDotId() {
		return this.sourcePanelAliasDotName;
	}

	@Override
	public String getTargetPanelAliasDotId() {
		return this.targetPanelAliasDotName;
	}

	@Override
	public String getRelationshipId() {
		return relationshipId;
	}

	@Override
	public void setRelationshipId(String relationshipId) {
		this.relationshipId = relationshipId;
		this.aliasDotName = AmiWebUtils.getFullAlias(this.alias, this.relationshipId);
		updateAri();
	}

	public void setOptions(short options) {
		this.options = options;
	}

	@Override
	public Set<String> getWhereClauseVarNames() {
		return this.whereStatements.keySet();
	}

	@Deprecated
	@Override
	public String getWhereClause(String varname) {
		return this.getWhereClauseO(varname).getWhereClause().getFormula(true);
	}

	@Override
	public AmiWebDmLinkWhereClause getWhereClauseO(String varname) {
		return CH.getOrThrow(this.whereStatements, varname);
	}

	@Override
	public AmiWebDmLinkWhereClause removeWhereClauseO(String varname) {
		AmiWebDmLinkWhereClause r = CH.removeOrThrow(this.whereStatements, varname);
		this.formulas.removeFormula(r.getWhereClause().getFormulaId());
		return r;
	}

	@Override
	public void setCurrentRequest(AmiWebDmRequest req) {
		this.currentRequest = req;
	}

	@Override
	public AmiWebDmRequest getCurrentRequest() {
		return this.currentRequest;
	}

	@Override
	public void setLinkUid(String linkId) {
		this.linkId = linkId;
	}
	@Override
	public Table getSourceTableData() {
		return dmManager.getDmByAliasDotName(getSourceDmAliasDotName()).getResponseTableset().getTable(getSourceDmTableName());
	}

	@Override
	public boolean ensureValid(StringBuilder errorSink) {
		AmiWebPortlet sp = this.getSourcePanelNoThrow();
		if (sp == null) {
			errorSink.append("Source panel not found: ").append(this.sourcePanelAliasDotName);
			return false;
		}
		AmiWebPortlet tp = this.getTargetPanelNoThrow();
		if (tp == null) {
			errorSink.append("Target panel not found: ").append(this.targetPanelAliasDotName);
			return false;
		}
		if (sp instanceof AmiWebDmPortlet && this.getSourceDm() == null && !(sp instanceof AmiWebLinkableVarsPortlet)) {
			errorSink.append("Source panel's datamodel has changed,  relationship's source datamodel no longer exists in source panel.");
			return false;
		}
		if (tp instanceof AmiWebDmPortlet && this.getTargetDm() == null) {
			errorSink.append("Target panel's datamodel has changed,  relationship's target datamodel no longer exists in source panel.");
			return false;
		}
		return true;
	}

	@Override
	public boolean isRunOnSelect() {
		return MH.anyBits(this.options, OPTION_ON_SELECT | OPTION_ON_SELECT_FORCE);
	}

	@Override
	public boolean isRunOnDoubleClick() {
		return MH.anyBits(this.options, OPTION_ON_USER_DBL_CLICK);
	}

	@Override
	public boolean isRunOnRightClickMenu() {
		return MH.anyBits(this.options, OPTION_EMPTYSEL_IGNORE | OPTION_ON_RIGHT_CLICK_MENU);
	}

	@Override
	public boolean isRunOnAmiScript() {
		return MH.anyBits(this.options, OPTION_ON_AMISCRIPT);
	}

	@Override
	public String getAmiLayoutFullAlias() {
		return this.alias;
	}

	@Override
	public void setAmiLayoutFullAlias(String alias) {
		this.alias = alias;
		this.aliasDotName = AmiWebUtils.getFullAlias(this.alias, this.relationshipId);
		this.amiScript.setAmiLayoutAlias(alias);
		updateAri();
	}

	@Override
	public AmiWebPortlet getSourcePanel() {
		return OH.assertNotNull(this.getSourcePanelNoThrow());
	}

	@Override
	public AmiWebPortlet getTargetPanel() {
		return OH.assertNotNull(this.getTargetPanelNoThrow());
	}

	@Override
	public AmiWebPortlet getSourcePanelNoThrow() {
		return this.sourcePanel != null ? this.sourcePanel : (AmiWebPortlet) service.getPortletByAliasDotPanelId(this.sourcePanelAliasDotName);
	}

	@Override
	public AmiWebPortlet getTargetPanelNoThrow() {
		return this.targetPanel != null ? this.targetPanel : (AmiWebPortlet) service.getPortletByAliasDotPanelId(this.targetPanelAliasDotName);
	}

	@Override
	public void onAmiPanelAdnChanged(AmiWebPortlet portlet, String oldAdn, String newAdn) {
		if (OH.eq(oldAdn, this.sourcePanelAliasDotName))
			sourcePanelAliasDotName = newAdn;
		if (OH.eq(oldAdn, this.targetPanelAliasDotName))
			targetPanelAliasDotName = newAdn;
	}

	@Override
	public void setSourceDm(String sourceDmAliasDotName, String sourceDmTableName) {
		if (OH.eq(this.sourceDmAliasDotName, sourceDmAliasDotName) && OH.eq(this.sourceDmTableName, sourceDmTableName))
			return;
		this.sourceDmAliasDotName = sourceDmAliasDotName;
		this.sourceDmTableName = sourceDmTableName;
		if (this.sourceDm != null) {
			sourceDm.removeDmLinkFromThisDm(this);
			this.sourceDm = null;
		}
	}
	public void setTargetDm(String targetDmAliasDotName) {
		if (OH.eq(this.targetDmAliasDotName, targetDmAliasDotName))
			return;
		this.targetDmAliasDotName = targetDmAliasDotName;
		if (this.targetDm != null) {
			targetDm.removeDmLinkToThisDm(this);
			this.targetDm = null;
		}
	}

	@Override
	public String getSourceDmAliasDotName() {
		return this.sourceDmAliasDotName;
	}

	@Override
	public String getTargetDmAliasDotName() {
		return this.targetDmAliasDotName;
	}

	@Override
	public String getAmiLayoutFullAliasDotId() {
		return this.aliasDotName;
	}

	private boolean wasLastSelectedEmpty = true;
	private String ari;
	private boolean isTransient;

	public boolean isSelectionStillEmpty(Table values) {
		boolean isSelectedEmpty = values == null || values.getRows().size() == 0;
		boolean isSelectionStillEmpty = isSelectedEmpty && wasLastSelectedEmpty;
		this.wasLastSelectedEmpty = isSelectedEmpty;
		return isSelectionStillEmpty;
	}

	@Override
	public String getAri() {
		return this.ari;
	}
	@Override
	public void updateAri() {
		String oldAri = ari;
		this.ari = AmiWebDomObject.ARI_TYPE_RELATIONSHIP + ":" + this.getAmiLayoutFullAliasDotId();
		service.getDomObjectsManager().fireAriChanged(this, oldAri);
	}

	@Override
	public String getAriType() {
		return AmiWebDomObject.ARI_TYPE_RELATIONSHIP;
	}

	@Override
	public String getDomLabel() {
		return this.getRelationshipId();
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public AmiWebDomObject getParentDomObject() {
		return this.service.getLayoutFilesManager().getLayoutByFullAlias(this.getAmiLayoutFullAlias());
	}

	@Override
	public Class<?> getDomClassType() {
		return AmiWebDmLink.class;
	}

	@Override
	public Object getDomValue() {
		return this;
	}

	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return this.amiScript;
	}

	@Override
	public String toDerivedString() {
		return getAri();
	}
	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(getAri());
	}

	@Override
	public boolean isTransient() {
		return this.isTransient;
	}
	@Override
	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	private boolean isManagedByDomManager = false;
	final private AmiWebFormulasImpl formulas;

	@Override
	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			this.isManagedByDomManager = true;
		}
	}
	@Override
	public void removeFromDomManager() {
		this.service.getDomObjectsManager().fireRemoved(this);
		if (this.isManagedByDomManager == true) {
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
	}

	@Override
	public AmiWebService getService() {
		return this.service;
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		com.f1.base.CalcTypes r1 = AmiWebDesktopLinkHelper.getSourceVars(this, this.getSourcePanelNoThrow());
		com.f1.base.CalcTypes r2 = AmiWebDesktopLinkHelper.getTargetVars(this, this.getTargetPanelNoThrow());
		return new SourceTargetHelper.SourceTargetTypesMapping(AmiWebDmUtils.VARPREFIX_SOURCE, r1, AmiWebDmUtils.VARPREFIX_TARGET, r2);
	}

	@Override
	public AmiWebFormulasImpl getFormulas() {
		return this.formulas;
	}
	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}
