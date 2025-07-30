package com.f1.utils.diff;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.diff.SequenceDiffer.Block;

public class SequenceDifferTests {

	@Test
	public void test1() {
		assertBlocks("1,2,3,x,4,5,6,7", "1,2,3,y,4,5,6,7", "1,2,3|<x>y|4,5,6,7");
		assertBlocks("1,2,3,x,4,5,6,7", "1,2,3,y,z,4,5,6,7", "1,2,3|<x>y,>z|4,5,6,7");
		assertBlocks("1,2,3,w,x,4,5,6,7", "1,2,3,y,4,5,6,7", "1,2,3|<w>y,<x|4,5,6,7");
		assertBlocks("1,2,3,w,x,4,5,6,7", "1,2,3,y,z,4,5,6,7", "1,2,3|<w>y,<x>z|4,5,6,7");
		assertBlocks("1,2,3", "1,2,3", "1,2,3");
		assertBlocks("", "1,2,3", ">1,>2,>3");
		assertBlocks("1,2,3", "", "<1,<2,<3");
		assertBlocks("1,2,3", "1,2,3,4,5", "1,2,3|>4,>5");
		assertBlocks("0,1,2,3", "1,2,3,4,5", "<0|1,2,3|>4,>5");
		assertBlocks("0,1,2,3", "0,11,22,33,4,5", "0|<1>11,<2>22,<3>33,>4,>5");
		assertBlocks("0,1,2,3", "0,1,5,3", "0,1|<2>5|3");
		assertBlocks("a,0,1,2,3,4,5,6,7,8,9,10,11,12,b", "0,1,2,3,4,5,6,7,8,9,10,11,12", "<a|0,1,2,3,4,5,6,7,8,9,10,11,12|<b");
		assertBlocks("a,0,1,2,3,a,0,1,2,3,a", "0,1,2,3", "<a|0,1,2,3|<a,<0,<1,<2,<3,<a");
	}
	@Test
	@Ignore("For these tests the logic should be confirmed, and updated")
	public void test1a() {
		assertBlocks("a,0,1,a,0,1,2,3,4,5,6,a", "0,1,2,3,4,5,6", "<a,<0,<1|0,1,2,3,4,5,6|<a");
		assertBlocks("a,0,1,2,3,a,0,1,2,3,4,a", "0,1,2,3,4", "<a,<0,<1,<2,<3|0,1,2,3,4|<a");

	}

	private static String[] i(String s) {
		return AH.toArray(SH.splitToList(",", s), String.class);
	}

	//syntax for each node of expectedStr is delimited by pipe(|):
	// <leftValue
	// >rightValue
	// sameValue
	// <leftValue>rightValue
	public void assertBlocks(String l, String r, String expectedStr) {
		System.out.println(l + " vs " + r);
		SequenceDiffer<String> t = new SequenceDiffer<String>(i(l), i(r));
		//		t.dump();
		List<Block<String>> blocks = t.getBlocks();
		String[] expected = SH.split('|', expectedStr);
		try {
			OH.assertEq(expected.length, blocks.size());
			for (int i = 0; i < expected.length; i++) {
				Block<String> block = blocks.get(i);
				String[] eblock = i(expected[i]);
				OH.assertEq(block.getMaxCount(), eblock.length);
				for (int j = 0; j < eblock.length; j++) {
					String s = eblock[j];
					if (s.startsWith("<")) {
						s = s.substring(1);
						String left = SH.beforeFirst(s, '>', s);
						String right = SH.afterFirst(s, '>', null);
						assertEquals(left, block.getLeftOrNull(j));
						assertEquals(right, block.getRightOrNull(j));
					} else if (s.startsWith(">")) {
						assertEquals(null, block.getLeftOrNull(j));
						assertEquals(s.substring(1), block.getRightOrNull(j));
					} else {
						assertEquals(s, block.getLeftOrNull(j));
						assertEquals(s, block.getRightOrNull(j));
					}
				}
			}
		} finally {
			StringBuilder sb = new StringBuilder();
			for (Block<String> block : blocks) {
				for (int i = 0; i < block.getMaxCount(); i++) {
					if (i > 0)
						sb.append(',');
					String lval = block.getLeftOrNull(i);
					String rval = block.getRightOrNull(i);
					if (lval == null)
						sb.append('>').append(rval);
					else if (rval == null)
						sb.append('<').append(lval);
					else if (OH.eq(lval, rval))
						sb.append(lval);
					else
						sb.append('<').append(lval).append('>').append(rval);
				}
				sb.append('|');
			}
			sb.setLength(sb.length() - 1);
			System.out.println("  actual:" + sb);
			System.out.println("expected:" + expectedStr);
			System.out.println();
		}
	}
}
