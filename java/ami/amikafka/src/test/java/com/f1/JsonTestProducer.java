package com.f1;

import java.time.LocalTime;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTestProducer {

	private KafkaProducer<String, User> producer;
	private String ticks[] = new String[] { "GOOG", "AAPL", "MIR", "AMZN", "META", "CRM", "KEX", "WMT", "NFLX", "NVDA", "MSFT" };

	private final static Random random = new Random();

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
		this.producer = new KafkaProducer<String, User>(props);

		while (true) {
			String key = UUID.randomUUID().toString();
			String fName = getRandomName();
			String lName = getRandomName();
			double price = getRandomPrice();
			double size = getRandomSize();

			User user = new User(fName, lName, (short) random.nextInt(), random.nextLong());

			//send the message
			this.send(topics[0], key, user);
			Thread.sleep(10000);
		}
	}

	public void shutdown() throws Exception {
		this.producer.flush();
		this.producer.close();
	}

	Properties setProperties() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "fire.3forge.net:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonSerializer");
		props.put("group.id", "test-group");

		props.put("schema.registry.url", "fire.3forge.net:8081");

		return props;

	}

	void send(String topic, String key, User user) {
		ProducerRecord<String, User> record = new ProducerRecord<String, User>(topic, key, user);
		producer.send(record);
	}

	public static void main(String[] args) throws Exception {

		args = new String[1];
		args[0] = "testTopic";

		if (args.length < 1) {
			System.out.println("********pls specify topic(s)");
			return;
		}

		ObjectMapper mapper = new ObjectMapper();
		User user = new User("Anand", "Doshi", (short) 20, 11L);
		String json = mapper.writeValueAsString(user);
		System.out.println(json);

		Object deserializedJson = mapper.readValue(json, Object.class);
		Map<String, Object> map = (Map<String, Object>) deserializedJson;

		AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " -> " + entry.getValue().toString());
			map.replace(entry.getKey(), entry.getValue().toString());
		}
		System.out.println(converter.toBytes(map));

		new JsonTestProducer().runAlways(args);
	}

	public static class User {
		@JsonProperty
		public String firstName;
		@JsonProperty
		public String lastName;
		@JsonProperty
		public short age;
		@JsonProperty
		public long id;
		@JsonProperty
		public Name fullName;

		public User() {
		}

		public User(String firstName, String lastName, short age, long id) {
			this.age = age;
			this.firstName = firstName;
			this.lastName = lastName;
			this.id = id;
			this.fullName = new Name(firstName, lastName);
		}
	}

	public static class Name {
		@JsonProperty
		private final String firstName;
		@JsonProperty
		private final String lastName;

		public Name(String firstName, String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}

		@Override
		public String toString() {
			return lastName + ", " + firstName;
		}
	}
}
