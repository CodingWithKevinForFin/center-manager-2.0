package chroniclequeuedemo;

import java.util.HashMap;

import com.f1.ami.client.AmiClient;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;

import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.wire.DocumentContext;
import net.openhft.chronicle.wire.Wire;

public class Consumer {
	private static final String SAMPLE_TABLE = "Orders";
	private String appId;
	private String file;
	private String host;
	private int port;
	private AmiClient client;

	public static void main(String[] args) {
		//		String path = "C:/Users/david/Documents/Test/Data";
		String file = args[0];
		String host = args[1];
		int port = Caster_Integer.INSTANCE.castOr(args[2], 3289);
		Consumer c = new Consumer("Consumer", file, host, port);
		c.start();
	}

	public Consumer(String appId, String file, String host, int port) {
		this.appId = appId;
		this.file = file;
		this.host = host;
		this.port = port;
		this.client = new AmiClient();
	}

	public void start() {
		System.out.println("\nConnecting to AMI");
		this.client.start(this.host, this.port, this.appId, AmiClient.ENABLE_QUIET);
		System.out.println("\nWaiting for messages");
		try (ChronicleQueue queue = ChronicleQueue.singleBuilder(this.file).build()) {
			final ExcerptTailer tailer = queue.createTailer();
			while (true) {
				readOrder(tailer);
			}
		}

	}

	private void readOrder(final ExcerptTailer tailer) {
		try (final DocumentContext dc = tailer.readingDocument()) {
			if (dc.isPresent()) {
				Wire wire = dc.wire();
				//Read Row From ChronicleQueue
				HashMap<Object, Object> values = new HashMap<Object, Object>();
				wire.readAllAsMap(Object.class, Object.class, values);
				//Write to AMI
				this.writeToAmiClient(values);

				//				System.out.println(ObjectToJsonConverter.INSTANCE_CLEAN.objectToString(values));
			}
			//			else
			//				Jvm.pause(50);

		}
	}
	private void writeToAmiClient(HashMap<Object, Object> values) {
		//		this.client.startObjectMessage(SAMPLE_TABLE, SH.s(System.currentTimeMillis()));
		this.client.startObjectMessage(SAMPLE_TABLE, null);
		for (Object key : values.keySet()) {
			String key_ami = SH.s(key);
			this.client.addMessageParamObject(key_ami, values.get(key));
		}
		this.client.sendMessageAndFlush();
	}

}
