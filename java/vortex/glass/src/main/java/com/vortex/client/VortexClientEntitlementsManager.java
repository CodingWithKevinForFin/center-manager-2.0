package com.vortex.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.ObjectGenerator;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.povo.f1app.reqres.F1AppChangeLogLevelRequest;
import com.f1.povo.f1app.reqres.F1AppInspectPartitionRequest;
import com.f1.povo.f1app.reqres.F1AppInterruptThreadRequest;
import com.f1.povo.f1app.reqres.F1AppRequest;
import com.f1.suite.web.portal.PortletManager;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.ToDoException;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.JavaInvoker;
import com.f1.utils.string.JavaInvoker.ObjectScope;
import com.f1.utils.string.Node;
import com.f1.utils.string.node.ArrayNode;
import com.f1.utils.string.node.ConstNode;
import com.f1.utils.string.node.KeywordNode;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.MapInMapInMap;
import com.f1.utils.structs.Tuple2;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentCron;
import com.f1.vortexcommon.msg.agent.VortexAgentDbColumn;
import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;
import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.f1.vortexcommon.msg.agent.VortexAgentDbPrivilege;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.VortexAgentDbTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentEvent;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.VortexAgentFileSystem;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.VortexAgentMachineEventStats;
import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.f1.vortexcommon.msg.agent.VortexAgentNetLink;
import com.f1.vortexcommon.msg.agent.VortexAgentProcess;
import com.f1.vortexcommon.msg.agent.VortexMetadatable;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunDeploymentRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunSignalProcessRequest;
import com.f1.vortexcommon.msg.eye.VortexBuildProcedure;
import com.f1.vortexcommon.msg.eye.VortexBuildResult;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexDeploymentSet;
import com.f1.vortexcommon.msg.eye.VortexExpectation;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.VortexEyeBackupDestination;
import com.f1.vortexcommon.msg.eye.VortexEyeMetadataField;
import com.f1.vortexcommon.msg.eye.VortexEyeScheduledTask;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeCreateDeploymentEnvironmentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupDestinationRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBuildProcedureRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBuildResultRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDbServerRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentSetRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageExpectationRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageMachineRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageMetadataFieldRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageScheduledTaskRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToAgentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToF1AppRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryHistoryRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBackupRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBuildProcedureRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDbInspectionRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDeploymentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunScheduledTaskRequest;

public class VortexClientEntitlementsManager {

	private static final Logger log = LH.get(VortexClientEntitlementsManager.class);
	private static final MapInMap<String, String, Object> RULES_TO_VARS = new MapInMap<String, String, Object>();
	private static final Set<Class<? extends Node>> VALID_EXPRESSION_TYPES = new HashSet<Class<? extends Node>>();
	private static final Map<Class, Byte> ENTITY_TYPES = new HashMap<Class, Byte>();
	private static final Map<String, Object> CATEGORIES = new HashMap<String, Object>();
	private static final String GLOBAL_VARIABLE_HOUR = "hour";
	private static final String GLOBAL_VARIABLE_REMOTE_IP = "remoteip";

	public static final String RUL_RUN_BACKUP = "run_backup";
	public static final String RUL_BACKUP_CREATE = "backup_create";
	public static final String RUL_BACKUP_UPDATE = "backup_update";
	public static final String RUL_BACKUP_DELETE = "backup_delete";
	public static final String RUL_DEPLOYMENT_CREATE = "deployment_create";
	public static final String RUL_RUN_BUILD_PROCEDURE = "build_procedure_run";
	public static final String RUL_RUN_DB_INSPECTION = "db_run_inspection";
	public static final String RUL_RUN_DEPLOYMENT = "deployment_run";
	public static final String RUL_SCHEDULED_TASK = "scheduled_task_run";
	public static final String RUL_QUERY_AUDIT_RULE_HISTORY = "audit_history";
	public static final String RUL_QUERY_SCHEDULED_TASK_HISTORY = "scheduled_task_history";
	public static final String RUL_QUERY_BACKUP_HISTORY = "backup_history";
	public static final String RUL_QUERY_BACKUP_DESTINATION_HISTORY = "backup_history_destination";
	public static final String RUL_QUERY_DEPLOYMENT_HISTORY = "deployment_history";
	public static final String RUL_QUERY_DEPLOYMENT_SET_HISTORY = "deployment_set_history";
	public static final String RUL_QUERY_BUILD_PROCEDURE_HISTORY = "query_build_procedure";
	public static final String RUL_QUERY_BUILD_RESULT_HISTORY = "query_build_result";
	public static final String RUL_QUERY_DB_HISTORY = "db_history";
	public static final String RUL_QUERY_MACHINE_HISTORY = "machine_history";
	public static final String RUL_AUDIT_RULE_CREATE = "audit_rule_create";
	public static final String RUL_AUDIT_RULE_DELETE = "audit_rule_delete";
	public static final String RUL_AUDIT_RULE_UPDATE = "audit_rule_update";
	public static final String RUL_BACKUP_DESTINATION_CREATE = "backup_destination_create";
	public static final String RUL_BACKUP_DESTINATION_DELETE = "backup_destination_delete";
	public static final String RUL_BACKUP_DESTINATION_UPDATE = "backup_destination_update";
	public static final String RUL_BUILD_PROCEDURE_CREATE = "build_procedure_create";
	public static final String RUL_BUILD_PROCEDURE_DELETE = "build_procedure_delete";
	public static final String RUL_BUILD_PROCEDURE_UPDATE = "build_procedure_update";
	public static final String RUL_BUILD_RESULT_DELETE = "build_result_delete";
	public static final String RUL_BUILD_RESULT_UPDATE = "build_result_update";
	public static final String RUL_DB_CREATE = "db_create";
	public static final String RUL_DB_DELETE = "db_delete";
	public static final String RUL_DB_UPDATE = "db_update";
	public static final String RUL_DEPLOYMENT_DELETE = "deployment_delete";
	public static final String RUL_DEPLOYMENT_UPDATE = "deployment_update";
	public static final String RUL_DEPLOYMENT_SET_CREATE = "deployment_set_create";
	public static final String RUL_DEPLOYMENT_SET_DELETE = "deployment_set_delete";
	public static final String RUL_DEPLOYMENT_SET_UPDATE = "deployment_set_update";
	public static final String RUL_EXPECTATION_UPDATE = "expectation_update";
	public static final String RUL_EXPECTATION_DELETE = "expectation_delete";
	public static final String RUL_EXPECTATION_CREATE = "expectation_create";
	public static final String RUL_MACHINE_DELETE = "machine_delete";
	public static final String RUL_MACHINE_UPDATE = "machine_update";
	public static final String RUL_METADATA_FIELD_CREATE = "metadata_field_create";
	public static final String RUL_METADATA_FIELD_DELETE = "metadata_field_delete";
	public static final String RUL_METADATA_FIELD_UPDATE = "metadata_field_update";
	public static final String RUL_SCHEDULED_TASK_CREATE = "scheduled_task_create";
	public static final String RUL_SCHEDULED_TASK_DELETE = "scheduled_task_delete";
	public static final String RUL_SCHEDULED_TASK_UPDATE = "scheduled_task_update";
	public static final String RUN_SEND_SIGNAL_TO_PROCESS = "run_send_signal_to_proess";
	public static final String DEPLOYMENT_DELETE_ALL_FILES = "deployment_delete_all_files";
	public static final String DEPLOYMENT_DEPLOY = "deployment_deploy";
	public static final String DEPLOYMENT_GET_FILE = "deployment_get_file";
	public static final String DEPLOYMENT_GET_FILE_STRUCTURE = "deployment_get_file_structure";
	public static final String DEPLOYMENT_RUN_SCRIPT = "deployemnt_run_script";
	public static final String DEPLOYMENT_START = "deployment_start";
	public static final String DEPLOYMENT_STOP = "deployment_stop";
	public static final String RUN_F1_APP_CHANGE_LOG_LEVEL = "run_f1app_change_log_level";
	public static final String RUN_F1_APP_INSPECT_PARTITION = "run_f1app_inspect_partition";
	public static final String RUN_F1_APP_INTERRUPT_THREAD = "run_f1app_interrupt_thread";
	public static final String GET_FILES = "get_files";

	public static final String CAT_TARGET_MACHINE = "target_machine";
	public static final String CAT_DEST_MACHINE = "dest_machine";
	public static final String CAT_BACKUP = "backup";
	public static final String CAT_BACKUP_DESTINATION = "backup_destination";
	public static final String CAT_MACHINE = "machine";
	public static final String CAT_PROCESS = "process";
	public static final String CAT_AUDIT_RULE = "audit_rule";
	public static final String CAT_BUILD_PROCEDURE = "build_procedure";
	public static final String CAT_BUILD_RESULT = "build_result";
	public static final String CAT_DB_SERVER = "db_server";
	public static final String CAT_DEPLOYMENT = "deployment";
	public static final String CAT_DEPLOYMENT_SET = "deployment_set";
	public static final String CAT_EXPECTATION = "expectation";
	public static final String CAT_METADATA_FIELD = "metadata_field";
	public static final String CAT_SCHEDULED_TASK = "scheduled_task";

	static {
		CATEGORIES.put(CAT_DEST_MACHINE, VortexAgentMachine.class);
		CATEGORIES.put(CAT_MACHINE, VortexAgentMachine.class);
		CATEGORIES.put(CAT_PROCESS, VortexAgentProcess.class);
		CATEGORIES.put(CAT_BACKUP, VortexEyeBackup.class);
		CATEGORIES.put(CAT_BACKUP_DESTINATION, VortexEyeBackupDestination.class);
		CATEGORIES.put(CAT_AUDIT_RULE, F1AppAuditTrailRule.class);
		CATEGORIES.put(CAT_BUILD_PROCEDURE, VortexBuildProcedure.class);
		CATEGORIES.put(CAT_BUILD_RESULT, VortexBuildResult.class);
		CATEGORIES.put(CAT_DB_SERVER, VortexAgentDbServer.class);
		CATEGORIES.put(CAT_DEPLOYMENT, VortexDeployment.class);
		CATEGORIES.put(CAT_DEPLOYMENT_SET, VortexDeploymentSet.class);
		CATEGORIES.put(CAT_EXPECTATION, VortexExpectation.class);
		CATEGORIES.put(CAT_METADATA_FIELD, VortexEyeMetadataField.class);
		CATEGORIES.put(CAT_SCHEDULED_TASK, VortexEyeScheduledTask.class);

		define(RUL_RUN_BACKUP, CAT_BACKUP);

		define(RUL_BACKUP_CREATE, CAT_BACKUP, CAT_MACHINE);
		define(RUL_BACKUP_UPDATE, CAT_BACKUP);
		define(RUL_BACKUP_DELETE, CAT_BACKUP);
		define(RUL_DEPLOYMENT_CREATE, CAT_DEPLOYMENT, CAT_MACHINE);
		define(RUL_RUN_BUILD_PROCEDURE, CAT_BUILD_PROCEDURE);
		define(RUL_RUN_DB_INSPECTION, CAT_DB_SERVER);
		define(RUL_RUN_DEPLOYMENT, CAT_DEPLOYMENT);
		define(RUL_SCHEDULED_TASK, CAT_SCHEDULED_TASK);
		define(RUL_QUERY_AUDIT_RULE_HISTORY, CAT_AUDIT_RULE);
		define(RUL_QUERY_SCHEDULED_TASK_HISTORY, CAT_SCHEDULED_TASK);
		define(RUL_QUERY_BACKUP_HISTORY, CAT_BACKUP);
		define(RUL_QUERY_BACKUP_DESTINATION_HISTORY, CAT_BACKUP_DESTINATION);
		define(RUL_QUERY_DEPLOYMENT_HISTORY, CAT_DEPLOYMENT);
		define(RUL_QUERY_DB_HISTORY, CAT_DB_SERVER);
		define(RUL_QUERY_MACHINE_HISTORY, CAT_MACHINE);
		define(RUL_AUDIT_RULE_CREATE, CAT_AUDIT_RULE);
		define(RUL_AUDIT_RULE_DELETE, CAT_AUDIT_RULE);
		define(RUL_AUDIT_RULE_UPDATE, CAT_AUDIT_RULE);
		define(RUL_BACKUP_DESTINATION_CREATE, CAT_MACHINE, CAT_BACKUP_DESTINATION);
		define(RUL_BACKUP_DESTINATION_DELETE, CAT_BACKUP_DESTINATION);
		define(RUL_BACKUP_DESTINATION_UPDATE, CAT_BACKUP_DESTINATION);
		define(RUL_BUILD_PROCEDURE_CREATE, CAT_MACHINE, CAT_BUILD_PROCEDURE);
		define(RUL_BUILD_PROCEDURE_DELETE, CAT_BUILD_PROCEDURE);
		define(RUL_BUILD_PROCEDURE_UPDATE, CAT_BUILD_PROCEDURE);
		define(RUL_BUILD_RESULT_DELETE, CAT_BUILD_RESULT);
		define(RUL_BUILD_RESULT_UPDATE, CAT_BUILD_RESULT);
		define(RUL_DB_CREATE, CAT_DB_SERVER, CAT_MACHINE);
		define(RUL_DB_DELETE, CAT_DB_SERVER);
		define(RUL_DB_UPDATE, CAT_DB_SERVER);
		define(RUL_DEPLOYMENT_DELETE, CAT_DEPLOYMENT);
		define(RUL_DEPLOYMENT_UPDATE, CAT_DEPLOYMENT);
		define(RUL_DEPLOYMENT_SET_CREATE, CAT_DEPLOYMENT_SET, CAT_MACHINE);
		define(RUL_DEPLOYMENT_SET_DELETE, CAT_DEPLOYMENT_SET);
		define(RUL_DEPLOYMENT_SET_UPDATE, CAT_DEPLOYMENT_SET);
		define(RUL_EXPECTATION_UPDATE, CAT_EXPECTATION);
		define(RUL_EXPECTATION_DELETE, CAT_EXPECTATION);
		define(RUL_EXPECTATION_CREATE, CAT_EXPECTATION, CAT_MACHINE);
		define(RUL_MACHINE_DELETE, CAT_MACHINE);
		define(RUL_MACHINE_UPDATE, CAT_MACHINE);
		define(RUL_METADATA_FIELD_CREATE, CAT_METADATA_FIELD, CAT_MACHINE);
		define(RUL_METADATA_FIELD_DELETE, CAT_METADATA_FIELD);
		define(RUL_METADATA_FIELD_UPDATE, CAT_METADATA_FIELD);
		define(RUL_SCHEDULED_TASK_CREATE, CAT_SCHEDULED_TASK, CAT_MACHINE);
		define(RUL_SCHEDULED_TASK_DELETE, CAT_SCHEDULED_TASK);
		define(RUL_SCHEDULED_TASK_UPDATE, CAT_SCHEDULED_TASK);
		define(RUN_SEND_SIGNAL_TO_PROCESS, CAT_MACHINE, CAT_PROCESS);
		define(DEPLOYMENT_DELETE_ALL_FILES, CAT_DEPLOYMENT);
		//RULES_TO_VARS.put(DEPLOYMENT_DEPLOY, empty);
		define(DEPLOYMENT_GET_FILE, CAT_DEPLOYMENT);
		define(DEPLOYMENT_GET_FILE_STRUCTURE, CAT_DEPLOYMENT);
		define(DEPLOYMENT_RUN_SCRIPT, CAT_DEPLOYMENT);
		define(DEPLOYMENT_START, CAT_DEPLOYMENT);
		define(DEPLOYMENT_STOP, CAT_DEPLOYMENT);
		define(RUN_F1_APP_CHANGE_LOG_LEVEL, CAT_MACHINE);
		define(RUN_F1_APP_INSPECT_PARTITION, CAT_MACHINE);
		define(RUN_F1_APP_INTERRUPT_THREAD, CAT_MACHINE);
		define(GET_FILES, CAT_MACHINE);

		VALID_EXPRESSION_TYPES.add(OperationNode.class);
		VALID_EXPRESSION_TYPES.add(ArrayNode.class);
		VALID_EXPRESSION_TYPES.add(VariableNode.class);
		VALID_EXPRESSION_TYPES.add(MethodNode.class);
		VALID_EXPRESSION_TYPES.add(KeywordNode.class);
		VALID_EXPRESSION_TYPES.add(ConstNode.class);

		ENTITY_TYPES.put(F1AppAuditTrailRule.class, VortexAgentEntity.TYPE_AUDIT_EVENT_RULE);
		ENTITY_TYPES.put(VortexEyeBackup.class, VortexAgentEntity.TYPE_BACKUP);
		ENTITY_TYPES.put(VortexEyeBackupDestination.class, VortexAgentEntity.TYPE_BACKUP_DESTINATION);
		ENTITY_TYPES.put(VortexBuildProcedure.class, VortexAgentEntity.TYPE_BUILD_PROCEDURE);
		ENTITY_TYPES.put(VortexBuildResult.class, VortexAgentEntity.TYPE_BUILD_RESULT);
		ENTITY_TYPES.put(VortexAgentCron.class, VortexAgentEntity.TYPE_CRON);
		ENTITY_TYPES.put(VortexAgentDbColumn.class, VortexAgentEntity.TYPE_DB_COLUMN);
		ENTITY_TYPES.put(VortexAgentDbDatabase.class, VortexAgentEntity.TYPE_DB_DATABASE);
		ENTITY_TYPES.put(VortexAgentDbObject.class, VortexAgentEntity.TYPE_DB_OBJECT);
		ENTITY_TYPES.put(VortexAgentDbPrivilege.class, VortexAgentEntity.TYPE_DB_PRIVILEDGE);
		ENTITY_TYPES.put(VortexAgentDbServer.class, VortexAgentEntity.TYPE_DB_SERVER);
		ENTITY_TYPES.put(VortexAgentDbTable.class, VortexAgentEntity.TYPE_DB_TABLE);
		ENTITY_TYPES.put(VortexDeploymentSet.class, VortexAgentEntity.TYPE_DEPLOYMENT_SET);
		ENTITY_TYPES.put(VortexDeployment.class, VortexAgentEntity.TYPE_DEPLOYMENT);
		ENTITY_TYPES.put(VortexAgentEvent.class, VortexAgentEntity.TYPE_EVENT);
		ENTITY_TYPES.put(VortexExpectation.class, VortexAgentEntity.TYPE_EXPECTATION);
		ENTITY_TYPES.put(VortexAgentFile.class, VortexAgentEntity.TYPE_FILE);
		ENTITY_TYPES.put(VortexAgentFileSystem.class, VortexAgentEntity.TYPE_FILE_SYSTEM);
		ENTITY_TYPES.put(VortexAgentMachine.class, VortexAgentEntity.TYPE_MACHINE);
		ENTITY_TYPES.put(VortexAgentMachineEventStats.class, VortexAgentEntity.TYPE_MACHINE_EVENT);
		ENTITY_TYPES.put(VortexEyeMetadataField.class, VortexAgentEntity.TYPE_METADATA_FIELD);
		ENTITY_TYPES.put(VortexAgentNetAddress.class, VortexAgentEntity.TYPE_NET_ADDRESS);
		ENTITY_TYPES.put(VortexAgentNetConnection.class, VortexAgentEntity.TYPE_NET_CONNECTION);
		ENTITY_TYPES.put(VortexAgentNetLink.class, VortexAgentEntity.TYPE_NET_LINK);
		ENTITY_TYPES.put(VortexAgentProcess.class, VortexAgentEntity.TYPE_PROCESS);
		ENTITY_TYPES.put(VortexEyeScheduledTask.class, VortexAgentEntity.TYPE_SCHEDULED_TASK);
	}

	public static class RulesInvoker extends JavaInvoker {

		@Override
		protected Object getReflectedField(Object o, String name) {
			if (o instanceof Map)
				return ((Map) o).get(name);
			else if (o instanceof VortexEntity) {
				VortexEntity e = (VortexEntity) o;
				if (name.startsWith("_"))
					return e.ask(name.substring(1));
				else if (o instanceof VortexMetadatable)
					return ((VortexMetadatable) o).getMetadata().get(name);
			}
			throw new RuntimeException("Can't handle: " + o + "," + name);
		}

		@Override
		public Object evaluateOperation(OperationNode n, ObjectScope objects) {
			if ("=".equals(n.operation)) {
				Object l = evaluate(n.left, objects);
				Object r = evaluate(n.right, objects);
				if (OH.eq(l, r))
					return Boolean.TRUE;
				else if (l instanceof String || r instanceof String) {
					return OH.eq(OH.toString(l), OH.toString(r));
				} else
					return false;
			}
			return super.evaluateOperation(n, objects);
		}

	}

	public static class RulesObjectScope implements ObjectScope {

		public static Map<String, ?> variables;

		public RulesObjectScope(Map<String, ?> vars) {
			this.variables = vars;
		}
		@Override
		public boolean containsKey(String key) {
			return variables.containsKey(key);
		}

		@Override
		public Object get(String key) {
			return variables.get(key);
		}

		@Override
		public Object put(String key, Object value) {
			return null;
		}

		@Override
		public List<String> getImports() {
			return Collections.EMPTY_LIST;
		}

	}

	private final JavaExpressionParser parser = new JavaExpressionParser();
	private final JavaInvoker invoker = new RulesInvoker();
	private VortexClientManager manager;
	//private Set<String> globalVariables = CH.s(GLOBAL_VARIABLE_NOW);
	private ObjectGenerator gen;
	private Map<String, Node> entitlements = new HashMap<String, Node>();

	private Map<Class, ValuedSchema<Valued>> schemas = new HashMap<Class, ValuedSchema<Valued>>();

	public Set<String> getRuleKeys() {
		return RULES_TO_VARS.keySet();
	}

	private static void define(String rulRunBackup, String... categories) {
		Map<String, Object> values = new HashMap<String, Object>();
		for (String category : categories)
			values.put(category, CH.getOrThrow(CATEGORIES, category));
		RULES_TO_VARS.put(rulRunBackup, values);
	}

	private <T extends Valued> ValuedSchema<T> getSchema(Class<T> type) {
		ValuedSchema<Valued> r = schemas.get(type);
		if (r == null)
			schemas.put(type, r = this.gen.nw(type).askSchema());
		if (r == null)
			CH.getOrThrow(schemas, type);
		return (ValuedSchema<T>) r;
	}

	public Node parse(String ruleKey, String expression, StringBuilder errorsSink) {
		Map<String, Object> availableVariables = RULES_TO_VARS.get(ruleKey);
		if (availableVariables == null) {
			errorsSink.append("Invalid rule: " + ruleKey);
			return null;
		}
		Node node = parser.parse(expression);
		BasicMultiMap.List<Class<? extends Node>, Node> sink = new BasicMultiMap.List<Class<? extends Node>, Node>();
		//		JavaExpressionParser.getNodesByType(node, sink);
		Set<Class<? extends Node>> unsupported = CH.comm(sink.keySet(), VALID_EXPRESSION_TYPES, true, false, false);
		if (!unsupported.isEmpty()) {
			errorsSink.append("Invalid rules syntax for rule " + ruleKey + " . The following node types are not supported: " + SH.join(",", unsupported)).append(SH.NEWLINE);
			return null;
		}

		//all variables should be in the form aaaa.bbbb, meaning always a hierarchy of 2
		Set<VariableNode> varNames = new IdentityHashSet<VariableNode>();
		if (sink.containsKey(VariableNode.class))
			varNames.addAll((List) sink.get(VariableNode.class));
		Set<Tuple2<String, String>> vars = new HashSet<Tuple2<String, String>>();
		for (Node i : CH.i(sink.get(OperationNode.class))) {
			OperationNode on = (OperationNode) i;
			if (".".equals(on.operation) && on.right instanceof VariableNode) {
				if (on.left instanceof VariableNode) {
					varNames.remove(on.left);
					varNames.remove(on.right);
					vars.add(new Tuple2<String, String>(on.left.toString(), on.right.toString()));
				} else {
					errorsSink.append("Invalid variable for rule " + ruleKey + ": " + on).append(SH.NEWLINE);
					return null;
				}
			}
		}
		if (!CH.isEmpty(varNames)) {
			errorsSink.append("Invalid variable(s) for rule " + ruleKey + ": " + SH.join(",", varNames)).append(SH.NEWLINE);
			return null;
		}
		if (CH.isntEmpty(vars)) {
			for (Tuple2<String, String> var : vars) {
				Object obj = availableVariables.get(var.getA());
				String subvar = var.getB();
				if (obj == null) {
					errorsSink.append("Unknown variable: ").append(var.getA());
				} else if (obj instanceof Class) {
					ValuedSchema schema = getSchema((Class) obj);
					if (SH.startsWith(subvar, '_')) {
						String key = subvar.substring(1);
						if (!schema.askParamValid(subvar.substring(1))) {
							errorsSink.append(
									"Invalid variable(s) for rule " + ruleKey + ": " + schema.askOriginalType().getSimpleName() + " does not have field " + SH.join(",", varNames))
									.append(SH.NEWLINE);
						}
					} else {
						byte entityType = ENTITY_TYPES.get(schema.askOriginalType());
						if (!manager.getMetadataFieldsForEntityType(entityType).containsKey(subvar)) {
							errorsSink.append(
									"Invalid variable(s) for rule " + ruleKey + ": " + schema.askOriginalType().getSimpleName() + " does not have metadata "
											+ SH.join(",", varNames)).append(SH.NEWLINE);
						}
					}
				} else if (((String) obj).equals(var.getB())) {

				}
			}
		}
		return node;
	}

	public VortexClientEntitlementsManager(VortexClientManager manager, ObjectGenerator gen) {
		this.manager = manager;
		this.gen = gen;
	}

	public void setEntitlements(Map<String, String> entitlements) {
		for (Entry<String, Tuple2<Map<String, Object>, String>> e : CH.join(RULES_TO_VARS, entitlements).entrySet()) {
			String key = e.getKey();
			Tuple2<Map<String, Object>, String> val = e.getValue();
			if (val.getA() == null)
				LH.warning(log, "Dropping unknown entitlement: ", key);
			else if (val.getB() == null)
				this.entitlements.put(key, FALSE_NODE);
			else
				setEntitlement(key, val.getB());
		}
	}
	public static final Node FALSE_NODE = new ConstNode(Boolean.FALSE);

	private void setEntitlement(String key, String expression) {
		StringBuilder errorsSink = new StringBuilder();
		Node node = this.parse(key, expression, errorsSink);
		if (errorsSink.length() > 0) {
			LH.warning(log, errorsSink);
			node = FALSE_NODE;
		}
		this.entitlements.put(key, node);
		// TODO Auto-generated method stub

	}

	//rule -> category -> var -> description
	public MapInMapInMap<String, String, String, String> getVariablesTree() {
		MapInMapInMap<String, String, String, String> r = new MapInMapInMap<String, String, String, String>();
		for (Entry<String, Map<String, Object>> rules : RULES_TO_VARS.entrySet()) {
			String rule = rules.getKey();
			for (Entry<String, Object> categories : rules.getValue().entrySet()) {
				String category = categories.getKey();
				Object val = categories.getValue();
				if (val instanceof Class) {
					ValuedSchema schema = getSchema((Class) val);

					for (ValuedParam i : schema.askValuedParams()) {
						if (i.getPid() == VortexMetadatable.PID_METADATA)
							continue;
						String var = "_" + i.getName();
						String desc = i.getReturnType().getSimpleName();
						r.putMulti(rule, category, var, desc);
					}
					byte entityType = CH.getOrThrow(ENTITY_TYPES, schema.askOriginalType());
					Map<String, VortexClientMetadataField> types = manager.getMetadataFieldsForEntityType(entityType);
					for (Entry<String, VortexClientMetadataField> t : types.entrySet()) {
						String var = t.getKey();
						String desc = SH.noNull(t.getValue().getData().getDescription()) + " (" + t.getValue().getValidationDescription() + ")";
						r.putMulti(rule, category, var, desc);
					}
				}
			}
		}
		return r;
	}

	public boolean checkEntitled(List<? extends VortexEyeRequest> requests) {
		for (VortexEyeRequest req : requests) {
			if (!checkEntitled(req, null)) {
				return false;
			}
		}
		return true;
	}
	public boolean checkEntitled(VortexEyeRequest req, PortletManager portletManager) {

		Node node = null;
		List<Map<String, VortexEntity>> variablesList = new ArrayList<Map<String, VortexEntity>>();
		Map<String, VortexEntity> variables = new HashMap<String, VortexEntity>();
		variablesList.add(variables);
		if (req instanceof VortexEyeQueryDataRequest) {
			switch (((VortexEyeQueryDataRequest) req).getType()) {
			//TODO: Audit event rule is not an eye entity
			//				case VortexAgentEntity.TYPE_AUDIT_EVENT_RULE:
			//					node = entitlements.get(RUL_QUERY_AUDIT_RULE_HISTORY);
			//					for (long id : ((VortexEyeQueryDataRequest) req).getIds()) {
			//						variables.put(CAT_AUDIT_RULE, manager.getAuditTrailRule(id).getData());
			//						variablesList.add(variables = new HashMap<String, VortexEntity>());
			//					}
			//					break;
				case VortexAgentEntity.TYPE_SCHEDULED_TASK:
					node = entitlements.get(RUL_QUERY_SCHEDULED_TASK_HISTORY);
					for (long id : ((VortexEyeQueryDataRequest) req).getIds()) {
						variables.put(CAT_SCHEDULED_TASK, manager.getScheduledTask(id).getData());
						variablesList.add(variables = new HashMap<String, VortexEntity>());
					}
					break;
				case VortexAgentEntity.TYPE_BACKUP:
					node = entitlements.get(RUL_QUERY_BACKUP_HISTORY);
					for (long id : ((VortexEyeQueryDataRequest) req).getIds()) {
						variables.put(CAT_BACKUP, manager.getBackup(id).getData());
						variablesList.add(variables = new HashMap<String, VortexEntity>());
					}
					break;
				case VortexAgentEntity.TYPE_BACKUP_DESTINATION:
					node = entitlements.get(RUL_QUERY_BACKUP_DESTINATION_HISTORY);
					for (long id : ((VortexEyeQueryDataRequest) req).getIds()) {
						variables.put(CAT_BACKUP_DESTINATION, manager.getBackupDestination(id).getData());
						variablesList.add(variables = new HashMap<String, VortexEntity>());
					}
					break;
				case VortexAgentEntity.TYPE_BUILD_PROCEDURE:
					node = entitlements.get(RUL_QUERY_BUILD_PROCEDURE_HISTORY);
					for (long id : ((VortexEyeQueryDataRequest) req).getIds()) {
						variables.put(CAT_BUILD_PROCEDURE, manager.getBuildProcedure(id).getData());
						variablesList.add(variables = new HashMap<String, VortexEntity>());
					}
					break;
				case VortexAgentEntity.TYPE_DEPLOYMENT:
					node = entitlements.get(RUL_QUERY_DEPLOYMENT_HISTORY);
					for (long id : ((VortexEyeQueryDataRequest) req).getIds()) {
						variables.put(CAT_DEPLOYMENT, manager.getDeployment(id).getData());
						variablesList.add(variables = new HashMap<String, VortexEntity>());
					}
					break;
				case VortexAgentEntity.TYPE_DEPLOYMENT_SET:
					node = entitlements.get(RUL_QUERY_DEPLOYMENT_SET_HISTORY);
					for (long id : ((VortexEyeQueryDataRequest) req).getIds()) {
						variables.put(CAT_DEPLOYMENT_SET, manager.getDeploymentSet(id).getData());
						variablesList.add(variables = new HashMap<String, VortexEntity>());
					}
					break;
				case VortexAgentEntity.TYPE_BUILD_RESULT:
					node = entitlements.get(RUL_QUERY_BUILD_RESULT_HISTORY);
					for (long id : ((VortexEyeQueryDataRequest) req).getIds()) {
						variables.put(CAT_BUILD_RESULT, manager.getBuildResult(id).getData());
						variablesList.add(variables = new HashMap<String, VortexEntity>());
					}
					break;
				case VortexAgentEntity.TYPE_DB_COLUMN:
				case VortexAgentEntity.TYPE_DB_DATABASE:
				case VortexAgentEntity.TYPE_DB_OBJECT:
				case VortexAgentEntity.TYPE_DB_PRIVILEDGE:
				case VortexAgentEntity.TYPE_DB_TABLE:
					throw new ToDoException();
				case VortexAgentEntity.TYPE_DB_SERVER:
					node = entitlements.get(RUL_QUERY_DB_HISTORY);
					for (long id : ((VortexEyeQueryDataRequest) req).getIds()) {
						variables.put(CAT_DB_SERVER, manager.getDbServer(id).getData());
						variablesList.add(variables = new HashMap<String, VortexEntity>());
					}
					break;
				case VortexAgentEntity.TYPE_CRON:
				case VortexAgentEntity.TYPE_EXPECTATION:
				case VortexAgentEntity.TYPE_MACHINE:
				case VortexAgentEntity.TYPE_NET_ADDRESS:
				case VortexAgentEntity.TYPE_NET_CONNECTION:
				case VortexAgentEntity.TYPE_NET_LINK:
				case VortexAgentEntity.TYPE_PROCESS:
					node = entitlements.get(RUL_QUERY_MACHINE_HISTORY);
					for (long id : ((VortexEyeQueryDataRequest) req).getIds()) {
						variables.put(CAT_MACHINE, manager.getAgentMachine(((VortexAgentEntity) manager.getEntityById(id)).getMachineInstanceId()).getData());
						variablesList.add(variables = new HashMap<String, VortexEntity>());
					}
					break;
			}
		} else if (req instanceof VortexEyeQueryHistoryRequest) {
			return false;
		} else if (req instanceof VortexEyeRunBackupRequest) {
			node = entitlements.get(RUL_RUN_BACKUP);
			for (Long id : ((VortexEyeRunBackupRequest) req).getBackups()) {
				variables.put(CAT_BACKUP, manager.getBackup(id).getData());
				variablesList.add(variables = new HashMap<String, VortexEntity>());
			}
		} else if (req instanceof VortexEyeRunBuildProcedureRequest) {
			node = entitlements.get(RUL_RUN_BUILD_PROCEDURE);
			long id = ((VortexEyeRunBuildProcedureRequest) req).getBuildProcedureId();
			variables.put(CAT_BUILD_PROCEDURE, manager.getBuildProcedure(id).getData());
		} else if (req instanceof VortexEyeRunDbInspectionRequest) {
			node = entitlements.get(RUL_RUN_DB_INSPECTION);
			variables.put(CAT_DB_SERVER, manager.getDbServer(((VortexEyeRunDbInspectionRequest) req).getDbServerId()).getData());
		} else if (req instanceof VortexEyeRunDeploymentRequest) {
			node = entitlements.get(RUL_RUN_DEPLOYMENT);
			variables.put(CAT_DEPLOYMENT, manager.getDeployment(((VortexEyeRunDeploymentRequest) req).getDeploymentId()).getData());
		} else if (req instanceof VortexEyeRunScheduledTaskRequest) {
			node = entitlements.get(RUL_SCHEDULED_TASK);
			variables.put(CAT_DEPLOYMENT, manager.getScheduledTask(((VortexEyeRunScheduledTaskRequest) req).getScheduledTaskId()).getData());
		} else if (req instanceof VortexEyeManageBackupRequest) {
			VortexEyeManageBackupRequest req2 = (VortexEyeManageBackupRequest) req;
			switch (getManageType((req2.getBackup()))) {
				case VortexAgentEntity.REVISION_NEW:
					node = entitlements.get(RUL_BACKUP_CREATE);
					variables.put(CAT_MACHINE, manager.getAgentMachineByUid(req2.getBackup().getSourceMachineUid()).getData());
					variables.put(CAT_BACKUP, req2.getBackup());
					break;
				case VortexAgentEntity.REVISION_DONE:
					node = entitlements.get(RUL_BACKUP_DELETE);
					variables.put(CAT_BACKUP, manager.getBackup(req2.getBackup().getId()).getData());
					break;
				default:
					variables.put(CAT_BACKUP, manager.getBackup(req2.getBackup().getId()).getData());
					variablesList.add(variables = new HashMap<String, VortexEntity>());
					variables.put(CAT_BACKUP, req2.getBackup());
					break;
			}
		} else if (req instanceof VortexEyeCreateDeploymentEnvironmentRequest) {
			for (VortexDeployment dep : ((VortexEyeCreateDeploymentEnvironmentRequest) req).getDeployments()) {
				variables.put(CAT_DEPLOYMENT, dep);
				variablesList.add(variables = new HashMap<String, VortexEntity>());
			}
			node = entitlements.get(RUL_DEPLOYMENT_CREATE);
			//TODO: audit rule is not eye entity
			//		} else if (req instanceof VortexEyeManageAuditTrailRuleRequest) {
			//			VortexEyeManageAuditTrailRuleRequest req2 = (VortexEyeManageAuditTrailRuleRequest) req;
			//			switch (getManageType(req2.getRule())) {
			//				case VortexAgentEntity.REVISION_NEW:
			//					node = entitlements.get(RUL_AUDIT_RULE_CREATE);
			//					variables.put(CAT_AUDIT_RULE, req2.getRule());
			//					break;
			//				case VortexAgentEntity.REVISION_DONE:
			//					node = entitlements.get(RUL_AUDIT_RULE_DELETE);
			//					variables.put(CAT_AUDIT_RULE, manager.getAuditTrailRule(req2.getRule().getId()).getData());
			//					break;
			//				default:
			//					node = entitlements.get(RUL_AUDIT_RULE_UPDATE);
			//					variables.put(CAT_AUDIT_RULE, manager.getAuditTrailRule(req2.getRule().getId()).getData());
			//					variablesList.add(variables = new HashMap<String, VortexEntity>());
			//					variables.put(CAT_AUDIT_RULE, req2.getRule());
			//					break;
			//			}
		} else if (req instanceof VortexEyeManageBackupDestinationRequest) {
			VortexEyeManageBackupDestinationRequest req2 = (VortexEyeManageBackupDestinationRequest) req;
			switch (getManageType(((VortexEyeManageBackupDestinationRequest) req).getBackupDestination())) {
				case VortexAgentEntity.REVISION_NEW:
					node = entitlements.get(RUL_BACKUP_DESTINATION_CREATE);
					variables.put(CAT_MACHINE, manager.getAgentMachineByUid(req2.getBackupDestination().getDestinationMachineUid()).getData());
					variables.put(CAT_BACKUP_DESTINATION, req2.getBackupDestination());
					break;
				case VortexAgentEntity.REVISION_DONE:
					node = entitlements.get(RUL_BACKUP_DESTINATION_DELETE);
					variables.put(CAT_BACKUP_DESTINATION, manager.getBackupDestination(req2.getBackupDestination().getId()).getData());
					break;
				default:
					node = entitlements.get(RUL_BACKUP_DESTINATION_UPDATE);
					variables.put(CAT_BACKUP_DESTINATION, manager.getBackupDestination(req2.getBackupDestination().getId()).getData());
					variablesList.add(variables = new HashMap<String, VortexEntity>());
					variables.put(CAT_BACKUP_DESTINATION, req2.getBackupDestination());
					break;
			}
		} else if (req instanceof VortexEyeManageBuildProcedureRequest) {
			VortexEyeManageBuildProcedureRequest req2 = (VortexEyeManageBuildProcedureRequest) req;
			switch (getManageType((req2.getBuildProcedure()))) {
				case VortexAgentEntity.REVISION_NEW:
					node = entitlements.get(RUL_BUILD_PROCEDURE_CREATE);
					variables.put(CAT_MACHINE, manager.getAgentMachineByUid(req2.getBuildProcedure().getBuildMachineUid()).getData());
					variables.put(CAT_BUILD_PROCEDURE, req2.getBuildProcedure());
					break;
				case VortexAgentEntity.REVISION_DONE:
					node = entitlements.get(RUL_BUILD_PROCEDURE_DELETE);
					variables.put(CAT_BUILD_PROCEDURE, manager.getBuildProcedure(req2.getBuildProcedure().getId()).getData());
					break;
				default:
					node = entitlements.get(RUL_BUILD_PROCEDURE_UPDATE);
					variables.put(CAT_BUILD_PROCEDURE, manager.getBuildProcedure(req2.getBuildProcedure().getId()).getData());
					variablesList.add(variables = new HashMap<String, VortexEntity>());
					variables.put(CAT_BUILD_PROCEDURE, req2.getBuildProcedure());
					break;
			}
		} else if (req instanceof VortexEyeManageBuildResultRequest) {
			VortexEyeManageBuildResultRequest req2 = (VortexEyeManageBuildResultRequest) req;
			switch (getManageType(req2.getBuildResult())) {
				case VortexAgentEntity.REVISION_DONE:
					node = entitlements.get(RUL_BUILD_RESULT_DELETE);
					variables.put(CAT_BUILD_RESULT, manager.getBuildResult(req2.getBuildResult().getId()).getData());
					break;
				default:
					node = entitlements.get(RUL_BUILD_RESULT_UPDATE);
					variables.put(CAT_BUILD_RESULT, manager.getBuildResult(req2.getBuildResult().getId()).getData());
					variablesList.add(variables = new HashMap<String, VortexEntity>());
					variables.put(CAT_BUILD_RESULT, req2.getBuildResult());
					break;
			}
		} else if (req instanceof VortexEyeManageDbServerRequest) {
			VortexEyeManageDbServerRequest req2 = (VortexEyeManageDbServerRequest) req;
			switch (getManageType(((VortexEyeManageDbServerRequest) req).getDbServer())) {
				case VortexAgentEntity.REVISION_NEW:
					node = entitlements.get(RUL_DB_CREATE);
					variables.put(CAT_MACHINE, manager.getAgentMachineByUid(req2.getDbServer().getMachineUid()).getData());
					variables.put(CAT_DB_SERVER, req2.getDbServer());
					break;
				case VortexAgentEntity.REVISION_DONE:
					node = entitlements.get(RUL_DB_DELETE);
					variables.put(CAT_DB_SERVER, manager.getDbServer(req2.getDbServer().getId()).getData());
					break;
				default:
					node = entitlements.get(RUL_DB_UPDATE);
					variables.put(CAT_DB_SERVER, manager.getDbServer(req2.getDbServer().getId()).getData());
					variablesList.add(variables = new HashMap<String, VortexEntity>());
					variables.put(CAT_DB_SERVER, req2.getDbServer());
					break;
			}
		} else if (req instanceof VortexEyeManageDeploymentRequest) {
			VortexEyeManageDeploymentRequest req2 = (VortexEyeManageDeploymentRequest) req;
			switch (getManageType(req2.getDeployment())) {
				case VortexAgentEntity.REVISION_NEW:
					node = entitlements.get(RUL_DEPLOYMENT_CREATE);
					variables.put(CAT_DEPLOYMENT, req2.getDeployment());
					variables.put(CAT_MACHINE, manager.getAgentMachineByUid(req2.getDeployment().getTargetMachineUid()).getData());
					break;
				case VortexAgentEntity.REVISION_DONE:
					node = entitlements.get(RUL_DEPLOYMENT_DELETE);
					variables.put(CAT_DEPLOYMENT, manager.getDeployment(req2.getDeployment().getId()).getData());
					break;
				default:
					node = entitlements.get(RUL_DEPLOYMENT_UPDATE);
					variables.put(CAT_DEPLOYMENT, manager.getDeployment(req2.getDeployment().getId()).getData());
					variablesList.add(variables = new HashMap<String, VortexEntity>());
					variables.put(CAT_DEPLOYMENT, req2.getDeployment());
					break;
			}
		} else if (req instanceof VortexEyeManageDeploymentSetRequest) {
			VortexEyeManageDeploymentSetRequest req2 = (VortexEyeManageDeploymentSetRequest) req;
			switch (getManageType(req2.getDeploymentSet())) {
				case VortexAgentEntity.REVISION_NEW:
					node = entitlements.get(RUL_DEPLOYMENT_SET_CREATE);
					variables.put(CAT_DEPLOYMENT_SET, req2.getDeploymentSet());
					break;
				case VortexAgentEntity.REVISION_DONE:
					node = entitlements.get(RUL_DEPLOYMENT_SET_DELETE);
					variables.put(CAT_DEPLOYMENT_SET, manager.getDeploymentSet(req2.getDeploymentSet().getId()).getData());
					break;
				default:
					node = entitlements.get(RUL_DEPLOYMENT_SET_UPDATE);
					variables.put(CAT_DEPLOYMENT_SET, manager.getDeploymentSet(req2.getDeploymentSet().getId()).getData());
					variablesList.add(variables = new HashMap<String, VortexEntity>());
					variables.put(CAT_DEPLOYMENT_SET, req2.getDeploymentSet());
					break;
			}
		} else if (req instanceof VortexEyeManageExpectationRequest) {
			VortexEyeManageExpectationRequest req2 = (VortexEyeManageExpectationRequest) req;
			switch (getManageType(req2.getExpectation())) {
				case VortexAgentEntity.REVISION_NEW:
					node = entitlements.get(RUL_EXPECTATION_CREATE);
					variables.put(CAT_MACHINE, manager.getAgentMachineByUid(req2.getExpectation().getMachineUid()).getData());
					variables.put(CAT_EXPECTATION, req2.getExpectation());
					break;
				case VortexAgentEntity.REVISION_DONE:
					node = entitlements.get(RUL_EXPECTATION_DELETE);
					variables.put(CAT_EXPECTATION, manager.getExpectation(req2.getExpectation().getId()).getData());
					break;
				default:
					node = entitlements.get(RUL_EXPECTATION_UPDATE);
					variables.put(CAT_EXPECTATION, manager.getExpectation(req2.getExpectation().getId()).getData());
					variablesList.add(variables = new HashMap<String, VortexEntity>());
					variables.put(CAT_EXPECTATION, req2.getExpectation());
					break;
			}
		} else if (req instanceof VortexEyeManageMachineRequest) {
			VortexEyeManageMachineRequest req2 = (VortexEyeManageMachineRequest) req;
			switch (getManageType(req2.getMachine())) {
				case VortexAgentEntity.REVISION_DONE:
					node = entitlements.get(RUL_MACHINE_DELETE);
					variables.put(CAT_MACHINE, manager.getAgentMachine(req2.getMachine().getId()).getData());
					break;
				default:
					node = entitlements.get(RUL_MACHINE_UPDATE);
					variables.put(CAT_MACHINE, manager.getAgentMachine(req2.getMachine().getId()).getData());
					variablesList.add(variables = new HashMap<String, VortexEntity>());
					variables.put(CAT_MACHINE, req2.getMachine());
					break;
			}
		} else if (req instanceof VortexEyeManageMetadataFieldRequest) {
			VortexEyeManageMetadataFieldRequest req2 = (VortexEyeManageMetadataFieldRequest) req;
			switch (getManageType(req2.getMetadataField())) {
				case VortexAgentEntity.REVISION_NEW:
					variables.put(CAT_DEPLOYMENT, req2.getMetadataField());
					node = entitlements.get(RUL_METADATA_FIELD_CREATE);
					break;
				case VortexAgentEntity.REVISION_DONE:
					node = entitlements.get(RUL_METADATA_FIELD_DELETE);
					variables.put(CAT_DEPLOYMENT, manager.getMetadataField(req2.getMetadataField().getId()).getData());
					break;
				default:
					node = entitlements.get(RUL_METADATA_FIELD_UPDATE);
					variables.put(CAT_DEPLOYMENT, manager.getMetadataField(req2.getMetadataField().getId()).getData());
					variablesList.add(variables = new HashMap<String, VortexEntity>());
					variables.put(CAT_DEPLOYMENT, req2.getMetadataField());
					break;
			}
		} else if (req instanceof VortexEyeManageScheduledTaskRequest) {
			VortexEyeManageScheduledTaskRequest req2 = (VortexEyeManageScheduledTaskRequest) req;
			switch (getManageType(req2.getScheduledTask())) {
				case VortexAgentEntity.REVISION_NEW:
					node = entitlements.get(RUL_SCHEDULED_TASK_CREATE);
					//TODO:variables.put(CAT_MACHINE, manager.getAgentMachineByUid(req2.getScheduledTask().get()));
					break;
				case VortexAgentEntity.REVISION_DONE:
					node = entitlements.get(RUL_SCHEDULED_TASK_DELETE);
					variables.put(CAT_SCHEDULED_TASK, manager.getScheduledTask(req2.getScheduledTask().getId()).getData());
					break;
				default:
					node = entitlements.get(RUL_SCHEDULED_TASK_UPDATE);
					variables.put(CAT_SCHEDULED_TASK, manager.getScheduledTask(req2.getScheduledTask().getId()).getData());
					variablesList.add(variables = new HashMap<String, VortexEntity>());
					variables.put(CAT_SCHEDULED_TASK, req2.getScheduledTask());
					break;
			}
		} else if (req instanceof VortexEyePassToAgentRequest) {
			VortexEyePassToAgentRequest req3 = (VortexEyePassToAgentRequest) req;
			VortexAgentRequest request = ((VortexEyePassToAgentRequest) req).getAgentRequest();
			if (request instanceof VortexAgentRunSignalProcessRequest) {
				VortexAgentRunSignalProcessRequest req2 = (VortexAgentRunSignalProcessRequest) request;
				node = entitlements.get(RUN_SEND_SIGNAL_TO_PROCESS);
				variables.put(CAT_MACHINE, manager.getAgentMachineByUid(req3.getAgentMachineUid()).getData());
				variables.put(CAT_MACHINE, (VortexAgentProcess) manager.getEntityById(req2.getProcessId()));
			} else if (request instanceof VortexAgentRunDeploymentRequest) {
				VortexAgentRunDeploymentRequest req2 = (VortexAgentRunDeploymentRequest) request;
				variables.put(CAT_DEPLOYMENT, manager.getDeployment(req2.getDeploymentId()).getData());
				switch (req2.getCommandType()) {
					case VortexAgentRunDeploymentRequest.TYPE_DELETE_ALL_FILES:
						node = entitlements.get(DEPLOYMENT_DELETE_ALL_FILES);
						break;
					case VortexAgentRunDeploymentRequest.TYPE_DEPLOY:
						//node = entitlements.get(DEPLOYMENT_DEPLOY);
						break;
					//case VortexAgentRunDeploymentRequest.TYPE_GET_FILE:
					//node = entitlements.get(DEPLOYMENT_GET_FILE);
					//break;
					//case VortexAgentRunDeploymentRequest.TYPE_GET_FILE_STRUCTURE:
					//node = entitlements.get(DEPLOYMENT_GET_FILE_STRUCTURE);
					//break;
					case VortexAgentRunDeploymentRequest.TYPE_RUN_SCRIPT:
						node = entitlements.get(DEPLOYMENT_RUN_SCRIPT);
						break;
					case VortexAgentRunDeploymentRequest.TYPE_START_SCRIPT:
						node = entitlements.get(DEPLOYMENT_START);
						break;
					case VortexAgentRunDeploymentRequest.TYPE_STOP_SCRIPT:
						node = entitlements.get(DEPLOYMENT_STOP);
						break;
				}
			} else if (request instanceof VortexAgentFileSearchRequest) {
				node = entitlements.get(GET_FILES);
				variables.put(CAT_MACHINE, manager.getAgentMachineByUid(req3.getAgentMachineUid()).getData());
			}
		} else if (req instanceof VortexEyePassToF1AppRequest) {
			VortexEyePassToF1AppRequest action = (VortexEyePassToF1AppRequest) req;
			VortexClientMachine machine = manager.getAgentMachine(manager.getJavaAppState(((VortexEyePassToF1AppRequest) req).getF1AppId()).getMachineInstanceId());
			variables.put(CAT_MACHINE, machine.getData());
			F1AppRequest appreq = action.getF1AppRequest();
			if (appreq instanceof F1AppChangeLogLevelRequest) {
				node = entitlements.get(RUN_F1_APP_CHANGE_LOG_LEVEL);
			} else if (appreq instanceof F1AppInspectPartitionRequest) {
				node = entitlements.get(RUN_F1_APP_INSPECT_PARTITION);
			} else if (appreq instanceof F1AppInterruptThreadRequest) {
				node = entitlements.get(RUN_F1_APP_INTERRUPT_THREAD);
			}
		}
		//boolean found = false;
		//for (Entry<String, Node> i : entitlements.entrySet()) {
		//if (i.getValue() == node) {
		//portletManager.showAlert("test performed: " + i.getKey());
		//found = true;
		//}
		//}
		//if (!found)
		//portletManager.showAlert("no test for : " + req.getClass());
		//if (node == null)
		//return true;
		if (node == null) {
			LH.fine(log, "Unknown action: ", OH.getClassName(req));
			return true;
		}

		for (int i = 0; i < variablesList.size(); i++) {
			Map<String, VortexEntity> o = variablesList.get(i);
			if (o.isEmpty() && i == variables.size() - 1 && variables.size() > 0)
				break;
			try {
				ObjectScope objects = new RulesObjectScope(o);
				if (!(Boolean.TRUE.equals(this.invoker.evaluate(node, objects))))
					return false;
			} catch (Exception e) {
				LH.log(log, Level.WARNING, "Error evaluating entitlements for ", req, ", node: ", node, "vars", o, e);
				return false;
			}
		}
		return true;
	}
	private static int getManageType(VortexEntity req) {
		if (req.getId() == 0)
			return VortexEntity.REVISION_NEW;
		else if (req.getRevision() == VortexEntity.REVISION_DONE)
			return VortexEntity.REVISION_DONE;
		return 1;
	}

	//private boolean checkEntitledForRunningBackup(long id) {
	//VortexDeployment dep = this.manager.getBackup(id).getDeployment().getData();
	//target_backet = this.manager.getBackup(id).getMachine().getData();
	//return false;
	//}
}
