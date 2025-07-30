package com.f1.ami.relay.fh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.plugins.AmiRelayInvokablePlugin;
import com.f1.console.impl.TelnetShellConnection;
import com.f1.console.impl.shell.UserShell;
import com.f1.console.impl.shell.UserShellCtrlBreakListener;
import com.f1.utils.AH;
import com.f1.utils.EH;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.concurrent.FastQueue;

public class AmiSocketFH extends AmiFHMessageParser implements Runnable, UserShellCtrlBreakListener {
	private static final Logger log = LH.get();
	private Thread thread;
	private FastBufferedInputStream in;
	private FastBufferedOutputStream out;
	final private Socket clientSocket;
	private TelnetShellConnection ttyShell;
	private UserShell userShell;
	private boolean inPluginForCtrlBreak = false;
	private Thread writeThread;

	public AmiSocketFH(Socket clientSocket, long now) {
		super(now);
		this.clientSocket = clientSocket;
		IOH.optimize(clientSocket);
		try {
			clientSocket.setKeepAlive(true);
		} catch (SocketException e) {
			throw OH.toRuntime(e);
		}
		this.setRemoteIp(this.clientSocket.getInetAddress().getHostName());
		this.setRemotePort(this.clientSocket.getPort());
	}
	public AmiSocketFH(long now, InputStream in2, OutputStream out2, String remoteIp, int remotePort) {
		super(now);
		this.in = new FastBufferedInputStream(in2);
		this.out = (new FastBufferedOutputStream(out2));
		this.clientSocket = null;
		this.setRemoteIp(remoteIp);
		this.setRemotePort(remotePort);
		this.setConnectionTime(now);
	}
	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);
	}

	@Override
	public void start() {
		super.start();

		try {
			if (this.clientSocket != null) {
				setClientDescription(clientSocket.getInetAddress().getHostAddress());
				this.in = new FastBufferedInputStream(clientSocket.getInputStream());
				this.out = (new FastBufferedOutputStream(clientSocket.getOutputStream()));
			}
			LH.info(amilog, "AMI Connection [", connectionIdString, "] from ", this.getRemoteIp(), ":", this.getRemotePort());
			LH.info(amilog, "[", connectionIdString, "] -- ", this.getRemoteIp(), ":", this.getRemotePort());
			this.getAmiRelayIn().onConnection(AmiRelayMapToBytesConverter.EMPTY);
			if (this.thread == null) {
				this.thread = this.getManager().getThreadFactory().newThread(this);
				this.thread.start();
			}
			this.writeThread = this.getManager().getThreadFactory().newThread(outRunner);
			this.writeThread.start();
			onStartFinish(this.clientSocket == null || this.clientSocket.isConnected());
		} catch (IOException e) {
			onStartFinish(false);
			LH.severe(log, "Failed to start", e);
		}
	}

	@Override
	public void stop() {
		super.stop();

		this.thread.interrupt();
		try {
			if (this.clientSocket != null)
				this.clientSocket.close();
			IOH.close(this.in, false);
			IOH.close(this.out, false);
			onStopFinish(true);
		} catch (Exception e) {
			LH.log(log, Level.WARNING, "Error closing ami socket session ", this.getId(), e);
			onStopFinish(false);
		}
	}

	@Override
	public void run() {
		try {
			FastByteArrayOutputStream inbuf = new FastByteArrayOutputStream();
			StringBuilder outbuf = new StringBuilder();
			StringBuilder errorSink = new StringBuilder();
			in.mark(1);
			int n = in.read();
			in.reset();
			if (n == 255) {
				this.optionTty = true;
				onOptionTty();
			}
			while (open) {
				int val;
				if (optionTty) {
					out.write('\r');
					out.flush();
					for (;;) {
						byte[] bytes = ttyShell.readLine().getBytes();
						if (AH.last(bytes, (byte) 0) == '\\') {
							inbuf.write(bytes, 0, bytes.length - 1);
						} else {
							inbuf.write(bytes);
							break;
						}
					}
					val = '\n';
				} else {
					val = this.in.read();

				}
				switch (val) {
					case -1:
						if (inbuf.size() == 0)
							onEOF(true);
						else
							onEOF(false);
						open = false;
						this.writeThread.interrupt();
						break;
					case '\\':
						int val2 = in.read();
						if (val2 == '\r')
							val2 = in.read();
						if (val2 == '\n')
							break;
						else if (val2 == -1)
							onEOF(false);
						inbuf.write('\\');
						inbuf.write(val2);
						break;
					case '\r':
						break;
					case '\n':
						processLine(inbuf, outbuf, errorSink);
						break;
					default:
						inbuf.write(val);

				}
			}
			LH.info(log, "Reader closed for ", connectionIdString);
			onEOF(true);
		} catch (Exception e) {
			LH.warning(log, "Critical error for: ", connectionIdString, " so closing connection.", e);
			onEOF(false);
		}
	}

	private Runnable outRunner = new Runnable() {

		@Override
		public void run() {
			while (open) {
				if (outputBufferSize.get() == 0) {
					synchronized (this) {
						if (outputBufferSize.get() == 0) {
							try {
								this.wait();
							} catch (InterruptedException e) {
							}
							continue;
						}
					}
				}
				String text = outputBuffer.get();
				outputBufferSize.decrementAndGet();
				try {
					if (optionTty) {
						ttyShell.getUserShell().writeToOutput(text);
					} else {
						if (clientSocket == null || !clientSocket.isClosed()) {
							SH.writeUTF(text, out);
							out.flush();
						}
					}
				} catch (SocketException e) {
					LH.info(log, "Socket error sending output for ", connectionIdString, ": ", e.getMessage());
				} catch (Exception e) {
					LH.warning(log, "Error sending output for ", connectionIdString, e);
				}
			}
			LH.info(log, "Writer closed for ", connectionIdString);
		}
	};

	private AtomicInteger outputBufferSize = new AtomicInteger();
	private FastQueue<String> outputBuffer = new FastQueue<String>();

	@Override
	protected boolean sendToOutput(CharSequence string) {
		if (!open)
			return false;
		if (!super.sendToOutput(string))
			return false;
		if (optionTty) {
			try {
				ttyShell.getUserShell().writeToOutput(string);
			} catch (IOException e) {
			}
			return true;
		}
		outputBuffer.put(string.toString());
		int n = outputBufferSize.incrementAndGet();
		if (n == 1) {
			synchronized (outRunner) {
				outRunner.notify();
			}
		} else if (n % 1000 == 0)
			LH.warning(log, "Slow Consumer has ", n, " messages pending: ", this.connectionIdString);
		return true;
	}

	@Override
	protected void onEOF(boolean clean) {
		IOH.close(this.clientSocket, false);
		IOH.close(this.in, false);
		IOH.close(this.out, false);
		this.open = false;
		this.writeThread.interrupt();
		if (!clean) {
			LH.log(amilog, Level.WARNING, "[", connectionIdString, "] Unexpected EOF from client ", getDescription());
			if (!eofCalled)
				LH.info(amilog, "[", connectionIdString, "] !! ", this.getRemoteIp(), ":", this.getRemotePort(), " (Unexpected disconnect)");
		} else {
			LH.log(amilog, Level.INFO, "Disconnect [", connectionIdString, "]");
			if (!eofCalled)
				LH.info(amilog, "[", connectionIdString, "] !! ", this.getRemoteIp(), ":", this.getRemotePort());
		}
		if (this.isLoggedIn) {
			this.isLoggedIn = false;
		}
		this.eofCalled = true;
		if (!isLoggedOut) {
			isLoggedOut = true;
			this.getAmiRelayIn().onLogout(EMPTY_PARAMS, false);
		}
	}

	@Override
	public String getDescription() {
		return super.getClientDescription();
	}

	@Override
	protected boolean invokePlugin(Map<String, Object> params, StringBuilder errorSink, AmiRelayInvokablePlugin plugin) {
		if (this.optionTty) {
			try {
				this.inPluginForCtrlBreak = true;
				return super.invokePlugin(params, errorSink, plugin);
			} finally {
				this.inPluginForCtrlBreak = false;
				Thread.interrupted();//clear any pending ctrl-c
			}
		} else
			return plugin.invoke(this.getManager(), params, this, errorSink);
	}

	@Override
	protected void enableTty() {
		if (this.clientSocket != null)
			super.enableTty();
		else
			LH.info(log, super.getClientDescription(), ": Ignoring TTY option, not in terminal mode");
	}

	@Override
	protected void onOptionTty() {
		if (this.ttyShell != null)
			return;
		this.ttyShell = new TelnetShellConnection(in, out, null, null, "AMIRT>");
		this.userShell = this.ttyShell.getUserShell();
		userShell.setBreakChars("|=");
		userShell.addCtrlBreakListener(this);
	}

	@Override
	public void onCtrlBreakListener(int ctrlPressedCount, int code) {
		switch (code) {
			case KEYCODE_CTRL_C: {
				if (thread != null && inPluginForCtrlBreak) {
					thread.interrupt();
					LH.info(log, super.getClientDescription(), " ctrl+c pressed... Breaking call to plugin: " + OH.getClassName(this.getPlugin()));
				}
				break;
			}
		}
	}

	@Override
	public void onCtrlBreakListenerDuringReadline(int code) {
	}

	@Override
	public boolean onAck(long seqnum) {
		if (optionQuiet)
			return true;
		StringBuilder outbuf = new StringBuilder();
		outbuf.append("M");
		outbuf.append('@');
		outbuf.append(EH.currentTimeMillis());
		if (seqnum != -1) {
			outbuf.append("|Q=").append(seqnum);
			outbuf.append("|S=0");
			outbuf.append("|M=\"ACK\"");
		} else {
			outbuf.append("|Q=").append(nextSeqnum());
			outbuf.append("|S=0");
			outbuf.append("|M=\"OK, object accepted\"");
		}
		outbuf.append(SH.CHAR_NEWLINE);
		if (optionSendCr || optionTty)
			outbuf.append(SH.CHAR_RETURN);
		return sendToOutput(outbuf);
	}
	public void setThread(Thread thread) {
		OH.assertNull(this.thread);
		this.thread = thread;
	}
}
