package com.f1.ami.amicommon.centerclient;

import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.structs.RangeArray;

public class AmiCenterClientObjectPool {
	private static final Logger log = LH.get();

	private RangeArray<ByKey> poolsByKey = new RangeArray<ByKey>();
	private String type;

	public AmiCenterClientObjectPool(String type) {
		this.type = type;
	}

	public Object poolObject(short key, Object value) {
		if (value == null)
			return null;
		ByKey byKey = poolsByKey.get(key);
		if (byKey == null)
			poolsByKey.put(key, byKey = new ByKey(key));
		return byKey.poolObject(value);
	}

	private class ByKey {
		private static final int MAX_POOL_SIZE = 10000;
		private static final int UNIQUE_VALUES_CUTOFF = 1000;
		private HasherSet<Object> values = new HasherSet<Object>();
		private int hit;
		private short key;

		public ByKey(short key) {
			this.key = key;
			LH.fine(log, "Object pooling started for: ", type, ".", key);
		}

		public Object poolObject(Object value) {
			if (values == null)
				return value;
			Object r = values.get(value);
			if (r != null) {
				this.hit++;
				if (hit == UNIQUE_VALUES_CUTOFF)
					LH.fine(log, "Object pooling confirmed for: ", type, ".", key, " (", hit, " cache hits with ", values.size(), " unique values)");
				return r;
			} else {
				if (hit < UNIQUE_VALUES_CUTOFF && values.size() > UNIQUE_VALUES_CUTOFF) {
					LH.fine(log, "Object pooling dropped for: ", type, ".", key, " (Pooled ", UNIQUE_VALUES_CUTOFF, " values and only ", hit, " cache hits)");
					values = null;
				} else if (values.size() < MAX_POOL_SIZE) {
					values.add(value);
					if (values.size() == MAX_POOL_SIZE)
						LH.fine(log, "Object pooling limit of ", MAX_POOL_SIZE, " reached for: ", type, ".", key, " (", hit, " cache hits)");
				}
				return value;
			}
		}
	}

}
