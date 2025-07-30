package com.f1.omsweb;

import java.io.IOException;
import java.util.HashMap;

import com.f1.base.ValuedListenable;
import com.f1.base.ValuedListener;
import com.f1.codegen.impl.BasicCodeCompiler;
import com.f1.codegen.impl.BasicCodeGenerator;
import com.f1.pofo.oms.Order;
import com.f1.utils.Duration;

public class SampleValued implements ValuedListener {

	public static void main(String a[]) throws IOException {

		//for(int i=0;i<10000)
		//if(true)
		//return;

		BasicCodeGenerator coder = new BasicCodeGenerator(new BasicCodeCompiler(".autocode"), true);
		Order order = coder.nw(Order.class);
		order.setAccount("account1");
		order.setLimitPx(45);

		String[] keys = order.askSchema().askParams();
		HashMap<String, Object> orderMap = new HashMap<String, Object>();
		for (String key : keys)
			orderMap.put(key, order.ask(key));

		StringBuilder sb;
		for (Duration d = new Duration(); d.count() < 10; d.stampStdout(1)) {
			for (int i = 0; i < 100000; i++) {
				//order.setAccount("asdfasdf");
				order.put("account", "asdfasdf");
				//sb = new StringBuilder();
				//sb.append(order.ask("account"));
			}
		}
		for (Duration d = new Duration(); d.count() < 10; d.stampStdout(1)) {
			for (int i = 0; i < 100000; i++) {

				orderMap.put("account", "asdfasdf");
				//sb = new StringBuilder();
				//sb.append(orderMap.get("account"));
			}
		}
		//order.addListener(new SampleValued());
		//order.setLimitPx(55);
	}
	@Override
	public void onValuedAdded(ValuedListenable target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onValuedRemoved(ValuedListenable target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onValued(ValuedListenable target, String name, byte pid, Object old, Object value) {
		System.out.println(name + " changed from " + old + " to " + value);
	}

	@Override
	public void onValuedBoolean(ValuedListenable target, String name, byte pid, boolean old, boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onValuedByte(ValuedListenable target, String name, byte pid, byte old, byte value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onValuedChar(ValuedListenable target, String name, byte pid, char old, char value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onValuedShort(ValuedListenable target, String name, byte pid, short old, short value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onValuedInt(ValuedListenable target, String name, byte pid, int old, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onValuedLong(ValuedListenable target, String name, byte pid, long old, long value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onValuedFloat(ValuedListenable target, String name, byte pid, float old, float value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onValuedDouble(ValuedListenable target, String name, byte pid, double old, double value) {
		System.out.println(name + " changed from " + old + " to " + value);

	}
}
