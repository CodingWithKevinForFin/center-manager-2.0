package com.f1.ami.relay.fh.hazelcast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.fh.AmiFHBase;
import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.hazelcast.client.Client;
import com.hazelcast.client.ClientListener;
import com.hazelcast.client.ClientService;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientSecurityConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.query.Predicates;

public class AmiHazelcastFHMapPortable extends AmiFHBase {

	private static final Logger log = LH.get();
	private PropertyController props;

	private static final String PROP_MAPS = "maps";
	private static final String PROP_TABLENAME = "tablename";
	private static final String PROP_URL = "url";
	private static final String PROP_CLUSTER = "cluster";
	private static final String PROP_USERNAME = "username";
	private static final String PROP_PASSWORD = "password";
	private static final String PROP_SSL_ENABLED = "sslenabled";
	private static final String PROP_GET_EXISTING_VALUES = "getexistingvalues";
	private static final String PROP_PORTABLE_FACTORY_CLASSES = "portablefactoryclasses";
	private static final String PROP_PORTABLE_FACTORY_IDS = "portablefactoryids";
	private static final String PROP_PORTABLE_CLASS = "portableclass";
	private static final String PROP_PORTABLE_CLASS_ID = "portableclassid";
	private static final String PROP_PORTABLE_CLASS_FACTORY_ID = "portablefactoryid";
	private static final String PROP_DEBUG_ENABLED = "debug.enabled";
	private static final String PROP_DISABLE_LISTENER = "disable.listener";

	private Boolean getExistingValues = false;
	private String[] maps;
	private Map<String, Class<Portable>> portableClassMapping = new HashMap<String, Class<Portable>>();
	private Map<String, Boolean> existingValuesMap = new HashMap<String, Boolean>();
	private Map<String, Boolean> disableListenerMap = new HashMap<String, Boolean>();
	private HazelcastInstance client;
	private ClientService clientService;
	private Boolean debugEnabled;

	public AmiHazelcastFHMapPortable() {
	}

	public static void main(String[] args) {
	}

	@Override
	public void onCenterConnected(String centerId) {
		super.onCenterConnected(centerId);
		if (debugEnabled)
			LH.info(log, "Center ", centerId, " is ready. Starting hazelcast consumption.");
		new Thread(new Connector(), "Hazelcast Portable Map Connector").start();
	}
	@Override
	public void start() {
		super.start();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);
		this.props = props;
		this.debugEnabled = props.getOptional(PROP_DEBUG_ENABLED, false);
		if (this.debugEnabled)
			LH.info(log, "Debug mode enabled for Hazelcast Feedhandler");
		String[] factoryClassList = SH.splitWithEscape(',', '\\', props.getRequired(PROP_PORTABLE_FACTORY_CLASSES));
		String[] factoryIDList = SH.splitWithEscape(',', '\\', props.getRequired(PROP_PORTABLE_FACTORY_IDS));

		if (factoryClassList.length == 0 || factoryIDList.length == 0 || factoryClassList.length != factoryIDList.length) {
			LH.severe(log, "ERROR - Number of PortableFactory classes set in properties needs to be non-zero " + "AND equal to number of PortableFactory IDs. Classes declared: "
					+ factoryClassList.length + ", IDs declared: " + factoryIDList.length);
			return;
		}

		ClientConfig clientConfig = new ClientConfig();
		clientConfig.setProperty("hazelcast.map.entry.filtering.natural.event.types", "true");
		for (int i = 0; i < factoryClassList.length; ++i) {
			try {
				Class<PortableFactory> factoryClass = (Class<PortableFactory>) Class.forName(factoryClassList[i]);
				clientConfig.getSerializationConfig().addPortableFactory(Integer.valueOf(factoryIDList[i]), factoryClass.newInstance());
			} catch (Exception e) {
				LH.warning(log, e.getMessage());
			}
		}

		String url = props.getRequired(PROP_URL);
		String cluster = props.getRequired(PROP_CLUSTER);

		maps = SH.splitWithEscape(',', '\\', props.getRequired(PROP_MAPS));
		if (maps.length == 0)
			LH.warning(log, "No maps added!");

		Boolean sslEnabled = props.getOptional(PROP_SSL_ENABLED, false);
		String username = props.getOptional(PROP_USERNAME, "");
		if (!SH.isEmpty(username)) {
			String password = props.getRequired(PROP_PASSWORD);
			ClientSecurityConfig secConfig = new ClientSecurityConfig();
			secConfig.setUsernamePasswordIdentityConfig(username, password);
			clientConfig.setSecurityConfig(secConfig);
		}

		this.getExistingValues = props.getOptional(PROP_GET_EXISTING_VALUES, false);

		for (String map : maps) {
			if (!this.getExistingValues) {
				Boolean existingValues = props.getOptional(map + "." + PROP_GET_EXISTING_VALUES, false);
				existingValuesMap.put(map, existingValues);
			}

			boolean disableListener = props.getOptional(map + "." + PROP_DISABLE_LISTENER, false);
			disableListenerMap.put(map, disableListener);

			String portableClassStr = props.getRequired(map + "." + PROP_PORTABLE_CLASS);
			Class<Portable> portableClass = null;
			try {
				portableClass = (Class<Portable>) Class.forName(portableClassStr);
				portableClassMapping.put(map, portableClass);
			} catch (ClassNotFoundException e) {
				LH.warning(log,
						"Error in casting to Portable for \"" + map + "\": " + "Check that the class implement com.hazelcast.nio.serialization.Portable\n" + e.getMessage());
			}
			try {
				Class<AmiHazelcastPortableIDSetter> IDSetterClass = (Class<AmiHazelcastPortableIDSetter>) Class.forName(portableClassStr);
				AmiHazelcastPortableIDSetter IDSetter = IDSetterClass.newInstance();
				String classID = props.getRequired(map + "." + PROP_PORTABLE_CLASS_ID);
				String factoryID = props.getRequired(map + "." + PROP_PORTABLE_CLASS_FACTORY_ID);
				IDSetter.setPortableClassID(Integer.parseInt(classID));
				IDSetter.setPortableFactoryID(Integer.parseInt(factoryID));
			} catch (Exception e) {
				LH.warning(log, "Error in casting to AmiHazelCastPortableIDSetter for \"" + map + "\": "
						+ "Check that the class implements com.f1.ami.relay.fh.hazelcast.AmiHazelCastPortableIDSetter\n" + e.getMessage());
			}
		}

		List<String> addressList = new ArrayList<String>();
		addressList.add(url);
		clientConfig.getNetworkConfig().setAddresses(addressList).setSSLConfig(sslEnabled ? new SSLConfig() : null);
		clientConfig.setClusterName(cluster);
		
		HazelcastInstance instance = Hazelcast.newHazelcastInstance();
		this.clientService = instance.getClientService();
		this.clientService.addClientListener(new ClientListener() {
            @Override
            public void clientConnected(Client client) {
                LH.info(log, "Hazelcast Client connected!");
            }

            @Override
            public void clientDisconnected(Client client) {
            	LH.warning(log, "Hazelcast Client disconnected!");
            }
        });
		
		this.client = HazelcastClient.newHazelcastClient(clientConfig);
		getAmiRelayIn().onConnection(EMPTY_PARAMS);
		login();
	}

	public class AmiHazelcastMapListener implements EntryAddedListener<Object, Object>, EntryUpdatedListener<Object, Object>, EntryRemovedListener<Object, Object> {

		private String table;
		private Class<Portable> portableClass;

		public AmiHazelcastMapListener(String table, Class<Portable> portableClass) {
			this.table = table;
			this.portableClass = portableClass;
		}

		private void publish(Object entry) {
			final AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
			Object body;
			try {
				body = portableClass.cast(entry);
			} catch (Exception e) {
				LH.warning(log, "Casting error! Check that portable class & factory IDs are set correctly: \n" + e.getMessage());
				return;
			}
			Field[] fields = this.portableClass.getDeclaredFields();
			for (Field f : fields) {
				f.setAccessible(true);
				if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
					try {
						converter.append(f.getName(), f.get(body));
					} catch (Exception e) {
						LH.warning(log, "Could not get field value for " + f.getName() + ": " + e.getMessage());
					}
				}
			}
			byte[] msg = converter.toBytes();
			if (AmiHazelcastFHMapPortable.this.debugEnabled)
				LH.info(log, "Publishing record to table \"" + this.table + "\": " + AmiRelayMapToBytesConverter.toMap(msg));
			publishObjectToAmi(-1, null, this.table, 0, msg);
		}

		@Override
		public void entryRemoved(EntryEvent<Object, Object> event) {
		}

		@Override
		public void entryUpdated(EntryEvent<Object, Object> event) {
			publish(event.getValue());
		}

		@Override
		public void entryAdded(EntryEvent<Object, Object> event) {
			publish(event.getValue());
		}
	}

	public class Connector implements Runnable {
		@Override
		public void run() {
			for (String mapName : maps) {
				String tableName = props.getOptional(mapName + "." + PROP_TABLENAME, mapName);
				IMap<Object, Object> map = client.getMap(mapName);
				Class<Portable> portableClass = portableClassMapping.get(mapName);
				if (getExistingValues || existingValuesMap.get(mapName)) {
					LH.info(log, "Getting existing values for \"" + mapName + "\" (getexistingvalues enabled)");
					final AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
					if (AmiHazelcastFHMapPortable.this.debugEnabled)
						LH.info(log, "Found " + map.size() + " records for \"" + mapName + "\" (getexistingvalues enabled)");
					int count = 0;
					for (final Entry<Object, Object> entry : map.entrySet()) {
						converter.clear();
						Object body;
						try {
							body = portableClass.cast(entry.getValue());
						} catch (Exception e) {
							LH.warning(log, "Casting error! Check that portable class & factory IDs are set correctly: \n" + e.getMessage());
							break;
						}
						Field[] fields = portableClass.getDeclaredFields();
						for (Field f : fields) {
							f.setAccessible(true);
							if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
								try {
									converter.append(f.getName(), f.get(body));
								} catch (Exception e) {
									LH.warning(log, "Could not get field value for " + f.getName() + ": " + e.getMessage());
								}
							}
						}
						byte[] msg = converter.toBytes();
						if (AmiHazelcastFHMapPortable.this.debugEnabled)
							LH.info(log, "Publishing record to table \"" + tableName + "\": " + AmiRelayMapToBytesConverter.toMap(msg));
						publishObjectToAmi(-1, null, tableName, 0, msg);
						++count;
					}
					LH.info(log, "Published " + count + " records to table \"" + tableName + "\" for \"" + mapName + "\" (getexistingvalues enabled)");
				}
				try {
					if (!disableListenerMap.get(mapName)) { // add listener unless explicitly disabled by prop
						map.addEntryListener(new AmiHazelcastMapListener(tableName, portableClass), Predicates.alwaysTrue(), true);
						LH.info(log, "Map Listener registered for map \"" + mapName + "\" with table name \"" + tableName + "\" and portableClass: " + portableClass.getName());
					} else
						LH.info(log, "Map Listener disabled for map \"" + mapName + "\" with table name \"" + tableName + "\" and portableClass: " + portableClass.getName());
				} catch (Exception e) {
					LH.warning(log, "Could not register Map Listener for Hazelcast Map \"" + mapName + "\": " + e.getMessage());
				}
			}
		}
	}
}