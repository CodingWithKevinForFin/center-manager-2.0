package com.f1.ami.relay.fh.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.RecordDeserializationException;

import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.fh.AmiFHBase;
import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;

public class AmiKafkaFH extends AmiFHBase {
	private static final Logger log = LH.get();

	public AmiKafkaFH() {
	}

	private static final String PROP_TOPICS = "topics";

	//AMI specific kafka properties
	private static final String PROP_USE_RECORD_KEY = "use.record.key";
	private static final String PROP_ENABLE_DEBUG_LOG = "enable.debug.log";
	private static final String PROP_HELPER_FACTORY_CLASS = "helper.factory.class";
	private static final String PROP_STOP_CONSUMPTION_ON_INVALID_RECORD = "stop.consumption.on.invalid.record";

	private String[] topics;
	private KafkaConsumer<String, Object> consumer;
	private Properties kprops;
	private List<String> amiProps;
	private AmiKafkaHelper kafkaHelper;
	private boolean stopConsumptionInvalidRecord;

	private Boolean useRecordKey;

	private Boolean debugLogEnabled;
	private Long pollCount = 0L;

	@Override
	public void start() {
		super.start();
		new Thread(new Connector(), "kafka connector").start();
	}

	// configure Kafka properties
	@SuppressWarnings("unchecked")
	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);

		//AMI specific fields
		topics = SH.split(',', this.props.getRequired(PROP_TOPICS));
		useRecordKey = this.props.getOptional(PROP_USE_RECORD_KEY, true);
		debugLogEnabled = this.props.getOptional(PROP_ENABLE_DEBUG_LOG, false);
		stopConsumptionInvalidRecord = this.props.getOptional(PROP_STOP_CONSUMPTION_ON_INVALID_RECORD, true);

		//exclusion list for kafka props
		amiProps = new ArrayList<String>();
		amiProps.add(PROP_TOPICS);
		amiProps.add(PROP_USE_RECORD_KEY);
		amiProps.add(PROP_ENABLE_DEBUG_LOG);
		amiProps.add(PROP_HELPER_FACTORY_CLASS);

		String valueDeserializer = this.props.getRequired(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG);
		String helperFactoryClass = this.props.getOptional(PROP_HELPER_FACTORY_CLASS, "com.f1.ami.relay.fh.kafka.AmiKafkaHelperFactory");
		try {
			Class<AmiKafkaHelperFactory> factoryClass = (Class<AmiKafkaHelperFactory>) Class.forName(helperFactoryClass);
			kafkaHelper = factoryClass.newInstance().getKafkaHelper(valueDeserializer);
			if (kafkaHelper == null) {
				LH.warning(log, "No helper class found for deserializer class " + valueDeserializer + "! Records may have parsing errors.");
				kafkaHelper = new AmiKafkaHelper();
			}
		} catch (Exception e) {
			LH.warning(log, e.getMessage());
		}

		kprops = setProperties();
		getAmiRelayIn().onConnection(EMPTY_PARAMS);
		login();
	}

	public class Connector implements Runnable {

		@Override
		public void run() {

			try {
				// set up consumer using properties set earlier
				consumer = new KafkaConsumer<>(kprops);
				// subscribe to topics given by the user
				consumer.subscribe(Arrays.asList(topics));
				StringBuilder topicList = new StringBuilder();

				if (debugLogEnabled) {
					for (String topic : topics) {
						topicList.append(topic);
						topicList.append(" AS ");
						topicList.append(AmiKafkaFH.this.getAmiTableName(topic));
						topicList.append(",");
					}
					LH.info(log, "Subscribed to topics: " + SH.stripSuffix(topicList.toString(), ",", false));
				}

				while (true) {
					try {
						ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(100));

						if (debugLogEnabled && records.count() != 0)
							LH.info(log, "Polling... (Poll Count " + ++pollCount + "). Found " + records.count() + " records");
						for (ConsumerRecord<String, Object> record : records) {
							String topic = record.topic();
							try {
								String key = useRecordKey ? record.key() : null;
								Object value = record.value();
								long timestamp = record.timestamp();

								// sending the data received to AMI:
								MessageHandler messagehandler = new Handler();
								messagehandler.sendMessage(AmiKafkaFH.this.getAmiTableName(topic), key, value, timestamp);

							} catch (Exception e) {
								LH.warning(log, "Error for Kafka topic: ", topic, AmiKafkaFH.this.getAmiTableName(topic), e);
							}
						}
						consumer.commitAsync();
						if (debugLogEnabled && records.count() != 0)
							LH.info(log, records.count() + " records sent");
					} catch (RecordDeserializationException re) {
						re.printStackTrace();
						long offset = re.offset();
						Throwable t = re.getCause();
						LH.warning(log, "Failed to consume at partition={} offset={}", re.topicPartition().partition(), offset, t);
						LH.info(log, "Skipping offset=" + offset);
						if (!stopConsumptionInvalidRecord) {
							consumer.seek(re.topicPartition(), offset + 1);
							consumer.commitAsync();
						} else {
							LH.warning(log, "Stopping consumption because property " + PROP_STOP_CONSUMPTION_ON_INVALID_RECORD + " is set to " + stopConsumptionInvalidRecord);
							stop();
						}
					} catch (Exception e) {
						LH.warning(log, "Failed to consume record ", e);
					}
				}
			} catch (Exception e) {
				LH.warning(log, "Error for Kafka subscribing or polling: ", e);
			}
		}

	}

	@Override
	public void stop() {
		// handle Kafka client closing
		consumer.close();
		super.stop();
	}

	private class Handler implements MessageHandler {
		private AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
		private Map<String, Object> msg = new HashMap<String, Object>();
		private StringBuilder error = new StringBuilder();

		public Handler() {
		}

		// sends the content of the message to AMI
		@Override
		public void sendMessage(String topic, String key, Object value, long timestamp) {
			try {
				// emptying the map
				msg.clear();
				error.setLength(0);

				// trying to parse the data received from consumer into the msg map
				if (!kafkaHelper.parseMessage(value, msg, error)) {
					LH.warning(log, "Error processing data: ", value, " ==> ", error);
					return;
				}
				String id = key;

				// converting the map to a byte array to send to AMI
				byte[] b = converter.toBytes(msg);

				String type = getAmiTableName(topic);

				// sending the converted byte array as a row to a table (type) as an object to AMI
				getAmiRelayIn().onObject(-1, id, type, 0, b);
			} catch (Exception e) {
				LH.warning(log, "Error for ", value, " ==> ", e);
			}
		}

	}

	protected Map<String, Object> processMessage(Map<String, Object> parts, String topic2) {
		return parts;
	}

	private Properties setProperties() {
		Properties kafkaProps = new Properties();

		for (String prop : this.props.getKeys()) {
			if (amiProps.contains(prop))
				continue;
			String propValue = this.props.getOptional(prop, "");
			if (SH.is(propValue))
				kafkaProps.put(prop, propValue);
		}

		return kafkaProps;
	}
}