package com.f1.console.impl.shell;

import static com.f1.console.impl.TelnetConstants.TELNET_DO;
import static com.f1.console.impl.TelnetConstants.TELNET_DONT;
import static com.f1.console.impl.TelnetConstants.TELNET_IAC;
import static com.f1.console.impl.TelnetConstants.TELNET_NOP;
import static com.f1.console.impl.TelnetConstants.TELNET_SB;
import static com.f1.console.impl.TelnetConstants.TELNET_SE;
import static com.f1.console.impl.TelnetConstants.TELNET_WILL;
import static com.f1.console.impl.TelnetConstants.TELNET_WONT;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import com.f1.console.impl.TelnetConstants;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.structs.IntSet;

public class UserShell {

	private static final Logger log = LH.get();

	public class CtrlBreakWatcher implements Runnable {

		private InputStream inBuf;
		private OutputStream outBuf;

		public CtrlBreakWatcher(InputStream inBuf, OutputStream outBuf) {
			this.inBuf = inBuf;
			this.outBuf = outBuf;
		}

		@Override
		public void run() {
			int lastLineCount = -1;
			int pressedCount = 0;
			int pressedKey = -1;
			try {
				int last = 253;
				for (;;) {
					int i = this.inBuf.read();
					if (i == -1)
						break;
					if (i == SH.CHAR_ETX && readThread == null && last != 253 && last != 251) {
						if (lastLineCount == linesCount && pressedKey == i)
							pressedCount++;
						else {
							lastLineCount = linesCount;
							pressedKey = i;
							pressedCount = 1;
						}
						fireCtrlKeyPressed(pressedCount, pressedKey);
					}
					this.outBuf.write(i);
					synchronized (in) {
						in.notify();
					}
					last = i;
				}
			} catch (IOException e) {
			}
		}

	}

	private static final byte[] ERASE = new byte[] { SH.CHAR_BS, ' ', SH.CHAR_BS };
	private static final byte[] REVERSE_TEXT = "(3forge-reverse-search):".getBytes();
	private InputStream in;
	private OutputStream out;
	private int historyPosition = 0;
	private List<String> history = new ArrayList<String>();
	private StringBuilder buf = new StringBuilder();
	private ShellAutoCompleter shellAutoCompleter;
	private String prompt;
	private byte[] promptBytes;
	private String oldTextBeforeHistoryNav = null;

	private IntSet breakChars = new IntSet();
	private Thread ctrlBreakThread;
	private List<UserShellCtrlBreakListener> ctrlBreakListeners = new CopyOnWriteArrayList<UserShellCtrlBreakListener>();

	public void setPrompt(String prompt) {
		this.prompt = prompt;
		this.promptBytes = prompt.getBytes();
	}

	public void addCtrlBreakListener(UserShellCtrlBreakListener listener) {
		if (this.ctrlBreakThread == null) {
			try {
				watchForCtrlBreak();
			} catch (IOException e) {
				throw OH.toRuntime(e);
			}
		}
		this.ctrlBreakListeners.add(listener);
	}
	public void fireCtrlKeyPressed(int ctrlPressedCount, int code) {
		for (UserShellCtrlBreakListener cbr : this.ctrlBreakListeners) {
			try {
				cbr.onCtrlBreakListener(ctrlPressedCount, code);
			} catch (Exception e) {
				LH.warning(log, "listener throw an exception:", e);
			}

		}
	}
	public void fireCtrlKeyPressedDuringReadline(int code) {
		for (UserShellCtrlBreakListener cbr : this.ctrlBreakListeners) {
			try {
				cbr.onCtrlBreakListenerDuringReadline(code);
			} catch (Exception e) {
				LH.warning(log, "listener throw an exception:", e);
			}

		}
	}
	public boolean removeCtrlBreakListener(UserShellCtrlBreakListener listener) {
		return this.ctrlBreakListeners.remove(listener);
	}

	private void watchForCtrlBreak() throws IOException {
		if (this.ctrlBreakThread != null)
			throw new IllegalStateException("already watching for ctrlBreak");
		InputStream inBuf = this.in;
		PipedOutputStream outBuf = new PipedOutputStream();
		this.in = new PipedInputStream(outBuf, 1024 * 1024);

		ctrlBreakThread = new Thread(new CtrlBreakWatcher(inBuf, outBuf));
		ctrlBreakThread.setDaemon(true);
		ctrlBreakThread.start();
	}

	public UserShell(InputStream in, OutputStream out, List<String> history, ShellAutoCompleter telnetAutoCompleter, String prompt) {
		this.shellAutoCompleter = telnetAutoCompleter;
		this.in = in;
		this.out = new BufferedOutputStream(out, 1024);
		if (history != null)
			this.history.addAll(history);
		this.historyPosition = this.history.size();
		setPrompt(prompt);
		this.breakChars.add(' ');
	}

	public void setHistory(Collection<String> history) {
		this.history.clear();
		if (history != null) {
			this.history.addAll(history);
		}
		this.historyPosition = this.history.size();
	}

	public void setBreakChars(String chars) {
		this.breakChars.clear();
		for (int i = 0; i < chars.length(); i++)
			this.breakChars.add(chars.charAt(i));
	}

	private int reverseMatchIndex;
	private int position;
	private boolean inReverseSearchMode;
	private Thread readThread;
	private boolean started = false;
	private int linesCount = 0;
	private boolean readingPassword = false;
	private boolean promptVisible;

	public String readLineNoIteract() {
		this.readThread = Thread.currentThread();
		try {
			buf.setLength(0);
			for (;;) {
				int c = read();
				switch (c) {
					case -1:
						return buf.length() == 0 ? null : SH.toStringAndClear(buf);
					case '\n':
						return SH.toStringAndClear(buf);
					case '\r':
						continue;
					default:
						buf.append((char) c);
				}
			}
		} catch (Exception e) {
			throw OH.toRuntime(e);
		} finally {
			this.readThread = null;
			this.linesCount++;
		}
	}
	public String readLine() {
		this.readThread = Thread.currentThread();
		inReverseSearchMode = false;
		oldTextBeforeHistoryNav = null;
		reverseMatchIndex = -1;
		position = 0;
		try {
			if (!readingPassword) {
				out.write(promptBytes);
				promptVisible = true;
			}
			out.flush();
			int c;
			String r = null;
			while (r == null) {
				c = read();
				if (c == -1)
					return null;
				synchronized (this) {
					if (inReverseSearchMode)
						r = processReverseLookupChar(c);
					else
						r = processChar(c);
					out.flush();
				}
			}
			return r;
		} catch (Exception e) {
			throw OH.toRuntime(e);
		} finally {
			this.readThread = null;
			this.linesCount++;
		}
	}

	private String processChar(int c) throws IOException {
		if (readingPassword) {
			switch (c) {
				case SH.CHAR_DEL:
				case SH.CHAR_BS:
					processBackspace();
					return null;
				case SH.CHAR_ESC: {
					switch (read()) {
						case '[': {
							switch (read()) {
								case '1':// home
								case '4':// end
								case '3':
									read();
								case 'A':// up
								case 'B':// down
								case 'C':// right
								case 'D':// left
									break;
							}
							break;
						}
						case 'O':
						case SH.CHAR_ESC: {//CTRL KEY
							switch (read()) {
								case '[':
									read();
								case 'C'://right
								case 'D'://left
									break;
							}
							break;
						}
						case SH.CHAR_DEL:
							break;
						default:
					}
					return null;
				}
				case SH.CHAR_CR:
					return processReturn(false);
				default:
					processNormalChar((char) c);
					return null;
			}
		}
		switch ((byte) c) {
			case TELNET_IAC:
				readIac(in, false);
				break;
			case SH.CHAR_ETX: {
				processNormalChar('^');
				processNormalChar('C');
				int p = position;
				String str = processReturn(false);
				fireCtrlKeyPressedDuringReadline(UserShellCtrlBreakListener.KEYCODE_CTRL_C);
				out.write(promptBytes);
				promptVisible = true;
				out.flush();
				str = SH.splice(str, p - 2, 2, "");//remove the ^C
				onCtrlBreak(str);
				return null;
			}
			case SH.CHAR_DC2:
				processCtrlR();
				break;
			case SH.CHAR_CR:
				return processReturn(true);
			case SH.CHAR_DEL:
			case SH.CHAR_BS:
				processBackspace();
				break;
			case SH.CHAR_ESC:
				c = read();
				switch (c) {
					case '[': {
						switch (c = read()) {
							case 'A':// up
								processUp();
								break;
							case 'B':// down
								processDown();
								break;
							case 'C':// right
								processRight();
								break;
							case 'D':// left
								processLeft();
								break;
							case '1':// home
								if (read() == '~')
									processHome();
								break;
							case '4':// end
								if (read() == '~')
									processEnd();
								break;
							case '3':
								if (read() == '~')
									processDelete();
								break;
						}
						break;
					}
					case 'O':
					case SH.CHAR_ESC: {//CTRL KEY
						c = read();
						switch (c) {
							case '[':
								if (read() == '3' && read() == '~')
									processCtrlDelete();
								break;
							case 'C'://right
								processCtrlRight();
								break;
							case 'D'://left
								processCtrlLeft();
								break;
						}
						break;
					}
					case SH.CHAR_DEL: {
						processCtrlBackspace();
						break;
					}
					default:
						System.out.println("UNKNOWN: " + (int) c);
				}
				break;
			case SH.CHAR_TAB:
				processTab();
				break;
			default:
				processNormalChar((char) c);
		}
		return null;
	}

	private void onCtrlBreak(String str) {
	}

	private int read() throws IOException {
		int r = in.read();
		return r;
	}

	private void processCtrlDelete() throws IOException {
		if (buf.length() == position)
			return;
		int pos = position + 1;
		boolean inBreakChar = isBreakCharAt(pos);
		while (pos < buf.length() && isBreakCharAt(pos) == inBreakChar)
			pos++;

		while (pos > position) {
			char t = remove(buf, position);
			pos--;
			for (int i = position; i < buf.length(); i++)
				out.write(buf.charAt(i));
			out.write(' ');
			for (int i = position; i < buf.length(); i++)
				out.write(SH.CHAR_BS);
			out.write(SH.CHAR_BS);
			if (t == ' ')
				break;
		}

	}
	private void processDelete() throws IOException {
		if (buf.length() > position) {
			remove(buf, position);
			for (int i = position; i < buf.length(); i++)
				out.write(buf.charAt(i));
			out.write(' ');
			for (int i = position; i < buf.length(); i++)
				out.write(SH.CHAR_BS);
			out.write(SH.CHAR_BS);
		}

	}
	private void processCtrlBackspace() throws IOException {
		if (position == 0)
			return;
		int pos = position - 1;
		boolean inBreakChar = isBreakCharAt(pos);
		while (pos > 0 && isBreakCharAt(pos - 1) == inBreakChar)
			pos--;

		while (position > pos) {
			position--;
			remove(buf, position);
			out.write(SH.CHAR_BS);
			for (int i = position; i < buf.length(); i++)
				out.write(buf.charAt(i));
			out.write(' ');
			for (int i = position; i < buf.length(); i++)
				out.write(SH.CHAR_BS);
			out.write(SH.CHAR_BS);
		}

	}
	private void processCtrlLeft() throws IOException {
		processLeft();
		boolean inBreakChar = isBreakCharAt(position);
		while (position > 0 && isBreakCharAt(position - 1) == inBreakChar) {
			out.write(SH.CHAR_BS);
			position--;
		}

	}

	private void processCtrlRight() throws IOException {
		processRight();
		boolean inBreakChar = isBreakCharAt(position);
		while (position < buf.length() && isBreakCharAt(position) == inBreakChar) {
			out.write(buf.charAt(position));
			position++;
		}
	}
	private void processEnd() throws IOException {
		while (position < buf.length()) {
			out.write(buf.charAt(position));
			position++;
		}
	}
	private void processHome() throws IOException {
		while (position > 0) {
			out.write(SH.CHAR_BS);
			position--;
		}
	}
	private void processLeft() throws IOException {
		if (position > 0) {
			out.write(SH.CHAR_BS);
			position--;
		}

	}
	private void processRight() throws IOException {
		if (position < buf.length()) {
			out.write(buf.charAt(position));
			position++;
		}
	}
	private void processDown() throws IOException {
		if (historyPosition == history.size())
			return;
		for (int j = position; j < buf.length(); j++)
			out.write(' ');
		for (int j = position; j < buf.length(); j++)
			out.write(SH.CHAR_BS);
		erase(out, position);
		String last = buf.toString();
		buf.setLength(0);
		while (historyPosition < history.size()) {
			if (++historyPosition == history.size() || !last.equals(history.get(historyPosition)))
				break;
		}
		if (historyPosition < history.size()) {
			String current = history.get(historyPosition);
			for (char c2 : current.toCharArray()) {
				out.write(c2);
				buf.append(c2);
				position++;
			}
		} else if (oldTextBeforeHistoryNav != null) {
			for (char c2 : oldTextBeforeHistoryNav.toCharArray()) {
				out.write(c2);
				buf.append(c2);
				position++;
			}
			oldTextBeforeHistoryNav = null;
		}
		position = buf.length();

	}
	private void processUp() throws IOException {
		if (oldTextBeforeHistoryNav == null)
			oldTextBeforeHistoryNav = SH.join("", buf);
		for (int j = position; j < buf.length(); j++)
			out.write(' ');
		for (int j = position; j < buf.length(); j++)
			out.write(SH.CHAR_BS);
		erase(out, position);
		String last = buf.toString();
		buf.setLength(0);
		while (historyPosition >= 0) {
			if (--historyPosition == -1 || !last.equals(history.get(historyPosition)))
				break;
		}
		if (historyPosition != -1) {
			String current = history.get(historyPosition);
			for (char c2 : current.toCharArray()) {
				out.write(c2);
				buf.append(c2);
				position++;
			}
		}
		position = buf.length();
	}
	private void processBackspace() throws IOException {
		if (position > 0) {
			position--;
			remove(buf, position);
			out.write(SH.CHAR_BS);
			for (int i = position; i < buf.length(); i++)
				out.write(readingPassword ? '*' : buf.charAt(i));
			out.write(' ');
			for (int i = position; i < buf.length(); i++)
				out.write(SH.CHAR_BS);
			out.write(SH.CHAR_BS);
		}
	}

	private void processTab() throws IOException {
		if (this.shellAutoCompleter != null) {
			String str = buf.toString();
			final ShellAutoCompletion remaining = shellAutoCompleter.autoComplete(str);
			if (remaining != null) {
				final String comments = remaining.getText();
				if (!comments.isEmpty()) {
					out.write(TelnetConstants.NEWLINE.getBytes());
					out.write(comments.getBytes());
					out.write(this.promptBytes);
					for (int i = 0; i < buf.length(); i++)
						out.write((byte) buf.charAt(i));
				}
				for (char c2 : remaining.getAutoCompletion().toCharArray()) {
					buf.append(c2);
					out.write((byte) c2);
					position++;
				}
			}
		}

	}

	private void processNormalChar(char c) throws IOException {
		if (!SH.isntUnicode(c)) {
			buf.insert(position++, c);
			if (readingPassword) {
				out.write('*');
				if (position <= buf.length()) {
					for (int i = position; i < buf.length(); i++)
						out.write('*');
					for (int i = position; i < buf.length(); i++)
						out.write(SH.CHAR_BS);
				}
			} else {
				out.write(c);
				if (position <= buf.length()) {
					for (int i = position; i < buf.length(); i++)
						out.write(buf.charAt(i));
					for (int i = position; i < buf.length(); i++)
						out.write(SH.CHAR_BS);
				}
			}
		}
	}
	private String processReturn(boolean addToHistory) throws IOException {
		for (int i = position; i < buf.length(); i++)
			out.write(buf.charAt(i));
		out.write(SH.CHAR_LF);
		out.write(SH.CHAR_CR);
		this.promptVisible = false;
		String str = buf.toString();
		if (addToHistory)
			if (buf.length() > 0)
				history.add(str);
		historyPosition = history.size();
		buf.setLength(0);
		position = 0;
		out.flush();
		return str;
	}

	public void rebuildPromptAndText() throws IOException {
		out.write(promptBytes);
		this.promptVisible = true;
		for (int i = 0; i < buf.length(); i++)
			out.write(buf.charAt(i));
		for (int i = position; i < buf.length(); i++) {
			out.write(SH.CHAR_BS);
		}
		out.flush();
	}

	private void processCtrlR() throws IOException {
		while (position < buf.length()) {
			out.write(buf.charAt(position));
			position++;
		}
		if (promptVisible)
			erase(out, prompt.length());
		erase(out, buf.length());
		out.write(REVERSE_TEXT);
		inReverseSearchMode = true;
		position = 0;
		buf.setLength(0);
	}

	private String processReverseLookupChar(int c) throws IOException {
		switch ((byte) c) {
			case SH.CHAR_DC2:
				return null;
			case SH.CHAR_ETX:
			case SH.CHAR_CR:
				String r = "";
				if (buf.length() > 0) {
					erase(out, buf.length() + 1);
					buf.setLength(0);
					if (reverseMatchIndex != -1) {
						r = history.get(reverseMatchIndex);
						erase(out, r.length());
						if (c == SH.CHAR_ETX) {
							r = "";
						} else {
							for (char c2 : r.toCharArray())
								buf.append(c2);
							position = buf.length();
						}
					}
				}
				erase(out, REVERSE_TEXT.length);
				out.write(promptBytes);
				promptVisible = true;
				out.write(r.getBytes());
				inReverseSearchMode = false;
				reverseMatchIndex = -1;
				break;
			case SH.CHAR_ESC:
				c = read();
				if (c == '[') {
					int old = reverseMatchIndex;
					String bufStr = buf.toString();
					TextMatcher matcher = SH.m(bufStr);
					switch (c = read()) {
						case 'A':// up
							if (reverseMatchIndex != -1) {
								for (int i = reverseMatchIndex - 1; i >= 0; i--) {
									if (matcher.matches(history.get(i)) && !history.get(reverseMatchIndex).equals(history.get(i))) {
										reverseMatchIndex = i;
										break;
									}
								}
							}
							break;
						case 'B':// down
							if (reverseMatchIndex != -1) {
								for (int i = reverseMatchIndex + 1; i < history.size(); i++) {
									if (matcher.matches(history.get(i)) && !history.get(reverseMatchIndex).equals(history.get(i))) {
										reverseMatchIndex = i;
										break;
									}
								}
							}
							break;
						case 'C':// right
						case 'D':// left
					}
					if (old != reverseMatchIndex) {
						erase(out, history.get(old).length());
						out.write(history.get(reverseMatchIndex).getBytes());
					}
				}
				break;
			default:
				if (buf.length() > 0)
					erase(out, buf.length() + 1);
				if (c == SH.CHAR_DEL || c == SH.CHAR_BS) {
					if (buf.length() > 0)
						remove(buf, buf.length() - 1);
				} else if (!SH.isntUnicode((char) c))
					buf.append((char) c);
				String bufStr = buf.toString();
				if (reverseMatchIndex != -1)
					erase(out, history.get(reverseMatchIndex).length());
				out.write(bufStr.getBytes());
				if (buf.length() > 0)
					out.write(':');
				reverseMatchIndex = -1;
				if (bufStr.length() > 0) {
					TextMatcher matcher = SH.m(bufStr);
					for (int i = history.size() - 1; i >= 0; i--) {
						if (matcher.matches(history.get(i))) {
							reverseMatchIndex = i;
							out.write(history.get(i).getBytes());
							break;
						}
					}
				}
				break;
		}
		return null;
	}

	private char remove(StringBuilder buf, int pos) {
		return SH.removeChar(buf, pos);
	}

	private static void erase(OutputStream out, int i) throws IOException {
		while (i-- > 0)
			out.write(ERASE);

	}

	/**
	 * 
	 * @param in
	 *            the stream to read
	 * @return first byte is the option, rest are the payload
	 * @throws IOException
	 */
	private static byte[] readIac(InputStream in, boolean needsIAC) throws IOException {
		int cmd = in.read();
		if (needsIAC) {
			if (cmd != 255)
				throw new RuntimeException("Expection IAC(255): " + cmd);
			cmd = in.read();
		}
		if (cmd == -1)
			return null;
		switch ((byte) cmd) {
			case TELNET_NOP:
				return null;
			case TELNET_WILL:
			case TELNET_WONT:
			case TELNET_DO:
			case TELNET_DONT:
				int option = in.read();
				if (option == -1)
					return null;
				return new byte[] { (byte) cmd, (byte) option };
			case TELNET_SB:
				ArrayList<Byte> bytes = new ArrayList<Byte>();
				bytes.add((byte) cmd);
				int c;
				while ((byte) (c = in.read()) != TELNET_IAC)
					if (c == -1)
						return null;
					else
						bytes.add((byte) c);
				byte se = (byte) in.read();
				if (se != TELNET_SE)
					throw new RuntimeException("expecting SE after IAC, not " + se);
				return AH.toArrayByte(bytes);
			default:
				throw new RuntimeException("invalid command: " + cmd);
		}
	}
	private boolean isBreakCharAt(int position) {
		return position < 0 || position >= buf.length() || this.breakChars.contains((int) buf.charAt(position));
	}
	public List<String> getHistory() {
		return this.history;
	}
	public String readPassword(String passwordPrompt) {
		this.readingPassword = true;
		try {
			out.write(passwordPrompt.getBytes());
			out.flush();
			return readLine();
		} catch (Exception e) {
			throw OH.toRuntime(e);
		} finally {
			this.readingPassword = false;
		}
	}

	public void writeToOutput(CharSequence string) throws IOException {
		synchronized (this) {
			int position = this.position;
			while (position < buf.length()) {
				out.write(buf.charAt(position));
				position++;
			}
			if (promptVisible)
				erase(out, prompt.length());
			erase(out, buf.length());
			byte[] bytes = string.toString().getBytes();
			out.write(bytes);
			byte last = AH.last(bytes, (byte) -1);
			switch (last) {
				default:
					out.write(SH.CHAR_LF);
				case SH.CHAR_LF:
					out.write(SH.CHAR_CR);
					break;
				case SH.CHAR_CR:
					break;
			}
			rebuildPromptAndText();
		}
	}

	public String getPrompt() {
		return this.prompt;
	}
}
