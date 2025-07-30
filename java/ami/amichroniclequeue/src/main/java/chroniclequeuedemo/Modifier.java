package chroniclequeuedemo;

import java.util.HashMap;

import com.f1.utils.EH;

import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.wire.DocumentContext;
import net.openhft.chronicle.wire.Wire;

public class Modifier {

	private String appId;
	private String inFile;
	private String outFile;
	private String newColName;
	private int counter = 0;

	public static void main(String[] args) {
		//		String path = "C:/Users/david/Documents/Test/Data";
		String inFile = args[0];
		String outFile = args[1];
		IOTools.deleteDirWithFiles(outFile);
		String appId = args[2];
		String newColName = args[3];

		Modifier m = new Modifier(appId, inFile, outFile, newColName);
		m.start();

	}
	public Modifier(String appId, String inFile, String outFile, String newColName) {
		this.appId = appId;
		this.inFile = inFile;
		this.outFile = outFile;
		this.newColName = newColName;

	}
	public void start() {
		System.out.println("\nWaiting for messages");
		try (ChronicleQueue queueIn = ChronicleQueue.singleBuilder(this.inFile).build()) {
			try (ChronicleQueue queueOut = ChronicleQueue.singleBuilder(this.outFile).build()) {
				final ExcerptTailer tailer = queueIn.createTailer();
				final ExcerptAppender appender = queueOut.acquireAppender();
				while (true) {
					readOrder(tailer, appender);
				}
			}
		}

	}

	private void readOrder(final ExcerptTailer tailer, final ExcerptAppender appender) {
		try (final DocumentContext dc = tailer.readingDocument()) {
			if (dc.isPresent()) {
				Wire wire = dc.wire();
				//Read Row From ChronicleQueue
				HashMap<Object, Object> values = new HashMap<Object, Object>();
				wire.readAllAsMap(Object.class, Object.class, values);
				//Write to AMI
				writeOrder(appender, values);
				counter++;

				//				System.out.println(ObjectToJsonConverter.INSTANCE_CLEAN.objectToString(values));
			}
			//			if ("Modifier2".equals(this.appId) && counter % 215 == 0) {
			//				Jvm.pause(75);
			//			}
			//						else
			//				Jvm.pause(50);

		}
	}
	public void writeOrder(final ExcerptAppender appender, HashMap<Object, Object> values) {
		try (final DocumentContext dc = appender.writingDocument()) {
			Wire wire = dc.wire();
			values.put(this.newColName, EH.currentTimeNanos());
			values.put(Producer.KEY_APPID, this.appId);
			wire.writeAllAsMap(Object.class, Object.class, values);
			//			System.out.println(ObjectToJsonConverter.INSTANCE_CLEAN.objectToString(values));
			long indexWritten = dc.index();
		}
	}
}
