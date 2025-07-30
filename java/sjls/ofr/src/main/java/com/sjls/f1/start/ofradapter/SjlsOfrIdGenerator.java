package com.sjls.f1.start.ofradapter;

import java.util.Collection;

import com.f1.base.Clock;
import com.f1.utils.Formatter;
import com.f1.utils.SH;
import com.f1.utils.ids.IdGenerator;
import com.f1.utils.ids.NamespaceIdGenerator;

public class SjlsOfrIdGenerator implements IdGenerator<String>, NamespaceIdGenerator<String> {

	private Formatter formatter;
	private Clock clock;
	private String systemName;
	private NamespaceIdGenerator<Long> generator;

	public SjlsOfrIdGenerator(Formatter formatter, Clock clock, String systemName, NamespaceIdGenerator<Long> generator) {
		this.formatter = formatter;
		this.clock = clock;
		this.systemName = systemName;
		this.generator = generator;
	}
	@Override
	public String createNextId() {
		StringBuilder sb = new StringBuilder();
		formatter.format(clock.getNowDate(), sb);
		sb.append(".todays_ids");
		String namespace = sb.toString();
		sb.setLength(8);
		sb.append('-').append(systemName).append("-");
		SH.rightAlign('0', SH.toString((long) generator.createNextId(namespace)), 8, false, sb);
		return sb.toString();
	}
	@Override
	public void createNextIds(int count, Collection<? super String> sink) {
		while (count-- > 0)
			sink.add(createNextId());
	}
	@Override
	public IdGenerator<String> getIdGenerator(String nameSpace) {
		return this;
	}
	@Override
	public String createNextId(String nameSpace) {
		return createNextId();
	}

}
