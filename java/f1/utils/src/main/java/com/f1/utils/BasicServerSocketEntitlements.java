package com.f1.utils;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Logger;

import com.f1.utils.CachedFile.Cache;

public class BasicServerSocketEntitlements implements ServerSocketEntitlements {

	private TextMatcher[] matchers;
	private CachedFile cachedFile;
	private Cache data;
	private static final Logger log = LH.get();

	public BasicServerSocketEntitlements(String[] text) {
		process(text);
	}
	public BasicServerSocketEntitlements(File text) {
		if (!text.canRead())
			throw new RuntimeException("Can not read file: " + IOH.getFullPath(text));
		this.cachedFile = new CachedFile(text, 1000);
		this.data = this.cachedFile.getData();

		process(SH.splitLines(this.data.getText()));
	}
	private void process(String[] text) {
		TextMatcher[] ms = new TextMatcher[text.length];
		for (int i = 0; i < text.length; i++) {
			String t = text[i];
			if (SH.isnt(t))
				return;
			t = SH.replaceAll(t, ".", "\\.");
			t = SH.trim(t);
			if (!t.startsWith("^"))
				t = "^" + t;
			if (!t.endsWith("$"))
				t += "$";
			ms[i] = SH.m(t);
		}
		this.matchers = ms;
	}
	@Override
	public String getNotEntitledMessage(ServerSocket serverSocket, Socket clientSocket) {
		if (cachedFile != null) {
			if (this.data != cachedFile.getData()) {
				synchronized (this) {
					Cache d = cachedFile.getData();
					if (this.data != d) {
						if (OH.ne(this.data.getText(), d.getText())) {
							LH.info(log, "Whitelist File has changed, reprocessing: " + IOH.getFullPath(this.cachedFile.getFile()));
							process(SH.splitLines(d.getText()));
						}
						this.data = d;
					}
				}
			}
		}
		SocketAddress rsa = clientSocket.getRemoteSocketAddress();
		if (!(rsa instanceof InetSocketAddress))
			return "Unrecognized address type: " + OH.getClassName(rsa);
		final InetSocketAddress addr = (InetSocketAddress) rsa;
		final String address = addr.getHostName();
		for (final TextMatcher i : matchers)
			if (i.matches(address))
				return null;
		return "Not entitled: " + address;
	}
}
