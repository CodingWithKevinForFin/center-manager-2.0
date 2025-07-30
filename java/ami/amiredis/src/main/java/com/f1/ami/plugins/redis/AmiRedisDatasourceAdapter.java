package com.f1.ami.plugins.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiDatasourceAbstractAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Bytes;
import com.f1.container.ContainerTools;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.TimeoutController;

import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.args.BitCountOption;
import redis.clients.jedis.args.BitOP;
import redis.clients.jedis.args.ExpiryOption;
import redis.clients.jedis.args.FlushMode;
import redis.clients.jedis.args.GeoUnit;
import redis.clients.jedis.args.ListDirection;
import redis.clients.jedis.args.ListPosition;
import redis.clients.jedis.args.SortedSetOption;
import redis.clients.jedis.params.BitPosParams;
import redis.clients.jedis.params.GeoAddParams;
import redis.clients.jedis.params.GeoSearchParam;
import redis.clients.jedis.params.GetExParams;
import redis.clients.jedis.params.LCSParams;
import redis.clients.jedis.params.LPosParams;
import redis.clients.jedis.params.RestoreParams;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.params.SortingParams;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.params.ZParams;
import redis.clients.jedis.params.ZParams.Aggregate;
import redis.clients.jedis.params.ZRangeParams;
import redis.clients.jedis.resps.GeoRadiusResponse;
import redis.clients.jedis.resps.KeyedZSetElement;
import redis.clients.jedis.resps.LCSMatchResult;
import redis.clients.jedis.resps.LCSMatchResult.MatchedPosition;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.Tuple;
import redis.clients.jedis.util.KeyValue;

public class AmiRedisDatasourceAdapter extends AmiDatasourceAbstractAdapter {

	private static final Logger log = LH.get();

	private JedisPool jedisPool;
	private Jedis jedis;
	private AmiServiceLocator locator;

	private String url;
	private int port;
	private String username;
	private char[] password;

	interface RedisCommand {
		void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
				throws Exception;
	}

	private static Map<String, RedisCommand> commands = null;

	@Override
	public void init(ContainerTools tools, AmiServiceLocator locator) throws AmiDatasourceException {
		super.init(tools, locator);
		this.locator = locator;
		String fullURL = SH.replaceAll(locator.getUrl(), '\\', '/');
		if (fullURL.isEmpty())
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "A valid redis URL is required");
		this.url = SH.beforeLast(fullURL, ':');
		this.port = SH.parseInt(SH.afterLast(fullURL, ':'));
		this.username = locator.getUsername();
		this.password = locator.getPassword();

		this.jedisPool = new JedisPool(this.url, this.port);
		this.jedis = jedisPool.getResource();

		if (this.password.length != 0) {
			if (!this.username.isEmpty())
				jedis.auth(this.username, new String(this.password));
			else
				jedis.auth(new String(this.password));
		}

		if (!this.jedis.isConnected())
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Failed to connect to redis server at " + this.url + ":" + this.port);

		this.tools = tools;
		initializeCommands();
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		// Redis has no concept of a table, closest would be a hash - we can do a scan + type to detect hashes but even
		// then it wouldn't translate well into a table
		return Collections.emptyList();
	}

	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		return tables;
	}

	private static List<String> parseArguments(String args) throws Exception {
		ArrayList<String> results = new ArrayList<String>();

		for (int i = 0; i < args.length();) {
			char curr = args.charAt(i);

			//Skip empty whitespaces
			if (curr == ' ') {
				++i;
				continue;
			}

			int next;
			//Handle double and single quotes
			if (curr == '\"') {
				++i;
				int start = i;
				while (true) {

					next = args.indexOf('\"', i);
					if (next == -1)
						throw new Exception("Failed to parse: " + args + ", could not find matching pair of \"");
					else if (args.charAt(next - 1) == '\\') {
						i = next + 1;
						continue;
					}
					results.add(args.substring(start, next));
					i = next + 1;
					break;
				}
				continue;
			} else if (curr == '\'') {
				++i;
				int start = i;
				while (true) {

					next = args.indexOf('\'', i);
					if (next == -1)
						throw new Exception("Failed to parse: " + args + ", could not find matching pair of \'");
					else if (args.charAt(next - 1) == '\\') {
						i = next + 1;
						continue;
					}
					results.add(args.substring(start, next));
					i = next + 1;
					break;
				}
				continue;
			}

			next = args.indexOf(' ', i + 1);
			if (next == -1) {
				results.add(args.substring(i, args.length()));
				break;
			} else
				results.add(args.substring(i, next));
			i = next + 1;
		}

		return results;
	}

	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		//this.ensureConnected();

		try {

			List<String> queries = parseArguments(query.getQuery());
			if (queries.isEmpty()) {
				throw new Exception("Failed to parse query: " + query.getQuery());
			}
			String function = SH.toUpperCase(SH.trim(queries.get(0)));
			//Pass remaining args
			queries.remove(0);
			final RedisCommand c = commands.get(function);
			if (c == null) {
				throw new Exception("Failed to parse command: " + function);
			}

			c.run(jedis, queries, resultSink, debugSink, tc);

		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Redis Query compilation failed", e);
		} finally {
			if (jedis != null)
				jedis.close();

		}

	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult resultsSink, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload to datasource");
	}

	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.locator;
	}

	public static Map<String, String> buildOptions() {
		return Collections.emptyMap();
	}

	//Registers available redis commands
	private void initializeCommands() {

		if (commands != null)
			return;

		commands = new HashMap<String, RedisCommand>();

		commands.put("GET", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 1) {
					throw new Exception("Invalid number of arguments passed for GET, expected 1, got " + args.size());
				}

				String key = args.get(0);
				resultSink.setReturnType(String.class);
				resultSink.setReturnValue(jedis.get(key));
			}
		});

		commands.put("SET", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for SET, expected >=2, got " + args.size());
				}

				resultSink.setReturnType(String.class);
				if (argCount == 2) {
					resultSink.setReturnValue(jedis.set(args.get(0), args.get(1)));
				} else {
					String key = args.get(0);
					String val = args.get(1);
					args.remove(0);
					args.remove(0);
					SetParams params = new SetParams();
					for (int i = 0; i < args.size(); ++i) {
						String arg = SH.toUpperCase(args.get(i));
						if (SH.equals(arg, "EX")) {
							params.ex(SH.parseLong(args.get(i + 1)));
							++i;
						} else if (SH.equals(arg, "PX")) {
							params.px(SH.parseLong(args.get(i + 1)));
							++i;
						} else if (SH.equals(arg, "EXAT")) {
							params.exAt(SH.parseLong(args.get(i + 1)));
							++i;
						} else if (SH.equals(arg, "PXAT")) {
							params.pxAt(SH.parseLong(args.get(i + 1)));
							++i;
						} else if (SH.equals(arg, "NX"))
							params.nx();
						else if (SH.equals(arg, "XX"))
							params.xx();
						else if (SH.equals(arg, "KEEPTTL"))
							params.keepttl();
						else if (SH.equals(arg, "GET"))
							params.get();
						else
							throw new Exception("Unknown SET params: " + arg + ", expected: EX, PX, EXAT, PXAT, NX, XX, KEEPTTL, or GET");

					}

					resultSink.setReturnValue(jedis.set(key, val, params));
				}
			}
		});

		commands.put("EXISTS", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() < 1) {
					throw new Exception("Invalid number of arguments passed for EXISTS, expected >1, got " + args.size());
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.exists(args.toArray(new String[0])));
			}
		});

		commands.put("HELP", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 0) {
					throw new Exception("Invalid number of arguments passed for HELP, expected 0, got " + args.size());
				}

				resultSink.setReturnType(List.class);
				ArrayList<String> commandList = new ArrayList<String>(commands.keySet());
				Collections.sort(commandList);
				resultSink.setReturnValue(commandList);
			}
		});

		commands.put("AUTH", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (!(argCount == 1 || argCount == 2)) {
					throw new Exception("Invalid number of arguments passed for AUTH, expected 1, or 2, got " + argCount);
				}

				resultSink.setReturnType(String.class);
				if (argCount == 1)
					resultSink.setReturnValue(jedis.auth(args.get(0)));
				else
					resultSink.setReturnValue(jedis.auth(args.get(0), args.get(1)));
			}
		});

		commands.put("APPEND", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 2) {
					throw new Exception("Invalid number of arguments passed for APPEND, expected 2, got " + args.size());
				}
				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.append(args.get(0), args.get(1)));

			}
		});

		commands.put("BITCOUNT", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (!(argCount == 1 || argCount == 3 || argCount == 4)) {
					throw new Exception("Invalid number of arguments passed for BITCOUNT, expected 1, 3, or 4, got " + argCount);
				}

				resultSink.setReturnType(Long.class);
				if (argCount == 1)
					resultSink.setReturnValue(jedis.bitcount(args.get(0)));
				else if (argCount == 3)
					resultSink.setReturnValue(jedis.bitcount(args.get(0), SH.parseLong(args.get(1)), SH.parseLong(args.get(2))));
				else if (argCount == 4) {
					String bitcountOptionStr = SH.trim(SH.toUpperCase(args.get(3)));
					BitCountOption option = null;
					if (SH.equals(bitcountOptionStr, "BYTE"))
						option = BitCountOption.BYTE;
					if (SH.equals(bitcountOptionStr, "BYTE"))
						option = BitCountOption.BIT;
					if (option == null)
						throw new Exception("Failed to parse bitcount option string: " + option + ", expecting \"BYTE\" OR \"BIT\"");

					resultSink.setReturnValue(jedis.bitcount(args.get(0), SH.parseLong(args.get(1)), SH.parseLong(args.get(2)), option));
				}

			}
		});

		commands.put("HGET", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 2) {
					throw new Exception("Invalid number of arguments passed for HGET, expected 2, got " + args.size());
				}
				resultSink.setReturnType(String.class);
				resultSink.setReturnValue(jedis.hget(args.get(0), args.get(1)));

			}
		});

		commands.put("HGETALL", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 1) {
					throw new Exception("Invalid number of arguments passed for HGETALL, expected 1, got " + args.size());
				}
				resultSink.setReturnType(Map.class);
				resultSink.setReturnValue(jedis.hgetAll(args.get(0)));

			}
		});

		commands.put("BITFIELD", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() < 1) {
					throw new Exception("Invalid number of arguments passed for BITFIELD, expected >=1, got " + args.size());
				}
				String key = args.get(0);
				args.remove(0);
				resultSink.setReturnType(List.class);
				resultSink.setReturnValue(jedis.bitfield(key, args.toArray(new String[0])));
			}
		});

		commands.put("BITFIELD_RO", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() < 1) {
					throw new Exception("Invalid number of arguments passed for BITFIELD_RO, expected >=1, got " + args.size());
				}
				String key = args.get(0);
				args.remove(0);
				resultSink.setReturnType(List.class);
				resultSink.setReturnValue(jedis.bitfieldReadonly(key, args.toArray(new String[0])));
			}
		});

		commands.put("BITOP", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() < 3) {
					throw new Exception("Invalid number of arguments passed for BITOP, expected >=3, got " + args.size());
				}
				String operationStr = SH.toUpperCase(args.get(0));
				BitOP operation = null;
				if (SH.equals("AND", operationStr))
					operation = BitOP.AND;
				else if (SH.equals("OR", operationStr))
					operation = BitOP.OR;
				else if (SH.equals("XOR", operationStr))
					operation = BitOP.XOR;
				else if (SH.equals("NOT", operationStr))
					operation = BitOP.NOT;
				else
					throw new Exception("Unknown BITOP operation: " + operation + ", expected AND, OR, XOR, or NOT");

				args.remove(0);

				String key = args.get(0);
				args.remove(0);

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.bitop(operation, key, args.toArray(new String[0])));
			}
		});

		commands.put("BITPOS", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() < 2) {
					throw new Exception("Invalid number of arguments passed for BITPOS, expected >=2, got " + args.size());
				}

				String key = args.get(0);
				args.remove(0);

				String bitTestStr = args.get(0);
				Boolean bitTest = null;
				if (SH.equals(bitTestStr, "1"))
					bitTest = true;
				else if (SH.equals(bitTestStr, "0"))
					bitTest = false;
				else
					throw new Exception("Failed to parse bit: " + bitTestStr + ", expecting 1, or 0");
				args.remove(0);

				resultSink.setReturnType(Long.class);

				//Parse arguments
				if (!args.isEmpty()) {
					BitPosParams params = null;
					if (args.size() == 1) {
						params = new BitPosParams(SH.parseLong(args.get(0)));
					} else if (args.size() == 2) {
						params = new BitPosParams(SH.parseLong(args.get(0)), SH.parseLong(args.get(1)));
					} else {
						throw new Exception("Failed to parse BitPosParams, expected 1 or 2 arguments, got: " + args.size());
					}

					resultSink.setReturnValue(jedis.bitpos(key, bitTest, params));
				} else {
					resultSink.setReturnValue(jedis.bitpos(key, bitTest));
				}
			}
		});

		commands.put("BLMOVE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 5) {
					throw new Exception("Invalid number of arguments passed for BLMOVE, expected 5, got " + args.size());
				}

				String directionFrom = SH.toUpperCase(args.get(2));
				ListDirection from = null;
				if (SH.equals(directionFrom, "LEFT"))
					from = ListDirection.LEFT;
				else if (SH.equals(directionFrom, "RIGHT"))
					from = ListDirection.RIGHT;
				else
					throw new Exception("Failed to parse list direction: " + directionFrom + ", expected LEFT, or RIGHT");

				String directionTo = SH.toUpperCase(args.get(3));
				ListDirection to = null;
				if (SH.equals(directionTo, "LEFT"))
					to = ListDirection.LEFT;
				else if (SH.equals(directionTo, "RIGHT"))
					to = ListDirection.RIGHT;
				else
					throw new Exception("Failed to parse list direction: " + directionTo + ", expected LEFT, or RIGHT");

				Double timeout = SH.parseDouble(args.get(4));
				resultSink.setReturnType(String.class);
				resultSink.setReturnValue(jedis.blmove(args.get(0), args.get(1), from, to, timeout));

			}
		});

		commands.put("BLPOP", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() < 2) {
					throw new Exception("Invalid number of arguments passed for BLPOP, expected >=2, got " + args.size());
				}

				//Get timeout value
				Integer timeout = SH.parseInt(args.get(args.size() - 1));
				args.remove(args.size() - 1);

				resultSink.setReturnType(List.class);
				if (args.size() == 1)
					resultSink.setReturnValue(jedis.blpop(timeout, args.get(0)));
				else
					resultSink.setReturnValue(jedis.blpop(timeout, args.toArray(new String[0])));

			}
		});

		commands.put("BRPOP", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() < 2) {
					throw new Exception("Invalid number of arguments passed for BRPOP, expected >=2, got " + args.size());
				}

				//Get timeout value
				Integer timeout = SH.parseInt(args.get(args.size() - 1));
				args.remove(args.size() - 1);

				resultSink.setReturnType(List.class);
				if (args.size() == 1)
					resultSink.setReturnValue(jedis.brpop(timeout, args.get(0)));
				else
					resultSink.setReturnValue(jedis.brpop(timeout, args.toArray(new String[0])));

			}
		});

		commands.put("BRPOPLPUSH", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 3) {
					throw new Exception("Invalid number of arguments passed for BRPOPLPUSH, expected 3, got " + args.size());
				}

				//Get timeout value
				Integer timeout = SH.parseInt(args.get(args.size() - 1));
				args.remove(args.size() - 1);

				resultSink.setReturnType(String.class);
				resultSink.setReturnValue(jedis.brpoplpush(args.get(0), args.get(1), timeout));

			}
		});

		commands.put("BZPOPMAX", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() < 2) {
					throw new Exception("Invalid number of arguments passed for BZPOPMAX, expected >=2, got " + args.size());
				}

				//Get timeout value
				Double timeout = SH.parseDouble(args.get(args.size() - 1));
				args.remove(args.size() - 1);

				resultSink.setReturnType(List.class);
				KeyedZSetElement output = jedis.bzpopmax(timeout, args.toArray(new String[0]));
				resultSink.setReturnValue(Arrays.asList(output.getKey(), output.getElement(), SH.toString(output.getScore())));

			}
		});

		commands.put("BZPOPMIN", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() < 2) {
					throw new Exception("Invalid number of arguments passed for BZPOPMAX, expected >=2, got " + args.size());
				}

				//Get timeout value
				Double timeout = SH.parseDouble(args.get(args.size() - 1));
				args.remove(args.size() - 1);

				resultSink.setReturnType(List.class);
				KeyedZSetElement output = jedis.bzpopmin(timeout, args.toArray(new String[0]));
				resultSink.setReturnValue(Arrays.asList(output.getKey(), output.getElement(), SH.toString(output.getScore())));

			}
		});

		commands.put("COPY", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for COPY, expected >= 2, got " + argCount);
				}

				resultSink.setReturnType(Integer.class);
				if (argCount == 2) {
					resultSink.setReturnValue(jedis.copy(args.get(0), args.get(1), false));
				} else {
					Integer dbIndex = null;
					Boolean replace = false;

					for (int i = 2; i < argCount; ++i) {
						String arg = SH.trim(SH.toUpperCase(args.get(i)));
						if (SH.equals(arg, "DB")) {
							++i;
							dbIndex = SH.parseInt(args.get(i));
						} else if (SH.equals(arg, "REPLACE")) {
							replace = true;
						} else {
							throw new Exception("Unknown option for COPY, expected DB or COPY, got: " + arg);
						}
					}

					if (dbIndex != null) {
						resultSink.setReturnValue(jedis.copy(args.get(0), args.get(1), dbIndex, replace) ? 1 : 0);
					} else {
						resultSink.setReturnValue(jedis.copy(args.get(0), args.get(1), replace) ? 1 : 0);
					}

				}
			}
		});

		commands.put("DBSIZE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 0) {
					throw new Exception("Invalid number of arguments passed for DBSIZE, expected 0, got " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.dbSize());

			}
		});

		commands.put("DECR", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for DECR, expected 1, got " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.decr(args.get(0)));

			}
		});

		commands.put("DECRBY", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 2) {
					throw new Exception("Invalid number of arguments passed for DECRBY, expected 2, got " + argCount);
				}

				resultSink.setReturnType(Long.class);
				Long decrement = SH.parseLong(args.get(1));
				resultSink.setReturnValue(jedis.decrBy(args.get(0), decrement));

			}
		});

		commands.put("DEL", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 1) {
					throw new Exception("Invalid number of arguments passed for DEL, expected >= 1, got " + argCount);
				}

				resultSink.setReturnType(Long.class);
				if (argCount == 1)
					resultSink.setReturnValue(jedis.del(args.get(0)));
				else
					resultSink.setReturnValue(jedis.del(args.toArray(new String[0])));
			}
		});

		commands.put("DUMP", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for DUMP, expected 1, got " + argCount);
				}

				resultSink.setReturnType(Bytes.class);
				Bytes b = new Bytes(jedis.dump(args.get(0)));
				resultSink.setReturnValue(b);

			}
		});

		commands.put("ECHO", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for ECHO, expected 1, got " + argCount);
				}

				resultSink.setReturnType(String.class);
				resultSink.setReturnValue(jedis.echo(args.get(0)));
			}
		});

		commands.put("EVAL", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for EVAL, expected >= 2, got " + argCount);
				}

				resultSink.setReturnType(Object.class);

				String script = args.get(0);
				args.remove(0);
				Integer numKeys = SH.parseInt(args.get(0));
				args.remove(0);

				resultSink.setReturnValue(jedis.eval(script, numKeys, args.toArray(new String[0])));
			}
		});

		commands.put("EVALSHA", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for EVALSHA, expected >= 2, got " + argCount);
				}

				resultSink.setReturnType(Object.class);

				String sha = args.get(0);
				args.remove(0);
				Integer numKeys = SH.parseInt(args.get(0));
				args.remove(0);

				resultSink.setReturnValue(jedis.evalsha(sha, numKeys, args.toArray(new String[0])));

			}
		});

		commands.put("EXPIRE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for EXPIRE, expected >=2, got " + args.size());
				}
				Long seconds = SH.parseLong(args.get(1));

				resultSink.setReturnType(Long.class);
				if (argCount == 2) {
					resultSink.setReturnValue(jedis.expire(args.get(0), seconds));
				} else {
					//Jedis does not support REDIS compliant options such as XX GT / XX LT, maximum of one option allowed
					String key = args.get(0);
					args.remove(0);
					args.remove(0);
					ExpiryOption expiryOptions = null;
					for (int i = 0; i < args.size(); ++i) {
						String arg = SH.toUpperCase(args.get(i));
						if (SH.equals(arg, "NX")) {
							expiryOptions = ExpiryOption.NX;
							break;
						} else if (SH.equals(arg, "XX")) {
							expiryOptions = ExpiryOption.XX;
							break;
						} else if (SH.equals(arg, "GT")) {
							expiryOptions = ExpiryOption.GT;
							break;
						} else if (SH.equals(arg, "LT")) {
							expiryOptions = ExpiryOption.LT;
							break;
						} else {
							throw new Exception("Unrecognized expiry option: " + arg + ", expecting, NX, XX, GT, or LT");
						}

					}

					resultSink.setReturnValue(jedis.expire(key, seconds, expiryOptions));
				}
			}
		});

		commands.put("EXPIREAT", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for EXPIREAT, expected >=2, got " + args.size());
				}
				Long unixTime = SH.parseLong(args.get(1));

				resultSink.setReturnType(Long.class);
				if (argCount == 2) {
					resultSink.setReturnValue(jedis.expireAt(args.get(0), unixTime));
				} else {
					//Jedis does not support REDIS compliant options such as XX GT / XX LT, maximum of one option allowed
					String key = args.get(0);
					args.remove(0);
					args.remove(0);
					ExpiryOption expiryOptions = null;
					for (int i = 0; i < args.size(); ++i) {
						String arg = SH.toUpperCase(args.get(i));
						if (SH.equals(arg, "NX")) {
							expiryOptions = ExpiryOption.NX;
							break;
						} else if (SH.equals(arg, "XX")) {
							expiryOptions = ExpiryOption.XX;
							break;
						} else if (SH.equals(arg, "GT")) {
							expiryOptions = ExpiryOption.GT;
							break;
						} else if (SH.equals(arg, "LT")) {
							expiryOptions = ExpiryOption.LT;
							break;
						} else {
							throw new Exception("Unrecognized expiry option: " + arg + ", expecting, NX, XX, GT, or LT");
						}

					}

					resultSink.setReturnValue(jedis.expireAt(key, unixTime, expiryOptions));
				}
			}
		});

		commands.put("EXPIRETIME", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for EXPIRETIME, expected 1, got " + args.size());
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.expireTime(args.get(0)));

			}
		});

		commands.put("FCALL", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for FCALL, expected >= 2, got " + args.size());
				}

				resultSink.setReturnType(Object.class);
				String functionName = args.get(0);
				args.remove(0);
				Integer keyCount = SH.parseInt(args.get(0));
				args.remove(0);

				if (args.size() < keyCount)
					throw new Exception("Invalid number of keys passed, expecting: " + keyCount + ", got: " + args.size());

				resultSink.setReturnValue(jedis.fcall(functionName, args.subList(0, keyCount), args.subList(keyCount, args.size())));

			}
		});

		commands.put("FCALL_RO", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for FCALL_RO, expected >= 2, got " + args.size());
				}

				resultSink.setReturnType(Object.class);
				String functionName = args.get(0);
				args.remove(0);
				Integer keyCount = SH.parseInt(args.get(0));
				args.remove(0);

				if (args.size() < keyCount)
					throw new Exception("Invalid number of keys passed, expecting: " + keyCount + ", got: " + args.size());

				resultSink.setReturnValue(jedis.fcallReadonly(functionName, args.subList(0, keyCount), args.subList(keyCount, args.size())));

			}
		});

		commands.put("FLUSHALL", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (!(argCount == 0 || argCount == 1)) {
					throw new Exception("Invalid number of arguments passed for FLUSHALL, expected 0, or 1, got " + args.size());
				}

				resultSink.setReturnType(String.class);

				if (argCount == 0)
					resultSink.setReturnValue(jedis.flushAll());
				else {
					FlushMode flushMode = null;
					String flushModeStr = SH.toUpperCase(args.get(0));
					if (SH.equals(flushModeStr, "ASYNC"))
						flushMode = FlushMode.ASYNC;
					else if (SH.equals(flushModeStr, "SYNC"))
						flushMode = FlushMode.SYNC;
					else
						throw new Exception("Failed to parse flush mode: " + flushModeStr + ", expecting ASYNC or SYNC");

					resultSink.setReturnValue(jedis.flushAll(flushMode));
				}

			}
		});

		commands.put("FLUSHDB", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (!(argCount == 0 || argCount == 1)) {
					throw new Exception("Invalid number of arguments passed for FLUSHDB, expected 0, or 1, got " + args.size());
				}

				resultSink.setReturnType(String.class);

				if (argCount == 0)
					resultSink.setReturnValue(jedis.flushDB());
				else {
					FlushMode flushMode = null;
					String flushModeStr = SH.toUpperCase(args.get(0));
					if (SH.equals(flushModeStr, "ASYNC"))
						flushMode = FlushMode.ASYNC;
					else if (SH.equals(flushModeStr, "SYNC"))
						flushMode = FlushMode.SYNC;
					else
						throw new Exception("Failed to parse flush mode: " + flushModeStr + ", expecting ASYNC or SYNC");

					resultSink.setReturnValue(jedis.flushDB(flushMode));
				}

			}
		});

		//Removing all support for FUNCTION commands -
		//FUNCTION LOAD on jedis not correctly handling the same input as on the CLI
		/*
		commands.put("FUNCTION", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 1) {
					throw new Exception("Invalid number of arguments passed for FUNCTION, expected >1, got " + args.size());
				}
		
				String functionType = SH.toUpperCase(args.get(0));
		
				if (SH.equals(functionType, "DELETE")) {
					if (argCount != 2)
						throw new Exception("Invalid number of arguments passed in for FUNCTION DELETE, expected 2, got " + argCount);
					resultSink.setReturnType(String.class);
					resultSink.setReturnValue(jedis.functionDelete(args.get(1)));
				} else if (SH.equals(functionType, "DUMP")) {
					if (argCount != 1)
						throw new Exception("Invalid number of arguments passed in for FUNCTION DUMP, expected 1, got " + argCount);
					resultSink.setReturnType(byte[].class);
					resultSink.setReturnValue(jedis.functionDump());
				} else if (SH.equals(functionType, "FLUSH")) {
					if (!(argCount == 1 || argCount == 2))
						throw new Exception("Invalid number of arguments passed in for FUNCTION FLUSH, expected 1 or 2, got " + argCount);
					resultSink.setReturnType(String.class);
					if (argCount == 1)
						resultSink.setReturnValue(jedis.functionFlush());
					else {
						FlushMode flushMode = null;
						String flushModeStr = SH.toUpperCase(args.get(0));
						if (SH.equals(flushModeStr, "ASYNC"))
							flushMode = FlushMode.ASYNC;
						else if (SH.equals(flushModeStr, "SYNC"))
							flushMode = FlushMode.SYNC;
						else
							throw new Exception("Failed to parse flush mode: " + flushModeStr + ", expecting ASYNC or SYNC");
						resultSink.setReturnValue(jedis.functionFlush(flushMode));
					}
				} else if (SH.equals(functionType, "KILL")) {
					if (argCount != 1)
						throw new Exception("Invalid number of arguments passed in for FUNCTION KILL, expected 1, got " + argCount);
					resultSink.setReturnType(String.class);
					resultSink.setReturnValue(jedis.functionKill());
				} else if (SH.equals(functionType, "LIST")) {
					if (argCount == 1) {
						List<LibraryInfo> functionList = jedis.functionList();
						List<String> functionListStr = new ArrayList<String>(functionList.size());
						for (int i = 0; i < functionList.size(); ++i)
							functionListStr.set(1, functionList.get(i).toString());
						resultSink.setReturnType(List.class);
						resultSink.setReturnValue(functionListStr);
					} else if (argCount == 2) {
						String firstArg = args.get(1);
						if (SH.equals(SH.toUpperCase(firstArg), "WITHCODE")) {
							List<LibraryInfo> functionList = jedis.functionListWithCode();
							List<String> functionListStr = new ArrayList<String>(functionList.size());
							for (int i = 0; i < functionList.size(); ++i)
								functionListStr.set(1, functionList.get(i).toString());
							resultSink.setReturnType(List.class);
							resultSink.setReturnValue(functionListStr);
						} else {
							List<LibraryInfo> functionList = jedis.functionList(firstArg);
							List<String> functionListStr = new ArrayList<String>(functionList.size());
							for (int i = 0; i < functionList.size(); ++i)
								functionListStr.set(1, functionList.get(i).toString());
							resultSink.setReturnType(List.class);
							resultSink.setReturnValue(functionListStr);
						}
					} else if (argCount == 3) {
						String firstArg = args.get(1);
						String secondArg = args.get(2);
						if (SH.equals(SH.toUpperCase(firstArg), "WITHCODE")) {
							List<LibraryInfo> functionList = jedis.functionListWithCode(secondArg);
							List<String> functionListStr = new ArrayList<String>(functionList.size());
							for (int i = 0; i < functionList.size(); ++i)
								functionListStr.set(1, functionList.get(i).toString());
							resultSink.setReturnType(List.class);
							resultSink.setReturnValue(functionListStr);
						} else if (SH.equals(SH.toUpperCase(secondArg), "WITHCODE")) {
							List<LibraryInfo> functionList = jedis.functionListWithCode(firstArg);
							List<String> functionListStr = new ArrayList<String>(functionList.size());
							for (int i = 0; i < functionList.size(); ++i)
								functionListStr.set(1, functionList.get(i).toString());
							resultSink.setReturnType(List.class);
							resultSink.setReturnValue(functionListStr);
						} else {
							throw new Exception("Could not parse arguments for FUNCTION LIST, arg1: " + firstArg + "arg2: " + secondArg
									+ ". Expected format: FUNCTION LIST [LIBRARYNAME library-name-pattern] [WITHCODE]");
						}
					} else {
						throw new Exception("Invalid number of arguments for FUNCTION LIST, received: " + argCount + ", expected 1, 2, or 3");
					}
		
				} else if (SH.equals(functionType, "LOAD")) {
					if (!(argCount == 2 || argCount == 3))
						throw new Exception("Invalid number of arguments passed in for FUNCTION LOAD, expected 2 or 3, got: " + argCount);
					resultSink.setReturnType(String.class);
					String function;
					boolean replace = false;
					if (argCount == 2)
						function = args.get(1);
					else {
						if (SH.equals(SH.toUpperCase(args.get(1)), "REPLACE")) {
							function = args.get(2);
							replace = true;
						} else {
							throw new Exception("Failed to parse arguments for FUNCTION LOAD, arg1: " + args.get(1) + ", arg2: " + args.get(2)
									+ ". Expected format - FUNCTION LOAD [REPLACE] function-code");
						}
					}
		
					if (replace) {
						resultSink.setReturnValue(jedis.functionLoadReplace(function));
					} else
						resultSink.setReturnValue(jedis.functionLoad(function));
		
				} else if (SH.equals(functionType, "RESTORE")) {
					if (!(argCount == 2 || argCount == 3))
						throw new Exception("Invalid number of arguments passed in for FUNCTION RESTORE, expected 2, or 3, got: " + argCount);
					resultSink.setReturnType(String.class);
		
					byte[] value = args.get(1).getBytes();
					if (argCount == 2)
						resultSink.setReturnValue(jedis.functionRestore(value));
					else {
						String policyStr = SH.toUpperCase(args.get(2));
						FunctionRestorePolicy policy = null;
						if (SH.equals(policyStr, "APPEND"))
							policy = FunctionRestorePolicy.APPEND;
						else if (SH.equals(policyStr, "FLUSH"))
							policy = FunctionRestorePolicy.FLUSH;
						else if (SH.equals(policyStr, "REPLACE"))
							policy = FunctionRestorePolicy.REPLACE;
						else
							throw new Exception("Failed to parse function restore policy. Given: " + policyStr + ", expected: APPEND, FLUSH, or REPLACE");
						resultSink.setReturnValue(jedis.functionRestore(value, policy));
					}
				} else if (SH.equals(functionType, "STATS")) {
					if (argCount != 1)
						throw new Exception("Invalid number of arguments passed in for FUNCTION STATS, expected 1, got " + argCount);
					resultSink.setReturnType(Map.class);
					FunctionStats stats = jedis.functionStats();
					Map<String, Object> functionStatMap = new HashMap<String, Object>();
					functionStatMap.put("running_script", stats.getRunningScript());
					functionStatMap.put("engines", stats.getEngines());
					resultSink.setReturnValue(functionStatMap);
				}
		
			}
		});
		*/

		commands.put("GEOADD", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 4) {
					throw new Exception("Invalid number of arguments passed for GEOADD, expected >=4, got: " + args.size());
				}

				String key = args.get(0);
				args.remove(0);

				//Parse optional flags
				boolean parseFlags = true;
				GeoAddParams params = new GeoAddParams();
				while (parseFlags) {
					String flag = SH.toUpperCase(args.get(0));
					if (SH.equals(flag, "NX")) {
						params.nx();
						args.remove(0);
						continue;
					} else if (SH.equals(flag, "XX")) {
						params.xx();
						args.remove(0);
						continue;
					} else if (SH.equals(flag, "CH")) {
						params.ch();
						args.remove(0);
						continue;
					}

					parseFlags = false;

				}

				if (args.size() % 3 != 0)
					throw new Exception("Failed to parse GEOADD arguments - argument size should be divisible by 3 in the following format: "
							+ "GEOADD key [ NX | XX] [CH] longitude latitude member [ longitude latitude member ...]");

				Map<String, GeoCoordinate> coordinateMap = new HashMap<String, GeoCoordinate>();
				for (int i = 0; i < args.size(); i += 3) {
					Double longitude = SH.parseDouble(args.get(i));
					Double latitude = SH.parseDouble(args.get(i + 1));
					coordinateMap.put(args.get(i + 2), new GeoCoordinate(longitude, latitude));
				}
				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.geoadd(key, params, coordinateMap));
			}
		});

		commands.put("GEODIST", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 3) {
					throw new Exception("Invalid number of arguments passed for GEODIST, expected >=3, got: " + args.size());
				}

				resultSink.setReturnType(Double.class);
				if (argCount == 3)
					resultSink.setReturnValue(jedis.geodist(args.get(0), args.get(1), args.get(2)));
				else if (argCount == 4) {
					String unitStr = SH.toUpperCase(args.get(3));
					GeoUnit unit = null;
					if (SH.equals(unitStr, "M"))
						unit = GeoUnit.M;
					else if (SH.equals(unitStr, "KM"))
						unit = GeoUnit.KM;
					else if (SH.equals(unitStr, "FT"))
						unit = GeoUnit.FT;
					else if (SH.equals(unitStr, "MI"))
						unit = GeoUnit.MI;
					else
						throw new Exception("Failed to parse GeoUnit arguments. Received: " + unitStr + ", expected: M, KM, FT, or MI");
					resultSink.setReturnValue(jedis.geodist(args.get(0), args.get(1), args.get(2), unit));
				}
			}
		});

		commands.put("GEOHASH", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for GEOHASH, expected >=2, got: " + args.size());
				}

				String key = args.get(0);
				args.remove(0);
				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.geohash(key, args.toArray(new String[0])));

			}
		});

		commands.put("GEOPOS", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for GEOPOS, expected >=2, got: " + args.size());
				}

				String key = args.get(0);
				args.remove(0);
				resultSink.setReturnType(List.class);
				ArrayList<String> results = new ArrayList<String>();
				List<GeoCoordinate> geoResults = jedis.geopos(key, args.toArray(new String[0]));
				for (final GeoCoordinate geo : geoResults) {
					if (geo != null)
						results.addAll(Arrays.asList(SH.toString(geo.getLatitude()), SH.toString(geo.getLongitude())));
					else
						results.add(null);
				}

				resultSink.setReturnValue(results);

			}
		});

		commands.put("GEOSEARCH", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 7) {
					throw new Exception("Invalid number of arguments passed for GEOSEARCH, expected >=7, got: " + args.size());
				}
				//Full format: GEOSEARCH key FROMMEMBER member | FROMLONLAT longitude latitude 
				//	BYRADIUS radius M | KM | FT | MI | BYBOX width height M | KM | FT | MI [ ASC | DESC] 
				//	[ COUNT count [ANY]] [WITHCOORD] [WITHDIST] [WITHHASH]

				String key = args.get(0);
				args.remove(0);

				String queryType = SH.toUpperCase(args.get(0));
				args.remove(0);

				GeoSearchParam searchParams = new GeoSearchParam();

				if (SH.equals(queryType, "FROMMEMBER")) {
					searchParams.fromMember(args.get(0));
					args.remove(0);

				} else if (SH.equals(queryType, "FROMLONLAT")) {
					Double longitude = SH.parseDouble(args.get(0));
					args.remove(0);
					Double latitude = SH.parseDouble(args.get(0));
					args.remove(0);
					searchParams.fromLonLat(longitude, latitude);
				} else
					throw new Exception("Invalid query type: " + queryType + ", expected FROMMEMBER or FROMLONLAT");

				String searchBy = SH.toUpperCase(args.get(0));
				args.remove(0);

				if (SH.equals(searchBy, "BYRADIUS")) {

					Double radius = SH.parseDouble(args.get(0));
					args.remove(0);

					String geoUnitStr = SH.toUpperCase(args.get(0));
					args.remove(0);

					if (SH.equals(geoUnitStr, "M"))
						searchParams.byRadius(radius, GeoUnit.M);
					else if (SH.equals(geoUnitStr, "KM"))
						searchParams.byRadius(radius, GeoUnit.KM);
					else if (SH.equals(geoUnitStr, "FT"))
						searchParams.byRadius(radius, GeoUnit.FT);
					else if (SH.equals(geoUnitStr, "MI"))
						searchParams.byRadius(radius, GeoUnit.MI);
					else
						throw new Exception("Failed to parse GeoUnit, received: " + geoUnitStr + ", expected: M, KM, FT, or MI");

				} else if (SH.equals(searchBy, "BYBOX")) {

					double boxWidth, boxHeight;
					boxWidth = SH.parseDouble(args.get(0));
					args.remove(0);
					boxHeight = SH.parseDouble(args.get(0));
					args.remove(0);
					String geoUnitStr = SH.toUpperCase(args.get(0));
					args.remove(0);
					if (SH.equals(geoUnitStr, "M"))
						searchParams.byBox(boxWidth, boxHeight, GeoUnit.M);
					else if (SH.equals(geoUnitStr, "KM"))
						searchParams.byBox(boxWidth, boxHeight, GeoUnit.KM);
					else if (SH.equals(geoUnitStr, "FT"))
						searchParams.byBox(boxWidth, boxHeight, GeoUnit.FT);
					else if (SH.equals(geoUnitStr, "MI"))
						searchParams.byBox(boxWidth, boxHeight, GeoUnit.MI);
					else
						throw new Exception("Failed to parse GeoUnit, received: " + geoUnitStr + ", expected: M, KM, FT, or MI");
				} else
					throw new Exception("Failed to parse GEOSEARCH arguments, received: " + args.get(0) + ", expecting: BYRADIUS, or BYBOX");

				//Parse optional arguments
				boolean parseArgs = true;
				Boolean withCoord = false;
				Boolean withDist = false;
				Boolean withHash = false;

				while (parseArgs && !args.isEmpty()) {
					String param = SH.toUpperCase(args.get(0));
					args.remove(0);

					if (SH.equals(param, "ASC")) {
						searchParams = searchParams.asc();
						continue;
					} else if (SH.equals(param, "DESC")) {
						searchParams = searchParams.desc();
						continue;
					} else if (SH.equals(param, "COUNT")) {
						int count = SH.parseInt(args.get(0));
						args.remove(0);
						if (SH.equals(SH.toUpperCase(args.get(0)), "ANY")) {
							searchParams = searchParams.count(count, true);
							args.remove(0);
						} else {
							searchParams = searchParams.count(count);
						}
						continue;
					} else if (SH.equals(param, "WITHCOORD")) {
						searchParams = searchParams.withCoord();
						withCoord = true;
						continue;
					} else if (SH.equals(param, "WITHDIST")) {
						searchParams = searchParams.withDist();
						withDist = true;
						continue;
					} else if (SH.equals(param, "WITHHASH")) {
						searchParams = searchParams.withHash();
						withHash = true;
						continue;
					}

					parseArgs = false;
				}

				if (!args.isEmpty())
					throw new Exception("Failed to parse remaining arguments for GEOSEARCH: " + args.toString());

				List<GeoRadiusResponse> response = jedis.geosearch(key, searchParams);

				ArrayList<Object> responseStr = new ArrayList<Object>(response.size());
				for (final GeoRadiusResponse r : response) {
					responseStr.add(r.getMemberByString());
					if (withDist)
						responseStr.add(SH.toString(r.getDistance()));
					if (withHash)
						responseStr.add(SH.toString(r.getRawScore()));
					if (withCoord)
						responseStr.addAll(Arrays.asList(SH.toString(r.getCoordinate().getLongitude()), SH.toString(r.getCoordinate().getLatitude())));
				}

				resultSink.setReturnType(List.class);
				resultSink.setReturnValue(responseStr);

			}
		});

		commands.put("GEOSEARCHSTORE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 8) {
					throw new Exception("Invalid number of arguments passed for GEOSEARCHSTORE, expected >=8, got: " + args.size());
				}
				//Full format: GEOSEARCHSTORE destination source FROMMEMBER member | FROMLONLAT longitude latitude 
				//	BYRADIUS radius M | KM | FT | MI | BYBOX width height M | KM | FT | MI [ ASC | DESC] [ COUNT count [ANY]] [STOREDIST]

				String destination = args.get(0);
				args.remove(0);

				String source = args.get(0);
				args.remove(0);

				String queryType = SH.toUpperCase(args.get(0));
				args.remove(0);

				GeoSearchParam searchParams = new GeoSearchParam();

				if (SH.equals(queryType, "FROMMEMBER")) {
					searchParams.fromMember(args.get(0));
					args.remove(0);

				} else if (SH.equals(queryType, "FROMLONLAT")) {
					Double longitude = SH.parseDouble(args.get(0));
					args.remove(0);
					Double latitude = SH.parseDouble(args.get(0));
					args.remove(0);
					searchParams.fromLonLat(longitude, latitude);
				} else
					throw new Exception("Invalid query type: " + queryType + ", expected FROMMEMBER or FROMLONLAT");

				String searchBy = SH.toUpperCase(args.get(0));
				args.remove(0);

				if (SH.equals(searchBy, "BYRADIUS")) {

					Double radius = SH.parseDouble(args.get(0));
					args.remove(0);

					String geoUnitStr = SH.toUpperCase(args.get(0));
					args.remove(0);

					if (SH.equals(geoUnitStr, "M"))
						searchParams.byRadius(radius, GeoUnit.M);
					else if (SH.equals(geoUnitStr, "KM"))
						searchParams.byRadius(radius, GeoUnit.KM);
					else if (SH.equals(geoUnitStr, "FT"))
						searchParams.byRadius(radius, GeoUnit.FT);
					else if (SH.equals(geoUnitStr, "MI"))
						searchParams.byRadius(radius, GeoUnit.MI);
					else
						throw new Exception("Failed to parse GeoUnit, received: " + geoUnitStr + ", expected: M, KM, FT, or MI");

				} else if (SH.equals(searchBy, "BYBOX")) {

					double boxWidth, boxHeight;
					boxWidth = SH.parseDouble(args.get(0));
					args.remove(0);
					boxHeight = SH.parseDouble(args.get(0));
					args.remove(0);
					String geoUnitStr = SH.toUpperCase(args.get(0));
					args.remove(0);
					if (SH.equals(geoUnitStr, "M"))
						searchParams.byBox(boxWidth, boxHeight, GeoUnit.M);
					else if (SH.equals(geoUnitStr, "KM"))
						searchParams.byBox(boxWidth, boxHeight, GeoUnit.KM);
					else if (SH.equals(geoUnitStr, "FT"))
						searchParams.byBox(boxWidth, boxHeight, GeoUnit.FT);
					else if (SH.equals(geoUnitStr, "MI"))
						searchParams.byBox(boxWidth, boxHeight, GeoUnit.MI);
					else
						throw new Exception("Failed to parse GeoUnit, received: " + geoUnitStr + ", expected: M, KM, FT, or MI");
				} else
					throw new Exception("Failed to parse GEOSEARCH arguments, received: " + args.get(0) + ", expecting: BYRADIUS, or BYBOX");

				//Parse optional arguments
				boolean parseArgs = !args.isEmpty();
				boolean storeDistance = false;
				while (!args.isEmpty() && parseArgs) {
					String param = SH.toUpperCase(args.get(0));
					args.remove(0);

					if (SH.equals(param, "ASC")) {
						searchParams = searchParams.asc();
						continue;
					} else if (SH.equals(param, "DESC")) {
						searchParams = searchParams.desc();
						continue;
					} else if (SH.equals(param, "COUNT")) {
						int count = SH.parseInt(args.get(0));
						args.remove(0);
						if (!args.isEmpty() && SH.equals(SH.toUpperCase(args.get(0)), "ANY")) {
							searchParams = searchParams.count(count, true);
							args.remove(0);
						} else {
							searchParams = searchParams.count(count);
						}
						continue;
					} else if (SH.equals(param, "STOREDIST")) {
						storeDistance = true;
						continue;
					}

					parseArgs = false;
				}

				if (!args.isEmpty())
					throw new Exception("Failed to parse remaining arguments for GEOSEARCH: " + args.toString());

				resultSink.setReturnType(Long.class);
				if (storeDistance)
					resultSink.setReturnValue(jedis.geosearchStoreStoreDist(destination, source, searchParams));
				else
					resultSink.setReturnValue(jedis.geosearchStore(destination, source, searchParams));

			}
		});

		commands.put("GETBIT", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 2) {
					throw new Exception("Invalid number of arguments passed for GETBIT, expected 2, got: " + args.size());
				}

				Long offset = SH.parseLong(args.get(1));
				resultSink.setReturnType(Integer.class);
				resultSink.setReturnValue(jedis.getbit(args.get(0), offset) ? 1 : 0);

			}
		});

		commands.put("GETDEL", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for GETDEL, expected 1, got: " + argCount);
				}

				resultSink.setReturnType(String.class);
				resultSink.setReturnValue(jedis.getDel(args.get(0)));

			}
		});

		commands.put("GETEX", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 1) {
					throw new Exception("Invalid number of arguments passed for GETEX, expected >=1, got: " + argCount);
				}

				resultSink.setReturnType(String.class);
				GetExParams params = new GetExParams();
				if (argCount > 1) {
					String param = SH.toUpperCase(args.get(1));
					if (SH.equals(param, "EX")) {
						long seconds = SH.parseLong(args.get(2));
						params.ex(seconds);
					} else if (SH.equals(param, "PX")) {
						long milliseconds = SH.parseLong(args.get(2));
						params.px(milliseconds);
					} else if (SH.equals(param, "EXAT")) {
						long seconds = SH.parseLong(args.get(2));
						params.exAt(seconds);
					} else if (SH.equals(param, "PXAT")) {
						long milliseconds = SH.parseLong(args.get(2));
						params.pxAt(milliseconds);
					} else if (SH.equals(param, "PERSIST")) {
						params.persist();
					} else {
						throw new Exception("Error parsing params for GETEX - received: " + param + ", expecting: "
								+ "EX seconds | PX milliseconds | EXAT unix-time-seconds | PXAT unix-time-milliseconds | PERSIST");
					}
				}
				resultSink.setReturnValue(jedis.getEx(args.get(0), params));

			}
		});

		commands.put("GETRANGE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for GETRANGE, expected 3, got: " + argCount);
				}

				resultSink.setReturnType(String.class);
				long start = SH.parseLong(args.get(1));
				long end = SH.parseLong(args.get(2));

				resultSink.setReturnValue(jedis.getrange(args.get(0), start, end));

			}
		});

		commands.put("GETSET", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 2) {
					throw new Exception("Invalid number of arguments passed for GETSET, expected 2, got: " + argCount);
				}

				resultSink.setReturnType(String.class);
				resultSink.setReturnValue(jedis.getSet(args.get(0), args.get(1)));

			}
		});

		commands.put("HDEL", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for HDEL, expected >=2, got: " + argCount);
				}

				String key = args.get(0);
				args.remove(0);

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.hdel(key, args.toArray(new String[0])));

			}
		});

		commands.put("HEXISTS", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 2) {
					throw new Exception("Invalid number of arguments passed for HEXISTS, expected 2, got: " + argCount);
				}

				resultSink.setReturnType(Boolean.class);
				resultSink.setReturnValue(jedis.hexists(args.get(0), args.get(1)));

			}
		});

		commands.put("HINCRBY", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for HINCRBY, expected 3, got: " + argCount);
				}

				Long value = SH.parseLong(args.get(2));

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.hincrBy(args.get(0), args.get(1), value));

			}
		});

		commands.put("HINCRBYFLOAT", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for HINCRBYFLOAT, expected 3, got: " + argCount);
				}

				Double value = SH.parseDouble(args.get(2));

				resultSink.setReturnType(Boolean.class);
				resultSink.setReturnValue(jedis.hincrByFloat(args.get(0), args.get(1), value));

			}
		});

		commands.put("HKEYS", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for HKEYS, expected 1, got: " + argCount);
				}

				resultSink.setReturnType(java.util.Set.class);
				resultSink.setReturnValue(jedis.hkeys(args.get(0)));

			}
		});

		commands.put("HLEN", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for HLEN, expected 1, got: " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.hlen(args.get(0)));

			}
		});

		commands.put("HMGET", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 1) {
					throw new Exception("Invalid number of arguments passed for HMGET, expected >= 1, got: " + argCount);
				}

				String key = args.get(0);
				args.remove(0);

				resultSink.setReturnType(List.class);
				resultSink.setReturnValue(jedis.hmget(key, args.toArray(new String[0])));

			}
		});

		//		commands.put("HMSET", new RedisCommand() {
		//			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
		//					throws Exception {
		//				final int argCount = args.size();
		//				if (argCount < 3) {
		//					throw new Exception("Invalid number of arguments passed for HMSET, expected >= 3, got: " + argCount);
		//				}
		//
		//				String key = args.get(0);
		//				args.remove(0);
		//
		//				if (args.size() % 2 != 0)
		//					throw new Exception("Failed to parse HMSET arguments, expected format: HMSET key field value [ field value ...] ");
		//
		//				Map<String, String> valueHash = new HashMap<String, String>();
		//				for (int i = 0; i < args.size(); i += 2) {
		//					valueHash.put(args.get(i), args.get(i + 1));
		//				}
		//				resultSink.setReturnType(String.class);
		//				resultSink.setReturnValue(jedis.hmset(key, valueHash));
		//			}
		//		});

		commands.put("HRANDFIELD", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (!(argCount == 1 || argCount == 2 || argCount == 3)) {
					throw new Exception("Invalid number of arguments passed for HRANDFIELD, expected 1, 2, or 3, got: " + argCount);
				}

				if (argCount == 1) {
					resultSink.setReturnType(String.class);
					resultSink.setReturnValue(jedis.hrandfield(args.get(0)));
				} else if (argCount == 2) {
					resultSink.setReturnType(List.class);
					Long count = SH.parseLong(args.get(1));
					resultSink.setReturnValue(jedis.hrandfield(args.get(0), count));
				} else {
					resultSink.setReturnType(Map.class);
					Long count = SH.parseLong(args.get(1));
					if (!SH.equals(SH.toUpperCase(args.get(2)), "WITHVALUES"))
						throw new Exception("Failed to parse HRANDFIELD argument, received: " + args.get(2) + ", expecting: WITHVALUES");
					resultSink.setReturnValue(jedis.hrandfieldWithValues(args.get(0), count));
				}
			}
		});

		commands.put("HSCAN", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for HSCAN, expected >= 2, got: " + argCount);
				}
				if (argCount % 2 != 0)
					throw new Exception("Invalid argument format passed for HSCAN, expected: HSCAN key cursor [MATCH pattern] [COUNT count]");
				ScanParams param = new ScanParams();
				for (int i = 2; i < argCount; i += 2) {
					String paramStr = SH.toUpperCase(args.get(i));
					if (SH.equals(paramStr, "MATCH"))
						param.match(args.get(i + 1));
					else if (SH.equals(paramStr, "COUNT"))
						param.count(SH.parseInt(args.get(i + 1)));
					else
						throw new Exception("Invalid HSCAN param passed, received: " + paramStr + ", expecting: MATCH pattern OR COUNT count");
				}
				resultSink.setReturnType(List.class);
				ScanResult<Entry<String, String>> results = jedis.hscan(args.get(0), args.get(1), param);
				List<String> resultList = new ArrayList<String>();
				resultList.add(results.getCursor());
				for (Entry<String, String> r : results.getResult()) {
					resultList.add(r.getKey());
					resultList.add(r.getValue());
				}
				resultSink.setReturnValue(resultList);
			}
		});

		commands.put("HSET", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 3) {
					throw new Exception("Invalid number of arguments passed for HSET, expected >= 3, got: " + argCount);
				}

				String key = args.get(0);

				if (args.size() % 2 != 1)
					throw new Exception("Failed to parse HSET arguments, expected format: HSET key field value [ field value ...] ");

				Map<String, String> valueHash = new HashMap<String, String>();
				for (int i = 1; i < args.size(); i += 2) {
					valueHash.put(args.get(i), args.get(i + 1));
				}
				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.hset(key, valueHash));
			}
		});

		commands.put("HSETNX", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for HSETNX, expected 3, got: " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.hsetnx(args.get(0), args.get(1), args.get(2)));
			}
		});

		commands.put("HSTRLEN", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 2) {
					throw new Exception("Invalid number of arguments passed for HSTRLEN, expected 2, got: " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.hstrlen(args.get(0), args.get(1)));
			}
		});

		commands.put("HVALS", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for HVALS, expected 1, got: " + argCount);
				}

				resultSink.setReturnType(List.class);
				resultSink.setReturnValue(jedis.hvals(args.get(0)));
			}
		});

		commands.put("INCR", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for INCR, expected 1, got: " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.incr(args.get(0)));
			}
		});

		commands.put("INCRBY", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 2) {
					throw new Exception("Invalid number of arguments passed for INCRBY, expected 2, got: " + argCount);
				}
				Long val = SH.parseLong(args.get(1));
				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.incrBy(args.get(0), val));
			}
		});

		commands.put("INCRBYFLOAT", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 2) {
					throw new Exception("Invalid number of arguments passed for INCRBYFLOAT, expected 2, got: " + argCount);
				}
				Double val = SH.parseDouble(args.get(1));
				resultSink.setReturnType(Double.class);
				resultSink.setReturnValue(jedis.incrByFloat(args.get(0), val));
			}
		});

		commands.put("INFO", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (!(argCount == 0 || argCount == 1)) {
					throw new Exception("Invalid number of arguments passed for INFO, expected 0, or 1, got: " + argCount);
				}

				resultSink.setReturnType(String.class);
				if (argCount == 0)
					resultSink.setReturnValue(jedis.info());
				else
					resultSink.setReturnValue(jedis.info(args.get(0)));
			}
		});

		commands.put("KEYS", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for KEYS, expected 1, got: " + argCount);
				}

				resultSink.setReturnType(java.util.Set.class);
				resultSink.setReturnValue(jedis.keys(args.get(0)));
			}
		});

		commands.put("LASTSAVE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 0) {
					throw new Exception("Invalid number of arguments passed for LASTSAVE, expected 0, got: " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.lastsave());
			}
		});

		//Formatted to match - https://redis.io/commands/lcs/
		commands.put("LCS", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for LCS, expected >= 2, got: " + argCount);
				}

				Boolean len = false;
				Boolean idx = false;
				Boolean withMatchLen = false;

				LCSParams params = new LCSParams();
				for (int i = 2; i < argCount; ++i) {
					String paramStr = SH.toUpperCase(args.get(i));
					if (SH.equals(paramStr, "LEN")) {
						params.len();
						len = true;
					} else if (SH.equals(paramStr, "IDX")) {
						params.idx();
						idx = true;
					} else if (SH.equals(paramStr, "MINMATCHLEN")) {
						Long matchLength = SH.parseLong(args.get(i + 1));
						params.minMatchLen(matchLength);
						i += 1;
					} else if (SH.equals(paramStr, "WITHMATCHLEN")) {
						params.withMatchLen();
						withMatchLen = true;
					} else
						throw new Exception("Failed to parse params for LCS, received: " + paramStr + " expecting: LEN, IDX, MINMATCHLEN len, or WITHMATCHLEN");
				}

				LCSMatchResult result = jedis.lcs(args.get(0), args.get(1), params);
				if (len) {
					resultSink.setReturnType(Long.class);
					resultSink.setReturnValue(result.getLen());
				} else if (idx) {
					resultSink.setReturnType(List.class);
					ArrayList<Object> resultList = new ArrayList<Object>();
					resultList.add("matches");
					Long matchLen = null;
					ArrayList<Object> matchList = new ArrayList<Object>();
					for (MatchedPosition match : result.getMatches()) {
						if (matchLen == null)
							matchLen = match.getMatchLen();
						ArrayList<Object> matchAList = new ArrayList<Object>();
						matchAList.add(match.getA().getStart());
						matchAList.add(match.getA().getEnd());
						ArrayList<Object> matchBList = new ArrayList<Object>();
						matchBList.add(match.getB().getStart());
						matchBList.add(match.getB().getEnd());
						matchList.add(matchAList);
						matchList.add(matchBList);
					}
					if (withMatchLen)
						matchList.add(matchLen);

					resultList.add(matchList);
					resultList.add("len");
					resultList.add(result.getLen());
					resultSink.setReturnValue(resultList);
				} else {
					resultSink.setReturnType(String.class);
					resultSink.setReturnValue(result.getMatchString());
				}

			}
		});

		commands.put("LINDEX", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 2) {
					throw new Exception("Invalid number of arguments passed for LCS, expected == 2, got: " + argCount);
				}

				resultSink.setReturnType(String.class);
				resultSink.setReturnValue(jedis.lindex(args.get(0), SH.parseLong(args.get(1))));
			}
		});

		commands.put("LINSERT", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 4) {
					throw new Exception("Invalid number of arguments passed for LINSERT, expected == 4, got: " + argCount);
				}

				ListPosition position = null;
				String posStr = SH.toUpperCase(args.get(1));
				if (SH.equals(posStr, "BEFORE"))
					position = ListPosition.BEFORE;
				else if (SH.equals(posStr, "AFTER"))
					position = ListPosition.AFTER;
				else
					throw new Exception("Could not parse LINSERT argument, received: " + posStr + ", expected: BEFORE, or AFTER");

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.linsert(args.get(0), position, args.get(2), args.get(3)));
			}
		});

		commands.put("LLEN", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for LLEN, expected == 1, got: " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.llen(args.get(0)));
			}
		});

		commands.put("LMOVE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 4) {
					throw new Exception("Invalid number of arguments passed for LMOVE, expected == 4, got: " + argCount);
				}

				ListDirection positionFrom = null;
				String posStr = SH.toUpperCase(args.get(2));
				if (SH.equals(posStr, "LEFT"))
					positionFrom = ListDirection.LEFT;
				else if (SH.equals(posStr, "RIGHT"))
					positionFrom = ListDirection.RIGHT;
				else
					throw new Exception("Could not parse LINSERT argument, received: " + posStr + ", expected: BEFORE, or AFTER");

				ListDirection positionTo = null;
				posStr = SH.toUpperCase(args.get(3));
				if (SH.equals(posStr, "LEFT"))
					positionTo = ListDirection.LEFT;
				else if (SH.equals(posStr, "RIGHT"))
					positionTo = ListDirection.RIGHT;
				else
					throw new Exception("Could not parse LINSERT argument, received: " + posStr + ", expected: BEFORE, or AFTER");

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.lmove(args.get(0), args.get(1), positionFrom, positionTo));
			}
		});

		commands.put("LMPOP", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 3) {
					throw new Exception("Invalid number of arguments passed for LMPOP, expected >= 3, got: " + argCount);
				}

				int count = SH.parseInt(args.get(0));
				args.remove(0);

				ListDirection positionFrom = null;
				String posStr = SH.toUpperCase(args.get(count));
				if (SH.equals(posStr, "LEFT"))
					positionFrom = ListDirection.LEFT;
				else if (SH.equals(posStr, "RIGHT"))
					positionFrom = ListDirection.RIGHT;
				else
					throw new Exception("Could not parse LMPOP argument, received: " + posStr + ", expected: LEFT, or RIGHT");
				args.remove(count);

				//Parse optional COUNT argument
				resultSink.setReturnType(List.class);
				ArrayList<String> results = new ArrayList<String>();
				if (args.size() > count) {
					if (!SH.equals(SH.toUpperCase(args.get(count)), "COUNT"))
						throw new Exception("Failed to parse LMPOP parameter, received: " + args.get(count) + ", expected: COUNT");
					int val = SH.parseInt(args.get(count + 1));
					KeyValue<String, List<String>> result = jedis.lmpop(positionFrom, val, args.toArray(new String[0]));
					results.add(result.getKey());
					results.addAll(result.getValue());
					resultSink.setReturnValue(results);
				} else {
					KeyValue<String, List<String>> result = jedis.lmpop(positionFrom, args.toArray(new String[0]));
					results.add(result.getKey());
					results.addAll(result.getValue());
					resultSink.setReturnValue(results);
				}

			}

		});

		commands.put("LPOP", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (!(argCount == 1 || argCount == 2)) {
					throw new Exception("Invalid number of arguments passed for LPOP, expected 1, or 2, got: " + argCount);
				}

				if (argCount == 1) {
					resultSink.setReturnType(String.class);
					resultSink.setReturnValue(jedis.lpop(args.get(0)));
				} else {
					resultSink.setReturnType(List.class);
					resultSink.setReturnValue(jedis.lpop(args.get(0), SH.parseInt(args.get(1))));
				}
			}

		});

		commands.put("LPOS", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for LPOS, expected >= 2, got: " + argCount);
				}

				String key = SH.trim(args.get(0));
				args.remove(0);
				String element = SH.trim(args.get(0));
				args.remove(0);

				LPosParams params = new LPosParams();
				Integer count = null;

				for (int i = 0; i < args.size(); i += 2) {
					String paramStr = SH.toUpperCase(args.get(i));
					if (SH.equals(paramStr, "RANK")) {
						int rank = SH.parseInt(args.get(i + 1));
						params.rank(rank);
					} else if (SH.equals(paramStr, "COUNT")) {
						int _count = SH.parseInt(args.get(i + 1));
						count = _count;
					} else if (SH.equals(paramStr, "MAXLEN")) {
						int len = SH.parseInt(args.get(i + 1));
						params.maxlen(len);
					}
				}

				if (count != null) {
					resultSink.setReturnType(List.class);
					resultSink.setReturnValue(jedis.lpos(key, element, params, count));
				} else {
					resultSink.setReturnType(Long.class);
					resultSink.setReturnValue(jedis.lpos(key, element, params));
				}

			}

		});

		commands.put("LPUSH", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for LPUSH, expected >= 2, got: " + argCount);
				}

				String key = SH.trim(args.get(0));
				args.remove(0);

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.lpush(key, args.toArray(new String[0])));
			}

		});

		commands.put("LPUSHX", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for LPUSHX, expected >= 2, got: " + argCount);
				}

				String key = SH.trim(args.get(0));
				args.remove(0);

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.lpushx(key, args.toArray(new String[0])));
			}

		});

		commands.put("LRANGE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for LRANGE, expected 3, got: " + argCount);
				}

				resultSink.setReturnType(List.class);
				resultSink.setReturnValue(jedis.lrange(args.get(0), SH.parseLong(args.get(1)), SH.parseLong(args.get(2))));
			}

		});

		commands.put("LREM", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for LREM, expected 3, got: " + argCount);
				}

				resultSink.setReturnType(List.class);
				resultSink.setReturnValue(jedis.lrem(args.get(0), SH.parseLong(args.get(1)), args.get(2)));
			}

		});

		commands.put("LSET", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for LSET, expected 3, got: " + argCount);
				}

				resultSink.setReturnValue(jedis.lset(args.get(0), SH.parseLong(args.get(1)), args.get(2)));
				resultSink.setReturnType(List.class);
			}

		});

		commands.put("LTRIM", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for LTRIM, expected 3, got: " + argCount);
				}

				resultSink.setReturnValue(jedis.ltrim(args.get(0), SH.parseLong(args.get(1)), SH.parseLong(args.get(2))));
				resultSink.setReturnType(List.class);

			}

		});

		commands.put("MGET", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 1) {
					throw new Exception("Invalid number of arguments passed for MGET, expected >= 1, got: " + argCount);
				}

				resultSink.setReturnType(List.class);
				resultSink.setReturnValue(jedis.mget(args.toArray(new String[0])));

			}

		});

		commands.put("MOVE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 2) {
					throw new Exception("Invalid number of arguments passed for MOVE, expected 2, got: " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.move(args.get(0), SH.parseInt(args.get(1))));
			}
		});

		commands.put("MSET", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount % 2 != 0 || argCount < 2) {
					throw new Exception("Invalid number of arguments passed for MSET, expected >=2 and even number of arguments, got: " + argCount);
				}

				resultSink.setReturnType(String.class);
				resultSink.setReturnValue(jedis.mset(args.toArray(new String[0])));
			}
		});

		commands.put("MSETNX", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount % 2 != 0 || argCount < 2) {
					throw new Exception("Invalid number of arguments passed for MSETNX, expected >=2 and even number of arguments, got: " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.msetnx(args.toArray(new String[0])));
			}
		});

		commands.put("PERSIST", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for PERSIST, expected 1, got: " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.persist(args.get(0)));
			}
		});

		commands.put("PEXPIRE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for PEXPIRE, expected >=2, got " + args.size());
				}
				Long seconds = SH.parseLong(args.get(1));

				resultSink.setReturnType(Long.class);
				if (argCount == 2) {
					resultSink.setReturnValue(jedis.pexpire(args.get(0), seconds));
				} else {
					//Jedis does not support REDIS compliant options such as XX GT / XX LT, maximum of one option allowed
					String key = args.get(0);
					args.remove(0);
					args.remove(0);
					ExpiryOption expiryOptions = null;
					for (int i = 0; i < args.size(); ++i) {
						String arg = SH.toUpperCase(args.get(i));
						if (SH.equals(arg, "NX")) {
							expiryOptions = ExpiryOption.NX;
							break;
						} else if (SH.equals(arg, "XX")) {
							expiryOptions = ExpiryOption.XX;
							break;
						} else if (SH.equals(arg, "GT")) {
							expiryOptions = ExpiryOption.GT;
							break;
						} else if (SH.equals(arg, "LT")) {
							expiryOptions = ExpiryOption.LT;
							break;
						} else {
							throw new Exception("Unrecognized expiry option: " + arg + ", expecting, NX, XX, GT, or LT");
						}

					}

					resultSink.setReturnValue(jedis.pexpire(key, seconds, expiryOptions));
				}
			}

		});

		commands.put("PEXPIREAT", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for PEXPIREAT, expected >=2, got " + args.size());
				}
				Long seconds = SH.parseLong(args.get(1));

				resultSink.setReturnType(Long.class);
				if (argCount == 2) {
					resultSink.setReturnValue(jedis.pexpireAt(args.get(0), seconds));
				} else {
					//Jedis does not support REDIS compliant options such as XX GT / XX LT, maximum of one option allowed
					String key = args.get(0);
					args.remove(0);
					args.remove(0);
					ExpiryOption expiryOptions = null;
					for (int i = 0; i < args.size(); ++i) {
						String arg = SH.toUpperCase(args.get(i));
						if (SH.equals(arg, "NX")) {
							expiryOptions = ExpiryOption.NX;
							break;
						} else if (SH.equals(arg, "XX")) {
							expiryOptions = ExpiryOption.XX;
							break;
						} else if (SH.equals(arg, "GT")) {
							expiryOptions = ExpiryOption.GT;
							break;
						} else if (SH.equals(arg, "LT")) {
							expiryOptions = ExpiryOption.LT;
							break;
						} else {
							throw new Exception("Unrecognized expiry option: " + arg + ", expecting, NX, XX, GT, or LT");
						}

					}

					resultSink.setReturnValue(jedis.pexpireAt(key, seconds, expiryOptions));
				}
			}
		});

		commands.put("PEXPIRETIME", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for PEXPIRETIME, expected 1, got " + args.size());
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.pexpireTime(args.get(0)));

			}
		});

		commands.put("PING", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (!(argCount == 0 || argCount == 1)) {
					throw new Exception("Invalid number of arguments passed for PEXPIRETIME, expected 0, or 1, got " + argCount);
				}
				resultSink.setReturnType(String.class);
				if (argCount == 0)
					resultSink.setReturnValue(jedis.ping());
				else
					resultSink.setReturnValue(jedis.ping(args.get(0)));
			}
		});

		commands.put("RANDOMKEY", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 0) {
					throw new Exception("Invalid number of arguments passed for RANDOMKEY, expected 0, got " + argCount);
				}
				resultSink.setReturnType(String.class);
				resultSink.setReturnValue(jedis.randomKey());
			}
		});

		commands.put("RENAME", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 2) {
					throw new Exception("Invalid number of arguments passed for RENAME, expected 2, got " + argCount);
				}
				resultSink.setReturnType(String.class);
				resultSink.setReturnValue(jedis.rename(args.get(0), args.get(1)));
			}
		});

		commands.put("RENAMENX", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 2) {
					throw new Exception("Invalid number of arguments passed for RENAMENX, expected 2, got " + argCount);
				}
				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.renamenx(args.get(0), args.get(1)));
			}
		});

		commands.put("RENAMENX", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 2) {
					throw new Exception("Invalid number of arguments passed for RENAMENX, expected 2, got " + argCount);
				}
				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.renamenx(args.get(0), args.get(1)));
			}
		});

		commands.put("RESTORE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 3) {
					throw new Exception("Invalid number of arguments passed for RESTORE, expected >=3, got " + argCount);
				}

				RestoreParams param = new RestoreParams();
				for (int i = 3; i < argCount; ++i) {
					String paramStr = SH.toUpperCase(args.get(i));
					if (SH.equals(paramStr, "REPLACE")) {
						param.replace();
					} else if (SH.equals(paramStr, "ABSTTL")) {
						param.absTtl();
					} else if (SH.equals(paramStr, "IDLETIME")) {
						Long idleTime = SH.parseLong(args.get(i + 1));
						param.idleTime(idleTime);
						++i;
					} else if (SH.equals(paramStr, "FREQ")) {
						Long frequency = SH.parseLong(args.get(i + 1));
						param.frequency(frequency);
						++i;
					} else
						throw new Exception("Failed to parse resource param, received: " + paramStr + ", expected: " + "REPLACE, ABSTTL, IDLETIME seconds, or FREQ frequency");
				}
				resultSink.setReturnType(String.class);
				String byteStr = args.get(2);
				final int byteLength = byteStr.length();
				if (byteLength % 2 != 0)
					throw new Exception("Invalid byte array size received, please use a base 16 binary string or binaryToStr16 when converting DUMP results");
				byte[] result = new byte[byteStr.length() / 2];
				for (int i = 0; i < byteStr.length(); i += 2) {
					String sub = byteStr.substring(i, i + 2);
					int c = SH.parseInt(sub, 16);
					result[i / 2] = (byte) c;
				}

				resultSink.setReturnValue(jedis.restore(args.get(0), SH.parseLong(args.get(1)), result, param));
			}
		});

		commands.put("RPOP", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (!(argCount == 1 || argCount == 2)) {
					throw new Exception("Invalid number of arguments passed for RPOP, expected 1, or 2, got " + argCount);
				}

				if (argCount == 1) {
					resultSink.setReturnType(String.class);
					resultSink.setReturnValue(jedis.rpop(args.get(0)));
				} else {
					resultSink.setReturnType(List.class);
					resultSink.setReturnValue(jedis.rpop(args.get(0), SH.parseInt(args.get(1))));
				}

			}
		});

		commands.put("RPOP", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (!(argCount == 1 || argCount == 2)) {
					throw new Exception("Invalid number of arguments passed for RPOP, expected 1, or 2, got " + argCount);
				}

				if (argCount == 1) {
					resultSink.setReturnType(String.class);
					resultSink.setReturnValue(jedis.rpop(args.get(0)));
				} else {
					resultSink.setReturnType(String.class);
					resultSink.setReturnValue(jedis.rpop(args.get(0), SH.parseInt(args.get(1))));
				}

			}
		});

		commands.put("RPUSH", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for RPUSH, expected >= 2, got " + argCount);
				}

				String key = args.get(0);
				args.remove(0);

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.rpush(key, args.toArray(new String[0])));

			}
		});

		commands.put("RPUSHX", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for RPUSHX, expected >= 2, got " + argCount);
				}

				String key = args.get(0);
				args.remove(0);

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.rpushx(key, args.toArray(new String[0])));

			}
		});

		commands.put("SADD", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for SADD, expected >= 2, got " + argCount);
				}

				String key = args.get(0);
				args.remove(0);

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.sadd(key, args.toArray(new String[0])));

			}
		});

		commands.put("SAVE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 0) {
					throw new Exception("Invalid number of arguments passed for SAVE, expected 0, got " + argCount);
				}

				resultSink.setReturnType(String.class);
				resultSink.setReturnValue(jedis.save());

			}
		});

		commands.put("SCAN", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 1) {
					throw new Exception("Invalid number of arguments passed for SCAN, expected >= 1, got: " + argCount);
				}
				if (argCount % 2 != 1)
					throw new Exception("Invalid argument format passed for SCAN, expected: SCAN cursor [MATCH pattern] [COUNT count]");
				ScanParams param = new ScanParams();
				String scanType = "";
				for (int i = 1; i < argCount; i += 2) {
					String paramStr = SH.toUpperCase(args.get(i));
					if (SH.equals(paramStr, "MATCH"))
						param.match(args.get(i + 1));
					else if (SH.equals(paramStr, "COUNT"))
						param.count(SH.parseInt(args.get(i + 1)));
					else if (SH.equals(paramStr, "TYPE")) {
						scanType = args.get(i + 1);
					} else
						throw new Exception("Invalid SCAN param passed, received: " + paramStr + ", expecting: MATCH pattern OR COUNT count");
				}
				resultSink.setReturnType(List.class);

				if (scanType.isEmpty()) {
					ScanResult<String> results = jedis.scan(args.get(0), param);
					List<String> resultList = new ArrayList<String>();
					resultList.add(results.getCursor());
					resultList.addAll(results.getResult());
					resultSink.setReturnValue(resultList);
				} else {
					ScanResult<String> results = jedis.scan(args.get(0), param, scanType);
					List<String> resultList = new ArrayList<String>();
					resultList.add(results.getCursor());
					resultList.addAll(results.getResult());
					resultSink.setReturnValue(resultList);
				}
			}
		});

		commands.put("SCARD", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for SCARD, expected 1, got " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.scard(args.get(0)));

			}
		});

		commands.put("SDIFF", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 1) {
					throw new Exception("Invalid number of arguments passed for SDIFF, expected >= 1, got " + argCount);
				}

				resultSink.setReturnType(List.class);
				resultSink.setReturnValue(new ArrayList<String>(jedis.sdiff(args.toArray(new String[0]))));

			}
		});

		commands.put("SDIFFSTORE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for SDIFFSTORE, expected >= 2, got " + argCount);
				}

				String dest = args.get(0);
				args.remove(0);

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.sdiffstore(dest, args.toArray(new String[0])));

			}
		});

		commands.put("SETBIT", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for SETBIT, expected 3, got " + argCount);
				}

				Boolean value = SH.parseInt(args.get(2)) == 1;
				resultSink.setReturnType(Integer.class);
				resultSink.setReturnValue(jedis.setbit(args.get(0), SH.parseLong(args.get(1)), value) ? 1 : 0);

			}
		});

		commands.put("SETEX", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for SETEX, expected 3, got " + argCount);
				}

				resultSink.setReturnType(String.class);
				resultSink.setReturnValue(jedis.setex(args.get(0), SH.parseLong(args.get(1)), args.get(2)));

			}
		});

		commands.put("SETNX", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 2) {
					throw new Exception("Invalid number of arguments passed for SETNX, expected 2, got " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.setnx(args.get(0), args.get(1)));

			}
		});

		commands.put("SETRANGE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for SETRANGE, expected 3, got " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.setrange(args.get(0), SH.parseLong(args.get(1)), args.get(2)));

			}
		});

		commands.put("SINTER", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 1) {
					throw new Exception("Invalid number of arguments passed for SINTER, expected >= 1, got " + argCount);
				}

				resultSink.setReturnType(Set.class);
				resultSink.setReturnValue(jedis.sinter(args.toArray(new String[0])));

			}
		});

		commands.put("SINTERCARD", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 3) {
					throw new Exception("Invalid number of arguments passed for SINTERCARD, expected >= 1, got " + argCount);
				}

				int numKeys = SH.parseInt(args.get(0));
				args.remove(0);

				if (args.size() < numKeys)
					throw new Exception("Invalid number of keys passed in, expecting: " + numKeys + ", got: " + args.size());

				Integer limit = null;
				if (args.size() > numKeys) {
					if (!SH.equals(SH.toUpperCase(args.get(args.size() - 2)), "LIMIT"))
						throw new Exception("Failed to parse LIMIT parameter for SINTERCARD, received: " + args.get(args.size() - 2) + ", expecting: LIMIT");
					limit = SH.parseInt(args.get(args.size() - 1));
					args.remove(args.size() - 1);
					args.remove(args.size() - 1);
				}

				if (limit == null) {
					resultSink.setReturnType(Long.class);
					resultSink.setReturnValue(jedis.sintercard(args.toArray(new String[0])));
				} else {
					resultSink.setReturnType(Long.class);
					resultSink.setReturnValue(jedis.sintercard(limit, args.toArray(new String[0])));
				}

			}
		});

		commands.put("SINTERSTORE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for SINTERSTORE, expected >= 2, got " + argCount);
				}

				String destination = args.get(0);
				args.remove(0);

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.sinterstore(destination, args.toArray(new String[0])));

			}
		});

		commands.put("SISMEMBER", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 2) {
					throw new Exception("Invalid number of arguments passed for SISMEMBER, expected 2, got " + argCount);
				}

				resultSink.setReturnType(Boolean.class);
				resultSink.setReturnValue(jedis.sismember(args.get(0), args.get(1)));

			}
		});

		commands.put("SMEMBERS", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for SMEMBERS, expected 1, got " + argCount);
				}

				resultSink.setReturnType(Set.class);
				resultSink.setReturnValue(jedis.smembers(args.get(0)));

			}
		});

		commands.put("SMISMEMBER", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for SMISMEMBER, expected >= 2, got " + argCount);
				}

				String key = args.get(0);
				args.remove(0);

				resultSink.setReturnType(List.class);
				resultSink.setReturnValue(jedis.smismember(key, args.toArray(new String[0])));

			}
		});

		commands.put("SMOVE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for SMOVE, expected 3, got " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.smove(args.get(0), args.get(1), args.get(2)));

			}
		});

		commands.put("SORT", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 1) {
					throw new Exception("Invalid number of arguments passed for SORT, expected >= 1, got " + argCount);
				}

				SortingParams param = new SortingParams();
				String destination = "";
				for (int i = 1; i < argCount; ++i) {
					String paramStr = SH.toUpperCase(args.get(i));

					if (SH.equals(paramStr, "BY")) {
						param.by(args.get(i + 1));
						++i;
					} else if (SH.equals(paramStr, "LIMIT")) {
						int offset = SH.parseInt(args.get(i + 1));
						int count = SH.parseInt(args.get(i + 2));
						i += 2;
						param.limit(offset, count);
					} else if (SH.equals(paramStr, "GET")) {
						param.get(args.get(i + 1));
						++i;
					} else if (SH.equals(paramStr, "ASC")) {
						param.asc();
					} else if (SH.equals(paramStr, "DESC")) {
						param.desc();
					} else if (SH.equals(paramStr, "ALPHA")) {
						param.alpha();
					} else if (SH.equals(paramStr, "STORE")) {
						destination = args.get(i + 1);
						++i;
					} else {
						throw new Exception("Failed to parse param for SORT, received: " + paramStr
								+ ", expecting:  BY pattern, LIMIT offset count, GET pattern, ASC|DESC, ALPHA, or STORE destination");
					}
				}

				if (destination.isEmpty()) {
					resultSink.setReturnType(List.class);
					resultSink.setReturnValue(jedis.sort(args.get(0), param));
				} else {
					resultSink.setReturnType(Long.class);
					resultSink.setReturnValue(jedis.sort(args.get(0), param, destination));
				}
			}
		});

		commands.put("SORT_RO", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 1) {
					throw new Exception("Invalid number of arguments passed for SORT_RO, expected >= 1, got " + argCount);
				}

				SortingParams param = new SortingParams();
				for (int i = 1; i < argCount; ++i) {
					String paramStr = SH.toUpperCase(args.get(i));

					if (SH.equals(paramStr, "BY")) {
						param.by(args.get(i + 1));
						++i;
					} else if (SH.equals(paramStr, "LIMIT")) {
						int offset = SH.parseInt(args.get(i + 1));
						int count = SH.parseInt(args.get(i + 2));
						i += 2;
						param.limit(offset, count);
					} else if (SH.equals(paramStr, "GET")) {
						param.get(args.get(i + 1));
						++i;
					} else if (SH.equals(paramStr, "ASC")) {
						param.asc();
					} else if (SH.equals(paramStr, "DESC")) {
						param.desc();
					} else if (SH.equals(paramStr, "ALPHA")) {
						param.alpha();
					} else {
						throw new Exception(
								"Failed to parse param for SORT, received: " + paramStr + ", expecting:  BY pattern, LIMIT offset count, GET pattern, ASC|DESC, or ALPHA");
					}
				}

				resultSink.setReturnType(List.class);
				resultSink.setReturnValue(jedis.sortReadonly(args.get(0), param));
			}
		});

		commands.put("SPOP", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (!(args.size() == 1 || args.size() == 2)) {
					throw new Exception("Invalid number of arguments passed for SPOP, expected 1, or 2, got " + args.size());
				}

				if (args.size() == 2) {
					resultSink.setReturnType(Set.class);
					resultSink.setReturnValue(jedis.spop(args.get(0), SH.parseLong(args.get(1))));
				} else {
					resultSink.setReturnType(String.class);
					resultSink.setReturnValue(jedis.spop(args.get(0)));
				}

			}
		});

		commands.put("SRANDMEMBER", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (!(args.size() == 1 || args.size() == 2)) {
					throw new Exception("Invalid number of arguments passed for SRANDMEMBER, expected 1, or 2, got " + args.size());
				}

				if (args.size() == 2) {
					resultSink.setReturnType(Set.class);
					resultSink.setReturnValue(new HashSet<String>(jedis.srandmember(args.get(0), SH.parseInt(args.get(1)))));
				} else {
					resultSink.setReturnType(String.class);
					resultSink.setReturnValue(jedis.srandmember(args.get(0)));
				}

			}
		});

		commands.put("SREM", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for SREM, expected >= 2, got " + argCount);
				}

				String key = args.get(0);
				args.remove(0);

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.srem(key, args.toArray(new String[0])));

			}
		});

		commands.put("SSCAN", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for SSCAN, expected >= 2, got: " + argCount);
				}
				if (argCount % 2 != 0)
					throw new Exception("Invalid argument format passed for SSCAN, expected: SSCAN key cursor [MATCH pattern] [COUNT count]");
				ScanParams param = new ScanParams();
				for (int i = 2; i < argCount; i += 2) {
					String paramStr = SH.toUpperCase(args.get(i));
					if (SH.equals(paramStr, "MATCH"))
						param.match(args.get(i + 1));
					else if (SH.equals(paramStr, "COUNT"))
						param.count(SH.parseInt(args.get(i + 1)));
					else
						throw new Exception("Invalid SSCAN param passed, received: " + paramStr + ", expecting: MATCH pattern OR COUNT count");
				}
				resultSink.setReturnType(List.class);
				ScanResult<String> results = jedis.sscan(args.get(0), args.get(1), param);
				List<String> resultList = new ArrayList<String>();
				resultList.add(results.getCursor());
				resultList.addAll(results.getResult());
				resultSink.setReturnValue(resultList);
			}
		});

		commands.put("STRLEN", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 1) {
					throw new Exception("Invalid number of arguments passed for STRLEN, expected 1, got " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.strlen(args.get(0)));

			}
		});

		//		commands.put("SUBSTR", new RedisCommand() {
		//			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
		//					throws Exception {
		//				final int argCount = args.size();
		//				if (argCount != 3) {
		//					throw new Exception("Invalid number of arguments passed for SUBSTR, expected 3, got " + argCount);
		//				}
		//
		//				resultSink.setReturnType(String.class);
		//				resultSink.setReturnValue(jedis.substr(args.get(0), SH.parseInt(args.get(1)), SH.parseInt(args.get(2))));
		//
		//			}
		//		});

		commands.put("SUNION", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 1) {
					throw new Exception("Invalid number of arguments passed for SUNION, expected >= 1, got " + argCount);
				}

				resultSink.setReturnType(Set.class);
				resultSink.setReturnValue(jedis.sunion(args.toArray(new String[0])));

			}
		});

		commands.put("SUNIONSTORE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for SUNIONSTORE, expected >= 2, got " + argCount);
				}

				String dest = args.get(0);
				args.remove(0);

				resultSink.setReturnType(Set.class);
				resultSink.setReturnValue(jedis.sunionstore(dest, args.toArray(new String[0])));

			}
		});

		commands.put("TIME", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 0) {
					throw new Exception("Invalid number of arguments passed for TIME, expected 0, got " + argCount);
				}

				resultSink.setReturnType(List.class);
				resultSink.setReturnValue(jedis.time());

			}
		});

		commands.put("TOUCH", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 1) {
					throw new Exception("Invalid number of arguments passed for TOUCH, expected >= 1, got " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.touch(args.toArray(new String[0])));

			}
		});

		commands.put("TTL", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 1) {
					throw new Exception("Invalid number of arguments passed for GET, expected 1, got " + args.size());
				}

				resultSink.setReturnType(String.class);
				resultSink.setReturnValue(jedis.ttl(args.get(0)));
			}
		});

		commands.put("TYPE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 1) {
					throw new Exception("Invalid number of arguments passed for TYPE, expected 1, got " + args.size());
				}

				resultSink.setReturnType(String.class);
				resultSink.setReturnValue(jedis.type(args.get(0)));
			}
		});

		commands.put("UNLINK", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 1) {
					throw new Exception("Invalid number of arguments passed for UNLINK, expected >= 1, got " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.unlink(args.toArray(new String[0])));

			}
		});

		commands.put("ZADD", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 3) {
					throw new Exception("Invalid number of arguments passed for ZADD, expected >=3, got: " + args.size());
				}

				String key = args.get(0);
				args.remove(0);

				//Parse optional flags
				boolean parseFlags = true;
				boolean incr = false;
				ZAddParams params = new ZAddParams();
				while (parseFlags) {
					String flag = SH.toUpperCase(args.get(0));
					if (SH.equals(flag, "NX")) {
						params.nx();
						args.remove(0);
						continue;
					} else if (SH.equals(flag, "XX")) {
						params.xx();
						args.remove(0);
						continue;
					} else if (SH.equals(flag, "CH")) {
						params.ch();
						args.remove(0);
						continue;
					} else if (SH.equals(flag, "GT")) {
						params.gt();
						args.remove(0);
						continue;
					} else if (SH.equals(flag, "LT")) {
						params.lt();
						args.remove(0);
						continue;
					} else if (SH.equals(flag, "INCR")) {
						incr = true;
						args.remove(0);
						continue;
					}

					parseFlags = false;

				}

				if (args.size() % 2 != 0)
					throw new Exception("Failed to parse ZADD arguments - argument size should be divisible by 2 in the following format: "
							+ "ZADD key [ NX | XX] [ GT | LT] [CH] [INCR] score member [ score member ...]");

				if (incr == true) {
					if (args.size() > 2)
						throw new Exception("Invalid format for ZADD with INCR option - only one score-element pair can be specified");
					resultSink.setReturnType(Double.class);
					resultSink.setReturnValue(jedis.zaddIncr(key, SH.parseDouble(args.get(0)), args.get(1), params));
				} else {
					Map<String, Double> members = new HashMap<String, Double>();
					for (int i = 0; i < args.size(); i += 2) {
						members.put(args.get(i + 1), SH.parseDouble(args.get(i)));
					}

					resultSink.setReturnType(Long.class);
					resultSink.setReturnValue(jedis.zadd(key, members, params));
				}

			}
		});

		commands.put("ZCARD", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 1) {
					throw new Exception("Invalid number of arguments passed for ZCARD, expected 1, got " + args.size());
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.zcard(args.get(0)));
			}
		});

		commands.put("ZCOUNT", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for ZCOUNT, expected 3, got " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.zcount(args.get(0), args.get(1), args.get(2)));

			}
		});

		commands.put("ZCOUNT", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for ZCOUNT, expected 3, got " + argCount);
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.zcount(args.get(0), args.get(1), args.get(2)));

			}
		});

		commands.put("ZDIFF", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for ZDIFF, expected >= 2, got: " + argCount);
				}

				int count = SH.parseInt(args.get(0));
				args.remove(0);

				Boolean WITHSCORES = false;
				String posStr = SH.toUpperCase(args.get(args.size() - 1));
				if (SH.equals(posStr, "WITHSCORES")) {
					WITHSCORES = true;
					args.remove(args.size() - 1);
				}

				if (args.size() != count)
					throw new Exception("Invalid number of keys, received: " + args.size() + ", expecting: " + count);

				if (WITHSCORES) {
					resultSink.setReturnType(List.class);
					Set<Tuple> results = jedis.zdiffWithScores(args.toArray(new String[0]));
					ArrayList<String> result = new ArrayList<String>();
					for (Tuple t : results) {
						result.add(t.getElement());
						result.add(SH.toString(t.getScore()));
					}
					resultSink.setReturnValue(result);
				} else {
					resultSink.setReturnType(Set.class);
					resultSink.setReturnValue(jedis.zdiff(args.toArray(new String[0])));
				}
			}

		});

		commands.put("ZDIFFSTORE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 3) {
					throw new Exception("Invalid number of arguments passed for ZDIFFSTORE, expected >= 3, got: " + argCount);
				}

				String destination = args.get(0);
				args.remove(0);

				int count = SH.parseInt(args.get(0));
				args.remove(0);

				if (args.size() != count)
					throw new Exception("Invalid number of keys, received: " + args.size() + ", expecting: " + count);

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.zdiffStore(destination, args.toArray(new String[0])));

			}

		});

		commands.put("ZINCRBY", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for ZINCRBY, expected 3, got " + argCount);
				}

				resultSink.setReturnType(Double.class);
				resultSink.setReturnValue(jedis.zincrby(args.get(0), SH.parseDouble(args.get(1)), args.get(2)));

			}
		});

		commands.put("ZINTER", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for ZINTER, expected >= 2, got " + argCount);
				}

				int numKeys = SH.parseInt(args.get(0));
				args.remove(0);

				Boolean withScores = false;
				ZParams params = new ZParams();
				for (int i = numKeys; i < args.size(); ++i) {
					String paramStr = SH.toUpperCase(args.get(i));
					if (SH.equals(paramStr, "WEIGHTS")) {
						List<String> weightsStr = args.subList(i + 1, i + 1 + numKeys);
						double[] result = new double[numKeys];
						for (int j = 0; j < weightsStr.size(); ++j)
							result[j] = SH.parseDouble(weightsStr.get(j));

						params.weights(result);
						i += numKeys;
					} else if (SH.equals(paramStr, "AGGREGATE")) {
						String aggregateType = SH.toUpperCase(args.get(i + 1));
						if (SH.equals(aggregateType, "SUM"))
							params.aggregate(Aggregate.SUM);
						else if (SH.equals(aggregateType, "MIN"))
							params.aggregate(Aggregate.MIN);
						else if (SH.equals(aggregateType, "MAX"))
							params.aggregate(Aggregate.MAX);
						else
							throw new Exception("Failed to parse AGGREGATE type for ZINTER, received: " + aggregateType + ", expecting: SUM, MIN, or MAX");
						++i;
					} else if (SH.equals(paramStr, "WITHSCORES")) {
						withScores = true;
					} else {
						throw new Exception("Failed to parse param type for ZINTER, received: " + paramStr
								+ ", expecting: [WEIGHTS weight [weight ...]] [AGGREGATE SUM | MIN | MAX] [WITHSCORES]");
					}
				}

				if (withScores) {
					resultSink.setReturnType(List.class);
					Set<Tuple> results = jedis.zinterWithScores(params, args.subList(0, numKeys).toArray(new String[0]));
					ArrayList<String> result = new ArrayList<String>();
					for (Tuple t : results) {
						result.add(t.getElement());
						result.add(SH.toString(t.getScore()));
					}
					resultSink.setReturnValue(result);
				} else {
					resultSink.setReturnType(Set.class);
					resultSink.setReturnValue(jedis.zinter(params, args.subList(0, numKeys).toArray(new String[0])));
				}
			}
		});

		commands.put("ZINTERCARD", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for ZINTERCARD, expected >= 2, got " + argCount);
				}

				int numKeys = SH.parseInt(args.get(0));
				args.remove(0);

				if (args.size() < numKeys)
					throw new Exception("Invalid number of keys passed in, expecting: " + numKeys + ", got: " + args.size());

				Integer limit = null;
				if (args.size() > numKeys) {
					if (!SH.equals(SH.toUpperCase(args.get(args.size() - 2)), "LIMIT"))
						throw new Exception("Failed to parse LIMIT parameter for ZINTERCARD, received: " + args.get(args.size() - 2) + ", expecting: LIMIT");
					limit = SH.parseInt(args.get(args.size() - 1));
					args.remove(args.size() - 1);
					args.remove(args.size() - 1);
				}

				if (limit == null) {
					resultSink.setReturnType(Long.class);
					resultSink.setReturnValue(jedis.zintercard(args.toArray(new String[0])));
				} else {
					resultSink.setReturnType(Long.class);
					resultSink.setReturnValue(jedis.zintercard(limit, args.toArray(new String[0])));
				}

			}
		});

		commands.put("ZINTERSTORE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 3) {
					throw new Exception("Invalid number of arguments passed for ZINTERSTORE, expected >= 3, got " + argCount);
				}

				String destination = args.get(0);
				args.remove(0);

				int numKeys = SH.parseInt(args.get(0));
				args.remove(0);

				ZParams params = new ZParams();
				for (int i = numKeys; i < args.size(); ++i) {
					String paramStr = SH.toUpperCase(args.get(i));
					if (SH.equals(paramStr, "WEIGHTS")) {
						List<String> weightsStr = args.subList(i + 1, i + 1 + numKeys);
						double[] result = new double[numKeys];
						for (int j = 0; j < weightsStr.size(); ++j)
							result[j] = SH.parseDouble(weightsStr.get(j));

						params.weights(result);
						i += numKeys;
					} else if (SH.equals(paramStr, "AGGREGATE")) {
						String aggregateType = SH.toUpperCase(args.get(i + 1));
						if (SH.equals(aggregateType, "SUM"))
							params.aggregate(Aggregate.SUM);
						else if (SH.equals(aggregateType, "MIN"))
							params.aggregate(Aggregate.MIN);
						else if (SH.equals(aggregateType, "MAX"))
							params.aggregate(Aggregate.MAX);
						else
							throw new Exception("Failed to parse AGGREGATE type for ZINTER, received: " + aggregateType + ", expecting: SUM, MIN, or MAX");
						++i;
					} else {
						throw new Exception("Failed to parse param type for ZINTER, received: " + paramStr
								+ ", expecting: [WEIGHTS weight [weight ...]] [AGGREGATE SUM | MIN | MAX] [WITHSCORES]");
					}
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.zinterstore(destination, params, args.subList(0, numKeys).toArray(new String[0])));

			}
		});

		commands.put("ZLEXCOUNT", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount != 3) {
					throw new Exception("Invalid number of arguments passed for ZLEXCOUNT, expected 3, got " + argCount);
				}

				resultSink.setReturnType(Double.class);
				resultSink.setReturnValue(jedis.zlexcount(args.get(0), args.get(1), args.get(2)));

			}
		});

		commands.put("ZMPOP", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 3) {
					throw new Exception("Invalid number of arguments passed for ZMPOP, expected >= 3, got " + argCount);
				}

				int numKeys = SH.parseInt(args.get(0));
				args.remove(0);

				if (args.size() < numKeys)
					throw new Exception("Invalid number of keys passed in, expecting: " + numKeys + ", got: " + args.size());

				SortedSetOption option = null;
				String sortedOption = SH.toUpperCase(args.get(numKeys));
				if (SH.equals(sortedOption, "MIN"))
					option = SortedSetOption.MIN;
				else if (SH.equals(sortedOption, "MAX"))
					option = SortedSetOption.MAX;
				else
					throw new Exception("Invalid Sorted Set Option, received: " + sortedOption + ", expecting: MIN, or MAX");
				args.remove(numKeys);

				Integer count = null;
				if (args.size() > numKeys) {
					if (!SH.equals(SH.toUpperCase(args.get(args.size() - 2)), "COUNT"))
						throw new Exception("Failed to parse LIMIT parameter for ZMPOP, received: " + args.get(args.size() - 2) + ", expecting: COUNT");
					count = SH.parseInt(args.get(args.size() - 1));
					args.remove(args.size() - 1);
					args.remove(args.size() - 1);
				}

				KeyValue<String, List<Tuple>> result;
				if (count == null) {
					result = jedis.zmpop(option, args.toArray(new String[0]));
				} else {
					result = jedis.zmpop(option, count, args.toArray(new String[0]));
				}

				if (result == null) {
					resultSink.setReturnType(Object.class);
					resultSink.setReturnValue(null);
				} else {
					resultSink.setReturnType(List.class);
					ArrayList<Object> results = new ArrayList<Object>();

					for (final Tuple t : result.getValue()) {
						ArrayList<String> innerResult = new ArrayList<String>();
						innerResult.add(t.getElement());
						innerResult.add(SH.toString(t.getScore()));
						results.add(innerResult);
					}

					resultSink.setReturnValue(results);
				}

			}
		});

		commands.put("ZMSCORE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for ZMSCORE, expected >= 2, got " + argCount);
				}

				String key = args.get(0);
				args.remove(0);

				resultSink.setReturnType(List.class);
				resultSink.setReturnValue(jedis.zmscore(key, args.toArray(new String[0])));
			}
		});

		commands.put("ZMSCORE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for ZMSCORE, expected >= 2, got " + argCount);
				}

				String key = args.get(0);
				args.remove(0);

				resultSink.setReturnType(List.class);
				resultSink.setReturnValue(jedis.zmscore(key, args.toArray(new String[0])));
			}
		});

		commands.put("ZPOPMAX", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (!(argCount == 1 || argCount == 2)) {
					throw new Exception("Invalid number of arguments passed for ZPOPMAX, expected 1, or 2, got: " + argCount);
				}

				if (argCount == 1) {
					resultSink.setReturnType(List.class);
					Tuple result = jedis.zpopmax(args.get(0));
					ArrayList<String> listResult = new ArrayList<String>();
					listResult.add(result.getElement());
					listResult.add(SH.toString(result.getScore()));
					resultSink.setReturnValue(listResult);
				} else {
					resultSink.setReturnType(List.class);
					List<Tuple> result = jedis.zpopmax(args.get(0), SH.parseInt(args.get(1)));
					ArrayList<String> listResult = new ArrayList<String>();
					for (final Tuple t : result) {
						listResult.add(t.getElement());
						listResult.add(SH.toString(t.getScore()));
					}
					resultSink.setReturnValue(listResult);

				}
			}
		});

		commands.put("ZPOPMIN", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (!(argCount == 1 || argCount == 2)) {
					throw new Exception("Invalid number of arguments passed for ZPOPMIN, expected 1, or 2, got: " + argCount);
				}

				if (argCount == 1) {
					resultSink.setReturnType(List.class);
					Tuple result = jedis.zpopmin(args.get(0));
					ArrayList<String> listResult = new ArrayList<String>();
					listResult.add(result.getElement());
					listResult.add(SH.toString(result.getScore()));
					resultSink.setReturnValue(listResult);
				} else {
					resultSink.setReturnType(List.class);
					List<Tuple> result = jedis.zpopmin(args.get(0), SH.parseInt(args.get(1)));
					ArrayList<String> listResult = new ArrayList<String>();
					for (final Tuple t : result) {
						listResult.add(t.getElement());
						listResult.add(SH.toString(t.getScore()));
					}
					resultSink.setReturnValue(listResult);

				}
			}
		});

		commands.put("ZRANDMEMBER", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (!(argCount == 1 || argCount == 2 || argCount == 3)) {
					throw new Exception("Invalid number of arguments passed for ZRANDMEMBER, expected 1, 2, or 3, got: " + argCount);
				}

				if (argCount == 1) {
					resultSink.setReturnType(String.class);
					resultSink.setReturnValue(jedis.zrandmember(args.get(0)));
				} else {
					Boolean withScores = false;

					if (argCount == 3) {
						if (SH.equals(SH.toUpperCase(args.get(argCount - 1)), "WITHSCORES")) {
							withScores = true;
						} else {
							throw new Exception("Failed to parse argument for ZRANDMEMBER, received: " + args.get(argCount - 1) + ", expected: WITHSCORES");
						}
					}

					if (withScores) {
						resultSink.setReturnType(List.class);
						List<Tuple> result = jedis.zrandmemberWithScores(args.get(0), SH.parseInt(args.get(1)));
						ArrayList<String> listResult = new ArrayList<String>();
						for (final Tuple t : result) {
							listResult.add(t.getElement());
							listResult.add(SH.toString(t.getScore()));
						}
						resultSink.setReturnValue(listResult);
					} else {
						resultSink.setReturnType(List.class);
						resultSink.setReturnValue(jedis.zrandmember(args.get(0), SH.parseInt(args.get(1))));
					}

				}
			}
		});

		commands.put("ZRANGE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 3) {
					throw new Exception("Invalid number of arguments passed for ZRANGE, expected >= 3, got " + argCount);
				}

				ZRangeParams params = ZRangeParams.zrangeParams(SH.parseInt(args.get(1)), SH.parseInt(args.get(2)));
				Boolean withScores = false;

				for (int i = 3; i < args.size(); ++i) {
					String paramStr = SH.toUpperCase(args.get(i));
					if (SH.equals(paramStr, "BYSCORE")) {
						params = ZRangeParams.zrangeByScoreParams(SH.parseDouble(args.get(1)), SH.parseDouble(args.get(2)));
					} else if (SH.equals(paramStr, "BYLEX")) {
						params = ZRangeParams.zrangeByLexParams(args.get(1), args.get(2));
					} else if (SH.equals(paramStr, "REV")) {
						params.rev();
					} else if (SH.equals(paramStr, "LIMIT")) {
						if (i + 2 >= args.size())
							throw new Exception("Invalid number of arguments passed for LIMIT, expected: offset count");
						params.limit(SH.parseInt(args.get(i + 1)), SH.parseInt(args.get(i + 2)));
						i += 2;
					} else if (SH.equals(paramStr, "WITHSCORES")) {
						withScores = true;
					} else {
						throw new Exception("Failed to parse param type for ZRANGE, received: " + paramStr
								+ ", expecting: ZRANGE key min max [ BYSCORE | BYLEX] [REV] [LIMIT offset count] [WITHSCORES]");
					}
				}

				if (withScores) {
					resultSink.setReturnType(List.class);
					List<Tuple> result = jedis.zrangeWithScores(args.get(0), params);
					ArrayList<String> listResult = new ArrayList<String>();
					for (final Tuple t : result) {
						listResult.add(t.getElement());
						listResult.add(SH.toString(t.getScore()));
					}
					resultSink.setReturnValue(listResult);
				} else {
					resultSink.setReturnType(List.class);
					resultSink.setReturnValue(jedis.zrange(args.get(0), params));
				}

			}
		});

		commands.put("ZRANGESTORE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 4) {
					throw new Exception("Invalid number of arguments passed for ZRANGESTORE, expected >= 4, got " + argCount);
				}

				ZRangeParams params = ZRangeParams.zrangeParams(SH.parseInt(args.get(2)), SH.parseInt(args.get(3)));

				for (int i = 4; i < args.size(); ++i) {
					String paramStr = SH.toUpperCase(args.get(i));
					if (SH.equals(paramStr, "BYSCORE")) {
						params = ZRangeParams.zrangeByScoreParams(SH.parseDouble(args.get(2)), SH.parseDouble(args.get(3)));
					} else if (SH.equals(paramStr, "BYLEX")) {
						params = ZRangeParams.zrangeByLexParams(args.get(2), args.get(3));
					} else if (SH.equals(paramStr, "REV")) {
						params.rev();
					} else if (SH.equals(paramStr, "LIMIT")) {
						if (i + 2 >= args.size())
							throw new Exception("Invalid number of arguments passed for LIMIT, expected: offset count");
						params.limit(SH.parseInt(args.get(i + 1)), SH.parseInt(args.get(i + 2)));
						i += 2;
					} else {
						throw new Exception("Failed to parse param type for ZRANGESTORE, received: " + paramStr
								+ ", expecting: ZRANGE dst key min max [ BYSCORE | BYLEX] [REV] [LIMIT offset count]");
					}
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.zrangestore(args.get(0), args.get(1), params));

			}
		});

		commands.put("ZRANK", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 2) {
					throw new Exception("Invalid number of arguments passed for ZRANK, expected 2, got " + args.size());
				}
				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.zrank(args.get(0), args.get(1)));

			}
		});

		commands.put("ZREM", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() < 1) {
					throw new Exception("Invalid number of arguments passed for ZREM, expected >=1, got " + args.size());
				}
				String key = args.get(0);
				args.remove(0);
				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.zrem(key, args.toArray(new String[0])));
			}
		});

		commands.put("ZREMRANGEBYLEX", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 3) {
					throw new Exception("Invalid number of arguments passed for ZREMRANGEBYLEX, expected 3, got " + args.size());
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.zremrangeByLex(args.get(0), args.get(1), args.get(2)));

			}
		});

		commands.put("ZREMRANGEBYRANK", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 3) {
					throw new Exception("Invalid number of arguments passed for ZREMRANGEBYRANK, expected 3, got " + args.size());
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.zremrangeByRank(args.get(0), SH.parseLong(args.get(1)), SH.parseLong(args.get(2))));

			}
		});

		commands.put("ZREMRANGEBYSCORE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 3) {
					throw new Exception("Invalid number of arguments passed for ZREMRANGEBYRANK, expected 3, got " + args.size());
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.zremrangeByScore(args.get(0), args.get(1), args.get(2)));

			}
		});

		commands.put("ZREVRANK", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 2) {
					throw new Exception("Invalid number of arguments passed for ZREVRANK, expected 2, got " + args.size());
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.zrevrank(args.get(0), args.get(1)));

			}
		});

		commands.put("ZSCAN", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for ZSCAN, expected >= 2, got: " + argCount);
				}

				ScanParams param = new ScanParams();
				for (int i = 2; i < argCount; i += 2) {
					String paramStr = SH.toUpperCase(args.get(i));
					if (SH.equals(paramStr, "MATCH"))
						param.match(args.get(i + 1));
					else if (SH.equals(paramStr, "COUNT"))
						param.count(SH.parseInt(args.get(i + 1)));
					else
						throw new Exception("Invalid HSCAN param passed, received: " + paramStr + ", expecting: MATCH pattern OR COUNT count");
				}
				resultSink.setReturnType(List.class);
				ScanResult<Tuple> results = jedis.zscan(args.get(0), args.get(1), param);
				List<String> resultList = new ArrayList<String>();
				resultList.add(results.getCursor());
				for (final Tuple t : results.getResult()) {
					resultList.add(t.getElement());
					resultList.add(SH.toString(t.getScore()));
				}
				resultSink.setReturnValue(resultList);
			}
		});

		commands.put("ZSCORE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 2) {
					throw new Exception("Invalid number of arguments passed for ZSCORE, expected 2, got " + args.size());
				}

				resultSink.setReturnType(Double.class);
				resultSink.setReturnValue(jedis.zscore(args.get(0), args.get(1)));

			}
		});

		commands.put("ZUNION", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 2) {
					throw new Exception("Invalid number of arguments passed for ZUNION, expected >= 2, got " + argCount);
				}

				int numKeys = SH.parseInt(args.get(0));
				args.remove(0);

				Boolean withScores = false;
				ZParams params = new ZParams();
				for (int i = numKeys; i < args.size(); ++i) {
					String paramStr = SH.toUpperCase(args.get(i));
					if (SH.equals(paramStr, "WEIGHTS")) {
						List<String> weightsStr = args.subList(i + 1, i + 1 + numKeys);
						double[] result = new double[numKeys];
						for (int j = 0; j < weightsStr.size(); ++j)
							result[j] = SH.parseDouble(weightsStr.get(j));

						params.weights(result);
						i += numKeys;
					} else if (SH.equals(paramStr, "AGGREGATE")) {
						String aggregateType = SH.toUpperCase(args.get(i + 1));
						if (SH.equals(aggregateType, "SUM"))
							params.aggregate(Aggregate.SUM);
						else if (SH.equals(aggregateType, "MIN"))
							params.aggregate(Aggregate.MIN);
						else if (SH.equals(aggregateType, "MAX"))
							params.aggregate(Aggregate.MAX);
						else
							throw new Exception("Failed to parse AGGREGATE type for ZUNION, received: " + aggregateType + ", expecting: SUM, MIN, or MAX");
						++i;
					} else if (SH.equals(paramStr, "WITHSCORES")) {
						withScores = true;
					} else {
						throw new Exception("Failed to parse param type for ZUNION, received: " + paramStr
								+ ", expecting: [WEIGHTS weight [weight ...]] [AGGREGATE SUM | MIN | MAX] [WITHSCORES]");
					}
				}

				if (withScores) {
					resultSink.setReturnType(List.class);
					Set<Tuple> result = jedis.zunionWithScores(params, args.subList(0, numKeys).toArray(new String[0]));
					ArrayList<String> listResult = new ArrayList<String>();
					for (final Tuple t : result) {
						listResult.add(t.getElement());
						listResult.add(SH.toString(t.getScore()));
					}
					resultSink.setReturnValue(listResult);
				} else {
					resultSink.setReturnType(List.class);
					resultSink.setReturnValue(new ArrayList<String>(jedis.zunion(params, args.subList(0, numKeys).toArray(new String[0]))));

				}
			}
		});

		commands.put("ZUNIONSTORE", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				final int argCount = args.size();
				if (argCount < 3) {
					throw new Exception("Invalid number of arguments passed for ZUNIONSTORE, expected >= 3, got " + argCount);
				}

				String destination = args.get(0);
				args.remove(0);

				int numKeys = SH.parseInt(args.get(0));
				args.remove(0);

				ZParams params = new ZParams();
				for (int i = numKeys; i < args.size(); ++i) {
					String paramStr = SH.toUpperCase(args.get(i));
					if (SH.equals(paramStr, "WEIGHTS")) {
						List<String> weightsStr = args.subList(i + 1, i + 1 + numKeys);
						double[] result = new double[numKeys];
						for (int j = 0; j < weightsStr.size(); ++j)
							result[j] = SH.parseDouble(weightsStr.get(j));

						params.weights(result);
						i += numKeys;
					} else if (SH.equals(paramStr, "AGGREGATE")) {
						String aggregateType = SH.toUpperCase(args.get(i + 1));
						if (SH.equals(aggregateType, "SUM"))
							params.aggregate(Aggregate.SUM);
						else if (SH.equals(aggregateType, "MIN"))
							params.aggregate(Aggregate.MIN);
						else if (SH.equals(aggregateType, "MAX"))
							params.aggregate(Aggregate.MAX);
						else
							throw new Exception("Failed to parse AGGREGATE type for ZUNIONSTORE, received: " + aggregateType + ", expecting: SUM, MIN, or MAX");
						++i;
					} else {
						throw new Exception("Failed to parse param type for ZUNIONSTORE, received: " + paramStr
								+ ", expecting: [WEIGHTS weight [weight ...]] [AGGREGATE SUM | MIN | MAX] [WITHSCORES]");
					}
				}

				resultSink.setReturnType(Long.class);
				resultSink.setReturnValue(jedis.zunionstore(destination, params, args.subList(0, numKeys).toArray(new String[0])));

			}
		});

		//Removing support for SELECT - jedis select function doesn't seem to do anything,
		//(https://github.com/redis/jedis/issues/1790) - if required, we can support by
		//recreating the pool
		/*
		commands.put("SELECT", new RedisCommand() {
			public void run(final Jedis jedis, final List<String> args, final AmiCenterQueryResult resultSink, final AmiDatasourceTracker debugSink, final TimeoutController tc)
					throws Exception {
				if (args.size() != 1) {
					throw new Exception("Invalid number of arguments passed for SELECT, expected 1, got " + args.size());
				}
		
				resultSink.setReturnType(String.class);				
				resultSink.setReturnValue(jedis.select(SH.parseInt(args.get(0))));
			}
		});
		*/

	}

}
