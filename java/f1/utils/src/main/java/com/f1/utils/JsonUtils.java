package com.f1.utils;

import com.f1.utils.impl.StringCharReader;
import com.f1.utils.string.ExpressionParserException;

public class JsonUtils {
	private static final int[] SPACE_COLON = StringCharReader.toIntsAndEof(" :");
	private static final int[] CLOSINGS = StringCharReader.toIntsAndEof("}] :,");

	public static void main(String a[]) {
		try {
			System.out.println(extractFromJson("  {me:-123,you:'where'}  ", SH.split('.', "me"), true));
			System.out.println(extractFromJson("  {me:123,you:'where'}  ", SH.split('.', "us"), true));
			System.out.println(extractFromJson("  {me:123,you:'where'}  ", SH.split('.', ""), true));
			System.out.println(extractFromJson("  {\"nonce\":\"asdf\",\"LAT\":123,LON:'where'}  ", SH.split('.', "LAT"), true));
			System.out.println(extractFromJson("  {\"nonce\":\"asdf\",\'LAT\':123,LON:'where'}  ", SH.split('.', "LAT"), true));
			System.out.println(extractFromJson("  {\"nonce\":\"asdf\",  LAT  :123,LON:'where'}  ", SH.split('.', "LAT"), true));
			System.out.println(extractFromJson("\"me\\\"here\"", SH.split('.', ""), true));
			System.out.println(extractFromJson("  {me:123,you:'where'}  ", SH.split('.', "me"), true));
			System.out.println(extractFromJson("  {me:[4,5,6,72],you:'where'}  ", SH.split('.', "you"), true));
			System.out.println(extractFromJson("  {me:[4,5,6,{when:34,where:{this:\"that\"}}],you:'where'}  ", SH.split('.', "me.3.where"), true));
			System.out.println(extractFromJson("  {me:[4,5,6,{when:34,where:{this:\"that\"}}],you:'where'}  ", SH.split('.', "you"), true));
			System.out.println(extractFromJson("  {me:[4,5,6,{when:34,where:{this:\"that\"}}],you:null}  ", SH.split('.', "me.2"), true));
			System.out.println(extractFromJson("  {me:[4,5,6,{when:34,where:{this:\"that\"}}],you:true,me:false}  ", SH.split('.', "you"), true));
			System.out.println(extractFromJson("  {me:[4,5,6,{when:34,where:{this:\"that\"}}],you:true,us:false}  ", SH.split('.', "us"), true));
			System.out.println(extractFromJson("  {me:[4,5,6,{when:34,where:{this:\"that\"}}],you:true,us:false}  ", SH.split('.', "us"), true));
			System.out.println(extractFromJson("  {me:[4,5,6,{when:34,where:{this:\"that\"}}],you:true,us:false}  ", SH.split('.', "them"), true));
		} catch (Exception e) {
			System.out.println(SH.printStackTrace(e));
		}
	}

	/**
	 * The empty path ("") will return the json object itself
	 * 
	 * @param json
	 *            json to parse
	 * @param path
	 *            ex: a.b.3.c will grab the c field from the 3rd element of the b field of the a field of the base map.
	 * @param throwOnError
	 *            if false then null is returned on error.
	 * @return null or value as string. Not that maps and lists will maintain valid json, essentially leaving quotes escpaed.
	 */
	public static String extractFromJson(String json, String path[], boolean throwOnError) {
		StringBuilder sb;
		switch (extractFromJson(new StringCharReader(json).setToStringIncludesLocation(true), 0, path, 0, throwOnError, sb = new StringBuilder())) {
			case INVALID_JSON:
			case NOT_FOUND:
			case NULL_FOUND:
			case MAP_FOUND_INNER:
			case LIST_FOUND_INNER:
				return null;
			default:
				return sb.toString();
		}
	}

	public static final byte INVALID_JSON = 1;
	public static final byte NOT_FOUND = 2;
	public static final byte NULL_FOUND = 3;
	public static final byte STRING_FOUND = 4;
	public static final byte NUMBER_FOUND = 5;
	public static final byte BOOLEAN_FOUND = 6;
	public static final byte LIST_FOUND = 7;
	public static final byte MAP_FOUND = 8;

	public static final byte LIST_FOUND_INNER = 9;
	public static final byte MAP_FOUND_INNER = 10;

	public static boolean skipToken(StringCharReader json, boolean throwOnError) {
		return extractFromJson(json, -1, null, -1, throwOnError, null) != INVALID_JSON;
	}
	public static byte extractFromJson(StringCharReader json, int depth, String path[], int pp, boolean throwOnError, StringBuilder sink) {
		json.skip(StringCharReader.WHITE_SPACE);
		if (json.isEof()) {
			if (depth == 0)
				return NOT_FOUND;
			else {
				return invalid(json, throwOnError, "Unexpected EOF");
			}
		} else if (path != null && pp == path.length) {
			int start = json.getCountRead();
			char t = json.peak();
			switch (t) {
				case '"':
				case '\'':
					json.expect(t);
					if (json.readUntilSkipEscaped(t, '\\', sink) == CharReader.EOF)
						return INVALID_JSON;
					json.expect(t);
					return STRING_FOUND;
				default:
					byte result = extractFromJson(json, -1, null, -1, throwOnError, null);
					if (result == INVALID_JSON)
						return INVALID_JSON;
					int end = json.getCountRead();
					sink.append(json.getInner(start, end));
					if (result == MAP_FOUND_INNER)
						return MAP_FOUND;
					if (result == LIST_FOUND_INNER)
						return LIST_FOUND;
					return result;
			}
		} else {
			String p = path == null ? null : path[pp];
			switch (json.readChar()) {
				case '{':
					for (;;) {
						json.skip(StringCharReader.WHITE_SPACE);
						final int start;
						final int end;
						switch (json.peak()) {
							case '\'':
								json.expect('\'');
								start = json.getCountRead();
								json.readUntilSkipEscaped('\'', '\\', null);
								end = json.getCountRead();
								if (!json.expectNoThrow('\''))
									return invalid(json, throwOnError, "Expecting closing quote (') after key");
								break;
							case '\"':
								json.expect('\"');
								start = json.getCountRead();
								json.readUntilSkipEscaped('\"', '\\', null);
								end = json.getCountRead();
								if (!json.expectNoThrow('\"'))
									return invalid(json, throwOnError, "Expecting closing quote (\") after key");
								break;
							default:
								start = json.getCountRead();
								json.readUntilAny(SPACE_COLON, null);
								end = json.getCountRead();
						}
						json.skip(StringCharReader.WHITE_SPACE);
						if (!json.expectNoThrow(':'))
							return invalid(json, throwOnError, "Expecting colon (:) after key");
						json.skip(StringCharReader.WHITE_SPACE);
						if (p != null && SH.equals(p, 0, p.length(), json, start, end)) {
							return extractFromJson(json, depth + 1, path, pp + 1, throwOnError, sink);
						}
						if (!skipToken(json, throwOnError))
							return INVALID_JSON;
						json.skip(StringCharReader.WHITE_SPACE);
						switch (json.readChar()) {
							case ',':
								continue;
							case '}':
								return MAP_FOUND_INNER;
							default:
								return invalid(json, throwOnError, "Expecting , or } after map token");
						}
					}
				case '[':
					int position;
					if (p != null) {
						if (!SH.areBetween(p, '0', '9'))
							return MAP_FOUND_INNER;
						position = SH.parseInt(p);
					} else
						position = -1;
					for (int i = 0;; i++) {
						if (i == position)
							return extractFromJson(json, depth + 1, path, pp + 1, throwOnError, sink);
						if (!skipToken(json, throwOnError))
							return INVALID_JSON;
						json.skip(StringCharReader.WHITE_SPACE);
						switch (json.readChar()) {
							case ',':
								json.skip(StringCharReader.WHITE_SPACE);
								continue;
							case ']':
								return LIST_FOUND_INNER;
							default:
								return invalid(json, throwOnError, "Expecting , or ] after list token");
						}
					}
				case 't':
					if (!json.expectSequenceNoThrow("rue"))
						return invalid(json, throwOnError, "Invalid constant");
					if (path != null && pp != path.length)
						return NULL_FOUND;
					return BOOLEAN_FOUND;
				case 'f':
					if (!json.expectSequenceNoThrow("alse"))
						return invalid(json, throwOnError, "Invalid constant");
					if (path != null && pp != path.length)
						return NULL_FOUND;
					return BOOLEAN_FOUND;
				case 'n':
					if (!json.expectSequenceNoThrow("ull"))
						return invalid(json, throwOnError, "Invalid constant");
					if (path != null && pp != path.length)
						return NULL_FOUND;
					return NULL_FOUND;
				case '\'':
					if (json.readUntilSkipEscaped('\'', '\\', sink) == CharReader.EOF)
						return invalid(json, throwOnError, "Missing closing quote (')");
					if (path != null && pp != path.length)
						return NULL_FOUND;
					json.expect('\'');
					return STRING_FOUND;
				case '"':
					if (json.readUntilSkipEscaped('\"', '\\', sink) == CharReader.EOF)
						return invalid(json, throwOnError, "Missing closing quote (\")");
					if (path != null && pp != path.length)
						return NULL_FOUND;
					json.expect('\"');
					return STRING_FOUND;
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
				case '.':
					if (path != null && pp != path.length)
						return NULL_FOUND;
					json.readUntilAny(CLOSINGS, null);
					return NUMBER_FOUND;
				default:
					return invalid(json, throwOnError, "Expecting map or list or constant");
			}
		}
	}
	private static byte invalid(StringCharReader json, boolean throwOnError, String msg) {
		if (throwOnError)
			throw new ExpressionParserException(json.getAsText(), json.getCountRead(), msg);
		return INVALID_JSON;
	}

}
