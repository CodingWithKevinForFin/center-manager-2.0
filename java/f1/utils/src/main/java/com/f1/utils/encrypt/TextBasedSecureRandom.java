package com.f1.utils.encrypt;

import java.security.SecureRandom;

import com.f1.utils.OH;

public class TextBasedSecureRandom extends SecureRandom {

	final private String text;

	private int read = 0;
	public TextBasedSecureRandom(String text) {
		this.text = text;
	}

	synchronized public void nextBytes(byte[] bytes) {
		this.read = fill(text, this.read, bytes);
	}

	public static int fill(String text, int textOffset, byte[] bytes) {
		if (bytes.length == 0)
			return textOffset;
		int bit = 0;
		for (;;) {
			char c = text.charAt(textOffset++ % text.length());
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
				j = c;
			j = (j + (textOffset / text.length())) & 63;

			for (int k = 1; k < 64; k *= 2) {
				if ((j & k) != 0)
					bytes[bit / 8] ^= 1 << bit % 8;
				if (++bit == bytes.length * 8) {
					return textOffset;
				}
			}
		}
	}

	public static void main(String a[]) {
	}
}
