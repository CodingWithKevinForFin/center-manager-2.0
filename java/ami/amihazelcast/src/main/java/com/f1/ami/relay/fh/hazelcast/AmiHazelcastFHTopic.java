package com.f1.ami.relay.fh.hazelcast;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.fh.AmiFHBase;
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
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.client.config.ClientSecurityConfig;

public class AmiHazelcastFHTopic extends AmiFHBase{
	
	private static ObjectToJsonConverter INSTANCE = new ObjectToJsonConverter();
	private AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
	private static final Logger log = LH.get();
	
	private static final String PROP_TOPICS = "topics";
	private static final String PROP_URL = "url";
	private static final String PROP_CLUSTER = "cluster";	
	private static final String PROP_USERNAME = "username";
	private static final String PROP_PASSWORD = "password";
	private static final String PROP_SSL_ENABLED = "sslenabled";
	private static final String PROP_TABLE_MAPPING = "mapping";
	
	private String[] topics;
	private Map<String, Map<String, FHSetter>> columnMappings = new HashMap<String, Map<String, FHSetter>>();
	private HazelcastInstance client;
	
	public AmiHazelcastFHTopic() {}
	
	public static void main(String[] args) {		
//		ClientConfig clientConfig = new ClientConfig();
//		clientConfig.setProperty("hazelcast.map.entry.filtering.natural.event.types", "true");
//        HazelcastInstance hz = HazelcastClient.newHazelcastClient(clientConfig);
//        
//        IMap<String, HazelcastJsonValue> map = hz.getMap("testMap");
//        map.addEntryListener(new AmiHazelcastMapListener(), Predicates.alwaysTrue(), true);
//        System.out.println("Entry Listener registered");        
//    
//	    while(true) {}
    }
	
	@Override
	public void start() {
		super.start();
		new Thread(new Connector(), "Hazelcast Topic Connector").start();
	}
	
	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);
		
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.setProperty("hazelcast.map.entry.filtering.natural.event.types", "true");
		String url = props.getRequired(PROP_URL);
		String cluster = props.getRequired(PROP_CLUSTER);
				
		topics = SH.splitWithEscape(',', '\\', props.getRequired(PROP_TOPICS));
		if (topics.length == 0)
			LH.info(log, "No topics added!");
		for (int i = 0; i < topics.length; ++i) {
			String mapping = props.getOptional(topics[i] + "." + PROP_TABLE_MAPPING, "");
			if (!SH.isEmpty(mapping)) {
				buildTableSchema(topics[i], mapping);
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
		
		List<String> addressList = new ArrayList<String>();
		addressList.add(url);
		clientConfig.getNetworkConfig().setAddresses(addressList).setSSLConfig(sslEnabled ? new SSLConfig() : null);
		clientConfig.setClusterName(cluster);
		this.client = HazelcastClient.newHazelcastClient(clientConfig);
				
		getAmiRelayIn().onConnection(EMPTY_PARAMS);
		login();
	}
		
	public class AmiHazelcastMessageListener implements MessageListener<Object> {
		
		private String topic;
		
		public AmiHazelcastMessageListener (String _topic) {
			topic = _topic;
		}
		
        public void onMessage(Message<Object> m) {        	
        	String body = m.getMessageObject().toString();
        	@SuppressWarnings("unchecked")
        	Map<String, Object> jsonMap = (Map<String, Object>)INSTANCE.stringToObject(body);
        	
        	converter.clear();
        	for (final Entry<String, Object> entry: jsonMap.entrySet()) {
        		final Object o = entry.getValue();
				//If object is null, ignore (FH does not support writing/reading nulls for types)
    			if (o == null)
    				continue;
        		Map<String, FHSetter> columnMapping = columnMappings.get(this.topic);
        		final FHSetter setter = columnMapping != null ? columnMapping.get(entry.getKey()) : null;
    			if (setter != null)				            						            				
    					setter.set(converter, o);
    			else
    				converter.appendString(entry.getKey(), SH.toString(o));
    		}
        	
    		publishObjectToAmi(-1, null, this.topic, 0, converter.toBytes());
        }
    }
	
	public class Connector implements Runnable {
		@Override
		public void run() {
			for (int i = 0; i < topics.length; ++i) {
				ITopic<Object> topic = client.getReliableTopic(topics[i]);
				topic.addMessageListener(new AmiHazelcastMessageListener(topics[i]));	
			}
		}
	}
	
	//Expected format: col1=int,col2=string,col3=double,...
	private void buildTableSchema(final String tableName, final String mapping) {
		Map<String, FHSetter> columnMapping = new HashMap<String, FHSetter>();
		List<String> colMaps = SH.splitToList(",", mapping);
		for (String colMap: colMaps) {
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
				columnMapping.put(columnName,  new Long_FHSetter(columnName));
				break;
			case "float":
				columnMapping.put(columnName,  new Float_FHSetter(columnName));
				break;
			case "double":
				columnMapping.put(columnName,  new Double_FHSetter(columnName));
				break;
			case "char":
			case "character":
				columnMapping.put(columnName,  new Char_FHSetter(columnName));
				break;
			case "bool":
			case "boolean":
				columnMapping.put(columnName,  new Bool_FHSetter(columnName));
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