package com.f1;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class AvroTestProducer {
	private KafkaProducer<String, GenericRecord> producer;
	private String ticks[] = new String[] { "GOOG", "AAPL", "MIR", "AMZN", "META", "CRM", "KEX", "WMT", "NFLX", "NVDA", "MSFT" };

	private final static String SCHEMA_DIR = "C:\\Users\\XPS17-3\\Downloads\\amikafkaavro\\kafkafh-avro\\src\\main\\resources\\";
	private final static String SIMPLE_MESSAGE_SCHEMA_PATH = SCHEMA_DIR + "SimpleMessage.avsc";
	private final static String ORDER_SCHEMA_PATH = SCHEMA_DIR + "Order.avsc";
	private final static String EXECUTION_SCHEMA_PATH = SCHEMA_DIR + "Execution.avsc";

	private final static Random random = new Random();

	Properties setProperties() {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "ec2-44-195-149-134.compute-1.amazonaws.com:9092");
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		//		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		properties.put("schema.registry.url", "http://ec2-44-195-149-134.compute-1.amazonaws.com:8081");

		return properties;
	}

	private String getRandomName() {
		return ticks[new Random().nextInt(ticks.length)];
	}

	private double getRandomPrice() {
		double r = random.nextDouble() * 1000;
		return Double.parseDouble(String.format("%.2f", r));
	}

	private double getRandomSize() {
		double r = random.nextDouble() * 100;
		return Double.parseDouble(String.format("%.2f", r));
	}

	private long timestamp() {
		return LocalTime.now().toNanoOfDay();
	}

	public void runAlways(String... topics) throws Exception {
		Properties props = setProperties();
		this.producer = new KafkaProducer<String, GenericRecord>(props);
		Schema orderSchema = setUpSchema(ORDER_SCHEMA_PATH);
		Schema executionSchema = setUpSchema(EXECUTION_SCHEMA_PATH);

		while (true) {
			String key = UUID.randomUUID().toString();
			String name = getRandomName();
			double price = getRandomPrice();
			double size = getRandomSize();

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("id", key);
			params.put("name", name);
			params.put("price", price);
			params.put("size", size);

			//send the message
			this.send(topics[0], key, prepareRecord(orderSchema, params));
			Thread.sleep(10);

			params.clear();

			params.put("order_id", key);
			params.put("time", timestamp());

			this.send(topics[1], key, prepareRecord(executionSchema, params));
			Thread.sleep(10);
		}
	}

	Schema setUpSchema(String path) {
		String schemaStr = "";
		try {
			//            Path path = Paths.get("/mnt/c/Users/Kim Akius/Desktop/3forge/kafka_src/src/main/avro/SimpleMessage.avsc");

			schemaStr = readFile(path, StandardCharsets.US_ASCII);
		} catch (IOException e) {
			System.err.println("Schema file not found.");
		}

		Schema.Parser parser = new Schema.Parser();
		return parser.parse(schemaStr);
	}

	GenericRecord prepareRecord(Schema schema, Map<String, Object> params) {
		GenericRecord record = new GenericData.Record(schema);
		for (Map.Entry<String, Object> e : params.entrySet())
			record.put(e.getKey(), e.getValue());
		return record;
	}

	void send(String topic, String key, GenericRecord record) {
		ProducerRecord<String, GenericRecord> producerRecord = new ProducerRecord<String, GenericRecord>(topic, key, record);
		producer.send(producerRecord);
	}

	public void shutdown() throws Exception {
		this.producer.flush();
		this.producer.close();
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("********pls specify topic(s)");
			return;
		}

		new AvroTestProducer().runAlways(args);
	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

}
