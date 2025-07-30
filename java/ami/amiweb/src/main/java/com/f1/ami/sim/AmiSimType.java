package com.f1.ami.sim;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.client.AmiClient;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.agg.IntegerAggregator;
import com.f1.utils.structs.SkipList;
import com.f1.utils.structs.SkipListDataEntry;

public class AmiSimType {

	private static final Logger log = LH.get();
	private String type;
	private int periodMs = 1000;
	private int maxSize = 1000;
	private int minSize = 100;

	private double addPerSecond = 1;
	private double updPerSecond = 1;
	private double delPerSecond = .1;

	final private String id;
	final private AmiSimField<String> idGenerator;
	final private AmiSimField<Object> expiresGenerator;
	final private SkipList<SkipListDataEntry<AmiSimObject>> objects = new SkipList<SkipListDataEntry<AmiSimObject>>(1000);
	final private Set<String> usedIds = new HashSet<String>();
	final private LinkedHashMap<String, AmiSimField<Object>> fields = new LinkedHashMap<String, AmiSimField<Object>>();
	private int messagesSentCount;

	private boolean isStoring = true;
	private boolean isAlert;

	public AmiSimType(String id, AmiSimField<String> idGenerator, AmiSimField<Object> expiresGenerator, boolean isAlert) {
		this.id = id;
		this.type = id;
		this.idGenerator = idGenerator;
		this.expiresGenerator = expiresGenerator;
		this.isAlert = isAlert;
	}

	public String getId() {
		return id;
	}

	@SuppressWarnings("unchecked")
	public AmiSimType addField(AmiSimField<?> field) {
		fields.put(field.getParamName(), (AmiSimField<Object>) field);
		return this;
	}

	public void visit(AmiClient sink, AmiSimSession session, long timePassed) {

		int addCount = getCount(addPerSecond, timePassed, session.getRand());
		if (isStoring)
			for (int i = 0; i < addCount && objects.size() < maxSize; i++)
				session.sendAdd(createObject(session, null), isAlert);
		else
			for (int i = 0; i < addCount && messagesSentCount < maxSize; i++) {
				messagesSentCount++;
				session.sendAdd(createObject(session, null), isAlert);
			}

		if (isStoring) {
			int delCount = getCount(delPerSecond * objects.size(), timePassed, session.getRand());
			for (int i = 0; i < delCount && objects.size() > minSize; i++)
				session.sendRemove(generateMessageDel(sink, session), isAlert);

			int updCount = getCount(updPerSecond * objects.size(), timePassed, session.getRand());
			Map<String, Object> params = new HashMap<String, Object>();
			for (int i = 0; i < updCount; i++) {
				params.clear();
				AmiSimObject obj = createUpdate(session, params);
				session.sendUpdate(obj, params, isAlert);
			}
		}
	}

	static public int getCount(double nps, long msPassed, Random rand) {
		double v = msPassed * nps / 1000d;
		if (v > 1)
			return Math.max((int) (v * 2 * rand.nextDouble() + .5d), 0);
		else
			return v > rand.nextDouble() ? 1 : 0;
	}
	public static void main(String a[]) {
		Random r = new Random(123);
		IntegerAggregator agg = new IntegerAggregator();
		for (double N = .05; N < 100; N += .1) {
			agg.clear();
			for (int i = 0; i < 10000; i++) {
				int j = getCount(N, 1000, r);
				agg.add(j);
			}
			System.out.println("Expecting " + agg.getCount() + " * " + (int) N + " = " + (int) (agg.getCount() * N) + "      ==>      " + agg.getTotal());
		}
	}
	private AmiSimObject generateMessageDel(AmiClient sink, AmiSimSession session) {
		Random rand = session.getRand();
		SkipListDataEntry<AmiSimObject> obj = objects.remove(rand.nextInt(objects.size()));
		String id = obj.getData().getId();
		usedIds.remove(id);
		return obj.getData();
	}
	private AmiSimObject createUpdate(AmiSimSession session, Map<String, Object> params) {
		Random rand = session.getRand();
		AmiSimObject obj = objects.get(rand.nextInt(objects.size())).getData();
		Map<String, Object> origParams = obj.getParams();
		for (AmiSimField<Object> field : this.fields.values()) {
			if (field.canUpdate() <= 0 || field.canUpdate() < rand.nextDouble())
				continue;
			final Object origVal = origParams.get(field.getParamName());
			try {
				final Object val = field.generateValue(obj, origVal, session);
				if (OH.ne(origVal, val)) {
					params.put(field.getParamName(), val);
					origParams.put(field.getParamName(), val);
				}
			} catch (Exception e) {
				LH.warning(log, "Error processing update for ", field.getParamName(), " = ", origVal, e);
			}
		}
		return obj;
	}

	public AmiSimObject createObject(AmiSimSession session, List<AmiSimObject> optionalReferences) {
		final Random rand = session.getRand();
		final long expires = (Long) expiresGenerator.generateValue(null, null, session);
		String id;
		if (idGenerator != null) {
			id = idGenerator.generateValue(null, null, session);
			for (int count = 0; usedIds.contains(id); count++) {
				if (count == 1000)
					throw new RuntimeException("bad id generator: " + idGenerator);
				id = idGenerator.generateValue(null, null, session);
			}
		} else
			id = null;
		final AmiSimObject obj = new AmiSimObject(type, id, expires);
		if (optionalReferences != null)
			for (AmiSimObject e : optionalReferences)
				obj.addReference(e);
		Map<String, Object> params = obj.getParams();
		for (AmiSimField<?> field : this.fields.values()) {
			Object val = field.generateValue(obj, null, session);
			if (val != null)
				params.put(field.getParamName(), val);
		}
		if (isStoring)
			objects.add(new SkipListDataEntry<AmiSimObject>(obj));
		usedIds.add(obj.getId());
		return obj;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public int getPeriodMs() {
		return periodMs;
	}
	public AmiSimType setPeriodMs(int periodMs) {
		this.periodMs = periodMs;
		return this;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public int getMinSize() {
		return minSize;
	}
	public void setCapacityRange(int minSize, int maxSize) {
		OH.assertGe(minSize, 0);
		OH.assertLe(minSize, maxSize);
		this.minSize = minSize;
		this.maxSize = maxSize;
	}

	public double getAddPerSecond() {
		return addPerSecond;
	}
	public AmiSimType setAddPerSecond(double addPerSecond) {
		OH.assertGe(addPerSecond, 0);
		this.addPerSecond = addPerSecond;
		return this;
	}

	public double getUpdPerSecond() {
		return updPerSecond;
	}
	public AmiSimType setUpdPerSecond(double updPerSecond) {
		OH.assertGe(updPerSecond, 0);
		this.updPerSecond = updPerSecond;
		return this;
	}

	public double getDelPerSecond() {
		return delPerSecond;
	}
	public AmiSimType setDelPerSecond(double delPerSecond) {
		OH.assertGe(delPerSecond, 0);
		this.delPerSecond = delPerSecond;
		return this;
	}

	public Map<String, AmiSimField<Object>> getFields() {
		return fields;
	}

	public List<SkipListDataEntry<AmiSimObject>> getObjects() {
		return this.objects;
	}

	public void validate(AmiSimSession session) {
		for (AmiSimField<Object> field : this.fields.values()) {
			try {
				field.validate(session);
			} catch (AmiSimException e) {
				throw new AmiSimException("bad value for: " + field.getParamName(), e);
			}
		}
	}

	public AmiSimField<String> getIdGenerator() {
		return this.idGenerator;
	}

	public AmiSimField<Object> getExpiresGenerator() {
		return this.expiresGenerator;
	}

	public boolean isStoring() {
		return isStoring;
	}

	public void setStoring(boolean isStoring) {
		this.isStoring = isStoring;
		if (this.isStoring == false) {
			this.updPerSecond = 0L;
			this.delPerSecond = 0L;
		}
	}

	public boolean getIsAlert() {
		return isAlert;
	}

}
