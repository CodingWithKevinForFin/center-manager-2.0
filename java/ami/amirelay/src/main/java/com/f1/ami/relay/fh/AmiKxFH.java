package com.f1.ami.relay.fh;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiNamingServiceResolver;
import com.f1.ami.amicommon.AmiNamingServiceResolverHelper;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.relay.AmiRelayIn;
import com.f1.base.DateNanos;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.json.JsonBuilder;

import kx.c;
import kx.c.Dict;
import kx.c.Flip;
import kx.c.KException;
import kx.c.Minute;
import kx.c.Month;
import kx.c.Second;
import kx.c.Timespan;

public class AmiKxFH extends AmiFHBase {

	private static final int RECONNECT_INTERVAL_MS = 5000;

	private static class TableEntry {

		final private Getter[] getters;
		final private Getter kg;

		public TableEntry(Getter[] getters, Getter kg) {
			this.getters = getters;
			this.kg = kg;
		}

	}

	static final String SUB_DEFAULT = ".u.sub[`;`]; (.u `i`L;.u.t!{0!meta x} each .u.t)";
	public static String PROP_KX_URL = "kxUrl";
	public static String PROP_KX_USERNAME = "kxUsername";
	public static String PROP_KX_PASSWORD = "kxPassword";
	public static String PROP_KX_REPLAY_URL = "replayUrl";
	public static String PROP_KEY_MAP = "tableKeyMap";
	public static String PROP_COLTYPE_OVERRIDE = "columnTypesOverride";
	public static String PROP_KX_SUBQUERY = "subscribeQuery";
	public static String PROP_KX_DEBUG = "debug";
	public static String PROP_KX_DATE_FORMAT = "date.format";
	public static String PROP_KX_DATE_TIMEZONE = "date.timezone";
	public static String PROP_KX_TIMESTAMP_USENANOS = "timestamp.usenanos";

	private static final Logger log = LH.get();
	private static final String PLUGIN_IN = "KDB_FH";
	private Thread t;
	private HashMap<String, TableEntry> tableParamMap = new HashMap<String, TableEntry>();
	// key -> tableName
	// value -> map of (colName,overrideType)
	private LinkedHashMap<String, HashMap<String, String>> tableColOverride = new LinkedHashMap<String, HashMap<String, String>>();
	private String replayUrl = null;
	private String kxUrl;
	private HashMap<String, String[]> tableKeymap = new HashMap<String, String[]>();
	private String sub = SUB_DEFAULT;
	private c c; // connection object

	private AmiNamingServiceResolver serviceResolver;
	private String kxUsername;
	private String kxPassword;
	private boolean debug;
	private String dateFormat;
	private String dateTimezone;
	private SimpleDateFormat dateFormatter;
	private TimeZone dateTz;
	private boolean useNanos = false;

	@SuppressWarnings("unchecked")
	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);
		kxUrl = this.props.getRequired(PROP_KX_URL);
		sub = this.props.getOptional(PROP_KX_SUBQUERY, SUB_DEFAULT);
		replayUrl = this.props.getOptional(PROP_KX_REPLAY_URL);
		kxUsername = this.props.getOptional(PROP_KX_USERNAME);
		kxPassword = this.props.getOptional(PROP_KX_PASSWORD);
		dateFormat = this.props.getOptional(PROP_KX_DATE_FORMAT);
		dateTimezone = this.props.getOptional(PROP_KX_DATE_TIMEZONE);
		useNanos = this.props.getOptional(PROP_KX_TIMESTAMP_USENANOS, Boolean.FALSE);
		if (SH.is(dateFormat)) {
			this.dateFormatter = new SimpleDateFormat(dateFormat);
			if (SH.is(dateTimezone)) {
				this.dateTz = TimeZone.getTimeZone(dateTimezone);
				this.dateFormatter.setTimeZone(this.dateTz);
			}
		}

		debug = this.props.getOptional(PROP_KX_DEBUG, false);
		serviceResolver = AmiNamingServiceResolverHelper.getService(getManager().getTools().getContainer());

		//expected in the following format: table1=col1, col2, coln|table2=col1, col2
		Map<String, String> m = SH.splitToMap('|', '=', this.props.getOptional(PROP_KEY_MAP));
		for (Map.Entry<String, String> e : m.entrySet())
			tableKeymap.put(e.getKey(), SH.trimArray(SH.split(',', e.getValue())));

		Properties properties = this.props.getSubPropertyController(PROP_COLTYPE_OVERRIDE + ".").getProperties();
		for (Entry<Object, Object> e : properties.entrySet()) {
			String tableName = e.getKey().toString();
			HashMap<String, String> m2 = (HashMap<String, String>) SH.splitToMap(",", "=", e.getValue().toString());
			this.tableColOverride.put(tableName, m2);
		}
		getAmiRelayIn().onConnection(EMPTY_PARAMS);
		login();
	}
	@Override
	public void start() {
		super.start();
		try {
			onStart();
			onStartFinish(true);
		} catch (Exception e) {
			onStartFinish(false);
			LH.log(log, Level.SEVERE, "Failed to start up the FH", e);
		}
	}

	@Override
	public void stop() {
		super.stop();
		onStopFinish(onStop());
	}

	private void onStart() throws Exception {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				c = null;
				Object o[] = null;
				while (true) {
					try {
						//connect
						if (c == null || !isConnected(c)) {
							o = null;
							c = GetConnection(kxUrl, kxUsername, kxPassword, serviceResolver);
							if (c == null) {
								LH.severe(log, "Failed to establish a kx connection, will retry in " + RECONNECT_INTERVAL_MS + "ms");
								onFailed("Failed to establish a kx connection: " + kxUrl);
								OH.sleep(RECONNECT_INTERVAL_MS);
								continue;
							}
							LH.info(log, "Successfully connect to kx via " + kxUrl);
							//set conn attributes
							setConnectionTime(getManager().getTools().getNow());
							setRemoteIp(c.s.getInetAddress().getHostName());
							setRemotePort(c.s.getPort());

							//sub to all tables and request schemas to be cached + sequence + logfile
							Object[] data = (Object[]) c.k(sub);

							//read schema
							readSchema(data[1]);

							//recover with sequence/log
							recover(data[0]);

							log.info("resuming msg processing after recovery");
						}

						//start listening for data
						o = (Object[]) c.k();

						pubToAMI((String) Array.get(o, 1), Array.get(o, 2));

					} catch (Exception e) {
						LH.severe(log, "Exception occurred while listening for updates from kx: ", Arrays.toString(o), e, "will attempt to reconnect in " + RECONNECT_INTERVAL_MS);
						onFailed("Connection to KX has been closed");
						OH.sleep(RECONNECT_INTERVAL_MS);
					}
				}

				//should never log out...we want to keep this FH around
				//				logout();
			}
		};

		t = getManager().getThreadFactory().newThread(r);
		t.setDaemon(true);
		t.setName("KX subscriber for url - " + this.kxUrl);
		t.start();
	}

	protected void recover(Object params) {
		if (replayUrl != null) {
			//attempt to recover from the recovery service
			c c = null;
			try {
				long chunks = (Long) ((Object[]) params)[0];

				if (chunks < 1)
					return; //nothing to recover

				log.info("Connecting to replay service - " + replayUrl);
				c = GetConnection(replayUrl, kxUsername, kxPassword, serviceResolver);
				if (c == null)
					return;

				c.ks("replay", params); //start replay

				log.info("Recovering " + chunks + " chunks");
				Flip f = new Flip(new Dict(null, null));
				for (int i = 0; i < chunks; i++) {
					Object o = c.k(); //this is just data...build a flip out of it
					f.y = (Object[]) Array.get(o, 2);
					f.x = new String[f.y.length];
					pubToAMI((String) Array.get(o, 1), f);
				}

				log.info("Finished recovering " + chunks + " chunks");
			} catch (NumberFormatException e) {
				log.log(Level.SEVERE, "Failed to replay/recover data...problem with url - " + replayUrl, e);
			} catch (KException e) {
				log.log(Level.SEVERE, "Failed to replay/recover data...kx exception...url - " + replayUrl, e);
			} catch (IOException e) {
				log.log(Level.SEVERE, "Failed to replay/recover data...failed to connect with url - " + replayUrl, e);
			} finally {
				if (c != null)
					try {
						c.close();
					} catch (Exception e) {
						LH.warning(log, e);
					}
			}
		}
	}

	protected void readSchema(Object meta) {
		Dict d = (Dict) meta;
		Flip f = new Flip(d);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < f.x.length; i++) {
			// TODO ADD
			String tableName = f.x[i];
			Flip s = (Flip) f.y[i];
			int cols = Array.getLength(s.y[0]);
			HashMap<String, String> curColOverrideConfig = null;
			// retrieve the col override config for this table if exists
			if (this.tableColOverride.containsKey(tableName)) {
				curColOverrideConfig = this.tableColOverride.get(tableName);
			}

			final Getter[] getters = new Getter[cols];

			String[] keys = tableKeymap.containsKey(tableName) ? tableKeymap.get(tableName) : new String[0];
			final int[] keyGetters = new int[keys.length];

			if (this.debug == true) {
				sb.append("Subscribing to table `").append(tableName);
				sb.append("` with ").append(cols);
				sb.append(" columns:");
				for (int c = 0; c < cols; c++) {
					final String name = (String) Array.get(s.y[0], c);
					Character type = Array.getChar(s.y[1], c);
					if (c != 0)
						sb.append(',');
					sb.append("i ").append(c);
					sb.append(" name: `").append(name);
					sb.append("` type: `").append(type);
					sb.append("` ");
				}
				LH.info(log, SH.toStringAndClear(sb));
			}

			//assuming that columns always come in the same order...as they should with standard TP
			for (int c = 0; c < cols; c++) {
				final String name = (String) Array.get(s.y[0], c);
				Character type = Array.getChar(s.y[1], c);
				// check for override
				if (curColOverrideConfig != null && curColOverrideConfig.containsKey(name)) {
					String tmp = curColOverrideConfig.get(name);
					if (SH.isChar(tmp)) {
						Character newType = SH.parseChar(tmp);
						if (this.debug) {
							LH.info(log, "For table `", tableName, "`: column `", name, "` type ", type, ", will be overrided to type ", newType);
						}
						type = newType;
					}
				}
				initKXGetters(type, name, getters, c);

				//check if col matches a key
				int ki = AH.indexOf(name, keys);
				if (ki > -1)
					keyGetters[ki] = c;
			}

			Getter kg;
			if (keys.length > 0) {
				kg = new Getter() {
					final String n = "I";

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						//note a is an array of cols
						for (int k = 0; k < keyGetters.length; k++) {
							int gi = keyGetters[k];
							getters[gi].appendVal(Array.get(a, gi), i, buf);
							buf.append(':');
						}

						buf.setLength(buf.length() - 1); //remove last :
					}

					private StringBuilder buf = new StringBuilder(200);

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						SH.clear(buf);
						appendVal(a, i, buf);
						sink.appendString(n, buf.toString());
					}

				};

			} else
				kg = null;

			//store entries and the map
			tableParamMap.put(tableName, new TableEntry(getters, kg));
		}

	}

	private void initKXGetters(Character type, final String name, Getter[] getters, int c) {

		// Supported types:
		//		b x h i j J e f F g c C s z p d t n m u v
		//		TODO: B I S T
		switch (type) {
			case 'b': //boolean
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append(Array.getBoolean(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						try {
							sink.appendBoolean(n, Array.getBoolean(a, i));
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", a.getClass().getName(), " to type b");
						}
					}
				};
				break;
			case 'B': // arr boolean
				getters[c] = new Getter() {
					final String n = name;
					final JsonBuilder jb = new JsonBuilder();

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append((boolean[]) Array.get(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						Object obj = Array.get(a, i);
						try {
							jb.add((boolean[]) obj);
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", obj.getClass().getName(), " to type B");
						}

						sink.appendString(name, jb.toString());
						jb.clear();
					}
				};
				break;
			case 'x': // byte
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append(Array.getByte(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						try {
							sink.appendByte(n, Array.getByte(a, i));
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", a.getClass().getName(), " to type x");
						}
					}
				};
				break;
			case 'h': // short
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append(Array.getShort(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						try {
							sink.appendShort(n, Array.getShort(a, i));
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", a.getClass().getName(), " to type h");
						}
					}
				};
				break;
			case 'i': // int
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append(Array.getInt(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						try {
							sink.appendInt(n, Array.getInt(a, i));
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", a.getClass().getName(), " to type i");
						}
					}
				};
				break;
			case 'I': // arr int
				getters[c] = new Getter() {
					final String n = name;
					final JsonBuilder jb = new JsonBuilder();

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append((int[]) Array.get(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						Object obj = Array.get(a, i);
						try {
							jb.add((int[]) obj);
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", obj.getClass().getName(), " to type I");
						}

						sink.appendString(n, jb.toString());
						jb.clear();
					}
				};
				break;
			case 'j': // long
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append(Array.getLong(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						try {
							sink.appendLong(n, Array.getLong(a, i));
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", a.getClass().getName(), " to type j");
						}
					}
				};
				break;
			case 'J': // arr longs
				getters[c] = new Getter() {
					final String n = name;
					final JsonBuilder jb = new JsonBuilder();

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append((long[]) Array.get(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						Object obj = Array.get(a, i);
						try {
							jb.add((long[]) obj);
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", obj.getClass().getName(), " to type J");
						}

						sink.appendString(n, jb.toString());
						jb.clear();
					}
				};

				break;
			case 'e': // real
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append(Array.getFloat(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						try {
							sink.appendFloat(n, Array.getFloat(a, i));
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", a.getClass().getName(), " to type e");
						}
					}

				};
				break;
			case 'f': // double
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append(Array.getDouble(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						try {
							sink.appendDouble(n, Array.getDouble(a, i));
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", a.getClass().getName(), " to type f");
						}

					}
				};
				break;

			case 'F': // arr double
				getters[c] = new Getter() {
					final String n = name;
					final JsonBuilder jb = new JsonBuilder();

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append((double[]) Array.get(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						Object obj = Array.get(a, i);
						try {
							jb.add((double[]) obj);
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", obj.getClass().getName(), " to type F");
							return;
						}
						sink.appendString(n, jb.toString());
						jb.clear();
					}
				};
				break;
			case 'g': // guid
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append(Array.get(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						SH.clear(buf);
						buf.append(Array.get(a, i));//TODO: this is a GUID, should shortcut
						sink.appendString(n, buf);
					}
				};
				break;
			case 'c': // char
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append(Array.getChar(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						try {
							sink.appendChar(n, Array.getChar(a, i));
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", a.getClass().getName(), " to type c");
						}

					}
				};
				break;
			case 'C': // arr chars
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append((char[]) Array.get(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						SH.clear(buf);
						Object obj = Array.get(a, i);
						try {
							sink.appendChars(n, (char[]) obj);
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", obj.getClass().getName(), " to type C");
							return;
						}
					}
				};
				break;
			case 's': // symbol
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append(Array.get(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						SH.clear(buf);
						buf.append(Array.get(a, i));
						sink.appendString(n, buf);
					}
				};
				break;
			case 'S': // arr symbol
				getters[c] = new Getter() {
					final String n = name;
					final JsonBuilder jb = new JsonBuilder();

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append((Object[]) Array.get(a, i));
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						Object obj = Array.get(a, i);
						try {
							jb.addQuoted((Object[]) obj);
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", obj.getClass().getName(), " to type S");
						}

						sink.appendString(n, jb.toString());
						jb.clear();
					}
				};
				break;
			//dates...etc
			case 'z': // datetime
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						Object obj = Array.get(a, i);
						if (dateFormatter != null && obj instanceof String) {
							try {
								buf.append(((Date) dateFormatter.parse((String) obj)).getTime());
							} catch (ParseException e) {
								LH.warning(log, "Couldn't convert String to Date: `", obj, "` - ", e);
							}
						} else
							buf.append(((Date) obj).getTime());
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						Object obj = Array.get(a, i);
						if (dateFormatter != null && obj instanceof String) {
							try {
								buf.append(((Date) dateFormatter.parse((String) obj)).getTime());
							} catch (ParseException e) {
								LH.warning(log, "Couldn't convert String to Date: `", obj, "` - ", e);
							}
						} else
							sink.appendLong(n, ((Date) obj).getTime());
					}
				};
				break;
			case 'p': // timestamp
				if (useNanos) {
					getters[c] = new Getter() {
						final String n = name;

						@Override
						public void appendVal(Object a, int i, StringBuilder buf) {
							buf.append(getNanos(((Timestamp) Array.get(a, i))));
						}

						@Override
						public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
							try {
								sink.appendLong(n, getNanos(((Timestamp) Array.get(a, i))));
							} catch (Exception e) {
								LH.warning(log, "Cannot cast ", a.getClass().getName(), " to type p");
							}
						}

					};
				} else {
					getters[c] = new Getter() {
						final String n = name;

						@Override
						public void appendVal(Object a, int i, StringBuilder buf) {
							buf.append(((Date) Array.get(a, i)).getTime());
						}

						@Override
						public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
							try {
								sink.appendLong(n, ((Date) Array.get(a, i)).getTime());
							} catch (Exception e) {
								LH.warning(log, "Cannot cast ", a.getClass().getName(), " to type p");
							}
						}
					};
				}
				break;
			case 'd': // date
			case 't': // time
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append(((Date) Array.get(a, i)).getTime());
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						try {
							sink.appendLong(n, ((Date) Array.get(a, i)).getTime());
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", a.getClass().getName(), " to type t or d");
						}
					}
				};
				break;
			case 'T': // arr time
				getters[c] = new Getter() {
					final String n = name;
					final JsonBuilder jb = new JsonBuilder();

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						//						buf.append(((Date) Array.get(a, i)).getTime());
						Date[] dates = (Date[]) Array.get(a, i);
						int l = dates.length;
						long[] arr = new long[l];
						for (int k = 0; k < l; k++) {
							arr[k] = dates[k].getTime();
						}
						buf.append(dates);
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						Object obj = (Date[]) Array.get(a, i);
						try {
							Date[] dates = (Date[]) obj;
							int l = dates.length;
							long[] arr = new long[l];
							for (int k = 0; k < l; k++) {
								arr[k] = dates[k].getTime();
							}
							jb.add(arr);
							sink.appendString(n, jb.toString());
							jb.clear();
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", obj.getClass().getName(), " to type T");
						}
					}
				};
				break;
			case 'n': // timespan
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append(((Timespan) Array.get(a, i)).j);
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						try {
							sink.appendLong(n, ((Timespan) Array.get(a, i)).j);
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", a.getClass().getName(), " to type n");
						}
					}
				};
				break;

			//month/minute/second
			case 'm': // month
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append(((Month) Array.get(a, i)).i);
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						try {
							sink.appendInt(n, ((Month) Array.get(a, i)).i);
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", a.getClass().getName(), " to type m");
						}
					}
				};
				break;
			case 'u': // minute
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append(((Minute) Array.get(a, i)).i);
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						try {
							sink.appendInt(n, ((Minute) Array.get(a, i)).i);
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", a.getClass().getName(), " to type u");
						}
					}
				};
				break;
			case 'v': // second
				getters[c] = new Getter() {
					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						buf.append(((Second) Array.get(a, i)).i);
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						try {
							sink.appendInt(n, ((Second) Array.get(a, i)).i);
						} catch (Exception e) {
							LH.warning(log, "Cannot cast ", a.getClass().getName(), " to type v");
						}
					}
				};
				break;

			default: // unknown type
				getters[c] = new Getter() {

					final String n = name;

					@Override
					public void appendVal(Object a, int i, StringBuilder buf) {
						Object object = Array.get(a, i);
						if (object == null)
							buf.append("null");
						else if (object instanceof char[])
							SH.escape((char[]) object, '"', '\\', buf);
						else
							SH.escape(object.toString(), '"', '\\', buf);
					}

					@Override
					public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception {
						SH.clear(buf);
						Object object = Array.get(a, i);
						if (object == null)
							return;
						else if (object instanceof char[]) {
							SH.escape((char[]) object, '"', '\\', buf);
						} else {
							SH.escape(object.toString(), '"', '\\', buf);
						}
						sink.appendString(n, buf);
					}
				};
				LH.warning(log, "Unhandled column type for '" + name + "': `" + type + "`");
		}

	}

	private boolean onStop() {
		boolean success = true;
		if (t != null && t.isAlive()) {
			log.info("Attempting to stop the listening thread - " + t.getName());
			t.interrupt();
			if (c != null)
				try {
					c.close(); //attempt to close socket...otherwise reading thread will not interrupt
				} catch (IOException e1) {
				}
			try {
				t.join(1000);
			} catch (InterruptedException e) {
				log.log(Level.WARNING, "Failed to stop the listening thread - " + t.getName(), e);
				success = false;
			}
		}

		return success;
	}

	private StringBuilder buf = new StringBuilder(1000);
	private AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();

	StringBuilder idSink = new StringBuilder();

	@SuppressWarnings("unchecked")
	private int pubToAMI(String table, Object o) throws IOException {
		try {
			if (o != null && o.getClass().isArray()) { // convert array into a table of one col
				o = new Flip(new Dict(new String[] { "values" }, new Object[] { o }));
			} else if (o instanceof Dict) { // map to flip...assuming an aggregate t
				Dict d = (Dict) o;

				if (d.x.getClass() == String[].class) { // sym -> flip ... convert to table with one row of flips
					Object[] vals = new Object[Array.getLength(d.y)];
					for (int i = 0; i < vals.length; i++)
						vals[i] = new Object[] { Array.get(d.y, i) };
					o = new Flip(new Dict(d.x, vals));
				} else if (d.x.getClass() == Flip.class) { // keyed table or a key value dict
					Flip f1 = (Flip) d.x;
					Flip f2 = (Flip) d.y;

					String[] cols = AH.cat(f1.x, f2.x);
					Object[] data = AH.cat(f1.y, f2.y);

					o = new Flip(new Dict(cols, data));
				} else {
					o = new Flip(new Dict(new String[] { "key", "value" }, new Object[] { d.x, d.y }));
				}
			} else if (!(o instanceof Flip)) {
				o = new Flip(new Dict(new String[] { "value" }, new Object[] { o }));
			}

			if (o instanceof Flip) {
				Flip f = (Flip) o;
				int rows = Array.getLength(f.y[0]);
				int cols = f.x.length;

				TableEntry params = tableParamMap.get(table);

				if (params == null) {
					LH.warning(log, "No schema for table '", table, "' available");
					return 0;
				}

				if (log.isLoggable(Level.FINE))
					log.fine("Publishing " + rows + " rows for table " + table + " to ami: ");

				Getter[] getters = params.getters;
				Getter keyGetter = params.kg;
				if (cols != getters.length) {
					LH.warning(log, "Mismatched schema, expecting ", getters.length, " columns, instead received ", cols, " columns");
				}

				for (int r = 0; r < rows; r++) {
					converter.clear();
					for (int c = 0; c < cols; c++) {
						getters[c].get(f.y[c], r, converter);
					}

					//					if (log.isLoggable(Level.FINER))
					//						LH.finer(log, " #", r, " - Message: ", toLogString(cols, entries));

					if (keyGetter != null) {
						keyGetter.appendVal(f.y, r, idSink);
						this.publishObjectToAmi(-1, SH.toStringAndClear(idSink), table, 0, converter.toBytes());
					} else
						this.publishObjectToAmi(-1, null, table, 0, converter.toBytes());
				}

				return rows;
			} else
				throw new RuntimeException("only Flips are supported");
		} catch (Exception e) {
			LH.warning(log, "error with table '", table, "':", e);
		}
		return 0;
	}
	//	private String toLogString(int cols, Entry<String, Object>[] entries) {
	//		StringBuilder sb = new StringBuilder();
	//		for (int c = 0; c < cols; c++) {
	//			Object val = entries[c].getValue();
	//			if (c > 0)
	//				sb.append(',');
	//			sb.append(entries[c].getKey()).append('=');
	//			if (val instanceof char[]) {
	//				char[] t = (char[]) val;
	//				sb.append(new String(t)).append(t[0]);
	//			} else if (val instanceof String) {
	//				String t = (String) val;
	//				sb.append(t);
	//				sb.append(t.charAt(0));
	//			} else
	//				sb.append(val);
	//		}
	//		return sb.toString();
	//	}

	interface Getter {
		public void appendVal(Object a, int i, StringBuilder buf);
		public void get(Object a, int i, AmiRelayMapToBytesConverter sink) throws Exception;
	}

	public c GetConnection(String url, String username, String password, AmiNamingServiceResolver serviceResolver) {
		try {
			if (serviceResolver != null) {
				AmiServiceLocator locator = new AmiServiceLocator(AmiServiceLocator.TARGET_TYPE_FEEDHANDLER, PLUGIN_IN, getDescription(), url, username, SH.toCharArray(password),
						null, null);
				if (serviceResolver.canResolve(locator)) {
					AmiServiceLocator locator2 = serviceResolver.resolve(locator);
					if (locator2 != null) {
						url = locator2.getUrl();
						username = locator2.getUsername();
						password = locator2.getPassword() == null ? null : new String(locator2.getPassword());
					}
				}
			}
			String host = SH.beforeFirst(url, ':', url);
			String portStr = SH.afterFirst(url, ':', "1234");
			if (portStr.indexOf(':') != -1 && SH.isnt(username)) {//backwards compatibility for the host:port:user:pass syntax
				username = SH.afterFirst(portStr, ':');
				portStr = SH.beforeFirst(portStr, ':');
			}
			final int port = SH.parseInt(portStr);
			if (password == null && username == null)
				return new c(host, port);
			else
				return new c(host, port, password == null ? username : username + ":" + password);
		} catch (java.net.ConnectException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	static private long getNanos(Timestamp ts) {
		return ts.getTime() * 1000L * 1000L + ts.getNanos() % 1000000L;
	}

	static private boolean isConnected(c conn) {
		try {
			// light-weight query to test kx connection
			conn.k(".z.P");
			return true;
		} catch (KException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	public static void main(String a[]) {
		Timestamp ts = new Timestamp(123);
		ts.setNanos(234567890);
		System.out.println(ts.getNanos());
		System.out.println(ts.getTime());
		DateNanos dn = new DateNanos(ts);
		System.out.println();
		System.out.println(dn.getMillis());
		System.out.println(dn.getMicros());
		System.out.println(dn.getNanos());
		System.out.println(dn.getTimeNanos());
		System.out.println(dn.getTimeMillis());

	}
}
