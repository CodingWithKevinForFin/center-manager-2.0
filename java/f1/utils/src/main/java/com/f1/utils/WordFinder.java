package com.f1.utils;

import com.f1.utils.structs.CharKeyMap;

public class WordFinder {

	private CharKeyMap root = new CharKeyMap();

	public void addWord(String word) {
		CharKeyMap map = root;
		CharKeyMap map2 = null;
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			map2 = (CharKeyMap) map.get(c);
			if (map2 == null) {
				map.put(word.charAt(i), map2 = new CharKeyMap());
			} else if (map2.size() == 0)
				return;
			map = map2;

		}
		map2.clear();
	}

	public Entry findWord(String text) {
		return findWord(text, 0, text.length());
	}

	public Entry findWord(String text, int start, int end) {
		for (int i = start; i < end; i++) {
			CharKeyMap map = root;
			for (int j = i; j < end; j++) {
				map = (CharKeyMap) map.get(text.charAt(j));
				if (map == null)
					break;
				if (map.size() == 0)
					return new Entry(i, text.substring(i, j + 1));
			}
		}
		return null;
	}

	private Entry findChar(CharKeyMap map, String text, int start) {
		return null;
	}

	public static class Entry {
		private final int location;
		private final String word;

		public Entry(int location, String word) {
			this.word = word;
			this.location = location;
		}

		public String getWord() {
			return word;
		}

		public int getLocation() {
			return location;
		}

	}
}

