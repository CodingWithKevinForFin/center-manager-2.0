package com.f1.ami.one;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.net.URI;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.f1.ami.amicommon.AmiCommonProperties;
import com.f1.ami.amicommon.AmiStartup;
import com.f1.ami.amicommon.rest.AmiRestServer;
import com.f1.ami.center.AmiCenterMain;
import com.f1.ami.relay.AmiRelayMain;
import com.f1.ami.relay.AmiRelayProperties;
import com.f1.ami.web.AmiWebMain;
import com.f1.ami.web.AmiWebProperties;
import com.f1.ami.webbalancer.AmiWebBalancerMain;
import com.f1.ami.webmanager.AmiWebManagerMain;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.bootstrap.F1Constants;
import com.f1.suite.web.HttpWebSuite;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.CaseInsensitiveHasher;

public class AmiOneMain {

	public static void main(String[] a) throws Exception {
		String javaVersion = System.getProperty("java.version");
		boolean tlscompat = versionCheck(javaVersion);
		if (System.getProperty("https.protocols") == null)
			if (tlscompat) {
				System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");
			} else {
				System.setProperty("https.protocols", "TLSv1.2");
			}
		boolean showSplash = "true".equals(System.getProperty("showsplash"));
		JFrame frame = null;
		if (showSplash) {
			try {
				frame = showSplash();
			} catch (Throwable e) {
			}
		}
		ContainerBootstrap cb = new ContainerBootstrap(AmiOneMain.class, a);
		boolean exit = false;
		try {
			main2(cb);
			if (showSplash)
				browse(cb);
		} catch (Exception e) {
			e.printStackTrace();
			String pst = SH.printStackTrace(e);
			if (pst.contains("could not roll") || pst.contains("socket at port")) {
				try {
					System.err.println("Already running (" + e.getMessage() + ") ... Just Launching a browser");
					exit = true;
					browse(cb);
				} catch (Exception e2) {
					e2.printStackTrace();
					throw e;
				}
			} else
				throw e;
		} finally {
			try {
				if (frame != null)
					frame.dispose();
			} catch (Throwable t) {
			}
		}
		if (exit) {
			OH.sleep(15000);
			System.err.println("Exiting....");
			System.exit(0);
		}
	}
	private static boolean versionCheck(String javaVersion) {
		try {
			if (javaVersion.startsWith("1.")) {
				String[] parts = javaVersion.split("\\.");
				int version = Integer.parseInt(parts[1]);
				return version >= 11;
			} else {
				int version = Integer.parseInt(javaVersion.split("\\.")[0]);
				return version >= 11;
			}
		} catch (NumberFormatException e) {
			System.err.println("Unknown java version: " + javaVersion);
			return false;
		}
	}
	public static void main2(ContainerBootstrap cb) throws Exception {
		cb.setProperty(F1Constants.PROPERTY_APPNAME, "AmiOne");
		cb.setProperty(F1Constants.PROPERTY_LOGFILENAME, "AmiOne");
		cb.setProperty(HttpWebSuite.ATTRIBUTE_WEB_TITLE, "3forge AMI");
		cb.setProperty(F1Constants.PROPERTY_AUTOCODED_DISABLED, "true");
		cb.setProperty(AmiRelayProperties.OPTION_AMI_RELAY_INVOKABLES,
				"com.f1.ami.relay.plugins.AmiRelayInvokablePlugin_LoadFile,com.f1.ami.relay.plugins.AmiRelayInvokablePlugin_Replay");
		cb.setProperty("sso.key.portlet.layout", "portletlayout_ami");
		cb.setProperty("sso.namespace", "AMI");
		cb.setProperty(AmiCommonProperties.PROPERTY_AMI_CENTER_HOST, "localhost");
		cb.setProperty(AmiRelayProperties.OPTION_AMI_RELAY_FH_ACTIVE, "ssocket");
		cb.setProperty(AmiRelayProperties.OPTION_AMI_RELAY_FH_SSOCKET_START, "true");
		cb.setProperty(AmiRelayProperties.OPTION_AMI_RELAY_FH_SSOCKET_CLASS, "com.f1.ami.relay.fh.AmiServerSocketFH");
		cb.setProperty(AmiRelayProperties.OPTION_AMI_RELAY_FH_SSOCKET_PROPS_AMIID, "Server_Socket");
		cb.setProperty(AmiRelayProperties.OPTION_AMI_RELAY_ID, "relay_0");
		cb.setProperty(AmiRelayProperties.OPTION_AMI_PORT, "3289");
		cb.setProperty(AmiWebProperties.PROPERTY_STYLE_FILES, "data/styles/*.amistyle.json");
		AmiStartup.startupAmi(cb, "ami_amione");
		AmiRestServer restServer = AmiRestServer.create(cb);
		if (restServer != null)
			cb.addContainerService(AmiRestServer.SERVICE_ID, restServer);
		Set<CharSequence> components = new HasherSet<CharSequence>(CaseInsensitiveHasher.INSTANCE,
				SH.splitToSet(",", cb.getProperties().getOptional(AmiCommonProperties.PROPERTY_AMI_COMPONENTS, "relay,center,web")));
		if (components.remove("webmanager"))
			AmiWebManagerMain.main2(cb);
		if (components.remove("webbalancer"))
			AmiWebBalancerMain.main2(cb);
		if (components.remove("web"))
			AmiWebMain.main2(cb);
		if (restServer != null && restServer.getServer() == null)
			restServer.initHttpServer();
		if (components.remove("center"))
			AmiCenterMain.main2(cb);
		if (components.remove("relay"))
			AmiRelayMain.main2(cb);
		if (!components.isEmpty())
			throw new RuntimeException(
					"Invalid option for " + AmiCommonProperties.PROPERTY_AMI_COMPONENTS + ": " + components + "   (valid components are webmanager,webbalancer,web,center,relay)");
		AmiStartup.startupComplete(cb);
	}
	private static void browse(ContainerBootstrap cb) {
		final String httpPort = cb.getProperties().getOptional(AmiWebProperties.PROPERTY_HTTP_PORT, Caster_String.INSTANCE);
		final Integer httpsPort = cb.getProperties().getOptional(AmiWebProperties.PROPERTY_HTTPS_PORT, Caster_Integer.INSTANCE);
		if (httpPort != null)
			browse(false, SH.parseInt(SH.beforeFirst(httpPort, ",", httpPort)));
		if (httpsPort != null)
			browse(true, httpsPort);
	}

	private static void browse(final boolean secure, final int port) {
		final String url = secure ? "https://localhost:" : "http://localhost:";
		Thread t = new Thread() {
			public void run() {
				try {
					if (Desktop.isDesktopSupported()) {
						Desktop desktop = Desktop.getDesktop();
						desktop.browse(new URI(url + port));
					} else {
						Runtime runtime = Runtime.getRuntime();
						runtime.exec("xdg-open " + url + port);
					}
					System.out.println("Opened browser to: " + url);
				} catch (Exception e) {
					System.err.println("Failed to browse to: " + url);
					e.printStackTrace(System.err);
				}
			}
		};
		t.setDaemon(true);
		t.setName("browser");
		t.start();
	}
	private static JFrame showSplash() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		final JFrame frame = new JFrame("Starting up AMI");
		final JLabel label = new JLabel("", SwingConstants.LEFT) {
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

				super.paintComponent(g2d);
			}
		};
		label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		label.setText("<html>Launching AMI One in Browser");
		frame.add(label);
		new Thread() {
			public void run() {
				int cnt = 0;
				while (label.isVisible()) {
					OH.sleep(100);
					cnt++;
					label.setText("<html>&nbsp;&nbsp;&nbsp;Launching AMI One in your browser" + SH.repeat('.', cnt % 30));
				}
			}

		}.start();

		frame.pack();
		frame.getContentPane().setBackground(Color.WHITE);
		label.setForeground(new Color(12, 74, 97));
		frame.setSize(600, 200);
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
		frame.setVisible(true);
		return frame;
	}
}
