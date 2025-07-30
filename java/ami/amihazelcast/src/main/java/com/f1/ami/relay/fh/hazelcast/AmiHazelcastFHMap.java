package com.f1.ami.relay.fh.hazelcast;

import java.util.ArrayList;
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
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Character;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Float;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientSecurityConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.query.Predicates;

public class AmiHazelcastFHMap extends AmiFHBase {

	private static ObjectToJsonConverter INSTANCE = new ObjectToJsonConverter();
	private static final Logger log = LH.get();

	private static final String PROP_MAPS = "maps";
	private static final String PROP_TABLENAME = "tablename";
	private static final String PROP_URL = "url";
	private static final String PROP_CLUSTER = "cluster";
	private static final String PROP_USERNAME = "username";
	private static final String PROP_PASSWORD = "password";
	private static final String PROP_SSL_ENABLED = "sslenabled";
	private static final String PROP_TABLE_MAPPING = "mapping";
	private static final String PROP_GET_EXISTING_VALUES = "getexistingvalues";
	private static final String PROP_DEBUG_ENABLED = "debug.enabled";

	private Boolean getExistingValues = false;
	private String[] maps;
	private Map<String, Boolean> existingValuesMap = new HashMap<String, Boolean>();
	private Map<String, Map<String, FHSetter>> columnMappings = new HashMap<String, Map<String, FHSetter>>();
	private HazelcastInstance client;
	private Boolean debugEnabled;

	public AmiHazelcastFHMap() {
	}

	public static void main(String[] args) {
	}

	@Override
	public void start() {
		super.start();
	}
	@Override
	public void onCenterConnected(String centerId) {
		super.onCenterConnected(centerId);
		if (debugEnabled)
			LH.info(log, "Center ", centerId, " is ready. Starting hazelcast consumption.");
		new Thread(new Connector(), "Hazelcast Map Connector").start();
	}

	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);

		ClientConfig clientConfig = new ClientConfig();
		clientConfig.setProperty("hazelcast.map.entry.filtering.natural.event.types", "true");
		String url = props.getRequired(PROP_URL);
		String cluster = props.getRequired(PROP_CLUSTER);
		this.debugEnabled = props.getOptional(PROP_DEBUG_ENABLED, false);

		maps = SH.splitWithEscape(',', '\\', props.getRequired(PROP_MAPS));
		if (maps.length == 0)
			LH.warning(log, "No maps added!");
		for (int i = 0; i < maps.length; ++i) {
			String mapping = props.getOptional(maps[i] + "." + PROP_TABLE_MAPPING, "");
			if (!SH.isEmpty(mapping)) {
				buildTableSchema(maps[i], mapping);
			}
		}

		Boolean sslEnabled = props.getOptional(PROP_SSL_ENABLED, false);
		String username = props.getOptional(PROP_USERNAME, "");
		if (!SH.isEmpty(username)) {
			String password = props.getRequired(PROP_PASSWORD);
			ClientSecurityConfig secConfig = new ClientSecurityConfig();
			secConfig.setUsernamePasswordIdentityConfig(username, password);
			clientConfig.setSecurityConfig(secConfig);
		}

		this.getExistingValues = props.getOptional(PROP_GET_EXISTING_VALUES, false);
		if (!this.getExistingValues) {
			for (int i = 0; i < maps.length; ++i) {
				Boolean existingValues = props.getOptional(maps[i] + "." + PROP_GET_EXISTING_VALUES, false);
				existingValuesMap.put(maps[i], existingValues);
			}
		}

		List<String> addressList = new ArrayList<String>();
		addressList.add(url);
		clientConfig.getNetworkConfig().setAddresses(addressList).setSSLConfig(sslEnabled ? new SSLConfig() : null);
		clientConfig.setClusterName(cluster);
		this.client = HazelcastClient.newHazelcastClient(clientConfig);

		getAmiRelayIn().onConnection(EMPTY_PARAMS);
		login();
	}

	public class AmiHazelcastMapListener
			implements EntryAddedListener<String, HazelcastJsonValue>, EntryUpdatedListener<String, HazelcastJsonValue>, EntryRemovedListener<String, HazelcastJsonValue> {

		private String map;
		private String tableName;

		public AmiHazelcastMapListener(String _tableName, String _map) {
			this.map = _map;
			this.tableName = _tableName;
		}

		@Override
		public void entryRemoved(EntryEvent<String, HazelcastJsonValue> event) {
		}

		@Override
		public void entryUpdated(EntryEvent<String, HazelcastJsonValue> event) {
			String body = event.getValue().getValue();
			@SuppressWarnings("unchecked")
			Map<String, Object> jsonMap = (Map<String, Object>) INSTANCE.stringToObject(body);
			final AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
			for (final Entry<String, Object> entry : jsonMap.entrySet()) {
				final Object o = entry.getValue();
				//If object is null, ignore (FH does not support writing/reading nulls for types)
				if (o == null)
					continue;
				Map<String, FHSetter> columnMapping = columnMappings.get(this.map);
				final FHSetter setter = columnMapping != null ? columnMapping.get(entry.getKey()) : null;
				if (setter != null)
					setter.set(converter, o);
				else
					converter.appendString(entry.getKey(), SH.toString(o));
			}
			publishObjectToAmi(-1, null, this.tableName, 0, converter.toBytes());
		}

		@Override
		public void entryAdded(EntryEvent<String, HazelcastJsonValue> event) {
			String body = event.getValue().getValue();
			@SuppressWarnings("unchecked")
			Map<String, Object> jsonMap = (Map<String, Object>) INSTANCE.stringToObject(body);
			final AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
			for (final Entry<String, Object> entry : jsonMap.entrySet()) {
				final Object o = entry.getValue();
				//If object is null, ignore (FH does not support writing/reading nulls for types)
				if (o == null)
					continue;
				Map<String, FHSetter> columnMapping = columnMappings.get(this.map);
				final FHSetter setter = columnMapping != null ? columnMapping.get(entry.getKey()) : null;
				if (setter != null)
					setter.set(converter, o);
				else
					converter.appendString(entry.getKey(), SH.toString(o));
			}
			publishObjectToAmi(-1, null, this.tableName, 0, converter.toBytes());
		}
	}

	public class Connector implements Runnable {

		@Override
		public void run() {
			for (int i = 0; i < maps.length; ++i) {
				String tableName = props.getOptional(maps[i] + "." + PROP_TABLENAME, maps[i]);
				IMap<String, HazelcastJsonValue> map = client.getMap(maps[i]);
				if (getExistingValues || existingValuesMap.get(maps[i])) {
					LH.info(log, "Getting existing values for \"" + maps[i] + "\" (getexistingvalues enabled)");
					if (AmiHazelcastFHMap.this.debugEnabled)
						LH.info(log, "Found " + map.size() + " records for " + maps[i] + " (getexistingvalues enabled)");
					final AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
					int count = 0;
					for (final Entry<String, HazelcastJsonValue> entry : map.entrySet()) {
						String body = entry.getValue().getValue();
						@SuppressWarnings("unchecked")
						Map<String, Object> jsonMap = (Map<String, Object>) INSTANCE.stringToObject(body);
						converter.clear();
						for (final Entry<String, Object> jsonEntry : jsonMap.entrySet()) {
							final Object o = jsonEntry.getValue();
							if (o == null)
								continue;
							Map<String, FHSetter> columnMapping = columnMappings.get(maps[i]);
							final FHSetter setter = columnMapping != null ? columnMapping.get(jsonEntry.getKey()) : null;
							if (setter != null)
								setter.set(converter, o);
							else
								converter.appendString(jsonEntry.getKey(), SH.toString(o));
						}
						if (AmiHazelcastFHMap.this.debugEnabled)
							LH.info(log, "Publishing record to table \"" + tableName + "\": " + jsonMap);
						publishObjectToAmi(-1, null, tableName, 0, converter.toBytes());
						++count;
					}
					LH.info(log, "Published " + count + " records to table " + tableName + " for " + maps[i] + " (getexistingvalues enabled)");
				}
				map.addEntryListener(new AmiHazelcastMapListener(tableName, maps[i]), Predicates.alwaysTrue(), true);
				LH.info(log, "Map Listener registered for " + maps[i] + "(Table Name \"" + tableName + "\"");
			}
		}

	}

	//Expected format: col1=int,col2=string,col3=double,...
	private void buildTableSchema(final String tableName, final String mapping) {
		Map<String, FHSetter> columnMapping = new HashMap<String, FHSetter>();
		List<String> colMaps = SH.splitToList(",", mapping);
		for (String colMap : colMaps) {
			//Extract column name and column type
			List<String> colType = SH.splitToList("=", colMap);
			if (colType.size() != 2)
				throw new RuntimeException("Failed to parse column name and column type: " + colMap);
			final String columnName = SH.trim(colType.get(0));
			final String columnType = SH.toLowerCase(SH.trim(colType.get(1)));
			switch (columnType) {
				case "string":
				case "str":
					columnMapping.put(columnName, new String_FHSetter(columnName));
					break;
				case "integer":
				case "int":
					columnMapping.put(columnName, new Int_FHSetter(columnName));
					break;
				case "short":
					columnMapping.put(columnName, new Short_FHSetter(columnName));
					break;
				case "long":
					columnMapping.put(columnName, new Long_FHSetter(columnName));
					break;
				case "float":
					columnMapping.put(columnName, new Float_FHSetter(columnName));
					break;
				case "double":
					columnMapping.put(columnName, new Double_FHSetter(columnName));
					break;
				case "char":
				case "character":
					columnMapping.put(columnName, new Char_FHSetter(columnName));
					break;
				case "bool":
				case "boolean":
					columnMapping.put(columnName, new Bool_FHSetter(columnName));
					break;
				default:
					throw new UnsupportedOperationException("Unsupported column type: " + columnType);
			}
		}
		columnMappings.put(tableName, columnMapping);
	}

	//Setter classes
	private class Double_FHSetter extends FHSetter {

		public Double_FHSetter(final String key) {
			super(key);
		}
		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendDouble(key, Caster_Double.INSTANCE.cast(val));
		}
	}

	private class Float_FHSetter extends FHSetter {

		public Float_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendFloat(key, Caster_Float.INSTANCE.cast(val));
		}
	}

	private class Long_FHSetter extends FHSetter {

		public Long_FHSetter(final String key) {
			super(key);
		}
		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendLong(key, Caster_Long.INSTANCE.cast(val));
		}
	}

	private class Short_FHSetter extends FHSetter {

		public Short_FHSetter(final String key) {
			super(key);
		}
		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendShort(key, Caster_Short.INSTANCE.cast(val));
		}
	}

	private class Bool_FHSetter extends FHSetter {

		public Bool_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendBoolean(key, Caster_Boolean.INSTANCE.cast(val));
		}
	}

	private class Char_FHSetter extends FHSetter {

		public Char_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendChar(key, Caster_Character.INSTANCE.cast(val));
		}
	}

	private class Int_FHSetter extends FHSetter {

		public Int_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendInt(key, Caster_Integer.INSTANCE.cast(val));
		}
	}

	private class String_FHSetter extends FHSetter {

		public String_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendString(key, val.toString());
		}
	}

	private abstract class FHSetter {

		final String key;

		public FHSetter(final String key) {
			this.key = key;
		}
		public abstract void set(final AmiRelayMapToBytesConverter c, final Object val);
	}
}