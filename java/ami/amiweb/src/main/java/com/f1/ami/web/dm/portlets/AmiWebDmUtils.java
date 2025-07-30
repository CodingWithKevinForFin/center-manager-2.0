package com.f1.ami.web.dm.portlets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiDatasourcePlugin;
import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebDatasourceWrapper;
import com.f1.ami.web.AmiWebLinkableVarsPortlet;
import com.f1.ami.web.AmiWebObject;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmLinkWhereClause;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmRequest;
import com.f1.ami.web.dm.AmiWebDmTablesetSchema;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.filter.SourceTargetHelper.SourceTargetTypesMapping;
import com.f1.ami.web.filter.SourceTargetHelper.SourceTargetValuesMap;
import com.f1.base.CalcFrame;
import com.f1.base.IterableAndSize;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.sql.Tableset;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebDmUtils {

	public static final Logger log = LH.get();

	public static final String VARPREFIX_TARGET = "Target_";
	public static final String VARPREFIX_SOURCE = "Source_";
	private static final ObjectToJsonConverter JSON_CONVERTER = new ObjectToJsonConverter();
	static {
		JSON_CONVERTER.setCompactMode(true);
	}

	public static com.f1.base.CalcTypes getTarget(AmiWebService service, AmiWebDmLink dmLink) {
		com.f1.base.CalcTypes target;
		if (dmLink.getTargetDmAliasDotName() != null) {
			return null;//getTargetTypes(service.getDmManager().getDmByAliasDotName(dmLink.getTargetDmAliasDotName()).getRequestInSchema());
		} else {
			AmiWebLinkableVarsPortlet rtp = (AmiWebLinkableVarsPortlet) service.getPortletByAliasDotPanelId(dmLink.getTargetPanelAliasDotId());
			target = rtp.getLinkableVars();
		}
		return target;
	}
	public static com.f1.base.CalcTypes getTargetTypes(AmiWebDmTablesetSchema dm) {
		com.f1.utils.structs.table.stack.BasicCalcTypes r = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		for (String i : dm.getTableNamesSorted()) {
			r.putAll(dm.getTable(i).getClassTypes());
		}
		return r;
	}

	public static void sendRequestDeleteRelationship(final AmiWebService service, AmiWebDmLink i, CalcFrameStack sf) {
		if (service.getLayoutFilesManager().getIsLayoutClosing() || service.getPortletManager().getIsClosed())
			return;
		AmiWebDm targetDm = i.getTargetDmAliasDotName() == null ? null : service.getDmManager().getDmByAliasDotName(i.getTargetDmAliasDotName());
		if (targetDm == null)
			return;

		AmiWebPortlet panel = i.getSourcePanelNoThrow();
		if (panel != null) {
			Table values = getSourceValues(service, i);
			if (values == null && MH.allBits(i.getOptions(), AmiWebDmLink.OPTION_EMPTYSEL_IGNORE))
				return;
		}
		try {
			AmiWebDmRequest req = buildReq(service.getDebugManager(), service, i, null, new StringBuilder(), sf);
			service.putLastRealtimeRequest(targetDm.getDmUid(), null);
			if (req != null) {
				i.setCurrentRequest(req);
				targetDm.processRequest(req, service.getDebugManager());
			}
		} catch (Exception e) {
			LH.w(log, "For ", service.getUserName(), ": Error with link: " + i, e);
			String msg = "Error with removing relationship: " + i.getSourcePanelAliasDotId() + " --> " + i.getTargetPanelAliasDotId();
			if (e instanceof ExpressionParserException)
				msg += "<BR>" + e.getMessage();
			service.getPortletManager().showAlert(msg, e);
		}

	}
	// Returns true if the request can be run
	public static boolean checkShouldRunRequest(AmiWebDmLink i) {
		// If target panel is null stop the request
		if (i.getTargetPanelNoThrow() == null)
			return false;
		if (i.getSourcePanelNoThrow() == null)
			return false;
		// If bring to front is not set && both source and target panels are hidden
		if (!(MH.anyBits(i.getOptions(), AmiWebDmLink.OPTION_BRING_TO_FRONT)) && !i.getTargetPanelNoThrow().getVisible() && !i.getSourcePanelNoThrow().getVisible())
			return false;
		return true;
	}
	//null values indicates show everything (open query), empty values means show nothing
	public static void sendRequest(final AmiWebService service, AmiWebDmLink i) {
		if (!checkShouldRunRequest(i))
			return;
		AmiWebDm targetDm = i.getTargetDmAliasDotName() == null ? null : service.getDmManager().getDmByAliasDotName(i.getTargetDmAliasDotName());
		Table values = getSourceValues(service, i);
		sendRequest(service, i, values, targetDm);
	}
	public static void sendRequest(final AmiWebService service, AmiWebDmLink i, Table values, AmiWebDm targetDm) {
		if (i.getTargetPanelNoThrow() == null)
			return;
		if (!MH.anyBits(i.getOptions(), AmiWebDmLink.OPTION_EMPTYSEL_ALLSEL) && i.isSelectionStillEmpty(values))
			return;
		if (values == null && MH.allBits(i.getOptions(), AmiWebDmLink.OPTION_EMPTYSEL_IGNORE))
			return;
		boolean isTesting = false;
		if (service.getDesktop().getInEditMode()) {
			AmiWebDmAddLinkPortlet ld = service.getDesktop().getLinkHelper().getCurrentLinkDialog();
			if (ld != null) {
				if (OH.eq(i.getLinkUid(), ld.getCurrentLink().getLinkUid())) {
					isTesting = true;
					StringBuilder sink = new StringBuilder();
					i = ld.createLink(sink, true);
					if (sink.length() > 0) {
						service.getPortletManager().showAlert(sink.toString());
						return;
					}
				}
			}
		}

		Object result = i.getAmiScript().execute(AmiWebDmLink.CALLBACK_ONPROCESS, values);
		if (Boolean.FALSE.equals(result))
			return;
		StringBuilder errorSink = new StringBuilder();
		try {

			if (i.getTargetDmAliasDotName() == null) {
				AmiWebLinkableVarsPortlet target = (AmiWebLinkableVarsPortlet) i.getTargetPanelNoThrow();
				if (target != null)
					target.processFilter(i, values);
			} else {
				AmiWebDmRequest req = buildReq(service.getDebugManager(), service, i, values, errorSink, service.createStackFrame(i));
				if (req == null && (isTesting || !service.getDesktop().getInEditMode())) {
					service.getPortletManager().showAlert(errorSink.toString());
					return;
				}
				if (req == null)
					throw new RuntimeException(errorSink.toString());
				if (MH.anyBits(i.getOptions(), AmiWebDmLink.OPTION_ON_SELECT)) {
					Map<String, Object> last = service.getLastRealtimeRequest(targetDm.getDmUid());
					if (CH.areSame(req.getVariables(), last))
						return;
					else
						service.putLastRealtimeRequest(targetDm.getDmUid(), req.getVariables());
				} else
					service.putLastRealtimeRequest(targetDm.getDmUid(), null);
				i.setCurrentRequest(req);
				((AmiWebDmsImpl) targetDm).processRequest(req, service.getDebugManager(), false, true);
			}
			if (MH.allBits(i.getOptions(), AmiWebDmLink.OPTION_BRING_TO_FRONT)) {
				AmiWebPortlet targetPanelNoThrow = i.getTargetPanelNoThrow();
				if (targetPanelNoThrow.getAmiParent() != null)
					PortletHelper.ensureVisible(targetPanelNoThrow);
			}
		} catch (Exception e) {
			if (service.getDesktop().getLinkHelper().getCurrentLinkDialog() != null)
				return;
			LH.w(log, "For ", service.getUserName(), ": Error with link: " + i, e);
			String msg = "Error with relationship: " + i.getSourcePanelAliasDotId() + " --> " + i.getTargetPanelAliasDotId();
			if (e instanceof ExpressionParserException)
				msg += "<BR>" + e.getMessage();
			if (service.getDesktop().getInEditMode()) {
				ConfirmDialogListener listener = new ConfirmDialogListener() {

					@Override
					public boolean onButton(ConfirmDialog source, String id) {
						if (id.equals(ConfirmDialogPortlet.ID_YES)) {
							AmiWebDmLink link = (AmiWebDmLink) source.getCorrelationData();
							service.getDesktop().getLinkHelper().showEditRelationship(link);
						}
						return true;
					}
				};
				ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(service.getPortletManager().generateConfig(), msg + "<BR>&nbsp<br><B>Would you like to edit it now?",
						ConfirmDialogPortlet.TYPE_YES_NO, listener).setCorrelationData(i);
				cdp.setDetails(SH.printStackTrace(e));
				service.getDesktop().getManager().showDialog("Error with Relationship", cdp);
			} else {
				service.getPortletManager().showAlert(msg, e);
			}
		}
		if (isTesting)
			i.close();

	}

	public static class ParsedWhere {
		public final DerivedCellCalculator calc;
		public final SourceTargetTypesMapping classTypes;

		public ParsedWhere(AmiWebService service, com.f1.base.CalcTypes target, AmiWebDmLink dmLink, String varname, AmiDebugManager debugManager, StringBuilder errorSink) {
			String where = dmLink.getWhereClause(varname);
			com.f1.base.CalcTypes source;
			if (dmLink.getSourceDmAliasDotName() == null) {
				AmiWebLinkableVarsPortlet rtp = (AmiWebLinkableVarsPortlet) dmLink.getSourcePanel();
				source = rtp.getLinkableVars();
			} else {
				source = dmLink.getSourceTable().getClassTypes();
			}
			this.classTypes = new SourceTargetTypesMapping(AmiWebDmUtils.VARPREFIX_SOURCE, source, AmiWebDmUtils.VARPREFIX_TARGET, target);
			AmiWebPortlet sourcePortlet = dmLink.getSourcePanel();
			if (dmLink.getTargetDm() == null)//is  realtime
				this.calc = sourcePortlet.getScriptManager().parseAmiScript(where, classTypes, errorSink, debugManager, AmiDebugMessage.TYPE_FORMULA, dmLink, varname, false, null);
			else
				this.calc = sourcePortlet.getScriptManager().parseAmiScriptTemplate(where, classTypes, errorSink, debugManager, AmiDebugMessage.TYPE_FORMULA, dmLink, varname,
						null);
		}

	}

	public static AmiWebDmRequest buildReq(AmiDebugManager debugManager, AmiWebService service, AmiWebDmLink dmLink, Table values, StringBuilder errorSink, CalcFrameStack sf) {
		ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf);
		if (!dmLink.ensureValid(errorSink))
			return null;
		AmiWebDmRequest r = new AmiWebDmRequest();
		StringBuilder sb = new StringBuilder();
		if (values != null) {
			com.f1.base.CalcTypes target = getTarget(service, dmLink);
			for (String varname : dmLink.getWhereClauseVarNames()) {

				AmiWebDmLinkWhereClause wc = dmLink.getWhereClauseO(varname);
				boolean first = true;
				if (values.getSize() == 0) { // when nothing selected: clear
					if (SH.is(wc.getFalseOverride()))
						sb.append(wc.getFalseOverride());
					else
						sb.append(wc.getPrefix()).append(wc.getFalseConst()).append(wc.getSuffix());
				} else { // when nothing selected: same as selecting everything
					sb.append(wc.getPrefix());
					Set<Object> exists = new HashSet<Object>();
					com.f1.base.CalcTypes source;
					if (dmLink.getSourceDmAliasDotName() == null) {
						AmiWebLinkableVarsPortlet rtp = (AmiWebLinkableVarsPortlet) dmLink.getSourcePanel();
						source = rtp.getLinkableVars();
					} else {
						source = dmLink.getSourceTable().getClassTypes();
					}
					int start = errorSink.length();
					ParsedWhere pw = new ParsedWhere(service, target, dmLink, varname, debugManager, errorSink);
					SourceTargetTypesMapping classTypes = pw.classTypes;
					DerivedCellCalculator calc = pw.calc;
					if (calc == null) {
						errorSink.insert(start, "Relationship formula for '" + varname + "' is invalid: ");
						return null;
					}
					CalcFrame vars = service.getScriptManager(dmLink.getAmiLayoutFullAlias()).getConstsMap(dmLink);
					SourceTargetValuesMap t = new SourceTargetValuesMap(classTypes, vars);
					for (Row row : values.getRows()) {
						t.resetUnderlyingSourceValues(row);
						final Object object;
						try {
							object = calc.get(rsf.reset(t));
						} catch (FlowControlThrow e) {
							errorSink.append("relationship expression error: " + e);
							return null;
						}
						if (object != null && exists.add(object)) {
							if (first)
								first = false;
							else
								sb.append(wc.getJoin());
							sb.append(object);
						}
					}
					sb.append(wc.getSuffix());
				}
				r.putVariable(varname, SH.toStringAndClear(sb));
			}
		} else { // when nothing selected: do thing, show everything
			for (String varname : dmLink.getWhereClauseVarNames()) {
				AmiWebDmLinkWhereClause wc = dmLink.getWhereClauseO(varname);
				if (SH.is(wc.getTrueOverride()))
					sb.append(wc.getTrueOverride());
				else
					sb.append(wc.getPrefix()).append(wc.getTrueConst()).append(wc.getSuffix());
				r.putVariable(varname, SH.toStringAndClear(sb));
			}
		}
		return r;
	}
	//null indicates it could be IGNORE or SHOWALL, just check i.getOptions()
	public static Table getSourceValues(AmiWebService service, AmiWebDmLink i) {
		AmiWebPortlet panel = i.getSourcePanel();
		if (!panel.hasSelectedRows(i)) {
			switch (i.getOptions() & AmiWebDmLink.OPTIONS_FOR_EMPTYSEL) {
				case AmiWebDmLink.OPTION_EMPTYSEL_ALLSEL:
					return panel.getSelectableRows(i, AmiWebPortlet.ALL);
				case AmiWebDmLink.OPTION_EMPTYSEL_CLEAR:
					return panel.getSelectableRows(i, AmiWebPortlet.NONE);
				case AmiWebDmLink.OPTION_EMPTYSEL_IGNORE:
					return null;
				case AmiWebDmLink.OPTION_EMPTYSEL_SHOWALL:
					return null;
				default:
					throw new RuntimeException("Unknown: " + i.getOptions());
			}
		} else
			return panel.getSelectableRows(i, AmiWebPortlet.SELECTED);
	}
	public static void getAllUpperDm(AmiWebDmManager dmm, String dmId, Set<String> sink) {
		if (!sink.add(dmId))
			return;
		AmiWebDm datamodel = dmm.getDmByAliasDotName(dmId);
		for (String s : datamodel.getUpperDmAliasDotNames())
			getAllUpperDm(dmm, s, sink);
	}

	public static void getUnderlyingDatasources(PortletManager manager, List<String> usedDatasources, List<AmiWebDatasourceWrapper> sink) {
		if (sink == null || usedDatasources == null || usedDatasources.isEmpty())
			return;
		for (String ds : usedDatasources) {
			AmiWebDatasourceWrapper t = AmiWebUtils.getService(manager).getSystemObjectsManager().getDatasource(ds);
			if (t != null)
				sink.add(t);
		}
	}
	public static void getUnderlyingDatasources(PortletManager manager, AmiWebDm dm, List<AmiWebDatasourceWrapper> sink) {
		if (sink == null || dm == null)
			return;
		AmiWebDmManager dmManager = dm.getDmManager();
		for (String ds : dm.getUsedDatasources()) {
			AmiWebDatasourceWrapper t = AmiWebUtils.getService(manager).getSystemObjectsManager().getDatasource(ds);
			if (t != null)
				sink.add(t);
		}
		for (String id : ((AmiWebDm) dm).getLowerDmAliasDotNames())
			getUnderlyingDatasources(manager, dmManager.getDmByAliasDotName(id), sink);
	}
	public static void getUnderlyingDatasourceAdapters(PortletManager manager, AmiWebDm dm, Set<String> adaptersSink) {
		if (adaptersSink == null)
			return;
		ArrayList<AmiWebDatasourceWrapper> sink = new ArrayList<AmiWebDatasourceWrapper>();
		getUnderlyingDatasources(manager, dm, sink);
		for (AmiWebDatasourceWrapper ds : sink)
			adaptersSink.add(ds.getAdapter());
	}

	public static void createDatasourceOperatorsMenus(BasicWebMenu r, PortletManager manager, AmiWebDatasourceWrapper ds) {
		AmiWebObject obj = getDatasourceTypeObject(manager, ds == null ? null : ds.getAdapter());
		String objectId;
		WebMenu operatorsMenu;
		List<String> operators;
		if (obj == null)
			return;
		objectId = obj.getObjectId();
		operatorsMenu = new BasicWebMenu(objectId + " Operators", true);
		operators = new ArrayList<String>();
		getDatasourceOperators(obj, operators);
		for (String op : operators)
			operatorsMenu.add(new BasicWebMenuLink(op, true, "dsOp_" + op).setAutoclose(false));
		r.add(operatorsMenu);
	}
	public static void createDatasourceOperatorsMenus(BasicWebMenu r, PortletManager manager, AmiWebDm dm) {
		Set<String> adapters = new HashSet<String>();
		getUnderlyingDatasourceAdapters(manager, dm, adapters);
		Set<AmiWebObject> dsTypeObjects = new HashSet<AmiWebObject>();
		//		if (dm instanceof AmiWebDmt) {
		//			dsTypeObjects.add(getDatasourceTypeObject(manager, "__AMI"));
		//		} else {
		getUnderlyingDatasourceTypeObjects(manager, dm, dsTypeObjects);
		//		}
		boolean hasMultipleDs = dsTypeObjects.size() > 1;
		WebMenu dsMenu = new BasicWebMenu("Datasource Operators", true);
		String objectId;
		WebMenu operatorsMenu;
		List<String> operators;
		if (hasMultipleDs)
			r.add(dsMenu);

		for (AmiWebObject obj : dsTypeObjects) {
			if (obj == null)
				continue;
			objectId = obj.getObjectId();
			operatorsMenu = new BasicWebMenu(hasMultipleDs ? objectId : objectId + " Operators", true);
			operators = new ArrayList<String>();
			getDatasourceOperators(obj, operators);
			for (String op : operators)
				operatorsMenu.add(new BasicWebMenuLink(op, true, "dsOp_" + op).setAutoclose(false));
			if (hasMultipleDs)
				dsMenu.add(operatorsMenu);
			else
				r.add(operatorsMenu);
		}
	}

	public static AmiWebObject getDatasourceTypeObject(PortletManager manager, String adapter) {
		//		if (ds == null)
		//			return null;
		//		String adapter = ds.getAdapter();
		if (adapter == null)
			return null;
		IterableAndSize<AmiWebObject> map = AmiWebUtils.getService(manager).getPrimaryWebManager().getAmiObjectsByType(AmiConsts.TYPE_DATASOURCE_TYPE).getAmiObjects();
		for (AmiWebObject node : map)
			if (adapter.equals(node.getObjectId()))
				return node;
		return null;
	}

	public static void getUnderlyingDatasourceTypeObjects(PortletManager manager, AmiWebDm dm, Set<AmiWebObject> sink) {
		if (sink == null)
			return;
		List<AmiWebDatasourceWrapper> dsList = new ArrayList<AmiWebDatasourceWrapper>();
		getUnderlyingDatasources(manager, dm, dsList);
		for (AmiWebDatasourceWrapper ds : dsList)
			if (ds != null)
				sink.add(getDatasourceTypeObject(manager, ds.getAdapter()));
	}

	public static void getDatasourceOperators(AmiWebObject dsTypeObject, List<String> sink) {
		if (sink == null)
			return;
		String symbolsJson = (String) dsTypeObject.getParam("Properties");
		Map<String, Object> symbolsMap = (Map<String, Object>) JSON_CONVERTER.stringToObject(symbolsJson);
		if (symbolsMap == null)
			return;
		Map<String, String> operatorsMap = (Map<String, String>) symbolsMap.get(AmiDatasourcePlugin.OPERATORS);
		if (operatorsMap == null)
			return;
		String operator;
		for (String operatorKey : AmiDatasourcePlugin.OPERATOR_KEY_LIST) {
			operator = operatorsMap.get(operatorKey);
			if (operator != null)
				sink.add(operator);
		}
	}

	public static Map<String, Object> getUnderlyingDatasourceWhereClauseSyntax(AmiWebObject dsTypeObject) {
		if (dsTypeObject == null)
			return null;
		String symbolsJson = (String) dsTypeObject.getParam("Properties");
		Map<String, Object> symbolsMap = (Map<String, Object>) JSON_CONVERTER.stringToObject(symbolsJson);
		if (symbolsMap == null)
			return null;
		return (Map<String, Object>) symbolsMap.get(AmiDatasourcePlugin.WHERE_SYNTAX);
	}
	public static AmiWebDmLinkWhereClause getUnderlyingDatasourceWhereClauseObject(PortletManager manager, AmiWebDm targetDm, AmiWebDmLink link) {
		List<AmiWebDatasourceWrapper> sink = new ArrayList<AmiWebDatasourceWrapper>();
		AmiWebDmUtils.getUnderlyingDatasources(manager, targetDm, sink);
		AmiWebDatasourceWrapper firstDs = null;
		for (AmiWebDatasourceWrapper ds : sink)
			if (ds != null && ds.getAdapter() != null) {
				firstDs = ds;
				break;
			}
		return getDatasourceWhereClauseSyntax(manager, firstDs, link);
	}
	public static String getDefaultTrueConst(PortletManager manager, AmiWebDm targetDm) {
		List<AmiWebDatasourceWrapper> sink = new ArrayList<AmiWebDatasourceWrapper>();
		AmiWebDmUtils.getUnderlyingDatasources(manager, targetDm, sink);
		for (AmiWebDatasourceWrapper ds : sink)
			if (ds != null && ds.getAdapter() != null) {
				AmiWebObject dsTypeObject = AmiWebDmUtils.getDatasourceTypeObject(manager, ds.getAdapter());
				Map<String, Object> syntaxMap = AmiWebDmUtils.getUnderlyingDatasourceWhereClauseSyntax(dsTypeObject);
				if (syntaxMap != null)
					return (String) syntaxMap.get(AmiDatasourcePlugin.WHERE_SYNTAX_TRUE);
			}
		return AmiWebDmLinkWhereClause.DEFAULT_TRUE_CONST;
	}
	public static String getDefaultTrueConst(PortletManager manager, List<String> usedDatasources) {
		List<AmiWebDatasourceWrapper> sink = new ArrayList<AmiWebDatasourceWrapper>();
		AmiWebDmUtils.getUnderlyingDatasources(manager, usedDatasources, sink);
		for (AmiWebDatasourceWrapper ds : sink)
			if (ds != null && ds.getAdapter() != null) {
				AmiWebObject dsTypeObject = AmiWebDmUtils.getDatasourceTypeObject(manager, ds.getAdapter());
				Map<String, Object> syntaxMap = AmiWebDmUtils.getUnderlyingDatasourceWhereClauseSyntax(dsTypeObject);
				if (syntaxMap != null)
					return (String) syntaxMap.get(AmiDatasourcePlugin.WHERE_SYNTAX_TRUE);
			}
		return AmiWebDmLinkWhereClause.DEFAULT_TRUE_CONST;
	}
	public static AmiWebDmLinkWhereClause getDatasourceWhereClauseSyntax(PortletManager manager, AmiWebDatasourceWrapper ds, AmiWebDmLink link) {
		AmiWebObject dsTypeObject = AmiWebDmUtils.getDatasourceTypeObject(manager, ds == null ? null : ds.getAdapter());
		Set<String> existing = new HashSet<String>();
		for (String s : link.getFormulas().getFormulaIds())
			if (SH.startsWith(s, AmiWebDmLinkWhereClause.WHERE_CLAUSE))
				existing.add(SH.stripPrefix(s, AmiWebDmLinkWhereClause.WHERE_CLAUSE, true));
		String varname = SH.getNextId("WHERE", existing, 2);
		Map<String, Object> syntaxMap = AmiWebDmUtils.getUnderlyingDatasourceWhereClauseSyntax(dsTypeObject);
		if (syntaxMap == null) {
			return new AmiWebDmLinkWhereClause(link, varname, null);
		}
		String prefix = (String) syntaxMap.get(AmiDatasourcePlugin.WHERE_SYNTAX_PREFIX);
		String join = (String) syntaxMap.get(AmiDatasourcePlugin.WHERE_SYNTAX_JOIN);
		String suffix = (String) syntaxMap.get(AmiDatasourcePlugin.WHERE_SYNTAX_SUFFIX);
		String trueConst = (String) syntaxMap.get(AmiDatasourcePlugin.WHERE_SYNTAX_TRUE);
		String falseConst = (String) syntaxMap.get(AmiDatasourcePlugin.WHERE_SYNTAX_FALSE);
		return new AmiWebDmLinkWhereClause(link, varname, null, prefix, join, suffix, trueConst, falseConst, AmiWebDmLinkWhereClause.DEFAULT_TRUE_OVERRIDE,
				AmiWebDmLinkWhereClause.DEFAULT_FALSE_OVERRIDE);
	}
	public static void getUnderlyingDatasourceQuotes(PortletManager manager, AmiWebDm dm, List<String> quotesSink) {
		if (quotesSink == null)
			return;
		Set<AmiWebObject> dsObjSet = new HashSet<AmiWebObject>();
		getUnderlyingDatasourceTypeObjects(manager, dm, dsObjSet);
		String symbolsJson;
		Map<String, Object> symbolsMap;
		String quotes;
		for (AmiWebObject obj : dsObjSet) {
			if (obj == null)
				continue;
			symbolsJson = (String) obj.getParam("Properties");
			symbolsMap = (Map<String, Object>) JSON_CONVERTER.stringToObject(symbolsJson);
			if (symbolsMap == null)
				continue;
			quotes = (String) symbolsMap.get(AmiDatasourcePlugin.QUOTES);
			if (quotes != null)
				quotesSink.add(quotes);
		}
	}

	public static String getDatasourceQuoteType(PortletManager manager, AmiWebDatasourceWrapper ds) {
		AmiWebObject datasourceTypeObject = getDatasourceTypeObject(manager, ds == null ? null : ds.getAdapter());
		if (datasourceTypeObject == null)
			return null;
		String symbolsJson = (String) datasourceTypeObject.getParam("Properties");
		Map<String, Object> symbolsMap = (Map<String, Object>) JSON_CONVERTER.stringToObject(symbolsJson);
		if (symbolsMap == null)
			return null;
		return (String) symbolsMap.get(AmiDatasourcePlugin.QUOTES);
	}

	public static void getUnderlyingDatamodels(AmiWebDm dm, List<AmiWebDm> sink) {
		if (sink == null || dm == null)
			return;
		AmiWebDmManager dmManager = dm.getDmManager();
		sink.add(dm);
		for (String id : dm.getLowerDmAliasDotNames())
			getUnderlyingDatamodels(dmManager.getDmByAliasDotName(id), sink);
	}
	public static Map<String, Object> getDatasourceProperties(PortletManager manager, AmiWebObject dsTypeObject) {
		if (dsTypeObject == null)
			return null;
		String propertiesJson = (String) dsTypeObject.getParam("Properties");
		Map<String, Object> propertiesMap = (Map<String, Object>) JSON_CONVERTER.stringToObject(propertiesJson);
		return propertiesMap == null ? Collections.EMPTY_MAP : propertiesMap;
	}
	public static String describeSchema(Tableset ts) {
		StringBuilder sb = new StringBuilder();
		for (String name : ts.getTableNamesSorted()) {
			Table table = ts.getTable(name);
			TableHelper.toString(table, "", TableHelper.SHOW_ALL, sb, 4000);
			sb.append("Note: ").append(name).append(" has ").append(table.getSize()).append(" rows(s)").append(SH.NEWLINE);
		}
		return sb.toString();
	}
}
