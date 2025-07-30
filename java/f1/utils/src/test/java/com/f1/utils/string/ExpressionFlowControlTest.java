package com.f1.utils.string;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.f1.utils.CH;
import com.f1.utils.structs.table.derived.BasicDerivedCellParser;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;
import com.f1.utils.structs.table.stack.MutableCalcFrame;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class ExpressionFlowControlTest {

	@Test
	@Ignore("TODO:Using int instead of `integer` throws class not found for these tests, we refrained from updating BasicMethodFactory as that may cause other issues")
	public void tInt() {
		StringBuilder test = new StringBuilder();
		test.append("{").append('\n');
		test.append("  int j=1024;").append('\n');
		test.append("   return j;").append('\n');
		test.append("}").append('\n');
		System.out.println(test);

		BasicDerivedCellParser dcp = new BasicDerivedCellParser(new JavaExpressionParser());
		DerivedCellCalculator c = dcp.toCalc(test, EmptyCalcFrameStack.INSTANCE);
		System.out.println(c);//

		assertEquals(1024, c.get(null));
	}

	@Test
	@Ignore("Test failure after 19778.dev, moved to backend test")
	public void tFor() {
		StringBuilder test = new StringBuilder();
		test.append("{").append('\n');
		test.append("  integer j=1;").append('\n');
		test.append("  for(integer k=0;k<10;k++){").append('\n');
		test.append("      j=j*2;").append('\n');
		test.append("  }").append('\n');
		test.append("   return j;").append('\n');
		test.append("}").append('\n');
		System.out.println(test);

		BasicDerivedCellParser dcp = new BasicDerivedCellParser(new JavaExpressionParser());
		DerivedCellCalculator c = dcp.toCalc(test, EmptyCalcFrameStack.INSTANCE);
		System.out.println(c);//

		assertEquals(1024, c.get(null));
	}
	@Test
	@Ignore("Test failure after 19778.dev, moved to backend test")
	public void tForEach() {
		StringBuilder test = new StringBuilder();
		test.append("{").append('\n');
		test.append("  integer j=1;").append('\n');
		test.append("  for(integer k:vals){").append('\n');
		test.append("      j=j+k;").append('\n');
		test.append("  }").append('\n');
		test.append("   return j;").append('\n');
		test.append("}").append('\n');
		System.out.println(test);

		BasicDerivedCellParser dcp = new BasicDerivedCellParser(new JavaExpressionParser());
		DerivedCellCalculator c = dcp.toCalc(test, EmptyCalcFrameStack.INSTANCE);
		System.out.println(c);//

		MutableCalcFrame vars = new MutableCalcFrame();
		vars.putType("vars", List.class);
		vars.putValue("vals", CH.l(1, 2, 3, 4));
		assertEquals(11, c.get(new ReusableCalcFrameStack(EmptyCalcFrameStack.INSTANCE, vars)));
	}
	@Test
	@Ignore("Test failure after 19778.dev, moved to backend test")
	public void tWhile() {
		StringBuilder test = new StringBuilder();
		test.append("{").append('\n');
		test.append("  integer j=1;integer i=0;").append('\n');
		test.append("  while(i<10){").append('\n');
		test.append("      j=j+2;i++;").append('\n');
		test.append("  }").append('\n');
		test.append("   return j;").append('\n');
		test.append("}").append('\n');
		System.out.println(test);

		BasicDerivedCellParser dcp = new BasicDerivedCellParser(new JavaExpressionParser());
		DerivedCellCalculator c = dcp.toCalc(test, EmptyCalcFrameStack.INSTANCE);
		System.out.println(c);//

		assertEquals(21, c.get(null));
	}
	@Test
	@Ignore("Test failure after 19778.dev, moved to backend test")
	public void t1() {
		StringBuilder test = new StringBuilder();
		test.append("{").append('\n');
		test.append("  integer j=1;").append('\n');
		test.append("  for(integer k=0;k<10;k++){").append('\n');
		test.append("    for(integer i=0;i<4;i++){").append('\n');
		test.append("      if(i==2) continue;").append('\n');
		test.append("      j=j*2;").append('\n');
		test.append("    }").append('\n');
		test.append("    if(k==2) break;").append('\n');
		test.append("  }").append('\n');
		test.append("   return j;").append('\n');
		test.append("}").append('\n');
		System.out.println(test);

		BasicDerivedCellParser dcp = new BasicDerivedCellParser(new JavaExpressionParser());
		DerivedCellCalculator c = dcp.toCalc(test, EmptyCalcFrameStack.INSTANCE);
		System.out.println(c);//

		assertEquals(512, c.get(null));
	}

	/* Commenting out t2 because pause no longer exists */
	//	@Test
	//	public void t2() {
	//		StringBuilder test = new StringBuilder();
	//		test.append("{").append('\n');
	//		test.append("  int j=1;").append('\n');
	//		test.append("  for(int k=0;k<10;k++){").append('\n');
	//		test.append("    for(int i=0;i<4;i++){").append('\n');
	//		test.append("      if(i==2) continue;").append('\n');
	//		test.append("      pause;").append('\n');
	//		test.append("      j=j*2;").append('\n');
	//		test.append("    }").append('\n');
	//		test.append("    if(k==2) break;").append('\n');
	//		test.append("  }").append('\n');
	//		test.append("   return j;").append('\n');
	//		test.append("}").append('\n');
	//		System.out.println(test);
	//
	//		BasicDerivedCellParser dcp = new DeclarativeDerivedCellParser(new JavaExpressionParser());
	//		DerivedCellCalculator c = dcp.toCalc(test, EmptyMapping.INSTANCE, new BasicMethodFactory());
	//		System.out.println(c);//
	//
	//		Object t = c.get(null);
	//
	//		while (t instanceof FlowControlPause) {
	//			System.out.println(t);
	//			t = ((FlowControlPause) t).resume();
	//		}
	//		System.out.println(t);
	//	}
}
