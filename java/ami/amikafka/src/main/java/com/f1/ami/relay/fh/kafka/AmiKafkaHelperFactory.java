package com.f1.ami.relay.fh.kafka;

public class AmiKafkaHelperFactory {
	
	//mapping helper classes to deserializer classes
	public AmiKafkaHelper getKafkaHelper (String className) {
		switch (className) {
		case "io.confluent.kafka.serializers.KafkaJsonDeserializer":
			return new AmiKafkaHelperJson();
		case "io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer":
			return new AmiKafkaHelperAvro();
		case "io.confluent.kafka.serializers.KafkaAvroDeserializer":
			return new AmiKafkaHelperProtobuf();
		default:
			return null;
		}
	}
}
