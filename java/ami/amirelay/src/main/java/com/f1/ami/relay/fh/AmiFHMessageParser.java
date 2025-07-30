package com.f1.ami.relay.fh;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.ami.amicommon.msg.AmiRelayCommandDefMessage;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.AmiRelayPlugin;
import com.f1.ami.relay.AmiRelayProperties;
import com.f1.ami.relay.AmiRelayServer;
import com.f1.ami.relay.plugins.AmiRelayInvokablePlugin;
import com.f1.utils.ByteArray;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.IntArrayList;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.OfflineConverter;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.concurrent.ConcurrentHashSet;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.impl.CharSequenceHasher;
import com.f1.utils.json.JsonBuilder;

public abstract class AmiFHMessageParser extends AmiFHBase {
	private static final Logger log = LH.get();

	public static final Logger amilog = Logger.getLogger("AMI_MESSAGES");
	private static final String PROP_AUTOLOGIN = "autologin";
	private static final String PROP_LOG_MESSAGES = AmiRelayProperties.OPTION_AMI_LOG_MESSAGES;

	private static final long MAX_TIME_DRIFT = 3000;
	public static final String OPTION_QUIET = "QUIET";
	public static final String OPTION_LOG = "LOG";
	public static final String OPTION_NOCR = "UNIX";
	public static final String OPTION_CR = "WINDOWS";
	public static final String OPTION_TTY = "TTY";
	private static final int STRING_POOL_SIZE = 1000;
	private static final Integer ZERO = Integer.valueOf(0);
	private String clientDescription;
	protected boolean open = true;
	private long nextSeqNum = 0;
	private final StringBuilder sbuf = new StringBuilder();
	//	protected final Map<String, Object> paramsBuf = new HashMap<String, Object>();
	protected boolean isLoggedIn = false;
	protected boolean isLoggedOut = false;

	protected boolean optionQuiet = false;
	protected boolean optionLog = false;
	protected boolean optionSendCr = true;
	protected boolean optionTty = false;

	//reserved keys are all variable names between 'A' and 'Z'
	private Object[] reservedValues = new Object[26];//A=0 .... Z=25
	private int reservedValuesBitset = 0;//A=0th bit, Z=25th bit

	private Set<String> pendingRequests = new ConcurrentHashSet<String>();
	private String partitionId;
	protected String connectionIdString;
	private AmiRelayPlugin plugin;
	private ObjectToJsonConverter jsonConverter;
	private ByteArray tmpKey = new ByteArray();
	private String autologinCommand;
	private AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();

	protected byte[] toBytes() {
		if (this.reservedValuesBitset != 0) {
			for (int i = MH.indexOfBitSet(this.reservedValuesBitset, 0); i >= 0; i = MH.indexOfBitSet(this.reservedValuesBitset, i + 1)) {
				String key = SH.toString((char) (i + 'A'));
				Object val = this.reservedValues[i];
				this.converter.append(key, val);
			}
			this.reservedValuesBitset = 0;
		}
		return this.converter.toBytes();
	}

	public AmiFHMessageParser(long now) {
		this.setConnectionTime(now);

		jsonConverter = new ObjectToJsonConverter();
		jsonConverter.setOptions(OfflineConverter.OPTION_COMPACT_MODE);
		jsonConverter.setStrictValidation(true);
	}

	private ByteArray tmpByteArray = new ByteArray();
	private IntArrayList tmpIntArrayList = new IntArrayList();

	protected boolean processLine(FastByteArrayOutputStream inbuf, StringBuilder outbuf, StringBuilder errorSink) throws IOException {
		SH.clear(outbuf);
		tmpIntArrayList.clear();
		boolean status = onEOL(inbuf.getBuffer(), 0, inbuf.getCount(), SH.clear(errorSink), tmpIntArrayList);
		if (optionLog && amilog.isLoggable(Level.INFO)) {
			if (tmpIntArrayList.isEmpty()) {
				LH.info(amilog, "[", connectionIdString, "] >> ", tmpByteArray.reset(inbuf.getBuffer(), 0, inbuf.getCount()));
			} else {
				SH.clear(outbuf);
				int start = 0;
				int keep = this.maxlogBinarySize / 2;
				for (int i = 0; i < tmpIntArrayList.size();) {
					int end = tmpIntArrayList.get(i++) + keep;
					outbuf.append(tmpByteArray.reset(inbuf.getBuffer(), start, end));
					start = tmpIntArrayList.getInt(i++) - keep;
					outbuf.append("<").append(start - end).append("b>");
				}
				tmpByteArray.reset(inbuf.getBuffer(), start, inbuf.size());
				outbuf.append(tmpByteArray);
				LH.info(amilog, "[", connectionIdString, "] >~ ", outbuf);
				SH.clear(outbuf);
			}
		}
		if (!status || errorSink.length() > 0) {
			prepareMessage('M', outbuf);
			outbuf.append("|S=");
			outbuf.append(status ? 0 : 1);
			outbuf.append("|M=\"");
			SH.escape(errorSink, '"', '\\', outbuf);
			outbuf.append('"');
			outbuf.append(SH.CHAR_NEWLINE);
			if (optionSendCr)
				outbuf.append(SH.CHAR_RETURN);
			if (!optionQuiet)
				sendToOutput(outbuf);
			else if (!status) {
				LH.info(amilog, "[", connectionIdString, "] XX ", outbuf.substring(0, outbuf.length() - (optionSendCr ? 2 : 1)));
			} else {
				LH.fine(amilog, "QUIET Mode. Not sending to '", this.clientDescription, "': ", outbuf);
			}
		}
		if (!status) {
			if (plugin != null && pluginStatus == AmiRelayPlugin.OKAY) {
				LH.warning(log, "Plugin produced invalid String: ", pluginParam);
			}
			this.getAmiRelayIn().onError(null, outbuf);
		}
		inbuf.reset();
		SH.clear(outbuf);
		return status;

	}

	protected boolean sendToOutput(CharSequence string) {
		if (optionLog) {
			LH.info(amilog, "[", connectionIdString, "] << ", string.subSequence(0, string.length() - (optionSendCr ? 2 : 1)));
		}
		return true;
	}
	private void prepareMessage(char type, StringBuilder sink) {
		sink.append(type);
		sink.append('@');
		sink.append(EH.currentTimeMillis());
		sink.append("|Q=");
		sink.append(nextSeqnum());
	}
	protected long nextSeqnum() {
		return nextSeqNum++;
	}

	boolean eofCalled = false;

	protected void onEOF(boolean clean) {
		if (!clean) {
			LH.log(amilog, Level.WARNING, "[", connectionIdString, "] Unexpected EOF from client ", getClientDescription());
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

	public String getClientDescription() {
		return clientDescription;
	}

	private ByteArray pluginParam = new ByteArray();
	private int maxlogBinarySize = 16;
	private HasherMap<CharSequence, SimpleDateFormat> dataformatters = new HasherMap<CharSequence, SimpleDateFormat>(CharSequenceHasher.INSTANCE);
	private int pluginStatus;

	public boolean onEOL(byte[] rawData, int start, int end, StringBuilder errorSink, IntArrayList binaryStartEnds) throws IOException {
		SH.clear(sbuf);
		this.converter.clear();
		reservedValuesBitset = 0;
		if (end == start)
			return true;
		byte[] data;
		if (this.plugin != null) {
			final int length = errorSink.length();
			pluginParam.reset(rawData, start, end);
			try {
				this.pluginStatus = this.plugin.processData(pluginParam, errorSink);
			} catch (Throwable e) {
				LH.info(amilog, "Plugin '", this.plugin.getClass().getName(), "' threw exception: ", e);
				if (errorSink.length() == length)
					errorSink.append("Plugin threw exception: ").append(e.getClass().getName());
				return false;
			}
			switch (pluginStatus) {
				case AmiRelayPlugin.SKIP:
					return true;
				case AmiRelayPlugin.ERROR:
					if (errorSink.length() == length)
						errorSink.append("plugin returned error");
					return false;
				case AmiRelayPlugin.NA:
					data = rawData;
					break;
				case AmiRelayPlugin.OKAY:
					data = pluginParam.getData();
					start = pluginParam.getStart();
					end = pluginParam.getEnd();
					break;
				default:
					errorSink.append("plugin returned invalid code: ").append(pluginStatus);
					return false;
			}
		} else
			data = rawData;
		byte type = data[start];
		if (type == '#') {
			errorSink.append("Ok, ignoring comment");
			return true;
		}
		long seqnum = -1;
		long now = -1;

		start++;
		outer: while (start < end) {
			switch (data[start++]) {
				case '#': {
					if (seqnum != -1) {
						errorSink.append("Duplicate '#' at byte ").append(start);
						return false;
					}
					seqnum = 0;
					boolean ok = false;
					while (start < end) {
						byte val = data[start];
						if (val < '0' || val > '9')
							break;
						seqnum = seqnum * 10 + (val - '0');
						ok = true;
						start++;
					}
					if (!ok) {
						errorSink.append("Error with seqnum, must start with 0-9. At byte ").append(start);
						return false;
					}
					break;
				}
				case '@': {
					if (now != -1) {
						errorSink.append("Duplicate '@' at byte ").append(start);
						return false;
					}
					now = 0;
					boolean ok = false;
					while (start < end) {
						byte val = data[start];
						if (val < '0' || val > '9')
							break;
						now = now * 10 + (val - '0');
						ok = true;
						start++;
					}
					if (!ok) {
						errorSink.append("Error with timestamp, must start with 0-9. At byte ").append(start);
						return false;
					}
					break;
				}
				case '|': {
					int e = start;

					//parse key
					for (;;) {
						if (e == end) {
							if (start == e - 1)
								break outer;
							errorSink.append("Dangling key '");
							errorSink.append(new String(data, start, e - start));
							errorSink.append("' without '=' at byte ").append(start);
							return false;
						}
						byte c = data[e];

						if (c == '=')
							break;
						if (c != '_' && OH.isntBetween(c, 'a', 'z') && OH.isntBetween(c, 'A', 'Z') && OH.isntBetween(c, '0', '9')) {
							errorSink.append("Invalid char '").append((char) c).append("' in key at byte ").append(e);
							return false;
						} else if (e == start && c == '`') {
							errorSink.append("Invalid starting char '").append((char) c).append("' in key at byte ").append(e);
							return false;
						}
						e++;
					}
					if (e == start) {
						errorSink.append("empty key at byte ").append(e);
						return false;
					}

					char reserved;
					if (e - start == 1 && OH.isBetween((char) data[start], 'A', 'Z'))
						reserved = (char) data[start];
					else {
						reserved = 0;
						tmpKey.resetNoCheck(data, start, e);
					}
					start = e + 1;
					e = start;
					//					Object value;

					//parse value
					if (e == end) {
						errorSink.append("Dangling key without value at byte ").append(e);
						return false;
					} else {
						byte c = data[e];
						switch (c) {
							case 't':
								if (e + 3 < end && data[e + 1] == 'r' && data[e + 2] == 'u' && data[e + 3] == 'e') {
									if (reserved != 0)
										putReservedValue(reserved, Boolean.TRUE);
									else
										converter.appendBoolean(this.tmpKey, true);
									e += 4;
									break;
								} else {
									errorSink.append("Invalid value, strings wrapped in quotes (\"), numeric, 'true', 'false' or null, not: ").append((char) c)
											.append("' in key at byte ").append(e);
									return false;
								}
							case 'f':
								if (e + 4 < end && data[e + 1] == 'a' && data[e + 2] == 'l' && data[e + 3] == 's' && data[e + 4] == 'e') {
									if (reserved != 0)
										putReservedValue(reserved, Boolean.FALSE);
									else
										converter.appendBoolean(this.tmpKey, false);
									e += 5;
									break;
								} else {
									errorSink.append("Invalid value, strings wrapped in quotes (\"), numeric, 'true', 'false' or null, not: ").append((char) c)
											.append("' in key at byte ").append(e);
									return false;
								}
							case 'n':
								if (e + 3 < end && data[e + 1] == 'u' && data[e + 2] == 'l' && data[e + 3] == 'l') {
									if (reserved != 0)
										putReservedValue(reserved, null);
									else
										converter.appendNull(this.tmpKey);
									e += 4;
									break;
								} else {
									errorSink.append("Invalid value, strings wrapped in quotes (\"), numeric, 'true', 'false' or null, not: ").append((char) c)
											.append("' in key at byte ").append(e);
									return false;
								}
							case 'D':
								e++;
								converter.appendDouble(this.tmpKey, Double.longBitsToDouble(EncoderUtils.decodeLong64(data, e)));
								e += 11;
								break;
							case 'F':
								e++;
								converter.appendFloat(this.tmpKey, Float.intBitsToFloat(EncoderUtils.decodeInt64(data, e)));
								e += 6;
								break;
							case '\'':
							case '\"': {
								SH.clear(sbuf);
								char startChar = (char) c;
								for (;;) {
									if (++e == end) {
										errorSink.append("Value missing ending quote (").append(startChar).append(")  at byte ").append(e);
										return false;
									}
									c = data[e];
									if (c == '\\') {
										if (++e == end) {
											errorSink.append("Value missing ending quote (").append(startChar).append(")  at byte ").append(e);
											return false;
										}
										c = data[e];
										switch (c) {
											case 'r':
												sbuf.append('\r');
												break;
											case 'n':
												sbuf.append('\n');
												break;
											case 't':
												sbuf.append('\t');
												break;
											case 'f':
												sbuf.append('\f');
												break;
											case 'b':
												sbuf.append('\b');
												break;
											case 'u':
												if (e + 4 > end) {
													errorSink.append("Invalid unicode '\\").append((char) c).append("',should be in format: \\uFFFF  at byte ").append(e);
													return false;
												}
												final int n1 = (toHex(data[++e]) << 12) + (toHex(data[++e]) << 8) + (toHex(data[++e]) << 4) + (toHex(data[++e]));
												if (n1 >= 65536) {
													errorSink.append("Invalid unicode '\\").append((char) c).append("',should be in format: \\uFFFF  at byte ").append(e);
													return false;
												}
												sbuf.append((char) n1);
												break;
											case '\\':
											case '|':
											case '"':
											case '\'':
												sbuf.append((char) c);
												break;
											default: {
												errorSink.append("Invalid escape '\\").append((char) c).append("'  at byte ").append(e);
												return false;
											}

										}
									} else if (c == startChar) {
										e++;
										if (e < end) {
											switch (data[e]) {
												case 'J': //this string is json...validate + parse
													try {
														Object o = jsonConverter.stringToObject(sbuf);
														String value = jsonConverter.objectToString(o);
														if (reserved != 0)
															putReservedValue(reserved, value);
														else
															converter.append(this.tmpKey, value);
													} catch (Exception ex) {
														errorSink.append("Invalid JSON structure for field value at byte ").append(e - sbuf.length()).append(" at byte ").append(e)
																.append(": ").append(ex.getMessage());
														return false;
													}

													e++;
													break;
												case 'U': //this uuencoded data is json...validate + parse
													try {
														byte[] v = EncoderUtils.decode64(sbuf);
														if (reserved != 0)
															putReservedValue(reserved, v);
														else
															converter.append(this.tmpKey, v);

													} catch (Exception ex) {
														errorSink.append("Invalid base64 Uuencode for field value at byte ").append(e - sbuf.length()).append(" at byte ").append(e)
																.append(": ").append(ex.getMessage());
														return false;
													}
													if (sbuf.length() - 1 > maxlogBinarySize) {
														binaryStartEnds.add(e - sbuf.length());
														binaryStartEnds.add(e - 1);
													}

													e++;
													break;
												case 'T':
													String t = sbuf.substring(1);
													sbuf.setLength(0);
													if (++e >= end || data[e++] != '(') {
														errorSink.append("Bad Date Format, missing opening parenthesis: ").append(sbuf).append(" at byte ").append(e);
														return false;
													}
													for (;;) {
														if (e == end) {
															errorSink.append("Bad Date Format, missing closing parenthesis: ").append(sbuf).append(" at byte ").append(e);
															return false;
														}
														c = data[e++];
														if (c == ')')
															break;
														sbuf.append((char) c);
													}
													SimpleDateFormat sdf = getOrCreateDataParser(sbuf);
													if (sdf == null) {
														errorSink.append("Bad Date Format: ").append(sbuf).append(" at byte ").append(e);
														return false;
													}
													try {
														Date v = sdf.parse(t);
														if (reserved != 0)
															putReservedValue(reserved, v);
														else
															converter.appendDate(this.tmpKey, v);
													} catch (ParseException e1) {
														errorSink.append("Bad value for given date Format: ").append(sbuf).append(" ==> ").append(t).append(" at byte ").append(e);
														return false;
													}
													break;
												default:
													if (reserved != 0)
														putReservedValue(reserved, getValueFromPool(sbuf));
													else
														converter.appendString(this.tmpKey, sbuf);
											}
										} else {
											if (reserved != 0)
												putReservedValue(reserved, getValueFromPool(sbuf));
											else
												converter.appendString(this.tmpKey, sbuf);
										}
										break;
									} else
										sbuf.append((char) c);
								}
								break;
							}
							case '-':
							case '+':
							case '.':
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9': {
								boolean isDec = c == '.';
								boolean isBig = false;
								boolean isUtc = false;
								SH.clear(sbuf);
								sbuf.append((char) c);
								for (;;) {
									if (++e < end) {
										c = data[e];
										switch (c) {
											case 'e':
											case 'E':
											case 'f':
											case 'F':
											case '.':
												isDec = true;
											case '-':
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
												sbuf.append((char) c);
												continue;
											case 'D':
											case 'd':
												isDec = true;
												isBig = true;
												e++;
												break;
											case 'L':
											case 'l':
												isDec = false;
												isBig = true;
												e++;
												break;
											case 'T':
											case 't':
												isBig = true;
												isDec = false;
												isUtc = true;
												e++;
												break;
										}
									}
									try {
										if (isBig) {
											if (isDec) {
												double v = SH.parseDouble(sbuf);
												if (reserved != 0)
													putReservedValue(reserved, OH.valueOf(v));
												else
													converter.appendDouble(this.tmpKey, v);
											} else if (isUtc) {
												long time = SH.parseLong(sbuf, 10);
												if (time > AmiDataEntity.MAX_UTC_VALUE) {
													errorSink.append("UTC too large: ").append(sbuf).append(" at byte ").append(e);
													return false;
												} else if (time < 0) {
													errorSink.append("UTC negative: ").append(sbuf).append(" at byte ").append(e);
													return false;
												}
												if (reserved != 0)
													putReservedValue(reserved, new Date(time));
												else
													converter.appendDate(this.tmpKey, time);
											} else {
												long v = SH.parseLong(sbuf, 10);
												if (reserved != 0)
													putReservedValue(reserved, OH.valueOf(v));
												else
													converter.appendLong(this.tmpKey, v);
											}
										} else if (isDec) {
											float v = SH.parseFloat(sbuf);
											if (reserved != 0)
												putReservedValue(reserved, OH.valueOf(v));
											else
												converter.appendFloat(this.tmpKey, v);
										} else {
											int v = SH.parseInt(sbuf);
											if (reserved != 0)
												putReservedValue(reserved, OH.valueOf(v));
											else
												converter.appendInt(this.tmpKey, v);
										}
									} catch (Exception ex) {
										errorSink.append("Invalid numeric: ").append(sbuf).append(" at byte ").append(e);
										return false;
									}
									break;
								}
								break;
							}
							default: {
								errorSink.append("Invalid value, strings wrapped in quotes (\"), numeric, 'true', 'false' or null, not: ").append((char) c)
										.append("' in key at byte ").append(e);
								return false;
							}

						}
					}
					start = e;
					break;
				}
				default: {
					errorSink.append("Unknown char '").append((char) data[start - 1]).append("' at byte ").append(start);
					return false;
				}
			}
		}
		return processMessage(type, seqnum, now, errorSink);
	}
	private void putReservedValue(char key, Object val) {
		int keyPos = key - 'A';
		this.reservedValuesBitset = MH.setBits(this.reservedValuesBitset, 1 << keyPos, true);
		this.reservedValues[keyPos] = val;
	}

	private SimpleDateFormat getOrCreateDataParser(CharSequence sbuf) {
		SimpleDateFormat dateParser = this.dataformatters.get(sbuf);
		if (dateParser == null) {
			String s = sbuf.toString();
			try {
				dateParser = new SimpleDateFormat(s);
			} catch (Exception e) {
				return null;
			}
			this.dataformatters.put(s, dateParser);
		}
		return dateParser;
	}

	private static int toHex(byte b) {
		if (OH.isBetween(b, '0', '9'))
			return b - '0';
		else if (OH.isBetween(b, 'A', 'F'))
			return b - 'A' + 10;
		else if (OH.isBetween(b, 'a', 'f'))
			return b - 'a' + 10;
		return 65536;
	}

	final private HasherMap<CharSequence, String> valuePool = new HasherMap<CharSequence, String>(new CharSequenceHasher());

	private String getValueFromPool(StringBuilder key) {
		String r = valuePool.get(key);
		if (r == null) {
			r = key.toString();
			if (valuePool.size() < STRING_POOL_SIZE) {
				valuePool.put(r, r);
				return r;
			}
		}
		return r;

	}

	private final boolean processMessage(byte type, long seqnum, long now, StringBuilder errorSink) {
		long localNow = EH.currentTimeMillis();
		if (seqnum != -1) {
			if (seqnum != nextSeqNum) {
				nextSeqNum = seqnum;
			}
		}
		if (now != -1L) {
		}
		switch (type) {
			case 'L'://login
			case 'l': {
				if (isLoggedIn) {
					errorSink.append("Already logged in");
					return false;
				}
				String id = removeReservedValue('I', String.class, true, errorSink, "Application Instance ID");
				if (id == null)
					return false;
				final String plugin;
				if (isReservedValueSet('P')) {
					plugin = removeReservedValue('P', String.class, false, errorSink, "Plugin");
					if (!setPlugin(plugin, errorSink))
						return false;
				} else
					plugin = null;
				final String options;
				if (isReservedValueSet('O')) {
					options = removeReservedValue('O', String.class, false, errorSink, "Options");
					if (options == null) {
						return false;
					} else {
						for (String option : SH.split(',', options)) {
							if (OPTION_QUIET.equals(option)) {
								this.optionQuiet = true;
							} else if (OPTION_LOG.equals(option)) {
								this.optionLog = true;
							} else if (OPTION_NOCR.equals(option)) {
								this.optionSendCr = false;
							} else if (OPTION_TTY.equals(option)) {
								enableTty();
							} else if (OPTION_CR.equals(option)) {
								this.optionSendCr = true;
							} else {
								errorSink.append("Valid options are [").append(OPTION_QUIET).append(',').append(OPTION_LOG).append(',').append(OPTION_CR).append(',')
										.append(OPTION_TTY).append(',').append(OPTION_NOCR).append("], Not: ").append(option);
								return false;
							}
						}
					}
				} else
					options = null;

				this.setAppId(id);
				this.partitionId = "VAM_" + this.getRemoteIp() + "_" + getAppId();
				if (!validateReservedValuesEmpty(errorSink))
					return false;
				errorSink.append("Welcome to 3forge AMI v1.0");
				this.getAmiRelayIn().onLogin(options, plugin, toBytes());
				if (optionTty) {
					onOptionTty();
				}
				isLoggedIn = true;
				break;
			}
			case 'D'://object
			case 'd': {
				if (!isLoggedIn) {
					errorSink.append("Must login (L) before sending objects");
					return false;
				}
				String id = removeReservedValue('I', String.class, false, errorSink, "Object ID");
				String typ = removeReservedValue('T', String.class, true, errorSink, "Object Type");
				if (typ == null)
					return false;
				if (!validateReservedValuesEmpty(errorSink))
					return false;
				this.getAmiRelayIn().onObjectDelete(seqnum, id, typ, toBytes());
				break;
			}
			case 'a':
			case 'A':
			case 'O'://object
			case 'o': {
				if (!isLoggedIn) {
					errorSink.append("Must login (L) before sending objects");
					return false;
				}
				String id = removeReservedValue('I', String.class, false, errorSink, "Object ID");
				String typ = removeReservedValue('T', String.class, true, errorSink, "Object Type");
				if (typ == null)
					return false;
				if (typ.startsWith("__")) {
					errorSink.append("Can not use reserved type: " + typ);
					return false;
				}
				long expires;
				if (isReservedValueSet('E')) {
					Long exp = removeReservedValue('E', Long.class, false, errorSink, "Expires on");
					if (exp == null) {
						return false;
					} else {
						if (exp > 0) {
							expires = exp;
							if (expires < localNow) {
								errorSink.append("Ignoring expiry time because it is in the past. ");
								expires = 0L;
							}
						} else if (exp < 0)
							expires = localNow - exp;
						else
							expires = 0L;
					}
				} else
					expires = 0L;
				if (!validateReservedValuesEmpty(errorSink))
					return false;

				if (isReservedValueSet('L') && ZERO.equals(removeReservedValue('L', Integer.class, true, errorSink, "Level"))) {
					this.getAmiRelayIn().onObjectDelete(seqnum, id, typ, toBytes());
				} else {
					this.getAmiRelayIn().onObject(seqnum, id, typ, expires, toBytes());
				}
				break;
			}
			case 'R'://response
			case 'r': {
				if (!isLoggedIn) {
					errorSink.append("Must login (L) before responding to requests");
					return false;
				}
				String id = removeReservedValue('I', String.class, true, errorSink, "Original Request ID");
				if (id == null)
					return false;
				final String amiScript = removeReservedValue('X', String.class, false, errorSink, "AmiScript");
				String msg = removeReservedValue('M', String.class, false, errorSink, "Message");
				Object vals = removeReservedValue('V', Object.class, false, errorSink, "Values");
				//								if (vals != null) {
				//									for (Map<String, Object> p : vals)
				//										if (!validateParamKeys(p, errorSink, 'O'))
				//											return false;
				//								}

				Integer status = removeReservedValue('S', Integer.class, true, errorSink, "Status");
				if (status == null)
					return false;
				if (!this.pendingRequests.remove(id)) {
					errorSink.append("Unknown Original Request ID: '").append(id).append("'");
					return false;
				}
				if (!validateReservedValuesEmpty(errorSink))
					return false;
				putReservedValue('V', vals);
				this.getAmiRelayIn().onResponse(id, status, msg, amiScript, AmiRelayMapToBytesConverter.toMap(toBytes()));
				errorSink.append("OK, response accepted");
				break;
			}
			case 'X'://clean exit
			case 'x': {
				errorSink.append("OK, goodbye");
				this.open = false;
				if (this.isLoggedIn) {
					this.isLoggedIn = false;
					if (!validateReservedValuesEmpty(errorSink))
						return false;
					if (!isLoggedOut) {
						isLoggedOut = true;
						this.getAmiRelayIn().onLogout(toBytes(), true);
					}
				}
				break;
			}
			case 'S'://status
			case 's':
				if (!isLoggedIn) {
					errorSink.append("Must login (L) before updating statuses");
					return false;
				}
				if (!validateReservedValuesEmpty(errorSink))
					return false;
				this.getAmiRelayIn().onStatus(toBytes());
				break;
			case 'C'://command definition
			case 'c': {
				if (!isLoggedIn) {
					errorSink.append("Must login (L) before defining commands");
					return false;
				}
				final String id = removeReservedValue('I', String.class, true, errorSink, "Command ID");
				if (id == null)
					return false;
				final String args = removeReservedValue('A', String.class, false, errorSink, "Arguments");
				final String help = removeReservedValue('H', String.class, false, errorSink, "Help");
				final String title;
				if (isReservedValueSet('N')) {
					title = removeReservedValue('N', String.class, true, errorSink, "Name");
					if (title == null)
						return false;
				} else
					title = id;

				int len = errorSink.length();
				final String whereClause = removeReservedValue('W', String.class, false, errorSink, "Where");
				final String filterClause = removeReservedValue('T', String.class, false, errorSink, "filTer");
				final String enabledClause = removeReservedValue('E', String.class, false, errorSink, "Enabled Clause");
				final String style = removeReservedValue('S', String.class, false, errorSink, "Style");
				final String amiScript = removeReservedValue('X', String.class, false, errorSink, "AmiScript");
				final String selectMode = removeReservedValue('M', String.class, false, errorSink, "Selection Mode");
				final String fields = removeReservedValue('F', String.class, false, errorSink, "Fields");
				Integer priority = OH.noNull(removeReservedValue('P', Integer.class, false, errorSink, "Priority"), -1);
				Integer lvl = removeReservedValue('L', Integer.class, false, errorSink, "Permissions Level");
				final String callbacks = removeReservedValue('C', String.class, false, errorSink, "Conditions");
				if (errorSink.length() > len)
					return false;
				int callbacksMask = 0;
				if (callbacks == null)
					callbacksMask = AmiRelayCommandDefMessage.CALLBACK_USER_CLICK;
				else {
					for (String callback : SH.split(',', callbacks)) {
						if ("now".equals(callback))
							callbacksMask |= AmiRelayCommandDefMessage.CALLBACK_NOW;
						else if ("user_click".equals(callback))
							callbacksMask |= AmiRelayCommandDefMessage.CALLBACK_USER_CLICK;
						else if ("user_close_layout".equals(callback))
							callbacksMask |= AmiRelayCommandDefMessage.CALLBACK_USER_LOGOUT;
						else if ("user_open_layout".equals(callback))
							callbacksMask |= AmiRelayCommandDefMessage.CALLBACK_USER_LOGIN;
						else {
							errorSink.append("invalid callback: ").append(callback);
							return false;
						}
					}
				}
				if (lvl == null)
					lvl = 1;
				final String targetType = removeReservedValue('T', String.class, false, errorSink, "T");
				if (!validateReservedValuesEmpty(errorSink))
					return false;
				this.getAmiRelayIn().onCommandDef(id, title, lvl, whereClause, filterClause, help, args, amiScript, priority, enabledClause, style, selectMode, fields, toBytes(),
						callbacksMask);
				errorSink.append("OK, command accepted");
				break;
			}
			case 'H'://help
			case 'h': {
				errorSink.append(
						"Valid commands are: L (login) , S (status) ,A (alert), O (Object),D (Delete object) C (command def), X (exit), R (response), H (help), W (wait), V (Invoke plugin)");
				return true;
			}
			case 'V'://invoke
			case 'v': {
				String typ = removeReservedValue('T', String.class, true, errorSink, "Invokable Type");
				if (typ == null)
					return false;
				AmiRelayInvokablePlugin plugin = getManager().getInvokable(typ);
				if (plugin == null) {
					errorSink.append("Invalid invokable type: ").append(typ).append(" Valid types are: [");
					SH.join(",", getManager().getInvokableTypes(), errorSink).append(']');
					return false;
				}
				try {
					return invokePlugin(AmiRelayMapToBytesConverter.toMap(toBytes()), errorSink, plugin);
				} catch (Throwable t) {
					String ticket = this.getManager().getTools().generateErrorTicket();
					LH.warning(log, "Ticket ", ticket, " -- Error executing Invokable Plugin ", typ, ": ", t);
					errorSink.append("Plugin threw ").append(t.getClass().getName()).append(". Reference ticket ").append(ticket).append(" in the log file for details");
					return false;
				}
			}
			case 'P'://pause
			case 'p': {
				Integer delay = removeReservedValue('D', Integer.class, true, errorSink, "delay");
				if (delay == null)
					return false;
				if (delay > 0) {
					OH.sleep(delay);
					errorSink.append("OK, waited ");
					SH.formatDuration(delay, errorSink);
				} else {
					errorSink.append("Delay must be positive value");
					return false;
				}
				break;
			}
			default:
				errorSink.append("Invalid instruction type: ").append((char) type);
				return false;
		}
		return true;
	}
	protected boolean isReservedValueSet(char key) {
		int keyPos = key - 'A';
		return MH.anyBits(this.reservedValuesBitset, 1 << keyPos);
	}

	protected void onOptionTty() {

	}
	private boolean setPlugin(String pluginNameAndSwitches, StringBuilder errorSink) {
		String pluginName = SH.beforeFirst(pluginNameAndSwitches, ';', pluginNameAndSwitches);
		String pluginSwitches = SH.afterFirst(pluginNameAndSwitches, ';', "");
		if (SH.isnt(pluginName)) {
			errorSink.append("Invalid Plugin, class name can not be empty: ");
			return false;
		}
		final Class<?> clazz;
		try {
			clazz = Class.forName(pluginName);
		} catch (ClassNotFoundException e) {
			LH.info(amilog, "Error loading plugin: ", pluginName, e);
			errorSink.append("Invalid Plugin, class not found: ").append(pluginName);
			return false;
		}
		if (!AmiRelayPlugin.class.isAssignableFrom(clazz)) {
			LH.info(amilog, "Error loading plugin: ", pluginName);
			errorSink.append("Invalid Plugin, must implement ").append(AmiRelayPlugin.class.getName()).append(": ").append(pluginName);
			return false;
		}
		final Object instance;
		try {
			instance = clazz.newInstance();
		} catch (Exception e) {
			LH.info(amilog, "Error loading plugin: ", pluginName, e);
			errorSink.append("Invalid Plugin, Could not access default constructor: ").append(pluginName);
			return false;
		}
		AmiRelayPlugin plugin = (AmiRelayPlugin) instance;
		try {
			if (!plugin.init(this.getManager().getTools(), this, pluginSwitches, errorSink))
				return false;
		} catch (Exception e) {
			LH.info(amilog, "Error loading plugin: ", pluginName, e);
			errorSink.append("Invalid Plugin, init(...) failed: ").append(pluginName);
			return false;
		}
		this.plugin = plugin;
		return true;
	}

	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);
		this.maxlogBinarySize = props.getOptional("ami.log.binary.max", 2048);
		this.autologinCommand = props.getOptional(PROP_AUTOLOGIN);
		this.optionSendCr = props.getOptional("ami.send.cr", EH.isWindows());
		optionLog = props.getOptional(PROP_LOG_MESSAGES, Boolean.TRUE);
		StringBuilder sbuf = new StringBuilder();
		sbuf.append("CC-");
		SH.rightAlign('0', SH.toString(this.getId()), 4, false, sbuf);
		this.connectionIdString = SH.toStringAndClear(sbuf);
	}

	private boolean validateReservedValuesEmpty(StringBuilder errorSink) {
		if (reservedValuesBitset == 0)
			return true;
		for (int i = MH.indexOfBitSet(this.reservedValuesBitset, 0); i >= 0; i = MH.indexOfBitSet(this.reservedValuesBitset, i + 1)) {
			char c = (char) i;
			c += 'A';
			if (c != 'A' && c != 'L' && c != 'U') {//TODO: this seems weird, need to figure out which scenarios this makes sense for
				errorSink.append("Can not use reserved key: '").append((char) (i + 'A')).append('\'');
				return false;
			}
		}
		return true;
	}
	@SuppressWarnings("unchecked")
	private <T> T removeReservedValue(char key, Class<T> type, boolean required, StringBuilder errorSink, String description) {
		int keyPos = key - 'A';
		if (!MH.anyBits(this.reservedValuesBitset, 1 << keyPos)) {
			if (required)
				errorSink.append("Missing required ").append(description).append(" Field: '").append(key).append("'");
			return null;
		}
		this.reservedValuesBitset = MH.clearBits(this.reservedValuesBitset, 1 << keyPos);
		Object r = this.reservedValues[keyPos];
		if (r == null) {
			if (required)
				errorSink.append("Missing required ").append(description).append(" Field: '").append(key).append("'");
			return null;
		}
		if (r.getClass() != type && !type.isAssignableFrom(r.getClass())) {
			errorSink.append("Field '").append(key).append("' must be a ").append(OH.getSimpleName(type));
			return null;
		} else {
			return (T) r;
		}
	}

	@Override
	public void call(AmiRelayServer server, AmiRelayRunAmiCommandRequest action, StringBuilder errorSink) {
		call(action.getCommandUid(), action.getCommandDefinitionId(), action.getArguments(), action.getObjectTypes(), action.getObjectIds(), action.getFields(),
				action.getInvokedBy(), action.getSessionId(), action.getIsManySelect(), action.getAmiObjectIds());
	}

	private void call(String id, String cmd, Map<String, Object> arguments, String[] objectTypes, String[] objectIds, List<Map<String, Object>> fields, String invokedBy,
			String sessionId, boolean isMulti, long[] amiObjectIds) {
		if (!isLoggedIn)
			throw new RuntimeException("can not call command prior to login");

		String ttyLine;
		StringBuilder sink = new StringBuilder();
		if (optionTty) {
			sink.append(SH.CHAR_NEWLINE).append(SH.CHAR_RETURN);
		}
		sink.append("E@");
		sink.append(EH.currentTimeMillis());
		pendingRequests.add(id);
		append(sink, 'C', cmd, true);
		append(sink, 'I', id, true);
		append(sink, 'U', invokedBy, true);
		append(sink, 'S', sessionId, true);

		//add args as is
		if (arguments != null) {
			for (Entry<String, Object> e : arguments.entrySet()) {
				sink.append('|').append(e.getKey()).append("=");
				AmiUtils.appendObject(sink, e.getValue());
			}
		}

		if (!isMulti) {
			if (objectTypes != null && objectTypes[0] != null)
				append(sink, 'T', objectTypes[0], true);
			if (objectIds != null && objectIds.length > 0 && objectIds[0] != null)
				append(sink, 'O', objectIds[0], true);
			if (amiObjectIds != null)
				sink.append("|o=").append(amiObjectIds[0]).append("L");
			if (CH.isntEmpty(fields)) {
				Map<String, Object> m = fields.get(0);
				for (Entry<String, Object> e : m.entrySet()) {
					sink.append('|').append(e.getKey()).append("=");
					AmiUtils.appendObject(sink, e.getValue());
				}
			}
		} else {
			//send multiple records in V field in json format
			if (objectIds != null) {
				sink.append("|V=\"");

				JsonBuilder jb = new JsonBuilder();
				jb.startList();
				for (int i = 0; i < objectIds.length; i++) {
					//add Object id
					jb.startMap();
					if (objectIds[i] != null) {
						jb.addKeyValueQuoted('O', objectIds[i]);
					}
					if (amiObjectIds != null)
						jb.addKeyValue("o", (long) amiObjectIds[i]);

					//add fields of the record
					if (CH.isntEmpty(fields)) {
						Map<String, Object> m = fields.get(i);
						for (Entry<String, Object> e : m.entrySet()) {
							Object v = e.getValue();
							if (v instanceof Number)
								jb.addKeyValue(e.getKey(), (Number) v);
							else if (v instanceof Boolean)
								jb.addKeyValue(e.getKey(), (Boolean) v);
							else if (v instanceof byte[]) {
								sink.append('"');
								EncoderUtils.encode64UrlSafe((byte[]) v, sink);
								sink.append('"');
							} else if (v != null)
								jb.addKeyValueQuoted(e.getKey(), v);
						}
					}

					jb.endMap();
				}
				jb.endList();
				AmiUtils.escape(jb.getStringBuilder(), sink);
				sink.append("\"J");
			}
		}
		sink.append(SH.CHAR_NEWLINE);
		if (optionSendCr || optionTty)
			sink.append(SH.CHAR_RETURN);
		sendToOutput(sink);
	}
	private void append(StringBuilder sink, char c, String text, boolean includeNull) {
		if (text != null) {
			sink.append('|').append(c).append("=\"");
			SH.escape(text, '"', '\\', sink);
			sink.append('"');
		} else if (includeNull)
			sink.append('|').append(c).append("=null");
	}

	public String getPartitionId() {
		return partitionId;
	}

	@Override
	protected void login() {
		if (autologinCommand != null) {
			if (!processLine(autologinCommand))
				LH.warning(log, getName(), " ==> AUTO-LOGIN FAILED.");
		}
		super.login();
	}

	final StringBuilder processLineBuf_sink = new StringBuilder();
	final StringBuilder processLineBuf_outbuf = new StringBuilder();
	final FastByteArrayOutputStream processLineBuf_inbuf = new FastByteArrayOutputStream();
	private Lock processLineBuf_lock = new ReentrantLock();

	protected boolean processLine(String text) {
		byte[] bytes = text.getBytes();
		processLineBuf_inbuf.reset(Math.max(1024, bytes.length));
		processLineBuf_inbuf.write(bytes);
		try {
			processLineBuf_lock.lock();
			processLineBuf_sink.setLength(0);
			processLineBuf_outbuf.setLength(0);
			return processLine(processLineBuf_inbuf, processLineBuf_outbuf, processLineBuf_sink);
		} catch (IOException e) {
			LH.warning(log, processLineBuf_sink, " Data: ", text, e);
			return false;
		} finally {
			processLineBuf_lock.unlock();
		}
	}

	public AmiRelayPlugin getPlugin() {
		return this.plugin;
	}

	public void setClientDescription(String str) {
		this.clientDescription = str;
	}

	protected boolean invokePlugin(Map<String, Object> params, StringBuilder errorSink, AmiRelayInvokablePlugin plugin) {
		return plugin.invoke(this.getManager(), params, this, errorSink);
	}
	protected void enableTty() {
		this.optionTty = true;
	}

}
