package com.f1.utils.string;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import com.f1.base.CalcFrame;
import com.f1.utils.CH;
import com.f1.utils.structs.table.derived.AbstractDerivedCellMemberMethod;
import com.f1.utils.structs.table.derived.BasicDerivedCellParser;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;
import com.f1.utils.structs.table.stack.MutableCalcFrame;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class ExpressionParserTests {

	@Test

	public void t1() {

		assertEquals(0x25, eval("0x25"));

		assertEquals(0x25L, eval("0x25L"));

		assertEquals(5E2D, eval("5E2D"));

		assertEquals(5E+2, eval("5E+2D"));

		assertEquals(5E2, eval("5E2D"));

		assertEquals(5E-2, eval("5E-2D"));

		assertEquals(5.5E2, eval("5.5E2D"));

		assertEquals(5.5E+2, eval("5.5E+2D"));

		assertEquals(5.5E2, eval("5.5E2D"));

		assertEquals(5.5E-2, eval("5.5E-2D"));

		assertEquals(5E2, eval("5E2D"));

		assertEquals(5E+2 + 3, eval("5E+2D+3"));

		assertEquals(5E2, eval("5E2"));

		assertEquals(5E-2, eval("5E-2"));

		assertEquals(5.5E2, eval("5.5E2"));

		assertEquals(5.5E+2, eval("5.5E+2"));

		assertEquals(5.5E2, eval("5.5E2"));

		assertEquals(5.5E-2, eval("5.5E-2"));

		assertEquals(5E2, eval("5E2"));

		assertEquals(5E+2, eval("5E+2"));

		assertEquals(5E2, eval("5E2"));

		assertEquals(5E-2, eval("5E-2"));

		assertEquals(5.5E2, eval("5.5E2"));

		assertEquals(5.5E+2, eval("5.5E+2"));

		assertEquals(5.5E2, eval("5.5E2"));

		assertEquals(5.5E-2, eval("5.5E-2"));

		assertEquals(5E2, eval("5E2"));

		assertEquals(5E+2 + 3, eval("5E+2+3"));

		assertEquals(5E2, eval("5E2"));

		assertEquals(5E-2, eval("5E-2"));

		assertEquals(5.5E2, eval("5.5E2"));

		assertEquals(5.5E+2, eval("5.5E+2"));

		assertEquals(5.5E2, eval("5.5E2"));

		assertEquals(5.5E-2, eval("5.5E-2"));

		assertEquals(+5, eval("+5;"));

		assertEquals(5, eval("5;"));

		assertEquals(-5, eval("-5;"));

		assertEquals(7, eval("2+5;"));

		assertEquals(69, eval("(2*32)+5;"));

		assertEquals(37, eval("32+5;"));

		assertEquals(37, eval("32+ 5;"));

		assertEquals(37, eval("32 +5;"));

		assertEquals(37, eval("32 + 5;"));

		assertEquals(true, eval("1+5*2 == 11 && true;"));

		assertEquals(6, eval("1+5;"));

		assertEquals(11, eval("1+5*2;"));

		assertEquals(4, eval("1 + 5 - 2;"));

		assertEquals(false, eval("1+5*2 <  8;"));

		assertEquals(1, eval("1;"));

		assertEquals(64, eval("2*32;"));

		assertEquals(64, eval("(2*32);"));

		assertEquals(64, eval("((2*32));"));

		assertEquals(64, eval("(((2)*(32)));"));

		assertEquals(69, eval("(2*32) + 5;"));

		assertEquals(69, eval("(2*32)+ 5;"));

		assertEquals(69, eval("(2*32)+5;"));

		assertEquals(69, eval("(((2)*(32)))+5;"));

		assertEquals(207, eval("3*((((2)*(32)))+5);"));

		assertEquals(207.0, eval("3*((((2)*(32)))+5.0);"));

		assertEquals(-207.0, eval("-3*((((2)*(32)))+5.0);"));

		assertEquals("asdf-207.0", eval("\"asdf\"+ -3*((((2)*(32)))+5.0);"));

		assertEquals(1 == 1, eval("1==1;"));

		assertEquals(1 == 1, eval("1==1;"));

		assertEquals(1 != 1, eval("1!=1;"));

		assertEquals(1 >= 1, eval("1>=1;"));

		assertEquals(1 <= 1, eval("1<=1;"));

		assertEquals(-1 == 1, eval("-1==1;"));

		assertEquals(-1 == 1, eval("-1==1;"));

		assertEquals(-1 != 1, eval("-1!=1;"));

		assertEquals(-1 >= 1, eval("-1>=1;"));

		assertEquals(-1 <= 1, eval("-1<=1;"));

		assertEquals(true ? 5 : 7, eval("true ? 5 : 7"));

		assertEquals(false ? 5 : 7, eval("false ? 5 : 7"));

	}

	@Test
	@Ignore("TODO: Review tests")

	public void t21() {

		MutableCalcFrame vars = DerivedHelper.toFrame(CH.m("a", 10, "b", 40.5d, "s", "hello", "e", new Integer[] { 1, 2, 3 }));

		assertEquals(2, eval("{int[] abc=e;a=abc[1];}", vars, "a"));

		assertEquals(11, eval("a=++a", vars, "a"));

		assertEquals(10, eval("a=a++", vars, "a"));

		assertEquals(11, eval("a=a+1", vars, "a"));

		assertEquals(15, eval("{int i=5;while(i>0){a=a+1;i=i-1;}}", vars, "a"));

		assertEquals(60, eval("{int i=5*10;while(i>0){a=a+1;i=i-1;}}", vars, "a"));

		assertEquals(78 + 410, eval("{int i=5*10;while(i>0){a=a+1;i=i-1;if(i>40){a=a+2;}else{ a=a+10;}}}", vars, "a"));

		assertEquals(415, eval("a+=(int)(b*10)", vars, "a"));

		assertEquals(410, eval("a+=(int)b*10", vars, "a"));

		assertEquals(20, eval("for(int i=0;i<10;i=i+1) a+=1;", vars, "a"));

		assertEquals(15, eval("for(int i=0;i<10;i=i+1){ if(i==5) break; a+=1;}", vars, "a"));

		assertEquals(19, eval("{int i=0;while(i++<10){ if(i==5) continue; a+=1;}}", vars, "a"));

		assertEquals(19, eval("{int i=0;while(++i<=10){ if(i==5) continue; a+=1;}}", vars, "a"));

		assertEquals(52, eval("do{a+=3;}while(a<50);", vars, "a"));

	}

	@Test
	@Ignore("TODO: Review tests")

	public void t3() {
		MutableCalcFrame vars = DerivedHelper.toFrame(CH.m("a", 10, "b", 40.5d, "s", "hello"));

		assertEquals(62, eval("{int foo(int q){return q + 15;};a=foo(47);}", vars, "a"));

		assertEquals(109, eval("{int bar(int c,int d){return c + d;};int foo(int c){return bar(c,c) + 15;};a=foo(47);}", vars, "a"));

	}

	private Object eval(String string) {

		BasicDerivedCellParser dcp = new BasicDerivedCellParser(new JavaExpressionParser());

		return dcp.toCalc(string, EmptyCalcFrameStack.INSTANCE).get(EmptyCalcFrameStack.INSTANCE);

	}

	private Object eval(String string, CalcFrame vars, String varName) {

		BasicDerivedCellParser dcp = new BasicDerivedCellParser(new JavaExpressionParser());

		//		com.f1.utils.BasicTypes types = new HasherMap(vars);
		//
		//		for (Map.Entry e : types.entrySet()) {
		//
		//			e.setValue(e.getValue().getClass());
		//
		//		}
		//
		BasicMethodFactory mFactory = new BasicMethodFactory();

		mFactory.addMemberMethod(new AbstractDerivedCellMemberMethod<String>(String.class, "length", Integer.class) {

			@Override

			public Object invokeMethod(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {

				return targetObject.length();

			}

			// @Override
			public boolean isReadOnly() {
				return false;
			}

			@Override
			public Object resumeMethod(CalcFrameStack sf, String target, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isPausable() {
				// TODO Auto-generated method stub
				return false;
			}

		});
		CalcFrameStack sf = EmptyCalcFrameStack.INSTANCE;

		dcp.toCalc(string, sf).get(new ReusableCalcFrameStack(EmptyCalcFrameStack.INSTANCE, vars));

		return vars.getValue(varName);

	}

}