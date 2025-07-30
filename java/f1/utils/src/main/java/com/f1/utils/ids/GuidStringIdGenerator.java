package com.f1.utils.ids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.utils.GuidHelper;
import com.f1.utils.SH;

public class GuidStringIdGenerator implements IdGenerator<String> {
	private final String prefix;
	private int base;

	public GuidStringIdGenerator(String prefix, int base) {
		this.prefix = SH.length(prefix) == 0 ? null : prefix;
		this.base = base;
	}

	@Override
	public String createNextId() {
		return toTicket();
	}

	@Override
	public void createNextIds(int count, Collection<? super String> sink) {
		List<Number> ids = new ArrayList<Number>(count);
		if (prefix != null) {
			for (Number v : ids)
				sink.add(GuidHelper.getGuid(base));
		} else {
			for (Number v : ids)
				sink.add(GuidHelper.getGuid(base));
		}
	}

	public String toTicket() {
		if (prefix != null)
			return prefix + GuidHelper.getGuid(base);
		else
			return GuidHelper.getGuid(base);
	}

	public static class Factory implements com.f1.base.Factory<String, GuidStringIdGenerator> {
		private final int base;
		private final boolean useKeyAsPrefix;

		public Factory(int base, boolean useKeyAsPrefix) {
			this.base = base;
			this.useKeyAsPrefix = useKeyAsPrefix;
		}

		public Factory() {
			this(16, true);
		}
		@Override
		public GuidStringIdGenerator get(String key) {
			return new GuidStringIdGenerator(useKeyAsPrefix ? key : null, base);
		}

	}
}
