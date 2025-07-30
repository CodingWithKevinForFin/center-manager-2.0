package com.f1.ami.web.rt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebAbstractRealtimeProcessor;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebObject;
import com.f1.ami.web.AmiWebObjectFields;
import com.f1.ami.web.AmiWebObjectFieldsImpl;
import com.f1.ami.web.AmiWebRealtimeObjectManager;
import com.f1.ami.web.AmiWebService;
import com.f1.base.CalcFrame;
import com.f1.base.IterableAndSize;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.Partition;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.grpc.GRPCWrapper;
import com.f1.utils.grpc.ProtobufParser;
import com.f1.utils.grpc.ProtobufProperties;
import com.f1.utils.grpc.ProtobufUtils;
import com.f1.utils.grpc.ProtobufUtils.ProtobufObjectTemplate;
import com.f1.utils.grpc.ProtobufUtils.StubType;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.stack.BasicCalcTypes;

import io.grpc.stub.StreamObserver;

public class AmiWebRealtimeProcessor_GRPC extends AmiWebAbstractRealtimeProcessor implements StreamObserver<Object> {

	BasicCalcTypes paramToSchema = new BasicCalcTypes();
	private Boolean keepOutput = true;
	private GRPCWrapper grpc;
	private final static Logger log = LH.get();
	private final LongKeyMap<AmiWebObject> output = new LongKeyMap<AmiWebObject>();
	final AmiWebService service;
	private String lastCommand;
	private String url;
	private String stubClass;
	private String metadata;
	private String classPrefix;
	private String primaryKey;
	private boolean rerunOnComplete = false;
	private boolean rerunOnFailure = false;
	final private Partition partition;

	boolean initialized = false;

	private HashMap<String, AmiWebObject_GRPC> primaryKeyMap = new HashMap<String, AmiWebObject_GRPC>();
	private AmiWebObjectFieldsImpl tmpChanges = new AmiWebObjectFieldsImpl();

	public AmiWebRealtimeProcessor_GRPC(AmiWebService service) {
		super(service);
		this.partition = service.getPortletManager().getState().getPartition();
		this.service = service;
		this.url = "";
		this.stubClass = "";
		this.metadata = "";
		this.classPrefix = "";
		this.primaryKey = "";
	}

	public AmiWebRealtimeProcessor_GRPC(AmiWebService service, String alias) {
		super(service, alias);
		this.partition = service.getPortletManager().getState().getPartition();
		this.service = service;
		this.url = "";
		this.stubClass = "";
		this.metadata = "";
		this.classPrefix = "";
		this.primaryKey = "";
	}

	public AmiWebRealtimeProcessor_GRPC(AmiWebService service, String alias, Boolean setKeepOutput, final String url, final String stubClass, final String metadata,
			final String classPrefix, final Boolean rerunOnComplete, final Boolean rerunOnFailure, final String primaryKey) {
		super(service, alias);
		this.partition = service.getPortletManager().getState().getPartition();
		this.service = service;
		this.keepOutput = setKeepOutput;
		this.rerunOnComplete = rerunOnComplete;
		this.rerunOnFailure = rerunOnFailure;
		this.primaryKey = primaryKey;

		this.url = url;
		if (SH.isnt(url))
			throw new RuntimeException("Failed to initialize GRPC FH: A valid GRPC URL is required");
		this.stubClass = stubClass;
		if (SH.isnt(this.stubClass))
			throw new RuntimeException("Fully qualified stub class required");
		this.metadata = metadata;
		this.classPrefix = classPrefix;
	}

	@Override
	public String getType() {
		return AmiWebRealtimeProcessorPlugin_GRPC.PLUGIN_ID;
	}

	@Override
	public IterableAndSize<AmiWebObject> getAmiObjects() {
		return output.values();
	}

	@Override
	public com.f1.utils.structs.table.stack.BasicCalcTypes getRealtimeObjectschema() {
		return this.paramToSchema;
	}

	@Override
	public com.f1.utils.structs.table.stack.BasicCalcTypes getRealtimeObjectsOutputSchema() {
		return this.paramToSchema;
	}

	@Override
	public void rebuild() {
		this.primaryKeyMap.clear();
		fireOnAmiEntitiesCleared();
		this.stop();
	}

	@Override
	public void onAmiEntitiesReset(AmiWebRealtimeObjectManager manager) {
		rebuild();
	}

	@Override
	public void onAmiEntityAdded(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		if (keepOutput)
			output.put(entity.getUniqueId(), entity);
		fireAmiEntityAdded(entity);
	}

	@Override
	public void onAmiEntityUpdated(AmiWebRealtimeObjectManager manager, AmiWebObjectFields fields, AmiWebObject entity) {
		if (keepOutput)
			output.put(entity.getUniqueId(), entity);
		fireAmiEntityUpdated(fields, entity);
	}

	@Override
	public void onAmiEntityRemoved(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		if (keepOutput)
			output.remove(entity.getUniqueId());
		fireAmiEntityRemoved(entity);
	}

	public String getURL() {
		return this.url;
	}

	public void setURL(final String url) {
		this.url = url;
	}

	public String getStubClass() {
		return this.stubClass;
	}

	public void setStubClass(final String stubClass) {
		this.stubClass = stubClass;
	}

	public String getMetadata() {
		return this.metadata;
	}

	public void setMetadata(final String metadata) {
		this.metadata = metadata;
	}

	public String getClassPrefix() {
		return this.classPrefix;
	}

	public void setClassPrefix(final String classPrefix) {
		this.classPrefix = classPrefix;
	}

	public Boolean getRerunOnFailure() {
		return this.rerunOnFailure;
	}

	public void setRerunOnFailure(final Boolean rerunOnFailure) {
		this.rerunOnFailure = rerunOnFailure;
	}

	public Boolean getRerunOnComplete() {
		return this.rerunOnComplete;
	}

	public void setRerunOnComplete(final Boolean rerunOnComplete) {
		this.rerunOnComplete = rerunOnComplete;
	}

	public void setPrimaryKey(final String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getPrimaryKey() {
		return this.primaryKey;
	}

	private Object noNull(Object val, Object _default) {
		return val == null ? _default : val;
	}

	private void initialize() {
		this.grpc = new GRPCWrapper();

		if (SH.isnt(url))
			throw new RuntimeException("Failed to initialize GRPC FH: A valid GRPC URL is required");
		if (SH.isnt(this.stubClass))
			throw new RuntimeException("Fully qualified stub class required");

		grpc.init(url, stubClass, metadata, StubType.NORMAL, classPrefix);
	}

	private void compile(final String command) {
		final byte firstInstruction = grpc.compileCommand(command);
		if (firstInstruction != ProtobufParser.INSTRUCTION_CALL_METHOD)
			throw new RuntimeException("Failed to initialize GRPC: Could not create method");

		initialized = true;
	}

	@Override
	public void init(String alias, Map<String, Object> configuration) {
		super.init(alias, configuration);
		this.keepOutput = (Boolean) noNull(configuration.get(ProtobufProperties.PROPERTY_GRPC_KEEP_OUTPUT), true);
		this.url = (String) configuration.get(ProtobufProperties.PROPERTY_GRPC_SERVER_URL);
		this.classPrefix = (String) configuration.get(ProtobufProperties.PROPERTY_GRPC_CLASS_PREFIX);
		this.metadata = (String) configuration.get(ProtobufProperties.PROPERTY_GRPC_METADATA);
		this.stubClass = (String) configuration.get(ProtobufProperties.PROPERTY_GRPC_STUB_CLASS);
		this.rerunOnFailure = (Boolean) noNull(configuration.get(ProtobufProperties.PROPERTY_GRPC_RERUN_ON_FAILURE), false);
		this.rerunOnComplete = (Boolean) noNull(configuration.get(ProtobufProperties.PROPERTY_GRPC_RERUN_ON_COMPLETE), false);
		this.primaryKey = (String) noNull(configuration.get(ProtobufProperties.PROPERTY_GRPC_PRIMARY_KEY), "");
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put(ProtobufProperties.PROPERTY_GRPC_KEEP_OUTPUT, keepOutput);
		r.put(ProtobufProperties.PROPERTY_GRPC_SERVER_URL, this.url);
		r.put(ProtobufProperties.PROPERTY_GRPC_STUB_CLASS, this.stubClass);
		r.put(ProtobufProperties.PROPERTY_GRPC_METADATA, this.metadata);
		r.put(ProtobufProperties.PROPERTY_GRPC_CLASS_PREFIX, this.classPrefix);
		r.put(ProtobufProperties.PROPERTY_GRPC_RERUN_ON_FAILURE, this.rerunOnFailure);
		r.put(ProtobufProperties.PROPERTY_GRPC_RERUN_ON_COMPLETE, this.rerunOnComplete);
		r.put(ProtobufProperties.PROPERTY_GRPC_PRIMARY_KEY, this.primaryKey);
		return r;
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return this.getRealtimeObjectsOutputSchema();
	}

	public boolean getKeepOutput() {
		return this.keepOutput;
	}

	public void setKeepOutput(boolean setKeepOutput) {
		this.keepOutput = setKeepOutput;
	}

	private void run() {
		if (this.initialized) {
			log.info("Running grpc processor: " + this.getAri());
			this.grpc.invokeAsyncCompiledMethod(this);
		}
	}

	public void stop() {
		log.info("Stopping grpc processor: " + this.getAri());
		this.initialized = false;
		if (this.grpc != null) {
			this.grpc.shutdownChannel();
		}
	}

	public void run(String command) {
		this.lastCommand = command;
		this.stop();
		this.initialize();
		this.compile(command);
		this.initialized = true;
		this.run();
	}

	@Override
	public Set<String> getLowerRealtimeIds() {
		return CH.s();
	}
	@Override
	public void onLowerAriChanged(AmiWebRealtimeObjectManager manager, String oldAri, String newAri) {
		return;
	}

	@SuppressWarnings("serial")
	public class AmiWebObject_GRPC extends HashMap<String, Object> implements AmiWebObject {

		private long uniqueId;

		public AmiWebObject_GRPC() {
		}

		public AmiWebObject_GRPC(final AmiWebObject entity, long uniqueId) {
			this.uniqueId = uniqueId;
			entity.fill((CalcFrame) this);
		}

		public void setID(Long uniqueId) {
			this.uniqueId = uniqueId;
		}
		@Override
		public Object getParam(String param) {
			return this.get(param);
		}

		@Override
		public void fill(Map<String, Object> o) {
			this.putAll(o);
		}

		@Override
		public StringBuilder toString(StringBuilder sink) {
			return null;
		}

		@Override
		public Object getValue(String key) {
			return get(key);
		}

		@Override
		public Object putValue(String key, Object value) {
			return super.put(key, value);
		}

		@Override
		public Class<?> getType(String var) {
			return getRealtimeObjectschema().getType(var);
		}

		@Override
		public boolean isVarsEmpty() {
			return false;
		}

		@Override
		public Iterable<String> getVarKeys() {
			return getRealtimeObjectschema().getVarKeys();
		}

		@Override
		public int getVarsCount() {
			return getRealtimeObjectschema().getVarsCount();
		}

		@Override
		public long getUniqueId() {
			return uniqueId;
		}

		@Override
		public String getObjectId() {
			return null;
		}

		@Override
		public long getId() {
			return uniqueId;
		}

		@Override
		public String getTypeName() {
			return getName();
		}

		@Override
		public void fill(CalcFrame sink) {
			for (String s : keySet())
				sink.putValue(s, get(s));
		}

		@Override
		public boolean equals(Object o) {
			if (o == this)
				return true;
			if (!(o instanceof AmiWebObject_GRPC))
				return false;
			final AmiWebObject_GRPC other = (AmiWebObject_GRPC) o;
			return this.uniqueId == other.uniqueId;
		}

	}

	@Override
	public void onNext(Object stubResult) {
		final long timeout = 60000;//This is how long to wait for gaining access to 
		if (!partition.lockForWrite(timeout, TimeUnit.MILLISECONDS)) {
			log.severe("Failed to aquire lock to user session, dropping: " + stubResult);
			return;
		}
		try {
			final ProtobufUtils.ProtobufPrimaryKey primaryKey = new ProtobufUtils.ProtobufPrimaryKey(this.primaryKey);
			ProtobufObjectTemplate template = null;
			Table resultTable = null;
			if (stubResult instanceof Iterator) {
				Iterator<?> it = (Iterator<?>) stubResult;
				while (it.hasNext()) {
					Object o = it.next();
					if (template == null) {
						template = grpc.getTemplate(o);
						resultTable = template.toEmptyTable(grpc.getTemplates());
						resultTable.setTitle(grpc.getMethodName());
						if (SH.is(primaryKey))
							resultTable.addColumn(Object.class, "I");
					}
					primaryKey.reset();
					template.fillRow(resultTable, null, o, grpc.getTemplates(), primaryKey);
				}
			} else {
				template = grpc.getTemplate(stubResult);
				resultTable = template.toEmptyTable(grpc.getTemplates());
				resultTable.setTitle(grpc.getMethodName());
				if (SH.is(primaryKey))
					resultTable.addColumn(Object.class, "I");
				primaryKey.reset();
				template.fillRow(resultTable, null, stubResult, grpc.getTemplates(), primaryKey);

			}
			this.paramToSchema.putAllIfAbsent(resultTable.getColumnTypesMapping());
			for (int i = 0; i < resultTable.getRows().size(); ++i) {
				final Row r = resultTable.getRow(i);
				AmiWebObject_GRPC add = null;

				boolean exists = false;
				if (r.containsKey("I")) {
					String pk = SH.toString(r.get("I"));
					if (this.primaryKeyMap.containsKey(pk)) {
						tmpChanges.clear();
						add = this.primaryKeyMap.get(pk);
						for (String k : add.getVarKeys())
							tmpChanges.addChange(k, add.get(k));
						add.fill((Map<String, Object>) r);
						exists = true;
					} else {
						add = new AmiWebObject_GRPC();
						add.fill((Map<String, Object>) r);
						add.setID(getService().getNextAmiObjectUId());
						this.primaryKeyMap.put(pk, add);
					}
				} else {
					add = new AmiWebObject_GRPC();
					add.fill((Map<String, Object>) r);
					add.setID(getService().getNextAmiObjectUId());
				}

				if (exists)
					this.onAmiEntityUpdated(null, tmpChanges, add);
				else
					this.onAmiEntityAdded(null, add);

			}
		} catch (Exception e) {
			log.severe("Received exception while parsing object: " + e.toString());
		} finally {
			partition.unlockForWrite();
		}
	}
	@Override
	public void onError(Throwable t) {
		if (this.initialized) {
			log.severe("Received error while processing: " + t.toString());
			this.initialized = false;

			if (this.rerunOnFailure) {
				log.info("Rerunning processor because rerunOnFailure=true: " + this.getAri());
				this.run(this.lastCommand);
			} else {
				throw new RuntimeException(t);
			}
		}

	}
	@Override
	public void onCompleted() {
		log.info("gRPC Processor " + this.getAri() + " received onCompleted!");
		if (this.rerunOnComplete)
			this.run(this.lastCommand);
	}

}
