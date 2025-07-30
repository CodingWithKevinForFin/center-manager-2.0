package com.f1.utils;

import java.security.SecureRandom;

public class RsaEncryptHelper {

	public static class TextBasedSecureRandom extends SecureRandom {

		final private String text;

		private int read = 0;
		public TextBasedSecureRandom(String text) {
			this.text = text;
		}

		synchronized public void nextBytes(byte[] bytes) {

			if (bytes.length == 0)
				return;
			int bit = 0;
			for (;;) {
				char c = text.charAt(read++ % text.length());
				c += read / text.length();
				int j;
				if (OH.isBetween(c, 'a', 'z'))
					j = c - 'a';
				else if (OH.isBetween(c, 'A', 'Z'))
					j = c - 'A' + 26;
				else if (OH.isBetween(c, '0', '9'))
					j = c - '0' + 52;
				else if (c == ' ')
					j = 62;
				else
					j = 63;
				for (int k = 1; k < 64; k *= 2) {
					if ((j & k) != 0)
						bytes[bit / 8] ^= 1 << bit % 8;
					if (++bit == bytes.length * 8) {
						return;
					}
				}
			}
		}
	}

}
