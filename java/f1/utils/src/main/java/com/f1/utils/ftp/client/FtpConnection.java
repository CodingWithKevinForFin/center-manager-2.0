package com.f1.utils.ftp.client;

import static com.f1.utils.ftp.client.FtpConstants.*;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.SocketConfig;

public class FtpConnection implements Closeable {

	public static final String DATETIME_FORMAT = "yyyyMMddHHmmss";

	private SocketConfig socketConfig = new SocketConfig();
	private Socket socket;
	private OutputStream out;
	private InputStream in;
	private OutputStreamWriter writer;
	private BufferedReader reader;

	private String host;

	private int port;

	private String welcomeMessage;

	private StringBuilder responseText = new StringBuilder();

	private int responseCode;

	final private DateFormat dateParser = new SimpleDateFormat(DATETIME_FORMAT);

	public FtpConnection(String host, int port) throws UnknownHostException, IOException {
		socketConfig.setSoTimeout(10000);
		dateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
		this.host = host;
		this.port = port;
		this.socket = prepareSocket(IOH.openClientSocketWithReason(host, port, "Ftp client"));
		this.in = socket.getInputStream();
		this.out = socket.getOutputStream();
		this.reader = new BufferedReader(new InputStreamReader(in));
		this.writer = new OutputStreamWriter(out);
		readResponse();
		welcomeMessage = responseText.toString();
	}

	public static void main(String a[]) throws IOException {
		FtpConnection ftp = new FtpConnection("192.168.3.21", 21);
		ftp.login("test", "123456");
		String[] files = ftp.list(null);
		for (String file : files)
			System.out.println(file);
		for (String file : ftp.nlst(null))
			System.out.println(file);
		for (String file : ftp.mlsd(null))
			System.out.println("MLSD FILE: " + file);
		System.out.println(ftp.cwd("workspace"));
		ftp.cdup();
		System.out.println(ftp.dele("robthebomb2.txt"));
		System.out.println(ftp.help(null));
		System.out.println(ftp.mdtm("robthebomb.txt"));
		System.out.println(ftp.mkd("mydir"));
		ftp.mode(MODE_STREAM);
		ftp.noop();
		System.out.println(ftp.pwd());
		System.out.println(new String(ftp.retr("robthebomb.txt")));
		System.out.println(ftp.rmd("mydir"));
		System.out.println(ftp.rename("blah.txt", "blah2.txt"));
		System.out.println(ftp.size("blahblah.txt"));
		System.out.println(ftp.size("robthebomb.txt"));
		ftp.stor("fromrob.txt", "hello from the client!".getBytes());
		System.out.println(ftp.syst());
		ftp.type(FtpConstants.TYPE_IMAGE_BINARY_DATA);
		byte[] data = IOH.readData(new File("/tmp/lsp2.gz"));
		ftp.stor("remotelsp.zip", data);
		byte[] data2 = ftp.retr("remotelsp.zip");
		IOH.writeData(new File("/tmp/lsp3.gz"), data2);
		ftp.quit();
	}
	public void login(String user, String pass) {
		user(user);
		pass(pass);
	}

	public void user(String user) {
		sendCommandAndExpectCode(COMMAND_USER, user, CODE_331_USERNAME_OK_NEED_PASSWORD);
	}

	public void pass(String pass) {
		sendCommandAndExpectCode(COMMAND_PASS, pass, CODE_230_USER_LOGGED_IN_PROCEED);
	}

	public String[] list(String args) {
		return SH.splitLines(IOH.toString(getPassiveData(COMMAND_LIST, args)));
	}
	public String[] mlst(String args) {
		sendCommandAndExpectCode(COMMAND_MLST, args, CODE_250_REQUESTED_FILE_ACTION_OKAY_COMPLETED);
		return SH.splitLines(responseText.toString());
	}
	public String[] mlsd(String args) {
		return SH.splitLines(IOH.toString(getPassiveData(COMMAND_MLSD, args)));
	}

	public String[] nlst(String args) {
		return SH.splitLines(IOH.toString(getPassiveData(COMMAND_NLST, args)));
	}

	public byte[] retr(String remoteFileName) {
		return getPassiveData(COMMAND_RETR, remoteFileName);
	}
	public void stor(String remoteFileName, byte data[]) {
		sendPassiveData(COMMAND_STOR, remoteFileName, data);
	}
	public void stou(byte data[]) throws IOException {
		sendPassiveData(COMMAND_STOU, null, data);
	}

	public boolean cwd(String workingDirectory) {
		return sendCommandAndExpectCode(COMMAND_CWD, workingDirectory, CODE_250_REQUESTED_FILE_ACTION_OKAY_COMPLETED, CODE_550_REQUESTED_ACTION_NOT_TAKEN_FILE_NOT_FOUND);
	}
	public void cdup() {
		sendCommandAndExpectCode(COMMAND_CDUP, null, CODE_200_COMMAND_OKAY);
	}

	public boolean dele(String remoteFileName) {
		return sendCommandAndExpectCode(COMMAND_DELE, remoteFileName, CODE_250_REQUESTED_FILE_ACTION_OKAY_COMPLETED, CODE_550_REQUESTED_ACTION_NOT_TAKEN_FILE_NOT_FOUND);
	}
	public String help(String command) {
		sendCommandAndExpectCode(COMMAND_HELP, command, CODE_214_HELP_MESSAGE);
		return responseText.toString();
	}
	public String syst() {
		sendCommandAndExpectCode(COMMAND_SYST, null, CODE_215_NAME_SYSTEM_TYPE);
		return responseText.toString();
	}

	public Date mdtm(String remoteFileName) {
		if (!sendCommandAndExpectCode(COMMAND_MDTM, remoteFileName, CODE_213_FILE_STATUS, CODE_550_REQUESTED_ACTION_NOT_TAKEN_FILE_NOT_FOUND))
			return null;
		StringBuilder date = responseText;
		try {
			return dateParser.parse(date.toString().trim());
		} catch (ParseException e) {
			throw new FtpException("bad response, expecting " + DATETIME_FORMAT + ": " + responseText);
		}
	}
	public boolean mkd(String remoteFileName) {
		return sendCommandAndExpectCode(COMMAND_MKD, remoteFileName, CODE_550_REQUESTED_ACTION_NOT_TAKEN_FILE_NOT_FOUND, CODE_257_PATHNAME_CREATED);
	}
	public void mode(char mode) {
		switch (mode) {
			case MODE_STREAM:
			case MODE_BLOCK:
			case MODE_COMPRESSED:
				sendCommandAndExpectCode(COMMAND_MODE, SH.toString(mode), CODE_200_COMMAND_OKAY);
				break;
			default:
				throw new FtpException("Unknown mode: " + mode);
		}
	}
	public void type(char type) {
		switch (type) {
			case TYPE_ASCII_TEXT:
			case TYPE_EBCDIC_TEXT:
			case TYPE_IMAGE_BINARY_DATA:
			case TYPE_LOCAL_FORMAT:
				sendCommandAndExpectCode(COMMAND_TYPE, SH.toString(type), CODE_200_COMMAND_OKAY);
				break;
			default:
				throw new FtpException("Unknown mode: " + type);
		}
	}
	public void noop() {
		sendCommandAndExpectCode(COMMAND_NOOP, null, CODE_200_COMMAND_OKAY);
	}
	public String pwd() {
		sendCommandAndExpectCode(COMMAND_PWD, null, CODE_257_PATHNAME_CREATED);
		String text = responseText.toString().trim();
		if (text.length() > 0 && text.charAt(0) == '"') {
			return text.substring(1, text.lastIndexOf('"'));
		} else
			throw new FtpException("Bad syntax for working directory: " + text);
	}
	public void quit() {
		sendCommandAndExpectCode(COMMAND_QUIT, null, CODE_221_SERVICE_CLOSING_CONTROL_CONNECTION);
	}

	public boolean rmd(String remoteFileName) {
		return sendCommandAndExpectCode(COMMAND_RMD, remoteFileName, CODE_250_REQUESTED_FILE_ACTION_OKAY_COMPLETED, CODE_550_REQUESTED_ACTION_NOT_TAKEN_FILE_NOT_FOUND);
	}

	public boolean rnfr(String remoteFileName) {
		return sendCommandAndExpectCode(COMMAND_RNFR, remoteFileName, CODE_350_REQUESTED_FILE_ACTION_PENDING_FURTHER_INFO, CODE_550_REQUESTED_ACTION_NOT_TAKEN_FILE_NOT_FOUND);
	}

	public boolean rnto(String remoteFileName) {
		return sendCommandAndExpectCode(COMMAND_RNTO, remoteFileName, CODE_250_REQUESTED_FILE_ACTION_OKAY_COMPLETED, CODE_553_ACTION_NOT_TAKEN_FILENAME_NOT_ALLOWED);
	}
	public void site(String args) {
		sendCommand(COMMAND_SITE, args);
	}

	public long size(String remoteFileName) {
		if (!sendCommandAndExpectCode(COMMAND_SIZE, remoteFileName, CODE_213_FILE_STATUS, CODE_550_REQUESTED_ACTION_NOT_TAKEN_FILE_NOT_FOUND))
			return -1;
		return Long.parseLong(responseText.toString().trim());
	}
	public String stat(String remoteFileName) {
		sendCommandAndExpectCode(COMMAND_STAT, remoteFileName, CODE_200_COMMAND_OKAY);
		return responseText.toString().trim();
	}
	public boolean rename(String from, String to) {
		return rnfr(from) && rnto(to);
	}

	private byte[] getPassiveData(String command, String args) {
		System.out.println("Getting: '" + command + "' args: " + SH.join(',', args));
		final Socket socket = getPassiveConnection();
		final byte[] r;
		try {
			sendCommandAndExpectCode(command, args, CODE_150_FILE_STATUS_OK_ABOUT_TO_OPEN_CONNECTION);
			r = IOH.readData(socket.getInputStream());
		} catch (IOException e) {
			throw new FtpException(e);
		} finally {
			IOH.close(socket);
			readResponseAndExpectCode(CODE_226_CLOSING_DATA_CONNECTION);
		}
		return r;
	}
	private void sendPassiveData(String command, String args, byte[] data) {
		Socket socket = getPassiveConnection();
		try {
			sendCommandAndExpectCode(command, args, CODE_150_FILE_STATUS_OK_ABOUT_TO_OPEN_CONNECTION);
			out = socket.getOutputStream();
			out.write(data);
			IOH.close(socket);
			socket = null;
			readResponseAndExpectCode(CODE_226_CLOSING_DATA_CONNECTION);
		} catch (IOException e) {
			throw new FtpException(e);
		} finally {
			if (socket != null)
				IOH.close(socket);
		}
	}
	private Socket getPassiveConnection() {
		sendCommandAndExpectCode(COMMAND_PASV, null, CODE_227_ENTERING_PASSIVE_MODE);
		int start = responseText.lastIndexOf("(") + 1;
		int end = responseText.lastIndexOf(")");
		if (start == 0 || end == -1)
			throw new FtpException("malformatted response to " + COMMAND_PASV + ": " + responseText);
		String[] parts = SH.split(',', responseText.substring(start, end));
		if (parts.length != 6)
			throw new FtpException("malformatted response to " + COMMAND_PASV + ": " + responseText);
		String host = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
		int port = (Integer.parseInt(parts[4]) << 8) + Integer.parseInt(parts[5]);
		try {
			return prepareSocket(IOH.openClientSocketWithReason(host, port, "ftp pasv data"));
		} catch (Exception e) {
			throw new FtpException("Failed to establish passive connection on " + host + ":" + port, e);
		}
	}

	private Socket prepareSocket(Socket socket) throws SocketException {
		this.socketConfig.applyToSocket(socket);
		return socket;
	}

	private void sendCommandAndExpectCode(String cmd, String argument, int expectedCode) {
		sendCommand(cmd, argument);
		readResponseAndExpectCode(expectedCode);
	}
	private boolean sendCommandAndExpectCode(String cmd, String argument, int trueCode, int falseCode) {
		sendCommand(cmd, argument);
		return readResponseAndExpectCode(trueCode, falseCode);
	}

	private void readResponseAndExpectCode(int expectedCode) {
		int code = readResponse();
		expectCode(code, expectedCode);
	}
	private boolean readResponseAndExpectCode(int trueCode, int falseCode) {
		int code = readResponse();
		if (code == trueCode)
			return true;
		if (code == falseCode)
			return false;
		throw new FtpException(code, "Expected codes " + trueCode + " or " + falseCode + ": " + code + " - " + responseText);
	}

	protected int sendCommandAndReadResponse(String cmd, String argument) {
		sendCommand(cmd, argument);
		return readResponse();
	}

	private void expectCode(int code, int expected) {
		if (code != expected && (code / 100) != 2)
			throw new FtpException(code, "Expected code " + expected + ": " + code + " - " + responseText);
	}

	protected void sendCommand(String cmd, String argument) {
		try {
			writer.append(cmd);
			if (argument != null) {
				writer.append(' ');
				writer.append(argument);
			}
			writer.append("\r\n");
			writer.flush();
		} catch (IOException e) {
			throw new FtpException("error writing to " + host + ":" + port, e);
		}
	}
	public int readResponse() {
		SH.clear(responseText);
		int code = -1;
		try {
			for (;;) {
				final String line = reader.readLine();
				if (line == null)
					throw new FtpException("EOF");
				if (line.length() == 0)
					break;
				if (line.length() > 3 && SH.areBetween(line, 0, 3, '0', '9')) {
					responseText.append(line, 4, line.length()).append(SH.NEWLINE);
					code = SH.parseInt(line, 0, 3, 10);
					if (line.charAt(3) != '-')
						break;
				} else
					responseText.append(line).append(SH.NEWLINE);
			}
		} catch (IOException e) {
			throw new FtpException("error reading from " + host + ":" + port, e);
		}
		if (code == -1)
			throw new FtpException("error reading from " + host + ":" + port + "  NO CODE IN RESPONSE: " + responseText);
		responseCode = code;
		return code;
	}

	public int getLastResponseCode() {
		return responseCode;
	}

	public String getLastResponseText() {
		return responseText.toString();
	}

	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	public void setTimeZone(TimeZone tz) {
		dateParser.setTimeZone(tz);
	}

	public TimeZone getTimeZone() {
		return dateParser.getTimeZone();
	}

	public void close() {
		IOH.close(socket);
	}

	public DateFormat getDateParser() {
		return dateParser;
	}

	public SocketConfig getSocketConfig() {
		return socketConfig;
	}

	public void setSocketConfig(SocketConfig socketConfig) {
		this.socketConfig = socketConfig;
	}
}
