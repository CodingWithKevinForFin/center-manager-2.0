package amiavro;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import amiavro.example2.Trade;
import amiavro.example2.User;

public class AvroTest {

	public static List<User> generateSampleData() {
		User user1 = new User();
		user1.setName("Alyssa");
		user1.setFavoriteNumber(256);
		// Leave favorite color null

		// Alternate constructor
		User user2 = new User("Ben", 7, "red");

		// Construct via builder
		User user3 = User.newBuilder().setName("Charlie").setFavoriteColor("blue").setFavoriteNumber(null).build();

		List<User> users = new ArrayList<User>();
		users.add(user1);
		users.add(user2);
		users.add(user3);

		return users;
	}
	public static void generateSampleData3() {
		Random rand = new Random();

		Schema schema = null;
		try {
			schema = new Schema.Parser().parse(new File("trade.avsc"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<GenericRecord> records = new ArrayList<GenericRecord>();

		String sym = "AAPL";
		long t = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000);

		double px = 119.90;
		//		for (int i = 0; i < 100000; i++) {
		//			GenericRecord record = new GenericData.Record(schema);
		//			record.put("sym", sym);
		//			record.put("time", t + i * 100);
		//			px += rand.nextDouble() - 0.5;
		//			record.put("px", px);
		//			records.add(record);
		//		}

		DatumWriter<GenericRecord> userDatumWriter = new GenericDatumWriter<GenericRecord>(schema);
		DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(userDatumWriter);
		//		Schema schema = User.getClassSchema();

		schema = Trade.getClassSchema();

		try {
			dataFileWriter.create(schema, new File("tradesmed.avro"));
			for (int i = 0; i < 100000; i++) {
				GenericRecord record = new GenericData.Record(schema);
				record.put("sym", sym);
				record.put("time", t + i * 100);
				px += rand.nextDouble() - 0.5;
				record.put("px", px);
				dataFileWriter.append(record);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (dataFileWriter != null)
				try {
					dataFileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public static List<GenericRecord> generateSampleData2() {
		Schema schema = null;
		try {
			schema = new Schema.Parser().parse(new File("user3.avsc"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//		Using this schema, let's create some users.

		GenericRecord user1 = new GenericData.Record(schema);
		List<Integer> numbers = new ArrayList<Integer>();
		numbers.add(0);
		numbers.add(1);
		numbers.add(2);
		numbers.add(3);
		numbers.add(4);
		List<String> msgs = new ArrayList<String>();
		msgs.add("hello");
		msgs.add("wolld");

		user1.put("name", "Alyssa");
		user1.put("favorite_number", 256);
		user1.put("numbers", numbers);
		user1.put("msgs", msgs);
		user1.put("mybool", true);
		user1.put("myint", 123);
		user1.put("mylong", System.currentTimeMillis());
		user1.put("myfloat", 0.55f);
		user1.put("mydouble", 22.55);
		ByteBuffer wrap = ByteBuffer.wrap("\u00FF".getBytes(Charset.defaultCharset()));
		user1.put("mybytes", wrap);
		user1.put("mystring", "hello world");
		byte[] bytes = "0123456789abcdef".getBytes();
		GenericFixed fix = new GenericData.Fixed(schema.getField("myfixed").schema(), bytes);
		user1.put("myfixed", fix);
		user1.put("myenum", new GenericData.EnumSymbol(schema.getField("myenum").schema(), "SPADES"));

		Map<String, Long> msl = new HashMap<String, Long>();
		msl.put("a", 1L);
		msl.put("b", 10L);
		msl.put("c", System.currentTimeMillis());
		Map<String, String> mss = new HashMap<String, String>();
		mss.put("a", "hello");
		mss.put("b", "world");
		user1.put("mymap", mss);
		user1.put("mymap2", msl);

		Record ir = new Record(schema.getField("myrecord").schema());
		ir.put("a", System.currentTimeMillis());
		ir.put("b", "record");
		user1.put("myrecord", ir);

		// Leave favorite color null

		GenericRecord user2 = new GenericData.Record(schema);
		user2.put("name", "Ben");
		user2.put("favorite_number", 7);
		user2.put("favorite_color", "red");
		user2.put("mybool", false);
		user2.put("myfixed", fix);
		user2.put("myenum", new GenericData.EnumSymbol(schema.getField("myenum").schema(), "SPADES"));
		user2.put("myrecord", ir);

		List<GenericRecord> users = new ArrayList<GenericRecord>();
		users.add(user1);
		users.add(user2);
		//		users.add(user3);

		return users;
	}
	public static void writeSerializeData(List<User> users) {
		DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
		DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter);
		Schema schema = User.getClassSchema();

		try {
			dataFileWriter.create(schema, new File("users.avro"));
			for (User user : users) {
				dataFileWriter.append(user);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (dataFileWriter != null)
				try {
					dataFileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public static void writeSerializeData2(List<GenericRecord> users) {
		Schema schema = null;
		try {
			schema = new Schema.Parser().parse(new File("user3.avsc"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DatumWriter<GenericRecord> userDatumWriter = new GenericDatumWriter<GenericRecord>(schema);
		DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(userDatumWriter);
		//		Schema schema = User.getClassSchema();

		try {
			dataFileWriter.create(schema, new File("users2.avro"));
			for (GenericRecord user : users) {
				dataFileWriter.append(user);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (dataFileWriter != null)
				try {
					dataFileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	public static void readDeserializeData() {
		File file = new File("users.avro");
		// Deserialize Users from disk
		DatumReader<User> userDatumReader = new SpecificDatumReader<User>(User.class);
		DataFileReader<User> dataFileReader;
		try {
			dataFileReader = new DataFileReader<User>(file, userDatumReader);
			// TODO Auto-generated catch block
			User user = null;
			while (dataFileReader.hasNext()) {
				// Reuse user object by passing it to next(). This saves us from
				// allocating and garbage collecting many objects for files with
				// many items.
				user = dataFileReader.next(user);
				System.out.println(user);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void genericDeserializeData() {
		File file = new File("users.avro");
		// Deserialize users from disk
		//		Schema schema = User.getClassSchema();
		try {
			Schema schema = new Schema.Parser().parse(new File("user.avsc"));
			DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>(schema);
			DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
			GenericRecord user = null;
			while (dataFileReader.hasNext()) {
				// Reuse user object by passing it to next(). This saves us from
				// allocating and garbage collecting many objects for files with
				// many items.
				user = dataFileReader.next(user);
				System.out.println(user);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		generateSampleData3();
		//		List<GenericRecord> users = generateSampleData3();
		//		writeSerializeData2(users);
		//		genericDeserializeData();

	}

}
