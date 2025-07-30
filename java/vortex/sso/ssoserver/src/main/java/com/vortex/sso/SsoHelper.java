package com.vortex.sso;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.f1.utils.FastByteArrayInputStream;
import com.f1.utils.IOH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.sso.messages.SsoUser;

public class SsoHelper {

	public static void encode(SsoUser user, byte type) {
		if (user.getPassword() != null)
			user.setPassword(encode(user.getPassword(), user.getEncodingAlgorithm(), type));
		if (user.getResetAnswer() != null)
			user.setResetAnswer(encode(user.getResetAnswer(), user.getEncodingAlgorithm(), type));
		user.setEncodingAlgorithm(type);
	}

	public static String encode(String text, byte from, byte to) {
		if (from == to)
			return text;
		else if (from != SsoUser.ENCODING_PLAIN)
			throw new IllegalArgumentException("can only encode plain text. Supplied encoding type: " + from);
		switch (to) {
			case SsoUser.ENCODING_CHECKSUM64:
				try {
					return text == null ? null : SH.toString(IOH.checkSumBsdLong(new FastByteArrayInputStream(text.getBytes())));
				} catch (IOException e) {
					throw OH.toRuntime(e);
				}
			case SsoUser.ENCODING_PLAIN:
				return text;
			default:
				throw new RuntimeException("unknown type: " + to);
		}
	}

	public static String generatePassword(Random random) {
		StringBuilder sb = new StringBuilder();
		sb.append((char) MH.rand(random, 'a', 'z' + 1));
		sb.append((char) MH.rand(random, 'a', 'z' + 1));
		sb.append((char) MH.rand(random, 'a', 'z' + 1));
		sb.append((char) MH.rand(random, 'A', 'Z' + 1));
		sb.append((char) MH.rand(random, 'A', 'Z' + 1));
		sb.append((char) MH.rand(random, 'A', 'Z' + 1));
		sb.append((char) MH.rand(random, '0', '9' + 1));
		sb.append((char) MH.rand(random, '0', '9' + 1));
		sb.append((char) MH.rand(random, '0', '9' + 1));
		sb.append("_");
		List<Character> chars = SH.toCharList(sb);
		Collections.shuffle(chars, random);
		return SH.fromCharList(chars);
	}

	public static void main(String a[]) {
		Random r = new Random();
		System.out.println(generatePassword(r));
		System.out.println(generatePassword(r));
		System.out.println(generatePassword(r));
		System.out.println(generatePassword(r));
	}
}
