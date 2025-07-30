package com.f1.anvil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import com.f1.utils.AH;
import com.f1.utils.ArgParser;
import com.f1.utils.ArgParser.Arguments;
import com.f1.utils.CH;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.FastBufferedReader;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_File;

public class AnvilMerge implements Runnable {

	public static final int MAX_FILES = 1000;//seems realistic
	public long timeoutNs;//amount of time, in nanosecods after a file is silent that it will be removed from active streaming list

	private volatile int sourceFilesSize = 0;
	private SourceFile sourceFiles[] = new SourceFile[MAX_FILES];//active list of files being watched
	private FastBufferedOutputStream out;//output sink
	private Set<String> filesToWatch;//files to look for
	volatile private boolean waitingForFile;//are we still waiting for files?
	private File outFile;//name of outputfile or null if stderr
	private long lastTimeWritten;//time of last record written to sink, used as threshold to skip out-of-order messages
	private int logLevel;//0=OFF 1=File leve, 2=Record level

	public static void main(String a[]) throws FileNotFoundException {
		AnvilMerge am = new AnvilMerge(a);
	}
	public AnvilMerge(String[] a) throws FileNotFoundException {
		final ArgParser ap = new ArgParser("fix2Anvil");
		ap.setDescription("Merges Files and guarantees ordering");
		ap.addSwitch("o", "out", SH.m("*"), false, false, "Out file, if not specified stdout is used");
		ap.addSwitch("i", "in", SH.m("*"), false, true, "Comma delimited list of in files");
		ap.addSwitch("to", "timeout", SH.m("*"), false, false,
				"Timeout in milliseconds (after a file is silent for this period, it is removed from the 'active streaming' list, if records show up later it will be re-engaged)");
		ap.addSwitch("v", "verbose", SH.m("*"), false, false, "Log record level exceptions to stderr");
		ap.addSwitch("q", "quite", SH.m("*"), false, false, "Dont log at all");
		try {
			Arguments options = ap.parse(a);
			outFile = options.getOptional("o", Caster_File.INSTANCE);
			timeoutNs = 1000000L * options.getOptional("to", 10000L);
			this.filesToWatch = CH.s(SH.trimArray(SH.split(',', options.getRequired("i"))));
			OH.assertLe(this.filesToWatch.size(), MAX_FILES, "too many files");
			this.out = new FastBufferedOutputStream(outFile == null ? System.out : new FileOutputStream(outFile));
			this.waitingForFile = true;
			if (options.getOptional("v", Boolean.FALSE))
				logLevel = 2;
			else if (options.getOptional("q", Boolean.FALSE))
				logLevel = 0;
			else
				logLevel = 1;
		} catch (Exception e) {
			System.out.println(SH.printStackTrace(e));
			System.out.println(ap.toLegibleString());
			return;
		}

		//start pumping thread.
		final Thread t = new Thread(this, "READWRITE");
		t.setDaemon(false);
		t.start();

		//use main thread to wait for files...
		outer: while (!filesToWatch.isEmpty()) {
			for (String s : filesToWatch) {
				final File f = new File(s);
				if (f.exists()) {
					filesToWatch.remove(s);
					addFile(new SourceFile(f, this));
					continue outer;
				}
			}
			OH.sleep(100);
		}

		//Once we've found all of the files, then we can flag the watcher as done, 
		//meaning the program can exit once all files beeing pumped are terminated
		this.waitingForFile = false;
	}

	@Override
	public void run() {
		try {
			int misses = 0;
			while (waitingForFile || this.sourceFilesSize > 0)
				if (pumpLine())
					misses = 0;
				else if (misses++ > 10)
					OH.sleep(1);
		} catch (IOException e) {
			log("OUTFILE_ERROR: ", this.outFile == null ? "stdout" : IOH.getFullPath(this.outFile));
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	synchronized public void addFile(SourceFile in) {
		sourceFiles[sourceFilesSize] = in;
		sourceFilesSize++;
	}
	synchronized private void removeFile(int i) {
		AH.removeInplace(sourceFiles, i, 1);
		sourceFilesSize--;
	}

	//If all files are either in a EOF_TIMEOUT or LINE_READY status, then lets find the oldest line and pass it through
	//If multiple files have the exact timestamp print them all in one shot
	public boolean pumpLine() throws IOException {
		SourceFile nextFile = null;
		boolean waitingOnFile = false;
		int count = 0;
		for (int i = 0; i < sourceFilesSize; i++) {
			final SourceFile sf = sourceFiles[i];
			final byte origStatus = sf.status;
			sf.pump();
			switch (sf.status) {
				case SourceFile.STATUS_CLOSED:
					log("INFILE_CLOSED: ", sf.getFullPath());
					removeFile(i--);
					break;
				case SourceFile.STATUS_EOF_REACHED:
					waitingOnFile = true;
					break;
				case SourceFile.STATUS_EOF_TIMEOUT:
					if (origStatus != SourceFile.STATUS_EOF_TIMEOUT)//just transitioned to this state, let user know
						log("INFILE_TIMEOUT: ", sf.getFullPath());
					continue;
				case SourceFile.STATUS_LINE_READY:
					if (origStatus == SourceFile.STATUS_EOF_TIMEOUT)//Just transitioned out of timeout state, let user know
						log("INFILE_RECOVER: ", sf.getFullPath());
					if (sf.timeOnBufferedLine < this.lastTimeWritten) {//We've gotten an out of time record so...
						logVerbose("OUT_OF_SEQUENCE: ", sf.getFullPath(), sf.lineNumber, sf.bufferedLine);
						sf.clearBuffer();//drop it
						i--;//and re-read from this file
					} else if (nextFile == null || sf.timeOnBufferedLine < nextFile.timeOnBufferedLine) {
						nextFile = sf;//Number one contender
						count = 1;
					} else if (sf.timeOnBufferedLine == nextFile.timeOnBufferedLine)
						count++;

					break;
			}
		}
		if (waitingOnFile || nextFile == null) {
			out.flush();//obviously we're waiting on the input side, might as well flush
			return false;
		} else {
			long time = nextFile.timeOnBufferedLine;
			if (count > 1) {
				for (int i = 0; i < sourceFilesSize; i++) {
					final SourceFile sf = sourceFiles[i];
					if (sf.status == SourceFile.STATUS_LINE_READY && sf.timeOnBufferedLine == time) {
						out.writeBytes(sf.bufferedLine);
						sf.clearBuffer();
					}
				}
			} else {
				out.writeBytes(nextFile.bufferedLine);
				nextFile.clearBuffer();
			}
			this.lastTimeWritten = time;
			return true;
		}
	}
	private void log(CharSequence string, CharSequence string2) {
		if (logLevel >= 1)
			System.err.println(System.currentTimeMillis() + " " + string + string2);
	}
	private void logVerbose(CharSequence string, CharSequence string2, long lineNumber, CharSequence details) {
		if (logLevel >= 2)
			System.err.print(System.currentTimeMillis() + " " + string + string2 + ":" + lineNumber + " ==> " + details);
	}

	private static class SourceFile {

		public static final byte STATUS_INIT = 0;//Ready to read another line
		public static final byte STATUS_CLOSED = 1;//Error was detected/EOF was reached so file was closed
		public static final byte STATUS_LINE_READY = 2;//The next line has been read into buffer and it's time is parsed
		public static final byte STATUS_EOF_REACHED = 3;//We've reached the EOF in otherwords ready() returned false
		public static final byte STATUS_EOF_TIMEOUT = 4;//We've reached the EOF over timeout Period

		final public FastBufferedReader in;
		final private StringBuilder bufferedLine = new StringBuilder();
		final private File file;
		final private String fullpath;
		final private long delayNs;
		final private AnvilMerge anvilMerger;

		private byte status;
		private long timeOnBufferedLine;
		private long eofReachedAt;
		private int lineNumber = 0;

		public SourceFile(File f, AnvilMerge anvilMerge) throws FileNotFoundException {
			this.file = f;
			this.delayNs = anvilMerge.timeoutNs;
			this.anvilMerger = anvilMerge;
			this.fullpath = IOH.getFullPath(file);
			this.in = new FastBufferedReader(new FileReader(file));
		}

		public void pump() {
			if (status == STATUS_LINE_READY)
				return;
			outer: for (;;) {
				bufferedLine.setLength(0);
				try {
					if (!in.ready()) {
						switch (status) {
							case STATUS_INIT:
								status = STATUS_EOF_REACHED;
								eofReachedAt = System.nanoTime();
								return;
							case STATUS_EOF_REACHED:
								if (eofReachedAt + delayNs < System.nanoTime())
									status = STATUS_EOF_TIMEOUT;
							default:
								return;
						}
					} else
						eofReachedAt = -1L;
					in.readLine(bufferedLine);
					lineNumber++;
				} catch (IOException e) {
					IOH.close(in);
					this.status = STATUS_CLOSED;
				}
				long time = 0;

				//(1) ensure the line is valid and parse the time in one shot
				for (int i = 0, l = bufferedLine.length(); i < l; i++) {
					char c = bufferedLine.charAt(i);
					switch (c) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							time = (time << 3) + (time << 1) + c - '0';
							continue;
						case '|':
							this.status = STATUS_LINE_READY;
							bufferedLine.append(SH.NEWLINE);
							this.timeOnBufferedLine = time;
							return;
						default:
							if (SH.equals("EOF", bufferedLine)) {
								IOH.close(in);
								this.status = STATUS_CLOSED;
								return;
							}
							bufferedLine.append(SH.NEWLINE);
							anvilMerger.logVerbose("BAD_LINE: ", getFullPath(), lineNumber, bufferedLine);
							continue outer;
					}
				}
			}

		}
		public String getFullPath() {
			return fullpath;
		}
		public void clearBuffer() {
			this.status = STATUS_INIT;
			this.bufferedLine.setLength(0);
		}
	}

}
