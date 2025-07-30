package com.f1.utils.converter.json2;

import com.f1.utils.CharReader;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;

public class StringToJsonConverter extends AbstractJsonConverter<String> {

	public static final StringToJsonConverter INSTANCE = new StringToJsonConverter();
	private static final int[] QUOTES = new int[] { '\'', '\"' };
	private static int[] WHITE_SPACE = StringCharReader.toIntsAndEof(" \n\r\t");

	public StringToJsonConverter() {
		super(String.class);
	}

	@Override
	public void objectToString(String o, ToJsonConverterSession session) {
		StringBuilder out = session.getStream();
		out.append(SH.CHAR_QUOTE);
		SH.toStringEncode(o, SH.CHAR_QUOTE, out);
		out.append(SH.CHAR_QUOTE);
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		CharReader stream = session.getStream();
		StringBuilder temp = session.getTempStringBuilder();

		// Revert to original code for now, since we should only support legal json and a key not quoted isn't legal json
		char c = stream.readChar();
		if (c != '"' && c != '\'')
			throw new RuntimeException("expecting single quote(') or double quote(\") at char " + (stream.getCountRead() - 1));
		for (;;) {
			char c2 = stream.readChar();
			if (c == c2)
				return SH.toStringAndClear(temp);
			if (c2 == '\\') {
				char c3 = stream.readChar();
				if (c3 == 'u') {
					int unicode = (readDigit(stream) << 12) + (readDigit(stream) << 8) + (readDigit(stream) << 4) + readDigit(stream);
					temp.append((char) unicode);
				} else
					temp.append(SH.toSpecialIfSpecial(c3));
			} else
				temp.append(c2);
		}
		//		char c = stream.peak();
		//		boolean startsWithQuote = c == '"' || c == '\'';
		//		//		if (c != '"' && c != '\'')
		//		//			throw new RuntimeException("expecting single quote(') or double quote(\") at char " + (stream.getCountRead() - 1));
		//		// If it starts with quote it needs to end with quote
		//		if (startsWithQuote) {
		//			c = stream.readChar();
		//			for (;;) {
		//				char c2 = stream.readChar();
		//				if (c == c2)
		//					return SH.toStringAndClear(temp);
		//				if (c2 == '\\') {
		//					char c3 = stream.readChar();
		//					if (c3 == 'u') {
		//						int unicode = (readDigit(stream) << 12) + (readDigit(stream) << 8) + (readDigit(stream) << 4) + readDigit(stream);
		//						temp.append((char) unicode);
		//					} else
		//						temp.append(SH.toSpecialIfSpecial(c3));
		//				} else
		//					temp.append(c2);
		//			}
		//		} else {
		//			// If it doesn't start with quote aka as in json { myKey: "value" } it needs to end with ":"
		//			// Trims whitespace at beginning and end
		//			// Reads the key but doesn't read the ':'
		//			stream.skipAny(WHITE_SPACE);
		//
		//			for (;;) {
		//				char c2 = stream.peak();
		//				if (c2 == ':')
		//					return SH.toStringAndClear(SH.trimInplace(temp));
		//
		//				stream.skipChars(1);
		//				if (c2 == '\\') {
		//					char c3 = stream.readChar();
		//					if (c3 == 'u') {
		//						int unicode = (readDigit(stream) << 12) + (readDigit(stream) << 8) + (readDigit(stream) << 4) + readDigit(stream);
		//						temp.append((char) unicode);
		//					} else
		//						temp.append(SH.toSpecialIfSpecial(c3));
		//					//				} else if (c2 == ' ' || c2 == '\t' || c2 == '\n') {
		//					//					
		//				} else
		//					temp.append(c2);
		//			}
		//
		//		}
	}

	private int readDigit(CharReader stream) {
		switch (stream.readChar()) {
			case '0':
				return 0x0;
			case '1':
				return 0x1;
			case '2':
				return 0x2;
			case '3':
				return 0x3;
			case '4':
				return 0x4;
			case '5':
				return 0x5;
			case '6':
				return 0x6;
			case '7':
				return 0x7;
			case '8':
				return 0x8;
			case '9':
				return 0x9;
			case 'a':
			case 'A':
				return 0xA;
			case 'b':
			case 'B':
				return 0xB;
			case 'c':
			case 'C':
				return 0xC;
			case 'd':
			case 'D':
				return 0xD;
			case 'e':
			case 'E':
				return 0xE;
			case 'f':
			case 'F':
				return 0xF;
			default:
				throw new RuntimeException("Invalid unicode encoding at char " + (stream.getCountRead() - 1));
		}
	}

}
