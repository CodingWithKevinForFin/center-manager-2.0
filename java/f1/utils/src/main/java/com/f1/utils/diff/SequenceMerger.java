package com.f1.utils.diff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.f1.utils.AH;
import com.f1.utils.SH;
import com.f1.utils.diff.SequenceDiffer.Block;

public class SequenceMerger<T> {
	public static byte MATCH_BL = 1;
	public static byte MATCH_BR = 2;
	public static byte MATCH_LR = 4;

	private T[] orgArray;
	private T[] lftArray;
	private T[] rgtArray;
	private List<MergeBlock<T>> blocks;

	public SequenceMerger(T[] orgArray, T[] lftArray, T[] rgtArray) {
		this.orgArray = orgArray;
		this.lftArray = lftArray;
		this.rgtArray = rgtArray;
		final List<Block<T>> lBlocks = new SequenceDiffer<T>(orgArray, lftArray).getBlocks();
		List<Block<T>> rBlocks = new SequenceDiffer<T>(orgArray, rgtArray).getBlocks();
		final Iterator<Block<T>> lIterator = lBlocks.iterator();
		final Iterator<Block<T>> rIterator = rBlocks.iterator();
		this.blocks = new ArrayList<MergeBlock<T>>();
		Block<T> lBlock = lIterator.hasNext() ? lIterator.next() : null;
		Block<T> rBlock = rIterator.hasNext() ? rIterator.next() : null;
		int baseStart = 0;
		int lStart = 0;
		int rStart = 0;
		while (lBlock != null && rBlock != null) {
			final int lEnd = lBlock.getLeftEnd();
			final int rEnd = rBlock.getLeftEnd();
			int n = Math.min(lEnd, rEnd);
			int baseLength = n - baseStart;
			int rLength = getLength(baseStart, baseLength, rStart, rBlock);
			int lLength = getLength(baseStart, baseLength, lStart, lBlock);
			MergeBlock<T> mb = new MergeBlock<T>(this, baseStart, baseLength, lStart, lLength, rStart, rLength);
			blocks.add(mb);
			lStart = mb.getLeftEnd();
			rStart = mb.getRightEnd();
			if (n == lEnd)
				lBlock = lIterator.hasNext() ? lIterator.next() : null;
			if (n == rEnd)
				rBlock = rIterator.hasNext() ? rIterator.next() : null;
			baseStart = n;
		}
		if (lBlock != null) {
			MergeBlock<T> mb = new MergeBlock<T>(this, baseStart, 0, lStart, lBlock.getRightCount(), rStart, 0);
			blocks.add(mb);
		}
		if (rBlock != null) {
			MergeBlock<T> mb = new MergeBlock<T>(this, baseStart, 0, lStart, 0, rStart, rBlock.getRightCount());
			blocks.add(mb);
		}
		//		if (rBlock != null)
		//			throw new IllegalStateException();
	}
	public T[] getLeftValues() {
		return this.lftArray;
	}

	public T[] getRightValues() {
		return this.rgtArray;
	}

	public T[] getOrigValues() {
		return this.orgArray;
	}

	public List<MergeBlock<T>> getBlocks() {
		return this.blocks;
	}

	private static <T> int getLength(int baseStart, int baseLength, int rStart, Block<T> rBlock) {
		int rLength;
		if (rBlock.getLeftStart() == baseStart && rBlock.getLeftCount() == baseLength) {
			rLength = rBlock.getRightCount();
		} else if (rBlock.areSame()) {
			rLength = baseLength;
		} else {
			rLength = Math.max(0, rBlock.getRightCount() + rBlock.getLeftStart() - baseStart);
		}
		if (rLength + rStart > rBlock.getRightEnd())
			rLength = rBlock.getRightEnd() - rStart;
		return rLength;
	}

	public static class MergeBlock<T> {
		private final int baseStart;
		private final int baseLength;
		private final int lStart;
		private final int rStart;
		private final int rLength;
		private final int lLength;
		private final SequenceMerger<T> merger;

		public MergeBlock(SequenceMerger<T> merger, int baseStart, int baseLength, int lStart, int lLength, int rStart, int rLength) {
			this.merger = merger;
			this.baseStart = baseStart;
			this.baseLength = baseLength;
			this.lStart = lStart;
			this.lLength = lLength;
			this.rStart = rStart;
			this.rLength = rLength;
		}

		public int getLeftStart() {
			return lStart;
		}
		public int getRightStart() {
			return rStart;
		}
		public int getLeftLength() {
			return lLength;
		}
		public int getRightLength() {
			return rLength;
		}
		public int getLeftEnd() {
			return lStart + lLength;
		}
		public int getRightEnd() {
			return rStart + rLength;
		}

		public T[] getOrigValues() {
			return (T[]) AH.subarray(merger.getOrigValues(), baseStart, baseLength);
		}
		public T[] getRightValues() {
			return (T[]) AH.subarray(merger.getRightValues(), rStart, rLength);
		}
		public T[] getLeftValues() {
			return (T[]) AH.subarray(merger.getLeftValues(), lStart, lLength);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(baseStart);
			sb.append(" - ");
			sb.append(baseLength + baseStart);
			SH.repeat(' ', 15 - sb.length(), sb);
			sb.append(SH.join("", getOrigValues()));
			SH.repeat(' ', 30 - sb.length(), sb);
			sb.append("| ");
			sb.append(lStart);
			SH.repeat(' ', 45 - sb.length(), sb);
			sb.append(SH.join("", getLeftValues()));
			SH.repeat(' ', 60 - sb.length(), sb);
			sb.append("| ");
			sb.append(rStart);
			SH.repeat(' ', 75 - sb.length(), sb);
			sb.append(SH.join("", getRightValues()));
			return sb.toString();

		}
	}

	//	public static String[] load(String name) throws IOException {
	//		String readText = IOH.readText(new File("C:\\Users\\RobertCooke\\Downloads\\" + name));
	//		//		System.out.println(readText);
	//		String[] r = SH.splitLines(readText);
	//		System.out.println(SH.join("+", r));
	//		//		System.out.println("====");
	//		return r;
	//	}

	public static void main(String a[]) {
		//		String orig = "{\n\n\nString (){}\n\n}";
		//		String left = "{\nString (){}\n\n}";
		//		String right = "{\n}";
		//		System.out.println(orig);
		//		System.out.println("======");
		//		System.out.println(left);
		//		System.out.println("======");
		//		System.out.println(right);

		//		{
		//			//			Character[] o = AH.box("aaabbbccc".toCharArray());
		//			//			Character[] l = AH.box("aaabbb".toCharArray());
		//			//			Character[] r = AH.box("bbbccc".toCharArray());
		//			SequenceMerger<String> sm = new SequenceMerger<String>(load("base.txt"), load("left.txt"), load("right.txt"));
		//			List<MergeBlock<String>> b = sm.getBlocks();
		//			for (MergeBlock<String> i : b) {
		//				System.out.println(i);
		//				System.out.println(">>");
		//
		//				System.out.println("ORIG:");
		//				System.out.println(SH.join("", i.getOrigValues()));
		//				System.out.println("LEFT:");
		//				System.out.println(SH.join("", i.getLeftValues()));
		//				System.out.println("RIGHT:");
		//				System.out.println(SH.join("", i.getRightValues()));
		//				System.out.println("===============");
		//			}
		//			System.err.println();
		//		}
		//		{
		//			Character[] o = AH.box("abdg".toCharArray());
		//			Character[] l = AH.box("abg".toCharArray());
		//			Character[] r = AH.box("ag".toCharArray());
		//			SequenceMerger<Character> sm = new SequenceMerger<Character>(o, l, r);
		//			for (MergeBlock<Character> i : sm.blocks) {
		//				System.out.println(i);
		//				System.out.println(">>");
		//
		//				System.out.println("ORIG:");
		//				System.out.println(SH.join("", i.getOrigValues()));
		//				System.out.println("LEFT:");
		//				System.out.println(SH.join("", i.getLeftValues()));
		//				System.out.println("RIGHT:");
		//				System.out.println(SH.join("", i.getRightValues()));
		//				System.out.println("===============");
		//			}
		//			//			System.err.println();
		//		}
		//		{
		//			Character[] o = AH.box("what".toCharArray());
		//			Character[] l = AH.box("what".toCharArray());
		//			Character[] r = AH.box("what".toCharArray());
		//			SequenceMerger<Character> sm = new SequenceMerger<Character>(o, l, r);
		//			System.err.println();
		//		}
		//		{
		//			Character[] o = AH.box("abc222def".toCharArray());
		//			Character[] l = AH.box("abc111def".toCharArray());
		//			Character[] r = AH.box("abc2333ef".toCharArray());
		//			SequenceMerger<Character> sm = new SequenceMerger<Character>(o, l, r);
		//			System.err.println();
		//		}
		//		{
		//			Character[] o = AH.box("0123456789robgone".toCharArray());
		//			Character[] l = AH.box("0123abc456789davegone".toCharArray());
		//			Character[] r = AH.box("012345def6789stevegone".toCharArray());
		//			SequenceMerger<Character> sm = new SequenceMerger<Character>(o, l, r);
		//			System.err.println();
		//		}
	}

}
