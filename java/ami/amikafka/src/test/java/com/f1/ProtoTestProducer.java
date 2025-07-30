package com.f1;

import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import com.f1.test.gen.Person;
import com.f1.test.gen.TruePerson;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;



public class ProtoTestProducer {

    private KafkaProducer<String, Person> personProducer;
    private KafkaProducer<String, TruePerson> addressProducer;
    private String firstNames[] = new String[] { "Mike", "El", "Will", "Dustin", "Lucas", "Max", "Suzie", "Jonathan", "Nancy", "Steve", "Robin" };
    private String lastNames[] = new String[] {"Wheeler", "Hopper", "Byers", "Henderson", "Sinclair", "Mayfield", "Bingham", "Harrington", "Buckley"};

    private static Properties setProperties() {
        Properties props = new Properties();

        // set producer properties
        //		props.put("group.id", "test-kimbo-group");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "ec2-44-202-70-244.compute-1.amazonaws.com:9092");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put("schema.registry.url", "http://ec2-44-202-70-244.compute-1.amazonaws.com:8081");

        return props;
    }

    public void runAlways(String topic) throws Exception {
        if (this.personProducer == null || this.addressProducer == null) {
            Properties props = setProperties();
            this.personProducer = new KafkaProducer<>(props);
            this.addressProducer = new KafkaProducer<>(props);
        }

        while (true) {
            Random rand = new Random();

            Person.PhoneNumber phone = Person.PhoneNumber.newBuilder().setNumber(UUID.randomUUID().toString()).build();

            String key = UUID.randomUUID().toString();

            String firstName = firstNames[rand.nextInt(firstNames.length)];
            String lastName = lastNames[rand.nextInt(lastNames.length)];

            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com";

            int id = rand.nextInt(1234);

            String p = String.format("%.2f", rand.nextDouble() * 6);
            double height = Double.parseDouble(p);

            Person person = Person.newBuilder().addPhones(phone).setName(firstName + " " + lastName).setEmail(email)
                    .setHeight(height).setId(id).build();

            boolean isHidden = rand.nextBoolean();

            TruePerson truePerson = TruePerson.newBuilder().setPerson(person).setIsHidden(isHidden).build();

            //send the message
            this.send(topic, key, person);
            Thread.sleep(10);

            String orderTopic = "true-" + topic;
            this.send(orderTopic, key, truePerson);

            Thread.sleep(100);
        }
    }

    protected void send(String topic, String key, Person message) {
        ProducerRecord<String, Person> data = new ProducerRecord<String, Person>(topic, key, message);
        this.personProducer.send(data);
    }

    protected void send(String topic, String key, TruePerson message) {
        ProducerRecord<String, TruePerson> data = new ProducerRecord<String, TruePerson>(topic, key, message);
        this.addressProducer.send(data);
    }

    public void shutdown() throws Exception {
        this.personProducer.close();
        this.addressProducer.close();
    }

    public static void main(String[] args) throws Exception {
        ProtoTestProducer producer = new ProtoTestProducer();

        producer.runAlways(args[0]);
    }
}
