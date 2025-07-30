package chroniclequeuedemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jetbrains.annotations.Nullable;
import org.python.jline.internal.InputStreamReader;

import com.f1.utils.EH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;

import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.wire.DocumentContext;
import net.openhft.chronicle.wire.Wire;

public class Producer {
	private static final String KEY_TIME = "time";
	private static final String KEY_ORDERID = "orderId";
	private static final String KEY_SYM = "sym";
	private static final String KEY_SIDE = "side";
	private static final String KEY_PX = "px";
	private static final String KEY_QTY = "qty";
	private static final String KEY_REGION = "region";
	private static final String KEY_ACCOUNT = "account";
	private static final String KEY_STRATEGY = "strategy";
	public static final String KEY_APPID = "appId";
	private String appId;
	private String file;
	private Random rand;
	private long nextId;
	private BufferedReader br;
	private List<String> accounts = new ArrayList<String>(
			Arrays.asList("Sally McCoven", "Alex Miller", "Brian Davis", "Sally Jones", "Jenn Brown", "Johnathan Smith", "Jacob Clark"));
	private List<String> strategies = new ArrayList<String>(Arrays.asList("VWAP", "TWAP", "SLICER"));
	private List<String> symbols = new ArrayList<String>(Arrays.asList("AAPL", "CISCO", "MSFT", "IBM", "DELL", "TWL", "AMZN", "BABA", "INTC", "AMD", "QCOM", "AAL"));
	private List<String> regions = new ArrayList<String>(Arrays.asList("NA", "EU", "AS", "SA"));

	public static void main(String[] args) {
		//		String path = "C:/Users/david/Documents/Test/Data";
		String file = args[0];
		IOTools.deleteDirWithFiles("C:/Users/david/Documents/Test/");
		IOTools.deleteDirWithFiles(file);
		Producer p = new Producer("Producer", file);
		try {
			p.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//Number of Messages to Produce Long
	//Message Rate Double
	public Producer(String appId, String file) {
		this.rand = new Random(0);
		this.nextId = 0;
		this.file = file;
		this.appId = appId;
		this.br = new BufferedReader(new InputStreamReader(System.in));
	}

	public void start() throws IOException {
		System.out.println("\nHit blank line to add an order, ## to add n orders(s), exit to exit");
		try (ChronicleQueue queue = ChronicleQueue.singleBuilder(this.file).build()) {
			final ExcerptAppender appender = queue.acquireAppender();

			int ordersToAdd = 0;
			while (true) {
				String line = this.br.readLine();

				if ("".equals(line)) {
					ordersToAdd = 1;
				} else if ("exit".contentEquals(line)) {
					break;
				} else
					ordersToAdd = Caster_Integer.INSTANCE.castOr(line, 0);

				for (int i = 0; i < ordersToAdd; i++) {
					writeOrderMap(appender);
				}
				ordersToAdd = 0;
			}
			queue.close();
		}
	}

	public void writeOrder(final ExcerptAppender appender) {
		try (final DocumentContext dc = appender.writingDocument()) {
			@Nullable
			Wire wire = dc.wire();
			wire.write(KEY_TIME).int64(now());
			wire.write(KEY_ORDERID).text(generateOrderId());
			wire.write(KEY_SYM).text(generateSym());
			wire.write(KEY_SIDE).text(generateSide());
			wire.write(KEY_PX).float64(generatePx());
			wire.write(KEY_QTY).int32(generateQty());

			wire.write(KEY_REGION).text(generateRegion());
			wire.write(KEY_ACCOUNT).text(generateAccount());
			wire.write(KEY_STRATEGY).text(generateStrategy());
			wire.write(KEY_APPID).text(this.appId);
			long indexWritten = dc.index();
		}
	}
	public Map<String, Object> writeOrderMap(final ExcerptAppender appender) {
		try (final DocumentContext dc = appender.writingDocument()) {
			@Nullable
			Wire wire = dc.wire();
			HashMap<String, Object> order = new HashMap<String, Object>();
			order.put(KEY_TIME, now());
			order.put(KEY_ORDERID, generateOrderId());
			order.put(KEY_SYM, generateSym());
			order.put(KEY_SIDE, generateSide());
			order.put(KEY_PX, generatePx());
			order.put(KEY_QTY, generateQty());

			order.put(KEY_REGION, generateRegion());
			order.put(KEY_ACCOUNT, generateAccount());
			order.put(KEY_STRATEGY, generateStrategy());
			order.put(KEY_APPID, this.appId);
			wire.writeAllAsMap(String.class, Object.class, order);
			//			System.out.println(ObjectToJsonConverter.INSTANCE_CLEAN.objectToString(order));
			long indexWritten = dc.index();
			return order;
		}

	}

	public long now() {
		return EH.currentTimeNanos();
	}

	public String generateOrderId() {
		return "ID-" + SH.rightAlign('0', SH.s(nextId++), 12, false);
	}

	public String generateSym() {
		return (String) randGuassianObject(this.symbols);
	}

	public String generateSide() {
		return rand.nextBoolean() ? "BUY" : "SELL";
	}

	public double generatePx() {
		return rand.nextDouble() * 1000.0;
	}

	public int generateQty() {
		return rand.nextInt(1000) + 1;
	}
	public String generateRegion() {
		return (String) randGuassianObject(this.regions);
	}
	public String generateAccount() {
		return (String) randGuassianObject(this.accounts);
	}
	public String generateStrategy() {
		return (String) randGuassianObject(this.strategies);
	}

	public Object randGuassianObject(List items) {
		int sz = items.size();
		double m = sz / 2;
		double v = sz / 3.4;
		int r = (int) (m + rand.nextGaussian() * v);

		if (r < 0) {
			//			System.out.println(r);
			r = 0;
		}
		if (r >= sz) {
			//			System.out.println(r);
			r = sz - 1;
		}
		return items.get(r);
	}
}
