package com.f1.ami.relay.fh.grpc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.fh.AmiFHBase;
import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.grpc.GRPCWrapper;
import com.f1.utils.grpc.ProtobufProperties;
import com.f1.utils.grpc.ProtobufUtils;
import com.f1.utils.grpc.ProtobufUtils.ProtobufObjectTemplate;
import com.f1.utils.grpc.ProtobufUtils.StubType;

public class AmiGRPCFH extends AmiFHBase {

	private static final Logger log = LH.get();
	private Thread t;
	private String targetTableName;
	private GRPCWrapper grpc = new GRPCWrapper();
	private HashMap<String, ProtobufObjectTemplate> templates = new HashMap<String, ProtobufObjectTemplate>();
	private String primaryKey = "";

	private static final String PROPERTY_GRPC_TABLE_NAME = "tableName";

	public AmiGRPCFH() {
	}

	private void startGRPC() {
		final AmiFHBase base = this;
		final Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					final ProtobufUtils.ProtobufPrimaryKey pk = new ProtobufUtils.ProtobufPrimaryKey(primaryKey);
					final AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
					ProtobufObjectTemplate template = null;
					while (true) {
						Object stubResult = grpc.invokeCompiledMethod();
						if (stubResult instanceof Iterator) {
							Iterator<?> it = (Iterator<?>) stubResult;
							while (it.hasNext()) {
								Object o = it.next();
								if (template == null) {
									template = ProtobufUtils.getOrSetTemplate(o, templates);
								}
								pk.reset();
								template.fillConverter(converter, o, templates, base, targetTableName, pk);
							}

						} else {
							if (template == null) {
								template = ProtobufUtils.getOrSetTemplate(stubResult, templates);
							}
							pk.reset();
							template.fillConverter(converter, stubResult, templates, base, targetTableName, pk);
						}
					}
				} catch (Exception e) {
					log.severe(e.getMessage());
					throw new RuntimeException(e);
				} finally {
					grpc.shutdownChannel();
				}
			}
		};

		t = getManager().getThreadFactory().newThread(r);
		t.setDaemon(true);
		t.setName("GRPC-" + this.getId());
		t.start();

	}

	@Override
	public void start() {
		super.start();
		try {
			startGRPC();
			onStartFinish(true);
		} catch (Exception e) {
			log.severe("Failed to start up the FH: " + e.getMessage());
			onStartFinish(false);
		}
	}

	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);

		final String url = props.getRequired(ProtobufProperties.PROPERTY_GRPC_SERVER_URL);
		final String stubClass = props.getRequired(ProtobufProperties.PROPERTY_GRPC_STUB_CLASS);
		String metadata = props.getOptional(ProtobufProperties.PROPERTY_GRPC_METADATA, "");
		final String classPrefix = props.getOptional(ProtobufProperties.PROPERTY_GRPC_CLASS_PREFIX, "");
		this.targetTableName = props.getOptional(PROPERTY_GRPC_TABLE_NAME, "GRPCTable");
		final String command = props.getRequired(ProtobufProperties.PROPERTY_GRPC_COMMAND);
		this.primaryKey = props.getOptional(ProtobufProperties.PROPERTY_GRPC_PRIMARY_KEY, "");

		grpc.init(url, stubClass, metadata, StubType.BLOCKING, classPrefix);
		grpc.compileCommand(command);

		getAmiRelayIn().onConnection(EMPTY_PARAMS);
		login();

	}

}