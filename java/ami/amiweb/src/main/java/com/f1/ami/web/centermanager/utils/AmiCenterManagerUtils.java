package com.f1.ami.web.centermanager.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.web.AmiWebFormPortletAmiScriptField;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.centermanager.graph.nodes.AmiCenterGraphNode;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.style.PortletStyleManager_Dialog;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.BlockNode;
import com.f1.utils.string.node.ConstNode;
import com.f1.utils.string.node.MethodDeclarationNode;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.sqlnode.AdminNode;
import com.f1.utils.string.sqlnode.CreateTableNode;
import com.f1.utils.string.sqlnode.SqlColumnDefNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.SqlOperationNode;
import com.f1.utils.string.sqlnode.UseNode;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.Tuple2;

public class AmiCenterManagerUtils {
	public static HashMap<String, String> TABLE_CONFIG_MAP = new HashMap<String, String>();
	public static MapInMap<String, String, String> TRIGGER_CONFIG_MAP = new MapInMap();
	public static HashMap<String, String> TIMER_CONFIG_MAP = new HashMap<String, String>();
	public static HashMap<String, String> PROCEDURE_CONFIG_MAP = new HashMap<String, String>();
	public static HashMap<String, String> INDEX_CONFIG_MAP = new HashMap<String, String>();
	public static HashMap<String, String> METHOD_CONFIG_MAP = new HashMap<String, String>();
	public static MapInMap<String, String, Object> DBO_CONFIG_MAP = new MapInMap<String, String, Object>();
	static {
		//TABLES
		TABLE_CONFIG_MAP.put("name", null);
		TABLE_CONFIG_MAP.put("tableSchema", null);
		TABLE_CONFIG_MAP.put("PersistEngine", null);
		TABLE_CONFIG_MAP.put("custom", null);//indicate whether there is a custom persist engine
		TABLE_CONFIG_MAP.put("PersistOptions", null);
		TABLE_CONFIG_MAP.put("BroadCast", null);
		TABLE_CONFIG_MAP.put("RefreshPeriodMs", null);
		TABLE_CONFIG_MAP.put("OnUndefColumn", null);
		TABLE_CONFIG_MAP.put("InitialCapacity", null);

		//TRIGGERS
		TRIGGER_CONFIG_MAP.putMulti("COMMON_CONFIG", "triggerName", null);
		TRIGGER_CONFIG_MAP.putMulti("COMMON_CONFIG", "triggerType", null);
		TRIGGER_CONFIG_MAP.putMulti("COMMON_CONFIG", "triggerOn", null);
		TRIGGER_CONFIG_MAP.putMulti("COMMON_CONFIG", "triggerPriority", null);
		//AMISCRIPT config
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_AMISCRIPT, "canMutateRow", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_AMISCRIPT, "runOnStartup", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_AMISCRIPT, "onInsertingScript", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_AMISCRIPT, "onUpdatingScript", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_AMISCRIPT, "onDeletingScript", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_AMISCRIPT, "onInsertedScript", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_AMISCRIPT, "onUpdatedScript", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_AMISCRIPT, "rowVar", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_AMISCRIPT, "onStartupScript", null);
		//AGGREGATE config
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_AGGREGATE, "groupBys", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_AGGREGATE, "selects", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_AGGREGATE, "allowExternalUpdates", null);
		//PROJECTION config
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_PROJECTION, "selects", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_PROJECTION, "wheres", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_PROJECTION, "allowExternalUpdates", null);
		//JOIN config
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_JOIN, "type", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_JOIN, "on", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_JOIN, "selects", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_JOIN, "wheres", null);
		//DECORATE config
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_DECORATE, "on", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_DECORATE, "selects", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_DECORATE, "keysChange", null);
		//RELAY config
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_RELAY, "hosts", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_RELAY, "port", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_RELAY, "login", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_RELAY, "keystoreFile", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_RELAY, "keystorePass", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_RELAY, "derivedValues", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_RELAY, "inserts", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_RELAY, "updates", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_RELAY, "deletes", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_RELAY, "target", null);
		TRIGGER_CONFIG_MAP.putMulti(AmiCenterEntityConsts.TRIGGER_TYPE_RELAY, "where", null);

		//Start init timer config
		TIMER_CONFIG_MAP.put("timerName", null);
		TIMER_CONFIG_MAP.put("timerType", null);
		TIMER_CONFIG_MAP.put("timerPriority", null);
		//**timer use options
		TIMER_CONFIG_MAP.put("logging", null);
		TIMER_CONFIG_MAP.put("vars", null);
		TIMER_CONFIG_MAP.put("onStartupScript", null);
		TIMER_CONFIG_MAP.put("timeout", null);
		TIMER_CONFIG_MAP.put("limit", null);

		//Start init procedure config
		PROCEDURE_CONFIG_MAP.put("procedureName", null);
		PROCEDURE_CONFIG_MAP.put("procedureType", null);
		//*procedure use options
		PROCEDURE_CONFIG_MAP.put("arguments", null);
		PROCEDURE_CONFIG_MAP.put("script", null);
		PROCEDURE_CONFIG_MAP.put("logging", null);
		PROCEDURE_CONFIG_MAP.put("onStartupScript", null);

		//Start init index config
		INDEX_CONFIG_MAP.put("indexName", null);
		INDEX_CONFIG_MAP.put("indexOn", null);
		INDEX_CONFIG_MAP.put("constraint", null);

		//Start init method config
		METHOD_CONFIG_MAP.put("methodReturnType", null);
		METHOD_CONFIG_MAP.put("methodName", null);
		METHOD_CONFIG_MAP.put("methodArguments", null);
		METHOD_CONFIG_MAP.put("methodImplementation", null);

		//Start init dbo config
		DBO_CONFIG_MAP.putMulti("dboSchema", "dboName", null);
		DBO_CONFIG_MAP.putMulti("dboSchema", "dboType", null);
		DBO_CONFIG_MAP.putMulti("dboSchema", "dboCallbacks", new HashMap<String, String>());//the map will go like: {"onTimer=<Amiscript impl>,..."}
	}

	public static AdminNode scriptToAdminNode(String script) {
		SqlExpressionParser sep = new SqlExpressionParser();
		sep.setAllowSqlInjection(false);
		Node node = sep.parse(script);
		if (node instanceof AdminNode)
			return (AdminNode) node;
		return null;
	}

	public static CreateTableNode scriptToCreateTableNode(String script) {
		SqlExpressionParser sep = new SqlExpressionParser();
		sep.setAllowSqlInjection(false);
		Node node = sep.parse(script);
		if (node instanceof CreateTableNode)
			return (CreateTableNode) node;
		return null;
	}

	public static String formatPreviewScript(String rawText) {
		String formattedText = rawText.replace(AmiCenterEntityConsts.REQUIRED_FEILD_WARNING, AmiCenterEntityConsts.REQUIRED_FEILD_WARNING_HTML);
		for (String keyword : AmiCenterEntityConsts.SQL_KEYWORDS)
			formattedText = formattedText.replaceAll("\\b" + keyword + "\\b", AmiCenterEntityConsts.SQL_KEYWORD_HTML + keyword + "</span>");
		return formattedText;
	}

	public static void popDialog(AmiWebService service, String warning, String dialogueTitle) {
		PortletStyleManager_Dialog dp = service.getPortletManager().getStyleManager().getDialogStyle();
		final PortletManager portletManager = service.getPortletManager();
		ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(portletManager.generateConfig(), warning, ConfirmDialogPortlet.TYPE_MESSAGE);
		int w = dp.getDialogWidth();
		int h = dp.getDialogHeight();
		portletManager.showDialog(dialogueTitle, cdp, w + 200, h);
	}

	public static Map<String, String> parseAdminNode_Index(AdminNode nodes) {
		Map<String, String> fieldMap = new HashMap(INDEX_CONFIG_MAP);
		//		AdminNode[] tableDefs = nodes.getTableDefs();
		//		for (int n = 0; n < tableDefs.length; n++) {
		//			AdminNode node = tableDefs[n];
		//			MethodNode def = (MethodNode) node.getNext();
		//			String name = def.getMethodName();
		//			fieldMap.put("name", name);
		//			String schema = SH.afterFirst(def.toString(), name);
		//			fieldMap.put("tableSchema", schema);
		//			Map<String, Node> useOptions = node.getUseNode() != null ? node.getUseNode().getOptionsMap() : null;
		//			for (Entry<String, Node> e : useOptions.entrySet()) {
		//				String key = e.getKey();
		//				Node val = e.getValue();
		//				if (!(val instanceof ConstNode))
		//					throw new RuntimeException("Expecting value to be of type constNode");
		//				ConstNode constVal = (ConstNode) val;
		//				fieldMap.put(key, (String) constVal.getValue());
		//			}
		//		}
		return fieldMap;
	}

	public static Map<String, String> parseAdminNode_Table(CreateTableNode nodes) {
		Map<String, String> fieldMap = new HashMap(TABLE_CONFIG_MAP);
		AdminNode[] tableDefs = nodes.getTableDefs();
		for (int n = 0; n < tableDefs.length; n++) {
			AdminNode node = tableDefs[n];
			MethodNode def = (MethodNode) node.getNext();
			String name = def.getMethodName();
			fieldMap.put("name", name);
			String schema = SH.afterFirst(def.toString(), name);
			String[] schemaArray = SH.split(",", schema);
			//process schema such that any column e.g. "id string use BITMAP=true" -> "id string BITMAP
			AdminNode[] defs = nodes.getTableDefs();
			AdminNode tableDef = defs[0];
			MethodNode schemaNode = (MethodNode) tableDef.getNext();
			for (int i = 0; i < schemaNode.getParamsCount(); i++) {
				Node nn = schemaNode.getParamAt(i);
				SqlColumnDefNode scd = (SqlColumnDefNode) nn;
				UseNode un = scd.getUse();
				if (!un.getOptions().isEmpty()) {
					if (i == 0) //add opening parenthesis if the first
						schemaArray[i] = "(" + scd.getName().getValue() + " " + scd.getType().getValue() + " " + SH.join(" ", un.getOptions());
					else if (i == schemaNode.getParamsCount() - 1)
						schemaArray[i] = scd.getName().getValue() + " " + scd.getType().getValue() + " " + SH.join(" ", un.getOptions()) + ")";
					else
						schemaArray[i] = scd.getName().getValue() + " " + scd.getType().getValue() + " " + SH.join(" ", un.getOptions());

				}

			}
			schema = SH.join(",", schemaArray);
			fieldMap.put("tableSchema", schema);
			Map<String, Node> useOptions = node.getUseNode() != null ? node.getUseNode().getOptionsMap() : null;
			for (Entry<String, Node> e : useOptions.entrySet()) {
				String key = e.getKey();
				Node val = e.getValue();
				if (!(val instanceof ConstNode))
					throw new RuntimeException("Expecting value to be of type constNode");
				ConstNode constVal = (ConstNode) val;
				fieldMap.put(key, (String) constVal.getValue());
			}
		}
		return fieldMap;
	}

	public static Map<String, String> parseAdminNode_Dbo(AdminNode node) {
		Map<String, String> fieldMap = new HashMap(DBO_CONFIG_MAP);
		SqlOperationNode dboNode = JavaExpressionParser.castNode(node.getNext(), SqlOperationNode.class);
		SqlOperationNode typeNode = JavaExpressionParser.castNode(dboNode.getNext(), SqlOperationNode.class);
		SqlOperationNode priorityNode = typeNode.getNext() == null ? null : JavaExpressionParser.castNode(typeNode.getNext(), SqlOperationNode.class);
		int priority;
		try {
			priority = priorityNode == null ? 0 : ((Number) SH.parseConstant(priorityNode.getNameAsString())).intValue();
		} catch (Exception e) {
			throw new ExpressionParserException(priorityNode.getPosition(), "Not a valid number: " + priorityNode.getNameAsString(), e);
		}
		String dboName = dboNode.getNameAsString();
		String typeName = typeNode.getNameAsString();

		fieldMap.put("dboName", dboName);
		fieldMap.put("dboType", typeName);
		fieldMap.put("dboPriority", SH.toString(priority));
		//start parsing USE
		Map<String, Node> useOptions = node.getUseNode() != null ? node.getUseNode().getOptionsMap() : Collections.EMPTY_MAP;
		for (Entry<String, Node> e : useOptions.entrySet()) {
			String key = e.getKey();
			Node val = e.getValue();
			if (!(val instanceof ConstNode))
				throw new RuntimeException("Expecting value to be of type constNode");
			ConstNode constVal = (ConstNode) val;
			fieldMap.put(key, (String) constVal.getValue());

		}
		return fieldMap;
	}

	//TODO:method
	public static Map<String, String> parseAdminNode_Method(AdminNode node) {
		Map<String, String> fieldMap = new HashMap(METHOD_CONFIG_MAP);
		String name = null;
		String returnType = null;
		String args = null;
		String code = null;

		BlockNode bn = (BlockNode) node.getNext();
		int cnt = bn.getNodesCount();
		OH.assertTrue(cnt == 1, "Only 1 method node is allowed for edit");
		Node n = bn.getNodeAt(0);

		//Integer foofoo(Integer a,Integer b){return a + b; }
		if (n instanceof MethodDeclarationNode) {
			MethodDeclarationNode mdn = (MethodDeclarationNode) n;
			returnType = mdn.getReturnType();
			name = mdn.getMethodName();
			code = mdn.getBody().toString();
			//fetch args
			StringBuilder argBuilder = new StringBuilder();
			argBuilder.append('(');
			for (int i = 0; i < mdn.getParamsCount(); i++) {
				argBuilder.append(mdn.getParamAt(i));
				if (i != mdn.getParamsCount() - 1)
					argBuilder.append(',');
			}
			argBuilder.append(')');
			args = argBuilder.toString();
		} else
			throw new RuntimeException("unknown node type:" + n.getClass());

		fieldMap.put("methodName", name);
		fieldMap.put("methodArguments", args);
		fieldMap.put("methodReturnType", returnType);
		fieldMap.put("methodImplementation", code);

		return fieldMap;
	}

	public static Map<String, String> parseAdminNode_Procedure(AdminNode node) {
		Map<String, String> fieldMap = new HashMap(PROCEDURE_CONFIG_MAP);
		String name = null;
		String type = null;

		SqlOperationNode procedureNode = JavaExpressionParser.castNode(node.getNext(), SqlOperationNode.class);
		SqlOperationNode typeNode = JavaExpressionParser.castNode(procedureNode.getNext(), SqlOperationNode.class);
		name = procedureNode.getNameAsString();
		type = typeNode.getNameAsString();

		fieldMap.put("procedureName", name);
		fieldMap.put("procedureType", type);
		//start parsing USE
		Map<String, Node> useOptions = node.getUseNode() != null ? node.getUseNode().getOptionsMap() : Collections.EMPTY_MAP;
		for (Entry<String, Node> e : useOptions.entrySet()) {
			String key = e.getKey();
			Node val = e.getValue();
			if (!(val instanceof ConstNode))
				throw new RuntimeException("Expecting value to be of type constNode");
			ConstNode constVal = (ConstNode) val;
			fieldMap.put(key, (String) constVal.getValue());

		}
		return fieldMap;
	}

	public static String formatIndexNames(String indexOn, String indexName) {
		String formattedName = indexOn + "::" + indexName;
		return formattedName;
	}

	public static Tuple2<String, String> indexNameToTableAndIndex(String formattedName) {
		String tableName = SH.beforeFirst(formattedName, "::");
		String indexName = SH.afterFirst(formattedName, "::");
		return new Tuple2<String, String>(tableName, indexName);
	}

	public static Map<String, String> parseAdminNode_Timer(AdminNode node) {
		Map<String, String> fieldMap = new HashMap(TIMER_CONFIG_MAP);
		String name = null;
		String type = null;
		String on = null;
		int priority;
		SqlOperationNode timerNode = JavaExpressionParser.castNode(node.getNext(), SqlOperationNode.class);
		SqlOperationNode typeNode = JavaExpressionParser.castNode(timerNode.getNext(), SqlOperationNode.class);
		SqlOperationNode onNode = JavaExpressionParser.castNode(typeNode.getNext(), SqlOperationNode.class);
		SqlOperationNode priorityNode = onNode.getNext() == null ? null : JavaExpressionParser.castNode(onNode.getNext(), SqlOperationNode.class);
		try {
			priority = priorityNode == null ? 0 : ((Number) SH.parseConstant(priorityNode.getNameAsString())).intValue();
		} catch (Exception e) {
			throw new ExpressionParserException(priorityNode.getPosition(), "Not a valid number: " + priorityNode.getNameAsString(), e);
		}
		name = timerNode.getNameAsString();
		type = typeNode.getNameAsString();
		on = onNode.getNameAsString();
		fieldMap.put("timerName", name);
		fieldMap.put("timerType", type);
		fieldMap.put("timerOn", on);
		fieldMap.put("timerPriority", SH.toString(priority));
		//start parsing USE
		Map<String, Node> useOptions = node.getUseNode() != null ? node.getUseNode().getOptionsMap() : Collections.EMPTY_MAP;
		for (Entry<String, Node> e : useOptions.entrySet()) {
			String key = e.getKey();
			Node val = e.getValue();
			if (!(val instanceof ConstNode))
				throw new RuntimeException("Expecting value to be of type constNode");
			ConstNode constVal = (ConstNode) val;
			fieldMap.put(key, (String) constVal.getValue());

		}
		return fieldMap;
	}

	public static Map<String, String> parseAdminNode_Trigger(AdminNode n) {
		Map<String, String> fieldMap = new HashMap<String, String>();
		String name = null;
		String type = null;
		String on = null;
		int priority;
		SqlOperationNode triggerNode = JavaExpressionParser.castNode(n.getNext(), SqlOperationNode.class);
		SqlOperationNode typeNode = JavaExpressionParser.castNode(triggerNode.getNext(), SqlOperationNode.class);
		SqlColumnsNode tableNameNode = JavaExpressionParser.castNode(typeNode.getNext(), SqlColumnsNode.class);
		SqlOperationNode priorityNode = tableNameNode.getNext() == null ? null : JavaExpressionParser.castNode(tableNameNode.getNext(), SqlOperationNode.class);
		try {
			priority = priorityNode == null ? 0 : ((Number) SH.parseConstant(priorityNode.getNameAsString())).intValue();
		} catch (Exception e) {
			throw new ExpressionParserException(priorityNode.getPosition(), "Not a valid number: " + priorityNode.getNameAsString(), e);
		}
		name = triggerNode.getNameAsString();
		type = typeNode.getNameAsString();
		String tableNames[] = new String[tableNameNode.getColumnsCount()];
		int tableNamePos[] = new int[tableNameNode.getColumnsCount()];

		StringBuilder tableNameBuilder = new StringBuilder();
		for (int i = 0; i < tableNames.length; i++) {
			tableNames[i] = JavaExpressionParser.castNode(tableNameNode.getColumnAt(i), VariableNode.class).getVarname();
			tableNameBuilder.append(tableNames[i]);
			if (i != tableNames.length - 1)
				tableNameBuilder.append(",");
			tableNamePos[i] = tableNameNode.getColumnAt(i).getPosition();
		}
		on = tableNameBuilder.toString();
		System.out.println("name: " + name + " " + "type:" + type + " " + "on:" + on + " " + "priority:" + priority);
		UseNode useNode = n.getUseNode();
		Map<String, Node> options = n.getUseNode() != null ? n.getUseNode().getOptionsMap() : Collections.EMPTY_MAP;
		for (Entry<String, Node> e : options.entrySet()) {
			String key = e.getKey();
			Node val = e.getValue();
			if (!(val instanceof ConstNode))
				throw new RuntimeException("Expecting value to be of type constNode");
			ConstNode constVal = (ConstNode) val;
			fieldMap.put(key, (String) constVal.getValue());
		}
		//feed common config into the map
		fieldMap.put("triggerName", name);
		fieldMap.put("triggerType", type);
		fieldMap.put("triggerOn", on);
		fieldMap.put("triggerPriority", Caster_String.INSTANCE.cast(priority));

		return fieldMap;
	}

	public static MapInMap<String, String, String> parseAdminNode_Trigger2(AdminNode n) {
		MapInMap<String, String, String> fieldMap = new MapInMap(TRIGGER_CONFIG_MAP);
		String name = null;
		String type = null;
		String on = null;
		int priority;
		SqlOperationNode triggerNode = JavaExpressionParser.castNode(n.getNext(), SqlOperationNode.class);
		SqlOperationNode typeNode = JavaExpressionParser.castNode(triggerNode.getNext(), SqlOperationNode.class);
		SqlColumnsNode tableNameNode = JavaExpressionParser.castNode(typeNode.getNext(), SqlColumnsNode.class);
		SqlOperationNode priorityNode = tableNameNode.getNext() == null ? null : JavaExpressionParser.castNode(tableNameNode.getNext(), SqlOperationNode.class);
		try {
			priority = priorityNode == null ? 0 : ((Number) SH.parseConstant(priorityNode.getNameAsString())).intValue();
		} catch (Exception e) {
			throw new ExpressionParserException(priorityNode.getPosition(), "Not a valid number: " + priorityNode.getNameAsString(), e);
		}
		name = triggerNode.getNameAsString();
		type = typeNode.getNameAsString();
		String tableNames[] = new String[tableNameNode.getColumnsCount()];
		int tableNamePos[] = new int[tableNameNode.getColumnsCount()];

		StringBuilder tableNameBuilder = new StringBuilder();
		for (int i = 0; i < tableNames.length; i++) {
			tableNames[i] = JavaExpressionParser.castNode(tableNameNode.getColumnAt(i), VariableNode.class).getVarname();
			tableNameBuilder.append(tableNames[i]);
			if (i != tableNames.length - 1)
				tableNameBuilder.append(",");
			tableNamePos[i] = tableNameNode.getColumnAt(i).getPosition();
		}
		on = tableNameBuilder.toString();
		System.out.println("name: " + name + " " + "type:" + type + " " + "on:" + on + " " + "priority:" + priority);
		UseNode useNode = n.getUseNode();
		Map<String, Node> options = n.getUseNode() != null ? n.getUseNode().getOptionsMap() : Collections.EMPTY_MAP;
		for (Entry<String, Node> e : options.entrySet()) {
			String key = e.getKey();
			Node val = e.getValue();
			if (!(val instanceof ConstNode))
				throw new RuntimeException("Expecting value to be of type constNode");
			ConstNode constVal = (ConstNode) val;
			fieldMap.putMulti(type, key, (String) constVal.getValue());
		}
		//feed common config into the map
		fieldMap.putMulti("COMMON_CONFIG", "triggerName", name);
		fieldMap.putMulti("COMMON_CONFIG", "triggerType", type);
		fieldMap.putMulti("COMMON_CONFIG", "triggerOn", on);
		fieldMap.putMulti("COMMON_CONFIG", "triggerPriority", Caster_String.INSTANCE.cast(priority));

		return fieldMap;
	}

	public static MapInMap<String, String, String> parseRow_trigger(Row r) {
		MapInMap<String, String, String> fieldMap = new MapInMap(TRIGGER_CONFIG_MAP);
		String triggerName = (String) r.get("TriggerName");
		String tableOn = (String) r.get("TableName");
		String triggerType = (String) r.get("TriggerType");
		String triggerPriority = (String) r.get("TriggerPriority");
		String options = (String) r.get("Options");
		return fieldMap;
	}

	public static Map<String, String> parseUseOptions(String s) {
		Map<String, String> result = new HashMap<>();
		//Pattern pattern = Pattern.compile("(\\w+)=\"([^\"]*)\"");
		//Pattern pattern = Pattern.compile("(\\w+)=\"(.*?)\"");
		Pattern pattern = Pattern.compile("(\\w+)=\"((?:\\\\\"|[^\"])*?)\"");
		Matcher matcher = pattern.matcher(s);

		while (matcher.find()) {
			String key = matcher.group(1);
			String value = matcher.group(2);
			result.put(key, value);
		}
		return result;
	}

	public static Map<String, Object> showClauseToObjectConfig(Table t, Row r) {
		HashMap<String, Object> config = new HashMap<String, Object>();
		for (String key : t.getColumnsMap().keySet()) {
			String value = (String) r.get(key);
			if ("Options".equals(key))
				config.put(key, AmiCenterManagerUtils.parseUseOptions(value));
			else
				config.put(key, value);
		}
		return config;
	}

	public static short centerObjectTypeToCode(byte groupCode, String name) {
		switch (groupCode) {
			case AmiCenterGraphNode.TYPE_TRIGGER:
				if (AmiCenterEntityConsts.TRIGGER_TYPE_AMISCRIPT.equals(name))
					return AmiCenterEntityConsts.TRIGGER_TYPE_CODE_AMISCRIPT;
				if (AmiCenterEntityConsts.TRIGGER_TYPE_AGGREGATE.equals(name))
					return AmiCenterEntityConsts.TRIGGER_TYPE_CODE_AGGREGATE;
				if (AmiCenterEntityConsts.TRIGGER_TYPE_PROJECTION.equals(name))
					return AmiCenterEntityConsts.TRIGGER_TYPE_CODE_PROJECTION;
				if (AmiCenterEntityConsts.TRIGGER_TYPE_JOIN.equals(name))
					return AmiCenterEntityConsts.TRIGGER_TYPE_CODE_JOIN;
				if (AmiCenterEntityConsts.TRIGGER_TYPE_DECORATE.equals(name))
					return AmiCenterEntityConsts.TRIGGER_TYPE_CODE_DECORATE;
				if (AmiCenterEntityConsts.TRIGGER_TYPE_RELAY.equals(name))
					return AmiCenterEntityConsts.TRIGGER_TYPE_CODE_RELAY;
				break;
			case AmiCenterGraphNode.TYPE_TIMER:
				if (AmiCenterEntityConsts.TIMER_TYPE_AMISCRIPT.equals(name))
					return AmiCenterEntityConsts.TIMER_TYPE_CODE_AMISCRIPT;
				break;
			case AmiCenterGraphNode.TYPE_PROCEDURE:
				if (AmiCenterEntityConsts.PROCEDURE_TYPE_AMISCRIPT.equals(name))
					return AmiCenterEntityConsts.PROCEDURE_TYPE_CODE_AMISCRIPT;
				break;
			default:
				throw new RuntimeException();

		}
		return -1;

	}

	public static String toCenterObjectString(byte code, boolean isUppercase) {
		switch (code) {
			case AmiCenterGraphNode.TYPE_TABLE:
				return isUppercase ? SH.toUpperCase("table") : "Table";
			case AmiCenterGraphNode.TYPE_TRIGGER:
				return isUppercase ? SH.toUpperCase("trigger") : "Trigger";
			case AmiCenterGraphNode.TYPE_TIMER:
				return isUppercase ? SH.toUpperCase("timer") : "Timer";
			case AmiCenterGraphNode.TYPE_PROCEDURE:
				return isUppercase ? SH.toUpperCase("procedure") : "Procedure";
			case AmiCenterGraphNode.TYPE_METHOD:
				return isUppercase ? SH.toUpperCase("method") : "Method";
			case AmiCenterGraphNode.TYPE_INDEX:
				return isUppercase ? SH.toUpperCase("index") : "Index";
			case AmiCenterGraphNode.TYPE_DBO:
				return isUppercase ? SH.toUpperCase("dbo") : "Dbo";
			default:
				throw new IllegalArgumentException("unknown code: " + code);
		}
	}

	public static short toLoggingTypeCode(String loggingType) {
		switch (loggingType) {
			case AmiCenterEntityConsts.LOGGING_LEVEL_OFF:
				return AmiCenterEntityConsts.LOGGING_LEVEL_CODE_OFF;
			case AmiCenterEntityConsts.LOGGING_LEVEL_ON:
				return AmiCenterEntityConsts.LOGGING_LEVEL_CODE_ON;
			case AmiCenterEntityConsts.LOGGING_LEVEL_VERBOSE:
				return AmiCenterEntityConsts.LOGGING_LEVEL_CODE_VERBOSE;
			default:
				throw new RuntimeException("Unknow loggingType:" + loggingType);
		}
	}

	public static String toLoggingType(short type) {
		switch (type) {
			case AmiCenterEntityConsts.LOGGING_LEVEL_CODE_OFF:
				return AmiCenterEntityConsts.LOGGING_LEVEL_OFF;
			case AmiCenterEntityConsts.LOGGING_LEVEL_CODE_ON:
				return AmiCenterEntityConsts.LOGGING_LEVEL_ON;
			case AmiCenterEntityConsts.LOGGING_LEVEL_CODE_VERBOSE:
				return AmiCenterEntityConsts.LOGGING_LEVEL_VERBOSE;
			default:
				throw new RuntimeException("Unknow loggingType:" + type);
		}
	}

	public static short toIndexConstraintCode(String constraint) {
		switch (constraint) {
			case AmiCenterEntityConsts.INDEX_CONSTRAINT_TYPE_NONE:
				return AmiCenterEntityConsts.INDEX_CONSTRAINT_TYPE_CODE_NONE;
			case AmiCenterEntityConsts.INDEX_CONSTRAINT_TYPE_UNIQUE:
				return AmiCenterEntityConsts.INDEX_CONSTRAINT_TYPE_CODE_UNIQUE;
			case AmiCenterEntityConsts.INDEX_CONSTRAINT_TYPE_PRIMARY:
				return AmiCenterEntityConsts.INDEX_CONSTRAINT_TYPE_CODE_PRIMARY;
			default:
				throw new RuntimeException("Unknow loggingType:" + constraint);
		}
	}

	public static String toIndexConstraint(short constraint) {
		switch (constraint) {
			case AmiCenterEntityConsts.INDEX_CONSTRAINT_TYPE_CODE_NONE:
				return AmiCenterEntityConsts.INDEX_CONSTRAINT_TYPE_NONE;
			case AmiCenterEntityConsts.INDEX_CONSTRAINT_TYPE_CODE_UNIQUE:
				return AmiCenterEntityConsts.INDEX_CONSTRAINT_TYPE_UNIQUE;
			case AmiCenterEntityConsts.INDEX_CONSTRAINT_TYPE_CODE_PRIMARY:
				return AmiCenterEntityConsts.INDEX_CONSTRAINT_TYPE_PRIMARY;
			default:
				throw new RuntimeException("Unknow constraint:" + constraint);
		}
	}

	public static short toIndexAutogenCode(String autogen) {
		switch (autogen) {
			case AmiCenterEntityConsts.AUTOGEN_TYPE_NONE:
				return AmiCenterEntityConsts.AUTOGEN_TYPE_CODE_NONE;
			case AmiCenterEntityConsts.AUTOGEN_TYPE_RAND:
				return AmiCenterEntityConsts.AUTOGEN_TYPE_CODE_RAND;
			case AmiCenterEntityConsts.AUTOGEN_TYPE_INC:
				return AmiCenterEntityConsts.AUTOGEN_TYPE_CODE_INC;
			default:
				throw new RuntimeException("Unknow autogen:" + autogen);
		}
	}

	public static String toIndexAutogen(short autogen) {
		switch (autogen) {
			case AmiCenterEntityConsts.AUTOGEN_TYPE_CODE_NONE:
				return AmiCenterEntityConsts.AUTOGEN_TYPE_NONE;
			case AmiCenterEntityConsts.AUTOGEN_TYPE_CODE_RAND:
				return AmiCenterEntityConsts.AUTOGEN_TYPE_RAND;
			case AmiCenterEntityConsts.AUTOGEN_TYPE_CODE_INC:
				return AmiCenterEntityConsts.AUTOGEN_TYPE_INC;
			default:
				throw new RuntimeException("Unknow autogen:" + autogen);
		}
	}

	public static short toTablePersistEngineCode(String persistEngine) {
		if (persistEngine == null)
			return AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_CODE_NONE;
		switch (persistEngine) {
			case AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_FAST:
				return AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_CODE_FAST;
			case AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_TEXT:
				return AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_CODE_TEXT;
			case AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_HISTORICAL:
				return AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_CODE_HISTORICAL;
			case AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_NONE:
				return AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_CODE_NONE;
			default:
				throw new RuntimeException("Unknow PersistEngine:" + persistEngine);
		}
	}

	public static short toUnDefColumnCode(String onUndefColumnOption) {
		switch (onUndefColumnOption) {
			case AmiCenterEntityConsts.ON_UNDEF_COLUMN_OPTION_REJECT:
				return AmiCenterEntityConsts.ON_UNDEF_COLUMN_OPTION_CODE_REJECT;
			case AmiCenterEntityConsts.ON_UNDEF_COLUMN_OPTION_IGNORE:
				return AmiCenterEntityConsts.ON_UNDEF_COLUMN_OPTION_CODE_IGNORE;
			case AmiCenterEntityConsts.ON_UNDEF_COLUMN_OPTION_ADD:
				return AmiCenterEntityConsts.ON_UNDEF_COLUMN_OPTION_CODE_ADD;
			default:
				throw new RuntimeException("Unknow onUndefColumnOption:" + onUndefColumnOption);
		}
	}

	public static short toIndexTypeCode(String indexType) {
		switch (indexType) {
			case AmiCenterEntityConsts.INDEX_TYPE_HASH:
				return AmiCenterEntityConsts.INDEX_TYPE_CODE_HASH;
			case AmiCenterEntityConsts.INDEX_TYPE_SORT:
				return AmiCenterEntityConsts.INDEX_TYPE_CODE_SORT;
			case AmiCenterEntityConsts.INDEX_TYPE_SERIES:
				return AmiCenterEntityConsts.INDEX_TYPE_CODE_SERIES;
			default:
				throw new NullPointerException();
		}
	}

	public static String toIndexType(short indexType) {
		switch (indexType) {
			case AmiCenterEntityConsts.INDEX_TYPE_CODE_HASH:
				return AmiCenterEntityConsts.INDEX_TYPE_HASH;
			case AmiCenterEntityConsts.INDEX_TYPE_CODE_SORT:
				return AmiCenterEntityConsts.INDEX_TYPE_SORT;
			case AmiCenterEntityConsts.INDEX_TYPE_CODE_SERIES:
				return AmiCenterEntityConsts.INDEX_TYPE_SERIES;
			default:
				throw new NullPointerException();
		}
	}

	public static byte toCacheUnitCode(String unit) {
		switch (unit) {
			case AmiConsts.CACHE_UNIT_GB:
				return AmiConsts.CODE_CACHE_UNIT_GB;
			case AmiConsts.CACHE_UNIT_KB:
				return AmiConsts.CODE_CACHE_UNIT_KB;
			case AmiConsts.CACHE_UNIT_MB:
				return AmiConsts.CODE_CACHE_UNIT_MB;
			case AmiConsts.CACHE_UNIT_TB:
				return AmiConsts.CODE_CACHE_UNIT_TB;
			case AmiConsts.CACHE_UNIT_DEFAULT_BYTE:
				return AmiConsts.CODE_CACHE_UNIT_DEFAULT_BYTE;
			default:
				throw new NullPointerException();
		}
	}

	public static String toCacheUnit(Byte unit) {
		switch (unit) {
			case AmiConsts.CODE_CACHE_UNIT_GB:
				return AmiConsts.CACHE_UNIT_GB;
			case AmiConsts.CODE_CACHE_UNIT_KB:
				return AmiConsts.CACHE_UNIT_KB;
			case AmiConsts.CODE_CACHE_UNIT_MB:
				return AmiConsts.CACHE_UNIT_MB;
			case AmiConsts.CODE_CACHE_UNIT_TB:
				return AmiConsts.CACHE_UNIT_TB;
			case AmiConsts.CODE_CACHE_UNIT_DEFAULT_BYTE:
				return AmiConsts.CACHE_UNIT_DEFAULT_BYTE;
			default:
				throw new NullPointerException();
		}
	}

	public static byte toDataTypeCode(String type) {
		switch (type) {
			case AmiConsts.TYPE_NAME_STRING:
				return AmiDatasourceColumn.TYPE_STRING;
			case AmiConsts.TYPE_NAME_BINARY:
				return AmiDatasourceColumn.TYPE_BINARY;
			case AmiConsts.TYPE_NAME_BOOLEAN:
				return AmiDatasourceColumn.TYPE_BOOLEAN;
			case AmiConsts.TYPE_NAME_DOUBLE:
				return AmiDatasourceColumn.TYPE_DOUBLE;
			case AmiConsts.TYPE_NAME_FLOAT:
				return AmiDatasourceColumn.TYPE_FLOAT;
			case AmiConsts.TYPE_NAME_UTC:
				return AmiDatasourceColumn.TYPE_UTC;
			case AmiConsts.TYPE_NAME_UTCN:
				return AmiDatasourceColumn.TYPE_UTCN;
			case AmiConsts.TYPE_NAME_LONG:
				return AmiDatasourceColumn.TYPE_LONG;
			case AmiConsts.TYPE_NAME_INTEGER:
				return AmiDatasourceColumn.TYPE_INT;
			case AmiConsts.TYPE_NAME_SHORT:
				return AmiDatasourceColumn.TYPE_SHORT;
			case AmiConsts.TYPE_NAME_BYTE:
				return AmiDatasourceColumn.TYPE_BYTE;
			case AmiConsts.TYPE_NAME_BIGINT:
				return AmiDatasourceColumn.TYPE_BIGINT;
			case AmiConsts.TYPE_NAME_BIGDEC:
				return AmiDatasourceColumn.TYPE_BIGDEC;
			case AmiConsts.TYPE_NAME_COMPLEX:
				return AmiDatasourceColumn.TYPE_COMPLEX;
			case AmiConsts.TYPE_NAME_UUID:
				return AmiDatasourceColumn.TYPE_UUID;
			case "<NONE>":
				return AmiDatasourceColumn.TYPE_NONE;
			default:
				throw new NullPointerException();
		}
	}

	public static String toJoinTriggerType(short code) {
		switch (code) {
			case AmiCenterEntityConsts.TRIGGER_JOIN_TYPE_CODE_INNER:
				return "INNER";
			case AmiCenterEntityConsts.TRIGGER_JOIN_TYPE_CODE_LEFT:
				return "LEFT";
			case AmiCenterEntityConsts.TRIGGER_JOIN_TYPE_CODE_LEFT_ONLY:
				return "LEFT ONLY";
			case AmiCenterEntityConsts.TRIGGER_JOIN_TYPE_CODE_OUTER:
				return "OUTER";
			case AmiCenterEntityConsts.TRIGGER_JOIN_TYPE_CODE_OUTER_ONLY:
				return "OUTER ONLY";
			case AmiCenterEntityConsts.TRIGGER_JOIN_TYPE_CODE_RIGHT:
				return "RIGHT";
			case AmiCenterEntityConsts.TRIGGER_JOIN_TYPE_CODE_RIGHT_ONLY:
				return "RIGHT ONLY";
			default:
				throw new NullPointerException();
		}
	}

	public static short toJoinTriggerTypeCode(String name) {
		switch (name) {
			case "INNER":
				return AmiCenterEntityConsts.TRIGGER_JOIN_TYPE_CODE_INNER;
			case "LEFT":
				return AmiCenterEntityConsts.TRIGGER_JOIN_TYPE_CODE_LEFT;
			case "LEFT ONLY":
				return AmiCenterEntityConsts.TRIGGER_JOIN_TYPE_CODE_LEFT_ONLY;
			case "OUTER":
				return AmiCenterEntityConsts.TRIGGER_JOIN_TYPE_CODE_OUTER;
			case "OUTER ONLY":
				return AmiCenterEntityConsts.TRIGGER_JOIN_TYPE_CODE_OUTER_ONLY;
			case "RIGHT":
				return AmiCenterEntityConsts.TRIGGER_JOIN_TYPE_CODE_RIGHT;
			case "RIGHT ONLY":
				return AmiCenterEntityConsts.TRIGGER_JOIN_TYPE_CODE_RIGHT_ONLY;
			default:
				throw new NullPointerException();
		}
	}

	public static String formatRequiredField(String title) {
		return title + AmiCenterEntityConsts.REQUIRED_FIELD_ANNOTATION_HTML;
	}

	public static void main(String[] args) {
		String qry = "CREATE TRIGGER myAggregateTrigger5 OFTYPE AMISCRIPT ON algos2 PRIORITY 0 USE canMutateRow=\"true\" onInsertingScript=\"int i = 0;\\ni++;\\ni;\";";
		scriptToAdminNode(qry);

		String test = "{default_limit=2.0E8, product_limits=[{product_id=12345, limit=3.3E7}]}";
		Map m = parseUseOptions(test);
		System.out.println(m);
	}

	public static void formatFieldTitle(FormPortletField<?> field, boolean hasChanged) {
		if (hasChanged)
			field.setTitle(AmiCenterEntityConsts.CHANGED_FIELD_ANNOTATION_HTML + field.getTitle());
		else
			field.setTitle(field.getTitle().replace(AmiCenterEntityConsts.CHANGED_FIELD_ANNOTATION_HTML, ""));

	}
	//if a field is edited
	public static void onFieldEdited(FormPortletField<?> field, boolean changed) {
		if (changed) {
			field.setCssStyle("_bg=#FFFFAA");
			field.setTitle(AmiCenterEntityConsts.CHANGED_FIELD_ANNOTATION_HTML + field.getTitle());
		} else {
			field.setCssStyle("_bg=#FFFFFF");
			field.setTitle(field.getTitle().replace(AmiCenterEntityConsts.CHANGED_FIELD_ANNOTATION_HTML, ""));
		}

	}

	//if a field is edited
	public static void onFieldDisabled(FormPortletField<?> field, boolean disabled) {
		if (!(field instanceof AmiWebFormPortletAmiScriptField))
			return;
		if (disabled) {
			field.setCssStyle("_bg=#D3D3D3");
		} else {
			field.setCssStyle("_bg=#FFFFFF");
		}

	}
}
