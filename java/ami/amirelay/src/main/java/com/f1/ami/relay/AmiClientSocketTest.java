package com.f1.ami.relay;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.NullOutputStream;
import com.f1.utils.StreamPiper;

public class AmiClientSocketTest implements Runnable {

	private static final String[] VENUE = new String[] { "BATS", "NYSE", "ARCA", "NASD", "CBOE", "CHX", "CME" };
	private static final int DEFAULT_PORT = 3289;

	public AmiClientSocketTest(String string) {
		this.name = string;
	}

	public static void main(String a[]) throws Exception {

		new AmiClientSocketTest("TEST").run();
		final Socket socket = new Socket("cinder", DEFAULT_PORT);
		try {
			final PrintStream out = new PrintStream(socket.getOutputStream());
			out.println("L|I=\"Hand Break2\"|O=\"QUIET\"");
			out.println("A|T=\"HIGH_VOL\"|I=\"ALERT2\"|L=5|msg=\"unexpectedly high volume for MSFT\"");
			out.println("O|I=\"exec100\"|T=\"EXEC\"|A=\"ALERT2\"|sym=\"MSFT\"|qty=100|px=50.6");
			out.println("O|I=\"exec102\"|T=\"EXEC\"|A=\"ALERT2\"|sym=\"IBMT\"|qty=200|px=52.6");
			out.println("O|I=\"exec103\"|T=\"EXEC\"|A=\"ALERT2\"|sym=\"ASDT\"|qty=500|px=33.6");
			out.println("O|I=\"exec104\"|T=\"EXEC\"|A=\"ALERT2\"|sym=\"FFAT\"|qty=200|px=45.6");
			out.println("O|I=\"exec107\"|T=\"EXEC\"|A=\"ALERT2\"|sym=\"MKDT\"|qty=500|px=70.6");
			out.flush();
			//do more stuff ;)
		} finally {
			try {
				socket.close();
			} catch (Exception ex) {
			}
		}
	}

	private String name;

	@Override
	public void run() {

		Random r = new Random(123);
		String STOCKS[] = new String[10000];
		Set<String> stocks = new HashSet<String>();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < STOCKS.length;) {
			int length = 1 + r.nextInt(4);
			while (length-- > 0) {
				sb.append((char) ('A' + r.nextInt(26)));
			}
			String stock = sb.toString();
			if (stocks.add(stock))
				STOCKS[i++] = stock;
			sb.setLength(0);

		}

		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream("/tmp/ami.txt");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			System.out.println(new Date() + ":START");
			Socket socket = new Socket("localhost", DEFAULT_PORT);
			OutputStream out = new FastBufferedOutputStream(socket.getOutputStream(), 10000);
			new Thread(new StreamPiper(new FastBufferedInputStream(socket.getInputStream()), new NullOutputStream(), 1024)).start();

			out.write(("L|I=\"" + name + "\"|O=\"QUIET\"\n").getBytes());
			int orders = 0, exec = 0;
			long next = System.currentTimeMillis() + 200;
			//top 960000
			//bot 975000
			int i = 0;
			while (i < 1000 * 1000 * 50) {
				//while (i <= 915) {
				if (i % 10000 == 0) {
					long now = System.currentTimeMillis();
					System.out.println(now + ": " + i);
					long sleep = next - now;
					next = next + 200;
					if (sleep > 0)
						Thread.sleep(sleep);
				}
				float price = r.nextInt(10000) / 100f;
				int qty = 100 * r.nextInt(10000) * 100;
				String symbol = STOCKS[r.nextInt(STOCKS.length)];
				String venue = VENUE[r.nextInt(VENUE.length)];
				//if (i > 914)
				String msg = "O|T=\"MarketData\"|Venue='" + venue + "'|Symbol='" + symbol + "'|Price=" + price + "|exectime=" + System.currentTimeMillis() + "L|qty=" + qty + "\n";
				fileOut.write(msg.getBytes());
				out.write(msg.getBytes());

				out.flush();
				i++;
				orders += 5;
				if (orders % 20 == 0)
					exec = exec + r.nextInt(200);
			}

			//System.out.println():

			System.out.println(new Date() + ":DONE");
			System.exit(0);
		} catch (Exception e) {

		}
		//AmiClient am = new AmiClient();
		//am.connect("localhost", DEFAULT_PORT);
		//am.sendLogin("App123", null);
		//am.sendObject("Order", "123", null, null);

	}
}
