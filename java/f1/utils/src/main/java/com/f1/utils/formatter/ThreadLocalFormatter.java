package com.f1.utils.formatter;

import java.io.IOException;
import java.io.Writer;

import com.f1.utils.Formatter;

public class ThreadLocalFormatter<T extends Formatter> extends ThreadLocal<T> implements Formatter {

	private T formatterFactory;

	@Override
	protected T initialValue() {
		return (T) formatterFactory.clone();
	}

	public ThreadLocalFormatter(T formatter) {
		this.formatterFactory = formatter;
	}

	@Override
	public void format(Object value, StringBuilder sb) {
		this.get().format(value, sb);
	}

	@Override
	public String format(Object value) {
		return this.get().format(value);
	}

	@Override
	public void format(Object value, Writer out) throws IOException {
		this.get().format(value, out);
	}

	@Override
	public Formatter clone() {
		throw new IllegalStateException("you should not be cloning the thread local formatter");
	}

	@Override
	public String get(Object key) {
		return format(key);
	}

	@Override
	public boolean canFormat(Object obj) {
		return get().canFormat(obj);
	}

	@Override
	public boolean canParse(String text) {
		return get().canParse(text);
	}

	@Override
	public Object parse(String text) {
		return get().parse(text);
	}

	@Override
	public String getPattern() {
		return this.formatterFactory.getPattern();
	}
}
