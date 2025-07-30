package com.f1.ami.web;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessage;
import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiWebObject_Feed implements AmiWebObject, CalcTypes, Cloneable {
	private static final Logger log = LH.get();

	final private long uid;
	final private Long id;
	final private String objectId;
	final private short type;
	final private String amiApplicationIdName;
	private Long createdOn;
	private Long modifiedOn;
	private Integer revision;
	private Long expiresInMillis;

	private int size;

	private String centerName;

	private Object[] values;

	private AmiWebObject_FeedPositions feedPositions;

	public AmiWebObject_Feed(AmiWebObject_FeedPositions objects, AmiCenterClientObjectMessage data) {
		this.feedPositions = objects;
		this.centerName = data.getCenterName();
		this.createdOn = data.getCreatedOn();
		this.modifiedOn = data.getModifiedOn();
		this.id = data.getId();
		this.uid = data.getUid();
		this.revision = data.getRevision();
		this.expiresInMillis = data.getExpiresInMillis();
		this.type = data.getType();
		this.objectId = data.getObjectId();
		this.amiApplicationIdName = data.getAmiApplicationId();
		this.values = new Object[objects.getParamsCount()];
		for (int i = 0, n = data.getParamsCount(); i < n; i++)
			put(data.getParamName(i), data.getParamValue(i));
	}

	public Object getParam(String param) {
		if (param.length() == 1) {
			switch (param.charAt(0)) {
				case 'I':
					return getObjectId();
				case 'E':
					return getExpiresInMillis();
				case 'M':
					return getModifiedOn();
				case 'D':
					return getId();
				case 'C':
					return getCreatedOn();
				case 'V':
					return getRevision();
				case 'T':
					return getTypeName();
				case 'P':
					return getAmiApplicationIdName();
				case 'A':
					return getCenterName();
			}
		}
		int n = this.feedPositions.getParamPosition(param);
		return n < this.values.length ? this.values[n] : null;
	}

	public void update(AmiCenterClientObjectMessage m, AmiWebObjectFieldsImpl changesSink) {
		byte mask = m.getMask();
		if ((mask & (AmiDataEntity.MASK_CREATED_ON | AmiDataEntity.MASK_MODIFIED_ON | AmiDataEntity.MASK_REVISION | AmiDataEntity.MASK_EXPIRES_IN_MILLIS)) != 0) {
			if ((mask & AmiDataEntity.MASK_CREATED_ON) != 0)
				setCreatedOn(m.getCreatedOn());
			if ((mask & AmiDataEntity.MASK_MODIFIED_ON) != 0) {
				Long old = getModifiedOn();
				if (changesSink != null)
					changesSink.addChange("M", old);
				setModifiedOn(m.getModifiedOn());
			}
			if ((mask & AmiDataEntity.MASK_REVISION) != 0) {
				setRevision(m.getRevision());
				Integer old = getRevision();
				if (changesSink != null)
					changesSink.addChange("V", old);
			}
			if ((mask & AmiDataEntity.MASK_EXPIRES_IN_MILLIS) != 0) {
				Long old = getExpiresInMillis();
				setExpires(m.getExpiresInMillis());
				if (changesSink != null)
					changesSink.addChange("E", old);
			}
		}
		if ((mask & AmiDataEntity.MASK_PARAMS) != 0) {
			int fieldsCount = m.getParamsCount();
			for (int i = 0; i < fieldsCount; i++) {
				Object value = m.getParamValue(i);
				String keyString = m.getParamName(i);
				Object old = put(keyString, value);
				if (changesSink != null)
					changesSink.addChange(keyString, old);
			}
		}
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	public String getObjectId() {
		return objectId;
	}

	public short getType() {
		return type;
	}
	public Long getExpiresInMillis() {
		return expiresInMillis;
	}
	public Integer getRevision() {
		return revision;
	}
	public long getId() {
		return id;
	}
	public Long getModifiedOn() {
		return modifiedOn;
	}
	public Long getCreatedOn() {
		return createdOn;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(this.getTypeName()).append('[').append(centerName).append(':').append(this.id).append(']');
		boolean first = true;
		for (int i = 0; i < this.values.length; i++) {
			Object v = this.values[i];
			if (v == null)
				continue;
			if (first)
				first = false;
			else
				sink.append(',');
			sink.append(this.feedPositions.getParamName(i)).append('=').append(v);
		}
		return sink;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public AmiWebObject_Feed clone(AmiWebObject_FeedPositions newManager) {
		AmiWebObject_Feed r;
		try {
			r = (AmiWebObject_Feed) super.clone();
			r.values = r.values.clone();
		} catch (CloneNotSupportedException e) {
			throw OH.toRuntime(e);
		}
		r.feedPositions = newManager;
		return r;

	}

	public String getTypeName() {
		return this.feedPositions.getTypeName();
	}
	public String getAmiApplicationIdName() {
		return this.amiApplicationIdName;
	}

	@Override
	public int size() {
		return this.size + 8;//8  constants
	}
	@Override
	public int getVarsCount() {
		return this.size + 8;//8  constants
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		if (key instanceof String) {
			String param = (String) key;
			if (param.length() == 1) {
				switch (param.charAt(0)) {
					case 'I':
					case 'E':
					case 'M':
					case 'D':
					case 'C':
					case 'V':
					case 'T':
					case 'P':
					case 'A':
						return true;
				}
			}
			return get(param) != null;
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(Object key) {
		if (key instanceof String)
			return getParam((String) key);
		else
			return null;
	}

	@Override
	public Object put(String key, Object value) {
		//		if (key.length() == 1) {
		//			switch (key.charAt(0)) {
		//				case 'I':
		//				case 'E':
		//				case 'M':
		//				case 'D':
		//				case 'C':
		//				case 'V':
		//				case 'T':
		//				case 'P':
		//				case 'A':
		//					throw new UnsupportedOperationException(key + " ==> " + value);
		//			}
		//		}
		if (value == null)
			return remove(key);
		int i = feedPositions.getParamPosition(key);
		if (i >= this.values.length) {
			this.values = Arrays.copyOf(this.values, this.feedPositions.getParamsCount());
			this.values[i] = value;
			size++;
			return null;
		} else {
			Object r = this.values[i];
			this.values[i] = value;
			if (r == null)
				size++;
			return r;
		}
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		for (Entry<? extends String, ? extends Object> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}

	@Override
	public Object remove(Object key) {
		if (!(key instanceof String))
			return null;
		String s = (String) key;
		//		if (s.length() == 1) {
		//			switch (s.charAt(0)) {
		//				case 'I':
		//				case 'E':
		//				case 'M':
		//				case 'D':
		//				case 'C':
		//				case 'V':
		//				case 'T':
		//				case 'P':
		//				case 'A':
		//					throw new UnsupportedOperationException();
		//			}
		//		}
		int i = feedPositions.getParamPosition(s);
		if (i >= this.values.length)
			return null;
		Object r = this.values[i];
		if (r == null)
			return null;
		this.values[i] = null;
		this.size--;
		return r;
	}

	@Override
	public void clear() {
		if (size > 0)
			AH.fill(this.values, null);
		this.size = 0;
	}

	@Override
	public Set keySet() {
		Map<String, Object> t = new TreeMap<String, Object>();
		fill(t);
		return t.keySet();
	}

	@Override
	public Collection values() {
		Map<String, Object> t = new TreeMap<String, Object>();
		fill(t);
		return t.values();
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		Map<String, Object> t = new TreeMap<String, Object>();
		fill(t);
		return t.entrySet();
	}

	@Override
	public void fill(Map<String, Object> sink) {
		for (int i = 0; i < this.feedPositions.getParamsCount() && i < this.values.length; i++)
			if (this.values[i] != null)
				sink.put(this.feedPositions.getParamName(i), this.values[i]);
		sink.put("D", this.id);
		if (this.objectId != null)
			sink.put("I", objectId);
		if (this.expiresInMillis != null)
			sink.put("E", expiresInMillis);
		if (this.modifiedOn != null)
			sink.put("M", modifiedOn);
		if (this.createdOn != null)
			sink.put("C", createdOn);
		if (this.revision != null)
			sink.put("V", revision);
		if (this.revision != null)
			sink.put("T", type);
		if (this.amiApplicationIdName != null)
			sink.put("P", amiApplicationIdName);
		if (this.centerName != null)
			sink.put("A", centerName);
	}
	@Override
	public void fill(CalcFrame sink) {
		for (int i = 0; i < this.feedPositions.getParamsCount() && i < this.values.length; i++)
			if (this.values[i] != null)
				sink.putValue(this.feedPositions.getParamName(i), this.values[i]);
		sink.putValue("D", this.id);
		if (this.objectId != null)
			sink.putValue("I", objectId);
		if (this.expiresInMillis != null)
			sink.putValue("E", expiresInMillis);
		if (this.modifiedOn != null)
			sink.putValue("M", modifiedOn);
		if (this.createdOn != null)
			sink.putValue("C", createdOn);
		if (this.revision != null)
			sink.putValue("V", revision);
		if (this.revision != null)
			sink.putValue("T", type);
		if (this.amiApplicationIdName != null)
			sink.putValue("P", amiApplicationIdName);
		if (this.centerName != null)
			sink.putValue("A", centerName);
	}

	public long getUniqueId() {
		return uid;
	}

	public String getCenterName() {
		return this.centerName;
	}

	public AmiWebObject_FeedPositions getFeedPositions() {
		return this.feedPositions;
	}

	public void setCreatedOn(Long n) {
		this.createdOn = n;
	}

	public void setModifiedOn(Long n) {
		this.modifiedOn = n;
	}

	public void setRevision(Integer n) {
		this.revision = n;
	}

	public void setExpires(Long n) {
		this.expiresInMillis = n;
	}

	public Long getIdBoxed() {
		return this.id;
	}

	public void setFeedPositions(AmiWebObject_FeedPositions fp) {
		Object[] oldValues = this.values;
		AmiWebObject_FeedPositions oldFeedPositions = this.feedPositions;
		this.feedPositions = fp;
		this.values = new Object[this.feedPositions.getParamsCount()];
		for (int i = 0; i < oldValues.length; i++)
			if (oldValues[i] != null)
				put(oldFeedPositions.getParamName(i), oldValues[i]);

	}

	@Override
	public Iterable<String> getVarKeys() {
		return keySet();
	}

	@Override
	public Class<?> getType(String param) {
		if (param.length() == 1) {
			switch (param.charAt(0)) {
				case 'I':
					return String.class;
				case 'E':
					return Long.class;
				case 'M':
					return Long.class;
				case 'D':
					return Long.class;
				case 'C':
					return Long.class;
				case 'V':
					return Integer.class;
				case 'T':
					return String.class;
				case 'P':
					return String.class;
				case 'A':
					return String.class;
			}
		}
		int n = this.feedPositions.getParamPosition(param);
		Object o = n < this.values.length ? this.values[n] : null;
		return o == null ? Object.class : OH.getClass(o);//TODO: I still don't like this, it should come from the schema
	}

	@Override
	public Object getValue(String key) {
		return get(key);
	}

	@Override
	public Object putValue(String key, Object value) {
		return put(key, value);
	}

	@Override
	public boolean isVarsEmpty() {
		return false;
	}

}
